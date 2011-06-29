/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.ab.yc;

import static com.zimbra.common.mailbox.ContactConstants.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Strings;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.cs.offline.OfflineLog;
import com.zimbra.cs.offline.ab.Ab;
import com.zimbra.cs.offline.util.yc.Action;
import com.zimbra.cs.offline.util.yc.AddressField;
import com.zimbra.cs.offline.util.yc.Contact;
import com.zimbra.cs.offline.util.yc.DateField.AnniversaryField;
import com.zimbra.cs.offline.util.yc.DateField.BirthdayField;
import com.zimbra.cs.offline.util.yc.Fields;
import com.zimbra.cs.offline.util.yc.Fields.Flag;
import com.zimbra.cs.offline.util.yc.Fields.Type;
import com.zimbra.cs.offline.util.yc.NameField;
import com.zimbra.cs.offline.util.yc.SimpleField;

/**
 * 
 * class that translate fields from yahoo into zimbra contact fields and vice
 * versa
 * 
 */
public class ContactData {

    // All Zimbra fields that are synchronized with Yahoo address book
    private static final List<String> ALL_FIELDS = new ArrayList<String>();

    private static final String PREFIX_PHONE = "phone";
    private static final String PREFIX_HOME_PHONE = "homePhone";
    private static final String PREFIX_WORK_PHONE = "workPhone";
    private static final String PREFIX_MOBILE_PHONE = "mobilePhone";
    private static final String PREFIX_EMAIL = "email";
    private static final String PREFIX_HOME_EMAIL = "homeEmail";
    private static final String PREFIX_WORK_EMAIL = "workEmail";
    private static final String PREFIX_IM_ADDRESS = "imAddress";

    private static final List<String> OTHER_FIELDS = Arrays.asList(A_company, A_nickname, A_jobTitle, A_email,
            A_email2, A_email3, A_notes, A_homeURL, A_workURL, A_notes, A_homeURL, A_workURL, A_homeFax, A_workFax,
            A_homePhone, A_homePhone2, A_workPhone, A_workPhone2, A_mobilePhone, A_workMobile, A_pager, A_otherPhone,
            A_imAddress1, A_imAddress2, A_imAddress3, A_fileAs, A_fullName);

    static {
        ALL_FIELDS.addAll(Ab.NAME_FIELDS);
        ALL_FIELDS.addAll(Ab.WORK_ADDRESS_FIELDS);
        ALL_FIELDS.addAll(Ab.HOME_ADDRESS_FIELDS);
        ALL_FIELDS.addAll(OTHER_FIELDS);
    }

    private Map<String, String> fields = new HashMap<String, String>();

    public ContactData(Contact yContact) {
        for (String name : ALL_FIELDS) {
            fields.put(name, null);
        }
        for (List<Fields> fieldList : yContact.getAllFields()) {
            for (Fields f : fieldList) {
                importField(f);
            }
        }
    }

    private ContactData() {
    }

    public static Contact getYContactFromZcsContact(com.zimbra.cs.mailbox.Contact mboxContact, Action op) {
        ContactData cd = new ContactData();
        cd.fields.putAll(mboxContact.getFields());

        Contact contact = new Contact();
        contact.setOp(op);
        Map<Type, List<Fields>> fieldsMap = new HashMap<Type, List<Fields>>();
        contact.setFields(fieldsMap);

        cd.exportAndAddNameField(fieldsMap, op);
        cd.exportAndAddEmails(fieldsMap, op);
        cd.exportAndAddAddress(fieldsMap, op);
        cd.exportAndAddBirthday(fieldsMap, op);
        cd.exportAndAddAnniversary(fieldsMap, op);
        cd.exportAndAddPhones(fieldsMap, op);
        cd.exportAndAddIms(fieldsMap, op);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.nickname, A_nickname);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.company, A_company);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.jobTitle, A_jobTitle);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.notes, A_notes);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.link, A_homeURL);

        return contact;
    }

    /**
     * return the delta part of a mailbox contact and the old contact zd got
     * from yahoo
     * 
     * @param mboxContact
     *            new value
     * @param oldContact
     *            old value
     * @return
     */
    public static List<Fields> delta(com.zimbra.cs.mailbox.Contact mboxContact, Contact oldContact) {
        List<Fields> fieldsChangeList = new ArrayList<Fields>();
        ContactData newContactData = new ContactData();
        newContactData.fields.putAll(mboxContact.getFields());

        if (newContactData.hasNameFields()) {
            Fields change = null;
            if (oldContact.hasField(Type.name)) {
                if (newContactData.isFieldsUpdated(oldContact, Type.name)) {
                    change = newContactData.exportNameField(Action.UPDATE);
                    change.setId(oldContact.getFields(Type.name).get(0).getId());
                }
            } else {
                change = newContactData.exportNameField(Action.ADD);
            }
            if (change != null) {
                fieldsChangeList.add(change);
            }
        } else {
            if (oldContact.hasField(Type.name)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(Type.name).get(0).getId());
                fieldsChangeList.add(change);
            }
        }

        if (newContactData.hasHomeAddressFields()) {
            Fields change = null;
            if (oldContact.hasFieldByFlag(Type.address, Flag.home)) {
                if (newContactData.isFieldsUpdated(oldContact, Type.address, Flag.home)) {
                    List<Fields> list = oldContact.getFields(Type.address);
                    change = newContactData.exportHomeAddress(Action.UPDATE);
                    for (Fields f : list) {
                        if (Flag.home == f.getFlag()) {
                            change.setId(f.getId());
                        }
                    }
                }
            } else {
                change = newContactData.exportHomeAddress(Action.ADD);
            }
            if (change != null) {
                fieldsChangeList.add(change);
            }
        } else {
            if (oldContact.hasFieldByFlag(Type.address, Flag.home)) {
                Fields change = new Fields();
                change.setOp(Action.REMOVE);
                List<Fields> list = oldContact.getFields(Type.address);
                for (Fields f : list) {
                    if (Flag.home.equals(f.getFlag())) {
                        change.setId(f.getId());
                    }
                }
                fieldsChangeList.add(change);
            }
        }

        if (newContactData.hasWorkAddressFields()) {
            Fields change = null;
            if (oldContact.hasFieldByFlag(Type.address, Flag.work)) {
                if (newContactData.isFieldsUpdated(oldContact, Type.address, Flag.work)) {
                    change = newContactData.exportWorkAddress(Action.UPDATE);
                    List<Fields> list = oldContact.getFields(Type.address);
                    for (Fields f : list) {
                        if (Flag.work.equals(f.getFlag())) {
                            change.setId(f.getId());
                        }
                    }
                }
            } else {
                change = newContactData.exportWorkAddress(Action.ADD);
            }
            if (change != null) {
                fieldsChangeList.add(change);
            }
        } else {
            if (oldContact.hasFieldByFlag(Type.address, Flag.work)) {
                Fields change = new Fields();
                change.setOp(Action.REMOVE);
                List<Fields> list = oldContact.getFields(Type.address);
                for (Fields f : list) {
                    if (Flag.work.equals(f.getFlag())) {
                        change.setId(f.getId());
                    }
                }
                fieldsChangeList.add(change);
            }
        }

        if (newContactData.hasField(A_birthday)) {
            Fields change = null;
            if (oldContact.hasField(Type.birthday)) {
                if (newContactData.isFieldsUpdated(oldContact, Type.birthday)) {
                    change = newContactData.exportBirthday(Action.UPDATE);
                    change.setId(oldContact.getFields(Type.birthday).get(0).getId());
                }
            } else {
                change = newContactData.exportBirthday(Action.ADD);
            }
            if (change != null) {
                fieldsChangeList.add(change);
            }
        } else {
            if (oldContact.hasField(Type.birthday)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(Type.birthday).get(0).getId());
                fieldsChangeList.add(change);
            }
        }

        if (newContactData.hasField(A_anniversary)) {
            Fields change = null;
            if (oldContact.hasField(Type.anniversary)) {
                if (newContactData.isFieldsUpdated(oldContact, Type.anniversary)) {
                    change = newContactData.exportAnniversary(Action.UPDATE);
                    change.setId(oldContact.getFields(Type.anniversary).get(0).getId());
                }
            } else {
                change = newContactData.exportAnniversary(Action.ADD);
            }
            if (change != null) {
                fieldsChangeList.add(change);
            }
        } else {
            if (oldContact.hasField(Type.anniversary)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(Type.anniversary).get(0).getId());
                fieldsChangeList.add(change);
            }
        }

        deltaEmailFields(newContactData, oldContact, fieldsChangeList);
        deltaPhoneFields(newContactData, oldContact, fieldsChangeList);
        deltaSimpleField(newContactData, oldContact, fieldsChangeList, Type.nickname, A_nickname);
        deltaSimpleField(newContactData, oldContact, fieldsChangeList, Type.jobTitle, A_jobTitle);
        deltaSimpleField(newContactData, oldContact, fieldsChangeList, Type.company, A_company);
        deltaSimpleField(newContactData, oldContact, fieldsChangeList, Type.notes, A_notes);
        deltaSimpleField(newContactData, oldContact, fieldsChangeList, Type.link, A_homeURL);
        deltaMultiValueFields(newContactData, oldContact, fieldsChangeList, ContactData.PREFIX_IM_ADDRESS, Type.yahooid);

        return fieldsChangeList;
    }

    private static void deltaSimpleField(ContactData newContactData, Contact oldContact, List<Fields> fieldsChangeList,
            Type type, String attr) {
        if (newContactData.hasField(attr)) {
            Fields change = null;
            if (oldContact.hasField(type)) {
                if (newContactData.isFieldsUpdated(oldContact, type)) {
                    change = newContactData.exportSimpleField(Action.UPDATE, type, attr);
                    change.setId(oldContact.getFields(type).get(0).getId());
                }
            } else {
                change = newContactData.exportSimpleField(Action.ADD, type, attr);
            }
            if (change != null) {
                fieldsChangeList.add(change);
            }
        } else {
            // attr and type must match. avoid situations like type is "phone"
            // and attr is "workPhone"
            if (oldContact.hasField(type)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(type).get(0).getId());
                fieldsChangeList.add(change);
            }
        }
    }

    private static void deltaEmailFields(ContactData newContactData, Contact oldContact, List<Fields> fieldsChangeList) {
        Map<String, String> newEmails = new HashMap<String, String>();
        Map<String, String> newHomeEmails = new HashMap<String, String>();
        Map<String, String> newWorkEmails = new HashMap<String, String>();
        for (String key : newContactData.fields.keySet()) {
            if (key.startsWith(ContactData.PREFIX_EMAIL)) {
                newEmails.put(newContactData.fields.get(key), key);
            } else if (key.startsWith(ContactData.PREFIX_HOME_EMAIL)) {
                newHomeEmails.put(newContactData.fields.get(key), key);
            } else if (key.startsWith(ContactData.PREFIX_WORK_EMAIL)) {
                newWorkEmails.put(newContactData.fields.get(key), key);
            }
        }
        List<Fields> oldEmailsFields = oldContact.getFields(Type.email);
        Map<String, String> oldEmails = new HashMap<String, String>();
        Map<String, String> oldHomeEmails = new HashMap<String, String>();
        Map<String, String> oldWorkEmails = new HashMap<String, String>();
        if (oldEmailsFields != null) {
            for (Fields f : oldEmailsFields) {
                if (f.getFlag() != null) {
                    switch (f.getFlag()) {
                    case personal:
                        oldHomeEmails.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
                        break;
                    case work:
                        oldWorkEmails.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
                        break;
                    }
                } else {
                    oldEmails.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
                }
            }
        }
        // email
        Set<String> newEmailsSet = new HashSet<String>(newEmails.keySet());
        newEmails.keySet().removeAll(oldEmails.keySet());
        oldEmails.keySet().removeAll(newEmailsSet);
        for (String newEmail : newEmails.keySet()) {
            fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, Type.email, newEmails.get(newEmail)));
        }
        for (String oldEmail : oldEmails.keySet()) {
            fieldsChangeList.add(ContactData.exportFieldsToRemove(oldEmails.get(oldEmail)));
        }
        // homeEmail
        newEmailsSet = new HashSet<String>(newHomeEmails.keySet());
        newHomeEmails.keySet().removeAll(oldHomeEmails.keySet());
        oldHomeEmails.keySet().removeAll(newEmailsSet);
        for (String newEmail : newHomeEmails.keySet()) {
            fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, Type.email, newHomeEmails.get(newEmail),
                    Flag.personal));
        }
        for (String oldEmail : oldHomeEmails.keySet()) {
            fieldsChangeList.add(ContactData.exportFieldsToRemove(oldHomeEmails.get(oldEmail)));
        }
        // workEmail
        newEmailsSet = new HashSet<String>(newWorkEmails.keySet());
        newWorkEmails.keySet().removeAll(oldWorkEmails.keySet());
        oldWorkEmails.keySet().removeAll(newEmailsSet);
        for (String newEmail : newWorkEmails.keySet()) {
            fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, Type.email, newWorkEmails.get(newEmail),
                    Flag.work));
        }
        for (String oldEmail : oldWorkEmails.keySet()) {
            fieldsChangeList.add(ContactData.exportFieldsToRemove(oldWorkEmails.get(oldEmail)));
        }
    }

    private static void deltaMultiValueFields(ContactData newContactData, Contact oldContact,
            List<Fields> fieldsChangeList, String prefix, Type type) {
        Map<String, String> newDataMap = new HashMap<String, String>();
        for (String key : newContactData.fields.keySet()) {
            if (key.startsWith(prefix)) {
                newDataMap.put(newContactData.fields.get(key), key);
            }
        }
        List<Fields> oldFields = oldContact.getFields(type);
        Map<String, String> oldDataMap = new HashMap<String, String>();
        if (oldFields != null) {
            for (Fields f : oldFields) {
                oldDataMap.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
            }
        }
        for (String newValue : newDataMap.keySet()) {
            if (!oldDataMap.keySet().contains(newValue)) {
                fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, type, newDataMap.get(newValue)));
            }
        }
        for (String oldValue : oldDataMap.keySet()) {
            if (!newDataMap.keySet().contains(oldValue)) {
                fieldsChangeList.add(ContactData.exportFieldsToRemove(oldDataMap.get(oldValue)));
            }
        }
    }

    private static void deltaPhoneFields(ContactData newContactData, Contact oldContact, List<Fields> fieldsChangeList) {
        Map<String, String> newHomePhones = new HashMap<String, String>();
        Map<String, String> newWorkPhones = new HashMap<String, String>();
        Map<String, String> newMobilePhones = new HashMap<String, String>();
        Map<String, String> newPhones = new HashMap<String, String>();

        for (String key : newContactData.fields.keySet()) {
            if (key.startsWith(ContactData.PREFIX_HOME_PHONE)) {
                newHomePhones.put(newContactData.fields.get(key), key);
            } else if (key.startsWith(ContactData.PREFIX_WORK_PHONE)) {
                newWorkPhones.put(newContactData.fields.get(key), key);
            } else if (key.startsWith(ContactData.PREFIX_MOBILE_PHONE)) {
                newMobilePhones.put(newContactData.fields.get(key), key);
            } else if (key.startsWith(ContactData.PREFIX_PHONE)) {
                newPhones.put(newContactData.fields.get(key), key);
            }
        }

        Map<String, String> oldHomePhones = new HashMap<String, String>();
        Map<String, String> oldWorkPhones = new HashMap<String, String>();
        Map<String, String> oldMobilePhones = new HashMap<String, String>();
        Map<String, String> oldPhones = new HashMap<String, String>();
        if (oldContact.getFields(Type.phone) != null) {
            for (Fields f : oldContact.getFields(Type.phone)) {
                if (f.getFlag() != null) {
                    switch (f.getFlag()) {
                    case home:
                        oldHomePhones.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
                        break;
                    case work:
                        oldWorkPhones.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
                        break;
                    case mobile:
                        oldMobilePhones.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
                        break;
                    }
                } else {
                    oldPhones.put(((SimpleField) f.getFieldValue()).getValue(), f.getId());
                }
            }
        }
        // add new phones
        for (String newValue : newHomePhones.keySet()) {
            if (!oldHomePhones.keySet().contains(newValue)) {
                fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, Type.phone,
                        newHomePhones.get(newValue), Flag.personal));
            }
        }
        for (String newValue : newWorkPhones.keySet()) {
            if (!oldWorkPhones.keySet().contains(newValue)) {
                fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, Type.phone,
                        newWorkPhones.get(newValue), Flag.work));
            }
        }
        for (String newValue : newMobilePhones.keySet()) {
            if (!oldMobilePhones.keySet().contains(newValue)) {
                fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, Type.phone,
                        newMobilePhones.get(newValue), Flag.mobile));
            }
        }
        for (String newValue : newPhones.keySet()) {
            if (!oldPhones.keySet().contains(newValue)) {
                fieldsChangeList.add(newContactData.exportSimpleField(Action.ADD, Type.phone,
                        newPhones.get(newValue)));
            }
        }
        // remove old phones
        for (String oldValue : oldHomePhones.keySet()) {
            if (!newHomePhones.keySet().contains(oldValue)) {
                fieldsChangeList.add(ContactData.exportFieldsToRemove(oldHomePhones.get(oldValue)));
            }
        }
        for (String oldValue : oldWorkPhones.keySet()) {
            if (!newWorkPhones.keySet().contains(oldValue)) {
                fieldsChangeList.add(ContactData.exportFieldsToRemove(oldWorkPhones.get(oldValue)));
            }
        }
        for (String oldValue : oldMobilePhones.keySet()) {
            if (!newMobilePhones.keySet().contains(oldValue)) {
                fieldsChangeList.add(ContactData.exportFieldsToRemove(oldMobilePhones.get(oldValue)));
            }
        }
        for (String oldValue : oldPhones.keySet()) {
            if (!newPhones.keySet().contains(oldValue)) {
                fieldsChangeList.add(ContactData.exportFieldsToRemove(oldPhones.get(oldValue)));
            }
        }
    }

    private static boolean isEqualStrs(String s1, String s2) {
        return StringUtil.equal(Strings.nullToEmpty(s1).trim(), Strings.nullToEmpty(s2).trim());
    }

    private boolean isFieldsUpdated(Contact oldContact, Type type) {
        return isFieldsUpdated(oldContact, type, null);
    }

    private boolean isFieldsUpdated(Contact oldContact, Type type, Flag flag) {
        boolean isDiff = true;
        switch (type) {
        case name:
            List<Fields> names = oldContact.getFields(Type.name);
            if (names.size() > 0) {
                NameField nameField = (NameField) names.get(0).getFieldValue();
                if (isEqualStrs(nameField.getFirst(), this.fields.get(A_firstName))
                        && (isEqualStrs(nameField.getMiddle(), this.fields.get(A_middleName)))
                        && (isEqualStrs(nameField.getLast(), this.fields.get(A_lastName)))
                        && (isEqualStrs(nameField.getPrefix(), this.fields.get(A_namePrefix)))
                        && (isEqualStrs(nameField.getSuffix(), this.fields.get(A_nameSuffix)))) {
                    isDiff = false;
                    break;
                }
            }
            break;
        case address:
            List<Fields> addresses = oldContact.getFields(Type.address);
            if (Flag.home == flag) {
                for (Fields address : addresses) {
                    if (Flag.home == address.getFlag()) {
                        isDiff = isHomeAddressUpdated((AddressField) address.getFieldValue());
                        break;
                    }
                }
            } else if (Flag.work == flag) {
                for (Fields address : addresses) {
                    if (Flag.work == address.getFlag()) {
                        isDiff = isWorkAddressUpdated((AddressField) address.getFieldValue());
                        break;
                    }
                }
            }
            break;
        case birthday:
            List<Fields> dates = oldContact.getFields(Type.birthday);
            if (dates.size() > 0) {
                BirthdayField birth = (BirthdayField) dates.get(0).getFieldValue();
                if (isEqualStrs(birth.toString(), this.fields.get(A_birthday))) {
                    isDiff = false;
                    break;
                }
            }
            break;
        case anniversary:
            dates = oldContact.getFields(Type.anniversary);
            if (dates.size() > 0) {
                AnniversaryField ann = (AnniversaryField) dates.get(0).getFieldValue();
                if (isEqualStrs(ann.toString(), this.fields.get(A_anniversary))) {
                    isDiff = false;
                    break;
                }
            }
            break;
        case nickname:
            isDiff = isSimpleFieldUpdated(oldContact, Type.nickname, A_nickname);
            break;
        case company:
            isDiff = isSimpleFieldUpdated(oldContact, Type.company, A_company);
            break;
        case jobTitle:
            isDiff = isSimpleFieldUpdated(oldContact, Type.jobTitle, A_jobTitle);
            break;
        case link:
            isDiff = isSimpleFieldUpdated(oldContact, Type.link, A_homeURL);
            break;
        case notes:
            isDiff = isSimpleFieldUpdated(oldContact, Type.notes, A_notes);
            break;
        }
        OfflineLog.yab.debug("contact field compared, %s %s field is %s", flag == null ? "" : flag.name(), type.name(),
                isDiff ? "different" : "the same");
        return isDiff;
    }

    private boolean isSimpleFieldUpdated(Contact oldContact, Type type, String attr) {
        boolean isDiff = true;
        List<Fields> fields = oldContact.getFields(type);
        if (fields.size() > 0) {
            SimpleField field = (SimpleField) fields.get(0).getFieldValue();
            if (isEqualStrs(field.getValue(), this.fields.get(attr))) {
                isDiff = false;
            }
        }
        return isDiff;
    }

    private boolean isHomeAddressUpdated(AddressField oldAddrField) {
        return !(isEqualStrs(oldAddrField.getStreet(), this.fields.get(A_homeStreet))
                && isEqualStrs(oldAddrField.getCity(), this.fields.get(A_homeCity))
                && isEqualStrs(oldAddrField.getState(), this.fields.get(A_homeState))
                && isEqualStrs(oldAddrField.getZip(), this.fields.get(A_homePostalCode)) && isEqualStrs(
                oldAddrField.getCountry(), this.fields.get(A_homeCountry)));
    }

    private boolean isWorkAddressUpdated(AddressField oldAddrField) {
        return !(isEqualStrs(oldAddrField.getStreet(), this.fields.get(A_workStreet))
                && isEqualStrs(oldAddrField.getCity(), this.fields.get(A_workCity))
                && isEqualStrs(oldAddrField.getState(), this.fields.get(A_workState))
                && isEqualStrs(oldAddrField.getZip(), this.fields.get(A_workPostalCode)) && isEqualStrs(
                oldAddrField.getCountry(), this.fields.get(A_workCountry)));
    }

    private static Fields exportFieldsToRemove(String id) {
        Fields f = new Fields();
        f.setId(id);
        f.setOp(Action.REMOVE);
        return f;
    }

    private void exportAndAddNameField(Map<Type, List<Fields>> map, Action op) {
        if (this.hasNameFields()) {
            if (map.containsKey(Type.name)) {
                List<Fields> list = map.get(Type.name);
                list.add(this.exportNameField(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportNameField(op));
                map.put(Type.name, list);
            }
        }
    }

    private Fields exportNameField(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.name);
        NameField nf = new NameField();
        f.setFieldValue(nf);
        nf.setFirst(this.fields.get(A_firstName));
        nf.setLast(this.fields.get(A_lastName));
        nf.setMiddle(this.fields.get(A_middleName));
        nf.setPrefix(this.fields.get(A_namePrefix));
        nf.setSuffix(this.fields.get(A_nameSuffix));
        return f;
    }

    private void exportAndAddEmails(Map<Type, List<Fields>> map, Action op) {
        List<Fields> fields = new ArrayList<Fields>();
        for (String key : this.fields.keySet()) {
            if (key.startsWith(ContactData.PREFIX_EMAIL)) {
                fields.add(this.exportSimpleField(Action.ADD, Type.email, key));
            } else if (key.startsWith(ContactData.PREFIX_HOME_EMAIL)) {
                fields.add(this.exportSimpleField(Action.ADD, Type.email, key, Flag.personal));
            } else if (key.startsWith(ContactData.PREFIX_WORK_EMAIL)) {
                fields.add(this.exportSimpleField(Action.ADD, Type.email, key, Flag.work));
            }
        }
        map.put(Type.email, fields);
    }

    private void exportAndAddAddress(Map<Type, List<Fields>> map, Action op) {
        if (this.hasHomeAddressFields()) {
            if (map.containsKey(Type.address)) {
                List<Fields> list = map.get(Type.address);
                list.add(this.exportHomeAddress(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportHomeAddress(op));
                map.put(Type.address, list);
            }
        }
        if (this.hasWorkAddressFields()) {
            if (map.containsKey(Type.address)) {
                List<Fields> list = map.get(Type.address);
                list.add(this.exportWorkAddress(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportWorkAddress(op));
                map.put(Type.address, list);
            }
        }
    }

    private Fields exportHomeAddress(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.address);
        AddressField af = new AddressField();
        f.setFieldValue(af);
        f.setFlags(Flag.home);
        af.setStreet(this.fields.get(A_homeStreet));
        af.setCity(this.fields.get(A_homeCity));
        af.setState(this.fields.get(A_homeState));
        af.setZip(this.fields.get(A_homePostalCode));
        af.setCountry(this.fields.get(A_homeCountry));
        return f;
    }

    private Fields exportWorkAddress(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.address);
        AddressField af = new AddressField();
        f.setFieldValue(af);
        f.setFlags(Flag.work);
        af.setStreet(this.fields.get(A_workStreet));
        af.setCity(this.fields.get(A_workCity));
        af.setState(this.fields.get(A_workState));
        af.setZip(this.fields.get(A_workPostalCode));
        af.setCountry(this.fields.get(A_workCountry));
        return f;
    }

    private void exportAndAddBirthday(Map<Type, List<Fields>> map, Action op) {
        if (this.hasField(A_birthday)) {
            if (map.containsKey(Type.birthday)) {
                List<Fields> list = map.get(Type.birthday);
                list.add(this.exportBirthday(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportBirthday(op));
                map.put(Type.birthday, list);
            }
        }
    }

    private Fields exportBirthday(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.birthday);
        String date = this.fields.get(A_birthday);
        BirthdayField bf = new BirthdayField(date);
        f.setFieldValue(bf);
        return f;
    }

    private void exportAndAddAnniversary(Map<Type, List<Fields>> map, Action op) {
        if (this.hasField(A_anniversary)) {
            if (map.containsKey(Type.anniversary)) {
                List<Fields> list = map.get(Type.anniversary);
                list.add(this.exportAnniversary(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportAnniversary(op));
                map.put(Type.anniversary, list);
            }
        }
    }

    private Fields exportAnniversary(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.anniversary);
        String date = this.fields.get(A_anniversary);
        AnniversaryField bf = new AnniversaryField(date);
        f.setFieldValue(bf);
        return f;
    }

    private void exportAndAddPhones(Map<Type, List<Fields>> map, Action op) {
        List<Fields> fields = new ArrayList<Fields>();
        for (String key : this.fields.keySet()) {
            if (key.startsWith(ContactData.PREFIX_HOME_PHONE)) {
                fields.add(this.exportSimpleField(Action.ADD, Type.phone, key, Flag.personal));
            } else if (key.startsWith(ContactData.PREFIX_WORK_PHONE)) {
                fields.add(this.exportSimpleField(Action.ADD, Type.phone, key, Flag.work));
            } else if (key.startsWith(ContactData.PREFIX_MOBILE_PHONE)) {
                fields.add(this.exportSimpleField(Action.ADD, Type.phone, key, Flag.mobile));
            }
        }
        map.put(Type.phone, fields);
    }

    private void exportAndAddIms(Map<Type, List<Fields>> map, Action op) {
        List<Fields> fields = new ArrayList<Fields>();
        for (String key : this.fields.keySet()) {
            if (key.startsWith("imAddress")) {
                fields.add(this.exportSimpleField(Action.ADD, Type.yahooid, key));
            }
        }
        map.put(Type.yahooid, fields);
    }

    private void exportAndAddSimpleField(Map<Type, List<Fields>> map, Action op, Type type, String attr) {
        if (this.hasField(attr)) {
            if (map.containsKey(type)) {
                List<Fields> list = map.get(type);
                list.add(this.exportSimpleField(op, type, attr));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportSimpleField(op, type, attr));
                map.put(type, list);
            }
        }
    }

    private Fields exportSimpleField(Action op, Type type, String attr) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(type);
        SimpleField sf = new SimpleField(type, this.fields.get(attr));
        f.setFieldValue(sf);
        return f;
    }

    private Fields exportSimpleField(Action op, Type type, String attr, Flag flag) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(type);
        f.setFlags(flag);
        SimpleField sf = new SimpleField(type, this.fields.get(attr));
        f.setFieldValue(sf);
        return f;
    }

    private boolean hasField(String attr) {
        return this.fields.containsKey(attr) && !StringUtil.isNullOrEmpty(this.fields.get(attr));
    }

    private boolean hasNameFields() {
        return !Collections.disjoint(this.fields.keySet(), Ab.NAME_FIELDS);
    }

    private boolean hasHomeAddressFields() {
        return !Collections.disjoint(this.fields.keySet(), Ab.HOME_ADDRESS_FIELDS);
    }

    private boolean hasWorkAddressFields() {
        return !Collections.disjoint(this.fields.keySet(), Ab.WORK_ADDRESS_FIELDS);
    }

    /**
     * update to server is at Fields (e.g. name, address) level, so keep all
     * fields (e.g. firstName, lastName) of a Fields (e.g. name) is okay for
     * generating delta parts
     */
    private void importField(Fields field) {
        switch (field.getType()) {
        case name:
            NameField nf = (NameField) field.getFieldValue();
            this.fields.put(A_firstName, nf.getFirst());
            this.fields.put(A_middleName, nf.getMiddle());
            this.fields.put(A_lastName, nf.getLast());
            this.fields.put(A_namePrefix, nf.getPrefix());
            this.fields.put(A_nameSuffix, nf.getSuffix());
            break;
        case address:
            AddressField af = (AddressField) field.getFieldValue();
            switch (field.getFlag()) {
            case home:
                this.fields.put(A_homeStreet, af.getStreet());
                this.fields.put(A_homeCity, af.getCity());
                this.fields.put(A_homeState, af.getState());
                this.fields.put(A_homePostalCode, af.getZip());
                this.fields.put(A_homeCountry, af.getCountry());
                break;
            case work:
                this.fields.put(A_workStreet, af.getStreet());
                this.fields.put(A_workCity, af.getCity());
                this.fields.put(A_workState, af.getState());
                this.fields.put(A_workPostalCode, af.getZip());
                this.fields.put(A_workCountry, af.getCountry());
                break;
            }
            break;
        case birthday:
            BirthdayField bdf = (BirthdayField) field.getFieldValue();
            this.fields.put(A_birthday, bdf.toString());
            break;
        case anniversary:
            AnniversaryField abf = (AnniversaryField) field.getFieldValue();
            this.fields.put(A_anniversary, abf.toString());
            break;
        case email:
            SimpleField esf = (SimpleField) field.getFieldValue();
            if (field.getFlag() != null) {
                switch (field.getFlag()) {
                case home:
                case personal:
                    for (int i = 1; i < Integer.MAX_VALUE; i++) {
                        String emailNo = ContactData.PREFIX_HOME_EMAIL + i;
                        if (StringUtil.isNullOrEmpty(this.fields.get(emailNo))) {
                            this.fields.put(emailNo, esf.getValue());
                            break;
                        }
                    }
                    break;
                case work:
                    for (int i = 1; i < Integer.MAX_VALUE; i++) {
                        String emailNo = ContactData.PREFIX_WORK_EMAIL + i;
                        if (StringUtil.isNullOrEmpty(this.fields.get(emailNo))) {
                            this.fields.put(emailNo, esf.getValue());
                            break;
                        }
                    }
                    break;
                }
            } else {
                if (StringUtil.isNullOrEmpty(this.fields.get(A_email))) {
                    this.fields.put(A_email, esf.getValue());
                } else {
                    for (int i = 2; i < Integer.MAX_VALUE; i++) {
                        String emailNo = ContactData.PREFIX_EMAIL + i;
                        if (StringUtil.isNullOrEmpty(this.fields.get(emailNo))) {
                            this.fields.put(emailNo, esf.getValue());
                            break;
                        }
                    }
                }
            }
            break;
        case phone:
            SimpleField psf = (SimpleField) field.getFieldValue();
            if (field.getFlag() != null) {
                switch (field.getFlag()) {
                case personal: // yahoo uses personal flag for home phones
                    if (StringUtil.isNullOrEmpty(this.fields.get(A_homePhone))) {
                        this.fields.put(A_homePhone, psf.getValue());
                    } else {
                        for (int i = 2; i < Integer.MAX_VALUE; i++) {
                            String phoneNo = ContactData.PREFIX_HOME_PHONE + i;
                            if (StringUtil.isNullOrEmpty(this.fields.get(phoneNo))) {
                                this.fields.put(phoneNo, psf.getValue());
                                break;
                            }
                        }
                    }
                    break;
                case work:
                    if (StringUtil.isNullOrEmpty(this.fields.get(A_workPhone))) {
                        this.fields.put(A_workPhone, psf.getValue());
                    } else {
                        for (int i = 2; i < Integer.MAX_VALUE; i++) {
                            String phoneNo = ContactData.PREFIX_WORK_PHONE + i;
                            if (StringUtil.isNullOrEmpty(this.fields.get(phoneNo))) {
                                this.fields.put(phoneNo, psf.getValue());
                                break;
                            }
                        }
                    }
                    break;
                case mobile:
                    if (StringUtil.isNullOrEmpty(this.fields.get(A_mobilePhone))) {
                        this.fields.put(A_mobilePhone, psf.getValue());
                    } else {
                        for (int i = 2; i < Integer.MAX_VALUE; i++) {
                            String phoneNo = ContactData.PREFIX_MOBILE_PHONE + i;
                            if (StringUtil.isNullOrEmpty(this.fields.get(phoneNo))) {
                                this.fields.put(phoneNo, psf.getValue());
                                break;
                            }
                        }
                    }
                }
                break;
            } else {
                // yahoo could return a field without flag
                if (StringUtil.isNullOrEmpty(this.fields.get(ContactData.PREFIX_PHONE))) {
                    this.fields.put(ContactData.PREFIX_PHONE, psf.getValue());
                } else {
                    for (int i = 2; i < Integer.MAX_VALUE; i++) {
                        String phoneNo = ContactData.PREFIX_PHONE + i;
                        if (StringUtil.isNullOrEmpty(this.fields.get(phoneNo))) {
                            this.fields.put(phoneNo, psf.getValue());
                            break;
                        }
                    }
                }
            }
            break;
        case company:
            SimpleField csf = (SimpleField) field.getFieldValue();
            this.fields.put(A_company, csf.getValue());
            break;
        case link:
            SimpleField lsf = (SimpleField) field.getFieldValue();
            this.fields.put(A_homeURL, lsf.getValue());
            break;
        case jobTitle:
            SimpleField jsf = (SimpleField) field.getFieldValue();
            this.fields.put(A_jobTitle, jsf.getValue());
            break;
        case nickname:
            SimpleField nsf = (SimpleField) field.getFieldValue();
            this.fields.put(A_nickname, nsf.getValue());
            break;
        case otherid:
        case yahooid:
            SimpleField osf = (SimpleField) field.getFieldValue();
            for (int i = 1; i < Integer.MAX_VALUE; i++) {
                String imAddrNo = ContactData.PREFIX_IM_ADDRESS + i;
                if (StringUtil.isNullOrEmpty(this.fields.get(imAddrNo))) {
                    this.fields.put(imAddrNo, osf.getValue());
                    break;
                }
            }
            break;
        case notes:
            SimpleField nosf = (SimpleField) field.getFieldValue();
            this.fields.put(A_notes, nosf.getValue());
            break;
        case guid:
            SimpleField gsf = (SimpleField) field.getFieldValue();
            this.fields.put(A_otherCustom1, "GUID:" + gsf.getValue());
            break;
        case custom:
            SimpleField cusf = (SimpleField) field.getFieldValue();
            this.fields.put(A_otherCustom2, "CUSTOM:" + cusf.getValue());
            break;
        default:
            break;
        }
    }

    public ParsedContact getParsedContact() throws ServiceException {
        return new ParsedContact(getFieldData());
    }

    private Map<String, String> getFieldData() {
        return this.fields;
    }

    public String toString() {
        return this.getFieldData().toString();
    }
}
