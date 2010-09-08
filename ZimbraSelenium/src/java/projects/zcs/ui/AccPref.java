package projects.zcs.ui;

import framework.core.*;
import framework.util.SleepUtil;

/**
 * This Class have UI-level methods related composing a contact and verifying
 * the mail's contents. e.g: zNavigateToContact,
 * zCreateNewAddBook,zCreateBasicContact etc It also has static-final variables
 * that holds ids of icons on the compose-toolbar(like zNewAddressBookIconBtn,
 * zNewContactMenuIconBtn etc). If you are dealing with the toolbar buttons, use
 * these icons since in vmware resolutions and in some languages button-labels
 * are not displayed(but just their icons)
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class AccPref extends AppPage {
	public static final String zPrefAccTabIcon = "id=ztab__PREF__"
			+ localize(locator.accounts) + "_title";
	public static final String zPrefSignatureTabIcon = "id=ztab__PREF__"
			+ localize(locator.signatures) + "_title";

	public static void zNavigateToPreferenceAccount() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");
	}

	public static void zEnterExternalAccData(String emailAddress,
			String accountName, String accountType, String password,
			String ssl, String downloadMsgTo) {
		obj.zEditField.zActivate(localize(locator.emailAddrLabel), "2");
		obj.zEditField.zType(localize(locator.emailAddrLabel), emailAddress,
				"2");
		obj.zEditField.zType(localize(locator.accountNameLabel), accountName,
				"2");
		obj.zEditField.zType("id=*EXTERNAL_USERNAME", emailAddress);
		if (accountType.equals("IMAP")) {
			obj.zRadioBtn.zClick(localize(locator.accountTypeImap));
		}
		if (!password.equals("")) {
			ClientSessionFactory.session().selenium().type("xpath=//input[@type='password']",
					password);
		}
		if (ssl.equals("SSL")) {
			obj.zCheckbox.zClick(localize(locator.accountUseSSL));
		}
		if (downloadMsgTo.equals("Inbox")) {
			obj.zRadioBtn.zClick(localize(locator.inbox));
		}
	}

	public static void zClickOnTestSettingsDlgBox(String dlgStatus) {
		if (dlgStatus.equals("true")) {
			obj.zButton.zClickInDlg(localize(locator.cancel));

		}
	}

	public static void zCreatePersona(String personaName, String fromName,
			String fromAddress) throws Exception {
		obj.zButton.zClick(localize(locator.addPersona));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.accountPersonaLabel)),
				personaName);
		obj.zEditField.zActivateAndType("id=DWT41_PERSONA_FROM_NAME", fromName);
		ClientSessionFactory.session().selenium().clickAt(
						"//div[contains(@id, 'PERSONA')]//td[contains(@class, 'ZOptionsSectionMain')]/table/tbody/tr[6]//td[contains(@id, '_select_container')]//td[contains(text(), '"
								+ SelNGBase.selfAccountName.get().toLowerCase()
								+ "')]", "");
		SleepUtil.sleep(500);
		if (fromAddress.equals("")) {
			ClientSessionFactory.session().selenium().clickAt(
							"//div[contains(@class, 'ZSelectMenuItem ZWidget ZHasText')]//td[contains(text(), '"
									+ SelNGBase.selfAccountName.get()
											.toLowerCase() + "')]", "");
		} else {
			ClientSessionFactory.session().selenium().clickAt(
							"//div[contains(@class, 'ZSelectMenuItem ZWidget ZHasText')]//td[contains(text(), '"
									+ fromAddress.toLowerCase() + "')]", "");
		}
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
	}
}