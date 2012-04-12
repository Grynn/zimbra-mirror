package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.OctopusCommonTest;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;

public class GUIRender extends OctopusCommonTest {

	public GUIRender() {
		logger.info("New " + GUIRender.class.getCanonicalName());

		// test starts at the History tab
		super.startingPage = app.zPageHistory;
		super.startingAccountPreferences = null;
	}
	
	/*
	 * @Test(description = "Verify GUI elements on the page", groups = ("debug"))
	public void VerifyTextDisplayed() throws HarnessException {
		// Click on History tab
		app.zPageOctopus.zToolbarPressButton(Button.B_TAB_HISTORY);
    
		//Verify the header History is displayed
		ZAssert.assertEquals(app.zPageHistory.sGetText(HISTORY_HEADER_VIEW_LOCATOR),
				           "History",
				           "Verify History header is displayed") ;
		
		
		//Verify the Refine panel and text are displayed in the rendered order
		String filterViewText = app.zPageHistory.sGetText(HISTORY_FILTER_VIEW_LOCATOR);
		for (int i=0; i <HISTORY_FILTER_VIEW_TEXT.length; i++) {		
		     ZAssert.assertTrue(filterViewText.trim().startsWith(HISTORY_FILTER_VIEW_TEXT[i]),
		                   "Verify " + HISTORY_FILTER_VIEW_TEXT[i] + " is displayed") ;
		     
		     filterViewText = filterViewText.substring(filterViewText.indexOf(HISTORY_FILTER_VIEW_TEXT[i]) + HISTORY_FILTER_VIEW_TEXT[i].length());						
		}		
		
	}
	*/
	
}
