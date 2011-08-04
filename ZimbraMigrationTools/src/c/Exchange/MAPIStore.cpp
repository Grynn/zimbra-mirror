#include "common.h"
#include "Exchange.h"
#include "MAPIObjects.h"
#include "MAPIStore.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// Exception class
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIStoreException::MAPIStoreException(HRESULT hrErrCode,
    LPCWSTR lpszDescription): GenericException(hrErrCode, lpszDescription) {
    //
}

MAPIStoreException::MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine,
    LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile) {
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIStore
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIStore::MAPIStore(): m_Store(NULL), m_mapiSession(NULL)
{}

MAPIStore::~MAPIStore() {
    ULONG flags = LOGOFF_ORDERLY;

    if (m_Store) {
        m_Store->StoreLogoff(&flags);
        m_Store->Release();
    }
    m_Store = NULL;
}

void MAPIStore::Initialize(LPMAPISESSION mapisession, LPMDB pMdb) {
    m_Store = pMdb;
    m_mapiSession = mapisession;
    g_ulIMAPHeaderInfoPropTag = Zimbra::MAPI::Util::IMAPHeaderInfoPropTag(m_Store);
}

HRESULT MAPIStore::CompareEntryIDs(SBinary *pBin1, SBinary *pBin2, ULONG &lpulResult) {
    HRESULT hr = S_OK;

    hr =
        m_Store->CompareEntryIDs(pBin1->cb, (LPENTRYID)(pBin1->lpb), pBin2->cb,
            (LPENTRYID)(pBin2->lpb), 0, &lpulResult);
    return hr;
}

HRESULT MAPIStore::GetRootFolder(MAPIFolder &rootFolder) {
    HRESULT hr = S_OK;
    SBinary bin;
    ULONG objtype = 0;
    LPMAPIFOLDER pFolder = NULL;

    if (FAILED(hr = Zimbra::MAPI::Util::HrMAPIFindIPMSubtree(m_Store, bin)))
        throw MAPIStoreException(hr, L"GetRootFolder(): HrMAPIFindIPMSubtree Failed.", __LINE__,
            __FILE__);
    if (FAILED(hr =
                m_Store->OpenEntry(bin.cb, (LPENTRYID)bin.lpb, NULL, MAPI_BEST_ACCESS, &objtype,
                    (LPUNKNOWN *)&pFolder)))
        throw MAPIStoreException(hr, L"GetRootFolder(): OpenEntry Failed.", __LINE__, __FILE__);
    // Init root folder object
    rootFolder.Initialize(pFolder, _TEXT("/"), &bin);
    return hr;
}
