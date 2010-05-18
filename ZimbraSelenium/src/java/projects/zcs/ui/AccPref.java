package projects.zcs.ui;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import org.testng.Assert;
import projects.zcs.tests.CommonTest;

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
public class AccPref extends CommonTest {
	public static final String zPrefAccTabIcon = "id=ztab__PREF__"
			+ localize(locator.accounts) + "_title";
	public static final String zPrefSignatureTabIcon = "id=ztab__PREF__"
			+ localize(locator.signatures) + "_title";

	public static void zNavigateToPreferenceAccount() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");

	}

	public static void zNavigateToPreferenceSignature() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Signatures");
	}

	public static void zcreateSignature(String signatureName,
			String signatureBody, String type) throws Exception {
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.signatureNameLabel)),
				signatureName);
		obj.zEditor.zType(signatureBody);
		if (type.equals("HTML")) {
			obj.zButton.zClick(localize(locator.formatAsText));
			obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		}
	}

	public static void zeditSignature(String signatureName,
			String signatureBody, String type) {
		if (!signatureName.equals("")) {
			obj.zEditField.zType(localize(locator.signatureNameLabel),
					signatureName);
		}
		if (!signatureBody.equals("")) {
			obj.zEditor.zType(signatureBody);
		}
		if (type.equals("HTML")) {
			obj.zButtonMenu.zClick(locator.formatAsText);
			obj.zMenuItem.zClick(localize(locator.formatAsText));
		}
	}

	public static void usingSignature(String placeOfSignature) {
		if (placeOfSignature.equals(localize(locator.aboveQuotedText))) {
			//obj.zCheckbox.zClick(localize(locator.aboveQuotedText));
			selenium.check("xpath=//input[contains(@id,'_input') and @type='radio' and @value='outlook']");
		} else {
			selenium.mouseOver("xpath=//input[contains(@id,'_input') and @type='radio' and @value='internet']");
			selenium.clickAt("xpath=//input[contains(@id,'_input') and @type='radio' and @value='internet']","");
			//obj.zCheckbox.zClick(localize(locator.atBottomOfMessage));
		}
	}

	public static void zverifySignatureInMail(String personaName,
			String signatureName, String signatureBody) throws Exception {
		page.zComposeView.zNavigateToMailCompose();
		obj.zFeatureMenu.zClick(localize(locator.fromLabel));
		selenium.windowFocus();
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_DOWN);
		zRobot.keyRelease(KeyEvent.VK_DOWN);
		Thread.sleep(1000);
		zRobot.keyPress(KeyEvent.VK_DOWN);
		zRobot.keyRelease(KeyEvent.VK_DOWN);
		Thread.sleep(1000);
		zRobot.keyPress(KeyEvent.VK_ENTER);
		zRobot.keyRelease(KeyEvent.VK_ENTER);
		Thread.sleep(1000);
		String actualSignature = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actualSignature.contains(signatureBody),
				"Signature not included in mail body");

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
			selenium.type("xpath=//input[@type='password']", password);
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
	public static void zVerifySignaturePlaceInText(String placeOfSignature,
			String signatureBody,String mode) throws Exception {
		int indexOfSignature;
		int indexOfOrignalMsg;
		String displayedBody = obj.zEditor.zGetInnerText("");
		indexOfSignature = displayedBody.indexOf(signatureBody);

		if (mode.equals("Forward")) {
			indexOfOrignalMsg = displayedBody
					.indexOf(localize(locator.forwardedMessage));
		} else {
			indexOfOrignalMsg = displayedBody
					.indexOf(localize(locator.origMsg));
		}

		if (placeOfSignature.equals(localize(locator.aboveQuotedText))) {
			Assert.assertTrue(indexOfSignature <= indexOfOrignalMsg,
					"The signature body " + signatureBody
							+ " is not displayed above the Mail body");

		} else if (placeOfSignature.equals(localize(locator.atBottomOfMessage))) {
			Assert.assertTrue(indexOfSignature > indexOfOrignalMsg,
					"The signature body " + signatureBody
							+ " is not displayed above the Mail body");
		}
		
		
	}
	
	public static void zVerifySignaturePlaceInHTML(String placeOfSignature,
			String signatureBody, String mode) throws Exception {
		int indexOfSignature;
		int indexOfOrignalMsg;
		String displayedBody = obj.zEditor.zGetInnerText("");
		indexOfSignature = displayedBody.indexOf(signatureBody);
		indexOfOrignalMsg = displayedBody.indexOf(localize(locator.fromLabel));

		if (mode.equals("Forward")) {
			indexOfOrignalMsg = displayedBody
					.indexOf(localize(locator.fromLabel));
		} else {
			/*
			 * Note:Right now both the condition are same but in future if any
			 * change happened in either fwd'ed or Reply message then will
			 * change accordingly
			 */
			indexOfOrignalMsg = displayedBody
					.indexOf(localize(locator.fromLabel));
		}

		if (placeOfSignature.equals(localize(locator.aboveQuotedText))) {
			Assert.assertTrue(indexOfSignature <= indexOfOrignalMsg,
					"The signature body " + signatureBody
							+ " is not displayed above the Mail body");

		} else if (placeOfSignature.equals(localize(locator.atBottomOfMessage))) {
			Assert.assertTrue(indexOfSignature > indexOfOrignalMsg,
					"The signature body " + signatureBody
							+ " is not displayed above the Mail body");
		}

	}
	
	
}