package projects.zcs.tests.preferences.mail.composing;

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
import com.zimbra.cs.zclient.ZFolder;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class ComposePreferencesSetFalse extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composePreferencesDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();

		if (test.equals("composePrefReplyFwdFormat")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"TRUE", "html" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"TRUE", "text" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"FALSE", "html" },
					{ getLocalizedData_NoSpecialChar(), getLocalizedData(3),
							"FALSE", "text" } };

		} else if (test.equals("composePrefReplyIncludeMsg")) {

			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeNone" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBody" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "|",
							"includeBodyWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBodyAndHeadersWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeAsAttachment" } };

		} else if (test.equals("composeFwdIncludeMsg")) {

			return new Object[][] {

					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBody" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "|",
							"includeBodyWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeBodyAndHeadersWithPrefix" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), ">",
							"includeAsAttachment" } };

		} else {

			return new Object[][] { { "test" } };
		}

	}

	// Before Class
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();

		// Setup

		String accountName = SelNGBase.selfAccountName.get();

		ProvZCS.modifyAccount(accountName, "zimbraPrefComposeFormat", "text");
		ProvZCS.modifyAccount(accountName,
				"zimbraPrefHtmlEditorDefaultFontFamily", "Times New Roman");
		ProvZCS.modifyAccount(accountName, "zimbraPrefComposeInNewWindow",
				"FALSE");

		ProvZCS.modifyAccount(accountName, "zimbraPrefAutoSaveDraftInterval",
				"0");

		ProvZCS.modifyAccount(accountName, "zimbraPrefSaveToSent", "FALSE");

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
	public void composePrefComposeAs() throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();

		obj.zButton.zNotExists("Times New Roman");

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefOpensInNewWindow()
			throws Exception {

		String browserWindowTitle;

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = SelNGBase.selfAccountName.get();

		page.zComposeView.zNavigateToMailCompose();

		browserWindowTitle = SelNGBase.selenium.get().getTitle();

		Assert
		.assertTrue(
				browserWindowTitle
						.indexOf(localize(locator.compose)) >= 0,
				"Compose opens in new window or doesn't open when 'zimbraPrefComposeInNewWindow' is set to FALSE");

		SelNGBase.needReset.set(false);
		
//		selenium.refresh();
		zReloginToAjax();
		
		SleepUtil.sleep(1000);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefAutoSaveDraft() throws Exception {

		String subject = getLocalizedData_NoSpecialChar();

		String body = getLocalizedData(3);


		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zEnterComposeValues("", "", "", subject, body, "");

		SleepUtil.sleep(30000);

		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		obj.zDialog.zExists(localize(locator.warningMsg));

		obj.zDialog.zVerifyAlertMessage(localize(locator.warningMsg),
				localize(locator.askSaveDraft));

		obj.zButton.zClickInDlg(localize(locator.no));

		SelNGBase.needReset.set(false);
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void composePrefSaveToSent() throws Exception {

		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData(3);

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String recepient = ProvZCS.getRandomAccount();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(recepient, "", "", subject, body,
				"");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);

		SleepUtil.sleep(500);

		obj.zFolder.zClick(localize(locator.sent));

		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}
}
