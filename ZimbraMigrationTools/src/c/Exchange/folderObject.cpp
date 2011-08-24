// folderObject.cpp : Implementation of CfolderObject

#include "common.h"

#include "folderObject.h"


// CfolderObject


STDMETHODIMP CfolderObject::InterfaceSupportsErrorInfo(REFIID riid) {
    static const IID *const arr[] = {
        &IID_IfolderObject
    };

    for (int i = 0; i < sizeof (arr) / sizeof (arr[0]); i++) {
        if (InlineIsEqualGUID(*arr[i], riid))
            return S_OK;
    }
    return S_FALSE;
}


STDMETHODIMP CfolderObject::get_Name(BSTR* pVal)
{
	// TODO: Add your implementation code here
	CComBSTR str(Strname);
	*pVal = str.m_str;
	return S_OK;
}


STDMETHODIMP CfolderObject::put_Name(BSTR newVal)
{
	// TODO: Add your implementation code here
	Strname = newVal;

	return S_OK;
}


STDMETHODIMP CfolderObject::get_Id( LONG* pVal)
{
	// TODO: Add your implementation code here
	*pVal = LngID;
	return S_OK;
}


STDMETHODIMP CfolderObject::put_Id(LONG newVal)
{
	// TODO: Add your implementation code here
	LngID = newVal;
	return S_OK;
}

STDMETHODIMP CfolderObject::get_ParentPath(BSTR* pVal)
{
	// TODO: Add your implementation code here
	CComBSTR str(parentPath);
	*pVal = str.m_str;
	return S_OK;
}


STDMETHODIMP CfolderObject::put_ParentPath(BSTR newVal)
{
	// TODO: Add your implementation code here
	parentPath = newVal;

	return S_OK;
}

