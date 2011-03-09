package com.zimbra.cs.mailbox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.ElementHandler;
import org.dom4j.ElementPath;

import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.Element.XMLElement;
import com.zimbra.common.util.Constants;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineGal;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbDataSource;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbPool;
import com.zimbra.cs.db.DbDataSource.DataSourceItem;
import com.zimbra.cs.db.DbMailItem.QueryParams;
import com.zimbra.cs.db.DbPool.DbConnection;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.common.OfflineConstants;

public class GalSyncSAXHandler implements ElementHandler {
    public static final String PATH_RESPONSE = "/Envelope/Body/SyncGalResponse";
    public static final String PATH_CN = PATH_RESPONSE + "/cn";
    public static final String PATH_DELETED = PATH_RESPONSE + "/deleted";

    private OfflineAccount galAccount;
    private boolean fullSync;
    private ZcsMailbox mainMbox;
    private Mailbox galMbox;
    private OperationContext context;
    private Exception exception = null;
    private String token = null;
    private int syncFolder;
    private ArrayList<String> idGroups = null;
    private int grpSize = OfflineLC.zdesktop_gal_sync_group_size.intValue();
    private int idCount;
    private DataSource ds;
    private OfflineProvisioning prov;

    public GalSyncSAXHandler(ZcsMailbox mainMbox, OfflineAccount galAccount, boolean fullSync, boolean trace) 
        throws ServiceException {
        this.mainMbox = mainMbox;
        this.galAccount = galAccount;
        this.fullSync = fullSync;
        prov = OfflineProvisioning.getOfflineInstance();        
        galMbox = MailboxManager.getInstance().getMailboxByAccountId(galAccount.getId(), false);
        context = new OperationContext(galMbox);
        
        ds = GalSyncUtil.createDataSourceForAccount(galAccount);
        
        syncFolder = OfflineGal.getSyncFolder(galMbox, context, fullSync).getId();
        OfflineLog.offline.debug("Offline GAL current sync folder: " + Integer.toString(syncFolder));
    }

    public String getToken() { return token; }
    public OfflineAccount getGalAccount() { return galAccount; }
    public Exception getException() { return exception; }
    public int getGroupCount() { return idGroups == null ? 0 : idGroups.size(); }
    public String removeGroup() { return idGroups.remove(0); }

    @Override
    public void onStart(ElementPath elPath) { //TODO: add trace logging;
        String path = elPath.getPath();
        if (!path.equals(PATH_RESPONSE))
            return;

        org.dom4j.Element row = elPath.getCurrent();
        token = row.attributeValue(AdminConstants.A_TOKEN);
        if (token == null) {
            OfflineLog.offline.debug("Offline GAL parse error: SyncGalResponse has no token attribute");
            unregisterHandlers(elPath);
            return;
        }

        try {                        
            if (fullSync) {
                OfflineLog.offline.debug("Offline GAL full sync requested: " + galAccount.getName());
                galMbox.emptyFolder(context, syncFolder, false);
                DbDataSource.deleteAllMappings(ds);
            }
        } catch (Exception e) {
            handleException(e, elPath);
        }
    }

    @Override
    public void onEnd(ElementPath elPath) { //TODO: add trace logging;
        String path = elPath.getPath();
        if (!path.equals(PATH_CN) && !path.equals(PATH_DELETED))
            return;

        if (token == null) {
            OfflineLog.offline.debug("Offline GAL parse error: missing SyncGalResponse tag");
            unregisterHandlers(elPath);
            return;
        }

        org.dom4j.Element row = elPath.getCurrent();
        String id = row.attributeValue(AdminConstants.A_ID);
        if (id == null) {
            OfflineLog.offline.debug("Offline GAL parse error: cn has no id attribute");
        } else if (path.equals(PATH_DELETED)) {
            try {
                deleteContact(id);
            } catch (Exception e) {
                handleException(e, elPath);
            }
        } else {
            Iterator<org.dom4j.Element> itr = row.elementIterator();
            if (itr.hasNext()) {
                Map<String, String> map = new HashMap<String, String>();
                map.put(OfflineConstants.GAL_LDAP_DN, id);
                while(itr.hasNext()) {
                    org.dom4j.Element child = (org.dom4j.Element) itr.next();
                    String key = child.attributeValue(AdminConstants.A_N);
                    if (!key.equals("objectClass"))
                        map.put(key, child.getText());
                }

                try {
                    saveContact(id, map);
                } catch (Exception e) {
                    handleException(e, elPath);
                }
            } else { // Ids Only
                if (idGroups == null)
                    idGroups = new ArrayList<String>();
                if (idGroups.size() == 0 || idCount >= grpSize) {
                    idGroups.add(id);
                    idCount = 1;
                } else {
                    int i = idGroups.size() - 1;
                    idGroups.set(i, idGroups.get(i) + "," + id);
                    idCount++;
                }
            }
        }

        row.detach(); // done with this node - prune it off to save memory
    }

    private void unregisterHandlers(ElementPath elPath) {
        elPath.removeHandler(PATH_CN);
        elPath.removeHandler(PATH_DELETED);
    }
    
    private void handleException(Exception e, ElementPath elPath) {
        OfflineLog.offline.debug("Offline GAL exception caught",e);
        if (e instanceof ServiceException || e instanceof IOException) {
            exception = e;
        }
        unregisterHandlers(elPath);
    }
    
    private void deleteContact(String id) throws ServiceException, IOException {
        int iid = GalSyncUtil.findContact(id, ds);
        if (iid > 0) {
            // always delete mapping first, so that in case of crash, unmapped contacts can be cleaned up in runMaintenance()
            DbDataSource.deleteMapping(ds, iid);
            galMbox.delete(context, iid, MailItem.Type.CONTACT);
            OfflineLog.offline.debug("Offline GAL contact deleted: " + Integer.toString(iid) + ", " + id);
        }
    }
    
    private void createContact(ParsedContact contact, String id, String logstr) throws ServiceException {
        Contact c = galMbox.createContact(context, contact, syncFolder, null);
        DbDataSource.addMapping(ds, new DataSourceItem(0, c.getId(), id, null));
        OfflineLog.offline.debug("Offline GAL contact created: " + logstr);
    }

    private void saveContact(String id, Map<String, String> map) throws ServiceException, IOException {
        String fullName = map.get(ContactConstants.A_fullName);
        if (fullName == null) {
            String fname = map.get(ContactConstants.A_firstName);
            String lname = map.get(ContactConstants.A_lastName);
            fullName = fname == null ? "" : fname;
            if (lname != null)
                fullName = fullName + (fullName.length() > 0 ? " " : "") + lname;
            if (fullName.length() > 0)
                map.put(ContactConstants.A_fullName, fullName);
        }
        String type = map.get(ContactConstants.A_type);
        if (type == null) {
            type = map.get(OfflineGal.A_zimbraCalResType) == null ? OfflineGal.CTYPE_ACCOUNT : OfflineGal.CTYPE_RESOURCE;
            map.put(ContactConstants.A_type, type);
        }
        String logstr = "id=" + id + " name=\"" + fullName + "\"" + " type=\""+type+"\"";
        if (type.equals(OfflineGal.CTYPE_GROUP) && mainMbox.getRemoteServerVersion().isAtLeast7xx()) {
            String dlName = map.get(ContactConstants.A_email);
            String dlMembers = GalSyncUtil.fetchDlMembers(dlName, mainMbox);
            if (dlMembers == null) {
                OfflineLog.offline.debug("No members in dlist %s",dlName);
            } else {
                map.put(ContactConstants.A_member, dlMembers);
            }
        }
        
        ParsedContact contact = new ParsedContact(map);
        if (fullSync) {
            createContact(contact, id, logstr);
        } else {
            int itemId = GalSyncUtil.findContact(id, ds);
            if (itemId > 0) {
                try {
                    galMbox.modifyContact(context, itemId, contact);
                    OfflineLog.offline.debug("Offline GAL contact modified: " + logstr);
                } catch (MailServiceException.NoSuchItemException e) {
                    OfflineLog.offline.warn("Offline GAL modify error - no such contact: " + logstr + " itemId=" + Integer.toString(itemId));
                }
            } else {
                createContact(contact, id, logstr);
            }
        }
    }
    
    private void removeUnmapped() throws ServiceException {
        List<Integer> folderIds = new ArrayList<Integer>();
        folderIds.add(OfflineGal.getSyncFolder(galMbox, context, false).getId());
        QueryParams params = new QueryParams();
        params.setFolderIds(folderIds);
        
        DbConnection conn = null;
        Set<Integer> galItemIds = null;
        synchronized (galMbox) {
            try {
                conn = DbPool.getConnection();            
                galItemIds = DbMailItem.getIds(galMbox, conn, params, false);
            } finally {
                DbPool.quietClose(conn);
            }
        }        
        if (galItemIds == null || galItemIds.size() == 0) {
            return;
        }
        
        Collection<DataSourceItem> dsItems = DbDataSource.getAllMappings(ds);
        int sz = dsItems.size();
        if (sz < galItemIds.size()) { // proceed only if mapping size is less than number of gal entries
            Set<Integer> dsItemIds = new HashSet<Integer>(sz);
            for (DataSourceItem dsi: dsItems) {
                dsItemIds.add(dsi.itemId);
            }
            
            try {
                galItemIds.removeAll(dsItemIds);
            } catch (Exception e) {
                OfflineLog.offline.warn("Offline GAL error in calculating set difference: " + e.getMessage());
                return;
            }
            
            if (galItemIds.size() > 100) {
                prov.setAccountAttribute(mainMbox.getOfflineAccount(), OfflineConstants.A_offlineGalAccountSyncToken, "");
                OfflineLog.offline.warn("Offline GAL too many unmapped items: " +
                    Integer.toString(galItemIds.size()) + ", falling back to full sync.");
            } else {
                for (Integer id : galItemIds) {
                    galMbox.delete(context, id.intValue(), MailItem.Type.CONTACT);
                }
                OfflineLog.offline.debug("Offline GAL deleted " + Integer.toString(galItemIds.size()) + " unmapped items.");
            }
        }
    }
    
    public void fetchContacts() throws ServiceException, IOException {
        XMLElement req = new XMLElement(MailConstants.GET_CONTACTS_REQUEST);
        req.addElement(AdminConstants.E_CN).addAttribute(AccountConstants.A_ID, removeGroup());
        Element response = mainMbox.sendRequest(req, true, true, OfflineLC.zdesktop_gal_sync_request_timeout.intValue(), SoapProtocol.Soap12);

        List<Element> contacts = response.listElements(MailConstants.E_CONTACT);
        for(Element elt : contacts) {
            String id = elt.getAttribute(AccountConstants.A_ID);
            Map<String, String> fields = new HashMap<String, String>();
            fields.put(OfflineConstants.GAL_LDAP_DN, id);
            for (Element eField : elt.listElements()) {
                String name = eField.getAttribute(Element.XMLElement.A_ATTR_NAME);
                if (!name.equals("objectClass"))
                    fields.put(name, eField.getText());
            }
            saveContact(id, fields);
        }
    }
    
    public void runMaintenance() {
        long lastRefresh = galAccount.getLongAttr(OfflineConstants.A_offlineGalAccountLastRefresh, 0);
        long interval = OfflineLC.zdesktop_gal_refresh_interval_days.longValue();
        if (lastRefresh > 0 && (System.currentTimeMillis() - lastRefresh) / Constants.MILLIS_PER_DAY < interval)
            return;

        try {
            OfflineLog.offline.debug("Offline GAL running maintenance");
            removeUnmapped();
            galMbox.optimize(null, 0);            
            prov.setAccountAttribute(galAccount, OfflineConstants.A_offlineGalAccountLastRefresh,
                Long.toString(System.currentTimeMillis()));
        } catch (ServiceException e) {
            OfflineLog.offline.warn("Offline GAL maintenance error: " + e.getMessage());
        }
    }
}
