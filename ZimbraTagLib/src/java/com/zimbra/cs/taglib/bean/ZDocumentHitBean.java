/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2008, 2009, 2010, 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.client.ZDocumentHit;
import com.zimbra.client.ZDocument;
import java.util.Date;

public class ZDocumentHitBean extends ZSearchHitBean {

    private final ZDocumentHit mHit;

    public ZDocumentHitBean(ZDocumentHit hit) {
        super(hit, HitType.briefcase);
        mHit = hit;
    }

    public ZDocument getDocument() {
        return mHit.getDocument();
    }

    public String getDocId() {
        return mHit.getId();
    }

    public String getDocSortField() {
        return mHit.getSortField();
    }

    public Date getCreatedDate() {
        return new Date(mHit.getDocument().getCreatedDate());
    }

    public Date getModifiedDate() {
        return new Date(mHit.getDocument().getModifiedDate());
    }

    public Date getMetaDataChangedDate() {
        return new Date(mHit.getDocument().getMetaDataChangedDate());
    }

}
