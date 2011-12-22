#pragma once

// MAPIContactException class
class MAPIContactException: public GenericException
{
public:
    MAPIContactException(HRESULT hrErrCode, LPCWSTR lpszDescription);
    MAPIContactException(HRESULT hrErrCode, LPCWSTR lpszDescription, int nLine, LPCSTR strFile);
    virtual ~MAPIContactException() {}
};

typedef struct _ContactUDFields
{
	wstring Name;
	wstring value;
}ContactUDFields;

// MAPIContact class
class MAPIContact
{
private:
    // prop tags for named properties
    ULONG pr_mail1address, pr_mail1entryid, pr_mail1type, pr_mail1dispname, pr_mail2address,
        pr_mail2entryid, pr_mail2type, pr_mail2dispname, pr_mail3address, pr_mail3entryid,
        pr_mail3type, pr_mail3dispname, pr_fileas, pr_fileasID, pr_business_address_city,
        pr_business_address_country, pr_business_address_postal_code, pr_business_address_state,
        pr_business_address_street, pr_contact_user1_idx, pr_contact_user2_idx,
        pr_contact_user3_idx, pr_contact_user4_idx, pr_contact_oneoffmemebrs, pr_imaddress,
        pr_anniversary;

    // index of props
    typedef enum _ContactsPropIdx
    {
        N_MAIL1, N_MAIL1EID, N_MAIL1TYPE, N_MAIL1DISPNAME, N_MAIL2, N_MAIL2EID, N_MAIL2TYPE,
        N_MAIL2DISPNAME, N_MAIL3, N_MAIL3EID, N_MAIL3TYPE, N_MAIL3DISPNAME, N_FILEAS,
        N_FILEAS_ID, N_BUS_CITY, N_BUS_COUNTRY, N_BUS_ZIP, N_BUS_STATE, N_BUS_STREET,
        N_CONTACT_USER1_IDX, N_CONTACT_USER2_IDX, N_CONTACT_USER3_IDX, N_CONTACT_USER4_IDX,
        N_CONTACT_ONEOFFMEMEBRS_IDX, N_IMADDRESS, N_ANNIVERSARY, N_NUM_NAMES
    } ContactsPropIdx;

    // this enum defines the order of the props
    enum
    {
        C_CALLBACK_TELEPHONE_NUMBER, C_CAR_TELEPHONE_NUMBER, C_COMPANY_NAME, C_MAIL1ADDRESS,
        C_MAIL1EID, C_MAIL1TYPE, C_MAIL1DISPNAME, C_MAIL2ADDRESS, C_MAIL2EID, C_MAIL2TYPE,
        C_MAIL2DISPNAME, C_MAIL3ADDRESS, C_MAIL3EID, C_MAIL3TYPE, C_MAIL3DISPNAME, C_FILEAS,
        C_FILEASID, C_GIVEN_NAME, C_HOME_ADDRESS_CITY, C_HOME_ADDRESS_COUNTRY,
        C_HOME_FAX_NUMBER, C_HOME_TELEPHONE_NUMBER, C_HOME2_TELEPHONE_NUMBER,
        C_HOME_ADDRESS_POSTAL_CODE, C_HOME_ADDRESS_STATE_OR_PROVINCE, C_HOME_ADDRESS_STREET,
        C_TITLE, C_SURNAME, C_MIDDLE_NAME, C_CELLULAR_TELEPHONE_NUMBER,
        C_DISPLAY_NAME_PREFIX, C_GENERATION,
        // NOTES IS PR_BODY AND PR_BODY_HTML
        C_OTHER_ADDRESS_CITY, C_OTHER_ADDRESS_COUNTRY, C_PRIMARY_FAX_NUMBER,    // OTHER FAX
        C_OTHER_TELEPHONE_NUMBER, C_OTHER_ADDRESS_POSTAL_CODE,
        C_OTHER_ADDRESS_STATE_OR_PROVINCE, C_OTHER_ADDRESS_STREET, C_PAGER_TELEPHONE_NUMBER,
        C_BUSINESS_ADDRESS_CITY, C_BUSINESS_ADDRESS_COUNTRY, C_BUSINESS_FAX_NUMBER,
        C_OFFICE_TELEPHONE_NUMBER, C_BUSINESS_ADDRESS_POSTAL_CODE, C_BUSINESS_ADDRESS_STATE,
        C_BUSINESS_ADDRESS_STREET, C_BUSINESS_HOME_PAGE, C_BIRTHDAY, C_CONTACT_USER1_IDX,
        C_CONTACT_USER2_IDX, C_CONTACT_USER3_IDX, C_CONTACT_USER4_IDX, C_ONEOFFMEMEBRS_IDX,
        C_IMADDRESS, C_ANNIVERSARY, C_NUM_PROPS
    };

    enum OLK_FILE_AS
    {
        OFA_COMPANY = 14870, OFA_LAST_C_FIRST_COMPANY = 32793, OFA_COMPANY_LAST_C_FIRST = 32792,
        OFA_LAST_C_FIRST = 32791, OFA_FIRST_LAST = 32823, OFA_CUSTOM = -1,
        // Not supported by outlook:
        // OFA_FIRST_LAST_COMPANY = XXXX,
        // OFA_COMPANY_FIRST_LAST = XXXX
    };

    // these are the named property id's
    LONG nameIds[N_NUM_NAMES];
    Zimbra::MAPI::MAPIMessage *m_mapiMessage;
    Zimbra::MAPI::MAPISession *m_session;
    LPMESSAGE m_pMessage;
    LPSPropValue m_pPropVals;
    bool m_bPersonalDL;
    LONG m_zimbraFileAsId;

    // contact data members
    wstring m_pCallbackPhone;
    wstring m_pCarPhone;
    wstring m_pCompany;
    wstring m_pEmail;
    wstring m_pEmail2;
    wstring m_pEmail3;
    wstring m_pFileAs;
    wstring m_pFirstName;
    wstring m_pHomeCity;
    wstring m_pHomeCountry;
    wstring m_pHomeFax;
    wstring m_pHomePhone;
    wstring m_pHomePhone2;
    wstring m_pHomePostalCode;
    wstring m_pHomeState;
    wstring m_pHomeStreet;
    wstring m_pHomeURL;
    wstring m_pJobTitle;
    wstring m_pLastName;
    wstring m_pMiddleName;
    wstring m_pMobilePhone;
    wstring m_pNamePrefix;
    wstring m_pNameSuffix;
    wstring m_pNotes;
    wstring m_pOtherCity;
    wstring m_pOtherCountry;
    wstring m_pOtherFax;
    wstring m_pOtherPhone;
    wstring m_pOtherPostalCode;
    wstring m_pOtherState;
    wstring m_pOtherStreet;
    wstring m_pOtherURL;
    wstring m_pPager;
    wstring m_pWorkCity;
    wstring m_pWorkCountry;
    wstring m_pWorkFax;
    wstring m_pWorkPhone;
    wstring m_pWorkPostalCode;
    wstring m_pWorkState;
    wstring m_pWorkStreet;
    wstring m_pWorkURL;
    wstring m_pBirthday;
    wstring m_pUserField1;
    wstring m_pUserField2;
    wstring m_pUserField3;
    wstring m_pUserField4;
    wstring m_pNickName;
    wstring m_pDList;
    wstring m_pType;
    wstring m_pPictureID;
    size_t m_size;
    wstring m_pIMAddress1;
    wstring m_anniversary;
    wstring m_contact_image_path;
	vector<ContactUDFields> m_vud_Fields;
    HRESULT Init();

public:
    MAPIContact(Zimbra::MAPI::MAPISession &session, Zimbra::MAPI::MAPIMessage &mMessage);
    ~MAPIContact();
    bool IsPersonalDL() { return m_bPersonalDL; }
    HRESULT GetContactImage(wstring &wstrImagePath);

	void AddUserDefinedField(ContactUDFields &cudf)
	{
		m_vud_Fields.push_back(cudf);
	}

    void CallbackPhone(LPTSTR pStr)
    {
        m_pCallbackPhone = pStr;
        m_size += m_pCallbackPhone.length();
    }

    void CarPhone(LPTSTR pStr)
    {
        m_pCarPhone = pStr;
        m_size += m_pCarPhone.length();
    }

    void Company(LPTSTR pStr)
    {
        m_pCompany = pStr;
        m_size += m_pCompany.length();
    }

    void Email(LPTSTR pStr)
    {
        m_pEmail = pStr;
        m_size += m_pEmail.length();
    }

    void Email2(LPTSTR pStr)
    {
        m_pEmail2 = pStr;
        m_size += m_pEmail2.length();
    }

    void Email3(LPTSTR pStr)
    {
        m_pEmail3 = pStr;
        m_size += m_pEmail3.length();
    }

    void FileAs(LPTSTR pStr)
    {
        m_pFileAs = pStr;
        m_size += m_pFileAs.length();
    }

    void FirstName(LPTSTR pStr)
    {
        m_pFirstName = pStr;
        m_size += m_pFirstName.length();
    }

    void HomeCity(LPTSTR pStr)
    {
        m_pHomeCity = pStr;
        m_size += m_pHomeCity.length();
    }

    void HomeCountry(LPTSTR pStr)
    {
        m_pHomeCountry = pStr;
        m_size += m_pHomeCountry.length();
    }

    void HomeFax(LPTSTR pStr)
    {
        m_pHomeFax = pStr;
        m_size += m_pHomeFax.length();
    }

    void HomePhone(LPTSTR pStr)
    {
        m_pHomePhone = pStr;
        m_size += m_pHomePhone.length();
    }

    void HomePhone2(LPTSTR pStr)
    {
        m_pHomePhone2 = pStr;
        m_size += m_pHomePhone2.length();
    }

    void HomePostalCode(LPTSTR pStr)
    {
        m_pHomePostalCode = pStr;
        m_size += m_pHomePostalCode.length();
    }

    void HomeState(LPTSTR pStr)
    {
        m_pHomeState = pStr;
        m_size += m_pHomeState.length();
    }

    void HomeStreet(LPTSTR pStr)
    {
        m_pHomeStreet = pStr;
        m_size += m_pHomeStreet.length();
    }

    void HomeURL(LPTSTR pStr)
    {
        m_pHomeURL = pStr;
        m_size += m_pHomeURL.length();
    }

    void JobTitle(LPTSTR pStr)
    {
        m_pJobTitle = pStr;
        m_size += m_pJobTitle.length();
    }

    void LastName(LPTSTR pStr)
    {
        m_pLastName = pStr;
        m_size += m_pLastName.length();
    }

    void MiddleName(LPTSTR pStr)
    {
        m_pMiddleName = pStr;
        m_size += m_pMiddleName.length();
    }

    void MobilePhone(LPTSTR pStr)
    {
        m_pMobilePhone = pStr;
        m_size += m_pMobilePhone.length();
    }

    void NamePrefix(LPTSTR pStr)
    {
        m_pNamePrefix = pStr;
        m_size += m_pNamePrefix.length();
    }

    void NameSuffix(LPTSTR pStr)
    {
        m_pNameSuffix = pStr;
        m_size += m_pNameSuffix.length();
    }

    void Notes(LPTSTR pStr)
    {
        m_pNotes = pStr;
        m_size += m_pNotes.length();
    }

    void OtherCity(LPTSTR pStr)
    {
        m_pOtherCity = pStr;
        m_size += m_pOtherCity.length();
    }

    void OtherCountry(LPTSTR pStr)
    {
        m_pOtherCountry = pStr;
        m_size += m_pOtherCountry.length();
    }

    void OtherFax(LPTSTR pStr)
    {
        m_pOtherFax = pStr;
        m_size += m_pOtherFax.length();
    }

    void OtherPhone(LPTSTR pStr)
    {
        m_pOtherPhone = pStr;
        m_size += m_pOtherPhone.length();
    }

    void OtherPostalCode(LPTSTR pStr)
    {
        m_pOtherPostalCode = pStr;
        m_size += m_pOtherPostalCode.length();
    }

    void OtherState(LPTSTR pStr)
    {
        m_pOtherState = pStr;
        m_size += m_pOtherState.length();
    }

    void OtherStreet(LPTSTR pStr)
    {
        m_pOtherStreet = pStr;
        m_size += m_pOtherStreet.length();
    }

    void OtherURL(LPTSTR pStr)
    {
        m_pOtherURL = pStr;
        m_size += m_pOtherURL.length();
    }

    void Pager(LPTSTR pStr)
    {
        m_pPager = pStr;
        m_size += m_pPager.length();
    }

    void WorkCity(LPTSTR pStr)
    {
        m_pWorkCity = pStr;
        m_size += m_pWorkCity.length();
    }

    void WorkCountry(LPTSTR pStr)
    {
        m_pWorkCountry = pStr;
        m_size += m_pWorkCountry.length();
    }

    void WorkFax(LPTSTR pStr)
    {
        m_pWorkFax = pStr;
        m_size += m_pWorkFax.length();
    }

    void WorkPhone(LPTSTR pStr)
    {
        m_pWorkPhone = pStr;
        m_size += m_pWorkPhone.length();
    }

    void WorkPostalCode(LPTSTR pStr)
    {
        m_pWorkPostalCode = pStr;
        m_size += m_pWorkPostalCode.length();
    }

    void WorkState(LPTSTR pStr)
    {
        m_pWorkState = pStr;
        m_size += m_pWorkState.length();
    }

    void WorkStreet(LPTSTR pStr)
    {
        m_pWorkStreet = pStr;
        m_size += m_pWorkStreet.length();
    }

    void WorkURL(LPTSTR pStr)
    {
        m_pWorkURL = pStr;
        m_size += m_pWorkURL.length();
    }

    void Birthday(LPTSTR pStr)
    {
        m_pBirthday = pStr;
        m_size += m_pBirthday.length();
    }

    void Anniversary(LPTSTR pStr)
    {
        m_anniversary = pStr;
        m_size += m_anniversary.length();
    }

    void UserField1(LPTSTR pStr)
    {
        m_pUserField1 = pStr;
        m_size += m_pUserField1.length();
    }

    void UserField2(LPTSTR pStr)
    {
        m_pUserField2 = pStr;
        m_size += m_pUserField2.length();
    }

    void UserField3(LPTSTR pStr)
    {
        m_pUserField3 = pStr;
        m_size += m_pUserField3.length();
    }

    void UserField4(LPTSTR pStr)
    {
        m_pUserField4 = pStr;
        m_size += m_pUserField4.length();
    }

    void NickName(LPTSTR pStr)
    {
        m_pNickName = pStr;
        m_size += m_pNickName.length();
    }

    void DList(LPTSTR pStr)
    {
        m_pDList = pStr;
        m_size += m_pDList.length();
    }

    void Type(LPTSTR pStr)
    {
        m_pType = pStr;
        m_size += m_pType.length();
    }

    void IMAddress1(LPTSTR pStr)
    {
        m_pIMAddress1 = pStr;
        m_size += m_pIMAddress1.length();
    }

    void ContactImagePath(LPTSTR pStr)
    {
        m_contact_image_path = pStr;
    }

    wstring CallbackPhone() { return m_pCallbackPhone; }
    wstring CarPhone() { return m_pCarPhone; }
    wstring Company() { return m_pCompany; }
    wstring Email() { return m_pEmail; }
    wstring Email2() { return m_pEmail2; }
    wstring Email3() { return m_pEmail3; }
    wstring FileAs() { return m_pFileAs; }
    wstring FirstName() { return m_pFirstName; }
    wstring HomeCity() { return m_pHomeCity; }
    wstring HomeCountry() { return m_pHomeCountry; }
    wstring HomeFax() { return m_pHomeFax; }
    wstring HomePhone() { return m_pHomePhone; }
    wstring HomePhone2() { return m_pHomePhone2; }
    wstring HomePostalCode() { return m_pHomePostalCode; }
    wstring HomeState() { return m_pHomeState; }
    wstring HomeStreet() { return m_pHomeStreet; }
    wstring HomeURL() { return m_pHomeURL; }
    wstring JobTitle() { return m_pJobTitle; }
    wstring LastName() { return m_pLastName; }
    wstring MiddleName() { return m_pMiddleName; }
    wstring MobilePhone() { return m_pMobilePhone; }
    wstring NamePrefix() { return m_pNamePrefix; }
    wstring NameSuffix() { return m_pNameSuffix; }
    wstring Notes() { return m_pNotes; }
    wstring OtherCity() { return m_pOtherCity; }
    wstring OtherCountry() { return m_pOtherCountry; }
    wstring OtherFax() { return m_pOtherFax; }
    wstring OtherPhone() { return m_pOtherPhone; }
    wstring OtherPostalCode() { return m_pOtherPostalCode; }
    wstring OtherState() { return m_pOtherState; }
    wstring OtherStreet() { return m_pOtherStreet; }
    wstring OtherURL() { return m_pOtherURL; }
    wstring Pager() { return m_pPager; }
    wstring WorkCity() { return m_pWorkCity; }
    wstring WorkCountry() { return m_pWorkCountry; }
    wstring WorkFax() { return m_pWorkFax; }
    wstring WorkPhone() { return m_pWorkPhone; }
    wstring WorkPostalCode() { return m_pWorkPostalCode; }
    wstring WorkState() { return m_pWorkState; }
    wstring WorkStreet() { return m_pWorkStreet; }
    wstring WorkURL() { return m_pWorkURL; }
    wstring Birthday() { return m_pBirthday; }
    wstring Anniversary() { return m_anniversary; }
    wstring UserField1() { return m_pUserField1; }
    wstring UserField2() { return m_pUserField2; }
    wstring UserField3() { return m_pUserField3; }
    wstring UserField4() { return m_pUserField4; }
    wstring NickName() { return m_pNickName; }
    wstring DList() { return m_pDList; }
    wstring Type() { return m_pType; }
    wstring IMAddress1() { return m_pIMAddress1; }
    size_t Size() { return m_size; }
    wstring ContactImagePath() { return m_contact_image_path; }
    wstring Picture() { return m_pPictureID; }
    wstring Anniverssary() { return m_anniversary; }
	vector<ContactUDFields>* UserDefinedFields() {return &m_vud_Fields;}
    void Picture(LPTSTR pStr, UINT ulFileSize)
    {
        m_pPictureID = pStr;
        m_size += ulFileSize;
    }
};
