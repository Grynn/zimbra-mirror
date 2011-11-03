// dllmain.cpp : Defines the entry point for the DLL application.
#include "common.h"
#include "Exchange.h"

CExchangeModule _AtlModule;
BOOL APIENTRY DllMain(HMODULE hModule, DWORD ul_reason_for_call, LPVOID lpReserved)
{
    (void)hModule;
    (void)ul_reason_for_call;
    (void)lpReserved;
    return TRUE;
}
