package projects.zcs.tests.preferences.general;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.cs.service.account.GetAccountInfo;
import com.zimbra.cs.service.account.GetPrefs;
import com.zimbra.cs.service.admin.GetAccount;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.DocumentApp;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class GeneralPreferencesSetFalseTests extends CommonTest {
	protected String currentloggedinuser;
	protected String aChar = new Character((char) 34).toString();
	public static final String zMailListItemChkBox = "id=zlhi__CLV__se";
	public static final String zTaskListItemChkBox = "id=zlhi__TKL__se";
	public static final String zBriefcaseListItemChkBox = "id=zlhi__BC__se";
	public static final String zPrefSaveIconBtn = "id=zb__PREF__SAVE_left_icon";

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		currentloggedinuser = selfAccountName;
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefIncludeSpamInSearch", "FALSE");
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefIncludeTrashInSearch", "FALSE");
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefShowSearchString", "FALSE");
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefShowSelectionCheckbox", "FALSE");
		selenium.refresh();
		Thread.sleep(3000);/* without this we get permission denied error */
		zWaitTillObjectExist("button", page.zLoginpage.zSearchFldr);
		Thread.sleep(2000);/*
							 * wait another 3 secs after we see the search
							 * button
							 */
		zGoToApplication("Mail");
		isExecutionARetry = false;
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	// Tests
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefVerifyAllCheckBoxesFalse() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Preferences");
		if (!config.getString("locale").equals("ru")) {
			obj.zCheckbox
					.zVerifyIsNotChecked(localize(locator.includeJunkFolder));
		}
		obj.zCheckbox.zVerifyIsNotChecked(localize(locator.includeTrashFolder));
		obj.zCheckbox.zVerifyIsNotChecked(localize(locator.showSearchString));
		obj.zCheckbox
				.zVerifyIsNotChecked(localize(locator.showSelectionString));

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefIncludeJunkFolderInSearchFalse() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] recipients = { SelNGBase.selfAccountName };
		String[] message = { "junksubject21", "junksubject22" };
		for (int i = 0; i <= 1; i++) {
			ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
					"ccuser@testdomain.com", message[i], "generalbody");
			MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
					message[i]);
			if (i == 1) {
				obj.zMessageItem.zClick(message[0]);
				obj.zButton.zClick(page.zMailApp.zJunkIconBtn);
				obj.zFolder.zClick(localize(locator.inbox));
				obj.zMessageItem.zNotExists(message[0]);
				obj.zFolder.zClick(localize(locator.junk));
				obj.zMessageItem.zExists(message[0]);
				obj.zFolder.zClick(localize(locator.inbox));
				selenium.type("xpath=//input[@class='search_input']",
						SelNGBase.selfAccountName);
				obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
				obj.zMessageItem.zNotExists(message[0]);
				obj.zMessageItem.zExists(message[1]);
			}
		}

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefIncludeTrashFolderInSearchFalse() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] recipients = { SelNGBase.selfAccountName };
		String[] message = { "trashsubject21", "trashsubject22" };
		for (int i = 0; i <= 1; i++) {
			ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
					"ccuser@testdomain.com", message[i], "generalbody");
			MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
					message[i]);
			if (i == 1) {
				obj.zMessageItem.zClick(message[0]);
				obj.zButton.zClick(page.zMailApp.zDeleteIconBtn);
				obj.zFolder.zClick(localize(locator.trash));
				Thread.sleep(1000);
				obj.zMessageItem.zExists(message[0]);
				selenium.type("xpath=//input[@class='search_input']",
						SelNGBase.selfAccountName);
				obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
				obj.zMessageItem.zNotExists(message[0]);
				obj.zMessageItem.zExists(message[1]);
			}
		}

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefShowAdvSearchLangFalse() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Inbox folder
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		String inboxSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), inboxSearchValue,
				"Advanced search string not showing blank while click on folder (Inbox)");

		// Sent folder
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zSentFldr));
		String sentSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), sentSearchValue,
				"Advanced search string not showing blank while click on folder (Sent)");

		// Drafts folder
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zMailApp.zDraftsFldr));
		String draftsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), draftsSearchValue,
				"Advanced search string not showing blank while click on folder (Drafts)");

		// Junk folder
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		String junkSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), junkSearchValue,
				"Advanced search string not showing blank while click on folder (Junk)");

		// Trash folder
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		String trashSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), trashSearchValue,
				"Advanced search string not showing blank while click on folder (Trash)");

		// New Folder
		String newFolder = getLocalizedData_NoSpecialChar();
		page.zMailApp.zCreateFolder(newFolder);
		obj.zFolder.zClick(newFolder);
		String newFolderSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), newFolderSearchValue,
				"Advanced search string not showing blank while click on new folder ("
						+ newFolder + ")");

		// Address Book - Contacts folder
		zGoToApplication("Address Book");
		String contactsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		contactsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), contactsSearchValue,
				"Advanced search string not showing blank while click on folder (Contacts)");

		// Address Book - 'Emailed Contacts' folder
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zEmailedContactsFolder));
		String emailedContactsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				localize(locator.search),
				emailedContactsSearchValue,
				"Advanced search string not showing blank while click on folder (Emailed Contacts)");

		// Tasks - Tasks folder
		zGoToApplication("Tasks");
		String tasksSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), tasksSearchValue,
				"Advanced search string not showing blank while go to 'Tasks' application tab");
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zTaskApp.zTasksFolder));
		tasksSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), tasksSearchValue,
				"Advanced search string not showing blank while click on folder (Tasks)");

		// Documents - Notebook folder
		zGoToApplication("Documents");
		obj.zFolder.zClick(DocumentApp.zNotebookFolder);
		String notebookSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(localize(locator.search), notebookSearchValue,
				"Advanced search string not showing blank while click on folder (Notebook)");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefDisplayChkboxInListItemFalse() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Verify mail list item check box
		String[] recipients = { SelNGBase.selfAccountName };
		ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
				"ccuser@testdomain.com", "checkboxinlistitem",
				"checkboxinlistitemmailbody");
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr),
				"checkboxinlistitem");
		String mailListItemChkBoxExist = obj.zCheckbox
				.zNotExistsDontWait(zMailListItemChkBox);
		assertReport(
				"false",
				mailListItemChkBoxExist,
				"Mail list item check box still showing though 'Display checkboxes to quickly select items in lists' general preference is TRUE");

		// Verify task list item check box
		zGoToApplication("Tasks");
		zWaitTillObjectExist("folder",
				replaceUserNameInStaticId(page.zTaskApp.zTasksFolder));
		String taskListItemChkBoxExist = obj.zCheckbox
				.zNotExistsDontWait(zTaskListItemChkBox);
		assertReport(
				"false",
				taskListItemChkBoxExist,
				"Task list item check box still showing though 'Display checkboxes to quickly select items in lists' general preference is TRUE");

		// Verify briefcase list item check box
		zGoToApplication("Briefcase");
		zWaitTillObjectExist("folder",
				replaceUserNameInStaticId(page.zBriefcaseApp.zBriefcaseFolder));
		obj.zButton.zClick(localize(locator.view));
		obj.zMenuItem.zClick(localize(locator.detailView));
		String briefcaseListItemChkBoxExist = obj.zCheckbox
				.zNotExistsDontWait(zBriefcaseListItemChkBox);
		assertReport(
				"false",
				briefcaseListItemChkBoxExist,
				"Briefcase list item check box still showing though 'Display checkboxes to quickly select items in lists' general preference is TRUE");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void VerifyGeneralPreferenceDBValues() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Preferences");

		// Verify database values by setting check box ON / OFF
		String getJunkStatus = ProvZCS.getAccountPreferenceValue(
				currentloggedinuser, "zimbraPrefIncludeSpamInSearch");
		String getTrashStatus = ProvZCS.getAccountPreferenceValue(
				currentloggedinuser, "zimbraPrefIncludeTrashInSearch");
		String getSearchyStringStatus = ProvZCS.getAccountPreferenceValue(
				currentloggedinuser, "zimbraPrefShowSearchString");
		String getShowChkboxStatus = ProvZCS.getAccountPreferenceValue(
				currentloggedinuser, "zimbraPrefShowSelectionCheckbox");

		assertReport(
				"FALSE",
				ProvZCS.getAccountPreferenceValue(currentloggedinuser,
						"zimbraPrefIncludeSpamInSearch"),
				"'Include Junk Folder in Searches' general preference not marked FALSE in database");
		assertReport(
				"FALSE",
				ProvZCS.getAccountPreferenceValue(currentloggedinuser,
						"zimbraPrefIncludeTrashInSearch"),
				"'Include Trash Folder in Searches' general preference not marked FALSE in database");
		assertReport(
				"FALSE",
				ProvZCS.getAccountPreferenceValue(currentloggedinuser,
						"zimbraPrefShowSearchString"),
				"'Show advanced search language in search toolbar' general preference not marked FALSE in database");
		assertReport(
				"FALSE",
				ProvZCS.getAccountPreferenceValue(currentloggedinuser,
						"zimbraPrefShowSelectionCheckbox"),
				"'Display checkboxes to quickly select items in lists' general preference not marked FALSE in database");

		if (!config.getString("locale").equals("ru")) {
			obj.zCheckbox.zClick(localize(locator.includeJunkFolder));
		}
		obj.zCheckbox.zClick(localize(locator.includeTrashFolder));
		obj.zCheckbox.zClick(localize(locator.showSearchString));
		obj.zCheckbox.zClick(localize(locator.showSelectionString));

		obj.zButton.zClick(zPrefSaveIconBtn);
		zWaitTillObjectExist(
				"button",
				replaceUserNameInStaticId(page.zMailApp.zNewFolderOverviewPaneIcon));
		;
		zGoToApplication("Preferences");

		if (!config.getString("locale").equals("ru")) {
			assertReport(
					"TRUE",
					ProvZCS.getAccountPreferenceValue(currentloggedinuser,
							"zimbraPrefIncludeSpamInSearch"),
					"'Include Junk Folder in Searches' general preference not marked TRUE in database");
		}
		assertReport(
				"TRUE",
				ProvZCS.getAccountPreferenceValue(currentloggedinuser,
						"zimbraPrefIncludeTrashInSearch"),
				"'Include Trash Folder in Searches' general preference not marked TRUE in database");
		assertReport(
				"TRUE",
				ProvZCS.getAccountPreferenceValue(currentloggedinuser,
						"zimbraPrefShowSearchString"),
				"'Show advanced search language in search toolbar' general preference not marked TRUE in database");
		assertReport(
				"TRUE",
				ProvZCS.getAccountPreferenceValue(currentloggedinuser,
						"zimbraPrefShowSelectionCheckbox"),
				"'Display checkboxes to quickly select items in lists' general preference not marked TRUE in database");

		if (!config.getString("locale").equals("ru")) {
			obj.zCheckbox.zClick(localize(locator.includeJunkFolder));
		}
		obj.zCheckbox.zClick(localize(locator.includeTrashFolder));
		obj.zCheckbox.zClick(localize(locator.showSearchString));
		obj.zCheckbox.zClick(localize(locator.showSelectionString));

		obj.zButton.zClick(zPrefSaveIconBtn);

		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
