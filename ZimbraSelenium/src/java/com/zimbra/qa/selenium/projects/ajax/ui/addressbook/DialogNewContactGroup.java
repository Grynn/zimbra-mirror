/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class DialogNewContactGroup extends AbsDialog {
	
	public static class Locators {
		public static final String WINDOW_DIALOGNAME = "css=div#CreateContactGroupDialog";
		public static final String INPUT_GROUPNAME = "css=input#CreateContactGroupDialog_name";
		public static final String BUTTON_SAVE     = "css=div#CreateContactGroupDialog_button2";
		public static final String BUTTON_CANCEL   = "css=div#CreateContactGroupDialog_button1";
	}
	
	public DialogNewContactGroup(AbsApplication application, AbsTab page) {
		super(application, page);

		logger.info("new " + DialogNewContactGroup.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
						
		if ( !this.sIsElementPresent(Locators.WINDOW_DIALOGNAME) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(Locators.WINDOW_DIALOGNAME, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}
	

	public void zEnterGroupName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetGroupName("+ name +")");

		String locator = Locators.INPUT_GROUPNAME;
		
		this.sType(locator, name);
		this.zWaitForBusyOverlay();
		
	}
	
	
	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			locator = Locators.BUTTON_SAVE;
			page = null;
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = Locators.BUTTON_CANCEL;
			page = null;

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		this.zClick(locator);
		zWaitForBusyOverlay();
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (null);
	}
}
