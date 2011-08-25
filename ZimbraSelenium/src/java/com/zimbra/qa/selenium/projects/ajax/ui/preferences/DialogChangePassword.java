/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.preferences;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogChangePassword extends AbsDialog {

	public static class Locators {

		public static final String LocatorOldPasswordCSS		= "css=input[id='oldPassword']";
		public static final String LocatorNewPasswordCSS		= "css=input[id='newPassword']";
		public static final String LocatorConfirmPasswordCSS	= "css=input[id='confirm']";
		
		public static final String LocatorChangePasswordCSS		= "css=div[id='ZLoginFormPanel'] input[class='zLoginButton']";

	}
	

	// TODO: need to I18N
	public static final String ChangePasswordDialogTitle = "Change password";
	public static final String MainPageTitle = "Zimbra: Preferences: General";

	protected String DialogWindowID = null;
	protected String MainWindowID = "null";
	
	public DialogChangePassword(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}
	
	private void myClick(String locator) throws HarnessException {
		logger.info(myPageName() + " click("+ locator +")");
		
		
		try {
			this.zSelectWindow(this.DialogWindowID);

			// Make sure the locator exists
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("click: "+ locator +" is not present");
			}
			
			this.sClick(locator);
			
			// Wait for the SOAP request to finish
			SleepUtil.sleepVeryLong();
			// zWaitForBusyOverlay();

		} finally {
			this.zSelectWindow(MainWindowID);
		}


	}
	
	private void myType(String locator, String value) throws HarnessException {
		logger.info(myPageName() + " type("+ locator +", " + value +")");
		
		
		try {
			this.zSelectWindow(this.DialogWindowID);

			// Make sure the locator exists
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("type: "+ locator +" is not present");
			}
			
			this.sType(locator, value);

		} finally {
			this.zSelectWindow(MainWindowID);
		}

	}

	public void zCloseWindow() throws HarnessException {
		logger.info(myPageName() + " zCloseWindow()");
		
		
		try {
		
			this.zSelectWindow(this.DialogWindowID);
			this.sClose();

		} finally {
			this.zSelectWindow(MainWindowID);
		}


	}
	
	public void zSetOldPassword(String password) throws HarnessException {
		this.myType(Locators.LocatorOldPasswordCSS, password);
	}
	
	public void zSetNewPassword(String password) throws HarnessException {
		this.myType(Locators.LocatorNewPasswordCSS, password);
	}
	
	public void zSetConfirmPassword(String password) throws HarnessException {
		this.myType(Locators.LocatorConfirmPasswordCSS, password);
	}
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_SAVE ) {
			
			locator = Locators.LocatorChangePasswordCSS;

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		myClick(locator);
		
//		zWaitForBusyOverlay();
		
		return (null);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
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

//		for (String name: this.sGetAllWindowIds()) {
//			logger.info("Window ID: "+ name);
//		}
//		
//		for (String name: this.sGetAllWindowNames()) {
//			logger.info("Window name: "+ name);
//		}
//		
		for (String title : this.sGetAllWindowTitles()) {
			logger.info("Window title: "+ title);
			if ( title.toLowerCase().contains(DialogChangePassword.ChangePasswordDialogTitle.toLowerCase()) ) {
				this.DialogWindowID = title;
				return (true);
			}
		}
		
		return (false);
		
	}



}
