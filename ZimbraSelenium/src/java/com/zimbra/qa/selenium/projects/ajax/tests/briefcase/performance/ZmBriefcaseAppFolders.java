package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.performance;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.performance.*;
import com.zimbra.qa.selenium.projects.ajax.core.*;



public class ZmBriefcaseAppFolders extends FeatureBriefcaseTest {

	public ZmBriefcaseAppFolders() {
		logger.info("New "+ ZmBriefcaseAppFolders.class.getCanonicalName());


		super.startingPage = app.zPageMail;

	}

	@Test(	description = "Measure the time to load the briefcase app, 1 briefcase",
			groups = { "performance" })
	public void ZmBriefcaseAppFolders_01() throws HarnessException {

		// Create a folder
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
		app.zGetActiveAccount().soapSend(
				"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
					"<folder name='case"+ ZimbraSeleniumProperties.getUniqueString() + "' view='document' l='"+ root.getId() +"'/>" +
				"</CreateFolderRequest>");


		// Sync the changes to the client (notification block)
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);
		
		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmBriefcaseAppOverviewPanel, "Load the briefcase app, 1 briefcase");

		// Currently in the mail app
		// Navigate to the addressbook
		app.zPageBriefcase.zNavigateTo();

		PerfMetrics.waitTimestamp(token);

		// Wait for the app to load
		app.zPageBriefcase.zWaitForActive();


	}

	@Test(	description = "Measure the time to load the briefcase app, 100 briefcases",
			groups = { "performance" })
	public void ZmBriefcaseAppFolders_02() throws HarnessException {

		// Create 100 folders
		FolderItem root = FolderItem.importFromSOAP(app.zGetActiveAccount(), FolderItem.SystemFolder.UserRoot);
		for (int i = 0; i < 100; i++) {
			app.zGetActiveAccount().soapSend(
					"<CreateFolderRequest xmlns='urn:zimbraMail'>" +
						"<folder name='case"+ ZimbraSeleniumProperties.getUniqueString() + "' view='document' l='"+ root.getId() +"'/>" +
					"</CreateFolderRequest>");
		}


		// Sync the changes to the client (notification block)
		app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

		PerfToken token = PerfMetrics.startTimestamp(PerfKey.ZmBriefcaseAppOverviewPanel, "Load the briefcase app, 100 briefcases");

		// Currently in the mail app
		// Navigate to the addressbook
		app.zPageBriefcase.zNavigateTo();

		PerfMetrics.waitTimestamp(token);

		// Wait for the app to load
		app.zPageBriefcase.zWaitForActive();


	}


}
