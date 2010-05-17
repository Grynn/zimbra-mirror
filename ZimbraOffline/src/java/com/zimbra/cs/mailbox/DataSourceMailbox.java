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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.DataSource;
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.Provisioning.IdentityBy;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.datasource.DataSourceManager;
import com.zimbra.cs.db.DbMailItem;
import com.zimbra.cs.mailbox.MailSender.SafeSendFailedException;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.mime.Mime.FixedMimeMessage;
import com.zimbra.cs.offline.LMailSender;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.YMailSender;
import com.zimbra.cs.offline.common.OfflineConstants;
import com.zimbra.cs.offline.util.ymail.YMailException;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.util.JMSession;

public class DataSourceMailbox extends SyncMailbox {
    private boolean hasFolders;
    private boolean isFlat;

    private final Flag mSyncFlag;
    private final Flag mSyncFolderFlag;
    private final Flag mNoInferiorsFlag;

    DataSourceMailbox(MailboxData data) throws ServiceException {
        super(data);

        OfflineDataSource ds = (OfflineDataSource)OfflineProvisioning.
            getOfflineInstance().getDataSource(getAccount());
        if (ds != null) {
            hasFolders = ds.getType() == DataSource.Type.imap;
            isFlat = ds.isLive() || ds.isYahoo();
        }
        mSyncFlag = getFlagById(Flag.ID_FLAG_SYNC);
        mSyncFolderFlag = getFlagById(Flag.ID_FLAG_SYNCFOLDER);
        mNoInferiorsFlag = getFlagById(Flag.ID_FLAG_NO_INFERIORS);
    }

    @Override
    protected synchronized void initialize() throws ServiceException {
        super.initialize();
        if (hasFolders) {
            List<Pair<Integer, String>> systemMailFolders = new ArrayList<
                Pair<Integer, String>>();
            
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_INBOX, "/Inbox"));
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_TRASH, "/Trash"));
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_SPAM,  "/Junk"));
            systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_SENT,  "/Sent"));
            // systemMailFolders.add(new Pair<Integer, String>(ID_FOLDER_DRAFTS, "/Drafts"));
            for (Pair<Integer, String> pair : systemMailFolders) {
                MailItem mi = getCachedItem(pair.getFirst());
                DbMailItem.alterTag(mSyncFolderFlag, Arrays.asList(pair.getFirst()), true);
                if (mi != null)
                    mi.mData.flags |= mSyncFolderFlag.getBitmask();
                if (isSyncEnabledByDefault(pair.getSecond())) {
                    DbMailItem.alterTag(mSyncFlag, Arrays.asList(pair.getFirst()), true);
                    if (mi != null)
                        mi.mData.flags |= mSyncFlag.getBitmask();
                }
            }
            if (isFlat) {
                DbMailItem.alterTag(mNoInferiorsFlag, Arrays.asList(
                    ID_FOLDER_INBOX, ID_FOLDER_TRASH, ID_FOLDER_SENT), true);
                MailItem mi = getCachedItem(ID_FOLDER_INBOX);
                if (mi != null)
                    mi.mData.flags |= mNoInferiorsFlag.getBitmask();
                mi = getCachedItem(ID_FOLDER_TRASH);
                if (mi != null)
                    mi.mData.flags |= mNoInferiorsFlag.getBitmask();
                mi = getCachedItem(ID_FOLDER_SENT);
                if (mi != null)
                    mi.mData.flags |= mNoInferiorsFlag.getBitmask();
            }
            OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.
                getOfflineInstance().getDataSource(getAccount()));
            if (ds.isYahoo() || ds.isGmail())
                getCachedItem(ID_FOLDER_CALENDAR).setColor(new MailItem.Color(
                    (byte)(ds.isYahoo() ? 4 : 5)));
        }
    }

    @Override
    synchronized boolean finishInitialization() throws ServiceException {
        if (super.finishInitialization()) {
            if (hasFolders) {
                Folder draft = getFolderById(ID_FOLDER_DRAFTS);
                if ((draft.getFlagBitmask() & Flag.BITMASK_SYNC) != 0)
                    alterTag(null, ID_FOLDER_DRAFTS, MailItem.TYPE_FOLDER,
                        Flag.ID_FLAG_SYNC, false);
                if ((draft.getFlagBitmask() & Flag.BITMASK_SYNCFOLDER) != 0)
                    alterTag(null, ID_FOLDER_DRAFTS, MailItem.TYPE_FOLDER,
                        Flag.ID_FLAG_SYNCFOLDER, false);
            }
            return true;
        }
        return false;
    }

    @Override
    public String getItemFlagString(MailItem mi) {
        if (hasFolders && mi.getType() == MailItem.TYPE_FOLDER) {
            try {
                OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.
                    getOfflineInstance().getDataSource(getAccount()));
                if (ds.isSyncInboxOnly()) {
                    int flags = mi.getFlagBitmask();
                    
                    flags &= ~Flag.BITMASK_SYNCFOLDER;
                    flags &= ~Flag.BITMASK_SYNC;
                    return Flag.bitmaskToFlags(flags);
                }
            } catch (ServiceException x) {}
        }
        return mi.getFlagString();
    }

    @Override
    public synchronized void alterTag(OperationContext octxt, int itemId,
        byte type, int tagId, boolean addTag) throws ServiceException {
        if (tagId == Flag.ID_FLAG_SYNC && addTag) {
            Folder folder = getFolderById(itemId);
            if ((folder.getFlagBitmask() & Flag.ID_FLAG_SYNCFOLDER) == 0)
                throw MailServiceException.MODIFY_CONFLICT();
        }
        super.alterTag(octxt, itemId, type, tagId, addTag);
    }

    private boolean isSyncEnabledByDefault(String path) throws ServiceException {
        OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.
            getOfflineInstance().getDataSource(getAccount()));
        return ds != null && ds.isSyncEnabledByDefault(path);
    }

    private void alterSyncFolderFlag(Folder folder, boolean canSync)
        throws ServiceException {
        folder.alterTag(mSyncFolderFlag, canSync);
        if (canSync) {
            folder.mData.flags |= mSyncFolderFlag.getBitmask();
            if (isSyncEnabledByDefault(folder.getPath())) {
                folder.alterTag(mSyncFlag, canSync);
                folder.mData.flags |= mSyncFlag.getBitmask();
            }
        } else {
            folder.mData.flags &= ~mSyncFolderFlag.getBitmask();
            folder.mData.flags &= ~mSyncFlag.getBitmask();
        }
    }

    @Override
    void itemCreated(MailItem item) throws ServiceException {
        if (hasFolders && item instanceof Folder &&
            ((Folder)item).getDefaultView() == MailItem.TYPE_MESSAGE &&
            (((Folder)item).getUrl() == null || ((Folder)item).getUrl().equals(""))) {
            alterSyncFolderFlag((Folder)item, true);
            if (isFlat)
                item.alterTag(mNoInferiorsFlag, true);
        }
    }

    @Override
    public MailSender getMailSender() {
        return new OfflineMailSender();
    }

    /*
     * Tracks messages that we've called SendMsg on but never got back a
     *  response.  This should help avoid duplicate sends when the connection
     *  goes away in the process of a SendMsg.<p>
     *
     *  key: a String of the form <tt>account-id:message-id</tt><p>
     *  value: a Pair containing the content change ID and the "send UID"
     *         used when the message was previously sent.
     */
    private static final Map<Integer, Pair<Integer, String>> sSendUIDs = new HashMap<Integer, Pair<Integer, String>>();

    public int sendPendingMessages(boolean isOnRequest) throws ServiceException {
        OperationContext context = null;

        int sentCount = 0;
        for (Iterator<Integer> iterator = OutboxTracker.iterator(this, isOnRequest ? 0 : 5 * Constants.MILLIS_PER_MINUTE); iterator.hasNext(); ) {
            int id = iterator.next();
            Message msg;
            
            if (context == null)
                context = new OperationContext(this);

            try {
                msg = getMessageById(context, id);
            } catch (NoSuchItemException x) { //message deleted
                OutboxTracker.remove(this, id);
                continue;
            }
            if (msg == null || msg.getFolderId() != ID_FOLDER_OUTBOX) {
                OutboxTracker.remove(this, id);
                continue;
            }

            OfflineAccount acct = (OfflineAccount)OfflineProvisioning.
            getOfflineInstance().getAccount(msg.getDraftAccountId());
            OfflineDataSource ds = (OfflineDataSource)OfflineProvisioning.getInstance().get(
                acct, DataSourceBy.id, msg.getDraftIdentityId());
            Session session = null;

            if (ds == null)
                ds = (OfflineDataSource)OfflineProvisioning.getOfflineInstance().getDataSource(acct);

            if (!isOnRequest && isAutoSyncDisabled(ds))
                continue;

            // For Yahoo bizmail use SMTP rather than Cascade
            boolean isYBizmail = ds.isYahoo() && ds.isYBizmail();

            if (isYBizmail) {
                session = ds.getYBizmailSession();
            } else if (!ds.isLive() && !ds.isYahoo()) {
                session = LocalJMSession.getSession(ds);
                if (session == null) {
                    OfflineLog.offline.info("SMTP configuration not valid: " + msg.getSubject());
                    bounceToInbox(context, acct, id, msg, "SMTP configuration not valid");
                    OutboxTracker.remove(this, id);
                    continue;
                }
            }
            Identity identity = Provisioning.getInstance().get(getAccount(), IdentityBy.id, msg.getDraftIdentityId());
            // try to avoid repeated sends of the same message by tracking "send UIDs" on SendMsg requests
            Pair<Integer, String> sendRecord = sSendUIDs.get(id);
            String sendUID = sendRecord == null || sendRecord.getFirst() != msg.getSavedSequence() ?
                UUID.randomUUID().toString() : sendRecord.getSecond();
            sSendUIDs.put(id, new Pair<Integer, String>(msg.getSavedSequence(), sendUID));

            MimeMessage mm = ((FixedMimeMessage) msg.getMimeMessage()).setSession(session);
            String origId = msg.getDraftOrigId();
            ItemId origMsgId = StringUtil.isNullOrEmpty(origId) ? null : new
                ItemId(origId, acct.getId());

            // Do we need to save a copy of the message ourselves to the Sent folder?
            boolean saveToSent = (isYBizmail || ds.isSaveToSent()) && getAccount().isPrefSaveToSent();

            if (ds.isYahoo() && !isYBizmail) {
                YMailSender ms = YMailSender.newInstance(ds);
                try {
                    ms.sendMimeMessage(context, this, saveToSent, mm, null, null,
                        origMsgId, msg.getDraftReplyType(), identity, false, false);
                } catch (ServiceException e) {
                    Throwable cause = e.getCause();
                    if (cause != null && cause instanceof YMailException) {
                        OfflineLog.offline.info("YMail request failed: " + msg.getSubject(), cause);
                        YMailException yme = (YMailException) cause;
                        if (yme.isRetriable()) {
                            OutboxTracker.recordFailure(this, id);
                        } else {
                            bounceToInbox(context, acct, id, msg, cause.getMessage());
                            OutboxTracker.remove(this, id);
                        }
                        continue;
                    }
                    throw e;
                }
            } else {
                MailSender ms = ds.isLive() ? LMailSender.newInstance(ds) : new MailSender();
                try {
                    ms.sendMimeMessage(context, this, saveToSent, mm, null, null,
                        origMsgId, msg.getDraftReplyType(), identity, false, false);
                } catch (ServiceException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof MessagingException) {
                        OfflineLog.offline.info("Mail send failure: " + msg.getSubject(), cause);
                        if (cause instanceof SafeSendFailedException) {
                            bounceToInbox(context, acct, id, msg, cause.getMessage());
                            OutboxTracker.remove(this, id);
                        } else {
                            OutboxTracker.recordFailure(this, id);
                        }
                        continue;
                    }
                    throw e;
                }
            }

            OfflineLog.offline.debug("sent pending mail (" + id + "): " + msg.getSubject());

            // remove the draft from the outbox
            delete(context, id, MailItem.TYPE_MESSAGE);
            OutboxTracker.remove(this, id);
            OfflineLog.offline.debug("deleted pending draft (" + id + ')');

            // the draft is now gone, so remove it from the "send UID" hash and the list of items to push
            sSendUIDs.remove(id);
            sentCount++;
        }

        return sentCount;
    }

    private void bounceToInbox(OperationContext context, Account acct, int id,
        Message msg, String error) {
        try {
            MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSession());
            
            mm.setFrom(new InternetAddress(acct.getName()));
            mm.setRecipient(RecipientType.TO, new InternetAddress(acct.getName()));
            mm.setSubject("Delivery failed: " + error);
    
            mm.saveChanges(); //must call this to update the headers
    
            MimeMultipart mmp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            
            mbp.setText(error == null ?
                "SEND FAILED. PLEASE CHECK RECIPIENT ADDRESSES AND SMTP SETTINGS" : error);
                    mmp.addBodyPart(mbp);
            mbp = new MimeBodyPart();
            mbp.setContent(msg.getMimeMessage(), "message/rfc822");
            mbp.setHeader("Content-Disposition", "attachment");
            mmp.addBodyPart(mbp, mmp.getCount());
            mm.setContent(mmp);
            mm.saveChanges();
            ParsedMessage pm = new ParsedMessage(mm, true);
            addMessage(context, pm, Mailbox.ID_FOLDER_INBOX, true,
                Flag.BITMASK_UNREAD | Flag.BITMASK_FROM_ME, null);
            delete(context, id, MailItem.TYPE_MESSAGE);
            OfflineLog.offline.warn("SMTP: bounced failed send " + id + ": " +
                error + ": " + msg.getSubject());
        } catch (Exception e) {
            OfflineLog.offline.warn("SMTP: bounced failed send " + id + ": " +
                error + ": " + msg.getSubject(), e);
        }
}

    private boolean isAutoSyncDisabled(DataSource ds) {
        return ds.getSyncFrequency() <= 0;
    }

    @Override
    public boolean isAutoSyncDisabled() {
        try {
            List<DataSource> dataSources = OfflineProvisioning.
                getOfflineInstance().getAllDataSources(getAccount());
            for (DataSource ds : dataSources) {
                if (!isAutoSyncDisabled(ds))
                    return false;
            }
        } catch (ServiceException x) {
            OfflineLog.offline.error(x);
        }
        return true;
    }

    @Override
    protected void syncOnTimer() {
        try {
            sync(false, false);
        } catch (ServiceException x) {
            OfflineLog.offline.error(x);
        }
    }

    private boolean isTimeToSync(DataSource ds) throws ServiceException {
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        if (isAutoSyncDisabled(ds) || !syncMan.reauthOK(ds) || !syncMan.retryOK(ds))
            return false;
        long freqLimit = syncMan.getSyncFrequencyLimit();
        long frequency = ds.getSyncFrequency() < freqLimit ? freqLimit :
            ds.getSyncFrequency();
        return System.currentTimeMillis() - syncMan.getLastSyncTime(ds) >= frequency;
    }

    private void syncAllLocalDataSources(boolean force, boolean isOnRequest) throws
        ServiceException {
        OfflineProvisioning prov = OfflineProvisioning.getOfflineInstance();
        List<DataSource> dataSources = prov.getAllDataSources(getAccount());
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        for (DataSource ds : dataSources) {
            boolean needsSync = ds.needsSync(false);
            if (!needsSync && !force && !isOnRequest && !isTimeToSync(ds))
                continue;
            try {
                OfflineLog.offline.info(
                    ">>>>>>>> name=%s;version=%s;build=%s;release=%s;os=%s;type=%s",
                    ds.getAccount().getName(), OfflineLC.zdesktop_version.value(),
                    OfflineLC.zdesktop_buildid.value(), OfflineLC.zdesktop_relabel.value(),
                    System.getProperty("os.name") + " " +
                    System.getProperty("os.arch") + " " +
                    System.getProperty("os.version"), ds.getType());
                syncMan.syncStart(ds);
                DataSourceManager.importData(ds, isOnRequest);
                syncMan.syncComplete(ds);
                OfflineProvisioning.getOfflineInstance().setDataSourceAttribute(
                    ds, OfflineConstants.A_zimbraDataSourceLastSync,
                    Long.toString(System.currentTimeMillis()));
            } catch (Exception x) {
                if (isDeleting())
                    OfflineLog.offline.info("Mailbox \"%s\" is being deleted",
                        getAccountName());
                else
                    syncMan.processSyncException(ds, x);
            } catch (Error e) {
                syncMan.processSyncError(ds, e);
            }
        }
    }

    public void sync(boolean isOnRequest, boolean isDebugTraceOn) throws ServiceException {
        if (!OfflineSyncManager.getInstance().isServiceActive()) {
            if (isOnRequest)
                OfflineLog.offline.debug("offline sync request ignored");
        } else if (lockMailboxToSync()) {
            synchronized (syncLock) {
                if (isOnRequest && isDebugTraceOn) {
                    OfflineLog.offline.debug(
                        "============================== SYNC DEBUG TRACE START ==============================");
                    getOfflineAccount().setRequestScopeDebugTraceOn(true);
                }
                try {
                    int count = sendPendingMessages(isOnRequest);
                    syncAllLocalDataSources(count > 0, isOnRequest);
                } catch (Exception x) {
                    if (!OfflineSyncManager.getInstance().isServiceActive())
                        return;
                    else if (isDeleting())
                        OfflineLog.offline.info("Mailbox \"%s\" is being deleted",
                            getAccountName());
                    else
                        OfflineLog.offline.error(
                            "exception encountered during sync", x);
                } finally {
                    if (isOnRequest && isDebugTraceOn) {
                        getOfflineAccount().setRequestScopeDebugTraceOn(false);
                        OfflineLog.offline.debug(
                            "============================== SYNC DEBUG TRACE END ================================");
                    }
                    unlockMailbox();
                }
            }
        } else if (isOnRequest) {
            OfflineLog.offline.debug("sync already in progress");
        }
    }
    
    Set<Folder> getAccessibleFolders(short rights) throws ServiceException {
        Set<Folder> accessable = super.getAccessibleFolders(rights);
        boolean all = true;
        OfflineDataSource ds = (OfflineDataSource)(OfflineProvisioning.
            getOfflineInstance().getDataSource(getAccount()));
        Set<Folder> visible = new HashSet<Folder>();

        if (ds == null)
            return accessable;
        for (Folder folder : accessable == null ? getFolderById(
            ID_FOLDER_ROOT).getSubfolderHierarchy() : accessable) {
            if (folder.getId() > Mailbox.FIRST_USER_ID ||
                folder.getDefaultView() != MailItem.TYPE_MESSAGE ||
                folder.getId() == ID_FOLDER_DRAFTS ||
                folder.getId() == ID_FOLDER_FAILURE ||
                folder.getId() == ID_FOLDER_OUTBOX ||
                folder.getId() == ID_FOLDER_SENT ||
                DataSourceManager.getInstance().isSyncCapable(ds, folder))
                visible.add(folder);
            else
                all = false;
        }
        return all ? null : visible;
    }
}
