#include "common.h"
#include "Exchange.h"
#include "MAPIContacts.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPIContact
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIContact::MAPIContact(LPMESSAGE pMessage)
{
	m_bPersonalDL=false;
	m_pMessage = pMessage;

	pr_mail1address = 0,
	pr_mail1entryid = 0,
	pr_mail1type = 0,
	pr_mail1dispname=0,
	pr_mail2address = 0,
	pr_mail2entryid = 0,
	pr_mail2type = 0,
	pr_mail2dispname=0,
	pr_mail3address = 0,
	pr_mail3entryid = 0,
	pr_mail3type = 0,
	pr_mail3dispname=0,
	pr_fileas = 0,
	pr_fileasID = 0,
	pr_business_address_city = 0,
	pr_business_address_country = 0,
	pr_business_address_postal_code = 0,
	pr_business_address_state = 0,
	pr_business_address_street = 0,
    pr_contact_user1_idx = 0,
    pr_contact_user2_idx = 0,
    pr_contact_user3_idx = 0,
    pr_contact_user4_idx = 0,
    pr_contact_oneoffmemebrs = 0,
	pr_imaddress = 0 ;

	//init named props
	nameIds[0] = 0x8083;
	nameIds[0] = 0x8085;
	nameIds[0] = 0x8082;
	nameIds[0] = 0x8084;
	nameIds[0] = 0x8093;
	nameIds[0] = 0x8095;
	nameIds[0] = 0x8092;
	nameIds[0] = 0x8094;
	nameIds[0] = 0x80A3;
	nameIds[0] = 0x80A5; 
	nameIds[0] = 0x80A2;
	nameIds[0] = 0x80A4;
	nameIds[0] = 0x8005;
	nameIds[0] = 0x8006;
	nameIds[0] = 0x8046;
	nameIds[0] = 0x8049;
	nameIds[0] = 0x8048;
	nameIds[0] = 0x8047;
	nameIds[0] = 0x8045;
	nameIds[0] = 0x804f;
	nameIds[0] = 0x8050;
	nameIds[0] = 0x8051;
	nameIds[0] = 0x8052;
	nameIds[0] = 0x8054;
	nameIds[0] = 0x8062;
	
	Init();
}

MAPIContact::~MAPIContact()
{

}

HRESULT MAPIContact::Init()
{
	//is Persoanl DL?
	LPSPropValue pPropValMsgClass = NULL ;
	HRESULT hr=HrGetOneProp( m_pMessage, PR_MESSAGE_CLASS, &pPropValMsgClass ) ;
    if( pPropValMsgClass->ulPropTag == PR_MESSAGE_CLASS_W && 
        _tcsicmp( pPropValMsgClass->Value.LPSZ, L"ipm.distlist" ) == 0 ) 
		m_bPersonalDL = true ;
	
    if( pPropValMsgClass )
		MAPIFreeBuffer( pPropValMsgClass ) ;
    
	//initialize the MAPINAMEID structure GetIDsFromNames requires
	LPMAPINAMEID ppNames[N_NUM_NAMES] = {0};
	for( int i = 0; i < N_NUM_NAMES; i++ )
	{
		MAPIAllocateBuffer( sizeof(MAPINAMEID), (LPVOID*)&(ppNames[i]) );
		ppNames[i]->ulKind = MNID_ID;
		ppNames[i]->lpguid = (LPGUID)(&PS_CONTACT_PROPERTIES);
		ppNames[i]->Kind.lID = nameIds[i];
	}

	//get the real prop tag ID's
	LPSPropTagArray pContactTags = NULL;
	hr = m_pMessage->GetIDsFromNames( N_NUM_NAMES, ppNames, MAPI_CREATE, &pContactTags );
	if(FAILED(hr)) {
        return hr;
    }
	
	//give the prop tag ID's a type
	pr_mail1address					= SetPropType( pContactTags->aulPropTag[N_MAIL1			], PT_TSTRING );
	pr_mail1entryid					= SetPropType( pContactTags->aulPropTag[N_MAIL1EID		], PT_BINARY );
	pr_mail1type					= SetPropType( pContactTags->aulPropTag[N_MAIL1TYPE		], PT_TSTRING );
	pr_mail1dispname				= SetPropType( pContactTags->aulPropTag[N_MAIL1DISPNAME	], PT_TSTRING );
	pr_mail2address					= SetPropType( pContactTags->aulPropTag[N_MAIL2			], PT_TSTRING );
	pr_mail2entryid					= SetPropType( pContactTags->aulPropTag[N_MAIL2EID		], PT_BINARY );
	pr_mail2type					= SetPropType( pContactTags->aulPropTag[N_MAIL2TYPE		], PT_TSTRING );
	pr_mail2dispname				= SetPropType( pContactTags->aulPropTag[N_MAIL2DISPNAME	], PT_TSTRING );
	pr_mail3address					= SetPropType( pContactTags->aulPropTag[N_MAIL3			], PT_TSTRING );
	pr_mail3entryid					= SetPropType( pContactTags->aulPropTag[N_MAIL3EID		], PT_BINARY );
	pr_mail3type					= SetPropType( pContactTags->aulPropTag[N_MAIL3TYPE		], PT_TSTRING );
	pr_mail3dispname				= SetPropType( pContactTags->aulPropTag[N_MAIL3DISPNAME	], PT_TSTRING );
	pr_fileas						= SetPropType( pContactTags->aulPropTag[N_FILEAS		], PT_TSTRING );
	pr_fileasID						= SetPropType( pContactTags->aulPropTag[N_FILEAS_ID		], PT_LONG );
	pr_business_address_city		= SetPropType( pContactTags->aulPropTag[N_BUS_CITY		], PT_TSTRING );
	pr_business_address_country		= SetPropType( pContactTags->aulPropTag[N_BUS_COUNTRY	], PT_TSTRING );
	pr_business_address_postal_code = SetPropType( pContactTags->aulPropTag[N_BUS_ZIP		], PT_TSTRING );
	pr_business_address_state		= SetPropType( pContactTags->aulPropTag[N_BUS_STATE		], PT_TSTRING );
	pr_business_address_street		= SetPropType( pContactTags->aulPropTag[N_BUS_STREET	], PT_TSTRING );
	pr_contact_user1_idx            = SetPropType( pContactTags->aulPropTag[N_CONTACT_USER1_IDX	], PT_TSTRING );
    pr_contact_user2_idx            = SetPropType( pContactTags->aulPropTag[N_CONTACT_USER2_IDX	], PT_TSTRING );
    pr_contact_user3_idx            = SetPropType( pContactTags->aulPropTag[N_CONTACT_USER3_IDX	], PT_TSTRING );
    pr_contact_user4_idx            = SetPropType( pContactTags->aulPropTag[N_CONTACT_USER4_IDX	], PT_TSTRING );
	pr_contact_oneoffmemebrs        = SetPropType( pContactTags->aulPropTag[N_CONTACT_ONEOFFMEMEBRS_IDX], PT_MV_BINARY );
	pr_imaddress			        = SetPropType( pContactTags->aulPropTag[N_IMADDRESS], PT_TSTRING );
	
	//free the memory we allocated on the head
	for( int i = 0; i < N_NUM_NAMES; i++ )
		MAPIFreeBuffer( ppNames[i] );
	MAPIFreeBuffer( pContactTags );

	//these are the contact properties we need to get
	SizedSPropTagArray( C_NUM_PROPS, contactProps ) = 
	{
		C_NUM_PROPS,
		{
			PR_CALLBACK_TELEPHONE_NUMBER,
			PR_CAR_TELEPHONE_NUMBER,
			PR_COMPANY_NAME,
			pr_mail1address,
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
			PR_GIVEN_NAME,
			PR_HOME_ADDRESS_CITY,
			PR_HOME_ADDRESS_COUNTRY,
			PR_HOME_FAX_NUMBER,
			PR_HOME_TELEPHONE_NUMBER,
			PR_HOME2_TELEPHONE_NUMBER,
			PR_HOME_ADDRESS_POSTAL_CODE,
			PR_HOME_ADDRESS_STATE_OR_PROVINCE,
			PR_HOME_ADDRESS_STREET,
			PR_TITLE,
			PR_SURNAME,
			PR_MIDDLE_NAME,
			PR_CELLULAR_TELEPHONE_NUMBER,
			PR_DISPLAY_NAME_PREFIX,
			PR_GENERATION,
			//notes is PR_BODY and PR_BODY_HTML
			PR_OTHER_ADDRESS_CITY,
			PR_OTHER_ADDRESS_COUNTRY,
			PR_PRIMARY_FAX_NUMBER, //other fax
			PR_OTHER_TELEPHONE_NUMBER,
			PR_OTHER_ADDRESS_POSTAL_CODE,
			PR_OTHER_ADDRESS_STATE_OR_PROVINCE,
			PR_OTHER_ADDRESS_STREET,
			PR_PAGER_TELEPHONE_NUMBER,
			pr_business_address_city,
			pr_business_address_country,
			PR_BUSINESS_FAX_NUMBER,
			PR_OFFICE_TELEPHONE_NUMBER,
			pr_business_address_postal_code,
			pr_business_address_state,
			pr_business_address_street,
			PR_BUSINESS_HOME_PAGE,
            PR_BIRTHDAY,
            pr_contact_user1_idx,
            pr_contact_user2_idx,
            pr_contact_user3_idx,
            pr_contact_user4_idx,
            pr_contact_oneoffmemebrs,
			pr_imaddress
		}
	};

	ULONG cVals = 0;
	hr = m_pMessage->GetProps( (LPSPropTagArray)&contactProps, fMapiUnicode, &cVals, &m_pPropVals );
	if( FAILED(hr) )
		return hr;
	
	return S_OK;
}