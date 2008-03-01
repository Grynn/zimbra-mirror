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
import com.zimbra.cs.offline.util.Xml;

public class SuccessResult extends Result {
    private AddAction addAction;
    private int cid = -1;
    private int catid = -1;

    public static final String TAG = "success";
    
    public static SuccessResult fromXml(Element e) {
        return new SuccessResult().parseXml(e);
    }
    
    private SuccessResult() {}

    @Override
    public boolean isSuccess() {
        return true;
    }
    
    public boolean isAdded() {
        return addAction == AddAction.ADD;
    }
    
    public boolean isMerged() {
        return addAction == AddAction.MERGE;
    }

    public AddAction getAddAction() {
        return addAction;
    }
    
    public int getContactId() {
        return cid;
    }

    public int getCategoryId() {
        return catid;
    }

    private SuccessResult parseXml(Element e) {
        assert e.getTagName().equals(TAG);
        addAction = AddAction.fromXml(e);
        cid = Xml.getIntAttribute(e, "cid");
        catid = Xml.getIntAttribute(e, "catid");
        return this;
    }
}
