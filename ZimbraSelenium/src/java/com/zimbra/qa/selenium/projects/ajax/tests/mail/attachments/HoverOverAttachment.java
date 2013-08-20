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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.attachments;

import java.io.*;
import java.util.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.TooltipImage.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class HoverOverAttachment extends PrefGroupMailByMessageTest {



	public HoverOverAttachment() {
		logger.info("New "+ HoverOverAttachment.class.getCanonicalName());

	}

	@DataProvider(name = "DataProviderMimeWithImageAttachments")
	public Object[][] DataProviderDeleteKeys() {
		return new Object[][] {
				new Object[] { "subject13715117780534", "/data/public/mime/email12/mime01.txt" },
				new Object[] { "subject13715024766995", "/data/public/mime/email12/mime02.txt" },
				new Object[] { "subject13715024846237", "/data/public/mime/email12/mime03.txt" },
				new Object[] { "subject13715020881915", "/data/public/mime/email12/mime04.txt" },
		};
	}

	@Bugs(	ids = "82807")
	@Test(	description = "Hover over an image attachment",
			dataProvider = "DataProviderMimeWithImageAttachments",
			groups = { "functional" })
	public void HoverOverAttachment_01(String subject, String path) throws HarnessException {

		//-- DATA


		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + path;
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));




		//-- GUI

		// Click on Get Mail to refresh the folder list
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message
		DisplayMail display = (DisplayMail)app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		List<AttachmentItem> attachments = display.zListGetAttachments();
		ZAssert.assertEquals(attachments.size(), 1, "Verify the attachment appears");

		TooltipImage tooltip = (TooltipImage)display.zListAttachmentItem(Action.A_HOVEROVER, attachments.get(0));


		//-- VERIFICATION

		ZAssert.assertTrue(tooltip.zIsActive(), "Verify the tooltip shows");
		ZAssert.assertNotNull(tooltip.zGetField(Field.URL), "Verify the image URL");

	}	



}
