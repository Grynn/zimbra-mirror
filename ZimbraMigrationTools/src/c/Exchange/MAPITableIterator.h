/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
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
