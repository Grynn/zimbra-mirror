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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.commons.httpclient.Header;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.Pair;
import com.zimbra.cs.mailbox.OfflineMailbox.OfflineContext;
import com.zimbra.cs.mailbox.OfflineMailbox.SyncState;
import com.zimbra.cs.mime.ParsedMessage;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.redolog.op.CreateContact;
import com.zimbra.cs.redolog.op.CreateFolder;
import com.zimbra.cs.redolog.op.CreateMessage;
import com.zimbra.cs.redolog.op.CreateMountpoint;
import com.zimbra.cs.redolog.op.CreateSavedSearch;
import com.zimbra.cs.redolog.op.CreateTag;
import com.zimbra.cs.redolog.op.SaveDraft;
import com.zimbra.cs.service.UserServlet;
import com.zimbra.cs.service.formatter.SyncFormatter;
import com.zimbra.cs.service.mail.FolderAction;
import com.zimbra.cs.service.mail.MailService;
import com.zimbra.cs.session.PendingModifications.Change;
import com.zimbra.soap.Element;

public class InitialSync {

    static final String A_RELOCATED = "relocated";

    private static final OfflineContext sContext = new OfflineContext();

    private final OfflineMailbox ombx;

    InitialSync(OfflineMailbox mbox) {
        ombx = mbox;
    }


    public static int sync(OfflineMailbox ombx) throws ServiceException {
        return new InitialSync(ombx).sync();
    }

    public int sync() throws ServiceException {
        Element request = new Element.XMLElement(MailService.SYNC_REQUEST);
        Element response = ombx.sendRequest(request);
        int token = (int) response.getAttributeLong(MailService.A_TOKEN);

        OfflineLog.offline.debug("starting initial sync");
        ombx.setSyncState(SyncState.INITIAL);
        initialFolderSync(response.getElement(MailService.E_FOLDER));
        ombx.setSyncState(SyncState.SYNC, token);
        OfflineLog.offline.debug("ending initial sync");

        return token;
    }

    static final Set<String> KNOWN_FOLDER_TYPES = new HashSet<String>(Arrays.asList(
            MailService.E_FOLDER, MailService.E_SEARCH, MailService.E_MOUNT
    ));

    private void initialFolderSync(Element elt) throws ServiceException {
        int folderId = (int) elt.getAttributeLong(MailService.A_ID);

        // first, sync the container itself
        syncContainer(elt, folderId);

        // next, sync the leaf-node contents
        if (elt.getName().equals(MailService.E_FOLDER)) {
            if (folderId == Mailbox.ID_FOLDER_TAGS) {
                for (Element eTag : elt.listElements(MailService.E_TAG))
                    syncTag(eTag);
                return;
            }

            Element eMessageIds = elt.getOptionalElement(MailService.E_MSG);
            if (eMessageIds != null) {
                for (String msgId : eMessageIds.getAttribute(MailService.A_IDS).split(","))
                    syncMessage(Integer.parseInt(msgId), folderId);
            }

            Element eContactIds = elt.getOptionalElement(MailService.E_CONTACT);
            if (eContactIds != null) {
                String ids = eContactIds.getAttribute(MailService.A_IDS);
                // FIXME: if a contact is deleted between sync and here, this will throw an exception
                for (Element eContact : fetchContacts(ombx, ids).listElements())
                    syncContact(eContact, folderId);
            }
        }

        // finally, sync the children
        for (Element child : elt.listElements()) {
            if (KNOWN_FOLDER_TYPES.contains(child.getName()))
                initialFolderSync(child);
        }
    }

    public int resume() throws ServiceException {
        Element request = new Element.XMLElement(MailService.SYNC_REQUEST);
        Element response = ombx.sendRequest(request);
        int token = (int) response.getAttributeLong(MailService.A_TOKEN);

        OfflineLog.offline.debug("resuming initial sync");
        Map<Integer,Folder> folders = new LinkedHashMap<Integer,Folder>();
        for (Folder folder : ombx.getFolderById(sContext, Mailbox.ID_FOLDER_ROOT).getSubfolderHierarchy())
            folders.put(folder.getId(), folder);
        ombx.setSyncState(SyncState.INITIAL);
        resumeFolderSync(response.getElement(MailService.E_FOLDER), folders);
        ombx.setSyncState(SyncState.SYNC, token);
        OfflineLog.offline.debug("ending initial sync");

        return token;
    }

    private void resumeFolderSync(Element elt, Map<Integer,Folder> folders) throws ServiceException {
        int folderId = (int) elt.getAttributeLong(MailService.A_ID);

        // first, sync the container itself
        syncContainer(elt, folderId);

        // next, sync the leaf-node contents
        if (elt.getName().equals(MailService.E_FOLDER)) {
            if (folderId == Mailbox.ID_FOLDER_TAGS) {
                for (Element eTag : elt.listElements(MailService.E_TAG))
                    syncTag(eTag);
                return;
            }

            Element eMessageIds = elt.getOptionalElement(MailService.E_MSG);
            if (eMessageIds != null) {
                for (String msgId : eMessageIds.getAttribute(MailService.A_IDS).split(","))
                    syncMessage(Integer.parseInt(msgId), folderId);
            }

            Element eContactIds = elt.getOptionalElement(MailService.E_CONTACT);
            if (eContactIds != null) {
                String ids = eContactIds.getAttribute(MailService.A_IDS);
                // FIXME: if a contact is deleted between sync and here, this will throw an exception
                for (Element eContact : fetchContacts(ombx, ids).listElements())
                    syncContact(eContact, folderId);
            }
        }

        // finally, sync the children
        for (Element child : elt.listElements()) {
            if (KNOWN_FOLDER_TYPES.contains(child.getName()))
                resumeFolderSync(child, folders);
        }
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
        int parentId = (int) elt.getAttributeLong(MailService.A_FOLDER);
        String name = elt.getAttribute(MailService.A_NAME);
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION, -1);

        String query = elt.getAttribute(MailService.A_QUERY);
        String searchTypes = elt.getAttribute(MailService.A_SEARCH_TYPES);
        String sort = elt.getAttribute(MailService.A_SORTBY);

        boolean relocated = elt.getAttributeBool(A_RELOCATED, false);

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
            new DeltaSync(ombx).syncSearchFolder(elt, id);
        }
    }

    void syncMountpoint(Element elt, int id) throws ServiceException {
        int parentId = (int) elt.getAttributeLong(MailService.A_FOLDER);
        String name = elt.getAttribute(MailService.A_NAME);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        byte view = MailItem.getTypeForName(elt.getAttribute(MailService.A_DEFAULT_VIEW, null));

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION, -1);

        String zid = elt.getAttribute(MailService.A_ZIMBRA_ID);
        int rid = (int) elt.getAttributeLong(MailService.A_REMOTE_ID);

        boolean relocated = elt.getAttributeBool(A_RELOCATED, false);

        CreateMountpoint redo = new CreateMountpoint(ombx.getId(), parentId, name, zid, rid, view, flags, color);
        redo.setId(id);
        redo.setChangeId(mod_content);
        redo.start(timestamp * 1000L);

        try {
            ombx.createMountpoint(new OfflineContext(redo), parentId, name, zid, rid, view, flags, color);
            if (relocated)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_MOUNTPOINT, Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_MOUNTPOINT, date, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created mountpoint (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            new DeltaSync(ombx).syncMountpoint(elt, id);
        }
    }

    void syncFolder(Element elt, int id) throws ServiceException {
        if (id <= Mailbox.HIGHEST_SYSTEM_ID) {
            // we know the system folders already exist...
            new DeltaSync(ombx).syncFolder(elt, id);
            return;
        }

        int parentId = (id == Mailbox.ID_FOLDER_ROOT) ? id : (int) elt.getAttributeLong(MailService.A_FOLDER);
        String name = (id == Mailbox.ID_FOLDER_ROOT) ? "ROOT" : elt.getAttribute(MailService.A_NAME);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        byte view = MailItem.getTypeForName(elt.getAttribute(MailService.A_DEFAULT_VIEW, null));

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE, -1000) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION, -1);

        ACL acl = parseACL(elt.getOptionalElement(MailService.E_ACL));
        String url = elt.getAttribute(MailService.A_URL, null);

        boolean relocated = elt.getAttributeBool(A_RELOCATED, false);

        CreateFolder redo = new CreateFolder(ombx.getId(), name, parentId, view, flags, color, url);
        redo.setFolderId(id);
        redo.setChangeId(mod_content);
        redo.start(timestamp * 1000L);

        try {
            // don't care about current feed syncpoint; sync can't be done offline
            ombx.createFolder(new OfflineContext(redo), name, parentId, view, flags, color, url);
            if (relocated)
                ombx.setChangeMask(sContext, id, MailItem.TYPE_FOLDER, Change.MODIFIED_FOLDER | Change.MODIFIED_NAME);
            if (acl != null)
                ombx.setPermissions(sContext, id, acl);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_FOLDER, date, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created folder (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            new DeltaSync(ombx).syncFolder(elt, id);
        }
    }

    ACL parseACL(Element eAcl) throws ServiceException {
        if (eAcl == null)
            return null;
        ACL acl = new ACL();
        for (Element eGrant : eAcl.listElements(MailService.E_GRANT)) {
            short rights = ACL.stringToRights(eGrant.getAttribute(MailService.A_RIGHTS));
            boolean inherit = eGrant.getAttributeBool(MailService.A_INHERIT, false);
            byte gtype = FolderAction.stringToType(eGrant.getAttribute(MailService.A_GRANT_TYPE));
            String zid = eGrant.getAttribute(MailService.A_ZIMBRA_ID, null);
            // FIXME: does not support passwords for external user access
            acl.grantAccess(zid, gtype, rights, inherit, null);
        }
        return acl;
    }

    void syncTag(Element elt) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailService.A_ID);
        String name = elt.getAttribute(MailService.A_NAME);
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION);

        CreateTag redo = new CreateTag(ombx.getId(), name, color);
        redo.setTagId(id);
        redo.setChangeId(mod_content);
        redo.start(date * 1000L);

        try {
            // don't care about current feed syncpoint; sync can't be done offline
            ombx.createTag(new OfflineContext(redo), name, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_TAG, date, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created tag (" + id + "): " + name);
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            new DeltaSync(ombx).syncTag(elt);
        }
    }

    static Element fetchContacts(OfflineMailbox ombx, String ids) throws ServiceException {
        Element request = new Element.XMLElement(MailService.GET_CONTACTS_REQUEST);
        request.addAttribute(MailService.A_SYNC, true);
        request.addElement(MailService.E_CONTACT).addAttribute(MailService.A_ID, ids);
        return ombx.sendRequest(request);
    }

    void syncContact(Element elt, int folderId) throws ServiceException {
        int id = (int) elt.getAttributeLong(MailService.A_ID);
        byte color = (byte) elt.getAttributeLong(MailService.A_COLOR, MailItem.DEFAULT_COLOR);
        int flags = Flag.flagsToBitmask(elt.getAttribute(MailService.A_FLAGS, null));
        String tags = elt.getAttribute(MailService.A_TAGS, null);

        Map<String, String> fields = new HashMap<String, String>();
        for (Element eField : elt.listElements())
            fields.put(eField.getAttribute(Element.XMLElement.A_ATTR_NAME), eField.getText());

        int timestamp = (int) elt.getAttributeLong(MailService.A_CHANGE_DATE);
        int changeId = (int) elt.getAttributeLong(MailService.A_MODIFIED_SEQUENCE);
        int date = (int) (elt.getAttributeLong(MailService.A_DATE) / 1000);
        int mod_content = (int) elt.getAttributeLong(MailService.A_REVISION);

        CreateContact redo = new CreateContact(ombx.getId(), folderId, fields, tags);
        redo.setContactId(id);
        redo.setChangeId(mod_content);
        redo.start(date * 1000L);

        try {
            Contact cn = ombx.createContact(new OfflineContext(redo), fields, folderId, tags);
            if (flags != 0)
                ombx.setTags(sContext, id, MailItem.TYPE_CONTACT, flags, MailItem.TAG_UNCHANGED);
            if (color != MailItem.DEFAULT_COLOR)
                ombx.setColor(sContext, id, MailItem.TYPE_CONTACT, color);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_CONTACT, date, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created contact (" + id + "): " + cn.getFileAsString());
        } catch (ServiceException e) {
            if (e.getCode() != MailServiceException.ALREADY_EXISTS)
                throw e;
            new DeltaSync(ombx).syncContact(elt, folderId);
        }
    }


    private static final Map<String, String> USE_SYNC_FORMATTER = new HashMap<String, String>();
        static {
            USE_SYNC_FORMATTER.put(UserServlet.QP_FMT, "sync");
            USE_SYNC_FORMATTER.put(SyncFormatter.QP_NOHDR, "1");
        }

    void syncMessage(int id, int folderId) throws ServiceException {
        byte[] content = null;
        Map<String, String> headers = new HashMap<String, String>();
        try {
            String hostname = new URL(ombx.getBaseUri()).getHost();
            String url = ombx.getBaseUri() + UserServlet.SERVLET_PATH + "/~/?fmt=sync&nohdr=1&id=" + id;
            Pair<Header[], byte[]> response = UserServlet.getRemoteResource(ombx.getAuthToken(), hostname, url);
            content = response.getSecond();
            for (Header hdr : response.getFirst())
                headers.put(hdr.getName(), hdr.getValue());
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.info("initial: message " + id + " has been deleted; skipping");
            return;
        } catch (MalformedURLException e) {
            OfflineLog.offline.warn("initial: base URI is invalid; aborting: " + ombx.getBaseUri(), e);
            return;
        }

        // XXX: UserServlet is also inlining these headers into the message body
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
            pm = new ParsedMessage(content, received, false);
            digest = pm.getRawDigest();
            size = pm.getRawSize();
        } catch (MessagingException e) {
            throw MailServiceException.MESSAGE_PARSE_ERROR(e);
        } catch (IOException e) {
            throw MailServiceException.MESSAGE_PARSE_ERROR(e);
        }

        CreateMessage redo = new CreateMessage(ombx.getId(), null, received, false, digest, size, folderId, true, flags, tags);
        redo.setMessageId(id);
        redo.setConvId(convId);
        redo.setChangeId(mod_content);
        redo.start(received * 1000L);

        try {
            // FIXME: not syncing COLOR
            // XXX: need to call with noICal = false
            Message msg = ombx.addMessage(new OfflineContext(redo), pm, folderId, true, flags, tags, convId);
            ombx.syncChangeIds(sContext, id, MailItem.TYPE_MESSAGE, received, mod_content, timestamp, changeId);
            OfflineLog.offline.debug("initial: created message (" + id + "): " + msg.getSubject());
            return;
        } catch (IOException e) {
            throw ServiceException.FAILURE("storing message " + id, e);
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
                SaveDraft redo2 = new SaveDraft(ombx.getId(), id, digest, size);
                redo2.setChangeId(mod_content);
                redo2.start(received * 1000L);

                synchronized (ombx) {
                    int change_mask = ombx.getChangeMask(sContext, id, MailItem.TYPE_MESSAGE);
                    if ((change_mask & Change.MODIFIED_CONTENT) == 0) {
                        ombx.saveDraft(new OfflineContext(redo2), pm, id);
                        ombx.syncChangeIds(sContext, id, MailItem.TYPE_MESSAGE, received, mod_content, timestamp, changeId);
                    }
                }
                OfflineLog.offline.debug("initial: re-saved draft (" + id + "): " + msg.getSubject());
            }
        } catch (MailServiceException.NoSuchItemException nsie) {
            OfflineLog.offline.debug("initial: message " + id + " has been deleted; no need to sync draft");
            return;
        } catch (IOException e) {
            throw ServiceException.FAILURE("storing message " + id, e);
        }

        // use this data to generate the XML entry used in message delta sync
        Element sync = new Element.XMLElement(MailService.E_MSG).addAttribute(MailService.A_ID, id);
        sync.addAttribute(MailService.A_FLAGS, Flag.bitmaskToFlags(flags)).addAttribute(MailService.A_TAGS, tags).addAttribute(MailService.A_CONV_ID, convId);
        sync.addAttribute(MailService.A_CHANGE_DATE, timestamp).addAttribute(MailService.A_MODIFIED_SEQUENCE, changeId);
        sync.addAttribute(MailService.A_DATE, received * 1000L).addAttribute(MailService.A_REVISION, mod_content);
        new DeltaSync(ombx).syncMessage(sync, folderId);
    }
}
