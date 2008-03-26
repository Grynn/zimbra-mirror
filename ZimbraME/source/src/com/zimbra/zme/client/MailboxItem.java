/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite J2ME Client
 * Copyright (C) 2007, 2008 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.zme.client;

public abstract class MailboxItem {
    public static final int MESSAGE = 1;
    public static final int FOLDER  = 2;
    public static final int SAVEDSEARCH = 3;
    public static final int TAG = 4;
    public static final int ATTACHMENT = 5;
    public static final int APPOINTMENT = 6;
    public static final int CONTACT = 7;
    
    public int mItemType;
    public String mId;
    public String mName;
    
    public boolean hasChildren() {
        return false;
    }
    public boolean hasParent() {
        return false;
    }
    public String getName() {
    	return mName;
    }
    public String getDescription() {
    	return null;
    }
}
