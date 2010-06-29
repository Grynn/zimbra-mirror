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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.Header;
import org.dom4j.ElementHandler;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AccountConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapHttpTransport;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.db.DbOfflineMailbox;
import com.zimbra.cs.httpclient.URLUtil;
import com.zimbra.cs.mailbox.MailItem.TargetConstraint;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.util.TypedIdList;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.UserServlet.HttpInputStream;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.store.MailboxBlob;
import com.zimbra.cs.store.StoreManager;

public class ZcsMailbox extends ChangeTrackingMailbox {

    public static final int FIRST_OFFLINE_ITEM_ID = 2 << 29;

    private String mSessionId;
    
    private MailboxSync mMailboxSync = new MailboxSync(this);

    private Map<Integer,Integer> mRenumbers = new HashMap<Integer,Integer>();
    private Set<Integer> mLocalTagDeletes = new HashSet<Integer>();

    private static final OfflineAccount.Version MIN_ZCS_VER_PUSH = new OfflineAccount.Version("5.0.6");
    
    ZcsMailbox(MailboxData data) throws ServiceException {
        super(data);
    }

    @Override public MailSender getMailSender() {
        return new OfflineMailSender();
    }

    @Override public boolean isAutoSyncDisabled() {
        try {
            return getAccount().getTimeInterval(OfflineProvisioning.A_offlineSyncFreq, OfflineConstants.DEFAULT_SYNC_FREQ) < 0;
        } catch (ServiceException x) {
            OfflineLog.offline.error(x);
        }
        return true;
    }

    @Override protected void syncOnTimer() {
        sync(false, false);
    }

    public long getSyncFrequency() throws ServiceException {
        long syncFreq = getAccount().getTimeInterval(OfflineProvisioning.A_offlineSyncFreq, OfflineConstants.DEFAULT_SYNC_FREQ);
        if (syncFreq > 0)
            return syncFreq;
        else if (syncFreq == 0)
            return OfflineConstants.MIN_SYNC_FREQ;
        else
            return OfflineConstants.DEFAULT_SYNC_FREQ;
    }

    public boolean isPushEnabled() throws ServiceException {
        return getRemoteServerVersion().isAtLeast(MIN_ZCS_VER_PUSH) && getAccount().getTimeInterval(OfflineProvisioning.A_offlineSyncFreq, OfflineConstants.DEFAULT_SYNC_FREQ) == 0;
    }

    public void sync(boolean isOnRequest, boolean isDebugTraceOn) {
        try {
            mMailboxSync.sync(isOnRequest, isDebugTraceOn);
        } catch (ServiceException x) {
            if (x.getCode().equals(ServiceException.AUTH_EXPIRED)) {
                OfflineLog.offline.info("auth token expired; reauth and rerun sync.");
                try {
                    mMailboxSync.sync(isOnRequest, isDebugTraceOn);
                } catch (ServiceException e) {
                    OfflineLog.offline.error(e);
                }
            } else if (x.getCode().equals(AccountServiceException.NO_SUCH_ACCOUNT)) {
                cancelCurrentTask();
            } else if (!OfflineSyncManager.getInstance().isServiceActive() &&
                !x.getCode().equals(ServiceException.INTERRUPTED)) {
                OfflineLog.offline.error(x);
            }
        }
    }

    MailboxSync getMailboxSync() {
        return mMailboxSync;
    }

    public ZAuthToken getAuthToken() throws ServiceException {
        return getAuthToken(true);
    }
    
    static private Map<String, Long> authErrorTimes = new HashMap<String, Long>();
    
    public ZAuthToken getAuthToken(boolean quickRetry) throws ServiceException {
        ZAuthToken authToken = OfflineSyncManager.getInstance().lookupAuthToken(getAccount());
        
        if (authToken == null) {
            String uri = getSoapUri();    
            synchronized(authErrorTimes) {
                if (!quickRetry) {
                    Long last = authErrorTimes.get(uri);
                    if (last != null && System.currentTimeMillis() -
                        last.longValue() < OfflineLC.zdesktop_authreq_retry_interval.longValue())
                        return null;
                }
            
                String passwd = getAccount().getAttr(OfflineProvisioning.A_offlineRemotePassword);
                Element request = new Element.XMLElement(AccountConstants.AUTH_REQUEST);
                request.addElement(AccountConstants.E_ACCOUNT).addAttribute(AccountConstants.A_BY, "id").setText(getAccountId());
                request.addElement(AccountConstants.E_PASSWORD).setText(passwd);
    
                Element response = null;
                try {
                    response = sendRequest(request, false, true, OfflineLC.zdesktop_authreq_timeout.intValue());
                } catch (ServiceException e) {
                    if (e.getCode().equals(ServiceException.PROXY_ERROR)) {
                        authErrorTimes.put(uri, Long.valueOf(System.currentTimeMillis()));
                    } else {
                        throw e;
                    }
                }
    
                if (response != null) {
                    authToken = new ZAuthToken(response.getElement(AccountConstants.E_AUTH_TOKEN), false);
                    long expires = System.currentTimeMillis() + response.getAttributeLong(AccountConstants.E_LIFETIME);
                    OfflineSyncManager.getInstance().authSuccess(getAccount(), authToken, expires);
                }
            }
        }
        
        return authToken;
    }

    String getRemoteUser() throws ServiceException {
        return getAccount().getName();
    }

    String getSoapUri() throws ServiceException {
        return Offline.getServerURI(getAccount(), AccountConstants.USER_SERVICE_URI);
    }
    
    String getRemoteHost() throws ServiceException, MalformedURLException {
    	return new URL(getSoapUri()).getHost();
    }

//    @Override protected synchronized void initialize() throws ServiceException {
//        super.initialize();
//
//        Folder userRoot = getFolderById(ID_FOLDER_USER_ROOT);
//        Mountpoint.create(ID_FOLDER_ARCHIVE, userRoot, "Archive", OfflineProvisioning.getOfflineInstance().getLocalAccount().getId(), Mailbox.ID_FOLDER_INBOX,
//        		          MailItem.TYPE_MESSAGE, 0, MailItem.DEFAULT_COLOR);
//    }

    @Override int getInitialItemId() {
        // locally-generated items must be differentiable from authentic, server-blessed ones
        return FIRST_OFFLINE_ITEM_ID;
    }

    @Override MailItem getItemById(int id, byte type) throws ServiceException {
        MailItem item = super.getItemById(id, type);
        if (item != null)
            return item;
        Integer renumbered = mRenumbers.get(id < -FIRST_USER_ID ? -id : id);
        if (renumbered != null)
            return super.getItemById(id < 0 ? -renumbered : renumbered, type);
        return null;
    }

    @Override MailItem[] getItemById(int[] ids, byte type) throws ServiceException {
        int renumbered[] = new int[ids.length], i = 0;
        for (int id : ids) {
            // use a little sleight-of-hand so we pick up virtual conv ids from the corresponding message id
            Integer newId = mRenumbers.get(id < -FIRST_USER_ID ? -id : id);
            renumbered[i++] = (newId == null ? id : (id < 0 ? -newId : newId));
        }
        return super.getItemById(renumbered, type);
    }

    @Override public synchronized void delete(OperationContext octxt, int[] itemIds, byte type, TargetConstraint tcon) throws ServiceException {
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
            } catch (Exception x) {
            	SyncExceptionHandler.localDeleteFailed(this, id, x);
            	//something is wrong, but we'll just skip since failed deleting a local item is not immediately fatal (not too good either)
            }
        }
    }

    @Override TypedIdList collectPendingTombstones() {
        TypedIdList tombstones = super.collectPendingTombstones();
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
                DbMailItem.setParent(msg, newConv);
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

    synchronized boolean renumberItem(OperationContext octxt, int id, byte type, int newId) throws ServiceException {
        if (id == newId)
            return true;
        else if (id <= 0 || newId <= 0)
            throw ServiceException.FAILURE("invalid item id when renumbering (" + id + " => " + newId + ")", null);

        boolean success = false;
        try {
            beginTransaction("renumberItem", octxt);
            MailItem item = getItemById(id, type);

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
                    MailboxBlob newBlob = StoreManager.getInstance().link(mblob, this, newId, item.getSavedSequence());
                    markOtherItemDirty(newBlob);
                } catch (IOException ioe) {
                    throw ServiceException.FAILURE("could not link blob for renumbered item (" + id + " => " + newId + ")", ioe);
                }
            }

            // update the id in the database and in memory
            markItemDeleted(item.getType(), id);
            DbOfflineMailbox.renumberItem(item, newId);
            item.mId = item.mData.id = newId;
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
        } catch (MailServiceException.NoSuchItemException nsie) {
            //item deleted from local before sync completes renumbering
            OfflineLog.offline.info("item %d deleted from local db before sync completes renumbering to %d", id, newId);
            TypedIdList tombstones = new TypedIdList();
            tombstones.add(type, newId);
            DbMailItem.writeTombstones(this, tombstones);
            success = true;
            return false;
        } finally {
            endTransaction(success);
        }

        mRenumbers.put(id, newId);
        return true;
    }

    synchronized void deleteEmptyFolder(OperationContext octxt, int folderId) throws ServiceException {
        try {
            Folder folder = getFolderById(octxt, folderId);
            if (folder.getItemCount() != 0 || folder.hasSubfolders())
                throw OfflineServiceException.FOLDER_NOT_EMPTY(folderId);
        } catch (MailServiceException.NoSuchItemException nsie) {
            ZimbraLog.mailbox.info("folder already deleted, skipping: " + folderId);
            return;
        }
        delete(octxt, folderId, MailItem.TYPE_FOLDER);
    }

    synchronized void syncDate(OperationContext octxt, int itemId, byte type, int date)
    throws ServiceException {
        if (date < 0)
            return;

        boolean success = false;
        try {
            beginTransaction("syncChangeIds", octxt);

            MailItem item = getItemById(itemId, type);
            markItemModified(item, Change.INTERNAL_ONLY);

            // update the database
            DbOfflineMailbox.setDate(item, date);

            // ... and update the in-memory item as well
            item.mData.date = date;

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

            String prevIndexId = item.getIndexId();
            item.move(getFolderById(folderId));
            if (!StringUtil.equal(prevIndexId, item.getIndexId())) {
                queueForIndexing(item, false, null);
            }
            
            item.setColor(new MailItem.Color(color));
            item.setTags(flags, tags);
            if (getFlagById(Flag.ID_FLAG_UNREAD).canTag(item))
                item.alterUnread(unread);
            success = true;
        } finally {
            endTransaction(success);
        }
    }

    @Override
    boolean isPushType(byte type) {
        return PushChanges.PUSH_TYPES_SET.contains(type);
    }
    
    @Override
    int getChangeMaskFilter(byte type) {
        switch (type) {
        case MailItem.TYPE_MESSAGE:       return PushChanges.MESSAGE_CHANGES;     
        case MailItem.TYPE_CHAT:          return PushChanges.CHAT_CHANGES;        
        case MailItem.TYPE_CONTACT:       return PushChanges.CONTACT_CHANGES;     
        case MailItem.TYPE_FOLDER:        return PushChanges.FOLDER_CHANGES;      
        case MailItem.TYPE_SEARCHFOLDER:  return PushChanges.SEARCH_CHANGES;      
        case MailItem.TYPE_TAG:           return PushChanges.TAG_CHANGES;         
        case MailItem.TYPE_APPOINTMENT:
        case MailItem.TYPE_TASK:          return PushChanges.APPOINTMENT_CHANGES; 
        case MailItem.TYPE_WIKI:
        case MailItem.TYPE_DOCUMENT:      return PushChanges.DOCUMENT_CHANGES;
        default:                          return 0;
        }
    }
    
    public Element proxyRequest(Element request, SoapProtocol resProto, boolean quietWhenOffline, String op) throws ServiceException {
        if (!OfflineSyncManager.getInstance().isConnectionDown()) {
            try {
                return sendRequest(request, true, true, OfflineLC.zdesktop_request_timeout.intValue(), resProto);         
            } catch (ServiceException e) {
                if (!OfflineSyncManager.isConnectionDown(e))
                    throw e;
            }
        }  
        if (quietWhenOffline) {
            OfflineLog.offline.debug(op + " is unavailable when offline");
            return null;
        } else {
            throw OfflineServiceException.ONLINE_ONLY_OP(op);
        }            
    }
    
    public Element sendRequest(Element request) throws ServiceException {
        return sendRequest(request, true);
    }
    
    Element sendRequest(Element request, boolean requiresAuth) throws ServiceException {
    	return sendRequest(request, requiresAuth, true, OfflineLC.zdesktop_request_timeout.intValue());
    }

    public Element sendRequest(Element request, boolean requiresAuth, boolean noSession, int timeout) throws ServiceException {
        return sendRequest(request, requiresAuth, noSession, timeout, null);
    }
    
    public Element sendRequest(Element request, boolean requiresAuth, boolean noSession, int timeout, SoapProtocol resProto) throws ServiceException {
        return sendRequest(request, requiresAuth, noSession, timeout, resProto, null);
    }
    
    public Element sendRequest(Element request, boolean requiresAuth, boolean noSession, int timeout, SoapProtocol resProto,
        Map<String, ElementHandler> saxHandlers) throws ServiceException {
        String uri = getSoapUri();
        OfflineAccount acct = getOfflineAccount();
        SoapHttpTransport transport = new SoapHttpTransport(uri);
        try {
            transport.setUserAgent(OfflineLC.zdesktop_name.value(), OfflineLC.getFullVersion());
            transport.setTimeout(timeout);
            if (requiresAuth)
                transport.setAuthToken(getAuthToken());
            transport.setRequestProtocol(SoapProtocol.Soap12);
            if (resProto != null)
                transport.setResponseProtocol(resProto);

            if (acct.isDebugTraceEnabled()) {
                Element elt = null;
                String pswd = null;
                if (request.getName().equals(AccountConstants.AUTH_REQUEST.getName())) {
                    elt = request.getElement(AccountConstants.E_PASSWORD);
                    pswd = elt.getText();
                    elt.setText("*");
                }
                
            	OfflineLog.request.debug(request);
            	
            	if (pswd != null)
            	    elt.setText(pswd);
            }

            Element response = null;
            if (saxHandlers != null) {
                response = transport.invoke(request.detach(), false, true, null, null, null, saxHandlers);
            } else if (noSession) {
            	response = transport.invokeWithoutSession(request.detach());
            } else {
            	if (mSessionId != null)
            		transport.setSessionId(mSessionId);
            	response = transport.invoke(request.detach());
            }
            if (acct.isDebugTraceEnabled() && response != null)
            	OfflineLog.response.debug(response);

            // update sessionId if changed
            if (transport.getSessionId() != null)
            	mSessionId = transport.getSessionId();

            return response;
        } catch (IOException e) {
            throw ServiceException.PROXY_ERROR(e, uri);
        }
    }
    
    OfflineAccount.Version getRemoteServerVersion() throws ServiceException {
    	return getOfflineAccount().getRemoteServerVersion();
    }
    
    void pollForUpdates() throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.NO_OP_REQUEST);
        request.addAttribute("wait", "1");
        request.addAttribute("delegate", "0");
        sendRequest(request, true, false, 15 * Constants.SECONDS_PER_MINUTE * 1000); //will block
    }
    
    public Pair<Integer,Integer> sendMailItem(MailItem item) throws ServiceException {
    	OfflineAccount acct = getOfflineAccount();
    	String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH) + "/~"+ URLUtil.urlEscape(item.getPath()) + "?lbfums=1";
    	try {
    	    Pair<Header[], HttpInputStream> resp = 
    	        UserServlet.putMailItem(getAuthToken(), url, item);
    	    int id = 0, version = 0;
    	    for (Header h : resp.getFirst()) {
    	        if (h.getName().equals("X-Zimbra-ItemId"))
    	            id = Integer.parseInt(h.getValue());
    	        else if (h.getName().equals("X-Zimbra-Version"))
    	            version = Integer.parseInt(h.getValue());
    	    }
    	    return new Pair<Integer,Integer>(id, version);
    	} catch (IOException e) {
    	    throw ServiceException.PROXY_ERROR(e, url);
    	}
    }

    static final String VERSIONS_KEY = "VERSIONS";

    public int getLastSyncedVersionForMailItem(int id) throws ServiceException {
        Metadata config = getConfig(null, VERSIONS_KEY);
        if (config == null) {
            config = new Metadata();
            setConfig(null, VERSIONS_KEY, config);
        }
        return (int) config.getLong("" + id, 0);
    }

    public void setSyncedVersionForMailItem(String id, int ver) throws ServiceException {
        Metadata config = getConfig(null, VERSIONS_KEY);
        if (config == null)
            config = new Metadata();

        config.put(id, ver);
        setConfig(null, VERSIONS_KEY, config);
    }

    public boolean pushNewFolder(OperationContext octxt, int id) throws ServiceException {
        if ((getChangeMask(octxt, id, MailItem.TYPE_FOLDER) & Change.MODIFIED_CONFLICT) == 0)
            return false;
        return PushChanges.syncFolder(this, id);
    }
    
    @Override
    protected void updateRssDataSource(Folder folder) {} //bug 38129, to suppress creation of datasource
}
