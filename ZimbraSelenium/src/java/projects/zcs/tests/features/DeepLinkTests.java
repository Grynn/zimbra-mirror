package projects.zcs.tests.features;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.core.*;
import framework.util.LmtpUtil;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

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
			return new Object[][] { { ClientSessionFactory.session().currentUserName(), "ccuser@testdomain.com",
					"bccuser@testdomain.com", "deepLinkTestSubject",
					"deepLinkTestBody", "" } };
		} else {
			return new Object[][] { {} };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		//super.NAVIGATION_TAB="documents";
		super.zLogin();
	}
	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void simpleDeepLinkTest(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = ClientSessionFactory.session().currentUserName();
		String[] recipients = { to };
		LmtpUtil.injectMessage(to, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		ClientSessionFactory.session().selenium().open(ZimbraSeleniumProperties.getStringProperty("mode") + "://"
				+ ZimbraSeleniumProperties.getStringProperty("server") + "/h");
		SleepUtil.sleep(5000);
		ClientSessionFactory.session().selenium().click("xpath=id('R0')/td[2]");
		SleepUtil.sleep(2000);
		String currentURL, msgLocation;
		int msgId;
		currentURL = ClientSessionFactory.session().selenium().getLocation();
		msgId = currentURL.indexOf("&cid=");
		msgLocation = currentURL.substring(msgId).replaceAll("&cid=-", "");
		System.out.println(msgLocation);

		ClientSessionFactory.session().selenium().open(ZimbraSeleniumProperties.getStringProperty("mode") + "://"
				+ ZimbraSeleniumProperties.getStringProperty("server") + "?app=mails&id=" + msgLocation);
		SleepUtil.sleep(5000);

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

		SelNGBase.needReset.set(false);
	}
}