package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineFavorite extends HistoryCommonTest {
 
	
	public RefineFavorite() {
		super();
		logger.info("New " + RefineFavorite.class.getCanonicalName());
	}


			
	@Test(description = "Verify check 'favorite' checkbox with favorite action", groups = { "smoke" })
	public void RefineCheckFavorite() throws HarnessException {
		
        // verify favorite text present
		verifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											

	}
	
	@Test(description = "Verify uncheck 'favorite' checkbox with favorite action", groups = { "functional" })
	public void RefineUnCheckFavorite() throws HarnessException {
		
		// verify favorite text not present
		verifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName), PageHistory.GetText.REGEXP.FAVORITE);											

	}
	
	@Test(description = "Verify check 'favorite' checkbox with non favorite action", groups = { "smoke" })
	public void RefineCheckNonFavorite() throws HarnessException {
		        
		// verify non favorite text present
		verifyCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											

	}
	

	@Test(description = "Verify uncheck 'favorite' checkbox with non favorite action", groups = { "functional" })
	public void RefineUnCheckNonFavorite() throws HarnessException {
		
		// verify non favorite text not present
		verifyUnCheckAction(Locators.zHistoryFilterFavorites.locator, 
				GetText.favorite(fileName));											

	}
		


	
}
