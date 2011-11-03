#include "common.h"
#include "Exchange.h"
#include "MAPIMessage.h"
#include "MAPIContacts.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIMessageException
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIContactException::MAPIContactException(HRESULT hrErrCode, LPCWSTR
    lpszDescription): GenericException(hrErrCode, lpszDescription)
{
    //
}

MAPIContactException::MAPIContactException(HRESULT hrErrCode, LPCWSTR lpszDescription, int
    nLine, LPCSTR strFile): GenericException(hrErrCode, lpszDescription, nLine, strFile)
{
    //
}

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIContact
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIContact::MAPIContact(Zimbra::MAPI::MAPISession &session,
    Zimbra::MAPI::MAPIMessage &mMessage)
{
    m_bPersonalDL = false;
    m_session = &session;
    m_mapiMessage = &mMessage;
    m_pMessage = m_mapiMessage->InternalMessageObject();
    m_pPropVals = NULL;

    pr_mail1address = 0, pr_mail1entryid = 0, pr_mail1type = 0, pr_mail1dispname = 0,
    pr_mail2address = 0, pr_mail2entryid = 0, pr_mail2type = 0, pr_mail2dispname = 0,
    pr_mail3address = 0, pr_mail3entryid = 0, pr_mail3type = 0, pr_mail3dispname = 0,
    pr_fileas = 0, pr_fileasID = 0, pr_business_address_city = 0,
    pr_business_address_country = 0, pr_business_address_postal_code = 0,
    pr_business_address_state = 0, pr_business_address_street = 0, pr_contact_user1_idx = 0,
    pr_contact_user2_idx = 0, pr_contact_user3_idx = 0, pr_contact_user4_idx = 0,
    pr_contact_oneoffmemebrs = 0, pr_imaddress = 0;
    pr_anniversary = 0;

    // init named props
    nameIds[0] = 0x8083;
    nameIds[1] = 0x8085;
    nameIds[2] = 0x8082;
    nameIds[3] = 0x8084;
    nameIds[4] = 0x8093;
    nameIds[5] = 0x8095;
    nameIds[6] = 0x8092;
    nameIds[7] = 0x8094;
    nameIds[8] = 0x80A3;
    nameIds[9] = 0x80A5;
    nameIds[10] = 0x80A2;
    nameIds[11] = 0x80A4;
    nameIds[12] = 0x8005;
    nameIds[13] = 0x8006;
    nameIds[14] = 0x8046;
    nameIds[15] = 0x8049;
    nameIds[16] = 0x8048;
    nameIds[17] = 0x8047;
    nameIds[18] = 0x8045;
    nameIds[19] = 0x804f;
    nameIds[20] = 0x8050;
    nameIds[21] = 0x8051;
    nameIds[22] = 0x8052;
    nameIds[23] = 0x8054;
    nameIds[24] = 0x8062;

    m_pCallbackPhone = L"";
    m_pCarPhone = L"";
    m_pCompany = L"";
    m_pEmail = L"";
    m_pEmail2 = L"";
    m_pEmail3 = L"";
    m_pFileAs = L"";
    m_pFirstName = L"";
    m_pHomeCity = L"";
    m_pHomeCountry = L"";
    m_pHomeFax = L"";
    m_pHomePhone = L"";
    m_pHomePhone2 = L"";
    m_pHomePostalCode = L"";
    m_pHomeState = L"";
    m_pHomeStreet = L"";
    m_pHomeURL = L"";
    m_pJobTitle = L"";
    m_pLastName = L"";
    m_pMiddleName = L"";
    m_pMobilePhone = L"";
    m_pNamePrefix = L"";
    m_pNameSuffix = L"";
    m_pNotes = L"";
    m_pOtherCity = L"";
    m_pOtherCountry = L"";
    m_pOtherFax = L"";
    m_pOtherPhone = L"";
    m_pOtherPostalCode = L"";
    m_pOtherState = L"";
    m_pOtherStreet = L"";
    m_pOtherURL = L"";
    m_pPager = L"";
    m_pWorkCity = L"";
    m_pWorkCountry = L"";
    m_pWorkFax = L"";
    m_pWorkPhone = L"";
    m_pWorkPostalCode = L"";
    m_pWorkState = L"";
    m_pWorkStreet = L"";
    m_pWorkURL = L"";
    m_pBirthday = L"";
    m_pUserField1 = L"";
    m_pUserField2 = L"";
    m_pUserField3 = L"";
    m_pUserField4 = L"";
    m_pNickName = L"";
    m_pDList = L"";
    m_pType = L"";
    m_pPictureID = L"";
    m_size = 0;
    m_pIMAddress1 = L"";
    m_anniversary = L"";
    Init();
}

MAPIContact::~MAPIContact()
{
    if (m_pPropVals)
        MAPIFreeBuffer(m_pPropVals);
    m_pPropVals = NULL;
}

HRESULT MAPIContact::Init()
{
    HRESULT hr = S_OK;
    Zimbra::Util::ScopedBuffer<SPropValue> pPropValMsgClass;

    if (FAILED(hr = HrGetOneProp(m_pMessage, PR_MESSAGE_CLASS, pPropValMsgClass.getptr())))
        throw MAPIContactException(hr, L"Init(): HrGetOneProp Failed.", __LINE__, __FILE__);
    if ((pPropValMsgClass->ulPropTag == PR_MESSAGE_CLASS_W) && (_tcsicmp(
        pPropValMsgClass->Value.LPSZ, L"ipm.distlist") == 0))
        m_bPersonalDL = true;

    // initialize the MAPINAMEID structure GetIDsFromNames requires
    LPMAPINAMEID ppNames[N_NUM_NAMES] = { 0 };

    for (int i = 0; i < N_NUM_NAMES; i++)
    {
        MAPIAllocateBuffer(sizeof (MAPINAMEID), (LPVOID *)&(ppNames[i]));
        ppNames[i]->ulKind = MNID_ID;
        ppNames[i]->lpguid = (LPGUID)(&PS_CONTACT_PROPERTIES);
        ppNames[i]->Kind.lID = nameIds[i];
    }

    // get the real prop tag ID's
    LPSPropTagArray pContactTags = NULL;

    if (FAILED(hr = m_pMessage->GetIDsFromNames(N_NUM_NAMES, ppNames, MAPI_CREATE,
            &pContactTags)))
        throw MAPIContactException(hr, L"Init(): GetIDsFromNames Failed.", __LINE__, __FILE__);
    // give the prop tag ID's a type
    pr_mail1address = SetPropType(pContactTags->aulPropTag[N_MAIL1], PT_TSTRING);
    pr_mail1entryid = SetPropType(pContactTags->aulPropTag[N_MAIL1EID], PT_BINARY);
    pr_mail1type = SetPropType(pContactTags->aulPropTag[N_MAIL1TYPE], PT_TSTRING);
    pr_mail1dispname = SetPropType(pContactTags->aulPropTag[N_MAIL1DISPNAME], PT_TSTRING);
    pr_mail2address = SetPropType(pContactTags->aulPropTag[N_MAIL2], PT_TSTRING);
    pr_mail2entryid = SetPropType(pContactTags->aulPropTag[N_MAIL2EID], PT_BINARY);
    pr_mail2type = SetPropType(pContactTags->aulPropTag[N_MAIL2TYPE], PT_TSTRING);
    pr_mail2dispname = SetPropType(pContactTags->aulPropTag[N_MAIL2DISPNAME], PT_TSTRING);
    pr_mail3address = SetPropType(pContactTags->aulPropTag[N_MAIL3], PT_TSTRING);
    pr_mail3entryid = SetPropType(pContactTags->aulPropTag[N_MAIL3EID], PT_BINARY);
    pr_mail3type = SetPropType(pContactTags->aulPropTag[N_MAIL3TYPE], PT_TSTRING);
    pr_mail3dispname = SetPropType(pContactTags->aulPropTag[N_MAIL3DISPNAME], PT_TSTRING);
    pr_fileas = SetPropType(pContactTags->aulPropTag[N_FILEAS], PT_TSTRING);
    pr_fileasID = SetPropType(pContactTags->aulPropTag[N_FILEAS_ID], PT_LONG);
    pr_business_address_city = SetPropType(pContactTags->aulPropTag[N_BUS_CITY], PT_TSTRING);
    pr_business_address_country = SetPropType(pContactTags->aulPropTag[N_BUS_COUNTRY],
        PT_TSTRING);
    pr_business_address_postal_code = SetPropType(pContactTags->aulPropTag[N_BUS_ZIP],
        PT_TSTRING);
    pr_business_address_state = SetPropType(pContactTags->aulPropTag[N_BUS_STATE], PT_TSTRING);
    pr_business_address_street = SetPropType(pContactTags->aulPropTag[N_BUS_STREET],
        PT_TSTRING);
    pr_contact_user1_idx = SetPropType(pContactTags->aulPropTag[N_CONTACT_USER1_IDX],
        PT_TSTRING);
    pr_contact_user2_idx = SetPropType(pContactTags->aulPropTag[N_CONTACT_USER2_IDX],
        PT_TSTRING);
    pr_contact_user3_idx = SetPropType(pContactTags->aulPropTag[N_CONTACT_USER3_IDX],
        PT_TSTRING);
    pr_contact_user4_idx = SetPropType(pContactTags->aulPropTag[N_CONTACT_USER4_IDX],
        PT_TSTRING);
    pr_contact_oneoffmemebrs = SetPropType(
        pContactTags->aulPropTag[N_CONTACT_ONEOFFMEMEBRS_IDX], PT_MV_BINARY);
    pr_imaddress = SetPropType(pContactTags->aulPropTag[N_IMADDRESS], PT_TSTRING);

    pr_anniversary = SetPropType(pContactTags->aulPropTag[N_ANNIVERSARY], PT_TSTRING);
    // free the memory we allocated on the head
    for (int i = 0; i < N_NUM_NAMES; i++)
        MAPIFreeBuffer(ppNames[i]);
    MAPIFreeBuffer(pContactTags);

    // these are the contact properties we need to get
    SizedSPropTagArray(C_NUM_PROPS, contactProps) = {
        C_NUM_PROPS, {
            PR_CALLBACK_TELEPHONE_NUMBER, PR_CAR_TELEPHONE_NUMBER, PR_COMPANY_NAME,
            pr_mail1address, pr_mail1entryid, pr_mail1type, pr_mail1dispname,
            pr_mail2address, pr_mail2entryid, pr_mail2type, pr_mail2dispname,
            pr_mail3address, pr_mail3entryid, pr_mail3type, pr_mail3dispname, pr_fileas,
            pr_fileasID, PR_GIVEN_NAME, PR_HOME_ADDRESS_CITY, PR_HOME_ADDRESS_COUNTRY,
            PR_HOME_FAX_NUMBER, PR_HOME_TELEPHONE_NUMBER, PR_HOME2_TELEPHONE_NUMBER,
            PR_HOME_ADDRESS_POSTAL_CODE, PR_HOME_ADDRESS_STATE_OR_PROVINCE,
            PR_HOME_ADDRESS_STREET, PR_TITLE, PR_SURNAME, PR_MIDDLE_NAME,
            PR_CELLULAR_TELEPHONE_NUMBER, PR_DISPLAY_NAME_PREFIX, PR_GENERATION,
            // notes is PR_BODY and PR_BODY_HTML
            PR_OTHER_ADDRESS_CITY, PR_OTHER_ADDRESS_COUNTRY, PR_PRIMARY_FAX_NUMBER,     // other fax
            PR_OTHER_TELEPHONE_NUMBER, PR_OTHER_ADDRESS_POSTAL_CODE,
            PR_OTHER_ADDRESS_STATE_OR_PROVINCE, PR_OTHER_ADDRESS_STREET,
            PR_PAGER_TELEPHONE_NUMBER, pr_business_address_city,
            pr_business_address_country, PR_BUSINESS_FAX_NUMBER, PR_OFFICE_TELEPHONE_NUMBER,
            pr_business_address_postal_code, pr_business_address_state,
            pr_business_address_street, PR_BUSINESS_HOME_PAGE, PR_BIRTHDAY,
            pr_contact_user1_idx, pr_contact_user2_idx, pr_contact_user3_idx,
            pr_contact_user4_idx, pr_contact_oneoffmemebrs, pr_imaddress,
            PR_WEDDING_ANNIVERSARY
        }
    };

    ULONG cVals = 0;

    if (FAILED(hr = m_pMessage->GetProps((LPSPropTagArray) & contactProps, fMapiUnicode, &cVals,
            &m_pPropVals)))
        throw MAPIContactException(hr, L"Init(): GetProps Failed.", __LINE__, __FILE__);

    // see if there is a file-as id
    LONG zimbraFileAsId = 0;

    if (m_bPersonalDL)                          // PDL's always have a custom file-as
    {
        zimbraFileAsId = 8;
        Type(L"group");
    }
    else if (m_pPropVals[C_FILEASID].ulPropTag == contactProps.aulPropTag[C_FILEASID])
    {
        switch (m_pPropVals[C_FILEASID].Value.l)
        {
        case OFA_LAST_C_FIRST:
            zimbraFileAsId = 1;
            break;
        case OFA_FIRST_LAST:
            zimbraFileAsId = 2;
            break;
        case OFA_COMPANY:
            zimbraFileAsId = 3;
            break;
        case OFA_LAST_C_FIRST_COMPANY:
            zimbraFileAsId = 4;
            break;
        case OFA_COMPANY_LAST_C_FIRST:
            zimbraFileAsId = 6;
            break;
        case OFA_CUSTOM:
            zimbraFileAsId = 8;
            break;
        }
    }
    // process all "String" properties
    if (m_pPropVals[C_CALLBACK_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_CALLBACK_TELEPHONE_NUMBER])
        CallbackPhone(m_pPropVals[C_CALLBACK_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_CAR_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_CAR_TELEPHONE_NUMBER])
        CarPhone(m_pPropVals[C_CAR_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_COMPANY_NAME].ulPropTag == contactProps.aulPropTag[C_COMPANY_NAME])
        Company(m_pPropVals[C_COMPANY_NAME].Value.lpszW);
    if (m_pPropVals[C_FILEAS].ulPropTag == contactProps.aulPropTag[C_FILEAS])
    {
        if (zimbraFileAsId == 8)
        {
            LPWSTR pwszFileAsValue = m_pPropVals[C_FILEAS].Value.lpszW;

            if ((pwszFileAsValue != NULL) && (wcsicmp(pwszFileAsValue, L"") != 0))
            {
                LPWSTR pwszTemp = new WCHAR[wcslen(m_pPropVals[C_FILEAS].Value.lpszW) + 3];

                // there is a legit string for the custom fileas value
                wsprintf(pwszTemp, L"8:%s", pwszFileAsValue);
                FileAs(pwszTemp);
                // PDL's require a nickname
                if (m_bPersonalDL)
                    NickName(m_pPropVals[C_FILEAS].Value.lpszW);
                delete[] pwszTemp;
            }
            else
            {
                LPWSTR pwszNONAME = new WCHAR[wcslen(L"NO_NAME") + 1];

                wsprintf(pwszNONAME, L"%s", L"NO_NAME");

                LPWSTR pwszTemp = new WCHAR[wcslen(pwszNONAME) + 3];

                // there is a legit string for the custom fileas value
                wsprintf(pwszTemp, L"8:%s", pwszNONAME);
                FileAs(pwszTemp);
                // PDL's require a nickname
                if (m_bPersonalDL)
                    NickName(pwszNONAME);
                delete[] pwszTemp;
                delete[] pwszNONAME;
            }
        }
        else if (zimbraFileAsId)
        {
            WCHAR pwszTemp[3];

            _ltow(zimbraFileAsId, pwszTemp, 10);
            FileAs(pwszTemp);
        }
    }
    if (m_pPropVals[C_GIVEN_NAME].ulPropTag == contactProps.aulPropTag[C_GIVEN_NAME])
        FirstName(m_pPropVals[C_GIVEN_NAME].Value.lpszW);
    if (m_pPropVals[C_HOME_ADDRESS_CITY].ulPropTag ==
        contactProps.aulPropTag[C_HOME_ADDRESS_CITY])
        HomeCity(m_pPropVals[C_HOME_ADDRESS_CITY].Value.lpszW);
    if (m_pPropVals[C_HOME_ADDRESS_COUNTRY].ulPropTag ==
        contactProps.aulPropTag[C_HOME_ADDRESS_COUNTRY])
        HomeCountry(m_pPropVals[C_HOME_ADDRESS_COUNTRY].Value.lpszW);
    if (m_pPropVals[C_HOME_FAX_NUMBER].ulPropTag == contactProps.aulPropTag[C_HOME_FAX_NUMBER])
        HomeFax(m_pPropVals[C_HOME_FAX_NUMBER].Value.lpszW);
    if (m_pPropVals[C_HOME_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_HOME_TELEPHONE_NUMBER])
        HomePhone(m_pPropVals[C_HOME_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_HOME2_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_HOME2_TELEPHONE_NUMBER])
        HomePhone2(m_pPropVals[C_HOME2_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_HOME_ADDRESS_POSTAL_CODE].ulPropTag ==
        contactProps.aulPropTag[C_HOME_ADDRESS_POSTAL_CODE])
        HomePostalCode(m_pPropVals[C_HOME_ADDRESS_POSTAL_CODE].Value.lpszW);
    if (m_pPropVals[C_HOME_ADDRESS_STATE_OR_PROVINCE].ulPropTag ==
        contactProps.aulPropTag[C_HOME_ADDRESS_STATE_OR_PROVINCE])
        HomeState(m_pPropVals[C_HOME_ADDRESS_STATE_OR_PROVINCE].Value.lpszW);
    if (m_pPropVals[C_HOME_ADDRESS_STREET].ulPropTag ==
        contactProps.aulPropTag[C_HOME_ADDRESS_STREET])
        HomeStreet(m_pPropVals[C_HOME_ADDRESS_STREET].Value.lpszW);
    if (m_pPropVals[C_TITLE].ulPropTag == contactProps.aulPropTag[C_TITLE])
        JobTitle(m_pPropVals[C_TITLE].Value.lpszW);
    if (m_pPropVals[C_SURNAME].ulPropTag == contactProps.aulPropTag[C_SURNAME])
        LastName(m_pPropVals[C_SURNAME].Value.lpszW);
    if (m_pPropVals[C_MIDDLE_NAME].ulPropTag == contactProps.aulPropTag[C_MIDDLE_NAME])
        MiddleName(m_pPropVals[C_MIDDLE_NAME].Value.lpszW);
    if (m_pPropVals[C_CELLULAR_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_CELLULAR_TELEPHONE_NUMBER])
        MobilePhone(m_pPropVals[C_CELLULAR_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_DISPLAY_NAME_PREFIX].ulPropTag ==
        contactProps.aulPropTag[C_DISPLAY_NAME_PREFIX])
        NamePrefix(m_pPropVals[C_DISPLAY_NAME_PREFIX].Value.lpszW);
    if (m_pPropVals[C_GENERATION].ulPropTag == contactProps.aulPropTag[C_GENERATION])
        NameSuffix(m_pPropVals[C_GENERATION].Value.lpszW);
    if (m_pPropVals[C_OTHER_ADDRESS_CITY].ulPropTag ==
        contactProps.aulPropTag[C_OTHER_ADDRESS_CITY])
        OtherCity(m_pPropVals[C_OTHER_ADDRESS_CITY].Value.lpszW);
    if (m_pPropVals[C_OTHER_ADDRESS_COUNTRY].ulPropTag ==
        contactProps.aulPropTag[C_OTHER_ADDRESS_COUNTRY])
        OtherCountry(m_pPropVals[C_OTHER_ADDRESS_COUNTRY].Value.lpszW);
    if (m_pPropVals[C_PRIMARY_FAX_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_PRIMARY_FAX_NUMBER])
        OtherFax(m_pPropVals[C_PRIMARY_FAX_NUMBER].Value.lpszW);
    if (m_pPropVals[C_OTHER_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_OTHER_TELEPHONE_NUMBER])
        OtherPhone(m_pPropVals[C_OTHER_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_OTHER_ADDRESS_POSTAL_CODE].ulPropTag ==
        contactProps.aulPropTag[C_OTHER_ADDRESS_POSTAL_CODE])
        OtherPostalCode(m_pPropVals[C_OTHER_ADDRESS_POSTAL_CODE].Value.lpszW);
    if (m_pPropVals[C_OTHER_ADDRESS_STATE_OR_PROVINCE].ulPropTag ==
        contactProps.aulPropTag[C_OTHER_ADDRESS_STATE_OR_PROVINCE])
        OtherState(m_pPropVals[C_OTHER_ADDRESS_STATE_OR_PROVINCE].Value.lpszW);
    if (m_pPropVals[C_OTHER_ADDRESS_STREET].ulPropTag ==
        contactProps.aulPropTag[C_OTHER_ADDRESS_STREET])
        OtherStreet(m_pPropVals[C_OTHER_ADDRESS_STREET].Value.lpszW);
    if (m_pPropVals[C_PAGER_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_PAGER_TELEPHONE_NUMBER])
        Pager(m_pPropVals[C_PAGER_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_BUSINESS_ADDRESS_CITY].ulPropTag ==
        contactProps.aulPropTag[C_BUSINESS_ADDRESS_CITY])
        WorkCity(m_pPropVals[C_BUSINESS_ADDRESS_CITY].Value.lpszW);
    if (m_pPropVals[C_BUSINESS_ADDRESS_COUNTRY].ulPropTag ==
        contactProps.aulPropTag[C_BUSINESS_ADDRESS_COUNTRY])
        WorkCountry(m_pPropVals[C_BUSINESS_ADDRESS_COUNTRY].Value.lpszW);
    if (m_pPropVals[C_BUSINESS_FAX_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_BUSINESS_FAX_NUMBER])
        WorkFax(m_pPropVals[C_BUSINESS_FAX_NUMBER].Value.lpszW);
    if (m_pPropVals[C_OFFICE_TELEPHONE_NUMBER].ulPropTag ==
        contactProps.aulPropTag[C_OFFICE_TELEPHONE_NUMBER])
        WorkPhone(m_pPropVals[C_OFFICE_TELEPHONE_NUMBER].Value.lpszW);
    if (m_pPropVals[C_BUSINESS_ADDRESS_POSTAL_CODE].ulPropTag ==
        contactProps.aulPropTag[C_BUSINESS_ADDRESS_POSTAL_CODE])
        WorkPostalCode(m_pPropVals[C_BUSINESS_ADDRESS_POSTAL_CODE].Value.lpszW);
    if (m_pPropVals[C_BUSINESS_ADDRESS_STATE].ulPropTag ==
        contactProps.aulPropTag[C_BUSINESS_ADDRESS_STATE])
        WorkState(m_pPropVals[C_BUSINESS_ADDRESS_STATE].Value.lpszW);
    if (m_pPropVals[C_BUSINESS_ADDRESS_STREET].ulPropTag ==
        contactProps.aulPropTag[C_BUSINESS_ADDRESS_STREET])
        WorkStreet(m_pPropVals[C_BUSINESS_ADDRESS_STREET].Value.lpszW);
    if (m_pPropVals[C_BUSINESS_HOME_PAGE].ulPropTag ==
        contactProps.aulPropTag[C_BUSINESS_HOME_PAGE])
        WorkURL(m_pPropVals[C_BUSINESS_HOME_PAGE].Value.lpszW);
    if (m_pPropVals[C_CONTACT_USER1_IDX].ulPropTag ==
        contactProps.aulPropTag[C_CONTACT_USER1_IDX])
        UserField1(m_pPropVals[C_CONTACT_USER1_IDX].Value.lpszW);
    if (m_pPropVals[C_CONTACT_USER2_IDX].ulPropTag ==
        contactProps.aulPropTag[C_CONTACT_USER2_IDX])
        UserField2(m_pPropVals[C_CONTACT_USER2_IDX].Value.lpszW);
    if (m_pPropVals[C_CONTACT_USER3_IDX].ulPropTag ==
        contactProps.aulPropTag[C_CONTACT_USER3_IDX])
        UserField3(m_pPropVals[C_CONTACT_USER3_IDX].Value.lpszW);
    if (m_pPropVals[C_CONTACT_USER4_IDX].ulPropTag ==
        contactProps.aulPropTag[C_CONTACT_USER4_IDX])
        UserField4(m_pPropVals[C_CONTACT_USER4_IDX].Value.lpszW);
    if (m_pPropVals[C_BIRTHDAY].ulPropTag == contactProps.aulPropTag[C_BIRTHDAY])
    {
        SYSTEMTIME st = { 0 };

        FileTimeToSystemTime(&(m_pPropVals[C_BIRTHDAY].Value.ft), &st);

        // We get PR_BIRTHDAY in UTC so let's convert it into local time
        TIME_ZONE_INFORMATION tzInfo = { 0 };

        GetTimeZoneInformation(&tzInfo);
        SystemTimeToTzSpecificLocalTime(&tzInfo, &st, &st);

        TCHAR pszBDay[11];

        swprintf(pszBDay, 11, _T("%4d-%02d-%02d"), st.wYear, st.wMonth, st.wDay);

        Birthday(pszBDay);
    }
    if (m_pPropVals[C_ANNIVERSARY].ulPropTag == contactProps.aulPropTag[C_ANNIVERSARY])
    {
        SYSTEMTIME st = { 0 };

        FileTimeToSystemTime(&(m_pPropVals[C_ANNIVERSARY].Value.ft), &st);

        // We get PR_ANNIVERSARY in UTC so let's convert it into local time
        TIME_ZONE_INFORMATION tzInfo = { 0 };

        GetTimeZoneInformation(&tzInfo);
        SystemTimeToTzSpecificLocalTime(&tzInfo, &st, &st);

        TCHAR pszAnniv[11];

        swprintf(pszAnniv, 11, _T("%4d-%02d-%02d"), st.wYear, st.wMonth, st.wDay);

        Anniversary(pszAnniv);
    }

    // email 1
    RECIP_INFO tempRecip;

    if ((m_pPropVals[C_MAIL1TYPE].ulPropTag == contactProps.aulPropTag[C_MAIL1TYPE]) &&
        (m_pPropVals[C_MAIL1ADDRESS].ulPropTag == contactProps.aulPropTag[C_MAIL1ADDRESS]))
    {
        if (wcscmp(m_pPropVals[C_MAIL1TYPE].Value.lpszW, L"SMTP") == 0)
        {
            Email(m_pPropVals[C_MAIL1ADDRESS].Value.lpszW);
        }
        else if (wcscmp(m_pPropVals[C_MAIL1TYPE].Value.lpszW, L"EX") == 0)
        {
            tempRecip.pAddrType = m_pPropVals[C_MAIL1TYPE].Value.lpszW;
            tempRecip.pEmailAddr = m_pPropVals[C_MAIL1ADDRESS].Value.lpszW;
            tempRecip.cbEid = m_pPropVals[C_MAIL1EID].Value.bin.cb;
            tempRecip.pEid = (LPENTRYID)(m_pPropVals[C_MAIL1EID].Value.bin.lpb);

            wstring strSenderEmail(_TEXT(""));
            HRESULT hr = Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(*m_session, tempRecip,
                strSenderEmail);

            if (hr != S_OK)
                Email(m_pPropVals[C_MAIL1DISPNAME].Value.lpszW);

            else
                Email((LPTSTR)strSenderEmail.c_str());
        }
    }
    // email 2
    if ((m_pPropVals[C_MAIL2TYPE].ulPropTag == contactProps.aulPropTag[C_MAIL2TYPE]) &&
        (m_pPropVals[C_MAIL2ADDRESS].ulPropTag == contactProps.aulPropTag[C_MAIL2ADDRESS]))
    {
        if (wcscmp(m_pPropVals[C_MAIL2TYPE].Value.lpszW, L"SMTP") == 0)
        {
            Email2(m_pPropVals[C_MAIL2ADDRESS].Value.lpszW);
        }
        else if (wcscmp(m_pPropVals[C_MAIL2TYPE].Value.lpszW, L"EX") == 0)
        {
            tempRecip.pAddrType = m_pPropVals[C_MAIL2TYPE].Value.lpszW;
            tempRecip.pEmailAddr = m_pPropVals[C_MAIL2ADDRESS].Value.lpszW;
            tempRecip.cbEid = m_pPropVals[C_MAIL2EID].Value.bin.cb;
            tempRecip.pEid = (LPENTRYID)(m_pPropVals[C_MAIL2EID].Value.bin.lpb);

            wstring strSenderEmail(_TEXT(""));
            HRESULT hr = Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(*m_session, tempRecip,
                strSenderEmail);

            if (hr != S_OK)
                Email(m_pPropVals[C_MAIL2DISPNAME].Value.lpszW);

            else
                Email2((LPTSTR)strSenderEmail.c_str());
        }
    }
    // email 3
    if ((m_pPropVals[C_MAIL3TYPE].ulPropTag == contactProps.aulPropTag[C_MAIL3TYPE]) &&
        (m_pPropVals[C_MAIL3ADDRESS].ulPropTag == contactProps.aulPropTag[C_MAIL3ADDRESS]))
    {
        if (wcscmp(m_pPropVals[C_MAIL3TYPE].Value.lpszW, L"SMTP") == 0)
        {
            Email3(m_pPropVals[C_MAIL3ADDRESS].Value.lpszW);
        }
        else if (wcscmp(m_pPropVals[C_MAIL3TYPE].Value.lpszW, L"EX") == 0)
        {
            tempRecip.pAddrType = m_pPropVals[C_MAIL3TYPE].Value.lpszW;
            tempRecip.pEmailAddr = m_pPropVals[C_MAIL3ADDRESS].Value.lpszW;
            tempRecip.cbEid = m_pPropVals[C_MAIL3EID].Value.bin.cb;
            tempRecip.pEid = (LPENTRYID)(m_pPropVals[C_MAIL3EID].Value.bin.lpb);

            wstring strSenderEmail(_TEXT(""));
            HRESULT hr = Zimbra::MAPI::Util::HrMAPIGetSMTPAddress(*m_session, tempRecip,
                strSenderEmail);

            if (hr != S_OK)
                Email(m_pPropVals[C_MAIL3DISPNAME].Value.lpszW);

            else
                Email3((LPTSTR)strSenderEmail.c_str());
        }
    }
    if (m_pPropVals[C_IMADDRESS].ulPropTag == contactProps.aulPropTag[C_IMADDRESS])
        IMAddress1(m_pPropVals[C_IMADDRESS].Value.lpszW);
    // add the 'notes' section
    if (m_mapiMessage->HasTextPart())
    {
        LPTSTR pBody = NULL;
        UINT nText = 0;

        m_mapiMessage->TextBody(&pBody, nText);

        LPTSTR psz = pBody;

        if (psz)
        {
            const char SPACE = ' ', TAB = '\t', CR = 0x0d, LF = 0x0a;

            while (*psz)
            {
                // Replace control characters with space
                // Exclude carriage return and new line characters while doing the same
                if ((*psz < SPACE) && (*psz != CR) && (*psz != LF))
                    *psz = _T(' ');
                psz++;
            }

            // We'll add the body only if it has data other than white spaces and new lines
            bool bHasValidChars = false;

            psz = pBody;
            while (*psz)
            {
                if ((*psz == SPACE) || (*psz == TAB) || (*psz == CR) || (*psz == LF))
                {
                    psz++;
                    continue;
                }
                else
                {
                    bHasValidChars = true;
                    break;
                }
            }
            if (bHasValidChars)
                Notes(pBody);
            MAPIFreeBuffer(pBody);
        }
    }

    // Save image path
    wstring wstrImagePath;

    if (m_mapiMessage->HasAttach())
    {
        if (!FAILED(hr = GetContactImage(wstrImagePath)))
            ContactImagePath((LPTSTR)wstrImagePath.c_str());
    }
    return S_OK;
}

HRESULT MAPIContact::GetContactImage(wstring &wstrImagePath)
{
    HRESULT hr = S_OK;
    Zimbra::Util::ScopedInterface<IStream> pSrcStream;
    {
        Zimbra::Util::ScopedRowSet pAttachRows;
        Zimbra::Util::ScopedInterface<IMAPITable> pAttachTable;

        SizedSPropTagArray(3, attachProps) = {
            3, { PR_ATTACH_NUM, PR_ATTACH_SIZE, PR_ATTACH_LONG_FILENAME }
        };

        hr = m_pMessage->GetAttachmentTable(MAPI_UNICODE, pAttachTable.getptr());
        if (SUCCEEDED(hr))
        {
            if (FAILED(hr = pAttachTable->SetColumns((LPSPropTagArray) & attachProps, 0)))
                return hr;
            ULONG ulRowCount = 0;
            if (FAILED(hr = pAttachTable->GetRowCount(0, &ulRowCount)))
                return hr;
            if (FAILED(hr = pAttachTable->QueryRows(ulRowCount, 0, pAttachRows.getptr())))
                return hr;
            if (SUCCEEDED(hr))
            {
                hr = MAPI_E_NOT_FOUND;
                for (unsigned int i = 0; i < pAttachRows->cRows; i++)
                {
                    // if property couldn't be found or returns error, skip it
                    if ((pAttachRows->aRow[i].lpProps[2].ulPropTag == PT_ERROR) ||
                        (pAttachRows->aRow[i].lpProps[2].Value.err == MAPI_E_NOT_FOUND))
                        continue;
                    // Discard the attachmetnt if its not contact picture
                    if (_tcscmp(pAttachRows->aRow[i].lpProps[2].Value.LPSZ, _T(
                        "ContactPicture.jpg")))
                        continue;
                    Zimbra::Util::ScopedInterface<IAttach> pAttach;

                    if (FAILED(hr = m_pMessage->OpenAttach(
                            pAttachRows->aRow[i].lpProps[0].Value.l, NULL, 0,
                            pAttach.getptr())))
                        continue;
                    if (FAILED(hr = pAttach->OpenProperty(PR_ATTACH_DATA_BIN, &IID_IStream,
                            STGM_READ, 0, (LPUNKNOWN FAR *)pSrcStream.getptr())))
                        return hr;
                    break;
                }
            }
        }
    }

    if (hr != S_OK)
        return hr;

    // copy image to file
    wstring wstrTempAppDirPath;
    char *lpszDirName = NULL;
    char *lpszUniqueName = NULL;
    Zimbra::Util::ScopedInterface<IStream> pDestStream;

    if (!Zimbra::MAPI::Util::GetAppTemporaryDirectory(wstrTempAppDirPath))
        return MAPI_E_ACCESS_DENIED;
    WtoA((LPWSTR)wstrTempAppDirPath.c_str(), lpszDirName);

    string strFQFileName = lpszDirName;

    WtoA((LPWSTR)Zimbra::MAPI::Util::GetUniqueName().c_str(), lpszUniqueName);
    strFQFileName += "\\ZmContact_";
    strFQFileName += lpszUniqueName;
    strFQFileName += ".jpg";
    SafeDelete(lpszDirName);
    SafeDelete(lpszUniqueName);
    // Open stream on file
    if (FAILED(hr = OpenStreamOnFile(MAPIAllocateBuffer, MAPIFreeBuffer, STGM_CREATE |
            STGM_READWRITE, (LPTSTR)strFQFileName.c_str(), NULL, pDestStream.getptr())))
        return hr;
    ULARGE_INTEGER liAll = { 0 };
    liAll.QuadPart = (ULONGLONG)-1;
    if (FAILED(hr = pSrcStream->CopyTo(pDestStream.get(), liAll, NULL, NULL)))
        return hr;
    if (FAILED(hr = pDestStream->Commit(0)))
    {
        return hr;
        ;
    }

    // mime file path
    LPWSTR lpwstrFQFileName = NULL;

    AtoW((LPSTR)strFQFileName.c_str(), lpwstrFQFileName);
    wstrImagePath = lpwstrFQFileName;
    SafeDelete(lpwstrFQFileName);
    return hr;
}
