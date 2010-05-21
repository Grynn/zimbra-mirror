package com.zimbra.bp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

import au.com.bytecode.opencsv.CSVWriter;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.localconfig.LocalConfig;
import com.zimbra.common.mailbox.ContactConstants;
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
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.cs.service.admin.AutoCompleteGal;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.service.admin.AdminFileDownload;
public class GenerateBulkProvisionFileFromLDAP extends AdminDocumentHandler {
	
	private static final String E_fileToken = "fileToken";
	private static final String E_ZCSImport = "ZCSImport";
	private static final String E_Options = "Options";
	private static final String E_ImportUsers = "ImportUsers";
	private static final String E_User = "User";
	private static final String E_ExchangeMail = "ExchangeMail";
	
	private static final String E_importMails = "importMails";
	private static final String E_importContacts = "importContacts";
	private static final String E_importTasks = "importTasks";
	private static final String E_importCalendar = "importCalendar";
	private static final String E_importDeletedItems = "importDeletedItems";
	private static final String E_importJunk = "importJunk";
	private static final String E_ignorePreviouslyImported = "ignorePreviouslyImported";
	private static final String E_InvalidSSLOk = "InvalidSSLOk";
	
	
	private static final int DEFAULT_PWD_LENGTH = 8;
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Map attrs = AdminService.getAttrs(request, true);
		String password = null;
		Element elPassword = request.getOptionalElement(ZimbraBulkProvisionExt.A_password);
		if(elPassword != null) {
			password = elPassword.getTextTrim();
		}
		String generatePwd = request.getElement(ZimbraBulkProvisionExt.A_generatePassword).getTextTrim();
		Element elPasswordLength = request.getOptionalElement(ZimbraBulkProvisionExt.A_genPasswordLength);
		String fileFormat = request.getElement(ZimbraBulkProvisionExt.A_fileFormat).getTextTrim();

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
		int maxResults = 0;
		Element elMaxResults = request.getOptionalElement(ZimbraBulkProvisionExt.A_maxResults);
		if(elMaxResults != null) {
			maxResults = Integer.parseInt(elMaxResults.getTextTrim());
		}
		GalParams.ExternalGalParams galParams = new GalParams.ExternalGalParams(attrs, GalOp.search);
		Element response = zsc.createElement(ZimbraBulkProvisionService.GENERATE_BULK_PROV_FROM_LDAP_RESPONSE);
		String fileToken = Double.toString(Math.random()*100);
        String[] galAttrs = Provisioning.getInstance().getConfig().getMultiAttr(Provisioning.A_zimbraGalLdapAttrMap);
        LdapGalMapRules rules = new LdapGalMapRules(galAttrs);
		try {
			SearchGalResult result = LdapUtil.searchLdapGal(galParams, GalOp.search, "*", maxResults, rules, null, null);
			List<GalContact> entries = result.getMatches();
            if (entries != null) {
            	String outFileName = null;
            	if(AdminFileDownload.FILE_FORMAT_BULK_CSV.equalsIgnoreCase(fileFormat)) {
            		outFileName = String.format("%s%s_bulk_%s_%s.csv", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);
            		FileOutputStream out = new FileOutputStream (outFileName) ;
            		CSVWriter writer = new CSVWriter(new OutputStreamWriter (out) ) ;	            	
	                for (GalContact entry : entries) {	
	                	String mail = entry.getSingleAttr(ContactConstants.A_email);
	                	if(mail == null)
	                		continue;
	                	
	                	String [] line = new String [3] ;
	                	line[0] = mail;
	                	line[1] = entry.getSingleAttr(ContactConstants.A_fullName);
	                	if(password != null) {
	                		line[2] = password;
	                	} else if(generatePwd.equalsIgnoreCase("true")) {
	                		line[2] = String.valueOf(GetBulkProvisionAccounts.generateStrongPassword(genPwdLength));
	                	}
	                	writer.writeNext(line);
	            	}
	                writer.close();
                } else if (AdminFileDownload.FILE_FORMAT_BULK_XML.equalsIgnoreCase(fileFormat)) {
                	outFileName = String.format("%s%s_bulk_%s_%s.xml", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);
                	FileWriter fileWriter = new FileWriter(outFileName);
                	XMLWriter xw = new XMLWriter(fileWriter, org.dom4j.io.OutputFormat.createPrettyPrint());
                    Document doc = DocumentHelper.createDocument();
                    org.dom4j.Element rootEl = DocumentHelper.createElement(E_ZCSImport);
                    org.dom4j.Element usersEl = DocumentHelper.createElement(E_ImportUsers);
                    doc.add(rootEl);
                    rootEl.add(usersEl);
                    for (GalContact entry : entries) {
                    	String email = entry.getSingleAttr(ContactConstants.A_email);
                    	if(email == null)
                    		continue;
                    	
                    	org.dom4j.Element eUser = DocumentHelper.createElement(E_User);
                    	org.dom4j.Element eName = DocumentHelper.createElement(AdminConstants.E_NAME);
                    	
                    	if(email != null) {
                    		eName.setText(email);
                    	}
                    	eUser.add(eName);
                    	
                    	org.dom4j.Element eDisplayName = DocumentHelper.createElement(Provisioning.A_displayName);
                    	String fullName = entry.getSingleAttr(ContactConstants.A_fullName);
                    	if(fullName != null) {
                    		eDisplayName.setText(fullName);
                    	}
                    	eUser.add(eDisplayName);
                    	
                    	org.dom4j.Element eGivenName = DocumentHelper.createElement(Provisioning.A_givenName);
                    	String firstName = entry.getSingleAttr(ContactConstants.A_firstName);
                    	if(firstName != null) {
                    		eGivenName.setText(firstName);
                    	}
                    	eUser.add(eGivenName);
                    	
                    	org.dom4j.Element eLastName = DocumentHelper.createElement(Provisioning.A_sn);
                    	String lastName = entry.getSingleAttr(ContactConstants.A_lastName);
                    	if(lastName != null) {
                    		eLastName.setText(lastName);
                    	}
                    	eUser.add(eLastName);
                    	org.dom4j.Element ePassword = DocumentHelper.createElement(ZimbraBulkProvisionExt.A_password);
	                	if(password != null) {
	                    	ePassword.setText(password);
	                	} else if(generatePwd.equalsIgnoreCase("true")) {
	                    	ePassword.setText(String.valueOf(GetBulkProvisionAccounts.generateStrongPassword(genPwdLength)));
	                	}   
	                	eUser.add(ePassword);
                        usersEl.add(eUser);
                    }                	
                    xw.write(doc);
                    xw.flush();
                } else if(AdminFileDownload.FILE_FORMAT_MIGRATION_XML.equalsIgnoreCase(fileFormat)) {
                	outFileName = String.format("%s%s_migration_%s_%s.xml", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);
                	FileWriter fileWriter = new FileWriter(outFileName);
                	XMLWriter xw = new XMLWriter(fileWriter, org.dom4j.io.OutputFormat.createPrettyPrint());
                    Document doc = DocumentHelper.createDocument();
                    org.dom4j.Element rootEl = DocumentHelper.createElement(E_ZCSImport);
                    doc.add(rootEl);
                    /**
                     * set Options section
                     */
                    org.dom4j.Element optionsEl = DocumentHelper.createElement(E_Options);
                    org.dom4j.Element importMailsEl = DocumentHelper.createElement(E_importMails);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_importMails).getTextTrim())) {
                    	importMailsEl.setText("1");
                    } else {
                    	importMailsEl.setText("0");
                    }
                	optionsEl.add(importMailsEl);
                    org.dom4j.Element importContactsEl = DocumentHelper.createElement(E_importContacts);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_importContacts).getTextTrim())) {
                    	importContactsEl.setText("1");
                    } else {
                    	importContactsEl.setText("0");
                    }
                	optionsEl.add(importContactsEl);                    
                    org.dom4j.Element importCalendarEl = DocumentHelper.createElement(E_importCalendar);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_importCalendar).getTextTrim())) {
                    	importCalendarEl.setText("1");
                    } else {
                    	importCalendarEl.setText("0");
                    }   
                    optionsEl.add(importCalendarEl);
                    org.dom4j.Element importTasksEl = DocumentHelper.createElement(E_importTasks);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_importTasks).getTextTrim())) {
                    	importTasksEl.setText("1");
                    } else {
                    	importTasksEl.setText("0");
                    }
                    optionsEl.add(importTasksEl);
                    org.dom4j.Element importJunkEl = DocumentHelper.createElement(E_importJunk);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_importJunk).getTextTrim())) {
                    	importJunkEl.setText("1");
                    } else {
                    	importJunkEl.setText("0");
                    }
                    optionsEl.add(importJunkEl);
                    org.dom4j.Element importDeletedItemsEl = DocumentHelper.createElement(E_importDeletedItems);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_importDeletedItems).getTextTrim())) {
                    	importDeletedItemsEl.setText("1");
                    } else {
                    	importDeletedItemsEl.setText("0");	
                    }
                    optionsEl.add(importDeletedItemsEl);
                    org.dom4j.Element ignorePreviouslyImportedEl = DocumentHelper.createElement(E_ignorePreviouslyImported);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_ignorePreviouslyImported).getTextTrim())) {
                    	ignorePreviouslyImportedEl.setText("1");
                    } else {
                    	ignorePreviouslyImportedEl.setText("0");
                    }
                    optionsEl.add(ignorePreviouslyImportedEl);
                    org.dom4j.Element InvalidSSLOkEl = DocumentHelper.createElement(E_InvalidSSLOk);
                    if("TRUE".equalsIgnoreCase(request.getElement(E_InvalidSSLOk).getTextTrim())) {
                    	InvalidSSLOkEl.setText("1");
                    } else {
                    	InvalidSSLOkEl.setText("0");
                    }
                    optionsEl.add(InvalidSSLOkEl);
                    
                    /**
                     * set ImportUsers section
                     */
                    org.dom4j.Element usersEl = DocumentHelper.createElement(E_ImportUsers);

                    rootEl.add(usersEl);
                    for (GalContact entry : entries) {
                    	String email = entry.getSingleAttr(ContactConstants.A_email);
                    	if(email == null)
                    		continue;
                    	
                    	org.dom4j.Element eUser = DocumentHelper.createElement(E_User);
                    	org.dom4j.Element eExchangeMail = DocumentHelper.createElement(E_ExchangeMail);
                    	eExchangeMail.setText(email);
                        usersEl.add(eUser);
                    }                	
                    xw.write(doc);
                    xw.flush();                	
                } else {
                	throw(ServiceException.INVALID_REQUEST("Wrong value for fileFormat parameter",null));
                }
                response.addElement(E_fileToken).setText(fileToken);
            }
		} catch (NamingException e) {
			throw ServiceException.FAILURE(e.getMessage(), e) ;
		} catch (IOException e) {
			throw ServiceException.FAILURE(e.getMessage(), e) ;
		} 
		return response;
	}
	
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
    	relatedRights.add(Admin.R_accessGAL);
    } 
}
