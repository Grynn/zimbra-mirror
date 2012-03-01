/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on Sep 23, 2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.ldaputils;

import java.util.Map;

import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.entry.LdapEntry;
import com.zimbra.cs.ldap.LdapException;
import com.zimbra.cs.ldap.ZAttributes;

/**
 * @author Greg Solovyev
 */
public class LDAPUtilEntry extends NamedEntry  implements LdapEntry {

    protected String mDn;
    
    LDAPUtilEntry(String dn, ZAttributes attrs, Map<String, Object> defaults) throws LdapException {
        super(attrs.getAttrString(Provisioning.A_cn),
                attrs.getAttrString(Provisioning.A_cn),
                attrs.getAttrs(), defaults, null);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }

}