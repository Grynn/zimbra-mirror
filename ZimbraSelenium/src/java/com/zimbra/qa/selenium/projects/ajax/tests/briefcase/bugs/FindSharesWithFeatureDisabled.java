package com.zimbra.qa.selenium.projects.ajax.tests.briefcase.bugs;

import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.LinkItem;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.ajax.core.FeatureBriefcaseTest;
import com.zimbra.qa.selenium.projects.ajax.ui.briefcase.DialogFindShares;

public class FindSharesWithFeatureDisabled extends FeatureBriefcaseTest {
	String url;

	public FindSharesWithFeatureDisabled() {
		logger.info("New "
				+ FindSharesWithFeatureDisabled.class.getCanonicalName());

		// test starts in the Briefcase tab
		super.startingPage = app.zPageBriefcase;

		// use an account with some of the Features disabled
		super.startingAccountPreferences.put("zimbraFeatureCalendarEnabled", "FALSE");
		// super.startingAccountPreferences.put("zimbraFeatureTasksEnabled", "FALSE");
	}	

	@Bugs(ids = "60854")
	@Test(description = "Click on Find Shares link when some of the Features are disabled - Verify Find Shares dialog is displayed", groups = { "functional" })
	public void FindSharesWithFeatureDisabled_01() throws HarnessException {
		ZimbraAccount account = app.zGetActiveAccount();

		FolderItem briefcaseFolder = FolderItem.importFromSOAP(account,
				SystemFolder.Briefcase);

		LinkItem link = new LinkItem();

		// refresh briefcase page
		app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, briefcaseFolder, false);

		// Click on Find shares link
		DialogFindShares dialog = (DialogFindShares) app.zTreeBriefcase
				.zTreeItem(Action.A_LEFTCLICK, link);

		// Verify Find Shares dialog is opened
		ZAssert.assertTrue(dialog.zIsActive(),
				"Verify Find Shares dialog is opened");

		/*
		 * check tree item check box
		 * ClientSessionFactory.session().selenium().getEval(
		 * "selenium.browserbot.triggerMouseEvent(selenium.browserbot.findElement('"
		 * +
		 * "css=div.DwtTreeItemLevel1ChildDiv:contains(enus13130974715803) div#ZmShareTreeItem_12_checkbox"
		 * + "'),'" + "click" + "', null, 0, 0, 0)");
		 * 
		 * get notifications by email
		 * ClientSessionFactory.session().selenium().sGetEval(
		 * "var x = selenium.browserbot.findElementOrNull('css=div[id$=_EMAIL][class^=DwtInputField]>input[id$=_EMAIL_input]');x.value"
		 * );
		 */

		// Dismiss the dialog
		dialog.zClickButton(Button.B_CANCEL);
	}
}
