package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.*;

import com.thoughtworks.selenium.SeleniumException;
import com.zimbra.qa.selenium.framework.util.*;

/**
 * The <code>AbsSeparateWindow</code> class is a base class that all 
 * "separate window" objects can derive from.  The main additional
 * functionality is the ability to switch focus between different windows
 * (i.e. the 'main' window and the 'separate' window) when
 * executing Selenium method calls.
 * <p>
 * All selenium methods (e.g. sClick(), sType()) must be redefined
 * in this class, with a wrapper to switch windows.
 * <p>
 * 
 * @author Matt Rhoades
 * 
 */
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
			changeFocus();

			super.sClick(locator);

			// Wait for the SOAP request to finish
			// zWaitForBusyOverlay();
			SleepUtil.sleepVeryLong();

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}


	}

	/**
	 * Change focus to the separate window, if DoChangeWindowFocus = true
	 * @throws HarnessException
	 */
	protected void changeFocus() throws HarnessException {
		if ( DoChangeWindowFocus ) {
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
			changeFocus();
			
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
			changeFocus();
			
			text = super.sGetText(locator);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

		return (text);
	}

	public void sSelectFrame(String locator) throws HarnessException {
		logger.info(myPageName() + " sSelectFrame("+ locator +")");
	
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			super.sSelectFrame(locator);
		
		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}
		
	}
	public int sGetCssCount(String css) throws HarnessException {
		logger.info(myPageName() + " sGetCssCount("+ css +")");
		
		Integer count = null;
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();
			
			count = super.sGetCssCount(css);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

		if ( count == null )
			throw new HarnessException("Unable to determine CSS count");
		
		logger.info("getCssCount(" + css + ") = " + count);

		return (count);
	}

	/**
	 * Enter text from a different iframe
	 * @param iframelocator
	 * @param locator
	 * @param value
	 * @throws HarnessException 
	 * @throws HarnessException
	 */
	public void sType(String iframelocator, String locator, String value) throws HarnessException {
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();
			

			/*
			 * To get the body contents, need to switch iframes
			 */
			try {
				
				super.sSelectFrame(iframelocator);
				super.sType(locator, value);

			} finally {
				// Make sure to go back to the original iframe
				this.sSelectFrame("relative=top");
			}

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}
		
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
			changeFocus();
			

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

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sIsElementPresent(java.lang.String)
	 */
	public boolean sIsElementPresent(String locator) throws HarnessException {
		logger.info(myPageName() + " sIsElementPresent("+ locator +")");
		
		boolean present = false;
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			present = super.sIsElementPresent(locator);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

		return (present);
	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#zIsVisiblePerPosition(java.lang.String, int, int)
	 */
	public boolean zIsVisiblePerPosition(String locator, int leftLimit, int topLimit)
	throws HarnessException 
	{
		logger.info(myPageName() + " zIsVisiblePerPosition("+ locator +", "+ leftLimit +", "+ topLimit +")");
		
		boolean present = false;
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			present = super.zIsVisiblePerPosition(locator, leftLimit, topLimit);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

		return (present);
	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sGetElementPositionLeft(java.lang.String)
	 */
	public int sGetElementPositionLeft(String locator)
	throws HarnessException 
	{
		logger.info(myPageName() + " sGetElementPositionLeft("+ locator +")");
				
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			int n = super.sGetElementPositionLeft(locator);
			
			return (n);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sGetElementPositionTop(java.lang.String)
	 */
	public int sGetElementPositionTop(String locator)
	throws HarnessException 
	{
		logger.info(myPageName() + " sGetElementPositionTop("+ locator +")");
				
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			int n = super.sGetElementPositionTop(locator);
			
			return (n);

		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sFocus(java.lang.String)
	 */
	public void sFocus(String locator) throws HarnessException {
		logger.info(myPageName() + " sFocus("+ locator +")");
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			super.sFocus(locator);
			
		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sMouseDown(java.lang.String)
	 */
	public void sMouseDown(String locator) throws HarnessException {
		logger.info(myPageName() + " sMouseDown("+ locator +")");
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			super.sMouseDown(locator);
			
		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#sMouseUp(java.lang.String)
	 */
	public void sMouseUp(String locator) throws HarnessException {
		logger.info(myPageName() + " sMouseUp("+ locator +")");
		
		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

			super.sMouseUp(locator);
			
		} finally {
			super.sSelectWindow(MainWindowID);
			super.sWindowFocus();
		}

	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsSeleniumObject#zClickAt(java.lang.String, java.lang.String)
	 */
	public void zClickAt(String locator, String coord) throws HarnessException {
		logger.info(myPageName() + " zClickAt("+ locator +", "+ coord +")");


		try {
			super.sSelectWindow(this.DialogWindowID);
			changeFocus();

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

			// Make sure the separate window was initialized
			if ( this.DialogWindowID == null || this.DialogWindowID.equals("null") ) {
				// Window was never opened/found.  Don't close anything.
				// This may leave an extra window open, but if we can't
				// find that window, there is no way to close it.
				//
				return;
			}

			// Select the window
			try {
				super.sSelectWindow(this.DialogWindowID);
			} catch (SeleniumException e) {
				logger.warn("In zCloseWindow(), unable to locate DialogWindowID.  Assume already closed.", e);
				return;
			}
			
			// Close the window
			super.sClose();

		} finally {
			super.zSelectWindow(MainWindowID);
		}


	}

	public void zWaitForBusyOverlay() throws HarnessException {
		logger.info(myPageName() + " zWaitForBusyOverlay()");

		try {
			
			super.sSelectWindow(this.DialogWindowID);
			super.sWaitForCondition("selenium.browserbot.getUserWindow().top.appCtxt.getShell().getBusy()==false");

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
	 * Used to locate the window.  Normally, the full browser 'title' is used, 
	 * but the Selenium ID and Selenium Name are also valid.
	 * @param id The window ID
	 */
	public void zSetWindowID(String id) throws HarnessException {
		this.DialogWindowID = id;
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
