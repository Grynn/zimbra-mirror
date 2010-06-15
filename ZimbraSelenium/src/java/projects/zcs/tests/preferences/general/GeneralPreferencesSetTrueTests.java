package projects.zcs.tests.preferences.general;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.DocumentApp;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class GeneralPreferencesSetTrueTests extends CommonTest {
	protected String currentloggedinuser;
	protected String aChar = new Character((char) 34).toString();
	public static final String zMailListItemChkBox = "id=zlhi__CLV__se";
	public static final String zTaskListItemChkBox = "id=zlhi__TKL__se";
	public static final String zBriefcaseListItemChkBox = "id=zlhi__BCD__se";

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		currentloggedinuser = selfAccountName;
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefIncludeSpamInSearch", "TRUE");
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefIncludeTrashInSearch", "TRUE");
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefShowSearchString", "TRUE");
		ProvZCS.modifyAccount(currentloggedinuser,
				"zimbraPrefShowSelectionCheckbox", "TRUE");
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
	public void GeneralPrefVerifyAllCheckBoxesTrue() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Preferences");
		if (!config.getString("locale").equals("ru")) {
			obj.zCheckbox.zVerifyIsChecked(localize(locator.includeJunkFolder));
		}
		obj.zCheckbox.zVerifyIsChecked(localize(locator.includeTrashFolder));
		obj.zCheckbox.zVerifyIsChecked(localize(locator.showSearchString));
		obj.zCheckbox.zVerifyIsChecked(localize(locator.showSelectionString));

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefIncludeJunkFolderInSearchTrue_Bug40528()
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] recipients = { SelNGBase.selfAccountName };
		String[] message = { "junksubject11", "junksubject12" };
		for (int i = 0; i <= 1; i++) {
			ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
					"ccuser@testdomain.com", message[i], "generalbody");
			MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
					message[i]);
			if (i == 1) {
				obj.zMessageItem.zClick(message[0]);
				obj.zButton.zClick(page.zMailApp.zJunkIconBtn);
				obj.zFolder.zClick(localize(locator.junk));
				Thread.sleep(1000);
				obj.zMessageItem.zExists(message[0]);
				selenium.type("xpath=//input[@class='search_input']",
						SelNGBase.selfAccountName);
				obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
				obj.zMessageItem.zExists(message[0]);
				obj.zMessageItem.zExists(message[1]);

				// verification for bug 40528
				obj.zFolder.zClick(page.zMailApp.zInboxFldr);
				Thread.sleep(2000);
				selenium.windowFocus();
				selenium.type("xpath=//input[@class='search_input']",
						SelNGBase.selfAccountName);
				obj.zEditField.zActivate("xpath=//input[@class='search_input']");
				Robot zRobot = new Robot();
				zRobot.keyPress(KeyEvent.VK_ENTER);
				zRobot.keyRelease(KeyEvent.VK_ENTER);
				Thread.sleep(1000);
				zRobot.keyPress(KeyEvent.VK_ENTER);
				zRobot.keyRelease(KeyEvent.VK_ENTER);
				obj.zMessageItem.zExists(message[0]);
				obj.zMessageItem.zExists(message[1]);
			}
		}

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefIncludeTrashFolderInSearchTrue_Bug40528()
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] recipients = { SelNGBase.selfAccountName };
		String[] message = { "trashsubject11", "trashsubject12" };
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
				obj.zMessageItem.zExists(message[0]);
				obj.zMessageItem.zExists(message[1]);

				// verification for bug 40528
				obj.zFolder.zClick(page.zMailApp.zInboxFldr);
				Thread.sleep(2000);
				selenium.windowFocus();
				selenium.type("xpath=//input[@class='search_input']",
						SelNGBase.selfAccountName);
				obj.zEditField.zActivate("xpath=//input[@class='search_input']");
				Robot zRobot = new Robot();
				zRobot.keyPress(KeyEvent.VK_ENTER);
				zRobot.keyRelease(KeyEvent.VK_ENTER);
				Thread.sleep(1000);
				zRobot.keyPress(KeyEvent.VK_ENTER);
				zRobot.keyRelease(KeyEvent.VK_ENTER);
				obj.zMessageItem.zExists(message[0]);
				obj.zMessageItem.zExists(message[1]);
			}
		}

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefShowAdvSearchLangTrue() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String expectedSearchValue;
		Thread.sleep(2000);

		// Inbox folder
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zInboxFldr));
		Thread.sleep(1000);
		String inboxSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:inbox";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "inbox" + (char) 34;
		}

		assertReport(
				expectedSearchValue,
				inboxSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Inbox)");

		// Sent folder
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:sent";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "sent" + (char) 34;
		}
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zSentFldr));
		Thread.sleep(1000);
		String sentSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				expectedSearchValue,
				sentSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Sent)");

		// Drafts folder
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:drafts";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "drafts" + (char) 34;
		}
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zMailApp.zDraftsFldr));
		Thread.sleep(1000);
		String draftsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				expectedSearchValue,
				draftsSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Drafts)");

		// Junk folder
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:junk";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "junk" + (char) 34;
		}
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zJunkFldr));
		Thread.sleep(1000);
		String junkSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				expectedSearchValue,
				junkSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Junk)");

		// Trash folder
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:trash";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "trash" + (char) 34;
		}
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zMailApp.zTrashFldr));
		Thread.sleep(1000);
		String trashSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				expectedSearchValue,
				trashSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Trash)");

		// New Folder
		String newFolder = getLocalizedData_NoSpecialChar();
		page.zMailApp.zCreateFolder(newFolder);
		obj.zFolder.zClick(newFolder);
		Thread.sleep(1000);
		String newFolderSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				"in:" + aChar + newFolder + aChar,
				newFolderSearchValue,
				"Advanced search string not showing on search edit field while click on new folder ("
						+ newFolder + ")");

		// Right click menu Search on username
		String[] recipients = { SelNGBase.selfAccountName };
		ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
				"ccuser@testdomain.com", "generalmail", "generalbody");
		MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
				"generalmail");
		SelNGBase.actOnLabel = true;
		obj.zMessageItem.zRtClick(SelNGBase.selfAccountName.split("@")[0]);
		SelNGBase.actOnLabel = false;
		obj.zMenuItem.zClick(page.zMailApp.zSearchMenuIconBtn);
		Thread.sleep(1000);
		String userNameSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				"from:(" + SelNGBase.selfAccountName + ")",
				userNameSearchValue,
				"Advanced search string not showing on search edit field while do mail right click menu > Search");

		// Right click menu Advanced Search on username
		SelNGBase.actOnLabel = true;
		obj.zMessageItem.zRtClick(SelNGBase.selfAccountName.split("@")[0]);
		SelNGBase.actOnLabel = false;
		obj.zMenuItem.zClick(page.zMailApp.zAdvancedSearchMenuIconBtn);
		Thread.sleep(1000);
		assertReport(
				"from:(" + SelNGBase.selfAccountName + ")",
				userNameSearchValue,
				"Advanced search string not showing on search edit field while do mail right click menu > Advanced Search");
		if (config.getString("locale").equals("ru")
				|| config.getString("locale").equals("pl")
				|| config.getString("locale").equals("sv")
				|| config.getString("locale").equals("ko")) {
			obj.zButton.zClick(localize(locator.close));
		} else {
			obj.zButton.zClick(localize(locator.advanced));
		}

		// Address Book - Contacts folder
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:contacts";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "contacts" + (char) 34;
		}
		zGoToApplication("Address Book");
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		Thread.sleep(1000);
		String contactsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		obj.zFolder.zClick(localize(locator.contacts));
		contactsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				expectedSearchValue,
				contactsSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Contacts)");

		// Address Book - 'Emailed Contacts' folder
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:Emailed Contacts";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "Emailed Contacts"
					+ (char) 34;
		}
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zEmailedContactsFolder));
		Thread.sleep(1000);
		String emailedContactsSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				expectedSearchValue,
				emailedContactsSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Emailed Contacts)");

		// Tasks - Tasks folder
		if (config.getString("locale").equals("deDELETED")) {
			expectedSearchValue = "in:tasks";
		} else {
			expectedSearchValue = "in:" + (char) 34 + "tasks" + (char) 34;
		}
		zGoToApplication("Tasks");
		zWaitTillObjectExist("folder", page.zTaskApp.zTasksFolder);
		Thread.sleep(1000);
		String tasksSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				"in:tasks",
				tasksSearchValue,
				"Advanced search string not showing on search edit field while go to 'Tasks' application tab");
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zTaskApp.zTasksFolder));
		Thread.sleep(1000);
		tasksSearchValue = selenium
				.getValue("xpath=//input[@class='search_input']");
		assertReport(
				expectedSearchValue,
				tasksSearchValue,
				"Advanced search string not showing on search edit field while click on folder (Tasks)");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void GeneralPrefDisplayChkboxInListItemTrue() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// Verify mail list item check box
		String[] recipients = { SelNGBase.selfAccountName };
		ProvZCS.injectMessage(SelNGBase.selfAccountName, recipients,
				"ccuser@testdomain.com", "checkboxinlistitem",
				"checkboxinlistitemmailbody");
		MailApp.ClickCheckMailUntilMailShowsUp(localize(locator.inbox),
				"checkboxinlistitem");
		String mailListItemChkBoxExist = obj.zCheckbox
				.zExistsDontWait(zMailListItemChkBox);
		assertReport(
				"true",
				mailListItemChkBoxExist,
				"Mail list item check box still showing though 'Display checkboxes to quickly select items in lists' general preference is TRUE");

		// Verify task list item check box
		zGoToApplication("Tasks");
		zWaitTillObjectExist("checkbox", zTaskListItemChkBox);
		String taskListItemChkBoxExist = obj.zCheckbox
				.zExistsDontWait(zTaskListItemChkBox);
		assertReport(
				"true",
				taskListItemChkBoxExist,
				"Task list item check box still showing though 'Display checkboxes to quickly select items in lists' general preference is TRUE");

		// Verify briefcase list item check box
		zGoToApplication("Briefcase");
		obj.zButton.zClick(localize(locator.view));
		obj.zMenuItem.zClick(localize(locator.detailView));
		zWaitTillObjectExist("checkbox", zBriefcaseListItemChkBox);
		String briefcaseListItemChkBoxExist = obj.zCheckbox
				.zExistsDontWait(zBriefcaseListItemChkBox);
		assertReport(
				"true",
				briefcaseListItemChkBoxExist,
				"Briefcase list item check box still showing though 'Display checkboxes to quickly select items in lists' general preference is TRUE");

		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
