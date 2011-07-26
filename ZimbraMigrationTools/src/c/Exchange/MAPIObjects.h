#pragma once
#include "MAPICommon.h"
#include "MAPITableIterator.h"

using namespace std;
namespace Zimbra {namespace MAPI {

class MAPIFolderException:public GenericException
{
public:
	MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	MAPIFolderException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~MAPIFolderException(){};
};

class MAPIFolder;
//Folder Iterator class
class FolderIterator:public MAPITableIterator
{
private:
	typedef enum _FolderIterPropTagIdx{ FI_DISPLAY_NAME, FI_ENTRYID, FI_PR_LONGTERM_ENTRYID_FROM_TABLE, FI_FLAGS, NFOLDERPROPS } FolerIterPropTagIdx;
	typedef struct _FolderIterPropTags
	{
		ULONG cValues;
		ULONG aulPropTags[NFOLDERPROPS];
	} FolderIterPropTags ;
protected:
	static FolderIterPropTags m_props;
public:
	FolderIterator();
	~FolderIterator();
	virtual LPSPropTagArray GetProps();
	virtual LPSSortOrderSet GetSortOrder(){return NULL;}
	virtual LPSRestriction GetRestriction(int isContact = 0){UNREFERENCED_PARAMETER(isContact); return NULL;}
	BOOL GetNext( MAPIFolder& folder );
};

class MAPIMessage;
//Message Iterator class
class MessageIterator:public MAPITableIterator
{
private:
	typedef enum _MessageIterPropTagIdx{ MI_ENTRYID, MI_DATE, NMSGPROPS } MessageIterPropTagIdx;
	typedef struct _MessageIterPropTags
	{
		ULONG cValues;
		ULONG aulPropTags[NMSGPROPS];
	} MessageIterPropTags ;

	typedef struct _MessageIterSort
	{
		ULONG cSorts;
		ULONG cCategories;
		ULONG cExpanded;
		SSortOrder aSort[1];
	}MessageIterSortOrder;
public:
	MessageIterator();
	virtual ~MessageIterator();
	virtual LPSPropTagArray GetProps();
	virtual LPSSortOrderSet GetSortOrder();
	virtual LPSRestriction GetRestriction(int isContact = 0);
	BOOL GetNext( MAPIMessage& msg );
	BOOL GetNext( __int64& date, SBinary& bin );

protected:
	static MessageIterPropTags  _props;
	static MessageIterSortOrder _sortOrder;
	//static MessageIterator::MIRestriction _restriction;
};

class MAPIMessage
{

};

//MapiFolder class
class MAPIFolder
{
private:
	LPMAPIFOLDER m_folder;
	wstring m_displayname;
	SBinary m_EntryID;
public:
	MAPIFolder();
	~MAPIFolder();
	MAPIFolder(const MAPIFolder& folder);
	void Initialize(LPMAPIFOLDER pFolder, LPTSTR displayName, LPSBinary pEntryId);
	HRESULT GetItemCount(ULONG &ulCount);
	HRESULT GetMessageIterator(MessageIterator &msgIterator);
	HRESULT GetFolderIterator( FolderIterator& folderIter );
	wstring Name() { return m_displayname;}
};

} //namespace MAPI

}//namespace Zimbra