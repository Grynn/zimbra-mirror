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
package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail;

/**
 * Represents a "Tag Picker" dialog
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogTagPicker extends AbsDialog {

	public static class Locators {
	
		// TODO:  See https://bugzilla.zimbra.com/show_bug.cgi?id=54173
		public static final String TagPicker_Div				= "css=div#ZmPickTagDialog";
		
		public static final String TagPicker_TagName_Locator	= TagPicker_Div + " div[id$='_inputDivId'] input";

		public static final String TagPickerButton_NEW_Locator	= TagPicker_Div + " div[id$='_buttons'] td[id^='New'] td[id$='_title']";
		public static final String TagPickerButton_OK_Locator	= TagPicker_Div + " div[id$='_buttons'] td[id^='OK'] td[id$='_title']";
		public static final String TagPickerButton_CANCEL_Locator	= TagPicker_Div + " div[id$='_buttons'] td[id^='Cancel'] td[id$='_title']";


	}
	
	
	public DialogTagPicker(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		logger.info("new " + DialogTag.class.getCanonicalName());
	}
	
	public void zSetTagName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetTagName("+ name +")");

		String locator = Locators.TagPicker_TagName_Locator;
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Tag name locator "+ locator +" is not present");
		}
		
		this.sType(locator, name);
		
	}
	
	public void zSetTagColor(String color) throws HarnessException {
		logger.info(myPageName() + " zSetTagColor("+ color +")");

		throw new HarnessException("implement me!");
		
	}
	
	public void zClickTreeTag(TagItem tag) throws HarnessException {

		logger.info(myPageName() + " zClickTreeTag(" + tag + ")");

		if (tag == null) {
			throw new HarnessException("tag must not be null");
		}
		
		

		String locator = null;

		if (MyTab instanceof PageMail) {

			locator = Locators.TagPicker_Div + " div[id='zti__ZmPickTagDialog__"+ tag.getId() +"'] td[id$='_textCell']";

		} else {
			throw new HarnessException("Unknown app type!");
		}

		// Click the tag
		this.zClick(locator);
		this.zWaitForBusyOverlay();

	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			locator = Locators.TagPickerButton_OK_Locator;
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = Locators.TagPickerButton_CANCEL_Locator;

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Button "+ button +" locator "+ locator +" not present!");
		}
		
		zClickAt(locator, "");
		
		zWaitForBusyOverlay();
		return (null);
	}

	public void zSubmit() throws HarnessException {
		   zClickButton(Button.B_OK);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
		// Need to implement for:
		
		// "Create New Tag"
		// "Tag name:"
		// "Blue", "Cyan", ..., "Orange", "More colors ..." (Tag color pulldown)
		// OK
		// Cancel
		
		throw new HarnessException("implement me");
	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.TagPicker_Div;

		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}

		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}

		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);

		//return ( this.sIsElementPresent(Locators.zTagDialogId) );
	}




}
