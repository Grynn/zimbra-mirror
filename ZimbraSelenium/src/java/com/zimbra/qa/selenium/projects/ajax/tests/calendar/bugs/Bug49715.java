/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2013 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.ajax.tests.calendar.bugs;

import java.io.File;
import org.testng.annotations.Test;
import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.PrefGroupMailByMessageTest;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;

public class Bug49715 extends PrefGroupMailByMessageTest {
	
	public Bug49715() throws HarnessException {
		logger.info("New "+ Bug49715.class.getCanonicalName());
		super.startingPage =  app.zPageMail;
	}
	
	@Test(description = "Bug 49715 - Links in email messages to .ics files should provide method to add to calendar",
			groups = { "functional" })
			
	public void Bug49715_01() throws HarnessException {

		// -- Data Setup
		String subject = "test ics";
		String newCalFolder = ZimbraSeleniumProperties.getUniqueString();
		ZDate startUTC = new ZDate(2013, 02, 21, 12, 0, 0);
		ZDate endUTC   = new ZDate(2013, 02, 21, 12, 0, 0);
		final String mimeFile = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email08/mime02.txt";
		
		// Inject the message
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFile));
		app.zPageMail.zToolbarPressButton(Button.B_REFRESH);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
		app.zPageMail.zToolbarPressPulldown(Button.B_ICS_LINK_IN_BODY, Button.B_CREATE_NEW_CALENDAR);
		DialogCreateFolder dialog = new DialogCreateFolder(app, app.zPageCalendar);
		dialog.zEnterFolderName(newCalFolder);
		dialog.zClickButton(Button.B_OK);
		SleepUtil.sleepLong(); //client takes longer time

		// Make sure the folder was created on the ZCS server
		FolderItem folder = FolderItem.importFromSOAP(app.zGetActiveAccount(), newCalFolder);
		ZAssert.assertNotNull(folder, "Verify the new folder is found");
		ZAssert.assertEquals(folder.getName(), newCalFolder, "Verify the server and client folder names match");
		
		//-- Verification
		app.zGetActiveAccount().soapSend(
				"<SearchRequest xmlns='urn:zimbraMail' types='appointment' calExpandInstStart='"+ startUTC.addDays(-10).toMillis() +"' calExpandInstEnd='"+ endUTC.addDays(10).toMillis() +"'>"
			+		"<query>"+ "in:" + newCalFolder +"</query>"
			+	"</SearchRequest>");
		
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:appt");
		ZAssert.assertGreaterThan(nodes.length, 0, "Verify imported appointments");
	
	}
	
}