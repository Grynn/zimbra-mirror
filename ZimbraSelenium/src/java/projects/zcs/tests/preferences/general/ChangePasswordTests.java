package projects.zcs.tests.preferences.general;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ChangePasswordTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "GeneralPrefDataProvider")
	protected Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		return new Object[][] { {} };

	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
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

	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void WrongOldPassword() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String actualEnterNewPWMsg;
		String errorMessage;
		page.zGenPrefUI.zNavigateToChangePasswordWindow();
		selenium.selectWindow("_blank");
		Thread.sleep(2000);
		actualEnterNewPWMsg = selenium.getText("class=errorText");

		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")) {
			Assert.assertTrue(localize(locator.enterNewPassword).contains(
					actualEnterNewPWMsg), "expeted message is "
					+ localize(locator.enterNewPassword)
					+ "but the actual message is " + actualEnterNewPWMsg);
		}

		page.zGenPrefUI.zVerifyChangePwdErrMsg("WrongOldPassword", "test321",
				"testtest", "testtest");

		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void NewConfirmPwdMismatch() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String actualEnterNewPWMsg;
		String errorMessage;
		page.zGenPrefUI.zNavigateToChangePasswordWindow();
		selenium.selectWindow("_blank");
		Thread.sleep(2000);
		actualEnterNewPWMsg = selenium.getText("class=errorText");

		page.zGenPrefUI.zVerifyChangePwdErrMsg("New&ConfirmPwdMismatch",
				"test123", "testtest", "test321");

		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ChangePwdRelogin() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zGenPrefUI.zNavigateToChangePasswordWindow();

		page.zGenPrefUI.zEnterChangePWData("test123", "test321", "test321");
		obj.zButton.zClick("class=zLoginButton");
		Thread.sleep(2000);

		resetSession();
		page.zLoginpage
				.zLoginToZimbraAjax(SelNGBase.selfAccountName, "test321");

		resetSession();

		String accountName = ProvZCS.getRandomAccount();
		SelNGBase.selfAccountName = accountName;
		page.zLoginpage.zLoginToZimbraAjax(accountName);

		needReset = false;

	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
