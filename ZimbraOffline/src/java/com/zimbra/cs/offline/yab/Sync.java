package com.zimbra.cs.offline.yab;

import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.Tag;
import com.zimbra.cs.mailbox.MailItem;
import com.zimbra.cs.offline.util.yauth.Auth;
import com.zimbra.cs.offline.util.yab.Session;
import com.zimbra.cs.offline.util.yab.Contact;
import com.zimbra.cs.offline.util.yab.SyncResponse;
import com.zimbra.cs.offline.util.yab.SyncResponseEvent;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.ArrayUtil;

import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.IOException;

public class Sync {
    private final Mailbox mbox;
    private final Mailbox.OperationContext context;
    private final Session session;
    private SyncState state;

    public Sync(Mailbox mbox, Session session) throws ServiceException {
        this.mbox = mbox;
        this.session = session;
        context = new Mailbox.OperationContext(mbox);
    }

    public void sync() throws IOException, ServiceException {
        state = SyncState.load(mbox);
        // Get state of modified contacts
        List<Contact> contacts = getModifiedContacts();
        // Get contact changes since last revision
        SyncResponse res = session.getChanges(state.getRevision());
        Map<Integer, SyncResponseEvent> events = new HashMap<Integer, SyncResponseEvent>();
        for (SyncResponseEvent event : res.getEvents()) {
            events.put(event.getContactId(), event);
        }
    }

    private List<Contact> getModifiedContacts() throws IOException, ServiceException {
        int[] contactIds = getModifiedContactIds(state.getSequence());
        if (contactIds.length == 0) return null;
        int[] cids = state.getCids(contactIds);
        return session.getContacts(cids);
    }
    
    private int[] getModifiedContactIds(int lastSeq) throws ServiceException {
        synchronized (mbox) {
            List<Integer> contactIds =
                mbox.getModifiedItems(context, lastSeq, MailItem.TYPE_CONTACT).getFirst();
            return ArrayUtil.toIntArray(contactIds);
        }
    }

}