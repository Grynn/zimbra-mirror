/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.client;

public class SavedSearch extends MailboxItem {
	public String mQuery; // Saved search query
	public String mTypes; // Saved search types
	public String mSortBy; // Saved search sort by	
    
    public SavedSearch() {
        mItemType = SAVEDSEARCH;
    }
}
