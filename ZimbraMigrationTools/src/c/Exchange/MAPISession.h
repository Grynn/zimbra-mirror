#pragma once

namespace Zimbra {
namespace MAPI {
class MAPISessionException: public GenericException {
public:
    MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPISessionException() {}
};

class MAPIStore;

// MAPI session class
class MAPISession {
private:
    IMAPISession *m_Session;
    HRESULT _mapiLogon(LPWSTR strProfile, DWORD dwFlags, LPMAPISESSION &session);

public:
    MAPISession();
    ~MAPISession();
    HRESULT Logon(LPWSTR strProfile);
    HRESULT Logon(bool bDefaultProfile = true);

    LPMAPISESSION GetMAPISessionObject() { return m_Session; }
    HRESULT OpenDefaultStore(MAPIStore &Store);
    HRESULT OpenOtherStore(LPMDB OpenedStore, LPWSTR pServerDn, LPWSTR pUserDn,
    MAPIStore &OtherStore);
    HRESULT OpenAddressBook(LPADRBOOK *ppAddrBook);
	HRESULT OpenEntry(ULONG cbEntryID,LPENTRYID lpEntryID,LPCIID lpInterface,
		ULONG ulFlags,ULONG FAR * lpulObjType,LPUNKNOWN FAR * lppUnk );
	HRESULT CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult);
};
}
}
