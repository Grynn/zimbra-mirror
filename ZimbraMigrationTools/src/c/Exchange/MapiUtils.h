#pragma once

#define PR_IPM_OTHERSPECIALFOLDERS_ENTRYID	PROP_TAG( PT_MV_BINARY, 0x36D8)

typedef struct _ObjectPickerData
{
	wstring wstrUsername;
	wstring wstrExchangeStore;
	vector<wstring> vAliases;
	vector<std::pair<wstring,wstring>> pAttributeList;
} ObjectPickerData;

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

inline LPTSTR LongToHexString(LONG l) {
    LPTSTR pRetVal = new TCHAR[34];

    *pRetVal = _T('x');
    _ltot(l, pRetVal + 1, 16);
    return pRetVal;
}

inline LPTSTR Int32ToString(int i) {
    LPTSTR pRetVal = new TCHAR[34];

    _itot(i, pRetVal, 10);
    return pRetVal;
}

inline ULONG SetPropType( ULONG propTag, ULONG propType )
{
	if( PROP_TYPE(propTag) == PT_ERROR )
		return PR_NULL;
	return PROP_TAG( propType, PROP_ID(propTag) );
}

inline int CopyString( LPWSTR& pDest, LPWSTR pSrc )
{
	if( pSrc == NULL )
	{
		pDest = NULL;
		return 0;
	}

	int nLength = (int)wcslen(pSrc);
	pDest = new WCHAR[ nLength + 1 ];
	wcscpy(pDest, pSrc);
	return nLength;
}

#define UNICODE_EXCEPTION_STRING L"ErrCode:%s Description:%s SrcFile:%s SrcLine:%s"
inline LPTSTR FromatExceptionInfo(HRESULT errCode, LPWSTR errDescription, LPSTR srcFile,
    int srcLine) {
    LPWSTR lpBuffer = NULL;
    LPWSTR lpstrSrcFile = NULL;

    LPWSTR lpstrErrCode = LongToHexString(errCode);
    LPWSTR lpstrSrecline = Int32ToString(srcLine);

    AtoW(srcFile, lpstrSrcFile);

    long totalLen = wcslen(lpstrErrCode) + wcslen(errDescription) + wcslen(lpstrSrcFile) +
        wcslen(lpstrSrecline) + (sizeof (WCHAR) * 2);
    lpBuffer = new TCHAR[totalLen * sizeof (WCHAR)];
    wsprintf(lpBuffer, UNICODE_EXCEPTION_STRING, lpstrErrCode, errDescription, lpstrSrcFile,
        lpstrSrecline);

    delete[] lpstrErrCode;
    delete[] lpstrSrecline;
    delete[] lpstrSrcFile;
    return lpBuffer;
}

namespace Zimbra {
namespace MAPI {
namespace Util {
class MapiUtilsException: public GenericException {
public:
    MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription): GenericException(hrErrCode,
            lpszDescription) {}
    MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine,
    LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile) {}
    virtual ~MapiUtilsException() {}
};


HRESULT HrMAPIFindDefaultMsgStore(LPMAPISESSION lplhSession, SBinary &bin);
HRESULT MailboxLogon(LPMAPISESSION pSession, LPMDB pMdb, LPWSTR pStoreDn, LPWSTR pMailboxDn,
    LPMDB *ppMdb);
HRESULT GetUserDNAndLegacyName(LPCWSTR lpszServer, LPCWSTR lpszUser, LPCWSTR lpszPwd, wstring &wstruserdn,
    wstring &wstrlegacyname);
HRESULT GetUserDnAndServerDnFromProfile(LPMAPISESSION pSession, LPSTR &pExchangeServerDn,
    LPSTR &pExchangeUserDn);
HRESULT HrMAPIFindIPMSubtree(LPMDB lpMdb, SBinary &bin);
HRESULT GetMdbSpecialFolders(IN LPMDB lpMdb, IN OUT SBinaryArray* pEntryIds);
HRESULT GetInboxSpecialFolders(LPMAPIFOLDER pInbox, SBinaryArray* pEntryIds);
HRESULT GetAllSpecialFolders(IN LPMDB lpMdb, IN OUT SBinaryArray* pEntryIds);
HRESULT FreeAllSpecialFolders(IN SBinaryArray* lpSFIds);
ExchangeSpecialFolderId GetExchangeSpecialFolderId(IN LPMAPISESSION lpSession, IN ULONG cbEntryId, IN LPENTRYID pFolderEntryId, SBinaryArray* pEntryIds);
HRESULT GetExchangeUsersUsingObjectPicker(vector<ObjectPickerData> &vUserList);

ULONG IMAPHeaderInfoPropTag(LPMAPIPROP lpMapiProp);
HRESULT CopyEntryID(SBinary &src, SBinary &dest);
wstring ReverseDelimitedString(wstring wstrString, WCHAR* delimiter);
}
}
}
