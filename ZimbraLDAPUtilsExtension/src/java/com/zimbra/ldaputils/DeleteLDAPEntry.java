package com.zimbra.ldaputils;

import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class DeleteLDAPEntry extends AdminDocumentHandler {

	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
		ZimbraSoapContext lc = getZimbraSoapContext(context);
		String dn = request.getAttribute(ZimbraLDAPUtilsService.E_DN);
		DirContext ctxt = null;
		try {
        	ctxt = LdapUtil.getDirContext(true);
            LdapUtil.deleteChildren(ctxt, dn);
            ctxt.unbind(dn);
    		Element response = lc.createElement(ZimbraLDAPUtilsService.DELETE_LDAP_ENTRY_RESPONSE);
    		return response;
            
        } catch (NamingException e) {
            throw ServiceException.FAILURE("unable to purge dn: "+dn, e);
        } finally {
            LdapUtil.closeContext(ctxt);
        }
	}

}
