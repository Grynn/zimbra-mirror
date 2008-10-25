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
package com.zimbra.cs.offline.gab;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.ValueConstruct;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.Content;
import com.google.gdata.data.extensions.PostalAddress;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.data.extensions.Organization;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.OrgName;
import com.google.gdata.data.extensions.OrgTitle;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

import static com.zimbra.cs.mailbox.Contact.*;
import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;

public class ContactData {
    private final Map<String, String> fields = new HashMap<String, String>();

    private static final String SERVICE_YAHOO = "yahoo";
    private static final String SERVICE_AOL = "aol";
    private static final String SERVICE_MSN = "msn";
    private static final String SERVICE_OTHER = "other";

    public ContactData(ContactEntry contact) {
        importContact(contact);
    }

    public ContactData(Contact contact) {
        fields.putAll(contact.getFields());
    }
    
    public ContactEntry newContactEntry() {
        ContactEntry contact = new ContactEntry();
        exportContact(contact);
        return contact;
    }

    public ContactEntry updateContactEntry(ContactEntry contact) {
        exportContact(contact);
        return contact;
    }

    public ParsedContact getParsedContact() throws ServiceException {
        return new ParsedContact(fields);
    }
    
    private void importContact(ContactEntry contact) {
        TextConstruct title = contact.getTitle();
        if (title != null) {
            importName(title.getPlainText());
        }
        importEmail(contact, 0, A_email);
        importEmail(contact, 1, A_email2);
        importEmail(contact, 2, A_email3);
        importIm(contact, 0, A_imAddress1);
        importIm(contact, 1, A_imAddress2);
        importIm(contact, 2, A_imAddress3);
        PostalAddress homeAddr = getPostalAddress(contact, PostalAddress.Rel.HOME);
        if (homeAddr != null) {
            importHomeAddress(homeAddr.getValue());
        }
        PostalAddress workAddr = getPostalAddress(contact, PostalAddress.Rel.WORK);
        if (workAddr != null) {
            importWorkAddress(workAddr.getValue());
        }
        importPhone(contact, PhoneNumber.Rel.HOME, A_homePhone);
        importPhone(contact, PhoneNumber.Rel.WORK, A_workPhone);
        importPhone(contact, PhoneNumber.Rel.HOME_FAX, A_homeFax);
        importPhone(contact, PhoneNumber.Rel.WORK_FAX, A_workFax);
        importPhone(contact, PhoneNumber.Rel.PAGER, A_pager);
        if (contact.hasOrganizations()) {
            Organization organization = contact.getOrganizations().get(0);
            set(A_company, getValue(organization.getOrgName()));
            set(A_jobTitle, getValue(organization.getOrgTitle()));
        }
        if (contact.getContent() != null) {
            TextContent notes = contact.getTextContent();
            if (notes != null) {
                set(A_notes, notes.getContent().getPlainText());
            }
        }
    }

    private void exportContact(ContactEntry contact) {
        exportName(contact);
        exportEmail(contact, 0, A_email);
        exportEmail(contact, 1, A_email2);
        exportEmail(contact, 2, A_email3);
        exportIm(contact, 0, A_imAddress1);
        exportIm(contact, 1, A_imAddress2);
        exportIm(contact, 2, A_imAddress3);
        exportHomeAddress(contact);
        exportWorkAddress(contact);
        exportPhone(contact, PhoneNumber.Rel.HOME, A_homePhone);
        exportPhone(contact, PhoneNumber.Rel.WORK, A_workPhone);
        exportPhone(contact, PhoneNumber.Rel.HOME_FAX, A_homeFax);
        exportPhone(contact, PhoneNumber.Rel.WORK_FAX, A_workFax);
        exportPhone(contact, PhoneNumber.Rel.PAGER, A_pager);
        exportOrganization(contact);
        String notes = get(A_notes);
        if (notes != null) {
            contact.setContent(new PlainTextConstruct(notes));
        }
    }

    private void exportOrganization(ContactEntry contact) {
        String company = get(A_company);
        String title = get(A_jobTitle);
        if (company == null || title == null) return;
        Organization org;
        List<Organization> orgs = contact.getOrganizations();
        if (orgs.size() > 0) {
            org = orgs.get(0);
        } else {
            org = new Organization();
            orgs.add(org);
        }
        org.setOrgName(new OrgName(company));
        org.setOrgTitle(new OrgTitle(title));
    }
    
    private void importEmail(ContactEntry contact, int index, String field) {
        if (contact.hasEmailAddresses()) {
            List<Email> emails = contact.getEmailAddresses();
            if (index < emails.size()) {
                set(field, emails.get(index).getAddress());
            }
        }
    }

    private void importIm(ContactEntry contact, int index, String field) {
        if (contact.hasImAddresses()) {
            List<Im> ims = contact.getImAddresses();
            if (index < ims.size()) {
                set(field, getLocalImAddress(ims.get(index)));
            }
        }
    }
    
    private PostalAddress getPostalAddress(ContactEntry contact, String type) {
        if (contact.hasPostalAddresses()) {
            for (PostalAddress addr : contact.getPostalAddresses()) {
                if (type.equals(addr.getRel())) {
                    return addr;
                }
            }
        }
        return null;
    }

    private void importPhone(ContactEntry contact, String type, String field) {
        if (contact.hasPhoneNumbers()) {
            for (PhoneNumber phone : contact.getPhoneNumbers()) {
                if (type.equals(phone.getRel())) {
                    fields.put(field, phone.getPhoneNumber());
                }
            }
        }
    }

    private void exportPhone(ContactEntry contact, String type, String field) {
        String value = get(field);
        if (value == null) return;
        for (PhoneNumber phone : contact.getPhoneNumbers()) {
            if (type.equals(phone.getRel())) {
                phone.setPhoneNumber(value);
                return;
            }
        }
        PhoneNumber phone = new PhoneNumber();
        phone.setPhoneNumber(value);
        phone.setRel(type);
        contact.getPhoneNumbers().add(phone);
    }
    
    private void exportName(ContactEntry contact) {
        Name name = new Name();
        name.setFirst(get(A_firstName));
        name.setMiddle(get(A_middleName));
        name.setLast(get(A_lastName));
        name.setPrefix(get(A_namePrefix));
        String title = name.toString();
        if (title.length() > 0) {
            contact.setTitle(new PlainTextConstruct(title));
        }
    }

    private void exportEmail(ContactEntry contact, int index, String field) {
        String addr = fields.get(field);
        if (addr == null) return;
        List<Email> emails = contact.getEmailAddresses();
        if (index < emails.size()) {
            emails.get(index).setAddress(addr);
        } else {
            Email email = new Email();
            email.setAddress(addr);
            email.setRel(Email.Rel.HOME);
            emails.add(email);
        }
    }

    private void exportIm(ContactEntry contact, int index, String field) {
        String addr = fields.get(field);
        if (addr == null) return;
        List<Im> ims = contact.getImAddresses();
        if (index < ims.size()) {
            Im newIm = getRemoteImAddress(addr);
            Im im = ims.get(index);
            im.setAddress(newIm.getAddress());
            im.setProtocol(newIm.getProtocol());
        } else {
            ims.add(getRemoteImAddress(addr));
        }
    }
    
    private void importHomeAddress(String spec) {
        Address addr = Address.parse(spec);
        set(A_homeStreet, addr.getStreet());
        set(A_homeCity, addr.getCity());
        set(A_homeState, addr.getState());
        set(A_homePostalCode, addr.getZip());
        set(A_homeCountry, addr.getCountry());
    }

    private void importWorkAddress(String spec) {
        Address addr = Address.parse(spec);
        set(A_homeStreet, addr.getStreet());
        set(A_homeCity, addr.getCity());
        set(A_homeState, addr.getState());
        set(A_homePostalCode, addr.getZip());
        set(A_homeCountry, addr.getCountry());
    }

    private void exportHomeAddress(ContactEntry contact) {
        Address addr = new Address();
        addr.setStreet(get(A_homeStreet));
        addr.setCity(get(A_homeCity));
        addr.setState(get(A_homeState));
        addr.setZip(get(A_homePostalCode));
        addr.setCountry(get(A_homeCountry));
        String value = addr.toString();
        if (value.length() == 0) return;
        PostalAddress paddr = getPostalAddress(contact, PostalAddress.Rel.HOME);
        if (paddr == null) {
            paddr = new PostalAddress();
            paddr.setRel(PostalAddress.Rel.HOME);
            contact.getPostalAddresses().add(paddr);
        }
        paddr.setValue(value);
    }

    private void exportWorkAddress(ContactEntry contact) {
        Address addr = new Address();
        addr.setStreet(get(A_workStreet));
        addr.setCity(get(A_workCity));
        addr.setState(get(A_workState));
        addr.setZip(get(A_workPostalCode));
        addr.setCountry(get(A_workCountry));
        String value = addr.toString();
        if (value.length() == 0) return;
        PostalAddress paddr = getPostalAddress(contact, PostalAddress.Rel.WORK);
        if (paddr == null) {
            paddr = new PostalAddress();
            paddr.setRel(PostalAddress.Rel.WORK);
            contact.getPostalAddresses().add(paddr);
        }
        paddr.setValue(value);
    }

    private void importName(String spec) {
        Name name = Name.parse(spec);
        if (name != null) {
            set(A_firstName, name.getFirst());
            set(A_middleName, name.getMiddle());
            set(A_lastName, name.getLast());
            set(A_namePrefix, name.getPrefix());
            set(A_nameSuffix, "");
        }
    }

    private static String getLocalImAddress(Im im) {
        return String.format("%s://%s", getService(im), im.getAddress());
    }
    
    private static String getService(Im im) {
        String protocol = im.getProtocol();
        if (Im.Protocol.YAHOO.equals(protocol)) return SERVICE_YAHOO;
        if (Im.Protocol.MSN.equals(protocol)) return SERVICE_MSN;
        if (Im.Protocol.AIM.equals(protocol)) return SERVICE_AOL;
        return SERVICE_OTHER;
    }
    
    private static Im getRemoteImAddress(String value) {
        Im im = new Im();
        im.setRel(Im.Rel.HOME);
        int i = value.indexOf("://");
        if (i == -1) {
            im.setAddress(value);
            return im;
        }
        im.setAddress(value.substring(i + 3));
        String service = value.substring(0, i);
        if (service.equals(SERVICE_YAHOO)) {
            im.setProtocol(Im.Protocol.YAHOO);
        } else if (service.equals(SERVICE_AOL)) {
            im.setProtocol(Im.Protocol.AIM);
        } else if (service.equals(SERVICE_MSN)) {
            im.setProtocol(Im.Protocol.MSN);
        }
        return im;
    }
    
    private String getValue(ValueConstruct vc) {
        return vc != null ? vc.getValue() : null;
    }

    private void set(String name, String value) {
        fields.put(name, value != null ? value : "");
    }

    private String get(String name) {
        return fields.get(name);
    }
}
