package projects.zcs.tests.calendar.meetingrequests.locations;

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
import framework.util.ZimbraSeleniumProperties;
import projects.zcs.clients.ProvZCS;
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
		if (test.equals("verifyLocationAutocompleteAndLocationLink")
				|| test.equals("searchLocationAndAddtoAppointment")
				|| test.equals("addRemoveLocationAndResourceAndVerify")) {
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
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	/**
	 * Verify resources autocomplete and location link. Steps, 1.Create resource
	 * account with type "location" 2.Compose appointment and verify location
	 * autocomplete with enter key and also verify location link by clicking it
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyLocationAutocompleteAndLocationLink(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String location, actualValue, expectedValue;
		location = "location1@testdomain.com";
		ProvZCS.createResource(location, "location");
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zEditField
				.zActivate(getNameWithoutSpace(localize(locator.locationLabel)));
		pressKeys("l, o, c, a, t, i, o, n, 1, enter");
		SleepUtil.sleep(1500);
		expectedValue = location + ";";
		actualValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport(expectedValue.trim(), actualValue.trim(),
				"Verifying autocomplete for location");
		SelNGBase.selenium.get().click("link=" + location);
		obj.zButton.zIsDisabled(localize(locator.select));
		obj.zButton.zIsEnabled(localize(locator.remove));
		obj.zButton.zClick(page.zCalCompose.zApptCancelBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Search location > add to appointment and verify. Steps, 1.Create resource
	 * type 'location' 2.Go to Calendar > find locations tab and verify buttons
	 * enable/disable 3.Search particular location and add to appointment and
	 * verify
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void searchLocationAndAddtoAppointment(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String location, actualValue, expectedValue;
		location = "location3@testdomain.com";
		ProvZCS.createResource(location, "location");
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zTab.zClick(localize(locator.findLocations));
		SleepUtil.sleep(1000);
		obj.zButton.zIsDisabled(localize(locator.remove));
		obj.zButton.zClick(localize(locator.search), "2");
		obj.zButton.zIsEnabled(localize(locator.select));
		obj.zButton.zIsDisabled(localize(locator.remove));
		obj.zEditField.zType(getNameWithoutSpace(localize(locator.nameLabel)),
				location.split("@")[0]);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(localize(locator.search), "2");
		SleepUtil.sleep(2000);
		obj.zButton.zClick(localize(locator.select));
		SleepUtil.sleep(2000);
		obj.zTab.zClick(localize(locator.apptDetails));
		SleepUtil.sleep(1000);
		expectedValue = location;
		actualValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport(expectedValue, actualValue,
				"Verifying autocomplete for location");
		SelNGBase.selenium.get().click("link=" + location);
		obj.zButton.zIsDisabled(localize(locator.select));
		obj.zButton.zIsEnabled(localize(locator.remove));
		obj.zButton.zClick(page.zCalCompose.zApptCancelBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));

		SelNGBase.needReset.set(false);
	}

	
	/**
	 * Add/remove location and resource and verify accordingly. Steps, 1.Create
	 * location and equipment account 2.Compose appointment > add location via
	 * autocomplete and add equipment via search 3.Go to Appointment Details tab
	 * and verify both link 4.Save appointment > open appointment > remove
	 * location and verify location removed properly 5.Click to equipment link >
	 * remove it and save it 6.Open appointment again and re-verify location and
	 * equipment removed from appointment
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addRemoveLocationAndResourceAndVerify(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String apptSubject, location, equipment, actualValue, expectedValue;
		apptSubject = getLocalizedData_NoSpecialChar();
		location = "location4@testdomain.com";
		equipment = "equipment3@testdomain.com";
		ProvZCS.createResource(location, "location");
		ProvZCS.createResource(equipment, "equipment");
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.subjectLabel)),
				apptSubject);
		obj.zEditField
				.zActivate(getNameWithoutSpace(localize(locator.locationLabel)));
		pressKeys("l, o, c, a, t, i, o, n, 4, down, enter");
		actualValue = location;
		expectedValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport(expectedValue, actualValue,
				"Verifying autocomplete for location");
		obj.zTab.zClick(localize(locator.findResources));
		SleepUtil.sleep(1000);
		obj.zEditField.zType(getNameWithoutSpace(localize(locator.nameLabel)),
				equipment);
		SleepUtil.sleep(2000);
		obj.zButton.zClick(localize(locator.search), "2");
		SleepUtil.sleep(2000);
		obj.zButton.zClick(localize(locator.add));
		SleepUtil.sleep(1000);
		obj.zTab.zClick(localize(locator.apptDetails));
		SleepUtil.sleep(1000);
		Assert.assertEquals(true,
				SelNGBase.selenium.get().isElementPresent("link=" + location),
				"Verifying location link exists");
		Assert.assertEquals(true,
				SelNGBase.selenium.get().isElementPresent("link=" + equipment),
				"Verifying resource link exists");
		expectedValue = location;
		actualValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport(expectedValue, actualValue,
				"Verifying autocomplete for location");
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(2000);
		String isDlgExists;
		isDlgExists = obj.zDialog
				.zExistsDontWait(localize(locator.resourceConflictLabel));
		if (isDlgExists.equals("true")) {
			obj.zButton.zClickInDlgByName(localize(locator.save),
					localize(locator.resourceConflictLabel));
		}

		obj.zAppointment.zDblClick(apptSubject);
		SleepUtil.sleep(1500);
		actualValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport(expectedValue, actualValue,
				"Verifying autocomplete for location");
		Assert.assertEquals(true,
				SelNGBase.selenium.get().isElementPresent("link=" + location),
				"Verifying location link exists");
		Assert.assertEquals(true,
				SelNGBase.selenium.get().isElementPresent("link=" + equipment),
				"Verifying resource link exists");
		obj.zEditField
				.zActivate(getNameWithoutSpace(localize(locator.locationLabel)));
		SleepUtil.sleep(1000);
		pressKeys("ctrl+a, backspace");
		SleepUtil.sleep(1000);
		actualValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport("<blank>", actualValue,
				"Verifying autocomplete for location");
		Assert.assertEquals(false,
				SelNGBase.selenium.get().isElementPresent("link=" + location),
				"Verifying location link not exists");
		Assert.assertEquals(true,
				SelNGBase.selenium.get().isElementPresent("link=" + equipment),
				"Verifying resource link exists");
		SelNGBase.selenium.get().click("link=" + equipment);
		obj.zButton.zClick(localize(locator.remove));
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);
		SleepUtil.sleep(1000);

		obj.zAppointment.zDblClick(apptSubject);
		SleepUtil.sleep(1500);
		actualValue = obj.zEditField
				.zGetInnerText(getNameWithoutSpace(localize(locator.locationLabel)));
		assertReport("<blank>", actualValue,
				"Verifying autocomplete for location");
		Assert.assertEquals(false,
				SelNGBase.selenium.get().isElementPresent("link=" + location),
				"Verifying location link not exists");
		Assert.assertEquals(false,
				SelNGBase.selenium.get().isElementPresent("link=" + equipment),
				"Verifying resource link not exists");
		obj.zButton.zClick(page.zCalCompose.zApptCloseBtn);

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

	// --------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	// --------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}