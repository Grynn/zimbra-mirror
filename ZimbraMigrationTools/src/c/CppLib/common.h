/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2006, 2007, 2009, 2010 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */
// Precompiled header file.

#pragma once

#ifndef _WIN32_WINNT
#define _WIN32_WINNT	0x0500
#endif
#ifndef WINVER
#define WINVER		0x0500
#endif

//to use the 2003 or later version of the 2000 EDK, install the 
//edk*.h files and change the following line to #define. the
//latest version (later than the 2003 EDK, even) is only available 
//in a 2004 Ex2000 rollup; extract the headers from that. See 
//KB870540 for (some) details.
#undef BUILD_WITH_EDK_2003

#define WIN32_LEAN_AND_MEAN		// Exclude rarely-used stuff from Windows headers

#include <initguid.h>

#define USES_IID_IMAPISession
#define USES_IID_IMAPITable
#define USES_IID_IMAPIAdviseSink
#define USES_IID_IMAPIProp
#define USES_IID_IProfSect
#define USES_IID_IMAPIStatus
#define USES_IID_IMsgStore
#define USES_IID_IMessage
#define USES_IID_IAttachment
#define USES_IID_IAddrBook
#define USES_IID_IMailUser
#define USES_IID_IMAPIContainer
#define USES_IID_IMAPIFolder
#define USES_IID_IABContainer
#define USES_IID_IDistList
#define USES_IID_IMAPISup
#define USES_IID_IMSProvider
#define USES_IID_IABProvider
#define USES_IID_IXPProvider
#define USES_IID_IMSLogon
#define USES_IID_IABLogon
#define USES_IID_IXPLogon
#define USES_IID_IMAPITableData
#define USES_IID_IMAPISpoolerInit
#define USES_IID_IMAPISpoolerSession
#define USES_IID_ITNEF
#define USES_IID_IMAPIPropData
#define USES_IID_IMAPIControl
#define USES_IID_IProfAdmin
#define USES_IID_IMsgServiceAdmin
#define USES_IID_IMAPISpoolerService
#define USES_IID_IMAPIProgress
#define USES_IID_ISpoolerHook
#define USES_IID_IMAPIViewContext
#define USES_IID_IMAPIFormMgr
#define USES_IID_IEnumMAPIFormProp
#define USES_IID_IMAPIFormInfo
#define USES_IID_IProviderAdmin
#define USES_IID_IMAPIForm
#define USES_IID_IPersistMessage
#define USES_IID_IMAPIViewAdviseSink
#define USES_IID_IStreamDocfile
#define USES_IID_IMAPIFormProp
#define USES_IID_IMAPIFormContainer
#define USES_IID_IMAPIFormAdviseSink
#define USES_IID_IStreamTnef
#define USES_IID_IMAPIFormFactory
#define USES_IID_IMAPIMessageSite



// Windows Header Files:
#include <windows.h>
#include <tchar.h>
#include <stdio.h>
#include <wincrypt.h>
#include <psapi.h>
#include <objbase.h>
#include <msxml2.h>
#include <comdef.h>

///for EWT support
#include <wmistr.h>
#include <evntrace.h>


// TODO: reference additional headers your program requires here
#include <mapispi.h>
#include <mapiutil.h>

#ifdef BUILD_WITH_EDK_2003
#include "edkmdb.h"
#include "edkguid.h"
#endif // BUILD_WITH_EDK_2003

//#include "..\Zimbra\ZimbraUtil.h"


//we need a provider specific tag to hold the location of the server
//we need a provider specific tag to hold the user that is logging on
#define BASE_PROVIDER_ID						0x6600  /* From MAPITAGS.H comments */

//address book provider properties as required by the ms ldap provider
#define PR_ZIMBRA_LDAP_SERVER_A					PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0000))
#define PR_ZIMBRA_LDAP_PORT_A					PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0001))
#define PR_ZIMBRA_LDAP_USER_A					PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0002))
#define PR_ZIMBRA_LDAP_SEARCH_BASE_A			PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0003))
#define PR_ZIMBRA_LDAP_SEARCH_QUERY_A			PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0004))
#define PR_ZIMBRA_LDAP_PROXY_PREFIX_A			PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0005))
#define PR_ZIMBRA_LDAP_MAIL_ATTRIBUTE_A			PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0006))
#define PR_ZIMBRA_LDAP_SEARCH_TIMEOUT_A			PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0007))
#define PR_ZIMBRA_LDAP_MAX_SEARCH_RESULTS_A		PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0008))
#define PR_ZIMBRA_LDAP_UNKNOWN_01_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0009))
#define PR_ZIMBRA_LDAP_UNKNOWN_02_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x000A))
#define PR_ZIMBRA_LDAP_UNKNOWN_03_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x000B))
#define PR_ZIMBRA_LDAP_UNKNOWN_04_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x000C))
#define PR_ZIMBRA_LDAP_UNKNOWN_05_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x000D))
#define PR_ZIMBRA_LDAP_UNKNOWN_06_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x000E))
#define PR_ZIMBRA_LDAP_UNKNOWN_07_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x000F))
#define PR_ZIMBRA_LDAP_UNKNOWN_08_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0010))
#define PR_ZIMBRA_LDAP_UNKNOWN_09_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0011))
#define PR_ZIMBRA_LDAP_UNKNOWN_10_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0012))
#define PR_ZIMBRA_LDAP_USE_SSL					PROP_TAG (PT_BOOLEAN,	(BASE_PROVIDER_ID + 0x0013))
#define PR_ZIMBRA_LDAP_USE_SPA					PROP_TAG (PT_BOOLEAN,	(BASE_PROVIDER_ID + 0x0015))
#define PR_ZIMBRA_LDAP_ENCRYPTED_PWD			PROP_TAG (PT_BINARY,	(BASE_PROVIDER_ID + 0x0017))
#define PR_ZIMBRA_LDAP_UNKNOWN_11_A				PROP_TAG (PT_BINARY,	(BASE_PROVIDER_ID + 0x0031))


//generic properties shared by multiple providers
#define PR_ZIMBRA_SERVER_NAME					PROP_TAG (PT_TSTRING,   (BASE_PROVIDER_ID + 0x0040))
#define PR_ZIMBRA_SERVER_NAME_A					PROP_TAG (PT_STRING8,   (BASE_PROVIDER_ID + 0x0040))
#define PR_ZIMBRA_SERVER_NAME_W					PROP_TAG (PT_UNICODE,   (BASE_PROVIDER_ID + 0x0040))
#define PR_ZIMBRA_EMAIL_ADDRESS					PROP_TAG (PT_TSTRING,   (BASE_PROVIDER_ID + 0x0041))
#define PR_ZIMBRA_EMAIL_ADDRESS_A				PROP_TAG (PT_STRING8,   (BASE_PROVIDER_ID + 0x0041))
#define PR_ZIMBRA_EMAIL_ADDRESS_W				PROP_TAG (PT_UNICODE,   (BASE_PROVIDER_ID + 0x0041))
#define PR_ZIMBRA_ENCRYPTED_PWD					PROP_TAG (PT_BINARY,	(BASE_PROVIDER_ID + 0x0042))
#define PR_ZIMBRA_SERVER_PORT					PROP_TAG (PT_LONG,		(BASE_PROVIDER_ID + 0x0043))
#define PR_ZIMBRA_USE_SECURE_CONXN				PROP_TAG (PT_BOOLEAN,	(BASE_PROVIDER_ID + 0x0044))
#define PR_ZIMBRA_DISPLAY_NAME					PROP_TAG (PT_TSTRING,	(BASE_PROVIDER_ID + 0x0045))
#define PR_ZIMBRA_DISPLAY_NAME_A				PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0045))
#define PR_ZIMBRA_DISPLAY_NAME_W				PROP_TAG (PT_UNICODE,	(BASE_PROVIDER_ID + 0x0045))
#define PR_ZIMBRA_FULL_EMAIL_ADDRESS			PROP_TAG (PT_TSTRING,	(BASE_PROVIDER_ID + 0x0046))
#define PR_ZIMBRA_FULL_EMAIL_ADDRESS_A			PROP_TAG (PT_STRING8,	(BASE_PROVIDER_ID + 0x0046))
#define PR_ZIMBRA_FULL_EMAIL_ADDRESS_W			PROP_TAG (PT_UNICODE,	(BASE_PROVIDER_ID + 0x0046))
#define PR_ZIMBRA_USER_ID						PROP_TAG (PT_BINARY,	(BASE_PROVIDER_ID + 0x0047))




//configure what gets traced and what doesnt
//trace AddRef/Release calls
#define BTRACE_REFCOUNT		0
#define BTRACE_SAX			0
#define BTRACE_GETPROPS		0
#define BTRACE_SETPROPS		0
#define BTRACE_CHANGES		1
#define BTRACE_IDMAP		0
#define BTRACE_MAPIALLOC	0
#define BTRACE_ROWS			0

#if BTRACE_REFCOUNT
#	define TRACE_REFCOUNT	TRACE
#else
#	define TRACE_REFCOUNT	__noop
#endif


#if BTRACE_SAX
#	define TRACE_SAX		TRACE
#else
#	define TRACE_SAX		__noop
#endif


#if BTRACE_ROWS
#   define TRACE_ROWS		DumpRows
#else
#   define TRACE_ROWS		__noop
#endif

#if BTRACE_GETPROPS
#	define TRACE_PROPS		DumpPropValues
#	define TRACE_PROP_TAGS	DumpPropTags
#else
#	define TRACE_PROPS		__noop
#	define TRACE_PROP_TAGS	__noop
#endif


#if BTRACE_SETPROPS
#	define TRACE_SETPROPS	DumpPropValues
#else
#	define TRACE_SETPROPS	__noop
#endif

#if BTRACE_CHANGES
#	define TRACE_CHANGE	TRACE
#else
#	define TRACE_CHANGE	__noop
#endif

#if BTRACE_IDMAP
#	define TRACE_IDMAP TRACE
#else
#	define TRACE_IDMAP __noop
#endif

#if BTRACE_MAPIALLOC
#	define TRACE_ALLOC TRACE
#else
#	define TRACE_ALLOC __noop
#endif

//stuff that needs to be moved to a mapi utility library
//TODO: OUTLOOK_NAME_PROPS_GUID_2 is PS_PUBLIC_STRINGS; scrap it and use that....
DEFINE_GUID(OUTLOOK_NAME_PROPS_GUID_1,		0x00062008, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x46);
DEFINE_GUID( ZIMBRA_NAME_PROPS_GUID_1,		0x20022104, 0x6842, 0x430d, 0xb1, 0x9c, 0x87, 0x39, 0xbf, 0xdb, 0x91, 0x88);
DEFINE_GUID(OUTLOOK_NAME_PROPS_GUID_2,		0x00020329, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x46);
DEFINE_GUID(IID_IMagicPst1,					0xd9f7aef8, 0x60b0, 0x11d3, 0x9a, 0x52, 0x00, 0x50, 0x04, 0x76, 0xd2, 0x3b);
DEFINE_GUID(OUTLOOK_MEETING_PROPERTIES,	    0x6ED8DA90, 0x450B, 0x101B, 0x98, 0xDA, 0x00, 0xAA, 0x00, 0x3F, 0x13, 0x05);
//DEFINE_GUID(OUTLOOK_APPT_PROPERTIES,	    0x00062002, 0x0000, 0x0000, 0xC0, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x46);

//a few more quasi-documented GUIDs that the EDK references but doesn't 
//define... could be added to ZimbraDebug, but don't bother
//DEFINE_GUID(OUTBOX_SYNC_EVENTS,				0xb2dc5b57, 0xaf2d, 0x4915, 0xba, 0xe3, 0x90, 0xe5, 0xbd, 0xfb, 0x00, 0x70);
//DEFINE_GUID(MTS_IN_SYNC_EVENTS,				0x2185ee91, 0x28cd, 0x4d9b, 0xbf, 0xb4, 0xbc, 0x49, 0xbb, 0x1d, 0xd8, 0xc0);
//DEFINE_GUID(MTS_OUT_SYNC_EVENTS,			0x1bdbafd3, 0x1384, 0x449b, 0xa2, 0x00, 0xde, 0x47, 0x45, 0xb0, 0x78, 0x39);
//DEFINE_GUID(TRANSPORT_TEMP_SYNC_EVENTS,		0x221ed74d, 0x0b5c, 0x4c0e, 0x88, 0x07, 0x23, 0xaf, 0xdd, 0x8a, 0xc2, 0xff);

#define __STR2__(x) #x
#define __STR1__(x) __STR2__(x)
#define __LOC__ __FILE__ "("__STR1__(__LINE__)") : "