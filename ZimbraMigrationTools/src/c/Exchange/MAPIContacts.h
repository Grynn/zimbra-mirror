#pragma once

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

	HRESULT Init();
public:
	MAPIContact(LPMESSAGE pMessage);
	~MAPIContact();
	bool IsPersonalDL(){return m_bPersonalDL;};
};
