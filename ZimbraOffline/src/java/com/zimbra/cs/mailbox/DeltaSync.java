/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * The Original Code is: Zimbra Network
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.mailbox;

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
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.mailbox.OfflineMailbox.SyncState;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.mail.MailService;
import com.zimbra.cs.service.mail.SyncOperation;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.soap.Element;

public class DeltaSync {

    private static final OfflineContext sContext = new OfflineContext();

    private final OfflineMailbox ombx;
    private final Set<Integer> mSyncRenames = new HashSet<Integer>();

    DeltaSync(OfflineMailbox mbox) {
        ombx = mbox;
    }


    public static int sync(OfflineMailbox ombx) throws ServiceException {
        return new DeltaSync(ombx).sync();
    }

    public int sync() throws ServiceException {
        int token = ombx.getSyncToken();
        if (token <= 0)
            return InitialSync.sync(ombx);

        Element request = new Element.XMLElement(MailService.SYNC_REQUEST).addAttribute(MailService.A_TOKEN, token).addAttribute(MailService.A_TYPED_DELETES, true);
        Element response = ombx.sendRequest(request);
        token = (int) response.getAttributeLong(MailService.A_TOKEN);

        OfflineLog.offline.debug("starting delta sync");
        ombx.setSyncState(SyncState.DELTA);
        deltaSync(response);
        ombx.setSyncState(SyncState.SYNC, token);
        OfflineLog.offline.debug("ending delta sync");

        return token;
    }

    private void deltaSync(Element response) throws ServiceException {
        // make sure to handle deletes first, as tags can reuse ids
        Set<Integer> foldersToDelete = processLeafDeletes(response);

        // sync down metadata changes and note items that need to be downloaded in full
        StringBuilder contacts = null;
        Map<Integer, Integer> messages = null;
        for (Element change : response.listElements()) {
            int id = (int) change.getAttributeLong(MailService.A_ID);
            String type = change.getName();

            if (type.equals(MailService.E_TAG)) {
                // can't tell new tags from modified ones, so might as well go through the initial sync process
                new InitialSync(ombx).syncTag(change);
                continue;
            }

            int folderId = (id == Mailbox.ID_FOLDER_ROOT ? Mailbox.ID_FOLDER_ROOT : (int) change.getAttributeLong(MailService.A_FOLDER));
            boolean create = (change.getAttribute(MailService.A_FLAGS, null) == null);

            if (type.equals(MailService.E_MSG)) {
                if (create)
                    (messages == null ? messages = new HashMap<Integer,Integer>() : messages).put(id, folderId);
                else
                    syncMessage(change, folderId);
            } else if (type.equals(MailService.E_CONTACT)) {
                if (create)
                    (contacts == null ? contacts = new StringBuilder() : contacts.append(',')).append(id);
                else
                    syncContact(change, folderId);
            } else if (InitialSync.KNOWN_FOLDER_TYPES.contains(type)) {
                // can't tell new folders from modified ones, so might as well go through the initial sync process
                syncContainer(change, id);
            }
        }

        // for messages and contacts that are created or had their content modified, fetch new content
        if (messages != null) {
            for (Map.Entry<Integer,Integer> msg : messages.entrySet())
                new InitialSync(ombx).syncMessage(msg.getKey(), msg.getValue());
        }
        if (contacts != null) {
            for (Element eContact : InitialSync.fetchContacts(ombx, contacts.toString()).listElements())
                new InitialSync(ombx).syncContact(eContact, (int) eContact.getAttributeLong(MailService.A_FOLDER));
        }

        // delete any deleted folders, starting from the bottom of the tree
        if (foldersToDelete != null && !foldersToDelete.isEmpty()) {
            List<Folder> folders = ombx.getFolderById(sContext, Mailbox.ID_FOLDER_ROOT).getSubfolderHierarchy();
            Collections.reverse(folders);
            for (Folder folder : folders) {
                if (foldersToDelete.remove(folder.getId()))
                    ombx.deleteEmptyFolder(sContext, folder.getId());
            }
        }
    }

    private Set<Integer> processLeafDeletes(Element response) throws ServiceException {
        Element delement = response.getOptionalElement(MailService.E_DELETED);
        if (delement == null)
            return null;
        delement.detach();

        // sort the deleted items into a bucket of leaf nodes to delete now and a set of folders to delete later
        List<Integer> leafIds = new ArrayList<Integer>();
        Set<Integer> foldersToDelete = new HashSet<Integer>();

        // sort the deleted items into two sets: leaves and folders
        for (Element deltype : delement.listElements()) {
            byte type = SyncOperation.typeForElementName(deltype.getName());
            if (type == MailItem.TYPE_UNKNOWN || type == MailItem.TYPE_CONVERSATION)
                continue;
            boolean isFolder = InitialSync.KNOWN_FOLDER_TYPES.contains(deltype.getName());
            for (String idStr : deltype.getAttribute(MailService.A_IDS).split(","))
                (isFolder ? foldersToDelete : leafIds).add(Integer.valueOf(idStr));
        }

        // delete all the leaves now
        int idx = 0, ids[] = new int[leafIds.size()];
        for (int id : leafIds)
            ids[idx++] = id;
        ombx.delete(sContext, ids, MailItem.TYPE_UNKNOWN, null);
        OfflineLog.offline.debug("delta: deleted leaves: " + Arrays.toString(ids));

        // save the folder deletes for later
        return (foldersToDelete.isEmpty() ? null : foldersToDelete);
    }

    private void syncContainer(Element elt, int id) throws ServiceException {
        String type = elt.getName();
        if (type.equalsIgnoreCase(MailService.E_SEARCH))
            syncSearchFolder(elt, id);
        else if (type.equalsIgnoreCase(MailService.E_MOUNT))
            syncMountpoint(elt, id);
        else if (type.equalsIgnoreCase(MailService.E_FOLDER))
            syncFolder(elt, id);
    }

    void syncSearchFolder(Element elt, int id) throws ServiceException {
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION, -1);

        String query = elt.getAttribute(MailService.A_QUERY);
        String searchTypes = elt.getAttribute(MailService.A_SEARCH_TYPES);
        String sort = elt.getAttribute(MailService.A_SORTBY);

        synchronized (ombx) {
            // deal with the case where the referenced search folder doesn't exist
            Folder folder = getFolder(id);
            if (folder == null) {
                // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
                if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_SEARCHFOLDER))
                    return;
                // resolve any naming conflicts and actually create the folder
                if (resolveFolderConflicts(elt, id, MailItem.TYPE_SEARCHFOLDER, folder)) {
                    new InitialSync(ombx).syncSearchFolder(elt, id);
                    return;
                } else {
                    folder = getFolder(id);
                }
            }

            // if the search folder was moved/renamed locally, that trumps any changes made remotely
            resolveFolderConflicts(elt, id, MailItem.TYPE_SEARCHFOLDER, folder);

            int parentId = (int) elt.getAttributeLong(MailService.A_FOLDER);
            String name = elt.getAttribute(MailService.A_NAME);

            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_SEARCHFOLDER);
            ombx.renameFolder(sContext, id, parentId, name);
            if ((change_mask & Change.MODIFIED_QUERY) == 0)
                ombx.modifySearchFolder(sContext, id, query, searchTypes, sort);
            ombx.syncMetadata(sContext, id, MailItem.TYPE_SEARCHFOLDER, parentId, flags, 0, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_SEARCHFOLDER, date, mod_content, timestamp, changeId);

            OfflineLog.offline.debug("delta: updated search folder (" + id + "): " + name);
        }
    }

    void syncMountpoint(Element elt, int id) throws ServiceException {
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION, -1);

        synchronized (ombx) {
            // deal with the case where the referenced mountpoint doesn't exist
            Folder folder = getFolder(id);
            if (folder == null) {
                // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
                if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_MOUNTPOINT))
                    return;
                // resolve any naming conflicts and actually create the folder
                if (resolveFolderConflicts(elt, id, MailItem.TYPE_MOUNTPOINT, folder)) {
                    new InitialSync(ombx).syncMountpoint(elt, id);
                    return;
                } else {
                    folder = getFolder(id);
                }
            }

            // if the mountpoint was moved/renamed locally, that trumps any changes made remotely
            resolveFolderConflicts(elt, id, MailItem.TYPE_MOUNTPOINT, folder);

            int parentId = (int) elt.getAttributeLong(MailService.A_FOLDER);
            String name = elt.getAttribute(MailService.A_NAME);

            ombx.renameFolder(sContext, id, parentId, name);
            ombx.syncMetadata(sContext, id, MailItem.TYPE_MOUNTPOINT, parentId, flags, 0, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_MOUNTPOINT, date, mod_content, timestamp, changeId);

            OfflineLog.offline.debug("delta: updated mountpoint (" + id + "): " + name);
        }
    }

    void syncFolder(Element elt, int id) throws ServiceException {
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null)) & ~Flag.BITMASK_UNREAD;
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        String url = elt.getAttribute(MailService.A_URL, null);

        ACL acl = new InitialSync(ombx).parseACL(elt.getOptionalElement(MailService.E_ACL));

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION, -1);

        synchronized (ombx) {
            // deal with the case where the referenced folder doesn't exist
            Folder folder = getFolder(id);
            if (folder == null) {
                // if it's been locally deleted but not pushed to the server yet, just return and let the delete happen later
                if (ombx.isPendingDelete(sContext, id, MailItem.TYPE_FOLDER))
                    return;
                // resolve any naming conflicts and actually create the folder
                if (resolveFolderConflicts(elt, id, MailItem.TYPE_FOLDER, folder)) {
                    new InitialSync(ombx).syncFolder(elt, id);
                    return;
                } else {
                    folder = getFolder(id);
                }
            }

            // if the folder was moved/renamed locally, that trumps any changes made remotely
            resolveFolderConflicts(elt, id, MailItem.TYPE_FOLDER, folder);

            int parentId = (id == Mailbox.ID_FOLDER_ROOT) ? id : (int) elt.getAttributeLong(MailService.A_FOLDER);
            String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : elt.getAttribute(MailService.A_NAME);

            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_FOLDER);
            if (id != Mailbox.ID_FOLDER_ROOT)
                ombx.renameFolder(sContext, id, parentId, name);
            // XXX: do we need to sync if the folder has perms but the new ACL is empty?
            if ((change_mask & Change.MODIFIED_ACL) == 0 && acl != null)
                ombx.setPermissions(sContext, id, acl);
            if ((change_mask & Change.MODIFIED_URL) == 0)
                ombx.setFolderUrl(sContext, id, url);
            // don't care about current feed syncpoint; sync can't be done offline
            ombx.syncMetadata(sContext, id, MailItem.TYPE_FOLDER, parentId, flags, 0, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_FOLDER, date, mod_content, timestamp, changeId);

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
        int parentId = (id == Mailbox.ID_FOLDER_ROOT) ? id : (int) elt.getAttributeLong(MailService.A_FOLDER);
        if ((change_mask & Change.MODIFIED_FOLDER) != 0) {
            parentId = local.getFolderId();  elt.addAttribute(MailService.A_FOLDER, parentId);
        }

        String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : elt.getAttribute(MailService.A_NAME);
        if ((change_mask & Change.MODIFIED_NAME) != 0 && !mSyncRenames.contains(id)) {
            name = local.getName();  elt.addAttribute(MailService.A_NAME, name);
        }

        // if the parent folder doesn't exist or is of an incompatible type, default to using the top-level user folder as the container
        Folder parent = getFolder(parentId);
        if (parent == null || !parent.canContain(type)) {
            parentId = Mailbox.ID_FOLDER_USER_ROOT;  parent = getFolder(parentId);
            elt.addAttribute(MailService.A_FOLDER, parentId).addAttribute(InitialSync.A_RELOCATED, true);
        }

        Folder conflict = parent.findSubfolder(name);
        if (conflict != null && conflict.getId() != id) {
            int conflict_mask = ombx.getChangeMask(sContext, conflict.getId(), conflict.getType());

            String uuid = '{' + UUID.randomUUID().toString() + '}', newName;
            if (name.length() + uuid.length() > Folder.MAX_FOLDER_LENGTH)
                newName = name.substring(0, Folder.MAX_FOLDER_LENGTH - uuid.length()) + uuid;
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
                elt.addAttribute(MailService.A_NAME, name).addAttribute(InitialSync.A_RELOCATED, true);
            } else {
                // if there's a folder naming conflict within the target folder, usually push the local folder out of the way
                ombx.renameFolder(null, conflict.getId(), newName);
                if ((conflict_mask & Change.MODIFIED_NAME) == 0)
                    mSyncRenames.add(conflict.getId());
            }
        }

        // if conflicts have forced us to deviate from the specified sync, update the local store such that these changes are pushed during the next sync
        if (local != null && elt.getAttributeBool(InitialSync.A_RELOCATED, false))
            ombx.renameFolder(null, id, parentId, name);

        return true;
    }

    private boolean isCompatibleFolder(Folder folder, Element elt, byte type) {
        if (type != folder.getType())
            return false;

        if (type == MailItem.TYPE_FOLDER)
           return folder.getDefaultView() == MailItem.getTypeForName(elt.getAttribute(MailService.A_DEFAULT_VIEW, null));
        else
            return false;
    }

    void syncTag(Element elt) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailService.A_ID);
        try {
            // make sure that the tag we're delta-syncing actually exists
            ombx.getTagById(sContext, id);
        } catch (MailServiceException.NoSuchItemException nsie) {
            new InitialSync(ombx).syncTag(elt);
            return;
        }

        String name = elt.getAttribute(MailService.A_NAME);
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION);

        synchronized (ombx) {
            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_TAG);
            // FIXME: if FOO was renamed BAR and BAR was renamed FOO, this will break
            if ((change_mask & Change.MODIFIED_NAME) == 0)
                ombx.renameTag(sContext, id, name);
            if ((change_mask & Change.MODIFIED_COLOR) == 0)
                ombx.setColor(sContext, id, MailItem.TYPE_TAG, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_TAG, date, mod_content, timestamp, changeId);
        }
        OfflineLog.offline.debug("delta: updated tag (" + id + "): " + name);
    }

    private Tag getTag(int id) throws ServiceException {
        try {
            return ombx.getTagById(sContext, id);
        } catch (MailServiceException.NoSuchItemException nsie) {
            return null;
        }
    }

    void syncContact(Element elt, int folderId) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailService.A_ID);
        Contact cn = null;
        try {
            // make sure that the contact we're delta-syncing actually exists
            cn = ombx.getContactById(sContext, id);
        } catch (MailServiceException.NoSuchItemException nsie) {
            new InitialSync(ombx).syncContact(elt, folderId);
            return;
        }

        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));
        long tags = Tag.tagsToBitmask(elt.getAttribute(MailService.A_TAGS, null));

        Map<String, String> fields = new HashMap<String, String>();
        for (Element eField : elt.listElements())
            fields.put(eField.getAttribute(Element.XMLElement.A_ATTR_NAME), eField.getText());

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION);

        synchronized (ombx) {
            int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_CONTACT);
            if ((change_mask & Change.MODIFIED_CONTENT) == 0 && !fields.isEmpty())
                ombx.modifyContact(sContext, id, fields, true);
            ombx.syncMetadata(sContext, id, MailItem.TYPE_CONTACT, folderId, flags, tags, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_CONTACT, date, mod_content, timestamp, changeId);
        }
        OfflineLog.offline.debug("delta: updated contact (" + id + "): " + cn.getFileAsString());
    }

    void syncMessage(Element elt, int folderId) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailService.A_ID);
        Message msg = null;
        try {
            // make sure that the message we're delta-syncing actually exists
            msg = ombx.getMessageById(sContext, id);
        } catch (MailServiceException.NoSuchItemException nsie) {
            new InitialSync(ombx).syncMessage(id, folderId);
            return;
        }

        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));
        long tags = Tag.tagsToBitmask(elt.getAttribute(MailService.A_TAGS, null));
        int convId = (int) elt.getAttributeLong(MailService.A_CONV_ID);

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION);

        // double-check to make sure that it's just a metadata change
//        if (mod_content != msg.getSavedSequence() || date != msg.getDate() / 1000) {
//            // content changed; must re-download body
//            new InitialSync(ombx).syncMessage(id, folderId);
//            return;
//        }

        synchronized (ombx) {
            ombx.setConversationId(sContext, id, convId <= 0 ? -id : convId);
            ombx.syncMetadata(sContext, id, MailItem.TYPE_MESSAGE, folderId, flags, tags, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_MESSAGE, date, mod_content, timestamp, changeId);
        }
        OfflineLog.offline.debug("delta: updated message (" + id + "): " + msg.getSubject());
    }
}
