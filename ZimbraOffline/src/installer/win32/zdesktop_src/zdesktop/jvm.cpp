/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009,2010 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

#include "jvm.h"
#include <string>
#include <vector>

using namespace std;

const char * VirtualMachine::err_file = NULL;

VirtualMachine::VirtualMachine(Config &c) : cfg(c) {
    jvm = NULL;
    state = Stopped;

    if (err_file == NULL) {
        string ef = cfg.Get("error.file");
        if (!ef.empty())
            err_file = _strdup(ef.c_str());
    }
}

VirtualMachine::~VirtualMachine() {
    if (jvm)
        jvm->DestroyJavaVM();
}

bool VirtualMachine::Run() {
    thread_handle = CreateThread(NULL, 0, JvmThreadMain, (void *)this, 0, &thread_id);
    if (thread_handle == NULL) {
        last_err = "unable to create jvm thread";
        return false;
    }
    while (state == Stopped)
        Sleep(100);
    return state == Running;
}

void VirtualMachine::Stop() {
    jvm->AttachCurrentThread((void **)&env, NULL);
    jmethodID shutdown = env->GetStaticMethodID(wcls, "shutdown", "()V");
    env->CallStaticVoidMethod(wcls, shutdown, NULL);
}

void VirtualMachine::RedirectIO() {
    string outfile = cfg.Get("redirect.file");
    if (!outfile.empty()) {
        jmethodID redirect = env->GetStaticMethodID(wcls, "redirect", "(Ljava/lang/String;)V");
        jstring str = env->NewStringUTF(outfile.c_str());
        env->CallStaticVoidMethod(wcls, redirect, str);
    }
}

static string GetKeyName(const char *prefix, int c) {
    char numstr[32];
    sprintf_s(numstr, "%d", ++c);
    return string(prefix) + numstr;
}

static size_t GetPropertyList(Config &cfg, const char *prefix, vector<string> &list) {
    int c = 0;
    string arg;
    while (!(arg = cfg.Get(GetKeyName(prefix, c))).empty()) {
        list.push_back(arg);
        c++;
    }
    return c;
}

void VirtualMachine::CallMain() {
    vector<string> arglist;
    size_t c = GetPropertyList(cfg, "app.arg.", arglist);

    jclass strcls = env->FindClass("java/lang/String");
    jobjectArray args = env->NewObjectArray(c == 0 ? 1 : c, strcls, NULL);
    for(size_t i = 0; i < c; i++) {
        env->SetObjectArrayElement(args, i, env->NewStringUTF(arglist[i].c_str()));
    }

    jmethodID main = env->GetStaticMethodID(mcls, "main", "([Ljava/lang/String;)V");
    env->CallStaticVoidMethod(mcls, main, args);
}

jint JNICALL VirtualMachine::zd_vfprintf(FILE *fp, const char *format, va_list args) {
    FILE *fs;
    fopen_s(&fs, err_file, "a");
    if (fs != NULL) {
        vfprintf(fs, format, args);
        fclose(fs);
    }
    return 1;
}

CreateVMProc VirtualMachine::FindCreateJavaVM(const char *vmlibpath) {
    HINSTANCE hVM = LoadLibrary(vmlibpath);
    return hVM == NULL ? NULL : (CreateVMProc)GetProcAddress(hVM, "JNI_CreateJavaVM");
}

DWORD WINAPI VirtualMachine::JvmThreadMain(LPVOID lpParam) {
    VirtualMachine *self = (VirtualMachine *)lpParam;

    vector<string> classpaths;
    size_t numcps = GetPropertyList(self->cfg, "java.classpath.", classpaths);
    vector<string> javaargs;
    size_t numjargs = GetPropertyList(self->cfg, "java.arg.", javaargs);
    
    size_t numopts = (numcps > 0 ? 1 : 0) + numjargs + 1; 
    JavaVMOption *options = new JavaVMOption[numopts];
    string cpstr = "-Djava.class.path=";
    size_t j = 0;
    if (numcps > 0) {
        for (size_t i = 0; i < numcps; i++) {
            if (i > 0)
                cpstr.append(";");
            cpstr.append(classpaths[i]);
        }
        options[j++].optionString = (char *)cpstr.c_str();
    }
    for (size_t i = 0; i < numjargs; i++) {
        options[j++].optionString = (char *)javaargs[i].c_str();
    }
    options[j].optionString = "vfprintf";
    options[j].extraInfo = zd_vfprintf;

    JavaVMInitArgs vm_args;
    vm_args.version = JNI_VERSION_1_6;
    vm_args.options = options;
    vm_args.nOptions = numopts;
    vm_args.ignoreUnrecognized = JNI_TRUE;

    /* Create Java VM */
    CreateVMProc CreateVM = self->FindCreateJavaVM(self->cfg.Get("java.jvm.path").c_str());
    if (CreateVM == NULL) {
        self->last_err = "can't get JNI_CreateJavaVM";
        goto error0;
    }

    jint res = (*CreateVM)(&(self->jvm), (void**)&(self->env), &vm_args);
    if (res < 0) {
        self->last_err = "can't create Java VM";
        goto error0;
    }

    self->mcls = self->env->FindClass(self->cfg.Get("java.main.class").c_str());
    if (self->mcls == NULL) {
        self->last_err = "main class not found";
        goto error1;
    }

    self->wcls = self->env->FindClass(self->cfg.Get("java.wrapper.class").c_str());
    if (self->wcls == NULL) {
        self->last_err = "wrapper class not found";
        goto error1;
    }

    self->state = Running; // set state before going into blocking java call
    self->RedirectIO();
    self->CallMain();

error1:
    if (self->env->ExceptionOccurred())
        self->env->ExceptionDescribe();

error0:
    self->state = self->state == Running ? Stopped : Failed;
    delete[] options;
    return 1;
}

