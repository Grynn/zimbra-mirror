/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.gab;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.Nickname;
import com.google.gdata.data.contacts.Birthday;
import com.google.gdata.data.contacts.Website;
import com.google.gdata.data.contacts.Website.Rel;
import com.google.gdata.data.TextContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.data.extensions.Organization;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.OrgName;
import com.google.gdata.data.extensions.OrgTitle;
import com.google.gdata.data.extensions.GivenName;
import com.google.gdata.data.extensions.AdditionalName;
import com.google.gdata.data.extensions.FamilyName;
import com.google.gdata.data.extensions.NamePrefix;
import com.google.gdata.data.extensions.NameSuffix;
import com.google.gdata.data.extensions.StructuredPostalAddress;
import com.google.gdata.data.extensions.Street;
import com.google.gdata.data.extensions.City;
import com.google.gdata.data.extensions.Region;
import com.google.gdata.data.extensions.PostCode;
import com.google.gdata.data.extensions.Country;
import com.google.gdata.data.extensions.OrgDepartment;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

import com.zimbra.cs.mailbox.Contact;
import com.zimbra.cs.mailbox.Contact.Attachment;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.ab.Ab;
import com.zimbra.cs.offline.ab.Address;
import com.zimbra.cs.offline.ab.Name;
import com.zimbra.common.mailbox.ContactConstants;
import com.zimbra.common.service.ServiceException;

import static com.zimbra.common.mailbox.ContactConstants.*;

public class ContactData {
    private final Map<String, String> fields = new HashMap<String, String>();

    private static final String SERVICE_ZIMBRA = "zimbra";
    private static final String SERVICE_YAHOO = "yahoo";
    private static final String SERVICE_AOL = "aol";
    private static final String SERVICE_MSN = "msn";
    private static final String SERVICE_OTHER = "other";

    private static final Map<String, String> PHONES = Ab.asMap(
        PhoneNumber.Rel.MOBILE, A_mobilePhone,
        PhoneNumber.Rel.HOME, A_homePhone,
        PhoneNumber.Rel.WORK, A_workPhone,
        PhoneNumber.Rel.WORK_MOBILE, A_workMobile,
        PhoneNumber.Rel.CALLBACK, A_callbackPhone,
        PhoneNumber.Rel.COMPANY_MAIN, A_companyPhone,
        PhoneNumber.Rel.HOME_FAX, A_homeFax,
        PhoneNumber.Rel.WORK_FAX, A_workFax,
        PhoneNumber.Rel.OTHER_FAX, A_otherFax,
        PhoneNumber.Rel.PAGER, A_pager,
        PhoneNumber.Rel.CAR, A_carPhone,
        PhoneNumber.Rel.OTHER, A_otherPhone
    );

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

    public void updateContactEntry(ContactEntry contact) {
        exportContact(contact);
    }

    public ParsedContact newParsedContact(Attachment photo)
        throws ServiceException {
        setFileAs();
        return new ParsedContact(fields, photo != null ? Arrays.asList(photo) : null);
    }

    public void updateParsedContact(ParsedContact pc, Attachment photo)
        throws ServiceException {
        setFileAs();
        // OfflineLog.gab.debug("updateParsedContact: " + fields);
        pc.modify(fields, photo != null ? Arrays.asList(photo) : null);
    }

    private void setFileAs() {
        String fileAs = Ab.getFileAs(fields);
        if (fileAs != null) {
            fields.put(A_fileAs, ContactConstants.FA_EXPLICIT + ":" + fileAs);
        }
    }

    public boolean isEmpty() {
        return fields.isEmpty();
    }

    private void importContact(ContactEntry contact) {
        Name name = toName(contact.getName());
        fields.putAll(name.toContactFields());
        importEmail(contact, 0, A_email);
        importEmail(contact, 1, A_email2);
        importEmail(contact, 2, A_email3);
        importIm(contact, 0, A_imAddress1);
        importIm(contact, 1, A_imAddress2);
        importIm(contact, 2, A_imAddress3);
        Address homeAddr = toAddress(
            getStructuredPostalAddress(contact, StructuredPostalAddress.Rel.HOME));
        fields.putAll(homeAddr.toHomeContactFields());
        Address workAddr = toAddress(
            getStructuredPostalAddress(contact, StructuredPostalAddress.Rel.WORK));
        fields.putAll(workAddr.toWorkContactFields());
        Address otherAddr = toAddress(
            getStructuredPostalAddress(contact, StructuredPostalAddress.Rel.OTHER));
        fields.putAll(otherAddr.toWorkContactFields());
        for (Map.Entry<String, String> e : PHONES.entrySet()) {
            importPhone(contact, e.getKey(), e.getValue());
        }
        importOrganization(contact);
        set(A_birthday, contact.hasBirthday() ? contact.getBirthday().getValue() : null);
        set(A_nickname, contact.hasNickname() ? contact.getNickname().getValue() : null);
        importUrl(contact);
        set(A_notes, null);
        if (contact.getContent() != null) {
            TextContent notes = contact.getTextContent();
            if (notes != null) {
                set(A_notes, notes.getContent().getPlainText());
            }
        }
    }

    private void importUrl(ContactEntry contact) {
        if (contact.hasWebsites()) {
            for (Website webURL : contact.getWebsites())
            {
                if (webURL.getRel().toString().equalsIgnoreCase(Website.Rel.HOME_PAGE.toString()))
                    set(A_homeURL, webURL.getHref());
                else if (webURL.getRel().toString().equalsIgnoreCase(Website.Rel.WORK.toString()))
                    set(A_workURL, webURL.getHref());
                else
                    set(A_otherURL, webURL.getHref());
            }
        } else {
            set(A_homeURL, null);
            set(A_workURL, null);
            set(A_otherURL, null);
        }
    }

    private void importEmail(ContactEntry contact, int index, String field) {
        if (contact.hasEmailAddresses()) {
            List<Email> emails = contact.getEmailAddresses();
            if (index < emails.size()) {
                set(field, emails.get(index).getAddress());
                return;
            }
        }
        set(field, null);
    }

    private void importIm(ContactEntry contact, int index, String field) {
        if (contact.hasImAddresses()) {
            List<Im> ims = contact.getImAddresses();
            if (index < ims.size()) {
                set(field, getLocalImAddress(ims.get(index)));
                return;
            }
        }
        set(field, null);
    }

    private void importPhone(ContactEntry contact, String type, String field) {
        if (contact.hasPhoneNumbers()) {
            for (PhoneNumber phone : contact.getPhoneNumbers()) {
                if (type.equals(phone.getRel())) {
                    fields.put(field, phone.getPhoneNumber());
                    return;
                }
            }
        }
        set(field, null);
    }

    private void importOrganization(ContactEntry contact) {
        if (contact.hasOrganizations()) {
            Organization org = contact.getOrganizations().get(0);
            set(A_company, org.hasOrgName() ? org.getOrgName().getValue() : null);
            set(A_jobTitle, org.hasOrgTitle() ? org.getOrgTitle().getValue() : null);
            set(A_department, org.hasOrgDepartment() ? org.getOrgDepartment().getValue() : null);
        } else {
            set(A_company, null);
            set(A_jobTitle, null);
            set(A_department, null);
        }
    }

    private void exportContact(ContactEntry contact) {
        contact.setName(toGoogleName(Name.fromContactFields(fields)));
        exportEmail(contact, 0, A_email);
        exportEmail(contact, 1, A_email2);
        exportEmail(contact, 2, A_email3);
        exportIm(contact, 0, A_imAddress1);
        exportIm(contact, 1, A_imAddress2);
        exportIm(contact, 2, A_imAddress3);
        exportAddress(contact, Address.fromHomeContactFields(fields), StructuredPostalAddress.Rel.HOME);
        exportAddress(contact, Address.fromWorkContactFields(fields), StructuredPostalAddress.Rel.WORK);
        exportAddress(contact, Address.fromOtherContactFields(fields), StructuredPostalAddress.Rel.OTHER);
        for (Map.Entry<String, String> e : PHONES.entrySet()) {
            exportPhone(contact, e.getKey(), e.getValue());
        }
        contact.setNickname(new Nickname(get(A_nickname)));
        contact.setBirthday(new Birthday(get(A_birthday)));
        exportOrganization(contact);
        exportUrl(contact, Website.Rel.HOME_PAGE, A_homeURL);
        exportUrl(contact, Website.Rel.WORK, A_workURL);
        exportUrl(contact, Website.Rel.OTHER, A_otherURL);
        String notes = get(A_notes);
        if (notes != null) {
            contact.setContent(new PlainTextConstruct(notes));
        }
    }

    private void exportEmail(ContactEntry contact, int index, String field) {
        String addr = fields.get(field);
        if (addr != null) {
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
    }

    private void exportIm(ContactEntry contact, int index, String field) {
        String addr = fields.get(field);
        if (addr != null) {
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
    }

    private void exportAddress(ContactEntry contact, Address addr, String type) {
        if (addr.isEmpty()) return;
        StructuredPostalAddress spa = getStructuredPostalAddress(contact, type);
        if (spa == null) {
            spa = new StructuredPostalAddress();
            contact.addStructuredPostalAddress(spa);
            spa.setRel(type);
        } else if (spa.hasFormattedAddress()) {
            spa.setFormattedAddress(null);
        }
        spa.setStreet(addr.hasStreet() ? new Street(addr.getStreet()) : null);
        spa.setCity(addr.hasCity() ? new City(addr.getCity()) : null);
        spa.setRegion(addr.hasState() ? new Region(addr.getState()) : null);
        spa.setPostcode(addr.hasPostalCode() ? new PostCode(addr.getPostalCode()) : null);
        spa.setCountry(addr.hasCountry() ? new Country(null, addr.getCountry()) : null);
    }

    private StructuredPostalAddress getStructuredPostalAddress(ContactEntry contact, String type) {
        if (contact.hasStructuredPostalAddresses()) {
            for (StructuredPostalAddress addr : contact.getStructuredPostalAddresses()) {
                if (type.equals(addr.getRel())) {
                    return addr;
                }
            }
        }
        return null;
    }
    
    private void exportPhone(ContactEntry contact, String type, String field) {
        String value = get(field);
        if (value != null) {
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
    }

    private void exportUrl(ContactEntry contact, Rel key, String field) {
        String value = get(field);
        if (value != null) {
            for (Website webURL : contact.getWebsites()) {
                if (!key.equals(Website.Rel.OTHER)) {
                    if (key.toString().equalsIgnoreCase(webURL.getRel().toString())) {
                        webURL.setHref(value);
                        return;
                    }
                } else if (!webURL.getRel().equals(Website.Rel.WORK) && !webURL.getRel().equals(Website.Rel.HOME_PAGE)) {
                    webURL.setHref(value);
                    return;
                }
            }
            Website webURL = new Website();
            webURL.setHref(value);
            webURL.setRel(key);
            contact.getWebsites().add(webURL);
        } else {
            // delete
            for (Website webURL : contact.getWebsites()) {
                if (!key.equals(Website.Rel.OTHER)) {
                    if (key.toString().equalsIgnoreCase(webURL.getRel().toString())) {
                        contact.getWebsites().remove(webURL);
                        return;
                    }
                } else if (!webURL.getRel().equals(Website.Rel.WORK) && !webURL.getRel().equals(Website.Rel.HOME_PAGE)) {
                    contact.getWebsites().remove(webURL);
                    return;
                }
            }
        }
    }

    private void exportOrganization(ContactEntry contact) {
        String company = get(A_company);
        String title = get(A_jobTitle);
        String dept = get(A_department);
        if (company != null || title != null || dept != null) {
            Organization org;
            List<Organization> orgs = contact.getOrganizations();
            if (orgs.size() > 0) {
                org = orgs.get(0);
            } else {
                org = new Organization();
                org.setRel(Organization.Rel.WORK);
                orgs.add(org);
            }
            org.setOrgName(new OrgName(company));
            org.setOrgTitle(new OrgTitle(title));
            org.setOrgDepartment(new OrgDepartment(dept));
        }
    }

    private static Address toAddress(StructuredPostalAddress spa) {
        Address addr = new Address();
        if (spa == null) {
            return addr;
        }
        if (spa.hasStreet()) {
            addr.setStreet(spa.getStreet().getValue());
        }
        if (spa.hasCity()) {
            addr.setCity(spa.getCity().getValue());
        }
        if (spa.hasRegion()) {
            addr.setState(spa.getRegion().getValue());
        }
        if (spa.hasPostcode()) {
            addr.setPostalCode(spa.getPostcode().getValue());
        }
        if (spa.hasCountry()) {
            addr.setCountry(spa.getCountry().getValue());
        }
        if (addr.isEmpty() && spa.hasFormattedAddress()) {
            return Address.parse(spa.getFormattedAddress().getValue());
        }
        return addr;
    }
    
    private static Name toName(com.google.gdata.data.extensions.Name gname) {
        Name name = new Name();
        if (gname == null) return name;
        name.setFirst(gname.hasGivenName() ? gname.getGivenName().getValue() : null);
        name.setMiddle(gname.hasAdditionalName() ? gname.getAdditionalName().getValue() : null);
        name.setLast(gname.hasFamilyName() ? gname.getFamilyName().getValue() : null);
        name.setPrefix(gname.hasNamePrefix() ? gname.getNamePrefix().getValue() : null);
        name.setSuffix(gname.hasNameSuffix() ? gname.getNameSuffix().getValue() : null);
        if (name.isEmpty() && gname.hasFullName()) {
            return Name.parse(gname.getFullName().getValue());
        }
        return name;
    }

    private static com.google.gdata.data.extensions.Name toGoogleName(Name name) {
        com.google.gdata.data.extensions.Name gname =
            new com.google.gdata.data.extensions.Name();
        if (name.hasFirst()) {
            gname.setGivenName(new GivenName(name.getFirst(), null));
        }
        if (name.hasMiddle()) {
            gname.setAdditionalName(new AdditionalName(name.getMiddle(), null));
        }
        if (name.hasLast()) {
            gname.setFamilyName(new FamilyName(name.getLast(), null));
        }
        if (name.hasPrefix()) {
            gname.setNamePrefix(new NamePrefix(name.getPrefix()));
        }
        if (name.hasSuffix()) {
            gname.setNameSuffix(new NameSuffix(name.getSuffix()));
        }
        return gname;
    }

    private static final String ZIM_PREFIX = "zimbra:";
    
    private static String getLocalImAddress(Im im) {
        String address = im.getAddress();
        String protocol = im.getProtocol();
        if (Im.Protocol.YAHOO.equals(protocol)) {
            return SERVICE_YAHOO + "://" + address;
        } else if (Im.Protocol.MSN.equals(protocol)) {
            return SERVICE_MSN + "://" + address;
        } else if (Im.Protocol.AIM.equals(protocol)) {
            return SERVICE_AOL + "://" + address;
        } else if (Im.Protocol.JABBER.equals(protocol) &&
                   address.startsWith(ZIM_PREFIX)) {
            return SERVICE_ZIMBRA + "://" + address.substring(ZIM_PREFIX.length());
        } else {
            return SERVICE_OTHER + "://" + address;
        }
    }
    
    private static Im getRemoteImAddress(String value) {
        Im im = new Im();
        im.setRel(Im.Rel.HOME);
        int i = value.indexOf("://");
        if (i == -1) {
            im.setAddress(value);
            return im;
        }
        String service = value.substring(0, i);
        String addr = value.substring(i + 3);
        if (service.equals(SERVICE_ZIMBRA)) {
            im.setProtocol(Im.Protocol.JABBER);
            im.setAddress(ZIM_PREFIX + addr);
            return im;
        }
        im.setAddress(addr);
        if (service.equals(SERVICE_YAHOO)) {
            im.setProtocol(Im.Protocol.YAHOO);
        } else if (service.equals(SERVICE_AOL)) {
            im.setProtocol(Im.Protocol.AIM);
        } else if (service.equals(SERVICE_MSN)) {
            im.setProtocol(Im.Protocol.MSN);
        }
        return im;
    }

    private void set(String name, String value) {
        fields.put(name, normalize(value));
    }

    private String get(String name) {
        return normalize(fields.get(name));
    }

    private String normalize(String value) {
        return value != null && value.length() > 0 ? value : null;
    }
}
