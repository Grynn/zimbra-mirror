/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.extension.ExtensionUtil;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.util.ZimbraApplication;

public class ExchangeMailbox extends ChangeTrackingMailbox {

    private static final String EXCHANGE_HELPER_CLASS = "com.zimbra.cs.offline.OfflineExchangeHelper";
    private static final Class[] EXCHANGE_HELPER_CONSTRUCTOR_SIGNATURE = {
        Mailbox.class, DataSource.class
    };
    
    private static boolean isXsyncEnabled;
    private static boolean isXsyncExtChecked;
    
    static boolean isXsyncEnabled() {
        if (!isXsyncExtChecked) {
            List<String> extNames = ZimbraApplication.getInstance().getExtensionNames();
            if (extNames != null) {
                for (String ext : extNames)
                    if (OfflineConstants.EXTENSION_XSYNC.equals(ext))
                        isXsyncEnabled = true;
            }
            isXsyncExtChecked = true;
        }
        return isXsyncEnabled;
    }
    
    private ExchangeHelper helper;
    
    public ExchangeMailbox(MailboxData data) throws ServiceException {
        super(data);
        
        if (isXsyncEnabled()) {
            try {
                Class cmdClass = null;
                try {
                    cmdClass = Class.forName(EXCHANGE_HELPER_CLASS);
                } catch (ClassNotFoundException x) {
                    cmdClass = ExtensionUtil.findClass(EXCHANGE_HELPER_CLASS);
                }
                Constructor constructor = cmdClass.getConstructor(EXCHANGE_HELPER_CONSTRUCTOR_SIGNATURE);
                helper = (ExchangeHelper)constructor.newInstance(new Object[] {this, getDataSource()});
            } catch (Exception x) {
                throw ServiceException.FAILURE("failed init exchange mailbox", x);
            }
        }
    }

    @Override
    boolean isPushType(byte type) {
        switch (type) {
        case MailItem.TYPE_MESSAGE:
        case MailItem.TYPE_APPOINTMENT:
        case MailItem.TYPE_CONTACT:
        case MailItem.TYPE_TASK:
//        case MailItem.TYPE_WIKI:
            return true;
        }
        return false;
    }
    
    /** The bitmask of all message changes that we propagate to the server. */
    static final int MESSAGE_CHANGES = Change.MODIFIED_UNREAD | Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER;

    /** The bitmask of all chat changes that we propagate to the server. */
    //static final int CHAT_CHANGES = Change.MODIFIED_UNREAD | Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER;

    /** The bitmask of all contact changes that we propagate to the server. */
    static final int CONTACT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER | Change.MODIFIED_CONTENT;

    /** The bitmask of all folder changes that we propagate to the server. */
    static final int FOLDER_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME;
    
    /** The bitmask of all appointment changes that we propagate to the server. */
    static final int APPOINTMENT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER |
                                           Change.MODIFIED_CONTENT | Change.MODIFIED_INVITE;
    
    /** The bitmask of all document changes that we propagate to the server. */
    static final int DOCUMENT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER |
                                        Change.MODIFIED_CONTENT | Change.MODIFIED_NAME;
    
    @Override
    int getChangeMaskFilter(byte type) {
        switch (type) {
        case MailItem.TYPE_MESSAGE:       return MESSAGE_CHANGES;     
        //case MailItem.TYPE_CHAT:          return CHAT_CHANGES;     
        case MailItem.TYPE_CONTACT:       return CONTACT_CHANGES;     
        case MailItem.TYPE_FOLDER:        return FOLDER_CHANGES;      
        case MailItem.TYPE_APPOINTMENT:
        case MailItem.TYPE_TASK:          return APPOINTMENT_CHANGES; 
        case MailItem.TYPE_WIKI:
        case MailItem.TYPE_DOCUMENT:      return DOCUMENT_CHANGES;
        default:                          return 0;
        }
    }

    OfflineDataSource getDataSource() throws ServiceException {
        return (OfflineDataSource)OfflineProvisioning.getOfflineInstance().getDataSource(getAccount());
    }
    
    @Override
    public MailSender getMailSender() throws ServiceException {
        return new OfflineMailSender();
    }
    
    private static final OperationContext sContext = new TracelessContext();
    private static final Map<Integer, Pair<Integer, String>> sSendUIDs = new HashMap<Integer, Pair<Integer, String>>();
    
    private int sendPendingMessages(boolean isOnRequest) throws ServiceException {
        int sentCount = 0;
        for (Iterator<Integer> iterator = OutboxTracker.iterator(this, isOnRequest ? 0 : 5 * Constants.MILLIS_PER_MINUTE); iterator.hasNext(); ) {
            int id = iterator.next();

            Message msg;
            try {
                msg = getMessageById(sContext, id);
            } catch (NoSuchItemException x) { //message deleted
                OutboxTracker.remove(this, id);
                continue;
            }
            if (msg == null || msg.getFolderId() != ID_FOLDER_OUTBOX) {
                OutboxTracker.remove(this, id);
                continue;
            }

            OfflineDataSource ds = getDataSource();
            if (!isOnRequest && isAutoSyncDisabled(ds))
                continue;

            ZimbraLog.xsync.debug("sending pending mail (id=%d): %s", msg.getId(), msg.getSubject());
            
            
            // try to avoid repeated sends of the same message by tracking "send UIDs" on SendMsg requests
            Pair<Integer, String> sendRecord = sSendUIDs.get(id);
            String sendUID = sendRecord == null || sendRecord.getFirst() != msg.getSavedSequence() ? UUID.randomUUID().toString() : sendRecord.getSecond();
            sSendUIDs.put(id, new Pair<Integer, String>(msg.getSavedSequence(), sendUID));

            // Do we need to save a copy of the message ourselves to the Sent folder?
            boolean saveToSent = (ds.isSaveToSent()) && getAccount().isPrefSaveToSent();
            
            try {
                //new Request(syncFactory.getSyncSettings(ds), syncFactory.getPolicyKey(this))
                helper.doSendMail(msg.getContentStream(), msg.getSize(), saveToSent);
            } catch (ServiceException x) {
                //TODO:
                ZimbraLog.xsync.warn("send mail failure (id=%d)", msg.getId(), x);
            } catch (IOException x) {
                //TODO:
                ZimbraLog.xsync.warn("send mail failure (id=%d)", msg.getId(), x);
            }

            ZimbraLog.xsync.debug("sent pending mail (id=%d)", msg.getId());

            // remove the draft from the outbox
            delete(sContext, id, MailItem.TYPE_MESSAGE);
            OutboxTracker.remove(this, id);

            // the draft is now gone, so remove it from the "send UID" hash and the list of items to push
            sSendUIDs.remove(id);
            sentCount++;
        }

        return sentCount;
    }

    private boolean isAutoSyncDisabled(DataSource ds) {
        return ds.getSyncFrequency() <= 0;
    }
    
    private boolean isTimeToSync(DataSource ds) throws ServiceException {
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        if (isAutoSyncDisabled(ds) || !syncMan.reauthOK(ds) || !syncMan.retryOK(ds))
            return false;
        long freqLimit = syncMan.getSyncFrequencyLimit();
        long frequency = ds.getSyncFrequency() < freqLimit ? freqLimit : ds.getSyncFrequency();
        return System.currentTimeMillis() - syncMan.getLastSyncTime(ds) >= frequency;
    }
    
    @Override
    public boolean isAutoSyncDisabled() {
        try {
            return isAutoSyncDisabled(getDataSource());
        } catch (ServiceException x) {
            return true;
        }
    }

    @Override
    protected void syncOnTimer() {
        try {
            sync(false, false);
        } catch (ServiceException x) {
            ZimbraLog.xsync.warn(x);
        }
    }

    @Override protected synchronized void initialize() throws ServiceException {
        super.initialize();
        getCachedItem(ID_FOLDER_CALENDAR).setColor(new MailItem.Color((byte)1));
    }

    @Override
    public void sync(boolean isOnRequest, boolean isDebugTraceOn) throws ServiceException {
        if (!isXsyncEnabled())
            return;
        
        if (!OfflineSyncManager.getInstance().isServiceActive()) {
            if (isOnRequest)
                OfflineLog.offline.debug("offline sync request ignored");
        } else if (lockMailboxToSync()) {
            synchronized (syncLock) {
                if (isOnRequest && isDebugTraceOn) {
                    OfflineLog.offline.debug("============================== SYNC DEBUG TRACE START ==============================");
                    getOfflineAccount().setRequestScopeDebugTraceOn(true);
                    getDataSource().setRequestScopeDebugTraceOn(true);
                }

                try {
                    int count = 0;
                    try {
                        count = sendPendingMessages(isOnRequest);
//                    } catch (NeedProvisioningException x) {
//                        ZimbraLog.xsync.info("Server requiring Policy Provision. Force sync to trigger Provision.");
//                        isOnRequest = true;
//                    } catch (HttpStatusException x) {
                    } catch (Exception x) {
                        //TODO
                        ZimbraLog.xsync.warn("send mail failure", x);
                    }
                    syncDataSource(count > 0, isOnRequest);
                } catch (Exception x) {
                    if (!OfflineSyncManager.getInstance().isServiceActive())
                        return;
                    else if (isDeleting())
                        OfflineLog.offline.info("Mailbox \"%s\" is being deleted", getAccountName());
                    else
                        OfflineLog.offline.error("exception encountered during sync", x);
                } finally {
                    if (isOnRequest && isDebugTraceOn) {
                        getOfflineAccount().setRequestScopeDebugTraceOn(false);
                        getDataSource().setRequestScopeDebugTraceOn(false);
                        OfflineLog.offline.debug("============================== SYNC DEBUG TRACE END ================================");
                    }
                    unlockMailbox();
                }
            }
        } else if (isOnRequest) {
            OfflineLog.offline.debug("sync already in progress");
        }
    }

    private void syncDataSource(boolean force, boolean isOnRequest) throws ServiceException {
        OfflineDataSource ds = getDataSource();
        if (!force && !isOnRequest && !isTimeToSync(ds) && !ds.isSyncNeeded())
            return;
        
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        try {
            OfflineLog.offline.info(">>>>>>>> name=%s;version=%s;build=%s;release=%s;os=%s;type=%s",
                    ds.getAccount().getName(), OfflineLC.zdesktop_version.value(), OfflineLC.zdesktop_buildid.value(), OfflineLC.zdesktop_relabel.value(),
                    System.getProperty("os.name") + " " + System.getProperty("os.arch") + " " + System.getProperty("os.version"), ds.getType());

            syncMan.syncStart(ds);
            DataSourceManager.importData(ds, null, true);
            syncMan.syncComplete(ds);
            OfflineProvisioning.getOfflineInstance().setDataSourceAttribute(ds, OfflineConstants.A_zimbraDataSourceLastSync, Long.toString(System.currentTimeMillis()));
        } catch (Exception x) {
            if (isDeleting())
                OfflineLog.offline.info("Mailbox \"%s\" is being deleted", getAccountName());
            else
                syncMan.processSyncException(ds, x);
        } catch (Error e) {
            syncMan.processSyncError(ds, e);
        }
    }
}
