/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
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
#include "common.h"
#include "Exchange.h"
#include "MAPIMessage.h"

// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
// MAPITableIterator
// >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
MAPITableIterator::MAPITableIterator(): m_pTable(NULL), m_pParentFolder(NULL), m_pRows(NULL),
    m_currRow(0), m_batchSize(200), m_rowsVisited(0), m_totalRows(0), m_session(NULL) {}

MAPITableIterator::~MAPITableIterator()
{
    if (m_pRows != NULL)
        FreeProws(m_pRows);
}

void MAPITableIterator::Initialize(LPMAPITABLE pTable, LPMAPIFOLDER pFolder,
    MAPISession &session, ULONG ulItemTypeMask)
{
	UNREFERENCED_PARAMETER(ulItemTypeMask);
    HRESULT hr = S_OK;

    m_session = &session;
    if (m_pParentFolder != NULL)
    {
        UlRelease(m_pParentFolder);
        m_pParentFolder = NULL;
    }
    if (m_pRows != NULL)
        FreeProws(m_pRows);
    m_pParentFolder = pFolder;
    m_pTable = pTable;

    hr = m_pTable->SetColumns(GetProps(), 0);
    if (FAILED(hr))
    {
        throw GenericException(hr, L"MAPITableIterator::Initialize():SetColumns Failed.",ERR_SET_RESTRICTION,
            __LINE__, __FILE__);
    }
    
    if (GetSortOrder() != NULL)
    {
        if (FAILED(hr = m_pTable->SortTable(GetSortOrder(), 0)))
        {
            throw GenericException(hr, L"MAPITableIterator::Initialize():SortTable Failed.",ERR_SET_RESTRICTION,
                __LINE__, __FILE__);
        }
    }
    if (FAILED(hr = m_pTable->GetRowCount(0, &m_totalRows)))
    {
        throw GenericException(hr, L"MAPITableIterator::Initialize():GetRowCount Failed.",ERR_SET_RESTRICTION,
            __LINE__, __FILE__);
    }
    if (FAILED(hr = m_pTable->QueryRows(m_batchSize, 0, &m_pRows)))
    {
        throw GenericException(hr, L"MAPITableIterator::Initialize():QueryRows Failed.",ERR_SET_RESTRICTION,
            __LINE__, __FILE__);
    }
}

SRow *MAPITableIterator::GetNext()
{
    HRESULT hr = S_OK;

    if (0 == m_totalRows)
        return NULL;
    if (m_currRow >= m_pRows->cRows)
    {
        if (m_rowsVisited >= m_totalRows)
            return NULL;
        FreeProws(m_pRows);
        m_pRows = NULL;
        if (m_totalRows - m_rowsVisited < m_batchSize)
        {
            hr = m_pTable->QueryRows((m_totalRows - m_rowsVisited), 0, &m_pRows);
            if (m_pRows && (m_pRows->cRows < (m_totalRows - m_rowsVisited)))
            {
                // "**Warning**: %d Table Rows Requested, Got just %d, hr = %d"), _totalRows - _rowsVisited, _pRows->cRows, hr
                m_rowsVisited = m_totalRows - m_pRows->cRows;
            }
        }
        else
        {
            hr = m_pTable->QueryRows(m_batchSize, 0, &m_pRows);
            if (m_pRows && (m_pRows->cRows < m_batchSize))
            {
                // "**Warning**: %d Table Rows Requested, Got just %d, hr = %d"), _batchSize, _pRows->cRows, hr
                m_rowsVisited += m_batchSize - m_pRows->cRows;
            }
        }
        if (FAILED(hr))
        {
            throw GenericException(hr, L"MAPITableIterator::GetNext():QueryRows Failed.",ERR_GET_NEXT,
                __LINE__, __FILE__);
        }
        m_currRow = 0;
    }
    if (!m_pRows->cRows)
        return NULL;

    SRow *pRow = &(m_pRows->aRow[m_currRow]);

    m_currRow++;
    m_rowsVisited++;

    return pRow;
}
