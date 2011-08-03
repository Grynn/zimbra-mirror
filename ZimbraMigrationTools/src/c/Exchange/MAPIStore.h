#pragma once
#include "MAPICommon.h"

using namespace std;
namespace Zimbra {namespace MAPI {

class MAPIStoreException:public GenericException
{
public:
	MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription);
	MAPIStoreException(HRESULT hrErrCode, LPCWSTR lpszDescription,int nLine, LPCSTR strFile);
	virtual ~MAPIStoreException(){};
};

class MAPIFolder;
//Mapi Store class
class MAPIStore
{
private:
	LPMDB m_Store;
	LPMAPISESSION m_mapiSession;
public:
	MAPIStore();
	~MAPIStore();
	void Initialize(LPMAPISESSION mapisession, LPMDB pMdb);
	HRESULT CompareEntryIDs( SBinary* pBin1, SBinary* pBin2, ULONG &lpulResult);
	HRESULT GetRootFolder(MAPIFolder &rootFolder);
	LPMDB GetInternalMAPIStore(){return m_Store;}
};


}
}