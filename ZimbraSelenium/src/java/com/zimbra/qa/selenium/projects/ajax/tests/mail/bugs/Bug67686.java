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
package com.zimbra.qa.selenium.projects.ajax.tests.mail.bugs;

import java.io.*;

import org.testng.annotations.*;

import com.zimbra.common.soap.*;
import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.*;


public class Bug67686 extends PrefGroupMailByMessageTest {


	
	public Bug67686() {
		logger.info("New "+ Bug67686.class.getCanonicalName());


	}

	@Bugs(	ids = "67686, 69384")
	@Test(	description = "Verify bug 67686",
			groups = { "functional" })
	public void Bug67686_01() throws HarnessException {

		//-- DATA
		
		String subject = "subject13690880312762";

		String MimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/Bugs/Bug67686";
		LmtpInject.injectFile(ZimbraAccount.AccountZWC().EmailAddress, new File(MimeFolder));

		
		//-- GUI
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the item
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Reply the item
		FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_REPLYALL);

		// Send the message
		mailform.zSubmit();

		
		//-- VERIFICATION
		
		// Verify no draft messages exist
		FolderItem drafts = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Drafts);
		app.zGetActiveAccount().soapSend(
					"<SearchRequest xmlns='urn:zimbraMail' types='message'>"
				+		"<query>subject:("+ subject +") inid:"+ drafts.getId() +"</query>"
				+	"</SearchRequest>");

		
		Element[] nodes = app.zGetActiveAccount().soapSelectNodes("//mail:m");
		ZAssert.assertEquals(nodes.length, 0, "Verify no drafts are saved");
		
		
	}



}
