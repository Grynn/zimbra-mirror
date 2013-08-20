/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attributes;

import java.io.*;
import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;

public class ZimbraAttachmentsViewInHtmlOnlyTrue extends PrefGroupMailByMessageTest {

	
	
	public ZimbraAttachmentsViewInHtmlOnlyTrue() {
		
		super.startingAccountPreferences.put("zimbraAttachmentsViewInHtmlOnly", "TRUE");
		
	}

	@Test(
			description = "Verify 'download' link does not appear when zimbraAttachmentsViewInHtmlOnly = TRUE", 
			groups = { "functional" })
	public void ZimbraAttachmentsViewInHtmlOnlyTrue_01() throws HarnessException {

		//-- DATA
		
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email05/mime01.txt";
		final String subject = "subject151615738";
		final String attachmentname = "file.txt";
		
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));
		
		
		//-- GUI
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		DisplayMail display = (DisplayMail) app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		
		//-- VERIFICATION
		
		List<AttachmentItem> items = display.zListGetAttachments();
		ZAssert.assertEquals(items.size(), 1, "Verify one attachment is in the message");
		
		AttachmentItem found = null;
		for ( AttachmentItem item : items ) {
			if ( item.getAttachmentName().equals(attachmentname)) {
				found = item;
				break;
			}
		}
		ZAssert.assertNotNull(found, "Verify the attachment appears in the list (by file name)");
		
		String locator = found.getLocator() + " a[id$='_download']";
		ZAssert.assertFalse(app.zPageMail.sIsElementPresent(locator), "Verify the download link is not present");


	}

	
}
