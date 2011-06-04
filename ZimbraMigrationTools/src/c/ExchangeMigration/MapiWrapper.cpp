// MapiWrapper.cpp : Implementation of CMapiWrapper

#include "stdafx.h"
#include "MapiWrapper.h"


// CMapiWrapper

STDMETHODIMP CMapiWrapper::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* const arr[] = 
	{
		&IID_IMapiWrapper
	};

	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}


STDMETHODIMP CMapiWrapper::ConnectToServer(BSTR ServerHostName, BSTR Port, BSTR AdminID)
{
	// TODO: Add your implementation code here

	
	baseMigrationObj->Connecttoserver();

	return S_OK;
}


STDMETHODIMP CMapiWrapper::ConnecttoXchgServer(BSTR HostName, BSTR ProfileName, BSTR Password)
{
	// TODO: Add your implementation code here
	

	baseMigrationObj->Connecttoserver();
	return S_OK;
}


STDMETHODIMP CMapiWrapper::ImportMailOptions(BSTR OptionsTag)
{
	// TODO: Add your implementation code here
	//baseMigrationObj = new MapiMigration();

	baseMigrationObj->ImportMail();
	return S_OK;
}
