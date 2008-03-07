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

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import com.zimbra.cs.offline.util.Xml;
import com.zimbra.cs.offline.util.yab.Category;

public class SyncResponse extends Response {
    private long lastModifiedTime = -1;
    private int revision = -1;
    private List<Category> categories;
    private List<Result> results;
    private List<SyncResponseEvent> events;

    private static final String TAG = "sync-response";
    
    public static SyncResponse fromXml(Element e) {
        return new SyncResponse().parseXml(e);
    }

    private SyncResponse() {}

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    public int getRevision() {
        return revision;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Result> getResults() {
        return results;
    }

    public List<SyncResponseEvent> getEvents() {
        return events;
    }
    
    private SyncResponse parseXml(Element e) {
        if (!e.getTagName().equals(TAG)) {
            throw new IllegalArgumentException(
                "Not a '" + TAG + "' element: " + e.getTagName());
        }
        lastModifiedTime = Xml.getIntAttribute(e, "lmt");
        revision = Xml.getIntAttribute(e, "rev");
        ListIterator<Element> it = Xml.getChildren(e).listIterator();
        // Parse categories
        categories = new ArrayList<Category>();
        Category category;
        while ((category = parseCategory(it)) != null) {
            categories.add(category);
        }
        // Parse success or error results
        results = new ArrayList<Result>();
        Result result;
        while ((result = parseResult(it)) != null) {
            results.add(result);
        }
        // Parse events
        events = new ArrayList<SyncResponseEvent>();
        while (it.hasNext()) {
            events.add(SyncResponseEvent.fromXml(it.next()));
        }
        return this;
    }

    private static Category parseCategory(ListIterator<Element> it) {
        if (it.hasNext()) {
            Element e = it.next();
            if (e.getTagName().equals("category")) return Category.fromXml(e);
            it.previous();
        }
        return null;
    }

    private static Result parseResult(ListIterator<Element> it) {
        if (it.hasNext()) {
            Element e = it.next();
            String tag = e.getTagName();
            if (tag.equals("success")) return SuccessResult.fromXml(e);
            if (tag.equals("error")) return ErrorResult.fromXml(e);
            it.previous();
        }
        return null;
    }
}
