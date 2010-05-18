package projects.zcs.tests.others;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.zimbra.cs.service.account.GetAccountInfo;
import com.zimbra.cs.service.account.GetPrefs;
import com.zimbra.cs.service.admin.GetAccount;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

@SuppressWarnings( { "static-access", "unused" })
public class CustomLoginTest extends CommonTest {

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zKillBrowsers();
		page.zLoginpage.zCustomLoginToZimbraAjax("calendar");
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

	// Tests
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void CustomLoginToCalendarApp() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		obj.zFolder.zExists(localize(locator.calendar));
		obj.zButton.zNotExists(localize(locator.newFolder));
		obj.zButton.zNotExists(page.zMailApp.zGetMailIconBtn);
		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
