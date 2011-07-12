// MapiWrapper.cpp : Implementation of CMapiWrapper

#include "common.h"
#include "MapiWrapper.h"
#include "ExchangeCommon.h"


#define PR_PROFILE_UNRESOLVED_NAME 0x6607001e
#define PR_PROFILE_UNRESOLVED_SERVER 0x6608001e




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


STDMETHODIMP CMapiWrapper::GetProfilelist(VARIANT* Profiles)
{
	// TODO: Add your implementation code here
	HRESULT hr=S_OK;
	
	hr = MAPIInitialize(NULL);
	
	Zimbra::Mapi::Memory::SetMemAllocRoutines( NULL, MAPIAllocateBuffer, MAPIAllocateMore, MAPIFreeBuffer );
	vector<string> vProfileList;
	exchadmin->GetAllProfiles(vProfileList);

	vector<CComBSTR> tempvectors;
	std::vector<string>::iterator its;
	for (its = (vProfileList.begin()); its != vProfileList.end(); its++)
	{

		string str= (*its).c_str();

		CComBSTR temp = SysAllocString( str_to_wstr( str).c_str() );

		

		tempvectors.push_back(temp);


	}
	
	VariantInit(Profiles);
	Profiles->vt = VT_ARRAY | VT_BSTR;
	SAFEARRAY* psa;
	SAFEARRAYBOUND bounds = {vProfileList.size(), 0};
	psa = SafeArrayCreate(VT_BSTR, 1, &bounds);

	BSTR* bstrArray;

	SafeArrayAccessData(psa, (void**)&bstrArray);
	std::vector<CComBSTR>::iterator it;
	int i = 0;

	for (it = (tempvectors.begin()); it != tempvectors.end(); it++, i++)
	{
	bstrArray[i] = SysAllocString((*it).m_str);
	}

	SafeArrayUnaccessData(psa);
	Profiles->parray = psa;



	return hr;
}


std::wstring CMapiWrapper::str_to_wstr( const std::string& str )
{
  std::wstring wstr( str.length()+1, 0 );
  
  MultiByteToWideChar( CP_ACP,
             0,
             str.c_str(),
             str.length(),
             &wstr[0],
             str.length() );
  return wstr;
}

