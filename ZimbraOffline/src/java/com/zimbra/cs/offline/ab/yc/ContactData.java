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
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.mime.ParsedContact;
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
        cd.exportAndAddPhone(fieldsMap, op);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.nickname, A_nickname);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.company, A_company);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.jobTitle, A_jobTitle);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.notes, A_notes);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.link, A_homeURL);
        cd.exportAndAddSimpleField(fieldsMap, op, Type.otherid, A_imAddress1);

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
                change = newContactData.exportNameField(Action.UPDATE);
                // TODO not 0, need to compare fields
                change.setId(oldContact.getFields(Type.name).get(0).getId());
            } else {
                change = newContactData.exportNameField(Action.ADD);
            }
            fieldsChangeList.add(change);
        } else {
            if (oldContact.hasField(Type.name)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(Type.name).get(0).getId());
                fieldsChangeList.add(change);
            }
        }
        if (newContactData.hasHomeAddressFields()) {
            Fields change = null;
            ;
            if (oldContact.hasFieldByFlag(Type.address, Flag.home)) {
                change = newContactData.exportNameField(Action.UPDATE);
                List<Fields> list = oldContact.getFields(Type.address);
                for (Fields f : list) {
                    if (Flag.home.equals(f.getFlag())) {
                        change.setId(f.getId());
                    }
                }
            } else {
                newContactData.exportNameField(Action.ADD);
            }
            fieldsChangeList.add(change);
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
            ;
            if (oldContact.hasFieldByFlag(Type.address, Flag.work)) {
                change = newContactData.exportNameField(Action.UPDATE);
                List<Fields> list = oldContact.getFields(Type.address);
                for (Fields f : list) {
                    if (Flag.work.equals(f.getFlag())) {
                        change.setId(f.getId());
                    }
                }
            } else {
                newContactData.exportNameField(Action.ADD);
            }
            fieldsChangeList.add(change);
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
            Fields change = oldContact.hasField(Type.birthday) ? newContactData.exportBirthday(Action.UPDATE)
                    : newContactData.exportBirthday(Action.ADD);
            if (oldContact.hasField(Type.birthday)) {
                change.setId(oldContact.getFields(Type.birthday).get(0).getId());
            }
            fieldsChangeList.add(change);
        } else {
            if (oldContact.hasField(Type.birthday)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(Type.birthday).get(0).getId());
                fieldsChangeList.add(change);
            }
        }

        if (newContactData.hasField(A_anniversary)) {
            Fields change = oldContact.hasField(Type.anniversary) ? newContactData.exportBirthday(Action.UPDATE)
                    : newContactData.exportBirthday(Action.ADD);
            if (oldContact.hasField(Type.anniversary)) {
                change.setId(oldContact.getFields(Type.anniversary).get(0).getId());
            }
            fieldsChangeList.add(change);
        } else {
            if (oldContact.hasField(Type.anniversary)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(Type.anniversary).get(0).getId());
                fieldsChangeList.add(change);
            }
        }

        if (newContactData.hasField(A_nickname)) {
            Fields change = oldContact.hasField(Type.nickname) ? newContactData.exportSimpleField(Action.UPDATE,
                    Type.nickname, A_nickname) : newContactData
                    .exportSimpleField(Action.ADD, Type.nickname, A_nickname);
            if (oldContact.hasField(Type.nickname)) {
                change.setId(oldContact.getFields(Type.nickname).get(0).getId());
            }
            fieldsChangeList.add(change);
        } else {
            if (oldContact.hasField(Type.nickname)) {
                Fields change = ContactData.exportFieldsToRemove(oldContact.getFields(Type.nickname).get(0).getId());
                fieldsChangeList.add(change);
            }
        }
        // TODO more fields
        return fieldsChangeList;
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
        if (this.hasField(A_email)) {
            if (map.containsKey(Type.email)) {
                List<Fields> list = map.get(Type.email);
                list.add(this.exportHomeEmail(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportHomeEmail(op));
                map.put(Type.email, list);
            }
        }

        if (this.hasField(A_email2)) {
            if (map.containsKey(Type.email)) {
                List<Fields> list = map.get(Type.email);
                list.add(this.exportWorkEmail(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportWorkEmail(op));
                map.put(Type.email, list);
            }
        }
    }

    private Fields exportHomeEmail(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.email);
        SimpleField sf = new SimpleField(Type.email, this.fields.get(A_email));
        f.setFieldValue(sf);
        f.setFlags(Flag.home);
        return f;
    }

    private Fields exportWorkEmail(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.email);
        SimpleField sf = new SimpleField(Type.email, this.fields.get(A_email2));
        f.setFieldValue(sf);
        f.setFlags(Flag.work);
        return f;
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

    private void exportAndAddPhone(Map<Type, List<Fields>> map, Action op) {
        if (this.hasField(A_homePhone)) {
            if (map.containsKey(Type.phone)) {
                List<Fields> list = map.get(Type.phone);
                list.add(this.exportHomePhone(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportHomePhone(op));
                map.put(Type.phone, list);
            }
        }

        if (this.hasField(A_workPhone)) {
            if (map.containsKey(Type.phone)) {
                List<Fields> list = map.get(Type.phone);
                list.add(this.exportWorkPhone(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportWorkPhone(op));
                map.put(Type.phone, list);
            }
        }

        if (this.hasField(A_mobilePhone)) {
            if (map.containsKey(Type.phone)) {
                List<Fields> list = map.get(Type.phone);
                list.add(this.exportMobilePhone(op));
            } else {
                List<Fields> list = new ArrayList<Fields>();
                list.add(this.exportMobilePhone(op));
                map.put(Type.phone, list);
            }
        }
    }

    private Fields exportHomePhone(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.phone);
        f.setFlags(Flag.home);
        SimpleField sf = new SimpleField(Type.phone, this.fields.get(A_homePhone));
        f.setFieldValue(sf);
        return f;
    }

    private Fields exportWorkPhone(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.phone);
        f.setFlags(Flag.work);
        SimpleField sf = new SimpleField(Type.phone, this.fields.get(A_workPhone));
        f.setFieldValue(sf);
        return f;
    }

    private Fields exportMobilePhone(Action op) {
        Fields f = new Fields();
        f.setOp(op);
        f.setType(Type.phone);
        f.setFlags(Flag.mobile);
        SimpleField sf = new SimpleField(Type.phone, this.fields.get(A_mobilePhone));
        f.setFieldValue(sf);
        return f;
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

            if (!StringUtil.isNullOrEmpty(this.fields.get(A_email))) {
                this.fields.put(A_email, esf.getValue());
            } else if (!StringUtil.isNullOrEmpty(this.fields.get(A_email2))) {
                this.fields.put(A_email2, esf.getValue());
            } else if (!StringUtil.isNullOrEmpty(this.fields.get(A_email3))) {
                this.fields.put(A_email3, esf.getValue());
            }
            break;
        case phone:
            SimpleField psf = (SimpleField) field.getFieldValue();
            switch (field.getFlag()) {
            case home:
                if (!StringUtil.isNullOrEmpty(this.fields.get(A_homePhone))) {
                    this.fields.put(A_homePhone, psf.getValue());
                } else {
                    this.fields.put(A_homePhone2, psf.getValue());
                }
                break;
            case work:
                if (!StringUtil.isNullOrEmpty(this.fields.get(A_workPhone))) {
                    this.fields.put(A_workPhone, psf.getValue());
                } else {
                    this.fields.put(A_workPhone2, psf.getValue());
                }
                break;
            case mobile:
                this.fields.put(A_mobilePhone, psf.getValue());
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
            SimpleField osf = (SimpleField) field.getFieldValue();
            if (!StringUtil.isNullOrEmpty(this.fields.get(A_imAddress1))) {
                this.fields.put(A_imAddress1, osf.getValue());
            } else if (!StringUtil.isNullOrEmpty(this.fields.get(A_imAddress2))) {
                this.fields.put(A_imAddress2, osf.getValue());
            } else if (!StringUtil.isNullOrEmpty(this.fields.get(A_imAddress3))) {
                this.fields.put(A_imAddress3, osf.getValue());
            }
            break;
        case notes:
            SimpleField nosf = (SimpleField) field.getFieldValue();
            this.fields.put(A_notes, nosf.getValue());
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
}
