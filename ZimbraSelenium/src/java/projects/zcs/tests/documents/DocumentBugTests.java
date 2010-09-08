package projects.zcs.tests.documents;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.DocumentCompose;
import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

/**
 * This covers some high priority test cases related to Documents
 * 
 * @author Prashant JAISWAL
 * 
 */
@SuppressWarnings("static-access")
public class DocumentBugTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	private static String WARNING_MESSAGE = "The name must be at most 128 characters long";

	@DataProvider(name = "DocumentDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("tooLongDocName_Bug37614")) {
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
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="documents";
		super.zLogin();
	}

	@Test(dataProvider = "", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tooLongDocName_Bug37614() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		obj.zButton.zClick(DocumentCompose.zNewPageIconBtn);
		zWaitTillObjectExist("button", DocumentCompose.zSavePageIconBtn);
		DocumentCompose
				.zEnterBasicPageData(
						"page1pagepage1pagepage1pagepage1pagepage1pagepage1pagepage1pagepage1pagepage1page page1page page1page page1page23456789pageNamepage1pagepage1pagepage1pagepage1pagepage1pagepage1pagepage1pagepage1pagepage1page page1page page1page page1page23456789pageName",
						"Hello World");
		obj.zButton.zClick(DocumentCompose.zSavePageIconBtn);

		obj.zDialog.zExists(localize(locator.warningMsg));
		if (ZimbraSeleniumProperties.getStringProperty("locale").equalsIgnoreCase("en_US")) {
			Assert.assertTrue(obj.zDialog.zGetMessage(
					localize(locator.warningMsg)).equals(WARNING_MESSAGE));
		}
		SelNGBase.needReset.set(false);
	}
}