package projects.zcs.tests.calendar.apptactions;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class AppointmentActionTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "dataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("sendingInvitationFromPersona")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(), "" },
					{
							getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							getOnlyEnglishAlphabetCharAndNumber()
									+ "@testdomain.com" } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	/**
	 * Sending invitation from persona using just from address and from & alias
	 * address. #Test1: Steps, 1.Create persona with ONLY from address 2.Compose
	 * appointment and invite to user2 3.Login to user2 and verify received mail
	 * with persona from address. #Test2: Steps, 1.Create persona with from &
	 * alias address 2.Compose appointment and invite to user2 3.Login to user2
	 * and verify received mail with persona from & alias address.
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void sendingInvitationFromPersona(String subject,
			String personaName, String alias) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		if (!alias.equals("")) {
			ProvZCS.addAlias(SelNGBase.selfAccountName.get(), alias);
			resetSession();
			Thread.sleep(1000);
			page.zLoginpage.zLoginToZimbraAjax(SelNGBase.selfAccountName.get());
		}
		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");
		String user1 = SelNGBase.selfAccountName.get().toLowerCase();
		String user2 = ProvZCS.getRandomAccount();
		String fromName = getLocalizedData_NoSpecialChar();
		page.zAccPref.zCreatePersona(personaName, fromName, alias);

		SelNGBase.selenium.get().refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		SelNGBase.selenium.get()
				.clickAt(
						"//td[contains(@id, '_select_container')]//td[contains(text(), '"
								+ SelNGBase.selfAccountName.get().toLowerCase()
								+ "')]", "");
		Thread.sleep(500);
		if (alias.equals("")) {
			SelNGBase.selenium
					.get()
					.clickAt(
							"//div[contains(@class, 'ZSelectMenuItem ZWidget ZHasText')]//td[contains(text(), '"
									+ SelNGBase.selfAccountName.get()
											.toLowerCase() + "')]", "");
		} else {
			SelNGBase.selenium
					.get()
					.clickAt(
							"//div[contains(@class, 'ZSelectMenuItem ZWidget ZHasText')]//td[contains(text(), '"
									+ alias.toLowerCase() + "')]", "");
		}
		page.zCalCompose.zCalendarEnterSimpleDetails(subject, "", user2,
				"body of" + subject);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		zWaitTillObjectExist("button", page.zCalApp.zCalNewApptBtn);
		obj.zAppointment.zExists(subject);

		resetSession();
		Thread.sleep(1000);
		SelNGBase.selfAccountName.set(user2);
		page.zLoginpage.zLoginToZimbraAjax(user2);
		MailApp.ClickCheckMailUntilMailShowsUp(fromName);
		Thread.sleep(500);
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(page.zMailApp.zShowOriginalMenuIconBtn);
		Thread.sleep(4000); // failed because of timing issue
		SelNGBase.selenium.get().selectWindow("_blank");
		String showOrigText = SelNGBase.selenium.get().getBodyText();
		Thread.sleep(1000);
		String expectedValue;
		if (alias.equals("")) {
			expectedValue = "From: " + fromName + " <" + user1.toLowerCase()
					+ ">";
		} else {
			expectedValue = "From: " + fromName + " <" + alias.toLowerCase()
					+ ">";
		}
		Assert.assertTrue(showOrigText.indexOf(expectedValue) >= 0,
				"Expected value(" + expectedValue + "), Actual Value("
						+ showOrigText + ")");
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}