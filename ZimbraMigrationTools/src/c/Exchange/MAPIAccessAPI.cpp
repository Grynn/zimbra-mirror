#include "common.h"
#include "Exchange.h"
#include "MAPIAccessAPI.h"

Zimbra::MAPI::MAPISession *MAPIAccessAPI::m_zmmapisession = NULL;
Zimbra::MAPI::MAPIStore *MAPIAccessAPI::m_defaultStore = NULL;
std::wstring MAPIAccessAPI::m_strTargetProfileName = L"";
std::wstring MAPIAccessAPI::m_strExchangeHostName = L"";
bool MAPIAccessAPI::m_bSingleMailBoxMigration = false;

// Initialize with Exchange Sever hostname, Outlook Admin profile name, Exchange mailbox name to be migrated
MAPIAccessAPI::MAPIAccessAPI(wstring strUserName): m_userStore(NULL), m_rootFolder(NULL)
{
    if (strUserName.empty())
        m_bSingleMailBoxMigration = true;

    else
        m_strUserName = strUserName;
    MAPIInitialize(NULL);

    Zimbra::Mapi::Memory::SetMemAllocRoutines(NULL, MAPIAllocateBuffer, MAPIAllocateMore,
        MAPIFreeBuffer);

    InitFoldersToSkip();
}

MAPIAccessAPI::~MAPIAccessAPI()
{
    if ((m_userStore) && (!m_bSingleMailBoxMigration))
        delete m_userStore;
    m_userStore = NULL;
    MAPIUninitialize();
    m_bSingleMailBoxMigration = false;
}

void MAPIAccessAPI::InitFoldersToSkip()
{
    FolderToSkip[TS_JOURNAL] = JOURNAL;
    FolderToSkip[TS_OUTBOX] = OUTBOX;
    FolderToSkip[TS_SYNC_CONFLICTS] = SYNC_CONFLICTS;
    FolderToSkip[TS_SYNC_ISSUES] = SYNC_ISSUES;
    FolderToSkip[TS_SYNC_LOCAL_FAILURES] = SYNC_LOCAL_FAILURES;
    FolderToSkip[TS_SYNC_SERVER_FAILURES] = SYNC_SERVER_FAILURES;
    // FolderToSkip[TS_JUNK_MAIL] = JUNK_MAIL;
}

bool MAPIAccessAPI::SkipFolder(ExchangeSpecialFolderId exfid)
{
    for (int i = TS_JOURNAL; i < TS_FOLDERS_MAX; i++)
    {
        if (FolderToSkip[i] == exfid)
            return true;
    }
    return false;
}

LPCWSTR MAPIAccessAPI::InitGlobalSessionAndStore(LPCWSTR lpcwstrMigTarget)
{
    LPCWSTR lpwstrStatus = NULL;

    // Get Domain
    m_strExchangeHostName = Zimbra::MAPI::Util::GetDomainName();
    if (m_strExchangeHostName.empty())
    {
        lpwstrStatus = FromatExceptionInfo(E_FAIL, L"GetDomainName Failed.", __FILE__,
            __LINE__);
        goto CLEAN_UP;
    }
    try
    {
        // Logon into target profile
        m_zmmapisession = new Zimbra::MAPI::MAPISession();

        // Detreming if its a profile or PST by extension
        wstring strMigTarget = lpcwstrMigTarget;
        std::transform(strMigTarget.begin(), strMigTarget.end(), strMigTarget.begin(),
            ::toupper);

        // if pst file, create associated MAPI profile for migration
        if (strMigTarget.find(L".PST") != std::wstring::npos)
        {
            LPSTR lpstrMigTarget;

            WtoA((LPWSTR)lpcwstrMigTarget, lpstrMigTarget);

            // delete any left over profiles from previous migration
            Zimbra::MAPI::Util::DeleteAlikeProfiles(
                Zimbra::MAPI::Util::PSTMIG_PROFILE_PREFIX.c_str());
            string strPSTProfileName = Zimbra::MAPI::Util::PSTMIG_PROFILE_PREFIX;

            // Add timestamp to profile to make it unique
            char timeStr[9];

            _strtime(timeStr);

            string strTmpProfile(timeStr);

            replace(strTmpProfile.begin(), strTmpProfile.end(), ':', '_');
            strPSTProfileName += strTmpProfile;
            // create PST profile
            if (!Zimbra::MAPI::Util::CreatePSTProfile((LPSTR)strPSTProfileName.c_str(),
                lpstrMigTarget))
            {
                SafeDelete(lpstrMigTarget);
                lpwstrStatus = FromatExceptionInfo(E_FAIL, L"CreatePSTProfile Failed.",
                    __FILE__, __LINE__);
                goto CLEAN_UP;
            }
            SafeDelete(lpstrMigTarget);

            LPWSTR wstrProfileName;

            AtoW((LPSTR)strPSTProfileName.c_str(), wstrProfileName);
            m_strTargetProfileName = wstrProfileName;
            SafeDelete(wstrProfileName);
        }
        else
        {
            m_strTargetProfileName = lpcwstrMigTarget;
        }

        HRESULT hr = m_zmmapisession->Logon((LPWSTR)m_strTargetProfileName.c_str());

        if (hr != S_OK)
            goto CLEAN_UP;
        m_defaultStore = new Zimbra::MAPI::MAPIStore();

        // Open target default store
        hr = m_zmmapisession->OpenDefaultStore(*m_defaultStore);
        if (hr != S_OK)
            goto CLEAN_UP;
    }
    catch (MAPISessionException &msse)
    {
        lpwstrStatus = FromatExceptionInfo(msse.ErrCode(), (LPWSTR)msse.Description().c_str(),
            (LPSTR)msse.SrcFile().c_str(), msse.SrcLine());
    }
    catch (MAPIStoreException &mste)
    {
        lpwstrStatus = FromatExceptionInfo(mste.ErrCode(), (LPWSTR)mste.Description().c_str(),
            (LPSTR)mste.SrcFile().c_str(), mste.SrcLine());
    }
    catch (Util::MapiUtilsException &muex)
    {
        lpwstrStatus = FromatExceptionInfo(muex.ErrCode(), (LPWSTR)muex.Description().c_str(),
            (LPSTR)muex.SrcFile().c_str(), muex.SrcLine());
    }
    // Create Temporary dir for temp files
    Zimbra::MAPI::Util::CreateAppTemporaryDirectory();

CLEAN_UP: if (lpwstrStatus)
    {
        if (m_zmmapisession)
            delete m_zmmapisession;
        m_zmmapisession = NULL;
        if (m_defaultStore)
            delete m_defaultStore;
        m_defaultStore = NULL;
    }
    return lpwstrStatus;
}

void MAPIAccessAPI::UnInitGlobalSessionAndStore()
{
    // Delete any PST migration profiles
    Zimbra::MAPI::Util::DeleteAlikeProfiles(Zimbra::MAPI::Util::PSTMIG_PROFILE_PREFIX.c_str());

    if (m_defaultStore)
        delete m_defaultStore;
    m_defaultStore = NULL;
    if (m_zmmapisession)
        delete m_zmmapisession;
    m_zmmapisession = NULL;
}

// Open MAPI sessiona and Open Stores
LPCWSTR MAPIAccessAPI::OpenUserStore()
{
    LPCWSTR lpwstrStatus = NULL;
    HRESULT hr = S_OK;
    wstring wstruserdn;
    wstring legacyName;
    LPSTR ExchangeServerDN = NULL;
    LPSTR ExchangeUserDN = NULL;
    LPWSTR pwstrExchangeServerDN = NULL;

    try
    {
        // user store
        m_userStore = new Zimbra::MAPI::MAPIStore();
        // Get Exchange Server DN
        hr = Zimbra::MAPI::Util::GetUserDnAndServerDnFromProfile(
            m_zmmapisession->GetMAPISessionObject(), ExchangeServerDN, ExchangeUserDN);
        if (hr != S_OK)
            goto CLEAN_UP;
        AtoW(ExchangeServerDN, pwstrExchangeServerDN);

        // Get DN of user to be migrated
        Zimbra::MAPI::Util::GetUserDNAndLegacyName(m_strExchangeHostName.c_str(),
            m_strUserName.c_str(), NULL, wstruserdn, legacyName);

        hr = m_zmmapisession->OpenOtherStore(m_defaultStore->GetInternalMAPIStore(),
            pwstrExchangeServerDN, (LPWSTR)legacyName.c_str(), *m_userStore);
        if (hr != S_OK)
            goto CLEAN_UP;
    }
    catch (MAPISessionException &msse)
    {
        lpwstrStatus = FromatExceptionInfo(msse.ErrCode(), (LPWSTR)msse.Description().c_str(),
            (LPSTR)msse.SrcFile().c_str(), msse.SrcLine());
    }
    catch (MAPIStoreException &mste)
    {
        lpwstrStatus = FromatExceptionInfo(mste.ErrCode(), (LPWSTR)mste.Description().c_str(),
            (LPSTR)mste.SrcFile().c_str(), mste.SrcLine());
    }
    catch (Util::MapiUtilsException &muex)
    {
        lpwstrStatus = FromatExceptionInfo(muex.ErrCode(), (LPWSTR)muex.Description().c_str(),
            (LPSTR)muex.SrcFile().c_str(), muex.SrcLine());
    }
CLEAN_UP: SafeDelete(ExchangeServerDN);
    SafeDelete(ExchangeUserDN);
    SafeDelete(pwstrExchangeServerDN);
    if (hr != S_OK)
        lpwstrStatus = FromatExceptionInfo(hr, L"MAPIAccessAPI::OpenSessionAndStore() Failed",
            __FILE__, __LINE__);
    return lpwstrStatus;
}

// Get root folders
LPCWSTR MAPIAccessAPI::InitializeUser()
{
    LPCWSTR lpwstrStatus = NULL;
    HRESULT hr = S_OK;

    try
    {
        if (!m_bSingleMailBoxMigration)
        {
            lpwstrStatus = OpenUserStore();
            if (lpwstrStatus)
                return lpwstrStatus;
        }
        else
        {
            // if profile to be migrated
            m_userStore = m_defaultStore;
        }
        // Get root folder from user store
        m_rootFolder = new Zimbra::MAPI::MAPIFolder(*m_zmmapisession, *m_userStore);
        if (FAILED(hr = m_userStore->GetRootFolder(*m_rootFolder)))
            lpwstrStatus = FromatExceptionInfo(hr, L"MAPIAccessAPI::Initialize() Failed",
                __FILE__, __LINE__);
    }
    catch (GenericException &ge)
    {
        lpwstrStatus = FromatExceptionInfo(ge.ErrCode(), (LPWSTR)ge.Description().c_str(),
            (LPSTR)ge.SrcFile().c_str(), ge.SrcLine());
    }
    return lpwstrStatus;
}

LPCWSTR MAPIAccessAPI::GetRootFolderHierarchy(vector<Folder_Data> &vfolderlist)
{
    LPCWSTR lpwstrStatus = NULL;
    HRESULT hr = S_OK;

    try
    {
        hr = Iterate_folders(*m_rootFolder, vfolderlist);
    }
    catch (GenericException &ge)
    {
        lpwstrStatus = FromatExceptionInfo(ge.ErrCode(), (LPWSTR)ge.Description().c_str(),
            (LPSTR)ge.SrcFile().c_str(), ge.SrcLine());
    }
    return lpwstrStatus;
}

HRESULT MAPIAccessAPI::Iterate_folders(Zimbra::MAPI::MAPIFolder &folder,
    vector<Folder_Data> &fd)
{
    Zimbra::MAPI::FolderIterator *folderIter = new Zimbra::MAPI::FolderIterator;

    folder.GetFolderIterator(*folderIter);

    BOOL bMore = TRUE;

    while (bMore)
    {
        ULONG itemCount = 0;

        // delete them while clearing the tree nodes
        Zimbra::MAPI::MAPIFolder *childFolder = new Zimbra::MAPI::MAPIFolder(*m_zmmapisession,
            *m_userStore);

        bMore = folderIter->GetNext(*childFolder);

        bool bSkipFolder = false;
        ExchangeSpecialFolderId exfid = childFolder->GetExchangeFolderId();
        wstring wstrContainerClass;

        childFolder->ContainerClass(wstrContainerClass);
        // skip folders in exclusion list, hidden folders and non-standard type folders
        if (SkipFolder(exfid) || childFolder->HiddenFolder() || (((wstrContainerClass !=
            L"IPF.Note") && (wstrContainerClass != L"IPF.Contact") && (wstrContainerClass !=
            L"IPF.Appointment") && (wstrContainerClass != L"IPF.Task") && (wstrContainerClass !=
            L"IPF.StickyNote") && (wstrContainerClass != L"")) && (exfid ==
            SPECIAL_FOLDER_ID_NONE)))
            bSkipFolder = true;
        if (bMore && !bSkipFolder)
        {
            childFolder->GetItemCount(itemCount);

            // store foldername
            Folder_Data flderdata;

            flderdata.name = childFolder->Name();

            // folder item count
            flderdata.itemcount = itemCount;

            // store Folder EntryID
            SBinary sbin = childFolder->EntryID();

            CopyEntryID(sbin, flderdata.sbin);

            // folder path
            flderdata.folderpath = childFolder->GetFolderPath();

            // container class
            flderdata.containerclass = wstrContainerClass;

            // ExchangeFolderID
            flderdata.zimbraid = (long)childFolder->GetZimbraFolderId();

            // append
            fd.push_back(flderdata);
        }
        if (bMore && !bSkipFolder)
            Iterate_folders(*childFolder, fd);
        delete childFolder;
        childFolder = NULL;
    }
    delete folderIter;
    folderIter = NULL;
    return S_OK;
}

HRESULT MAPIAccessAPI::GetInternalFolder(SBinary sbFolderEID, MAPIFolder &folder)
{
    LPMAPIFOLDER pFolder = NULL;
    HRESULT hr = S_OK;
    ULONG objtype;

    if ((hr = m_userStore->OpenEntry(sbFolderEID.cb, (LPENTRYID)sbFolderEID.lpb, NULL,
            MAPI_BEST_ACCESS, &objtype, (LPUNKNOWN *)&pFolder)) != S_OK)
        throw GenericException(hr, L"GetFolderItems OpenEntry Failed.", __LINE__, __FILE__);

    Zimbra::Util::ScopedBuffer<SPropValue> pPropValues;

    // Get PR_DISPLAY_NAME
    if (FAILED(hr = HrGetOneProp(pFolder, PR_DISPLAY_NAME, pPropValues.getptr())))
        throw GenericException(hr, L"GetFolderItems HrGetOneProp() Failed.", __LINE__,
            __FILE__);
    folder.Initialize(pFolder, pPropValues->Value.LPSZ, &sbFolderEID);
    return hr;
}

LPCWSTR MAPIAccessAPI::GetFolderItemsList(SBinary sbFolderEID, vector<Item_Data> &ItemList)
{
    LPCWSTR lpwstrStatus = NULL;
    HRESULT hr = S_OK;
    MAPIFolder folder;

    try
    {
        if (FAILED(hr = GetInternalFolder(sbFolderEID, folder) != S_OK))
        {
            lpwstrStatus = FromatExceptionInfo(hr,
                L"MAPIAccessAPI::GetFolderItemsList() Failed", __FILE__, __LINE__);
            goto ZM_EXIT;
        }

        Zimbra::MAPI::MessageIterator *msgIter = new Zimbra::MAPI::MessageIterator();

        folder.GetMessageIterator(*msgIter);

        BOOL bContinue = true;

        while (bContinue)
        {
            Zimbra::MAPI::MAPIMessage *msg = new Zimbra::MAPI::MAPIMessage();

            bContinue = msgIter->GetNext(*msg);
            if (bContinue)
            {
                Item_Data itemdata;

                //
                itemdata.lItemType = msg->ItemType();

                SBinary sbin = msg->EntryID();

                CopyEntryID(sbin, itemdata.sbMessageID);
                itemdata.MessageDate = msg->Date();
                ItemList.push_back(itemdata);
            }
            delete msg;
        }
        delete msgIter;
    }
    catch (MAPISessionException &mssex)
    {
        lpwstrStatus = FromatExceptionInfo(mssex.ErrCode(), (LPWSTR)mssex.Description().c_str(),
            (LPSTR)mssex.SrcFile().c_str(), mssex.SrcLine());
    }
    catch (MAPIFolderException &mfex)
    {
        lpwstrStatus = FromatExceptionInfo(mfex.ErrCode(), (LPWSTR)mfex.Description().c_str(),
            (LPSTR)mfex.SrcFile().c_str(), mfex.SrcLine());
    }
    catch (MAPIMessageException &msgex)
    {
        lpwstrStatus = FromatExceptionInfo(msgex.ErrCode(), (LPWSTR)msgex.Description().c_str(),
            (LPSTR)msgex.SrcFile().c_str(), msgex.SrcLine());
    }
    catch (GenericException &genex)
    {
        lpwstrStatus = FromatExceptionInfo(genex.ErrCode(), (LPWSTR)genex.Description().c_str(),
            (LPSTR)genex.SrcFile().c_str(), genex.SrcLine());
    }
ZM_EXIT: return lpwstrStatus;
}

LPCWSTR MAPIAccessAPI::GetItem(SBinary sbItemEID, BaseItemData &itemData)
{
    LPWSTR lpwstrStatus = NULL;
    HRESULT hr = S_OK;
    LPMESSAGE pMessage = NULL;
    ULONG objtype;

    if (FAILED(hr = m_userStore->OpenEntry(sbItemEID.cb, (LPENTRYID)sbItemEID.lpb, NULL,
            MAPI_BEST_ACCESS, &objtype, (LPUNKNOWN *)&pMessage)))
    {
        lpwstrStatus = FromatExceptionInfo(hr, L"MAPIAccessAPI::GetItem() Failed", __FILE__,
            __LINE__);
        goto ZM_EXIT;
    }
    try
    {
        MAPIMessage msg;

        msg.Initialize(pMessage, *m_zmmapisession);
        if (msg.ItemType() == ZT_MAIL)
        {
            printf("ITEM TYPE: ZT_MAIL \n");

            MessageItemData *msgdata = (MessageItemData *)&itemData;

            // subject
            msgdata->Subject = L"";

            LPTSTR lpstrsubject;

            if (msg.Subject(&lpstrsubject))
            {
                msgdata->Subject = lpstrsubject;
                SafeDelete(lpstrsubject);
            }
            msgdata->IsFlagged = msg.IsFlagged();

            msgdata->Urlname = L"";

            LPTSTR lpstrUrlName;

            if (msg.GetURLName(&lpstrUrlName))
            {
                msgdata->Urlname = lpstrUrlName;
                SafeDelete(lpstrUrlName);
            }
            msgdata->IsDraft = msg.IsDraft();
            msgdata->IsFromMe = (msg.IsFromMe() == TRUE);
            msgdata->IsUnread = (msg.IsUnread() == TRUE);
            msgdata->IsForwared = (msg.Forwarded() == TRUE);
            msgdata->RepliedTo = msg.RepliedTo() == TRUE;
            msgdata->HasAttachments = msg.HasAttach();
            msgdata->IsUnsent = msg.IsUnsent() == TRUE;
            msgdata->HasHtml = msg.HasHtmlPart();
            msgdata->HasText = msg.HasTextPart();

            msgdata->Date = msg.Date();

            LPWSTR wstrDateString;

            AtoW(msg.DateString(), wstrDateString);
            msgdata->DateString = wstrDateString;
            SafeDelete(wstrDateString);

            msgdata->deliveryDate = msg.DeliveryDate();

            LPWSTR wstrDelivUnixString;

            AtoW(msg.DeliveryUnixString(), wstrDelivUnixString);
            msgdata->DeliveryUnixString = wstrDelivUnixString;
            SafeDelete(wstrDelivUnixString);

            LPWSTR wstrDelivDateString;

            AtoW(msg.DeliveryDateString(), wstrDelivDateString);
            msgdata->DeliveryDateString = wstrDelivDateString;
            SafeDelete(wstrDelivDateString);

/*
 *          if (msgdata->HasText)
 *          {
 *              LPTSTR textMsgBuffer;
 *              unsigned int nTextchars;
 *              msg.TextBody(&textMsgBuffer, nTextchars);
 *              msgdata->textbody.buffer = textMsgBuffer;
 *              msgdata->textbody.size = nTextchars;
 *          }
 *          if (msgdata->HasHtml)
 *          {
 *              LPVOID pHtmlBodyBuffer = NULL;
 *              unsigned int nHtmlchars;
 *              msg.HtmlBody(&pHtmlBodyBuffer, nHtmlchars);
 *              msgdata->htmlbody.buffer = (LPTSTR)pHtmlBodyBuffer;
 *              msgdata->htmlbody.size = nHtmlchars;
 *          }
 */
            // Save mime to file in temp dir
            HRESULT hr = S_OK;
            ULONG nBytesWritten = 0;
            ULONG nTotalBytesWritten = 0;
            wstring wstrTempAppDirPath;
            char *lpszDirName = NULL;
            char *lpszUniqueName = NULL;
            Zimbra::Util::ScopedInterface<IStream> pStream;
            mimepp::Message mimeMsg;

            msg.ToMimePPMessage(mimeMsg);

            LPCSTR pDes = mimeMsg.getString().c_str();
            int nBytesToBeWritten = (int)(mimeMsg.getString().size());

            if (!Zimbra::MAPI::Util::GetAppTemporaryDirectory(wstrTempAppDirPath))
            {
                lpwstrStatus = FromatExceptionInfo(hr, L"GetAppTemporaryDirectory Failed",
                    __FILE__, __LINE__);
                goto ZM_EXIT;
            }
            WtoA((LPWSTR)wstrTempAppDirPath.c_str(), lpszDirName);

            string strFQFileName = lpszDirName;

            WtoA((LPWSTR)Zimbra::MAPI::Util::GetUniqueName().c_str(), lpszUniqueName);
            strFQFileName += "\\";
            strFQFileName += lpszUniqueName;
            SafeDelete(lpszDirName);
            SafeDelete(lpszUniqueName);
            // Open stream on file
            if (FAILED(hr = OpenStreamOnFile(MAPIAllocateBuffer, MAPIFreeBuffer, STGM_CREATE |
                    STGM_READWRITE, (LPTSTR)strFQFileName.c_str(), NULL, pStream.getptr())))
            {
                lpwstrStatus = FromatExceptionInfo(hr,
                    L"Message Error: OpenStreamOnFile Failed.", __FILE__, __LINE__);
                goto ZM_EXIT;
            }
            // write to file
            while (!FAILED(hr) && nBytesToBeWritten > 0)
            {
                hr = pStream->Write(pDes, nBytesToBeWritten, &nBytesWritten);
                pDes += nBytesWritten;
                nBytesToBeWritten -= nBytesWritten;
                nTotalBytesWritten += nBytesWritten;
                nBytesWritten = 0;
            }
            if (FAILED(hr = pStream->Commit(0)))
                return L"Message Error: Stream Commit Failed.";
                         // mime file path

            LPWSTR lpwstrFQFileName = NULL;

            AtoW((LPSTR)strFQFileName.c_str(), lpwstrFQFileName);
            msgdata->MimeFile = lpwstrFQFileName;
            SafeDelete(lpwstrFQFileName);
        }
        else if (msg.ItemType() == ZT_CONTACTS)
        {
            printf("ITEM TYPE: ZT_CONTACTS \n");

            MAPIContact mapicontact(*m_zmmapisession, msg);
            ContactItemData *cd = (ContactItemData *)&itemData;

            cd->Birthday = mapicontact.Birthday();
            cd->CallbackPhone = mapicontact.CallbackPhone();
            cd->CarPhone = mapicontact.CarPhone();
            cd->Company = mapicontact.Company();
            cd->Email1 = mapicontact.Email();
            cd->Email2 = mapicontact.Email2();
            cd->Email3 = mapicontact.Email3();
            cd->FileAs = mapicontact.FileAs();
            cd->FirstName = mapicontact.FirstName();
            cd->HomeCity = mapicontact.HomeCity();
            cd->HomeCountry = mapicontact.HomeCountry();
            cd->HomeFax = mapicontact.HomeFax();
            cd->HomePhone = mapicontact.HomePhone();
            cd->HomePhone2 = mapicontact.HomePhone2();
            cd->HomePostalCode = mapicontact.HomePostalCode();
            cd->HomeState = mapicontact.HomeState();
            cd->HomeStreet = mapicontact.HomeStreet();
            cd->HomeURL = mapicontact.HomeURL();
            cd->IMAddress1 = mapicontact.IMAddress1();
            cd->JobTitle = mapicontact.JobTitle();
            cd->LastName = mapicontact.LastName();
            cd->MiddleName = mapicontact.MiddleName();
            cd->MobilePhone = mapicontact.MobilePhone();
            cd->NamePrefix = mapicontact.NamePrefix();
            cd->NameSuffix = mapicontact.NameSuffix();
            cd->NickName = mapicontact.NickName();
            cd->Notes = mapicontact.Notes();
            cd->OtherCity = mapicontact.OtherCity();
            cd->OtherCountry = mapicontact.OtherCountry();
            cd->OtherFax = mapicontact.OtherFax();
            cd->OtherPhone = mapicontact.OtherPhone();
            cd->OtherPostalCode = mapicontact.OtherPostalCode();
            cd->OtherState = mapicontact.OtherState();
            cd->OtherStreet = mapicontact.OtherStreet();
            cd->OtherURL = mapicontact.OtherURL();
            cd->Pager = mapicontact.Pager();
            cd->pDList = L"";                   // mapicontact.P
            cd->PictureID = mapicontact.Picture();
            cd->Type = mapicontact.Type();
            cd->UserField1 = mapicontact.UserField1();
            cd->UserField2 = mapicontact.UserField2();
            cd->UserField3 = mapicontact.UserField3();
            cd->UserField4 = mapicontact.UserField4();
            cd->WorkCity = mapicontact.WorkCity();
            cd->WorkCountry = mapicontact.WorkCountry();
            cd->WorkFax = mapicontact.WorkFax();
            cd->WorkPhone = mapicontact.WorkPhone();
            cd->WorkPostalCode = mapicontact.WorkPostalCode();
            cd->WorkState = mapicontact.WorkState();
            cd->WorkStreet = mapicontact.WorkStreet();
            cd->WorkURL = mapicontact.WorkURL();
            cd->ContactImagePath = mapicontact.ContactImagePath();
            cd->Anniversary = mapicontact.Anniversary();
        }
        else if (msg.ItemType() == ZT_APPOINTMENTS)
        {
            MAPIAppointment mapiappointment(*m_zmmapisession, msg);
            ApptItemData *ad = (ApptItemData *)&itemData;
            ad->Subject = mapiappointment.GetSubject();
            ad->Name = mapiappointment.GetSubject();
            ad->StartDate = mapiappointment.GetStartDate();
            ad->EndDate = mapiappointment.GetEndDate();
            ad->Location = mapiappointment.GetLocation();
            ad->PartStat = mapiappointment.GetResponseStatus();
            ad->FreeBusy = mapiappointment.GetBusyStatus();
            ad->AllDay = mapiappointment.GetAllday();
            ad->Transparency = mapiappointment.GetTransparency();
            ad->AlarmTrigger = mapiappointment.GetReminderMinutes();

            MessagePart mp;
            mp.contentType = L"text/plain";
            mp.content = mapiappointment.GetPlainTextFileAndContent();
            ad->vMessageParts.push_back(mp);
            mp.contentType = L"text/html";
            mp.content = mapiappointment.GetHtmlFileAndContent();
            ad->vMessageParts.push_back(mp);
            // TODO: ad->Organizer, ad->UID
        }
        else if (msg.ItemType() == ZT_TASKS)
        {
            printf("ITEM TYPE: ZT_TASKS \n");
        }
        else if (msg.ItemType() == ZT_MEETREQ_RESP)
        {
            printf("ITEM TYPE: ZT_MEETREQ_RESP \n");
        }
    }
    catch (MAPIMessageException &mex)
    {
        lpwstrStatus = FromatExceptionInfo(mex.ErrCode(), (LPWSTR)mex.Description().c_str(),
            (LPSTR)mex.SrcFile().c_str(), mex.SrcLine());
    }
    catch (MAPIContactException &cex)
    {
        lpwstrStatus = FromatExceptionInfo(cex.ErrCode(), (LPWSTR)cex.Description().c_str(),
            (LPSTR)cex.SrcFile().c_str(), cex.SrcLine());
    }
ZM_EXIT: return lpwstrStatus;
}

// Access MAPI folder items
void MAPIAccessAPI::travrese_folder(Zimbra::MAPI::MAPIFolder &folder)
{
    Zimbra::MAPI::MessageIterator *msgIter = new Zimbra::MAPI::MessageIterator();

    folder.GetMessageIterator(*msgIter);

    BOOL bContinue = true;

    while (bContinue)
    {
        Zimbra::MAPI::MAPIMessage *msg = new Zimbra::MAPI::MAPIMessage();

        bContinue = msgIter->GetNext(*msg);
        if (bContinue)
        {
            Zimbra::Util::ScopedBuffer<WCHAR> subject;

            if (msg->Subject(subject.getptr()))
                printf("\tsubject--%S\n", subject.get());
        }
        delete msg;
    }
    delete msgIter;
}
