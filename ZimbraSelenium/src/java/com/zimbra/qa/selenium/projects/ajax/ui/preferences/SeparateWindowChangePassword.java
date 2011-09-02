/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.preferences;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsSeparateWindow;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class SeparateWindowChangePassword extends AbsSeparateWindow {

	public static class Locators {

		public static final String LocatorOldPasswordCSS		= "css=input[id='oldPassword']";
		public static final String LocatorNewPasswordCSS		= "css=input[id='newPassword']";
		public static final String LocatorConfirmPasswordCSS	= "css=input[id='confirm']";
		
		public static final String LocatorChangePasswordCSS		= "css=div[id='ZLoginFormPanel'] input[class='zLoginButton']";

	}
	

	public SeparateWindowChangePassword(AbsApplication application) {
		super(application);
		
		this.DialogWindowTitle = "Change password"; // TODO: need to I18N
	}
	
	public void zSetOldPassword(String password) throws HarnessException {
		this.sType(Locators.LocatorOldPasswordCSS, password);
	}
	
	public void zSetNewPassword(String password) throws HarnessException {
		this.sType(Locators.LocatorNewPasswordCSS, password);
	}
	
	public void zSetConfirmPassword(String password) throws HarnessException {
		this.sType(Locators.LocatorConfirmPasswordCSS, password);
	}
	
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
		
		sClick(locator);
		
//		zWaitForBusyOverlay();
		
		return (null);
	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}




}
