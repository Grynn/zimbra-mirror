// dllmain.cpp : Defines the entry point for the DLL application.
#include "common.h"
#include "Logger.h"

extern "C" {

BOOL APIENTRY DllMain(HMODULE module, DWORD reason, LPVOID reserved)
{
    (void)module;
    (void)reserved;
    if (reason == DLL_PROCESS_ATTACH || reason == DLL_THREAD_ATTACH || reason == DLL_THREAD_DETACH)
        dlog.open(NULL);
    return TRUE;
}

}