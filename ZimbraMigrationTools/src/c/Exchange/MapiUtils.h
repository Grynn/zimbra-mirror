#pragma once

inline void HexStrFromBSTR(_bstr_t bstr, LPTSTR &szHex) {
    int i = 0;
    int len = bstr.length();
    BSTR bstrChar = bstr.GetBSTR();

    szHex = new TCHAR[2 * len * sizeof (TCHAR) + 1];
    ZeroMemory(szHex, 2 * len * sizeof (TCHAR) + 1);

    LPTSTR szTemp = szHex;
    while (i++ < len)
        szTemp += wsprintf(szTemp, _T("%X"), *bstrChar++);
}

inline void WtoA(LPWSTR pStrW, LPSTR &pStrA) {
    int nWChars = (int)wcslen(pStrW);
    int nAChars = WideCharToMultiByte(CP_ACP, 0, pStrW, nWChars, NULL, 0, NULL, NULL);

    pStrA = new CHAR[nAChars + 1];
    ZeroMemory((void *)pStrA, nAChars + 1);
    WideCharToMultiByte(CP_ACP, 0, pStrW, nWChars, pStrA, nAChars, NULL, NULL);
}

inline void AtoW(LPSTR pStrA, LPWSTR &pStrW) {
    int AChars = (int)strlen(pStrA);
    int WChars = MultiByteToWideChar(CP_ACP, 0, pStrA, AChars, NULL, 0);

    pStrW = new WCHAR[WChars + 1];
    ZeroMemory((void *)pStrW, (WChars + 1) * sizeof (WCHAR));
    MultiByteToWideChar(CP_ACP, 0, pStrA, AChars, pStrW, WChars);
}

inline void SafeDelete(LPWSTR &pStr) {
    if (pStr != NULL) {
        delete[] pStr;
        pStr = NULL;
    }
}

inline void SafeDelete(LPCWSTR pStr) {
    if (pStr != NULL)
        delete[] pStr;
}

inline void SafeDelete(LPSTR &pStr) {
    if (pStr != NULL) {
        delete[] pStr;
        pStr = NULL;
    }
}

class MapiUtilsException: public GenericException {
public:
    MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription): GenericException(hrErrCode,
            lpszDescription) {}
    MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine,
    LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile) {}
    virtual ~MapiUtilsException() {}
};

namespace Zimbra {
namespace MAPI {
namespace Util {
HRESULT HrMAPIFindDefaultMsgStore(LPMAPISESSION lplhSession, SBinary &bin);
HRESULT MailboxLogon(LPMAPISESSION pSession, LPMDB pMdb, LPWSTR pStoreDn, LPWSTR pMailboxDn,
    LPMDB *ppMdb);
HRESULT GetUserDN(LPCWSTR lpszServer, LPCWSTR lpszUser, wstring &wstruserdn);
HRESULT GetUserDnAndServerDnFromProfile(LPMAPISESSION pSession, LPSTR &pExchangeServerDn,
    LPSTR &pExchangeUserDn);
HRESULT HrMAPIFindIPMSubtree(LPMDB lpMdb, SBinary &bin);
ULONG IMAPHeaderInfoPropTag(LPMAPIPROP lpMapiProp);
}
}
}
