package projects.zcs.tests.preferences;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

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
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("verifyNewMailedContactAddsToEmailedContact")) {
			return new Object[][] { { ProvZCS.getRandomAccount() } };
		} else if (test.equals("importContact")) {
			return new Object[][] { { "MultiLingualContact.csv", "lastName" } };
		} else {
			return new Object[][] { { "localize(locator.GAL)" } };
		}

	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
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
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zNavigateToPrefImportExport();
		obj.zRadioBtn.zClick(localize(locator.contacts));
		File f = new File("projects/zcs/data/" + csvFile);
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
		page.zABCompose.zNavigateToContact();

		obj.zContactListItem.zExists(englishContact);

		String currentLocale = config.getString("locale");
		if (currentLocale.equals("ja") || currentLocale.equals("zh_CN")
				|| currentLocale.equals("ru") || currentLocale.equals("de")
				|| currentLocale.equals("pl")) {
			obj.zContactListItem.zExists(localize(locator.cell));

		}

		needReset = false;

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
		if (isExecutionARetry)
			handleRetry();

		String accountName = selfAccountName;
		page.zABCompose.zNavigateToPreferenceAB();

		String actualValue = ProvZCS.getAccountPreferenceValue(accountName,
				"zimbraPrefAutoAddAddressEnabled");
		if (actualValue.equals("FALSE")) {
			String[] autoAddToEmailedContact = localize(locator.autoAddContacts)
					.split("\"");
			if (config.getString("locale").equals("ko")
					|| config.getString("locale").equals("hi")) {
				obj.zCheckbox.zClick(autoAddToEmailedContact[1]);
			} else
				obj.zCheckbox.zClick(autoAddToEmailedContact[0]);
		}
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(2000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"preferences should be saved");
		actualValue = ProvZCS.getAccountPreferenceValue(accountName,
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

		page.zABCompose.zNavigateToContact();
		obj.zFolder.zClick(page.zABCompose.zEmailedContactsFolder);
		obj.zContactListItem.zExists(toContactSplit[0]);
		// reset auto-add contact to false
		ProvZCS.modifyAccount(SelNGBase.selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "FALSE");
		needReset = false;

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
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zNavigateToPreferenceAB();
		String actualValue = ProvZCS.getAccountPreferenceValue(
				SelNGBase.selfAccountName, "zimbraPrefGalSearchEnabled");
		if (actualValue.equals("FALSE")) {
			obj.zCheckbox.zClick(localize(locator.initiallySearchGal));
			obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
			actualValue = ProvZCS.getAccountPreferenceValue(
					SelNGBase.selfAccountName, "zimbraPrefGalSearchEnabled");

			Assert
					.assertEquals(actualValue, "TRUE",
							"Intiatlly Search GAL when using contact picker is not set at DB");
		}

		obj.zButton.zClick(page.zABCompose.zMailTabIconBtn);
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		obj.zButton.zClick(localize(locator.toLabel));
		Thread.sleep(2000);
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

		// if (config.getString("locale").equals("zh_HK")) {
		// obj.zFeatureMenu.zExistsDontWait(GAL);
		// Robot keyToPress = new Robot();
		// keyToPress.keyPress(KeyEvent.VK_ESCAPE);
		// keyToPress.keyRelease(KeyEvent.VK_ESCAPE);
		// } else {
		// String infoDlgExist;
		// Thread.sleep(1000);
		// infoDlgExist = obj.zDialog
		// .zExistsDontWait(localize(locator.infoMsg));
		// if (infoDlgExist.equals("true")) {
		// obj.zButton.zClickInDlg(localize(locator.ok), "2");
		// }
		// obj.zFeatureMenu.zExistsDontWait(GAL);
		// obj.zButton.zClickInDlg(localize(locator.cancel));
		// }
		needReset = false;

	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

}