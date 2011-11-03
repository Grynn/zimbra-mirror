#pragma once

namespace Zimbra
{
namespace MAPI
{
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
    MAPISession *m_session;

public:
    MAPITableIterator();
    virtual ~MAPITableIterator();

    virtual void Initialize(LPMAPITABLE pTable, LPMAPIFOLDER pFolder, MAPISession &session,
        ULONG ulItemTypeMask = ZCM_ALL);
    virtual LPSPropTagArray GetProps() = 0;
    virtual LPSSortOrderSet GetSortOrder() = 0;
    virtual LPSRestriction GetRestriction(ULONG TypeMask, FILETIME startDate) = 0;
    SRow *GetNext();
};
}
}
