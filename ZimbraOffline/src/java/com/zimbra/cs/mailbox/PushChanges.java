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
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
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

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.BigByteBuffer;
import com.zimbra.common.util.Constants;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.zclient.ZClientException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.mime.MimeConstants;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.account.offline.OfflineProvisioning;
import com.zimbra.cs.mailbox.ChangeTrackingMailbox.TracelessContext;
import com.zimbra.cs.mailbox.Contact.Attachment;
import com.zimbra.cs.mailbox.InitialSync.InviteMimeLocator;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
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
import com.zimbra.cs.zclient.ZMailbox;

public class PushChanges {
	
	private static class LocalInviteMimeLocator implements InviteMimeLocator {
		ZcsMailbox ombx;
		
		public LocalInviteMimeLocator(ZcsMailbox ombx) {
			this.ombx = ombx;
		}
		
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
    static final int MESSAGE_CHANGES = Change.MODIFIED_UNREAD | Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS |
                                       Change.MODIFIED_FOLDER | Change.MODIFIED_COLOR | Change.MODIFIED_CONTENT;

    /** The bitmask of all chat changes that we propagate to the server. */
    static final int CHAT_CHANGES = Change.MODIFIED_UNREAD | Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS |
                                    Change.MODIFIED_FOLDER | Change.MODIFIED_COLOR | Change.MODIFIED_CONTENT;

    /** The bitmask of all contact changes that we propagate to the server. */
    static final int CONTACT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER |
                                       Change.MODIFIED_COLOR | Change.MODIFIED_CONTENT;

    /** The bitmask of all folder changes that we propagate to the server. */
    static final int FOLDER_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME |
                                      Change.MODIFIED_COLOR | Change.MODIFIED_URL    | Change.MODIFIED_ACL;

    /** The bitmask of all search folder changes that we propagate to the server. */
    static final int SEARCH_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME |
                                      Change.MODIFIED_COLOR | Change.MODIFIED_QUERY;

    /** The bitmask of all tag changes that we propagate to the server. */
    static final int TAG_CHANGES = Change.MODIFIED_NAME | Change.MODIFIED_COLOR;
    
    /** The bitmask of all appointment changes that we propagate to the server. */
    static final int APPOINTMENT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER |
                                           Change.MODIFIED_COLOR | Change.MODIFIED_CONTENT | Change.MODIFIED_INVITE;
    
    /** The bitmask of all document changes that we propagate to the server. */
    static final int DOCUMENT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER |
                                        Change.MODIFIED_COLOR | Change.MODIFIED_CONTENT | Change.MODIFIED_NAME;

    /** A list of all the "leaf types" (i.e. non-folder types) that we
     *  synchronize with the server. */
    private static final byte[] PUSH_LEAF_TYPES = new byte[] {
        MailItem.TYPE_TAG, 
        MailItem.TYPE_CONTACT, 
        MailItem.TYPE_MESSAGE, 
        MailItem.TYPE_CHAT, 
        MailItem.TYPE_APPOINTMENT,
        MailItem.TYPE_TASK,
        MailItem.TYPE_WIKI,
        MailItem.TYPE_DOCUMENT
    };

    /** The set of all the MailItem types that we synchronize with the server. */
    static final Set<Byte> PUSH_TYPES_SET = new HashSet<Byte>(Arrays.asList(
        MailItem.TYPE_FOLDER, 
        MailItem.TYPE_SEARCHFOLDER,
        MailItem.TYPE_TAG, 
        MailItem.TYPE_CONTACT, 
        MailItem.TYPE_MESSAGE, 
        MailItem.TYPE_CHAT, 
        MailItem.TYPE_APPOINTMENT,
        MailItem.TYPE_TASK,
        MailItem.TYPE_WIKI,
        MailItem.TYPE_DOCUMENT
    ));


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


    public static boolean sync(ZcsMailbox ombx, boolean isOnRequest) throws ServiceException {
        return new PushChanges(ombx).sync(isOnRequest);
    }

    public static boolean syncFolder(ZcsMailbox ombx, int id) throws ServiceException {
        return new PushChanges(ombx).syncFolder(id);
    }
    
    private boolean sync(boolean isOnRequest) throws ServiceException {
        int limit;
        TypedIdList changes, tombstones;
        // do simple change batch push first
        List<Pair<Integer, Integer>> simpleReadChanges = null; //list of Pair<itemId,modSequence> for items marked read locally
        List<Pair<Integer, Integer>> simpleUnreadChanges = null; //list of Pair<itemId,modSequence> for items marked unread locally
        Map<Integer, List<Pair<Integer, Integer>>> simpleFolderMoveChanges = null; //list of Pair<itemId,modSequence> for items locally moved, sorted by folderId in map

        synchronized (ombx) {
            limit = ombx.getLastChangeID();
            tombstones = ombx.getTombstones(0);
            changes = ombx.getLocalChanges(sContext);
            if (!changes.isEmpty()) {
            	simpleReadChanges = ombx.getSimpleUnreadChanges(sContext, false);
            	simpleUnreadChanges = ombx.getSimpleUnreadChanges(sContext, true);
            	simpleFolderMoveChanges = ombx.getFolderMoveChanges(sContext);
            }
        }

        OfflineSyncManager.getInstance().continueOK();
        
        OfflineLog.offline.debug("starting change push");

        boolean hasDeletes = !tombstones.isEmpty();

        // because tags reuse IDs, we need to do tag deletes before any other changes (especially tag creates)
        List<Integer> tagDeletes = tombstones.getIds(MailItem.TYPE_TAG);
        if (tagDeletes != null && !tagDeletes.isEmpty()) {
            Element request = new Element.XMLElement(MailConstants.TAG_ACTION_REQUEST);
            request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_HARD_DELETE).addAttribute(MailConstants.A_ID, concatenateIds(tagDeletes));
            ombx.sendRequest(request);
            OfflineLog.offline.debug("push: pushed tag deletes: " + tagDeletes);

            tombstones.remove(MailItem.TYPE_TAG);
        }

        // do folder ops top-down so that we don't get dinged when folders switch places
        if (!changes.isEmpty()) {
            if (changes.getIds(MailItem.TYPE_FOLDER) != null || changes.getIds(MailItem.TYPE_SEARCHFOLDER) != null) {
                for (Folder folder : ombx.getFolderById(sContext, Mailbox.ID_FOLDER_ROOT).getSubfolderHierarchy()) {
                    if (changes.remove(folder.getType(), folder.getId())) {
                        switch (folder.getType()) {
                            case MailItem.TYPE_SEARCHFOLDER:  syncSearchFolder(folder.getId());  break;
                            case MailItem.TYPE_FOLDER:        syncFolder(folder.getId());        break;
                        }
                    }
                }
                changes.remove(MailItem.TYPE_FOLDER);  changes.remove(MailItem.TYPE_SEARCHFOLDER);
            }
        }

        // make sure that tags are synced before subsequent item updates
        List<Integer> changedTags = changes.getIds(MailItem.TYPE_TAG);
        if (changedTags != null) {
            for (int id : changedTags)
                syncTag(id);
            changes.remove(MailItem.TYPE_TAG);
        }
        
        // Do simple change batch push first
        Set<Integer> batched = new HashSet<Integer>();
        if (simpleReadChanges != null && simpleReadChanges.size() > 0) {
            OfflineSyncManager.getInstance().continueOK();
        	pushSimpleChanges(simpleReadChanges, Change.MODIFIED_UNREAD, false, 0, batched);
        }
        
        if (simpleUnreadChanges != null && simpleUnreadChanges.size() > 0) {
            OfflineSyncManager.getInstance().continueOK();
        	pushSimpleChanges(simpleUnreadChanges, Change.MODIFIED_UNREAD, true, 0, batched);
        }
        
        if (simpleFolderMoveChanges != null && simpleFolderMoveChanges.size() > 0) {
        	Set<Integer> folders = simpleFolderMoveChanges.keySet();
	        for (int folderId : folders) {
	            OfflineSyncManager.getInstance().continueOK();
	        	pushSimpleChanges(simpleFolderMoveChanges.get(folderId), Change.MODIFIED_FOLDER, false, folderId, batched);
	        }
        }

        // modifies must come after folder and tag creates so that move/tag ops can succeed
        if (!changes.isEmpty()) {
            for (byte type : PUSH_LEAF_TYPES) {
                List<Integer> ids = changes.getIds(type);
                if (ids == null)
                    continue;
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
	                        case MailItem.TYPE_TAG:         syncTag(id);          break;
	                        case MailItem.TYPE_CONTACT:     syncContact(id);      break;
	                        case MailItem.TYPE_MESSAGE:     syncMessage(id);      break;
	                        case MailItem.TYPE_APPOINTMENT: syncCalendarItem(id, true); break;
	                        case MailItem.TYPE_TASK:        syncCalendarItem(id, false); break;
	                        case MailItem.TYPE_WIKI:
	                        case MailItem.TYPE_DOCUMENT:    syncDocument(id);     break;
	                    }
                	} catch (Exception x) {
                		SyncExceptionHandler.checkRecoverableException("PushChanges.sync", x);
                		SyncExceptionHandler.pushItemFailed(ombx, id, x);
                		ombx.setChangeMask(sContext, id, type, 0); //clear change mask since we failed to push up an item due to unrecoverable reasons
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
                    ombx.delete(sContext, id, MailItem.TYPE_MESSAGE);
                    OfflineLog.offline.debug("push: deleted pending draft (" + id + ')');
                } catch (ServiceException x) {
                	if ((x instanceof ZClientException || x instanceof SoapFaultException) && !x.isReceiversFault() &&
                			!x.getCode().equals(ZClientException.IO_ERROR) && !x.getCode().equals(ZClientException.UPLOAD_FAILED)) { //supposedly this is client fault
                		OfflineLog.offline.debug("push: failed to send mail (" + id + "): " + msg.getSubject(), x);
                		
                		ombx.move(sContext, id, MailItem.TYPE_MESSAGE, Mailbox.ID_FOLDER_DRAFTS); //move message back to drafts folder;
                		
                		//we need to tell user of the failure
                		try {
                		    MimeMessage mm = new Mime.FixedMimeMessage(JMSession.getSession());
                			mm.setSentDate(new Date());
                			mm.setFrom(new InternetAddress("donotreply@host.local", "Desktop Notifier"));
	                		mm.setRecipient(RecipientType.TO, new InternetAddress(ombx.getAccount().getName()));
	                		
	                		String sentDate = new SimpleDateFormat("MMMMM d, yyyy").format(msg.getDate());
	                		String sentTime = new SimpleDateFormat("h:mm a").format(msg.getDate());
	                		
	                		String text = "Your message \"" + msg.getSubject() + "\" sent on " + sentDate + " at " + sentTime + " to \"" + msg.getRecipients() + "\" can't be delivered.  None of the recipients will receive the message.  It has been returned to Drafts folder for your review.\n";
	                		String subject = null;
	                		if (x.getCode().equals(ZClientException.UPLOAD_SIZE_LIMIT_EXCEEDED))
	                			subject = "message size exceeds server limit";
	                		else if (x.getCode().equals(MailServiceException.SEND_ABORTED_ADDRESS_FAILURE))
	                			subject = x.getMessage();
	                		else {
	                			text += "\n----------------------------------------------------------------------------\n\n" +
	                				    SyncExceptionHandler.sendMailFailed(ombx, id, x);
	                			subject = x.getCode();
	                		}
	                		mm.setSubject("Delivery Failure Notification: " + subject);
	                		mm.setText(text);

	                		mm.saveChanges(); //must call this to update the headers
	                		ParsedMessage pm = new ParsedMessage(mm, true);
	                		ombx.addMessage(sContext, pm, DesktopMailbox.ID_FOLDER_INBOX, true, Flag.BITMASK_UNREAD, null);
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
    private Pair<Integer,Integer> pushRequest(Element request, boolean create, int id, byte type, String name, int folderId)
    throws ServiceException {
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
            byte conflictType = Sync.typeForElementName(conflict.getName());

            // rename the conflicting item out of the way
            Element rename = null;
            switch (conflictType) {
                case MailItem.TYPE_SEARCHFOLDER:
                case MailItem.TYPE_FOLDER:  rename = new Element.XMLElement(MailConstants.FOLDER_ACTION_REQUEST);  break;

                case MailItem.TYPE_TAG:     rename = new Element.XMLElement(MailConstants.TAG_ACTION_REQUEST);  break;

                case MailItem.TYPE_DOCUMENT:
                case MailItem.TYPE_WIKI:    rename = new Element.XMLElement(MailConstants.WIKI_ACTION_REQUEST);  break;

                default:                    rename = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);  break;
            }
            rename.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_RENAME).addAttribute(MailConstants.A_ID, conflictId)
                                                   .addAttribute(MailConstants.A_FOLDER, folderId).addAttribute(MailConstants.A_NAME, conflictRename);
            ombx.sendRequest(rename);
            OfflineLog.offline.info("push: renamed remote " + MailItem.getNameForType(conflictType) + " (" + conflictId + ") to " + folderId + '/' + conflictRename);

            // retry the original create/update
            return sendRequest(request, create, id, type, name);
        } catch (SoapFaultException sfe) {
            // remote server doesn't support GetItem, so all we can do is to rename the local item and retry
            boolean unsupported = sfe.getCode().equals(ServiceException.UNKNOWN_DOCUMENT);
            OfflineLog.offline.info("push: could not resolve naming conflict with remote item; will rename locally", unsupported ? null : sfe);
        }

        ombx.rename(null, id, type, conflictRename, folderId);
        OfflineLog.offline.info("push: renamed local " + MailItem.getNameForType(type) + " (" + id + ") to " + folderId + '/' + conflictRename);
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
    private Pair<Integer,Integer> sendRequest(Element request, boolean create, int id, byte type, String name) throws ServiceException {
        // try to create/update the item as requested
        Element response = ombx.sendRequest(request);
        if (create) {
            int newId = (int) response.getElement(Sync.elementNameForType(type)).getAttributeLong(MailConstants.A_ID);
            int newRevision = (int) response.getElement(Sync.elementNameForType(type)).getAttributeLong(MailConstants.A_REVISION, -1);
            OfflineLog.offline.debug("push: created " + MailItem.getNameForType(type) + " (" + newId + ") from local (" + id + (name == null ? ")" : "): " + name));
            return new Pair<Integer,Integer>(newId, newRevision);
        } else {
            OfflineLog.offline.debug("push: updated " + MailItem.getNameForType(type) + " (" + id + (name == null ? ")" : "): " + name));
            return null;
        }
    }

    private boolean syncSearchFolder(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.FOLDER_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);

        int flags, parentId;
        byte color;
        String name, query, searchTypes, sort;
        boolean create = false;
        synchronized (ombx) {
            SearchFolder search = ombx.getSearchFolderById(sContext, id);
            
            name = search.getName();    flags = search.getInternalFlagBitmask();
            color = search.getColor();  parentId = search.getFolderId();  
            query = search.getQuery();  searchTypes = search.getReturnTypes();  sort = search.getSortField();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_SEARCHFOLDER);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new search folder; need to push to the server
                request = new Element.XMLElement(MailConstants.CREATE_SEARCH_FOLDER_REQUEST);
                action = request.addElement(MailConstants.E_SEARCH);
                create = true;
            }
            if (create || (mask & Change.MODIFIED_FLAGS) != 0)
                action.addAttribute(MailConstants.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailConstants.A_FOLDER, parentId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailConstants.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_NAME) != 0)
                action.addAttribute(MailConstants.A_NAME, name);
            if (create || (mask & Change.MODIFIED_QUERY) != 0)
                action.addAttribute(MailConstants.A_QUERY, query).addAttribute(MailConstants.A_SEARCH_TYPES, searchTypes).addAttribute(MailConstants.A_SORT_FIELD, sort);
        }

        try {
            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.TYPE_SEARCHFOLDER, name, parentId);
            if (create) {
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.TYPE_SEARCHFOLDER, createData.getFirst()))
                	return true;
                id = createData.getFirst();
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_FOLDER))
                throw sfe;
            OfflineLog.offline.info("push: remote search folder " + id + " has been deleted; skipping");
        }

        synchronized (ombx) {
            SearchFolder search = ombx.getSearchFolderById(sContext, id);
            // check to see if the search was changed while we were pushing the update...
            int mask = 0;
            if (flags != search.getInternalFlagBitmask())  mask |= Change.MODIFIED_FLAGS;
            if (parentId != search.getFolderId())          mask |= Change.MODIFIED_NAME;
            if (color != search.getColor())                mask |= Change.MODIFIED_COLOR;
            if (!name.equals(search.getName()))            mask |= Change.MODIFIED_NAME;
            if (!query.equals(search.getQuery()))              mask |= Change.MODIFIED_QUERY;
            if (!searchTypes.equals(search.getReturnTypes()))  mask |= Change.MODIFIED_QUERY;
            if (!sort.equals(search.getSortField()))           mask |= Change.MODIFIED_QUERY;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.TYPE_SEARCHFOLDER, mask);
            return (mask == 0);
        }
    }

    private boolean syncFolder(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.FOLDER_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);

        int flags, parentId;
        byte color;
        String name, url;
        boolean create = false;
        Folder folder = null;
        synchronized (ombx) {
            folder = ombx.getFolderById(sContext, id);
            name = folder.getName();  parentId = folder.getFolderId();  flags = folder.getInternalFlagBitmask();
            url = folder.getUrl();    color = folder.getColor();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_FOLDER);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new folder; need to push to the server
                request = new Element.XMLElement(MailConstants.CREATE_FOLDER_REQUEST);
                action = request.addElement(MailConstants.E_FOLDER).addAttribute(MailConstants.A_DEFAULT_VIEW, MailItem.getNameForType(folder.getDefaultView()));
                create = true;
            }
            if (create || (mask & Change.MODIFIED_FLAGS) != 0)
                action.addAttribute(MailConstants.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailConstants.A_FOLDER, parentId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailConstants.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_NAME) != 0)
                action.addAttribute(MailConstants.A_NAME, name);
            if (create || (mask & Change.MODIFIED_URL) != 0)
                action.addAttribute(MailConstants.A_URL, url);
            // FIXME: does not support ACL sync at all...
        }

        try {
            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.TYPE_FOLDER, name, parentId);
            if (create) {
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.TYPE_FOLDER, createData.getFirst()))
                	return true;
                id = createData.getFirst();
            }
        } catch (SoapFaultException sfe) {
            if (folder.getUrl() != null) {
                OfflineErrorUtil.reportError(ombx, folder.getId(), "failed to sync rss url ["+folder.getUrl()+"]", sfe);
                return true;
            } else if (!sfe.getCode().equals(MailServiceException.NO_SUCH_FOLDER)) {
                throw sfe;
            }
            OfflineLog.offline.info("push: remote folder " + id + " has been deleted; skipping");
        }

        synchronized (ombx) {
            folder = ombx.getFolderById(sContext, id);
            // check to see if the folder was changed while we were pushing the update...
            int mask = 0;
            if (flags != folder.getInternalFlagBitmask())  mask |= Change.MODIFIED_FLAGS;
            if (parentId != folder.getFolderId())          mask |= Change.MODIFIED_NAME;
            if (color != folder.getColor())                mask |= Change.MODIFIED_COLOR;
            if (!name.equals(folder.getName()))            mask |= Change.MODIFIED_NAME;
            if (!url.equals(folder.getUrl()))              mask |= Change.MODIFIED_URL;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.TYPE_FOLDER, mask);
            return (mask == 0);
        }
    }

    private boolean syncTag(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.TAG_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);

        byte color;
        String name;
        boolean create = false;
        synchronized (ombx) {
            Tag tag = ombx.getTagById(sContext, id);
            color = tag.getColor();  name = tag.getName();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_TAG);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new tag; need to push to the server
                request = new Element.XMLElement(MailConstants.CREATE_TAG_REQUEST);
                action = request.addElement(MailConstants.E_TAG);
                create = true;
            }
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailConstants.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_NAME) != 0)
                action.addAttribute(MailConstants.A_NAME, name);
        }

        try {
            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.TYPE_TAG, name, Mailbox.ID_FOLDER_TAGS);
            if (create) {
                int newId = createData.getFirst();
                // first, deal with more headaches caused by reusing tag ids
                if (id != createData.getFirst() && DeltaSync.getTag(ombx, newId) != null) {
                    int renumber = DeltaSync.getAvailableTagId(ombx);
                    if (renumber < 0)
                        ombx.delete(sContext, newId, MailItem.TYPE_TAG);
                    else
                        ombx.renumberItem(sContext, newId, MailItem.TYPE_TAG, renumber);
                }
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.TYPE_TAG, newId))
                	return true;
                id = newId;
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_TAG))
                throw sfe;
            OfflineLog.offline.info("push: remote tag " + id + " has been deleted; skipping");
        }

        synchronized (ombx) {
            Tag tag = ombx.getTagById(sContext, id);
            // check to see if the tag was changed while we were pushing the update...
            int mask = 0;
            if (color != tag.getColor())      mask |= Change.MODIFIED_COLOR;
            if (!name.equals(tag.getName()))  mask |= Change.MODIFIED_NAME;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.TYPE_TAG, mask);
            return (mask == 0);
        }
    }

    private void pushSimpleChanges(List<Pair<Integer, Integer>> changes, int changeMask, boolean isUnread, int folderId, Set<Integer> doneSet) throws ServiceException {
    	assert (changes != null && changes.size() > 0);
        assert (changeMask == Change.MODIFIED_UNREAD || changeMask == Change.MODIFIED_FOLDER); //only these two are considered simple
        
        Element request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION);
        
        switch (changeMask) {
        	case Change.MODIFIED_FOLDER:
        		action.addAttribute(MailConstants.A_OPERATION, ItemAction.OP_MOVE);
        		action.addAttribute(MailConstants.A_FOLDER, folderId);
        		break;
        	case Change.MODIFIED_UNREAD:
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
        
        synchronized (ombx) {
        	Map<Integer, Integer> refresh = ombx.getItemModSequences(sContext, ids);
            for (Pair<Integer, Integer> pair : changes) {
            	int id = pair.getFirst();
            	Integer newModSequence = refresh.get(id);
            	if (newModSequence != null) {
            		if (newModSequence.intValue() == pair.getSecond()) {
            			//because we know the item hasn't changed since we last checked,
            			//and we know it was a simple change, we can simply clear the mask.
    		            ombx.setChangeMask(sContext, id, MailItem.TYPE_UNKNOWN, 0);
    		            doneSet.add(id);
            		} else {
            			OfflineLog.offline.debug("push: item " + id + " further modified from local during push");
            		}
            	} else {
            		OfflineLog.offline.debug("push: item " + id + " deleted from local during push");
            	}
            }
        }
    }
    
    private boolean syncContact(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.MODIFY_CONTACT_REQUEST).addAttribute(MailConstants.A_REPLACE, "1");
        Element cnElem = request.addElement(MailConstants.E_CONTACT).addAttribute(MailConstants.A_ID, id);
    	
        int flags, folderId;
        long date, tags;
        byte color;
        boolean create = false;
        Contact cn = null;
        synchronized (ombx) {
            cn = ombx.getContactById(sContext, id);
            date = cn.getDate();    flags = cn.getFlagBitmask();  tags = cn.getTagBitmask();
            color = cn.getColor();  folderId = cn.getFolderId();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_CONTACT);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new contact; need to push to the server
                request = new Element.XMLElement(MailConstants.CREATE_CONTACT_REQUEST);
                cnElem = request.addElement(MailConstants.E_CONTACT);
                create = true;
            }
            
            if (create || (mask & Change.MODIFIED_CONTENT) != 0) {
                for (Map.Entry<String, String> field : cn.getFields().entrySet()) {
                    String name = field.getKey(), value = field.getValue();
                    if (name == null || name.trim().equals("") || value == null || value.equals(""))
                        continue;
                    cnElem.addKeyValuePair(name, value);
                }
            } else {
            	request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
            	cnElem = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);
            }
            
            if (create || (mask & Change.MODIFIED_FLAGS) != 0)
            	cnElem.addAttribute(MailConstants.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_TAGS) != 0)
            	cnElem.addAttribute(MailConstants.A_TAGS, cn.getTagString());
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
            	cnElem.addAttribute(MailConstants.A_FOLDER, folderId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
            	cnElem.addAttribute(MailConstants.A_COLOR, color);
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
        	
            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.TYPE_CONTACT, null, folderId);
            if (create) {
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.TYPE_CONTACT, createData.getFirst()))
                	return true;
                id = createData.getFirst();
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_CONTACT))
                throw sfe;
            OfflineLog.offline.info("push: remote contact " + id + " has been deleted; skipping");
        } catch (IOException e) {
            throw ServiceException.FAILURE("Unable to sync contact.", e);
        }

        synchronized (ombx) {
            cn = ombx.getContactById(sContext, id);
            // check to see if the contact was changed while we were pushing the update...
            int mask = 0;
            if (flags != cn.getInternalFlagBitmask())  mask |= Change.MODIFIED_FLAGS;
            if (tags != cn.getTagBitmask())            mask |= Change.MODIFIED_TAGS;
            if (folderId != cn.getFolderId())          mask |= Change.MODIFIED_FOLDER;
            if (color != cn.getColor())                mask |= Change.MODIFIED_COLOR;
            if (date != cn.getDate())                  mask |= Change.MODIFIED_CONTENT;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.TYPE_CONTACT, mask);
            return (mask == 0);
        }
    }

    private boolean syncWikiItem(WikiItem item, boolean create) throws ServiceException {
    	int id = item.getId();
        Element request = new Element.XMLElement(MailConstants.SAVE_WIKI_REQUEST);
        Element w = request.addElement(MailConstants.E_WIKIWORD);
        w.addAttribute(MailConstants.A_NAME, item.getName());
        if (!create) {
        	w.addAttribute(MailConstants.A_ID, id);
        	w.addAttribute(MailConstants.A_VERSION, ombx.getLastSyncedVersionForMailItem(id));
        }
        w.addAttribute(MailConstants.A_FOLDER, item.getFolderId());
        try {
            w.setText(new String(((WikiItem)item).getContent(), "UTF-8"));
        } catch (IOException e) {}
        Element response = null;
        boolean retry = false;
        while (response == null) {
            try {
            	response = ombx.sendRequest(request);
            } catch (SoapFaultException e) {
            	if (e.getCode().equals(MailServiceException.ALREADY_EXISTS) ||
            			e.getCode().equals(MailServiceException.MODIFY_CONFLICT)) {
            		String iid = e.getArgumentValue("id");
            		String v = e.getArgumentValue("ver");
            		w.addAttribute(MailConstants.A_ID, iid);
            		w.addAttribute(MailConstants.A_VERSION, v);
            		if (!retry) {
            			response = null;
            	        ArrayList<SyncExceptionHandler.Revision> revisions = new ArrayList<SyncExceptionHandler.Revision>();
            	        int firstV = ombx.getLastSyncedVersionForMailItem(id);
            	        int lastV = Integer.parseInt(v);
            	        for (int i = firstV+1; i <= lastV; i++) {
                    		SyncExceptionHandler.Revision rev = new SyncExceptionHandler.Revision();
                    		rev.editor = "";
                    		rev.version = i;
                    		rev.modifiedDate = 0;
            	        	revisions.add(rev);
            	        }
                    	SyncExceptionHandler.logDocumentEditConflict(ombx, item, revisions);
            		}
            		retry = true;
            	}
            }
        }
		w = response.getElement(MailConstants.E_WIKIWORD);
		int newid = (int)w.getAttributeLong(MailConstants.A_ID);
		int ver = (int)w.getAttributeLong(MailConstants.A_VERSION);
		if (create) {
            if (!ombx.renumberItem(sContext, id, MailItem.TYPE_WIKI, newid))
            	return true;
		}
		ombx.setSyncedVersionForMailItem("" + id, ver);
		return true;
    }
    
    private boolean syncDocument(int id) throws ServiceException {
        if (!OfflineLC.zdesktop_sync_documents.booleanValue() ||
        		!ombx.getRemoteServerVersion().isAtLeast(InitialSync.sMinDocumentSyncVersion)) {
        	return true;
        }
    	MailItem item = null;
    	boolean create = false;
    	synchronized (ombx) {
            if (id > ZcsMailbox.FIRST_OFFLINE_ITEM_ID) {
            	create = true;
            }
        	item = ombx.getItemById(sContext, id, MailItem.TYPE_UNKNOWN);
    	}

    	String digest = item.getDigest();
    	String name = item.getName();
    	byte type = item.getType();
    	
    	if (!ombx.getRemoteServerVersion().isAtLeast(InitialSync.sDocumentSyncHistoryVersion) &&
    			type == MailItem.TYPE_WIKI) {
    		syncWikiItem((WikiItem)item, create);
    	} else {
    		if (ombx.getRemoteServerVersion().isAtLeast(InitialSync.sDocumentSyncHistoryVersion) && 
    				!create)
    			checkDocumentSyncConflict(item);
    		Pair<Integer,Integer> resp = ombx.sendMailItem(item);
            if (create) {
                if (!ombx.renumberItem(sContext, id, type, resp.getFirst()))
                	return true;
    		}
    		ombx.setSyncedVersionForMailItem("" + item.getId(), resp.getSecond());
    	}
    	
        synchronized (ombx) {
        	item = ombx.getItemById(sContext, id, MailItem.TYPE_UNKNOWN);
            int mask = 0;
            if (!StringUtil.equal(digest, item.getDigest()))  mask |= Change.MODIFIED_CONTENT;
            if (!StringUtil.equal(name, item.getName()))      mask |= Change.MODIFIED_NAME;
            ombx.setChangeMask(sContext, id, MailItem.TYPE_DOCUMENT, mask);
            return (mask == 0);
        }
    }
    
    private void checkDocumentSyncConflict(MailItem item) throws ServiceException {
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
        while (iter.hasNext()) {
        	Element e = iter.next();
        	int ver = (int)e.getAttributeLong(MailConstants.A_VERSION);
        	if (lastSyncVersion > 0 && ver > lastSyncVersion) {
        		conflict = true;
        		SyncExceptionHandler.Revision rev = new SyncExceptionHandler.Revision();
        		rev.editor = e.getAttribute(MailConstants.A_CREATOR);
        		rev.version = (int)e.getAttributeLong(MailConstants.A_VERSION);
        		rev.modifiedDate = e.getAttributeLong(MailConstants.A_MODIFIED_DATE);
        		revisions.add(rev);
        	}
        }
        if (conflict) {
        	SyncExceptionHandler.logDocumentEditConflict(ombx, item, revisions);
        }
    }
    
    private boolean syncMessage(int id) throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.MSG_ACTION_REQUEST);
        Element action = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);

        int flags, folderId;
        long tags;
        String digest;
        byte color;
        boolean create = false;
        boolean upload = false;
        Message msg = null;
        synchronized (ombx) {
        	try {
        		msg = ombx.getMessageById(sContext, id);
        	} catch (NoSuchItemException x) {
        		OfflineLog.offline.debug("push: message %d deleted before push", id);
        		return false;
        	}
            digest = msg.getDigest();  flags = msg.getFlagBitmask();  tags = msg.getTagBitmask();
            color = msg.getColor();    folderId = msg.getFolderId();
            
        	if (folderId == DesktopMailbox.ID_FOLDER_OUTBOX)
        		return false; //don't mind anything left over in Outbox, most likely sending message failed due to server side issues

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_MESSAGE);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new message; need to push to the server
                request = new Element.XMLElement(msg.isDraft() ? MailConstants.SAVE_DRAFT_REQUEST : MailConstants.ADD_MSG_REQUEST);
                action = request.addElement(MailConstants.E_MSG);
                if (msg.isDraft() && !msg.getDraftOrigId().equals(""))
                    action.addAttribute(MailConstants.A_REPLY_TYPE, msg.getDraftReplyType()).addAttribute(MailConstants.A_ORIG_ID, msg.getDraftOrigId());
                else if (!msg.isDraft())
                    action.addAttribute(MailConstants.A_DATE, msg.getDate());
                upload = true;
                create = true;
            } else if ((mask & Change.MODIFIED_CONTENT) != 0) {            	
                // for draft message content changes, need to go through the SaveDraft door instead of the MsgAction door
                if (!msg.isDraft())
                    throw MailServiceException.IMMUTABLE_OBJECT(id);
                request = new Element.XMLElement(MailConstants.SAVE_DRAFT_REQUEST);
                action = request.addElement(MailConstants.E_MSG).addAttribute(MailConstants.A_ID, id);
                upload = true;
            }
            if (create || (mask & Change.MODIFIED_FLAGS | Change.MODIFIED_UNREAD) != 0)
                action.addAttribute(MailConstants.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_TAGS) != 0)
                action.addAttribute(MailConstants.A_TAGS, msg.getTagString());
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailConstants.A_FOLDER, folderId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailConstants.A_COLOR, color);
            if (msg.isDraft() && (create || (mask & Change.MODIFIED_METADATA) != 0) && msg.getDraftAutoSendTime() != 0)
                action.addAttribute(MailConstants.A_AUTO_SEND_TIME, msg.getDraftAutoSendTime());
        }

        try {
            if (upload) {
                // upload draft message body to the remote FileUploadServlet, then use the returned attachment id to save draft
            	String attachId = uploadMessage(msg);
                action.addAttribute(MailConstants.A_ATTACHMENT_ID, attachId);
            }
        	
            Pair<Integer,Integer> createData = pushRequest(request, create, id, MailItem.TYPE_MESSAGE, null, folderId);
            if (create) {
                // make sure the old item matches the new item...
                if (!ombx.renumberItem(sContext, id, MailItem.TYPE_MESSAGE, createData.getFirst()))
                	return true;
                id = createData.getFirst();
            }
        } catch (ZClientException x) {
        	if (!x.getCode().equals(ZClientException.UPLOAD_SIZE_LIMIT_EXCEEDED))
        		throw x;
        	OfflineLog.offline.info("push: draft message %d too large to save to remote Drafts folder", id);
        	//let it fall through so we clear the dirty bit so we don't try to push it up any more
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_MSG))
                throw sfe;
            OfflineLog.offline.info("push: remote message " + id + " has been deleted; skipping");
        }

        synchronized (ombx) {
        	try {
        		msg = ombx.getMessageById(sContext, id);
        	} catch (NoSuchItemException x) {
        		OfflineLog.offline.debug("push: message %d deleted after push", id);
        		return false;
        	}
            // check to see if the message was changed while we were pushing the update...
            int mask = 0;
            if (flags != msg.getFlagBitmask())    mask |= Change.MODIFIED_FLAGS;
            if (tags != msg.getTagBitmask())      mask |= Change.MODIFIED_TAGS;
            if (folderId != msg.getFolderId())    mask |= Change.MODIFIED_FOLDER;
            if (color != msg.getColor())          mask |= Change.MODIFIED_COLOR;
            if (!StringUtil.equal(digest, msg.getDigest()))  mask |= Change.MODIFIED_CONTENT;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.TYPE_MESSAGE, mask);
            return (mask == 0);
        }
    }
    
    private boolean syncCalendarItem(int id, boolean isAppointment) throws ServiceException {

        int flags, folderId;
        long date, tags;
        byte color;
        int mask;
        
        Element request = null;
        boolean create = false;
        String name = null;
        
        byte type = isAppointment ? MailItem.TYPE_APPOINTMENT : MailItem.TYPE_TASK;
        
        synchronized (ombx) {
            CalendarItem cal = ombx.getCalendarItemById(sContext, id);
            name = cal.getSubject();
            date = cal.getDate();
            tags = cal.getTagBitmask();
            flags = cal.getFlagBitmask();
            folderId = cal.getFolderId();
            color = cal.getColor();
            mask = ombx.getChangeMask(sContext, id, type);

	        if ((mask & Change.MODIFIED_CONFLICT) != 0 || (mask & Change.MODIFIED_CONTENT) != 0 || (mask & Change.MODIFIED_INVITE) != 0) { // need to push to the server
	        	request = new Element.XMLElement(isAppointment ? MailConstants.SET_APPOINTMENT_REQUEST : MailConstants.SET_TASK_REQUEST);
	            ToXML.encodeCalendarItemSummary(request, new ItemIdFormatter(true), ombx.getOperationContext(), cal, ToXML.NOTIFY_FIELDS, true);
	            request = InitialSync.makeSetCalRequest(request.getElement(isAppointment ? MailConstants.E_APPOINTMENT : MailConstants.E_TASK), new LocalInviteMimeLocator(ombx), getZMailbox(), ombx.getAccount(), isAppointment);
	        	create = true; //content mod is considered same as create since we use SetAppointment for both
	        } else {
	        	request = new Element.XMLElement(MailConstants.ITEM_ACTION_REQUEST);
	        	Element action = request.addElement(MailConstants.E_ACTION).addAttribute(MailConstants.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailConstants.A_ID, id);
		        if ((mask & Change.MODIFIED_TAGS) != 0)
		        	action.addAttribute(MailConstants.A_TAGS, cal.getTagString());
		        if ((mask & Change.MODIFIED_FLAGS) != 0)
		        	action.addAttribute(MailConstants.A_FLAGS, cal.getFlagString());
		        if ((mask & Change.MODIFIED_FOLDER) != 0)
		        	action.addAttribute(MailConstants.A_FOLDER, folderId);
		        if ((mask & Change.MODIFIED_COLOR) != 0)
		        	action.addAttribute(MailConstants.A_COLOR, color);
	        }
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
					if (!ombx.renumberItem(sContext, id, type, serverItemId))
						return true;
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

        synchronized (ombx) {
            CalendarItem cal = ombx.getCalendarItemById(sContext, id);
            // check to see if the calendar item was changed while we were pushing the update...
            mask = 0;
            if (flags != cal.getInternalFlagBitmask())  mask |= Change.MODIFIED_FLAGS;
            if (tags != cal.getTagBitmask())            mask |= Change.MODIFIED_TAGS;
            if (folderId != cal.getFolderId())          mask |= Change.MODIFIED_FOLDER;
            if (color != cal.getColor())                mask |= Change.MODIFIED_COLOR;
            if (date != cal.getDate())                  mask |= Change.MODIFIED_CONTENT;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, type, mask);
            return (mask == 0);
        }
    }
}
