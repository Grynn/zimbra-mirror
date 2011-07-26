#pragma once
#include "MAPICommon.h"

using namespace std;
namespace Zimbra {namespace MAPI {

class MAPITableIterator
{
protected:
	LPMAPIFOLDER m_pParentFolder;
	LPMAPITABLE m_pTable;
	LPSRowSet m_pRows;	
	ULONG m_currRow;		
	ULONG m_batchSize;	
	ULONG m_rowsVisited; 
	ULONG m_totalRows;	
public:
	MAPITableIterator();
	virtual ~MAPITableIterator();
	virtual void Initialize( LPMAPITABLE pTable, LPMAPIFOLDER pFolder );
	virtual LPSPropTagArray GetProps() = 0;
	virtual LPSSortOrderSet GetSortOrder() = 0;
	virtual LPSRestriction GetRestriction(int isContact = 0) = 0;
	SRow* GetNext();

};

}
}