package projects.html.ui;

import org.testng.Assert;

import framework.core.SelNGBase;
import projects.html.tests.CommonTest;

/**
 * variable for static id's of objects and wrapper functions for signature and
 * account preferences
 * 
 * @author Prashant Jaiswal
 * 
 */

@SuppressWarnings("static-access")
public class AccPref extends CommonTest {

	public static final String zSaveIconBtn = "name=actionSave";
	public static final String zCancelIconBtn = "name=actionCancel";

	// Signature related id's
	public static final String zAddSignatureIconBtn = "id=OPNEW";
	public static final String zNewSignatureName = "id=newSignatureName";
	public static final String zNewSignatureValue = "id=newSignatureValue";

	public static final String zPlaceAboveChkBox = "id=placeAbove";
	public static final String zPlaceBelowChkBox = "id=placeBelow";

	// Account related id's
	public static final String zAccNameEditField = "id=zimbraPrefIdentityName";
	public static final String zFromEditField = "id=mailFrom";
	public static final String zFromMenu = "id=zimbraPrefFromAddress";
	public static final String zReplyToChkBox = "id=REPLYCHECKED";
	public static final String zReplyToDisplayEditField = "id=replyToDisplay";
	public static final String zReplyToAddressEditField = "id=replyToAddress";
	public static final String zSignatureSelectMenu = "id=signatureSelect";

	// ===========================
	// NAVIGATE METHODS
	// ===========================

	/**
	 * To navigate to pref-signature
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToPreferenceSignature() throws Exception {
		obj.zButton.zClick("id=TAB_OPTIONS");
		// obj.zTab.zClick(localize(locator.preferences));
		Thread.sleep(2000);
		obj.zTab.zClick(localize(locator.signatures));
		Thread.sleep(1000);
	}

	/**
	 * To navigate to pref-accounts
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToPreferenceAccounts() throws Exception {
		obj.zButton.zClick("id=TAB_OPTIONS");
		Thread.sleep(2000); // fails in safari
		obj.zTab.zClick(localize(locator.accounts));
		Thread.sleep(2000); // fails in safari
	}

	/**
	 * To navigate to pref-signature and to create signature
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @param place
	 * @throws Exception
	 */
	public static void zNavigateToPrefSignatureAndCreateSignature(
			String signatureName, String signatureBody, String place)
			throws Exception {
		zNavigateToPreferenceSignature();
		zCreateSignature(signatureName, signatureBody, place);
	}

	/**
	 * To navigate to pref-acc tab and verify the signature is displayed in
	 * signature menu
	 * 
	 * @param signatureName
	 * @throws Exception
	 */
	public static void zNavigateToAccPrefAndVerifySignature(String signatureName)
			throws Exception {
		zNavigateToPreferenceAccounts();
		zVerifySignatureInAccTab(signatureName);
	}

	/**
	 * To create a signature
	 * 
	 * @param signatureName
	 * @param signatureBody
	 * @param place
	 *            ="Above/Below"
	 */
	public static void zCreateSignature(String signatureName,
			String signatureBody, String place) throws Exception{
		Thread.sleep(500);
		obj.zButton.zClick(zAddSignatureIconBtn);
		Thread.sleep(500);
		obj.zEditField.zType(zNewSignatureName, signatureName);
		obj.zTextAreaField.zType(zNewSignatureValue, signatureBody);
		Thread.sleep(500);
		if (!place.equals("")) {
			if (place.equals("Above")) {
				obj.zCheckbox.zClick(zPlaceAboveChkBox);
			} else if (place.equals("Below")) {
				obj.zCheckbox.zClick(zPlaceBelowChkBox);
			}
		}
		obj.zButton.zClick(zSaveIconBtn);
		Thread.sleep(1500);
	}

	/**
	 * To verify toaster message displayed
	 * 
	 * @param actualToastMsg
	 * @param expectedToastMsg
	 */
	public static void zVerifyPrefToasterMsgs(String actualToastMsg,
			String expectedToastMsg) {
		Assert.assertTrue(actualToastMsg.contains(expectedToastMsg),
				"Toaster message " + actualToastMsg
						+ " does not contain expected message "
						+ expectedToastMsg);
	}

	/**
	 * To verify the specifiled signature name is displayed in accounts tab
	 * 
	 * @param signatureName
	 * @throws Exception
	 */
	public static void zVerifySignatureInAccTab(String signatureName)
			throws Exception {
		obj.zHtmlMenu.zClick(zSignatureSelectMenu, signatureName);
		Thread.sleep(500);
		obj.zButton.zClick(zSaveIconBtn);// added save button to avoid navigate
		Thread.sleep(500);
		Assert.assertTrue(obj.zHtmlMenu.zGetSelectedItemName(
				zSignatureSelectMenu).equals(signatureName), "The signature "
				+ signatureName + " is not displayed in accounts tab");

	}

	/**
	 * To click on reply on a preselected mail and to verify the place of
	 * signature in mail body
	 * 
	 * @param place
	 *            ="Above/Below"
	 * @param signatureBody
	 */
	public static void zClickReplyToAMailAndVerifySignaturePlace(String place,
			String signatureBody) throws Exception {
		int indexOfSignature;
		int indexOfOrignalMsg;
		obj.zButton.zClick(page.zComposeView.zReplyBtn);
		String displayedBody = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zBodyTextAreaField);

		indexOfSignature = displayedBody.indexOf(signatureBody);
		indexOfOrignalMsg = displayedBody.indexOf(localize(locator.origMsg));
		
		if (place.equals("Above")) {
			Assert.assertTrue(indexOfSignature <= indexOfOrignalMsg,
					"The signature body " + signatureBody
							+ " is not displayed above the Mail body");

		} else if (place.equals("Below")) {
			Assert.assertTrue(indexOfSignature > indexOfOrignalMsg,
					"The signature body " + signatureBody
							+ " is not displayed above the Mail body");
		}
	}

	/**
	 * To change the place of signature
	 * 
	 * @param place
	 *            ="Above/Below"
	 */
	public static void zChangeSignaturePlacment(String place) {
		if (place.equals("Above")) {
			obj.zCheckbox.zClick(zPlaceAboveChkBox);
		} else if (place.equals("Below")) {
			obj.zCheckbox.zClick(zPlaceBelowChkBox);
		}
		obj.zButton.zClick(zSaveIconBtn);
	}

	/**
	 * To select the specified signature name from the account tab signature
	 * drop down
	 * 
	 * @param signatureName
	 */
	public static void zSelectSignatureInAccounts(String signatureName) {
		obj.zHtmlMenu.zClick(zSignatureSelectMenu, signatureName);
	}

	/**
	 * To enter specified acc details in pref-acc tab
	 * 
	 * @param accName
	 * @param fromField
	 * @param setReplyTo
	 * @param replyToDisplay
	 * @param replyToAddress
	 * @param signatureName
	 */
	public static void zEnterAccountDetails(String accName, String fromField,
			String setReplyTo, String replyToDisplay, String replyToAddress,
			String signatureName) {
		if (!accName.equals("")) {
			obj.zEditField.zType(zAccNameEditField, accName);
		}
		if (!fromField.equals("")) {
			obj.zEditField.zType(zFromEditField, fromField);
		}
		if (!fromField.equals("")) {
			obj.zEditField.zType(zFromEditField, fromField);
		}
		if (setReplyTo.equals("setReplyTo")) {
			obj.zCheckbox.zClick(zReplyToChkBox);
			if (!replyToDisplay.equals("")) {
				obj.zEditField.zType(zReplyToDisplayEditField, replyToDisplay);
			}
			if (!replyToAddress.equals("")) {
				obj.zEditField.zType(zReplyToAddressEditField, replyToAddress);
			}

		}
		if (!signatureName.equals("")) {
			zSelectSignatureInAccounts(signatureName);
		}
	}

	/**
	 * To enter acc details and to save the details
	 * 
	 * @param accName
	 * @param fromField
	 * @param setReplyTo
	 * @param replyToDisplay
	 * @param replyToAddress
	 * @param signatureName
	 */
	public static void zMakeAccSettings(String accName, String fromField,
			String setReplyTo, String replyToDisplay, String replyToAddress,
			String signatureName) {
		zEnterAccountDetails(accName, fromField, setReplyTo, replyToDisplay,
				replyToAddress, signatureName);
		obj.zButton.zClick(zSaveIconBtn);
	}

	/**
	 * To verify the signature in the mail body
	 * 
	 * @param signatureBody
	 */
	public static void zVerifySignatureInMailCompose(String signatureBody) {
		String displayedBody = obj.zTextAreaField
				.zGetInnerText(page.zComposeView.zBodyTextAreaField);

		Assert.assertTrue(displayedBody.contains(signatureBody),
				"The signature body " + signatureBody
						+ " is not displayed in mail compose");

	}

	/**
	 * To send mail to self and verify the settings made in preference account
	 * tab
	 * 
	 * @param fromField
	 * @param replyToName
	 * @param replyToAcc
	 * @throws Exception
	 */
	public static void zSendMailToSelfAndVerifyAccSettings(String fromField,
			String replyToName, String replyToAcc) throws Exception {

		page.zComposeView.zSendMailToSelfAndSelectIt(SelNGBase.selfAccountName.get(),
				"", "", getLocalizedData_NoSpecialChar(), "", "");
		String actualMsgHeader = obj.zMessageItem.zGetCurrentMsgHeaderText();
		Assert.assertTrue(actualMsgHeader.contains(fromField),
				"The from field " + fromField
						+ " is not displayed in msg header");
		Assert.assertTrue(actualMsgHeader.contains(replyToName),
				"The reply to name " + replyToName
						+ " is not displayed in msg header");
		Assert.assertTrue(actualMsgHeader.contains(replyToAcc),
				"The reply to account email id " + replyToAcc
						+ " is not displayed in msg header");

	}
}