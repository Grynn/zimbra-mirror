package projects.zcs.tests.calendar.meetingrequests.resources;

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
		if (test.equals("searchResourceAndAddtoAppointment")) {
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

	/**
	 * Search resource > add to appointment and verify. Steps, 1.Create resource
	 * type 'equipment' 2.Go to Calendar > find resources tab and verify buttons
	 * enable/disable 3.Search particular equipment and add to appointment and
	 * verify
	 */
	@Test(dataProvider = "dataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void searchResourceAndAddtoAppointment(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String equipment;
		equipment = "equipment2@testdomain.com";
		Stafzmprov.createEquipment(equipment);
		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		zGoToApplication("Calendar");
		page.zCalApp.zNavigateToApptCompose();
		obj.zTab.zClick(localize(locator.findResources));
		SleepUtil.sleep(1000);
		obj.zButton.zIsDisabled(localize(locator.add));
		obj.zButton.zIsDisabled(localize(locator.addAll));
		obj.zButton.zIsDisabled(localize(locator.remove));
		obj.zButton.zIsDisabled(localize(locator.removeAll));
		obj.zButton.zClick(localize(locator.search), "2");
		obj.zButton.zIsEnabled(localize(locator.add));
		obj.zButton.zIsEnabled(localize(locator.addAll));
		obj.zButton.zIsDisabled(localize(locator.remove));
		obj.zButton.zIsDisabled(localize(locator.removeAll));
		obj.zEditField.zType(getNameWithoutSpace(localize(locator.nameLabel)),
				equipment);
		pressKeys("enter");
		obj.zButton.zIsEnabled(localize(locator.add));
		obj.zButton.zIsEnabled(localize(locator.addAll));
		obj.zButton.zIsDisabled(localize(locator.remove));
		obj.zButton.zIsDisabled(localize(locator.removeAll));
		obj.zButton.zClick(localize(locator.add));
		obj.zButton.zIsDisabled(localize(locator.add));
		obj.zButton.zIsDisabled(localize(locator.addAll));
		obj.zButton.zIsDisabled(localize(locator.remove));
		obj.zButton.zIsEnabled(localize(locator.removeAll));

		obj.zTab.zClick(localize(locator.apptDetails));
		SelNGBase.selenium.get().click("link=" + equipment);
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