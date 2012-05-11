#include "common.h"
#include "Util.h"


Zimbra::Util::DllVersion::DllVersion(HINSTANCE hDbgHelpDll)
{
    if (hDbgHelpDll)
    {
        m_hDbgHelpDll = hDbgHelpDll;
        GetModuleFileName(m_hDbgHelpDll, m_pwszDllDir, MAX_PATH);
    }
}

BOOL Zimbra::Util::DllVersion::GetDllVersion(DWORD &dwMajor, DWORD &dwMinor,
    DWORD &dwMajorBuildNumber, DWORD &dwMinorBuildNumber)
{
    if (!GetDllVersionFromDll(dwMajor, dwMinor, dwMajorBuildNumber, dwMinorBuildNumber))
        return GetDllVersionFromFileInfo(dwMajor, dwMinor, dwMajorBuildNumber,
            dwMinorBuildNumber);
    return TRUE;
}

BOOL Zimbra::Util::DllVersion::GetDllVersionFromDll(DWORD &dwMajor, DWORD &dwMinor,
    DWORD &dwMajorBuildNumber, DWORD &dwMinorBuildNumber)
{
    DLLGETVERSIONPROC pDllGetVersion = NULL;

    pDllGetVersion = (DLLGETVERSIONPROC)GetProcAddress(m_hDbgHelpDll, "DllGetVersion");
    if (!pDllGetVersion)
    {
        return FALSE;
    }
    else
    {
        DLLVERSIONINFO dvi;

        ZeroMemory(&dvi, sizeof (dvi));
        dvi.cbSize = sizeof (dvi);

        HRESULT hr = (*pDllGetVersion)(&dvi);

        if (FAILED(hr))
        {
            return FALSE;
        }
        else
        {
            dwMajor = dvi.dwMajorVersion;
            dwMinor = dvi.dwMinorVersion;
            dwMajorBuildNumber = dvi.dwBuildNumber;
            dwMinorBuildNumber = 0;
            return TRUE;
        }
    }
}

BOOL Zimbra::Util::DllVersion::GetDllVersionFromFileInfo(DWORD &dwMajor, DWORD &dwMinor,
    DWORD &dwMajorBuildNumber, DWORD &dwMinorBuildNumber)
{
    DWORD dwVerHnd = 0;
    DWORD dwVerInfoSize = GetFileVersionInfoSize(m_pwszDllDir, &dwVerHnd);

    if (!dwVerInfoSize)
        return FALSE;

    LPVOID lpVffInfo = malloc(dwVerInfoSize + 1);

    if (!GetFileVersionInfo(m_pwszDllDir, dwVerHnd, dwVerInfoSize, lpVffInfo))
    {
        free(lpVffInfo);
        return FALSE;
    }

    LPWSTR lpwszVersion = NULL;
    WCHAR dllVersion[256];
    DWORD langD = 0, dwLen = 0;

    wcscpy(dllVersion, L"\\VarFileInfo\\Translation");

    BOOL bRes = VerQueryValue(lpVffInfo, dllVersion, (LPVOID *)&lpwszVersion, (UINT *)&dwLen);

    if (bRes && (dwLen == 4))
    {
        memcpy(&langD, lpwszVersion, 4);
        wsprintf(dllVersion, L"\\StringFileInfo\\%02X%02X%02X%02X\\FileVersion", (langD &
        0xff00) >> 8, langD & 0xff, (langD & 0xff000000) >> 24, (langD & 0xff0000) >> 16);
    }
    else
    {
        wsprintf(dllVersion, L"\\StringFileInfo\\%04X04B0\\FileVersion",
        GetUserDefaultLangID());
    }
    if (!VerQueryValue(lpVffInfo, dllVersion, (LPVOID *)&lpwszVersion, (UINT *)&dwLen))
    {
        free(lpVffInfo);
        return FALSE;
    }   
    // Now we have a string that looks like this :
    // "MajorVersion.MinorVersion.BuildNumber", so let's parse it
    bRes = ParseVersionString(lpwszVersion, dwMajor, dwMinor, dwMajorBuildNumber,
    dwMinorBuildNumber);

    free(lpVffInfo);

    return bRes;
}

BOOL Zimbra::Util::DllVersion::ParseVersionString(LPWSTR lpwszVersion, DWORD &dwMajor,
    DWORD &dwMinor, DWORD &dwMajorBuildNumber, DWORD &dwMinorBuildNumber)
{
    LPWSTR pwszMajor = NULL, pwszMinor = NULL, pwszMajorBuildNo = NULL, pwszMinorBuildNo = NULL;

    pwszMajor = wcstok(lpwszVersion, L".");     // Get first token (Major version number)
    pwszMinor = wcstok(NULL, L".");             // Get second token (Minor version number)
    pwszMajorBuildNo = wcstok(NULL, L".");      // Get third token (Major build number)
    pwszMinorBuildNo = wcstok(NULL, L".");      // Get fourth token (Minor build number)
    if (pwszMajor && pwszMinor && pwszMajorBuildNo && pwszMinorBuildNo)
    {
        dwMajor = _wtoi(pwszMajor);
        dwMinor = _wtoi(pwszMinor);
        dwMajorBuildNumber = _wtoi(pwszMajorBuildNo);
        dwMinorBuildNumber = _wtoi(pwszMinorBuildNo);
        return TRUE;
    }
    else
    {
    // It may be comma separated
        pwszMajor = wcstok(lpwszVersion, L","); // Get first token (Major version number)
        pwszMinor = wcstok(NULL, L",");         // Get second token (Minor version number)
        pwszMajorBuildNo = wcstok(NULL, L",");  // Get third token (Major build number)
        pwszMinorBuildNo = wcstok(NULL, L",");  // Get fourth token (Minor build number)
        if (pwszMajor && pwszMinor && pwszMajorBuildNo && pwszMinorBuildNo)
        {
        dwMajor = _wtoi(pwszMajor);
        dwMinor = _wtoi(pwszMinor);
        dwMajorBuildNumber = _wtoi(pwszMajorBuildNo);
        dwMinorBuildNumber = _wtoi(pwszMinorBuildNo);
        return TRUE;
        }
        else
        {
            return FALSE;
        }
    }
}

Zimbra::Util::MiniDumpGenerator::MiniDumpGenerator(LPTSTR strDbgHelpDllPath)
{
    m_MiniDumpWriteDumpPtr = NULL;
    m_hDbgHelpDll = NULL;
    m_wstrDbgHelpDllPath= strDbgHelpDllPath;
    m_initialized=false;
    Initialize();
}

Zimbra::Util::MiniDumpGenerator::~MiniDumpGenerator()
{
    if (m_hDbgHelpDll)
    FreeLibrary(m_hDbgHelpDll);
    m_hDbgHelpDll= NULL;
}

bool Zimbra::Util::MiniDumpGenerator::Initialize()
{
    m_hDbgHelpDll = LoadLibrary(m_wstrDbgHelpDllPath.c_str());
    if (!m_hDbgHelpDll)
    {
        //error loading dbghelp.dll
        return false;
    }
    DWORD dwMajor = 0, dwMinor = 0, dwMajorBuildNumber = 0, dwMinorBuildNumber = 0;
    Zimbra::Util::DllVersion dllversion(m_hDbgHelpDll);

    if (!dllversion.GetDllVersion(dwMajor, dwMinor, dwMajorBuildNumber,
    dwMinorBuildNumber))
    {
        //error getting dll version
        return false;
    }

    if ((dwMajor < DBGHELP_MAJOR_VERSION) || ((dwMajor == DBGHELP_MAJOR_VERSION) &&
    (dwMinor < DBGHELP_MINOR_VERSION)) || ((dwMajor == DBGHELP_MAJOR_VERSION) &&
    (dwMinor == DBGHELP_MINOR_VERSION) && (dwMajorBuildNumber <
    DBGHELP_MAJOR_BUILD_NUMBER)) || ((dwMajor == DBGHELP_MAJOR_VERSION) &&
    (dwMinor == DBGHELP_MINOR_VERSION) && (dwMajorBuildNumber ==
    DBGHELP_MAJOR_BUILD_NUMBER) && (dwMinorBuildNumber <
    DBGHELP_MINOR_BUILD_NUMBER)))
    {
        //dbghelp.dll has older version
        return false;
    }

    m_MiniDumpWriteDumpPtr = (Zimbra::Util::MiniDumpWriteDumpPtr_t)GetProcAddress(
    m_hDbgHelpDll, "MiniDumpWriteDump");
    if (!m_MiniDumpWriteDumpPtr)
    {
        //Error in getting MiniDumpWriteDump function from dbghelp.dll
        return false;
    }
    m_initialized=true;
    return m_initialized;
}

LONG WINAPI Zimbra::Util::MiniDumpGenerator::GenerateCoreDump(LPEXCEPTION_POINTERS pExPtrs, LPWSTR &wstrOutMessage)
{
    LONG retVal= ZM_CORE_GENERATION_FAILED;
    CriticalSection cs;
    
    if(!m_initialized)
        return ZM_MINIDMP_UNINIT;
    Zimbra::Util::AutoCriticalSection acs(cs);
    if (pExPtrs)
    {
        static std::set<PVOID> setOccuredExcepAddrs;
        static std::set<DWORD> setOccuredExcepCodes;

        if ((setOccuredExcepAddrs.find(pExPtrs->ExceptionRecord->ExceptionAddress) !=
            setOccuredExcepAddrs.end()) && (setOccuredExcepCodes.end() !=
            setOccuredExcepCodes.find(pExPtrs->ExceptionRecord->ExceptionCode)))
        {
            Zimbra::Util::CopyString(wstrOutMessage,L"Similar core dump already generated. Hence skipping this one.");
            return ZM_CORE_ALREADY_GENERATED;
        }
        if (pExPtrs->ExceptionRecord)
        {
            WCHAR strbuf[128];
            wsprintf(strbuf,L"Exception Address: 0x%x",            
            pExPtrs->ExceptionRecord->ExceptionAddress);
            Zimbra::Util::CopyString(wstrOutMessage,strbuf);
        }

        if (m_MiniDumpWriteDumpPtr)
        {
            MINIDUMP_EXCEPTION_INFORMATION mdExInfo;

            mdExInfo.ThreadId = GetCurrentThreadId();
            mdExInfo.ExceptionPointers = pExPtrs;
            mdExInfo.ClientPointers = TRUE;

            WCHAR pwszDmpFile[MAX_PATH + 32];
            WCHAR pwszTempPath[MAX_PATH];

            GetTempPath(MAX_PATH, pwszTempPath);

            CString strCoreFileName;
            SYSTEMTIME st;

            GetLocalTime(&st);
            strCoreFileName.Format(L"ZimbraCore_%02d%02d%d_%02d%02d%02d.dmp", st.wMonth,
            st.wDay, st.wYear, st.wHour, st.wMinute, st.wSecond);

            wcscpy(pwszDmpFile, pwszTempPath);

            //Get App Name
            WCHAR szAppPath[MAX_PATH] = L"";
            std::wstring strAppName;
            ::GetModuleFileName(0, szAppPath, MAX_PATH);
            strAppName = szAppPath;
            strAppName = strAppName.substr(strAppName.rfind(L"\\") + 1);
            strAppName = strAppName.substr(0,strAppName.find(L"."));
            wcscat(pwszDmpFile, strAppName.c_str());
            wcscat(pwszDmpFile, L"\\");
            wcscat(pwszDmpFile, strCoreFileName.GetString());

            HANDLE hFile = CreateFile(pwszDmpFile, GENERIC_READ | GENERIC_WRITE, 0, NULL,
            CREATE_ALWAYS, FILE_ATTRIBUTE_NORMAL, NULL);

            if (hFile != INVALID_HANDLE_VALUE)
            {
                if (m_MiniDumpWriteDumpPtr(GetCurrentProcess(), GetCurrentProcessId(), hFile,
                (MINIDUMP_TYPE)(MiniDumpWithDataSegs | MiniDumpWithFullMemory), &mdExInfo,
                NULL, NULL))
                {
                    WCHAR strbuf[512];
                    wsprintf(strbuf,L"  CORE: Generated core dump: %s", pwszDmpFile);
                    if(wstrOutMessage)
                        Zimbra::Util::AppendString(wstrOutMessage,strbuf);
                    else
                        Zimbra::Util::CopyString(wstrOutMessage,strbuf);
                    setOccuredExcepAddrs.insert(pExPtrs->ExceptionRecord->ExceptionAddress);
                    setOccuredExcepCodes.insert(pExPtrs->ExceptionRecord->ExceptionCode);
                    CloseHandle(hFile);
                    retVal = ZM_CORE_GENERATED;
                }
                else
                {
                    if(wstrOutMessage)
                        Zimbra::Util::AppendString(wstrOutMessage,L"  CORE: Failed to generate core dump.");
                    else
                        Zimbra::Util::CopyString(wstrOutMessage,L"  CORE: Failed to generate core dump.");
                    CloseHandle(hFile);
                    DeleteFile(pwszDmpFile);
                }
            }
        }
    }
    else
    {
        if(wstrOutMessage)
            Zimbra::Util::AppendString(wstrOutMessage,L"  CORE: Failed to generate core dump. Invalid LPEXCEPTION_POINTERS.");
        else
            Zimbra::Util::CopyString(wstrOutMessage,L"  CORE: Failed to generate core dump. Invalid LPEXCEPTION_POINTERS.");
    }
    return retVal;
}



