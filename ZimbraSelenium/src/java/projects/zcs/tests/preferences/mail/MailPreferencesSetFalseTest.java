package projects.zcs.tests.preferences.mail;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;

//import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.Selenium;
import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class MailPreferencesSetFalseTest extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailPreferencesDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		return new Object[][] { { "localize(locator.GAL)" } };

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();

		String accountName = selfAccountName;

		ProvZCS.modifyAccount(accountName, "zimbraPrefShowFragments", "FALSE");
		ProvZCS.modifyAccount(accountName, "zimbraPrefOpenMailInNewWindow",
				"FALSE");

//		selenium.refresh();
		zReloginToAjax();

		Thread.sleep(5000);
		isExecutionARetry = false;
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Imports a ics file and verifies that all the appointments are imported
	 * correctly
	 * 
	 * @throws Exception
	 */
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefDisplaySnippets() throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		obj.zMessageItem.zNotExists(body);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefDoubleClickOpensInNewWindow() throws Exception {

		String browserWindowTitle;

		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		obj.zMessageItem.zDblClick(subject);

		browserWindowTitle = selenium.getTitle();

		Assert
				.assertTrue(
						browserWindowTitle.indexOf(subject) >= 0,
						"Double clicking a mail opens in new window when 'zimbraPrefOpenMailInNewWindow' is set to FALSE");

		selenium.refresh();

		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

}
