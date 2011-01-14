/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

import com.zimbra.cs.zclient.ZTag;

public class ZTagBean {

    private ZTag mTag;
    
    public ZTagBean(ZTag tag){
        mTag = tag;
    }

    public String getId() { return mTag.getId(); }

    public String getName() { return mTag.getName(); }

    public int getUnreadCount() { return mTag.getUnreadCount(); }
    
    public boolean getHasUnread() { return getUnreadCount() > 0; }

    public String getColor() { return mTag.getColor().name(); }
    
    public String getImage() {
        switch(mTag.getColor()) {
        case blue:
            return "zimbra/ImgTagBlue.png";
        case cyan:
            return "zimbra/ImgTagCyan.png";
        case green:
            return "zimbra/ImgTagGreen.png";
        case purple: 
            return "zimbra/ImgTagPurple.png";
        case red:
            return "zimbra/ImgTagRed.png";
        case yellow: 
            return "zimbra/ImgTagYellow.png";
        case orange:
        case defaultColor:
        default:
            return "zimbra/ImgTagOrange.png";
        }
    }
    
    public String getMiniImage() {
        switch(mTag.getColor()) {
        case blue:
            return "zimbra/ImgTagBlue.png";
        case cyan:
            return "zimbra/ImgTagCyan.png";
        case green:
            return "zimbra/ImgTagGreen.png";
        case purple: 
            return "zimbra/ImgTagPurple.png";
        case red:
            return "zimbra/ImgTagRed.png";
        case yellow: 
            return "zimbra/ImgTagYellow.png";
        case orange:
        case defaultColor:
        default:
            return "zimbra/ImgTagOrange.png";
        }
    }

}
