package com.zimbra.bp;

import java.io.File;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.io.XMLWriter;

import au.com.bytecode.opencsv.CSVWriter;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminExtConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.EmailUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.GalContact;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.SearchGalResult;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.gal.GalOp;
import com.zimbra.cs.account.gal.GalParams;
import com.zimbra.cs.account.ldap.LdapGalMapRules;
import com.zimbra.cs.account.ldap.LdapGalSearch;
import com.zimbra.cs.ldap.LdapException;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.service.admin.AdminFileDownload;
public class GenerateBulkProvisionFileFromLDAP extends AdminDocumentHandler {

    private static final String E_Options = "Options";
    private static final String E_ZimbraServer = "ZimbraServer";
    private static final String E_profile = "profile";
    private static final String E_server = "server";
    private static final String E_UserProvision = "UserProvision";
    private static final String E_domain = "domain";
    private static final String E_logonUserDN = "logonUserDN";
    private static final String E_password = "password";

    private static final String FILE_FORMAT_PREVIEW = "preview";
    private static final int DEFAULT_PWD_LENGTH = 8;

    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
	ZimbraSoapContext zsc = getZimbraSoapContext(context);
	Map<String,Object> attrs = AdminService.getAttrs(request, true);
	String password = null;
	Element elPassword = request
		.getOptionalElement(AdminExtConstants.A_password);
	if (elPassword != null) {
	    password = elPassword.getTextTrim();
	}
	String generatePwd = request.getElement(AdminExtConstants.A_generatePassword).getTextTrim();
	Element elPasswordLength = request.getOptionalElement(AdminExtConstants.A_genPasswordLength);
	String fileFormat = request.getElement(AdminExtConstants.A_fileFormat).getTextTrim();
	String mustChangePassword = request.getElement(AdminExtConstants.E_mustChangePassword).getTextTrim();
		
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
		Element elMaxResults = request.getOptionalElement(AdminExtConstants.A_maxResults);
		if(elMaxResults != null) {
			maxResults = Integer.parseInt(elMaxResults.getTextTrim());
		}
		GalParams.ExternalGalParams galParams = new GalParams.ExternalGalParams(attrs, GalOp.search);
		Element response = zsc.createElement(AdminExtConstants.GENERATE_BULK_PROV_FROM_LDAP_RESPONSE);
		String fileToken = Double.toString(Math.random()*100);
        LdapGalMapRules rules = new LdapGalMapRules(Provisioning.getInstance().getConfig(), true);
		try {
			SearchGalResult result = LdapGalSearch.searchLdapGal(galParams, GalOp.search, "*", maxResults, rules, null, null);
            
			List<GalContact> entries = result.getMatches();
			int totalAccounts = 0;
			int totalDomains = 0;
			int totalExistingDomains = 0;
			int totalExistingAccounts = 0;
			List<String> domainList = new ArrayList<String>();
            if (entries != null) {
        	String outFileName = null;
            	if(FILE_FORMAT_PREVIEW.equalsIgnoreCase(fileFormat)) {
                    String SMTPHost = "";
                    String SMTPPort = "";
                    
                    Element eSMTPHost = request.getOptionalElement(AdminExtConstants.E_SMTPHost);
                    if(eSMTPHost != null) {
                        SMTPHost = eSMTPHost.getTextTrim();
                    }
                    Element eSMTPPort = request.getOptionalElement(AdminExtConstants.E_SMTPPort);
                    if(eSMTPPort != null) {
                        SMTPPort = eSMTPPort.getTextTrim();
                    }
	                for (GalContact entry : entries) {	
	                    String mail = entry.getSingleAttr(ContactConstants.A_email);
	                    if(mail == null)
	                	continue;
	                	
	                    String parts[] = EmailUtil.getLocalPartAndDomain(mail);
	                    if (parts == null)
	                        continue;

	                    if(!domainList.contains(parts[1])) {
                    		totalDomains++;
                    		//Check if this domain is in Zimbra
                    		Domain domain = Provisioning.getInstance().getDomainByName(parts[1]);
                    		if(domain != null) {
                    		    totalExistingDomains++;
                    		}
                    		domainList.add(parts[1]);
	                    }
	                    totalAccounts++;
	                    Account acct = Provisioning.getInstance().getAccountByName(mail);
	                    if(acct!=null) {
	                	totalExistingAccounts++;
	                    }	
	            	}
	                response.addElement(AdminExtConstants.E_totalCount).setText(Integer.toString(totalAccounts));
	                response.addElement(AdminExtConstants.E_domainCount).setText(Integer.toString(totalDomains));
	                response.addElement(AdminExtConstants.E_skippedAccountCount).setText(Integer.toString(totalExistingAccounts));
	                response.addElement(AdminExtConstants.E_skippedDomainCount).setText(Integer.toString(totalExistingDomains));
	                response.addElement(AdminExtConstants.E_SMTPHost).setText(SMTPHost);
	                response.addElement(AdminExtConstants.E_SMTPPort).setText(SMTPPort);	                
	                return response;
            	} else if(AdminFileDownload.FILE_FORMAT_BULK_CSV.equalsIgnoreCase(fileFormat)) {
            		outFileName = String.format("%s%s_bulk_%s_%s.csv", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);
            		FileOutputStream out = null;
            		CSVWriter writer = null;	            	
            		try {
	            		out = new FileOutputStream (outFileName) ;
	            		writer = new CSVWriter(new OutputStreamWriter (out) ) ;	            	
		                for (GalContact entry : entries) {	
		                	String mail = entry.getSingleAttr(ContactConstants.A_email);
		                	if(mail == null)
		                		continue;
		                	
		                	String [] line = new String [6] ;
		                	line[0] = mail;
		                	line[1] = entry.getSingleAttr(ContactConstants.A_fullName);
		                	line[2] = entry.getSingleAttr(ContactConstants.A_firstName);
		                	line[3] = entry.getSingleAttr(ContactConstants.A_lastName);
		                	if(password != null) {
		                		line[4] = password;
		                	} else if(generatePwd.equalsIgnoreCase("true")) {
		                		line[4] = String.valueOf(BulkImportAccounts.generateStrongPassword(genPwdLength));
		                	}
		                	
		                	line[5] = mustChangePassword;
		                	writer.writeNext(line);
		            	}
		                writer.close();
            		} catch (IOException e) {
		        			throw ServiceException.FAILURE(e.getMessage(), e);
		        	} finally {
		        		if(writer != null) {
		        			try {
		        				writer.close();
		        			} catch (IOException ignore) {}
		        		}
		        		if(out != null) {
		        			try {
		        				out.close();
		        			} catch (IOException ignore) {}
		        		}
		        	}
                } else if (AdminFileDownload.FILE_FORMAT_BULK_XML.equalsIgnoreCase(fileFormat)) {
                	outFileName = String.format("%s%s_bulk_%s_%s.xml", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);
                	FileWriter fileWriter = null;
                	XMLWriter xw = null;
                	try {
	                	fileWriter = new FileWriter(outFileName);
	                	xw = new XMLWriter(fileWriter, org.dom4j.io.OutputFormat.createPrettyPrint());
	                    Document doc = DocumentHelper.createDocument();
	                    org.dom4j.Element rootEl = DocumentHelper.createElement(AdminExtConstants.E_ZCSImport);
	                    org.dom4j.Element usersEl = DocumentHelper.createElement(AdminExtConstants.E_ImportUsers);
	                    doc.add(rootEl);
	                    rootEl.add(usersEl);
	                    for (GalContact entry : entries) {
	                    	String email = entry.getSingleAttr(ContactConstants.A_email);
	                    	if(email == null)
	                    		continue;
	                    	
	                    	org.dom4j.Element eUser = DocumentHelper.createElement(AdminExtConstants.E_User);
	                    	org.dom4j.Element eName = DocumentHelper.createElement(AdminExtConstants.E_ExchangeMail);
	                    	
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
	                    	org.dom4j.Element ePassword = DocumentHelper.createElement(AdminExtConstants.A_password);
		                	if(password != null) {
		                    	ePassword.setText(password);
		                	} else if(generatePwd.equalsIgnoreCase("true")) {
		                    	ePassword.setText(String.valueOf(BulkImportAccounts.generateStrongPassword(genPwdLength)));
		                	}   
		                	eUser.add(ePassword);
		                	
		                	org.dom4j.Element elMustChangePassword = DocumentHelper.createElement(Provisioning.A_zimbraPasswordMustChange);
		                	elMustChangePassword.setText(mustChangePassword);
		                	eUser.add(elMustChangePassword);
		                	
	                        usersEl.add(eUser);
	                    }                	
	                    xw.write(doc);
	                    xw.flush();
                	} catch (IOException e) {
		        			throw ServiceException.FAILURE(e.getMessage(), e);
		        	} finally {
		        		if(xw != null) {
		        			try {
		        				xw.close();
		        			} catch (IOException ignore) {}
		        		}
		        		if(fileWriter != null) {
		        			try {
		        				fileWriter.close();
		        			} catch (IOException ignore) {}
		        		}
		        	}
                } else if(AdminFileDownload.FILE_FORMAT_MIGRATION_XML.equalsIgnoreCase(fileFormat)) {
                	outFileName = String.format("%s%s_migration_%s_%s.xml", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);
                	FileWriter fileWriter = null;
                	XMLWriter xw = null;
                	try {
	                	fileWriter = new FileWriter(outFileName);
	                	xw = new XMLWriter(fileWriter, org.dom4j.io.OutputFormat.createPrettyPrint());
	                    Document doc = DocumentHelper.createDocument();
	                    org.dom4j.Element rootEl = DocumentHelper.createElement(AdminExtConstants.E_ZCSImport);
	                    doc.add(rootEl);
	                    /**
	                     * set Options section
	                     */
	                    org.dom4j.Element optionsEl = DocumentHelper.createElement(E_Options);
	                    org.dom4j.Element importMailsEl = DocumentHelper.createElement(AdminExtConstants.E_importMails);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_importMails).getTextTrim())) {
	                    	importMailsEl.setText("1");
	                    } else {
	                    	importMailsEl.setText("0");
	                    }
	                	optionsEl.add(importMailsEl);
	                	rootEl.add(optionsEl);
	                    org.dom4j.Element importContactsEl = DocumentHelper.createElement(AdminExtConstants.E_importContacts);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_importContacts).getTextTrim())) {
	                    	importContactsEl.setText("1");
	                    } else {
	                    	importContactsEl.setText("0");
	                    }
	                	optionsEl.add(importContactsEl);                    
	                    org.dom4j.Element importCalendarEl = DocumentHelper.createElement(AdminExtConstants.E_importCalendar);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_importCalendar).getTextTrim())) {
	                    	importCalendarEl.setText("1");
	                    } else {
	                    	importCalendarEl.setText("0");
	                    }   
	                    optionsEl.add(importCalendarEl);
	                    org.dom4j.Element importTasksEl = DocumentHelper.createElement(AdminExtConstants.E_importTasks);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_importTasks).getTextTrim())) {
	                    	importTasksEl.setText("1");
	                    } else {
	                    	importTasksEl.setText("0");
	                    }
	                    optionsEl.add(importTasksEl);
	                    org.dom4j.Element importJunkEl = DocumentHelper.createElement(AdminExtConstants.E_importJunk);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_importJunk).getTextTrim())) {
	                    	importJunkEl.setText("1");
	                    } else {
	                    	importJunkEl.setText("0");
	                    }
	                    optionsEl.add(importJunkEl);
	                    org.dom4j.Element importDeletedItemsEl = DocumentHelper.createElement(AdminExtConstants.E_importDeletedItems);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_importDeletedItems).getTextTrim())) {
	                    	importDeletedItemsEl.setText("1");
	                    } else {
	                    	importDeletedItemsEl.setText("0");	
	                    }
	                    optionsEl.add(importDeletedItemsEl);
	                    org.dom4j.Element ignorePreviouslyImportedEl = DocumentHelper.createElement(AdminExtConstants.E_ignorePreviouslyImported);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_ignorePreviouslyImported).getTextTrim())) {
	                    	ignorePreviouslyImportedEl.setText("1");
	                    } else {
	                    	ignorePreviouslyImportedEl.setText("0");
	                    }
	                    optionsEl.add(ignorePreviouslyImportedEl);
	                    org.dom4j.Element InvalidSSLOkEl = DocumentHelper.createElement(AdminExtConstants.E_InvalidSSLOk);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_InvalidSSLOk).getTextTrim())) {
	                    	InvalidSSLOkEl.setText("1");
	                    } else {
	                    	InvalidSSLOkEl.setText("0");
	                    }
	                    optionsEl.add(InvalidSSLOkEl);
	                    
	                    /**
	                     * set MapiProfile section
	                     */
	                    org.dom4j.Element mapiProfileEl = DocumentHelper.createElement(AdminExtConstants.E_MapiProfile);
	                    rootEl.add(mapiProfileEl);
	                    org.dom4j.Element profileEl = DocumentHelper.createElement(E_profile);
	                    profileEl.setText(request.getElement(AdminExtConstants.E_MapiProfile).getTextTrim());
	                    mapiProfileEl.add(profileEl);
	                    
	                    org.dom4j.Element serverEl = DocumentHelper.createElement(E_server);
	                    serverEl.setText(request.getElement(AdminExtConstants.E_MapiServer).getTextTrim());
	                    mapiProfileEl.add(serverEl);
	                    
	                    org.dom4j.Element logonUserDNEl = DocumentHelper.createElement(E_logonUserDN);
	                    logonUserDNEl.setText(request.getElement(AdminExtConstants.E_MapiLogonUserDN).getTextTrim());
	                    mapiProfileEl.add(logonUserDNEl); 
	                    
	                    /**
	                     * set ZimbraServer section
	                     */
	                    org.dom4j.Element zimbraSererEl = DocumentHelper.createElement(E_ZimbraServer);
	                    rootEl.add(zimbraSererEl);                    
	                   
	                    org.dom4j.Element serverNameEl = DocumentHelper.createElement(AdminExtConstants.E_serverName);
	                    serverNameEl.setText(Provisioning.getInstance().getLocalServer().getName());
	                    zimbraSererEl.add(serverNameEl); 
	                    
	                    org.dom4j.Element adminUserNameEl = DocumentHelper.createElement(AdminExtConstants.E_adminUserName);
	                    adminUserNameEl.setText(request.getElement(AdminExtConstants.E_ZimbraAdminLogin).getTextTrim());
	                    zimbraSererEl.add(adminUserNameEl);                     
	                    
	                    org.dom4j.Element adminUserPasswordEl = DocumentHelper.createElement(E_password);
	                    adminUserPasswordEl.setText(request.getElement(AdminExtConstants.E_ZimbraAdminPassword).getTextTrim());
	                    zimbraSererEl.add(adminUserPasswordEl);                                         
	
	                    org.dom4j.Element domaindEl = DocumentHelper.createElement(E_domain);
	                    domaindEl.setText(request.getElement(AdminExtConstants.E_TargetDomainName).getTextTrim());
	                    zimbraSererEl.add(domaindEl); 
	                    
	                    /**
	                     * set UserProvision section
	                     */
	                    org.dom4j.Element userProvisionEl = DocumentHelper.createElement(E_UserProvision);
	                    rootEl.add(userProvisionEl); 
	                    org.dom4j.Element provisionUsersEl = DocumentHelper.createElement(AdminExtConstants.E_provisionUsers);
	                    if("TRUE".equalsIgnoreCase(request.getElement(AdminExtConstants.E_provisionUsers).getTextTrim())) {
	                    	provisionUsersEl.setText("1");
	                    } else {
	                    	provisionUsersEl.setText("0");
	                    }    
	                    userProvisionEl.add(provisionUsersEl);
	                    
	                    /**
	                     * set ImportUsers section
	                     */
	                    org.dom4j.Element usersEl = DocumentHelper.createElement(AdminExtConstants.E_ImportUsers);
	
	                    rootEl.add(usersEl);
	                    for (GalContact entry : entries) {
	                    	String email = entry.getSingleAttr(ContactConstants.A_email);
	                    	if(email == null)
	                    		continue;
	                    	
	                    	org.dom4j.Element eUser = DocumentHelper.createElement(AdminExtConstants.E_User);
	                    	org.dom4j.Element eExchangeMail = DocumentHelper.createElement(AdminExtConstants.E_ExchangeMail);
	                    	eExchangeMail.setText(email);
	                        eUser.add(eExchangeMail);
	                        
	                    	org.dom4j.Element ePassword = DocumentHelper.createElement(AdminExtConstants.A_password);
		                	if(password != null) {
		                    	ePassword.setText(password);
		                	} else if(generatePwd.equalsIgnoreCase("true")) {
		                    	ePassword.setText(String.valueOf(BulkImportAccounts.generateStrongPassword(genPwdLength)));
		                	}   
		                	eUser.add(ePassword);                        
		                	org.dom4j.Element elMustChangePassword = DocumentHelper.createElement(Provisioning.A_zimbraPasswordMustChange);
		                	elMustChangePassword.setText(mustChangePassword);
		                	eUser.add(elMustChangePassword);                  
		                	usersEl.add(eUser);
	                    }                	
	                    xw.write(doc);
	                    xw.flush();     
                	} catch (IOException e) {
		        			throw ServiceException.FAILURE(e.getMessage(), e);
		        	} finally {
		        		if(xw != null) {
		        			try {
		        				xw.close();
		        			} catch (IOException ignore) {}
		        		}
		        		if(fileWriter != null) {
		        			try {
		        				fileWriter.close();
		        			} catch (IOException ignore) {}
		        		}
		        	}
                } else {
                	throw(ServiceException.INVALID_REQUEST("Wrong value for fileFormat parameter",null));
                }
                response.addElement(AdminExtConstants.E_fileToken).setText(fileToken);
            }
		} catch (ServiceException e) {
			if (LdapException.INVALID_SEARCH_FILTER.equals(e.getCode()) ||
			        e.getCause() instanceof LdapException.LdapInvalidSearchFilterException) {
				throw BulkProvisionException.BP_INVALID_SEARCH_FILTER(e);
			} else {
				throw e;
			}
		}
		return response;
	}
	
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
    	relatedRights.add(Admin.R_accessGAL);
    } 
}
