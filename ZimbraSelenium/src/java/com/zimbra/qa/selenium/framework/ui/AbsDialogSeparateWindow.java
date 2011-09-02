package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

public abstract class AbsDialogSeparateWindow extends AbsDialog {
	protected static Logger logger = LogManager.getLogger(AbsDialogSeparateWindow.class);

	public static boolean IsDebugging = false;

	/**
	 * The Selenium ID for the separate window
	 */
	protected String DialogWindowID = null;

	/**
	 * The Selenium ID for the main window ("null" by default)
	 */
	protected String MainWindowID = "null";

	protected String DialogWindowTitle = null;
	
	public AbsDialogSeparateWindow(AbsApplication application, AbsTab page) {
		super(application, page);

		logger.info("new " + AbsDialogSeparateWindow.class.getCanonicalName());

	}

	/**
	 * Click on a locator in the separate window
	 * @param locator
	 * @throws HarnessException
	 */
	protected void myClick(String locator) throws HarnessException {
		logger.info(myPageName() + " myClick("+ locator +")");


		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();

			// Make sure the locator exists
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("myClick: "+ locator +" is not present");
			}

			this.sClick(locator);

			// Wait for the SOAP request to finish
			SleepUtil.sleepVeryLong();
			// zWaitForBusyOverlay();

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}


	}

	/**
	 * Type text into a locator in the separate window
	 * @param locator
	 * @param value
	 * @throws HarnessException
	 */
	protected void myType(String locator, String value) throws HarnessException {
		logger.info(myPageName() + " myType("+ locator +", " + value +")");


		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();

			// Make sure the locator exists
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("myType: "+ locator +" is not present");
			}

			this.sType(locator, value);

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}

	}
	
	/**
	 * Get text by locator in the separate window
	 * @param locator
	 * @return text
	 * @throws HarnessException
	 */
	protected String myGetText(String locator) throws HarnessException {
		logger.info(myPageName() + " myGetText("+ locator +")");

		String text = "";
		
		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();

			// Make sure the locator exists
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("myGetText: "+ locator +" is not present");
			}

			text = this.sGetText(locator);

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}

		return (text);
	}

	/**
	 * Close the separate window (DefaultSelenium.close())
	 * @throws HarnessException
	 */
	public void zCloseWindow() throws HarnessException {
		logger.info(myPageName() + " zCloseWindow()");


		try {

			this.zSelectWindow(this.DialogWindowID);
			this.sClose();

		} finally {
			this.zSelectWindow(MainWindowID);
		}


	}

	/**
	 * Used to locate the window.  Window title is "Zimbra: <subject>"
	 * @param title A partial string that must be contained in the window title
	 */
	public void zSetWindowTitle(String title) throws HarnessException {
		this.DialogWindowTitle = title;
	}
	

	/**
	 * Set the Selenium Window ID based on partial window title
	 * @param title
	 * @return true if found, false otherwise
	 */
	protected boolean setWindowIdByTitle(String title) throws HarnessException {

		if ( IsDebugging ) {

			// Helpful for debugging, log all the names, titles, names
			for (String name: this.sGetAllWindowIds()) {
				logger.info("Window ID: "+ name);
			}

			for (String name: this.sGetAllWindowNames()) {
				logger.info("Window name: "+ name);
			}

			for (String t: this.sGetAllWindowTitles()) {
				logger.info("Window title: "+ t);
			}


		}

		for (String t : this.sGetAllWindowTitles()) {
			logger.info("Window title: "+ t);
			if ( t.toLowerCase().contains(title.toLowerCase()) ) {
				this.DialogWindowID = title;
				return (true);
			}
		}

		return (false);

	}

	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		if ( this.DialogWindowTitle == null )
			throw new HarnessException("Window Title is null.  Use zSetWindowTitle() first.");
		
		for (String title : this.sGetAllWindowTitles()) {
			logger.info("Window title: "+ title);
			if ( title.toLowerCase().contains(this.DialogWindowTitle.toLowerCase()) ) {
				this.DialogWindowID = title;
				logger.info("zIsActive() = false ... title = "+ this.DialogWindowID);
				return (true);
			}
		}
		
		logger.info("zIsActive() = false");
		return (false);
		
	}


}
