// ExchangeMigObject.cpp : Implementation of CExchangeMigObject

#include "stdafx.h"
#include "ExchangeMigObject.h"


// CExchangeMigObject

STDMETHODIMP CExchangeMigObject::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* const arr[] = 
	{
		&IID_IExchangeMigObject
	};

	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}


STDMETHODIMP CExchangeMigObject::InitializeMigration(BSTR ConfigXMLFileName, BSTR UserMapFileName)
{
	// TODO: Add your implementation code here
	MigrationObj = new MapiMigration();
	MigrationObj->SetConfigXMLFile(ConfigXMLFileName);
	MigrationObj->SetUserMapFile(UserMapFileName);



	return S_OK;
}


STDMETHODIMP CExchangeMigObject::ConnectToserver(void)
{
	MigrationObj->Connecttoserver();

	return S_OK;
}


STDMETHODIMP CExchangeMigObject::ImportMailoptions(void)
{
	MigrationObj->ImportContacts();

	return S_OK;
}
