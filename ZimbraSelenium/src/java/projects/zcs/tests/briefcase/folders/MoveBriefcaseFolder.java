package projects.zcs.tests.briefcase.folders;

import java.lang.reflect.Method;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class MoveBriefcaseFolder extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "briefcaseDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("moveBriefcaseFolder")) {
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
		zGoToApplication("Briefcase");
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	@Test(dataProvider = "briefcaseDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveBriefcaseFolder(String briefcaseName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zBriefcaseApp.zCreateNewBriefcaseFolder(briefcaseName);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Briefcase') and contains(text(), '"
						+ briefcaseName + "')]",
				page.zBriefcaseApp.zTrashFolder);
		Assert
				.assertTrue(SelNGBase.selenium
						.get()
						.isElementPresent(
								"//div[@id='zti__main_Briefcase__3']/div[@class='DwtTreeItemChildDiv']//td[contains(text(), '"
										+ briefcaseName + "')]"));

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