package com.zimbra.bp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.GalContact;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.SearchGalResult;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.TargetType;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.gal.GalOp;
import com.zimbra.cs.account.gal.GalParams;
import com.zimbra.cs.account.ldap.LdapGalMapRules;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class ImportAccountsFromLDAP extends AdminDocumentHandler {

	public static final String A_password = "password" ;
	public static final String A_generatePassword = "generatePassword";
	public static final String A_genPasswordLength = "genPasswordLength";
	public static final String A_op = "op";
	public static final String E_status = "status";
	
	private static final int DEFAULT_PWD_LENGTH = 8;
	private static final String OP_GET_STATUS = "getStatus";
	private static final String OP_START_IMPORT = "startImport";
	private static final String OP_ABORT_IMPORT = "abortImport";
	
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Map attrs = AdminService.getAttrs(request, true);
		String op = request.getAttribute(A_op);
		Element response = zsc.createElement(ZimbraBulkProvisionService.IMPORT_ACCOUNTS_FROM_LDAP_RESPONSE);
		if(op.equalsIgnoreCase(OP_START_IMPORT)) {
			GalParams.ExternalGalParams galParams = new GalParams.ExternalGalParams(attrs, GalOp.search);
	        String[] galAttrs = Provisioning.getInstance().getConfig().getMultiAttr(Provisioning.A_zimbraGalLdapAttrMap);
	        LdapGalMapRules rules = new LdapGalMapRules(galAttrs);
			Element elPassword = request.getOptionalElement(A_password);
			Element elPasswordLength = request.getOptionalElement(A_genPasswordLength);
			String password = null;
			if(elPassword != null) {
				password = elPassword.getTextTrim();
			}
			String generatePwd = request.getElement(A_generatePassword).getTextTrim();
			int genPwdLength = 0;
			if(generatePwd == null) {
				generatePwd = "false";			
			} else if(generatePwd.equalsIgnoreCase("true")) {
				if(elPasswordLength != null) {
					genPwdLength = Integer.valueOf(elPasswordLength.getTextTrim());
				} else {
					genPwdLength = DEFAULT_PWD_LENGTH;
				}
				if(genPwdLength < 1) {
					genPwdLength = DEFAULT_PWD_LENGTH;
				}
			}		        
			try {
				SearchGalResult result = LdapUtil.searchLdapGal(galParams, GalOp.search, "*", 0, rules, null, null);
				List<GalContact> entries = result.getMatches();
				List<Map<String, Object>> sourceEntries = new ArrayList<Map<String, Object>>();
				Provisioning prov = Provisioning.getInstance();
				if (entries != null) {
	                for (GalContact entry : entries) {
	                	String emailAddress = entry.getSingleAttr(ContactConstants.A_email);
	                	if(emailAddress == null) {
	                		continue;
	                	}
	                	
	                	
	                	Account acct = prov.getAccountByName(emailAddress);
	                	if(acct != null) {
	                		continue;
	                	}
	                	checkDomainRightByEmail(zsc, entry.getSingleAttr(ContactConstants.A_email), Admin.R_createAccount);
	                	Map<String, Object> accAttrs = new HashMap<String, Object>();
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_givenName, entry.getSingleAttr(ContactConstants.A_firstName));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_displayName, entry.getSingleAttr(ContactConstants.A_fullName));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_sn, entry.getSingleAttr(ContactConstants.A_lastName));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_initials, entry.getSingleAttr(ContactConstants.A_initials));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_description, entry.getSingleAttr(ContactConstants.A_notes));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_telephoneNumber, entry.getSingleAttr(ContactConstants.A_homePhone));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_mobile, entry.getSingleAttr(ContactConstants.A_mobilePhone));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_homePhone, entry.getSingleAttr(ContactConstants.A_homePhone));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_l, entry.getSingleAttr(ContactConstants.A_homeCity));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_st, entry.getSingleAttr(ContactConstants.A_homeState));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_co, entry.getSingleAttr(ContactConstants.A_homeCountry));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_postalCode, entry.getSingleAttr(ContactConstants.A_homePostalCode));
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_street, entry.getSingleAttr(ContactConstants.A_homeAddress));			
	        			
	        			checkSetAttrsOnCreate(zsc, TargetType.account, emailAddress, accAttrs);

	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_mail, entry.getSingleAttr(ContactConstants.A_email));
	                	if(password != null) {
	                		StringUtil.addToMultiMap(accAttrs, Provisioning.A_userPassword, password);
	                	} else if(generatePwd.equalsIgnoreCase("true")) {
	                		StringUtil.addToMultiMap(accAttrs, Provisioning.A_userPassword, String.valueOf(GetBulkProvisionAccounts.generateStrongPassword(genPwdLength)));
	                	}
	        			
	                	sourceEntries.add(accAttrs);
	                }
	                if(sourceEntries.size()>0) {
	                	BulkProvisioningThread thread = BulkProvisioningThread.getThreadInstance(zsc.getAuthtokenAccountId(),true);
	                	int status = thread.getStatus();
	                	if(status == BulkProvisioningThread.iSTATUS_FINISHED) {
	                		BulkProvisioningThread.deleteThreadInstance(zsc.getAuthtokenAccountId());
	                		thread = BulkProvisioningThread.getThreadInstance(zsc.getAuthtokenAccountId(),true);
	                		status = thread.getStatus();
	                	}
	                	if(status != BulkProvisioningThread.iSTATUS_IDLE) {
	                		throw(BulkProvisionException.BP_IMPORT_ALREADY_RUNNING());
	                	}
	                	thread.setSourceAccounts(sourceEntries);
	                	thread.start();
	                	response.addElement(E_status).setText(Integer.toString(thread.getStatus()));
	                } else {
	                	throw(BulkProvisionException.BP_NO_ACCOUNTS_TO_IMPORT());
	                }
	            }
			} catch (NamingException e) {
				throw ServiceException.FAILURE("", e) ;
			} catch (IOException e) {
				throw ServiceException.FAILURE("", e) ;
			}
		} else if(op.equalsIgnoreCase(OP_ABORT_IMPORT)) {
			BulkProvisioningThread thread = BulkProvisioningThread.getThreadInstance(zsc.getAuthtokenAccountId(),false);
			if(thread != null) {
				thread.abort();
				response.addElement(E_status).setText(Integer.toString(thread.getStatus()));
			} else {
				response.addElement(E_status).setText("-1");
			}				
		} else if(op.equalsIgnoreCase(OP_GET_STATUS)) {
			BulkProvisioningThread thread = BulkProvisioningThread.getThreadInstance(zsc.getAuthtokenAccountId(),false);
			if(thread != null) {
				response.addElement(E_status).setText(Integer.toString(thread.getStatus()));
			} else {
				response.addElement(E_status).setText("-1");
			}			
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
