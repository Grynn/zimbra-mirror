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

public class Contact extends MailboxItem {
	public String mEmail;
	public boolean mSelected;
	public boolean mNew;
	public String mFirstName;
	public String mLastName;
	public String mNameStr;
	public String mDesc;
	
	public Contact() {
		mItemType = CONTACT;
	}
	private void generateNames() {
		StringBuffer buf = new StringBuffer();
		if (mFirstName != null)
			buf.append(mFirstName).append(" ");
		if (mLastName != null)
			buf.append(mLastName);
		if (buf.length() == 0 && mEmail != null)
			buf.append(mEmail);
		else
			mDesc = mEmail;
		if (buf.length() > 0)
			mNameStr = buf.toString();
		else
			mNameStr = "<NONE>";
	}
	public String getName() {
		if (mNameStr == null)
			generateNames();
		return mNameStr;
	}
	public String getDescription() {
		if (mDesc == null)
			generateNames();
		return mDesc;
	}
}
