package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

public abstract class AbsSeparateWindow extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsSeparateWindow.class);

	public static boolean IsDebugging = false;

	/**
	 * The Selenium ID for the separate window
	 */
	protected String DialogWindowID = null;

	/**
	 * The Selenium ID for the main window ("null" by default)
	 */
	protected String MainWindowID = "null";

	/**
	 * The title bar text
	 */
	protected String DialogWindowTitle = null;
	
	public AbsSeparateWindow(AbsApplication application) {
		super(application);

		logger.info("new " + AbsSeparateWindow.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sClick(java.lang.String)
	 */
	public void sClick(String locator) throws HarnessException {
		logger.info(myPageName() + " sClick("+ locator +")");


		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();

			super.sClick(locator);

			// Wait for the SOAP request to finish
			// zWaitForBusyOverlay();
			SleepUtil.sleepVeryLong();

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}


	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sType(java.lang.String, java.lang.String)
	 */
	public void sType(String locator, String value) throws HarnessException {
		logger.info(myPageName() + " sType("+ locator +", " + value +")");


		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();
			
			super.sType(locator, value);

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}

	}
	
	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sGetText(java.lang.String)
	 */
	public String sGetText(String locator) throws HarnessException {
		logger.info(myPageName() + " sGetText("+ locator +")");

		String text = "";
		
		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();
			
			text = super.sGetText(locator);

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}

		return (text);
	}

	/**
	 * Determine if a locator is present
	 * @param locator
	 * @return true if present, false otherwise
	 * @throws HarnessException
	 */
	public boolean sIsElementPresent(String locator) throws HarnessException {
		logger.info(myPageName() + " sIsElementPresent("+ locator +")");
		
		boolean present = false;
		
		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();

			present = super.sIsElementPresent(locator);

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}

		return (present);
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
			this.sWindowFocus();
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
	protected boolean zSetWindowIdByTitle(String title) throws HarnessException {

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
				logger.info("zIsActive() = true ... title = "+ this.DialogWindowID);
				return (true);
			}
		}
		
		logger.info("zIsActive() = false");
		return (false);
		
	}


}
