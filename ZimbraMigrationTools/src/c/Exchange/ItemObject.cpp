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


STDMETHODIMP CItemObject::get_ID(BSTR *pVal)
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

STDMETHODIMP CItemObject::get_Type(FolderType *pVal)
{
    // TODO: Add your implementation code here

    /*CComBSTR str(TYPE);
     * *pVal = str.m_str;
     * return S_OK;*/
    *pVal = TYPE;
    return S_OK;
}

STDMETHODIMP CItemObject::put_Type(FolderType newVal)
{
    // TODO: Add your implementation code here
    TYPE = newVal;
    return S_OK;
}

STDMETHODIMP CItemObject::get_CreationDate(VARIANT *pVal)
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

STDMETHODIMP CItemObject::get_Parentfolder(IFolderObject **pVal)
{
    // TODO: Add your implementation code here
    *pVal = parentObj;
    (*pVal)->AddRef();

    return S_OK;
}

STDMETHODIMP CItemObject::put_Parentfolder(IFolderObject *newVal)
{
    // TODO: Add your implementation code here
    parentObj = newVal;
    return S_OK;
}


STDMETHODIMP CItemObject::GetDataForItemID(IUserObject *Userobj,VARIANT ItemId, FolderType type, VARIANT *pVal)
{
    HRESULT hr = S_OK;
    std::map<BSTR, BSTR> pIt;
    std::map<BSTR, BSTR>::iterator it;
    //SBinary ItemID;
    FolderType ft;

    if (type == NULL)
    {
        get_Type(&ft);
    }
    else
    {
        ft = type;
    }

	CComPtr<IMapiAccessWrap> Mapi;

	hr = Userobj->GetMapiAccessObject(L"",&Mapi);

	hr = Mapi->GetData(L"",ItemId,ft,pVal);
  

    return hr;
}

STDMETHODIMP CItemObject::put_ItemID(VARIANT id)
{
    // FolderId = id;
// Binary data is stored in the variant as an array of unsigned char
    if (id.vt == (VT_ARRAY | VT_UI1))           // (OLE SAFEARRAY)
    {
        // Retrieve size of array
        ItemID.cb = id.parray->rgsabound[0].cElements;
        ItemID.lpb = new BYTE[ItemID.cb];       // Allocate a buffer to store the data
        if (ItemID.lpb != NULL)
        {
            void *pArrayData;

            // Obtain safe pointer to the array
            SafeArrayAccessData(id.parray, &pArrayData);
            // Copy the bitmap into our buffer
            memcpy(ItemID.lpb, pArrayData, ItemID.cb);  // Unlock the variant data
            SafeArrayUnaccessData(id.parray);
        }
    }
    return S_OK;
}

STDMETHODIMP CItemObject::get_ItemID(VARIANT *id)
{
    HRESULT hr = S_OK;

    VariantInit(id);
    id->vt = VT_ARRAY | VT_UI1;

    SAFEARRAY *psa;
    SAFEARRAYBOUND bounds[1];                   // ={1,0};

    bounds[0].cElements = ItemID.cb;
    bounds[0].lLbound = 0;

    psa = SafeArrayCreate(VT_UI1, 1, bounds);
    if (psa != NULL)
    {
        void *pArrayData = NULL;

        SafeArrayAccessData(psa, &pArrayData);
        memcpy(pArrayData, ItemID.lpb, ItemID.cb);
        // Unlock the variant data
        // SafeArrayUnaccessData(var.parray);
        SafeArrayUnaccessData(psa);
        id->parray = psa;
    }
    return hr;
}
