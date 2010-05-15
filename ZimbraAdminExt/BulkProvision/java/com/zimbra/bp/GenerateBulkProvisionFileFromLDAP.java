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

public class GenerateBulkProvisionFileFromLDAP extends AdminDocumentHandler {
	
	public static final String A_password = "password" ;
	public static final String A_generatePassword = "generatePassword";
	public static final String A_genPasswordLength = "genPasswordLength";
	public static final String A_fileFormat = "fileFormat";
	
	private static final String E_filePath = "filePath";
	private static final String E_ZCSImport = "ZCSImport";
	private static final String E_ImportUsers = "ImportUsers";
	private static final String E_User = "User";
	
	private static final int DEFAULT_PWD_LENGTH = 8;
	private static final String FILE_FORMAT_MIGRATION_XML = "migrationxml";
	private static final String FILE_FORMAT_BULK_XML = "bulkxml";
	private static final String FILE_FORMAT_BULK_CSV = "csv";
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Map attrs = AdminService.getAttrs(request, true);
		String password = null;
		Element elPassword = request.getOptionalElement(A_password);
		if(elPassword != null) {
			password = elPassword.getTextTrim();
		}
		String generatePwd = request.getElement(A_generatePassword).getTextTrim();
		Element elPasswordLength = request.getOptionalElement(A_genPasswordLength);
		String fileFormat = request.getElement(A_fileFormat).getTextTrim();
		if(fileFormat == null || !(fileFormat.equalsIgnoreCase(FILE_FORMAT_BULK_CSV) || fileFormat.equalsIgnoreCase(FILE_FORMAT_BULK_XML))) {
			fileFormat = FILE_FORMAT_BULK_CSV;
		}
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
		
		GalParams.ExternalGalParams galParams = new GalParams.ExternalGalParams(attrs, GalOp.search);
		Element response = zsc.createElement(ZimbraBulkProvisionService.GENERATE_BULK_PROV_FROM_LDAP_RESPONSE);
		String fileToken = Double.toString(Math.random()*100);
        String[] galAttrs = Provisioning.getInstance().getConfig().getMultiAttr(Provisioning.A_zimbraGalLdapAttrMap);
        LdapGalMapRules rules = new LdapGalMapRules(galAttrs);
		try {
			SearchGalResult result = LdapUtil.searchLdapGal(galParams, GalOp.search, "*", 0, rules, null, null);
			List<GalContact> entries = result.getMatches();
            if (entries != null) {
            	String outFileName = null;
            	if(fileFormat.equalsIgnoreCase(FILE_FORMAT_BULK_CSV)) {
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
	                writer.close();	                	//AutoCompleteGal.addContact(response, entry);
                } else if (fileFormat.equalsIgnoreCase(FILE_FORMAT_BULK_XML)) {
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
                    	org.dom4j.Element ePassword = DocumentHelper.createElement(A_password);
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
                }            	
                response.addElement(E_filePath).setText(outFileName);
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
