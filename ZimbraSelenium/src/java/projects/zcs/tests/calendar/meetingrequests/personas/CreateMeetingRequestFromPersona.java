package projects.zcs.tests.calendar.meetingrequests.personas;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class CreateMeetingRequestFromPersona extends CommonTest {
	// --------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	// --------------------------------------------------------------------------
	@DataProvider(name = "dataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("sendingInvitationWithPersonaFrom")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "" } };
		} else if (test.equals("sendingInvitationWithPersonaFromWithAlias")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getOnlyEnglishAlphabetCharAndNumber() + "@testdomain.com" } };
		} else {
			return new Object[][] { { "" } };
		}
	}
	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="calendar";
		super.zLogin();
	}

	// --------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	// --------------------------------------------------------------------------
	/**
	 * Sending invitation from persona using just from address and from & alias
	 * address. Steps, 1.Create persona with ONLY from address 2.Compose
	 * appointment and invite to user2 3.Login to user2 and verify received mail
	 * with persona from address.
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendingInvitationWithPersonaFrom(String subject,
			String personaName, String alias) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");
		String user1 = SelNGBase.selfAccountName.get().toLowerCase();
		String user2 = Stafzmprov.getRandomAccount();
		String fromName = getLocalizedData_NoSpecialChar();
		page.zAccPref.zCreatePersona(personaName, fromName, alias);

		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		SelNGBase.selenium.get()
				.clickAt(
						"//td[contains(@id, '_select_container')]//td[contains(text(), '"
								+ SelNGBase.selfAccountName.get().toLowerCase()
								+ "')]", "");
		SleepUtil.sleep(500);
		SelNGBase.selenium
				.get()
				.clickAt(
						"//div[contains(@class, 'ZSelectMenuItem ZWidget ZHasText')]//td[contains(text(), '"
								+ SelNGBase.selfAccountName.get().toLowerCase()
								+ "')]", "");

		page.zCalCompose.zCalendarEnterSimpleDetails(subject, "", user2,
				"body of" + subject);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
		obj.zAppointment.zExists(subject);

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(user2);
		page.zLoginpage.zLoginToZimbraAjax(user2);
		MailApp.ClickCheckMailUntilMailShowsUp(fromName);
		SleepUtil.sleep(500);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(page.zMailApp.zShowOriginalMenuIconBtn);
		SleepUtil.sleep(4000); // failed because of timing issue
		SelNGBase.selenium.get().selectWindow("_blank");
		String showOrigText = SelNGBase.selenium.get().getBodyText();
		SleepUtil.sleep(1000);
		String expectedValue;
		expectedValue = "From: " + fromName + " <" + user1.toLowerCase() + ">";
		Assert.assertTrue(showOrigText.indexOf(expectedValue) >= 0,
				"Expected value(" + expectedValue + "), Actual Value("
						+ showOrigText + ")");
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Sending invitation from persona using just from address and from & alias
	 * address. Steps, 1.Create persona with from & alias address 2.Compose
	 * appointment and invite to user2 3.Login to user2 and verify received mail
	 * with persona from & alias address.
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendingInvitationWithPersonaFromWithAlias(String subject,
			String personaName, String alias) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		Stafzmprov.addAccountAlias(SelNGBase.selfAccountName.get(), alias);
		resetSession();
		SleepUtil.sleep(1000);
		page.zLoginpage.zLoginToZimbraAjax(SelNGBase.selfAccountName.get());

		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");
		String user2 = Stafzmprov.getRandomAccount();
		String fromName = getLocalizedData_NoSpecialChar();
		page.zAccPref.zCreatePersona(personaName, fromName, alias);

		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		SelNGBase.selenium.get()
				.clickAt(
						"//td[contains(@id, '_select_container')]//td[contains(text(), '"
								+ SelNGBase.selfAccountName.get().toLowerCase()
								+ "')]", "");
		SleepUtil.sleep(500);
		SelNGBase.selenium
				.get()
				.clickAt(
						"//div[contains(@class, 'ZSelectMenuItem ZWidget ZHasText')]//td[contains(text(), '"
								+ alias.toLowerCase() + "')]", "");
		page.zCalCompose.zCalendarEnterSimpleDetails(subject, "", user2,
				"body of" + subject);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
		obj.zAppointment.zExists(subject);

		resetSession();
		SleepUtil.sleep(1000);
		SelNGBase.selfAccountName.set(user2);
		page.zLoginpage.zLoginToZimbraAjax(user2);
		MailApp.ClickCheckMailUntilMailShowsUp(fromName);
		SleepUtil.sleep(500);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(page.zMailApp.zShowOriginalMenuIconBtn);
		SleepUtil.sleep(4000); // failed because of timing issue
		SelNGBase.selenium.get().selectWindow("_blank");
		String showOrigText = SelNGBase.selenium.get().getBodyText();
		SleepUtil.sleep(1000);
		String expectedValue;
		expectedValue = "From: " + fromName + " <" + alias.toLowerCase() + ">";
		Assert.assertTrue(showOrigText.indexOf(expectedValue) >= 0,
				"Expected value(" + expectedValue + "), Actual Value("
						+ showOrigText + ")");
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}
}