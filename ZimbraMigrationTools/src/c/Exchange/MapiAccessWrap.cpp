// MapiAccessWrap.cpp : Implementation of CMapiAccessWrap

#include "common.h"
#include "MapiAccessWrap.h"

Zimbra::Util::CriticalSection  cs;
// CMapiAccessWrap

STDMETHODIMP CMapiAccessWrap::InterfaceSupportsErrorInfo(REFIID riid)
{
	static const IID* const arr[] = 
	{
		&IID_IMapiAccessWrap
	};

	for (int i=0; i < sizeof(arr) / sizeof(arr[0]); i++)
	{
		if (InlineIsEqualGUID(*arr[i],riid))
			return S_OK;
	}
	return S_FALSE;
}

STDMETHODIMP CMapiAccessWrap::UserInit(BSTR UserName, BSTR userAccount, BSTR *StatusMsg)
{
    Zimbra::Util::AutoCriticalSection autocriticalsection(cs);
    // TODO: Add your implementation code here

    maapi = new Zimbra::MAPI::MAPIAccessAPI(UserName, userAccount);

    // Init session and stores
    LPCWSTR lpStatus = maapi->InitializeUser();

    *StatusMsg = (lpStatus) ? CComBSTR(lpStatus) : SysAllocString(L"");
    return S_OK;
}

STDMETHODIMP CMapiAccessWrap::UserUninit()
{
    // TODO: Add your implementation code here

     delete maapi;

    // *StatusMsg = (lpStatus) ? CComBSTR(lpStatus) : SysAllocString(L"");
    return S_OK;
}

STDMETHODIMP CMapiAccessWrap::GetFolderList(VARIANT *folders)
{
    HRESULT hr = S_OK;

    VariantInit(folders);
    folders->vt = VT_ARRAY | VT_DISPATCH;

    SAFEARRAY *psa;

    USES_CONVERSION;
    vector<Folder_Data> vfolderlist;

    maapi->GetRootFolderHierarchy(vfolderlist);

    std::vector<Folder_Data>::iterator it;
    size_t size = vfolderlist.size();

    it = vfolderlist.begin();

    SAFEARRAYBOUND bounds = { (ULONG)size, 0 };

    psa = SafeArrayCreate(VT_DISPATCH, 1, &bounds);

    IFolderObject **pfolders;

    SafeArrayAccessData(psa, (void **)&pfolders);
    for (size_t i = 0; i < size; i++, it++)
    {
        CComPtr<IFolderObject> pIFolderObject;

        hr = CoCreateInstance(CLSID_FolderObject, NULL, CLSCTX_ALL, IID_IFolderObject,
            reinterpret_cast<void **>(&pIFolderObject));
        if (SUCCEEDED(hr))
        {
            CComBSTR temp((*it).name.c_str());

            pIFolderObject->put_Name(SysAllocString(temp));
            pIFolderObject->put_Id((*it).zimbraid);

            CComBSTR tempS((*it).folderpath.c_str());

            pIFolderObject->put_FolderPath(SysAllocString(tempS));

            CComBSTR temp3((*it).containerclass.c_str());

            pIFolderObject->put_ContainerClass(SysAllocString(temp3));

            pIFolderObject->put_ItemCount((*it).itemcount);

            // /////////////////////////////////////
            VARIANT var;
            SBinary Folderid = (*it).sbin;

            VariantInit(&var);                  // Initialize our variant
            // Set the type to an array of unsigned chars (OLE SAFEARRAY)
            var.vt = VT_ARRAY | VT_UI1;

            // Set up the bounds structure
            SAFEARRAYBOUND rgsabound[1];

            rgsabound[0].cElements = Folderid.cb;
            rgsabound[0].lLbound = 0;
            // Create an OLE SAFEARRAY
            var.parray = SafeArrayCreate(VT_UI1, 1, rgsabound);
            if (var.parray != NULL)
            {
                void *pArrayData = NULL;

                // Get a safe pointer to the array
                SafeArrayAccessData(var.parray, &pArrayData);
                // Copy data to it
                memcpy(pArrayData, Folderid.lpb, Folderid.cb);
                // Unlock the variant data
                SafeArrayUnaccessData(var.parray);
            }
            pIFolderObject->put_FolderID(var);
            // /////////////////////////////////////////////
        }
        if (FAILED(hr))
            return S_FALSE;
        pIFolderObject.CopyTo(&pfolders[i]);
    }
    SafeArrayUnaccessData(psa);
    folders->parray = psa;

    return S_OK;
}

STDMETHODIMP CMapiAccessWrap::GetItemsList(IFolderObject *FolderObj, VARIANT creattiondate,
    VARIANT *vItems)
{
    HRESULT hr = S_OK;

    VariantInit(vItems);
    vItems->vt = VT_ARRAY | VT_DISPATCH;

    SAFEARRAY *psa;

    vector<Item_Data> vItemDataList;
    vector<Item_Data>::iterator it;

    SBinary folderEntryid;

    folderEntryid.cb = 0;
    folderEntryid.lpb = NULL;

    USES_CONVERSION;

    VARIANT vararg;

    VariantInit(&vararg);
    vararg.vt = (VT_ARRAY | VT_UI1);

    FolderObj->get_FolderID(&vararg);
    if (vararg.vt == (VT_ARRAY | VT_UI1))       // (OLE SAFEARRAY)
    {
        // Retrieve size of array
        folderEntryid.cb = vararg.parray->rgsabound[0].cElements;

        ULONG size = folderEntryid.cb;

        folderEntryid.lpb = new BYTE[size];     // Allocate a buffer to store the data
        if (folderEntryid.lpb != NULL)
        {
            void *pArrayData;

            // Obtain safe pointer to the array
            SafeArrayAccessData(vararg.parray, &pArrayData);
            // Copy the bitmap into our buffer
            memcpy(folderEntryid.lpb, pArrayData, size);        // Unlock the variant data
            SafeArrayUnaccessData(vararg.parray);

            maapi->GetFolderItemsList(folderEntryid, vItemDataList);
        }
    }

    size_t size = vItemDataList.size();

    it = vItemDataList.begin();

    SAFEARRAYBOUND bounds = { (ULONG)size, 0 };

    psa = SafeArrayCreate(VT_DISPATCH, 1, &bounds);

    IItemObject **pItems;

    SafeArrayAccessData(psa, (void **)&pItems);
    for (size_t i = 0; i < size; i++, it++)
    {
        CComPtr<IItemObject> pIItemObject;
        // Isampleobj* pIStatistics;
        hr = CoCreateInstance(CLSID_ItemObject, NULL, CLSCTX_ALL, IID_IItemObject,
            reinterpret_cast<void **>(&pIItemObject));
        if (SUCCEEDED(hr))
        {
            /*pIFolderObject->put_Name(L"testoing"); // so far so good
             * pIFolderObject->put_Id(12222);
             * pIFolderObject->put_ParentPath(L"\\Inbox\\personal\\mine");*/

            pIItemObject->put_Type((FolderType)((*it).lItemType));
            // pIItemObject->put_ID((*it).sbMessageID))
            pIItemObject->put_Parentfolder(FolderObj);
            creattiondate.vt = VT_DATE;
            creattiondate.date = (long)(*it).MessageDate;

// /////////////////////////////////////////////

            VARIANT var;
            SBinary Itemid = (*it).sbMessageID;

            VariantInit(&var);                  // Initialize our variant
            // Set the type to an array of unsigned chars (OLE SAFEARRAY)
            var.vt = VT_ARRAY | VT_UI1;

            // Set up the bounds structure
            SAFEARRAYBOUND rgsabound[1];

            rgsabound[0].cElements = Itemid.cb;
            rgsabound[0].lLbound = 0;
            // Create an OLE SAFEARRAY
            var.parray = SafeArrayCreate(VT_UI1, 1, rgsabound);
            if (var.parray != NULL)
            {
                void *pArrayData = NULL;

                // Get a safe pointer to the array
                SafeArrayAccessData(var.parray, &pArrayData);
                // Copy data to it
                memcpy(pArrayData, Itemid.lpb, Itemid.cb);
                // Unlock the variant data
                SafeArrayUnaccessData(var.parray);
            }
            pIItemObject->put_ItemID(var);
        }
        if (FAILED(hr))
            return S_FALSE;
        // if

        {
            pIItemObject.CopyTo(&pItems[i]);
        }
    }
    SafeArrayUnaccessData(psa);
    vItems->parray = psa;
    if (folderEntryid.lpb != NULL)
        delete folderEntryid.lpb;
    return S_OK;
}

STDMETHODIMP CMapiAccessWrap::GetData(BSTR UserId, VARIANT ItemId, FolderType type,
    VARIANT *pVal)
{
    HRESULT hr = S_OK;
    std::map<BSTR, BSTR> pIt;
    std::map<BSTR, BSTR>::iterator it;
    SBinary ItemID;
    FolderType ft;
    CComBSTR name = UserId;

    ft = type;
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
                if (cd.Type.length() > 0)
                {
                    if (wcsicmp(cd.Type.c_str(), L"group") == 0)
                    {
                        pIt[L"type"] = SysAllocString(cd.Type.c_str());
                        pIt[L"dlist"] = SysAllocString((cd.pDList).c_str());
                    }
                }
                if (cd.UserDefinedFields.size() > 0)
                {
                    vector<ContactUDFields>::iterator it;
                    for (it = cd.UserDefinedFields.begin(); it != cd.UserDefinedFields.end();
                        it++)
                    {
                        BSTR bstrNam = SysAllocString(it->Name.c_str());

                        pIt[bstrNam] = SysAllocString(it->value.c_str());
                    }
                }
                bool bHasTags = false;
                if (cd.vTags)
                {
                    wstring tagData;
                    int numTags = (int)cd.vTags->size();
                    if (numTags > 0)
                    {
                        for (int i = 0; i < numTags; i++)
                        {
                            tagData += (*cd.vTags)[i];
                            if (i < (numTags - 1))
                            {
                                tagData += L",";
                            }
                        }
                        pIt[L"tags"] = SysAllocString(tagData.c_str());
                        delete cd.vTags;
                        bHasTags = true;
                    }
                }
                if (!bHasTags)
                {
                    pIt[L"tags"] = SysAllocString(L"");
                }
            }
            else if ((ft == 1) || (ft == 5))    // message or meeting request
            {
                MessageItemData msgdata;

                printf("Got message item:");
                maapi->GetItem(ItemID, msgdata);
                pIt[L"Subject"] = SysAllocString((msgdata.Subject).c_str());
                pIt[L"Date"] = SysAllocString((msgdata.DateString).c_str());
                pIt[L"filePath"] = SysAllocString((msgdata.MimeFile).c_str());
                pIt[L"UrlName"] = SysAllocString((msgdata.Urlname).c_str());
                pIt[L"rcvdDate"] = SysAllocString((msgdata.DeliveryUnixString.c_str()));

                bool bHasTags = false;
                if (msgdata.vTags)
                {
                    wstring tagData;
                    int numTags = (int)msgdata.vTags->size();
                    if (numTags > 0)
                    {
                        for (int i = 0; i < numTags; i++)
                        {
                            tagData += (*msgdata.vTags)[i];
                            if (i < (numTags - 1))
                            {
                                tagData += L",";
                            }
                        }
                        pIt[L"tags"] = SysAllocString(tagData.c_str());
                        delete msgdata.vTags;
                        bHasTags = true;
                    }
                }
                if (!bHasTags)
                {
                    pIt[L"tags"] = SysAllocString(L"");
                }

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
                pIt[L"class"] = SysAllocString((apptData.ApptClass).c_str());
                pIt[L"orAddr"] = SysAllocString((apptData.organizer.addr).c_str());
                pIt[L"orName"] = SysAllocString((apptData.organizer.nam).c_str());
                pIt[L"contentType0"] = SysAllocString(
                    (apptData.vMessageParts[0].contentType).c_str());
                pIt[L"content0"] = SysAllocString((apptData.vMessageParts[0].content).c_str());
                pIt[L"contentType1"] = SysAllocString(
                    (apptData.vMessageParts[1].contentType).c_str());
                pIt[L"content1"] = SysAllocString((apptData.vMessageParts[1].content).c_str());

                // attendees
                wstring attendeeData;
                int numAttendees = (int)apptData.vAttendees.size();     // cast it because in delete loop, we'll go negative

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
                        if (i < (numAttendees - 1))     // don't write comma after last attendee
                            attendeeData += L",";
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
                    pIt[L"count"] = SysAllocString((apptData.recurCount).c_str());      // can set this either way
                    if (apptData.recurEndDate.length() > 0)
                        pIt[L"until"] = SysAllocString((apptData.recurEndDate).c_str());
                    if (apptData.recurWkday.length() > 0)
                        pIt[L"wkday"] = SysAllocString((apptData.recurWkday).c_str());
                    if (apptData.recurDayOfMonth.length() > 0)
                        pIt[L"modaylist"] = SysAllocString((apptData.recurDayOfMonth).c_str());
                    if (apptData.recurMonthOfYear.length() > 0)
                        pIt[L"molist"] = SysAllocString((apptData.recurMonthOfYear).c_str());
                    if (apptData.recurMonthOccurrence.length() > 0)
                        pIt[L"poslist"] = SysAllocString(
                            (apptData.recurMonthOccurrence).c_str());
                     // timezone
                    pIt[L"tid"] = SysAllocString((apptData.tz.id).c_str());
                    pIt[L"stdoff"] = SysAllocString((apptData.tz.standardOffset).c_str());
                    pIt[L"dayoff"] = SysAllocString((apptData.tz.daylightOffset).c_str());
                    pIt[L"sweek"] = SysAllocString((apptData.tz.standardStartWeek).c_str());
                    pIt[L"swkday"] = SysAllocString((apptData.tz.standardStartWeekday).c_str());
                    pIt[L"smon"] = SysAllocString((apptData.tz.standardStartMonth).c_str());
                    pIt[L"shour"] = SysAllocString((apptData.tz.standardStartHour).c_str());
                    pIt[L"smin"] = SysAllocString((apptData.tz.standardStartMinute).c_str());
                    pIt[L"ssec"] = SysAllocString((apptData.tz.standardStartSecond).c_str());
                    pIt[L"dweek"] = SysAllocString((apptData.tz.daylightStartWeek).c_str());
                    pIt[L"dwkday"] = SysAllocString((apptData.tz.daylightStartWeekday).c_str());
                    pIt[L"dmon"] = SysAllocString((apptData.tz.daylightStartMonth).c_str());
                    pIt[L"dhour"] = SysAllocString((apptData.tz.daylightStartHour).c_str());
                    pIt[L"dmin"] = SysAllocString((apptData.tz.daylightStartMinute).c_str());
                    pIt[L"dsec"] = SysAllocString((apptData.tz.daylightStartSecond).c_str());

                    int numExceptions = (int)apptData.vExceptions.size();   
                    if (numExceptions > 0)
                    {
                        WCHAR pwszNumExceptions[10];
                        BSTR attrs[NUM_EXCEPTION_ATTRS];

                        _ltow(numExceptions, pwszNumExceptions, 10);
                        pIt[L"numExceptions"] = SysAllocString(pwszNumExceptions);
                        for (int i = 0; i < numExceptions; i++)
                        {
                            CreateExceptionAttrs(attrs, i);
                            pIt[attrs[0]] =  SysAllocString((apptData.vExceptions[i]->GetExceptionType()).c_str());
                            pIt[attrs[1]]  = SysAllocString((apptData.vExceptions[i]->GetResponseStatus()).c_str());
                            pIt[attrs[2]]  = SysAllocString((apptData.vExceptions[i]->GetBusyStatus()).c_str());
                            pIt[attrs[3]]  = SysAllocString((apptData.vExceptions[i]->GetAllday()).c_str());
                            pIt[attrs[4]]  = SysAllocString((apptData.vExceptions[i]->GetSubject()).c_str());
                            pIt[attrs[5]]  = SysAllocString((apptData.vExceptions[i]->GetSubject()).c_str());
                            pIt[attrs[6]]  = SysAllocString((apptData.vExceptions[i]->GetLocation()).c_str());
                            pIt[attrs[7]]  = SysAllocString((apptData.vExceptions[i]->GetReminderMinutes()).c_str());
                            pIt[attrs[8]]  = SysAllocString((apptData.vExceptions[i]->GetStartDate()).c_str());
                            pIt[attrs[9]]  = SysAllocString((apptData.vExceptions[i]->GetEndDate()).c_str());
                            pIt[attrs[10]] = SysAllocString((apptData.vExceptions[i]->GetOrganizerAddr()).c_str());
                            pIt[attrs[11]] = SysAllocString((apptData.vExceptions[i]->GetOrganizerName()).c_str());
                            pIt[attrs[12]] = SysAllocString(L"text/plain");
                            pIt[attrs[13]] = SysAllocString((apptData.vExceptions[i]->GetPlainTextFileAndContent()).c_str());
                            pIt[attrs[14]] = SysAllocString(L"text/html");
                            pIt[attrs[15]] = SysAllocString((apptData.vExceptions[i]->GetHtmlFileAndContent()).c_str());
                        }

                        // clean up any exceptions
                        for (int i = (numExceptions - 1); i >= 0; i--)
                        {
                            delete (apptData.vExceptions[i]);
                        }
                        //
                    }
                }
            }
            else if (ft == 4)
            {
                TaskItemData taskData;

                maapi->GetItem(ItemID, taskData);
                pIt[L"name"] = SysAllocString((taskData.Subject).c_str());
                pIt[L"su"] = SysAllocString((taskData.Subject).c_str());
                pIt[L"priority"] = SysAllocString((taskData.Importance).c_str());
                pIt[L"s"] = SysAllocString((taskData.TaskStart).c_str());
                pIt[L"e"] = SysAllocString((taskData.TaskDue).c_str());
                pIt[L"status"] = SysAllocString((taskData.Status).c_str());
                pIt[L"percentComplete"] = SysAllocString((taskData.PercentComplete).c_str());
                pIt[L"xp-TOTAL_WORK"] = SysAllocString((taskData.TotalWork).c_str());
                pIt[L"xp-ACTUAL_WORK"] = SysAllocString((taskData.ActualWork).c_str());
                pIt[L"xp-COMPANIES"] = SysAllocString((taskData.Companies).c_str());
                pIt[L"xp-MILEAGE"] = SysAllocString((taskData.Mileage).c_str());
                pIt[L"xp-BILLING"] = SysAllocString((taskData.BillingInfo).c_str());
                if (taskData.TaskFlagDueBy.length() > 0)
                {
                    pIt[L"taskflagdueby"] = SysAllocString((taskData.TaskFlagDueBy).c_str());
                }
                pIt[L"contentType0"] = SysAllocString((taskData.vMessageParts[0].contentType).c_str());
                pIt[L"content0"] = SysAllocString((taskData.vMessageParts[0].content).c_str());
                pIt[L"contentType1"] = SysAllocString((taskData.vMessageParts[1].contentType).c_str());
                pIt[L"content1"] = SysAllocString((taskData.vMessageParts[1].content).c_str());
                // recurrence
                if (taskData.recurPattern.length() > 0)
                {
                    pIt[L"freq"] = SysAllocString((taskData.recurPattern).c_str());
                    pIt[L"ival"] = SysAllocString((taskData.recurInterval).c_str());
                    pIt[L"count"] = SysAllocString((taskData.recurCount).c_str());      // can set this either way
                    if (taskData.recurEndDate.length() > 0)
                        pIt[L"until"] = SysAllocString((taskData.recurEndDate).c_str());
                    if (taskData.recurWkday.length() > 0)
                        pIt[L"wkday"] = SysAllocString((taskData.recurWkday).c_str());
                    if (taskData.recurDayOfMonth.length() > 0)
                        pIt[L"modaylist"] = SysAllocString((taskData.recurDayOfMonth).c_str());
                    if (taskData.recurMonthOfYear.length() > 0)
                        pIt[L"molist"] = SysAllocString((taskData.recurMonthOfYear).c_str());
                    if (taskData.recurMonthOccurrence.length() > 0)
                        pIt[L"poslist"] = SysAllocString(
                            (taskData.recurMonthOccurrence).c_str());
                     /*
                      * // timezone
                      * pIt[L"tid"] = SysAllocString((apptData.tz.id).c_str());
                      * pIt[L"stdoff"] = SysAllocString((apptData.tz.standardOffset).c_str());
                      * pIt[L"dayoff"] = SysAllocString((apptData.tz.daylightOffset).c_str());
                      * pIt[L"sweek"] = SysAllocString((apptData.tz.standardStartWeek).c_str());
                      * pIt[L"swkday"] = SysAllocString((apptData.tz.standardStartWeekday).c_str());
                      * pIt[L"smon"] = SysAllocString((apptData.tz.standardStartMonth).c_str());
                      * pIt[L"shour"] = SysAllocString((apptData.tz.standardStartHour).c_str());
                      * pIt[L"smin"] = SysAllocString((apptData.tz.standardStartMinute).c_str());
                      * pIt[L"ssec"] = SysAllocString((apptData.tz.standardStartSecond).c_str());
                      * pIt[L"dweek"] = SysAllocString((apptData.tz.daylightStartWeek).c_str());
                      * pIt[L"dwkday"] = SysAllocString((apptData.tz.daylightStartWeekday).c_str());
                      * pIt[L"dmon"] = SysAllocString((apptData.tz.daylightStartMonth).c_str());
                      * pIt[L"dhour"] = SysAllocString((apptData.tz.daylightStartHour).c_str());
                      * pIt[L"dmin"] = SysAllocString((apptData.tz.daylightStartMinute).c_str());
                      * pIt[L"dsec"] = SysAllocString((apptData.tz.daylightStartSecond).c_str());
                      * //
                      */
                }
            }
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

STDMETHODIMP CMapiAccessWrap::GetOOOInfo(BSTR *OOOInfo)
{
    LPCWSTR lpInfo = maapi->GetOOOStateAndMsg();
    *OOOInfo = CComBSTR(lpInfo);
    delete[] lpInfo;
    return S_OK;
}

STDMETHODIMP CMapiAccessWrap::GetRuleList(VARIANT *rules)
{
    // This is the big method for Exchange rules processing.  It reads the PR_RULES_TABLE,
    // gets the info back, and creates a map (pMap) that will be read by the Zimbra API
    // layer (SetModifyFilterRulesRequest, which calls AddFilterRuleToRequest for each rule.)
    // Assuming there are two rules, Foo and Bar, here is an example of pMap.  We'll only show
    // the format of the first rule.  There are 3 entries for each rule, rule, tests and actions.
    // So the first rule will have 0filterRule, 0filterTests, 0filterActions, the second will have
    // 1filterRule, 1filterTests, 1filterActions, etc.  There is a numRules name/value pair that
    // gives the number of rules.  In a given entry, there are certain token delimiters.  The rule
    // entry uses ",", the test entry uses an initial ":" followed by "`~".  The action entries
    // just delimit tokens by "`~".  The string "^^^" is the delimiter for multiple filterTests
    // and multiple filterActions.
    // Here is an example of a map for the rule:
    // "with foo in the subject, forward it to user2 and move it to the Test folder"

    // name            value
    // ----            -----
    // numRules        1
    // 0filterRule     name,TestRule,active,1
    // 0filterTests"   allof:headerTest`~index`~0`~stringComparison`~contains`~header`~Subject`~value`~foo
    // 0filterActions  actionRedirect`~a`~user2^^^actionFileInto`~folderPath`~Test
    
    HRESULT hr = S_OK;
    std::map<BSTR, BSTR> pMap;
    LPWSTR pwszLine = new WCHAR[1024];

    VariantInit(rules);
    rules->vt = VT_ARRAY | VT_DISPATCH;

    USES_CONVERSION;
    vector<CRule> vRuleList;

    maapi->GetExchangeRules(vRuleList);

    std::vector<CRule>::iterator ruleIndex;
    size_t numRules = vRuleList.size();
    if (numRules == 0)
    {
        return S_OK;
    }

    WCHAR pwszTemp[5];
    _itow((int)numRules, pwszTemp, 10);
    pMap[L"numRules"] = SysAllocString(pwszTemp);

    CRuleMap* pRuleMap = new CRuleMap();

    // Create the array of attrs (0filterRule, 0filterTests, 0filterActions, 1filterRule, etc.)
    // std:map needs all names allocated separately
    int numAttrs = ((int)numRules) * 3;
    WCHAR tmp[10];
    LPWSTR* ruleMapNames = new LPWSTR[numAttrs];
    for (int i = 0; i < numAttrs; i += 3)
    {
        _ltow(i/3, tmp, 10);
        ruleMapNames[i] = new WCHAR[20];
        lstrcpy(ruleMapNames[i], tmp);
        lstrcat(ruleMapNames[i], L"filterRule");
        ruleMapNames[i + 1] = new WCHAR[20];
        lstrcpy(ruleMapNames[i + 1], tmp);
        lstrcat(ruleMapNames[i + 1], L"filterTests");
        ruleMapNames[i + 2] = new WCHAR[20];
        lstrcpy(ruleMapNames[i + 2], tmp);
        lstrcat(ruleMapNames[i + 2], L"filterActions");
    }
    /////////////////

    int iIndex = 0;
    int iMapIndex = 0;
    for (ruleIndex = vRuleList.begin(); ruleIndex != vRuleList.end(); ruleIndex++)
    {
        std::wstring wstrRuleCondition;
        CRule &rule = *ruleIndex;

        iMapIndex = iIndex * 3;
        pRuleMap->WriteFilterRule(rule, pwszLine);
        pMap[ruleMapNames[iMapIndex]] = SysAllocString(pwszLine);
        pRuleMap->WriteFilterTests(rule, pwszLine);
        pMap[ruleMapNames[iMapIndex + 1]] = SysAllocString(pwszLine);
        pRuleMap->WriteFilterActions(rule, pwszLine);
        pMap[ruleMapNames[iMapIndex + 2]] = SysAllocString(pwszLine);
        iIndex++;
    }

    delete pRuleMap;
    delete pwszLine;

    std::map<BSTR, BSTR>::iterator it;
    VariantInit(rules);

    // Create SafeArray of VARIANT BSTRs
    SAFEARRAY *pSA = NULL;
    SAFEARRAYBOUND aDim[2];                     // two dimensional array

    aDim[0].lLbound = 0;
    aDim[0].cElements = (ULONG)pMap.size();
    aDim[1].lLbound = 0;
    aDim[1].cElements = (ULONG)pMap.size();      // rectangular array
    pSA = SafeArrayCreate(VT_BSTR, 2, aDim);    // again, 2 dimensions

    long aLong[2];

    if (pSA != NULL)
    {
        BSTR temp;

        for (long x = aDim[0].lLbound; x < 2 /*(aDim[0].cElements + aDim[0].lLbound)*/; x++)
        {
            aLong[0] = x;                       // set x index
            it = pMap.begin();
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
    rules->vt = VT_ARRAY | VT_BSTR;
    rules->parray = pSA;

    // Now that the map is set, delete the rule map names.
    // Don't need to go backwards, but it's a good convention
    for (int i = (numAttrs - 1); i >= 0; i--)
    {
        delete ruleMapNames[i];
    }
    delete ruleMapNames;
    ////

    return hr;
}

void CMapiAccessWrap::CreateExceptionAttrs(BSTR attrs[], int num)
{
    WCHAR pwszNum[10];
    LPWSTR names[] = {L"exceptionType", L"ptst", L"fb", L"allDay",
                      L"name", L"su", L"loc", L"m", L"s", L"e",                      
                      L"orAddr", L"orName", L"contentType0", L"content0",
                      L"contentType1", L"content1"};
                     
    WCHAR pwszAttr[30];

    _ltow(num, pwszNum, 10);
    for (int i = 0; i < NUM_EXCEPTION_ATTRS; i++)
    {
	lstrcpy(pwszAttr, names[i]);
        lstrcat(pwszAttr, L"_");
	lstrcat(pwszAttr, pwszNum);
	attrs[i] = SysAllocString(pwszAttr);
    }
}
