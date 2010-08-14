package projects.zcs.ui;

import org.testng.Assert;

/**
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class SignaturePref extends AppPage {
	public static void zNavigateToPreferenceSignature() throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Signatures");
	}

	public static void zCreateSignature(String signatureName,
			String signatureBody, String format) throws Exception {
		format = format.toLowerCase();
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.signatureNameLabel)),
				signatureName);
		obj.zEditor.zType(signatureBody);
		if (format.equals("html")) {
			Thread.sleep(1000);
			String isExists = obj.zButton
					.zExistsDontWait(localize(locator.formatAsText));
			if (isExists.equals("true")) {
				obj.zButton.zClick(localize(locator.formatAsText));
			} else {
				obj.zButton.zClick(localize(locator.formatAsHtml));
			}
			obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		}
	}

	public static void zEditSignature(String signatureName,
			String signatureBody, String format) throws Exception {
		format = format.toLowerCase();
		if (!signatureName.equals("")) {
			obj.zEditField.zType(localize(locator.signatureNameLabel),
					signatureName);
		}
		if (!signatureBody.equals("")) {
			obj.zEditor.zType(signatureBody);
		}
		if (format.equals("html")) {
			Thread.sleep(1000);
			String isExists = obj.zButton
					.zExistsDontWait(localize(locator.formatAsText));
			if (isExists.equals("true")) {
				obj.zButton.zClick(localize(locator.formatAsText));
			} else {
				obj.zButton.zClick(localize(locator.formatAsHtml));
			}
			obj.zMenuItem.zClick(localize(locator.formatAsHtml));
		}
	}

	public static void zUsingSignature(String placeOfSignature)
			throws Exception {
		if (placeOfSignature.equals(localize(locator.aboveQuotedText))) {
			// obj.zCheckbox.zClick(localize(locator.aboveQuotedText));
			selenium
					.check("xpath=//input[contains(@id,'_input') and @type='radio' and @value='outlook']");
		} else {
			selenium
					.mouseOver("xpath=//input[contains(@id,'_input') and @type='radio' and @value='internet']");
			selenium
					.clickAt(
							"xpath=//input[contains(@id,'_input') and @type='radio' and @value='internet']",
							"");
			// obj.zCheckbox.zClick(localize(locator.atBottomOfMessage));
		}
	}

	public static void zVerifySignatureInMail(String personaName,
			String signatureName, String signatureBody) throws Exception {
		page.zComposeView.zNavigateToMailCompose();
		obj.zFeatureMenu.zClick(localize(locator.fromLabel));
		//selenium.windowFocus();
		selenium.mouseOver("xpath=//td[contains(@id,'_title') and contains(text(),'"+personaName+"')]");
		selenium.clickAt("xpath=//td[contains(@id,'_title') and contains(text(),'"+personaName+"')]", "");
		/*Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_DOWN);
		zRobot.keyRelease(KeyEvent.VK_DOWN);
		Thread.sleep(1000);
		zRobot.keyPress(KeyEvent.VK_DOWN);
		zRobot.keyRelease(KeyEvent.VK_DOWN);
		Thread.sleep(1000);
		zRobot.keyPress(KeyEvent.VK_ENTER);
		zRobot.keyRelease(KeyEvent.VK_ENTER);*/
		Thread.sleep(2000);
		String actualSignature = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actualSignature.contains(signatureBody),
				"Signature not included in mail body");
	}

	public static void zVerifySignaturePlaceInText(String placeOfSignature,
			String signatureBody, String mode) throws Exception {
		int indexOfSignature;
		int indexOfOrignalMsg;
		mode = mode.toLowerCase();
		String displayedBody = obj.zEditor.zGetInnerText("");
		indexOfSignature = displayedBody.indexOf(signatureBody);
		if (mode.equals("forward")) {
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
		mode = mode.toLowerCase();
		String displayedBody = obj.zEditor.zGetInnerText("");
		indexOfSignature = displayedBody.indexOf(signatureBody);
		indexOfOrignalMsg = displayedBody.indexOf(localize(locator.fromLabel));
		if (mode.equals("forward")) {
			indexOfOrignalMsg = displayedBody
					.indexOf(localize(locator.fromLabel));
		} else {
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