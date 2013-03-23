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
package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

public class DialogInformational extends AbsDialog {

	public static class DialogWarningID {
		
		public static final DialogWarningID InformationalDialog = new DialogWarningID("ZmMsgDialog");

		protected String Id;
		public DialogWarningID(String id) {
			Id = id;
		}
	}
	
	protected String MyDivId = null;
	
	
	public DialogInformational(DialogWarningID dialogId, AbsApplication application, AbsTab tab) {
		super(application, tab);
	
		MyDivId = dialogId.Id;
		
		logger.info("new " + DialogInformational.class.getCanonicalName());

	}
	
	public String zGetWarningTitle() throws HarnessException {
		String locator = "css=div[id='"+ MyDivId +"'] td[id='"+ MyDivId +"_title']";
		return (zGetDisplayedText(locator));
	}
	
	public String zGetWarningContent() throws HarnessException {	
		String locator = "css=td[id^='MessageDialog'][class='DwtMsgArea']";
		return (zGetDisplayedText(locator));
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		if ( button == null )
			throw new HarnessException("button cannot be null");

		String locator = null;
		AbsPage page = null; 

		String buttonsTableLocator = "css=div[id='"+ MyDivId +"'] div[id$='_buttons']";
		
		if (button == Button.B_OK) {
			
			locator = buttonsTableLocator + " td[id^='OK_'] td[id$='_title']";
			
		} else if (button == Button.B_CANCEL) {

			locator = buttonsTableLocator + " td[id^='CANCEL_'] td[id$='_title']";

		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Click it
		zClickAt(locator,"0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if ( page != null ) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}
		
		// This dialog might send message(s), so wait for the queue
		Stafpostqueue sp = new Stafpostqueue();
		sp.waitForPostqueue();

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		if ( locator == null )
			throw new HarnessException("locator cannot be null");
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("locator cannot be found");
		
		return (this.sGetText(locator));
		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		
		if ( !this.sIsElementPresent(MyDivId) )
			return (false);
	
		if ( !this.zIsVisiblePerPosition(MyDivId, 0, 0) )
			return (false);
		
		return (true);
	}

}
