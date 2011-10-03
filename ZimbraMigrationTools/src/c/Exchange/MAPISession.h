#pragma once

namespace Zimbra
{
namespace MAPI
{
class MAPISessionException: public GenericException
{
public:
    MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPISessionException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPISessionException() {}
};

// Item type consts
#define ZCM_NONE                0x00
#define ZCM_MAIL                0x01
#define ZCM_CONTACTS            0x02
#define ZCM_APPOINTMENTS        0x04
#define ZCM_TASKS               0x08
#define ZCM_MEETRQRS            0x10
#define ZCM_ALL                 0xFF
class MAPIStore;

// MAPI session class
class MAPISession
{
private:
    IMAPISession *m_Session;
    HRESULT _mapiLogon(LPWSTR strProfile, DWORD dwFlags, LPMAPISESSION &session);

    Zimbra::Util::CriticalSection cs;

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
    HRESULT OpenEntry(ULONG cbEntryID, LPENTRYID lpEntryID, LPCIID lpInterface, ULONG ulFlags,
                ULONG FAR *lpulObjType,
                LPUNKNOWN FAR *lppUnk);
    HRESULT CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult);
};
}
}
