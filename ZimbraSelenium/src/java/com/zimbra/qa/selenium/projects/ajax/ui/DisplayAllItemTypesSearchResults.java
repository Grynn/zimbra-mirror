package com.zimbra.qa.selenium.projects.ajax.ui;


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;





public class DisplayAllItemTypesSearchResults extends AbsDisplay {

	/**
	 * Defines Selenium locators for various objects in {@link DisplayAllItemTypesSearchResults}
	 */
	public static class Locators {				
		public static final String CONTAINER   = "css=div#zv__MX";	
		public static final String CHECKBOX    = "css=div[id^=zlif__MX__][id$=__se]";
		public static final String TAG         = "css=div[id^=zlif__MX__][id$=__tg]";
		public static final String IMAGE       = "css=div[id^=zlif__MX__][id$=__ty]";
		public static final String FROM        = "css=td[id^=zlif__MX__][id$=__fr]";
		public static final String ATTACHMENT  = "css=div[id^=zlif__MX__][id$=__at]";
		public static final String SUBJECT     = "css=td[id^=zlif__MX__][id$=__su]";
		public static final String DATE        = "css=td[id^=zlif__MX__][id$=__dt]";
			
	}

		

	/**
	 * @param application
	 */
	public DisplayAllItemTypesSearchResults(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayAllItemTypesSearchResults.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zPressButton("+ button +")");
		
		tracer.trace("Click "+ button);

		AbsPage page = this;
		String locator = null;

		if ( locator == null )
			throw new HarnessException("no locator defined for button "+ button);
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("locator is not present for button "+ button +" : "+ locator);
		
		this.zClick(locator);
		
		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (page);
	}

	

	
	/**
	 * Get the string value of the specified field
	 * @return the displayed string value or a class of the displayed image
	 * @throws HarnessException
	 */
	public String zGetProperty(String field) throws HarnessException {
		logger.info("DisplayAllItemTypesSearchResults.zGetMailProperty(" + field + ")");

		String value = null;
	
		//TODO: get checkbox status
		//get the class 
		if (( field == Locators.TAG ) || ( field == Locators.IMAGE ) || ( field == Locators.ATTACHMENT )){
            value = this.sGetAttribute(field + "@class");   			
			
		} else if (( field == Locators.FROM ) || ( field == Locators.SUBJECT ) ){
			value = sGetText(field).trim();

		} else {			
			throw new HarnessException("no logic defined for field "+ field);			
		}

		
		logger.info("DisplayAllItemTypesSearchResults.zGetProperty(" + field + ") = " + value);
		return(value);		
	}
	
	/**
	 * Wait for Zimlets to be rendered in the message
	 * @throws HarnessException
	 */
	public void zWaitForZimlets() throws HarnessException {
		// TODO: don't sleep.  figure out a way to query the app if zimlets are applied
		logger.info("zWaitForZimlets: sleep a bit to let the zimlets be applied");
		SleepUtil.sleepLong();
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		//logger.warn("implement me", new Throwable());
		zWaitForZimlets();
			
		return (this.sIsVisible(Locators.CONTAINER));
				
	}
	
}
