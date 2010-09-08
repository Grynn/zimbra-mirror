package projects.zcs.tests.preferences.addressbook;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import projects.zcs.ui.ComposeView;

import com.zimbra.common.service.ServiceException;

import framework.core.*;
import framework.util.HarnessException;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

/**
 * This covers some high priority test cases related to address book
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class ABPreferences extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException, HarnessException {
		String test = method.getName();
		if (test.equals("verifyNewMailedContactAddsToEmailedContact")) {
			return new Object[][] { { Stafzmprov.getRandomAccount() } };
		} else if (test.equals("importContact")) {
			return new Object[][] { { "MultiLingualContact.csv", "lastName" } };
		} else {
			return new Object[][] { { "localize(locator.GAL)" } };
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
	

	/**
	 * Test to import contact from MultiLingualContact.csv file in test data.And
	 * then verify contacts got imported or not with respect to current
	 * locales.Does not covers all the locales
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void importContact(String csvFile, String englishContact)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("ar", "na", "", "not able to click on Contacts radio button (selenium bug)");

		page.zABCompose.zNavigateToPrefImportExport();
		obj.zRadioBtn.zClick(localize(locator.contacts));
		File f = new File("src/java/projects/zcs/data/" + csvFile);
		String path = f.getAbsolutePath();

		obj.zBrowseField.zTypeWithKeyboard(localize(locator.fileLabel), path);
		obj.zButton.zClick(localize(locator._import));

		zWaitTillObjectExist("dialog", localize(locator.infoMsg));
		String expectedMsg = localize(locator.importSuccess);
		String actualMsg = obj.zDialog.zGetMessage(localize(locator.infoMsg));

		Assert.assertTrue(actualMsg.equals(expectedMsg),
				"Actual message of import contact " + actualMsg
						+ " is not same as expected message " + expectedMsg);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.infoMsg));
		// Assert.assertTrue(actualMsg.equals(expectedMsg),
		// "Actual message of import contact " + actualMsg
		// + " is not same as expected message " + expectedMsg);
		// to verify the contacts got imported successfully
		page.zABCompose.navigateTo(ActionMethod.DEFAULT);

		obj.zContactListItem.zExists(englishContact);

		String currentLocale = ZimbraSeleniumProperties.getStringProperty("locale");
		if (currentLocale.equals("ja") || currentLocale.equals("zh_CN")
				|| currentLocale.equals("ru") || currentLocale.equals("de")
				|| currentLocale.equals("pl")) {
			obj.zContactListItem.zExists(localize(locator.cell));

		}

		SelNGBase.needReset.set(false);

	}

	/**
	 * @param to
	 *            : randomly generated account id to which the mail has to be
	 *            sent
	 * @throws Exception
	 */

	@Test(dataProvider = "ABPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyNewMailedContactAddsToEmailedContact(String to)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String accountName = SelNGBase.selfAccountName.get();
		page.zABCompose.zNavigateToPreferenceAB();

		String actualValue = Stafzmprov.getAccountPreferenceValue(accountName,
				"zimbraPrefAutoAddAddressEnabled");
		if (actualValue.equals("FALSE")) {
			String[] autoAddToEmailedContact = localize(locator.autoAddContacts)
					.split("\"");
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("ko")
					|| ZimbraSeleniumProperties.getStringProperty("locale").equals("hi")) {
				obj.zCheckbox.zClick(autoAddToEmailedContact[1]);
			} else
				obj.zCheckbox.zClick(autoAddToEmailedContact[0]);
		}
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(2000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"preferences should be saved");
		actualValue = Stafzmprov.getAccountPreferenceValue(accountName,
				"zimbraPrefAutoAddAddressEnabled");

		Assert.assertEquals(actualValue, "TRUE",
				"Add contacts to Emailed Contacts is not set at DB");

		String[] toContactSplit = to.split("_"); // splitting randomly created
		// account as the contact
		// gets displayed with name
		// before "_"
		obj.zButton.zClick(page.zABCompose.zMailTabIconBtn);
		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zEnterComposeValues(to, "", "", "Subject",
				"TestBody", "");
		obj.zButton.zClick(ComposeView.zSendIconBtn);

		page.zABCompose.navigateTo(ActionMethod.DEFAULT);
		obj.zFolder.zClick(page.zABCompose.zEmailedContactsFolder);
		obj.zContactListItem.zExists(toContactSplit[0]);
		// reset auto-add contact to false
		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefAutoAddAddressEnabled", "FALSE");
		SelNGBase.needReset.set(false);

	}

	/**
	 * Test to verify AB preference
	 * "Initially search the Global Address List when using the contact picker"
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyIntialSearchInGALForAddressPicker(String GAL)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zABCompose.zNavigateToPreferenceAB();
		String actualValue = Stafzmprov.getAccountPreferenceValue(
				SelNGBase.selfAccountName.get(), "zimbraPrefGalSearchEnabled");
		if (actualValue.equals("FALSE")) {
			obj.zCheckbox.zClick(localize(locator.initiallySearchGal));
			obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
			actualValue = Stafzmprov.getAccountPreferenceValue(
					SelNGBase.selfAccountName.get(), "zimbraPrefGalSearchEnabled");

			Assert
					.assertEquals(actualValue, "TRUE",
							"Intiatlly Search GAL when using contact picker is not set at DB");
		}

		obj.zButton.zClick(page.zABCompose.zMailTabIconBtn);
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		obj.zButton.zClick(localize(locator.toLabel));
		SleepUtil.sleep(2000);
		obj.zFeatureMenu.zExistsDontWait(GAL);
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.selectAddresses));
		Robot keyToPress = new Robot();
		keyToPress.keyPress(KeyEvent.VK_ESCAPE);
		keyToPress.keyRelease(KeyEvent.VK_ESCAPE);
		keyToPress.keyPress(KeyEvent.VK_ESCAPE);
		keyToPress.keyRelease(KeyEvent.VK_ESCAPE);
		keyToPress.keyPress(KeyEvent.VK_ESCAPE);
		keyToPress.keyRelease(KeyEvent.VK_ESCAPE);

		// if (ZimbraSeleniumProperties.getStringProperty("locale").equals("zh_HK")) {
		// obj.zFeatureMenu.zExistsDontWait(GAL);
		// Robot keyToPress = new Robot();
		// keyToPress.keyPress(KeyEvent.VK_ESCAPE);
		// keyToPress.keyRelease(KeyEvent.VK_ESCAPE);
		// } else {
		// String infoDlgExist;
		// SleepUtil.sleep(1000);
		// infoDlgExist = obj.zDialog
		// .zExistsDontWait(localize(locator.infoMsg));
		// if (infoDlgExist.equals("true")) {
		// obj.zButton.zClickInDlg(localize(locator.ok), "2");
		// }
		// obj.zFeatureMenu.zExistsDontWait(GAL);
		// obj.zButton.zClickInDlg(localize(locator.cancel));
		// }
		SelNGBase.needReset.set(false);

	}

}