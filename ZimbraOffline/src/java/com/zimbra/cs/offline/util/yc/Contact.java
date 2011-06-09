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
package com.zimbra.cs.offline.util.yc;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yc.Fields.Flag;
import com.zimbra.cs.offline.util.yc.Fields.Type;

public class Contact extends Entity {
    private String id = "";
    private boolean isConnection = false;
    private Action operation;
    private Map<Type, List<Fields>> fields; //may have several fields for the same type, even Flag couldn't tell them apart
    private String refid;
//    private List<Category> categories;

    public static final String TAG_NAME = "contacts";

    private static final String ID = "id";
    private static final String OP = "op";
    private static final String ISCONNECTION = "isConnection";
    private static final String FIELDS = "fields";
    private static final String REFID = "refid";

    public Contact() {
        fields = new HashMap<Type, List<Fields>>();
//        categories = new ArrayList<Category>();
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public Action getOp() {
        return this.operation;
    }
    
    public void setOp(Action op) {
        this.operation = op;
    }
    
//    public void addCategory(Category category) {
//        categories.add(category);
//    }

    public String getRefid() {
        return refid;
    }

    public void setRefid(String refid) {
        this.refid = refid;
    }

    public Collection<List<Fields>> getAllFields() {
        return this.fields.values();
    }
    
    public List<Fields> getFields(Type type) {
        return this.fields.get(type);
    }
    
    public void setFields(Map<Type, List<Fields>> fields) {
        this.fields = fields;
    }
    
    public void addField(Type type, Fields f) {
        List<Fields> fs = null;
        if (!this.fields.containsKey(type)) {
            fs = new ArrayList<Fields>();
            this.fields.put(type, fs);
        } else {
            fs = this.fields.get(type);
        }
        fs.add(f);
    }
    
    public boolean hasField(Type type) {
        return this.fields.containsKey(type);
    }
    
    public boolean hasFieldByFlag(Type type, Flag flag) {
        if (this.fields.containsKey(type)) {
            List<Fields> fs = this.fields.get(type);
            for (Fields f : fs) {
                if (flag.equals(f.getFlag())) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

//    public List<Category> getCategories() {
//        return categories;
//    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(TAG_NAME);
        Xml.appendElement(e, OP, this.operation.name().toLowerCase());
//        Xml.appendElement(e, ISCONNECTION, String.valueOf(this.isConnection));
        if (!StringUtil.isNullOrEmpty(this.id)) {
            Xml.appendElement(e, ID, String.valueOf(this.id));    
        }
        if (!StringUtil.isNullOrEmpty(this.refid)) {
            Xml.appendElement(e, REFID, String.valueOf(this.refid));
        }
        for (List<Fields> fieldList : this.getAllFields()) {
            for (Fields f : fieldList) {
                e.appendChild(f.toXml(e.getOwnerDocument()));
            }
        }
        return e;
    }

    public void extractFromXml(Element e) {
        for (Element child : Xml.getChildren(e)) {
            if (Contact.ID.equals(child.getTagName())) {
                this.id = child.getTextContent();
            } else if (Contact.REFID.equals(child.getTagName())) {
                this.refid = child.getTextContent();
            } else if (Contact.OP.equals(child.getTagName())) {
                this.operation = Action.getOp(child.getTextContent());
            } else if (Contact.ISCONNECTION.equals(child.getTagName())) {
                this.isConnection = Boolean.valueOf(child.getTextContent());
            } else if (Contact.FIELDS.equals(child.getNodeName())) {
                Fields field = new Fields();
                field.extractFromXml(child);
                this.addField(field.getType(), field);
            }
            // TODO category
        }
    }
    
    public static Contact extractFromXml(String xml) throws YContactException {
        Contact contact = null;
        try {
            DocumentBuilder docBuilder = Xml.newDocumentBuilder();
            Document doc = docBuilder.parse(new InputSource(new StringReader(xml)));
            Element root = doc.getDocumentElement();
            
            contact = new Contact();
            contact.extractFromXml(root);
        } catch (Exception e) {
            throw new YContactException("extract Contact from xml error", "", false, e, null);
        }
        return contact;
    }
    
    public static Element toXml(Document doc, List<Fields> fields, Action op, String id) {
        Element e = doc.createElement(Contact.TAG_NAME);
        Xml.appendElement(e, "op", op.name().toLowerCase());
        switch (op) {
        case ADD:
        case UPDATE:
            for (Fields f : fields) {
                Xml.appendElement(e, "fields", f.toXml(e.getOwnerDocument()));
            }
            break;
        case REMOVE:
            Xml.appendElement(e, "id", id);
        }
        return e;
    }
}
