package projects.zcs.tests.preferences.general;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ChangePassword extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "GeneralPrefDataProvider")
	protected Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		return new Object[][] { {} };

	}


	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="mail";
		super.zLogin();
	}
	

	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void WrongOldPassword() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String actualEnterNewPWMsg;
		String errorMessage;
		page.zGenPrefUI.zNavigateToChangePasswordWindow();
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		SleepUtil.sleep(2000);
		actualEnterNewPWMsg = ClientSessionFactory.session().selenium().getText("class=errorText");

		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("en_GB")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("en_AU")) {
			Assert.assertTrue(localize(locator.enterNewPassword).contains(
					actualEnterNewPWMsg), "expeted message is "
					+ localize(locator.enterNewPassword)
					+ "but the actual message is " + actualEnterNewPWMsg);
		}

		page.zGenPrefUI.zVerifyChangePwdErrMsg("WrongOldPassword", "test321",
				"testtest", "testtest");

		ClientSessionFactory.session().selenium().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void NewConfirmPwdMismatch() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String actualEnterNewPWMsg;
		String errorMessage;
		page.zGenPrefUI.zNavigateToChangePasswordWindow();
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		SleepUtil.sleep(2000);
		actualEnterNewPWMsg = ClientSessionFactory.session().selenium().getText("class=errorText");

		page.zGenPrefUI.zVerifyChangePwdErrMsg("New&ConfirmPwdMismatch",
				"test123", "testtest", "test321");

		ClientSessionFactory.session().selenium().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "GeneralPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ChangePwdRelogin() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zGenPrefUI.zNavigateToChangePasswordWindow();

		page.zGenPrefUI.zEnterChangePWData("test123", "test321", "test321");
		obj.zButton.zClick("class=zLoginButton");
		SleepUtil.sleep(2000);

		resetSession();
		page.zLoginpage
				.zLoginToZimbraAjax(SelNGBase.selfAccountName.get(), "test321");

		resetSession();

		String accountName = Stafzmprov.getRandomAccount();
		SelNGBase.selfAccountName.set(accountName);
		page.zLoginpage.zLoginToZimbraAjax(accountName);

		SelNGBase.needReset.set(false);

	}
}
