/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite J2ME Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
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
