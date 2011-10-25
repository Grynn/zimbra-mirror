// folderObject.cpp : Implementation of CfolderObject

#include "common.h"

#include "folderObject.h"

// CfolderObject

STDMETHODIMP CfolderObject::InterfaceSupportsErrorInfo(REFIID riid)
{
    static const IID *const arr[] = {
        &IID_IfolderObject
    };

    for (int i = 0; i < sizeof (arr) / sizeof (arr[0]); i++)
    {
        if (InlineIsEqualGUID(*arr[i], riid))
            return S_OK;
    }
    return S_FALSE;
}

STDMETHODIMP CfolderObject::get_Name(BSTR *pVal)
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

STDMETHODIMP CfolderObject::get_Id(LONG *pVal)
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
STDMETHODIMP CfolderObject::get_ItemCount(LONG *pVal)
{
    // TODO: Add your implementation code here
    *pVal = Itemcnt;
    return S_OK;
}

STDMETHODIMP CfolderObject::put_ItemCount(LONG newVal)
{
    // TODO: Add your implementation code here
    Itemcnt = newVal;
    return S_OK;
}
STDMETHODIMP CfolderObject::get_ParentPath(BSTR *pVal)
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

STDMETHODIMP CfolderObject::put_FolderID(VARIANT id)
{
    // FolderId = id;
// Binary data is stored in the variant as an array of unsigned char
    if (id.vt == (VT_ARRAY | VT_UI1))           // (OLE SAFEARRAY)
    {
        // Retrieve size of array
        FolderId.cb = id.parray->rgsabound[0].cElements;
        FolderId.lpb = new BYTE[FolderId.cb];   // Allocate a buffer to store the data
        if (FolderId.lpb != NULL)
        {
            void *pArrayData;
            // Obtain safe pointer to the array
            SafeArrayAccessData(id.parray, &pArrayData);
            // Copy the bitmap into our buffer
            memcpy(FolderId.lpb, pArrayData, FolderId.cb);      // Unlock the variant data
            SafeArrayUnaccessData(id.parray);
        }
    }
    return S_OK;
}

STDMETHODIMP CfolderObject::get_FolderID(VARIANT *id)
{
    // *id = FolderId;

    /*VARIANT var;
     * VariantInit(&var); //Initialize our variant
     * //Set the type to an array of unsigned chars (OLE SAFEARRAY)
     * var.vt = VT_ARRAY | VT_UI1;
     * //Set up the bounds structure
     * SAFEARRAYBOUND rgsabound[1];
     * rgsabound[0].cElements = FolderId.cb;
     * rgsabound[0].lLbound = 0;
     * //Create an OLE SAFEARRAY
     * var.parray = SafeArrayCreate(VT_UI1,1,rgsabound);
     * if(var.parray != NULL)
     * {
     * void * pArrayData = NULL;
     * //Get a safe pointer to the array
     * SafeArrayAccessData(var.parray,&pArrayData);
     * //Copy data to it
     * memcpy(pArrayData, FolderId.lpb, FolderId.cb);
     * //Unlock the variant data
     * SafeArrayUnaccessData(var.parray);
     * id->parray = var.parray;
     * // *id = var;
     * // Create a COleVariant based on our variant
     * VariantClear(&var);
     *
     * }*/
    HRESULT hr = S_OK;

    VariantInit(id);
    id->vt = VT_ARRAY | VT_UI1;
    SAFEARRAY *psa;
    SAFEARRAYBOUND bounds[1];    // ={1,0};
    bounds[0].cElements = FolderId.cb;
    bounds[0].lLbound = 0;

    psa = SafeArrayCreate(VT_UI1, 1, bounds);
    if (psa != NULL)
    {
        void *pArrayData = NULL;
        SafeArrayAccessData(psa, &pArrayData);
        memcpy(pArrayData, FolderId.lpb, FolderId.cb);
        // Unlock the variant data
        // SafeArrayUnaccessData(var.parray);
        SafeArrayUnaccessData(psa);
        id->parray = psa;
    }
    return hr;
}
