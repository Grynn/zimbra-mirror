/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2012, 2013 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
import org.openqa.selenium.WebElement;

import com.zimbra.qa.selenium.framework.items.AppointmentItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

public class DialogFindAttendees extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "SEND_UPDATES_DIALOG";
	
	
	public DialogFindAttendees(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogFindAttendees.class.getCanonicalName());
	}
	public static class Locators {

		public static final String LocationPickerSerach="css=div[class='DwtDialog'] td[id$='_title']:contains('Search')";
		public static final String SelectLocationFromPicker="css=div[class='DwtDialog'] td[id$='_title']:contains('Select')";
		public static final String AddLocationFromPicker="css=div[class='DwtDialog']  td[id^='OK'] td[id$='_button2_title']";
		
		
		public static final String ShowOptionalLink = "css=td[id$='_show_optional']";
		public static final String ContactPickerSerachField = "id=ZmContactPicker_searchField";
		public static final String ContactPickerSerachButton = "css=td[id$='_title']:contains('Search')";
		public static final String ContactPickerFirstContact = "css=nobr";
		public static final String SelectContactFromPicker = "css=td[id^='DwtChooserButton']:contains('To:')";
		public static final String AddContactFromPicker = "css=td[id^='ZmContactPicker_button']:contains('OK')";
		public static final String MessageHeader = "css= div[class='MsgHeader']:contains('";
		
	}
	
	public static class Field {

		public static final Field ContactPickerSerachField = new Field("ContactPickerSerachField");
		
		private String field;

		private Field(String name) {
			field = name;
		}

		@Override
		public String toString() {
			return (field);
		}

	}
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);
		if ( button == null )
			throw new HarnessException("button cannot be null");
	
		String locator = null;
		AbsPage page = null;
		boolean waitForPostfix = false;

		if (button == Button.B_SEARCH) {

			locator = Locators.ContactPickerSerachButton;
			page = null;

		} else if (button == Button.B_SELECT_LOCATION) {

			locator = Locators.SelectLocationFromPicker;
			page = null;
		
		} else if (button == Button.B_OK) {

			locator = Locators.AddContactFromPicker;
			page = null;
	
		} else if (button == Button.B_CANCEL) {

			locator = "css=div[class='DwtDialog'] td[id$='_button1_title']";
			page = null;
			                              
		} else if (button == Button.B_CHOOSE_CONTACT_FROM_PICKER) {
			
			this.zClick(Locators.ContactPickerFirstContact);
			locator = Locators.SelectContactFromPicker;
			page = null;
			                              
		} else if (button == Button.B_SELECT_FIRST_CONTACT) {

			locator = Locators.ContactPickerFirstContact;
			page = null;
			                              
		} else {
			
			return ( super.zClickButton(button) );

		}
		// Make sure the locator was set
		if (locator == null) {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not present!");
		}
		this.sFocus(locator);
		this.sClickAt(locator, "");
		SleepUtil.sleepMedium();
		this.zWaitForBusyOverlay();
		// If page was specified, make sure it is active
		if ( page != null ) {
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		// This dialog could send messages, so wait for the queue
		if ( waitForPostfix ) {
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		}

		return (page);
	}
	public void zFill(IItem item) throws HarnessException {
		logger.info(myPageName() + ".zFill(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a MailItem
		if (!(item instanceof AppointmentItem)) {
			throw new HarnessException(
					"Invalid item type - must be AppointmentItem");
		}

		AppointmentItem appt = (AppointmentItem) item;

		// Subject
		if (appt.getAttendeeName() != null) {
			zFillField(Field.ContactPickerSerachField, appt.getAttendeeName());
		}



	}
	
	public void zFillField(Field field, String value) throws HarnessException {

		tracer.trace("Set " + field + " to " + value);

		String locator = null;
		// subject
		  if (field == Field.ContactPickerSerachField) {

			locator = Locators.ContactPickerSerachField;

			// calendar folder
		}  else {
			throw new HarnessException("not implemented for field " + field);
		}
	
		if (locator == null) {
			throw new HarnessException("locator was null for field " + field);
		}
	
		// Default behavior, enter value into locator field
		//

		// Make sure the button exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Field is not present field=" + field
					+ " locator=" + locator);
	
		
			if (ZimbraSeleniumProperties.isWebDriver()) {
				this.sClickAt(locator, "");
				this.clearField(locator);
				this.sClickAt(locator, "");
			}
			this.sClickAt(locator, "");
			this.sType(locator, value);
			SleepUtil.sleepSmall();

		this.zWaitForBusyOverlay();
	}
}


