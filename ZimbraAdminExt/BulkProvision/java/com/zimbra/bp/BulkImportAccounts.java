package com.zimbra.bp;

import java.io.File;
import org.dom4j.io.SAXReader;
import org.dom4j.Document;
import org.dom4j.DocumentException;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Iterator;

import javax.naming.NamingException;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Domain;
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
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.cs.service.admin.AdminFileDownload;
import com.zimbra.soap.ZimbraSoapContext;
/**
 * @author Greg Solovyev
 */
public class BulkImportAccounts extends AdminDocumentHandler {

	public static final String A_op = "op";
	public static final String A_sourceType = "sourceType";
	
	private static final String E_status = "status";
	private static final String E_progress = "progress";
	private static final String E_finishedCount = "finishedCount";
	private static final String E_totalCount = "totalCount";
	private static final String E_attachmentID = "aid";
	private static final String E_errorReportFileToken = "fileToken";
	private static final String E_errorCount = "errorCount";
	private static final String E_mustChangePassword = "mustChangePassword";
	
	public static final String ERROR_INVALID_ACCOUNT_NAME = "Invalid account name. " ;
	
	private static final int DEFAULT_PWD_LENGTH = 8;
	private static final String OP_GET_STATUS = "getStatus";
	private static final String OP_START_IMPORT = "startImport";
	private static final String OP_ABORT_IMPORT = "abortImport";
	
	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		Map attrs = AdminService.getAttrs(request, true);
		String op = request.getAttribute(A_op);
		Element response = zsc.createElement(ZimbraBulkProvisionService.BULK_IMPORT_ACCOUNTS_RESPONSE);
		Provisioning prov = Provisioning.getInstance();
		if(op.equalsIgnoreCase(OP_START_IMPORT)) {
			GalParams.ExternalGalParams galParams = new GalParams.ExternalGalParams(attrs, GalOp.search);
	        String[] galAttrs = Provisioning.getInstance().getConfig().getMultiAttr(Provisioning.A_zimbraGalLdapAttrMap);
	        LdapGalMapRules rules = new LdapGalMapRules(galAttrs);
			/**
			 * list of entries found in the source (CSV file, XML file or directory)
			 */
			List<Map<String, Object>> sourceEntries = new ArrayList<Map<String, Object>>();
			String sourceType = request.getElement(A_sourceType).getTextTrim();
			if(sourceType.equalsIgnoreCase(AdminFileDownload.FILE_FORMAT_BULK_CSV)) {
				String aid = request.getElement(E_attachmentID).getText();
				ZimbraLog.extensions.debug("Uploaded CSV file id = " + aid) ;
				//response.addElement(E_attachmentID).addText(aid);
		        FileUploadServlet.Upload up = FileUploadServlet.fetchUpload(zsc.getAuthtokenAccountId(), aid, zsc.getAuthToken());
		        if (up == null) {
		           throw ServiceException.FAILURE("Uploaded CSV file with id " + aid + " was not found.", null);
		        }
		        InputStream in = null ;
		        try {
		            in = up.getInputStream() ;
		            CSVReader reader = new CSVReader(new InputStreamReader(in)) ;
		            String [] nextLine ;

		            List <String []> allEntries = reader.readAll() ;
		            int totalNumberOfEntries = allEntries.size() ;
		           
		            checkAccountLimits(allEntries, zsc, prov);

		            /**
		             * Iterate through records obtained from CSV file and add each record to sourceEntries
		             */
		            for (int i=0; i < totalNumberOfEntries; i ++) {
		                nextLine = (String []) allEntries.get(i);
		                boolean isValidEntry = false ;
		                try {
		                    isValidEntry = validEntry (nextLine, zsc) ;
		                } catch (ServiceException e) {
		                    ZimbraLog.extensions.error(e);
		                    throw(e);
		                }

		                if (!isValidEntry) {
		                	throw ServiceException.INVALID_REQUEST(String.format("Entry %d is not valid (%s %s %s %s %s %s)", i,nextLine[0],nextLine[1],nextLine[2],nextLine[3],nextLine[4],nextLine[5]), null);
		                }
	                	Map<String, Object> accAttrs = new HashMap<String, Object>();
	                	
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_displayName, nextLine[1].trim());
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_givenName, nextLine[2].trim());
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_sn, nextLine[3].trim());
        				StringUtil.addToMultiMap(accAttrs, Provisioning.A_zimbraPasswordMustChange, nextLine[5].trim());
        				
	        			checkSetAttrsOnCreate(zsc, TargetType.account, nextLine[1].trim(), accAttrs);

	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_mail, nextLine[1].trim());
                		StringUtil.addToMultiMap(accAttrs, Provisioning.A_userPassword, nextLine[4].trim());
                		
	                	sourceEntries.add(accAttrs);
		            }

		            in.close();

		        }catch (IOException e) {
		           throw ServiceException.FAILURE("", e) ;
		        }finally {
		            try {
		                in.close ();
		            }catch (IOException e) {
		                ZimbraLog.extensions.error(e);                
		            }
		        }		        
			} else if(sourceType.equalsIgnoreCase(AdminFileDownload.FILE_FORMAT_BULK_XML)) {
				String aid = request.getElement(E_attachmentID).getText();
				ZimbraLog.extensions.debug("Uploaded XML file id = " + aid) ;
		        FileUploadServlet.Upload up = FileUploadServlet.fetchUpload(zsc.getAuthtokenAccountId(), aid, zsc.getAuthToken());
		        if (up == null) {
		           throw ServiceException.FAILURE("Uploaded CSV file with id " + aid + " was not found.", null);
		        }				
		        SAXReader reader = new SAXReader();
		        try {
					Document doc = reader.read(up.getInputStream());
					org.dom4j.Element root = doc.getRootElement();
		            if (!root.getName().equals(ZimbraBulkProvisionExt.E_ZCSImport)) {
		                throw new DocumentException("Bulk provisioning XML file's root element must be " + ZimbraBulkProvisionExt.E_ZCSImport);
		            }
		            Iterator  iter = root.elementIterator(ZimbraBulkProvisionExt.E_ImportUsers);
		            if(!iter.hasNext()) {
		            	throw new DocumentException("Cannot find element " + ZimbraBulkProvisionExt.E_ImportUsers + " in uploaded bulk provisioning XML file");
		            }
		            org.dom4j.Element elImportUsers = (org.dom4j.Element)iter.next();
		            for(Iterator userIter = elImportUsers.elementIterator(ZimbraBulkProvisionExt.E_User);userIter.hasNext();) {
		            	org.dom4j.Element elUser  = (org.dom4j.Element)userIter.next();
		            	String userEmail = "";
		            	String userFN = "";
		            	String userLN = "";
		            	String userDN = "";
		            	String userPassword = "";
		            	String userPwdMustChange = "";
		            	boolean skipUser = false;
		            	for(Iterator userPropsIter = elUser.elementIterator(); userPropsIter.hasNext();) {
		            		org.dom4j.Element el = (org.dom4j.Element)userPropsIter.next();
		            		if(ZimbraBulkProvisionExt.E_ExchangeMail.equalsIgnoreCase(el.getName())) {
		            			userEmail = el.getTextTrim();
		            			/**
		            			 * Check if user exists
		            			 */
			                	Account acct = prov.getAccountByName(userEmail);
			                	if(acct!=null) {
			                		/**
			                		 * Skip existing user
			                		 */
			                		skipUser = true;
			                		break;
			                	}
		            		}
		            		if(Provisioning.A_displayName.equalsIgnoreCase(el.getName())) {
		            			userDN = el.getTextTrim();
		            		}
		            		
		            		if(Provisioning.A_givenName.equalsIgnoreCase(el.getName())) {
		            			userFN = el.getTextTrim();
		            		}
		            		
		            		if(Provisioning.A_sn.equalsIgnoreCase(el.getName())) {
		            			userLN = el.getTextTrim();
		            		}
		            		
		            		if(ZimbraBulkProvisionExt.A_password.equalsIgnoreCase(el.getName())) {
		            			userPassword = el.getTextTrim();
		            		}
		            		
		            		if(Provisioning.A_zimbraPasswordMustChange.equalsIgnoreCase(el.getName())) {
		            			userPwdMustChange = el.getTextTrim();
		            		}		            		
		            	}
	                	checkDomainRightByEmail(zsc, userEmail, Admin.R_createAccount);
	                	Map<String, Object> accAttrs = new HashMap<String, Object>();
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_givenName, userFN);
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_displayName, userDN);
	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_sn, userLN);
        				StringUtil.addToMultiMap(accAttrs, Provisioning.A_zimbraPasswordMustChange, userPwdMustChange);
        				
	        			checkSetAttrsOnCreate(zsc, TargetType.account, userEmail, accAttrs);

	        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_mail, userEmail);
	                	if(userPassword != null) {
	                		StringUtil.addToMultiMap(accAttrs, Provisioning.A_userPassword, userPassword);
	                	}
	        			
	                	sourceEntries.add(accAttrs);
		            	
		            }
		            
				} catch (DocumentException e) {
					throw ServiceException.FAILURE("Bulk provisioning failed to read uploaded XML document.", e) ;
				} catch (IOException e) {
					throw ServiceException.FAILURE("Bulk provisioning failed to read uploaded XML document.", e) ;
				}
			} else if(sourceType.equalsIgnoreCase(ZimbraBulkProvisionExt.FILE_FORMAT_BULK_LDAP)) {
				Element elPassword = request.getOptionalElement(ZimbraBulkProvisionExt.A_password);
				Element elPasswordLength = request.getOptionalElement(ZimbraBulkProvisionExt.A_genPasswordLength);

				String generatePwd = request.getElement(ZimbraBulkProvisionExt.A_generatePassword).getTextTrim();

				
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

				String password = null;
				if(elPassword != null) {
					password = elPassword.getTextTrim();
				}
				String mustChangePassword = request.getElement(E_mustChangePassword).getTextTrim();
				try {
					SearchGalResult result = LdapUtil.searchLdapGal(galParams, GalOp.search, "*", maxResults, rules, null, null);
					List<GalContact> entries = result.getMatches();

					if (entries != null) {
		                for (GalContact entry : entries) {
		                	String emailAddress = entry.getSingleAttr(ContactConstants.A_email);
		                	if(emailAddress == null) {
		                		continue;
		                	}
		                	
		                	Account acct = prov.getAccountByName(emailAddress);
		                	if(acct != null) {
		                		continue; //skip existing accounts
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
		        			if("true".equalsIgnoreCase(mustChangePassword)) {
		        				StringUtil.addToMultiMap(accAttrs, Provisioning.A_zimbraPasswordMustChange, "TRUE");
		        			}
		        			checkSetAttrsOnCreate(zsc, TargetType.account, emailAddress, accAttrs);
	
		        			StringUtil.addToMultiMap(accAttrs, Provisioning.A_mail, entry.getSingleAttr(ContactConstants.A_email));
		                	if(password != null) {
		                		StringUtil.addToMultiMap(accAttrs, Provisioning.A_userPassword, password);
		                	} else if(generatePwd.equalsIgnoreCase("true")) {
		                		StringUtil.addToMultiMap(accAttrs, Provisioning.A_userPassword, String.valueOf(GetBulkProvisionAccounts.generateStrongPassword(genPwdLength)));
		                	}
		        			
		                	sourceEntries.add(accAttrs);
		                }

		            }
				} catch (NamingException e) {
					throw ServiceException.FAILURE("", e) ;
				} catch (IOException e) {
					throw ServiceException.FAILURE("", e) ;
				}
			} else {
				throw ServiceException.INVALID_REQUEST(sourceType + " is not a valid value for parameter " + A_sourceType, null);
			}
			/**
			 * Spin off a provisioning thread
			 */
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
		} else if(op.equalsIgnoreCase(OP_ABORT_IMPORT)) {
			BulkProvisioningThread thread = BulkProvisioningThread.getThreadInstance(zsc.getAuthtokenAccountId(),false);
			if(thread != null) {
				int status = thread.getStatus();
				if(status != BulkProvisioningThread.iSTATUS_FINISHED) {
					thread.abort();
					response.addElement(E_status).setText(Integer.toString(thread.getStatus()));
					response.addElement(E_progress).setText(Integer.toString(thread.getProgressPercent()));
					response.addElement(E_finishedCount).setText(Integer.toString(thread.getProgress()));
					response.addElement(E_totalCount).setText(Integer.toString(thread.getTotalCount()));
					status = thread.getStatus();
					if(status == BulkProvisioningThread.iSTATUS_ABORTED) {
						BulkProvisioningThread.deleteThreadInstance(zsc.getAuthtokenAccountId());
					}
				} else {
					response.addElement(E_status).setText(Integer.toString(status));
				}
			} else {
				response.addElement(E_status).setText("-1");
			}				
		} else if(op.equalsIgnoreCase(OP_GET_STATUS)) {
			BulkProvisioningThread thread = BulkProvisioningThread.getThreadInstance(zsc.getAuthtokenAccountId(),false);
			if(thread != null) {
				int status = thread.getStatus();
				response.addElement(E_status).setText(Integer.toString(status));
				response.addElement(E_progress).setText(Integer.toString(thread.getProgressPercent()));
				response.addElement(E_finishedCount).setText(Integer.toString(thread.getProgress()));
				response.addElement(E_totalCount).setText(Integer.toString(thread.getTotalCount()));
				if(thread.getWithErrors()) {
					response.addElement(E_errorCount).addText(Integer.toString(thread.getFailCounter()));
				}
				if(status == BulkProvisioningThread.iSTATUS_FINISHED || status == BulkProvisioningThread.iSTATUS_ABORTED || status == BulkProvisioningThread.iSTATUS_ERROR) {
					String fileToken = Double.toString(Math.random()*100);
					String outSuccessFileName = String.format("%s%s_bulk_report_%s_%s.csv", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);					
					try {	
						FileOutputStream outReport = new FileOutputStream (outSuccessFileName) ;
						CSVWriter reportWriter = new CSVWriter(new OutputStreamWriter (outReport) ) ;	            	
						for(String failedAccount : thread.getfailedAccounts().keySet()) {	
							String [] line = new String [2] ;
							line[0] = failedAccount;
							line[1] = thread.getfailedAccounts().get(failedAccount).getMessage();
							reportWriter.writeNext(line);
						}
						reportWriter.close();					
					} catch (FileNotFoundException e) {
						throw(ServiceException.FAILURE("Failed to create CSV file with a list of provisioned accounts",e));
					} catch (IOException e) {
						throw(ServiceException.FAILURE("Failed to create CSV file with a list of provisioned accounts",e));
					}	
					response.addElement(E_errorReportFileToken).addText(fileToken);
					/**
					 * if thread is done for whichever reason and there are errors, generate an error report
					 */
					if(thread.getWithErrors()) {
						try {
							String outErrorsFileName = String.format("%s%s_bulk_errors_%s_%s.csv", LC.zimbra_tmp_directory.value(),File.separator,zsc.getAuthtokenAccountId(),fileToken);
							FileOutputStream out = new FileOutputStream (outErrorsFileName) ;
							CSVWriter errorWriter = new CSVWriter(new OutputStreamWriter (out) ) ;	            	
							for(String failedAccount : thread.getfailedAccounts().keySet()) {	
								String [] line = new String [2] ;
								line[0] = failedAccount;
								line[1] = thread.getfailedAccounts().get(failedAccount).getMessage();
								errorWriter.writeNext(line);
							}
							errorWriter.close();
						} catch (FileNotFoundException e) {
							throw(ServiceException.FAILURE("Failed to create CSV file with error report",e));
						} catch (IOException e) {
							throw(ServiceException.FAILURE("Failed to create CSV file with error report",e));
						}										
					}
					BulkProvisioningThread.deleteThreadInstance(zsc.getAuthtokenAccountId());
				}
			} else {
				response.addElement(E_status).setText("-1");
			}			
		}
		return response;
	}

    /**
     * The account limits are decided by the following factors:
     * 1) Hard limit: MAX_ACCOUNTS_LIMIT or accountLimit - License Account Limit (whichever is smaller)
     * 2) zimbraDomainMaxAccounts 
     *
     * @param allEntries
     * @throws ServiceException
     */
    private void checkAccountLimits (List<String []> allEntries, ZimbraSoapContext zsc, Provisioning prov ) throws ServiceException {
        ZimbraLog.extensions.debug ("Check the account limits ...") ;
        int numberOfEntries = allEntries.size() ;

        //check against zimbraDomainMaxAccounts
        Hashtable <String, Integer> h = new Hashtable <String, Integer>() ;

        for (int i=0; i < numberOfEntries; i ++) {
            String [] entry =  (String []) allEntries.get(i);
            String accountName = null;
            String parts[] = null;
            String domainName = null;

            if (entry != null) accountName = entry [0];
            if (accountName != null) parts = accountName.trim().split("@");
            if (parts != null && parts.length > 0) domainName = parts[1] ;
            if (domainName != null) {
                int count = 0;
                if (h.containsKey(domainName)) {
                    count = h.get(domainName).intValue()  ;
                }
                h.put(domainName, count + 1);
            }
        }

        for (Enumeration<String> keys = h.keys(); keys.hasMoreElements();){
            String domainName = keys.nextElement();
            Domain domain = prov.get(Provisioning.DomainBy.name, domainName);
            if (domain != null) {
                String domainMaxAccounts = domain.getAttr(Provisioning.A_zimbraDomainMaxAccounts) ;
                if (domainMaxAccounts != null && domainMaxAccounts.length() > 0) {
                    int limit = Integer.parseInt(domainMaxAccounts) ;
                    int used = 0;
                    Provisioning.CountAccountResult result = prov.countAccount(domain);
                    for (Provisioning.CountAccountResult.CountAccountByCos c : result.getCountAccountByCos()) {
                        used += c.getCount();
                    }

                    int newAccounts = h.get(domainName).intValue() ;
                    int available = limit - used ;
                    /*ZimbraLog.extensions.debug("For domain " + domainName + " : csv entry = " + newAccounts
                            + ", zimbraDomainMaxAccounts = " + limit + ", used accounts = " + used) ;*/
                    if (newAccounts > available) {
                       throw BulkProvisionException.BP_TOO_MANY_ACCOUNTS (
                            "the maximum accounts you can bulk provisioning for domain: " + domainName + " is "+ available + "\n");
                    }
                }
            }
        }
    }
    
    private boolean validEntry (String [] entries, ZimbraSoapContext lc) throws ServiceException {
        Provisioning prov = Provisioning.getInstance();
        String errorMsg = "" ;
        if (entries.length != 6) {
            errorMsg = "Invalid number of columns." ;
            throw ServiceException.PARSE_ERROR(errorMsg, new Exception(errorMsg)) ;
        }

        String accountName = entries [0] ;

        //1. account name is specified and can be accessed by current admin/domain admin user
        if (accountName == null || accountName.length() <= 0) {
            throw ServiceException.PARSE_ERROR(ERROR_INVALID_ACCOUNT_NAME, new Exception(ERROR_INVALID_ACCOUNT_NAME)) ;
        }
        accountName = accountName.trim();
        String parts[] = accountName.split("@");

        if (parts.length != 2)
            throw ServiceException.PARSE_ERROR(ERROR_INVALID_ACCOUNT_NAME, new Exception(ERROR_INVALID_ACCOUNT_NAME)) ;

        checkDomainRightByEmail(lc, accountName, Admin.R_createAccount);
 
        //2. if account already exists
        Account acct = null ;
        try {
            acct = prov.getAccount(accountName) ;
        }catch (Exception e) {
            //ignore
        }
        if (acct != null) {
            errorMsg = "Account " + accountName + " already exists." ;
            throw ServiceException.PARSE_ERROR(errorMsg, new Exception(errorMsg)) ;
        }

        String domain = parts[1];

        //domain exists
        Domain d = null ;
        try {
            d = prov.get(Provisioning.DomainBy.name, domain) ;
        }catch (Exception e) {
            //ignore
        }
        if (d == null) {
            errorMsg = "domain " + domain + " doesn't exist for account " + accountName ;
            throw ServiceException.PARSE_ERROR(errorMsg, new Exception(errorMsg)) ;
        }

        return true ;
    }

    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_createAccount);
        relatedRights.add(Admin.R_listAccount);
        
        notes.add("Only accounts on which the authed admin has " + Admin.R_listAccount.getName() +
                " right will be provisioned.");
    }    

}
