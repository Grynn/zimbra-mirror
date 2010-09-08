package projects.zcs.tests.calendar.meetingrequests.findattendees;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;

import framework.core.*;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class Autocomplete extends CommonTest {
	static String acc1, acc2;
	static String first = null, second = null, third = null, fourth = null,
			fifth = null, sixth = null, seventh = null, eighth = null;

	// --------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	// --------------------------------------------------------------------------
	@DataProvider(name = "dataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("verifyAttendeesFieldAutocompleteUsingEnterTabAndUPDownKey")) {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		} else {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
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
	 * Verify attendees field autocomplete and select value using enter, tab, up
	 * and down keyboard key and verify. 1.Create two account 2.Compose
	 * appointment and enter acc2 value and verify autocomplete > select value
	 * using enter key and send invitation to acc2 3.Login to acc2 and verify
	 * received mail 4.Compose appointment and search autocomplete of user1,
	 * select value using tab key and invite to acc1 5.Login to acc1 > check
	 * received mail and compose appointment again, verify autocomplete value
	 * one by one using up and down keyboard key
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAttendeesFieldAutocompleteUsingEnterTabAndUPDownKey(
			String from, String to, String cc, String bcc, String subject,
			String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		subject = getLocalizedData_NoSpecialChar();
		acc1 = ZimbraSeleniumProperties.getStringProperty("locale").replace(
				"_", "")
				+ "atte1@testdomain.com";
		acc2 = ZimbraSeleniumProperties.getStringProperty("locale").replace(
				"_", "")
				+ "atte2@testdomain.com";
		Stafzmprov.createAccount(acc1);
		Stafzmprov.createAccount(acc2);
		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		Stafzmprov.modifyAccount(acc1, "zimbraPrefGalAutoCompleteEnabled", "TRUE");
		Stafzmprov.modifyAccount(acc2, "zimbraPrefGalAutoCompleteEnabled", "TRUE");

		System.out
				.println("Select autocomplete value using keyboard ENTER key");
		ClientSessionFactory.session().selenium().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		pressKeys("2");
		page.zMailApp.zVerifyAutocompleteExists(acc2.toLowerCase(), 1, 1);
		pressKeys("enter");
		SleepUtil.sleep(1500);
		assertReport(
				obj.zTextAreaField
						.zGetInnerText(localize(locator.attendeesLabel)),
				acc2.toLowerCase(),
				"Verifying autocomplete value - selected using enter key");
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.subjectLabel)), subject);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
		resetSession();
		SelNGBase.selfAccountName.set(acc2);
		page.zLoginpage.zLoginToZimbraAjax(acc2);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);

		System.out.println("Select autocomplete value using keyboard TAB key");
		typeKeyboardKeys();
		pressKeys("1");
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		pressKeys("tab");
		SleepUtil.sleep(1500);
		assertReport(
				obj.zTextAreaField
						.zGetInnerText(localize(locator.attendeesLabel)),
				acc1.toLowerCase(),
				"Verifying autocomplete value - selected using tab key");
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.subjectLabel)), subject);
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
		resetSession();
		SelNGBase.selfAccountName.set(acc1);
		page.zLoginpage.zLoginToZimbraAjax(acc1);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);

		System.out
				.println("Select autocomplete value using keyboard UP/DOWN - ENTER key & removing value and re-verifying autocomplete");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(acc2.toLowerCase(), 2, 0);
		pressKeys("down, enter");
		SleepUtil.sleep(1500);
		assertReport(
				obj.zTextAreaField
						.zGetInnerText(localize(locator.attendeesLabel)),
				acc2.toLowerCase(),
				"Verifying autocomplete value - selected using tab key");
		obj.zTextAreaField.zActivate(localize(locator.attendeesLabel));
		pressKeys("ctrl+a");
		if (ZimbraSeleniumProperties.getStringProperty("locale")
				.equals("en_US")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_GB")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_AU")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"pt_BR")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"zh_CN")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"zh_HK")) {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth + "," + seventh + "," + eighth);
		} else {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth);
		}
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(acc2.toLowerCase(), 2, 0);
		pressKeys("down, up, enter");
		SleepUtil.sleep(1500);
		assertReport(
				obj.zTextAreaField
						.zGetInnerText(localize(locator.attendeesLabel)),
				acc1.toLowerCase(),
				"Verifying autocomplete value - selected using tab key");
		obj.zButton.zClick(page.zCalCompose.zApptCancelBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}

	private static void getKeyboardKeys(String accont) throws Exception {
		if (ZimbraSeleniumProperties.getStringProperty("locale")
				.equals("en_US")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_GB")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_AU")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"pt_BR")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"zh_CN")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"zh_HK")) {
			first = accont.substring(0, 1);
			second = accont.substring(1, 2);
			third = accont.substring(2, 3);
			fourth = accont.substring(3, 4);
			fifth = accont.substring(4, 5);
			sixth = accont.substring(5, 6);
			seventh = accont.substring(6, 7);
			eighth = accont.substring(7, 8);
		} else {
			first = accont.substring(0, 1);
			second = accont.substring(1, 2);
			third = accont.substring(2, 3);
			fourth = accont.substring(3, 4);
			fifth = accont.substring(4, 5);
			sixth = accont.substring(5, 6);
		}
	}

	private static void typeKeyboardKeys() throws Exception {
		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zTextAreaField.zActivate(localize(locator.attendeesLabel));
		if (ZimbraSeleniumProperties.getStringProperty("locale")
				.equals("en_US")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_GB")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"en_AU")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"pt_BR")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"zh_CN")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals(
						"zh_HK")) {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth + "," + seventh + "," + eighth);
		} else {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth);
		}
	}
}