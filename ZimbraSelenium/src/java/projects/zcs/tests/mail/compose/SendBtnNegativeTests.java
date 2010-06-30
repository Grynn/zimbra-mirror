package projects.zcs.tests.mail.compose;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

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
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
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
		if (isExecutionARetry)
			handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zVerifySendThrowsError(to, cc, bcc, subject, body,
				attachments, errDlgName, errMsg);
		page.zComposeView.zGoToMailAppFromCompose();
		needReset = false;
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
		if (isExecutionARetry)
			handleRetry();
		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zVerifySendThrowsError(to, cc, bcc, subject, body,
				attachments, errDlgName, errMsg);
		page.zComposeView.zGoToMailAppFromCompose();
		needReset = false;// indicates no need to login for the next test
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		page.zComposeView.zGoToMailAppFromCompose();
		zLogin();
	}

}
