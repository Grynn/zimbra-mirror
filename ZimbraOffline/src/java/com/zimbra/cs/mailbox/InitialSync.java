/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpStatus;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.BufferStream;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.CopyInputStream;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.tar.TarEntry;
import com.zimbra.common.util.tar.TarInputStream;
import com.zimbra.common.util.zip.ZipShort;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.ZimbraNamespace;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineAccount.Version;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.mailbox.MailItem.UnderlyingData;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.calendar.IcalXmlStrMap;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.mime.ParsedDocument;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.mime.ParsedMessageOptions;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.redolog.op.CreateChat;
import com.zimbra.cs.redolog.op.CreateContact;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.redolog.op.CreateMessage;
import com.zimbra.cs.redolog.op.CreateMountpoint;
import com.zimbra.cs.redolog.op.CreateSavedSearch;
import com.zimbra.cs.redolog.op.CreateTag;
import com.zimbra.cs.redolog.op.RedoableOp;
import com.zimbra.cs.redolog.op.SaveChat;
import com.zimbra.cs.redolog.op.SaveDocument;
import com.zimbra.cs.redolog.op.SaveDraft;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.ContentServlet;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.formatter.SyncFormatter;
import com.zimbra.cs.service.mail.SetCalendarItem;
import com.zimbra.cs.service.mail.Sync;
import com.zimbra.cs.service.mail.SetCalendarItem.SetCalendarItemParseResult;
import com.zimbra.cs.service.util.ItemData;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.store.Blob;
import com.zimbra.cs.store.StoreManager;
import com.zimbra.cs.util.AccountUtil;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.soap.ZimbraSoapContext;

public class InitialSync {
    public static interface InviteMimeLocator {
        public Pair<Integer, InputStream> getInviteMime(int calendarItemId, int inviteId) throws ServiceException;
    }
    
    private static class RemoteInviteMimeLocator implements InviteMimeLocator {
        ZcsMailbox ombx;
        
        public RemoteInviteMimeLocator(ZcsMailbox mbox) {
            this.ombx = mbox;
        }
        
        public Pair<Integer, InputStream> getInviteMime(int calendarItemId, int inviteId) throws ServiceException {
            final String contentUrlPrefix = ContentServlet.SERVLET_PATH + ContentServlet.PREFIX_GET + "?" +
                                            ContentServlet.PARAM_MSGID + "=";
            String contentUrl = Offline.getServerURI(ombx.getAccount(), contentUrlPrefix + calendarItemId + "-" + inviteId);
            try {
                return UserServlet.getRemoteResourceAsStreamWithLength(ombx.getAuthToken(), contentUrl);
            } catch (IOException x) {
                throw ServiceException.FAILURE(contentUrl, x);
            }
        }
    }
    
    static final OfflineAccount.Version sMinDocumentSyncVersion     = new OfflineAccount.Version("5.0.6");
    static final OfflineAccount.Version sDocumentSyncHistoryVersion = new OfflineAccount.Version("5.0.9");
    
    static final String A_RELOCATED = "relocated";

    private static final TracelessContext sContext = new TracelessContext();

    private final ZcsMailbox ombx;
    private final MailboxSync mMailboxSync;
    private DeltaSync dsync;
    private Element syncResponse;
    private boolean interrupted;

    InitialSync(ZcsMailbox mbox) {
        ombx = mbox;
        mMailboxSync = ombx.getMailboxSync();
    }

    InitialSync(DeltaSync delta) {
        ombx = delta.getMailbox();
        mMailboxSync = ombx.getMailboxSync();
        dsync = delta;
    }

    ZcsMailbox getMailbox() {
        return ombx;
    }

    private DeltaSync getDeltaSync() {
        if (dsync == null)
            dsync = new DeltaSync(this);
        return dsync;
    }

    public static String sync(ZcsMailbox ombx) throws ServiceException {
        return new InitialSync(ombx).sync();
    }

    private String sync() throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.SYNC_REQUEST);
        syncResponse = ombx.sendRequest(request);
        
        OfflineLog.offline.debug(syncResponse.prettyPrint());
        
        String token = syncResponse.getAttribute(MailConstants.A_TOKEN);

        lastPeek = System.currentTimeMillis();
        
        OfflineSyncManager.getInstance().continueOK();
        
        OfflineLog.offline.debug("starting initial sync");
        mMailboxSync.saveSyncTree(syncResponse, token);
        initialFolderSync(syncResponse.getElement(MailConstants.E_FOLDER));
        mMailboxSync.recordInitialSyncComplete(token);
        OfflineLog.offline.debug("ending initial sync");

        return token;
    }

    public static String resume(ZcsMailbox ombx) throws ServiceException {
        return new InitialSync(ombx).resume();
    }

    public String resume() throws ServiceException {
        // do a NOOP before resuming to make sure the link is viable
        ombx.sendRequest(new Element.XMLElement(MailConstants.NO_OP_REQUEST));

        syncResponse = mMailboxSync.getSyncTree();
        String token = syncResponse.getAttribute(MailConstants.A_TOKEN);
        interrupted = true;

        lastPeek = System.currentTimeMillis();
        
        OfflineSyncManager.getInstance().continueOK();
        
        OfflineLog.offline.debug("resuming initial sync");
        initialFolderSync(syncResponse.getElement(MailConstants.E_FOLDER));
        mMailboxSync.recordInitialSyncComplete(token);
        OfflineLog.offline.debug("ending initial sync");

        return token;
    }
    
    private long lastPeek;
    private void peekForward() throws ServiceException {
        assert (!mMailboxSync.isInitialSyncComplete());
        
        PushChanges.sendPendingMessages(ombx, false);
        
        if (System.currentTimeMillis() - lastPeek > ombx.getSyncFrequency()) {
            getDeltaSync().sync();
            lastPeek = System.currentTimeMillis();
        }
    }
    
    private void checkpoint(int id) throws ServiceException {
        mMailboxSync.checkpointItem(id);
        peekForward();
    }

    static final Set<String> KNOWN_FOLDER_TYPES = new HashSet<String>(Arrays.asList(
            MailConstants.E_FOLDER, MailConstants.E_SEARCH, MailConstants.E_MOUNT
    ));
    
    private void initialFolderSync(Element elt) throws ServiceException {
        int folderId = (int) elt.getAttributeLong(MailConstants.A_ID);
        if (mMailboxSync.isFolderDone(folderId))
            return;

        // first, sync the container itself
        syncContainer(elt, folderId);

        // next, sync the leaf-node contents
        if (elt.getName().equals(MailConstants.E_FOLDER) || elt.getName().equals(MailConstants.E_MOUNT)) {
            if (folderId == Mailbox.ID_FOLDER_TAGS) {
                for (Element eTag : elt.listElements(MailConstants.E_TAG)) {
                    syncTag(eTag);
                    //eTag.detach();
                }
            }
            
            if (OfflineLC.zdesktop_sync_appointments.booleanValue()) {
                syncCalendarItems(folderId, elt.getOptionalElement(MailConstants.E_APPOINTMENT), MailItem.TYPE_APPOINTMENT);
            }
            if (OfflineLC.zdesktop_sync_tasks.booleanValue()) {
                syncCalendarItems(folderId, elt.getOptionalElement(MailConstants.E_TASK), MailItem.TYPE_TASK);
            }
            if (OfflineLC.zdesktop_sync_messages.booleanValue()) {
                syncMessagelikeItems(folderId, elt.getOptionalElement(MailConstants.E_MSG), MailItem.TYPE_MESSAGE);
            }
            if (OfflineLC.zdesktop_sync_chats.booleanValue()) {
                syncMessagelikeItems(folderId, elt.getOptionalElement(MailConstants.E_CHAT), MailItem.TYPE_CHAT);
            }

            if (OfflineLC.zdesktop_sync_contacts.booleanValue()) {
                Element eContactIds = elt.getOptionalElement(MailConstants.E_CONTACT);
                if (eContactIds != null) {
                    String ids = eContactIds.getAttribute(MailConstants.A_IDS);
                    for (Element eContact : fetchContacts(ombx, ids)) {
                        int contactId = (int)eContact.getAttributeLong(MailConstants.A_ID);
                        if (OfflineSyncManager.getInstance().isInSkipList(contactId)) {
                            OfflineLog.offline.warn("Skipped contact id=%d per zdesktop_sync_skip_idlist", contactId);
                            continue;
                        }
                        if (!isAlreadySynced(contactId, MailItem.TYPE_CONTACT))
                            syncContact(eContact, folderId);
                    }
                }
            }

            if (OfflineLC.zdesktop_sync_documents.booleanValue() &&
                    ombx.getRemoteServerVersion().isAtLeast(sMinDocumentSyncVersion)) {
                Element eDocIds = elt.getOptionalElement(MailConstants.E_DOC);
                if (eDocIds != null) {
                    syncAllDocumentsInFolder(folderId);
                }
                eDocIds = elt.getOptionalElement(MailConstants.E_WIKIWORD);
                if (eDocIds != null) {
                    syncAllDocumentsInFolder(folderId);
                }
            }
        }

        // now, sync the children (with special priority given to Tags, Inbox, Calendar, Contacts and Sent)
        if (folderId == Mailbox.ID_FOLDER_USER_ROOT) {
            prioritySync(elt, Mailbox.ID_FOLDER_TAGS);
            prioritySync(elt, Mailbox.ID_FOLDER_DRAFTS);
            prioritySync(elt, Mailbox.ID_FOLDER_INBOX);
            prioritySync(elt, Mailbox.ID_FOLDER_CALENDAR);
            prioritySync(elt, Mailbox.ID_FOLDER_CONTACTS);
            prioritySync(elt, Mailbox.ID_FOLDER_AUTO_CONTACTS);
            prioritySync(elt, Mailbox.ID_FOLDER_NOTEBOOK);
            prioritySync(elt, Mailbox.ID_FOLDER_BRIEFCASE);
            prioritySync(elt, Mailbox.ID_FOLDER_SENT);
        }

        for (Element child : elt.listElements()) {
            if (KNOWN_FOLDER_TYPES.contains(child.getName()))
                initialFolderSync(child);
        }

        mMailboxSync.checkpointFolder(folderId);
        peekForward();
    }
    
    private void syncCalendarItems(int folderId, Element idsElem, byte mailItemType) throws ServiceException {
        if (idsElem == null) {
            return;
        }
        int counter = 0;
        int lastItem = mMailboxSync.getLastSyncedItem();
        String itemIds = idsElem.getAttribute(MailConstants.A_IDS);
        for (String calId : itemIds.split(",")) {
            int id = Integer.parseInt(calId);
            if (OfflineSyncManager.getInstance().isInSkipList(id)) {
                OfflineLog.offline.warn("Skipped "+MailItem.getNameForType(mailItemType)+" id=%d per zdesktop_sync_skip_idlist", id);
                continue;
            }
                
            if (interrupted && lastItem > 0) {
                if (id != lastItem) {
                    continue;
                } else {
                    lastItem = 0;
                }
            }
            if (isAlreadySynced(id, mailItemType)) {
                continue;
            }
            try {
                syncCalendarItem(id, folderId, true);
                if (++counter % 100 == 0) {
                    checkpoint(id);
                }
            } catch (Exception t) {
                OfflineLog.offline.warn("failed to sync "+MailItem.getNameForType(mailItemType)+" id=" + id, t);
            }
        }
    }
    
    private void syncMessagelikeItems(int folderId, Element idsElem, byte mailItemType) throws ServiceException {
        if (idsElem != null) {
            String[] itemIds = idsElem.getAttribute(MailConstants.A_IDS).split(",");
            List<Integer> ids = new ArrayList<Integer>();
            for (String idStr : itemIds) {
                int id = Integer.parseInt(idStr);
                if (OfflineSyncManager.getInstance().isInSkipList(id)) {
                    OfflineLog.offline.warn("Skipped "+MailItem.getNameForType(mailItemType)+" id=%s per zdesktop_sync_skip_idlist", id);
                    continue;
                }
                ids.add(id);
            }
            syncMessagelikeItems(ids, folderId, mailItemType, false, false);
        }
    }
    
    private void prioritySync(Element elt, int priorityFolderId) throws ServiceException {
        for (Element child : elt.listElements()) {
            if (KNOWN_FOLDER_TYPES.contains(child.getName()) && (int) child.getAttributeLong(MailConstants.A_ID) == priorityFolderId) {
                initialFolderSync(child);
            }
        }
    }

    private boolean isAlreadySynced(int id, byte type) throws ServiceException {
        try {
            ombx.getItemById(sContext, id, type);
            return true;
        } catch (NoSuchItemException nsie) {
            boolean synced = ombx.isPendingDelete(sContext, id, type);
            if (!synced)
                interrupted = false;
            return synced;
        }
    }
    
    public void syncMessagelikeItems(List<Integer> ids, int folderId, byte type, boolean isForceSync, boolean isDeltaSync) throws ServiceException {
        int counter = 0, lastItem = mMailboxSync.getLastSyncedItem();
        List<Integer> itemList = new ArrayList<Integer>();
        for (int id : ids) {
            if (interrupted && lastItem > 0) {
                if (id != lastItem)  continue;
                else                 lastItem = 0;
            }
            if (!isForceSync && isAlreadySynced(id, MailItem.TYPE_UNKNOWN))
                continue;

            int batchSize = OfflineLC.zdesktop_sync_batch_size.intValue();
            if (ombx.getRemoteServerVersion().getMajor() < 5 || batchSize == 1) {
                syncMessage(id, folderId, type);
                if (++counter % 100 == 0 && !isDeltaSync)
                    checkpoint(id);
            } else {
                itemList.add(id);
                if ((++counter % batchSize) == 0) {
                    syncMessages(itemList, type);
                    if (!isDeltaSync)
                        checkpoint(id);
                    itemList.clear();
                }
            }
        }
        if (!itemList.isEmpty())
            syncMessages(itemList, type);
    }

    private void syncContainer(Element elt, int id) throws ServiceException {
        String type = elt.getName();
        if (type.equalsIgnoreCase(MailConstants.E_SEARCH))
            syncSearchFolder(elt, id);
        else if (type.equalsIgnoreCase(MailConstants.E_FOLDER) || type.equalsIgnoreCase(MailConstants.E_MOUNT))
            syncFolder(elt, id, type);
    }

    void syncSearchFolder(Element elt, int id) throws ServiceException {
        int parentId = (int) elt.getAttributeLong(MailConstants.A_FOLDER);
        String name = MailItem.normalizeItemName(elt.getAttribute(MailConstants.A_NAME));
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));

        long timestamp = elt.getAttributeLong(MailConstants.A_DATE, -1000);

        String query = elt.getAttribute(MailConstants.A_QUERY);
        String searchTypes = elt.getAttribute(MailConstants.A_SEARCH_TYPES);
        String sort = elt.getAttribute(MailConstants.A_SORTBY);

        boolean relocated = elt.getAttributeBool(A_RELOCATED, false) || !name.equals(elt.getAttribute(MailConstants.A_NAME));

        CreateSavedSearch redo = new CreateSavedSearch(ombx.getId(), parentId, name, query, searchTypes, sort, flags, new MailItem.Color(color));
        redo.setSearchId(id);
        redo.start(timestamp > 0 ? timestamp : System.currentTimeMillis());

        try {
            ombx.createSearchFolder(new TracelessContext(redo), parentId, name, query, searchTypes, sort, flags, color);
            if (relocated)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_SEARCHFOLDER, Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);
            OfflineLog.offline.debug("initial: created search folder (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            getDeltaSync().syncSearchFolder(elt, id);
        }
    }
   
    void syncFolder(Element elt, int id, String type) throws ServiceException {
        //system folders should be already created during mailbox initialization, but just in cases the server is of newer version
        //and there's a newly added system folder
        byte system = id < Mailbox.FIRST_USER_ID ? Folder.FOLDER_IS_IMMUTABLE : (byte)0;
        byte itemType = type.equals(MailConstants.E_FOLDER) ? MailItem.TYPE_FOLDER : MailItem.TYPE_MOUNTPOINT;

        int parentId = (id == Mailbox.ID_FOLDER_ROOT) ? id : (int) elt.getAttributeLong(MailConstants.A_FOLDER);
        String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : MailItem.normalizeItemName(elt.getAttribute(MailConstants.A_NAME));
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        byte view = MailItem.getTypeForName(elt.getAttribute(MailConstants.A_DEFAULT_VIEW, null));

        long timestamp = elt.getAttributeLong(MailConstants.A_DATE, -1000);

        ACL acl = parseACL(elt.getOptionalElement(MailConstants.E_ACL));
        String url = elt.getAttribute(MailConstants.A_URL, null);

        boolean relocated = elt.getAttributeBool(A_RELOCATED, false) || (id != Mailbox.ID_FOLDER_ROOT && !name.equals(elt.getAttribute(MailConstants.A_NAME)));

        RedoableOp redo;
        String ownerId = null;
        String ownerName = null;
        int remoteId = 0;
        if (itemType == MailItem.TYPE_FOLDER) {
            redo = new CreateFolder(ombx.getId(), name, parentId, system, view, flags, new MailItem.Color(color), url);
            ((CreateFolder)redo).setFolderId(id);
        } else {
            if (!OfflineLC.zdesktop_sync_mountpoints.booleanValue()) {
                OfflineLog.offline.debug("mountpoint sync is disabled in local config (zdesktop_sync_mountpoints=false). mountpoint skipped: " + name);
                return;
            }
                
            ownerName = elt.getAttribute(MailConstants.A_OWNER_NAME, null);
            if (ownerName == null) {
                OfflineLog.offline.debug(elt.toString());
                OfflineLog.offline.warn("missing owner attr of a mountpoint. mountpoint is not sync'ed down: " + name);
                return;
            }
            
            ownerId = elt.getAttribute(MailConstants.A_ZIMBRA_ID);
            remoteId = (int)elt.getAttributeLong(MailConstants.A_REMOTE_ID);
            OfflineProvisioning.getOfflineInstance().createMountpointAccount(ownerName, ownerId, ombx.getOfflineAccount());
            
            redo = new CreateMountpoint(ombx.getId(), parentId, name, ownerId, remoteId, view, flags, new MailItem.Color(color));
            ((CreateMountpoint)redo).setId(id);
        }
        redo.start(timestamp > 0 ? timestamp : System.currentTimeMillis());

        try {
            if (itemType == MailItem.TYPE_FOLDER) {
                // don't care about current feed syncpoint; sync can't be done offline
                ombx.createFolder(new TracelessContext(redo), name, parentId, system, view, flags, color, url);
            } else {
                ombx.createMountpoint(new TracelessContext(redo), parentId, name, ownerId, remoteId, view, flags, color);               
            }
            if (relocated)
                ombx.setChangeMask(sContext, id, itemType, Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);
            if (acl != null)
                ombx.setPermissions(sContext, id, acl);
            OfflineLog.offline.debug("initial: created folder (id=" + id + " type=" + type + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            OfflineLog.offline.debug("initial: folder already exists (id=" + id + " type=" + type + "): " + name);
            getDeltaSync().syncFolder(elt, id, type);
        }
    }

    ACL parseACL(Element eAcl) throws ServiceException {
        if (eAcl == null)
            return null;
        ACL acl = new ACL();
        for (Element eGrant : eAcl.listElements(MailConstants.E_GRANT)) {
            short rights = ACL.stringToRights(eGrant.getAttribute(MailConstants.A_RIGHTS));
            byte gtype = ACL.stringToType(eGrant.getAttribute(MailConstants.A_GRANT_TYPE));
            String zid = eGrant.getAttribute(MailConstants.A_ZIMBRA_ID, null);
            String name = eGrant.getAttribute(MailConstants.A_DISPLAY, null);
            String secret = null;
            if (gtype == ACL.GRANTEE_GUEST)
                secret = eGrant.getAttribute(MailConstants.A_PASSWORD, null);
            else if (gtype == ACL.GRANTEE_KEY)
                secret = eGrant.getAttribute(MailConstants.A_ACCESSKEY, null);
                
            ACL.Grant grant =  acl.grantAccess(zid, gtype, rights, secret);
            grant.setGranteeName(name);
        }
        return acl;
    }

    void syncTag(Element elt) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailConstants.A_ID);
        String name = MailItem.normalizeItemName(elt.getAttribute(MailConstants.A_NAME));
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);

        long timestamp = elt.getAttributeLong(MailConstants.A_DATE, -1000);

        boolean renamed = elt.getAttributeBool(A_RELOCATED, false) || !name.equals(elt.getAttribute(MailConstants.A_NAME));

        CreateTag redo = new CreateTag(ombx.getId(), name, new MailItem.Color(color));
        redo.setTagId(id);
        redo.start(timestamp > 0 ? timestamp : System.currentTimeMillis());

        try {
            // don't care about current feed syncpoint; sync can't be done offline
            ombx.createTag(new TracelessContext(redo), name, color);
            if (renamed)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_TAG, Change.MODIFIED_NAME);
            OfflineLog.offline.debug("initial: created tag (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            getDeltaSync().syncTag(elt);
        }
    }

    static List<Element> fetchContacts(ZcsMailbox ombx, String ids) throws ServiceException {
        try {
            Element request = new Element.XMLElement(MailConstants.GET_CONTACTS_REQUEST);
            request.addAttribute(MailConstants.A_SYNC, true);
            request.addElement(MailConstants.E_CONTACT).addAttribute(MailConstants.A_ID, ids);
            return ombx.sendRequest(request).listElements(MailConstants.E_CONTACT);
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_CONTACT))
                throw sfe;

            String[] contactIds = ids.split(",");
            if (contactIds.length <= 1)
                return Collections.emptyList();

            Element batch = new Element.XMLElement(ZimbraNamespace.E_BATCH_REQUEST);
            for (String id : contactIds) {
                Element request = batch.addElement(MailConstants.GET_CONTACTS_REQUEST);
                request.addAttribute(MailConstants.A_SYNC, true).addElement(MailConstants.E_CONTACT).addAttribute(MailConstants.A_ID, id);
            }
            List<Element> contacts = new ArrayList<Element>(contactIds.length - 1);
            for (Element response : ombx.sendRequest(batch).listElements(MailConstants.GET_CONTACTS_RESPONSE.getName()))
                contacts.addAll(response.listElements(MailConstants.E_CONTACT));
            return contacts;
        }
    }
    
    private static final Version MIN_ZCS_VER_CAL_NO_MIME = new Version(OfflineLC.zdesktop_min_zcs_version_cal_no_mime.value()); //5.0.15
    
    void syncCalendarItem(int id, int folderId, boolean isAppointment) throws ServiceException {
        OfflineSyncManager.getInstance().continueOK();
        
        try {
            Element request = new Element.XMLElement(isAppointment ? MailConstants.GET_APPOINTMENT_REQUEST : MailConstants.GET_TASK_REQUEST);
            request.addAttribute(MailConstants.A_ID, Integer.toString(id));
            
            if (!ombx.getRemoteServerVersion().isAtLeast(MIN_ZCS_VER_CAL_NO_MIME))
                request.addAttribute(MailConstants.A_CAL_INCLUDE_CONTENT, 1);
            
            request.addAttribute(MailConstants.A_SYNC, 1);
            Element response = ombx.sendRequest(request);
            //OfflineLog.offline.debug(response.prettyPrint());
            
            Element calElement = response.getElement(isAppointment ? MailConstants.E_APPOINTMENT : MailConstants.E_TASK);
            String flagsStr = calElement.getAttribute(MailConstants.A_FLAGS, null);
            int flags = flagsStr != null ? Flag.flagsToBitmask(flagsStr) : 0;
            String tagsStr = calElement.getAttribute(MailConstants.A_TAGS, null);
            long tags = tagsStr != null ? Tag.tagsToBitmask(tagsStr) : 0;
            
            long timestamp = calElement.getAttributeLong(MailConstants.A_CAL_DATETIME, -1000);
            
            Element setCalRequest = makeSetCalRequest(calElement, new RemoteInviteMimeLocator(ombx), null, ombx.getAccount(), isAppointment);
            if (ombx.getOfflineAccount().isDebugTraceEnabled())
                OfflineLog.offline.debug(setCalRequest.prettyPrint());
            
            try {
                setCalendarItem(setCalRequest, id, folderId, timestamp, flags, tags, isAppointment);
            } catch (Exception x) {
                SyncExceptionHandler.syncCalendarFailed(ombx, id, setCalRequest.prettyPrint(), x);
            }
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.info("initial: %s %d has been deleted; skipping", isAppointment ? "appoitment" : "task", id);
        } catch (SoapFaultException sfe) {
            if (!(sfe.getCode().equals(MailServiceException.NO_SUCH_ITEM) || sfe.getCode().equals(MailServiceException.NO_SUCH_CALITEM))) {
                handleCalendarSyncException(sfe, id);
            } else {
                OfflineLog.offline.warn("NO_SUCH_ITEM while syncing item [%d] in folder [%d]. Must have been deleted from server while sync in progress.", id, folderId);
            }
        } catch (Exception x) {
            handleCalendarSyncException(x, id);
        }
    }
    
    private void handleCalendarSyncException(Exception x, int id) throws ServiceException {
        SyncExceptionHandler.checkRecoverableException("InitialSync.syncCalendarItem", x);
        SyncExceptionHandler.syncCalendarFailed(ombx, id, x);
    }

    //Massage the GetAppointmentResponse/GetTaskResponse into a SetAppointmentReqeust/SetTaskRequest
    static Element makeSetCalRequest(Element resp, InviteMimeLocator imLocator, ZMailbox remoteZMailbox, Account account, boolean isAppointment) throws ServiceException {
        String calId = resp.getAttribute(MailConstants.A_ID);
        
        Element req = new Element.XMLElement(isAppointment ? MailConstants.SET_APPOINTMENT_REQUEST : MailConstants.SET_TASK_REQUEST);
        req.addAttribute(MailConstants.A_FOLDER, resp.getAttribute(MailConstants.A_FOLDER));
        req.addAttribute(MailConstants.A_FLAGS, resp.getAttribute(MailConstants.A_FLAGS, ""));
        req.addAttribute(MailConstants.A_TAGS, resp.getAttribute(MailConstants.A_TAGS, ""));
        long nextAlarm = resp.getAttributeLong(MailConstants.A_CAL_NEXT_ALARM, 0);
        if (nextAlarm > 0)
            req.addAttribute(MailConstants.A_CAL_NEXT_ALARM, nextAlarm);
        else
            req.addAttribute(MailConstants.A_CAL_NO_NEXT_ALARM, true);
        
           // for each <inv>
        for (Iterator<Element> iter = resp.elementIterator(MailConstants.E_INVITE); iter.hasNext();) {
            Element inv = iter.next();
            Element comp = inv.getElement(MailConstants.E_INVITE_COMPONENT);
            String uid = comp.getAttribute("x_uid", null); //for some reason GetAppointment returns "x_uid" instead of "uid"
            if (uid != null) {
                comp.addAttribute(MailConstants.A_UID, uid);
            }
            
            String recurId = inv.getAttribute(MailConstants.A_CAL_RECURRENCE_ID, null);
            Element newInv = null;
            if (recurId == null) {
                newInv = req.addElement(MailConstants.A_DEFAULT);
            } else {
                //SetAppointment expects <exceptId> in <comp>
                int colon = recurId.lastIndexOf(':');
                String tz = colon > 0 ? recurId.substring(0, colon) : null;
                String dt = colon > 0 ? recurId.substring(colon + 1) : recurId;
                
                Element e = comp.addElement(MailConstants.E_CAL_EXCEPTION_ID);
                e.addAttribute(MailConstants.A_CAL_DATETIME, dt);
                if (tz != null) {
                    if (tz.startsWith("TZID=")) {
                        tz = tz.substring(5);
                    }
                    e.addAttribute(MailConstants.A_CAL_TIMEZONE, tz);
                }
                
                if (comp.getAttribute(MailConstants.A_STATUS, "").equalsIgnoreCase(IcalXmlStrMap.STATUS_CANCELLED)) {
                    newInv = req.addElement(MailConstants.E_CAL_CANCEL);
                } else {
                    newInv = req.addElement(MailConstants.E_CAL_EXCEPT);
                }
            }
            
            HIT: {
                for (Iterator<Element> i = comp.elementIterator(MailConstants.E_CAL_ATTENDEE); i.hasNext();) {
                    ZAttendee attendee = ZAttendee.parse(i.next());
                    if (AccountUtil.addressMatchesAccount(account, attendee.getAddress())) {
                        newInv.addAttribute(MailConstants.A_CAL_PARTSTAT, attendee.getPartStat());
                        break HIT;
                    }
                }
                newInv.addAttribute(MailConstants.A_CAL_PARTSTAT, IcalXmlStrMap.PARTSTAT_NEEDS_ACTION);
            }
            
            if (comp.getAttribute(MailConstants.A_CAL_DATETIME, null) ==  null) {
                //4.5 back compat.  Set DTSTAMP to -1 and SetCalendarItem will correct it using iCal's DTSTAMP
                comp.addAttribute(MailConstants.A_CAL_DATETIME, -1);
            }
            
            //Deal with MIME
            boolean mpOK = false;
            Element topMp = inv.getOptionalElement(MailConstants.E_MIMEPART);
            if (topMp != null) {
                //even if <mp> is present, it may still be missing attachments in which case we fall back to retrieving content separately
                mpOK = true;
                if (topMp.getAttribute(MailConstants.A_CONTENT_TYPE).startsWith("multipart")) {
                    List<Element> subMps = topMp.listElements(MailConstants.E_MIMEPART);
                    for (Element e : subMps) {
                        if (e.getOptionalElement(MailConstants.E_MIMEPART) == null &&
                                e.getOptionalElement(MailConstants.E_CONTENT) == null) {
                            mpOK = false;
                            break;
                        }
                    }
                }
            }
            
            Element msg = newInv.addElement(MailConstants.E_MSG);
            msg.addElement(inv.detach());
            if (mpOK) {
                msg.addElement(topMp.detach());
            } else {
                boolean noBlob = comp.getAttributeBool(MailConstants.A_CAL_NO_BLOB, false);
                if (!noBlob) { //we need to include the invite message
                    int invId = (int)inv.getAttributeLong(MailConstants.A_ID);
                    Pair<Integer, InputStream> pair = imLocator.getInviteMime(Integer.parseInt(calId), invId);
                    InputStream invStream = pair.getSecond();
                    String filename = "inv-" + calId + "-" + invId;
                    String uploadId = null;
                    try {
                        if (remoteZMailbox != null) { //upload to remote
                            int invMsgSize = pair.getFirst();
                            int timeout = OfflineLC.http_connection_timeout.intValue() + (invMsgSize > 0 ? (invMsgSize / 25000 * (int)Constants.MILLIS_PER_SECOND) : 0);
                            uploadId = remoteZMailbox.uploadContentAsStream(filename, invStream, MimeConstants.CT_MESSAGE_RFC822, invMsgSize, timeout);
                        } else { //upload to local
                            try {
                                uploadId = FileUploadServlet.saveUpload(invStream, filename, MimeConstants.CT_MESSAGE_RFC822, account.getId()).getId();
                            } catch (IOException x) {
                                throw ServiceException.FAILURE("upload failed: " + filename, x);
                            }
                        }
                    } finally {
                        try {
                            invStream.close();
                        } catch (IOException x) {}
                    }
                    msg.addAttribute(MailConstants.A_ATTACHMENT_ID, uploadId);
                } else {
                    // Add plain/html MIME parts for backward compatibility with older server.
                    String desc = null, descHtml = null;
                    Element descElem = comp.getOptionalElement(MailConstants.E_CAL_DESCRIPTION);
                    if (descElem != null)
                        desc = descElem.getText();
                    Element descHtmlElem = comp.getOptionalElement(MailConstants.E_CAL_DESC_HTML);
                    if (descHtmlElem != null)
                        descHtml = descHtmlElem.getText();
                    if ((desc != null && desc.length() > 0) || (descHtml != null && descHtml.length() > 0)) {
                        Element multiAltMp = msg.addElement(MailConstants.E_MIMEPART);
                        multiAltMp.addAttribute(MailConstants.A_CONTENT_TYPE, MimeConstants.CT_MULTIPART_ALTERNATIVE);
                        multiAltMp.addAttribute(MailConstants.A_BODY, "TEXT");
                        int estimatedMultiAltSize =
                            (desc != null ? desc.length() : 0) + (descHtml != null ? descHtml.length() : 0);
                        multiAltMp.addAttribute(MailConstants.A_SIZE, estimatedMultiAltSize);
                        int part = 1;
                        if (desc != null && desc.length() > 0) {
                            Element plainMp = multiAltMp.addElement(MailConstants.E_MIMEPART);
                            plainMp.addAttribute(MailConstants.A_CONTENT_TYPE, MimeConstants.CT_TEXT_PLAIN);
                            plainMp.addAttribute(MailConstants.A_SIZE, desc.length());
                            plainMp.addAttribute(MailConstants.A_BODY, true);
                            plainMp.addAttribute(MailConstants.A_PART, part);
                            ++part;
                            desc = StringUtil.stripControlCharacters(desc);
                            plainMp.addAttribute(MailConstants.E_CONTENT, desc, Element.Disposition.CONTENT);
                        }
                        if (descHtml != null && descHtml.length() > 0) {
                            Element htmlMp = multiAltMp.addElement(MailConstants.E_MIMEPART);
                            htmlMp.addAttribute(MailConstants.A_CONTENT_TYPE, MimeConstants.CT_TEXT_HTML);
                            htmlMp.addAttribute(MailConstants.A_SIZE, descHtml.length());
                            htmlMp.addAttribute(MailConstants.A_BODY, true);
                            htmlMp.addAttribute(MailConstants.A_PART, part);
                            // descHtml is already clean, so don't repeat defang or stripping of control chars.
                            htmlMp.addAttribute(MailConstants.E_CONTENT, descHtml, Element.Disposition.CONTENT);
                        }
                    }
                }
            }
            
            req.addElement(newInv);
        }
        
        Element replies = resp.getOptionalElement(MailConstants.E_CAL_REPLIES);
        if (replies != null) {
            req.addElement(replies.detach());
        }
        
        //OfflineLog.offline.debug(req.prettyPrint());
        
        return req;
    }
    
    private void setCalendarItem(Element request, int itemId, int folderId, long timestamp, int flags, long tags, boolean isAppointment)
    throws ServiceException {
        // make a fake context to trick the parser so that we can reuse the soap parsing code
        ZimbraSoapContext zsc = new ZimbraSoapContext(AuthProvider.getAuthToken(getMailbox().getAccount()), getMailbox().getAccountId(), SoapProtocol.Soap12, SoapProtocol.Soap12);
        Folder folder = getMailbox().getFolderById(folderId);
        SetCalendarItemParseResult parsed = SetCalendarItem.parseSetAppointmentRequest(request, zsc, sContext, folder, isAppointment ? MailItem.TYPE_APPOINTMENT : MailItem.TYPE_TASK, true);
        
        com.zimbra.cs.redolog.op.SetCalendarItem player = new com.zimbra.cs.redolog.op.SetCalendarItem(ombx.getId(), true, flags, tags);
        player.setData(parsed.defaultInv, parsed.exceptions, parsed.replies, parsed.nextAlarm);
        if (parsed.defaultInv != null)
            player.setCalendarItemPartStat(parsed.defaultInv.mInv.getPartStat());
        player.setCalendarItemAttrs(itemId, folderId);
        player.start(timestamp > 0 ? timestamp : System.currentTimeMillis());
        
        try {
             TracelessContext ctxt = new TracelessContext(player);
             ombx.setCalendarItem(ctxt, folderId, flags, tags, parsed.defaultInv, parsed.exceptions, parsed.replies, parsed.nextAlarm);
             if (OfflineLog.offline.isDebugEnabled()) {
                 String name = null;
                 if (parsed.defaultInv != null)
                     name = parsed.defaultInv.mInv.getName();
                 else if (parsed.exceptions != null && parsed.exceptions.length > 0)
                     name = parsed.exceptions[0].mInv.getName();
                 OfflineLog.offline.debug("initial: created %s %d: %s", isAppointment ? "appointment" : "task", itemId, name);
             }
        } catch (Exception x) {
            throw ServiceException.FAILURE("Failed setting calendar item id=" + itemId, x);
        }
    }

    void syncContact(Element elt, int folderId) throws ServiceException {
        OfflineSyncManager.getInstance().continueOK();
        
        int id = (int) elt.getAttributeLong(MailConstants.A_ID);
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));
        String tags = elt.getAttribute(MailConstants.A_TAGS, null);

        Map<String, String> fields = new HashMap<String, String>();
        for (Element eField : elt.listElements())
            if (!eField.getName().equals(MailConstants.E_METADATA)) //we should deal with metadata sync when there's api to set them
                fields.put(eField.getAttribute(Element.XMLElement.A_ATTR_NAME), eField.getText());

        long timestamp = elt.getAttributeLong(MailConstants.A_DATE, -1000);

        byte[] blob = null;
        OfflineAccount acct = ombx.getOfflineAccount();
        if ((flags & Flag.BITMASK_ATTACHED) != 0) {
            String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH + "/~/?fmt=native&id=" + id);
            if (acct.isDebugTraceEnabled())
                OfflineLog.request.debug("GET " + url);
            try {
                blob = UserServlet.getRemoteResource(ombx.getAuthToken(), url).getSecond();
            } catch (MailServiceException.NoSuchItemException nsie) {
                OfflineLog.offline.warn("initial: no blob available for contact " + id);
            } catch (Exception x) {
                SyncExceptionHandler.checkRecoverableException("InitialSync.syncContact", x);
                SyncExceptionHandler.syncContactFailed(ombx, id, x);
                return;
            }
        }
        
        ParsedContact pc;
        try {
            pc = new ParsedContact(fields, blob);
        } catch (ServiceException x) {
            OfflineLog.offline.debug("contact parse error: id=%d", id);
            OfflineLog.offline.warn("unable to parse contact entry; item skipped.", x);
            return;
        }

        CreateContact redo = new CreateContact(ombx.getId(), folderId, pc, tags);
        redo.setContactId(id);
        redo.start(timestamp > 0 ? timestamp : System.currentTimeMillis());
        
        try {
            Contact cn = ombx.createContact(new TracelessContext(redo), pc, folderId, tags);
            if (flags != 0)
                ombx.setTags(sContext, id, MailItem.TYPE_CONTACT, flags, MailItem.TAG_UNCHANGED);
            if (color != MailItem.DEFAULT_COLOR)
                ombx.setColor(sContext, id, MailItem.TYPE_CONTACT, color);
            OfflineLog.offline.debug("initial: created contact (" + id + "): " + cn.getFileAsString());
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            getDeltaSync().syncContact(elt, folderId);
        }
    }


    private static final Map<String, String> USE_SYNC_FORMATTER = new HashMap<String, String>();
        static {
            USE_SYNC_FORMATTER.put(UserServlet.QP_FMT, "sync");
            USE_SYNC_FORMATTER.put(SyncFormatter.QP_NOHDR, "1");
        }

    private static final byte[] ZIP_EXTRA_FIELD_HEADER_ID_X_ZIMBRA_HEADERS = { (byte) 0xFF, (byte) 0xFF };
        
    private Map<String, String> recoverHeadersFromBytes(byte[] hdrBytes) {
        Map<String, String> headers = new HashMap<String, String>();
        byte[] bytes = hdrBytes;
            
        // If it starts with 0xFFFF [len] it is the new-style data.  If it doesn't, it must be
        // old-style data from when we weren't doing zip extra field correctly.
        if (hdrBytes != null && hdrBytes.length >= 4) {
            if (hdrBytes[0] == ZIP_EXTRA_FIELD_HEADER_ID_X_ZIMBRA_HEADERS[0] &&
                hdrBytes[1] == ZIP_EXTRA_FIELD_HEADER_ID_X_ZIMBRA_HEADERS[1]) {
                int len = ZipShort.getValue(hdrBytes, 2);
                if (len == hdrBytes.length - 4) {
                    bytes = new byte[len];
                    System.arraycopy(hdrBytes, 4, bytes, 0, len);
                }
            }
        }
        if (bytes != null && bytes.length > 0) {
            String[] keyVals = new String(bytes).split("\r\n");
            for (String hdr : keyVals) {
                int delim = hdr.indexOf(": ");
                headers.put(hdr.substring(0, delim), hdr.substring(delim + 2));
            }
        }
        return headers;
    }
    
    private static final Version MIN_ZCS_VER_SYNC_TGZ = new Version(OfflineLC.zdesktop_min_zcs_version_sync_tgz.value()); //5.0.9
    
    private void syncMessages(List<Integer> ids, byte type) throws ServiceException {
        OfflineSyncManager.getInstance().continueOK();
        
        if (ombx.getRemoteServerVersion().isAtLeast(MIN_ZCS_VER_SYNC_TGZ))
            syncMessagesAsTgz(ids, type);
        else
            syncMessagesAsZip(ids, type);
    }
    
    private static byte[] readTarEntry(TarInputStream tis, TarEntry te) throws
        IOException {
        if (te == null)
            return null;
        
        int dsz = (int)te.getSize();
        byte[] data;
        
        if (dsz == 0)
            return null;
        data = new byte[dsz];
        if (tis.read(data, 0, dsz) != dsz)
            throw new IOException("archive read err");
        return data;
    }

    private void syncMessagesAsTgz(List<Integer> ids, byte type) throws ServiceException {
        OfflineAccount acct = ombx.getOfflineAccount();
        UserServlet.HttpInputStream in = null;
        Pair<Header[], UserServlet.HttpInputStream> response;
        TarInputStream tin = null;
        
        try {
            String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH +
                "/~/?fmt=tgz&list=" + StringUtil.join(",", ids));
            
            if (acct.isDebugTraceEnabled())
                OfflineLog.request.debug("GET " + url);
            try {
                response = UserServlet.getRemoteResourceAsStream(ombx.getAuthToken(), url);
                in = response.getSecond();
            } catch (MailServiceException.NoSuchItemException nsie) {
                OfflineLog.offline.info("initial: messages have been deleted; skipping");
                return;
            } catch (IOException x) {
                OfflineLog.offline.error("initial: can't read sync response: " + url, x);
                throw ServiceException.FAILURE("can't read sync response: " + url, x);
            }
            if (in.getStatusCode() == HttpStatus.SC_NO_CONTENT) {
                SyncExceptionHandler.saveFailureReport(ombx, ids.get(0),
                    "missing msg ids " + ids.toString() + " from server response", null);
                return;
            }
            
            Set<Integer> idSet = new HashSet<Integer>();
            idSet.addAll(ids);
            try {
                int msgId = 0;
                TarEntry te;

                tin = new TarInputStream(new GZIPInputStream(in), "UTF-8");
                while ((te = tin.getNextEntry()) != null) {
                    if (te.getName().endsWith(".meta")) {
                        ItemData itemData = new ItemData(readTarEntry(tin, te));
                        UnderlyingData ud = itemData.ud;
                        
                        assert (ud.type == type);
                        assert (ud.getBlobDigest() != null);
                        msgId = ud.id;
                        te = tin.getNextEntry(); //message always has a blob
                        if (te != null) {
                            try {
                                saveMessage(tin, te.getSize(), ud.id, ud.folderId,
                                    type, ud.date, Flag.flagsToBitmask(itemData.flags),
                                    ud.tags, ud.parentId);
                                idSet.remove(ud.id);
                            } catch (Exception x) {
                                SyncExceptionHandler.checkRecoverableException("InitialSync.syncMessagesAsTgz", x);
                                SyncExceptionHandler.syncMessageFailed(ombx, ud.id, x);
                            }
                        } else {
                            throw new RuntimeException("missing blob entry reading tgz stream");
                        }
                    } else if (te.getName().endsWith(".err")) {
                        OfflineLog.offline.warn("server returned error on message %d", msgId);
                    } else {
                        OfflineLog.offline.warn("failed to sync message due to missing meta entry reading tgz stream");
                    }
                }
            } catch (IOException x) {
                boolean empty = false;          // workaround bug in tar formatter
                
                for (Header hdr : response.getFirst()) {
                    if (hdr.getName().equalsIgnoreCase("Content-Length"))
                        if (hdr.getValue().equals("0"))
                            empty = true;
                }
                if (!empty)
                    throw ServiceException.FAILURE("TarInputStream", x);
            }
            if (!idSet.isEmpty()) {
                SyncExceptionHandler.saveFailureReport(ombx, idSet.iterator().next(),
                    "missing msg ids " + idSet.toString() + " from server response", null);
            }
        } finally {
            ByteUtil.closeStream(tin);
        }
    }
    
    private void syncMessagesAsZip(List<Integer> ids, byte type) throws ServiceException {
        UserServlet.HttpInputStream in = null;
        String zlv = OfflineLC.zdesktop_sync_zip_level.value();
        OfflineAccount acct = ombx.getOfflineAccount();
        ZipInputStream zin = null;

        try {
            String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH +
                "/~/?fmt=zip&zlv=" + zlv + "&list=" + StringUtil.join(",", ids));
            
            if (acct.isDebugTraceEnabled())
                OfflineLog.request.debug("GET " + url);
            try {
                Pair<Header[], UserServlet.HttpInputStream> response = UserServlet.getRemoteResourceAsStream(ombx.getAuthToken(), url);
                in = response.getSecond();
            } catch (MailServiceException.NoSuchItemException nsie) {
                OfflineLog.offline.info("initial: messages have been deleted; skipping");
                return;
            } catch (IOException x) {
                OfflineLog.offline.error("initial: can't read sync response: " + url, x);
                throw ServiceException.FAILURE("can't read sync response: " + url, x);
            }
            if (in == null) {
                SyncExceptionHandler.saveFailureReport(ombx, ids.get(0),
                    "missing msg ids " + ids.toString() + " from server response", null);
                return;
            }
            
            try {
                ZipEntry entry;

                zin = new ZipInputStream(in);
                while ((entry = zin.getNextEntry()) != null) {
                    Map<String, String> headers = recoverHeadersFromBytes(entry.getExtra());
                    int id = Integer.parseInt(headers.get("X-Zimbra-ItemId"));
                    int folderId = Integer.parseInt(headers.get("X-Zimbra-FolderId"));
                    
                    InputStream fin = new FilterInputStream(zin) {
                        @Override
                        public void close() {
                            //so that we can block the call to close()
                        }
                    };
                    try {
                        saveMessage(fin, entry.getSize(), headers, id, folderId, type);
                    } catch (Exception x) {
                        SyncExceptionHandler.checkRecoverableException("InitialSync.syncMessagesAsZip", x);
                        SyncExceptionHandler.syncMessageFailed(ombx, id, x);
                    }
                }
            } catch (IOException x) {
                OfflineLog.offline.error("Invalid sync format", x);
            }
        } finally {
            ByteUtil.closeStream(zin);
        }
    }
        
    void syncMessage(int id, int folderId, byte type) throws ServiceException {
        OfflineSyncManager.getInstance().continueOK();

        Map<String, String> headers = new HashMap<String, String>();

        OfflineAccount acct =ombx.getOfflineAccount();
        String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH + "/~/?fmt=sync&nohdr=1&id=" + id);
        if (acct.isDebugTraceEnabled())
            OfflineLog.request.debug("GET " + url);
        try {
            Pair<Header[], UserServlet.HttpInputStream> response = UserServlet.getRemoteResourceAsStream(
                    ombx.getAuthToken(), url);
            for (Header hdr : response.getFirst())
                headers.put(hdr.getName(), hdr.getValue());
            
            saveMessage(response.getSecond(), response.getSecond().getContentLength(), headers, id, folderId, type);
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.info("initial: message " + id + " has been deleted; skipping");
        } catch (IOException e) {
            OfflineLog.offline.error("initial: can't retrieve message from " + url, e);
            throw ServiceException.FAILURE("can't retrieve message from " + url, e);
        } catch (Exception x) {
            SyncExceptionHandler.checkRecoverableException("InitialSync.syncMessage", x);
            SyncExceptionHandler.syncMessageFailed(ombx, id, x);
        }
    }
    
    private void saveMessage(InputStream in, long sizeHint, Map<String, String> headers, int id, int folderId, byte type) throws ServiceException {
        int received = (int) (Long.parseLong(headers.get("X-Zimbra-Received")) / 1000);
        int flags = Flag.flagsToBitmask(headers.get("X-Zimbra-Flags"));
        long tags = Tag.tagsToBitmask(headers.get("X-Zimbra-Tags"));
        int convId = Integer.parseInt(headers.get("X-Zimbra-Conv"));

        saveMessage(in, sizeHint, id, folderId, type, received, flags, tags, convId);
    }
    
    private void saveMessage(InputStream in, long sizeHint, int id, int folderId,
        byte type, int received, int flags, long tags, int convId) throws ServiceException {
        Blob blob = null;
        int bufLen = Provisioning.getInstance().getLocalServer().getMailDiskStreamingThreshold();
        CopyInputStream cs = new CopyInputStream(in, sizeHint, bufLen, bufLen);
        BufferStream bs = cs.getBufferStream();
        byte data[] = null;
        String digest = null;
        ParsedMessage pm = null;
        CreateMessage redo;
        int size = 0;
        String tagStr = Tag.bitmaskToTags(tags);

        OfflineSyncManager.getInstance().continueOK();
        if (convId < 0)
            convId = Mailbox.ID_AUTO_INCREMENT;
        try {
            blob = StoreManager.getInstance().storeIncoming(cs, null);
            data = bs.isPartial() ? null : bs.getBuffer();
            OfflineLog.offline.debug("message id=%d streamed to %s", id,
                data == null ? blob.getPath() : "memory" );
            size = (int) blob.getRawSize();
        } catch (Exception e) {
            throw ServiceException.FAILURE("Unable to read/write message id=" + id, e);
        }
        try {
            digest = blob.getDigest();
            pm = new ParsedMessage(new ParsedMessageOptions(blob, data,
                received * 1000L, false));
            
            if (type == MailItem.TYPE_CHAT)
                redo = new CreateChat(ombx.getId(), digest, size, folderId, flags, tagStr);
            else
                redo = new CreateMessage(ombx.getId(), null, received, false,
                    digest, size, folderId, true, flags, tagStr, null);
            cs.release();
            redo.setMessageId(id);
            redo.setConvId(convId);
            redo.start(received * 1000L);

            // FIXME: not syncing COLOR
            // XXX: need to call with noICal = false
            Message msg;
            if (type == MailItem.TYPE_CHAT) {
                msg = ombx.createChat(new TracelessContext(redo), pm, folderId, flags, tagStr);
            } else {
                DeliveryContext deliveryCtxt = new DeliveryContext();

                deliveryCtxt.setIncomingBlob(blob);
                msg = ombx.addMessage(new TracelessContext(redo), pm, folderId,
                    true, flags, tagStr, convId, ":API:", null, deliveryCtxt);
            }
            OfflineLog.offline.debug("initial: created " + MailItem.getNameForType(type) + " (" + id + "): " + msg.getSubject());
            StoreManager.getInstance().quietDelete(blob);
            return;
        } catch (IOException e) {
            StoreManager.getInstance().quietDelete(blob);
            if (e.getMessage() != null && e.getMessage().startsWith("Unable to rename")) {
                SyncExceptionHandler.syncMessageFailed(ombx, id, pm, size, e);
                return;
            }
            throw ServiceException.FAILURE("storing " + MailItem.getNameForType(type) + " " + id, e);
        } catch (Exception e) {
            if (e instanceof ServiceException && ((ServiceException)e).getCode().equals(MailServiceException.NO_SUCH_FOLDER)) {
                OfflineLog.offline.debug("initial: moved " + MailItem.getNameForType(type) + " (" + id + ")");
                StoreManager.getInstance().quietDelete(blob);
                return; //message moved on server; we'll get that when we do delta
            } else if (!(e instanceof ServiceException) || !((ServiceException)e).getCode().equals(MailServiceException.ALREADY_EXISTS)) {
                SyncExceptionHandler.syncMessageFailed(ombx, id, pm, size, e);
                StoreManager.getInstance().quietDelete(blob);
                return;
            }
            // fall through...
        } finally {
            cs.release();
        }

        // if we're here, the message already exists; save new draft if needed, then update metadata
        try {
            Message msg = ombx.getMessageById(sContext, id);
            if (type == MailItem.TYPE_CHAT || msg.isDraft()) { //other messages are immutable so no content change
                CreateMessage redo2 = null;
                if (type == MailItem.TYPE_CHAT)
                    redo2 = new SaveChat(ombx.getId(), id, digest, size, folderId, flags, tagStr);
                else
                    redo2 = new SaveDraft(ombx.getId(), id, digest, size);
                redo2.start(received * 1000L);

                synchronized (ombx) {
                    int change_mask = ombx.getChangeMask(sContext, id, type);
                    if ((change_mask & Change.MODIFIED_CONTENT) == 0) {
                        if (type == MailItem.TYPE_CHAT)
                            ombx.updateChat(new TracelessContext(redo2), pm, id);
                        else
                            ombx.saveDraft(new TracelessContext(redo2), pm, id);
                        OfflineLog.offline.debug("initial: updated " + MailItem.getNameForType(type) + " content (" + id + "): " + msg.getSubject());
                    } else {
                        OfflineLog.offline.debug("initial: %s %d (%s) content updated locally, will overwrite remote change", MailItem.getNameForType(type), id, msg.getSubject());
                    }
                }
            }
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.debug("initial: " + MailItem.getNameForType(type) + " " + id + " has been deleted; no need to sync draft");
            return;
        } catch (IOException e) {
            throw ServiceException.FAILURE("storing message " + id, e);
        } finally {
            StoreManager.getInstance().quietDelete(blob);
        }

        // use this data to generate the XML entry used in message delta sync
        Element sync = new Element.XMLElement(Sync.elementNameForType(type)).addAttribute(MailConstants.A_ID, id);
        sync.addAttribute(MailConstants.A_FLAGS, Flag.bitmaskToFlags(flags)).addAttribute(MailConstants.A_TAGS, tagStr).addAttribute(MailConstants.A_CONV_ID, convId);
        sync.addAttribute(MailConstants.A_DATE, received * 1000L);
        getDeltaSync().syncMessage(sync, folderId, type);
    }
    
    /* sync all the documents in the folder */
    void syncAllDocumentsInFolder(int folderId) throws ServiceException {
        if (ombx.getRemoteServerVersion().isAtLeast(sDocumentSyncHistoryVersion)) {
            syncDocument("query=inid:"+folderId);
            return;
        }

        Element request = new Element.XMLElement(MailConstants.SEARCH_REQUEST);
        request.addAttribute(MailConstants.A_QUERY_LIMIT, 1024);  // XXX pagination
        request.addAttribute(MailConstants.A_TYPES, "document,wiki");
        request.addElement(MailConstants.E_QUERY).setText("inid:"+folderId);
        Element response = ombx.sendRequest(request);
        
        OfflineLog.offline.debug(response.prettyPrint());
        
        for (Element doc : response.listElements(MailConstants.E_DOC))
            syncDocument(doc);
        for (Element doc : response.listElements(MailConstants.E_WIKIWORD))
            syncDocument(doc);
    }
    
    // for 5.0.6 without tar formatter
    void syncDocument(Element doc) throws ServiceException {
        OfflineSyncManager.getInstance().continueOK();
        
        byte type = doc.getName().equals(MailConstants.E_WIKIWORD) ? MailItem.TYPE_WIKI : MailItem.TYPE_DOCUMENT; 
        int folderId = (int) doc.getAttributeLong(MailConstants.A_FOLDER);
        long modifiedDate = doc.getAttributeLong(MailConstants.A_MODIFIED_DATE);
        String lastEditedBy = doc.getAttribute(MailConstants.A_LAST_EDITED_BY);
        String itemIdStr = doc.getAttribute(MailConstants.A_ID);
        String name = doc.getAttribute(MailConstants.A_NAME);
        String contentType = doc.getAttribute(MailConstants.A_CONTENT_TYPE, "text/html");
        String description = doc.getAttribute(MailConstants.A_DESC, null);
        int flags = Flag.flagsToBitmask(doc.getAttribute(MailConstants.A_FLAGS, null));
        //String tags = doc.getAttribute(MailConstants.A_TAGS, null);
        int version = (int) doc.getAttributeLong(MailConstants.A_VERSION);
        InputStream rs = null;
        OfflineAccount acct = ombx.getOfflineAccount();
        String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH + "/~/?fmt=native&id=" + itemIdStr);
        if (acct.isDebugTraceEnabled())
            OfflineLog.request.debug("GET " + url);
        try {
            rs = UserServlet.getRemoteResourceAsStream(ombx.getAuthToken(), url).getSecond();
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.warn("initial: no blob available for document " + itemIdStr);
        } catch (IOException e) {
            OfflineLog.offline.warn("initial: can't download Document:  " + itemIdStr);
        } catch (Exception x) {
            SyncExceptionHandler.checkRecoverableException("InitialSync.syncDocument", x);
            SyncExceptionHandler.syncDocumentFailed(ombx, Integer.parseInt(itemIdStr), x);
            return;
        }

        try {
            ItemId itemId = new ItemId(itemIdStr, ombx.getAccountId());
            int id = itemId.getId();
            int date = (int) (modifiedDate / 1000);
            int change = ombx.getChangeMask(null, id, type);
            if (change > 0) {
                OfflineLog.offline.info("skipping locally modified item "+id+" (change "+change+")");
                return;
            }
            SaveDocument player = new SaveDocument();
            ParsedDocument pd = new ParsedDocument(rs, name, contentType, modifiedDate, lastEditedBy, description);
            player.setDocument(pd);
            player.setItemType(type);
            player.setMessageId(id);

            // XXX sync tags
            
            try {
                ombx.getItemById(sContext, id, MailItem.TYPE_UNKNOWN);
                ombx.addDocumentRevision(new TracelessContext(player), id, pd);
            } catch (MailServiceException.NoSuchItemException nsie) {
                ombx.createDocument(new TracelessContext(player), folderId, pd, type);
            }
            if (flags != 0)
                ombx.setTags(sContext, id, type, flags, MailItem.TAG_UNCHANGED);
            ombx.syncDate(sContext, id, type, date);
            ombx.setSyncedVersionForMailItem(itemIdStr, version);
            OfflineLog.offline.debug("initial: created document (" + itemIdStr + "): " + name);
        } catch (ServiceException e) {
            OfflineLog.offline.warn("initial: error saving a document:  " + itemIdStr, e);
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
        } catch (IOException e) {
            OfflineLog.offline.warn("initial: error saving a document:  " + itemIdStr, e);
        }
    }
    
    // tar formatter
    void syncDocument(String query) throws ServiceException {
        OfflineSyncManager.getInstance().continueOK();
        
        OfflineAccount acct = ombx.getOfflineAccount();
        String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH + "/~/?fmt=tgz&" + query);
        if (acct.isDebugTraceEnabled())
            OfflineLog.request.debug("GET " + url);
        UserServlet.HttpInputStream rs = null;
        int lastId = 0;
        int id = 0;
        try {
            rs = UserServlet.getRemoteResourceAsStream(ombx.getAuthToken(), url).getSecond();
            int statusCode = rs.getStatusCode();
            if (statusCode != 200) {
                OfflineLog.offline.warn("initial: remote server returned an error " + statusCode);
                return;
            }
            TarInputStream tis = new TarInputStream(new GZIPInputStream(rs), "UTF-8");
            TarEntry te;

            while ((te = tis.getNextEntry()) != null) {
                if (!te.getName().endsWith(".meta")) {
                    OfflineLog.offline.warn("skipping tar entry " + te.getName());
                    continue;
                }

                ItemData itemData = new ItemData(readTarEntry(tis, te));
                UnderlyingData ud = itemData.ud;
                
                MailItem item = MailItem.constructItem(ombx, ud);  // metadata only
                if (item.getType() != MailItem.TYPE_DOCUMENT && item.getType() != MailItem.TYPE_WIKI)
                    continue;

                id = item.getId();
                te = tis.getNextEntry();
                if (te == null) {
                    OfflineLog.offline.warn("unexpected EOF while reading TarInputStream.  current itemId="+id);
                    break;
                }

                int change = ombx.getChangeMask(null, id, item.getType());
                if (change > 0) {
                    OfflineLog.offline.info("skipping locally modified item "+id+" (change "+change+")");
                    continue;
                }
                
                if (id != lastId) {
                    // delete the local item so it can be reconstructed from scratch
                    int ids[] = new int[1];
                    ids[0] = id;
                    ombx.delete(sContext, ids, MailItem.TYPE_UNKNOWN, null);
                    lastId = id;
                }
                InputStream eofIn = new EofInputStream(tis, te.getSize());

                Document doc = (Document)item;
                SaveDocument player = new SaveDocument();
                ParsedDocument pd = new ParsedDocument(eofIn, doc.getName(), doc.getContentType(), doc.getDate(), doc.getCreator(), doc.getDescription());
                player.setDocument(pd);
                player.setItemType(doc.getType());
                player.setMessageId(doc.getId());

                // XXX sync tags

                try {
                    ombx.getItemById(sContext, id, MailItem.TYPE_UNKNOWN);
                    ombx.addDocumentRevision(new TracelessContext(player), id, pd);
                } catch (MailServiceException.NoSuchItemException nsie) {
                    try {
                        ombx.createDocument(new TracelessContext(player), doc.getFolderId(), pd, doc.getType());
                    } catch (MailServiceException me) {
                        if (me.getCode().equals(MailServiceException.ALREADY_EXISTS)) {
                            // this is an edge case where a different object of
                            // the same name already exists on Desktop.  when the object
                            // was created it should have been pushed to the server
                            // so it's unclear how it ended up with such
                            // inconsistency.  but evidently bug 33541
                            // triggered this case.
                            // rename the old item so we can sync the server item
                            // down to Desktop.
                            String name = doc.getName();
                            String path = ombx.getFolderById(doc.getFolderId()).getPath() + "/" + name;
                            MailItem oldItem = ombx.getItemByPath(sContext, path);
                            String newName = path + "-old";
                            ombx.rename(sContext, oldItem.mId, MailItem.TYPE_UNKNOWN, newName);
                            ombx.createDocument(new TracelessContext(player), doc.getFolderId(), pd, doc.getType());
                        } else
                            throw me;
                    }
                }
                int flags = doc.getFlagBitmask();
                if (flags != 0)
                    ombx.setTags(sContext, id, doc.getType(), flags, MailItem.TAG_UNCHANGED);
                ombx.syncDate(sContext, id, doc.getType(), (int)(doc.getDate() / 1000L));
                //ombx.setSyncedVersionForMailItem(itemIdStr, version);
                OfflineLog.offline.debug("initial: created document (" + id + "): " + doc.getName());
            }
        } catch (Exception x) {
            SyncExceptionHandler.checkRecoverableException("InitialSync.syncDocument", x);
            SyncExceptionHandler.syncDocumentFailed(ombx, id, x);
        } finally {
            if (rs != null)
                rs.close();
        }
    }
    
    // for use with codes that expects to read InputStream until EOF
    private static class EofInputStream extends InputStream {
        private InputStream mIn;
        private long mSize;
        private long mPos;
        public EofInputStream(InputStream in, long size) {
            mIn = in;
            mSize = size;
            mPos = 0;
        }
        public int read() throws IOException {
            if (mPos == mSize)
                return -1;
            mPos++;
            return mIn.read();
        }
    }
}
