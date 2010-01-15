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

import com.zimbra.cs.zclient.ZDocument;
import com.zimbra.cs.zclient.ZDocumentHit;
import com.zimbra.cs.zclient.ZWikiHit;

import java.util.Date;

public class ZWikiHitBean extends ZSearchHitBean {

    private ZWikiHit mHit; 

    public ZWikiHitBean(ZWikiHit hit) {
        super(hit, HitType.wiki);
        mHit = hit;
    }

    public ZDocument getDocument() {
    	return mHit.getDocument();
    }

	public String getDocId() {
		return mHit.getId();
	}

	public float getDocScore() {
		return mHit.getScore();
	}

	public String getDocSortField() {
		return mHit.getSortField();
	}

    public Date getCreatedDate() {
        return new Date(mHit.getDocument().getCreatedDate()/1000);
    }

    public Date getModifiedDate() {
        return new Date(mHit.getDocument().getModifiedDate()/1000);
    }

    public Date getMetaDataChangedDate() {
        return new Date(mHit.getDocument().getMetaDataChangedDate()/1000);
    }

}