/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

#ifndef JVM_H
#define JVM_H

#include "stdafx.h"
#include "cfg.h"
#include <jni.h>

typedef jint (JNICALL *CreateVMProc)(JavaVM **pvm, void **penv, void *args);

class VirtualMachine {
public:
    VirtualMachine(Config &c);
    ~VirtualMachine();
    bool Run();
    void Stop();
    bool IsRunning() { return state == Running; }
    string &LastError() { return last_err; }

protected:
    enum State {Running, Stopped, Failed};

    HANDLE thread_handle;
    DWORD  thread_id;
    JavaVM *jvm;
    JNIEnv *env;
    jclass mcls;
    jclass wcls;
    Config &cfg;
    State state;
    string last_err;
    static const char *err_file;

    CreateVMProc FindCreateJavaVM(const char *vmlibpath);
    void RedirectIO();
    void CallMain();
    static DWORD WINAPI JvmThreadMain(LPVOID lpParam);
    static jint JNICALL zd_vfprintf(FILE *fp, const char *format, va_list args);
};

#endif JVM_H