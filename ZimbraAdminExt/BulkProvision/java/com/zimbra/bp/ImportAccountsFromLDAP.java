package com.zimbra.bp;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.GalContact;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.SearchGalResult;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.gal.GalOp;
import com.zimbra.cs.account.gal.GalParams;
import com.zimbra.cs.account.ldap.LdapGalMapRules;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.gal.GalSearchParams;
import com.zimbra.cs.gal.GalSearchConfig.GalType;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.cs.service.admin.AutoCompleteGal;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class ImportAccountsFromLDAP extends AdminDocumentHandler {

	@Override
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Map attrs = AdminService.getAttrs(request, true);
		GalParams.ExternalGalParams galParams = new GalParams.ExternalGalParams(attrs, GalOp.search);
		Element response = zsc.createElement(ZimbraBulkProvisionService.IMPORT_ACCOUNTS_FROM_LDAP_RESPONSE);
        String[] galAttrs = Provisioning.getInstance().getConfig().getMultiAttr(Provisioning.A_zimbraGalLdapAttrMap);
        LdapGalMapRules rules = new LdapGalMapRules(galAttrs);
		try {
			SearchGalResult result = LdapUtil.searchLdapGal(galParams, GalOp.search, "*", 0, rules, null, null);
			List<GalContact> entries = result.getMatches();
            if (entries != null) {
                for (GalContact entry : entries) {
                	AutoCompleteGal.addContact(response, entry);
                }
            }
		} catch (NamingException e) {
			throw ServiceException.FAILURE("", e) ;
		} catch (IOException e) {
			throw ServiceException.FAILURE("", e) ;
		} 
		return response;
	}
	
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_createAccount);
        relatedRights.add(Admin.R_listAccount);
        
        notes.add("Only accounts on which the authed admin has " + Admin.R_listAccount.getName() +
                " right will be provisioned.");
    }    

}
