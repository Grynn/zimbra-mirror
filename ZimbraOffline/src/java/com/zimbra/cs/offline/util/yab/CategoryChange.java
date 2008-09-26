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

/**
 * YAB category action.
 */
public class CategoryChange extends Entity {
    private final Type type;
    private final Category category;

    public enum Type {
        ADD("add-to-category"), REMOVE("remove-from-category");

        String tag;
        Type(String tag) { this.tag = tag; }
    }

    public static CategoryChange add(Category category) {
        return new CategoryChange(Type.ADD, category);
    }

    public static CategoryChange add(int id) {
        return add(new Category(id));
    }

    public static CategoryChange add(String name) {
        return add(new Category(name));
    }
    
    public static CategoryChange remove(Category category) {
        return new CategoryChange(Type.REMOVE, category);
    }
    
    public static CategoryChange remove(int id) {
        return remove(new Category(id));
    }

    public static CategoryChange remove(String name) {
        return remove(new Category(name));
    }
    
    private CategoryChange(Type type, Category category) {
        this.type = type;
        this.category = category;
    }

    public Type getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public Element toXml(Document doc) {
        return category.toXml(doc, type.tag);
    }
}

