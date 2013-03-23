/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
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
/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui.briefcase;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

/**
 * Represents a "Add Document Version Notes" dialog box
 * <p>
 */
public class DialogAddVersionNotes extends AbsDialog {

	public static class Locators {
		public static final String zDialogClass = "DwtDialog";
		public static final String zDialogButtonsClass = "DwtDialogButtonBar";
		public static final String zDialogContentClassId = "DwtDialogBody";
	}

	public DialogAddVersionNotes(AbsApplication application, AbsTab tab) {
		super(application, tab);
		logger.info("new " + DialogAddVersionNotes.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = "class=" + Locators.zDialogClass;

		if (!this.sIsElementPresent(locator)) {
			return (false); // Not even present
		}

		if (!this.zIsVisiblePerPosition(locator, 0, 0)) {
			return (false); // Not visible per position
		}

		logger.info(myPageName() + " zIsActive() = true");
		return (true);

	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);

		String locator = null;

		if (button == Button.B_OK) {
			locator = "css=div[class='" + Locators.zDialogClass + "'] "
					+ "div[class='" + Locators.zDialogButtonsClass
					+ "'] td[class=ZWidgetTitle]:contains(OK)";
		} else if (button == Button.B_CANCEL) {
			locator = "css=div[class='" + Locators.zDialogClass + "'] "
					+ "div[class='" + Locators.zDialogButtonsClass
					+ "'] td[class=ZWidgetTitle]:contains(Cancel)";
		} else {
			throw new HarnessException("Button " + button + " not implemented");
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

		// if(zIsActive())
		// zGetDisplayedText("css=div[class=" + Locators.zDialogContentClassId +
		// "]");
		
		this.zClick(locator);

		return (null);
	}

	/**
	 * Enter text into the Add Document Version Notes dialog
	 * 
	 * @param notes
	 */
	private void zEnterVersionNotes(String notes) throws HarnessException {
		logger.info(myPageName() + " zEnterVersionNotes(" + notes + ")");

		tracer.trace("Enter version notes in text field " + notes);

		if (notes == null)
			throw new HarnessException("notes must not be null");

		String locator = "css=div[class=" + Locators.zDialogContentClassId
				+ "] textarea[id$='notes']";

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("unable to find body field " + locator);

		this.sFocus(locator);
		this.zClick(locator);
		this.sType(locator, notes);

		this.zWaitForBusyOverlay();
	}

	public void zDismissAddVersionNotesDlg(String parentWindow)
			throws HarnessException {
		zSelectWindow(PageBriefcase.pageTitle);
		//SleepUtil.sleepSmall();
		if (zIsWindowOpen(parentWindow)) {
			zSelectWindow(parentWindow);

			if (zIsActive()) {
				zEnterVersionNotes("notes"
						+ ZimbraSeleniumProperties.getUniqueString());

				zClickButton(Button.B_OK);
			}
		}
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText(" + locator + ")");

		if (locator == null)
			throw new HarnessException("locator was null");

		return (this.sGetText(locator));
	}
}
