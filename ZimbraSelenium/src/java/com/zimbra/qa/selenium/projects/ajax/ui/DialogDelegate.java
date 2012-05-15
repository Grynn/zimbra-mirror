/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;



/**
 * Represents a "Add Delegate" dialog box (Preferences -> Accounts -> Delegates)
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogDelegate extends AbsDialog {

	public static class Locators {
		public static final String zDialogLocator = "css=div[id='GrantRightsDialog']";
	}


	protected String MyDialogLocator = null;
	
	public enum Rights {
		SendAs,
		SendOnBehalfOf,
	}


	
	
	public DialogDelegate(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		MyDialogLocator = Locators.zDialogLocator;
		
	}
	
	
	

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			locator = MyDialogLocator + " td[id^='OK_'] td[id$='_title']";
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = MyDialogLocator + " td[id^='Cancel_'] td[id$='_title']";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		this.zClick(locator);
		
		zWaitForBusyOverlay();
		
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

		String locator = MyDialogLocator;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}




	public void zSetEmailAddress(String email) throws HarnessException {
		logger.info(myPageName() + " zSetEmailAddress(" + email + ")");

		String locator = "css=input#ZmGrantRightsDialog_name";

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("zSetEmailAddress " + locator + " is not present");
		}
		
		
		// Seems that the client can't handle filling out the new mail form too quickly
		// Click in the "To" fields, etc, to make sure the client is ready
		this.sFocus(locator);
		this.zClick(locator);
		this.zWaitForBusyOverlay();

		// Instead of sType() use zKeyboard
		this.sType(locator, email);
		


	}




	public void zCheckRight(Rights right) throws HarnessException {
		logger.info(myPageName() + " zCheckRight("+ right +")");
		
		
		String locator = null;
		
		if( right == Rights.SendAs ) {
			
			locator = "css=input#ZmGrantRightsDialog_sendAs";
			
		}else if ( right == Rights.SendOnBehalfOf ){

			locator = "css=input#ZmGrantRightsDialog_sendObo";
			
		}else{
			throw new HarnessException("zCheckRight: "+ right +" is not defined");
		}
		
		this.sFocus(locator);
		this.sClick(locator);
		
	}




	public void zUnCheckRight(Rights right) throws HarnessException {
		logger.info(myPageName() + " zUnCheckRight("+ right +")");
		
		zCheckRight(right);
		
	}



}
