/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
#pragma once
#include <objbase.h>
#include <dbghelp.h>

namespace Zimbra
{
namespace Util
{
#define DBGHELP_MAJOR_VERSION           6
#define DBGHELP_MINOR_VERSION           1
#define DBGHELP_MAJOR_BUILD_NUMBER      17
#define DBGHELP_MINOR_BUILD_NUMBER      1

#ifndef DLLVERSIONINFO
typedef struct _DllVersionInfo
{
    DWORD cbSize;
    DWORD dwMajorVersion;
    DWORD dwMinorVersion;
    DWORD dwBuildNumber;
    DWORD dwPlatformID;
} DLLVERSIONINFO;
#endif

#ifndef DLLGETVERSIONPROC
typedef int (FAR WINAPI * DLLGETVERSIONPROC)(DLLVERSIONINFO *);
#endif

typedef BOOL (WINAPI * MiniDumpWriteDumpPtr_t)(IN HANDLE hProcess, IN DWORD ProcessId, IN HANDLE
    hFile, IN MINIDUMP_TYPE DumpType, IN CONST PMINIDUMP_EXCEPTION_INFORMATION
    ExceptionParam, OPTIONAL IN CONST PMINIDUMP_USER_STREAM_INFORMATION UserStreamParam,
    OPTIONAL IN CONST PMINIDUMP_CALLBACK_INFORMATION CallbackParam OPTIONAL);

class DllVersion
{
public:
    DllVersion(HINSTANCE hDbgHelpDll);

    BOOL GetDllVersion(DWORD &dwMajor, DWORD &dwMinor, DWORD &dwMajorBuildNumber,
    DWORD &dwMinorBuildNumber);

private:
    BOOL GetDllVersionFromDll(DWORD &dwMajor, DWORD &dwMinor, DWORD &dwMajorBuildNumber,
    DWORD &dwMinorBuildNumber);
    BOOL GetDllVersionFromFileInfo(DWORD &dwMajor, DWORD &dwMinor, DWORD &dwMajorBuildNumber,
    DWORD &dwMinorBuildNumber);
    BOOL ParseVersionString(LPWSTR lpwszVersion, DWORD &dwMajor, DWORD &dwMinor,
    DWORD &dwMajorBuildNumber, DWORD &dwMinorBuildNumber);

    HINSTANCE m_hDbgHelpDll;
    WCHAR m_pwszDllDir[MAX_PATH];
};

#define ZM_MINIDMP_UNINIT				0
#define ZM_CORE_GENERATED				1
#define ZM_CORE_ALREADY_GENERATED		2
#define ZM_CORE_GENERATION_FAILED		3

class MiniDumpGenerator
{
public:
    static bool Initialize(LPTSTR strDbgHelpDllPath);
	static void UnInit();
    static LONG WINAPI GenerateCoreDump(LPEXCEPTION_POINTERS pExPtrs, LPWSTR &wstrOutMessage);
private:
	static CriticalSection cs;
    static HINSTANCE m_hDbgHelpDll;
    static wstring m_wstrDbgHelpDllPath;
    static MiniDumpWriteDumpPtr_t m_MiniDumpWriteDumpPtr;
    static bool m_initialized;    
};

//copy wstr to dest
inline void CopyString(LPWSTR &pDest, LPCWSTR pSrc)
{
    if (pSrc == NULL)
    {
        pDest = NULL;
        return;
    }
    int nLength = (int)wcslen(pSrc);
    pDest = new WCHAR[nLength + 1];
    wcscpy(pDest, pSrc);
}

//append str. No buffer allocation
inline void AppendString(LPWSTR &pDest, LPCWSTR pSrc)
{
    int nDestLength = (int)wcslen(pDest);
    int nSrcLength = (int)wcslen(pSrc);

    LPWSTR pTempStr= new WCHAR[nDestLength+nSrcLength+1];
    wcscpy(pTempStr, pDest);
    wcscat(pTempStr, pSrc);
    delete []pDest;
    pDest = pTempStr;
}

//free
inline void FreeString(LPWSTR wstr)
{
    if(wstr)
    delete[] wstr;
}

inline std::wstring GetAppDir()
{
    WCHAR path[ MAX_PATH ];
    GetModuleFileName( NULL, path, MAX_PATH );
    const wstring exe_path( path );
    const wstring file_path( exe_path.substr(0, exe_path.rfind(L"\\") + 1 ) );
    return file_path;
} 



}
}