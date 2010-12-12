package projects.ajax.ui.addressbook;

import framework.ui.AbsApplication;
import framework.ui.AbsDisplay;
import framework.util.HarnessException;
import framework.util.SleepUtil;

public class DisplayContact extends AbsDisplay {

	/**
	 * Defines Selenium locators for various objects in {@link DisplayContact}
	 */
	public static class Locators {
		public static final String zLocator = "xpath=//div[@class='ZmContactInfoView']";

	}

	/**
	 * The various displayed fields 
	 */
	public static enum Field {
     FirstName,
     LastName,
     FileAs,
     Location,
	 JobTitle,
     Company,
     Email
     //others
	}
	

	/**
	 * Protected constructor for this object.  Only classes within
	 * this package should create DisplayContact objects.
	 * 
	 * @param application
	 */
	protected DisplayContact(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayContact.class.getCanonicalName());
		
		// Let the reading pane load
		SleepUtil.sleepLong();


	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	
	/**
	 * Get the string value of the specified field
	 * @return the displayed string value
	 * @throws HarnessException
	 */
	public String zGetContactProperty(Field field) throws HarnessException {
		logger.info("DisplayMail.zGetDisplayedValue(" + field + ")");

		String locator = null;
		

		if ( field == Field.FileAs ) {			
		  locator = "xpath=//table[@class='contactHeaderTable NoneBg']/div[@class='contactHeader']";
		//TODO: other fields	
		} else {
			
			throw new HarnessException("no logic defined for field "+ field);
			
		}

		// Make sure something was set
		if ( locator == null )
			throw new HarnessException("locator was null for field = "+ field);
		
		// Default behavior, process the locator by clicking on it
		//
		
		// Make sure the subject is present
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Unable to find the field = "+ field +" using locator = "+ locator);
		
		// Get the subject value
		String value = this.sGetText(locator).trim();
		
		logger.info("DisplayMail.zGetDisplayedValue(" + field + ") = " + value);
		return(value);

		
	}
	




}
