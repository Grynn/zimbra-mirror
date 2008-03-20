/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
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
