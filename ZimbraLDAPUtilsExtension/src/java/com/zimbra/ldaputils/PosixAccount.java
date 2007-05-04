package com.zimbra.ldaputils;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.ldap.LdapUtil;
/**
 * @author Greg Solovyev
 */
public class PosixAccount extends LDAPEntry {
	private static final String A_uidNumber = "uidNumber";
	public PosixAccount(String dn, Attributes attrs,
			Map<String, Object> defaults) throws NamingException {
		super(dn, attrs, defaults);
        mId = LdapUtil.getAttrString(attrs, A_uidNumber);
	}

    public String getId() {
        return getAttr(A_uidNumber);
    }
}
