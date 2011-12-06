package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import java.util.*;


public class DisplayContactGroup extends AbsDisplay {
	public static String ALPHABET_PREFIX = "css=table[id$='alphabet'] td[_idx=";
	public static String ALPHABET_POSTFIX = "]";

	/**
	 * Defines Selenium locators for various objects in {@link DisplayContactGroup}
	 */
	public static class Locators {
		public static final String zLocator = "xpath=//div[@class='ZmContactInfoView']";

	}

	/**
	 * The various displayed fields 
	 */
	public static enum Field {
     FileAs,
     Company,
     Email    
	}
	

	/**
	 * Protected constructor for this object.  Only classes within
	 * this package should create DisplayContact objects.
	 * 
	 * @param application
	 */
	protected DisplayContactGroup(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayContactGroup.class.getCanonicalName());
	   
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
		
		tracer.trace("Click "+ button);
		
       	throw new HarnessException("implement me");
	}
	


	/**
	 * Get the string value of the specified field
	 * @return the displayed string value
	 * @throws HarnessException
	 */
	public String zGetContactProperty(Field field) throws HarnessException {
		logger.info("DisplayContactGroup.zGetContactProperty(" + field + ")");

		ArrayList<String> locatorArray = new ArrayList<String>();
	

		if ( field == Field.FileAs ) {			
		  //locator = "xpath=//table[@class='contactHeaderTable NoneBg']/div[@class='contactHeader']";
		  locatorArray.add("css=table[class*='contactHeaderTable'] div[class*='contactHeader']");
		}
		if ( field == Field.Company ) {			
			  locatorArray.add("css=table[class*='contactHeaderTable'] div[class*='companyName']");
			}		
		else if ( field == Field.Email ) {					   			
			getAllLocators(locatorArray,Field.Email);
		} 
		else {			
		  throw new HarnessException("no logic defined for field "+ field);			
		}
		
		String value = "";

		for (int i=0; i<locatorArray.size(); i++) {
           String locator = locatorArray.get(i);
           
			// Make sure something was set
		   if ( locator == null )
			  throw new HarnessException("locator was null for field = "+ field);
		
		   // Make sure the element is present
		   if ( !sIsElementPresent(locator) )
			 throw new HarnessException("Unable to find the field = "+ field +" using locator = "+ locator);
		
		   // Get the element value
		    value += sGetText(locator).trim();		
		}
		
		logger.info("DisplayContactGroup.zGetContactProperty(" + field + ") = " + value);
		return(value);

		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		return sIsElementPresent("css=div#zv__CNS-main");
	}
	

    private void getAllLocators(ArrayList<String> array, Field field) throws HarnessException {
  	   //String css= "css=div[id$='_content'][class='ZmContactInfoView'] table:nth-of-type(2) tbody tr";
  	   String css= "css=div[id$='_content'][class='ZmContactInfoView']>div.DwtComposite>table";
 
  	   int count= this.sGetCssCount(css);

       if (field == Field.Email) {
    	   for (int i=2; i<=count; i++) {
    		   //String tdLocator=  css + ":nth-of-type(" + i + ")" + " td[id$='_" + postfix + "']";  
    		   String tdLocator=  css + ":nth-of-type(" + i + ")" + " div[style='float: left; padding-right: 5px;']";
    		   if (sIsElementPresent(tdLocator)) {
    			   logger.info(tdLocator + " has text " + sGetText(tdLocator).trim());
    			   array.add(tdLocator);	    	 
    		   }
    	   }
       }
    }

}
