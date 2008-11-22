/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab;

import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Mailbox;
import com.zimbra.cs.mailbox.OfflineMailbox;
import com.zimbra.cs.mailbox.MailServiceException.NoSuchItemException;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class ContactGroup {
    private final Mailbox mbox;
    private int itemId;
    private String name;
    private final Set<Integer> contactIds = new HashSet<Integer>();
    private boolean nameChanged;
    private boolean contactsChanged;

    private static final String A_mlist = Contact.A_otherCustom1;

    private static final String[] EMAIL_FIELDS =
        { Contact.A_email, Contact.A_email2, Contact.A_email3 };
    
    private static final Mailbox.OperationContext CONTEXT =
        new OfflineMailbox.OfflineContext();

    public static boolean isContactGroup(Contact contact) {
        return Contact.TYPE_GROUP.equals(contact.get(Contact.A_type));
    }

    public static ContactGroup create(Mailbox mbox, int folderId, String name)
        throws ServiceException {
        ContactGroup group = new ContactGroup(mbox);
        group.setName(name);
        group.create(folderId);
        return group;
    }

    public static ContactGroup get(Mailbox mbox, int itemId)
        throws ServiceException {
        Contact contact = mbox.getContactById(CONTEXT, itemId);
        if (!isContactGroup(contact)) {
            throw ServiceException.FAILURE("Not a contact group", null);
        }
        ContactGroup group = new ContactGroup(mbox);
        group.get(contact);
        return group;
    }
    
    private ContactGroup(Mailbox mbox) throws ServiceException {
        this.mbox = mbox;
    }

    private void create(int folderId) throws ServiceException {
        contactsChanged = true;
        Contact contact = mbox.createContact(
            CONTEXT, getParsedContact(true), folderId, null);
        itemId = contact.getId();
    }
       
    private void get(Contact contact) throws ServiceException {
        itemId = contact.getId();
        name = contact.get(Contact.A_nickname);
        String mlist = contact.get(A_mlist).trim();
        if (mlist != null && mlist.length() > 0) {
            for (String id : mlist.split(",")) {
                contactIds.add(Integer.parseInt(id));
            }
        }
    }

    public int getId() {
        return itemId;
    }
    
    public void setName(String name) {
        this.name = name;
        nameChanged = true;
    }

    public String getName() {
        return name;
    }

    public boolean hasContact(int cid) {
        return contactIds.contains(cid);
    }
    
    public boolean addContact(int cid) {
        if (!contactIds.contains(cid)) {
            contactIds.add(cid);
            contactsChanged = true;
            return true;
        }
        return false;
    }

    public boolean removeContact(int cid) {
        if (contactIds.contains(cid)) {
            contactIds.remove(cid);
            contactsChanged = true;
            return true;
        }
        return false;
    }

    public boolean hasChanges() {
        return nameChanged || contactsChanged;
    }

    public int getCount() {
        return contactIds.size();
    }
    
    public void modify() throws ServiceException {
        if (hasChanges()) {
            mbox.modifyContact(CONTEXT, itemId, getParsedContact(contactsChanged));
            nameChanged = false;
            contactsChanged = false;
        }
    }

    private ParsedContact getParsedContact(boolean saveContacts)
        throws ServiceException {
        Map<String, String> fields = new HashMap<String, String>();
        fields.put(Contact.A_type, Contact.TYPE_GROUP);
        fields.put(Contact.A_nickname, name);
        fields.put(Contact.A_fileAs, Contact.FA_EXPLICIT + ":" + name);
        if (saveContacts) {
            fields.put(A_mlist, join(contactIds, ","));
            fields.put(Contact.A_dlist, join(getEmailAddresses(), ","));
        }
        return new ParsedContact(fields);
    }
    
    private List<String> getEmailAddresses() throws ServiceException {
        List<String> emails = new ArrayList<String>();
        for (int id : contactIds) {
            try {
                Contact contact = mbox.getContactById(CONTEXT, id);
                for (String name : EMAIL_FIELDS) {
                    String email = contact.get(name);
                    if (email != null && email.length() > 0) {
                        emails.add(email);
                    }
                }
            } catch (NoSuchItemException e) {
                e.printStackTrace(); // DEBUG
                // Ignore
            }
        }
        return emails;
    }

    private static String join(Collection<?> parts, String delim) {
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = parts.iterator();
        if (it.hasNext()) {
            sb.append(it.next().toString());
            while (it.hasNext()) {
                sb.append(delim).append(it.next().toString());
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format(
            "{name=%s,id=%d,count=%d}", name, itemId, contactIds.size());
    }
}
