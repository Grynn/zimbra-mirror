package projects.zcs.tests.calendar.meetingrequests.schedule;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
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
		if (test.equals("verifyAttendeeAutocompleteInScheduleTab")
				|| test.equals("verifyLocationAutocompleteInScheduleTab")
				|| test.equals("verifyResourcesAutocompleteInScheduleTab")) {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		} else {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		}
	}

	// --------------------------------------------------------------------------
	// SECTION 2: SETUP
	// --------------------------------------------------------------------------
	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="calendar";
		super.zLogin();
	}


	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Verify attendee autocomplete in schedule tab. Steps, 1.Create account
	 * 2.Compose appointment > go to Schedule tab and select type attendee
	 * 3.Verify autocomplete exists, press Enter and and verify again in
	 * Appointment details attendees field
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAttendeeAutocompleteInScheduleTab(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String actualValue, expectedValue;
		acc1 = ZimbraSeleniumProperties.getStringProperty("locale").replace(
				"_", "")
				+ "sche1@testdomain.com";
		acc1 = acc1.toLowerCase();
		getKeyboardKeys(acc1);
		Stafzmprov.createAccount(acc1);
		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zTab.zClick(localize(locator.schedule));
		SleepUtil.sleep(1000);
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

		pressKeys("enter");
		SleepUtil.sleep(1000);
		obj.zTab.zClick(localize(locator.apptDetails));
		SleepUtil.sleep(1000);
		expectedValue = acc1.toLowerCase();
		actualValue = obj.zTextAreaField
				.zGetInnerText(getNameWithoutSpace(localize(locator.attendeesLabel)));
		assertReport(actualValue, expectedValue,
				"Verifying autocomplete for attendee in schedule tab");
		obj.zButton.zClick(page.zCalCompose.zApptCancelBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verify location autocomplete in schedule tab & location link. Steps,
	 * 1.Create resource account with type "location" 2.Compose appointment > go
	 * to Schedule tab and select Location and verify location autocomplete with
	 * tab key and also verify location link by clicking it
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyLocationAutocompleteInScheduleTab(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String location, actualValue, expectedValue;
		location = "location2@testdomain.com";
		Stafzmprov.createLocation(location);
		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zTab.zClick(localize(locator.schedule));
		SleepUtil.sleep(1000);
		SelNGBase.selenium
				.get()
				.clickAt(
						"//div//table[contains(@id, 'attendeesTable')]//td[contains(@id, 'select_container')]",
						"");
		obj.zMenuItem.zClick(localize(locator.location));
		SleepUtil.sleep(1000);

		obj.zEditField
				.zActivate("//input[contains(@id,'_INPUT_') and @class='ZmSchedulerInput']");

		pressKeys("l, o, c, a, t, i, o, n, 2, tab");
		SleepUtil.sleep(1000);

		obj.zTab.zClick(localize(locator.apptDetails));
		SleepUtil.sleep(1000);

		expectedValue = location;
		actualValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport(expectedValue, actualValue,
				"Verifying autocomplete for location in schedule tab");
		SelNGBase.selenium.get().click("link=" + location);
		SleepUtil.sleep(1000);

		obj.zButton.zIsDisabled(localize(locator.select));
		obj.zButton.zIsEnabled(localize(locator.remove));
		obj.zButton.zClick(page.zCalCompose.zApptCancelBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verify resources autocomplete in schedule tab & resources link. Steps,
	 * 1.Create resource account with type "equipment" 2.Compose appointment >
	 * go to Schedule tab and select Resource and verify resource autocomplete
	 * with enter key and also verify resource link by clicking it
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyResourcesAutocompleteInScheduleTab(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String equipment;
		equipment = "equipment1@testdomain.com";
		Stafzmprov.createEquipment(equipment);
		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zTab.zClick(localize(locator.schedule));
		SleepUtil.sleep(1000);
		SelNGBase.selenium
				.get()
				.clickAt(
						"//div//table[contains(@id, 'attendeesTable')]//td[contains(@id, 'select_container')]",
						"");
		obj.zMenuItem.zClick(localize(locator.resourceAttendee));
		SleepUtil.sleep(1000);

		obj.zEditField
				.zActivate("//input[contains(@id,'_INPUT_') and @class='ZmSchedulerInput']");

		pressKeys("tab, e, q, u, i, p, m, e, n, t, 1, enter");
		SleepUtil.sleep(1000);

		obj.zTab.zClick(localize(locator.apptDetails));
		SleepUtil.sleep(1000);

		SelNGBase.selenium.get().click("link=" + equipment);
		SleepUtil.sleep(1000);

		obj.zButton.zIsDisabled(localize(locator.add));
		obj.zButton.zIsDisabled(localize(locator.addAll));
		obj.zButton.zIsEnabled(localize(locator.remove));
		obj.zButton.zIsEnabled(localize(locator.removeAll));
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