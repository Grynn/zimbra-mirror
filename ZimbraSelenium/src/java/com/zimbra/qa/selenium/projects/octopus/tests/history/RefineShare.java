package com.zimbra.qa.selenium.projects.octopus.tests.history;

import org.testng.annotations.*;

import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.octopus.core.CommonConstants.SHARE_PERMISSION;
import com.zimbra.qa.selenium.projects.octopus.ui.PageHistory.*;

public class RefineShare extends HistoryCommonTest {
 
	
	public RefineShare() {
		super();
		logger.info("New " + RefineShare.class.getCanonicalName());
	}


			
	@Test(description = "Verify check 'sharing' checkbox for sharing action ", groups = { "smoke" })
	public void RefineCheckSharingShareAction() throws HarnessException {
	   	   
       // verify check action for 'sharing' 
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));
	}
	   
	@Test(description = "Verify uncheck 'sharing' checkbox for sharing action", groups = { "functional" })
	public void RefineUnCheckSharingShareAction() throws HarnessException {
	   
	   	   
	 // verify uncheck action for 'sharing' 
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.share(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));

	}
	
	
	@Test(description = "Verify check 'sharing' checkbox for revoke action", groups = { "smoke" })
	public void RefineCheckSharingRevokeAction() throws HarnessException {
	   
       // verify check action for 'revoke' 
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));
	}
	
	@Test(description = "Verify uncheck 'sharing' checkbox for revoke action", groups = { "functional" })
	public void RefineUnCheckSharingRevokeAction() throws HarnessException {
	 // verify uncheck action for 'revoke' 
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READ,folder.getName(),readGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_READWRITE,folder.getName(),readWriteGrantee));
	   verifyUnCheckAction(Locators.zHistoryFilterSharing.locator,
				GetText.revoke(SHARE_PERMISSION.SHARE_AS_ADMIN,folder.getName(),adminGrantee));
	   
	}



	
}
