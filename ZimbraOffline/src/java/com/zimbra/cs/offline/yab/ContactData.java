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
package com.zimbra.cs.offline.yab;

import com.zimbra.cs.offline.util.yab.NameField;
import com.zimbra.cs.offline.util.yab.AddressField;
import com.zimbra.cs.offline.util.yab.DateField;
import com.zimbra.cs.offline.util.yab.SimpleField;
import com.zimbra.cs.offline.util.yab.Flag;
import com.zimbra.cs.offline.util.yab.Contact;
import com.zimbra.cs.offline.util.yab.Field;
import com.zimbra.cs.offline.util.yab.ContactChange;
import com.zimbra.cs.offline.util.yab.FieldChange;

import java.util.Map;
import java.util.HashMap;
import java.io.Serializable;

import static com.zimbra.cs.mailbox.Contact.*;
import com.zimbra.cs.mime.ParsedContact;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.util.DateUtil;

public class ContactData implements Serializable {
    private Map<String, Field> fields;

    private static final String[] NAME_FIELDS =
        { A_firstName, A_middleName, A_lastName, A_namePrefix, A_nameSuffix };

    private static final String[] WORK_ADDRESS_FIELDS =
        { A_workStreet, A_workCity, A_workState, A_workPostalCode, A_workCountry };

    private static final String[] HOME_ADDRESS_FIELDS =
        { A_homeStreet, A_homeCity, A_homeState, A_homePostalCode, A_homeCountry };

    private static final String SERVICE_YAHOO = "yahoo";
    private static final String SERVICE_AOL = "aol";
    private static final String SERVICE_MSN = "msn";
    private static final String SERVICE_OTHER = "other";

    public static ParsedContact getParsedContact(Contact contact)
        throws ServiceException {
        return new ContactData(contact).getParsedContact();
    }

    public static Contact getContact(com.zimbra.cs.mailbox.Contact zcontact) {
        return new ContactData(zcontact).getContact();
    }
    
    public ContactData(Contact contact) {
        fields = new HashMap<String, Field>();
        for (Field field : contact.getFields()) {
            importField(field);
        }
    }

    public ContactData(com.zimbra.cs.mailbox.Contact contact) {
        fields = new HashMap<String, Field>();
        Map<String, String> zfields = contact.getFields();
        importField(A_firstName, getName(zfields));
        importField(A_homeStreet, getHomeAddress(zfields));
        importField(A_workStreet, getWorkAddress(zfields));
        importField(A_birthday, getBirthday(zfields));
        for (Map.Entry<String, String> entry : zfields.entrySet()) {
            String name = entry.getKey();
            importField(name, getSimple(name, entry.getValue()));
        }
    }

    private void importField(Field field) {
        if (field == null) return;
        if (field.isName()) {
            importField(A_firstName, field);
        } else if (field.isAddress()) {
            if (field.isHome()) {
                importField(A_homeStreet, field);
            } else if (field.isWork()) {
                importField(A_workStreet, field);
            }
        } else if (field.isDate()) {
            if (((DateField) field).isBirthday()) {
                importField(A_birthday, field);
            }
        } else if (field.isSimple()) {
            String name = getSimpleName((SimpleField) field);
            if (name != null) {
                importField(name, field);
            }
        }
    }
         
    private void importField(String name, Field value) {
        if (!fields.containsKey(name) && value != null) {
            fields.put(name, value);
        }
    }

    private static NameField getName(Map<String, String> fields) {
        if (containsAny(fields, NAME_FIELDS)) {
            NameField name = new NameField();
            name.setFirst(get(fields, A_firstName, ""));
            name.setMiddle(get(fields, A_middleName, ""));
            name.setLast(get(fields, A_lastName, ""));
            name.setPrefix(get(fields, A_namePrefix, ""));
            name.setSuffix(get(fields, A_nameSuffix, ""));
            return name;
        }
        return null;
    }

    private static AddressField getWorkAddress(Map<String, String> fields) {
        if (containsAny(fields, WORK_ADDRESS_FIELDS)) {
            AddressField addr = new AddressField();
            addr.setStreet(get(fields, A_workStreet, ""));
            addr.setCity(get(fields, A_workCity, ""));
            addr.setState(get(fields, A_workState, ""));
            addr.setZip(get(fields, A_workPostalCode, ""));
            addr.setCountry(get(fields, A_workCountry, ""));
            addr.setFlag(Flag.WORK);
            return addr;
        }
        return null;
    }

    private static AddressField getHomeAddress(Map<String, String> fields) {
        if (containsAny(fields, HOME_ADDRESS_FIELDS)) {
            AddressField addr = new AddressField();
            addr.setStreet(get(fields, A_homeStreet, ""));
            addr.setCity(get(fields, A_homeCity, ""));
            addr.setState(get(fields, A_homeState, ""));
            addr.setZip(get(fields, A_homePostalCode, ""));
            addr.setCountry(get(fields, A_homeCountry, ""));
            addr.setFlag(Flag.HOME);
            return addr;
        }
        return null;
    }

    private static DateField getBirthday(Map<String, String> fields) {
        String value = fields.get(A_birthday);
        return value != null ?
            DateField.birthday(DateUtil.parseDateSpecifier(value)) : null;
    }

    private static SimpleField getSimple(String name, String value) {
        switch (getAttribute(name)) {
        case company:
            return SimpleField.company(value);
        case jobTitle:
            return SimpleField.jobtitle(value);
        case email: case email2: case email3:
            return SimpleField.email(value);
        case notes:
            return SimpleField.notes(value);
        case homeURL:
            return SimpleField.link(value, Flag.HOME);
        case workURL:
            return SimpleField.link(value, Flag.WORK);
        case homeFax:
            return SimpleField.phone(value, Flag.HOME, Flag.FAX);
        case workFax:
            return SimpleField.phone(value, Flag.WORK, Flag.FAX);
        case homePhone: case homePhone2:
            return SimpleField.phone(value, Flag.HOME);
        case workPhone: case workPhone2:
            return SimpleField.phone(value, Flag.WORK);
        case otherPhone:
            return SimpleField.phone(value);
        case imAddress1: case imAddress2: case imAddress3:
            return getImAddress(value);
        default:
            return null;
        }
    }

    private String getSimpleName(SimpleField simple) {
        if (simple.isCompany()) {
            return A_company;
        } else if (simple.isJobtitle()) {
            return A_jobTitle;
        } else if (simple.isEmail()) {
            return getFirst(A_email, A_email2, A_email3);
        } else if (simple.isYahooid() || simple.isOtherid()) {
            return getFirst(A_imAddress1, A_imAddress2, A_imAddress3);
        } else if (simple.isLink()) {
            if (simple.isHome()) {
                return A_homeURL;
            } else if (simple.isWork()) {
                return A_workURL;
            }
        } else if (simple.isNotes()) {
            return A_notes;
        } else if (simple.isPhone()) {
            if (simple.isFlag(Flag.FAX)) {
                if (simple.isHome()) {
                    return A_homeFax;
                } else if (simple.isWork()) {
                    return A_workFax;
                }
            } else if (simple.isHome()) {
                return getFirst(A_homePhone, A_homePhone2);
            } else if (simple.isWork()) {
                return getFirst(A_workPhone, A_workPhone2);
            } else {
                return A_otherPhone;
            }
        }
        return null;
    }

    private String getFirst(String... names) {
        for (String name : names) {
            if (!fields.containsKey(name)) {
                return name;
            }
        }
        return null;
    }

    public ParsedContact getParsedContact() throws ServiceException {
        Map<String, String> zfields = new HashMap<String, String>();
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            String name = entry.getKey();
            Field field = entry.getValue();
            switch (getAttribute(name)) {
            case firstName:
                addName(zfields, (NameField) field);
                break;
            case homeStreet:
                addHomeAddress(zfields, (AddressField) field);
                break;
            case workStreet:
                addWorkAddress(zfields, (AddressField) field);
                break;
            case birthday:
                zfields.put(A_birthday, toString((DateField) field));
                break;
            case imAddress1: case imAddress2: case imAddress3: {
                SimpleField simple = (SimpleField) field;
                zfields.put(name, getImService(simple) + "://" + simple.getValue());
            }
            default:
                zfields.put(name, ((SimpleField) field).getValue());
            }
        }
        return new ParsedContact(zfields);
    }

    private static void addName(Map<String, String> zfields, NameField name) {
        zfields.put(A_firstName, name.getFirst());
        zfields.put(A_middleName, name.getMiddle());
        zfields.put(A_lastName, name.getLast());
        zfields.put(A_namePrefix, name.getPrefix());
        zfields.put(A_nameSuffix, name.getSuffix());
    }

    private static void addHomeAddress(Map<String, String> zfields, AddressField addr) {
        zfields.put(A_homeStreet, addr.getStreet());
        zfields.put(A_homeCity, addr.getCity());
        zfields.put(A_homeState, addr.getState());
        zfields.put(A_homePostalCode, addr.getZip());
        zfields.put(A_homeCountry, addr.getCountry());
    }

    private static void addWorkAddress(Map<String, String> zfields, AddressField addr) {
        zfields.put(A_workStreet, addr.getStreet());
        zfields.put(A_workCity, addr.getCity());
        zfields.put(A_workState, addr.getState());
        zfields.put(A_workPostalCode, addr.getZip());
        zfields.put(A_workCountry, addr.getCountry());
    }

    public Contact getContact() {
        Contact contact = new Contact();
        for (Field field : fields.values()) {
            contact.addField(field);
        }
        return contact;
    }
    
    public ContactChange getContactChange(int id, ContactData oldData) {
        Map<String, Field> oldFields = new HashMap<String, Field>(oldData.fields);
        ContactChange cc = new ContactChange();
        cc.setId(id);
        // Get added and updated fields
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            Field newField = entry.getValue();
            Field oldField = oldFields.remove(entry.getKey());
            if (oldField == null) {
                cc.addFieldChange(FieldChange.add(newField));
            } else if (!isUnchanged(newField, oldField)) {
                newField.setId(oldField.getId());
                cc.addFieldChange(FieldChange.update(newField));
            }
        }
        // Remaining old fields have been removed
        for (Field field : oldFields.values()) {
            cc.addFieldChange(FieldChange.remove(field.getId()));
        }
        return cc;
    }

    private static boolean isUnchanged(Field field1, Field field2) {
        if (field1.isName()) {
            NameField name1 = (NameField) field1;
            NameField name2 = (NameField) field2;
            return eq(name1.getFirst(),   name2.getFirst())  &&
                   eq(name1.getMiddle(),  name2.getMiddle()) &&
                   eq(name1.getLast(),    name2.getLast())   &&
                   eq(name1.getPrefix(),  name2.getPrefix()) &&
                   eq(name1.getSuffix(),  name2.getSuffix());
        } else if (field1.isAddress()) {
            AddressField addr1 = (AddressField) field1;
            AddressField addr2 = (AddressField) field2;
            return eq(addr1.getStreet(),  addr2.getStreet()) &&
                   eq(addr1.getCity(),    addr2.getCity())   &&
                   eq(addr1.getState(),   addr2.getState())  &&
                   eq(addr1.getZip(),     addr2.getZip())    &&
                   eq(addr1.getCountry(), addr2.getCountry());
        } else if (field1.isDate()) {
            DateField date1 = (DateField) field1;
            DateField date2 = (DateField) field2;
            return date1.getDate().equals(date2.getDate());
        } else if (field1.isSimple()) {
            SimpleField simple1 = (SimpleField) field1;
            SimpleField simple2 = (SimpleField) field2;
            return eq(simple1.getValue(), simple2.getValue());
        }
        return false;
    }

    private static String getImService(SimpleField field) {
        if (field.isYahooid()) return SERVICE_YAHOO;
        if (field.isFlag(Flag.AOL)) return SERVICE_AOL;
        if (field.isFlag(Flag.MSN)) return SERVICE_MSN;
        return SERVICE_OTHER;
    }

    private static SimpleField getImAddress(String value) {
        int i = value.indexOf("://");
        if (i == -1) {
            return SimpleField.otherid(value);
        }
        String service = value.substring(0, i);
        String id = value.substring(i + 3);
        if (service.equals(SERVICE_YAHOO)) {
            return SimpleField.yahooid(id);
        }
        SimpleField simple = SimpleField.otherid(value);
        if (service.equals(SERVICE_AOL)) {
            simple.setFlag(Flag.AOL);
        } else if (service.equals(SERVICE_MSN)) {
            simple.setFlag(Flag.MSN);
        }
        return simple;
    }

    private static Attr getAttribute(String name) {
        try {
            return Attr.fromString(name);
        } catch (ServiceException e) {
            throw new IllegalArgumentException("Unknown attribute: " + name);
        }
    }
    
    private static String toString(DateField date) {
        return date.getMonth() + '/' + date.getDay() + "/" +
               (date.getYear() != -1 ? Integer.toString(date.getYear()) : "0000");
    }

    private static boolean eq(Object o1, Object o2) {
        return o1 != null ? o1.equals(o2) : o2 == null;
    }

    private static <T> boolean containsAny(Map<T, ?> map, T[] keys) {
        for (T key : keys) {
            if (map.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    private static <T, V> V get(Map<T, V> map, T key, V def) {
        V value = map.get(key);
        return value != null ? value : def;
    }
}
