/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 Zimbra Software, LLC.
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
/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.calendar;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

/**
 * Represents a "Add external calendar" dialog box
 * 
 * It would probably be best to fix up DialogCreateNewFolder and
 * DialogCreateBriefcaseFolder classes to be generic.  It seems
 * this class only changes the locator for finding the folder
 * name input field.
 * 
 */
public class DialogCreateCalendarFolder extends AbsDialog {

	public static class Locators {

		public static final String zDialogId = "CreateNewFolderDialog";
		public static final String zDialogCSS = "css=div[id^='"+ zDialogId +"']";

		public static final String zNameField = zDialogCSS + " div[id$='_content'] input[id$='_name']";

		public static final String zOkButton = zDialogCSS + " div[id$='_buttons'] td[id^='OK_'] td[id$='_title']";
		public static final String zCancelButton = zDialogCSS + " div[id$='_buttons'] td[id^='Cancel_'] td[id$='_title']";
		public static final String zBackButton = zDialogCSS + " div[id$='_buttons'] td[id^='Back_'] td[id$='_title']";

	}

	public DialogCreateCalendarFolder(AbsApplication application, AbsTab tab) {
		super(application, tab);

		logger.info("new " + DialogCreateCalendarFolder.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);

		AbsPage page = null;
		String locator = null;

		if (button == Button.B_OK) {

			locator = Locators.zOkButton;

		} else if (button == Button.B_CANCEL) {

			locator = Locators.zCancelButton;

		} else if (button == Button.B_BACK) {

			locator = Locators.zBackButton;

		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Default behavior, click the locator
		//

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not present!");
		}

		this.zClickAt(locator, "0,0");
		
		this.zWaitForBusyOverlay();

		//Check the message queue
		//Stafpostqueue sp = new Stafpostqueue();
		//sp.waitForPostqueue();
		
		SleepUtil.sleepSmall();
		
		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zDialogCSS;
	
		if ( !this.sIsElementPresent(locator) ) {
			return (false);
		}

		if (!this.zIsVisiblePerPosition(locator, 0, 0)) {
			return (false); // Not visible per position
		}

		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}

	public void zEnterFolderName(String calendarname)  throws HarnessException {
		logger.info(myPageName() + " zEnterFolderName("+ calendarname +")");
		
		String locator = Locators.zNameField;
		
		this.sType(locator, calendarname);
		this.zWaitForBusyOverlay();
				
	}


}
