/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009 Zimbra, Inc.
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
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import javax.mail.Address;
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
import com.zimbra.cs.account.Identity;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Provisioning.DataSourceBy;
import com.zimbra.cs.account.Provisioning.IdentityBy;
import com.zimbra.cs.account.offline.OfflineDataSource;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.MailSender.SafeSendFailedException;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.ZcsMailbox.OfflineContext;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.mime.Mime.FixedMimeMessage;
import com.zimbra.cs.offline.LMailSender;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.YMailSender;
import com.zimbra.cs.offline.util.ymail.YMailException;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.service.util.ItemId;
import com.zimbra.cs.util.JMSession;

public class LocalMailbox extends SyncMailbox {
    public LocalMailbox(MailboxData data) throws ServiceException {
        super(data);
    }
    
    @Override synchronized void ensureSystemFolderExists() throws ServiceException {
        super.ensureSystemFolderExists();
        try {
            getFolderById(ID_FOLDER_NOTIFICATIONS);
        } catch (NoSuchItemException e) {
            CreateFolder redo = new CreateFolder(getId(), NOTIFICATIONS_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB, null);
            
            redo.setFolderId(ID_FOLDER_NOTIFICATIONS);
            redo.start(System.currentTimeMillis());
            createFolder(new OfflineContext(redo), NOTIFICATIONS_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB, null);
        }
        try {
            getFolderById(ID_FOLDER_GLOBAL_SEARCHES);
        } catch (NoSuchItemException e) {
            CreateFolder redo = new CreateFolder(getId(), GLOBAL_SEARCHES_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_SEARCHFOLDER, 0, MailItem.DEFAULT_COLOR_RGB, null);
            
            redo.setFolderId(ID_FOLDER_GLOBAL_SEARCHES);
            redo.start(System.currentTimeMillis());
            createFolder(new OfflineContext(redo), GLOBAL_SEARCHES_PATH,
                ID_FOLDER_USER_ROOT, Folder.FOLDER_IS_IMMUTABLE,
                MailItem.TYPE_SEARCHFOLDER, 0, MailItem.DEFAULT_COLOR_RGB, null);
        }
        for (Account account : OfflineProvisioning.getOfflineInstance().getAllAccounts()) {
            try {
                getFolderByName(null, ID_FOLDER_NOTIFICATIONS, account.getId());
            } catch (NoSuchItemException e) {
                createMountpoint(null, ID_FOLDER_NOTIFICATIONS, account.getId(),
                    account.getId(), ID_FOLDER_USER_ROOT,
                    MailItem.TYPE_UNKNOWN, 0, MailItem.DEFAULT_COLOR_RGB);
            }
        }
    }

    @Override protected synchronized void initialize() throws ServiceException {
        super.initialize();
        Folder.create(ID_FOLDER_NOTIFICATIONS, this,
            getFolderById(ID_FOLDER_USER_ROOT), NOTIFICATIONS_PATH,
            Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_UNKNOWN, 0,
            MailItem.DEFAULT_COLOR_RGB, null, null);
        Folder.create(ID_FOLDER_GLOBAL_SEARCHES, this,
            getFolderById(ID_FOLDER_USER_ROOT), GLOBAL_SEARCHES_PATH,
            Folder.FOLDER_IS_IMMUTABLE, MailItem.TYPE_SEARCHFOLDER, 0,
            MailItem.DEFAULT_COLOR_RGB, null, null);
    }
    
    @Override public boolean isAutoSyncDisabled() { return false; }
    
    @Override
    protected void syncOnTimer() {
        try {
            sync(false, false);
        } catch (ServiceException e) {
            OfflineLog.offline.error(e);
        }
    }

    public void sync(boolean isOnRequest, boolean isDebugTraceOn) throws ServiceException {
        if (lockMailboxToSync()) {
            synchronized (syncLock) {
                if (isOnRequest && isDebugTraceOn) {
                    OfflineLog.offline.debug(
                        "============================== SYNC DEBUG TRACE START ==============================");
                    getOfflineAccount().setRequestScopeDebugTraceOn(true);
                }
                try {
                    sendPendingMessages(isOnRequest);
                } catch (Exception e) {
                    OfflineLog.offline.error("exception encountered during sync", e);
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
        OperationContext context = new OperationContext(this);
        int sentCount = 0;

        for (Iterator<Integer> iterator = OutboxTracker.iterator(this, isOnRequest ? 0 : 5 * Constants.MILLIS_PER_MINUTE); iterator.hasNext(); ) {
            int id = iterator.next();
            Message msg;
            
            try {
                msg = getMessageById(context, id);
            } catch (NoSuchItemException x) {
                OutboxTracker.remove(this, id);
                continue;
            }
            if (msg == null || msg.getFolderId() != ID_FOLDER_OUTBOX) {
                OutboxTracker.remove(this, id);
                continue;
            }
            
            Account acct = OfflineProvisioning.getOfflineInstance().getAccount(
                msg.getDraftAccountId());
            OfflineDataSource ds = (OfflineDataSource)OfflineProvisioning.getInstance().get(
                acct, DataSourceBy.id, msg.getDraftIdentityId());
            Session session = null;

            if (ds == null)
                ds = (OfflineDataSource)OfflineProvisioning.getOfflineInstance().getDataSource(acct);
            
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
            Identity identity = Provisioning.getInstance().get(acct, IdentityBy.id, msg.getDraftIdentityId());
            MimeMessage mm = ((FixedMimeMessage) msg.getMimeMessage()).setSession(session);
            MailSender ms;
            Mailbox origMbox = MailboxManager.getInstance().getMailboxByAccount(acct);
            String origId = msg.getDraftOrigId();
            ItemId origMsgId = StringUtil.isNullOrEmpty(origId) ? null : new
                ItemId(origId, acct.getId());
            String replyType = msg.getDraftReplyType();
            Collection<Address> sentAddresses = null;
            // try to avoid repeated sends of the same message by tracking "send UIDs" on SendMsg requests
            Pair<Integer, String> sendRecord = sSendUIDs.get(id);
            String sendUID = sendRecord == null || sendRecord.getFirst() != msg.getSavedSequence() ?
                UUID.randomUUID().toString() : sendRecord.getSecond();

            if (identity == null)
                identity = Provisioning.getInstance().getDefaultIdentity(acct);
            sSendUIDs.put(id, new Pair<Integer, String>(msg.getSavedSequence(), sendUID));

            if (ds.isYahoo() && !isYBizmail)
                ms = YMailSender.newInstance(ds);
            else
                ms = ds.isLive() ? LMailSender.newInstance(ds) : new MailSender();
            /*
             * sendMessage() has too many branches to deal with alternate mboxes
             * vs delegated access so all relevent features must be done here
             */
            try {
                try {
                    ms.logMessage(mm, origMsgId, null, replyType);
                    sentAddresses = ms.sendMessage(this, mm, false, null);
                } catch (SafeSendFailedException sfe) {
                    Address[] invalidAddrs = sfe.getInvalidAddresses();
                    Address[] validUnsentAddrs = sfe.getValidUnsentAddresses();
                    if (invalidAddrs != null && invalidAddrs.length > 0) { 
                        StringBuilder s = new StringBuilder("Invalid address").append(invalidAddrs.length > 1 ? "es: " : ": ");
                        for (int i = 0; i < invalidAddrs.length; i++) {
                            if (i > 0)
                                s.append(",");
                            s.append(invalidAddrs[i]);
                        }
                        s.append(".  ").append(sfe.toString());

                        if (Provisioning.getInstance().getLocalServer().isSmtpSendPartial())
                            throw MailServiceException.SEND_PARTIAL_ADDRESS_FAILURE(s.toString(), sfe, invalidAddrs, validUnsentAddrs);
                        else
                            throw MailServiceException.SEND_ABORTED_ADDRESS_FAILURE(s.toString(), sfe, invalidAddrs, validUnsentAddrs);
                    } else {
                        throw MailServiceException.SEND_FAILURE("SMTP server reported: " + sfe.getMessage(), sfe, invalidAddrs, validUnsentAddrs);
                    }
                } catch (IOException ioe) {
                    throw ServiceException.FAILURE("Unable to send message", ioe);
                } catch (MessagingException me) {
                    Exception chained = me.getNextException();
                    if (chained instanceof ConnectException || chained instanceof UnknownHostException) {
                        throw MailServiceException.TRY_AGAIN("Unable to connect to the MTA", chained);
                    } else {
                        throw ServiceException.FAILURE("Unable to send message", me);
                    }
                }
            } catch (ServiceException e) {
                Throwable cause = e.getCause();
                
                if (cause != null) {
                    OfflineLog.offline.info("mail send failure: " +
                        msg.getSubject(), cause);
                    if (cause instanceof YMailException) {
                        YMailException yme = (YMailException)cause;
                        
                        if (yme.isRetriable()) {
                            OutboxTracker.recordFailure(this, id);
                        } else {
                            bounceToInbox(context, acct, id, msg, cause.getMessage());
                            OutboxTracker.remove(this, id);
                        }
                    } else if (cause instanceof MessagingException) {
                        if (cause instanceof SafeSendFailedException) {
                            bounceToInbox(context, acct, id, msg, cause.getMessage());
                            OutboxTracker.remove(this, id);
                        } else {
                            OutboxTracker.recordFailure(this, id);
                        }
                    }
                }
                continue;
            }
            OfflineLog.offline.debug("sent pending mail (" + id + "): " + msg.getSubject());
            
            // save sent item back to original mbox
            if ((isYBizmail || ds.isSaveToSent()) && acct.isPrefSaveToSent()) {
                try {
                    ParsedMessage pm = new ParsedMessage(mm, mm.getSentDate().getTime(),
                        origMbox.attachmentsIndexingEnabled());
                    int convId = Mailbox.ID_AUTO_INCREMENT;

                    if (origMsgId != null && origMsgId.belongsTo(origMbox))
                        convId = origMbox.getConversationIdFromReferent(mm,
                            origMsgId.getId());
                    msg = origMbox.addMessage(null, pm,
                        MailSender.getSentFolderId(origMbox, identity),
                        true, Flag.BITMASK_FROM_ME, null, convId);
                } catch (Exception e) {
                    OfflineLog.offline.error("unable to save sent copy (" + id + ')');
                }
            }

            // remove the draft from the outbox
            try {
                delete(context, id, MailItem.TYPE_MESSAGE);
            } catch (Exception e) {
            }
            OutboxTracker.remove(this, id);
            OfflineLog.offline.debug("deleted pending draft (" + id + ')');
            sSendUIDs.remove(id);
            sentCount++;
            
            // check if this is a reply, and if so flag the msg appropriately
            if (origMsgId != null && origMsgId.belongsTo(origMbox)) {
                try {
                    if (MailSender.MSGTYPE_REPLY.equals(replyType))
                        origMbox.alterTag(context, origMsgId.getId(),
                            MailItem.TYPE_MESSAGE, Flag.ID_FLAG_REPLIED, true);
                    else if (MailSender.MSGTYPE_FORWARD.equals(replyType))
                        origMbox.alterTag(context, origMsgId.getId(),
                            MailItem.TYPE_MESSAGE, Flag.ID_FLAG_FORWARDED, true);
                } catch (ServiceException e) {
                    // not an error because the original message may be gone when
                    // accepting/declining an appointment
                } catch (Exception e) {
                    OfflineLog.offline.warn("unable to update reply flag", e);
                }
            }

            // update auto addresses
            if (!sentAddresses.isEmpty()) {
                try {
                    ContactRankings.increment(acct.getId(), sentAddresses);
                } catch (Exception e) {
                    OfflineLog.offline.warn("unable to update contact rankings", e);
                }
                try {
                    if (acct.getBooleanAttr(Provisioning.A_zimbraPrefAutoAddAddressEnabled, false)) {
                        Collection<InternetAddress> newContacts = ms.getNewContacts(
                            sentAddresses, acct, context, origMbox);
                        ms.saveNewContacts(newContacts, context, origMbox);
                    }
                } catch (Exception e) {
                    OfflineLog.offline.warn("unable to update auto addresses", e);
                }
            }
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


}
