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

public class AddResponse extends Response {
    private final List<Result> results;

    private static final String TAG = "add-response";

    private AddResponse() {
        results = new ArrayList<Result>();
    }

    public List<Result> getResults() {
        return results;
    }

    public static AddResponse fromXml(Element e) {
        return new AddResponse().parseXml(e);
    }

    private AddResponse parseXml(Element e) {
        if (!e.getTagName().equals(TAG)) {
            throw new IllegalArgumentException(
                "Not an '" + TAG + "' element: " + e.getTagName());
        }
        for (Element child : Xml.getChildren(e)) {
            String tag = child.getTagName();
            if (tag.equals(ContactResult.TAG)) {
                results.add(ContactResult.fromXml(child));
            } else if (tag.equals(ErrorResult.TAG)) {
                results.add(ErrorResult.fromXml(child));
            } else {
                throw new IllegalArgumentException(
                    "Unexpected result element: " + tag);
            }
        }
        if (results.isEmpty()) {
            throw new IllegalArgumentException(
                "Expected at least one result element");
        }
        return this;
    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(TAG);
        for (Result result : results) {
            e.appendChild(result.toXml(doc));
        }
        return e;
    }
}
