// ItemObject.cpp : Implementation of CItemObject

#include "common.h"
#include "ItemObject.h"


// CItemObject

STDMETHODIMP CItemObject::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* const arr[] = 
	{
		&IID_IItemObject
	};

	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}



STDMETHODIMP CItemObject::get_ID(BSTR* pVal)
{
	// TODO: Add your implementation code here
	CComBSTR str(ID);
	*pVal = str.m_str;
	return S_OK;
	
}


STDMETHODIMP CItemObject::put_ID(BSTR newVal)
{
	// TODO: Add your implementation code here
	ID = newVal;
	return S_OK;
}


STDMETHODIMP CItemObject::get_Type(FolderType* pVal)
{
	// TODO: Add your implementation code here
	/*CComBSTR str(TYPE);
	*pVal = str.m_str;
	return S_OK;*/
	*pVal = TYPE;
	return S_OK;
}


STDMETHODIMP CItemObject::put_Type(FolderType newVal)
{
	// TODO: Add your implementation code here
	TYPE = newVal;
	return S_OK;
}


STDMETHODIMP CItemObject::get_CreationDate(VARIANT* pVal)
{
	// TODO: Add your implementation code here
	_variant_t vt;
	*pVal = vt;
	return S_OK;
	
}


STDMETHODIMP CItemObject::put_CreationDate(VARIANT newVal)
{
	// TODO: Add your implementation code here
	_variant_t vt = newVal;
	return S_OK;
}


STDMETHODIMP CItemObject::get_Parentfolder(IfolderObject** pVal)
{
	// TODO: Add your implementation code here
	*pVal = parentObj;
	(*pVal)->AddRef();
	
	return S_OK;
}


STDMETHODIMP CItemObject::put_Parentfolder(IfolderObject* newVal)
{
	// TODO: Add your implementation code here
	parentObj = newVal ;
	return S_OK;
}
