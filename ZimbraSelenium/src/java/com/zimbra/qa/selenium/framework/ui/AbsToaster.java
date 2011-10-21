package com.zimbra.qa.selenium.framework.ui;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * An abstraction of the toaster message that occurs in the Ajax client
 * @author Matt Rhoades, Dieu Nguyen
 *
 */
public abstract class AbsToaster extends AbsSeleniumObject {
	
	public static class Locators {
		
		public static final String ToastDivContainerCSS = "css=div[id='z_toast']";
		public static final String idVisibleLocator = "z_toast";
		
		public static final String ToastTextLocatorCSS   = ToastDivContainerCSS + " div[id='z_toast_text']";

		public static final String ToastUndoLocatorCSS = ToastDivContainerCSS + " div[id='z_toast_text'] a";
		

	}
    
    
    
	/**
	 * A pointer to the application that created this object
	 */
	protected AbsApplication MyApplication = null;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsToaster(AbsApplication application) {
		MyApplication = application;
		
		logger.info("new "+ AbsToaster.class.getCanonicalName());
	}

	/**
	 * Returns the displayed text in the toaster
	 * @return
	 * @throws HarnessException
	 */
	public String zGetToastMessage() throws HarnessException {
		String text=null;		
		
		zWaitForElementVisible(Locators.ToastTextLocatorCSS);
    	text=sGetText(Locators.ToastTextLocatorCSS);
    	    
    	//make the toasted message invisible if it contains "Undo" link
    	sKeyPressNative(String.valueOf(KeyEvent.VK_ESCAPE));
    	    	
    	zWaitForElementInvisible(Locators.ToastTextLocatorCSS);
		return text;					
	}
	
	/**
	 * Click Undo in the toaster
	 * @param text
	 * @return
	 * @throws HarnessException
	 */
	public void zClickUndo() throws HarnessException {
		sClick(Locators.ToastUndoLocatorCSS);
		zWaitForBusyOverlay();
	}
	
    public boolean isContainedText(String text) throws HarnessException {
    	return zGetToastMessage().contains(text);    
    }
    
    public boolean isContainedLink(String innerText) throws HarnessException {
    	//TODO fill in later
    	throw new HarnessException("fill in later");
    }
	
    public boolean zIsActive() throws HarnessException {        
    	return zIsVisiblePerPosition(Locators.ToastDivContainerCSS,0,0);
    	
    }

    public boolean clickLink(String innerText) throws HarnessException {
    	throw new HarnessException("fill in later");
    }
    


}
