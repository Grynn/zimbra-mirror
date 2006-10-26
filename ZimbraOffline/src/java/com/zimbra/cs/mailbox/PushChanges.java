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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.zimbra.cs.mailbox.MailItem.TypedIdList;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.mailbox.OfflineMailbox.SyncState;
import com.zimbra.cs.mime.Mime;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.service.ServiceException;
import com.zimbra.cs.service.mail.ItemAction;
import com.zimbra.cs.service.mail.MailService;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.cs.zclient.ZMailbox;
import com.zimbra.soap.Element;
import com.zimbra.soap.SoapFaultException;

public class PushChanges {

    /** The set of message change types that we want to propagate to the server. */
    static final int MESSAGE_CHANGES = Change.MODIFIED_UNREAD | Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS |
                                       Change.MODIFIED_FOLDER | Change.MODIFIED_COLOR | Change.MODIFIED_CONTENT;
    /** The set of contact change types that we want to propagate to the server. */
    static final int CONTACT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_TAGS | Change.MODIFIED_FOLDER |
                                       Change.MODIFIED_COLOR | Change.MODIFIED_CONTENT;
    /** The set of folder change types that we want to propagate to the server. */
    static final int FOLDER_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME |
                                      Change.MODIFIED_COLOR | Change.MODIFIED_URL    | Change.MODIFIED_ACL;
    /** The set of search folder change types that we want to propagate to the server. */
    static final int SEARCH_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME |
                                      Change.MODIFIED_COLOR | Change.MODIFIED_QUERY;
    /** The set of mountpoint change types that we want to propagate to the server. */
    static final int MOUNT_CHANGES = Change.MODIFIED_FLAGS | Change.MODIFIED_FOLDER | Change.MODIFIED_NAME |
                                     Change.MODIFIED_COLOR;
    /** The set of tag change types that we want to propagate to the server. */
    static final int TAG_CHANGES = Change.MODIFIED_NAME | Change.MODIFIED_COLOR;

    private static final byte[] PUSH_LEAF_TYPES = new byte[] {
        MailItem.TYPE_TAG, MailItem.TYPE_CONTACT, MailItem.TYPE_MESSAGE
    };
    static final Set<Byte> PUSH_TYPES_SET = new HashSet<Byte>(Arrays.asList(
        MailItem.TYPE_FOLDER, MailItem.TYPE_SEARCHFOLDER, MailItem.TYPE_MOUNTPOINT, MailItem.TYPE_TAG, MailItem.TYPE_CONTACT, MailItem.TYPE_MESSAGE
    ));

    private static final OfflineContext sContext = new OfflineContext();

    public static boolean sync(OfflineMailbox ombx) throws ServiceException {
        int limit;
        TypedIdList changes, tombstones;

        synchronized (ombx) {
            limit = ombx.getLastChangeID();
            tombstones = ombx.getTombstoneSet(0);
            changes = ombx.getLocalChanges(sContext);
        }

        if (changes.isEmpty() && tombstones.isEmpty())
            return false;

        OfflineLog.offline.debug("starting change push");
        ombx.setSyncState(SyncState.PUSH);
        pushChanges(ombx, changes, tombstones, limit);
        ombx.setSyncState(SyncState.SYNC);
        OfflineLog.offline.debug("ending change push");

        return true;
    }

    private static void pushChanges(OfflineMailbox ombx, TypedIdList changes, TypedIdList tombstones, int limit) throws ServiceException {
        // because tags reuse IDs, we need to do tag deletes before any other changes (especially tag creates)
        List<Integer> tagDeletes = tombstones.getIds(MailItem.TYPE_TAG);
        if (tagDeletes != null && !tagDeletes.isEmpty()) {
            Element request = new Element.XMLElement(MailService.TAG_ACTION_REQUEST);
            request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_HARD_DELETE).addAttribute(MailService.A_ID, concatenateIds(tagDeletes));
            ombx.sendRequest(request);

            tombstones.remove(MailItem.TYPE_TAG);
        }

        // process pending "sent" messages
        if (ombx.getFolderById(sContext, OfflineMailbox.ID_FOLDER_OUTBOX).getSize() > 0)
            sendPendingMessages(ombx, changes);

        // do folder ops top-down so that we don't get dinged when folders switch places
        if (!changes.isEmpty()) {
            if (changes.getIds(MailItem.TYPE_FOLDER) != null || changes.getIds(MailItem.TYPE_SEARCHFOLDER) != null || changes.getIds(MailItem.TYPE_MOUNTPOINT) != null) {
                for (Folder folder : ombx.getFolderById(sContext, Mailbox.ID_FOLDER_ROOT).getSubfolderHierarchy()) {
                    if (changes.remove(folder.getType(), folder.getId())) {
                        switch (folder.getType()) {
                            case MailItem.TYPE_SEARCHFOLDER:  syncSearchFolder(ombx, folder.getId());  break;
                            case MailItem.TYPE_MOUNTPOINT:    syncMountpoint(ombx, folder.getId());    break;
                            case MailItem.TYPE_FOLDER:        syncFolder(ombx, folder.getId());        break;
                        }
                    }
                }
                changes.remove(MailItem.TYPE_FOLDER);  changes.remove(MailItem.TYPE_SEARCHFOLDER);  changes.remove(MailItem.TYPE_MOUNTPOINT);
            }
        }

        // do tag ops -- INCLUDING TAG DELETES -- next so they're all set up for 
        if (!changes.isEmpty() && changes.getIds(MailItem.TYPE_TAG) != null) {
        }

        // modifies must come after folder and tag creates so that move/tag ops can succeed
        if (!changes.isEmpty()) {
            for (byte type : PUSH_LEAF_TYPES) {
                List<Integer> ids = changes.getIds(type);
                if (ids == null)
                    continue;
                for (int id : ids) {
                    switch (type) {
//                        case MailItem.TYPE_SEARCHFOLDER:  syncSearchFolder(ombx, id);  break;
//                        case MailItem.TYPE_MOUNTPOINT:    syncMountpoint(ombx, id);    break;
//                        case MailItem.TYPE_FOLDER:        syncFolder(ombx, id);        break;
                        case MailItem.TYPE_TAG:      syncTag(ombx, id);           break;
                        case MailItem.TYPE_CONTACT:  syncContact(ombx, id);       break;
                        case MailItem.TYPE_MESSAGE:  syncMessage(ombx, id);       break;
                    }
                }
            }
        }

        // folder deletes need to come after moves are processed, else we'll be deleting items we shouldn't
        if (!tombstones.isEmpty()) {
            String ids = concatenateIds(tombstones.getAll());
            Element request = new Element.XMLElement(MailService.ITEM_ACTION_REQUEST);
            request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_HARD_DELETE).addAttribute(MailService.A_ID, ids);
            ombx.sendRequest(request);
            OfflineLog.offline.debug("push: pushed deletes: [" + ids + ']');

            ombx.clearTombstones(sContext, limit);
        }
    }

    private static void sendPendingMessages(OfflineMailbox ombx, TypedIdList creates) throws ServiceException {
        int[] pendingSends = ombx.listItemIds(sContext, MailItem.TYPE_MESSAGE, OfflineMailbox.ID_FOLDER_OUTBOX);
        if (pendingSends == null || pendingSends.length == 0)
            return;

        for (int id : pendingSends) {
            try {
                Message msg = ombx.getMessageById(sContext, id);
                String uploadId = uploadMessage(ombx, msg.getMessageContent());
                Element request = new Element.XMLElement(MailService.SEND_MSG_REQUEST);
                Element m = request.addElement(MailService.E_MSG).addAttribute(MailService.A_ATTACHMENT_ID, uploadId);
                if (msg.getDraftOrigId() > 0)
                    m.addAttribute(MailService.A_ORIG_ID, msg.getDraftOrigId()).addAttribute(MailService.A_REPLY_TYPE, msg.getDraftReplyType());
                ombx.sendRequest(request);
                OfflineLog.offline.debug("push: sent pending mail (" + id + "): " + msg.getSubject());

                ombx.delete(sContext, id, MailItem.TYPE_MESSAGE);
                OfflineLog.offline.debug("push: deleted pending draft (" + id + ')');

                creates.remove(MailItem.TYPE_MESSAGE, id);
            } catch (NoSuchItemException nsie) {
                OfflineLog.offline.debug("push: ignoring deleted pending mail (" + id + ")");
            }
        }
    }

    private static String uploadMessage(OfflineMailbox ombx, byte[] content) throws ServiceException {
        ZMailbox.Options options = new ZMailbox.Options(ombx.getAuthToken(), ombx.getSoapUri());
        options.setNoSession(true);
        ZMailbox zmbox = ZMailbox.getMailbox(options);
        return zmbox.uploadAttachment("message", content, Mime.CT_MESSAGE_RFC822, 5000);
    }

    private static String concatenateIds(List<Integer> ids) {
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (Integer id : ids) {
            if (i++ != 0)
                sb.append(',');
            sb.append(id);
        }
        return sb.toString();
    }

    private static boolean syncSearchFolder(OfflineMailbox ombx, int id) throws ServiceException {
        Element request = new Element.XMLElement(MailService.FOLDER_ACTION_REQUEST);
        Element action = request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailService.A_ID, id);

        int flags, parentId, newId = Mailbox.ID_AUTO_INCREMENT;
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
                request = new Element.XMLElement(MailService.CREATE_SEARCH_FOLDER_REQUEST);
                action = request.addElement(MailService.E_SEARCH);
                create = true;
            }
            if (create || (mask & Change.MODIFIED_FLAGS) != 0)
                action.addAttribute(MailService.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailService.A_FOLDER, parentId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailService.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_NAME) != 0)
                action.addAttribute(MailService.A_NAME, name);
            if (create || (mask & Change.MODIFIED_QUERY) != 0)
                action.addAttribute(MailService.A_QUERY, query).addAttribute(MailService.A_SEARCH_TYPES, searchTypes).addAttribute(MailService.A_SORT_FIELD, sort);
        }

        try {
            Element response = ombx.sendRequest(request);
            if (create) {
                newId = (int) response.getElement(MailService.E_SEARCH).getAttributeLong(MailService.A_ID);
                OfflineLog.offline.debug("push: created search folder (" + newId + ") from local (" + id + "): " + name);
            } else {
                OfflineLog.offline.debug("push: updated search folder (" + id + "): " + name);
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_FOLDER))
                throw sfe;
            OfflineLog.offline.info("push: remote search folder " + id + " has been deleted; skipping");
            return true;
        }

        // make sure the old item matches the new item...
        if (create) {
            ombx.renumberItem(sContext, id, MailItem.TYPE_SEARCHFOLDER, newId);
            id = newId;
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

    private static boolean syncMountpoint(OfflineMailbox ombx, int id) throws ServiceException {
        Element request = new Element.XMLElement(MailService.FOLDER_ACTION_REQUEST);
        Element action = request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailService.A_ID, id);

        int flags, parentId, newId = Mailbox.ID_AUTO_INCREMENT;
        byte color;
        String name;
        boolean create = false;
        synchronized (ombx) {
            Mountpoint mpt = ombx.getMountpointById(sContext, id);
            name = mpt.getName();    flags = mpt.getInternalFlagBitmask();
            color = mpt.getColor();  parentId = mpt.getFolderId();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_MOUNTPOINT);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new mountpoint; need to push to the server
                request = new Element.XMLElement(MailService.CREATE_MOUNTPOINT_REQUEST);
                action = request.addElement(MailService.E_MOUNT).addAttribute(MailService.A_REMOTE_ID, mpt.getRemoteId())
                                .addAttribute(MailService.A_ZIMBRA_ID, mpt.getOwnerId())
                                .addAttribute(MailService.A_DEFAULT_VIEW, MailItem.getNameForType(mpt.getDefaultView()));
                create = true;
            }
            if (create || (mask & Change.MODIFIED_FLAGS) != 0)
                action.addAttribute(MailService.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailService.A_FOLDER, parentId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailService.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_NAME) != 0)
                action.addAttribute(MailService.A_NAME, name);
        }

        try {
            Element response = ombx.sendRequest(request);
            if (create) {
                newId = (int) response.getElement(MailService.E_MOUNT).getAttributeLong(MailService.A_ID);
                OfflineLog.offline.debug("push: created mountpoint (" + newId + ") from local (" + id + "): " + name);
            } else {
                OfflineLog.offline.debug("push: updated mountpoint (" + id + "): " + name);
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_FOLDER))
                throw sfe;
            OfflineLog.offline.info("push: remote mountpoint " + id + " has been deleted; skipping");
            return true;
        }

        // make sure the old item matches the new item...
        if (create) {
            ombx.renumberItem(sContext, id, MailItem.TYPE_MOUNTPOINT, newId);
            id = newId;
        }

        synchronized (ombx) {
            Mountpoint mpt = ombx.getMountpointById(sContext, id);
            // check to see if the mountpoint was changed while we were pushing the update...
            int mask = 0;
            if (flags != mpt.getInternalFlagBitmask())  mask |= Change.MODIFIED_FLAGS;
            if (parentId != mpt.getFolderId())          mask |= Change.MODIFIED_NAME;
            if (color != mpt.getColor())                mask |= Change.MODIFIED_COLOR;
            if (!name.equals(mpt.getName()))            mask |= Change.MODIFIED_NAME;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.TYPE_MOUNTPOINT, mask);
            return (mask == 0);
        }
    }

    private static boolean syncFolder(OfflineMailbox ombx, int id) throws ServiceException {
        Element request = new Element.XMLElement(MailService.FOLDER_ACTION_REQUEST);
        Element action = request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailService.A_ID, id);

        int flags, parentId, newId = Mailbox.ID_AUTO_INCREMENT;
        byte color;
        String name, url;
        boolean create = false;
        synchronized (ombx) {
            Folder folder = ombx.getFolderById(sContext, id);
            name = folder.getName();  parentId = folder.getFolderId();  flags = folder.getInternalFlagBitmask();
            url = folder.getUrl();    color = folder.getColor();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_FOLDER);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new folder; need to push to the server
                request = new Element.XMLElement(MailService.CREATE_FOLDER_REQUEST);
                action = request.addElement(MailService.E_FOLDER).addAttribute(MailService.A_DEFAULT_VIEW, MailItem.getNameForType(folder.getDefaultView()));
                create = true;
            }
            if (create || (mask & Change.MODIFIED_FLAGS) != 0)
                action.addAttribute(MailService.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailService.A_FOLDER, parentId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailService.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_NAME) != 0)
                action.addAttribute(MailService.A_NAME, name);
            if (create || (mask & Change.MODIFIED_URL) != 0)
                action.addAttribute(MailService.A_URL, url);
            // FIXME: does not support ACL sync at all...
        }

        try {
            Element response = ombx.sendRequest(request);
            if (create) {
                newId = (int) response.getElement(MailService.E_FOLDER).getAttributeLong(MailService.A_ID);
                OfflineLog.offline.debug("push: created folder (" + newId + ") from local (" + id + "): " + name);
            } else {
                OfflineLog.offline.debug("push: updated folder (" + id + "): " + name);
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_FOLDER))
                throw sfe;
            OfflineLog.offline.info("push: remote folder " + id + " has been deleted; skipping");
            return true;
        }

        // make sure the old item matches the new item...
        if (create) {
            ombx.renumberItem(sContext, id, MailItem.TYPE_FOLDER, newId);
            id = newId;
        }

        synchronized (ombx) {
            Folder folder = ombx.getFolderById(sContext, id);
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

    private static boolean syncTag(OfflineMailbox ombx, int id) throws ServiceException {
        Element request = new Element.XMLElement(MailService.TAG_ACTION_REQUEST);
        Element action = request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailService.A_ID, id);

        int newId = Mailbox.ID_AUTO_INCREMENT;
        byte color;
        String name;
        boolean create = false;
        synchronized (ombx) {
            Tag tag = ombx.getTagById(sContext, id);
            color = tag.getColor();  name = tag.getName();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_TAG);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new tag; need to push to the server
                request = new Element.XMLElement(MailService.CREATE_TAG_REQUEST);
                action = request.addElement(MailService.E_TAG);
                create = true;
            }
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailService.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_NAME) != 0)
                action.addAttribute(MailService.A_NAME, name);
        }

        try {
            Element response = ombx.sendRequest(request);
            if (create) {
                newId = (int) response.getElement(MailService.E_TAG).getAttributeLong(MailService.A_ID);
                OfflineLog.offline.debug("push: created tag (" + newId + ") from local (" + id + "): " + name);
            } else {
                OfflineLog.offline.debug("push: updated tag (" + id + "): " + name);
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_TAG))
                throw sfe;
            OfflineLog.offline.info("push: remote tag " + id + " has been deleted; skipping");
            return true;
        }

        // make sure the old item matches the new item...
        if (create) {
            ombx.renumberItem(sContext, id, MailItem.TYPE_FOLDER, newId);
            id = newId;
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

    private static boolean syncContact(OfflineMailbox ombx, int id) throws ServiceException {
        Element request = new Element.XMLElement(MailService.CONTACT_ACTION_REQUEST);
        Element action = request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailService.A_ID, id);

        int flags, folderId, newId = Mailbox.ID_AUTO_INCREMENT, newRevision = -1;
        long date, tags;
        byte color;
        boolean create = false;
        synchronized (ombx) {
            Contact cn = ombx.getContactById(sContext, id);
            date = cn.getDate();    flags = cn.getFlagBitmask();  tags = cn.getTagBitmask();
            color = cn.getColor();  folderId = cn.getFolderId();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_CONTACT);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new contact; need to push to the server
                request = new Element.XMLElement(MailService.CREATE_CONTACT_REQUEST);
                action = request.addElement(MailService.E_CONTACT);
                create = true;
            }
            if (create || (mask & Change.MODIFIED_FLAGS) != 0)
                action.addAttribute(MailService.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_TAGS) != 0)
                action.addAttribute(MailService.A_TAGS, cn.getTagString());
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailService.A_FOLDER, folderId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailService.A_COLOR, color);
            if (create || (mask & Change.MODIFIED_CONTENT) != 0) {
                for (Map.Entry<String, String> field : cn.getFields().entrySet()) {
                    String name = field.getKey(), value = field.getValue();
                    if (name == null || name.trim().equals("") || value == null || value.equals(""))
                        continue;
                    action.addAttribute(name, value, Element.DISP_ELEMENT);
                }
            }
        }

        try {
            Element response = ombx.sendRequest(request);
            if (create) {
                newId = (int) response.getElement(MailService.E_CONTACT).getAttributeLong(MailService.A_ID);
                newRevision = (int) response.getElement(MailService.E_CONTACT).getAttributeLong(MailService.A_REVISION, -1);
                OfflineLog.offline.debug("push: created contact (" + newId + ") from local (" + id + ")");
            } else {
                OfflineLog.offline.debug("push: updated contact (" + id + ")");
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_CONTACT))
                throw sfe;
            OfflineLog.offline.info("push: remote contact " + id + " has been deleted; skipping");
            return true;
        }

        // make sure the old item matches the new item...
        if (create) {
            ombx.renumberItem(sContext, id, MailItem.TYPE_CONTACT, newId, newRevision);
            id = newId;
        }

        synchronized (ombx) {
            Contact cn = ombx.getContactById(sContext, id);
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

    private static boolean syncMessage(OfflineMailbox ombx, int id) throws ServiceException {
        Element request = new Element.XMLElement(MailService.MSG_ACTION_REQUEST);
        Element action = request.addElement(MailService.E_ACTION).addAttribute(MailService.A_OPERATION, ItemAction.OP_UPDATE).addAttribute(MailService.A_ID, id);

        int flags, folderId, newId = Mailbox.ID_AUTO_INCREMENT, newRevision = -1;
        long tags;
        String digest;
        byte color, newContent[] = null;
        boolean create = false;
        synchronized (ombx) {
            Message msg = ombx.getMessageById(sContext, id);
            digest = msg.getDigest();  flags = msg.getFlagBitmask();  tags = msg.getTagBitmask();
            color = msg.getColor();    folderId = msg.getFolderId();

            int mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_MESSAGE);
            if ((mask & Change.MODIFIED_CONFLICT) != 0) {
                // this is a new message; need to push to the server
                request = new Element.XMLElement(msg.isDraft() ? MailService.SAVE_DRAFT_REQUEST : MailService.ADD_MSG_REQUEST);
                action = request.addElement(MailService.E_MSG);
                if (msg.isDraft() && msg.getDraftOrigId() > 0)
                    action.addAttribute(MailService.A_REPLY_TYPE, msg.getDraftReplyType()).addAttribute(MailService.A_ORIG_ID, msg.getDraftOrigId());
                newContent = msg.getMessageContent();
                create = true;
            } else if ((mask & Change.MODIFIED_CONTENT) != 0) {
                // for draft message content changes, need to go through the SaveDraft door instead of the MsgAction door
                if (!msg.isDraft())
                    throw MailServiceException.IMMUTABLE_OBJECT(id);
                request = new Element.XMLElement(MailService.SAVE_DRAFT_REQUEST);
                action = request.addElement(MailService.E_MSG).addAttribute(MailService.A_ID, id);
                newContent = msg.getMessageContent();
            }
            if (create || (mask & Change.MODIFIED_FLAGS | Change.MODIFIED_UNREAD) != 0)
                action.addAttribute(MailService.A_FLAGS, Flag.bitmaskToFlags(flags));
            if (create || (mask & Change.MODIFIED_TAGS) != 0)
                action.addAttribute(MailService.A_TAGS, msg.getTagString());
            if (create || (mask & Change.MODIFIED_FOLDER) != 0)
                action.addAttribute(MailService.A_FOLDER, folderId);
            if (create || (mask & Change.MODIFIED_COLOR) != 0)
                action.addAttribute(MailService.A_COLOR, color);
        }

        if (newContent != null) {
            // upload draft message body to the remote FileUploadServlet, then use the returned attachment id to save draft
            String attachId = uploadMessage(ombx, newContent);
            action.addAttribute(MailService.A_ATTACHMENT_ID, attachId);
        }

        try {
            Element response = ombx.sendRequest(request);
            if (create) {
                newId = (int) response.getElement(MailService.E_MSG).getAttributeLong(MailService.A_ID);
                newRevision = (int) response.getElement(MailService.E_MSG).getAttributeLong(MailService.A_REVISION, -1);
                OfflineLog.offline.debug("push: created message (" + newId + ") from local (" + id + ")");
            } else {
                OfflineLog.offline.debug("push: updated message (" + id + ")");
            }
        } catch (SoapFaultException sfe) {
            if (!sfe.getCode().equals(MailServiceException.NO_SUCH_MSG))
                throw sfe;
            OfflineLog.offline.info("push: remote message " + id + " has been deleted; skipping");
            return true;
        }

        // make sure the old item matches the new item...
        if (create) {
            ombx.renumberItem(sContext, id, MailItem.TYPE_MESSAGE, newId, newRevision);
            id = newId;
        }

        synchronized (ombx) {
            Message msg = ombx.getMessageById(sContext, id);
            // check to see if the contact was changed while we were pushing the update...
            int mask = 0;
            if (flags != msg.getFlagBitmask())    mask |= Change.MODIFIED_FLAGS;
            if (tags != msg.getTagBitmask())      mask |= Change.MODIFIED_TAGS;
            if (folderId != msg.getFolderId())    mask |= Change.MODIFIED_FOLDER;
            if (color != msg.getColor())          mask |= Change.MODIFIED_COLOR;
            if (!digest.equals(msg.getDigest()))  mask |= Change.MODIFIED_CONTENT;

            // update or clear the change bitmask
            ombx.setChangeMask(sContext, id, MailItem.TYPE_MESSAGE, mask);
            return (mask == 0);
        }
    }
}
