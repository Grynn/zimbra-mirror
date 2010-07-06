package projects.zcs.tests.documents.folders;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.DocumentApp;
import framework.util.RetryFailedTests;

/**
 * This covers some high priority test cases related to Documents
 * 
 * @author Prashant JAISWAL
 * 
 */
@SuppressWarnings("static-access")
public class DocumentFolderTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "DocumentDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createDeleteNotebookFolder")
				|| test.equals("renameNotebookFolder")
				|| test.equals("tryToCreateDuplicateNotebookFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zDocumentCompose.zNavigateToDocument();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to delete Notebook folder using right click delete menu and verify
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createDeleteNotebookFolder(String notebookName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zDocumentCompose.zCreateNewNotebook(notebookName, "", "");
		page.zDocumentApp.zDeleteNotebookFolder(notebookName);
		obj.zFolder.zNotExists(notebookName);

		needReset = false;
	}

	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameNotebookFolder(String notebookName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String newNotebookName = getLocalizedData_NoSpecialChar();
		page.zDocumentCompose.zCreateNewNotebook(notebookName, "", "");
		Thread.sleep(1000);
		obj.zFolder.zRtClick(notebookName);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), newNotebookName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zFolder.zExists(newNotebookName);
		obj.zFolder.zNotExists(notebookName);

		needReset = false;
	}

	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateNotebookFolder(String notebookName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zDocumentCompose.zCreateNewNotebook(notebookName, "", "");
		Thread.sleep(1000);
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(DocumentApp.zNewNotebookOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newNotebook));
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), notebookName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		assertReport(localize(locator.errorAlreadyExists, notebookName, ""),
				obj.zDialog.zGetMessage(localize(locator.criticalMsg)),
				"Verifying dialog message");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.criticalMsg));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.createNewNotebook));

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