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

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;


import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class MailPreferencesSetFalse extends CommonTest {

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
	public void zLogin() throws Exception {
		zLoginIfRequired();

		String accountName = ClientSessionFactory.session().currentUserName();

		Stafzmprov.modifyAccount(accountName, "zimbraPrefShowFragments", "FALSE");
		Stafzmprov.modifyAccount(accountName, "zimbraPrefOpenMailInNewWindow",
				"FALSE");

//		selenium.refresh();
		super.zLogin();
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
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
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = ClientSessionFactory.session().currentUserName();
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		obj.zMessageItem.zNotExists(body);

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailPrefDoubleClickOpensInNewWindow() throws Exception {

		String browserWindowTitle;

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = ClientSessionFactory.session().currentUserName();
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zSendMailToSelfAndVerify(accountName, "", "",
				subject, body, "");

		obj.zMessageItem.zDblClick(subject);

		browserWindowTitle = ClientSessionFactory.session().selenium().getTitle();

		Assert
				.assertTrue(
						browserWindowTitle.indexOf(subject) >= 0,
						"Double clicking a mail opens in new window when 'zimbraPrefOpenMailInNewWindow' is set to FALSE");

		ClientSessionFactory.session().selenium().refresh();

		SelNGBase.needReset.set(false);
	}

}
