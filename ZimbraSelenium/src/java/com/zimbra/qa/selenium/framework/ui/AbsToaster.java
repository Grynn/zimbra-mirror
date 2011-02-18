package com.zimbra.qa.selenium.framework.ui;

import com.zimbra.qa.selenium.framework.util.HarnessException;

/**
 * An abstraction of the toaster message that occurs in the Ajax client
 * @author Matt Rhoades, Dieu Nguyen
 *
 */
public abstract class AbsToaster extends AbsSeleniumObject {
	protected String locator= "css=div[id='z_toast_text']";

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
		return (sGetText(locator));
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
	
    public boolean isVisible() throws HarnessException {
    	//TODO fill in later
    	throw new HarnessException("fill in later");
    }

    public boolean clickLink(String innerText) throws HarnessException {
    	throw new HarnessException("fill in later");
    }
    


}
