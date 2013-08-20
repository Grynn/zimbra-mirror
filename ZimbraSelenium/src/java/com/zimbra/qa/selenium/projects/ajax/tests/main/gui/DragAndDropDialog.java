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
package com.zimbra.qa.selenium.projects.ajax.tests.main.gui;


import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class DragAndDropDialog extends AjaxCommonTest {

	public DragAndDropDialog() {
		logger.info("New "+ DragAndDropDialog.class.getCanonicalName());
		
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;

		
	}
	
	
	@Bugs(	ids = "60492")
	@Test(	description = "Pop up any dialog and move it (use the 'About' dialog)",
			groups = { "functional" })
	public void DragAndDropDialog_01() throws HarnessException {
		
		
		
		//-- DATA
		
		
		FolderItem inbox = FolderItem.importFromSOAP(app.zGetActiveAccount(), SystemFolder.Inbox);

		
		
		
		//-- GUI
		
		
		// Click the Account -> About menu
		DialogInformational dialog = (DialogInformational)app.zPageMain.zToolbarPressPulldown(Button.B_ACCOUNT, Button.O_ABOUT);

		// Grab the 'Account' title
		// and drag to 
		// the 'Inbox' folder (for lack of any better destination)
		//
		String locatorSource = "css=div[id^='ZmMsgDialog'] tr[id$='_handle'] td[id$='_title']";
		String locatorDestination = "css=td[id='zti__main_Mail__" + inbox.getId() + "_textCell']";
		
		app.zPageMain.zDragAndDrop(locatorSource, locatorDestination);

		// Close the dialog
		dialog.zClickButton(Button.B_OK);

		
		//-- VERIFICATION
		
		// No verification, since the purpose of the test case is to move a dialog
		
	}


}
