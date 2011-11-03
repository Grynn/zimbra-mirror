#pragma once

#define PR_IPM_OTHERSPECIALFOLDERS_ENTRYID PROP_TAG(PT_MV_BINARY, 0x36D8)

typedef struct _ObjectPickerData
{
    wstring wstrUsername;
    wstring wstrExchangeStore;

    vector<wstring> vAliases;
    vector<std::pair<wstring, wstring> > pAttributeList;
} ObjectPickerData;
typedef struct _RecipInfo
{
    LPTSTR pAddrType;
    LPTSTR pEmailAddr;
    ULONG cbEid;
    LPENTRYID pEid;
} RECIP_INFO;

inline void HexStrFromBSTR(_bstr_t bstr, LPTSTR &szHex)
{
    int i = 0;
    int len = bstr.length();
    BSTR bstrChar = bstr.GetBSTR();

    szHex = new TCHAR[2 * len * sizeof (TCHAR) + 1];
    ZeroMemory(szHex, 2 * len * sizeof (TCHAR) + 1);

    LPTSTR szTemp = szHex;

    while (i++ < len)
        szTemp += wsprintf(szTemp, _T("%X"), *bstrChar++);
}

inline void WtoA(LPWSTR pStrW, LPSTR &pStrA)
{
    int nWChars = (int)wcslen(pStrW);
    int nAChars = WideCharToMultiByte(CP_ACP, 0, pStrW, nWChars, NULL, 0, NULL, NULL);

    pStrA = new CHAR[nAChars + 1];
    ZeroMemory((void *)pStrA, nAChars + 1);
    WideCharToMultiByte(CP_ACP, 0, pStrW, nWChars, pStrA, nAChars, NULL, NULL);
}

inline void AtoW(LPSTR pStrA, LPWSTR &pStrW)
{
    int AChars = (int)strlen(pStrA);
    int WChars = MultiByteToWideChar(CP_ACP, 0, pStrA, AChars, NULL, 0);

    pStrW = new WCHAR[WChars + 1];
    ZeroMemory((void *)pStrW, (WChars + 1) * sizeof (WCHAR));
    MultiByteToWideChar(CP_ACP, 0, pStrA, AChars, pStrW, WChars);
}

inline void SafeDelete(LPWSTR &pStr)
{
    if (pStr != NULL)
    {
        delete[] pStr;
        pStr = NULL;
    }
}

inline void SafeDelete(LPCWSTR pStr)
{
    if (pStr != NULL)
        delete[] pStr;
}

inline void SafeDelete(LPSTR &pStr)
{
    if (pStr != NULL)
    {
        delete[] pStr;
        pStr = NULL;
    }
}

inline LPTSTR LongToHexString(LONG l)
{
    LPTSTR pRetVal = new TCHAR[34];

    *pRetVal = _T('x');
    _ltot(l, pRetVal + 1, 16);
    return pRetVal;
}

inline LPTSTR Int32ToString(int i)
{
    LPTSTR pRetVal = new TCHAR[34];

    _itot(i, pRetVal, 10);
    return pRetVal;
}

inline ULONG SetPropType(ULONG propTag, ULONG propType)
{
    if (PROP_TYPE(propTag) == PT_ERROR)
        return PR_NULL;
    return PROP_TAG(propType, PROP_ID(propTag));
}

inline int CopyString(LPWSTR &pDest, LPWSTR pSrc)
{
    if (pSrc == NULL)
    {
        pDest = NULL;
        return 0;
    }

    int nLength = (int)wcslen(pSrc);

    pDest = new WCHAR[nLength + 1];
    wcscpy(pDest, pSrc);
    return nLength;
}

inline void CopyEntryID(SBinary &src, SBinary &dest)
{
    dest.cb = src.cb;
    MAPIAllocateBuffer(src.cb, (LPVOID *)&(dest.lpb));
    memcpy(dest.lpb, src.lpb, src.cb);
}

inline void FreeEntryID(SBinary &bin)
{
    bin.cb = 0;
    MAPIFreeBuffer(bin.lpb);
    bin.lpb = NULL;
}

#define UNICODE_EXCEPTION_STRING L"ErrCode:%s Description:%s SrcFile:%s SrcLine:%s"
inline LPTSTR FromatExceptionInfo(HRESULT errCode, LPWSTR errDescription, LPSTR srcFile, int
    srcLine)
{
    LPWSTR lpBuffer = NULL;
    LPWSTR lpstrSrcFile = NULL;
    LPWSTR lpstrErrCode = LongToHexString(errCode);
    LPWSTR lpstrSrecline = Int32ToString(srcLine);

    AtoW(srcFile, lpstrSrcFile);

    size_t totalLen = wcslen(lpstrErrCode) + wcslen(errDescription) + wcslen(lpstrSrcFile) +
        wcslen(lpstrSrecline) + (sizeof (WCHAR) * 2);

    lpBuffer = new TCHAR[totalLen * sizeof (WCHAR)];
    wsprintf(lpBuffer, UNICODE_EXCEPTION_STRING, lpstrErrCode, errDescription, lpstrSrcFile,
        lpstrSrecline);

    delete[] lpstrErrCode;
    delete[] lpstrSrecline;
    delete[] lpstrSrcFile;
    return lpBuffer;
}

namespace Zimbra
{
namespace MAPI
{
namespace Util
{
const std::string PSTMIG_PROFILE_PREFIX = "Z1mbr4PST23Migration";

enum MIME_ENCODING
{
    ME_7BIT, ME_QUOTED_PRINTABLE, ME_BASE64
};

typedef BOOL (STDAPICALLTYPE FGETCOMPONENTPATH)(LPSTR szComponent, LPSTR szQualifier, LPSTR
    szDllPath, DWORD cchBufferSize, BOOL fInstall);
typedef FGETCOMPONENTPATH FAR *LPFGETCOMPONENTPATH;
#define MAPI_NATIVE_BODY                0x00010000
#define MAPI_NATIVE_BODY_TYPE_RTF       0x00000001
#define MAPI_NATIVE_BODY_TYPE_HTML      0x00000002
#define MAPI_NATIVE_BODY_TYPE_PLAINTEXT 0x00000004

typedef struct
{
    ULONG size;
    ULONG ulStreamFlags;
} RTF_WCSRETINFO;
typedef struct
{
    ULONG size;
    ULONG ulFlags;
    ULONG ulInCodePage;
    ULONG ulOutCodePage;
} RTF_WCSINFO;
typedef HRESULT (STDMETHODCALLTYPE * WRAPCOMPRESSEDRTFSTREAMEX)(LPSTREAM lpCompressedRTFStream,
    CONST RTF_WCSINFO *pWCSInfo, LPSTREAM *lppUncompressedRTFStream,
    RTF_WCSRETINFO *pRetInfo);

class CriticalSection
{
public:
    CriticalSection() { InitializeCriticalSection(&cs); }
    ~CriticalSection() { DeleteCriticalSection(&cs); }
    void Enter() { EnterCriticalSection(&cs); }
    void Leave() { LeaveCriticalSection(&cs); }

private:
    CRITICAL_SECTION cs;
};

#pragma warning( disable : 4512 )
class AutoCriticalSection
{
public:
    AutoCriticalSection(CriticalSection &scs): cs(scs) { cs.Enter(); }
    ~AutoCriticalSection() { cs.Leave(); }

private:
    CriticalSection &cs;
};

class MapiUtilsException: public GenericException
{
public:
    MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription): GenericException(hrErrCode,
        lpszDescription) {}
    MapiUtilsException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR
        strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile) {}
    virtual ~MapiUtilsException() {}
};

HRESULT HrMAPIFindDefaultMsgStore(LPMAPISESSION lplhSession, SBinary &bin);
HRESULT MailboxLogon(LPMAPISESSION pSession, LPMDB pMdb, LPWSTR pStoreDn, LPWSTR pMailboxDn,
    LPMDB *ppMdb);
HRESULT GetUserDNAndLegacyName(LPCWSTR lpszServer, LPCWSTR lpszUser, LPCWSTR lpszPwd,
    wstring &wstruserdn, wstring &wstrlegacyname);
HRESULT GetUserDnAndServerDnFromProfile(LPMAPISESSION pSession, LPSTR &pExchangeServerDn,
    LPSTR &pExchangeUserDn);
HRESULT HrMAPIFindIPMSubtree(LPMDB lpMdb, SBinary &bin);
HRESULT GetMdbSpecialFolders(IN LPMDB lpMdb, IN OUT SBinaryArray *pEntryIds);
HRESULT GetInboxSpecialFolders(LPMAPIFOLDER pInbox, SBinaryArray *pEntryIds);
HRESULT GetAllSpecialFolders(IN LPMDB lpMdb, IN OUT SBinaryArray *pEntryIds);
HRESULT FreeAllSpecialFolders(IN SBinaryArray *lpSFIds);
ExchangeSpecialFolderId GetExchangeSpecialFolderId(LPMDB userStore, IN ULONG cbEntryId, IN
    LPENTRYID pFolderEntryId, SBinaryArray *pEntryIds);
HRESULT GetExchangeUsersUsingObjectPicker(vector<ObjectPickerData> &vUserList);
HRESULT HrMAPIGetSMTPAddress(IN MAPISession &session, IN RECIP_INFO &recipInfo, OUT
    wstring &strSmtpAddress);
BOOL CompareRecipients(MAPISession &session, RECIP_INFO &r1, RECIP_INFO &r2);
void CreateMimeSubject(IN LPTSTR pSubject, IN UINT codepage, IN OUT LPSTR *ppMimeSubject);
bool NeedsEncoding(LPSTR pStr);
ULONG IMAPHeaderInfoPropTag(LPMAPIPROP lpMapiProp);
wstring ReverseDelimitedString(wstring wstrString, WCHAR *delimiter);
void AddBodyToPart(mimepp::BodyPart *pPart, LPSTR pStr, size_t length, BOOL bConvertLFToCRLF =
    TRUE);
mimepp::BodyPart *AttachTooLargeAttachPart(ULONG attachSize, LPATTACH pAttach, LPSTR pCharset);
mimepp::BodyPart *AttachPartFromIAttach(MAPISession &session, LPATTACH pAttach, LPSTR pCharset,
    LONG codepage);

namespace CharsetUtil
{
void CharsetStringFromCodePageId(UINT codePageId, LPSTR *pCharset);
MIME_ENCODING FindBestEncoding(LPSTR pBuffer, int nBuffer);
}                                               // end CharsetUtil

class StoreUtils
{
private:
    static StoreUtils *stUtilsInst;
    HINSTANCE _hinstLib;
    WRAPCOMPRESSEDRTFSTREAMEX pWrapCompressedRTFEx;

    void HrGetRegMultiSZValueA(IN HKEY hKey, IN LPCSTR lpszValue, OUT LPVOID *lppData);
    void GetMAPIDLLPath(LPSTR szMAPIDir, ULONG cchMAPIDir);
    void UnInit();

    StoreUtils() { _hinstLib = NULL; }

public:
    static StoreUtils *getInstance()
    {
        if (!stUtilsInst)
            stUtilsInst = new StoreUtils;
        return stUtilsInst;
    }

    void DeleteStoreUtils()
    {
        if (stUtilsInst)
            delete stUtilsInst;
        stUtilsInst = NULL;
    }

    ~StoreUtils()
    {
        UnInit();
    }

    bool Init();

    bool isUnicodeStore(LPMESSAGE pMsg);
    bool GetAnsiStoreMsgNativeType(LPMESSAGE pMsg, ULONG *nBody);
};                                              // end StoreUtils

BOOL GetAppName(wstring &wstrAppName);
BOOL CreateAppTemporaryDirectory();
BOOL GetAppTemporaryDirectory(wstring &wstrTempAppDirPath);
wstring GetUniqueName();
wstring GetDomainName();
LONG GetOutlookVersion(int &iVersion);
BOOL CreatePSTProfile(LPSTR lpstrProfileName, LPSTR lpstrPSTFQPathName, bool bNoUI = true);
BOOL DeleteAlikeProfiles(LPCSTR lpstrProfileName);
}                                               // end Util
}                                               // end MAPI
}                                               // end Zimbra
