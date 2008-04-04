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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.MailConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.Offline;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.OfflineSyncManager;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.mail.Sync;
import com.zimbra.cs.session.PendingModifications.Change;

public class DeltaSync {

    private static final OfflineContext sContext = new OfflineContext();

    private final OfflineMailbox ombx;
    private final MailboxSync mMailboxSync;
    private final Set<Integer> mSyncRenames = new HashSet<Integer>();
    private InitialSync isync;

    DeltaSync(OfflineMailbox mbox) {
        ombx = mbox;
        mMailboxSync = ombx.getMailboxSync();
    }

    DeltaSync(InitialSync initial) {
        ombx = initial.getMailbox();
        mMailboxSync = ombx.getMailboxSync();
        isync = initial;
    }

    OfflineMailbox getMailbox() {
        return ombx;
    }

    private InitialSync getInitialSync() {
        if (isync == null)
            isync = new InitialSync(this);
        return isync;
    }


    public static String sync(OfflineMailbox ombx) throws ServiceException {
        return new DeltaSync(ombx).sync();
    }

    public String sync() throws ServiceException {
        String oldToken = mMailboxSync.getSyncToken();
        if (oldToken == null)
            oldToken = InitialSync.sync(ombx);

        Element response;
        String newToken;
        // keep delta sync'ing until the server tells us the delta sync is complete ("more=0")
        do {
            Element request = new Element.XMLElement(MailConstants.SYNC_REQUEST).addAttribute(MailConstants.A_TOKEN, oldToken).addAttribute(MailConstants.A_TYPED_DELETES, true);
            response = ombx.sendRequest(request);
            newToken = response.getAttribute(MailConstants.A_TOKEN);
    
            OfflineLog.offline.debug("starting delta sync [token " + oldToken + ']');
            deltaSync(response);
            // update the stored sync progress and loop again with new token if sync was incomplete
            if (!newToken.equals(oldToken))
            	mMailboxSync.recordSyncComplete(newToken);
            oldToken = newToken;
            OfflineLog.offline.debug("ending delta sync [token " + newToken + ']');
        } while (response.getAttributeBool(MailConstants.A_QUERY_MORE, false));

        return newToken;
    }

    private void deltaSync(Element response) throws ServiceException {
        // make sure to handle deletes first, as tags can reuse ids
        Set<Integer> foldersToDelete = processLeafDeletes(response);

        // sync down metadata changes and note items that need to be downloaded in full
        Map<Integer, List<Integer>> messages = new HashMap<Integer, List<Integer>>(), chats = new HashMap<Integer, List<Integer>>();
        Map<Integer, Integer> deltamsgs = null, deltachats = null, contacts = null, appts = null;
        for (Element change : response.listElements()) {
            int id = (int) change.getAttributeLong(MailConstants.A_ID);
            String type = change.getName();

            if (type.equals(MailConstants.E_TAG)) {
                syncTag(change);
                continue;
            }

            int folderId = (id == Mailbox.ID_FOLDER_ROOT ? Mailbox.ID_FOLDER_ROOT : (int) change.getAttributeLong(MailConstants.A_FOLDER));
            boolean create = (change.getAttribute(MailConstants.A_FLAGS, null) == null);

            if (type.equals(MailConstants.E_MSG)) {
            	if (!OfflineLC.zdesktop_sync_messages.booleanValue())
            		continue;
                if (OfflineSyncManager.getInstance().isInSkipList(id)) {
                	OfflineLog.offline.warn("Skipped message id=%d per zdesktop_sync_skip_idlist", id);
                	continue;
                }
            	
                if (create && canDeltaSyncMessage(id, change))
                    (deltamsgs == null ? deltamsgs = new HashMap<Integer, Integer>() : deltamsgs).put(id, folderId);
                else if (create) {
                	List<Integer> idList = messages.get(folderId);
                	if (idList == null) {
                		idList = new ArrayList<Integer>();
                		messages.put(folderId, idList);
                	}
                	idList.add(id);
                } else
                    syncMessage(change, folderId, MailItem.TYPE_MESSAGE);
            } else if (type.equals(MailConstants.E_CHAT)) {
            	if (!OfflineLC.zdesktop_sync_chats.booleanValue())
            		continue;
                if (OfflineSyncManager.getInstance().isInSkipList(id)) {
                	OfflineLog.offline.warn("Skipped chat id=%d per zdesktop_sync_skip_idlist", id);
                	continue;
                }
            	
                if (create && canDeltaSyncMessage(id, change))
                    (deltachats == null ? deltachats = new HashMap<Integer,Integer>() : deltachats).put(id, folderId);
                else if (create) {
                	List<Integer> idList = chats.get(folderId);
                	if (idList == null) {
                		idList = new ArrayList<Integer>();
                		chats.put(folderId, idList);
                	}
                	idList.add(id);
                } else
                    syncMessage(change, folderId, MailItem.TYPE_CHAT);
            } else if (type.equals(MailConstants.E_CONTACT)) {
            	if (!OfflineLC.zdesktop_sync_contacts.booleanValue())
            		continue;
                if (OfflineSyncManager.getInstance().isInSkipList(id)) {
                	OfflineLog.offline.warn("Skipped contact id=%d per zdesktop_sync_skip_idlist", id);
                	continue;
                }
            	
                if (create)
                    (contacts == null ? contacts = new HashMap<Integer,Integer>() : contacts).put(id, folderId);
                else
                    syncContact(change, folderId);
            } else if (type.equals(MailConstants.E_APPOINTMENT)) {
            	if (!OfflineLC.zdesktop_sync_appointments.booleanValue())
            		continue;
                if (OfflineSyncManager.getInstance().isInSkipList(id)) {
                	OfflineLog.offline.warn("Skipped appointment id=%d per zdesktop_sync_skip_idlist", id);
                	continue;
                }
            	
            	(appts == null ? appts = new HashMap<Integer,Integer>() : appts).put(id, folderId);
            } else if (InitialSync.KNOWN_FOLDER_TYPES.contains(type)) {
                // can't tell new folders from modified ones, so might as well go through the initial sync process
                syncContainer(change, id);
            }
        }

        // for messages, chats, and contacts that are created or had their content modified, fetch new content
        if (OfflineLC.zdesktop_sync_messages.booleanValue() && deltamsgs != null) {
            Element request = new Element.XMLElement(MailConstants.GET_MSG_METADATA_REQUEST);
            request.addElement(MailConstants.E_MSG).addAttribute(MailConstants.A_IDS, StringUtil.join(",", deltamsgs.keySet()));
            for (Element elt : ombx.sendRequest(request).listElements())
                syncMessage(elt, deltamsgs.get((int) elt.getAttributeLong(MailConstants.A_ID)), MailItem.TYPE_MESSAGE);
        }
        
        //sync appointments before messages so that new invite messages can be linked to appointments
        if (OfflineLC.zdesktop_sync_appointments.booleanValue() && appts != null) {
        	for (Map.Entry<Integer,Integer> entry : appts.entrySet())
                getInitialSync().syncCalendarItem(entry.getKey(), entry.getValue());
        }
        
        if (OfflineLC.zdesktop_sync_messages.booleanValue()) {
	        Set<Integer> msgFolders = messages.keySet();
	        for (int folderId : msgFolders)
	            getInitialSync().syncMessagelikeItems(messages.get(folderId), folderId, MailItem.TYPE_MESSAGE, true);
        }
        
        if (OfflineLC.zdesktop_sync_chats.booleanValue() && deltachats != null) {
            Element request = new Element.XMLElement(MailConstants.GET_MSG_METADATA_REQUEST);
            request.addElement(MailConstants.E_MSG).addAttribute(MailConstants.A_IDS, StringUtil.join(",", deltachats.keySet()));
            for (Element elt : ombx.sendRequest(request).listElements())
                syncMessage(elt, deltachats.get((int) elt.getAttributeLong(MailConstants.A_ID)), MailItem.TYPE_CHAT);
        }
        
        if (OfflineLC.zdesktop_sync_chats.booleanValue()) {
	        Set<Integer> chatFolders = chats.keySet();
	        for (int folderId : chatFolders)
	            getInitialSync().syncMessagelikeItems(chats.get(folderId), folderId, MailItem.TYPE_CHAT, true);
        }

        if (OfflineLC.zdesktop_sync_contacts.booleanValue() && contacts != null) {
            for (Element elt : InitialSync.fetchContacts(ombx, StringUtil.join(",", contacts.keySet())))
                getInitialSync().syncContact(elt, contacts.get((int) elt.getAttributeLong(MailConstants.A_ID)));
        }

        // delete any deleted folders, starting from the bottom of the tree
        if (foldersToDelete != null && !foldersToDelete.isEmpty()) {
            synchronized (ombx) {
                List<Folder> folders = ombx.getFolderById(sContext, Mailbox.ID_FOLDER_ROOT).getSubfolderHierarchy();
                Collections.reverse(folders);
                for (Folder folder : folders) {
                    if (foldersToDelete.remove(folder.getId()))
                        processFolderDelete(folder);
                }
            }
        }
    }

    private boolean canDeltaSyncMessage(int id, Element change) throws ServiceException {
        try {
            Message msg = ombx.getMessageById(sContext, id);
            if (msg.getSavedSequence() != change.getAttributeLong(MailConstants.A_REVISION, -1))
                return false;
            if (msg.getModifiedSequence() != change.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE))
                return false;
            return true;
        } catch (MailServiceException.NoSuchItemException e) {
            return false;
        }
    }

    private Set<Integer> processLeafDeletes(Element response) throws ServiceException {
        Element delement = response.getOptionalElement(MailConstants.E_DELETED);
        if (delement == null)
            return null;
        delement.detach();

        // sort the deleted items into a bucket of leaf nodes to delete now and a set of folders to delete later
        List<Integer> leafIds = new ArrayList<Integer>(), tagIds = new ArrayList<Integer>();
        Set<Integer> foldersToDelete = new HashSet<Integer>();

        // sort the deleted items into two sets: leaves and folders
        for (Element deltype : delement.listElements()) {
            byte type = Sync.typeForElementName(deltype.getName());
            if (type == MailItem.TYPE_UNKNOWN || type == MailItem.TYPE_CONVERSATION)
                continue;
            boolean isTag = type == MailItem.TYPE_TAG;
            boolean isFolder = InitialSync.KNOWN_FOLDER_TYPES.contains(deltype.getName());
            for (String idStr : deltype.getAttribute(MailConstants.A_IDS).split(",")) {
                Integer id = Integer.valueOf(idStr);
                // tag numbering conflict issues: don't delete tags we've created locally
                if (isTag && (ombx.getChangeMask(sContext, id, type) & Change.MODIFIED_CONFLICT) != 0)
                    continue;
                (isFolder ? foldersToDelete : leafIds).add(id);
                if (isTag)
                    tagIds.add(id);
            }
        }

        // temporary workaround because tags are left off the "typed delete" list up to 4.5.2
        for (String idStr : delement.getAttribute(MailConstants.A_IDS).split(",")) {
            Integer id = Integer.valueOf(idStr);
            if (id < MailItem.TAG_ID_OFFSET || id >= MailItem.TAG_ID_OFFSET + MailItem.MAX_TAG_COUNT || tagIds.contains(id))
                continue;
            // tag numbering conflict issues: don't delete tags we've created locally
            if ((ombx.getChangeMask(sContext, id, MailItem.TYPE_TAG) & Change.MODIFIED_CONFLICT) != 0)
                continue;
            leafIds.add(id);  tagIds.add(id);
        }

        // delete all the leaves now
        int idx = 0, ids[] = new int[leafIds.size()];
        for (int id : leafIds)
            ids[idx++] = id;
        ombx.delete(sContext, ids, MailItem.TYPE_UNKNOWN, null);
        OfflineLog.offline.debug("delta: deleted leaves: " + Arrays.toString(ids));

        // avoid some nasty corner cases caused by tag id reuse
        for (int tagId : tagIds)
            ombx.removePendingDelete(sContext, tagId, MailItem.TYPE_TAG);

        // save the folder deletes for later
        return (foldersToDelete.isEmpty() ? null : foldersToDelete);
    }

    private void processFolderDelete(Folder folder) throws ServiceException {
        if (!folder.isDeletable()) {
            OfflineLog.offline.warn("delta: cannot delete " + MailItem.getNameForType(folder) + " (" + folder.getId() + "): " + folder.getName());
            return;
        }

        if (folder.getItemCount() == 0 && !folder.hasSubfolders()) {
            // normal case: contents have already been deleted via processLeafDeletes()
            ombx.delete(sContext, folder.getId(), folder.getType());
            OfflineLog.offline.debug("delta: deleted folder: " + folder.getId());
            return;
        }

        // mark the remote folder for re-creation in order to hold its local contents
        ombx.setChangeMask(sContext, folder.getId(), folder.getType(), Change.MODIFIED_CONFLICT);
        // mark all the contents as moved so that they appear in the remote folder
        for (int contained : ombx.listItemIds(sContext, MailItem.TYPE_UNKNOWN, folder.getId())) {
            int change_mask = ombx.getChangeMask(sContext, contained, MailItem.TYPE_UNKNOWN);
            if ((change_mask & (Change.MODIFIED_FOLDER | Change.MODIFIED_CONFLICT)) == 0)
                ombx.setChangeMask(sContext, contained, MailItem.TYPE_UNKNOWN, change_mask | Change.MODIFIED_FOLDER);
        }
        OfflineLog.offline.debug("delta: queued folder for recreate: " + folder.getId());
    }

    private void syncContainer(Element elt, int id) throws ServiceException {
        String type = elt.getName();
        if (type.equalsIgnoreCase(MailConstants.E_SEARCH))
            syncSearchFolder(elt, id);
        else if (type.equalsIgnoreCase(MailConstants.E_FOLDER))
            syncFolder(elt, id);
    }

    void syncSearchFolder(Element elt, int id) throws ServiceException {
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION, -1);

        String query = elt.getAttribute(MailConstants.A_QUERY);
        String searchTypes = elt.getAttribute(MailConstants.A_SEARCH_TYPES);
        String sort = elt.getAttribute(MailConstants.A_SORTBY);

        synchronized (ombx) {
            // deal with the case where the referenced search folder doesn't exist
            Folder folder = getFolder(id);
            if (folder == null) {
                // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
                if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_SEARCHFOLDER))
                    return;
                // resolve any naming conflicts and actually create the folder
                if (resolveFolderConflicts(elt, id, MailItem.TYPE_SEARCHFOLDER, folder)) {
                    getInitialSync().syncSearchFolder(elt, id);
                    return;
                } else {
                    folder = getFolder(id);
                }
            }

            // if the search folder was moved/renamed locally, that trumps any changes made remotely
            resolveFolderConflicts(elt, id, MailItem.TYPE_SEARCHFOLDER, folder);

            int parentId = (int) elt.getAttributeLong(MailConstants.A_FOLDER);
            String name = elt.getAttribute(MailConstants.A_NAME);

            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_SEARCHFOLDER);
            ombx.rename(sContext, id, MailItem.TYPE_SEARCHFOLDER, name, parentId);
            if ((change_mask & Change.MODIFIED_QUERY) == 0)
                ombx.modifySearchFolder(sContext, id, query, searchTypes, sort);
            ombx.syncMetadata(sContext, id, MailItem.TYPE_SEARCHFOLDER, parentId, flags, 0, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_SEARCHFOLDER, date, mod_content, timestamp, changeId);

            if (elt.getAttributeBool(InitialSync.A_RELOCATED, false))
                ombx.setChangeMask(sContext, id, MailItem.TYPE_SEARCHFOLDER, change_mask | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);

            OfflineLog.offline.debug("delta: updated search folder (" + id + "): " + name);
        }
    }

    void syncFolder(Element elt, int id) throws ServiceException {
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null)) & ~Flag.BITMASK_UNREAD;
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        String url = elt.getAttribute(MailConstants.A_URL, null);

        ACL acl = getInitialSync().parseACL(elt.getOptionalElement(MailConstants.E_ACL));

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION, -1);

        synchronized (ombx) {
            // deal with the case where the referenced folder doesn't exist
            Folder folder = getFolder(id);
            if (folder == null) {
                // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
                if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_FOLDER))
                    return;
                // resolve any naming conflicts and actually create the folder
                if (resolveFolderConflicts(elt, id, MailItem.TYPE_FOLDER, folder)) {
                    getInitialSync().syncFolder(elt, id);
                    return;
                } else {
                    folder = getFolder(id);
                }
            }

            // if the folder was moved/renamed locally, that trumps any changes made remotely
            resolveFolderConflicts(elt, id, MailItem.TYPE_FOLDER, folder);

            int parentId = (id == Mailbox.ID_FOLDER_ROOT) ? id : (int) elt.getAttributeLong(MailConstants.A_FOLDER);
            String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : elt.getAttribute(MailConstants.A_NAME);

            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_FOLDER);
            if (id != Mailbox.ID_FOLDER_ROOT)
                ombx.rename(sContext, id, MailItem.TYPE_FOLDER, name, parentId);
            // XXX: do we need to sync if the folder has perms but the new ACL is empty?
            if ((change_mask & Change.MODIFIED_ACL) == 0 && acl != null)
                ombx.setPermissions(sContext, id, acl);
            if ((change_mask & Change.MODIFIED_URL) == 0)
                ombx.setFolderUrl(sContext, id, url);
            // don't care about current feed syncpoint; sync can't be done offline
            ombx.syncMetadata(sContext, id, MailItem.TYPE_FOLDER, parentId, flags, 0, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_FOLDER, date, mod_content, timestamp, changeId);

            if (elt.getAttributeBool(InitialSync.A_RELOCATED, false))
                ombx.setChangeMask(sContext, id, MailItem.TYPE_FOLDER, change_mask | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);

            OfflineLog.offline.debug("delta: updated folder (" + id + "): " + name);
        }
    }

    private Folder getFolder(int id) throws ServiceException {
        try {
            return ombx.getFolderById(sContext, id);
        } catch (MailServiceException.NoSuchItemException nsie) {
            return null;
        }
    }

    private boolean resolveFolderConflicts(Element elt, int id, byte type, Folder local) throws ServiceException {
        int change_mask = (local == null ? 0 : ombx.getChangeMask(sContext, id, type));

        // if the folder was moved/renamed locally, that trumps any changes made remotely
        int parentId = (id == Mailbox.ID_FOLDER_ROOT) ? id : (int) elt.getAttributeLong(MailConstants.A_FOLDER);
        if ((change_mask & Change.MODIFIED_FOLDER) != 0) {
            parentId = local.getFolderId();  elt.addAttribute(MailConstants.A_FOLDER, parentId);
        }

        String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : elt.getAttribute(MailConstants.A_NAME);
        if ((change_mask & Change.MODIFIED_NAME) != 0 && !mSyncRenames.contains(id)) {
            name = local.getName();  elt.addAttribute(MailConstants.A_NAME, name);
        } else {
            // handle the off-chance that the server's folder name is invalid
            String validName = MailItem.normalizeItemName(name);
            if (!validName.equals(name)) {
                name = validName;  elt.addAttribute(MailConstants.A_NAME, name).addAttribute(InitialSync.A_RELOCATED, true);
            }
        }

        // if the parent folder doesn't exist or is of an incompatible type, default to using the top-level user folder as the container
        Folder parent = getFolder(parentId);
        if (parent == null || !parent.canContain(type)) {
            parentId = Mailbox.ID_FOLDER_USER_ROOT;  parent = getFolder(parentId);
            elt.addAttribute(MailConstants.A_FOLDER, parentId).addAttribute(InitialSync.A_RELOCATED, true);
        }

        Folder conflict = parent.findSubfolder(name);
        if (conflict != null && conflict.getId() != id) {
            int conflict_mask = ombx.getChangeMask(sContext, conflict.getId(), conflict.getType());

            String uuid = '{' + UUID.randomUUID().toString() + '}', newName;
            if (name.length() + uuid.length() > MailItem.MAX_NAME_LENGTH)
                newName = name.substring(0, MailItem.MAX_NAME_LENGTH - uuid.length()) + uuid;
            else
                newName = name + uuid;

            if (local == null && (conflict_mask & Change.MODIFIED_CONFLICT) != 0 && isCompatibleFolder(conflict, elt, type)) {
                // if the new and existing folders are identical and being created, try to merge them
                ombx.renumberItem(sContext, conflict.getId(), type, id);
                ombx.setChangeMask(sContext, id, type, conflict_mask & ~Change.MODIFIED_CONFLICT);
                return false;
            } else if (!conflict.isMutable() || (conflict_mask & Change.MODIFIED_NAME) != 0) {
                // either the local user also renamed the folder or the folder's immutable, so the local client wins
                name = newName;
                elt.addAttribute(MailConstants.A_NAME, name).addAttribute(InitialSync.A_RELOCATED, true);
            } else {
                // if there's a folder naming conflict within the target folder, usually push the local folder out of the way
                ombx.rename(null, conflict.getId(), conflict.getType(), newName);
                if ((conflict_mask & Change.MODIFIED_NAME) == 0)
                    mSyncRenames.add(conflict.getId());
            }
        }

        // if conflicts have forced us to deviate from the specified sync, update the local store such that these changes are pushed during the next sync
        if (local != null && elt.getAttributeBool(InitialSync.A_RELOCATED, false))
            ombx.rename(null, id, type, name, parentId);

        return true;
    }

    private boolean isCompatibleFolder(Folder folder, Element elt, byte type) {
        if (type != folder.getType())
            return false;

        if (type == MailItem.TYPE_FOLDER)
           return folder.getDefaultView() == MailItem.getTypeForName(elt.getAttribute(MailConstants.A_DEFAULT_VIEW, null));
        else
            return false;
    }

    void syncTag(Element elt) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailConstants.A_ID);
        String name = elt.getAttribute(MailConstants.A_NAME);
        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION);

        synchronized (ombx) {
            Tag tag = getTag(id);

            // we're reusing tag IDs, so it's possible that we've got a conflict with a locally-created tag with that ID
            if (tag != null && !name.equalsIgnoreCase(tag.getName()) && (ombx.getChangeMask(sContext, id, MailItem.TYPE_TAG) & Change.MODIFIED_CONFLICT) != 0) {
                int newId = getAvailableTagId(ombx);
                if (newId < 0)
                    ombx.delete(sContext, id, MailItem.TYPE_TAG);
                else
                    ombx.renumberItem(sContext, id, MailItem.TYPE_TAG, newId);
                tag = null;
            }

            // deal with the case where the referenced tag doesn't exist
            if (tag == null) {
                // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
                if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_TAG))
                    return;
                // resolve any naming conflicts and actually create the tag
                if (resolveTagConflicts(elt, id, tag)) {
                    getInitialSync().syncTag(elt);
                    return;
                } else {
                    tag = getTag(id);
                }
            }

            // if the tag was renamed locally, that trumps any changes made remotely
            resolveTagConflicts(elt, id, tag);

            name = elt.getAttribute(MailConstants.A_NAME);

            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_TAG);
            // FIXME: if FOO was renamed BAR and BAR was renamed FOO, this will break
            if ((change_mask & Change.MODIFIED_NAME) == 0)
                ombx.rename(sContext, id, MailItem.TYPE_TAG, name);
            if ((change_mask & Change.MODIFIED_COLOR) == 0)
                ombx.setColor(sContext, id, MailItem.TYPE_TAG, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_TAG, date, mod_content, timestamp, changeId);

            if (elt.getAttributeBool(InitialSync.A_RELOCATED, false))
                ombx.setChangeMask(sContext, id, MailItem.TYPE_TAG, change_mask | Change.MODIFIED_NAME);

            OfflineLog.offline.debug("delta: updated tag (" + id + "): " + name);
        }
    }

    private boolean resolveTagConflicts(Element elt, int id, Tag local) throws ServiceException {
        int change_mask = (local == null ? 0 : ombx.getChangeMask(sContext, id, MailItem.TYPE_TAG));

        String name = elt.getAttribute(MailConstants.A_NAME);
        if ((change_mask & Change.MODIFIED_NAME) != 0 && !mSyncRenames.contains(id)) {
            name = local.getName();  elt.addAttribute(MailConstants.A_NAME, name);
        } else {
            // handle the off-chance that the server's folder name is invalid
            String validName = MailItem.normalizeItemName(name);
            if (!validName.equals(name)) {
                name = validName;  elt.addAttribute(MailConstants.A_NAME, name).addAttribute(InitialSync.A_RELOCATED, true);
            }
        }

        Tag conflict = getTag(name);
        if (conflict == null)
            return true;

        int conflict_mask = ombx.getChangeMask(sContext, conflict.getId(), MailItem.TYPE_TAG);
        if (conflict.getId() == id) {
            // new tag remotely, new tag locally, same name
            if ((conflict_mask & Change.MODIFIED_CONFLICT) != 0)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_TAG, conflict_mask & ~Change.MODIFIED_CONFLICT);
            return false;
        }

        String uuid = '{' + UUID.randomUUID().toString() + '}', newName;
        if (name.length() + uuid.length() > MailItem.MAX_NAME_LENGTH)
            newName = name.substring(0, MailItem.MAX_NAME_LENGTH - uuid.length()) + uuid;
        else
            newName = name + uuid;

        if (local == null && (conflict_mask & Change.MODIFIED_CONFLICT) != 0) {
            // if the new and existing tags are identical and being created, try to merge them
            ombx.renumberItem(sContext, conflict.getId(), MailItem.TYPE_TAG, id);
            ombx.setChangeMask(sContext, id, MailItem.TYPE_TAG, conflict_mask & ~Change.MODIFIED_CONFLICT);
            return false;
        } else if ((conflict_mask & Change.MODIFIED_NAME) != 0) {
            // the local user also renamed the tag, so the local client wins
            name = newName;
            elt.addAttribute(MailConstants.A_NAME, name).addAttribute(InitialSync.A_RELOCATED, true);
            if (local != null)
                ombx.rename(null, id, MailItem.TYPE_TAG, name);
        } else {
            // if there's a naming conflict, usually rename the local tag out of the way
            ombx.rename(null, conflict.getId(), MailItem.TYPE_TAG, newName);
            mSyncRenames.add(conflict.getId());
        }

        return true;
    }

    static int getAvailableTagId(Mailbox mbox) throws ServiceException {
        int tagId;
        for (tagId = MailItem.TAG_ID_OFFSET + MailItem.MAX_TAG_COUNT - 1; tagId >= MailItem.TAG_ID_OFFSET; tagId--)
            if (getTag(mbox, tagId) == null)
                break;
        return (tagId < MailItem.TAG_ID_OFFSET ? -1 : tagId);
    }

    private Tag getTag(int id) throws ServiceException {
        return getTag(ombx, id);
    }

    static Tag getTag(Mailbox mbox, int id) throws ServiceException {
        try {
            return mbox.getTagById(sContext, id);
        } catch (MailServiceException.NoSuchItemException nsie) {
            return null;
        }
    }

    private Tag getTag(String name) throws ServiceException {
        try {
            return ombx.getTagByName(name);
        } catch (MailServiceException.NoSuchItemException nsie) {
            return null;
        }
    }

    void syncContact(Element elt, int folderId) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailConstants.A_ID);
        Contact cn = null;
        try {
            // make sure that the contact we're delta-syncing actually exists
            cn = ombx.getContactById(sContext, id);
        } catch (MailServiceException.NoSuchItemException nsie) {
            // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
            if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_CONTACT))
                return;
            // before passing off to initial sync, make sure we have the contact's fields
            if (elt.listElements(Element.XMLElement.E_ATTRIBUTE).isEmpty())
                elt = InitialSync.fetchContacts(ombx, id + "").get(0);
            getInitialSync().syncContact(elt, folderId);
            return;
        }

        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));
        long tags = Tag.tagsToBitmask(elt.getAttribute(MailConstants.A_TAGS, null));

        boolean hasBlob = false;
        Map<String, String> fields = new HashMap<String, String>();
        for (Element eField : elt.listElements(Element.XMLElement.E_ATTRIBUTE)) {
            if (eField.getAttribute(MailConstants.A_PART, null) != null)
                hasBlob = true;
            else
                fields.put(eField.getAttribute(Element.XMLElement.A_ATTR_NAME), eField.getText());
        }
        assert(hasBlob == ((flags & Flag.BITMASK_ATTACHED) != 0));

        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION);

        byte[] blob = null;
        if (hasBlob) {
            String url = Offline.getServerURI(ombx.getAccount(), UserServlet.SERVLET_PATH + "/~/?fmt=native&id=" + id);
            OfflineLog.request.debug("GET " + url);
            try {
                String hostname = new URL(url).getHost();
                blob = UserServlet.getRemoteContent(ombx.getAuthToken(), hostname, url);
            } catch (MailServiceException.NoSuchItemException nsie) {
                OfflineLog.offline.warn("delta: no blob available for contact " + id);
            } catch (MalformedURLException e) {
                OfflineLog.offline.error("delta: base URI is invalid; aborting: " + url, e);
                throw ServiceException.FAILURE("base URI is invalid: " + url, e);
            }
        }
        ParsedContact pc = (fields.isEmpty() ? null : new ParsedContact(fields, blob));

        synchronized (ombx) {
            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_CONTACT);
            if ((change_mask & Change.MODIFIED_CONTENT) == 0 && pc != null)
                ombx.modifyContact(sContext, id, pc);
            ombx.syncMetadata(sContext, id, MailItem.TYPE_CONTACT, folderId, flags, tags, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_CONTACT, date, mod_content, timestamp, changeId);
        }
        OfflineLog.offline.debug("delta: updated contact (" + id + "): " + cn.getFileAsString());
    }

//    void syncCalendarItem(Element elt, int folderId) throws ServiceException {
//        int id = (int) elt.getAttributeLong(MailConstants.A_ID);
//        CalendarItem cal = null;
//        try {
//            // make sure that the item we're delta-syncing actually exists
//            cal = ombx.getCalendarItemById(sContext, id);
//        } catch (MailServiceException.NoSuchItemException nsie) {
//            // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
//            if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_APPOINTMENT))
//                return;
//            getInitialSync().syncCalendarItem(id, folderId);
//            return;
//        }
//
//        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
//        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));
//        long tags = Tag.tagsToBitmask(elt.getAttribute(MailConstants.A_TAGS, null));
//
//        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
//        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
//        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE) / 1000);
//        
//        synchronized (ombx) {
//            ombx.syncMetadata(sContext, id, MailItem.TYPE_APPOINTMENT, folderId, flags, tags, color);
//            ombx.syncChangeIds(sContext, id, MailItem.TYPE_APPOINTMENT, date, -1, timestamp, changeId);
//        }
//        OfflineLog.offline.debug("delta: updated appointment (" + id + "): " + cal.getSubject());
//    }
    
    void syncMessage(Element elt, int folderId, byte type) throws ServiceException {
   		int id = (int) elt.getAttributeLong(MailConstants.A_ID);
    	try {
	        Message msg = null;
	        try {
	            // make sure that the message we're delta-syncing actually exists
	            msg = ombx.getMessageById(sContext, id);
	        } catch (MailServiceException.NoSuchItemException nsie) {
	            // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
	            if (!ombx.isPendingDelete(sContext, id, type))
	                getInitialSync().syncMessage(id, folderId, type);
	            return;
	        }
	
	        byte color = (byte) elt.getAttributeLong(MailConstants.A_COLOR, MailItem.DEFAULT_COLOR);
	        int flags = Flag.flagsToBitmask(elt.getAttribute(MailConstants.A_FLAGS, null));
	        long tags = Tag.tagsToBitmask(elt.getAttribute(MailConstants.A_TAGS, null));
	        int convId = (int) elt.getAttributeLong(MailConstants.A_CONV_ID);
	
	        int timestamp = (int) elt.getAttributeLong(MailConstants.A_CHANGE_DATE);
	        int changeId = (int) elt.getAttributeLong(MailConstants.A_MODIFIED_SEQUENCE);
	        int date = (int) (elt.getAttributeLong(MailConstants.A_DATE) / 1000);
	        int mod_content = (int) elt.getAttributeLong(MailConstants.A_REVISION);
	
	        // double-check to make sure that it's just a metadata change
	//        if (mod_content != msg.getSavedSequence() || date != msg.getDate() / 1000) {
	//            // content changed; must re-download body
	//            getInitialSync().syncMessage(id, folderId);
	//            return;
	//        }
	
	        synchronized (ombx) {
	            ombx.setConversationId(sContext, id, convId <= 0 ? -id : convId);
	            ombx.syncMetadata(sContext, id, type, folderId, flags, tags, color);
	            ombx.syncChangeIds(sContext, id, type, date, mod_content, timestamp, changeId);
	        }
	        OfflineLog.offline.debug("delta: updated " + MailItem.getNameForType(type) + " (" + id + "): " + msg.getSubject());
    	} catch (ServiceException x) {
    		SyncExceptionHandler.syncMessageFailed(ombx, id, x);
    	}
    }
}
