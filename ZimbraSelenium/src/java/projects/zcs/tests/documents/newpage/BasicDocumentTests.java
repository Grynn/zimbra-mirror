package projects.zcs.tests.documents.newpage;

import java.lang.reflect.Method;
import org.testng.Assert;
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
public class BasicDocumentTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "DocumentDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("createSimplePageInSpecificNotebook")) {
			return new Object[][] { {
					"noteBookName" + getLocalizedData_NoSpecialChar(),
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3) } };
		} else if (test.equals("toolbarEditNotebookPageAndVerify")
				|| test.equals("linkEditNotebookPageAndVerify")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3),
					"newBodyContent" + getLocalizedData(1) } };
		} else if (test.equals("createSimpleNotebookPage")
				|| test.equals("toolbarDeleteNotebookPageAndVerify")
				|| test.equals("linkDeleteNotebookPageAndVerify")
				|| test.equals("negativeTestCreatePage")) {
			return new Object[][] { {
					"pageName" + getLocalizedData_NoSpecialChar(),
					"bodyContent:" + getLocalizedData(3) } };
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
	 * Test to create simple page with pageName and body
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimpleNotebookPage(String pageName, String bodyContent)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// page.zDocumentCompose.zNavigateToDocument();
		page.zDocumentCompose.zCreateBasicPage(pageName, bodyContent);
		Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("link=" + pageName),
				"The page is not created");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test to create page inside user created notebook
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createSimplePageInSpecificNotebook(String newNotebookName,
			String pageName, String bodyContent) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// page.zDocumentCompose.zNavigateToDocument();
		page.zDocumentCompose.zCreateNewNotebook(newNotebookName, "", "");
		obj.zFolder.zExistsDontWait(newNotebookName);
		page.zDocumentCompose.zCreatePageInSpecificNotebook(newNotebookName,
				pageName, bodyContent);
		Assert.assertTrue(SelNGBase.selenium.get().isElementPresent("link=" + pageName),
				"The page is not created");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test to modify the page body content and verify the changes using
	 * "Toolbar EDIT" btn
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void toolbarEditNotebookPageAndVerify(String pageName,
			String bodyContent, String newBodyContent) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		// page.zDocumentCompose.zNavigateToDocument();

		page.zDocumentCompose.zCreatePageInSpecificNotebook(
				DocumentApp.zNotebookFolder, pageName, bodyContent);
		page.zDocumentCompose.zModifyPageNameAndBody(
				DocumentApp.zNotebookFolder, pageName, "", newBodyContent,
				"Toolbar");
		obj.zButton.zClick((page.zDocumentCompose.zRefreshIconBtn));
		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		SleepUtil.sleep(1000);
		Assert.assertTrue(page.zDocumentCompose.zVerifyEditPage(
				DocumentApp.zNotebookFolder, pageName, "", newBodyContent),
				"Page Body Not Modified Successfully");
		SelNGBase.needReset.set(false);

	}

	/**
	 * Test to modify the page body content and verify the changes using EDIT
	 * LINK
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void linkEditNotebookPageAndVerify(String pageName,
			String bodyContent, String newBodyContent) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		// page.zDocumentCompose.zNavigateToDocument();

		page.zDocumentCompose.zCreatePageInSpecificNotebook(
				DocumentApp.zNotebookFolder, pageName, bodyContent);
		page.zDocumentCompose.zModifyPageNameAndBody(
				DocumentApp.zNotebookFolder, pageName, "", newBodyContent,
				"LinkEdit");

		Assert.assertTrue(page.zDocumentCompose.zVerifyEditPage(
				DocumentApp.zNotebookFolder, pageName, "", newBodyContent),
				"Page Body Not Modified Successfully");
		obj.zButton.zClick(page.zDocumentCompose.zClosePageIconBtn);
		zWaitTillObjectExist("button", page.zDocumentCompose.zRefreshIconBtn);
		obj.zButton.zClick(page.zDocumentCompose.zRefreshIconBtn);
		SelNGBase.needReset.set(false);

	}

	/**
	 * Test to delete the page in a notebook using Toolbar DELETE button and
	 * verify
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void toolbarDeleteNotebookPageAndVerify(String pageName,
			String bodyContent) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		// page.zDocumentCompose.zNavigateToDocument();

		page.zDocumentCompose.zCreatePageInSpecificNotebook(
				DocumentApp.zNotebookFolder, pageName, bodyContent);

		page.zDocumentApp.zDeleteNotebookPage(DocumentApp.zNotebookFolder,
				pageName, "ToolbarDelete");

		obj.zButton.zClick((page.zDocumentCompose.zRefreshIconBtn));
		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		SleepUtil.sleep(1000);
		Assert.assertFalse(SelNGBase.selenium.get().isElementPresent("link=" + pageName),
				"The page is not deleted");
		SelNGBase.needReset.set(false);

	}

	/**
	 * Test to delete the page in a notebook using Toolbar DELETE button and
	 * verify
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void linkDeleteNotebookPageAndVerify(String pageName,
			String bodyContent) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		// page.zDocumentCompose.zNavigateToDocument();

		page.zDocumentCompose.zCreatePageInSpecificNotebook(
				DocumentApp.zNotebookFolder, pageName, bodyContent);

		page.zDocumentApp.zDeleteNotebookPage(DocumentApp.zNotebookFolder,
				pageName, "LinkDelete");
		obj.zButton.zClick((page.zDocumentCompose.zRefreshIconBtn));
		obj.zFolder.zClick(page.zDocumentApp.zNotebookFolder);
		SleepUtil.sleep(3000);
		Assert.assertFalse(SelNGBase.selenium.get().isElementPresent("link=" + pageName),
				"The page is not deleted");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Negative test to verify the warning message when page creation closed in
	 * between and also to verify the when clicked "No" in warning dialog box it
	 * should save the page.
	 */
	@Test(dataProvider = "DocumentDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void negativeTestCreatePage(String pageName, String bodyContent)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		obj.zButton.zClick(page.zDocumentCompose.zNewPageIconBtn);
		page.zDocumentCompose.zEnterBasicPageData(pageName, bodyContent);
		obj.zButton.zClick(page.zDocumentCompose.zClosePageIconBtn);
		String warningMsg = obj.zDialog
				.zGetMessage(localize(locator.warningMsg));
		Assert.assertTrue(warningMsg.equals(localize(locator.askToSave)),
				"Warning message for save page is not correct");
		obj.zButton.zClickInDlg(localize(locator.no));
		Assert.assertFalse(SelNGBase.selenium.get().isElementPresent("link=" + pageName),
				"The page is Saved.However it should not");

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		// page.zCalendarView.zCancelAptInBtw();
		zLogin();
	}
}