/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.cs.taglib.bean;

import java.util.Date;

import com.zimbra.common.mime.ContentType;
import com.zimbra.cs.zclient.ZDocument;

public class ZDocumentBean {

    private ZDocument mDoc;

    public ZDocumentBean(ZDocument doc) {
        mDoc = doc;
    }

    public Date getCreatedDate() {
        return new Date(mDoc.getCreatedDate());
    }

    public Date getModifiedDate() {
        return new Date(mDoc.getModifiedDate() / 1000);
    }

    public Date getMetaDataChangedDate() {
        return new Date(mDoc.getMetaDataChangedDate());
    }

    public String getId() {
        return mDoc.getId();
    }

    public String getName() {
        return mDoc.getName();
    }

    public String getFolderId() {
        return mDoc.getFolderId();
    }

    public String getVersion() {
        return mDoc.getVersion();
    }

    public String getEditor() {
        return mDoc.getEditor();
    }

    public String getCreator() {
        return mDoc.getCreator();
    }

    public String getRestUrl() {
        return mDoc.getRestUrl();
    }

    public boolean isWiki() {
        return mDoc.isWiki();
    }

    public String getContentType() {
        String contentType = mDoc.getContentType();
        return new ContentType(contentType).getContentType();
    }

    public long getSize() {
        return mDoc.getSize();
    }

    public String getTagIds() {
        return mDoc.getTagIds();
    }

}