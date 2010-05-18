package projects.zcs.tests.preferences;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.zimbra.common.service.ServiceException;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest; //import projects.zcs.ui.MailApp;
import projects.zcs.ui.ComposeView;
import framework.util.RetryFailedTests;

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
		} else if (test.equals("createTextSignature")
				|| test.equals("editTextSignature")
				|| test.equals("lossOfSpacesAfterSignatureChange_Bug41092")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
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
		} else if (test.equals("verifyingComposingAndSignaturePref_39282")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };

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
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void lossOfSpacesAfterSignatureChange_Bug41092(String signatureName, String signatureBody)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();
		
		String defaultSignature = signatureName+"_default";
		String subject = "signature test";
		String body = "This is body with some spaces. We need to verify that spaces are not removed after switching signature.";

		/**
		 * Create Signature 1
		 */
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		
		/**
		 * Create Signature 2
		 */
		page.zAccPref.zNavigateToPreferenceSignature();
		obj.zButton.zClick(localize(locator.addSignature));
		page.zAccPref.zcreateSignature(defaultSignature, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		
		/**
		 * Make Signature 2 default
		 */
		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(defaultSignature);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		

		/**
		 * 1. Compose Mail.
		 * 2. Switch Signature.
		 * 3. Send Mail to self and click on received mail.
		 */
		page.zMailApp.zNavigateToMailApp();
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		page.zComposeView.zEnterComposeValues("_selfAccountName_", "", "", subject, body,
				"");
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		Thread.sleep(1000);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		zGoToApplication("Mail");
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zClick(subject);
		
		/**
		 * 1. Verify headers and body is correct.
		 * 2. Spaces should not be trimmed.
		 */
		page.zComposeView.zVerifyMsgHeaders("_selfAccountName_", "", "", subject, body,
				"");
		obj.zMessageItem.zVerifyCurrentMsgBodyText(body);

		needReset = false;
	}

	/**
	 * 
	 * This test case is to create text signature
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createTextSignature(String signatureName, String signatureBody)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");

		needReset = false;
	}
	
	
	
	/**
	 * Test To cerate a Text Signature and then edit the signature
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editTextSignature(String signatureName, String signatureBody)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zeditSignature("", getLocalizedData_NoSpecialChar(),
				"TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Edited Signature should be saved");

		needReset = false;
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
		if (isExecutionARetry)
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
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences should be saved");

		needReset = false;

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
		if (isExecutionARetry)
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);
		Thread.sleep(1500);

		page.zAccPref.zNavigateToPreferenceAccount();
		obj.zButton.zClick(localize(locator.addPersona));

		obj.zEditField
				.zType(localize(locator.accountPersonaLabel), personaName);

		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(signatureName);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences should be saved");
		page.zAccPref.zverifySignatureInMail(personaName, signatureName,
				signatureBody);
		obj.zButton.zClick(localize(locator.cancel));
		obj.zButton.zClickInDlg(localize(locator.no));
		needReset = false;

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
		if (isExecutionARetry)
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

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
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences-persona should be saved");
		page.zAccPref.zverifySignatureInMail(personaName, signatureName,
				signatureBody);

		signatureBody = getLocalizedData_NoSpecialChar();

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zeditSignature("", signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

		isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		page.zAccPref.zverifySignatureInMail(personaName, signatureName,
				signatureBody);

		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Accounts Preferences-edited signature should be saved");

		needReset = false;

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
		if (isExecutionARetry)
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

		needReset = false;

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
		if (isExecutionARetry)
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

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

		needReset = false;

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
		if (isExecutionARetry)
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

		needReset = false;

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
		if (isExecutionARetry)
			handleRetry();

		String isTestSettingDlgExist = obj.zDialog
				.zExistsDontWait(localize(locator.accountTest));

		page.zAccPref.zClickOnTestSettingsDlgBox(isTestSettingDlgExist);

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);

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

		needReset = false;

	}

	/**
	 * Test Case:-verifyingComposing(Always Compose in New
	 * Window)AndSignaturePref(AddSignature as Plain Text) Go to Preferences ->
	 * Composing Select: Always Compose in New Window. Compose: As Text. Go to
	 * Preference -> Signatures Add Signature as Plain Text. Go to Inbox Click
	 * on 'New' to compose a mail in New Window. From Options change 'Format As
	 * HTML' and send a mail. Now again Go to Preference Tab -> Signature. Click
	 * on 'Save' Observe that focus should not remains thr. Now click on any of
	 * the tabs "Mail" Verify Message Subject.
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "AccPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyingComposingAndSignaturePref_39282(String signatureName,
			String signatureBody) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zCheckbox.zClick(localize(locator.composeInNewWin));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Composing should be saved");

		page.zAccPref.zNavigateToPreferenceSignature();
		page.zAccPref.zcreateSignature(signatureName, signatureBody, "TEXT");
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Signature should be saved");

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		Thread.sleep(1500);
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zSendBtn_newWindow);
		obj.zButton.zClick(ComposeView.zOptionsDownArrowBtn);
		obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		Thread.sleep(1000);
		obj.zButton.zClick(ComposeView.zSignatureIconBtn);
		obj.zMenuItem.zClick(signatureName);
		String actualSignature = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actualSignature.contains(signatureBody),
				"Signature not included in mail body");
		String subject = "signature";
		page.zComposeView.zSendMailToSelfAndVerify("_selfAccountName_",
				"ccuser@testdomain.com", "bccuser@testdomain.com", subject,
				getLocalizedData(5), "");

		page.zAccPref.zNavigateToPreferenceSignature();
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		zGoToApplication("Mail");
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zExists(subject);

		needReset = false;
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
		if (isExecutionARetry)
			handleRetry();

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zCheckbox.zClick(localize(locator.composeInNewWin));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		Thread.sleep(1000);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Composing should be saved");
		Thread.sleep(2000);
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
				.assertTrue(selenium
						.isElementPresent("xpath=//td[contains(@id, '_test_status') and  contains(text(),"
								+ "'"
								+ localize(locator.dataSourceTestSuccess)
								+ "')" + "]"));
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		zWaitTillObjectExist("element",
				"xpath=//td[contains(@id, '_test_status') and  contains(text(),"
						+ "'" + localize(locator.dataSourceTestSuccess) + "')"
						+ "]");
		Assert
				.assertTrue(selenium
						.isElementPresent("xpath=//td[contains(@id, '_test_status') and  contains(text(),"
								+ "'"
								+ localize(locator.dataSourceTestSuccess)
								+ "')" + "]"));
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1500);

		zGoToApplication("Mail");
		obj.zButton.zClick(page.zMailApp.zNewMenuIconBtn);
		Thread.sleep(1500);
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zSendBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zSendBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zCancelBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zSaveDraftsBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zSpellCheckBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zAddAttachmentBtn_newWindow);
		obj.zButton.zIsEnabled(page.zMailApp.zOptionsBtn_newWindow);
		obj.zButton.zClick(page.zMailApp.zCancelBtn_newWindow);
		selenium.selectWindow(null);

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