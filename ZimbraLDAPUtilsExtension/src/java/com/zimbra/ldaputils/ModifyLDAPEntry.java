package com.zimbra.ldaputils;


import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.DirContext;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ZimbraLog;

import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class ModifyLDAPEntry extends AdminDocumentHandler {

	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {

		ZimbraSoapContext lc = getZimbraSoapContext(context);
		DirContext ctxt = null;
		ctxt = LdapUtil.getDirContext(true);

		String dn = request.getAttribute(ZimbraLDAPUtilsService.E_DN);
		Map<String, Object> attrs = AdminService.getAttrs(request, true);

		try {
			LDAPEntry ne = GetLDAPEntries.getObjectByDN(dn, ctxt);
			LdapUtil.modifyAttrs(ctxt, ne.getDN(), attrs, ne);

			ZimbraLog.security.info(ZimbraLog.encodeAttrs(new String[] { "cmd",
					"SaveLDAPEntry", "dn", dn }, attrs));
			
			LDAPEntry newNe = GetLDAPEntries.getObjectByDN(dn, ctxt);
			Element response = lc
					.createElement(ZimbraLDAPUtilsService.MODIFY_LDAP_ENTRY_RESPONSE);
			ZimbraLDAPUtilsService.encodeLDAPEntry(response, newNe);

			return response;

		} catch (ServiceException e) {
            throw ServiceException.FAILURE("unable to modify attrs: "
                    + e.getMessage(), e);
		} catch (NamingException e) {
            throw ServiceException.FAILURE("unable to modify attrs: "
                    + e.getMessage(), e);
        } finally {
        	LdapUtil.closeContext(ctxt);
        }

	}
}
