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
