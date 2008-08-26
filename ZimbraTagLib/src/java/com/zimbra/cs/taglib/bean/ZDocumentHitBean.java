/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
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
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZDocumentHit;
import com.zimbra.cs.zclient.ZDocument;
import java.util.Date;

public class ZDocumentHitBean extends ZSearchHitBean {

    private ZDocumentHit mHit;

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

	public float getDocScore() {
		return mHit.getScore();
	}

	public String getDocSortField() {
		return mHit.getSortField();
	}

    public Date getCreatedDate() {
        return new Date(mHit.getDocument().getCreatedDate());
    }

    public Date getModifiedDate() {
        return new Date(mHit.getDocument().getModifiedDate()/1000);
    }

    public Date getMetaDataChangedDate() {
        return new Date(mHit.getDocument().getMetaDataChangedDate());
    }
  
}