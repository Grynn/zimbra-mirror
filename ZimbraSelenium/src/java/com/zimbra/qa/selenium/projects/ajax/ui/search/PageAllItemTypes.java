package com.zimbra.qa.selenium.projects.ajax.ui.search;

import java.util.*;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;





public class PageAllItemTypes extends AbsTab {

	/**
	 * Defines Selenium locators for various objects in {@link PageAllItemTypes}
	 */
	public static class Locators {				
		public static final String CONTAINER   = "css=div#zv__MX";	
		public static final String CHECKBOX    = "div[id^=zlif__MX__][id$=__se]";
		public static final String TAG         = "div[id^=zlif__MX__][id$=__tg]";
		public static final String IMAGE       = "div[id^=zlif__MX__][id$=__ty]";
		public static final String FROM        = "td[id^=zlif__MX__][id$=__fr]";
		public static final String ATTACHMENT  = "div[id^=zlif__MX__][id$=__at]";
		public static final String SUBJECT     = "td[id^=zlif__MX__][id$=__su]";
		public static final String DATE        = "td[id^=zlif__MX__][id$=__dt]";
			
	}

	
		

	/**
	 * @param application
	 */
	public PageAllItemTypes(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageAllItemTypes.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {		
	}
    
	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		return null;
	}
	
	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		return null;
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption, String item) throws HarnessException {
		return null;
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
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
	 *  
	 * @return the array of rows of AllItemTypesItem
	 * @throws HarnessException
	 */
	public ArrayList<AllItemTypesItem> zListItems() throws HarnessException {
		logger.info("PageAllItemTypes.zListItems()");
		int numRow = Integer.parseInt(sGetEval("window.document.getElementById('zl__MX__rows').childNodes.length"));
        
		ArrayList<AllItemTypesItem> list= new ArrayList<AllItemTypesItem>(numRow);
			
		for (int i=0; i <numRow; i++) {
           //StringBuilder id = new StringBuilder(sGetEval("window.document.getElementById('zl__MX__rows').childNodes[" + i + "].id") + "__fr");
           StringBuilder id = new StringBuilder(sGetEval("window.document.getElementById('zl__MX__rows').childNodes[" + i + "].id") );

           try {
	           id.insert(3,'f');
	           AllItemTypesItem item = new AllItemTypesItem("","",sGetText("css=tr#"+id.toString()+"__rw>td:nth-of-type(4)"),"","","");
	/*           //String cssPrefix = "css=tr#" + id +" ";
	           String cssPrefix = "xpath=(//";
	           AllItemTypesItem item = new AllItemTypesItem(
	               	//sGetAttribute(cssPrefix + Locators.TAG + "@class"), sGetAttribute(cssPrefix + Locators.IMAGE + "@class"),	  
	               	//"","","",""
	               	//sGetText(cssPrefix + Locators.FROM), sGetAttribute(cssPrefix + Locators.ATTACHMENT + "@class"),
	               	//sGetText(cssPrefix + Locators.SUBJECT), sGetText(cssPrefix + Locators.DATE)
	
	        		 "", sGetAttribute(cssPrefix + "div[@id=" + id + "__ty])" + "@class"),	  
	        	    sGetText(cssPrefix + Locators.FROM), sGetAttribute(cssPrefix + Locators.ATTACHMENT + "@class"),
	        	    sGetText(cssPrefix + Locators.SUBJECT), sGetText(cssPrefix + Locators.DATE)
	                ); 
	  */
	           list.add(item);
	           logger.info(item.prettyPrint());
           }
           catch (Exception e) {
        	   logger.info(" It is not a valid item " + e.getMessage());
           }
		}			
		
		return list;		
	}
	
	
	@Override
	public boolean zIsActive() throws HarnessException {
		zWaitForElementPresent(Locators.CONTAINER);	
		return (sIsVisible(Locators.CONTAINER));
				
	}
	
}
