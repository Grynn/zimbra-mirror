package projects.zcs.tests.zcscommon;

import java.lang.reflect.Method;

import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class SavedSearchActionTestsForAllAppTab extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteSavedSearchAndVerifyInAllTab")) {
			return new Object[][] { {} };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
		isExecutionARetry = false;
	}
	
	@AfterClass(groups = { "always" })
	public void stopSession() throws Exception {
		selenium.stop();
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
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteSavedSearchAndVerifyInAllTab()
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String savedSearchFolder = getLocalizedData_NoSpecialChar();
		String renameSavedSearchFolder = getLocalizedData_NoSpecialChar();
		zCreateSavedSearchFolder(savedSearchFolder, "xyz");
		obj.zFolder.zClick(savedSearchFolder);
		zGoToApplication("Address Book");
		obj.zFolder.zClick(savedSearchFolder);
		obj.zFolder.zExists(page.zABCompose.zContactsFolder);
		zGoToApplication("Calendar");
		obj.zFolder.zClick(savedSearchFolder);
		obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
		zGoToApplication("Tasks");
		obj.zFolder.zClick(savedSearchFolder);
		obj.zFolder.zExists(page.zTaskApp.zTasksFolder);
		// zGoToApplication("Documents");
		// obj.zFolder.zClick(savedSearchFolder);
		// obj.zFolder.zExists(page.zDocumentApp.zNotebookFolder);
		// zGoToApplication("Briefcase");
		// obj.zFolder.zClick(savedSearchFolder);
		// obj.zFolder.zExists(page.zBriefcaseApp.zBriefcaseFolder);

		zRenameSavedSearchFolder(savedSearchFolder, renameSavedSearchFolder);
		// obj.zFolder.zClick(renameSavedSearchFolder);
		// obj.zFolder.zExists(page.zBriefcaseApp.zBriefcaseFolder);
		zGoToApplication("Mail");
		obj.zFolder.zClick(renameSavedSearchFolder);
		obj.zFolder.zExists(page.zMailApp.zInboxFldr);
		zGoToApplication("Address Book");
		obj.zFolder.zClick(renameSavedSearchFolder);
		obj.zFolder.zExists(page.zABCompose.zContactsFolder);
		zGoToApplication("Calendar");
		obj.zFolder.zClick(renameSavedSearchFolder);
		obj.zFolder.zExists(page.zCalApp.zCalendarFolder);
		zGoToApplication("Tasks");
		obj.zFolder.zClick(renameSavedSearchFolder);
		obj.zFolder.zExists(page.zTaskApp.zTasksFolder);
		// zGoToApplication("Documents");
		// obj.zFolder.zClick(renameSavedSearchFolder);
		// obj.zFolder.zExists(page.zDocumentApp.zNotebookFolder);

		zMoveFolderToTrash(renameSavedSearchFolder);
		obj.zFolder.zNotExists(renameSavedSearchFolder);
		zGoToApplication("Mail");
		obj.zFolder.zClick(renameSavedSearchFolder);
		zGoToApplication("Address Book");
		obj.zFolder.zClick(renameSavedSearchFolder);
		zGoToApplication("Calendar");
		obj.zFolder.zNotExists(renameSavedSearchFolder);
		zGoToApplication("Tasks");
		obj.zFolder.zNotExists(renameSavedSearchFolder);
		// zGoToApplication("Briefcase");
		// obj.zFolder.zNotExists(renameSavedSearchFolder);

		zGoToApplication("Mail");
		zPermanentlyDeleteFolder(renameSavedSearchFolder);
		obj.zFolder.zNotExists(renameSavedSearchFolder);
		zGoToApplication("Address Book");
		obj.zFolder.zNotExists(renameSavedSearchFolder);
		zGoToApplication("Calendar");
		obj.zFolder.zNotExists(renameSavedSearchFolder);
		zGoToApplication("Tasks");
		obj.zFolder.zNotExists(renameSavedSearchFolder);
		// zGoToApplication("Documents");
		// obj.zFolder.zNotExists(renameSavedSearchFolder);

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}