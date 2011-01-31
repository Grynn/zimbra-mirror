package com.zimbra.qa.selenium.projects.ajax.tests.zimlets.webex;

import java.util.List;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.ZimletItem;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;


public class FolderTree extends AjaxCommonTest {

	public FolderTree() {
		logger.info("New "+ FolderTree.class.getCanonicalName());
		
		// All tests start at the login page
		super.startingPage = app.zPageMail;
		super.startingAccountPreferences = null;
		
	}
	
	@Test(	description = "Verify the WebEx zimlet appears in the folder tree",
			groups = { "sanity" })
	public void FolderTree_01() throws HarnessException {
		
		
		List<ZimletItem> zimlets = app.zTreeMail.zListGetZimlets();
		
		ZimletItem found = null;
		for (ZimletItem z : zimlets) {
			if ( ZimletItem.getWebExZimlet().getName().equals(z.getName()) ) {
				found = z;
			}
		}
		
		ZAssert.assertNotNull(found, "Verify the LinkedIn Zimlet was found");
				
	}


}
