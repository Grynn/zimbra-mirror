/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2012 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
// MapiAccessWrap.h : Declaration of the CMapiAccessWrap


#pragma once
#include "resource.h"
#include "OaIdl.h"
#include "Wtypes.h"
#include "Exchange_i.h"
#include "MAPIDefs.h"
#include "Exchange.h"
#include "ExchangeAdmin.h"
#include "MAPIAccessAPI.h"

#define NUM_ATTACHMENT_ATTRS      4
#define NUM_EXCEPTION_ATTRS      18

// CMapiAccessWrap

class ATL_NO_VTABLE CMapiAccessWrap :
	public CComObjectRootEx<CComMultiThreadModel>,
	public CComCoClass<CMapiAccessWrap, &CLSID_MapiAccessWrap>,
	public ISupportErrorInfo,
	public IDispatchImpl<IMapiAccessWrap, &IID_IMapiAccessWrap, &LIBID_Exchange, /*wMajor =*/ 1, /*wMinor =*/ 0>
{
public:
	CMapiAccessWrap()
	{
          maapi = NULL;
	}

DECLARE_REGISTRY_RESOURCEID(IDR_MAPIACCESSWRAP)


BEGIN_COM_MAP(CMapiAccessWrap)
	COM_INTERFACE_ENTRY(IMapiAccessWrap)
	COM_INTERFACE_ENTRY(IDispatch)
	COM_INTERFACE_ENTRY(ISupportErrorInfo)
END_COM_MAP()

// ISupportsErrorInfo
	STDMETHOD(InterfaceSupportsErrorInfo)(REFIID riid);


	DECLARE_PROTECT_FINAL_CONSTRUCT()

	HRESULT FinalConstruct()
	{
		return S_OK;
	}

	void FinalRelease()
	{
	}

public:
    Zimbra::MAPI::MAPIAccessAPI *maapi;

    STDMETHOD(UserInit) (BSTR userName, BSTR userAccount, BSTR *statusMsg);
    STDMETHOD(GetFolderList) (VARIANT * folders);
    STDMETHOD(GetItemsList) (IFolderObject * folderObj, VARIANT creationDate, VARIANT * vItems);
    STDMETHOD(GetData) (BSTR userId, VARIANT itemId, FolderType type, VARIANT * pVal);
    STDMETHOD(UserUninit) ();
    STDMETHODIMP GetOOOInfo(BSTR *OOOInfo);
    STDMETHODIMP GetRuleList(VARIANT *rules);
    void CreateAttachmentAttrs(BSTR attrs[], int num);
    void CreateExceptionAttrs(BSTR attrs[], int num);


};

OBJECT_ENTRY_AUTO(__uuidof(MapiAccessWrap), CMapiAccessWrap)
