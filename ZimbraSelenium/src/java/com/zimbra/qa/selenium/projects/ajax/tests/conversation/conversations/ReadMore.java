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
package com.zimbra.qa.selenium.projects.ajax.tests.conversation.conversations;

import java.io.*;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;


public class ReadMore extends PrefGroupMailByConversationTest {

	
	public ReadMore() throws HarnessException {
		logger.info("New "+ ReadMore.class.getCanonicalName());
		

	}
	
	
	@Test(	description = "Use the 'Read More' button to scroll through the conversation content",
			groups = { "functional" })
	public void ViewMail_01() throws HarnessException {
		
		
		//-- DATA
		
		final String subject = "ReadMore13674340693103";

		final String mimeFolder = ZimbraSeleniumProperties.getBaseDirectory() + "/data/public/mime/email11";
		LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));


		
		//-- GUI
		
		
		// Click Get Mail button
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		// Select the message so that it shows in the reading pane
		app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);

		// Click on the "Read More" button
		app.zPageMail.zToolbarPressButton(Button.B_READMORE);
		
		//-- VERIFICATION
		
		// TODO: not sure how to verify that the scrollbar has moved?
		

	}


}
