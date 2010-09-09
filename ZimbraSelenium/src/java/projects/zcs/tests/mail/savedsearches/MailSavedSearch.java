package projects.zcs.tests.mail.savedsearches;

import java.lang.reflect.Method;

import org.testng.Assert;
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

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class MailSavedSearch extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("mailSaveSearch_Bug34872_Bug44871")
				|| test.equals("mailSaveSearch_Bug44232")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar(), getLocalizedData(5), "" } };
		} else {
			return new Object[][] { { "" } };
		}
	}


	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="mail";
		super.zLogin();
	}
	
	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailSaveSearch_Bug34872_Bug44871(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = ClientSessionFactory.session().currentUserName();
		String[] recipients = { to };

		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent("zlhi__CLV__ex"));

		LmtpUtil.injectMessage(to, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		ClientSessionFactory.session().selenium().type("xpath=//input[@class='search_input']", subject);
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zMessageItem.zExists(subject);
		obj.zButton.zClick("id=zb__Search__SAVE_left_icon");
		obj.zEditField.zTypeInDlgByName("id=*nameField", "Srch" + subject,
				localize(locator.saveSearch));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.saveSearch));

		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent("zlhi__TV__fg"));

		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		SleepUtil.sleep(1000);
		String msgExists = obj.zMessageItem.zExistsDontWait(subject);
		assertReport("false", msgExists, "Sent folder doesn't refresh properly");
		obj.zFolder.zClick("Srch" + subject);
		obj.zMessageItem.zClick(subject);

		obj.zButtonMenu.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		Assert.assertTrue(ClientSessionFactory.session().selenium().isElementPresent("zlhi__TV__fg"));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void mailSaveSearch_Bug44232(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		to = ClientSessionFactory.session().currentUserName();
		String[] recipients = { to };
		/**
		 * Send mail and verify that is:flagged search is not returning
		 * anything.
		 */
		LmtpUtil.injectMessage(to, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		ClientSessionFactory.session().selenium().type("xpath=//input[@class='search_input']", "is:flagged");
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zMessageItem.zNotExists(subject);

		/**
		 * Flag mail sent in the previous step.
		 */
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zClick(subject);
		ClientSessionFactory.session().selenium().clickAt(
				"//*[contains(@id, '__fg')  and contains(@class, 'ImgBlank')]",
				"");

		/**
		 * Send another mail and keep it unflagged.
		 */
		String new_subject = "New" + subject;
		LmtpUtil.injectMessage(to, recipients, cc, new_subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(new_subject);

		/**
		 * is:flagged search should return only first mail. Second mail should
		 * not be present after hitting search button.
		 */
		ClientSessionFactory.session().selenium().type("xpath=//input[@class='search_input']", "is:flagged");
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zMessageItem.zNotExists(new_subject);
		obj.zMessageItem.zExists(subject);

		SelNGBase.needReset.set(false);
	}
}