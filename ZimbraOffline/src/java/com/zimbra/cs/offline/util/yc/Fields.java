/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"), you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.offline.util.yc;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.google.common.base.Objects;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.DateField.AnniversaryField;
import com.zimbra.cs.offline.util.yc.DateField.BirthdayField;

public class Fields extends Entity {

    private String id;
    private String editedBy;
    private Flag flags;
    private Type type;
    private FieldValue fieldValue;
    private Action op; // used for zd->server sync

    public static final String TAG_NAME = "fields";
    private static final String ID = "id";
    private static final String OP = "op";
    private static final String TYPE = "type";
    private static final String VALUE = "value";
    private static final String EDITEDBY = "editedBy";
    private static final String FLAGS = "flags";

    protected static final String GUID = "guid";
    protected static final String NICKNAME = "nickname";
    protected static final String EMAIL = "email";
    protected static final String YAHOOID = "yahooid";
    protected static final String OTHERID = "otherid";
    protected static final String PHONE = "phone";
    protected static final String JOBTITLE = "jobTitle";
    protected static final String COMPANY = "company";
    protected static final String NOTES = "notes";
    protected static final String LINK = "link";
    protected static final String CUSTOM = "custom";
    protected static final String NAME = "name";
    protected static final String ADDRESS = "address";
    protected static final String BIRTHDAY = "birthday";
    protected static final String ANNIVERSARY = "anniversary";

    public FieldValue getFieldValue() {
        return fieldValue;
    }

    public Type getType() {
        return this.type;
    }

    public Flag getFlag() {
        return this.flags;
    }

    public void setOp(Action op) {
        this.op = op;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setEditedBy(String editedBy) {
        this.editedBy = editedBy;
    }

    public void setFlags(Flag flags) {
        this.flags = flags;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public void setFieldValue(FieldValue fieldValue) {
        this.fieldValue = fieldValue;
        this.type = fieldValue.getType();
    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(Fields.TAG_NAME);
        if (this.id != null) {
            Xml.appendElement(e, Fields.ID, this.id);
        }
        if (this.op != null) {
            Xml.appendElement(e, Fields.OP, this.op.name().toLowerCase());
        }
        if (this.type != null) {
            Xml.appendElement(e, Fields.TYPE, this.type.name().toLowerCase());
        }
        if (this.fieldValue != null) {
            e.appendChild(this.fieldValue.toXml(e.getOwnerDocument()));
        }
        if (!StringUtil.isNullOrEmpty(this.editedBy)) {
            Xml.appendElement(e, Fields.EDITEDBY, this.editedBy);
        }
        if (this.flags != null) {
            Xml.appendElement(e, Fields.FLAGS, this.flags.name().toUpperCase());
        }
        return e;
    }

    @Override
    public void extractFromXml(Element e) {
        for (Element child : Xml.getChildren(e)) {
            String tagName = child.getTagName();
            if (Fields.ID.equals(tagName)) {
                this.id = child.getTextContent();
            } else if (Fields.TYPE.equals(tagName)) {
                String fieldType = child.getTextContent();
                if (Fields.NAME.equals(fieldType)) {
                    this.type = Type.name;
                    this.fieldValue = new NameField();
                    this.fieldValue.extractFromXml(child);
                } else if (Fields.ADDRESS.equals(fieldType)) {
                    this.type = Type.address;
                    this.fieldValue = new AddressField();
                    this.fieldValue.extractFromXml(child);
                } else if (Fields.BIRTHDAY.equals(fieldType)) {
                    this.type = Type.birthday;
                    this.fieldValue = new BirthdayField();
                    this.fieldValue.extractFromXml(child);
                } else if (Fields.ANNIVERSARY.equals(fieldType)) {
                    this.type = Type.anniversary;
                    this.fieldValue = new AnniversaryField();
                    this.fieldValue.extractFromXml(child);
                } else {
                    // single field,
                    // http://developer.yahoo.com/social/rest_api_guide/field-resource.html
                    this.fieldValue = new SimpleField(fieldType);
                    this.type = this.fieldValue.getType();
                }
            } else if (Fields.VALUE.equals(tagName)) {
                assert this.fieldValue != null;
                this.fieldValue.extractFromXml(child);
            } else if (Fields.EDITEDBY.equals(tagName)) {
                this.editedBy = child.getTextContent();
            } else if (Fields.FLAGS.equals(tagName)) {
                this.flags = Flag.getFlag(child.getTextContent());
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.fieldValue, this.flags, this.type);
    }

    @Override
    public boolean equals(Object obj) {
        return Objects.equal(this, obj);
    }

    public static enum Type {
        guid, nickname, email, yahooid, otherid, phone, jobTitle, company, notes, link, custom, name, address, birthday, anniversary
    }

    // 22 types yahoo lists,
    // http://developer.yahoo.com/social/rest_api_guide/field-resource.html#field-flag
    public static enum Flag {
        aol, blog, dotmac, external, fax, google, ibm, icq, irc, jabber, lcs, mobile, msn, pager, personal, photo, qq, skype, work, yahoophone, yjp, y360, home;

        public static Flag getFlag(String aflag) {
            for (Flag f : Flag.values()) {
                if (f.name().equalsIgnoreCase(aflag)) {
                    return f;
                }
            }
            return null;
        }
    }
}
