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
package com.zimbra.cs.offline.yab.protocol;

import org.w3c.dom.Element;

import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;

import com.zimbra.cs.offline.util.Xml;

public class SyncResponse extends Response {
    private int lastModifiedTime = -1;
    private int revision = -1;
    private List<Category> categories;
    private List<Result> results;
    private List<SyncResponseEvent> events;

    private static final String TAG = "sync-response";
    
    public static SyncResponse fromXml(Element e) {
        return new SyncResponse().parseXml(e);
    }

    private SyncResponse() {}

    public int getLastModifiedTime() {
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
        Category category = parseCategory(it);
        if (category != null) {
            categories = new ArrayList<Category>();
            do {
                categories.add(category);
            } while ((category = parseCategory(it)) != null);
        }
        // Parse success or error results
        Result result = parseResult(it);
        if (result != null) {
            results = new ArrayList<Result>();
            do {
                results.add(result);
            } while ((result = parseResult(it)) != null);
        }
        // Parse events
        if (it.hasNext()) {
            events = new ArrayList<SyncResponseEvent>();
            do {
                events.add(SyncResponseEvent.fromXml(it.next()));
            } while (it.hasNext());
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
