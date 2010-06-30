package projects.zcs.tests.documents.folders;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
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
		if (test.equals("createDeleteNotebookFolder")) {
			return new Object[][] { { "noteBookName"
					+ getLocalizedData_NoSpecialChar() } };
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
	public void createDeleteNotebookFolder(String newNotebookName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zDocumentCompose.zCreateNewNotebook(newNotebookName, "", "");
		page.zDocumentApp.zDeleteNotebookFolder(newNotebookName);
		obj.zFolder.zNotExists(newNotebookName);

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