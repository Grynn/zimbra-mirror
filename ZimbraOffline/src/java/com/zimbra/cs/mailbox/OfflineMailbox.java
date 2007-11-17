/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapHttpTransport;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.mailbox.MailItem.TargetConstraint;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.redolog.op.RedoableOp;
import com.zimbra.cs.servlet.ZimbraServlet;
import com.zimbra.cs.session.PendingModifications;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.store.StoreManager;

public class OfflineMailbox extends Mailbox {

    public static class OfflineContext extends OperationContext {
        public OfflineContext()                 { super((RedoableOp) null); }
        public OfflineContext(RedoableOp redo)  { super(redo); }
    }

    public static final int ID_FOLDER_ARCHIVE = 253;
    public static final int ID_FOLDER_OUTBOX = 254;
    public static final int FIRST_OFFLINE_ITEM_ID = 2 << 29;

    private String mSessionId;
    
    private MailboxSync mMailboxSync = new MailboxSync(this);

    private Map<Integer,Integer> mRenumbers = new HashMap<Integer,Integer>();
    private Set<Integer> mLocalTagDeletes = new HashSet<Integer>();

    OfflineMailbox(MailboxData data) throws ServiceException {
        super(data);
    }

    @Override
    public MailSender getMailSender() {
        return new OfflineMailSender();
    }
    
    public void sync(boolean isOnRequest) throws ServiceException {
    	mMailboxSync.sync(isOnRequest);
    }

    MailboxSync getMailboxSync() {
    	return mMailboxSync;
    }
    
    OfflineAccount getOfflineAccount() throws ServiceException {
    	return (OfflineAccount)getAccount();
    }

    String getAuthToken() throws ServiceException {
    	String authToken = OfflineSyncManager.getInstance().lookupAuthToken(getAccount());
    	if (authToken == null) {
            String passwd = getAccount().getAttr(OfflineProvisioning.A_offlineRemotePassword);

            Element request = new Element.XMLElement(AccountConstants.AUTH_REQUEST);
            request.addElement(AccountConstants.E_ACCOUNT).addAttribute(AccountConstants.A_BY, "id").setText(getAccountId());
            request.addElement(AccountConstants.E_PASSWORD).setText(passwd);

            Element response = sendRequest(request, false);
            authToken = response.getAttribute(AccountConstants.E_AUTH_TOKEN);
            long expires = System.currentTimeMillis() + response.getAttributeLong(AccountConstants.E_LIFETIME);
    		
            OfflineSyncManager.getInstance().authSuccess(getAccount(), authToken, expires);
    	}
    	return authToken;
    }

    String getRemoteUser() throws ServiceException {
        return getAccount().getName();
    }

    String getSoapUri() throws ServiceException {
        return Offline.getServerURI(getAccount(), ZimbraServlet.USER_SERVICE_URI);
    }
    
    String getRemoteHost() throws ServiceException, MalformedURLException {
    	return new URL(getSoapUri()).getHost();
    }

    @Override
    synchronized void initialize() throws ServiceException {
        super.initialize();

        // create a system outbox folder
        Folder userRoot = getFolderById(ID_FOLDER_USER_ROOT);
        Folder.create(ID_FOLDER_OUTBOX, this, userRoot, "Outbox", Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR, null);
        Mountpoint.create(ID_FOLDER_ARCHIVE, userRoot, "Archive", OfflineProvisioning.LOCAL_ACCOUNT_ID, Mailbox.ID_FOLDER_INBOX, MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR);
    }

    @Override
    int getInitialItemId() {
        // locally-generated items must be differentiable from authentic, server-blessed ones
        return FIRST_OFFLINE_ITEM_ID;
    }

    @Override
    boolean isTrackingSync() {
        return !(getOperationContext() instanceof OfflineContext);
    }

    @Override
    public boolean isTrackingImap() {
        return false;
    }

    @Override
    public boolean checkItemChangeID(int modMetadata, int modContent) {
        return true;
    }

    @Override
    MailItem getItemById(int id, byte type) throws ServiceException {
        Integer renumbered = mRenumbers.get(id < -FIRST_USER_ID ? -id : id);
        return super.getItemById(renumbered == null ? id : (id < 0 ? -renumbered : renumbered), type);
    }

    @Override
    MailItem[] getItemById(int[] ids, byte type) throws ServiceException {
        int renumbered[] = new int[ids.length], i = 0;
        for (int id : ids) {
            // use a little sleight-of-hand so we pick up virtual conv ids from the corresponding message id
            Integer newId = mRenumbers.get(id < -FIRST_USER_ID ? -id : id);
            renumbered[i++] = (newId == null ? id : (id < 0 ? -newId : newId));
        }
        return super.getItemById(renumbered, type);
    }

    @Override
    public synchronized void delete(OperationContext octxt, int[] itemIds, byte type, TargetConstraint tcon) throws ServiceException {
        mLocalTagDeletes.clear();

        for (int id : itemIds) {
            try {
                if (id != ID_AUTO_INCREMENT) {
                    getTagById(octxt, id);
                    if ((getChangeMask(octxt, id, MailItem.TYPE_TAG) & Change.MODIFIED_CONFLICT) != 0)
                        mLocalTagDeletes.add(id);
                }
            } catch (NoSuchItemException nsie) { }
            
            try {
            	super.delete(octxt, new int[] {id}, type, tcon); //NOTE: don't call the one with single id as it will dead loop
            } catch (MailServiceException x) {
            	OfflineLog.offline.error("Failed to delete item " + id, x);
            	//something is wrong, but we'll just skip since failed deleting a local item is not immediately fatal (not too good either)
            }
        }
    }

    @Override
    MailItem.TypedIdList collectPendingTombstones() {
        MailItem.TypedIdList tombstones = super.collectPendingTombstones();
        for (Integer tagId : mLocalTagDeletes)
            tombstones.remove(MailItem.TYPE_TAG, tagId);
        return tombstones;
    }

    synchronized void setConversationId(OperationContext octxt, int msgId, int convId) throws ServiceException {
        // we're not allowing any magic -- we are being completely literal about the target conv id
        if (convId <= 0 && convId != -msgId)
            throw MailServiceException.NO_SUCH_CONV(convId);

        boolean success = false;
        try {
            beginTransaction("setConversationId", octxt);

            Message msg = getMessageById(msgId);
            if (convId == msg.getConversationId()) {
                success = true;
                return;
            }

            Conversation oldConv = (Conversation) msg.getParent();

            try {
                Conversation newConv;
                if (convId <= 0) {
                    // moving from a real conv to a virtual one
                    newConv = VirtualConversation.create(this, msg);
                } else {
                    // moving to an existing real conversation
                    newConv = getConversationById(convId);
                    newConv.addChild(msg);
                }
                DbMailItem.setParent(newConv, msg);
                msg.markItemModified(Change.MODIFIED_PARENT);
                msg.mData.parentId = convId;
                msg.mData.metadataChanged(this);
            } catch (MailServiceException.NoSuchItemException nsie) {
                // real conversation didn't exist; create it!
                createConversation(new Message[] {msg}, convId);
            }

            // and now we can update (and possibly delete) the old conversation
            oldConv.removeChild(msg);

            success = true;
        } finally {
            endTransaction(success);
        }
    }

    synchronized void renumberItem(OperationContext octxt, int id, byte type, int newId) throws ServiceException {
        renumberItem(octxt, id, type, newId, -1);
    }

    synchronized void renumberItem(OperationContext octxt, int id, byte type, int newId, int mod_content) throws ServiceException {
        if (id == newId)
            return;
        else if (id <= 0 || newId <= 0)
            throw ServiceException.FAILURE("invalid item id when renumbering (" + id + " => " + newId + ")", null);

        boolean success = false;
        try {
            beginTransaction("renumberItem", octxt);
            MailItem item = getItemById(id, type);

            if (mod_content < 0)
                mod_content = item.getSavedSequence();

            // changing a message's item id needs to purge its Conversation (virtual or real)
            if (item instanceof Message)
                uncacheItem(item.getParentId());

            // mark old blob as disposable, but don't reindex item because INDEX_ID should still be correct
            MailboxBlob mblob = item.getBlob();
            if (mblob != null) {
                // register old blob for post-commit deletion
                item.markBlobForDeletion();
                item.mBlob = null;

                // copy blob to new id (note that item.getSavedSequence() may change again later)
                try {
                    MailboxBlob newBlob = StoreManager.getInstance().link(mblob.getBlob(), this, newId, mod_content, item.getVolumeId());
                    markOtherItemDirty(newBlob);
                } catch (IOException ioe) {
                    throw ServiceException.FAILURE("could not link blob for renumbered item (" + id + " => " + newId + ")", ioe);
                }
            }

            // update the id in the database and in memory
            markItemDeleted(item.getType(), id);
            DbOfflineMailbox.renumberItem(item, newId, mod_content);
            item.mId = item.mData.id = newId;
            item.mData.modContent = mod_content;
            item.markItemCreated();

            // remove the old item from the cache, as it's gone now...
            uncacheItem(id);
            if (item instanceof Folder) {
                // old items have the wrong folder id, which sucks
                purge(MailItem.TYPE_MESSAGE);
                purge(MailItem.TYPE_FOLDER);
            } else if (item instanceof Tag) {
                // old items have the wrong tag bitmask, which also sucks
                purge(MailItem.TYPE_MESSAGE);
                purge(MailItem.TYPE_TAG);
            }

            success = true;
        } finally {
            endTransaction(success);
        }

        mRenumbers.put(id, newId);
    }

    synchronized void deleteEmptyFolder(OperationContext octxt, int folderId) throws ServiceException {
        try {
            Folder folder = getFolderById(octxt, folderId);
            if (folder.getSize() != 0 || folder.hasSubfolders())
                throw OfflineServiceException.FOLDER_NOT_EMPTY(folderId);
        } catch (MailServiceException.NoSuchItemException nsie) {
            ZimbraLog.mailbox.info("folder already deleted, skipping: " + folderId);
            return;
        }
        delete(octxt, folderId, MailItem.TYPE_FOLDER);
    }

    boolean isPendingDelete(OperationContext octxt, int itemId, byte type) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("isPendingDelete", octxt);

            boolean result = DbOfflineMailbox.isTombstone(this, itemId, type);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }

    void removePendingDelete(OperationContext octxt, int itemId, byte type) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("removePendingDelete", octxt);

            DbOfflineMailbox.removeTombstone(this, itemId, type);
            success = true;
        } finally {
            endTransaction(success);
        }
    }

    synchronized MailItem.TypedIdList getLocalChanges(OperationContext octxt) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getLocalChanges", octxt);

            MailItem.TypedIdList result = DbOfflineMailbox.getChangedItems(this);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized List<Pair<Integer, Integer>> getSimpleUnreadChanges(OperationContext octxt, boolean isUnread) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getSimpleUnreadChanges", octxt);

            List<Pair<Integer, Integer>> result = DbOfflineMailbox.getSimpleUnreadChanges(this, isUnread);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized Map<Integer, List<Pair<Integer, Integer>>> getFolderMoveChanges(OperationContext octxt) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getFolderMoveChanges", octxt);

            Map<Integer, List<Pair<Integer, Integer>>> result = DbOfflineMailbox.getFolderMoveChanges(this);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }
    
    synchronized Map<Integer, Integer> getItemModSequences(OperationContext octxt, int[] ids) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getItemModSequences", octxt);

            Map<Integer, Integer> result = DbOfflineMailbox.getItemModSequences(this, ids);
            success = true;
            return result;
        } finally {
            endTransaction(success);
        }
    }

    int getChangeMask(OperationContext octxt, int id, byte type) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("getChangeMask", octxt);

            MailItem item = getItemById(id, type);
            int mask = DbOfflineMailbox.getChangeMask(item);
            success = true;
            return mask;
        } catch (NoSuchItemException nsie) {
            return 0;
        } finally {
            endTransaction(success);
        }
    }

    void setChangeMask(OfflineContext octxt, int id, byte type, int mask) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("setChangeMask", octxt);

            MailItem item = getItemById(id, type);
            DbOfflineMailbox.setChangeMask(item, mask);
            success = true;
        } finally {
            endTransaction(success);
        }
    }

    synchronized void clearTombstones(OperationContext octxt, int token) throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("clearTombstones", octxt);
            DbOfflineMailbox.clearTombstones(this, token);
            success = true;
        } finally {
            endTransaction(success);
        }
    }

    synchronized void syncChangeIds(OperationContext octxt, int itemId, byte type, int date, int mod_content, int change_date, int mod_metadata)
    throws ServiceException {
        if (date < 0 && mod_content < 0 && change_date < 0 && mod_metadata < 0)
            return;

        boolean success = false;
        try {
            beginTransaction("syncChangeIds", octxt);

            MailItem item = getItemById(itemId, type);
            markItemModified(item, Change.INTERNAL_ONLY);

            // resolve the defaulting to find out the real new values
            date = (date < 0 ? (int) (item.getDate() / 1000) : date);
            mod_content = (mod_content < 0 ? item.getSavedSequence() : mod_content);
            change_date = (change_date < 0 ? (int) (item.getChangeDate() / 1000) : change_date);
            mod_metadata = (mod_metadata < 0 ? item.getModifiedSequence() : mod_metadata);

            if (date == item.getDate() && mod_content == item.getSavedSequence() && change_date == item.getChangeDate() && mod_metadata == item.getModifiedSequence()) {
                success = true;
                return;
            }

            // update the database if amything's changed ...
            DbOfflineMailbox.setChangeIds(item, date, mod_content, change_date, mod_metadata);

            // ... update the filename on the item's blob if necessary ...
            boolean blobAffected = mod_content != item.getSavedSequence() && item.getDigest() != null && !item.getDigest().equals("");
            if (blobAffected) {
                MailboxBlob mblob = item.getBlob();

                // mark old blob as disposable
                item.markBlobForDeletion();
                item.mBlob = null;

                // and link to new blob
                try {
                    MailboxBlob newBlob = StoreManager.getInstance().link(mblob.getBlob(), this, item.getId(), mod_content, item.getVolumeId());
                    markOtherItemDirty(newBlob);
                } catch (IOException ioe) {
                    throw ServiceException.FAILURE("could not link blob for item (" + itemId + ") with new change id (" + item.getSavedSequence() + " => " + mod_content + ")", ioe);
                }
            }

            // ... and update the in-memory item as well
            item.mData.date = date;
            item.mData.modContent = mod_content;
            item.mData.dateChanged = change_date;
            item.mData.modMetadata = mod_metadata;

            success = true;
        } finally {
            endTransaction(success);
        }
    }

    synchronized void syncMetadata(OperationContext octxt, int itemId, byte type, int folderId, int flags, long tags, byte color)
    		throws ServiceException {
        boolean success = false;
        try {
            beginTransaction("syncMetadata", octxt);
            MailItem item = getItemById(itemId, type);
            int change_mask = getChangeMask(octxt, itemId, type);

            if ((change_mask & Change.MODIFIED_FOLDER) != 0 || folderId == ID_AUTO_INCREMENT)
                folderId = item.getFolderId();

            if ((change_mask & Change.MODIFIED_COLOR) != 0 || color == ID_AUTO_INCREMENT)
                color = item.getColor();

            if ((change_mask & Change.MODIFIED_TAGS) != 0 || tags == MailItem.TAG_UNCHANGED)
                tags = item.getTagBitmask();

            if (flags == MailItem.FLAG_UNCHANGED) {
                flags = item.getFlagBitmask();
            } else {
                if ((change_mask & Change.MODIFIED_UNREAD) != 0)
                    flags = (item.isUnread() ? Flag.BITMASK_UNREAD : 0) | (flags & ~Flag.BITMASK_UNREAD);
                if ((change_mask & Change.MODIFIED_FLAGS) != 0)
                    flags = item.getInternalFlagBitmask() | (flags & Flag.BITMASK_UNREAD);
            }

            boolean unread = (flags & Flag.BITMASK_UNREAD) > 0;
            flags &= ~Flag.BITMASK_UNREAD;

            item.move(getFolderById(folderId));
            item.setColor(color);
            item.setTags(flags, tags);
            if (mUnreadFlag.canTag(item))
                item.alterUnread(unread);
            success = true;
        } finally {
            endTransaction(success);
        }
    }

    @Override
    void snapshotCounts() throws ServiceException {
        // do the normal persisting of folder/tag counts
        super.snapshotCounts();

        // no need to push changes brought in via sync back to the server
        if (!isTrackingSync())
            return;

        PendingModifications pms = getPendingModifications();
        if (pms == null || !pms.hasNotifications())
            return;

        if (pms.created != null) {
            for (MailItem item : pms.created.values()) {
                if ((item.getId() >= FIRST_USER_ID || item instanceof Tag) && PushChanges.PUSH_TYPES_SET.contains(item.getType()))
                    DbOfflineMailbox.updateChangeRecord(item, Change.MODIFIED_CONFLICT);
            }
        }

        if (pms.modified != null) {
            for (Change change : pms.modified.values()) {
                if (!(change.what instanceof MailItem))
                    continue;
                MailItem item = (MailItem) change.what;
                if (isLocalItem(item))
                    continue;

                int filter = 0;
                switch (item.getType()) {
                    case MailItem.TYPE_MESSAGE:       filter = PushChanges.MESSAGE_CHANGES;     break;
                    case MailItem.TYPE_CHAT:          filter = PushChanges.CHAT_CHANGES;        break;
                    case MailItem.TYPE_CONTACT:       filter = PushChanges.CONTACT_CHANGES;     break;
                    case MailItem.TYPE_FOLDER:        filter = PushChanges.FOLDER_CHANGES;      break;
                    case MailItem.TYPE_SEARCHFOLDER:  filter = PushChanges.SEARCH_CHANGES;      break;
                    case MailItem.TYPE_TAG:           filter = PushChanges.TAG_CHANGES;         break;
                    case MailItem.TYPE_APPOINTMENT:   filter = PushChanges.APPOINTMENT_CHANGES; break;
                }

                if ((change.why & filter) != 0)
                    DbOfflineMailbox.updateChangeRecord(item, change.why & filter);
            }
        }
    }

    private boolean isLocalItem(MailItem item) {
        return item.getId() == ID_FOLDER_OUTBOX;
    }
    
    public Element sendRequest(Element request) throws ServiceException {
        return sendRequest(request, true);
    }
    
    Element sendRequest(Element request, boolean requiresAuth) throws ServiceException {
    	return sendRequest(request, requiresAuth, true, OfflineLC.zdesktop_request_timeout.intValue());
    }

    private Element sendRequest(Element request, boolean requiresAuth, boolean noSession, int timeout) throws ServiceException {
        String uri = getSoapUri();
        OfflineAccount acct = (OfflineAccount)getAccount();
        SoapHttpTransport transport = new SoapHttpTransport(uri, acct.getProxyHost(), acct.getProxyPort(), acct.getProxyUser(), acct.getProxyPass());
        try {
            transport.setUserAgent(OfflineLC.zdesktop_name.value(), OfflineLC.zdesktop_version.value());
            transport.setTimeout(timeout);
            transport.setRetryCount(1);
            if (requiresAuth)
                transport.setAuthToken(getAuthToken());
            transport.setRequestProtocol(SoapProtocol.Soap12);

            OfflineLog.request.debug(request);

            Element response = null;
            if (noSession) {
            	response = transport.invokeWithoutSession(request.detach());
            } else {
            	if (mSessionId != null)
            		transport.setSessionId(mSessionId);
            	response = transport.invoke(request.detach());
            }
            OfflineLog.response.debug(response);
            
            if (transport.getSessionId() != null)
            	mSessionId = transport.getSessionId(); //update sessionId is changed

            return response;
        }catch (IOException e) {
            throw ServiceException.PROXY_ERROR(e, uri);
        } finally {
            transport.shutdown();
        }
    }
    
    OfflineAccount.Version getRemoteServerVersion() throws ServiceException {
    	return ((OfflineAccount)getAccount()).getRemoteServerVersion();
    }
    
    void pollForUpdates() throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.NO_OP_REQUEST);
        request.addAttribute("wait", "1");
        sendRequest(request, true, false, 15 * Constants.SECONDS_PER_MINUTE * 1000); //will block
    }
}
