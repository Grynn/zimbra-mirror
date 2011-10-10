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
     HRESULT hr = S_OK;
    std::map<BSTR, BSTR> pIt;
    std::map<BSTR, BSTR>::iterator it;

    SBinary ItemID;

	FolderType ft;
	get_Type(&ft);


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
			if(ft ==2)
			{
			ContactItemData cd;
			maapi->GetItem(ItemID, cd);
			 pIt[L"BirthDay"] = SysAllocString((cd.Birthday).c_str());
			pIt[L"FirstName"] = SysAllocString((cd.FirstName).c_str());
			pIt[L"JobTitle"] = SysAllocString((cd.JobTitle).c_str());
			pIt[L"CallbackPhone"] = SysAllocString((cd.CallbackPhone).c_str());
			pIt[L"Email1"] = SysAllocString((cd.Email1).c_str());
			}
			else if( ft == 1)
			{
				MessageItemData msgdata;
	
				printf("Got message item:");
				maapi->GetItem(ItemID,msgdata);
				pIt[L"Subject"] = SysAllocString((msgdata.Subject).c_str());
				pIt[L"Date"] = SysAllocString(( msgdata.DateString).c_str());
			    pIt[L"JobTitle"] = SysAllocString((msgdata.Urlname).c_str());
				pIt[L"filePath"] = SysAllocString((msgdata.MimeFile).c_str());
				pIt[L"UrlName"] = SysAllocString((msgdata.Urlname).c_str());
				
				pIt[L"rcvdDate"] =  SysAllocString(( msgdata.DeliveryDateString.c_str()));

			
				CComBSTR flags =L"";
				if(msgdata.HasAttachments)
				{
				flags.AppendBSTR(L"a");
				}
				if(msgdata.IsUnread)
				{
					flags.AppendBSTR(L"u");
				}
				if(msgdata.IsFlagged)
				{
					flags.AppendBSTR(L"f");
				}
				/*if(msgdata.HasText)
				{
					flags.AppendBSTR(L"T");
				}
				if(msgdata.HasHtml)
				{
					flags.AppendBSTR(L"H");
				}*/
				if(msgdata.IsDraft)
				{
					flags.AppendBSTR(L"d");
				}
				if(msgdata.IsForwared)
				{
					flags.AppendBSTR(L"w");
				}
				if(msgdata.IsUnsent)
				{
					flags.AppendBSTR(L"s");
				}
				if(msgdata.RepliedTo)
				{
					flags.AppendBSTR(L"r");
				}
			
				/*pIt[L"Has Attachments"] = (msgdata.HasAttachments)? L"True":L"False";
				pIt[L"HasHTML"] = (msgdata.HasHtml)? L"True":L"False";
				pIt[L"HasText"] = (msgdata.HasText)? L"True":L"False";
				pIt[L"IsDraft"] = (msgdata.IsDraft)? L"True":L"False";
				pIt[L"IsFlagged"] = (msgdata.IsFlagged)? L"True":L"False";
				pIt[L"IsForwared"] = (msgdata.IsForwared)? L"True":L"False";
				pIt[L"IsFromMe"] = (msgdata.IsFromMe)? L"True":L"False";
				pIt[L"IsUnread"] = (msgdata.IsUnread)? L"True":L"False";
				pIt[L"IsUnsent"] = (msgdata.IsUnsent)? L"True":L"False";
				pIt[L"IsUnread"] = (msgdata.IsUnread)? L"True":L"False";
				pIt[L"RepliedTo"] = (msgdata.IsUnread)? L"True":L"False";*/
				
				pIt[L"flags"] = SysAllocString(flags);


				pIt[L"UrlName"] = SysAllocString((msgdata.Urlname).c_str());

				/*printf("Subject: %S Date: %I64X DateString:%S		\
					DeliveryDate: %I64X deliveryDateString: %S		\
					Has Attachments: %d Has HTML:%d Has Text:%d	\
					Is Draft:%d Is Flagged: %d Is Forwarded: %d	\
					IsFromMe:%d IsUnread:%d IsUnsent:%d IsRepliedTo:%d	\
					URLName: %S\n",
					msgdata.Subject.c_str(), msgdata.Date, msgdata.DateString.c_str(),
					msgdata.deliveryDate, msgdata.DeliveryDateString.c_str(),msgdata.HasAttachments,
					msgdata.HasHtml, msgdata.HasText,msgdata.IsDraft,msgdata.IsFlagged,msgdata.IsForwared,
					msgdata.IsFromMe, msgdata.IsUnread, msgdata.IsUnsent,msgdata.RepliedTo,msgdata.Urlname.c_str()
					);

				printf("MIME FILE PATH: %S\n\n\n\n", msgdata.MimeFile.c_str());*/
			



			}
            
        }
    }
   

	
	
	










	////

    VariantInit(pVal);

    // Create SafeArray of VARIANT BSTRs
    SAFEARRAY *pSA = NULL;
    SAFEARRAYBOUND aDim[2];                     // two dimensional array
    aDim[0].lLbound = 0;
    aDim[0].cElements =  pIt.size();  
    aDim[1].lLbound = 0;
    aDim[1].cElements = pIt.size();                      // rectangular array
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
