#pragma once

typedef struct _RecipInfo
{
	LPTSTR pAddrType;
	LPTSTR pEmailAddr;
	ULONG cbEid;
	LPENTRYID pEid;

} RECIP_INFO;

//MAPContact class
class MAPIContact
{
private:
	//prop tags for named properties
	ULONG	pr_mail1address,
			pr_mail1entryid,
			pr_mail1type,
			pr_mail1dispname,
			pr_mail2address,
			pr_mail2entryid,
			pr_mail2type,
			pr_mail2dispname,
			pr_mail3address,
			pr_mail3entryid,
			pr_mail3type,
			pr_mail3dispname,
			pr_fileas,
			pr_fileasID,
			pr_business_address_city,
			pr_business_address_country,
			pr_business_address_postal_code,
			pr_business_address_state,
			pr_business_address_street,
            pr_contact_user1_idx,
            pr_contact_user2_idx,
            pr_contact_user3_idx,
            pr_contact_user4_idx,
            pr_contact_oneoffmemebrs,
			pr_imaddress;
	//index of props
	typedef enum _ContactsPropIdx{ 
		N_MAIL1, N_MAIL1EID, N_MAIL1TYPE, N_MAIL1DISPNAME,
		N_MAIL2, N_MAIL2EID, N_MAIL2TYPE, N_MAIL2DISPNAME,
		N_MAIL3, N_MAIL3EID, N_MAIL3TYPE,  N_MAIL3DISPNAME,
		N_FILEAS,N_FILEAS_ID, N_BUS_CITY, 
		N_BUS_COUNTRY, N_BUS_ZIP, N_BUS_STATE, 
		N_BUS_STREET, N_CONTACT_USER1_IDX, N_CONTACT_USER2_IDX,
        N_CONTACT_USER3_IDX, N_CONTACT_USER4_IDX, 
        N_CONTACT_ONEOFFMEMEBRS_IDX, N_IMADDRESS, N_NUM_NAMES  
	}ContactsPropIdx;

	//this enum defines the order of the props
	enum 
	{
		C_CALLBACK_TELEPHONE_NUMBER,
		C_CAR_TELEPHONE_NUMBER,
		C_COMPANY_NAME,
		C_MAIL1ADDRESS,
		C_MAIL1EID,
		C_MAIL1TYPE,
		C_MAIL1DISPNAME,
		C_MAIL2ADDRESS,
		C_MAIL2EID,
		C_MAIL2TYPE,
		C_MAIL2DISPNAME,
		C_MAIL3ADDRESS,
		C_MAIL3EID,
		C_MAIL3TYPE,
		C_MAIL3DISPNAME,
		C_FILEAS,
		C_FILEASID,
		C_GIVEN_NAME,
		C_HOME_ADDRESS_CITY,
		C_HOME_ADDRESS_COUNTRY,
		C_HOME_FAX_NUMBER,
		C_HOME_TELEPHONE_NUMBER,
		C_HOME2_TELEPHONE_NUMBER,
		C_HOME_ADDRESS_POSTAL_CODE,
		C_HOME_ADDRESS_STATE_OR_PROVINCE,
		C_HOME_ADDRESS_STREET,
		C_TITLE,
		C_SURNAME,
		C_MIDDLE_NAME,
		C_CELLULAR_TELEPHONE_NUMBER,
		C_DISPLAY_NAME_PREFIX,
		C_GENERATION,
		//NOTES IS PR_BODY AND PR_BODY_HTML
		C_OTHER_ADDRESS_CITY,
		C_OTHER_ADDRESS_COUNTRY,
		C_PRIMARY_FAX_NUMBER, //OTHER FAX
		C_OTHER_TELEPHONE_NUMBER,
		C_OTHER_ADDRESS_POSTAL_CODE,
		C_OTHER_ADDRESS_STATE_OR_PROVINCE,
		C_OTHER_ADDRESS_STREET,
		C_PAGER_TELEPHONE_NUMBER,
		C_BUSINESS_ADDRESS_CITY,
		C_BUSINESS_ADDRESS_COUNTRY,
		C_BUSINESS_FAX_NUMBER,
		C_OFFICE_TELEPHONE_NUMBER,
		C_BUSINESS_ADDRESS_POSTAL_CODE,
		C_BUSINESS_ADDRESS_STATE,
		C_BUSINESS_ADDRESS_STREET,
		C_BUSINESS_HOME_PAGE,
        C_BIRTHDAY,
        C_CONTACT_USER1_IDX,
        C_CONTACT_USER2_IDX,
        C_CONTACT_USER3_IDX,
        C_CONTACT_USER4_IDX,
        C_ONEOFFMEMEBRS_IDX,
		C_IMADDRESS,
		C_NUM_PROPS
	};

	enum OLK_FILE_AS
	{
		OFA_COMPANY = 14870,
		OFA_LAST_C_FIRST_COMPANY = 32793,
		OFA_COMPANY_LAST_C_FIRST = 32792,
		OFA_LAST_C_FIRST = 32791,
		OFA_FIRST_LAST = 32823,
		OFA_CUSTOM = -1,

		// Not supported by outlook:
		//OFA_FIRST_LAST_COMPANY = XXXX,
		//OFA_COMPANY_FIRST_LAST = XXXX
	};
	//these are the named property id's
	LONG nameIds[N_NUM_NAMES];
	LPMESSAGE m_pMessage;
	LPSPropValue m_pPropVals;
	bool m_bPersonalDL;
	LONG m_zimbraFileAsId;

	//contact data members
	LPTSTR m_pCallbackPhone;
	LPTSTR m_pCarPhone;
	LPTSTR m_pCompany;
	LPTSTR m_pEmail;
	LPTSTR m_pEmail2;
	LPTSTR m_pEmail3;
	LPTSTR m_pFileAs;
	LPTSTR m_pFirstName;
	LPTSTR m_pHomeCity;
	LPTSTR m_pHomeCountry;
	LPTSTR m_pHomeFax;
	LPTSTR m_pHomePhone;
	LPTSTR m_pHomePhone2;
	LPTSTR m_pHomePostalCode;
	LPTSTR m_pHomeState;
	LPTSTR m_pHomeStreet;
	LPTSTR m_pHomeURL;
	LPTSTR m_pJobTitle;
	LPTSTR m_pLastName;
	LPTSTR m_pMiddleName;
	LPTSTR m_pMobilePhone;
	LPTSTR m_pNamePrefix;
	LPTSTR m_pNameSuffix;
	LPTSTR m_pNotes;
	LPTSTR m_pOtherCity;
	LPTSTR m_pOtherCountry;
	LPTSTR m_pOtherFax;
	LPTSTR m_pOtherPhone;
	LPTSTR m_pOtherPostalCode;
	LPTSTR m_pOtherState;
	LPTSTR m_pOtherStreet;
	LPTSTR m_pOtherURL;
	LPTSTR m_pPager;
	LPTSTR m_pWorkCity;
	LPTSTR m_pWorkCountry;
	LPTSTR m_pWorkFax;
	LPTSTR m_pWorkPhone;
	LPTSTR m_pWorkPostalCode;
	LPTSTR m_pWorkState;
	LPTSTR m_pWorkStreet;
	LPTSTR m_pWorkURL;
    LPTSTR m_pBirthday;
    LPTSTR m_pUserField1;
    LPTSTR m_pUserField2;
    LPTSTR m_pUserField3;
    LPTSTR m_pUserField4;
    LPTSTR m_pNickName;
    LPTSTR m_pDList;
    LPTSTR m_pType;
    LPTSTR m_pPictureID;
	int	   m_size;
	LPTSTR m_pIMAddress1;

	HRESULT Init();
public:
	MAPIContact(LPMESSAGE pMessage);
	~MAPIContact();
	bool IsPersonalDL(){return m_bPersonalDL;};

	void CallbackPhone( LPTSTR pStr ){ m_size += CopyString( m_pCallbackPhone, pStr ); }
	void CarPhone( LPTSTR pStr ) { m_size += CopyString( m_pCarPhone, pStr );  }
	void Company( LPTSTR pStr ) { m_size += CopyString( m_pCompany, pStr );  }
	void Email( LPTSTR pStr ) { m_size += CopyString( m_pEmail, pStr );  }
	void Email2( LPTSTR pStr ) { m_size += CopyString( m_pEmail2, pStr );  }
	void Email3( LPTSTR pStr ) { m_size += CopyString( m_pEmail3, pStr );  }
	void FileAs( LPTSTR pStr ) { m_size += CopyString( m_pFileAs, pStr );  }
	void FirstName( LPTSTR pStr ) { m_size += CopyString( m_pFirstName, pStr );  }
	void HomeCity( LPTSTR pStr ) { m_size += CopyString( m_pHomeCity, pStr );  }
	void HomeCountry( LPTSTR pStr ) { m_size += CopyString( m_pHomeCountry, pStr );  }
	void HomeFax( LPTSTR pStr ) { m_size += CopyString( m_pHomeFax, pStr );  }
	void HomePhone( LPTSTR pStr ) { m_size += CopyString( m_pHomePhone, pStr );  }
	void HomePhone2( LPTSTR pStr ) { m_size += CopyString( m_pHomePhone2, pStr );  }
	void HomePostalCode( LPTSTR pStr ) { m_size += CopyString( m_pHomePostalCode, pStr );  }
	void HomeState( LPTSTR pStr ) { m_size += CopyString( m_pHomeState, pStr );  }
	void HomeStreet( LPTSTR pStr ) { m_size += CopyString( m_pHomeStreet, pStr );  }
	void HomeURL( LPTSTR pStr ) { m_size += CopyString( m_pHomeURL, pStr );  }
	void JobTitle( LPTSTR pStr ) { m_size += CopyString( m_pJobTitle, pStr );  }
	void LastName( LPTSTR pStr ) { m_size += CopyString( m_pLastName, pStr );  }
	void MiddleName( LPTSTR pStr ) { m_size += CopyString( m_pMiddleName, pStr );  }
	void MobilePhone( LPTSTR pStr ) { m_size += CopyString( m_pMobilePhone, pStr );  }
	void NamePrefix( LPTSTR pStr ) { m_size += CopyString( m_pNamePrefix, pStr );  }
	void NameSuffix( LPTSTR pStr ) { m_size += CopyString( m_pNameSuffix, pStr );  }
	void Notes( LPTSTR pStr ) { m_size += CopyString( m_pNotes, pStr );  }
	void OtherCity( LPTSTR pStr ) { m_size += CopyString( m_pOtherCity, pStr );  }
	void OtherCountry( LPTSTR pStr ) { m_size += CopyString( m_pOtherCountry, pStr );  }
	void OtherFax( LPTSTR pStr ) { m_size += CopyString( m_pOtherFax, pStr );  }
	void OtherPhone( LPTSTR pStr ) { m_size += CopyString( m_pOtherPhone, pStr );  }
	void OtherPostalCode( LPTSTR pStr ) { m_size += CopyString( m_pOtherPostalCode, pStr );  }
	void OtherState( LPTSTR pStr ) { m_size += CopyString( m_pOtherState, pStr );  }
	void OtherStreet( LPTSTR pStr ) { m_size += CopyString( m_pOtherStreet, pStr );  }
	void OtherURL( LPTSTR pStr ) { m_size += CopyString( m_pOtherURL, pStr );  }
	void Pager( LPTSTR pStr ) { m_size += CopyString( m_pPager, pStr );  }
	void WorkCity( LPTSTR pStr ) { m_size += CopyString( m_pWorkCity, pStr );  }
	void WorkCountry( LPTSTR pStr ) { m_size += CopyString( m_pWorkCountry, pStr );  }
	void WorkFax( LPTSTR pStr ) { m_size += CopyString( m_pWorkFax, pStr );  }
	void WorkPhone( LPTSTR pStr ) { m_size += CopyString( m_pWorkPhone, pStr );  }
	void WorkPostalCode( LPTSTR pStr ) { m_size += CopyString( m_pWorkPostalCode, pStr );  }
	void WorkState( LPTSTR pStr ) { m_size += CopyString( m_pWorkState, pStr );  }
	void WorkStreet( LPTSTR pStr ) { m_size += CopyString( m_pWorkStreet, pStr );  }
	void WorkURL( LPTSTR pStr ) { m_size += CopyString( m_pWorkURL, pStr );  }
    void Birthday( LPTSTR pStr ){ m_size += CopyString( m_pBirthday, pStr );  }
    void UserField1( LPTSTR pStr ){ m_size += CopyString( m_pUserField1, pStr );  }
    void UserField2( LPTSTR pStr ){ m_size += CopyString( m_pUserField2, pStr );  }
    void UserField3( LPTSTR pStr ){ m_size += CopyString( m_pUserField3, pStr );  }
    void UserField4( LPTSTR pStr ){ m_size += CopyString( m_pUserField4, pStr );  }
    void NickName( LPTSTR pStr ){ m_size += CopyString( m_pNickName, pStr ); }
    void DList( LPTSTR pStr ){ m_size += CopyString( m_pDList, pStr ); }
    void Type( LPTSTR pStr ){ m_size += CopyString( m_pType, pStr ); }
	void IMAddress1( LPTSTR pStr ){ m_size += CopyString( m_pIMAddress1, pStr ); }



	LPTSTR CallbackPhone(){ return m_pCallbackPhone; }
		LPTSTR CarPhone() { return m_pCarPhone; }
		LPTSTR Company(){ return m_pCompany; }
		LPTSTR Email() { return m_pEmail; }
		LPTSTR Email2() { return m_pEmail2; }
		LPTSTR Email3() { return m_pEmail3; }
		LPTSTR FileAs() { return m_pFileAs; }
		LPTSTR FirstName() { return m_pFirstName; }
		LPTSTR HomeCity() { return m_pHomeCity; }
		LPTSTR HomeCountry() { return m_pHomeCountry; }
		LPTSTR HomeFax() { return m_pHomeFax; }
		LPTSTR HomePhone() { return m_pHomePhone; }
		LPTSTR HomePhone2() { return m_pHomePhone2; }
		LPTSTR HomePostalCode() { return m_pHomePostalCode; }
		LPTSTR HomeState() { return m_pHomeState; }
		LPTSTR HomeStreet() { return m_pHomeStreet; }
		LPTSTR HomeURL() { return m_pHomeURL; }
		LPTSTR JobTitle() { return m_pJobTitle; }
		LPTSTR LastName() { return m_pLastName; }
		LPTSTR MiddleName() { return m_pMiddleName; }
		LPTSTR MobilePhone() { return m_pMobilePhone; }
		LPTSTR NamePrefix() { return m_pNamePrefix; }
		LPTSTR NameSuffix() { return m_pNameSuffix; }
		LPTSTR Notes() { return m_pNotes; }
		LPTSTR OtherCity() { return m_pOtherCity; }
		LPTSTR OtherCountry() { return m_pOtherCountry; }
		LPTSTR OtherFax() { return m_pOtherFax; }
		LPTSTR OtherPhone() { return m_pOtherPhone; }
		LPTSTR OtherPostalCode() { return m_pOtherPostalCode; }
		LPTSTR OtherState() { return m_pOtherState; }
		LPTSTR OtherStreet() { return m_pOtherStreet; }
		LPTSTR OtherURL() { return m_pOtherURL; }
		LPTSTR Pager() { return m_pPager; }
		LPTSTR WorkCity() { return m_pWorkCity; }
		LPTSTR WorkCountry() { return m_pWorkCountry; }
		LPTSTR WorkFax() { return m_pWorkFax; }
		LPTSTR WorkPhone() { return m_pWorkPhone; }
		LPTSTR WorkPostalCode() { return m_pWorkPostalCode; }
		LPTSTR WorkState() { return m_pWorkState; }
		LPTSTR WorkStreet() { return m_pWorkStreet; }
		LPTSTR WorkURL() { return m_pWorkURL; }
        LPTSTR Birthday(){ return m_pBirthday; }
        LPTSTR UserField1(){ return m_pUserField1; }
        LPTSTR UserField2(){ return m_pUserField2; }
        LPTSTR UserField3(){ return m_pUserField3; }
        LPTSTR UserField4(){ return m_pUserField4; }
        LPTSTR NickName(){ return m_pNickName; }
        LPTSTR DList(){ return m_pDList; }
        LPTSTR Type(){ return m_pType; }
		LPTSTR IMAddress1(){ return m_pIMAddress1; }
		int Size(){ return m_size; }

        LPTSTR Picture(){ return m_pPictureID; }
        void Picture( LPTSTR pStr, UINT ulFileSize )
        { 
            CopyString( m_pPictureID, pStr );
            m_size += ulFileSize;
        }
};
