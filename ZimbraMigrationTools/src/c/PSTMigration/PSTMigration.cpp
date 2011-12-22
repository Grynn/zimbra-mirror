#include "common.h"
#include "..\Exchange\Exchange.h"
#include "..\Exchange\ExchangeAdmin.h"
#include "Zimbra/RPC.h"
#include "..\Exchange\MAPIAccessAPI.h"

#pragma comment(lib, "netapi32.lib")

#include <lm.h>

#define PROFILE_MIGARTION 0

LPCWSTR lpProfileName = L"testprofile";
LPCWSTR lpExchangeServer = L"10.112.16.164";
LPCWSTR lpServerAddress = L"10.117.82.163";
LPCWSTR lpAdminUser = L"admin@zcs2.zmexch.in.zimbra.com";
LPCWSTR lpAccountUser = L"av1@zcs2.zmexch.in.zimbra.com";
LPCWSTR lpAccountUserPwd = L"test123";
LPCWSTR lpAdminPwd = L"z1mbr4";
ULONG nAdminPort = 7071;
ULONG nPort = 80;
Zimbra::Rpc::Connection *m_pConnection = NULL;

void Init()
{
    Zimbra::Mapi::Memory::SetMemAllocRoutines(NULL, MAPIAllocateBuffer, MAPIAllocateMore,
        MAPIFreeBuffer);
}

void AdminAuth()
{
    Zimbra::Rpc::AdminAuthRequest authRequest(lpAdminUser, lpAdminPwd, L"");
    Zimbra::Rpc::AdminConnection *m_pAdminConnection;
    Zimbra::Util::ScopedInterface<IXMLDOMDocument2> pResponseXml;

    m_pAdminConnection = new Zimbra::Rpc::AdminConnection(lpServerAddress, nAdminPort, TRUE, 0,
        L"");
    m_pAdminConnection->SetCurrentUser((LPWSTR)lpAccountUser);
    m_pAdminConnection->SendRequest(authRequest, pResponseXml.getref());
}

void UserAuth()
{
    Zimbra::Util::ScopedInterface<IXMLDOMDocument2> pResponseXml;

    m_pConnection = new Zimbra::Rpc::Connection(L"migration", lpServerAddress, nPort, false, 0,
        L"");

    m_pConnection->SetCurrentUser((LPWSTR)lpAccountUser);

    Zimbra::Rpc::AuthRequest authRequest(lpAccountUser, lpAccountUserPwd, lpServerAddress);

    m_pConnection->SendRequest(authRequest, pResponseXml.getref());

    Zimbra::Util::ScopedPtr<Zimbra::Rpc::Response> pResponse(
    Zimbra::Rpc::Response::Manager::NewResponse(pResponseXml.get()));
}

BOOL FileUpload(Zimbra::Rpc::ZimbraConnection *z_connection, LPWSTR *ppwszToken)
{
    LOGFN_INTERNAL_NO;

    LPSTR pszTestFile = "E:\\temp\\aa.log";
    LPSTREAM pStreamFile = NULL;
    HRESULT hr = OpenStreamOnFile(MAPIAllocateBuffer, MAPIFreeBuffer, STGM_READ,
        (LPWSTR)pszTestFile, NULL, &pStreamFile);

    if (FAILED(hr))
    {
        LOG_ERROR(_T("failed to OpenStreamOnFile call: %x"), hr);
        return FALSE;
    }

    HANDLE hFile = CreateFile(L"E:\\temp\\aa.log", GENERIC_READ, FILE_SHARE_READ, NULL,
        OPEN_EXISTING, FILE_ATTRIBUTE_NORMAL, NULL);
    DWORD dwFileSize = GetFileSize(hFile, NULL);

    CloseHandle(hFile);
    _tprintf(_T("  File Upload size=%d bytes\n"), dwFileSize);

    BOOL bResult = z_connection->DoFileUpload(pStreamFile, dwFileSize,
        L"/service/upload?fmt=raw", ppwszToken, NULL);

    pStreamFile->Release();

    return bResult;
}

void ZCFileUploadTest()
{
    Zimbra::Rpc::UserSession *session = Zimbra::Rpc::UserSession::CreateInstance(lpProfileName,
        lpAccountUser, lpAccountUserPwd, lpServerAddress, nPort, 0, 0, L"", false);
    Zimbra::Rpc::UserSession::SetProfileName(lpProfileName);
    Zimbra::Rpc::ZimbraConnection *z_pConnection = new Zimbra::Rpc::ZimbraConnection(
        L"migration", lpServerAddress, nPort, false, 0, L"");

    z_pConnection->SetProfileName(lpProfileName);

    LPWSTR pwszToken = NULL;

    FileUpload(z_pConnection, &pwszToken);

    Zimbra::Rpc::SendMsgRequest request(pwszToken);

    // free the token right away
    z_pConnection->FreeBuffer(pwszToken);

    request.SetAuthToken(session->AuthToken());
    if (session->SetNoSession())
        request.SetNoSession();
    Zimbra::Rpc::ScopedRPCResponse pResponse;

    try
    {
        z_pConnection->SendRequest(request, pResponse.getref());
    }
    catch (Zimbra::Rpc::SoapFaultResponse &fault)
    {
        LOG_ERROR(_T("Response is soap error exception (%s)."), fault.ErrorCode());
        return;
    }
}

void CreateExchangeMailBox()
{
    Zimbra::MAPI::ExchangeAdmin *exchadmin = new Zimbra::MAPI::ExchangeAdmin(lpExchangeServer);

    try
    {
        try
        {
            exchadmin->DeleteProfile(Zimbra::MAPI::DEFAULT_ADMIN_PROFILE_NAME);
        }
        catch (Zimbra::MAPI::ExchangeAdminException &ex)
        {
            UNREFERENCED_PARAMETER(ex);
        }
        try
        {
            exchadmin->DeleteExchangeMailBox(Zimbra::MAPI::DEFAULT_ADMIN_MAILBOX_NAME,
                L"Administrator", L"z1mbr4Migration");
        }
        catch (Zimbra::MAPI::ExchangeAdminException &ex)
        {
            UNREFERENCED_PARAMETER(ex);
        }
        exchadmin->CreateExchangeMailBox(Zimbra::MAPI::DEFAULT_ADMIN_MAILBOX_NAME,
            Zimbra::MAPI::DEFAULT_ADMIN_PASSWORD, L"Administrator", L"z1mbr4Migration");
        exchadmin->CreateProfile(Zimbra::MAPI::DEFAULT_ADMIN_PROFILE_NAME,
            Zimbra::MAPI::DEFAULT_ADMIN_MAILBOX_NAME, Zimbra::MAPI::DEFAULT_ADMIN_PASSWORD);
        exchadmin->SetDefaultProfile(Zimbra::MAPI::DEFAULT_ADMIN_PROFILE_NAME);
    }
    catch (Zimbra::MAPI::ExchangeAdminException &ex)
    {
        UNREFERENCED_PARAMETER(ex);
    }
    delete exchadmin;
}

void GetAllProfiles()
{
    Zimbra::MAPI::ExchangeAdmin *exchadmin = new Zimbra::MAPI::ExchangeAdmin(lpExchangeServer);

    vector<string> vProfileList;
    exchadmin->GetAllProfiles(vProfileList);
    vector<string>::iterator itr = vProfileList.begin();
    delete exchadmin;
}

void GetUserDN()
{
    wstring userDN;
    wstring lagcyName;
    Zimbra::MAPI::Util::GetUserDNAndLegacyName(lpExchangeServer, L"Administrator", NULL, userDN,
        lagcyName);
}

void ExchangeMigrationSetupTest()
{
/*	ExchangeMigrationSetup *exchmigsetup = new ExchangeMigrationSetup(lpExchangeServer,
 *              L"Administrator",L"z1mbr4Migration");
 *      exchmigsetup->Setup();
 *
 *      vector<string> vProfileList;
 *      exchmigsetup->GetAllProfiles(vProfileList);
 *      vector<string>::iterator itr= vProfileList.begin();
 *      while(itr != vProfileList.end())
 *      {
 *              printf("%s\n" ,((string)*itr).c_str());
 *              itr++ ;
 *      }
 *
 *      exchmigsetup->Clean();
 *      delete exchmigsetup;
 */
    // If Profile exists, rest are Optional else rest 3 params must be there!!!
    // ExchangeOps::Init("Profile", ExchangeIP[optional], AdminName[Optional], AdminPwd[Optional]);
    LPCWSTR lpwstrStatus = ExchangeOps::GlobalInit(L"Outlook");  // lpExchangeServer,L"Administrator",L"z1mbr4Migration");//(L"10.20.141.161", L"fbs",L"Test7777");//

    if (lpwstrStatus)
        delete[] lpwstrStatus;
    lpwstrStatus = ExchangeOps::GlobalUninit();
    if (lpwstrStatus)
        delete[] lpwstrStatus;
    vector<ObjectPickerData> vUserList;
    lpwstrStatus = ExchangeOps::SelectExchangeUsers(vUserList);
    if (lpwstrStatus)
        delete[] lpwstrStatus;
}

typedef struct
{
    wstring mailboxname;
} migrationThreadParams;

DWORD WINAPI AccountMigrationThread(LPVOID lpParameter)
{
    migrationThreadParams *mtparams = (migrationThreadParams *)lpParameter;

    vector<Folder_Data> vfolderlist;

    Zimbra::MAPI::MAPIAccessAPI *maapi = NULL;

    if (PROFILE_MIGARTION)
    {
        maapi = new Zimbra::MAPI::MAPIAccessAPI(L"");
    }
    else
    {
        // Create class instance with Exchange mailbox to be migrated
        maapi = new Zimbra::MAPI::MAPIAccessAPI(mtparams->mailboxname);
        printf("MAILBOXNAME: %S\n", mtparams->mailboxname.c_str());
    }

    // Init user stores
    LPCWSTR lpStatus = maapi->InitializeUser();

    if (lpStatus)
        return 1;
    // Get all folders
    maapi->GetRootFolderHierarchy(vfolderlist);

    //
    vector<Item_Data> vItemDataList;
    vector<Folder_Data>::iterator it;

    vector<Item_Data>::iterator idItr;
    for (it = vfolderlist.begin(); it != vfolderlist.end(); it++)
    {
        if (!PROFILE_MIGARTION)
            printf("MailboxName: %S ", mtparams->mailboxname.c_str());
        printf("FolderName:  %S ", (*it).name.c_str());
        printf("FolderPath: %S ", (*it).folderpath.c_str());
        printf("ContainerClass: %S ", (*it).containerclass.c_str());
        printf("ItemCount: %d ", (*it).itemcount);
        printf("ZimbraId: %d\n", (*it).zimbraid);
        printf("\n\n");

        SBinary sbin = (*it).sbin;

        maapi->GetFolderItemsList(sbin, vItemDataList);
        for (idItr = vItemDataList.begin(); idItr != vItemDataList.end(); idItr++)
        {
            ContactItemData cd;

            if ((*idItr).lItemType == ZT_MAIL)
            {
				MessageItemData msgdata;

                printf("Got message item:");
                maapi->GetItem((*idItr).sbMessageID, msgdata);
                printf(
                    "Subject: %S Date: %I64X DateString:%S		\
					DeliveryDate: %I64X deliveryDateString: %S		\
					Has Attachments: %d Has HTML:%d Has Text:%d	\
					Is Draft:%d Is Flagged: %d Is Forwarded: %d	\
					IsFromMe:%d IsUnread:%d IsUnsent:%d IsRepliedTo:%d	\
					URLName: %S\n"                                                                                                                                                                                                                                                   ,
                    msgdata.Subject.c_str(), msgdata.Date, msgdata.DateString.c_str(),
                    msgdata.deliveryDate, msgdata.DeliveryDateString.c_str(),
                    msgdata.HasAttachments, msgdata.HasHtml, msgdata.HasText, msgdata.IsDraft,
                    msgdata.IsFlagged, msgdata.IsForwared, msgdata.IsFromMe, msgdata.IsUnread,
                    msgdata.IsUnsent, msgdata.RepliedTo, msgdata.Urlname.c_str());

                printf("MIME FILE PATH: %S\n\n\n\n", msgdata.MimeFile.c_str());
                // Delete the mime file
                // DeleteFile(msgdata.MimeFile.c_str());
            }
            else if ((*idItr).lItemType == ZT_CONTACTS)
            {
                printf("Got contact item:");
                maapi->GetItem((*idItr).sbMessageID, cd);
                printf(
                    "%S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S			\
					%S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S		\
					%S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S %S\n "                                                                                                            ,
                    cd.Birthday.c_str(), cd.CallbackPhone.c_str(), cd.CarPhone.c_str(),
                    cd.Company.c_str(), cd.Email1.c_str(), cd.Email2.c_str(), cd.Email3.c_str(),
                    cd.FileAs.c_str(), cd.FirstName.c_str(), cd.HomeCity.c_str(),
                    cd.HomeCountry.c_str(), cd.HomeFax.c_str(), cd.HomePhone.c_str(),
                    cd.HomePhone2.c_str(), cd.HomePostalCode.c_str(), cd.HomeState.c_str(),
                    cd.HomeStreet.c_str(), cd.HomeURL.c_str(), cd.IMAddress1.c_str(),
                    cd.JobTitle.c_str(), cd.LastName.c_str(), cd.MiddleName.c_str(),
                    cd.MobilePhone.c_str(), cd.NamePrefix.c_str(), cd.NameSuffix.c_str(),
                    cd.NickName.c_str(), cd.Notes.c_str(), cd.OtherCity.c_str(),
                    cd.OtherCountry.c_str(), cd.OtherFax.c_str(), cd.OtherPhone.c_str(),
                    cd.OtherPostalCode.c_str(), cd.OtherState.c_str(), cd.OtherStreet.c_str(),
                    cd.OtherURL.c_str(), cd.Pager.c_str(), cd.pDList.c_str(),
                    cd.PictureID.c_str(), cd.Type.c_str(), cd.UserField1.c_str(),
                    cd.UserField2.c_str(), cd.UserField3.c_str(), cd.UserField4.c_str(),
                    cd.WorkCity.c_str(), cd.WorkCountry.c_str(), cd.WorkFax.c_str(),
                    cd.WorkPhone.c_str(), cd.WorkPostalCode.c_str(), cd.WorkState.c_str(),
					cd.WorkStreet.c_str(), cd.WorkURL.c_str(), cd.Anniversary.c_str(), cd.pDList.c_str());
				if(cd.UserDefinedFields.size())
				{
					printf("User Defined Field:\n");
					vector<ContactUDFields>::iterator it;
					for (it= cd.UserDefinedFields.begin();it != cd.UserDefinedFields.end();it++)
					{
						printf("%S : %S \n", it->Name.c_str(), it->value.c_str());
					}
				}
				
                printf("Contact Image Path: %S \n", cd.ContactImagePath.c_str());
            }
            else
            {
                printf("PSTMIG: %d Skipping it...\n", (*idItr).lItemType);
            }
            FreeEntryID((*idItr).sbMessageID);
        }
		FreeEntryID(sbin);
        vItemDataList.clear();
    }
    delete maapi;
    return 0;
}

void MAPIAccessAPITestV()
{
    if (PROFILE_MIGARTION)
    {
        // Outlook profile to be migrated
        MAPIAccessAPI::InitGlobalSessionAndStore(L"user1");        // (L"E:\\temp\\PST\\OriginalEmailCalendar.pst");//
        HANDLE hThread = ::CreateThread(NULL, 0, AccountMigrationThread, NULL, 0L, NULL);

        // wait till all finished
        WaitForSingleObject(hThread, INFINITE);
        // close handle
        CloseHandle(hThread);
    }
    else
    {
        // Create Session and Open admin store.
        MAPIAccessAPI::InitGlobalSessionAndStore(L"Admin@Exch2k7");
        DWORD const MAX_THREADS = 1;
        HANDLE hThreadArray[MAX_THREADS] = { 0 };
        migrationThreadParams mtparams[MAX_THREADS];

        mtparams[0].mailboxname = L"av1";

        /*	mtparams[1].mailboxname = L"av9 av9";
         *      mtparams[2].mailboxname = L"av1";
         *      mtparams[3].mailboxname = L"av2 av2";
         *      mtparams[4].mailboxname = L"av3 av3";
         *      mtparams[5].mailboxname = L"av4";
         *      mtparams[6].mailboxname = L"av5";
         *      mtparams[7].mailboxname = L"av7 av7";
         *      mtparams[8].mailboxname = L"appt1";
         */
        // One thread per mailbox.
        for (int i = 0; i < MAX_THREADS; i++)
        {
            hThreadArray[i] = ::CreateThread(NULL, 0, AccountMigrationThread, &mtparams[i], 0L,
                NULL);
        }
        // wait till all finished
        WaitForMultipleObjects(MAX_THREADS, hThreadArray, TRUE, INFINITE);
        // close handles
        for (int i = 0; i < MAX_THREADS; i++)
        {
            CloseHandle(hThreadArray[i]);
        }
    }

    // destroy session and admin store.
    MAPIAccessAPI::UnInitGlobalSessionAndStore();
}

void GetDomainName()
{
    DWORD dwLevel = 102;
    LPWKSTA_INFO_102 pBuf = NULL;
    NET_API_STATUS nStatus;
    LPWSTR pszServerName = NULL;

    nStatus = NetWkstaGetInfo(pszServerName, dwLevel, (LPBYTE *)&pBuf);
    //
    // If the call is successful,
    // print the workstation data.
    //
    if (nStatus == NERR_Success)
    {
        printf("\n\tPlatform: %d\n", pBuf->wki102_platform_id);
        wprintf(L"\tName:     %s\n", pBuf->wki102_computername);
        printf("\tVersion:  %d.%d\n", pBuf->wki102_ver_major, pBuf->wki102_ver_minor);
        wprintf(L"\tDomain:   %s\n", pBuf->wki102_langroup);
        wprintf(L"\tLan Root: %s\n", pBuf->wki102_lanroot);
        wprintf(L"\t# Logged On Users: %d\n", pBuf->wki102_logged_on_users);
    }
    //
    // Otherwise, indicate the system error.
    //
    else
    {
        fprintf(stderr, "A system error has occurred: %d\n", nStatus);
    }
    //
    // Free the allocated memory.
    //
    if (pBuf != NULL)
        NetApiBufferFree(pBuf);
}

int main(int argc, TCHAR *argv[])
{
    UNREFERENCED_PARAMETER(argc);
    UNREFERENCED_PARAMETER(argv);

// AdminAuth();
// UserAuth();
// ZCFileUploadTest();
// CreateExchangeMailBox();
// GetAllProfiles();
// GetDomainName();
    MAPIAccessAPITestV();
// Zimbra::MAPI::Util::ReverseDelimitedString(L"lb1/tv2/cr3/Inbox/TopFolder",L"/");
// ExchangeMigrationSetupTest();
// CreateExchangeMailBox();

    return 0;
}
