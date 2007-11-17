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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.mail.MessagingException;

import org.apache.commons.httpclient.Header;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Pair;
import com.zimbra.common.util.StringUtil;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.soap.ZimbraNamespace;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.offline.OfflineAccount;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.mailbox.calendar.IcalXmlStrMap;
import com.zimbra.cs.mailbox.calendar.ZAttendee;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.mime.ParsedMessage;
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
import com.zimbra.cs.redolog.op.SaveChat;
import com.zimbra.cs.redolog.op.SaveDraft;
import com.zimbra.cs.service.ContentServlet;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.formatter.SyncFormatter;
import com.zimbra.cs.service.mail.FolderAction;
import com.zimbra.cs.service.mail.SetCalendarItem;
import com.zimbra.cs.service.mail.Sync;
import com.zimbra.cs.service.mail.SetCalendarItem.SetCalendarItemParseResult;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.store.Volume;
import com.zimbra.cs.util.AccountUtil;
import com.zimbra.soap.ZimbraSoapContext;

public class InitialSync {

	public static interface InviteMimeLocator {
		public byte[] getInviteMime(int calendarItemId, int inviteId) throws ServiceException;
	}
	
	private static class RemoteInviteMimeLocator implements InviteMimeLocator {
		OfflineMailbox ombx;
		
		public RemoteInviteMimeLocator(OfflineMailbox mbox) {
			this.ombx = mbox;
		}
		
		public byte[] getInviteMime(int calendarItemId, int inviteId) throws ServiceException {
            final String contentUrlPrefix = ContentServlet.SERVLET_PATH + ContentServlet.PREFIX_GET + "?" +
		    			                    ContentServlet.PARAM_MSGID + "=";
			String contentUrl = Offline.getServerURI(ombx.getAccount(), contentUrlPrefix + calendarItemId + "-" + inviteId);
			try {
				return UserServlet.getRemoteContent(ombx.getAuthToken(), ombx.getRemoteHost(), contentUrl);
			} catch (MalformedURLException x) {
				throw ServiceException.FAILURE("MalformedURLException", x);
			}
		}
	}
	
    static final String A_RELOCATED = "relocated";

    private static final OfflineContext sContext = new OfflineContext();

    private final OfflineMailbox ombx;
    private final MailboxSync mMailboxSync;
    private DeltaSync dsync;
    private Element syncResponse;
    private boolean interrupted;

    InitialSync(OfflineMailbox mbox) {
        ombx = mbox;
        mMailboxSync = ombx.getMailboxSync();
    }

    InitialSync(DeltaSync delta) {
        ombx = delta.getMailbox();
        mMailboxSync = ombx.getMailboxSync();
        dsync = delta;
    }

    OfflineMailbox getMailbox() {
        return ombx;
    }

    private DeltaSync getDeltaSync() {
        if (dsync == null)
            dsync = new DeltaSync(this);
        return dsync;
    }

    public static String sync(OfflineMailbox ombx) throws ServiceException {
        return new InitialSync(ombx).sync();
    }

    public String sync() throws ServiceException {
        Element request = new Element.XMLElement(MailConstants.SYNC_REQUEST);
        syncResponse = ombx.sendRequest(request);
        
        OfflineLog.offline.debug(syncResponse.prettyPrint());
        
        String token = syncResponse.getAttribute(MailConstants.A_TOKEN);

        OfflineLog.offline.debug("starting initial sync");
        mMailboxSync.updateInitialSync(syncResponse);
        initialFolderSync(syncResponse.getElement(MailConstants.E_FOLDER));
        mMailboxSync.recordSyncComplete(token);
        OfflineLog.offline.debug("ending initial sync");

        return token;
    }

    public static String resume(OfflineMailbox ombx) throws ServiceException {
        return new InitialSync(ombx).resume();
    }

    public String resume() throws ServiceException {
        // do a NOOP before resuming to make sure the link is viable
        ombx.sendRequest(new Element.XMLElement(MailConstants.NO_OP_REQUEST));

        syncResponse = mMailboxSync.getInitialSyncResponse();
        String token = syncResponse.getAttribute(MailConstants.A_TOKEN);
        interrupted = true;

        OfflineLog.offline.debug("resuming initial sync");
        initialFolderSync(syncResponse.getElement(MailConstants.E_FOLDER));
        mMailboxSync.recordSyncComplete(token);
        OfflineLog.offline.debug("ending initial sync");

        return token;
    }

    static final Set<String> KNOWN_FOLDER_TYPES = new HashSet<String>(Arrays.asList(
            MailConstants.E_FOLDER, MailConstants.E_SEARCH
    ));

    private void initialFolderSync(Element elt) throws ServiceException {
        int folderId = (int) elt.getAttributeLong(MailConstants.A_ID);

        // first, sync the container itself
        syncContainer(elt, folderId);

        // next, sync the leaf-node contents
        if (elt.getName().equals(MailConstants.E_FOLDER)) {
            if (folderId == Mailbox.ID_FOLDER_TAGS) {
                for (Element eTag : elt.listElements(MailConstants.E_TAG)) {
                    syncTag(eTag);
                    eTag.detach();
                }
            }
            
            if (OfflineLC.zdesktop_sync_appointments.booleanValue()) {
	            int counter = 0;
	            int lastItem = mMailboxSync.getLastSyncedItem();
	            Element eCals = elt.getOptionalElement(MailConstants.E_APPOINTMENT);
	            if (eCals != null) {
	                for (String calId : eCals.getAttribute(MailConstants.A_IDS).split(",")) {
		                int id = Integer.parseInt(calId);
		                if (OfflineSyncManager.getInstance().isInSkipList(id)) {
		                	OfflineLog.offline.warn("Skipped appointment id=%d per zdesktop_sync_skip_idlist", id);
		                	continue;
		                }
		                	
		                if (interrupted && lastItem > 0) {
		                    if (id != lastItem) {
		                    	continue;
		                    } else {
		                    	lastItem = 0;
		                    }
		                }
		                if (isAlreadySynced(id, MailItem.TYPE_APPOINTMENT, false)) {
		                    continue;
		                }
		                
		                try {
		                	syncCalendarItem(id, folderId);
		                    if (++counter % 100 == 0)
		                        mMailboxSync.updateInitialSync(syncResponse, id);
		                } catch (Throwable t) {
		                	OfflineLog.offline.warn("failed to sync calendar item id=" + id, t);
		                }
	                }
	                
	                eCals.detach();
		            mMailboxSync.updateInitialSync(syncResponse);
	            }
            }
            
            if (OfflineLC.zdesktop_sync_messages.booleanValue()) {
	            Element eMessageIds = elt.getOptionalElement(MailConstants.E_MSG);
	            if (eMessageIds != null) {
	            	String[] msgIds = eMessageIds.getAttribute(MailConstants.A_IDS).split(",");
	            	List<Integer> ids = new ArrayList<Integer>();
	            	for (String msgId : msgIds) {
		                if (OfflineSyncManager.getInstance().isInSkipList(Integer.parseInt(msgId))) {
		                	OfflineLog.offline.warn("Skipped message id=%s per zdesktop_sync_skip_idlist", msgId);
		                	continue;
		                }
	            		ids.add(Integer.parseInt(msgId));
	            	}
	                syncMessagelikeItems(ids, folderId, MailItem.TYPE_MESSAGE, false);
	                eMessageIds.detach();
	                mMailboxSync.updateInitialSync(syncResponse);
	            }
            }

            if (OfflineLC.zdesktop_sync_chats.booleanValue()) {
	            Element eChatIds = elt.getOptionalElement(MailConstants.E_CHAT);
	            if (eChatIds != null) {
	            	String[] chatIds = eChatIds.getAttribute(MailConstants.A_IDS).split(",");
	            	List<Integer> ids = new ArrayList<Integer>();
	            	for (String chatId : chatIds) {
		                if (OfflineSyncManager.getInstance().isInSkipList(Integer.parseInt(chatId))) {
		                	OfflineLog.offline.warn("Skipped chat id=%s per zdesktop_sync_skip_idlist", chatId);
		                	continue;
		                }
	            		ids.add(Integer.parseInt(chatId));
	            	}
	                syncMessagelikeItems(ids, folderId, MailItem.TYPE_CHAT, false);
	                eChatIds.detach();
	                mMailboxSync.updateInitialSync(syncResponse);
	            }
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
	                    if (!isAlreadySynced(contactId, MailItem.TYPE_CONTACT, false))
	                        syncContact(eContact, folderId);
	                }
	                eContactIds.detach();
	                mMailboxSync.updateInitialSync(syncResponse);
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

        // finally, remove the node from the folder hierarchy to note that it's been processed
        elt.detach();
        mMailboxSync.updateInitialSync(syncResponse);
    }
    
    private void prioritySync(Element elt, int priorityFolderId) throws ServiceException {
        for (Element child : elt.listElements()) {
            if (KNOWN_FOLDER_TYPES.contains(child.getName()) && (int) child.getAttributeLong(MailConstants.A_ID) == priorityFolderId) {
            	initialFolderSync(child);
            }
        }
    }

    private boolean isAlreadySynced(int id, byte type, boolean isDeltaSync) throws ServiceException {
        if (!isDeltaSync && !interrupted)
            return false;

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
    
    public void syncMessagelikeItems(List<Integer> ids, int folderId, byte type, boolean isDeltaSync) throws ServiceException {
        int counter = 0, lastItem = mMailboxSync.getLastSyncedItem();
        List<Integer> itemList = new ArrayList<Integer>();
        for (int id : ids) {
            if (interrupted && lastItem > 0) {
                if (id != lastItem)  continue;
                else                 lastItem = 0;
            }
            if (isAlreadySynced(id, MailItem.TYPE_UNKNOWN, isDeltaSync))
                continue;

            int batchSize = OfflineLC.zdesktop_sync_batch_size.intValue();
            if (ombx.getRemoteServerVersion().getMajor() < 5 || batchSize == 1) {
                syncMessage(id, folderId, type);
                if (++counter % 100 == 0 && !isDeltaSync)
                    mMailboxSync.updateInitialSync(syncResponse, id);
            } else {
                itemList.add(id);
                if ((++counter % batchSize) == 0) {
                    syncMessages(itemList, type);
                    if (!isDeltaSync)
                    	mMailboxSync.updateInitialSync(syncResponse, id);
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
        else if (type.equalsIgnoreCase(MailConstants.E_FOLDER))
            syncFolder(elt, id);
    }

    void syncSearchFolder(Element elt, int id) throws ServiceException {
        int parentId = (int) elt.getAttributeLong(MailConstants.A_FOLDER);
        String name = MailItem.normalizeItemName(elt.getAttribute(MailConstants.A_NAME));
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION, -1);

        String query = elt.getAttribute(MailConstants.A_QUERY);
        String searchTypes = elt.getAttribute(MailConstants.A_SEARCH_TYPES);
        String sort = elt.getAttribute(MailConstants.A_SORTBY);

        boolean relocated = elt.getAttributeBool(A_RELOCATED, false) || !name.equals(elt.getAttribute(MailConstants.A_NAME));

        CreateSavedSearch redo = new CreateSavedSearch(ombx.getId(), parentId, name, query, searchTypes, sort, color);
        redo.setSearchId(id);
        redo.setChangeId(mod_content);
        redo.start(timestamp * 1000L);

        try {
            // XXX: FLAGS should be settable in the SearchFolder create...
            ombx.createSearchFolder(new OfflineContext(redo), parentId, name, query, searchTypes, sort, color);
            if (relocated)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_SEARCHFOLDER, Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);
            ombx.setTags(sContext, id, MailItem.TYPE_SEARCHFOLDER, flags, MailItem.TAG_UNCHANGED);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_SEARCHFOLDER, date, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created search folder (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            getDeltaSync().syncSearchFolder(elt, id);
        }
    }

    void syncFolder(Element elt, int id) throws ServiceException {
        //system folders should be already created during mailbox initialization, but just in cases the server is of newer version
    	//and there's a newly added system folder
        byte system = id < Mailbox.FIRST_USER_ID ? Folder.FOLDER_IS_IMMUTABLE : (byte)0;

        int parentId = (id == Mailbox.ID_FOLDER_ROOT) ? id : (int) elt.getAttributeLong(MailConstants.A_FOLDER);
        String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : MailItem.normalizeItemName(elt.getAttribute(MailConstants.A_NAME));
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        byte view = MailItem.getTypeForName(elt.getAttribute(MailConstants.A_DEFAULT_VIEW, null));

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION, -1);

        ACL acl = parseACL(elt.getOptionalElement(MailConstants.E_ACL));
        String url = elt.getAttribute(MailConstants.A_URL, null);

        boolean relocated = elt.getAttributeBool(A_RELOCATED, false) || (id != Mailbox.ID_FOLDER_ROOT && !name.equals(elt.getAttribute(MailConstants.A_NAME)));

        CreateFolder redo = new CreateFolder(ombx.getId(), name, parentId, system, view, flags, color, url);
        redo.setFolderId(id);
        redo.setChangeId(mod_content);
        redo.start(timestamp * 1000L);

        try {
            // don't care about current feed syncpoint; sync can't be done offline
            ombx.createFolder(new OfflineContext(redo), name, parentId, system, view, flags, color, url);
            if (relocated)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_FOLDER, Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);
            if (acl != null)
                ombx.setPermissions(sContext, id, acl);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_FOLDER, date, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created folder (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            getDeltaSync().syncFolder(elt, id);
        }
    }

    ACL parseACL(Element eAcl) throws ServiceException {
        if (eAcl == null)
            return null;
        ACL acl = new ACL();
        for (Element eGrant : eAcl.listElements(MailConstants.E_GRANT)) {
            short rights = ACL.stringToRights(eGrant.getAttribute(MailConstants.A_RIGHTS));
            byte gtype = FolderAction.stringToType(eGrant.getAttribute(MailConstants.A_GRANT_TYPE));
            String zid = eGrant.getAttribute(MailConstants.A_ZIMBRA_ID, null);
            String password = null;
            if (gtype == ACL.GRANTEE_GUEST) {
            	password = eGrant.getAttribute(MailConstants.A_PASSWORD, null);
            }
            acl.grantAccess(zid, gtype, rights, password);
        }
        return acl;
    }

    void syncTag(Element elt) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailConstants.A_ID);
        String name = MailItem.normalizeItemName(elt.getAttribute(MailConstants.A_NAME));
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION);

        boolean renamed = elt.getAttributeBool(A_RELOCATED, false) || !name.equals(elt.getAttribute(MailConstants.A_NAME));

        CreateTag redo = new CreateTag(ombx.getId(), name, color);
        redo.setTagId(id);
        redo.setChangeId(mod_content);
        redo.start(date * 1000L);

        try {
            // don't care about current feed syncpoint; sync can't be done offline
            ombx.createTag(new OfflineContext(redo), name, color);
            if (renamed)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_TAG, Change.MODIFIED_NAME);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_TAG, date, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created tag (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            getDeltaSync().syncTag(elt);
        }
    }

    static List<Element> fetchContacts(OfflineMailbox ombx, String ids) throws ServiceException {
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
    
    void syncCalendarItem(int id, int folderId) throws ServiceException {
        try {
            Element request = new Element.XMLElement(MailConstants.GET_APPOINTMENT_REQUEST);
            request.addAttribute(MailConstants.A_ID, Integer.toString(id));
            request.addAttribute(MailConstants.A_CAL_INCLUDE_CONTENT, 1);
            request.addAttribute(MailConstants.A_SYNC, 1);
            Element response = ombx.sendRequest(request);
            //OfflineLog.offline.debug(response.prettyPrint());
            
            Element apptElement = response.getElement(MailConstants.E_APPOINTMENT);
            String flagsStr = apptElement.getAttribute(MailConstants.A_FLAGS, null);
            int flags = flagsStr != null ? Flag.flagsToBitmask(flagsStr) : 0;
            String tagsStr = apptElement.getAttribute(MailConstants.A_TAGS, null);
            long tags = tagsStr != null ? Tag.tagsToBitmask(tagsStr) : 0;
            
            int date = (int)(apptElement.getAttributeLong(MailConstants.A_CAL_DATETIME) / 1000);
            int mod_content = (int)apptElement.getAttributeLong(MailConstants.A_REVISION);
            int change_date = (int)apptElement.getAttributeLong(MailConstants.A_CHANGE_DATE);
            int mod_metadata = (int)apptElement.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
            
            Element setAppointmentRequest = makeSetAppointmentRequest(apptElement, new RemoteInviteMimeLocator(ombx), ombx.getAccount());
            //OfflineLog.offline.debug(setAppointmentRequest.prettyPrint());
            
            setCalendarItem(setAppointmentRequest, id, folderId, date, mod_content, change_date, mod_metadata, flags, tags);
            
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.info("initial: appointment " + id + " has been deleted; skipping");
        }
    }
    
    //Massage the GetAppointmentResponse into a SetAppointmentReqeust
    static Element makeSetAppointmentRequest(Element resp, InviteMimeLocator imLocator, Account account) throws ServiceException {
    	String appId = resp.getAttribute(MailConstants.A_ID);
        
        Element req = new Element.XMLElement(MailConstants.SET_APPOINTMENT_REQUEST);
        req.addAttribute(MailConstants.A_FOLDER, resp.getAttribute(MailConstants.A_FOLDER));
        req.addAttribute(MailConstants.A_FLAGS, resp.getAttribute(MailConstants.A_FLAGS, ""));
        req.addAttribute(MailConstants.A_TAGS, resp.getAttribute(MailConstants.A_TAGS, ""));
        long nextAlarm = resp.getAttributeLong(MailConstants.A_CAL_NEXT_ALARM, 0);
        if (nextAlarm > 0)
            req.addAttribute(MailConstants.A_CAL_NEXT_ALARM, nextAlarm);
    	
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
            	Element content = msg.addElement(MailConstants.E_CONTENT);
                byte[] mimeContent = imLocator.getInviteMime(Integer.parseInt(appId), (int)inv.getAttributeLong(MailConstants.A_ID));
                content.setText(new String(mimeContent));
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
    
    void setCalendarItem(Element request, int itemId, int folderId, int date, int mod_content, int change_date, int mod_metadata, int flags, long tags) throws ServiceException {
    	// make a fake context to trick the parser so that we can reuse the soap parsing code
        ZimbraSoapContext zsc = new ZimbraSoapContext(new AuthToken(getMailbox().getAccount()), getMailbox().getAccountId(), SoapProtocol.Soap12, SoapProtocol.Soap12);
        SetCalendarItemParseResult parsed = SetCalendarItem.parseSetAppointmentRequest(request, zsc, sContext, MailItem.TYPE_APPOINTMENT, true);
    	
    	com.zimbra.cs.redolog.op.SetCalendarItem player = new com.zimbra.cs.redolog.op.SetCalendarItem(ombx.getId(), true, flags, tags);
    	player.setData(parsed.defaultInv, parsed.exceptions, parsed.replies, parsed.nextAlarm);
    	if (parsed.defaultInv != null)
        	player.setCalendarItemPartStat(parsed.defaultInv.mInv.getPartStat());
    	player.setCalendarItemAttrs(itemId, folderId, Volume.getCurrentMessageVolume().getId());
    	player.setChangeId(mod_content);
    	player.start(date);
    	
    	try {
 	    	OfflineContext ctxt = new OfflineContext(player);
 	    	ombx.setCalendarItem(ctxt, folderId, flags, tags, parsed.defaultInv, parsed.exceptions, parsed.replies, parsed.nextAlarm);
 	    	ombx.syncChangeIds(ctxt, itemId, MailItem.TYPE_APPOINTMENT, date, mod_content, change_date, mod_metadata);
 	    	if (OfflineLog.offline.isDebugEnabled()) {
 	    	    String name = null;
 	    	    if (parsed.defaultInv != null)
 	    	        name = parsed.defaultInv.mInv.getName();
 	    	    else if (parsed.exceptions != null && parsed.exceptions.length > 0)
 	    	        name = parsed.exceptions[0].mInv.getName();
     	    	OfflineLog.offline.debug("initial: created appointment (" + itemId + "): " + name);
 	    	}
    	} catch (Exception x) {
    		throw ServiceException.FAILURE("Failed setting calendar item id=" + itemId, x);
    	}
    }

    void syncContact(Element elt, int folderId) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailConstants.A_ID);
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));
        String tags = elt.getAttribute(MailConstants.A_TAGS, null);

        Map<String, String> fields = new HashMap<String, String>();
        for (Element eField : elt.listElements())
            fields.put(eField.getAttribute(Element.XMLElement.A_ATTR_NAME), eField.getText());

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION);

        byte[] blob = null;
        OfflineAccount acct = (OfflineAccount)ombx.getAccount();
        if ((flags & Flag.BITMASK_ATTACHED) != 0) {
            String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH + "/~/?fmt=native&id=" + id);
            OfflineLog.request.debug("GET " + url);
            try {
                String hostname = new URL(url).getHost();
                blob = UserServlet.getRemoteResource(ombx.getAuthToken(), hostname, url,
                		acct.getProxyHost(), acct.getProxyPort(), acct.getProxyUser(), acct.getProxyPass()).getSecond();
            } catch (MailServiceException.NoSuchItemException nsie) {
                OfflineLog.offline.warn("initial: no blob available for contact " + id);
            } catch (MalformedURLException e) {
                OfflineLog.offline.error("initial: base URI is invalid; aborting: " + url, e);
                throw ServiceException.FAILURE("base URI is invalid: " + url, e);
            }
        }
        ParsedContact pc = new ParsedContact(fields, blob);

        CreateContact redo = new CreateContact(ombx.getId(), folderId, pc, tags);
        redo.setContactId(id);
        redo.setChangeId(mod_content);
        redo.start(date * 1000L);

        try {
            Contact cn = ombx.createContact(new OfflineContext(redo), pc, folderId, tags);
            if (flags != 0)
                ombx.setTags(sContext, id, MailItem.TYPE_CONTACT, flags, MailItem.TAG_UNCHANGED);
            if (color != MailItem.DEFAULT_COLOR)
                ombx.setColor(sContext, id, MailItem.TYPE_CONTACT, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_CONTACT, date, mod_content, timestamp, changeId);
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

    private Map<String, String> recoverHeadersFromBytes(byte[] hdrBytes) {
    	Map<String, String> headers = new HashMap<String, String>();
    	if (hdrBytes != null && hdrBytes.length > 0) {
    		String[] keyVals = new String(hdrBytes).split("\r\n");
    		for (String hdr : keyVals) {
    			int delim = hdr.indexOf(": ");
    			headers.put(hdr.substring(0, delim), hdr.substring(delim + 2));
    		}
    	}
    	return headers;
    }
    
    private void syncMessages(List<Integer> ids, byte type) throws ServiceException {
    	UserServlet.HttpInputStream in = null;
    	
    	String zlv = OfflineLC.zdesktop_sync_zip_level.value();
    	OfflineAccount acct = (OfflineAccount)ombx.getAccount();
    	try {
	    	String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH + "/~/?fmt=zip&zlv=" + zlv + "&list=" + StringUtil.join(",", ids));
	    	OfflineLog.request.debug("GET " + url);
	        try {
	            String hostname = new URL(url).getHost();
	            Pair<Header[], UserServlet.HttpInputStream> response = UserServlet.getRemoteResourceAsStream(ombx.getAuthToken(), hostname, url,
	            		acct.getProxyHost(), acct.getProxyPort(), acct.getProxyUser(), acct.getProxyPass());
	            in = response.getSecond();
	        } catch (MailServiceException.NoSuchItemException nsie) {
	            OfflineLog.offline.info("initial: messages have been deleted; skipping");
	            return;
	        } catch (MalformedURLException e) {
	            OfflineLog.offline.error("initial: base URI is invalid; aborting: " + url, e);
	            throw ServiceException.FAILURE("base URI is invalid: " + url, e);
	        } catch (IOException x) {
	        	OfflineLog.offline.error("initial: can't read sync response: " + url, x);
	        	throw ServiceException.FAILURE("can't read sync response: " + url, x);
	        }
	        
	        ZipInputStream zin = new ZipInputStream(in);
	        ZipEntry entry = null;
	        try {
		        while ((entry = zin.getNextEntry()) != null) {
		        	ByteArrayOutputStream bout = new ByteArrayOutputStream();
	                byte[] buffer = new byte[4096];
	                int len;

                    while ((len = zin.read(buffer)) > 0)
	                    bout.write(buffer, 0, len);
		        	Map<String, String> headers = recoverHeadersFromBytes(entry.getExtra());
		        	int id = Integer.parseInt(headers.get("X-Zimbra-ItemId"));
		        	int folderId = Integer.parseInt(headers.get("X-Zimbra-FolderId"));
		        	
		        	saveMessage(bout.toByteArray(), headers, id, folderId, type);
		        }
	        } catch (IOException x) {
	        	OfflineLog.offline.error("Invalid sync format", x);
	        }
    	} finally {
    		if (in != null)
    			in.close();
    		}
    	}
        
    void syncMessage(int id, int folderId, byte type) throws ServiceException {
        byte[] content = null;
        Map<String, String> headers = new HashMap<String, String>();

        OfflineAccount acct = (OfflineAccount)ombx.getAccount();
        String url = Offline.getServerURI(acct, UserServlet.SERVLET_PATH + "/~/?fmt=sync&nohdr=1&id=" + id);
        OfflineLog.request.debug("GET " + url);
        try {
            String hostname = new URL(url).getHost();
            Pair<Header[], byte[]> response = UserServlet.getRemoteResource(ombx.getAuthToken(), hostname, url,
            		acct.getProxyHost(), acct.getProxyPort(), acct.getProxyUser(), acct.getProxyPass());
            content = response.getSecond();
            for (Header hdr : response.getFirst())
                headers.put(hdr.getName(), hdr.getValue());
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.info("initial: message " + id + " has been deleted; skipping");
            return;
        } catch (MalformedURLException e) {
            OfflineLog.offline.error("initial: base URI is invalid; aborting: " + url, e);
            throw ServiceException.FAILURE("base URI is invalid: " + url, e);
        }
        
        saveMessage(content, headers, id, folderId, type);
    }
    
    private void saveMessage(byte[] content, Map<String, String> headers, int id, int folderId, byte type) throws ServiceException {
        int received = (int) (Long.parseLong(headers.get("X-Zimbra-Received")) / 1000);
        int timestamp = (int) (Long.parseLong(headers.get("X-Zimbra-Modified")) / 1000);
        int mod_content = Integer.parseInt(headers.get("X-Zimbra-Revision"));
        int changeId = Integer.parseInt(headers.get("X-Zimbra-Change"));
        int flags = Flag.flagsToBitmask(headers.get("X-Zimbra-Flags"));
        String tags = headers.get("X-Zimbra-Tags");
        int convId = Integer.parseInt(headers.get("X-Zimbra-Conv"));
        if (convId < 0)
            convId = Mailbox.ID_AUTO_INCREMENT;

        ParsedMessage pm = null;
        String digest = null;
        int size;
        try {
            pm = new ParsedMessage(content, received * 1000L, false);
            digest = pm.getRawDigest();
            size = pm.getRawSize();
        } catch (MessagingException e) {
            throw MailServiceException.MESSAGE_PARSE_ERROR(e);
        } catch (IOException e) {
            throw MailServiceException.MESSAGE_PARSE_ERROR(e);
        }

        CreateMessage redo;
        if (type == MailItem.TYPE_CHAT)
            redo = new CreateChat(ombx.getId(), digest, size, folderId, flags, tags);
        else
            redo = new CreateMessage(ombx.getId(), null, received, false, digest, size, folderId, true, flags, tags);
        redo.setMessageId(id);
        redo.setConvId(convId);
        redo.setChangeId(mod_content);
        redo.start(received * 1000L);

        try {
            // FIXME: not syncing COLOR
            // XXX: need to call with noICal = false
            Message msg;
            if (type == MailItem.TYPE_CHAT)
                msg = ombx.createChat(new OfflineContext(redo), pm, folderId, flags, tags);
            else
                msg = ombx.addMessage(new OfflineContext(redo), pm, folderId, true, flags, tags, convId);
            ombx.syncChangeIds(sContext, id, type, received, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created " + MailItem.getNameForType(type) + " (" + id + "): " + msg.getSubject());
            return;
        } catch (IOException e) {
            throw ServiceException.FAILURE("storing " + MailItem.getNameForType(type) + " " + id, e);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            // fall through...
        }

        // if we're here, the message already exists; save new draft if needed, then update metadata
        try {
            Message msg = ombx.getMessageById(sContext, id);
            if (!digest.equals(msg.getDigest())) {
                pm.analyze();

                // FIXME: should check msg.isDraft() before doing this...
                CreateMessage redo2;
                if (type == MailItem.TYPE_CHAT)
                    redo2 = new SaveChat(ombx.getId(), id, digest, size, folderId, flags, tags);
                else
                    redo2 = new SaveDraft(ombx.getId(), id, digest, size);
                redo2.setChangeId(mod_content);
                redo2.start(received * 1000L);

                synchronized (ombx) {
                    int change_mask = ombx.getChangeMask(sContext, id, type);
                    if ((change_mask & Change.MODIFIED_CONTENT) == 0) {
                        if (type == MailItem.TYPE_CHAT)
                            ombx.updateChat(new OfflineContext(redo2), pm, id);
                        else
                        ombx.saveDraft(new OfflineContext(redo2), pm, id);
                        ombx.syncChangeIds(sContext, id, type, received, mod_content, timestamp, changeId);
                    }
                }
                OfflineLog.offline.debug("initial: re-saved draft " + MailItem.getNameForType(type) + " (" + id + "): " + msg.getSubject());
            }
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.debug("initial: " + MailItem.getNameForType(type) + " " + id + " has been deleted; no need to sync draft");
            return;
        } catch (IOException e) {
            throw ServiceException.FAILURE("storing message " + id, e);
        }

        // use this data to generate the XML entry used in message delta sync
        Element sync = new Element.XMLElement(Sync.elementNameForType(type)).addAttribute(MailConstants.A_ID, id);
        sync.addAttribute(MailConstants.A_FLAGS, Flag.bitmaskToFlags(flags)).addAttribute(MailConstants.A_TAGS, tags).addAttribute(MailConstants.A_CONV_ID, convId);
        sync.addAttribute(MailConstants.A_CHANGE_DATE, timestamp).addAttribute(MailConstants.A_MODIFIED_SEQUENCE, changeId);
        sync.addAttribute(MailConstants.A_DATE, received * 1000L).addAttribute(MailConstants.A_REVISION, mod_content);
        getDeltaSync().syncMessage(sync, folderId, type);
    }
}
