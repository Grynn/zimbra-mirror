/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */

/*
 * Created on Sep 23, 2004
 *
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.zimbra.ldaputils;

import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.ldap.LdapUtil;
/**
 * @author Greg Solovyev
 */
public class LDAPEntry extends NamedEntry  {

    protected String mDn;

    LDAPEntry(String dn, Attributes attrs, Map<String, Object> defaults) throws NamingException {
        super(LdapUtil.getAttrString(attrs, Provisioning.A_cn),
        		LdapUtil.getAttrString(attrs, Provisioning.A_cn), 
                getAttrs(attrs), defaults);
        mDn = dn;
    }

    public String getDN() {
        return mDn;
    }
    
     /**
     * Enumerates over the specified attributes and populates the specified map. The key in the map is the
     * attribute ID. For attrs with a single value, the value is a String, and for attrs with multiple values
     * the value is an array of Strings.
     * 
     * @param attrs the attributes to enumerate over
     * @throws NamingException
     */
    static Map<String, Object> getAttrs(Attributes attrs) throws NamingException  {
        Map<String,Object> map = new HashMap<String,Object>();        
        for (NamingEnumeration ne = attrs.getAll(); ne.hasMore(); ) {
            Attribute attr = (Attribute) ne.next();
            if (attr.size() == 1) {
                Object o = attr.get();
                if (o instanceof String)
                    map.put(attr.getID(), o);
                else 
                    map.put(attr.getID(), new String((byte[])o));
            } else {
                String result[] = new String[attr.size()];
                for (int i=0; i < attr.size(); i++) {
                    Object o = attr.get(i);
                    if (o instanceof String)
                        result[i] = (String) o;
                    else 
                        result[i] = new String((byte[])o);
                }
                map.put(attr.getID(), result);
            }
        }
        return map;
    }    
}