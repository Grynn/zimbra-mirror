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
package com.zimbra.cs.offline.util.yab;

import org.w3c.dom.Element;
import org.w3c.dom.Document;

import java.util.List;
import java.util.ArrayList;

import com.zimbra.cs.offline.util.Xml;

/**
 * YAB contact information.
 */
public class Contact extends Entity {
    private int id = -1;
    private final List<Field> fields;
    private final List<Category> categories;

    public static final String TAG = "contact";
    
    private static final String CID = "cid";

    public Contact() {
        fields = new ArrayList<Field>();
        categories = new ArrayList<Category>();
    }

    public Contact(int id) {
        this();
        this.id = id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void addField(Field field) {
        fields.add(field);
    }

    public void addCategory(Category category) {
        categories.add(category);
    }

    public List<Field> getFields() {
        return fields;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public ContactChange asContactChange() {
        ContactChange change = new ContactChange();
        change.setId(id);
        for (Field field : fields) {
            change.addFieldChange(FieldChange.add(field));
        }
        for (Category category : categories) {
            change.addCategoryChange(CategoryChange.add(category));
        }
        return change;
    }

    public Element toXml(Document doc, String tag) {
        Element e = doc.createElement(tag);
        if (id != -1) e.setAttribute(CID, String.valueOf(id));
        for (Field field : fields) {
            e.appendChild(field.toXml(doc, field.getName()));
        }
        for (Category cat : categories) {
            e.appendChild(cat.toXml(doc));
        }
        return e;
    }

    @Override
    public Element toXml(Document doc) {
        return toXml(doc, TAG);
    }
    
    public static Contact fromXml(Element e) {
        Contact contact = new Contact();
        contact.parseXml(e);
        return contact;
    }
    
    private Contact parseXml(Element e) {
        id = Xml.getIntAttribute(e, CID);
        for (Element child : Xml.getChildren(e)) {
            if (child.getTagName().equals(Category.TAG)) {
                addCategory(Category.fromXml(child));
            } else {
                addField(Field.fromXml(child));
            }
        }
        return this;
    }
}
