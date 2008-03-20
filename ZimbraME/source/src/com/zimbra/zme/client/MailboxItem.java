/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
