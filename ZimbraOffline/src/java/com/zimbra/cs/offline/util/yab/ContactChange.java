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

public class ContactChange {
    private int cid;
    private List<FieldChange> fieldChanges;
    private List<CategoryChange> categoryChanges;

    public ContactChange(int cid) {
        this.cid = cid;
    }

    public int getId() {
        return cid;
    }
    
    public void addFieldChange(FieldChange change) {
        if (fieldChanges == null) {
            fieldChanges = new ArrayList<FieldChange>();
        }
        fieldChanges.add(change);
    }

    public List<FieldChange> getFieldChanges() {
        return fieldChanges;
    }

    public void addCategoryChange(CategoryChange change) {
        if (categoryChanges == null) {
            categoryChanges = new ArrayList<CategoryChange>();
        }
        categoryChanges.add(change);
    }

    public List<CategoryChange> getCategoryChanges() {
        return categoryChanges;
    }

    public Element toXml(Document doc, String tag) {
        Element e = doc.createElement(tag);
        if (cid != -1) {
            e.setAttribute(Contact.CID, String.valueOf(cid));
        }
        if (fieldChanges != null) {
            for (FieldChange change : fieldChanges) {
                e.appendChild(change.toXml(doc));
            }
        }
        if (categoryChanges != null) {
            for (CategoryChange change : categoryChanges) {
                e.appendChild(change.toXml(doc));
            }
        }
        return e;
    }
}
