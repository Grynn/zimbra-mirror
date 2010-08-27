package projects.zcs.tests.mail.compose;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

import projects.zcs.tests.CommonTest;

@SuppressWarnings( { "static-access", "unused" })
public class SendBtnNegativeTests extends CommonTest {
	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) {
		return new Object[][] {
				{ "", "", "", "NoAddress:" + getLocalizedData(2), "", "",
						localize(locator.criticalMsg),
						localize(locator.noAddresses) },
				{ "$!~", "", "", "InvalidAddress " + getLocalizedData(2), "",
						"", localize(locator.warningMsg),
						localize(locator.compBadAddresses, "$!~", "") },
				{ "_selfAccountName_", "", "", "", "", "",
						localize(locator.warningMsg),
						localize(locator.compSubjectMissing) } };
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Enters different invalid email address types and checks if alert/warning
	 * dialog is thrown
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendThrowsErrorTest(String to, String cc, String bcc,
			String subject, String body, String attachments, String errDlgName,
			String errMsg) throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("fr", "na", "na", "Expected and actual message do not match though message are same (frech char. issue) - need to update wrapper for fr");

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zVerifySendThrowsError(to, cc, bcc, subject, body,
				attachments, errDlgName, errMsg);
		page.zComposeView.zGoToMailAppFromCompose();
		SelNGBase.needReset.set(false);
	}

	/**
	 * Opens compose-view in new window. Enters different invalid email address
	 * types and checks if alert/warning dialog is thrown when we hit send-btn.
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendThrowsErrorTest_NewWindow(String to, String cc, String bcc,
			String subject, String body, String attachments, String errDlgName,
			String errMsg) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("fr", "na", "na", "Expected and actual message do not match though message are same (frech char. issue) - need to update wrapper for fr");
		checkForSkipException("na", "SF", "39446", "New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");


		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zVerifySendThrowsError(to, cc, bcc, subject, body,
				attachments, errDlgName, errMsg);
		page.zComposeView.zGoToMailAppFromCompose();
		SelNGBase.needReset.set(false);// indicates no need to login for the next test
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		page.zComposeView.zGoToMailAppFromCompose();
		zLogin();
	}

}
