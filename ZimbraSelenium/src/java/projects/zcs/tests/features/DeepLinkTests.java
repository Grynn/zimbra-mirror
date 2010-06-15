package projects.zcs.tests.features;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class DeepLinkTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("simpleDeepLinkTest")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", "deepLinkTestSubject",
					"deepLinkTestBody", "" } };
		} else {
			return new Object[][] { {} };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
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

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void simpleDeepLinkTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		to = SelNGBase.selfAccountName;
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/h");
		Thread.sleep(5000);
		selenium.click("xpath=id('R0')/td[2]");
		Thread.sleep(2000);
		String currentURL, msgLocation;
		int msgId;
		currentURL = selenium.getLocation();
		msgId = currentURL.indexOf("&cid=");
		msgLocation = currentURL.substring(msgId).replaceAll("&cid=-", "");
		System.out.println(msgLocation);

		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "?app=mails&id=" + msgLocation);
		Thread.sleep(5000);

		obj.zFolder.zExists(page.zMailApp.zInboxFldr);
		obj.zFolder.zExists(page.zMailApp.zSentFldr);
		obj.zFolder.zExists(page.zMailApp.zDraftsFldr);
		obj.zFolder.zExists(page.zMailApp.zTrashFldr);
		obj.zButton.zClick("id=zb__MSG__CLOSE_left_icon");
		obj.zMessageItem.zExists(subject);
		obj.zFolder.zExists(page.zMailApp.zInboxFldr);
		obj.zFolder.zExists(page.zMailApp.zSentFldr);
		obj.zFolder.zExists(page.zMailApp.zDraftsFldr);
		obj.zFolder.zExists(page.zMailApp.zTrashFldr);
		obj.zMessageItem.zClick(subject);

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}