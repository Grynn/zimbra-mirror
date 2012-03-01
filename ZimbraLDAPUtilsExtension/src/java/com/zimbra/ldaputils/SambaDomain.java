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
package com.zimbra.ldaputils;

import java.util.Map;

import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.ldap.LdapException;
import com.zimbra.cs.ldap.ZAttributes;
/**
 * @author Greg Solovyev
 */
public class SambaDomain extends LDAPUtilEntry {

	private static final String A_sambaSID = "sambaSID";
	private static final String A_sambaDomainName = "sambaDomainName";	
	
	public SambaDomain(String dn, ZAttributes attrs, Map<String, Object> defaults) 
	throws LdapException {
        super(dn, attrs, defaults);
        mName = attrs.getAttrString(A_sambaSID);
        mId = attrs.getAttrString(A_sambaDomainName);
    }

    public String getId() {
        return getAttr(A_sambaSID);
    }

    public String getName() {
        return getAttr(A_sambaDomainName);
    }

    public int compareTo(Object obj) {
        if (!(obj instanceof NamedEntry))
            return 0;
        NamedEntry other = (NamedEntry) obj;
        return getName().compareTo(other.getName());
    }
}
