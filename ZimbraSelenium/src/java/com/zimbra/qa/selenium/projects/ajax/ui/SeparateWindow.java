/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
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

import java.util.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

public class SeparateWindow extends AbsSeparateWindow {

	// Windows that exist before this Show Original is opened
	protected List<String> existingWindowNames = null;
	
	// The selenium window name for this Window
	protected String DialogWindowName = null;
	
	/**
	 * Create a new Show Original Window
	 * 
	 * The Show Original will not have a window title.  So, we
	 * must use the window ID's from Selenium
	 * 
	 * The SeparateWindow object must be created before
	 * the window is opened, so that the harness will know
	 * which new window is opened.
	 *  
	 * @param application
	 */
	public SeparateWindow(AbsApplication application) {
		super(application);
		
		// Initialize existing names to empty
		existingWindowNames = new ArrayList<String>();
		
		// Use zInitializeWindowNames() and zSetWindowName()
		// to set the new window name
		this.DialogWindowName = null;
	}
	
		public static class Locators {
		
			public static final String openApptOnLaunchedWindow = "css= td[class='ZhAppContent']  table[class='ZhCalMonthTable'] div[class^='ZhCalMonthAppt'] >a";
			public static final String apptHeaderSubject = "css= td[class$='MsgHdrName']:contains('Subject')";
			public static final String apptHeaderOrganizer = "css= td[class$='MsgHdrName']:contains('Organizer')";
			public static final String apptValueSubject = "css= td[class$='MsgHdrValue']:contains('Test')";
		     
		}
	/**
	 * Call this before the Show Original is opened
	 */
	public void zInitializeWindowNames() throws HarnessException {
		logger.info(myPageName() + " zInitializeWindowNames()");
		
		// Get a list of existing window names before the Show Original is opened
		existingWindowNames = super.sGetAllWindowNames();
		
		// For logging
		for (String name : existingWindowNames) {
			logger.info("Existing name: "+ name);
		}

	}
	
	/**
	 * Determine if a new window opened
	 * Set DialogWindowName if found.
	 */
	public void zSetWindowName() throws HarnessException {
		logger.info(myPageName() + " zSetWindowName()");

		for (String name : super.sGetAllWindowNames()) {
			
			if ( name.contains("selenium_main_app_window") ) {
				
				// Main window
				logger.info("Already existing Name: "+ name);
				
			} else if ( name.contains("selenium_blank") ) {
				
				// Show Original, etc.
				logger.info("Found my Name: "+ name);
				this.DialogWindowName = name;
				this.DialogWindowID = name;
				return;
				
			} else if ( name.contains("undefined") ) {
				
				// Product Help
				logger.info("Found my Name: "+ name);
				this.DialogWindowName = name;
				this.DialogWindowID = name;
				return;
				
			} else {
				
				logger.info("Unhandled window name: "+ name);
				
			}
			
		}

	}
	

	/**
	 * Wait for the page to open.
	 * 
	 * Since the show original window doesn't have a title,
	 * this method waits for a new window to open, then assumes
	 * that new window is the Show Original.
	 * 
	 */
	public void zWaitForActive() throws HarnessException {
		logger.info(myPageName() + " zWaitForActive()");

		if ( DialogWindowName == null ) {

			for (int i = 0; i < 15; i++) {

				zSetWindowName();
				SleepUtil.sleep(5000);
				if (DialogWindowName != null ) {
					// Found it
					return;
				}

				logger.info("Waiting a second ...");
				SleepUtil.sleep(1000);
			}
		}else if ( DialogWindowName.contains("selenium_blank")){
			if (DialogWindowName != null ) {
				this.sSelectWindow(DialogWindowName);
				this.sWindowFocus();
				// Found it
				return;
			}

		}else{

		throw new HarnessException("Window never became active!");
		}
	}
	
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		if ( this.DialogWindowName == null )
			throw new HarnessException("Window Title is null.  Use zSetWindowName() first.");
		
		for (String name : super.sGetAllWindowNames()) {
			logger.info("Window name: "+ name);
			if ( name.toLowerCase().contains(DialogWindowName.toLowerCase()) ) {
				logger.info("zIsActive() = true ... title = "+ DialogWindowName);
				return (true);
			}
		}
		
		logger.info("zIsActive() = false");
		return (false);
		
	}


	@Override
	public String myPageName() {
		return (this.getClass().getCanonicalName());
	}

}
