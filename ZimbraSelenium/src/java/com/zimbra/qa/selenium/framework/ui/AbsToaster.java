package com.zimbra.qa.selenium.framework.ui;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

/**
 * An abstraction of the toaster message that occurs in the Ajax client
 * @author Matt Rhoades, Dieu Nguyen
 *
 */
public abstract class AbsToaster extends AbsSeleniumObject {
	protected String locator   = "css=div[id='z_toast_text']";
    protected String idVisibleLocator = "z_toast";
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
		
		this.zWaitForElementVisible(locator);
    	text=sGetText(locator);
    	    
    	//make the toasted message invisible if it contains "Undo" link
    	this.sKeyPressNative(String.valueOf(KeyEvent.VK_ESCAPE));
    	    	
    	this.zWaitForElementInvisible(locator);
		return text;					
	}
	
	/**
	 * Click Undo in the toaster
	 * @param text
	 * @return
	 * @throws HarnessException
	 */
	public void zClickUndo() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
    public boolean isContainedText(String text) throws HarnessException {
    	return zGetToastMessage().contains(text);    
    }
    
    public boolean isContainedLink(String innerText) throws HarnessException {
    	//TODO fill in later
    	throw new HarnessException("fill in later");
    }
	
    public boolean zIsVisible() throws HarnessException {        
    	return zIsVisiblePerPosition(locator,0,0);
    	
    }

    public boolean clickLink(String innerText) throws HarnessException {
    	throw new HarnessException("fill in later");
    }
    


}
