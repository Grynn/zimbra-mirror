package com.zimbra.bp;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import com.zimbra.bp.BulkIMAPImportTaskManager.taskKeys;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.common.soap.AdminExtConstants;
import com.zimbra.common.account.Key.AccountBy;
import com.zimbra.soap.admin.type.DataSourceType;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.datasource.ImportStatus;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminFileDownload;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
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
        Server localServer = Provisioning.getInstance().getLocalServer();
        checkRight(zsc, context, localServer, Admin.R_createMigrationTask);
        String op = request.getAttribute(AdminExtConstants.A_op);
        Element response = zsc.createElement(AdminExtConstants.BULK_IMAP_DATA_IMPORT_RESPONSE);
        Map<accountState, List<ExternalIMAPAccount>> IMAPAccounts = null;
        String IMAPhost = null, IMAPport = null, adminLogin = null, adminPassword = null, connectionType = null, sourceServerType = null;
        String sourceType = request.getElement(AdminExtConstants.A_sourceType).getTextTrim();
        String indexBatchSize = ZimbraBulkProvisionExt.DEFAULT_INDEX_BATCH_SIZE;
        boolean useAdminLogin = false;
        if (sourceType.equalsIgnoreCase(AdminFileDownload.FILE_FORMAT_BULK_XML)) {
            String aid = request.getElement(AdminExtConstants.E_attachmentID).getTextTrim();
            ZimbraLog.extensions.debug("Uploaded XML file id = " + aid);
            FileUploadServlet.Upload up = FileUploadServlet.fetchUpload(zsc.getAuthtokenAccountId(), aid, zsc.getAuthToken());
            if (up == null) {
                throw ServiceException.FAILURE("Uploaded XML file with id " + aid + " was not found.", null);
            }

            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(up.getInputStream());
                org.dom4j.Element root = doc.getRootElement();

                if (!root.getName().equals(AdminExtConstants.E_ZCSImport)) {
                    throw new DocumentException("Bulk provisioning XML file's root element must be " + AdminExtConstants.E_ZCSImport);
                }
                Iterator rootIter = root.elementIterator(AdminExtConstants.E_ImportUsers);
                if (!rootIter.hasNext()) {
                    throw new DocumentException("Cannot find element " + AdminExtConstants.E_ImportUsers + " in uploaded bulk provisioning XML file");
                }
                org.dom4j.Element elImportUsers = (org.dom4j.Element) rootIter.next();

                IMAPAccounts = parseExternalIMAPAccounts(elImportUsers, zsc);

                Iterator connectionTypeIter = root.elementIterator(AdminExtConstants.E_connectionType);
                if (connectionTypeIter.hasNext()) {
                    org.dom4j.Element elConnectionType = (org.dom4j.Element) connectionTypeIter.next();
                    connectionType = elConnectionType.getTextTrim();
                }
                Iterator sourceServerTypeIter = root.elementIterator(AdminExtConstants.E_sourceServerType);
                if (sourceServerTypeIter.hasNext()) {
                    org.dom4j.Element elSourceServerType = (org.dom4j.Element) sourceServerTypeIter.next();
                    sourceServerType = elSourceServerType.getTextTrim();
                }
                Iterator IMAPHostIter = root.elementIterator(AdminExtConstants.E_IMAPHost);
                if (IMAPHostIter.hasNext()) {
                    org.dom4j.Element elIMAPHost = (org.dom4j.Element) IMAPHostIter.next();
                    IMAPhost = elIMAPHost.getTextTrim();
                }

                Iterator IMAPPortIter  = root.elementIterator(AdminExtConstants.E_IMAPPort);
                if (IMAPPortIter.hasNext()) {
                    org.dom4j.Element elIMAPPort = (org.dom4j.Element) IMAPPortIter.next();
                    IMAPport = elIMAPPort.getTextTrim();
                }

                Iterator IndexBatchSizeIter  = root.elementIterator(AdminExtConstants.E_indexBatchSize);
                if (IndexBatchSizeIter.hasNext()) {
                    org.dom4j.Element elIxBatchSize = (org.dom4j.Element) IndexBatchSizeIter.next();
                    indexBatchSize = elIxBatchSize.getTextTrim();
                }

                Iterator useAdminLoginIter = root.elementIterator(AdminExtConstants.E_useAdminLogin);
                if (useAdminLoginIter.hasNext()) {
                    org.dom4j.Element elUseAdminLogin = (org.dom4j.Element) useAdminLoginIter.next();
                    useAdminLogin = "1".equalsIgnoreCase(elUseAdminLogin.getTextTrim());
                    if (useAdminLogin) {
                        Iterator adminLoginIter = root.elementIterator(AdminExtConstants.E_IMAPAdminLogin);
                        if (adminLoginIter.hasNext()) {
                            org.dom4j.Element elAdminLogin = (org.dom4j.Element) adminLoginIter.next();
                            adminLogin = elAdminLogin.getTextTrim();
                        }

                        Iterator adminPassIter = root.elementIterator(AdminExtConstants.E_IMAPAdminPassword);
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
        } else if (sourceType.equalsIgnoreCase(ZimbraBulkProvisionExt.FILE_FORMAT_ZIMBRA)) {
            IMAPAccounts = getZimbraAccounts(request, zsc);
        } else {
        	throw ServiceException.INVALID_REQUEST(
        			String.format("Invalid value of %s parameter: %s. Allowed values: %s, %s",
        					AdminExtConstants.A_sourceType, sourceType, ZimbraBulkProvisionExt.FILE_FORMAT_ZIMBRA,AdminFileDownload.FILE_FORMAT_BULK_XML), null);
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
                Element elRunningAccounts = response.addElement(AdminExtConstants.E_runningAccounts);
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
        Element elConnectionType = request.getOptionalElement(AdminExtConstants.E_connectionType);
        if (elConnectionType != null) {
            connectionType = elConnectionType.getTextTrim();
        }
        Element elSourceServerType = request.getOptionalElement(AdminExtConstants.E_sourceServerType);
        if (elSourceServerType != null) {
            sourceServerType = elSourceServerType.getTextTrim();
        }
        Element elIMAPHost = request.getOptionalElement(AdminExtConstants.E_IMAPHost);
        if (elIMAPHost != null) {
            IMAPhost = elIMAPHost.getTextTrim();
        }

        Element elIMAPPort = request.getOptionalElement(AdminExtConstants.E_IMAPPort);
        if (elIMAPPort != null) {
            IMAPport = elIMAPPort.getTextTrim();
        }

        Element elBatchSize = request.getOptionalElement(AdminExtConstants.E_indexBatchSize);
        if (elBatchSize != null) {
            indexBatchSize = elBatchSize.getTextTrim();
        }

        Element elUseAdminLogin = request.getOptionalElement(AdminExtConstants.E_useAdminLogin);
        if (elUseAdminLogin != null) {
            useAdminLogin = "1".equalsIgnoreCase(elUseAdminLogin.getTextTrim());
        }

        if (useAdminLogin) {
            Element elAdminLogin = request.getOptionalElement(AdminExtConstants.E_IMAPAdminLogin);
            if (elAdminLogin != null) {
                adminLogin = elAdminLogin.getTextTrim();
            }
            Element elAdminPassword = request.getOptionalElement(AdminExtConstants.E_IMAPAdminPassword);
            if (elAdminPassword != null) {
                adminPassword = elAdminPassword.getTextTrim();
            }
        }

        if (ZimbraBulkProvisionExt.OP_PREVIEW.equalsIgnoreCase(op)) {
            /*
             * Do not start the import. Just generate a preview. We will count
             * idle and non-idle accounts.
             */
            response.addElement(AdminExtConstants.E_totalCount).setText(Integer.toString(numIdleAccounts + numRunningAccounts + numFinishedAccounts));
            response.addElement(AdminExtConstants.E_idleCount).setText(Integer.toString(numIdleAccounts));
            response.addElement(AdminExtConstants.E_runningCount).setText(Integer.toString(numRunningAccounts));
            response.addElement(AdminExtConstants.E_finishedCount).setText(Integer.toString(numFinishedAccounts));

            response.addElement(AdminExtConstants.E_connectionType).setText(connectionType);
            response.addElement(AdminExtConstants.E_IMAPHost).setText(IMAPhost);
            response.addElement(AdminExtConstants.E_IMAPPort).setText(IMAPport);
            response.addElement(AdminExtConstants.E_indexBatchSize).setText(indexBatchSize);
            if(useAdminLogin) {
                response.addElement(AdminExtConstants.E_useAdminLogin).setText("1");
                response.addElement(AdminExtConstants.E_IMAPAdminLogin).setText(adminLogin);
                response.addElement(AdminExtConstants.E_IMAPAdminPassword).setText(adminPassword);
            } else {
                response.addElement(AdminExtConstants.E_useAdminLogin).setText("0");
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
                                    + AdminExtConstants.E_useAdminLogin
                                    + " option is selected!", null);
                }

            }
            /*
             * Create the import queue
             */
            Queue<HashMap<taskKeys, String>> queue = BulkIMAPImportTaskManager.createQueue(zsc.getAuthtokenAccountId());
            Queue<HashMap<taskKeys, String>> runningQ = BulkIMAPImportTaskManager.createRunningQueue(zsc.getAuthtokenAccountId());
            Iterator<ExternalIMAPAccount> idleAccIter = idleAccounts.iterator();
            try {
                int size = Integer.parseInt(indexBatchSize);
                if(size <=0)
                    indexBatchSize = ZimbraBulkProvisionExt.DEFAULT_INDEX_BATCH_SIZE;
            } catch (Exception e) {
                indexBatchSize = ZimbraBulkProvisionExt.DEFAULT_INDEX_BATCH_SIZE;
            }
            while (idleAccIter.hasNext()) {
                ExternalIMAPAccount acct = idleAccIter.next();
                HashMap<taskKeys, String> task = new HashMap<taskKeys, String>();
                String dataSourceID = createIMAPDataSource(
                        acct.getAccount(),
                        IMAPhost,
                        IMAPport,
                        connectionType,
                        sourceServerType,
                        acct.getUserEmail(),
                        useAdminLogin ? adminLogin
                                : acct.getUserLogin(),
                        useAdminLogin ? adminPassword
                                : acct.getUserPassword(),
                                indexBatchSize,useAdminLogin).getId();
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
                            && ds.getType() == DataSourceType.imap
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

        for (Iterator userIter = root.elementIterator(AdminExtConstants.E_User); userIter.hasNext();) {
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
                if (AdminExtConstants.E_ExchangeMail.equalsIgnoreCase(el.getName())) {
                    userEmail = el.getTextTrim();
                }
                if (AdminExtConstants.E_remoteEmail.equalsIgnoreCase(el.getName())) {
                    userEmail = el.getTextTrim();
                }
                if (AdminExtConstants.E_remoteIMAPLogin.equalsIgnoreCase(el.getName())) {
                    userLogin = el.getTextTrim();
                }
                if (AdminExtConstants.E_remoteIMAPPassword.equalsIgnoreCase(el.getName())) {
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
                        && ds.getType() == DataSourceType.imap
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
            String port, String connectionType, String sourceServerType, String accName, String login,
            String password, String batchSize, boolean useAdminAuth) throws ServiceException {
        Map<String, Object> dsAttrs = new HashMap<String, Object>();
        /*
         * Right now we support only plain authorization mechanism which uses either AUTHORIZE PLAIN and LOGIN commands
         * If we are using admin credentials, we have to use AUTHORIZE PLAIN command, which may be not supported by some IMAP servers
         * If we are using user's credentials, we will use LOGIN command which is supported by all IMAP servers
         */
        if (useAdminAuth) {
            StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceAuthMechanism,com.zimbra.cs.mailclient.auth.SaslAuthenticator.PLAIN);
            // this is the account name for the mailbox. If authorizing as admin, this is the user's login/mailbox name
            StringUtil.addToMultiMap(dsAttrs,Provisioning.A_zimbraDataSourceAuthorizationId, accName);
        }
        if(ZimbraBulkProvisionExt.EXCHANGE_IMAP.equalsIgnoreCase(sourceServerType)) {
            StringUtil.addToMultiMap(dsAttrs, Provisioning.A_zimbraDataSourceDomain,".msexchange");
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
        DataSource importDS = Provisioning.getInstance().createDataSource(account, DataSourceType.imap,ZimbraBulkProvisionExt.IMAP_IMPORT_DS_NAME, dsAttrs);
        Map<String, Object> accAttrs = new HashMap<String, Object>();
        StringUtil.addToMultiMap(accAttrs,Provisioning.A_zimbraBatchedIndexingSize, batchSize);
        Provisioning.getInstance().modifyAttrs(account, accAttrs, true);
        return importDS;
    }

   public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_createMigrationTask);
        relatedRights.add(Admin.R_modifyAccount);
        relatedRights.add(Admin.R_adminLoginAs);

        notes.add(String.format("Admin has to have %s and %s rights for each account that is being migrated.",Admin.R_modifyAccount.getName(),Admin.R_adminLoginAs.getName()));
        notes.add(Admin.R_createMigrationTask + " right is required in order to access this SOAP handler.");
    }
}
