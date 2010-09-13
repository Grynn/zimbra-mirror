package projects.zcs.tests.features.familymailboxes.gui;


import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import projects.zcs.tests.features.familymailboxes.FamilyMailboxCommonTest;
import framework.core.ClientSessionFactory;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

public class TabViews extends FamilyMailboxCommonTest {

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="mail";
		super.zLogin();
	}
	/**
	 * Test to create Parent/Child configuration and UI verification
	 */
	@SuppressWarnings("static-access")
	@Test(groups = { "smoke", "test" }, retryAnalyzer = RetryFailedTests.class)
	public void familyMailbox_UIverification() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		addChildAccount();
		
		/**
		 * UI Verification starts here
		 * Check Mail Tab
		 */
		
		obj.zButton.zClick(localize(locator.mail));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.inbox)));

		clickAt(PARENT_ACCOUNT, localize(locator.inbox));
		clickAt(PARENT_ACCOUNT, localize(locator.sent));
		clickAt(PARENT_ACCOUNT, localize(locator.drafts));
		clickAt(PARENT_ACCOUNT, localize(locator.junk));
		clickAt(PARENT_ACCOUNT, localize(locator.trash));
		clickAt(CHILD_ACCOUNT, localize(locator.inbox));
		clickAt(CHILD_ACCOUNT, localize(locator.sent));
		clickAt(CHILD_ACCOUNT, localize(locator.drafts));
		clickAt(CHILD_ACCOUNT, localize(locator.junk));
		clickAt(CHILD_ACCOUNT, localize(locator.trash));

		obj.zButton.zExists(page.zMailApp.zNewMenuIconBtn);
		obj.zButton.zExists(page.zMailApp.zViewIconBtn);

		/**
		 * Check Address Book Tab
		 */
		obj.zButton.zClick(localize(locator.addressBook));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.addressBook)));

		clickAt(PARENT_ACCOUNT, localize(locator.contacts));
		clickAt(PARENT_ACCOUNT, localize(locator.emailedContacts));
		clickAt(PARENT_ACCOUNT, localize(locator.trash));
		clickAt(CHILD_ACCOUNT, localize(locator.contacts));
		clickAt(CHILD_ACCOUNT, localize(locator.emailedContacts));
		clickAt(CHILD_ACCOUNT, localize(locator.trash));

		obj.zButton.zExists(page.zABApp.zNewContactMenuIconBtn);

		/**
		 * Check Calendar Tab
		 */
		obj.zButton.zClick(localize(locator.calendar));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.calendar)));

		clickAt(PARENT_ACCOUNT, localize(locator.calendar));
		clickAt(CHILD_ACCOUNT, localize(locator.calendar));

		obj.zButton.zExists(page.zCalApp.zCalNewApptBtn);
		obj.zButton.zExists(page.zCalApp.zCalTodayBtn);

		/**
		 * Check Tasks Tab
		 */
		obj.zButton.zClick(localize(locator.tasks));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.tasks)));

		clickAt(PARENT_ACCOUNT, localize(locator.tasks));
		clickAt(CHILD_ACCOUNT, localize(locator.tasks));

		obj.zButton.zExists(page.zTaskApp.zTasksNewBtn);
		obj.zButton.zExists(page.zTaskApp.zTasksViewBtn);

		/**
		 * Check Documents Tab
		 */
		obj.zButton.zClick(localize(locator.documents));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.notebook)));

		clickAt(PARENT_ACCOUNT, localize(locator.notebook));
		clickAt(CHILD_ACCOUNT, localize(locator.notebook));

		obj.zButton.zExists(page.zDocumentCompose.zNewPageIconBtn);

		/**
		 * Check Briefcase Tab
		 */
		obj.zButton.zClick(localize(locator.briefcase));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.briefcase)));
		
		obj.zButton.zExists(page.zBriefcaseApp.zNewMenuIconBtn);
		obj.zButton.zExists(page.zBriefcaseApp.zViewIconBtn);


		clickAt(PARENT_ACCOUNT, localize(locator.briefcase));
		clickAt(PARENT_ACCOUNT, localize(locator.trash));
		clickAt(CHILD_ACCOUNT, localize(locator.briefcase));
		clickAt(CHILD_ACCOUNT, localize(locator.trash));


		/**
		 * Check Preferences Tab
		 * Parent Preferences
		 */
		obj.zButton.zClick(localize(locator.preferences));
		zWaitTillObjectExist("xpath", createXpath(PARENT_ACCOUNT, localize(locator.preferences)));
		obj.zButton.zExists(localize(locator.changePassword));
		checkHeaders(localize(locator.loginOptions));
		checkHeaders(localize(locator.searches));
		checkHeaders(localize(locator.other));

		clickAt(PARENT_ACCOUNT, localize(locator.mail));
		checkHeaders(localize(locator.displayMessages));
		checkHeaders(localize(locator.messagesReceiving));		
		obj.zRadioBtn.zExists(localize(locator.displayAsHTML));
		obj.zEditField.zExists(localize(locator.forwardCopyTo));
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@class, 'DwtListView ZmWhiteBlackList')]");

		clickAt(PARENT_ACCOUNT, localize(locator.composing));
		checkHeaders(localize(locator.composingMessages));
		checkLabels("Compose:");
		checkLabels("Prefix:");
		ClientSessionFactory.session().selenium().isElementPresent("link=Accounts Page");

		clickAt(PARENT_ACCOUNT, localize(locator.signatures));
		checkHeaders(localize(locator.signatures));
		checkHeaders(localize(locator.signaturesUsing));
		obj.zButton.zExists(localize(locator.addSignature));
		ClientSessionFactory.session().selenium().isElementPresent("link=Accounts Page");

		clickAt(PARENT_ACCOUNT, localize(locator.accounts));
		checkHeaders(localize(locator.accounts));
		checkHeaders(localize(locator.accountHeaderPrimary));
		obj.zButton.zExists(localize(locator.addExternalAccount));
		obj.zButton.zExists(localize(locator.signatureDoNotAttach));		

		clickAt(PARENT_ACCOUNT, localize(locator.filterRules));
		checkHeaders(localize(locator.filterRules));
		obj.zButton.zExists(localize(locator.newFilter));

		clickAt(PARENT_ACCOUNT, localize(locator.addressBook));
		checkHeaders(localize(locator.options));
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@id, 'AUTOCOMPLETE_NO_GROUP_MATCH')]");
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@id, 'AUTO_ADD_ADDRESS')]");

		clickAt(PARENT_ACCOUNT, localize(locator.calendar));
		checkHeaders(localize(locator.general));
		checkHeaders(localize(locator.apptCreating));

		clickAt(PARENT_ACCOUNT, localize(locator.sharing));
		obj.zButton.zExists(localize(locator.share));

		clickAt(PARENT_ACCOUNT, localize(locator.importExport));
		checkHeaders(localize(locator.importLabel));
		checkHeaders(localize(locator.importLabel));		

		clickAt(PARENT_ACCOUNT, localize(locator.shortcuts));
		clickAt(PARENT_ACCOUNT, localize(locator.zimlets));
		checkHeaders(localize(locator.zimlets));

		/**
		 * Check Preferences Tab
		 * Child Preferences
		 */
		clickAt(CHILD_ACCOUNT, localize(locator.mail));
		checkHeaders(localize(locator.messagesReceiving));		
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@class, 'DwtListView ZmWhiteBlackList')]");

		clickAt(CHILD_ACCOUNT, localize(locator.signature));
		checkHeaders(localize(locator.signatures));
		checkHeaders(localize(locator.signaturesUsing));
		obj.zButton.zExists(localize(locator.addSignature));
		ClientSessionFactory.session().selenium().isElementPresent("link=Accounts Page");

		clickAt(CHILD_ACCOUNT, localize(locator.accounts));
		checkHeaders(localize(locator.accounts));
		checkHeaders(localize(locator.accountHeaderPrimary));
		obj.zButton.zExists(localize(locator.addExternalAccount));
		obj.zButton.zExists(localize(locator.signatureDoNotAttach));		

		clickAt(CHILD_ACCOUNT, localize(locator.filterRules));
		checkHeaders(localize(locator.filterRules));
		obj.zButton.zExists(localize(locator.newFilter));

		clickAt(CHILD_ACCOUNT, localize(locator.addressBook));
		checkHeaders(localize(locator.options));
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@id, 'AUTOCOMPLETE_NO_GROUP_MATCH')]");
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@id, 'AUTO_ADD_ADDRESS')]");

		clickAt(CHILD_ACCOUNT, localize(locator.calendar));
		checkHeaders(localize(locator.general));
		checkHeaders(localize(locator.permissions));

		clickAt(CHILD_ACCOUNT, localize(locator.sharing));
		obj.zButton.zExists(localize(locator.share));

		clickAt(CHILD_ACCOUNT, localize(locator.importExport));
		checkHeaders(localize(locator.importLabel));
		checkHeaders(localize(locator.importLabel));		

		SelNGBase.needReset.set(false);
	}

}
