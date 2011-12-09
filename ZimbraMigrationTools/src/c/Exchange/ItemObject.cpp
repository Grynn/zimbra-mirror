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
        aDim[1].cElements = 5;                          // rectangular array
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

STDMETHODIMP CItemObject::GetDataForItemID(BSTR UserId, VARIANT ItemId, FolderType type, VARIANT *pVal)
{
    HRESULT hr = S_OK;
    std::map<BSTR, BSTR> pIt;
    std::map<BSTR, BSTR>::iterator it;
    SBinary ItemID;
    FolderType ft;

    if (type == NULL)
    {
        get_Type(&ft);
    }
    else
    {
        ft = type;
    }
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

	    // We can do better than this.  Should get the pointer.  This is temporary
	    Zimbra::MAPI::MAPIAccessAPI *maapi = new Zimbra::MAPI::MAPIAccessAPI(UserId);
	    maapi->InitializeUser();

            if (ft == 2)
            {
                ContactItemData cd;

                maapi->GetItem(ItemID, cd);
                pIt[L"birthday"] = SysAllocString((cd.Birthday).c_str());
                pIt[L"anniversary"] = SysAllocString((cd.Anniversary).c_str());
                pIt[L"callbackPhone"] = SysAllocString((cd.CallbackPhone).c_str());
                pIt[L"carPhone"] = SysAllocString((cd.CarPhone).c_str());
                pIt[L"company"] = SysAllocString((cd.Company).c_str());
                pIt[L"email"] = SysAllocString((cd.Email1).c_str());
                pIt[L"email2"] = SysAllocString((cd.Email2).c_str());
                pIt[L"email3"] = SysAllocString((cd.Email3).c_str());
                pIt[L"fileAs"] = SysAllocString((cd.FileAs).c_str());
                pIt[L"firstName"] = SysAllocString((cd.FirstName).c_str());
                pIt[L"homeCity"] = SysAllocString((cd.HomeCity).c_str());
                pIt[L"homeCountry"] = SysAllocString((cd.HomeCountry).c_str());
                pIt[L"homeFax"] = SysAllocString((cd.HomeFax).c_str());
                pIt[L"homePhone"] = SysAllocString((cd.HomePhone).c_str());
                pIt[L"homePhone2"] = SysAllocString((cd.HomePhone2).c_str());
                pIt[L"homePostalCode"] = SysAllocString((cd.HomePostalCode).c_str());
                pIt[L"homeState"] = SysAllocString((cd.HomeState).c_str());
                pIt[L"homeStreet"] = SysAllocString((cd.HomeStreet).c_str());
                pIt[L"homeURL"] = SysAllocString((cd.HomeURL).c_str());
                pIt[L"jobTitle"] = SysAllocString((cd.JobTitle).c_str());
                pIt[L"lastName"] = SysAllocString((cd.LastName).c_str());
                pIt[L"middleName"] = SysAllocString((cd.MiddleName).c_str());
                pIt[L"mobilePhone"] = SysAllocString((cd.MobilePhone).c_str());
                pIt[L"namePrefix"] = SysAllocString((cd.NamePrefix).c_str());
                pIt[L"nameSuffix"] = SysAllocString((cd.NameSuffix).c_str());
                pIt[L"notes"] = SysAllocString((cd.Notes).c_str());
                pIt[L"otherCity"] = SysAllocString((cd.OtherCity).c_str());
                pIt[L"outerCountry"] = SysAllocString((cd.OtherCountry).c_str());
                pIt[L"otherFax"] = SysAllocString((cd.OtherFax).c_str());
                pIt[L"otherPhone"] = SysAllocString((cd.OtherPhone).c_str());
                pIt[L"otherPostalCode"] = SysAllocString((cd.OtherPostalCode).c_str());
                pIt[L"otherState"] = SysAllocString((cd.OtherState).c_str());
                pIt[L"otherStreet"] = SysAllocString((cd.OtherStreet).c_str());
                pIt[L"otherURL"] = SysAllocString((cd.OtherURL).c_str());
                pIt[L"pager"] = SysAllocString((cd.Pager).c_str());
                pIt[L"workCity"] = SysAllocString((cd.WorkCity).c_str());
                pIt[L"workCountry"] = SysAllocString((cd.WorkCountry).c_str());
                pIt[L"workFax"] = SysAllocString((cd.WorkFax).c_str());
                pIt[L"workPhone"] = SysAllocString((cd.WorkPhone).c_str());
                pIt[L"workPostalCode"] = SysAllocString((cd.WorkPostalCode).c_str());
                pIt[L"workState"] = SysAllocString((cd.WorkState).c_str());
                pIt[L"workStreet"] = SysAllocString((cd.WorkStreet).c_str());
                pIt[L"workURL"] = SysAllocString((cd.WorkURL).c_str());
                pIt[L"outlookUserField1"] = SysAllocString((cd.UserField1).c_str());
                pIt[L"outlookUserField2"] = SysAllocString((cd.UserField2).c_str());
                pIt[L"outlookUserField3"] = SysAllocString((cd.UserField3).c_str());
                pIt[L"outlookUserField4"] = SysAllocString((cd.UserField4).c_str());
                pIt[L"image"] = SysAllocString((cd.ContactImagePath).c_str());
            }
            else if (ft == 1)
            {
                MessageItemData msgdata;

                printf("Got message item:");
                maapi->GetItem(ItemID, msgdata);
                pIt[L"Subject"] = SysAllocString((msgdata.Subject).c_str());
                pIt[L"Date"] = SysAllocString((msgdata.DateString).c_str());
                pIt[L"filePath"] = SysAllocString((msgdata.MimeFile).c_str());
                pIt[L"UrlName"] = SysAllocString((msgdata.Urlname).c_str());
                pIt[L"rcvdDate"] = SysAllocString((msgdata.DeliveryUnixString.c_str()));

                CComBSTR flags = L"";

                if (msgdata.HasAttachments)
                    wcscat(flags, L"a");
                if (msgdata.IsUnread)
                    wcscat(flags, L"u");
                if (msgdata.IsFlagged)
                    wcscat(flags, L"f");
                                 /*if(msgdata.HasText)
                                  * {
                                  *      flags.AppendBSTR(L"T");
                                  * }
                                  * if(msgdata.HasHtml)
                                  * {
                                  *      flags.AppendBSTR(L"H");
                                  * }*/
                if (msgdata.IsDraft)
                    wcscat(flags, L"d");
                if (msgdata.IsForwared)
                    wcscat(flags, L"w");
                if ((msgdata.IsUnsent) || (msgdata.Urlname.substr(0, 11) == L"/Sent Items"))
                    wcscat(flags, L"s");
                if (msgdata.RepliedTo)
                    wcscat(flags, L"r");
                                 /*pIt[L"Has Attachments"] = (msgdata.HasAttachments)? L"True":L"False";
                                  * pIt[L"HasHTML"] = (msgdata.HasHtml)? L"True":L"False";
                                  * pIt[L"HasText"] = (msgdata.HasText)? L"True":L"False";
                                  * pIt[L"IsDraft"] = (msgdata.IsDraft)? L"True":L"False";
                                  * pIt[L"IsFlagged"] = (msgdata.IsFlagged)? L"True":L"False";
                                  * pIt[L"IsForwared"] = (msgdata.IsForwared)? L"True":L"False";
                                  * pIt[L"IsFromMe"] = (msgdata.IsFromMe)? L"True":L"False";
                                  * pIt[L"IsUnread"] = (msgdata.IsUnread)? L"True":L"False";
                                  * pIt[L"IsUnsent"] = (msgdata.IsUnsent)? L"True":L"False";
                                  * pIt[L"IsUnread"] = (msgdata.IsUnread)? L"True":L"False";
                                  * pIt[L"RepliedTo"] = (msgdata.IsUnread)? L"True":L"False";*/

                pIt[L"flags"] = SysAllocString(flags);

                /*printf("Subject: %S Date: %I64X DateString:%S		\
                 *      DeliveryDate: %I64X deliveryDateString: %S		\
                 *      Has Attachments: %d Has HTML:%d Has Text:%d	\
                 *      Is Draft:%d Is Flagged: %d Is Forwarded: %d	\
                 *      IsFromMe:%d IsUnread:%d IsUnsent:%d IsRepliedTo:%d	\
                 *      URLName: %S\n",
                 *      msgdata.Subject.c_str(), msgdata.Date, msgdata.DateString.c_str(),
                 *      msgdata.deliveryDate, msgdata.DeliveryDateString.c_str(),msgdata.HasAttachments,
                 *      msgdata.HasHtml, msgdata.HasText,msgdata.IsDraft,msgdata.IsFlagged,msgdata.IsForwared,
                 *      msgdata.IsFromMe, msgdata.IsUnread, msgdata.IsUnsent,msgdata.RepliedTo,msgdata.Urlname.c_str()
                 *      );
                 *
                 * printf("MIME FILE PATH: %S\n\n\n\n", msgdata.MimeFile.c_str());*/
            }
	    else if (ft == 3)
	    {
		ApptItemData apptData;

		maapi->GetItem(ItemID, apptData);
		pIt[L"ptst"] = SysAllocString((apptData.PartStat).c_str());
		pIt[L"fb"] = SysAllocString((apptData.FreeBusy).c_str());
		pIt[L"allDay"] = SysAllocString((apptData.AllDay).c_str());
		pIt[L"transp"] = SysAllocString((apptData.Transparency).c_str());
		pIt[L"name"] = SysAllocString((apptData.Name).c_str());
		pIt[L"su"] = SysAllocString((apptData.Subject).c_str());
		pIt[L"loc"] = SysAllocString((apptData.Location).c_str());
		pIt[L"uid"] = SysAllocString((apptData.Uid).c_str());
		pIt[L"m"] = SysAllocString((apptData.AlarmTrigger).c_str());
		pIt[L"s"] = SysAllocString((apptData.StartDate).c_str());
		pIt[L"e"] = SysAllocString((apptData.EndDate).c_str());
		pIt[L"orAddr"] = SysAllocString((apptData.organizer.addr).c_str());
		pIt[L"orName"] = SysAllocString((apptData.organizer.nam).c_str());
		pIt[L"contentType0"] = SysAllocString((apptData.vMessageParts[0].contentType).c_str());
		pIt[L"content0"] = SysAllocString((apptData.vMessageParts[0].content).c_str());
		pIt[L"contentType1"] = SysAllocString((apptData.vMessageParts[1].contentType).c_str());
		pIt[L"content1"] = SysAllocString((apptData.vMessageParts[1].content).c_str());
		
		// attendees	
		wstring attendeeData;
		int numAttendees = (int)apptData.vAttendees.size(); // cast it because in delete loop, we'll go negative
		if (numAttendees > 0)
		{
		    for (int i = 0; i < numAttendees; i++)
		    {
			attendeeData += apptData.vAttendees[i]->nam;
			attendeeData += L",";
			attendeeData += apptData.vAttendees[i]->addr;
                        attendeeData += L",";
			attendeeData += apptData.vAttendees[i]->role;
			attendeeData += L",";
			attendeeData += apptData.vAttendees[i]->partstat;
			if (i < (numAttendees - 1))	// don't write comma after last attendee
			{
			    attendeeData += L",";
			}
		    }
		    pIt[L"attendees"] = SysAllocString(attendeeData.c_str());

		    // now clean up
		    for (int i = (numAttendees - 1); i >= 0; i--)
		    {
			delete (apptData.vAttendees[i]);
		    }
		}

		// recurrence
		if (apptData.recurPattern.length() > 0)
		{
		    pIt[L"freq"] = SysAllocString((apptData.recurPattern).c_str());
		    pIt[L"ival"] = SysAllocString((apptData.recurInterval).c_str());
		    pIt[L"count"] = SysAllocString((apptData.recurCount).c_str());
		    if (apptData.recurPattern == L"WEE")
		    {
			pIt[L"wkday"] = SysAllocString((apptData.recurWkday).c_str());
		    }
		}
	    }
	    delete maapi;	// temporary
        }
	delete ItemID.lpb;
    }
    // //

    VariantInit(pVal);

    // Create SafeArray of VARIANT BSTRs
    SAFEARRAY *pSA = NULL;
    SAFEARRAYBOUND aDim[2];                     // two dimensional array

    aDim[0].lLbound = 0;
    aDim[0].cElements = (ULONG)pIt.size();
    aDim[1].lLbound = 0;
    aDim[1].cElements = (ULONG)pIt.size();      // rectangular array
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
