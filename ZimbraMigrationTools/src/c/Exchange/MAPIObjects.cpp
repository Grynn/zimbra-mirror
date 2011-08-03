#include "MAPIObjects.h"
#include "MapiUtils.h"
using namespace Zimbra::MAPI;

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//Exception class
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription):
	GenericException(hrErrCode,lpszDescription)
{
	//
}

MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile):
	GenericException(hrErrCode,lpszDescription,nLine,strFile)
{
	//
}

MAPIMessageException::MAPIMessageException(HRESULT hrErrCode, LPCWSTR lpszDescription):
	GenericException(hrErrCode,lpszDescription)
{
	//
}

MAPIMessageException::MAPIMessageException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile):
	GenericException(hrErrCode,lpszDescription,nLine,strFile)
{
	//
}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//FolderIterator
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
FolderIterator::FolderIterator()
{
	m_pParentFolder = NULL;
}

FolderIterator::~FolderIterator()
{

}

FolderIterator::FolderIterPropTags FolderIterator::m_props = 
{
	NFOLDERPROPS,{ PR_DISPLAY_NAME, PR_ENTRYID, PR_LONGTERM_ENTRYID_FROM_TABLE, PR_FOLDER_FLAGS}
};

LPSPropTagArray FolderIterator::GetProps()
{
	return (LPSPropTagArray)&m_props;
}

BOOL FolderIterator::GetNext(MAPIFolder& folder)
{
	SRow* pRow; 
	do
	{
		pRow = MAPITableIterator::GetNext();
		if( pRow == NULL )
			return FALSE;

	}while( (pRow->lpProps[FI_FLAGS].Value.l & MDB_FOLDER_NORMAL) == 0 );

	LPMAPIFOLDER pFolder = NULL;
    HRESULT hr = S_OK;
	ULONG objtype;
	ULONG cb = pRow->lpProps[FI_ENTRYID].Value.bin.cb;
	LPENTRYID peid = (LPENTRYID)(pRow->lpProps[FI_ENTRYID].Value.bin.lpb);

	if((hr = m_pParentFolder->OpenEntry( cb, peid, NULL, MAPI_BEST_ACCESS, &objtype, (LPUNKNOWN*)&pFolder ))!=S_OK)
		throw GenericException(hr,L"FolderIterator::GetNext():OpenEntry Failed.",__LINE__,__FILE__);

	folder.Initialize( pFolder, pRow->lpProps[FI_DISPLAY_NAME].Value.LPSZ, &(pRow->lpProps[FI_ENTRYID].Value.bin) );

	return TRUE;
}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//MAPIFolder
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIFolder::MAPIFolder():m_folder(NULL)
{
	m_EntryID.cb =0;
	m_EntryID.lpb = NULL;
}

MAPIFolder::~MAPIFolder()
{
	if( m_folder != NULL )
		UlRelease(m_folder);
	
	if( m_EntryID.lpb != NULL )
		MAPIFreeBuffer(m_EntryID.lpb);

	m_folder = NULL;
	m_EntryID.lpb = NULL;
	m_EntryID.cb = 0;
}

MAPIFolder::MAPIFolder(const MAPIFolder& folder)
{
	m_folder= folder.m_folder;
	m_displayname = folder.m_displayname;
	m_EntryID.cb = folder.m_EntryID.cb;
	MAPIAllocateBuffer( folder.m_EntryID.cb, (LPVOID*)&(m_EntryID.lpb) );
	memcpy( m_EntryID.lpb, folder.m_EntryID.lpb, folder.m_EntryID.cb );
}

void MAPIFolder::Initialize(LPMAPIFOLDER pFolder, LPTSTR displayName, LPSBinary pEntryId)
{
	if( m_folder != NULL )
		UlRelease(m_folder);
		
	if( m_EntryID.lpb != NULL )
		MAPIFreeBuffer(m_EntryID.lpb);

	m_folder=pFolder;
	m_displayname = displayName;
	m_EntryID.cb = pEntryId->cb;
	MAPIAllocateBuffer( m_EntryID.cb, (LPVOID*)&(m_EntryID.lpb) );
	memcpy( m_EntryID.lpb, pEntryId->lpb, m_EntryID.cb );
}

HRESULT MAPIFolder::GetItemCount(ULONG &ulCount)
{
	ulCount=0;
	if(m_folder ==NULL)
		throw MAPIFolderException(E_FAIL,L"GetItemCount(): Folder Object is NULL.",__LINE__,__FILE__);
	HRESULT hr=S_OK;
	Zimbra::Util::ScopedBuffer<SPropValue> pPropValues; 
	//Get PR_CONTENT_COUNT
	if(FAILED(hr=HrGetOneProp(m_folder,PR_CONTENT_COUNT,pPropValues.getptr())))
	{
		//if failed, try to read contents table and get count out of it.
		Zimbra::Util::ScopedInterface<IMAPITable> lpTable;
		if(FAILED(hr = m_folder->GetContentsTable( fMapiUnicode, lpTable.getptr())))
			throw MAPIFolderException(E_FAIL,L"GetItemCount(): GetContentsTable() Failed.",__LINE__,__FILE__); 
		
		if(FAILED(hr = lpTable->GetRowCount( 0, &ulCount )))
			throw MAPIFolderException(E_FAIL,L"GetItemCount(): GetRowCount() Failed.",__LINE__,__FILE__); 
	}
	else
	{
		ulCount=pPropValues->Value.ul;
	}

	return hr;
}

HRESULT MAPIFolder::GetFolderIterator( FolderIterator& folderIter )
{
	HRESULT hr =S_OK;
	if( m_folder == NULL )
		return MAPI_E_NOT_FOUND;

	LPMAPITABLE pTable = NULL;
	if(FAILED(hr = m_folder->GetHierarchyTable( fMapiUnicode, &pTable )))
		throw MAPIFolderException(E_FAIL,L"GetFolderIterator(): GetHierarchyTable Failed.",__LINE__,__FILE__);
/*
	SPropTagArray arrPropTags;
	arrPropTags.cValues=1;
	arrPropTags.aulPropTag[0]=PR_CONTAINER_CLASS;
	if(FAILED(hr = pTable->SetColumns( (LPSPropTagArray)&arrPropTags, 0 )))
	{

	}
	SPropValue spv;
	spv.dwAlignPad = 0;
	spv.ulPropTag = PR_CONTAINER_CLASS;
	spv.Value.LPSZ = L"ipm.note";

	SRestriction sr[3];
	//sr[0].rt = RES_OR;
	//sr[0].res.resOr.cRes = 2;
	//sr[0].res.resOr.lpRes = &sr[1];

		//sr[1].rt=RES_NOT;
		//sr[1].res.resNot.lpRes = &sr[4];

			//sr[1].rt= RES_EXIST;
			//sr[1].res.resExist.ulPropTag=PR_CONTAINER_CLASS_W;
/*	
	sr[2].rt= RES_AND;
	sr[2].res.resAnd.cRes = 2;
	sr[2].res.resAnd.lpRes = &sr[5];
		sr[5].rt = RES_EXIST;
		sr[5].res.resExist.ulPropTag=PR_CONTAINER_CLASS;
			sr[6].rt = RES_PROPERTY ;
			sr[6].res.resProperty.relop = RELOP_RE ;
			sr[6].res.resContent.ulFuzzyLevel = FL_IGNORECASE ;
			sr[6].res.resProperty.lpProp = &spv;

	sr[0].rt= RES_AND;
	sr[0].res.resAnd.cRes = 2;
	sr[0].res.resAnd.lpRes = &sr[1];
		sr[1].rt = RES_EXIST;
		sr[1].res.resExist.ulPropTag=PR_CONTAINER_CLASS;
			sr[2].rt = RES_PROPERTY ;
			sr[2].res.resProperty.relop = RELOP_RE ;
			sr[2].res.resContent.ulFuzzyLevel = FL_IGNORECASE ;
			sr[2].res.resProperty.lpProp = &spv;
	
	hr=pTable->Restrict(&sr[0],0);
*/
	folderIter.Initialize( pTable, m_folder );
	return S_OK;
}

HRESULT MAPIFolder::GetMessageIterator(MessageIterator &msgIterator)
{
	if( m_folder == NULL )
		throw MAPIFolderException(E_FAIL,L"GetMessageIterator(): Folder Object is NULL.",__LINE__,__FILE__);
	
	HRESULT hr = S_OK;
	LPMAPITABLE pContentsTable = NULL;
	if(FAILED(hr = m_folder->GetContentsTable( fMapiUnicode, &pContentsTable )))
		throw MAPIFolderException(E_FAIL,L"GetMessageIterator(): GetContentsTable Failed.",__LINE__,__FILE__);
	//Init message iterator
	msgIterator.Initialize(pContentsTable,m_folder);
	return S_OK;
}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//MessageIterator
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
 MessageIterator::MIRestriction MessageIterator::m_restriction;

MessageIterator::MessageIterPropTags MessageIterator::m_props = 
{	NMSGPROPS, {PR_ENTRYID,	PR_LONGTERM_ENTRYID_FROM_TABLE,	PR_CLIENT_SUBMIT_TIME, PR_MESSAGE_CLASS}
};

MessageIterator::MessageIterSortOrder MessageIterator::m_sortOrder = 
{
	1, 0, 0, { PR_MESSAGE_DELIVERY_TIME, TABLE_SORT_ASCEND }
};

MessageIterator::MessageIterator()
{

}

MessageIterator::~MessageIterator()
{

}

LPSPropTagArray MessageIterator::GetProps()
{
	return (LPSPropTagArray)&m_props;
}

LPSSortOrderSet MessageIterator::GetSortOrder()
{
	return (LPSSortOrderSet)&m_sortOrder;
}

LPSRestriction MessageIterator::GetRestriction(ULONG TypeMask, FILETIME startDate)
{
	return m_restriction.GetRestriction(TypeMask, startDate);
}

BOOL MessageIterator::GetNext( MAPIMessage& msg )
{
	SRow* pRow = MAPITableIterator::GetNext();

	if( pRow == NULL )
	{
		//msg.InternalFree();
		return FALSE;
	}
	
	LPMESSAGE pMessage = NULL;
    HRESULT hr = S_OK;
	ULONG objtype;
	ULONG cb = pRow->lpProps[MI_ENTRYID].Value.bin.cb;
	LPENTRYID peid = (LPENTRYID)(pRow->lpProps[MI_ENTRYID].Value.bin.lpb);

	if(FAILED(hr = m_pParentFolder->OpenEntry( cb, peid, NULL, MAPI_BEST_ACCESS, &objtype, (LPUNKNOWN*)&pMessage )))
		throw GenericException(hr,L"MessageIterator::GetNext():OpenEntry Failed.",__LINE__,__FILE__);
	msg.Initialize( pMessage );

	return TRUE;
}
BOOL MessageIterator::GetNext( __int64& date, SBinary& bin )
{
	UNREFERENCED_PARAMETER(date);
	UNREFERENCED_PARAMETER(bin);
	return false;
}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//MessageIterator::MIRestriction
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
Zimbra::MAPI::MessageIterator::MIRestriction::MIRestriction()
{
    //Task
	_pTaskClass = new WCHAR[10];
	wcscpy( _pTaskClass, L"ipm.task" );
	_propValTask.dwAlignPad = 0;
	_propValTask.ulPropTag = PR_MESSAGE_CLASS;
	_propValTask.Value.lpszW = _pTaskClass;

	//Appointment
	_pApptClass = new WCHAR[20];
	wcscpy( _pApptClass, L"ipm.appointment" );
	_propValAppt.dwAlignPad = 0;
	_propValAppt.ulPropTag = PR_MESSAGE_CLASS;
	_propValAppt.Value.lpszW = _pApptClass;

	//Meeting Request and responses
	_pReqAndResClass = new WCHAR[15] ;
	wcscpy( _pReqAndResClass, L"ipm.schedule" );
	_propValReqAndRes.dwAlignPad = 0;
	_propValReqAndRes.ulPropTag = PR_MESSAGE_CLASS;
	_propValReqAndRes.Value.lpszW = _pReqAndResClass;

    //Mails
	_pMailClass = new WCHAR[10];
	wcscpy( _pMailClass, L"ipm.note" );
	_propValMail.dwAlignPad = 0;
	_propValMail.ulPropTag = PR_MESSAGE_CLASS;
	_propValMail.Value.lpszW = _pMailClass;

    //Messages with Message class "IMP" were getting skipped. Bug 21064
    _propValCanbeMail.dwAlignPad = 0;
	_propValCanbeMail.ulPropTag = PR_MESSAGE_CLASS;
	_propValCanbeMail.Value.lpszW = L"ipm";

	//Messages with Message class "IPM.POST" were getting skipped. Bug 36277
    _propValCanbeMailPost.dwAlignPad = 0;
	_propValCanbeMailPost.ulPropTag = PR_MESSAGE_CLASS;
	_propValCanbeMailPost.Value.lpszW = L"ipm.post";

    //Distribution List
    _pDistListClass = new WCHAR[15];
	wcscpy( _pDistListClass, L"ipm.distlist" );
    _propValDistList.dwAlignPad = 0;
    _propValDistList.ulPropTag = PR_MESSAGE_CLASS;
    _propValDistList.Value.lpszW = _pDistListClass;

    //Contacts
	_pContactClass = new WCHAR[15];
	wcscpy( _pContactClass, L"ipm.contact" );
	_propValCont.dwAlignPad = 0;
	_propValCont.ulPropTag = PR_MESSAGE_CLASS;
	_propValCont.Value.lpszW = _pContactClass;

	_propValSTime.dwAlignPad = 0;
	_propValSTime.ulPropTag = PR_CLIENT_SUBMIT_TIME;
	_propValSTime.Value.ft.dwHighDateTime = 0;
	_propValSTime.Value.ft.dwLowDateTime = 0;

	_propValCTime.dwAlignPad = 0;
	_propValCTime.ulPropTag = PR_CREATION_TIME;
	_propValCTime.Value.ft.dwHighDateTime = 0;
	_propValCTime.Value.ft.dwLowDateTime = 0;
	
	//Property value structure for a named property which specifies 
	//that whether the mail is completely downloaded or not in case of IMAP
	//Being named property, Property tag is initialized with PR_NULL and 
	//needs to be set with appropriate value before use
	_propValIMAPHeaderOnly.dwAlignPad = 0 ;
	_propValIMAPHeaderOnly.ulPropTag = PR_NULL ;
	_propValIMAPHeaderOnly.Value.ul = 0 ;

	pR[0].rt = RES_AND;
	pR[0].res.resAnd.cRes = 2;
	pR[0].res.resAnd.lpRes = &pR[1];
		
		pR[1].rt = RES_OR;
		pR[1].res.resOr.cRes = 2;
		pR[1].res.resOr.lpRes = &pR[5];

			pR[5].rt = RES_AND;
			pR[5].res.resAnd.cRes = 2;
			pR[5].res.resAnd.lpRes = &pR[7];

				pR[7].rt = RES_EXIST;
				pR[7].res.resExist.ulPropTag = PR_CLIENT_SUBMIT_TIME;

				pR[8].rt = RES_PROPERTY;
				pR[8].res.resProperty.relop = RELOP_GE;
				pR[8].res.resProperty.ulPropTag = PR_CLIENT_SUBMIT_TIME;
				pR[8].res.resProperty.lpProp = &_propValSTime;

			pR[6].rt = RES_AND;
			pR[6].res.resAnd.cRes = 3;
			pR[6].res.resAnd.lpRes = &pR[9];

				pR[9].rt = RES_NOT;
				pR[9].res.resNot.lpRes = &pR[12];

					pR[12].rt = RES_EXIST;
					pR[12].res.resExist.ulPropTag = PR_CLIENT_SUBMIT_TIME;

				pR[10].rt = RES_EXIST;
				pR[10].res.resExist.ulPropTag = PR_CREATION_TIME;

				pR[11].rt = RES_PROPERTY;
				pR[11].res.resProperty.relop = RELOP_GE;
				pR[11].res.resProperty.ulPropTag = PR_CREATION_TIME;
				pR[11].res.resProperty.lpProp = &_propValCTime;

		pR[2].rt = RES_OR;
        pR[2].res.resOr.cRes = 7 ;
		pR[2].res.resOr.lpRes = &pR[13];

        //pR[13] will be set in GetRestriction
        //pR[14] will be set in GetRestriction
        //pR[15] will be set in GetRestriction
        //pR[16] will be set in GetRestriction
        //pR[17] will be set in GetRestriction
        //pR[18] will be set in GetRestriction
        //pR[19] will be set in GetRestriction

		//Restriction for selecting mails which are completely downloaded in case of IMAP
		pR[3].rt = RES_OR;
		pR[3].res.resOr.cRes = 2;
		pR[3].res.resOr.lpRes = &pR[20];

			//Either the property should not exit
			pR[20].rt = RES_NOT;
			pR[20].res.resNot.lpRes = &pR[4] ;

				pR[4].rt = RES_EXIST;
			  //pR[4].res.resExist.ulPropTag will be set in GetRestriction

			//if exists, it's value should be zero
			pR[21].rt = RES_AND;
			pR[21].res.resAnd.cRes = 2;
			pR[21].res.resAnd.lpRes = &pR[22];

				pR[22].rt = RES_EXIST;
			  //pR[22].res.resExist.ulPropTag will be set in GetRestriction

				pR[23].rt = RES_PROPERTY ;
				pR[23].res.resProperty.relop = RELOP_EQ ;
			  //pR[23].res.resProperty.ulPropTag will be set in GetRestriction
			  //pR[23].res.resProperty.lpProp will be set in GetRestriction
}

MessageIterator::MIRestriction::~MIRestriction()
{
	delete [] _pContactClass;
	delete [] _pMailClass;
	delete [] _pApptClass;
	delete [] _pReqAndResClass ;
    delete [] _pTaskClass ;
    delete [] _pDistListClass ;
}

LPSRestriction MessageIterator::MIRestriction::GetRestriction(ULONG TypeMask, FILETIME startDate)
{
    int iCounter = 13;
    int iNumRes = 0;

	if(TypeMask & ZCM_MAIL) //mail
    {
        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING ;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValMail;

        iCounter++;
        iNumRes++;

        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_FULLSTRING ;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValCanbeMail;

        iCounter++;
        iNumRes++;

		pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_FULLSTRING ;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValCanbeMailPost;

        iCounter++;
        iNumRes++;
    }
    if(TypeMask & ZCM_CONTACTS) 
    {
        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING ;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValCont;
        iCounter++;
        iNumRes++;

        pR[iCounter].rt = RES_CONTENT;
        pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING ;
        pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
        pR[iCounter].res.resContent.lpProp = &_propValDistList;
        iCounter++;
        iNumRes++;
    }

    //If we are using Outlook Object Model to extract calendar data, 
    //ImportApptments will always return TRUE but if we are using CDOEX 
    //to extract calendar data, and fail to retrieve base forder URL of Exchange Server,
    //ImportApptments will return FALSE
	if( true )
    {
        if(TypeMask & ZCM_TASKS)
        {
            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING ;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValTask;
            iCounter++;
            iNumRes++;
        }
		if(TypeMask & ZCM_APPOINTMENTS)
        {
            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING ;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValAppt;
            iCounter++;
            iNumRes++;

            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING ;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValReqAndRes;
            iCounter++;
            iNumRes++;
        }       
    }
    else
    {
        if(TypeMask & ZCM_APPOINTMENTS)
        {
            pR[iCounter].rt = RES_CONTENT;
            pR[iCounter].res.resContent.ulFuzzyLevel = FL_IGNORECASE | FL_SUBSTRING ;
            pR[iCounter].res.resContent.ulPropTag = PR_MESSAGE_CLASS;
            pR[iCounter].res.resContent.lpProp = &_propValReqAndRes;
            iCounter++;
            iNumRes++;
        }        
    }

    pR[2].res.resOr.cRes = iNumRes;
	ULONG ulIMAPHeaderInfoPropTag = g_ulIMAPHeaderInfoPropTag;
	if( _propValIMAPHeaderOnly.ulPropTag == PR_NULL )
	{
		_propValIMAPHeaderOnly.ulPropTag = ulIMAPHeaderInfoPropTag;
		pR[4].res.resExist.ulPropTag = pR[22].res.resExist.ulPropTag = _propValIMAPHeaderOnly.ulPropTag ;
		_propValIMAPHeaderOnly.Value.ul = 0 ;

		pR[23].res.resProperty.ulPropTag = ulIMAPHeaderInfoPropTag ;
		pR[23].res.resProperty.lpProp = &_propValIMAPHeaderOnly ;
	}
	
	bool bUseStartDate = false;
	bool bIgnoreBodyLessMessage = false;
	if( (bUseStartDate && (!(TypeMask & ZCM_CONTACTS))) && bIgnoreBodyLessMessage )
	{
		FILETIME& ft = startDate;
		_propValCTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
		_propValCTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;
		
		_propValSTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
		_propValSTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;

		if( ulIMAPHeaderInfoPropTag )
		{
			pR[0].res.resAnd.cRes = 3;
		}
		else
		{
			pR[0].res.resAnd.cRes = 2; 
		}
		pR[0].res.resAnd.lpRes = &pR[1];

		return &pR[0];
	}
    //Applying date restriction to messages other than contact    
	else if ( (bUseStartDate && (!(TypeMask & ZCM_CONTACTS))) && !bIgnoreBodyLessMessage )
	{
		FILETIME& ft = startDate;
		_propValCTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
		_propValCTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;

		_propValSTime.Value.ft.dwHighDateTime = ft.dwHighDateTime;
		_propValSTime.Value.ft.dwLowDateTime = ft.dwLowDateTime;

		pR[0].res.resAnd.cRes = 2;
		pR[0].res.resAnd.lpRes = &pR[1];

		return &pR[0];
	}
	else if ( !(bUseStartDate && (!(TypeMask & ZCM_CONTACTS))) && bIgnoreBodyLessMessage && ulIMAPHeaderInfoPropTag )
	{
		pR[0].res.resAnd.cRes = 2;
		pR[0].res.resAnd.lpRes = &pR[2];

		return &pR[0];
	}
	else
	{
		return &pR[2];
	}
}
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//MAPIMessage
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIMessage::MessagePropTags MAPIMessage::m_messagePropTags = 
{
	NMSGPROPS,
	{
		PR_MESSAGE_CLASS,				PR_MESSAGE_FLAGS,				PR_CLIENT_SUBMIT_TIME,
		PR_SENDER_ADDRTYPE,				PR_SENDER_EMAIL_ADDRESS,		PR_SENDER_NAME,
		PR_SENDER_ENTRYID,				PR_SUBJECT,						PR_BODY,
		PR_BODY_HTML,					PR_INTERNET_CPID,				PR_MESSAGE_CODEPAGE,
		PR_LAST_VERB_EXECUTED,			PR_FLAG_STATUS,					PR_ENTRYID,
		PR_SENT_REPRESENTING_ADDRTYPE,	PR_SENT_REPRESENTING_ENTRYID,	PR_SENT_REPRESENTING_EMAIL_ADDRESS,
		PR_SENT_REPRESENTING_NAME,		PR_REPLY_RECIPIENT_NAMES,		PR_REPLY_RECIPIENT_ENTRIES,
		PR_TRANSPORT_MESSAGE_HEADERS_A,	PR_IMPORTANCE,					PR_INTERNET_MESSAGE_ID_A,
		PR_MESSAGE_DELIVERY_TIME,		PR_URL_NAME,					PR_MESSAGE_SIZE,
        PR_STORE_SUPPORT_MASK,          PR_RTF_IN_SYNC
	}
};

MAPIMessage::RecipientPropTags MAPIMessage::m_recipientPropTags = 
{
	RNPROPS,
	{
		PR_DISPLAY_NAME, PR_ENTRYID, PR_ADDRTYPE, PR_EMAIL_ADDRESS, PR_RECIPIENT_TYPE
	}
};


MAPIMessage::ReplyToPropTags MAPIMessage::m_replyToPropTags = 
{
	NREPLYTOPROPS,
	{
		PR_DISPLAY_NAME, PR_ENTRYID, PR_ADDRTYPE, PR_EMAIL_ADDRESS
	}
};

MAPIMessage::MAPIMessage():m_pMessage(NULL), m_pMessagePropVals(NULL), m_pRecipientRows(NULL)
{

}

MAPIMessage::~MAPIMessage()
{
	InternalFree();
}

void MAPIMessage::Initialize(LPMESSAGE pMessage )
{
	HRESULT hr = S_OK;
	ULONG cVals = 0;
	LPMAPITABLE pRecipTable = NULL;

	__try
	{
		InternalFree();
		m_pMessage = pMessage;

		if(FAILED(hr = m_pMessage->GetProps( (LPSPropTagArray)&m_messagePropTags, fMapiUnicode, &cVals, &m_pMessagePropVals )))
			throw MAPIMessageException(E_FAIL,L"Initialize(): GetProps Failed.",__LINE__,__FILE__);

		if(FAILED(hr = m_pMessage->GetRecipientTable( fMapiUnicode, &pRecipTable )))
			throw MAPIMessageException(E_FAIL,L"Initialize(): GetRecipientTable Failed.",__LINE__,__FILE__);

		ULONG ulRecips = 0;
		if(FAILED(hr = pRecipTable->GetRowCount( 0, &ulRecips )))
			throw MAPIMessageException(E_FAIL,L"Initialize(): GetRowCount Failed.",__LINE__,__FILE__);

		if( ulRecips > 0 )
		{
			if(FAILED(hr = pRecipTable->SetColumns( (LPSPropTagArray)&m_recipientPropTags, 0 )))
				throw MAPIMessageException(E_FAIL,L"Initialize(): SetColumns Failed.",__LINE__,__FILE__);
			
			if(FAILED(hr = pRecipTable->QueryRows( ulRecips, 0, &m_pRecipientRows )))
				throw MAPIMessageException(E_FAIL,L"Initialize(): QueryRows Failed.",__LINE__,__FILE__);
		}
	}
	__finally
	{
		if( pRecipTable != NULL )
		{
			UlRelease(pRecipTable);
		}
	}
}

void MAPIMessage::InternalFree()
{
	if( m_pRecipientRows != NULL )
	{
		FreeProws(m_pRecipientRows);
		m_pRecipientRows = NULL;
	}
	if( m_pMessagePropVals != NULL )
	{
		MAPIFreeBuffer(m_pMessagePropVals);
		m_pMessagePropVals = NULL;
	}
	if( m_pMessage != NULL )
	{
		UlRelease(m_pMessage);
		m_pMessage = NULL;
	}
}

bool MAPIMessage::Subject(LPTSTR* ppSubject)
{
	if( PROP_TYPE(m_pMessagePropVals[SUBJECT].ulPropTag) != PT_ERROR )
	{
		int nLen = (int)_tcslen(m_pMessagePropVals[SUBJECT].Value.LPSZ);
		LPTSTR pSubject = m_pMessagePropVals[SUBJECT].Value.LPSZ;
		MAPIAllocateBuffer( (nLen + 1) * sizeof(TCHAR), (LPVOID*)ppSubject );
		ZeroMemory( *ppSubject, (nLen + 1) * sizeof(TCHAR) );
		_tcscpy( *ppSubject, pSubject );
		return true;
	}
	*ppSubject = NULL;
	return false;
}