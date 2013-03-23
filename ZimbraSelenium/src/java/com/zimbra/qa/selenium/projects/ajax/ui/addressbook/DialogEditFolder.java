/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012 VMware, Inc.
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
package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * Represents a "Create New Folder" dialog box
 * 
 * Lots of methods not yet implemented. See
 * https://bugzilla.zimbra.com/show_bug.cgi?id=55923
 * <p>
 * 
 * @author Matt Rhoades
 * 
 */
public class DialogEditFolder extends com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder{

	public static final class Locators {
		
		public static final String zNoneColorId = "css=div.ZmColorMenu div#COLOR_0 td[id$='_title']";
		public static final String zBlueColorId = "css=div.ZmColorMenu div#COLOR_1 td[id$='_title']";
		public static final String zCyanColorId = "css=div.ZmColorMenu div#COLOR_2 td[id$='_title']";
		public static final String zGreenColorId = "css=div.ZmColorMenu div#COLOR_3 td[id$='_title']";
		public static final String zPurpleColorId = "css=div.ZmColorMenu div#COLOR_4 td[id$='_title']";
		public static final String zRedColorId = "css=div.ZmColorMenu div#COLOR_5 td[id$='_title']";
		public static final String zYellowColorId = "css=div.ZmColorMenu div#COLOR_6 td[id$='_title']";
		public static final String zPinkColorId = "css=div.ZmColorMenu div#COLOR_7 td[id$='_title']";
		public static final String zGrayColorId = "css=div.ZmColorMenu div#COLOR_8 td[id$='_title']";
		public static final String zOrangeColorId = "css=div.ZmColorMenu div#COLOR_9 td[id$='_title']";
	
	}
	
	public DialogEditFolder(AbsApplication application, AbsTab tab) {
		super(application, tab);
		logger.info("new " + DialogEditFolder.class.getCanonicalName());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/**
	 * Set the color pulldown
	 * 
	 * @param folder
	 */
	public void zSetNewColor(FolderColor color) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderColor(" + color + ")");
		
		/*
			For system folders, it seems that class=ImgSelectPullDownArrowHover
			is used rather than class=ImgSelectPullDownArrow
			
			Define actionLocator to handle both.
			
			<td id="DWT78_dropdown" class="ZDropDown">
              <div class="ImgSelectPullDownArrowHover"></div>
            </td>

			AND
			
			<td id="DWT78_dropdown" class="ZDropDown">
              <div class="ImgSelectPullDownArrow"></div>
            </td>

		 */
		String actionLocator = "css=div[id^='FolderProperties'] td[id$='_dropdown'].ZDropDown>div";
		String optionLocator = null;
		tracer.trace("Enter folder color " + color);

		if (color == null)
			throw new HarnessException("folder must not be null");

		if (color == FolderColor.MoreColors) {
			
			throw new HarnessException("'more colors' - implement me!");
			
		}
		
		if (color == FolderColor.Gray) {

			optionLocator = Locators.zGrayColorId;

		} else if (color == FolderColor.Blue) {

			optionLocator = Locators.zBlueColorId;

		} else if (color == FolderColor.Cyan) {

			optionLocator = Locators.zCyanColorId;

		} else if (color == FolderColor.Green) {

			optionLocator = Locators.zGreenColorId;

		} else if (color == FolderColor.Red) {

			optionLocator = Locators.zRedColorId;

		} else if (color == FolderColor.Orange) {

			optionLocator = Locators.zOrangeColorId;

		} else if (color == FolderColor.Yellow) {

			optionLocator = Locators.zYellowColorId;

		} else if (color == FolderColor.Purple) {

			optionLocator = Locators.zPurpleColorId;

		} else {
			throw new HarnessException("color " + color + " not yet implemented");
		}

		if ( actionLocator != null ) {

			if ( !this.sIsElementPresent(actionLocator) ) {
				throw new HarnessException("actionLocator is not present! "+ this.sGetHtmlSource());
			}

			zClick(actionLocator);
			this.zWaitForBusyOverlay();

		}


		if ( optionLocator != null ) {

			if ( !this.sIsElementPresent(optionLocator) ) {
				throw new HarnessException("optionLocator is not present! "+ this.sGetHtmlSource());
			}

			zClick(optionLocator);
			this.zWaitForBusyOverlay();

		}
	}

	public void zSetNewName(String folder) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderName(" + folder + ")");

		tracer.trace("Enter new folder name " + folder);

		if (folder == null)
			throw new HarnessException("folder must not be null");

		String locator = "css=div[id='FolderProperties'] div[id$='_content'] td.Field input";

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("unable to find folder name field " + locator);

		// For some reason, the text doesn't get entered on the first try
//		this.sFocus(locator);
//		this.zClick(locator);
//		zKeyboard.zTypeCharacters(folder);
//		if (!(sGetValue(locator).equalsIgnoreCase(folder))) {
//			sType(locator, folder);
//		}

		this.sType(locator, folder);
		this.zWaitForBusyOverlay();

	}

}
