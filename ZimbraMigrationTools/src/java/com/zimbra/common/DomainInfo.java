/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite CSharp Client
 * Copyright (C) 2009, 2013 Zimbra Software, LLC.
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
package com.zimbra.common;

import java.util.ArrayList;

public class DomainInfo
{
    public String name;
    public String GalMode;
    public String GalMaxresults;
    public String Notes;
    public String Description;
    public String AuthMechanism;
    public String DoaminCosID;
    public String PublicServiceHostName;
    public String DomainStatus;
    public ArrayList VirtualHosts;
    public String GalLDAPUrl;
    public String GalLDAPSearchBase;
    public String GalLDAPBindDN;
    public String GalLDAPBindPwd;
    public String GalLDAPFilter;
    public String GalAutoCLDAPFilter;
    public String ZimbraLogOutUrl;
    public String ZimbraLoginUrl;
    public String zimbraDomainMaxAccounts;
    public String preAuthkey;
    public String zimbraAuthMech;
}

