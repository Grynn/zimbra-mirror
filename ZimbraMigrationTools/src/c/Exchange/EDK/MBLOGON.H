/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2013 Zimbra Software, LLC.
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
// --MBLOGON.h------------------------------------------------------------------
// 
//  Header file for module containing message store logon/logoff functions.
// 
// Copyright 1986 - 1998 Microsoft Corporation.  All Rights Reserved.
// -----------------------------------------------------------------------------

#ifndef _MBLOGON_H
#define _MBLOGON_H

#ifdef __cplusplus
extern "C" {
#endif // __cplusplus

//$--HrMailboxLogon------------------------------------------------------
// -----------------------------------------------------------------------------
HRESULT HrMailboxLogon(
    IN  LPMAPISESSION   lplhSession,                // ptr to MAPI session handle
    IN  LPMDB           lpMDB,                      // ptr to message store
    IN  LPSTR           lpszMsgStoreDN,             // ptr to message store DN
    IN  LPSTR           lpszMailboxDN,              // ptr to mailbox DN
    OUT LPMDB           *lppMailboxMDB);            // ptr to mailbox message store ptr

//$--HrMailboxLogoff-------------------------------------------------------
// -----------------------------------------------------------------------------
HRESULT HrMailboxLogoff(
    IN OUT LPMDB *lppMailboxMDB);                   // mailbox message store pointer

#ifdef __cplusplus
}
#endif // __cplusplus

#endif // _MBLOGON_H
