package projects.zcs.tests.mail.folders;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 * @param rssFeedfolderName
 *            - specify rss feed folder name in data provider according to test
 * @param rssFeedURL
 *            - specify valid rss feed URL name in data provider
 */
@SuppressWarnings( { "static-access" })
public class RssFeedFolderTests extends CommonTest {
	protected int i = 0;
	protected String rssFeedFolderName, renameRssFeedFolderName, rssFeedURL;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "RSSFeedDataProvier")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("duplicateRSSFeeds_Bug41488")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							"http://xkcd.com/rss.xml", "xkcd.com" },
					{ getLocalizedData_NoSpecialChar(),
							"http://d.yimg.com/ds/rss/V1/top10/all",
							"Yahoo! Buzz US: Top Stories" },
					{
							getLocalizedData_NoSpecialChar(),
							"http://newsrss.bbc.co.uk/rss/newsonline_world_edition/front_page/rss.xml",
							"BBC News | News Front Page | World Edition" }
			/*
			 * { getLocalizedData_NoSpecialChar(),
			 * "http://www.zimbra.com/forums/external.php?type=RSS2&forumids=2",
			 * "mmorse" }
			 */};
		} else {// default
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					localize(locator.criticalMsg),
					localize(locator.errorInvalidName) } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zMailApp.zNavigateToMailApp();
		SelNGBase.isExecutionARetry.set(false);
		i = 0;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * This test creates rss feed folder, renames and verifies it
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAndRenameRssFeedFolder() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		createData();
		obj.zFolder.zClick(rssFeedFolderName);
		obj.zFolder.zRtClick(rssFeedFolderName);
		SleepUtil.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.renameFolder));
		SleepUtil.sleep(500);
		obj.zDialog.zExists(localize(locator.renameFolder) + ": "
				+ rssFeedFolderName);
		obj.zEditField.zTypeInDlgByName(localize(locator.newName),
				renameRssFeedFolderName, localize(locator.renameFolder) + ": "
						+ rssFeedFolderName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		obj.zFolder.zRtClick(renameRssFeedFolderName);
		SleepUtil.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.renameFolder));
		SleepUtil.sleep(500);
		obj.zEditField.zTypeInDlgByName(localize(locator.newName),
				rssFeedFolderName, localize(locator.renameFolder) + ": "
						+ renameRssFeedFolderName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		obj.zFolder.zExists(rssFeedFolderName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates rss feed folder, deletes and verifies deletion
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAndMoveRssFeedFolder() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		createData();
		page.zMailApp.zDeleteFolder(rssFeedFolderName);
		obj.zFolder.zRtClick(rssFeedFolderName);
		obj.zMenuItem.zClick(localize(locator.move));
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("nl")) {
			obj.zFolder.zClickInDlgByName(localize(locator.folders), localize(
					locator.moveFolder, rssFeedFolderName, ""));
			obj.zButton.zClickInDlgByName(localize(locator.ok), localize(
					locator.moveFolder, rssFeedFolderName, ""));
		} else if (ZimbraSeleniumProperties.getStringProperty("locale").equals("de")) {
			obj.zFolder.zClickInDlgByName(localize(locator.folders),
					localize(locator.folder));
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.folder));
		} else {
			obj.zFolder.zClickInDlgByName(localize(locator.folders),
					localize(locator.move));
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.move));
		}
		obj.zFolder.zExists(rssFeedFolderName);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test verifies rss feed UI
	 */
	@SuppressWarnings("unused")
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyRssFeedUI() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		createData();
		obj.zFolder.zRtClick(rssFeedFolderName);
		obj.zMenuItem.zExists(localize(locator.checkFeed));
		obj.zMenuItem.zClick(localize(locator.checkFeed));
		obj.zFolder.zClick(rssFeedFolderName);
		if (rssFeedURL
				.equals("http://blogsearch.google.com/blogsearch_feeds?q=zimbra&oe=UTF-8&client=firefox-a&um=1&oi=property_suggestions&ct=property-revision&cd=2&ie=utf-8&num=10&output=rss")) {
			MailApp.ClickLoadFeedUntilFeedShowsUp(rssFeedFolderName, "Zimbra");
			obj.zMessageItem.zClick("zimbra");
		}

		boolean dialogFound = false;
		String dialogexistFlag = obj.zDialog
				.zExistsDontWait(localize(locator.zimbraTitle));
		for (int i = 1; i <= 4; i++) {
			if (dialogexistFlag.equals("true")) {
				obj.zButton.zClickInDlgByName(localize(locator.ok),
						localize(locator.zimbraTitle));
				obj.zButton.zExists(localize(locator.checkFeed));
				SleepUtil.sleep(2000);
				obj.zMessageItem.zClick("zimbra");
				SleepUtil.sleep(2000);
				dialogexistFlag = obj.zDialog
						.zExistsDontWait(localize(locator.zimbraTitle));
				dialogFound = true;
				break;
			} else {
				dialogFound = false;
				break;
			}
		}

		if (dialogFound = true) {
			Assert
					.fail("Rss feed URL is not reachable(http://blogsearch.google.com/blogsearch_feeds?q=zimbra&oe=UTF-8&client=firefox-a&um=1&oi=property_suggestions&ct=property-revision&cd=2&ie=utf-8&num=10&output=rss)");
		} else {
			obj.zButton.zIsDisabled(page.zMailApp.zReplyBtn);
			obj.zButton.zIsDisabled(page.zMailApp.zReplyAllBtn);
			obj.zButton.zIsEnabled(page.zMailApp.zDeleteBtn);
			obj.zButton.zIsEnabled(page.zMailApp.zForwardBtn);
		}

		SelNGBase.needReset.set(false);
	}

	private void createData() throws Exception {
		if (i == 0) {
			rssFeedFolderName = getLocalizedData_NoSpecialChar();
			renameRssFeedFolderName = getLocalizedData_NoSpecialChar();
			rssFeedURL = "http://blogsearch.google.com/blogsearch_feeds?q=zimbra&oe=UTF-8&client=firefox-a&um=1&oi=property_suggestions&ct=property-revision&cd=2&ie=utf-8&num=10&output=rss";
			page.zMailApp.zCreateRssFeedFolder(rssFeedFolderName, rssFeedURL);
			i = i + 1;
			SleepUtil.sleep(1000);
		}
	}

	@Test(dataProvider = "RSSFeedDataProvier", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void duplicateRSSFeeds_Bug41488(String folderName,
			String rssFeedURL, String fromName) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		/**
		 * 1. Create a RSS feed. 2. Go to newly created folder. 3. Click on
		 * first message. 4. Get a subject.
		 */
		page.zMailApp.zCreateRssFeedFolder(folderName, rssFeedURL);
		// page.zMailApp.zCreateFolderWithRss(folderName,
		// "http://xkcd.com/rss.xml");
		obj.zFolder.zClick(folderName);
		obj.zMessageItem.zClick(fromName, "1");
		String latestFeedSubject = SelNGBase.selenium.get()
				.getText("//*[contains(@class,'LabelColValue SubjectCol')]");

		/**
		 * 1. Reload feed 2-3 times. 2. Refresh seesion to be on safer side. 3.
		 * Again go to RSS feed folder.
		 */
		obj.zFolder.zClick(folderName);
		obj.zFolder.zClick(folderName);
		obj.zFolder.zClick(folderName);
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3000);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		obj.zFolder.zClick(folderName);

		/**
		 * 1. After refreshment again get subject of first and second message.
		 */
		obj.zMessageItem.zClick(fromName, "1");
		String latestFeedSubject_2 = SelNGBase.selenium.get()
				.getText("//*[contains(@class,'LabelColValue SubjectCol')]");
		obj.zMessageItem.zClick(fromName, "2");
		String penultimate_latestFeed_Subject = SelNGBase.selenium.get()
				.getText("//*[contains(@class,'LabelColValue SubjectCol')]");

		/**
		 * 1. Verify that no duplicate feeds are fetched.
		 */
		if (latestFeedSubject.equals(latestFeedSubject_2)) {
			Assert.assertFalse(latestFeedSubject
					.equals(penultimate_latestFeed_Subject));
		} else {
			Assert.assertFalse(latestFeedSubject_2
					.equals(penultimate_latestFeed_Subject));
		}

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}