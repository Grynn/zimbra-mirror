package projects.zcs.tests.preferences.mail.accounts;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

/**
 * This covers some high priority test cases related to address book
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class AccountsPref extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "AccPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("addBasicPersona")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("addBasicPersonaWithTextSignature")
				|| test.equals("addBasicPersonaWithEditTextSignature")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("addBasicPOP3ExternalAcc")
				|| test.equals("addBasicIMAPExternalAcc")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"accName" + getLocalizedData_NoSpecialChar(),
					"PW" + getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("addBasicPOP3ExternalAccWithTextSignature")
				|| test.equals("addBasicIMAPExternalAccWithTextSignature")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					ProvZCS.getRandomAccount(),
					"accName" + getLocalizedData_NoSpecialChar(),
					"PW" + getLocalizedData_NoSpecialChar() } };
		} else {
			return new Object[][] { {} };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
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

	/**
	 * This test creates a basic persona and check whether the preferences gets
	 * saved after creating basic persona
	 * 
	 * @param personaName
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addBasicPersona(String personaName) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addPersona));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.accountPersonaLabel)),
				personaName);
		obj.zCheckbox.zClick(localize(locator.personaWhenSentTo));
		obj.zCheckbox.zClick(localize(locator.personaWhenInFolder));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences should be saved");

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates a basic Persona with a text signature and verifies
	 * while composing when the persona is selected,the signature gets displayed
	 * in mail body
	 * 
	 * @param personaName
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addBasicPersonaWithTextSignature(String personaName,
			String signatureName, String signatureBody) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		SleepUtil.sleep(1500);
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addPersona));
		obj.zEditField
				.zType(localize(locator.accountPersonaLabel), personaName);

		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(signatureName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences should be saved");
		page.zSignaturePref.zVerifySignatureInMail(personaName, signatureName,
				signatureBody);
		obj.zButton.zClick(localize(locator.cancel));
		obj.zButton.zClickInDlg(localize(locator.no));

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test adds a persona with a text signature.Edits the signature and
	 * then verifies the edited signature w.r.t. edited signature
	 * 
	 * @param personaName
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addBasicPersonaWithEditTextSignature(String personaName,
			String signatureName, String signatureBody) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		zGoToApplication("Preferences");
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addPersona));
		obj.zEditField
				.zType(localize(locator.accountPersonaLabel), personaName);

		obj.zButton.zClick(localize(locator.signatureDoNotAttach), "2");
		obj.zMenuItem.zClick(signatureName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences-persona should be saved");
		page.zSignaturePref.zVerifySignatureInMail(personaName, signatureName,
				signatureBody);
		signatureBody = getLocalizedData_NoSpecialChar();
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zEditSignature("", signatureBody, "TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zSignaturePref.zVerifySignatureInMail(personaName, signatureName,
				signatureBody);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences-edited signature should be saved");

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test is to add a external POP3 account(not a actual POP3
	 * account,just a random zimbra acc).This test only enters some mandatory
	 * values and verifies whether "Test Setting" dlg box appears or not when
	 * Clicked on preferences Save button
	 * 
	 * @param emailAddress
	 * @param accName
	 * @param password
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addBasicPOP3ExternalAcc(String emailAddress, String accName,
			String password) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addExternalAccount));
		page.zAccPref.zEnterExternalAccData(emailAddress, accName, "POP3", "",
				"SSL", "");
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zDialog.zExists(localize(locator.accountTest));
		obj.zButton.zClickInDlg(localize(locator.cancel));
		obj.zButton.zClick(localize(locator.del));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test to create a External POP3 account with signature.This test only
	 * enters some mandatory values and verifies whether "Test Setting" dlg box
	 * appears or not when Clicked on preferences Save button
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @param emailAddress
	 * @param accName
	 * @param password
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addBasicPOP3ExternalAccWithTextSignature(String signatureName,
			String signatureBody, String emailAddress, String accName,
			String password) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addExternalAccount));
		page.zAccPref.zEnterExternalAccData(emailAddress, accName, "POP3", "",
				"SSL", "");
		obj.zFeatureMenu
				.zClick(getNameWithoutSpace(localize(locator.signatureLabel)));
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zDialog.zExists(localize(locator.accountTest));
		obj.zButton.zClickInDlg(localize(locator.cancel));
		obj.zButton.zClick(localize(locator.del));

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test is to add a external IMAP account(not a actual IMAP
	 * account,just a random zimbra acc).This test only enters some mandatory
	 * values and verifies whether "Test Setting" dlg box appears or not when
	 * Clicked on preferences Save button
	 * 
	 * @param emailAddress
	 * @param accName
	 * @param password
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addBasicIMAPExternalAcc(String emailAddress, String accName,
			String password) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addExternalAccount));
		page.zAccPref.zEnterExternalAccData(emailAddress, accName, "IMAP", "",
				"SSL", "");
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zDialog.zExists(localize(locator.accountTest));
		obj.zButton.zClickInDlg(localize(locator.cancel));
		obj.zButton.zClick(localize(locator.del));

		SelNGBase.needReset.set(false);

	}

	/**
	 * Test to create a External POP3 account with signature.This test only
	 * enters some mandatory values and verifies whether "Test Setting" dlg box
	 * appears or not when Clicked on preferences Save button
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @param emailAddress
	 * @param accName
	 * @param password
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addBasicIMAPExternalAccWithTextSignature(String signatureName,
			String signatureBody, String emailAddress, String accName,
			String password) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				"TEXT");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));
		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addExternalAccount));

		page.zAccPref.zEnterExternalAccData(emailAddress, accName, "IMAP", "",
				"SSL", "");
		obj.zFeatureMenu
				.zClick(getNameWithoutSpace(localize(locator.signatureLabel)));
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zDialog.zExists(localize(locator.accountTest));
		obj.zButton.zClickInDlg(localize(locator.cancel));
		obj.zButton.zClick(localize(locator.del));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:-compose in new window broken when identity is enabled Go to
	 * Preferences -> Composing Select: Always Compose in New Window. Go to
	 * Preference -> Account Add an identity for an external POP or IMAP
	 * account. Check Test Setting with "Success" Message. Click on Save button
	 * and again check for Test setting diaglog box with "success" msg. Go to
	 * Inbox Click on 'New' to compose a mail in New Window. Validate all the
	 * option for enable like Send,Save Draft,Spell Check etc all the option
	 * should enable.
	 * 
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addPOP3ExternalAccComposeWindow_38611() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zCheckbox.zClick(localize(locator.composeInNewWin));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Composing should be saved");
		SleepUtil.sleep(2000);
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addExternalAccount));
		page.zAccPref.zEnterExternalAccData("zimbratestpop@gmail.com", "dummy",
				"POP3", "test123456", "SSL", "");
		obj.zEditField.zType("id=*EXTERNAL_HOST", "pop.gmail.com");
		obj.zButton.zClick(localize(locator.accountTest));
		zWaitTillObjectExist("element",
				"xpath=//td[contains(@id, '_test_status') and  contains(text(),"
						+ "'" + localize(locator.dataSourceTestSuccess) + "')"
						+ "]");
		Assert
				.assertTrue(SelNGBase.selenium.get()
						.isElementPresent("xpath=//td[contains(@id, '_test_status') and  contains(text(),"
								+ "'"
								+ localize(locator.dataSourceTestSuccess)
								+ "')" + "]"));
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		zWaitTillObjectExist("element",
				"xpath=//td[contains(@id, '_test_status') and  contains(text(),"
						+ "'" + localize(locator.dataSourceTestSuccess) + "')"
						+ "]");
		Assert
				.assertTrue(SelNGBase.selenium.get()
						.isElementPresent("xpath=//td[contains(@id, '_test_status') and  contains(text(),"
								+ "'"
								+ localize(locator.dataSourceTestSuccess)
								+ "')" + "]"));
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1500);

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		SleepUtil.sleep(1500);
		SelNGBase.selenium.get().selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zSendBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zSendBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zCancelBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zSaveDraftsBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zSpellCheckBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zAddAttachmentBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zOptionsBtn_newWindow);
		obj.zButton.zClick(page.zMailApp.zCancelBtn_newWindow);
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}

}