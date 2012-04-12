package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineComment extends HistoryCommonTest {
 
	
	public RefineComment() {
		super();
		logger.info("New " + RefineComment.class.getCanonicalName());
	}


			
	@Test(description = "Verify check 'comment' checkbox", groups = { "functional" })
	public void RefineCheckComment() throws HarnessException {
    		
       // verify check action for 'comment' 
	   verifyCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		
	}

	@Test(description = "Verify uncheck 'comment' checkbox", groups = { "smoke" })
	public void RefineUnCheckComment() throws HarnessException {
			
       // verify uncheck action for 'comment' 
	   verifyCheckUnCheckAction(Locators.zHistoryFilterComment.locator,
				GetText.comment(fileName));
		
	}

	//TODO add test case for delete comment (bug #70800)

}
