package com.zimbra.bp;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;

import javax.naming.NamingException;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.zimbra.bp.BulkIMAPImportTaskManager.taskKeys;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.EmailUtil;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Domain;
import com.zimbra.cs.account.GalContact;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.AccountBy;
import com.zimbra.cs.account.Provisioning.SearchGalResult;
import com.zimbra.cs.account.accesscontrol.TargetType;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.account.gal.GalOp;
import com.zimbra.cs.account.gal.GalParams;
import com.zimbra.cs.account.ldap.LdapGalMapRules;
import com.zimbra.cs.account.ldap.LdapUtil;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.datasource.ImportStatus;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminFileDownload;
import com.zimbra.cs.service.admin.AdminService;
import com.zimbra.soap.ZimbraSoapContext;

/**
 * 
 * @author Greg Solovyev
 * Import data for multiple accounts via IMAP
 */
public class BulkIMAPDataImport extends AdminDocumentHandler {
	public static enum accountState {
		idle, running, finished;
	}

	public Element handle(Element request, Map<String, Object> context)
			throws ServiceException {
		ZimbraSoapContext zsc = getZimbraSoapContext(context);
		String op = request.getAttribute(ZimbraBulkProvisionExt.A_op);
		Element response = zsc.createElement(ZimbraBulkProvisionService.BULK_IMAP_DATA_IMPORT_RESPONSE);
		Map<accountState, List<ExternalIMAPAccount>> IMAPAccounts = null;
		String IMAPhost = null, IMAPport = null, adminLogin = null, adminPassword = null, connectionType = null;
		String sourceType = request.getElement(ZimbraBulkProvisionExt.A_sourceType).getTextTrim();
		boolean useAdminLogin = false;
		if (sourceType.equalsIgnoreCase(AdminFileDownload.FILE_FORMAT_BULK_XML)) {
	        String aid = request.getElement(ZimbraBulkProvisionExt.E_attachmentID).getTextTrim();
	        ZimbraLog.extensions.debug("Uploaded XML file id = " + aid);
	        FileUploadServlet.Upload up = FileUploadServlet.fetchUpload(zsc.getAuthtokenAccountId(), aid, zsc.getAuthToken());
	        if (up == null) {
	            throw ServiceException.FAILURE("Uploaded XML file with id " + aid + " was not found.", null);
	        }

		    try {
    			SAXReader reader = new SAXReader();
    			Document doc = reader.read(up.getInputStream());
    			org.dom4j.Element root = doc.getRootElement();
    
    			if (!root.getName().equals(ZimbraBulkProvisionExt.E_ZCSImport)) {
    				throw new DocumentException("Bulk provisioning XML file's root element must be " + ZimbraBulkProvisionExt.E_ZCSImport);
    			}
    			Iterator rootIter = root.elementIterator(ZimbraBulkProvisionExt.E_ImportUsers);
    			if (!rootIter.hasNext()) {
    				throw new DocumentException("Cannot find element " + ZimbraBulkProvisionExt.E_ImportUsers + " in uploaded bulk provisioning XML file");
    			}
    			org.dom4j.Element elImportUsers = (org.dom4j.Element) rootIter.next();
    
    			IMAPAccounts = parseExternalIMAPAccounts(elImportUsers, zsc);
    
    			Iterator connectionTypeIter = root.elementIterator(ZimbraBulkProvisionExt.E_connectionType);
    			if (connectionTypeIter.hasNext()) {
    				org.dom4j.Element elConnectionType = (org.dom4j.Element) connectionTypeIter.next();
    				connectionType = elConnectionType.getTextTrim();
    			}
    
    			Iterator IMAPHostIter = root.elementIterator(ZimbraBulkProvisionExt.E_IMAPHost);
    			if (IMAPHostIter.hasNext()) {
    				org.dom4j.Element elIMAPHost = (org.dom4j.Element) IMAPHostIter.next();
    				IMAPhost = elIMAPHost.getTextTrim();
    			}
    
    			Iterator IMAPPortIter  = root.elementIterator(ZimbraBulkProvisionExt.E_IMAPPort);
    			if (IMAPPortIter.hasNext()) {
    				org.dom4j.Element elIMAPPort = (org.dom4j.Element) IMAPPortIter.next();
    				IMAPport = elIMAPPort.getTextTrim();
    			}
    
    			Iterator useAdminLoginIter = root.elementIterator(ZimbraBulkProvisionExt.E_useAdminLogin);
    			if (useAdminLoginIter.hasNext()) {
    				org.dom4j.Element elUseAdminLogin = (org.dom4j.Element) useAdminLoginIter.next();
    				useAdminLogin = "1".equalsIgnoreCase(elUseAdminLogin.getTextTrim());
    				if (useAdminLogin) {
    				    Iterator adminLoginIter = root.elementIterator(ZimbraBulkProvisionExt.E_IMAPAdminLogin);
    					if (adminLoginIter.hasNext()) {
    						org.dom4j.Element elAdminLogin = (org.dom4j.Element) adminLoginIter.next();
    						adminLogin = elAdminLogin.getTextTrim();
    					}
    
    					Iterator adminPassIter = root.elementIterator(ZimbraBulkProvisionExt.E_IMAPAdminPassword);
    					if (adminPassIter.hasNext()) {
    						org.dom4j.Element elAdminPassword = (org.dom4j.Element) adminPassIter.next();
    						adminPassword = elAdminPassword.getTextTrim();
    					}
    				}
    			}
    
    		} catch (DocumentException e) {
    			throw ServiceException.FAILURE("Bulk provisioning failed to read uploaded XML document.",e);
    		} catch (IOException e) {
    			throw ServiceException.FAILURE("Bulk provisioning failed to read uploaded XML document.",e);
    		}
		} /*else if (sourceType.equalsIgnoreCase(ZimbraBulkProvisionExt.FILE_FORMAT_BULK_LDAP) || sourceType.equalsIgnoreCase(ZimbraBulkProvisionExt.FILE_FORMAT_BULK_AD)) {
		    IMAPAccounts = getExternalIMAPAccounts(request, zsc);
		} */ else if (sourceType.equalsIgnoreCase(ZimbraBulkProvisionExt.FILE_FORMAT_ZIMBRA)) {
		    IMAPAccounts = getZimbraAccounts(request, zsc);   
        }
		
		/*
		 * Process the list of accounts. Find existing datasources and check their states.
		 */
		int numIdleAccounts = 0;
		int numRunningAccounts = 0;
		int numFinishedAccounts = 0;
		List<ExternalIMAPAccount> idleAccounts = null;
		if (IMAPAccounts.containsKey(accountState.idle)) {
			idleAccounts = IMAPAccounts.get(accountState.idle);
			if (idleAccounts != null) {
				numIdleAccounts = idleAccounts.size();
			}
		}
		List<ExternalIMAPAccount> runningAccounts;
		if (IMAPAccounts.containsKey(accountState.running)) {
			runningAccounts = IMAPAccounts.get(accountState.running);
			if (runningAccounts != null) {
				Element elRunningAccounts = response.addElement(ZimbraBulkProvisionExt.E_runningAccounts);
				numRunningAccounts = runningAccounts.size();
				Iterator<ExternalIMAPAccount> accountsIter = runningAccounts.iterator();
				while (accountsIter.hasNext()) {
					ExternalIMAPAccount acct = accountsIter.next();
					Element elAccount = elRunningAccounts.addElement(AdminConstants.E_ACCOUNT);
					elAccount.addAttribute(AdminConstants.A_NAME,acct.getAccount().getName());
					elAccount.addAttribute(AdminConstants.A_ID,acct.getAccount().getId());
				}
			}
		}

		if (IMAPAccounts.containsKey(accountState.finished)) {
			List accounts = IMAPAccounts.get(accountState.finished);
			if (accounts != null) {
				numFinishedAccounts = accounts.size();
			}
		}
		
        /*
         * Check for overwritten options
         */
        Element elConnectionType = request.getOptionalElement(ZimbraBulkProvisionExt.E_connectionType);
        if (elConnectionType != null) {
            connectionType = elConnectionType.getTextTrim();
        }

        Element elIMAPHost = request.getOptionalElement(ZimbraBulkProvisionExt.E_IMAPHost);
        if (elIMAPHost != null) {
            IMAPhost = elIMAPHost.getTextTrim();
        }

        Element elIMAPPort = request.getOptionalElement(ZimbraBulkProvisionExt.E_IMAPPort);
        if (elIMAPPort != null) {
            IMAPport = elIMAPPort.getTextTrim();
        }

        Element elUseAdminLogin = request.getOptionalElement(ZimbraBulkProvisionExt.E_useAdminLogin);
        if (elUseAdminLogin != null) {
            useAdminLogin = "1".equalsIgnoreCase(elUseAdminLogin.getTextTrim());
        }

        if (useAdminLogin) {
            Element elAdminLogin = request.getOptionalElement(ZimbraBulkProvisionExt.E_IMAPAdminLogin);
            if (elAdminLogin != null) {
                adminLogin = elAdminLogin.getTextTrim();
            }
            Element elAdminPassword = request.getOptionalElement(ZimbraBulkProvisionExt.E_IMAPAdminPassword);
            if (elAdminPassword != null) {
                adminPassword = elAdminPassword.getTextTrim();
            }
        }
		
		if (ZimbraBulkProvisionExt.OP_PREVIEW.equalsIgnoreCase(op)) {
			/*
			 * Do not start the import. Just generate a preview. We will count
			 * idle and non-idle accounts.
			 */
			response.addElement(ZimbraBulkProvisionExt.E_totalCount).setText(Integer.toString(numIdleAccounts + numRunningAccounts + numFinishedAccounts));
			response.addElement(ZimbraBulkProvisionExt.E_idleCount).setText(Integer.toString(numIdleAccounts));
			response.addElement(ZimbraBulkProvisionExt.E_runningCount).setText(Integer.toString(numRunningAccounts));
			response.addElement(ZimbraBulkProvisionExt.E_finishedCount).setText(Integer.toString(numFinishedAccounts));
			
			response.addElement(ZimbraBulkProvisionExt.E_connectionType).setText(connectionType);
			response.addElement(ZimbraBulkProvisionExt.E_IMAPHost).setText(IMAPhost);
			response.addElement(ZimbraBulkProvisionExt.E_IMAPPort).setText(IMAPport);
			if(useAdminLogin) {
                response.addElement(ZimbraBulkProvisionExt.E_useAdminLogin).setText("1");
                response.addElement(ZimbraBulkProvisionExt.E_IMAPAdminLogin).setText(adminLogin);
                response.addElement(ZimbraBulkProvisionExt.E_IMAPAdminPassword).setText(adminPassword);
			} else {
			    response.addElement(ZimbraBulkProvisionExt.E_useAdminLogin).setText("0");
			}
		} else if (ZimbraBulkProvisionExt.OP_START_IMPORT.equalsIgnoreCase(op)) {
			if (idleAccounts == null) {
				throw ServiceException.INVALID_REQUEST("None of the specified accounts are available to start import right now.",null);
			}
	        if (IMAPhost == null) {
	            throw ServiceException.INVALID_REQUEST(
	                    "Must specify IMAP server address!", null);
	        }

	        if (IMAPport == null) {
	            throw ServiceException.INVALID_REQUEST(
	                    "Must specify IMAP server port number!", null);
	        }
			
	        if(useAdminLogin) {
	            /*
	             * We must have admin login/password if we are going to connect
	             * to IMAP server with admin credentials
	             */
	            if (adminPassword == null || adminLogin == null) {
	                throw ServiceException.INVALID_REQUEST(
	                        "Must specify admin credentials in order to log in as admin to IMAP server if "
	                                + ZimbraBulkProvisionExt.E_useAdminLogin
	                                + " option is selected!", null);
	            }

	        }
			/*
			 * Create the import queue
			 */
			Queue<HashMap<taskKeys, String>> queue = BulkIMAPImportTaskManager.getQueue(zsc.getAuthtokenAccountId());
			Queue<HashMap<taskKeys, String>> runningQ = BulkIMAPImportTaskManager.getRunningQueue(zsc.getAuthtokenAccountId());
			Iterator<ExternalIMAPAccount> idleAccIter = idleAccounts.iterator();
			
			while (idleAccIter.hasNext()) {
				ExternalIMAPAccount acct = idleAccIter.next();
				HashMap<taskKeys, String> task = new HashMap<taskKeys, String>();
				String dataSourceID = createIMAPDataSource(
                        acct.getAccount(),
                        IMAPhost,
                        IMAPport,
                        connectionType,
                        acct.getUserEmail(),
                        useAdminLogin ? adminLogin
                                : acct.getUserLogin(),
                        useAdminLogin ? adminPassword
                                : acct.getUserPassword(),
                        useAdminLogin).getId(); 
				String acctID = acct.getAccount().getId();
				task.put(taskKeys.accountID, acctID);
				task.put(taskKeys.dataSourceID,dataSourceID);
				
				synchronized (queue) {
				   queue.add(task);
			    }
                synchronized (runningQ) {
                    runningQ.add(task);
                }
				
		     }
			/*
			 * Start the import process
			 */
			BulkIMAPImportTaskManager.startImport(zsc.getAuthtokenAccountId());
		} else if (ZimbraBulkProvisionExt.OP_DISMISS_IMPORT.equalsIgnoreCase(op)) {

		}
		return response;
	}

	private Map<accountState,List<ExternalIMAPAccount>> getZimbraAccounts(Element request, ZimbraSoapContext zsc) throws ServiceException {
	    List<Element> acctElems = request.listElements(AdminConstants.E_ACCOUNT);
        Provisioning prov = Provisioning.getInstance();
        List<ExternalIMAPAccount> idleAccts = new ArrayList<ExternalIMAPAccount>();
        List<ExternalIMAPAccount> runningAccts = new ArrayList<ExternalIMAPAccount>();
        List<ExternalIMAPAccount> finishedAccts = new ArrayList<ExternalIMAPAccount>();
        Map<accountState, List<ExternalIMAPAccount>> accts = new HashMap<accountState, List<ExternalIMAPAccount>>();
        if(acctElems != null && acctElems.size()>0) {
            for(Element elem : acctElems) {
                String emailAddress = elem.getAttribute(AdminConstants.A_NAME);
                Account localAccount = null;
                try {
                    localAccount = prov.get(AccountBy.name, emailAddress);
                } catch (ServiceException se) {
                    ZimbraLog.gal.warn("error looking up account", se);
                }
    
                if (localAccount == null) {
                    throw AccountServiceException.NO_SUCH_ACCOUNT(emailAddress);
                }
                checkAdminLoginAsRight(zsc, prov, localAccount);
                // Check if an import is running on this account already
                boolean isRunning = false;
                boolean hasRun = false;
                List<DataSource> sources = Provisioning.getInstance().getAllDataSources(localAccount);
                for (DataSource ds : sources) {
                    if (ZimbraBulkProvisionExt.IMAP_IMPORT_DS_NAME.equalsIgnoreCase(ds.getName())
                            && ds.getType() == DataSource.Type.imap
                            && ds.isImportOnly()
                            && "1".equalsIgnoreCase(ds.getAttr(Provisioning.A_zimbraDataSourceFolderId))) {
                        ImportStatus importStatus = DataSourceManager.getImportStatus(localAccount, ds);
                        if (!isRunning) {
                            synchronized (importStatus) {
                                isRunning = importStatus.isRunning();
                                hasRun = importStatus.hasRun();
                            }
                        }
    
                        if (!hasRun) {
                            synchronized (importStatus) {
                                hasRun = importStatus.hasRun();
                            }
                        }
    
                        if (!isRunning) {
                            runningAccts.add(new ExternalIMAPAccount(emailAddress, emailAddress, "", localAccount));
                            break;
                        } else if (hasRun) {
                            finishedAccts.add(new ExternalIMAPAccount(emailAddress, emailAddress, "", localAccount));
                            break;
                        }
                    }
                }
                if (!isRunning && !hasRun) {
                    idleAccts.add(new ExternalIMAPAccount(emailAddress, emailAddress, "", localAccount));
                }
                
            }
        }
        accts.put(accountState.idle, idleAccts);
        accts.put(accountState.running, runningAccts);
        accts.put(accountState.finished, finishedAccts);
        return accts;
	    
	}
	/**
	 * Collects data about external IMAP accounts from LDAP directory
	 * @param request
	 * @param zsc
	 * @return Map<accountState,List<ExternalIMAPAccount>>
	 * @throws ServiceException
	 */
	/*
	private Map<accountState,List<ExternalIMAPAccount>> getExternalIMAPAccounts(Element request, ZimbraSoapContext zsc) throws ServiceException {
	    Map attrs = AdminService.getAttrs(request, true);
        GalParams.ExternalGalParams galParams = new GalParams.ExternalGalParams(attrs, GalOp.search);
        LdapGalMapRules rules = new LdapGalMapRules(Provisioning.getInstance().getConfig());
        Provisioning prov = Provisioning.getInstance();
        List<ExternalIMAPAccount> idleAccts = new ArrayList<ExternalIMAPAccount>();
        List<ExternalIMAPAccount> runningAccts = new ArrayList<ExternalIMAPAccount>();
        List<ExternalIMAPAccount> finishedAccts = new ArrayList<ExternalIMAPAccount>();
        Map<accountState, List<ExternalIMAPAccount>> accts = new HashMap<accountState, List<ExternalIMAPAccount>>();
        try {
            SearchGalResult result = LdapUtil.searchLdapGal(galParams,GalOp.search, "*", 0, rules, null, null);
            List<GalContact> entries = result.getMatches();
            
            if (entries != null) {
                for (GalContact entry : entries) {
                    String emailAddress = entry.getSingleAttr(ContactConstants.A_email);
                    if (emailAddress == null) {
                        continue;
                    }

                    Account localAccount = null;
                    try {
                        localAccount = prov.get(AccountBy.name, emailAddress);
                    } catch (ServiceException se) {
                        ZimbraLog.gal.warn("error looking up account", se);
                    }

                    if (localAccount == null) {
                        throw AccountServiceException.NO_SUCH_ACCOUNT(emailAddress);
                    }
                    checkAdminLoginAsRight(zsc, prov, localAccount);
                    // Check if an import is running on this account already
                    boolean isRunning = false;
                    boolean hasRun = false;
                    List<DataSource> sources = Provisioning.getInstance().getAllDataSources(localAccount);
                    for (DataSource ds : sources) {
                        if (ZimbraBulkProvisionExt.IMAP_IMPORT_DS_NAME.equalsIgnoreCase(ds.getName())
                                && ds.getType() == DataSource.Type.imap
                                && ds.isImportOnly()
                                && "1".equalsIgnoreCase(ds.getAttr(Provisioning.A_zimbraDataSourceFolderId))) {
                            ImportStatus importStatus = DataSourceManager.getImportStatus(localAccount, ds);
                            if (!isRunning) {
                                synchronized (importStatus) {
                                    isRunning = importStatus.isRunning();
                                    hasRun = importStatus.hasRun();
                                }
                            }

                            if (!hasRun) {
                                synchronized (importStatus) {
                                    hasRun = importStatus.hasRun();
                                }
                            }

                            if (!isRunning) {
                                runningAccts.add(new ExternalIMAPAccount(emailAddress, emailAddress, "", localAccount));
                                break;
                            } else if (hasRun) {
                                finishedAccts.add(new ExternalIMAPAccount(emailAddress, emailAddress, "", localAccount));
                                break;
                            }
                        }
                    }
                    if (!isRunning && !hasRun) {
                        idleAccts.add(new ExternalIMAPAccount(emailAddress, emailAddress, "", localAccount));
                    }
                }
            }
            
        } catch (NamingException e) {
            throw ServiceException.FAILURE("", e);
        } catch (IOException e) {
            throw ServiceException.FAILURE("", e);
        }
        
        accts.put(accountState.idle, idleAccts);
        accts.put(accountState.running, runningAccts);
        accts.put(accountState.finished, finishedAccts);
        return accts;
	}*/
	/**
	 * Collects data about external IMAP accounts from XML
	 * @param is
	 * @return ExternalIMAPAccount list of account data containers
	 * @throws DocumentException
	 * @throws IOException
	 * @throws ServiceException
	 */
	private Map<accountState, List<ExternalIMAPAccount>> parseExternalIMAPAccounts(org.dom4j.Element root, ZimbraSoapContext zsc) throws DocumentException, IOException, ServiceException {
		List<ExternalIMAPAccount> idleAccts = new ArrayList<ExternalIMAPAccount>();
		List<ExternalIMAPAccount> runningAccts = new ArrayList<ExternalIMAPAccount>();
		List<ExternalIMAPAccount> finishedAccts = new ArrayList<ExternalIMAPAccount>();
		Map<accountState, List<ExternalIMAPAccount>> accts = new HashMap<accountState, List<ExternalIMAPAccount>>();
		Provisioning prov = Provisioning.getInstance();

		for (Iterator userIter = root.elementIterator(ZimbraBulkProvisionExt.E_User); userIter.hasNext();) {
			org.dom4j.Element elUser = (org.dom4j.Element) userIter.next();
			String userEmail = "";
			String userLogin = "";
			String userPassword = "";


			for (Iterator userPropsIter = elUser.elementIterator(); userPropsIter.hasNext();) {
				org.dom4j.Element el = (org.dom4j.Element) userPropsIter.next();
				/*
				 * We support <ExchangeMail> element for compatibility with
				 * desktop based Exchange Migration utility <RemoteEmailAddress>
				 * takes prevalence over <ExchangeMail> element
				 */
				if (ZimbraBulkProvisionExt.E_ExchangeMail.equalsIgnoreCase(el.getName())) {
					userEmail = el.getTextTrim();
				}
				if (ZimbraBulkProvisionExt.E_remoteEmail.equalsIgnoreCase(el.getName())) {
					userEmail = el.getTextTrim();
				}
				if (ZimbraBulkProvisionExt.E_remoteIMAPLogin.equalsIgnoreCase(el.getName())) {
					userLogin = el.getTextTrim();
				}
				if (ZimbraBulkProvisionExt.E_remoteIMAPPassword.equalsIgnoreCase(el.getName())) {
					userPassword = el.getTextTrim();
				}
			}

            Account localAccount = null;
            try {
                localAccount = prov.get(AccountBy.name, userEmail,zsc.getAuthToken());
            } catch (ServiceException se) {
                ZimbraLog.gal.warn("error looking up account", se);
            }

            if (localAccount == null) {
                throw AccountServiceException.NO_SUCH_ACCOUNT(userEmail);
            }
            checkAdminLoginAsRight(zsc, prov, localAccount);
            
			if (userLogin.length() == 0) {
				userLogin = userEmail;
			}
			// Check if an import is running on this account already
			boolean isRunning = false;
			boolean hasRun = false;
			List<DataSource> sources = Provisioning.getInstance().getAllDataSources(
					localAccount);
			for (DataSource ds : sources) {
				if (ZimbraBulkProvisionExt.IMAP_IMPORT_DS_NAME.equalsIgnoreCase(ds.getName())
						&& ds.getType() == DataSource.Type.imap
						&& ds.isImportOnly()
						&& "1".equalsIgnoreCase(ds.getAttr(Provisioning.A_zimbraDataSourceFolderId))) {
					ImportStatus importStatus = DataSourceManager.getImportStatus(
							localAccount, ds);
					if (!isRunning) {
						synchronized (importStatus) {
							isRunning = importStatus.isRunning();
							hasRun = importStatus.hasRun();
						}
					}

					if (!hasRun) {
						synchronized (importStatus) {
							hasRun = importStatus.hasRun();
						}
					}

					if (!isRunning) {
						runningAccts.add(new ExternalIMAPAccount(userEmail, userLogin, userPassword, localAccount));
						break;
					} else if (hasRun) {
						finishedAccts.add(new ExternalIMAPAccount(userEmail, userLogin, userPassword, localAccount));
						break;
					}
				}
			}
			if (!isRunning && !hasRun) {
				idleAccts.add(new ExternalIMAPAccount(userEmail, userLogin, userPassword, localAccount));
			}
		}
		accts.put(accountState.idle, idleAccts);
		accts.put(accountState.running, runningAccts);
		accts.put(accountState.finished, finishedAccts);
		return accts;
	}
/**
 * Creates an external IMAP datasource for one-way IMAP import 
 * @param account
 * @param host
 * @param port
 * @param connectionType
 * @param accName
 * @param login
 * @param password
 * @param useAdminAuth
 * @return DataSource
 * @throws ServiceException
 */
	public static DataSource createIMAPDataSource(Account account, String host,
			String port, String connectionType, String accName, String login,
			String password, boolean useAdminAuth) throws ServiceException {
		Map<String, Object> dsAttrs = new HashMap<String, Object>();
		/*
		 * Right now we support only plain authorization mechanism which uses either AUTHORIZE PLAIN and LOGIN commands
		 * If we are using admin credentials, we have to use AUTHORIZE PLAIN command, which may be not supported by some IMAP servers
		 * If we are using user's credentials, we will use LOGIN command which is supported by all IMAP servers
		 */
		if (useAdminAuth) {
			StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceAuthMechanism,com.zimbra.cs.mailclient.auth.SaslAuthenticator.MECHANISM_PLAIN);
			// this is the account name for the mailbox. If authorizing as admin, this is the user's login/mailbox name
			StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceAuthorizationId, accName); 
		}

		StringUtil.addToMultiMap(dsAttrs, Provisioning.A_zimbraDataSourceHost,host);
		StringUtil.addToMultiMap(dsAttrs, Provisioning.A_zimbraDataSourcePort,port);
		//this is the login name used for authorization. If authorizing as admin, this is the admin's username
		StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceUsername, login); 
		StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourcePassword, password);
		StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceConnectionType, connectionType);
		StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceFolderId, "1");
		StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceImportOnly, "TRUE");
		StringUtil.addToMultiMap(dsAttrs, Provisioning.A_zimbraDataSourceIsInternal, "TRUE");
		StringUtil.addToMultiMap(dsAttrs, Provisioning.A_zimbraDataSourceName,ZimbraBulkProvisionExt.IMAP_IMPORT_DS_NAME);
		StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceEnabled, "TRUE");
		DataSource importDS = Provisioning.getInstance().createDataSource(account, DataSource.Type.imap,ZimbraBulkProvisionExt.IMAP_IMPORT_DS_NAME, dsAttrs);
		return importDS;
	}

}
