package projects.zcs.tests.documents.folders;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.DocumentApp;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

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
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Test to delete Notebook folder using right click delete menu and verify
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createDeleteNotebookFolder(String notebookName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zDocumentCompose.zCreateNewNotebook(notebookName, "", "");
		page.zDocumentApp.zDeleteNotebookFolder(notebookName);
		obj.zFolder.zNotExists(notebookName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameNotebookFolder(String notebookName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String newNotebookName = getLocalizedData_NoSpecialChar();
		page.zDocumentCompose.zCreateNewNotebook(notebookName, "", "");
		SleepUtil.sleep(1000);
		obj.zFolder.zRtClick(notebookName);
		obj.zMenuItem.zClick(localize(locator.editProperties));
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), newNotebookName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zFolder.zExists(newNotebookName);
		obj.zFolder.zNotExists(notebookName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateNotebookFolder(String notebookName)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zDocumentCompose.zCreateNewNotebook(notebookName, "", "");
		SleepUtil.sleep(1000);
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

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}