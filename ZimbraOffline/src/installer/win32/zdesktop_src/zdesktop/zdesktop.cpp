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

#include "stdafx.h"
#include "jvm.h"
#include <fstream>

const char *szTitle = "Zimbra Desktop Service";
const char *szWindowClass = "Zimbra Desktop Service Class";

VirtualMachine *java;
BOOL shutdown = FALSE;
HWND hCurrWnd;

BOOL InitInstance(HINSTANCE hInstance, int nCmdShow) {
    HWND hWnd = CreateWindow(szWindowClass, szTitle, WS_OVERLAPPEDWINDOW,
        CW_USEDEFAULT, 0, CW_USEDEFAULT, 0, NULL, NULL, hInstance, NULL);

    if (!hWnd) {
        return FALSE;
    }

    ShowWindow(hWnd, nCmdShow);
    UpdateWindow(hWnd);
    hCurrWnd = hWnd;

    return TRUE;
}

LRESULT CALLBACK WndProc(HWND hWnd, UINT message, WPARAM wParam, LPARAM lParam) {
    switch (message) {
        case WM_DESTROY:
            PostQuitMessage(0);
        case WM_QUERYENDSESSION:
        case WM_ENDSESSION:
            if (!shutdown) {
                shutdown = TRUE;
                java->Stop();
            }
            break;
        default:
            return DefWindowProc(hWnd, message, wParam, lParam);
    }

    if (message == WM_QUERYENDSESSION)
        return 1;
    while (shutdown && java->IsRunning()) {
        Sleep(100);
    }

    return 0;
}

ATOM RegisterClass(HINSTANCE hInstance) {
    WNDCLASSEX wcex;

    wcex.cbSize = sizeof(WNDCLASSEX);

    wcex.style			= CS_HREDRAW | CS_VREDRAW;
    wcex.lpfnWndProc	= WndProc;
    wcex.cbClsExtra		= 0;
    wcex.cbWndExtra		= 0;
    wcex.hInstance		= hInstance;
    wcex.hIcon			= NULL;
    wcex.hCursor		= NULL;
    wcex.hbrBackground	= (HBRUSH)(COLOR_WINDOW + 1);
    wcex.lpszMenuName	= NULL;
    wcex.lpszClassName	= szWindowClass;
    wcex.hIconSm		= NULL;

    return RegisterClassEx(&wcex);
}

DWORD WINAPI MonitorThread(LPVOID lpParam) {
    Config *cfg = (Config *)lpParam;
    size_t pollint = atoi(cfg->Get("anchor.poll.interval").c_str()) * 1000;
    const char *anchor = cfg->Get("anchor.file").c_str();

    while (true) {
        WIN32_FIND_DATA FindFileData;
        if (FindFirstFile(anchor, &FindFileData) == INVALID_HANDLE_VALUE) {
            PostMessage(hCurrWnd, WM_CLOSE, 0, NULL);
            break;
        }
        Sleep(pollint);
    }
    return 0;
}

int APIENTRY WinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, LPTSTR lpCmdLine, int nCmdShow) {
    UNREFERENCED_PARAMETER(hPrevInstance);
    UNREFERENCED_PARAMETER(lpCmdLine);

    Config cfg;
    string cmdline(lpCmdLine);
    size_t len = cmdline.length();
    if (len > 2 && cmdline[0] == '"' && cmdline[len - 1] == '"')
        cmdline = cmdline.substr(1, len - 2);
    if (!cfg.Load(cmdline)) {
        string err = "Unable to load config file: " + cmdline;
        MessageBox(NULL, err.c_str(), "Zimbra Desktop Service", MB_ICONERROR | MB_OK);
        return FALSE;
    }

    string mutexname = cfg.Get("mutex.name");
    if (!mutexname.empty()) {
        HANDLE mutex = CreateMutex(NULL, TRUE, mutexname.c_str());
        if (mutex != NULL && WaitForSingleObject(mutex, 0) != WAIT_OBJECT_0) {
            MessageBox(NULL, "Service is already running.", "Zimbra Desktop Service", MB_ICONERROR | MB_OK);         
            return FALSE;
        }
    }

    string workdir = cfg.Get("working.directory");
    if (!workdir.empty())
        SetCurrentDirectory(workdir.c_str());

    ofstream anchor(cfg.Get("anchor.file").c_str(), fstream::out | fstream::trunc);
    if (!anchor.is_open()) {
        MessageBox(NULL, "Unable to create anchor file", "Zimbra Desktop Service", MB_ICONERROR | MB_OK);
        return FALSE;
    }
    anchor << GetCurrentProcessId();
    anchor.close();

    RegisterClass(hInstance);
    nCmdShow = SW_HIDE; // hide the window
    if (!InitInstance(hInstance, nCmdShow)) {
        return FALSE;
    }

    java = new VirtualMachine(cfg);
    if (!java->Run()) {
        string err = "Failed to start Java VM: " + java->LastError();
        MessageBox(NULL, err.c_str(), "Zimbra Desktop Service", MB_ICONERROR | MB_OK);
        return FALSE;
    }

    DWORD monthrd_id;
    HANDLE monthrd_handle = CreateThread(NULL, 0, MonitorThread, (void *)&cfg, 0, &monthrd_id);
    if (monthrd_handle == NULL) {
        MessageBox(NULL, "Unable to start monitor thread", "Zimbra Desktop Service", MB_ICONERROR | MB_OK);
        return FALSE;
    }

    // main message loop:
    MSG msg;
    while (GetMessage(&msg, NULL, 0, 0)) {
        TranslateMessage(&msg);
        DispatchMessage(&msg);
    }

    delete java;
    TerminateThread(monthrd_handle, 0);
    return (int)msg.wParam;
}