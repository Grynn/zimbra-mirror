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
	
	/**
	 * Whether or not to switch focus when working in the separate window
	 */
	protected boolean DoChangeWindowFocus = false;
	
	public AbsSeparateWindow(AbsApplication application) {
		super(application);

		logger.info("new " + AbsSeparateWindow.class.getCanonicalName());

		DoChangeWindowFocus = false;
		
	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sClick(java.lang.String)
	 */
	public void sClick(String locator) throws HarnessException {
		logger.info(myPageName() + " sClick("+ locator +")");


		try {
			super.sSelectWindow(this.DialogWindowID);
			if ( DoChangeWindowFocus )			super.sWindowFocus();

			super.sClick(locator);

			// Wait for the SOAP request to finish
			// zWaitForBusyOverlay();
			SleepUtil.sleepVeryLong();

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}


	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sType(java.lang.String, java.lang.String)
	 */
	public void sType(String locator, String value) throws HarnessException {
		logger.info(myPageName() + " sType("+ locator +", " + value +")");


		try {
			super.sSelectWindow(this.DialogWindowID);
			if ( DoChangeWindowFocus )			super.sWindowFocus();
			
			super.sType(locator, value);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

	}
	
	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sGetText(java.lang.String)
	 */
	public String sGetText(String locator) throws HarnessException {
		logger.info(myPageName() + " sGetText("+ locator +")");

		String text = "";
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			if ( DoChangeWindowFocus )			super.sWindowFocus();
			
			text = super.sGetText(locator);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

		return (text);
	}

	/**
	 * Get text from a different iframe
	 * @param iframelocator
	 * @param locator
	 * @return
	 * @throws HarnessException
	 */
	public String sGetText(String iframelocator, String locator) throws HarnessException {
		
		String text = "";
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			if ( DoChangeWindowFocus )			super.sWindowFocus();
			

			/*
			 * To get the body contents, need to switch iframes
			 */
			try {
				
				super.sSelectFrame(iframelocator);
				text = super.zGetHtml(locator);
				
				logger.info("DisplayMail.zGetBody(" + iframelocator + ", "+ locator +") = " + text);

			} finally {
				// Make sure to go back to the original iframe
				this.sSelectFrame("relative=top");
			}

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
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
			super.sSelectWindow(this.DialogWindowID);
			if ( DoChangeWindowFocus )			super.sWindowFocus();

			present = super.sIsElementPresent(locator);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

		return (present);
	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#zClickAt(java.lang.String, java.lang.String)
	 */
	public void zClickAt(String locator, String coord) throws HarnessException {
		logger.info(myPageName() + " zClickAt("+ locator +", "+ coord +")");


		try {
			super.sSelectWindow(this.DialogWindowID);
			if ( DoChangeWindowFocus )			super.sWindowFocus();

			if ( !super.sIsElementPresent(locator) )
				throw new HarnessException("locator not present: "+ locator);
			
			super.sMouseDownAt(locator, coord);
			super.sMouseUpAt(locator, coord);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}


	}

	/**
	 * Type characters in the separate window
	 * @param characters
	 * @throws HarnessException
	 */
	public void zTypeCharacters(String characters) throws HarnessException {
		logger.info(myPageName() + " zTypeCharacters()");


		try {

			super.sSelectWindow(this.DialogWindowID);
			super.sWindowFocus(); // Must focus into the separate window

			super.zKeyboard.zTypeCharacters(characters);

		} finally {
			super.zSelectWindow(MainWindowID);
			super.sWindowFocus();
		}
		
	}
	
	/**
	 * Type characters in the separate window
	 * @param characters
	 * @throws HarnessException
	 */
	public void zKeyDown(String keyCode) throws HarnessException {
		logger.info(myPageName() + " zKeyDown()");


		try {

			super.sSelectWindow(this.DialogWindowID);
			super.sWindowFocus(); // Must focus into the separate window

			super.zKeyDown(keyCode);

		} finally {
			super.zSelectWindow(MainWindowID);
			super.sWindowFocus();
		}
		
	}
	
	/**
	 * Close the separate window (DefaultSelenium.close())
	 * @throws HarnessException
	 */
	public void zCloseWindow() throws HarnessException {
		logger.info(myPageName() + " zCloseWindow()");


		try {

			super.sSelectWindow(this.DialogWindowID);
			super.sClose();

		} finally {
			super.zSelectWindow(MainWindowID);
		}


	}

	/**
	 * Used to locate the window.  Window title is "Zimbra: <subject>"
	 * @param title A partial string that must be contained in the window title
	 */
	public void zSetWindowTitle(String title) throws HarnessException {
		DialogWindowTitle = title;
	}
	
	

	/**
	 * Set the Selenium Window ID based on partial window title
	 * @param title
	 * @return true if found, false otherwise
	 */
	protected boolean zSetWindowIdByTitle(String title) throws HarnessException {

		if ( IsDebugging ) {

			// Helpful for debugging, log all the names, titles, names
			for (String name: super.sGetAllWindowIds()) {
				logger.info("Window ID: "+ name);
			}

			for (String name: super.sGetAllWindowNames()) {
				logger.info("Window name: "+ name);
			}

			for (String t: super.sGetAllWindowTitles()) {
				logger.info("Window title: "+ t);
			}


		}

		for (String t : super.sGetAllWindowTitles()) {
			logger.info("Window title: "+ t);
			if ( t.toLowerCase().contains(title.toLowerCase()) ) {
				DialogWindowID = title;
				return (true);
			}
		}

		return (false);

	}

	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		if ( this.DialogWindowTitle == null )
			throw new HarnessException("Window Title is null.  Use zSetWindowTitle() first.");
		
		for (String title : super.sGetAllWindowTitles()) {
			logger.info("Window title: "+ title);
			if ( title.toLowerCase().contains(DialogWindowTitle.toLowerCase()) ) {
				DialogWindowID = title;
				logger.info("zIsActive() = true ... title = "+ DialogWindowID);
				return (true);
			}
		}
		
		logger.info("zIsActive() = false");
		return (false);
		
	}


}
