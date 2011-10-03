// ItemObject.cpp : Implementation of CItemObject

#include "common.h"
#include "ItemObject.h"
#include "ContactObj.h"

// CItemObject

STDMETHODIMP CItemObject::InterfaceSupportsErrorInfo(REFIID riid)
{
    static const IID *const arr[] = {
        &IID_IItemObject
    };

    for (int i = 0; i < sizeof (arr) / sizeof (arr[0]); i++)
    {
        if (InlineIsEqualGUID(*arr[i], riid))
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

STDMETHODIMP CItemObject::get_Parentfolder(IfolderObject **pVal)
{
    // TODO: Add your implementation code here
    *pVal = parentObj;
    (*pVal)->AddRef();

    return S_OK;
}

STDMETHODIMP CItemObject::put_Parentfolder(IfolderObject *newVal)
{
    // TODO: Add your implementation code here
    parentObj = newVal;
    return S_OK;
}

STDMETHODIMP CItemObject::GetDataForItem(VARIANT *data)
{
    // maapi-
    std::map<BSTR, BSTR> pIt;
    std::map<BSTR, BSTR>::iterator it;
    FolderType Type;
    HRESULT hr = get_Type(&Type);
    if (Type == 2)
    {
        ContactObj *C1 = new ContactObj();
        C1->GetData(pIt);

        VariantInit(data);

        // Create SafeArray of VARIANT BSTRs
        SAFEARRAY *pSA = NULL;
        SAFEARRAYBOUND aDim[2];                 // two dimensional array
        aDim[0].lLbound = 0;
        aDim[0].cElements = 5;
        aDim[1].lLbound = 0;
        aDim[1].cElements = 5;                  // rectangular array
        pSA = SafeArrayCreate(VT_BSTR, 2, aDim);        // again, 2 dimensions
        long aLong[2];
        if (pSA != NULL)
        {
            BSTR temp;
            for (long x = aDim[0].lLbound; x < 2 /*(aDim[0].cElements + aDim[0].lLbound)*/; x++)
            {
                aLong[0] = x;                   // set x index
                it = pIt.begin();
                for (long y = aDim[1].lLbound; y < (long)(aDim[1].cElements + aDim[1].lLbound);
                    y++)
                {
                    aLong[1] = y;               // set y index
                    if (aLong[0] > 0)
                        temp = SysAllocString((*it).second);
                    else
                        temp = SysAllocString((*it).first);
                    hr = SafeArrayPutElement(pSA, aLong, temp);

                    it++;
                }
            }
        }
        data->vt = VT_ARRAY | VT_BSTR;
        data->parray = pSA;
        return S_OK;
    }
    return S_OK;
}

STDMETHODIMP CItemObject::GetDataForItemID(VARIANT ItemId, VARIANT *pVal)
{
    ContactItemData cd;
    SBinary ItemID;

    if (ItemId.vt == (VT_ARRAY | VT_UI1))       // (OLE SAFEARRAY)
    {
        // Retrieve size of array
        ItemID.cb = ItemId.parray->rgsabound[0].cElements;
        ItemID.lpb = new BYTE[ItemID.cb];       // Allocate a buffer to store the data
        if (ItemID.lpb != NULL)
        {
            void *pArrayData;
            // Obtain safe pointer to the array
            SafeArrayAccessData(ItemId.parray, &pArrayData);
            // Copy the bitmap into our buffer
            memcpy(ItemID.lpb, pArrayData, ItemID.cb);  // Unlock the variant data
            SafeArrayUnaccessData(ItemId.parray);

            maapi->GetItem(ItemID, cd);
        }
    }
    HRESULT hr = S_OK;
    std::map<BSTR, BSTR> pIt;
    std::map<BSTR, BSTR>::iterator it;

    pIt[L"BirthDay"] = SysAllocString((cd.Birthday).c_str());
    pIt[L"FirstName"] = SysAllocString((cd.FirstName).c_str());
    pIt[L"JobTitle"] = SysAllocString((cd.JobTitle).c_str());
    pIt[L"CallbackPhone"] = SysAllocString((cd.CallbackPhone).c_str());
    pIt[L"Email1"] = SysAllocString((cd.Email1).c_str());

    VariantInit(pVal);

    // Create SafeArray of VARIANT BSTRs
    SAFEARRAY *pSA = NULL;
    SAFEARRAYBOUND aDim[2];                     // two dimensional array
    aDim[0].lLbound = 0;
    aDim[0].cElements = 5;
    aDim[1].lLbound = 0;
    aDim[1].cElements = 5;                      // rectangular array
    pSA = SafeArrayCreate(VT_BSTR, 2, aDim);    // again, 2 dimensions
    long aLong[2];
    if (pSA != NULL)
    {
        BSTR temp;
        for (long x = aDim[0].lLbound; x < 2 /*(aDim[0].cElements + aDim[0].lLbound)*/; x++)
        {
            aLong[0] = x;                       // set x index
            it = pIt.begin();
            for (long y = aDim[1].lLbound; y < (long)(aDim[1].cElements + aDim[1].lLbound); y++)
            {
                aLong[1] = y;                   // set y index
                if (aLong[0] > 0)
                    temp = SysAllocString((*it).second);
                else
                    temp = SysAllocString((*it).first);
                hr = SafeArrayPutElement(pSA, aLong, temp);

                it++;
            }
        }
    }
    pVal->vt = VT_ARRAY | VT_BSTR;
    pVal->parray = pSA;

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
