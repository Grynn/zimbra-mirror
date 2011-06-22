// MapiWrapper.cpp : Implementation of CMapiWrapper

#include "common.h"
#include "MapiWrapper.h"

STDMETHODIMP CMapiWrapper::InterfaceSupportsErrorInfo(REFIID riid) {
    static const IID* const arr[] = {
	&IID_IMapiWrapper
    };

    for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++) {
	if (InlineIsEqualGUID(*arr[i],riid))
	    return S_OK;
    }
    return S_FALSE;
}

STDMETHODIMP CMapiWrapper::ConnectToServer(BSTR ServerHostName, BSTR Port,
    BSTR AdminID) {
    (void)ServerHostName;
    (void)Port;
    (void)AdminID;
    baseMigrationObj->Connecttoserver();
    return S_OK;
}

STDMETHODIMP CMapiWrapper::ConnecttoXchgServer(BSTR HostName, BSTR ProfileName,
    BSTR Password) {
    (void)HostName;
    (void)ProfileName;
    (void)Password;
    baseMigrationObj->Connecttoserver();
    return S_OK;
}

STDMETHODIMP CMapiWrapper::ImportMailOptions(BSTR OptionsTag) {
    (void)OptionsTag;
    baseMigrationObj->ImportMail();
    return S_OK;
}
