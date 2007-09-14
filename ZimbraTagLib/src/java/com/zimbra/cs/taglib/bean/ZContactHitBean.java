/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * 
 * The contents of this file are subject to the Mozilla Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Server.
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s): 
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.cs.taglib.bean;

import com.zimbra.cs.zclient.ZContactHit;
import com.zimbra.cs.zclient.ZEmailAddress;

public class ZContactHitBean extends ZSearchHitBean {
    
    private ZContactHit mHit;
    
    public ZContactHitBean(ZContactHit hit) {
        super(hit, HitType.contact);
        mHit = hit;
    }

    public boolean getIsGroup() { return mHit.isGroup(); }
    
    public String getTagIds() { return mHit.getTagIds(); }    

    public String getFolderId() { return mHit.getFolderId(); }

    public String getRevision() { return mHit.getRevision(); }
    
    public String getFileAsStr() { return mHit.getFileAsStr(); } 

    public String getEmail() { return mHit.getEmail(); }

    public String getEmail2() { return mHit.getEmail2(); }

    public String getEmail3() { return mHit.getEmail3(); }

    public String getType() { return mHit.getType(); }

    public String getDlist() { return mHit.getDlist(); }

    public String toString() { return mHit.toString(); }

    public String getFullName() { return mHit.getFullName(); }
    
    /**
     * @return time in msecs
     */
    public long getMetaDataChangedDate() { return mHit.getMetaDataChangedDate(); }
    
    /**
     * @return first email from email/2/3 that is set, or an empty string
     */
    public String getDisplayEmail() {
        if (getIsGroup())
            return getDlist();
        else if (getEmail() != null && getEmail().length() > 0) 
            return getEmail();
        else if (getEmail2() != null && getEmail2().length() > 0) 
            return getEmail2();
        else if (getEmail3() != null && getEmail3().length() > 0) 
            return getEmail3();
        else
            return "";
    }
    
    /**
     *
     * @return the "full" email address suitable for inserting into a To/Cc/Bcc header
     */
    public String getFullAddress() {
        if (getIsGroup()) {
            return getDlist();
        } else {
            return new ZEmailAddress(getDisplayEmail(), null, getFileAsStr(), ZEmailAddress.EMAIL_TYPE_TO).getFullAddress();
        }
    }

    public String getImage() {
        if (getIsGroup())
            return "contacts/ImgGroup.gif";
        else
            return "contacts/ImgContact.gif";
    }

    public String getImageAltKey() {
        if (getIsGroup())
            return "ALT_CONTACT_CONTACT";
        else
            return "ALT_CONTACT_GROUP";
    }
}
