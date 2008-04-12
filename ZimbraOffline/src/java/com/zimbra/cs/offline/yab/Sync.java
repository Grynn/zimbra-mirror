package com.zimbra.cs.offline.yab;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.mailbox.MailServiceException;
import com.zimbra.cs.offline.util.yauth.Auth;
import com.zimbra.cs.offline.util.yab.Session;
import com.zimbra.cs.offline.util.yab.Contact;
import com.zimbra.cs.offline.util.yab.SyncResponse;
import com.zimbra.cs.offline.util.yab.SyncResponseEvent;
import com.zimbra.cs.offline.util.yab.SyncRequest;
import com.zimbra.cs.offline.util.yab.SyncRequestEvent;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ArrayUtil;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Collections;
import java.io.IOException;

import org.apache.log4j.Logger;

/*
 * Sync overview:
 *
 * - Get list of locally modified contact ids and associated cids
 * - Get YAB contact for each id
 * - Get YAB contact changes for last modified sequence
 * - If YAB contact modified, import changes deleting local (for YAB fields only)
 * - If YAB contact not modified, then get diffs and upload field level changes
 * - If YAB contact new then import
 * - If local contact new then export
 */
public class Sync {
    private final Mailbox mbox;
    private final Mailbox.OperationContext context;
    private final Session session;
    private SyncState state;
    private Set<Integer> deletedContactIds;
    private Set<Integer> modifiedContactIds; // Includes new contacts
    private List<Contact> modifiedContacts;

    public Sync(Mailbox mbox, Session session) throws ServiceException {
        this.mbox = mbox;
        this.session = session;
        context = new Mailbox.OperationContext(mbox);
    }

    public void sync() throws IOException, SyncException, ServiceException {
        // Load sync state         
        loadState();
        // Fetch potentially modified YAB contacts 
        modifiedContacts = session.getContacts(state.getCids(modifiedContactIds));
        // Fetch and process YAB contact changes
        SyncResponse res = session.getChanges(state.getRevision());
        process(res);
        // Push local updates to YAB server
        SyncRequest req = session.createSyncRequest(res.getRevision());
        // Add deleted contacts
        for (int contactId : deletedContactIds) {
            int cid = state.getCid(contactId);
            if (cid == -1) {
                throw new SyncException("Delete unknown contact id: " + contactId);
            }
            req.addEvent(SyncRequestEvent.removeContact(cid));
        }
        // Add updated contacts
        for (Contact contact : modifiedContacts) {
            int contactId = state.getContactId(contact.getId());
            assert contactId != -1;
            if (modifiedContactIds.remove(contactId)) {
                try {
                    req.addEvent(SyncRequestEvent.updateContact(
                        ContactData.exportChanged(mbox.getContactById(context, contactId), contact)));
                } catch (MailServiceException.NoSuchItemException e) {
                    // Contact must have just been deleted...
                    continue;
                }
            }
        }
        // Remaining modified contacts are new contacts
        for (int contactId : modifiedContactIds) {
            if (state.getCid(contactId) != -1) {
                throw new SyncException(
                    "Attempt to add new contact with duplicate id: " + contactId);
            }
            try {
                req.addEvent(SyncRequestEvent.addContact(
                    ContactData.exportNew(mbox.getContactById(context, contactId))));
            } catch (MailServiceException.NoSuchItemException e) {
                // Contact must have just been deleted...
                continue;
            }
        }
        res = (SyncResponse) req.send();
        process(res);
    }

    private void process(SyncResponse res) throws SyncException, ServiceException {
        // TODO Process Results...
        for (SyncResponseEvent event : res.getEvents()) {
            Contact contact = event.getContact();
            switch (event.getType()) {
            case ADD_CONTACT:
                addContact(contact);
                break;
            case UPDATE_CONTACT:
                updateContact(contact);
                break;
            case REMOVE_CONTACT:
                removeContact(contact.getId());
                break;
            case ADDRESS_BOOK_RESET:
                // TODO How to handle this?
            }
        }
        state.setRevision(res.getRevision());
    }

    private void addContact(Contact contact) throws SyncException, ServiceException {
        int cid = contact.getId();
        if (state.getContactId(cid) != -1) {
            throw new SyncException("YAB ADD_CONTACT with duplicate cid: " + cid);
        }
        ParsedContact pc = ContactData.importNew(contact);
        int contactId = mbox.createContact(
            context, pc, Mailbox.ID_FOLDER_CONTACTS, null).getId();
        state.addContact(contactId, cid);
    }
    
    private void updateContact(Contact contact) throws SyncException, ServiceException {
        int contactId = state.getContactId(contact.getId());
        if (contactId == -1) {
            throw new SyncException("YAB UPDATE_CONTACT for unknown cid: " + contact.getId());
        }
        mbox.modifyContact(context, contactId, ContactData.importChanged(contact));
        modifiedContactIds.remove(contactId); // YAB changes override local
    }

    private void removeContact(int cid) throws SyncException, ServiceException {
        int contactId = state.getContactId(cid);
        if (contactId == -1) {
            throw new SyncException("YAB REMOVE_CONTACT for unknown cid: " + cid);
        }
        mbox.delete(context, contactId, MailItem.TYPE_CONTACT);
        deletedContactIds.remove(contactId);
        modifiedContactIds.remove(contactId);
        state.removeContact(contactId);
    }

    private void loadState() throws ServiceException, IOException {
        synchronized (mbox) {
            state = SyncState.load(mbox);
            int seq = state.getSequence();
            // TODO Can the following sets overlap?
            deletedContactIds = new HashSet<Integer>(
                mbox.getTombstoneSet(seq).getIds(MailItem.TYPE_CONTACT));
            modifiedContactIds = new HashSet<Integer>(
                mbox.getModifiedItems(context, seq, MailItem.TYPE_CONTACT).getFirst());
        }
    }
}