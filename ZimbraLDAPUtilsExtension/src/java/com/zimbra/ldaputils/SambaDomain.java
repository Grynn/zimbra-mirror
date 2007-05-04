package com.zimbra.ldaputils;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

import com.zimbra.cs.account.NamedEntry;
import com.zimbra.cs.account.ldap.LdapUtil;
/**
 * @author Greg Solovyev
 */
public class SambaDomain extends LDAPEntry {

	private static final String A_sambaSID = "sambaSID";
	private static final String A_sambaDomainName = "sambaDomainName";	
	public SambaDomain(String dn, Attributes attrs, Map<String, Object> defaults)
			throws NamingException {
		super(dn, attrs, defaults);
        mName = LdapUtil.getAttrString(attrs, A_sambaSID);
        mId = LdapUtil.getAttrString(attrs, A_sambaDomainName);		
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
