package com.zimbra.ldaputils;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.ldap.LdapUtil;
/**
 * @author Greg Solovyev
 */
public class PosixGroup extends LDAPEntry {
	private static final String A_gidNumber = "gidNumber";

	public PosixGroup(String dn, Attributes attrs, Map<String, Object> defaults)
			throws NamingException {
		super(dn, attrs, defaults);
        mId = LdapUtil.getAttrString(attrs, A_gidNumber);		
	}

    public String getId() {
        return getAttr(A_gidNumber);
    }
}
