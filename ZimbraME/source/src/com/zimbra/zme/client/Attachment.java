/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite, Network Edition.
 * Copyright (C) 2007, 2008 Zimbra, Inc.  All Rights Reserved.
 * ***** END LICENSE BLOCK *****
 */

package com.zimbra.zme.client;

import de.enough.polish.util.Locale;

public class Attachment extends MailboxItem {
	public String mType;
	public String mMsgId;
	public String mPart;
	
	public Attachment(String type,
			          String filename,
			          String msgId,
			          String part) {
        if (filename == null && type.compareTo("text/html") == 0)
            filename = Locale.get("msgItem.HtmlContent");
        mItemType = ATTACHMENT;
		mType = type;
		mName = filename;
		mMsgId = msgId;
		mPart = part;
	}
}
