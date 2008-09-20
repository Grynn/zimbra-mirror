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
import com.zimbra.cs.offline.util.Xml;

public class ErrorResult extends Result {
    private int code;
    private String userMessage;
    private String debugMessage;
    private int retryAfter = -1;

    public static final String TAG = "error";

    private static final String CODE = "code";
    private static final String USER_MESSAGE = "user-message";
    private static final String DEBUG_MESSAGE = "debug-message";
    private static final String RETRY_AFTER = "retry-after";

    private ErrorResult() {}

    @Override
    public boolean isError() { return true; }
    
    public int getCode() {
        return code;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public int getRetryAfter() {
        return retryAfter;
    }

    public String getDebugMessage() {
        return debugMessage;
    }
 
    public static ErrorResult fromXml(Element e) {
        return new ErrorResult().parseXml(e);
    }

    private ErrorResult parseXml(Element e) {
        assert e.getTagName().equals(TAG);
        for (Element child : Xml.getChildren(e)) {
            String name = child.getTagName();
            if (name.equals(CODE)) {
                code = Xml.getIntValue(child);
            } else if (name.equals(USER_MESSAGE)) {
                userMessage = Xml.getTextValue(child);
            } else if (name.equals(DEBUG_MESSAGE)) {
                debugMessage = Xml.getTextValue(child);
            } else if (name.equals(RETRY_AFTER)) {
                retryAfter = Xml.getIntValue(child);
            } else {
                throw new IllegalArgumentException(
                    "Invalid '" + TAG + "' result element: " + name);
            }
        }
        return this;
    }

    @Override
    public Element toXml(Document doc) {
        Element e = doc.createElement(TAG);
        if (code != -1) {
            Xml.appendElement(e, CODE, code);
        }
        if (userMessage != null) {
            Xml.appendElement(e, USER_MESSAGE, userMessage);
        }
        if (debugMessage != null) {
            Xml.appendElement(e, DEBUG_MESSAGE, debugMessage);
        }
        if (retryAfter != -1) {
            Xml.appendElement(e, RETRY_AFTER, retryAfter);
        }
        return e;
    }
}
