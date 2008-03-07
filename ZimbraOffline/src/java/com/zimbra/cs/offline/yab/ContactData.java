package com.zimbra.cs.offline.yab;

import com.zimbra.cs.offline.yab.protocol.NameField;
import com.zimbra.cs.offline.yab.protocol.AddressField;
import com.zimbra.cs.offline.yab.protocol.DateField;
import com.zimbra.cs.offline.yab.protocol.SimpleField;
import com.zimbra.cs.offline.yab.protocol.Flag;
import com.zimbra.cs.offline.yab.protocol.Contact;
import com.zimbra.cs.offline.yab.protocol.Field;
import com.zimbra.cs.offline.yab.protocol.ContactChange;
import com.zimbra.cs.offline.yab.protocol.FieldChange;
import com.zimbra.common.util.DateUtil;
import com.zimbra.common.service.ServiceException;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

import static com.zimbra.cs.mailbox.Contact.*;
import com.zimbra.cs.mime.ParsedContact;

public class ContactData {
    private NameField name;
    private AddressField homeAddress;
    private AddressField workAddress;
    private DateField birthday;
    private DateField anniversary;
    private Map<String, SimpleField> otherFields;

    private static final String[] NAME_FIELDS = {
        A_firstName, A_middleName, A_lastName, A_namePrefix, A_nameSuffix
    };
    
    private static final String[] HOME_ADDRESS_FIELDS = {
        A_homeStreet, A_homeCity, A_homeState, A_homePostalCode, A_homeCountry
    };

    private static final String[] WORK_ADDRESS_FIELDS = {
        A_workStreet, A_workCity, A_workState, A_workPostalCode, A_workCountry
    };

    private static final String[] DATE_FIELDS = {
        A_birthday, A_otherAnniversary
    };
    
    private static final String[] OTHER_FIELDS = {
        A_homePhone, A_homePhone2, A_workPhone, A_workPhone2,
        A_email, A_email2, A_email3, A_workEmail1, A_workEmail2, A_workEmail3,
        A_imAddress1, A_imAddress2, A_workIM1, A_workIM2, A_homeFax, A_workFax,
        A_homeURL, A_workURL, A_nickname, A_company, A_jobTitle,
        A_mobilePhone, A_pager, A_notes
    };

    private static final String[] ALL_FIELDS =
        append(NAME_FIELDS, HOME_ADDRESS_FIELDS, WORK_ADDRESS_FIELDS,
               DATE_FIELDS, OTHER_FIELDS);

    private static String[] append(String[]... lists) {
        List<String> result = new ArrayList<String>();
        for (String[] list : lists) {
            result.addAll(Arrays.asList(list));
        }
        return result.toArray(new String[result.size()]);
    }
    
    public static ParsedContact importChanges(Contact contact)
            throws ServiceException {
        ContactData cd = new ContactData(contact.getFields());
        Map<String, String> fields = cd.getFields();
        // Clear fields that were not set
        for (String field : ALL_FIELDS) {
            if (!fields.containsKey(field)) fields.put(field, "");
        }
        return new ParsedContact(fields);
    }

    public static ParsedContact importNew(Contact contact)
            throws ServiceException {
        ContactData cd = new ContactData(contact.getFields());
        return new ParsedContact(cd.getFields());
    }

    public static Contact exportNew(com.zimbra.cs.mailbox.Contact contact) {
        return new ContactData(contact.getFields()).getContact();
    }

    public static ContactChange exportChanged(
            com.zimbra.cs.mailbox.Contact contact, Contact yabContact) {
        ContactData odata = new ContactData(yabContact.getFields());
        ContactData ndata = new ContactData(contact.getFields());
        return ndata.getContactChange(yabContact.getId(), odata);
    }

    public ContactData(Map<String, String> fields) {
        otherFields = new HashMap<String, SimpleField>();
        name = name(fields);
        homeAddress = address(fields, HOME_ADDRESS_FIELDS);
        workAddress = address(fields, WORK_ADDRESS_FIELDS);
        birthday = date(DateField.BIRTHDAY, fields.get(A_birthday));
        anniversary = date(DateField.ANNIVERSARY, fields.get(A_otherAnniversary));
        put(SimpleField.PHONE, fields, A_homePhone, Flag.HOME);
        put(SimpleField.PHONE, fields, A_homePhone2, Flag.HOME);
        put(SimpleField.PHONE, fields, A_workPhone, Flag.WORK);
        put(SimpleField.PHONE, fields, A_workPhone2, Flag.WORK);
        put(SimpleField.EMAIL, fields, A_email);
        put(SimpleField.EMAIL, fields, A_email2);
        put(SimpleField.EMAIL, fields, A_email3);
        put(SimpleField.EMAIL, fields, A_workEmail1, Flag.WORK);
        put(SimpleField.EMAIL, fields, A_workEmail2, Flag.WORK);
        put(SimpleField.EMAIL, fields, A_workEmail3, Flag.WORK);
        put(SimpleField.OTHERID, fields, A_imAddress1);
        put(SimpleField.OTHERID, fields, A_imAddress2);
        put(SimpleField.OTHERID, fields, A_workIM1, Flag.WORK);
        put(SimpleField.OTHERID, fields, A_workIM2, Flag.WORK);
        put(SimpleField.PHONE, fields, A_homeFax, Flag.HOME, Flag.FAX);
        put(SimpleField.PHONE, fields, A_workFax, Flag.WORK, Flag.FAX);
        put(SimpleField.LINK, fields, A_homeURL, Flag.HOME);
        put(SimpleField.LINK, fields, A_workURL, Flag.WORK);
        put(SimpleField.NICKNAME, fields, A_nickname);
        put(SimpleField.COMPANY, fields, A_company);
        put(SimpleField.JOBTITLE, fields, A_jobTitle);
        put(SimpleField.PHONE, fields, A_mobilePhone, Flag.MOBILE);
        put(SimpleField.PHONE, fields, A_pager, Flag.PAGER);
        put(SimpleField.NOTES, fields, A_notes);
    }

    public ContactData(List<Field> fields) {
        for (Field field : fields) addField(field);
    }
    
    private static void setYahooId(SimpleField... fields) {
        for (SimpleField field : fields) {
            if (field != null && field.getValue().endsWith("@yahoo.com")) {
                field.setName(SimpleField.YAHOOID);
                return;
            }
        }
    }

    private void put(String yabName, Map<String, String> fields,
                     String name, String... flags) {
        String value = fields.get(name);
        if (value != null) {
            otherFields.put(name, new SimpleField(yabName, value, flags));
        }
    }

    public void addField(Field field) {
        if (field.isSimple()) {
            name = (NameField) field;
        } else if (field.isDate()) {
            DateField date = (DateField) field;
            if (date.isBirthday()) {
                birthday = date;
            } else if (date.isAnniversary()) {
                anniversary = date;
            }
        } else if (field.isAddress()) {
            AddressField address = (AddressField) field;
            if (address.isWork()) {
                workAddress = address;
            } else if (address.isHome()) {
                homeAddress = address;
            }
        } else {
            String key = getKey(field);
            if (key != null) {
                otherFields.put(key, (SimpleField) field);
            }
        }
    }

    private String getKey(Field field) {
        String name = field.getName();
        if (name.equals(SimpleField.PHONE)) {
            if (field.isFlagSet(Flag.PAGER)) return A_pager;
            if (field.isFlagSet(Flag.MOBILE)) return A_mobilePhone;
            if (field.isHome()) {
                if (field.isFlagSet(Flag.FAX)) return A_homeFax;
                if (unset(A_homePhone)) return A_homePhone;
                if (unset(A_homePhone2)) return A_homePhone2;
            } else if (field.isWork()) {
                if (field.isFlagSet(Flag.FAX)) return A_workFax;
                if (unset(A_workPhone)) return A_workPhone;
                if (unset(A_workPhone2)) return A_workPhone2;
            }
        } else if (name.equals(SimpleField.EMAIL)) {
            if (field.isWork()) {
                if (unset(A_workEmail1)) return A_workEmail1;
                if (unset(A_workEmail2)) return A_workEmail2;
                if (unset(A_workEmail3)) return A_workEmail3;
            } else {
                if (unset(A_email)) return A_email;
                if (unset(A_email2)) return A_email2;
                if (unset(A_email3)) return A_email3;
            }
        } else if (name.equals(SimpleField.OTHERID) ||
                   name.equals(SimpleField.YAHOOID)) {
            if (field.isWork()) {
                if (unset(A_workIM1)) return A_workIM1;
                if (unset(A_workIM2)) return A_workIM2;
            } else {
                if (unset(A_imAddress1)) return A_imAddress1;
                if (unset(A_imAddress2)) return A_imAddress2;
            }
        } else if (name.equals(SimpleField.LINK)) {
            if (field.isHome()) return A_homeURL;
            if (field.isWork()) return A_workURL;
        } else {
            if (name.equals(SimpleField.NICKNAME)) return A_nickname;
            if (name.equals(SimpleField.JOBTITLE)) return A_jobTitle;
            if (name.equals(SimpleField.COMPANY)) return A_company;
            if (name.equals(SimpleField.NOTES)) return A_notes;
        }
        return null;
    }

    private boolean unset(String name) {
        return !otherFields.containsKey(name);
    }
    
    private NameField name(Map<String, String> fields) {
        NameField name = new NameField();
        name.setFirst(fields.get(A_firstName));
        name.setMiddle(fields.get(A_middleName));
        name.setLast(fields.get(A_lastName));
        name.setPrefix(fields.get(A_namePrefix));
        name.setSuffix(fields.get(A_nameSuffix));
        return name;
    }

    private AddressField address(Map<String, String> fields, String[] names,
                                 String... flags) {
        assert names.length == 5;
        AddressField address = new AddressField();
        address.setStreet(fields.get(names[0]));
        address.setCity(fields.get(names[1]));
        address.setState(fields.get(names[2]));
        address.setZip(fields.get(names[3]));
        address.setCountry(fields.get(names[4]));
        if (address.getStreet() != null && address.getCity() != null &&
            address.getState() != null && address.getZip() != null &&
            address.getCountry() != null) {
            address.setFlags(flags);
            return address;
        }
        return null;
    }

    private DateField date(String yabName, String value) {
        if (value == null) return null;
        DateField date = new DateField(yabName);
        date.setDate(DateUtil.parseDateSpecifier(value));
        if (date.getYear() == 0) date.setYear(-1);
        return date;
    }

    public Map<String, String> getFields() {
        Map<String, String> fields = new HashMap<String, String>();
        put(fields, A_firstName, name.getFirst());
        put(fields, A_middleName, name.getMiddle());
        put(fields, A_lastName, name.getLast());
        put(fields, A_namePrefix, name.getPrefix());
        put(fields, A_nameSuffix, name.getSuffix());
        if (homeAddress != null) {
            put(fields, A_homeStreet, homeAddress.getStreet());
            put(fields, A_homeState, homeAddress.getState());
            put(fields, A_homeCity, homeAddress.getCity());
            put(fields, A_homePostalCode, homeAddress.getZip());
            put(fields, A_homeCountry, homeAddress.getCountry());
        }
        if (workAddress != null) {
            put(fields, A_workStreet, workAddress.getStreet());
            put(fields, A_workState, workAddress.getState());
            put(fields, A_workCity, workAddress.getCity());
            put(fields, A_workPostalCode, workAddress.getZip());
            put(fields, A_workCountry, workAddress.getCountry());
        }
        if (birthday != null) {
            put(fields, A_birthday, dateString(birthday));
        }
        if (anniversary != null) {
            put(fields, A_otherAnniversary, dateString(anniversary));
        }
        for (Map.Entry<String, SimpleField> entry : otherFields.entrySet()) {
            put(fields, entry.getKey(), entry.getValue());
        }
        return fields;
    }

    private static String dateString(DateField date) {
        return date.getMonth() + '/' + date.getDay() + "/" +
               (date.getYear() != -1 ? date.getYear() : "0000");
    }
    
    private static void put(Map<String, String> fields, String name,
                            SimpleField field) {
        if (field != null) put(fields, name, field.getValue());
    }

    private static void put(Map<String, String> fields,
                            String name, String value) {
        if (value != null) fields.put(name, value);
    }

    public Contact getContact() {
        Contact contact = new Contact();
        if (name != null) contact.addField(name);
        if (homeAddress != null) contact.addField(homeAddress);
        if (workAddress != null) contact.addField(workAddress);
        if (birthday != null) contact.addField(birthday);
        if (anniversary != null) contact.addField(anniversary);
        for (SimpleField field : otherFields.values()) {
            contact.addField(field);
        }
        return contact;
    }

    public ContactChange getContactChange(int cid, ContactData newData) {
        ContactChange cc = new ContactChange(cid);
        add(cc, nameChange(name, newData.name));
        add(cc, addressChange(homeAddress, newData.homeAddress));
        add(cc, addressChange(workAddress, newData.workAddress));
        add(cc, dateChange(birthday, newData.birthday));
        add(cc, dateChange(anniversary, newData.anniversary));
        for (String name : OTHER_FIELDS) {
            add(cc, simpleChange(otherFields.get(name),
                                 newData.otherFields.get(name)));
        }
        return cc;
    }

    private void add(ContactChange cc, FieldChange fc) {
        if (fc != null) cc.addFieldChange(fc);
    }

    private static FieldChange nameChange(NameField oname,
                                          NameField nname) {
        assert oname != null && nname != null;
        assert oname.getId() != -1;
        if (eq(oname.getFirst(), nname.getFirst()) &&
            eq(oname.getMiddle(), nname.getMiddle()) &&
            eq(oname.getLast(), nname.getLast()) &&
            eq(oname.getPrefix(), nname.getPrefix()) &&
            eq(oname.getSuffix(), nname.getSuffix())) {
            // Field has not changed
            return null;
        }
        NameField changes = new NameField();
        changes.setId(oname.getId());
        // Updated field elements
        changes.setFirst(value(nname.getFirst()));
        changes.setMiddle(value(nname.getMiddle()));
        changes.setLast(value(nname.getLast()));
        changes.setPrefix(value(nname.getPrefix()));
        changes.setSuffix(value(nname.getSuffix()));
        // Unchanged field elements
        changes.setFirstSound(oname.getFirstSound());
        changes.setLastSound(oname.getLastSound());
        return FieldChange.update(changes);
    }

    private static FieldChange addressChange(AddressField oaddr,
                                             AddressField naddr) {
        if (oaddr == null) {
            return naddr != null ? FieldChange.add(naddr) : null;
        }
        assert oaddr.getId() != -1;
        if (naddr == null) {
            return FieldChange.remove(oaddr.getId());
        }
        if (eq(oaddr.getStreet(), naddr.getStreet()) &&
            eq(oaddr.getCity(), naddr.getCity()) &&
            eq(oaddr.getState(), naddr.getState()) &&
            eq(oaddr.getZip(), naddr.getZip()) &&
            eq(oaddr.getCountry(), naddr.getCountry())) {
            // Field has not been changed
            return null;
        }
        AddressField changes = new AddressField();
        changes.setId(oaddr.getId());
        changes.setStreet(value(naddr.getStreet()));
        changes.setCity(value(naddr.getCity()));
        changes.setState(value(naddr.getState()));
        changes.setZip(value(naddr.getZip()));
        changes.setCountry(value(naddr.getCountry()));
        return FieldChange.update(changes);
    }

    private static FieldChange dateChange(DateField odate, DateField ndate) {
        if (odate == null) {
            return ndate != null ? FieldChange.add(ndate) : null;
        }
        assert odate.getId() != -1;
        if (ndate == null) {
            return FieldChange.remove(odate.getId());
        }
        if (odate.getDay() == ndate.getDay() &&
            odate.getMonth() == ndate.getMonth() &&
            odate.getYear() == ndate.getYear()) {
            // No changes to field
            return null;
        }
        DateField changes = new DateField(
            odate.getName(), ndate.getDay(), ndate.getMonth(), ndate.getYear());
        changes.setId(odate.getId());
        return FieldChange.update(changes);
    }

    private static FieldChange simpleChange(SimpleField ofield, SimpleField nfield) {
        if (ofield == null) {
            return nfield != null ? FieldChange.add(nfield) : null;
        }
        assert ofield.getId() != -1;
        if (nfield == null) {
            return FieldChange.remove(nfield.getId());
        }
        if (eq(ofield.getValue(), nfield.getValue())) return null;
        SimpleField changes = new SimpleField(ofield.getName(), nfield.getValue());
        changes.setId(ofield.getId());
        return FieldChange.update(changes);
    }
    
    private static boolean eq(String s1, String s2) {
        return s1 != null ? s1.equals(s2) : s2 == null;
    }

    private static String value(String s) {
        return s != null ? s : "";
    }
}
