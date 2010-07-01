package projects.html.tests.mail;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.zcs.clients.ProvZCS;

/**
 * This class file contains mail folders related tests
 * 
 * @author Jitesh Sojitra
 * 
 */

@SuppressWarnings("static-access")
public class MailFolderTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("moveMailFolderAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(), "Inbox" } };
		} else if (test.equals("validSpecialCharFolderTest")) {
			return new Object[][] { { "!@#$", "Inbox" } };
		} else if (test.equals("invalidSpecialCharFolderTest")) {
			return new Object[][] { { ":#%", "" } };
		} else {
			return new Object[][] { { getLocalizedData_NoSpecialChar(), "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------

	/*
	 * This test create and renames mail folder and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameMailFolderAndVerify(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(MEDIUM_WAIT);
		page.zMailApp.zCreateFolder(folderName, parentFolder);
		obj.zFolder.zClick(folderName);
		Thread.sleep(LONG_WAIT);
		String renameFldrName = getLocalizedData_NoSpecialChar();
		obj.zEditField.zType(page.zMailApp.zEditFldrNameEditField,
				renameFldrName);
		Thread.sleep(SMALL_WAIT);
		obj.zButton.zClick(page.zMailApp.zCreateFolderBtn); // save changes
		if (config.getString("locale").equals("en_US")) {
			String folderUpdateMsg = obj.zToastAlertMessage.zGetMsg();
			assertReport(folderUpdateMsg, "Folder Updated",
					"Verifying folder updataion toast message");
		}
		obj.zFolder.zClick(renameFldrName);
		Thread.sleep(SMALL_WAIT);
		String verifyFldrValue = obj.zEditField
				.zGetInnerText(page.zMailApp.zEditFldrNameEditField);
		assertReport(verifyFldrValue, renameFldrName,
				"Verifying updated folder value");

		needReset = false;
	}

	/*
	 * This test tries to create duplicate folder and verifies error message
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateFolderAndVerify(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(MEDIUM_WAIT);
		page.zMailApp.zCreateFolder(folderName, parentFolder);
		page.zMailApp.zCreateFolder(folderName, parentFolder);
		if (config.getString("locale").equals("en_US")) {
			String errorMsg = obj.zToastAlertMessage.zGetMsg();
			assertReport(errorMsg, "Item already exists.",
					"Verifying duplicate folder creation toast message");
		}
		obj.zButton.zClick(page.zMailApp.zCreateFolderCancelBtn);

		needReset = false;
	}

	/*
	 * This test create and renames mail folder and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveMailFolderAndVerify(String folderName, String parentFolder)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(MEDIUM_WAIT);
		page.zMailApp.zCreateFolder(folderName, "");
		obj.zFolder.zClick(folderName);
		Thread.sleep(MEDIUM_WAIT);
		obj.zHtmlMenu.zClick(page.zMailApp.zParentFolderWebList, parentFolder);
		obj.zButton.zClick(page.zMailApp.zCreateFolderBtn); // save changes
		Thread.sleep(LONG_WAIT);
		zWaitTillObjectExist("htmlmenu", page.zMailApp.zParentFolderWebList);
		if (config.getString("locale").equals("en_US")) {
			String folderUpdateMsg = obj.zToastAlertMessage.zGetMsg();
			obj.zToastAlertMessage.zAlertMsgExists("Folder Updated",
					folderUpdateMsg);
		}
		obj.zFolder.zClick(folderName);
		Thread.sleep(MEDIUM_WAIT);
		String parentFolderValue = obj.zHtmlMenu
				.zGetSelectedItemName(page.zMailApp.zParentFolderWebList);
		assertReport(parentFolder, parentFolderValue,
				"Verifying parent folder value from respected web list");
		String newFolderName = "movefolder";
		page.zMailApp.zCreateFolder(newFolderName, parentFolder + "/"
				+ folderName);
		obj.zFolder.zClick(newFolderName);
		zWaitTillObjectExist("htmlmenu", page.zMailApp.zParentFolderWebList);
		parentFolderValue = obj.zHtmlMenu
				.zGetSelectedItemName(page.zMailApp.zParentFolderWebList);
		assertReport(parentFolder + "/" + folderName, parentFolderValue,
				"Verifying parent folder value from respected web list");

		needReset = false;
	}

	/*
	 * This test creates folder by specifying valid special character and verify
	 * it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void validSpecialCharFolderTest(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(LONG_WAIT);
		page.zMailApp.zCreateFolder(folderName, parentFolder);
		if (config.getString("locale").equals("en_US")) {
			String folderCreatedMsg = obj.zToastAlertMessage.zGetMsg();
			String actualmsg = "Created folder " + (char) 34 + "!@#$"
					+ (char) 34 + ".";
			assertReport(folderCreatedMsg, actualmsg,
					"Verifying valid special character folder creation toast message");
		}

		needReset = false;
	}

	/*
	 * This test creates folder by specifying invalid special character and
	 * verify error message
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void invalidSpecialCharFolderTest(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(LONG_WAIT);
		obj.zButton.zClick(page.zMailApp.zNewFolderBtn);
		Thread.sleep(MEDIUM_WAIT);
		obj.zEditField.zType(page.zMailApp.zNewFldrNameEditField, folderName);
		if (!parentFolder.equals("")) {
			obj.zHtmlMenu.zClick(page.zMailApp.zParentFolderWebList, "Inbox");

		}
		obj.zButton.zClick(page.zMailApp.zCreateFolderBtn);
		if (config.getString("locale").equals("en_US")) {
			String invalidFolderMsg = obj.zToastAlertMessage.zGetMsg();
			assertReport(
					invalidFolderMsg,
					"Invalid name. It contains at least one invalid character.",
					"Verifying invalid special character folder creation toast message");
		}
		obj.zFolder.zNotExists(folderName);

		needReset = false;
	}

	/*
	 * (-ve)This test tries to delete all the mail items within folder without
	 * selecting "Permanently hard delete .." check box
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void delAllMailWOSelectingConfChkBox(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(LONG_WAIT);
		obj.zFolder.zClick(localize(locator.inbox));
		Thread.sleep(MEDIUM_WAIT);
		obj.zButton.zClick(page.zMailApp.zDeleteAllItemsBtn);
		if (config.getString("locale").equals("en_US")) {
			String folderEmptyMsg = obj.zToastAlertMessage.zGetMsg();
			assertReport(
					folderEmptyMsg,
					"Please check the checkbox to confirm deletion.",
					"Verifying toast message while delete all mail items without selecting confirmation checkbox");
		}

		needReset = false;
	}

	/*
	 * Permanently delete all the mails from manage folder and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void perDelAllMailAndVerify(String folderName, String parentFolder)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject = "perDelAllMail";
		zGoToApplication("Mail");
		page.zMailApp.zInjectMessage(SelNGBase.selfAccountName,
				SelNGBase.selfAccountName, "ccuser@testdomain.com", "",
				subject, subject + " body", "");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(LONG_WAIT); // required
		obj.zFolder.zClick(localize(locator.inbox));
		Thread.sleep(MEDIUM_WAIT);
		obj.zCheckbox.zClick(page.zMailApp.zPermDelMailItemChkBox);
		obj.zButton.zClick(page.zMailApp.zDeleteAllItemsBtn);
		if (config.getString("locale").equals("en_US")) {
			String folderEmptyMsg = obj.zToastAlertMessage.zGetMsg();
			assertReport(folderEmptyMsg, "Folder " + (char) 34 + "Inbox"
					+ (char) 34 + " emptied.",
					"Verifying toast message after emptying folder");
		}
		zGoToApplication("Mail");
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zNotExists(subject);

		needReset = false;
	}

	/*
	 * (-ve)This test tries to delete folder without selecting
	 * "Delete this folder" check box
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void delMailFolderWOSelectingConfChkBox(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(MEDIUM_WAIT);
		page.zMailApp.zCreateFolder(folderName, parentFolder);
		obj.zFolder.zClick(folderName);
		Thread.sleep(MEDIUM_WAIT);
		obj.zButton.zClick(page.zMailApp.zDeleteFolderBtn);
		if (config.getString("locale").equals("en_US")) {
			String deleteFolderMsg = obj.zToastAlertMessage.zGetMsg();
			assertReport(
					deleteFolderMsg,
					"Please check the checkbox to confirm deletion.",
					"Verifying toast message while delete folders without selecting confirmation checkbox");
		}

		needReset = false;
	}

	/*
	 * (-ve)This test tries to delete folder without selecting
	 * "Delete this folder" check box
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void permDeleteMailFolderAndVerify(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(MEDIUM_WAIT);
		page.zMailApp.zCreateFolder(folderName, parentFolder);
		obj.zFolder.zClick(folderName);
		Thread.sleep(MEDIUM_WAIT);
		obj.zCheckbox.zClick(page.zMailApp.zDeleteThisFolderChkBox);
		obj.zButton.zClick(page.zMailApp.zDeleteFolderBtn);
		if (config.getString("locale").equals("en_US")) {
			String deleteFolderMsg = obj.zToastAlertMessage.zGetMsg();
			String currentMsg = "Folder " + (char) 34 + folderName + (char) 34
					+ " moved to " + (char) 34 + "Trash" + (char) 34;
			assertReport(
					deleteFolderMsg,
					currentMsg,
					"Verifying toast message after moving folder to trash without selecting confirmation checkbox");
		}
		obj.zFolder.zClick(folderName);
		Thread.sleep(MEDIUM_WAIT);
		obj.zCheckbox.zClick(page.zMailApp.zDeleteThisFolderChkBox);
		obj.zButton.zClick(page.zMailApp.zPermDeleteFolderBtn);
		if (config.getString("locale").equals("en_US")) {
			String deleteFolderMsg = obj.zToastAlertMessage.zGetMsg();
			assertReport(deleteFolderMsg, "Deleted folder " + (char) 34
					+ folderName + (char) 34,
					"Verifying toast message after permanently deleting folder");
		}
		obj.zFolder.zNotExists(folderName);
		zGoToApplication("Mail");
		obj.zFolder.zNotExists(folderName);
		needReset = false;
	}

	/*
	 * (-ve)This test tries to delete folder without selecting
	 * "Delete this folder" check box
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void systemFolderTest(String folderName, String parentFolder)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(SMALL_WAIT);
		String[] sysFolders = { localize(locator.inbox),
				localize(locator.sent), localize(locator.drafts),
				localize(locator.junk), localize(locator.trash) };
		for (int i = 0; i <= sysFolders.length - 1; i++) {
			obj.zFolder.zClick(sysFolders[i]);
			Thread.sleep(2000);
			Assert.assertTrue(selenium.isElementPresent("//*[contains(@id,'name') and contains(@disabled,'')]"));
			if (!sysFolders[i].equals(localize(locator.junk))
					&& !sysFolders[i].equals(localize(locator.trash))) {
				obj.zCheckbox.zExists(page.zMailApp.zPermDelMailItemChkBox);
				obj.zCheckbox.zExists(page.zMailApp.zDeleteAllItemsBtn);
			}
			if (sysFolders[i].equals(page.zMailApp.zInboxFldr)) {
				obj.zFolder.zExists(page.zMailApp.zMarkAllReadBtn);
			}
			if (!sysFolders[i].equals(page.zMailApp.zJunkFldr)
					&& !sysFolders[i].equals(page.zMailApp.zTrashFldr)) {
				obj.zCheckbox.zNotExists(page.zMailApp.zDeleteThisFolderChkBox);
				obj.zCheckbox.zNotExists(page.zMailApp.zDeleteFolderBtn);
			}
			if (sysFolders[i].equals(page.zMailApp.zJunkFldr)
					|| sysFolders[i].equals(page.zMailApp.zTrashFldr)) {
				obj.zButton.zExists(page.zMailApp.zEmptyJunkFolder);
			}
		}

		needReset = false;
	}

	/*
	 * This test creates rss feed folder and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRssFeedFolderAndVerify(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(SMALL_WAIT);
		String rssFeed = "http://blogsearch.google.com/blogsearch_feeds?q=zimbra&oe=UTF-8&client=firefox-a&um=1&oi=property_suggestions&ct=property-revision&cd=2&ie=utf-8&num=10&output=rss";
		obj.zButton.zClick(page.zMailApp.zNewRssFeedBtn);
		Thread.sleep(MEDIUM_WAIT);
		obj.zEditField.zType(page.zMailApp.zNewFldrNameEditField, folderName);
		if (!parentFolder.equals("")) {
			obj.zHtmlMenu.zClick(page.zMailApp.zParentFolderWebList,
					parentFolder);
		}
		obj.zEditField.zType(page.zMailApp.zRssFeedURLEditField, rssFeed);
		obj.zButton.zClick(page.zMailApp.zCreateFolderBtn);
		Thread.sleep(LONG_WAIT); // selenium failure
		zWaitTillObjectExist("folder", folderName); // wait for rss folder
		zGoToApplication("Mail");
		obj.zFolder.zClick(folderName);
		Thread.sleep(LONG_WAIT);
		zWaitTillObjectExist("message", "Zimbra"); // wait for feed
		obj.zMessageItem.zExists("Zimbra");

		needReset = false;
	}

	/**
	 * create a simple search that matches one of the emails and click on it and
	 * verify if mail shows up & delete saved search and verify if it doesnt
	 * show up in overview.
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void saveSearchFolderDeleteItAndVerify(String folderName,
			String parentFolder) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject = "testSavedSearch";
		folderName = "fldr_" + folderName;
		zGoToApplication("Mail");
		page.zMailApp.zInjectMessage(ProvZCS.getRandomAccount(),
				SelNGBase.selfAccountName, "ccuser@testdomain.com", "",
				subject, subject + "body", "");
		if (config.getString("browser").equals("IE")) {
			Thread.sleep(MEDIUM_WAIT); // selenium failure in IE
		}
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(MEDIUM_WAIT);
		obj.zButton.zClick(page.zMailApp.zNewSearchBtn);
		Thread.sleep(LONG_WAIT);
		obj.zEditField.zType(page.zMailApp.zNewFldrNameEditField, folderName);
		if (!parentFolder.equals("")) {
			obj.zHtmlMenu.zClick(page.zMailApp.zParentFolderWebList,
					parentFolder);
		}
		Thread.sleep(SMALL_WAIT);
		obj.zEditField.zType(page.zMailApp.zSearchQueryEditField, subject);
		obj.zButton.zClick(page.zMailApp.zCreateFolderBtn);
		Thread.sleep(MEDIUM_WAIT); // selenium failure here
		zWaitTillObjectExist("folder", folderName);
		zGoToApplication("Mail");
		Thread.sleep(MEDIUM_WAIT);
		obj.zFolder.zClick(folderName);
		Thread.sleep(MEDIUM_WAIT);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zEditLinkFldrOverviewPane);
		Thread.sleep(LONG_WAIT);
		obj.zFolder.zClick(folderName);
		Thread.sleep(MEDIUM_WAIT); // selenium failure here
		obj.zCheckbox.zClick(page.zMailApp.zDeleteThisFolderChkBox);
		obj.zButton.zClick(page.zMailApp.zDeleteFolderBtn);
		Thread.sleep(MEDIUM_WAIT); // selenium failure here
		obj.zCheckbox.zClick(page.zMailApp.zDeleteThisFolderChkBox);
		obj.zButton.zClick(page.zMailApp.zPermDeleteFolderBtn);
		Thread.sleep(MEDIUM_WAIT); // selenium failure here
		obj.zFolder.zNotExists(folderName);
		zGoToApplication("Mail");
		Thread.sleep(SMALL_WAIT);
		obj.zFolder.zNotExists(folderName);

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
