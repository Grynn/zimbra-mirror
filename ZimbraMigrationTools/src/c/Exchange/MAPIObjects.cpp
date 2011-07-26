#include "MAPIObjects.h"
#include "MapiUtils.h"
using namespace Zimbra::MAPI;

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//Exception class
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription):GenericException(hrErrCode,lpszDescription)
{
	//
}

MAPIFolderException::MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile):GenericException(hrErrCode,lpszDescription,nLine,strFile)
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

	if(FAILED(hr = m_pParentFolder->OpenEntry( cb, peid, NULL, MAPI_BEST_ACCESS, &objtype, (LPUNKNOWN*)&pFolder )))
		throw GenericException(hr,L"FolderIterator::GetNext():OpenEntry Failed.",__LINE__,__FILE__);

	folder.Initialize( pFolder, pRow->lpProps[FI_DISPLAY_NAME].Value.LPSZ, &(pRow->lpProps[FI_ENTRYID].Value.bin) );

	return TRUE;
}

//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
//MessageIterator
//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MessageIterator::MessageIterator()
{

}

MessageIterator::~MessageIterator()
{

}

LPSPropTagArray MessageIterator::GetProps()
{
	return NULL;
}

LPSSortOrderSet MessageIterator::GetSortOrder()
{
	return NULL;
}

LPSRestriction MessageIterator::GetRestriction(int isContact)
{
	UNREFERENCED_PARAMETER(isContact);
	return NULL;
}

BOOL MessageIterator::GetNext( MAPIMessage& msg )
{
	UNREFERENCED_PARAMETER(msg);
	return false;
}
BOOL MessageIterator::GetNext( __int64& date, SBinary& bin )
{
	UNREFERENCED_PARAMETER(date);
	UNREFERENCED_PARAMETER(bin);
	return false;
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
	UNREFERENCED_PARAMETER( msgIterator);
	return S_OK;
}