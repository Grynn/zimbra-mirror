/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.client;

public class Tag extends MailboxItem {
	public String mColor; // Tag color
    
    public Tag() {
        mItemType = TAG;
    }
}
