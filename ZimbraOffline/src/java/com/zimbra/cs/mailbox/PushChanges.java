/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.mail.Message.RecipientType;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.dom4j.QName;

import com.google.common.collect.ImmutableSet;
import com.zimbra.client.ZMailbox;
import com.zimbra.common.mailbox.Color;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.common.mime.shim.JavaMailInternetAddress;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.util.BigByteBuffer;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.mailbox.Contact.Attachment;
import com.zimbra.cs.mailbox.InitialSync.InviteMimeLocator;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.calendar.Invite;
import com.zimbra.cs.mailbox.util.TagUtil;
import com.zimbra.cs.mailbox.util.TypedIdList;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.offline.util.OfflineErrorUtil;
import com.zimbra.cs.service.mail.ItemAction;
import com.zimbra.cs.service.mail.Sync;
import com.zimbra.cs.service.mail.ToXML;
import com.zimbra.cs.service.util.ItemIdFormatter;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.util.JMSession;
import com.zimbra.soap.ZimbraSoapContext;

public class PushChanges {

    private static class LocalInviteMimeLocator implements InviteMimeLocator {
        ZcsMailbox ombx;

        public LocalInviteMimeLocator(ZcsMailbox ombx) {
            this.ombx = ombx;
        }

        @Override
        public Pair<Integer, InputStream> getInviteMime(int calendarItemId, int inviteId) throws ServiceException {
            CalendarItem cal = ombx.getCalendarItemById(PushChanges.sContext, calendarItemId);
            MimeMessage mm = cal.getSubpartMessage(inviteId);
            BigByteBuffer bbb = null;
            try {
                if (mm != null) {
                    bbb = new BigByteBuffer(mm.getSize());
                    mm.writeTo(bbb);
                } else {
                    bbb = new BigByteBuffer();
                }
                bbb.doneWriting();
                return new Pair<Integer, InputStream>(bbb.length(), bbb.getInputStream());
            } catch (Exception x) {
                if (bbb != null)
                    try {
                        bbb.destroy();
                    } catch (IOException e) {}
                throw ServiceException.FAILURE("calitem=" + calendarItemId + ";inv=" + inviteId, x);
            }
        }
    }

    /** The bitmask of all message changes that we propagate to the server. */
    static final int MESSAGE_CHANGES = Change.UNREAD | Change.FLAGS | Change.TAGS | Change.FOLDER | Change.COLOR |
            Change.CONTENT;

    /** The bitmask of all chat changes that we propagate to the server. */
    static final int CHAT_CHANGES = Change.UNREAD | Change.FLAGS | Change.TAGS | Change.FOLDER | Change.COLOR |
            Change.CONTENT;

    /** The bitmask of all contact changes that we propagate to the server. */
    static final int CONTACT_CHANGES = Change.FLAGS | Change.TAGS | Change.FOLDER | Change.COLOR | Change.CONTENT;

    /** The bitmask of all folder changes that we propagate to the server. */
    static final int FOLDER_CHANGES = Change.FLAGS | Change.FOLDER | Change.NAME | Change.COLOR | Change.URL |
            Change.ACL | Change.RETENTION_POLICY;

    /** The bitmask of all search folder changes that we propagate to the server. */
    static final int SEARCH_CHANGES = Change.FLAGS | Change.FOLDER | Change.NAME | Change.COLOR | Change.QUERY;

    /** The bitmask of all tag changes that we propagate to the server. */
    static final int TAG_CHANGES = Change.NAME | Change.COLOR;

    /** The bitmask of all appointment changes that we propagate to the server. */
    static final int APPOINTMENT_CHANGES = Change.FLAGS | Change.TAGS | Change.FOLDER | Change.COLOR | Change.CONTENT |
            Change.INVITE;

    /** The bitmask of all document changes that we propagate to the server. */
    static final int DOCUMENT_CHANGES = Change.FLAGS | Change.TAGS | Change.FOLDER | Change.COLOR | Change.CONTENT |
            Change.NAME;

    /** A list of all the "leaf types" (i.e. non-folder types) that we synchronize with the server. */
    private static final Set<MailItem.Type> PUSH_LEAF_TYPES = EnumSet.of(MailItem.Type.TAG, MailItem.Type.CONTACT,
            MailItem.Type.MESSAGE, MailItem.Type.CHAT, MailItem.Type.APPOINTMENT, MailItem.Type.TASK,
            MailItem.Type.DOCUMENT);

    /** The set of all the MailItem types that we synchronize with the server. */
    static final Set<MailItem.Type> PUSH_TYPES = ImmutableSet.of(MailItem.Type.FOLDER, MailItem.Type.SEARCHFOLDER,
            MailItem.Type.TAG, MailItem.Type.CONTACT, MailItem.Type.MESSAGE, MailItem.Type.CHAT,
            MailItem.Type.APPOINTMENT, MailItem.Type.TASK, MailItem.Type.DOCUMENT,
            MailItem.Type.MOUNTPOINT);

    private static final TracelessContext sContext = new TracelessContext();

    private final ZcsMailbox ombx;
    private ZMailbox mZMailbox = null;

    private PushChanges(ZcsMailbox mbox) {
        ombx = mbox;
    }

    private ZMailbox getZMailbox() throws ServiceException {
        if (mZMailbox == null) {
            ZMailbox.Options options = new ZMailbox.Options(ombx.getAuthToken(), ombx.getSoapUri());
            options.setNoSession(true);
            options.setUserAgent(OfflineLC.zdesktop_name.value(), OfflineLC.getFullVersion());
            options.setTimeout(OfflineLC.zdesktop_request_timeout.intValue());
            options.setRetryCount(1);
            mZMailbox = ZMailbox.getMailbox(options);
        }
        return mZMailbox;
    }

    private TagSync getTagSync() {
        return ombx.getTagSync();
    }

    public static boolean sync(ZcsMailbox ombx, boolean isOnRequest) throws ServiceException {
        return new PushChanges(ombx).sync(isOnRequest);
    }

    public static boolean syncFolder(ZcsMailbox ombx, int id, boolean suppressRssFailure, ZimbraSoapContext zsc) throws ServiceException {
        return new PushChanges(ombx).syncFolder(id, suppressRssFailure, zsc);
    }

    private boolean sync(boolean isOnRequest) throws ServiceException {
        int limit;
        TypedIdList changes, tombstones;
        // do simple change batch push first
        List<Pair<Integer, Integer>> simpleReadChanges = null; //list of Pair<itemId,modSequence> for items marked read locally
        List<Pair<Integer, Integer>> simpleUnreadChanges = null; //list of Pair<itemId,modSequence> for items marked unread locally
        Map<Integer, List<Pair<Integer, Integer>>> simpleFolderMoveChanges = null; //list of Pair<itemId,modSequence> for items locally moved, sorted by folderId in map

        ombx.lock.lock();
        try {
            limit = ombx.getLastChangeID();
            tombstones = ombx.getTombstones(0);
            changes = ombx.getLocalChanges(sContext);
            if (!changes.isEmpty()) {
                simpleReadChanges = ombx.getSimpleUnreadChanges(sContext, false);
                simpleUnreadChanges = ombx.getSimpleUnreadChanges(sContext, true);
                simpleFolderMoveChanges = ombx.getFolderMoveChanges(sContext);
            }
        } finally {
            ombx.lock.release();
        }

        OfflineSyncManager.getInstance().continueOK();

        OfflineLog.offline.debug("starting change push");

        boolean hasDeletes = !tombstones.isEmpty();

        // because tags reuse IDs, we need to do tag deletes before any other changes (especially tag creates)
        List<Integer> tagDeletes = tombstones.getIds(MailItem.Type.TAG);
        if (tagDeletes != null && !tagDeletes.isEmpty()) {
            Element request = new Element.XMLElement(MailConstants.TAG_ACTION_REQUEST);
            request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_HARD_DELETE).addAttribute(MailConstants.A_ID, concatenateIds(getTagSync().remoteIds(tagDeletes)));
            ombx.sendRequest(request);
            OfflineLog.offline.debug("push: pushed tag deletes: " + tagDeletes);
            tombstones.remove(MailItem.Type.TAG);
        }

        // do folder ops top-down so that we don't get dinged when folders switch places
        if (!changes.isEmpty()) {
            if (changes.getIds(MailItem.Type.FOLDER) != null || changes.getIds(MailItem.Type.SEARCHFOLDER) != null ||
                    changes.getIds(MailItem.Type.MOUNTPOINT) != null) {
                for (Folder folder : ombx.getFolderById(sContext, Mailbox.ID_FOLDER_ROOT).getSubfolderHierarchy()) {
                    if (changes.remove(folder.getType(), folder.getId())) {
                        switch (folder.getType()) {
                            case SEARCHFOLDER:
                                syncSearchFolder(folder.getId());
                                break;
                            case MOUNTPOINT:
                            case FOLDER:
                                syncFolder(folder.getId(), true, null);
                                break;
                        }
                    }
                }
                changes.remove(MailItem.Type.SEARCHFOLDER);
                changes.remove(MailItem.Type.MOUNTPOINT);
                changes.remove(MailItem.Type.FOLDER);
            }
        }

        // make sure that tags are synced before subsequent item updates
        List<Integer> changedTags = changes.getIds(MailItem.Type.TAG);
        if (changedTags != null) {
            for (int id : changedTags) {
                syncTag(id);
            }
            changes.remove(MailItem.Type.TAG);
        }

        // Do simple change batch push first
        Set<Integer> batched = new HashSet<Integer>();
        if (simpleReadChanges != null && simpleReadChanges.size() > 0) {
            OfflineSyncManager.getInstance().continueOK();
            pushSimpleChanges(simpleReadChanges, Change.UNREAD, false, 0, batched);
        }

        if (simpleUnreadChanges != null && simpleUnreadChanges.size() > 0) {
            OfflineSyncManager.getInstance().continueOK();
            pushSimpleChanges(simpleUnreadChanges, Change.UNREAD, true, 0, batched);
        }

        if (simpleFolderMoveChanges != null && simpleFolderMoveChanges.size() > 0) {
            Set<Integer> folders = simpleFolderMoveChanges.keySet();
            for (int folderId : folders) {
                OfflineSyncManager.getInstance().continueOK();
                pushSimpleChanges(simpleFolderMoveChanges.get(folderId), Change.FOLDER, false, folderId, batched);
            }
        }

        // modifies must come after folder and tag creates so that move/tag ops can succeed
        if (!changes.isEmpty()) {
            for (MailItem.Type type : PUSH_LEAF_TYPES) {
                List<Integer> ids = changes.getIds(type);
                if (ids == null) {
                    continue;
                }
                for (int id : ids) {
                    if (OfflineSyncManager.getInstance().isInSkipList(id)) {
                        OfflineLog.offline.warn("Skipped push item id=%d per zdesktop_sync_skip_idlist", id);
                        continue;
                    }

                    if (batched.contains(id)) //already done
                        continue;

                    OfflineSyncManager.getInstance().continueOK();

                    try {
                        switch (type) {
                            case TAG:
                                syncTag(id);
                                break;
                            case CONTACT:
                                syncContact(id);
                                break;
                            case MESSAGE:
                                syncMessage(id);
                                break;
                            case APPOINTMENT:
                                syncCalendarItem(id, true);
                                break;
                            case TASK:
                                syncCalendarItem(id, false);
                                break;
                            case DOCUMENT:
                                syncDocument(id, tombstones);
                                break;
                        }
                    } catch (Exception x) {
                        if (!SyncExceptionHandler.isRecoverableException(ombx, id, "PushChanges.sync", x)) {
                            SyncExceptionHandler.pushItemFailed(ombx, id, x);
                            ombx.setChangeMask(sContext, id, type, 0); //clear change mask since we failed to push up an item due to unrecoverable reasons
                        }
                    }
                }
            }
        }

        // folder deletes need to come after moves are processed, else we'll be deleting items we shouldn't
        if (!tombstones.isEmpty()) {
            OfflineSyncManager.getInstance().continueOK();

            String ids = concatenateIds(tombstones.getAll());
            Element request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
            request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_HARD_DELETE).addAttribute(MailConstants.A_ID, ids);
            ombx.sendRequest(request);
            OfflineLog.offline.debug("push: pushed deletes: [" + ids + ']');
        }

        if (hasDeletes)
            ombx.clearTombstones(sContext, limit);

        OfflineLog.offline.debug("ending change push");

        return true;
    }

    /** Tracks messages that we've called SendMsg on but never got back a
     *  response.  This should help avoid duplicate sends when the connection
     *  goes away in the process of a SendMsg.<p>
     *
     *  key: a String of the form <tt>account-id:message-id</tt><p>
     *  value: a Pair containing the content change ID and the "send UID"
     *         used when the message was previously sent. */
    private static final Map<String, Pair<Integer, String>> sSendUIDs = new HashMap<String, Pair<Integer, String>>();

    /** For each message in the Outbox, uploads it to the remote server, calls
     *  SendMsg to dispatch it appropriately, and deletes it from the local
     *  store.  As a side effect, removes the corresponding (now-deleted)
     *  drafts from the list of pending creates that need to be pushed to the
     *  server. */
    private int sendPendingMessages(boolean isOnRequest) throws ServiceException {
        int totalSent = 0;
        OfflineSyncManager syncMan = OfflineSyncManager.getInstance();
        for (Iterator<Integer> iterator = OutboxTracker.iterator(ombx, isOnRequest ? 0L : ombx.getSyncFrequency());	iterator.hasNext();) {
            int id = iterator.next();
            try {
                Message msg = ombx.getMessageById(sContext, id);
                if (msg.getFolderId() != DesktopMailbox.ID_FOLDER_OUTBOX) {
                    OutboxTracker.remove(ombx, id);
                    continue;
                }

                OfflineLog.offline.debug("push: sending mail (" + id + "): " + msg.getSubject());
                syncMan.syncStart(ombx.getAccount());

                // try to avoid repeated sends of the same message by tracking "send UIDs" on SendMsg requests
                String msgKey = ombx.getAccountId() + ':' + id;
                Pair<Integer, String> sendRecord = sSendUIDs.get(msgKey);
                String sendUID = sendRecord == null || sendRecord.getFirst() != msg.getSavedSequence() ? UUID.randomUUID().toString() : sendRecord.getSecond();
                sSendUIDs.put(msgKey, new Pair<Integer, String>(msg.getSavedSequence(), sendUID));

                try {
                    String uploadId = uploadMessage(msg);
                    Element request = new Element.XMLElement(MailConstants.SEND_MSG_REQUEST).addAttribute(MailConstants.A_SEND_UID, sendUID);
                    Element m = request.addElement(MailConstants.E_MSG).addAttribute(MailConstants.A_ATTACHMENT_ID, uploadId);
                    if (!msg.getDraftOrigId().equals(""))
                        m.addAttribute(MailConstants.A_ORIG_ID, msg.getDraftOrigId()).addAttribute(MailConstants.A_REPLY_TYPE, msg.getDraftReplyType());
                    String saveToSent = OfflineProvisioning.getOfflineInstance().getLocalAccount().getAttr(Provisioning.A_zimbraPrefSaveToSent);
                    if (!Boolean.valueOf(saveToSent)) {
                        request.addAttribute(MailConstants.A_NO_SAVE_TO_SENT,1);
                    }
                    //run one more time to make sure it's still in outbox after we finished uploading the message
                    msg = ombx.getMessageById(sContext, id);
                    if (msg.getFolderId() != DesktopMailbox.ID_FOLDER_OUTBOX) {
                        OutboxTracker.remove(ombx, id);
                        continue;
                    }

                    ombx.sendRequest(request);
                    OfflineLog.offline.debug("push: sent mail (" + id + "): " + msg.getSubject());
                    ++totalSent;

                    // remove the draft from the outbox
                    ombx.delete(sContext, id, MailItem.Type.MESSAGE);
                    OfflineLog.offline.debug("push: deleted pending draft (" + id + ')');
                } catch (ServiceException x) {
                    if ((x instanceof ZClientException || x instanceof SoapFaultException) && !x.isReceiversFault() &&
                            !x.getCode().equals(ZClientException.IO_ERROR) && !x.getCode().equals(ZClientException.UPLOAD_FAILED)) { //supposedly this is client fault
                        OfflineLog.offline.debug("push: failed to send mail (" + id + "): " + msg.getSubject(), x);

                        ombx.move(sContext, id, MailItem.Type.MESSAGE, Mailbox.ID_FOLDER_DRAFTS); //move message back to drafts folder;

                        // we need to tell user of the failure
                        try {
                            Account account = ombx.getAccount();
                            MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSmtpSession(account));
                            mm.setSentDate(new Date());
                            mm.setFrom(new InternetAddress("donotreply@host.local", "Desktop Notifier"));
                            mm.setRecipient(RecipientType.TO, new JavaMailInternetAddress(account.getName()));

                            String sentDate = new SimpleDateFormat("MMMMM d, yyyy").format(msg.getDate());
                            String sentTime = new SimpleDateFormat("h:mm a").format(msg.getDate());

                            String text = "Your message \"" + msg.getSubject() + "\" sent on " + sentDate + " at " + sentTime + " to \"" + msg.getRecipients() + "\" can't be delivered.  None of the recipients will receive the message.  It has been returned to Drafts folder for your review.\n";
                            String subject = null;
                            if (x.getCode().equals(ZClientException.UPLOAD_SIZE_LIMIT_EXCEEDED)) {
                                subject = "message size exceeds server limit";
                            } else if (x.getCode().equals(MailServiceException.SEND_ABORTED_ADDRESS_FAILURE)) {
                                subject = x.getMessage();
                            } else {
                                text += "\n----------------------------------------------------------------------------\n\n" +
                                SyncExceptionHandler.sendMailFailed(ombx, id, x);
                                subject = x.getCode();
                            }
                            mm.setSubject("Delivery Failure Notification: " + subject);
                            mm.setText(text);

                            mm.saveChanges(); //must call this to update the headers
                            ParsedMessage pm = new ParsedMessage(mm, true);
                            DeliveryOptions dopt = new DeliveryOptions().setFolderId(Mailbox.ID_FOLDER_INBOX).setNoICal(true).setFlags(Flag.BITMASK_UNREAD);
                            ombx.addMessage(sContext, pm, dopt, null);
                        } catch (Exception e) {
                            OfflineLog.offline.warn("can't save warning of failed push (" + id + ")" + msg.getSubject(), e);
                        }
                    } else {
                        OutboxTracker.recordFailure(ombx, id);
                        OfflineLog.offline.info("push: %s when sending message: %s", x.getCode(), msg.getSubject(), x);
                        continue; //will retry later
                    }
                }

                OutboxTracker.remove(ombx, id);

                // the draft is now gone, so remove it from the "send UID" hash and the list of items to push
                sSendUIDs.remove(msgKey);
            } catch (NoSuchItemException nsie) {
                OutboxTracker.remove(ombx, id);
                OfflineLog.offline.debug("push: ignoring deleted pending mail (" + id + ")");
            }
        }
        return totalSent;
    }

    public static int sendPendingMessages(ZcsMailbox ombx, boolean isOnRequest) throws ServiceException {
        return new PushChanges(ombx).sendPendingMessages(isOnRequest);
    }

    /**
     * Before 5.0.8 there's a bug that makes simply streaming up message content broken
     */
    private static final OfflineAccount.Version minServerVersionForUploadStreaming = new OfflineAccount.Version("5.0.9");

    /** Uploads the given message to the remote server using file upload.
     *  We scale the allowed timeout with the size of the message -- a base
     *  of 5 seconds, plus 1 second per 25K of message size. */
    private String uploadMessage(Message msg) throws ServiceException {
        int timeout = (int) (OfflineLC.http_connection_timeout.intValue() + msg.getSize() / 25000 * Constants.MILLIS_PER_SECOND);
        if (ombx.getRemoteServerVersion().isAtLeast(minServerVersionForUploadStreaming))
            return getZMailbox().uploadContentAsStream("msg-" + msg.getId(), msg.getContentStream(), MimeConstants.CT_MESSAGE_RFC822, msg.getSize(), timeout);
        else
            return getZMailbox().uploadAttachment("message", msg.getContent(), MimeConstants.CT_MESSAGE_RFC822, timeout);
    }

    /** Turns a List of Integers into a String of the form <tt>1,2,3,4</tt>. */
    private String concatenateIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Integer id : ids) {
            if (i++ != 0)
                sb.append(',');
            sb.append(id);
        }
        return sb.toString();
    }

    /** Dispatches a push request (either a create or an update) to the remote
     *  mailbox.  If there's a naming conflict, attempts to discover the remote
     *  item and rename it out of the way.  If that fails, renames the local
     *  item and throws the original <tt>mail.ALREADY_EXISTS</tt> exception.
     *
     * @param request   The SOAP request to be executed remotely.
     * @param create    Whether the request is a create or update operation.
     * @param id        The id of the created/updated item.
     * @param type      The type of the created/updated item.
     * @param name      The name of the created/updated item.
     * @param folderId  The location of the created/updated item.
     * @return A {@link Pair} containing the new item's ID and content change
     *         sequence for creates, or <tt>null</tt> for updates. */
    private Pair<Integer,Integer> pushRequest(Element request, boolean create, int id, MailItem.Type type, String name,
            int folderId) throws ServiceException {
        SoapFaultException originalException = null;
        try {
            // try to create/update the item as requested
            return sendRequest(request, create, id, type, name);
        } catch (SoapFaultException sfe) {
            if (name == null || !sfe.getCode().equals(MailServiceException.ALREADY_EXISTS))
                throw sfe;
            OfflineLog.offline.info("push: detected naming conflict with remote item " + folderId + '/' + name);
            originalException = sfe;
        }

        String uuid = '{' + UUID.randomUUID().toString() + '}', conflictRename;
        if (name.length() + uuid.length() > MailItem.MAX_NAME_LENGTH)
            conflictRename = name.substring(0, MailItem.MAX_NAME_LENGTH - uuid.length()) + uuid;
        else
            conflictRename = name + uuid;

        try {
            // figure out what the conflicting remote item is
            Element query = new Element.XMLElement(MailConstants.GET_ITEM_REQUEST);
            query.addElement(MailConstants.E_ITEM).addAttribute(MailConstants.A_FOLDER, folderId).addAttribute(MailConstants.A_NAME, name);
            Element conflict = ombx.sendRequest(query).listElements().get(0);
            int conflictId = (int) conflict.getAttributeLong(MailConstants.A_ID);
            MailItem.Type conflictType = Sync.typeForElementName(conflict.getName());

            // rename the conflicting item out of the way
            Element rename = null;
            switch (conflictType) {
                case MOUNTPOINT:
                case SEARCHFOLDER:
                case FOLDER:
                    rename = new Element.XMLElement(MailConstants.FOLDER_ACTION_REQUEST);
                    break;
                case TAG:
                    rename = new Element.XMLElement(MailConstants.TAG_ACTION_REQUEST);
                    break;
                default:
                    rename = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
                    break;
            }
            rename.addElement(MailConstants.E_ACTION)
                .addAttribute(MailConstants.A_OPERATION, ItemAction.OP_RENAME)
                .addAttribute(MailConstants.A_ID, conflictId)
                .addAttribute(MailConstants.A_FOLDER, folderId)
                .addAttribute(MailConstants.A_NAME, conflictRename);
            ombx.sendRequest(rename);
            OfflineLog.offline.info("push: renamed remote " + conflictType + " (" + conflictId + ") to " + folderId + '/' + conflictRename);

            // retry the original create/update
            return sendRequest(request, create, id, type, name);
        } catch (SoapFaultException sfe) {
            // remote server doesn't support GetItem, so all we can do is to rename the local item and retry
            boolean unsupported = sfe.getCode().equals(ServiceException.UNKNOWN_DOCUMENT);
            OfflineLog.offline.info("push: could not resolve naming conflict with remote item; will rename locally", unsupported ? null : sfe);
        }

        ombx.rename(null, id, type, conflictRename, folderId);
        OfflineLog.offline.info("push: renamed local " + type + " (" + id + ") to " + folderId + '/' + conflictRename);
        throw originalException;
    }

    /** Dispatches a push request (either a create or an update) to the remote
     *  mailbox.  Merely sends the request and logs; does not perform any
     *  conflict resolution.
     *
     * @param request  The SOAP request to be executed remotely.
     * @param create   Whether the request is a create or update operation.
     * @param id       The id of the created/updated item (for logging).
     * @param type     The type of the created/updated item (for logging).
     * @param name     The name of the created/updated item (for logging).
     * @return A {@link Pair} containing the new item's ID and content change
     *         sequence for creates, or <tt>null</tt> for updates. */
    private Pair<Integer,Integer> sendRequest(Element request, boolean create, int id, MailItem.Type type, String name)
            throws ServiceException {
        // try to create/update the item as requested
        Element response = ombx.sendRequest(request);
        if (create) {
            int newId = (int) response.getElement(Sync.elementNameForType(type)).getAttributeLong(MailConstants.A_ID);
            int newRevision = (int) response.getElement(Sync.elementNameForType(type)).getAttributeLong(MailConstants.A_REVISION, -1);
            OfflineLog.offline.debug("push: created " + type + " (" + newId + ") from local (" + id + (name == null ? ")" : "): " + name));
            return new Pair<Integer,Integer>(newId, newRevision);
        } else {
            OfflineLog.offline.debug("push: updated " + type + " (" + id + (name == null ? ")" : "): " + name));
            return null;
        }
    }

    private boolean syncSearchFolder(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.FOLDER_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);

        int flags, parentId;
        byte colorByte = 0;
        String colorStr = null;
        Color color;
        String name, query, searchTypes, sort;
        boolean create = false;
        ombx.lock.lock();
        try {
            SearchFolder search = ombx.getSearchFolderById(sContext, id);
            name = search.getName();
            flags = search.getInternalFlagBitmask();
            color = search.getRgbColor();
            parentId = search.getFolderId();
            query = search.getQuery();
            searchTypes = search.getReturnTypes();
            sort = search.getSortField();

            if (color.hasMapping()) {
                colorByte = color.getMappedColor();
            }
            else {
                colorStr = color.toString();
            }
            int mask = ombx.getChangeMask(sContext, id, MailItem.Type.SEARCHFOLDER);
            if ((mask & Change.CONFLICT) != 0) {
                // this is a new search folder; need to push to the server
                request = new Element.XMLElement(MailConstants.CREATE_SEARCH_FOLDER_REQUEST);
                action = request.addElement(MailConstants.E_SEARCH);
                create = true;
            }
            if (create || (mask & Change.FLAGS) != 0) {
                action.addAttribute(MailConstants.A_FLAGS, Flag.toString(flags));
            }
            if (create || (mask & Change.FOLDER) != 0) {
                action.addAttribute(MailConstants.A_FOLDER, parentId);
            }
            if (create || (mask & Change.COLOR) != 0) {
                if (color.hasMapping()) {
                    action.addAttribute(MailConstants.A_COLOR, colorByte);
                } else {
                    action.addAttribute(MailConstants.A_RGB, colorStr);
                }
            }
            if (create || (mask & Change.NAME) != 0) {
                action.addAttribute(MailConstants.A_NAME, name);
            }
            if (create || (mask & Change.QUERY) != 0) {
                action.addAttribute(MailConstants.A_QUERY, query)
                    .addAttribute(MailConstants.A_SEARCH_TYPES, searchTypes)
                    .addAttribute(MailConstants.A_SORT_FIELD, sort);
            }
        } finally {
            ombx.lock.release();
        }

        try {
            Pair<Integer, Integer> createData = pushRequest(request, create, id, MailItem.Type.SEARCHFOLDER, name, parentId);
            if (create) {
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.Type.SEARCHFOLDER, createData.getFirst())) {
                    return true;
                }
                id = createData.getFirst();
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_FOLDER))
                throw sfe;
            OfflineLog.offline.info("push: remote search folder " + id + " has been deleted; skipping");
        }

        ombx.lock.lock();
        try {
            SearchFolder search = ombx.getSearchFolderById(sContext, id);
            // check to see if the search was changed while we were pushing the update...
            int mask = 0;
            if (flags != search.getInternalFlagBitmask())  {
                mask |= Change.FLAGS;
            }
            if (parentId != search.getFolderId()) {
                mask |= Change.NAME;
            }
            if (!color.equals(search.getRgbColor())) {
                mask |= Change.COLOR;
            }
            if (!name.equals(search.getName())) {
                mask |= Change.NAME;
            }
            if (!query.equals(search.getQuery())) {
                mask |= Change.QUERY;
            }
            if (!searchTypes.equals(search.getReturnTypes())) {
                mask |= Change.QUERY;
            }
            if (!sort.equals(search.getSortField())) {
                mask |= Change.QUERY;
            }

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.Type.SEARCHFOLDER, mask);
            return (mask == 0);
        } finally {
            ombx.lock.release();
        }
    }

    private boolean syncFolder(int id, boolean suppressRssFailure, ZimbraSoapContext zsc) throws ServiceException {
        QName elementName = MailConstants.FOLDER_ACTION_REQUEST;
        Element request = zsc != null ? zsc.createElement(elementName) : new Element.XMLElement(elementName);
        Element action = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);

        int flags, parentId;
        byte colorByte = 0;
        String colorStr = null;
        Color color;
        String name, url;
        boolean create = false;
        Folder folder = null;
        ombx.lock.lock();
        try {
            folder = ombx.getFolderById(sContext, id);
            name = folder.getName();  parentId = folder.getFolderId();  flags = folder.getInternalFlagBitmask();
            url = folder.getUrl();    color = folder.getRgbColor();

            if (color.hasMapping()) {
                colorByte = color.getMappedColor();
            }
            else {
                colorStr = color.toString();
            }
            int mask = 0;
            switch (folder.getType()) {
                case MOUNTPOINT:
                    mask = ombx.getChangeMask(sContext, id, MailItem.Type.MOUNTPOINT);
                    break;
                case FOLDER:
                    mask = ombx.getChangeMask(sContext, id, MailItem.Type.FOLDER);
                    break;
            }
            if ((mask & Change.CONFLICT) != 0) {
                // this is a new folder; need to push to the server
                elementName = MailConstants.CREATE_FOLDER_REQUEST;
                request = zsc != null ? zsc.createElement(elementName) : new Element.XMLElement(elementName);
                action = request.addElement(MailConstants.E_FOLDER).addAttribute(MailConstants.A_DEFAULT_VIEW,
                        folder.getDefaultView().toString());
                create = true;
            }
            if (create || (mask & Change.FLAGS) != 0) {
                action.addAttribute(MailConstants.A_FLAGS, Flag.toString(flags));
            }
            if (create || (mask & Change.FOLDER) != 0) {
                action.addAttribute(MailConstants.A_FOLDER, parentId);
            }
            if (create || (mask & Change.COLOR) != 0) {
                if (color.hasMapping()) {
                    action.addAttribute(MailConstants.A_COLOR, colorByte);
                } else {
                    action.addAttribute(MailConstants.A_RGB, colorStr);
                }
            }
            if (create || (mask & Change.NAME) != 0) {
                action.addAttribute(MailConstants.A_NAME, name);
            }
            if (create || (mask & Change.URL) != 0) {
                action.addAttribute(MailConstants.A_URL, url);
            }
            // FIXME: does not support ACL sync at all...
        } finally {
            ombx.lock.release();
        }

        try {
            Pair<Integer,Integer> createData = null;
            switch (folder.getType()) {
                case MOUNTPOINT:
                    createData = pushRequest(request, create, id, MailItem.Type.MOUNTPOINT, name, parentId);
                    break;
                case FOLDER:
                    createData = pushRequest(request, create, id, MailItem.Type.FOLDER, name, parentId);
                    break;
            }
            if (create) {
                // make sure the old item matches the new item...
                switch (folder.getType()) {
                    case MOUNTPOINT:
                        if (!ombx.renumberItem(sContext, id, MailItem.Type.MOUNTPOINT, createData.getFirst())) {
                            return true;
                        }
                        break;
                    case FOLDER:
                        if (!ombx.renumberItem(sContext, id, MailItem.Type.FOLDER, createData.getFirst())) {
                            return true;
                        }
                        break;
                }
                id = createData.getFirst();
            }
        } catch (SoapFaultException sfe) {
            if (suppressRssFailure && folder.getUrl() != null && folder.getUrl() != "") {
                OfflineErrorUtil.reportError(ombx, folder.getId(), "failed to sync rss url ["+folder.getUrl()+"]", sfe);
                return true;
            } else if (!sfe.getCode().equals(MailServiceException.NO_SUCH_FOLDER)) {
                throw sfe;
            }
            OfflineLog.offline.info("push: remote folder " + id + " has been deleted; skipping");
        }

        ombx.lock.lock();
        try {
            folder = ombx.getFolderById(sContext, id);
            // check to see if the folder was changed while we were pushing the update...
            int mask = 0;
            if (flags != folder.getInternalFlagBitmask()) {
                mask |= Change.FLAGS;
            }
            if (parentId != folder.getFolderId()) {
                mask |= Change.NAME;
            }
            if (!color.equals(folder.getRgbColor())) {
                mask |= Change.COLOR;
            }
            if (!name.equals(folder.getName())) {
                mask |= Change.NAME;
            }
            if (!url.equals(folder.getUrl())) {
                mask |= Change.URL;
            }
            // update or clear the change bitmask
            switch (folder.getType()) {
                case MOUNTPOINT:
                    ombx.setChangeMask(sContext, id, MailItem.Type.MOUNTPOINT, mask);
                    break;
                case FOLDER:
                    ombx.setChangeMask(sContext, id, MailItem.Type.FOLDER, mask);
                    break;
            }
            return (mask == 0);
        } finally {
            ombx.lock.release();
        }
    }

    private boolean syncTag(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.TAG_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION)
                .addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE)
                .addAttribute(MailConstants.A_ID, getTagSync().remoteTagId(id));

        byte colorByte = 0;
        String colorStr = null;
        Color color;
        String name;
        boolean create = false;
        ombx.lock.lock();
        try {
            Tag tag = ombx.getTagById(sContext, id);
            color = tag.getRgbColor(); name = tag.getName();

            if (color.hasMapping()) {
                colorByte = color.getMappedColor();
            }
            else {
                colorStr = color.toString();
            }
            int mask = ombx.getChangeMask(sContext, id, MailItem.Type.TAG);
            if ((mask & Change.CONFLICT) != 0) {
                // this is a new tag; need to push to the server
                request = new Element.XMLElement(MailConstants.CREATE_TAG_REQUEST);
                action = request.addElement(MailConstants.E_TAG);
                create = true;
            }
            //in ZCS 7 & 8 tagaction op=update seems to reset color if not specified on request...so always encode it
            if (color.hasMapping()) {
                action.addAttribute(MailConstants.A_COLOR, colorByte);
            } else {
                action.addAttribute(MailConstants.A_RGB, colorStr);
            }
            if (create || (mask & Change.NAME) != 0) {
                action.addAttribute(MailConstants.A_NAME, name);
            }
        } finally {
            ombx.lock.release();
        }

        try {
            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.Type.TAG, name, Mailbox.ID_FOLDER_TAGS);
            if (create) {
                int newId = createData.getFirst();
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.Type.TAG, newId)) {
                    return true;
                }
                id = newId;
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_TAG)) {
                throw sfe;
            }
            OfflineLog.offline.info("push: remote tag " + id + " has been deleted; skipping");
        }

        ombx.lock.lock();
        try {
            Tag tag = ombx.getTagById(sContext, id);
            // check to see if the tag was changed while we were pushing the update...
            int mask = 0;
            if (!color.equals(tag.getRgbColor())) {
                mask |= Change.COLOR;
            }
            if (!name.equals(tag.getName())) {
                mask |= Change.NAME;
            }
            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.Type.TAG, mask);
            return (mask == 0);
        } finally {
            ombx.lock.release();
        }
    }

    private void pushSimpleChanges(List<Pair<Integer, Integer>> changes, int changeMask, boolean isUnread, int folderId,
            Set<Integer> doneSet) throws ServiceException {
        assert (changes != null && changes.size() > 0);
        assert (changeMask == Change.UNREAD || changeMask == Change.FOLDER); //only these two are considered simple

        Element request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION);

        switch (changeMask) {
            case Change.FOLDER:
                action.addAttribute(MailConstants.A_OPERATION, ItemAction.OP_MOVE);
                action.addAttribute(MailConstants.A_FOLDER, folderId);
                break;
            case Change.UNREAD:
                action.addAttribute(MailConstants.A_OPERATION, (isUnread ? "!" : "") + ItemAction.OP_READ);
                break;
            default:
                assert (false);
                break;
        }

        StringBuilder sb = new StringBuilder();
        int[] ids = new int[changes.size()];
        for (int i = 0; i < changes.size(); ++i) {
            int id = changes.get(i).getFirst();
            sb.append((i > 0 ? "," : "") + id);
            ids[i] = id;
        }
        action.addAttribute(MailConstants.A_ID, sb.toString());

        try {
            /* Element response = */ ombx.sendRequest(request);
            OfflineLog.offline.info("push: batch updated " + sb.toString());
        } catch (SoapFaultException sfe) {
            OfflineLog.offline.warn("push: failed batch update of " + sb.toString() + "; fall back to itemized push", sfe);
            return;
        }

        ombx.lock.lock();
        try {
            Map<Integer, Integer> refresh = ombx.getItemModSequences(sContext, ids);
            for (Pair<Integer, Integer> pair : changes) {
                int id = pair.getFirst();
                Integer newModSequence = refresh.get(id);
                if (newModSequence != null) {
                    if (newModSequence.intValue() == pair.getSecond()) {
                        //because we know the item hasn't changed since we last checked,
                        //and we know it was a simple change, we can simply clear the mask.
                        ombx.setChangeMask(sContext, id, MailItem.Type.UNKNOWN, 0);
                        doneSet.add(id);
                    } else {
                        OfflineLog.offline.debug("push: item " + id + " further modified from local during push");
                    }
                } else {
                    OfflineLog.offline.debug("push: item " + id + " deleted from local during push");
                }
            }
        } finally {
            ombx.lock.release();
        }
    }

    private boolean syncContact(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.MODIFY_CONTACT_REQUEST).addAttribute(MailConstants.A_REPLACE, "1");
        Element cnElem = request.addElement(MailConstants.E_CONTACT).addAttribute(MailConstants.A_ID, id);

        int flags, folderId;
        String[] tags;
        long date;
        byte colorByte = 0;
        String colorStr = null;
        Color color;
        boolean create = false;
        Contact cn = null;
        int mask = 0;
        ombx.lock.lock();
        try {
            cn = ombx.getContactById(sContext, id);
            date = cn.getDate();    flags = cn.getFlagBitmask();  tags = cn.getTags();
            color = cn.getRgbColor();  folderId = cn.getFolderId();

            if (color.hasMapping()) {
                colorByte = color.getMappedColor();
            }
            else {
                colorStr = color.toString();
            }
            mask = ombx.getChangeMask(sContext, id, MailItem.Type.CONTACT);
            if ((mask & Change.CONFLICT) != 0) {
                // this is a new contact; need to push to the server
                request = new Element.XMLElement(MailConstants.CREATE_CONTACT_REQUEST);
                cnElem = request.addElement(MailConstants.E_CONTACT);
                create = true;
            }

            if (create || (mask & Change.CONTENT) != 0) {
                for (Map.Entry<String, String> field : cn.getFields().entrySet()) {
                    String name = field.getKey(), value = field.getValue();
                    if (name == null || name.trim().equals("") || value == null || value.equals("")) {
                        continue;
                    }
                    cnElem.addKeyValuePair(name, value);
                }
            } else {
                request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
                cnElem = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);
            }

            if (create || (mask & Change.FLAGS) != 0) {
                cnElem.addAttribute(MailConstants.A_FLAGS, Flag.toString(flags));
            }
            if (create || (mask & Change.TAGS) != 0) {
                cnElem.addAttribute(MailConstants.A_TAG_NAMES, TagUtil.encodeTags(tags));
                cnElem.addAttribute(MailConstants.A_TAGS, TagUtil.getTagIdString(cn));
            }
            if (create || (mask & Change.FOLDER) != 0) {
                cnElem.addAttribute(MailConstants.A_FOLDER, folderId);
            }
            if (create || (mask & Change.COLOR) != 0) {
                if (color.hasMapping()) {
                    cnElem.addAttribute(MailConstants.A_COLOR, colorByte);
                } else {
                    cnElem.addAttribute(MailConstants.A_RGB, colorStr);
                }
            }
        } finally {
            ombx.lock.release();
        }

        try {
            if (cn.hasAttachment()) {
                ParsedContact pc = new ParsedContact(cn);
                for (Attachment attach : pc.getAttachments()) {
                    String aid = getZMailbox().uploadAttachment(attach.getName(), attach.getContent(), attach.getContentType(),
                            (int) ((5 + attach.getSize() / 25000) * Constants.MILLIS_PER_SECOND));
                    cnElem.addKeyValuePair(attach.getName(), null).addAttribute(MailConstants.A_ATTACHMENT_ID, aid);
                }
            }

            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.Type.CONTACT, null, folderId);
            if (create) {
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.Type.CONTACT, createData.getFirst())) {
                    return true;
                }
                id = createData.getFirst();
            } else if ((mask & Change.FOLDER) != 0) {
                Element moveRequest = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
                moveRequest.addElement(MailConstants.E_ACTION)
                    .addAttribute(MailConstants.A_OPERATION, ItemAction.OP_MOVE).addAttribute(MailConstants.A_ID, id)
                    .addAttribute(MailConstants.A_FOLDER, folderId);
                ombx.sendRequest(moveRequest);
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_CONTACT))
                throw sfe;
            OfflineLog.offline.info("push: remote contact " + id + " has been deleted; skipping");
        } catch (IOException e) {
            throw ServiceException.FAILURE("Unable to sync contact.", e);
        }

        ombx.lock.lock();
        try {
            cn = ombx.getContactById(sContext, id);
            // check to see if the contact was changed while we were pushing the update...
            mask = 0;
            if (flags != cn.getInternalFlagBitmask()) {
                mask |= Change.FLAGS;
            }
            if (!TagUtil.tagsMatch(tags, cn.getTags())) {
                mask |= Change.TAGS;
            }
            if (folderId != cn.getFolderId()) {
                mask |= Change.FOLDER;
            }
            if (!color.equals(cn.getRgbColor())) {
                mask |= Change.COLOR;
            }
            if (date != cn.getDate()) {
                mask |= Change.CONTENT;
            }
            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.Type.CONTACT, mask);
            return (mask == 0);
        } finally {
            ombx.lock.release();
        }
    }

    private boolean syncDocument(int id, TypedIdList tombstones) throws ServiceException {
        if (!OfflineLC.zdesktop_sync_documents.booleanValue() ||
                !ombx.getRemoteServerVersion().isAtLeast(InitialSync.sMinDocumentSyncVersion)) {
            return true;
        }
        MailItem item = null;
        boolean create = false;
        ombx.lock.lock();
        try {
            if (id > ZcsMailbox.FIRST_OFFLINE_ITEM_ID) {
                create = true;
            }
            item = ombx.getItemById(sContext, id, MailItem.Type.UNKNOWN);
        } finally {
            ombx.lock.release();
        }

        String digest = item.getDigest();
        String name = item.getName();
        MailItem.Type type = item.getType();

        RevisionInfo lastRev = null;
        if (ombx.getRemoteServerVersion().isAtLeast(InitialSync.sDocumentSyncHistoryVersion) &&
                !create) {
            List<RevisionInfo> revInfo = checkDocumentSyncConflict(item);
            if (revInfo.size() > 0) {
                //list documents always returns newest first
                lastRev = revInfo.get(0);
            }
        }
        //only upload document if we have a newer revision or modified content
        if (lastRev == null || !(lastRev.getVersion() == item.getVersion() && lastRev.getTimestamp() == item.getDate())) {
            Pair<Integer,Integer> resp = ombx.sendMailItem(item);
            if (create) {
                if (!ombx.renumberItem(sContext, id, type, resp.getFirst()))
                    return true;
                id = resp.getFirst();
                List<Integer> tombstonedDocs = tombstones.getIds(MailItem.Type.DOCUMENT);
                if (tombstonedDocs != null && tombstonedDocs.indexOf(id) > -1) {
                    ombx.removePendingDelete(sContext, id, type);
                    tombstonedDocs.remove(Integer.valueOf(id)); //remove(Object o), not remote(int idx)!!
                    if (tombstonedDocs.isEmpty()) {
                        tombstones.remove(MailItem.Type.DOCUMENT);
                    }
                }
            }
            ombx.setSyncedVersionForMailItem("" + item.getId(), resp.getSecond());
        }
        //set tags
        Element request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION);
        action.addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE);
        action.addAttribute(MailConstants.A_TAG_NAMES, TagUtil.encodeTags(item.getTags()));
        action.addAttribute(MailConstants.A_TAGS, TagUtil.getTagIdString(item));
        action.addAttribute(MailConstants.A_ID, id);
        ombx.sendRequest(request);

        ombx.lock.lock();
        try {
            item = ombx.getItemById(sContext, id, MailItem.Type.UNKNOWN);
            int mask = 0;
            if (!StringUtil.equal(digest, item.getDigest())) {
                mask |= Change.CONTENT;
            }
            if (!StringUtil.equal(name, item.getName())) {
                mask |= Change.NAME;
            }
            ombx.setChangeMask(sContext, id, MailItem.Type.DOCUMENT, mask);
            return (mask == 0);
        } finally {
            ombx.lock.release();
        }
    }

    private List<RevisionInfo> checkDocumentSyncConflict(MailItem item) throws ServiceException {
        int id = item.getId();
        int lastSyncVersion = ombx.getLastSyncedVersionForMailItem(id);
        Element request = new Element.XMLElement(MailConstants.LIST_DOCUMENT_REVISIONS_REQUEST);
        Element wiki = request.addElement(MailConstants.E_DOC);
        wiki.addAttribute(MailConstants.A_ID, id);
        wiki.addAttribute(MailConstants.A_COUNT, 20);
        Element response = ombx.sendRequest(request);
        Iterator<Element> iter = response.elementIterator(MailConstants.E_DOC);
        boolean conflict = false;
        ArrayList<SyncExceptionHandler.Revision> revisions = new ArrayList<SyncExceptionHandler.Revision>();
        List<RevisionInfo> revInfo = new ArrayList<RevisionInfo>();
        while (iter.hasNext()) {
            Element e = iter.next();
            int ver = (int)e.getAttributeLong(MailConstants.A_VERSION);
            if (lastSyncVersion > 0 && ver > lastSyncVersion) {
                conflict = true;
                SyncExceptionHandler.Revision rev = new SyncExceptionHandler.Revision();
                rev.editor = e.getAttribute(MailConstants.A_CREATOR);
                rev.version = (int)e.getAttributeLong(MailConstants.A_VERSION);
                rev.modifiedDate = e.getAttributeLong(MailConstants.A_DATE);
                revisions.add(rev);
            }
            revInfo.add(new RevisionInfo(ver, e.getAttributeLong(MailConstants.A_DATE), (int) e.getAttributeLong(MailConstants.A_FOLDER)));
        }
        if (conflict) {
            SyncExceptionHandler.logDocumentEditConflict(ombx, item, revisions);
        }
        return revInfo;
    }

    private boolean syncMessage(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.MSG_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION)
                .addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE)
                .addAttribute(MailConstants.A_ID, id);

        int flags, folderId;
        String[] tags;
        String digest;
        byte colorByte = 0;
        String colorStr = null;
        Color color;
        boolean create = false;
        boolean upload = false;
        Message msg = null;
        ombx.lock.lock();
        try {
            try {
                msg = ombx.getMessageById(sContext, id);
            } catch (NoSuchItemException x) {
                OfflineLog.offline.debug("push: message %d deleted before push", id);
                return false;
            }
            digest = msg.getDigest();  flags = msg.getFlagBitmask();  tags = msg.getTags();
            color = msg.getRgbColor();    folderId = msg.getFolderId();

            if (color.hasMapping()) {
                colorByte = color.getMappedColor();
            }
            else {
                colorStr = color.toString();
            }
            if (folderId == DesktopMailbox.ID_FOLDER_OUTBOX)
                return false; //don't mind anything left over in Outbox, most likely sending message failed due to server side issues

            int mask = ombx.getChangeMask(sContext, id, MailItem.Type.MESSAGE);
            if ((mask & Change.CONFLICT) != 0) {
                // this is a new message; need to push to the server
                request = new Element.XMLElement(msg.isDraft() ? MailConstants.SAVE_DRAFT_REQUEST : MailConstants.ADD_MSG_REQUEST);
                action = request.addElement(MailConstants.E_MSG);
                if (msg.isDraft() && !msg.getDraftOrigId().isEmpty()) {
                    action.addAttribute(MailConstants.A_REPLY_TYPE, msg.getDraftReplyType()).addAttribute(MailConstants.A_ORIG_ID, msg.getDraftOrigId());
                } else if (!msg.isDraft()) {
                    action.addAttribute(MailConstants.A_DATE, msg.getDate());
                }
                upload = true;
                create = true;
            } else if ((mask & Change.CONTENT) != 0) {
                // for draft message content changes, need to go through the SaveDraft door instead of the MsgAction door
                if (!msg.isDraft()) {
                    throw MailServiceException.IMMUTABLE_OBJECT(id);
                }
                request = new Element.XMLElement(MailConstants.SAVE_DRAFT_REQUEST);
                action = request.addElement(MailConstants.E_MSG).addAttribute(MailConstants.A_ID, id);
                upload = true;
            }
            if (create || (mask & Change.FLAGS | Change.UNREAD) != 0) {
                action.addAttribute(MailConstants.A_FLAGS, Flag.toString(flags));
            }
            if (create || (mask & Change.TAGS) != 0) {
                action.addAttribute(MailConstants.A_TAG_NAMES, TagUtil.encodeTags(msg.getTags()));
                action.addAttribute(MailConstants.A_TAGS, TagUtil.getTagIdString(msg));
            }
            if (create || (mask & Change.FOLDER) != 0) {
                action.addAttribute(MailConstants.A_FOLDER, folderId);
            }
            if (create || (mask & Change.COLOR) != 0) {
                if (color.hasMapping()) {
                    action.addAttribute(MailConstants.A_COLOR, colorByte);
                } else {
                    action.addAttribute(MailConstants.A_RGB, colorStr);
                }
            }
            if (msg.isDraft() && (create || (mask & Change.CONTENT) != 0) && msg.getDraftAutoSendTime() != 0) {
                action.addAttribute(MailConstants.A_AUTO_SEND_TIME, msg.getDraftAutoSendTime());
            }
        } finally {
            ombx.lock.release();
        }

        try {
            if (upload) {
                // upload draft message body to the remote FileUploadServlet, then use the returned attachment id to save draft
                String attachId = uploadMessage(msg);
                action.addAttribute(MailConstants.A_ATTACHMENT_ID, attachId);
            }

            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.Type.MESSAGE, null, folderId);
            if (create) {
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.Type.MESSAGE, createData.getFirst()))
                    return true;
                id = createData.getFirst();
            }
        } catch (ServiceException e) {
            //bug 54080
            //rather than synchronizing whole method on mbox (thereby blocking mailbox while potentially long upload occurs)
            //lets see if the message still exists
            try {
                ombx.getMessageById(sContext, id);
            } catch (NoSuchItemException x) {
                OfflineLog.offline.debug("push: message %d deleted during push", id);
                return false;
            }
            if (e.getCode().equals(ZClientException.UPLOAD_SIZE_LIMIT_EXCEEDED)) {
                OfflineLog.offline.info("push: draft message %d too large to save to remote Drafts folder", id);
                //let it fall through so we clear the dirty bit so we don't try to push it up any more
            } else if (e.getCode().equals(MailServiceException.NO_SUCH_MSG)) {
                OfflineLog.offline.info("push: remote message " + id + " has been deleted; skipping");
            } else if (!StringUtil.equal(digest, msg.getDigest())) {
                //message is still there but the attachment is changed
                OfflineLog.offline.debug("push: message %d is still there but the attachment is removed", id);
                //our intention is to just change lastChangeTime, so that the updated content/attachments is pushed
                ombx.trackChangeModified(msg, Change.NONE);
                return false;
            } else {
                throw e;
            }
        }

        ombx.lock.lock();
        try {
            try {
                msg = ombx.getMessageById(sContext, id);
            } catch (NoSuchItemException x) {
                OfflineLog.offline.debug("push: message %d deleted after push", id);
                return false;
            }
            // check to see if the message was changed while we were pushing the update...
            int mask = 0;
            if (flags != msg.getFlagBitmask()) {
                mask |= Change.FLAGS;
            }
            if (!TagUtil.tagsMatch(tags, msg.getTags())) {
                mask |= Change.TAGS;
            }
            if (folderId != msg.getFolderId()) {
                mask |= Change.FOLDER;
            }
            if (!color.equals(msg.getRgbColor())) {
                mask |= Change.COLOR;
            }
            if (!StringUtil.equal(digest, msg.getDigest())) {
                mask |= Change.CONTENT;
            }
            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.Type.MESSAGE, mask);
            return (mask == 0);
        } finally {
            ombx.lock.release();
        }
    }

    private boolean syncCalendarItem(int id, boolean isAppointment) throws ServiceException {
        int flags, folderId;
        long date;
        String[] tags;
        byte colorByte = 0;
        String colorStr = null;
        Color color;
        int mask;

        Element request = null;
        boolean create = false;
        String name = null;
        String uid = null;
        MailItem.Type type = isAppointment ? MailItem.Type.APPOINTMENT : MailItem.Type.TASK;
        CalendarItem cal = null;
        ombx.lock.lock();
        try {
            cal = ombx.getCalendarItemById(sContext, id);
            name = cal.getSubject();
            date = cal.getDate();
            tags = cal.getTags();
            flags = cal.getFlagBitmask();
            folderId = cal.getFolderId();
            color = cal.getRgbColor();
            uid = cal.getUid();
            mask = ombx.getChangeMask(sContext, id, type);

            if (color.hasMapping()) {
                colorByte = color.getMappedColor();
            }
            else {
                colorStr = color.toString();
            }
            if ((mask & Change.CONFLICT) != 0 || (mask & Change.CONTENT) != 0 || (mask & Change.INVITE) != 0) { // need to push to the server
                request = new Element.XMLElement(isAppointment ? MailConstants.SET_APPOINTMENT_REQUEST : MailConstants.SET_TASK_REQUEST);
                ToXML.encodeCalendarItemSummary(request, new ItemIdFormatter(true), ombx.getOperationContext(), cal, ToXML.NOTIFY_FIELDS, true);
                request = InitialSync.makeSetCalRequest(
                        request.getElement(isAppointment ? MailConstants.E_APPOINTMENT : MailConstants.E_TASK),
                        new LocalInviteMimeLocator(ombx), getZMailbox(), ombx.getOfflineAccount(), isAppointment, true,
                        getTagSync());
                create = true; //content mod is considered same as create since we use SetAppointment for both
            } else {
                request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
                Element action = request.addElement(MailConstants.E_ACTION)
                        .addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE)
                        .addAttribute(MailConstants.A_ID, id);
                if ((mask & Change.TAGS) != 0) {
                    action.addAttribute(MailConstants.A_TAG_NAMES, TagUtil.encodeTags(cal.getTags()));
                    action.addAttribute(MailConstants.A_TAGS, TagUtil.getTagIdString(cal));
                }
                if ((mask & Change.FLAGS) != 0) {
                    action.addAttribute(MailConstants.A_FLAGS, cal.getFlagString());
                }
                if ((mask & Change.FOLDER) != 0) {
                    action.addAttribute(MailConstants.A_FOLDER, folderId);
                }
                if ((mask & Change.COLOR) != 0) {
                    if (color.hasMapping()) {
                        action.addAttribute(MailConstants.A_COLOR, colorByte);
                    } else {
                        action.addAttribute(MailConstants.A_RGB, colorStr);
                    }
                }
            }
        } finally {
            ombx.lock.release();
        }

        try {
            if (create) {
                //Since we are using SetAppointment for both new and existing appointments we always need to sync ids
                Element response = ombx.sendRequest(request);
                int serverItemId = (int)response.getAttributeLong(MailConstants.A_CAL_ID);

                //We are not processing the invIds from the SetAppointment response.
                //Instead, we just let it bounce back as a calendar update from server.
                //mod sequence will always be bounced back in the next sync so we'll set there.
                if (serverItemId != id) { //new item
                    try {
                        CalendarItem calItem = ombx.getCalendarItemById(sContext, serverItemId);
                        OfflineLog.offline.debug("New calendar item %d has been mapped to existing calendar item %d during push", id, serverItemId);
                        boolean uidSame = (calItem.getUid() == null && uid == null) || (calItem.getUid() != null && (calItem.getUid().equals(uid) || (Invite.isOutlookUid(calItem.getUid()) && calItem.getUid().equalsIgnoreCase(uid))));
                        if (!uidSame) {
                            OfflineLog.offline.warn("calendar item %d UID %s differs from server-mapped item %d UID %s", id, uid, calItem.getId(), calItem.getUid());
                            assert(uidSame);
                        } else if (cal.getId() != calItem.getId()) {
                            OfflineLog.offline.warn("Deleting ZD cal item %d with same UID as existing %d",cal.getId(), calItem.getId());
                            ombx.delete(sContext, cal.getId(), cal.getType(), null);
                        }
                    } catch (NoSuchItemException nsie) {
                        if (!ombx.renumberItem(sContext, id, type, serverItemId))
                            return true;
                    }
                }
                id = serverItemId;
            } else {
                pushRequest(request, create, id, type, name, folderId);
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_CONTACT))
                throw sfe;
            OfflineLog.offline.info("push: remote calendar item " + id + " has been deleted; skipping");
        }

        ombx.lock.lock();
        try {
            cal = ombx.getCalendarItemById(sContext, id);
            // check to see if the calendar item was changed while we were pushing the update...
            mask = 0;
            if (flags != cal.getInternalFlagBitmask()) {
                mask |= Change.FLAGS;
            }
            if (!TagUtil.tagsMatch(tags, cal.getTags())) {
                mask |= Change.TAGS;
            }
            if (folderId != cal.getFolderId()) {
                mask |= Change.FOLDER;
            }
            if (!color.equals(cal.getRgbColor())) {
                mask |= Change.COLOR;
            }
            if (date != cal.getDate()) {
                mask |= Change.CONTENT;
            }
            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, type, mask);
            return (mask == 0);
        } finally {
            ombx.lock.release();
        }
    }
}
