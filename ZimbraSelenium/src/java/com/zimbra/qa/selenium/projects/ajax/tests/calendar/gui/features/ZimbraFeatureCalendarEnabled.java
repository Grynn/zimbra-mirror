package com.zimbra.qa.selenium.projects.ajax.tests.calendar.gui.features;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class ZimbraFeatureCalendarEnabled extends AjaxCommonTest {

	
	@SuppressWarnings("serial")
	public ZimbraFeatureCalendarEnabled() {
		logger.info("New "+ ZimbraFeatureCalendarEnabled.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageCalendar;

		// Make sure we are using an account with message view
		super.startingAccountPreferences = new HashMap<String, String>() {{
				    			
					// Only mail is enabled
				    put("zimbraFeatureMailEnabled", "FALSE");
				    put("zimbraFeatureContactsEnabled", "FALSE");
				    put("zimbraFeatureCalendarEnabled", "TRUE");
				    put("zimbraFeatureTasksEnabled", "FALSE");
				    put("zimbraFeatureBriefcasesEnabled", "FALSE");
				    
				    // https://bugzilla.zimbra.com/show_bug.cgi?id=62161#c3
				    // put("zimbraFeatureOptionsEnabled", "FALSE");
				    

				}};


	}
	
	/**
	 * @throws HarnessException
	 */
	@Test(	description = "Load the mail tab with just Calendar enabled",
			groups = { "functional" })
	public void ZimbraFeatureCalendarEnabled_01() throws HarnessException {
		
		// TODO: add basic verification that a simple appointment appears
		
	}


}
