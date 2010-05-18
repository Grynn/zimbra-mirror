package projects.zcs.tests.others;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
public class MiscBugTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("verifyAutoCompleteAndRankOnlyWithContactsFolder")
				|| test.equals("autocompleteOnSpecialCharacters")) {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		} else {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
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
	@SuppressWarnings("static-access")
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void thisKeySequenceUndefinedError_Bug40797(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefUseKeyboardShortcuts", "FALSE");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		// mail
		zGoToApplication("Mail");
		obj.zDialog.zNotExists(localize(locator.zimbraTitle));
		obj.zFolder.zExists(page.zMailApp.zInboxFldr);
		obj.zFolder.zExists(page.zMailApp.zSentFldr);
		obj.zFolder.zExists(page.zMailApp.zTrashFldr);
		verifyOverviewPane();
		obj.zButton.zExists(page.zMailApp.zNewMenuIconBtn);
		obj.zButton.zExists(page.zMailApp.zViewIconBtn);

		// address book
		zGoToApplication("Address Book");
		obj.zDialog.zNotExists(localize(locator.zimbraTitle));
		obj.zFolder.zExists(page.zABCompose.zContactsFolder);
		obj.zFolder.zExists(page.zABCompose.zEmailedContactsFolder);
		obj.zButton.zExists(page.zABCompose.zNewABOverviewPaneIcon);
		verifyOverviewPane();

		// calendar
		zGoToApplication("Calendar");
		obj.zDialog.zNotExists(localize(locator.zimbraTitle));
		obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
		obj.zButton.zExists(page.zCalApp.zCalNewApptBtn);
		obj.zButton.zExists(page.zCalApp.zCalTodayBtn);
		verifyOverviewPane();

		// tasks
		zGoToApplication("Tasks");
		obj.zDialog.zNotExists(localize(locator.zimbraTitle));
		obj.zFolder.zExists(page.zTaskApp.zTasksFolder);
		obj.zButton.zExists(page.zTaskApp.zTasksNewBtn);
		obj.zButton.zExists(page.zTaskApp.zTasksViewBtn);
		verifyOverviewPane();

		// documents
		zGoToApplication("Documents");
		obj.zDialog.zNotExists(localize(locator.zimbraTitle));
		obj.zFolder.zExists(page.zDocumentApp.zNotebookFolder);
		obj.zButton.zExists(page.zDocumentApp.zNewNotebookOverviewPaneIcon);
		verifyOverviewPane();

		// briefcase
		zGoToApplication("Briefcase");
		obj.zDialog.zNotExists(localize(locator.zimbraTitle));
		obj.zFolder.zExists(page.zBriefcaseApp.zBriefcaseFolder);
		obj.zButton.zExists(page.zBriefcaseApp.zNewMenuIconBtn);
		obj.zButton.zExists(page.zBriefcaseApp.zViewIconBtn);
		verifyOverviewPane();

		// preferences
		zGoToApplication("Preferences");
		obj.zDialog.zNotExists(localize(locator.zimbraTitle));
		obj.zButton.zExists(localize(locator.changePassword));
		zGoToPreferences("Mail");
		obj.zButton.zExists(page.zMailApp.zMailViewIconBtn);
		zGoToPreferences("Calendar");
		obj.zFeatureMenu.zExists(localize(locator.defaultViewLabel));
		zGoToPreferences("Sharing");
		obj.zRadioBtn.zExists("id=*_user");
		obj.zButton.zExists(page.zMailApp.zZimletsPrefFolder);
		Thread.sleep(2000);

		needReset = false;
	}

	@SuppressWarnings("static-access")
	private static void verifyOverviewPane() throws Exception {
		obj.zButton.zExists("id=ztih__main_Mail__FOLDER_textCell");
		obj.zButton.zExists("id=ztih__main_Mail__SEARCH_textCell");
		obj.zButton.zExists("id=ztih__main_Mail__TAG_textCell");
		obj.zButton.zExists("id=ztih__main_Mail__ZIMLET_textCell");
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