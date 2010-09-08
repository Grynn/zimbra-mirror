package projects.zcs.ui;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Map;

import org.testng.Assert;

import framework.core.*;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;



/**
 * This Class have UI-level methods related composing a mail and verifying the
 * mail's contents. e.g: zNavigateToMailCompose, zEnterComposeValues etc It also
 * has static-final variables that holds ids of icons on the
 * compose-toolbar(like zSendIconBtn, zSaveDraftsIconBtn etc). If you are
 * dealing with the toolbar buttons, use these icons since in vmware resolutions
 * and in some languages button-labels are not displayed(but just their icons)
 * 
 * @author Raja Rao DV
 * 
 */
@SuppressWarnings("static-access")
public class ComposeView extends AppPage {
	public static final String zSendIconBtn = "css=[id^=zb__COMPOSE][id$=__SEND_left_icon]";
	public static final String zCancelIconBtn = "css=[id^=zb__COMPOSE][id$=__CANCEL_left_icon]";
	public static final String zSaveDraftsIconBtn = "css=[id^=zb__COMPOSE][id$=__SAVE_DRAFT_left_icon]";
	public static final String zAddAttachmentIconBtn = "css=[id^=zb__COMPOSE][id$=__ATTACHMENT_left_icon]";
	public static final String zSpellCheckIconBtn = "css=[id^=zb__COMPOSE][id$=__SPELL_CHECK_left_icon]";
	public static final String zSignatureIconBtn = "css=[id^=zb__COMPOSE][id$=__ADD_SIGNATURE_left_icon]";
	public static final String zOptionsIconBtn = "css=[id^=zb__COMPOSE][id$=__COMPOSE_OPTIONS_left_icon]";
	public static final String zOptionsDownArrowBtn = "css=[id^=zb__COMPOSE][id$=__COMPOSE_OPTIONS_dropdown]";
	public static final String zAttachInineChkbox = "id=inline]";
	public static final String zToField = "css=[id^=zv__COMPOSE][id$=_to_control]";
	public static final String zCcField = "css=[id^=zv__COMPOSE][id$=_cc_control]";
	public static final String zBccField = "css=[id^=zv__COMPOSE][id$=_bcc_control]";
	public static final String zSubjectField = "css=[id^=zv__COMPOSE][id$=_subject_control]";
	public static String zRequestReadReceiptMenuItem = "id=zmi__COMPOSE1_NEW_MESSAGE__REQUEST_READ_RECEIPT_left_icon";

	// ===========================
	// NAVIGATE METHODS
	// ===========================

	/**
	 * Navigates to mailcompose from MailApp
	 */
	public static void zNavigateToMailCompose() throws Exception {
		zGoToApplication("Mail");
		obj.zButton.zClick(MailApp.zNewMenuIconBtn);
		zWaitTillObjectExist("button", zSendIconBtn);
	}

	/**
	 * Navigates to mailcompose using shift-click
	 */
	public static void zNavigateToComposeByShiftClick() throws Exception {
		zGoToApplication("Mail");
		obj.zButton.zShiftClick(MailApp.zNewMenuIconBtn);
		SleepUtil.sleep(2000);
		ClientSessionFactory.session().selenium().selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zSendBtn_newWindow);
	}

	/**
	 * This tries to go back from compose-to mailapp. Tries to cancel all the
	 * dialogs that might showup while doing so.
	 */
	public static void zGoToMailAppFromCompose() {
		if (obj.zButton.zExistsInDlgDontWait(localize("no")).equals("true")) {
			obj.zButton.zClickInDlg(localize("no"));
			// note in some intl, ajxMsg cancel(used in dlg btns) is different
			// from zmMsg(used for toolbars)
			// so directly choose them
		} else if (obj.zButton.zExistsInDlgDontWait(ajxMsg.getString("cancel"))
				.equals("true")) {
			obj.zButton.zClickInDlg(ajxMsg.getString("cancel"));
		} else if (obj.zButton.zExistsInDlgDontWait(localize("ok")).equals(
				"true")) {
			obj.zButton.zClickInDlg(localize("ok"));
		}
		if (obj.zButton.zExistsDontWait(localize("cancel")).equals("true")) {
			obj.zButton.zClick(zmMsg.getString("cancel"));
		}
		ClientSessionFactory.session().selenium().selectWindow(null);
	}

	/**
	 * Logs in using the given username and navigatest to compose
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static String zLoginAndNavigateToCompose(String username)
			throws Exception {
		page.zLoginpage.zLoginToZimbraAjax(username);
		zNavigateToMailCompose();
		return username;
	}

	// dynamically created account
	/**
	 * dynamically creates account, logs in using that accnt and navigates to
	 * compose
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String zLoginAndNavigateToCompose() throws Exception {
		String user1 = Stafzmprov.getRandomAccount();
		return zLoginAndNavigateToCompose(user1);
	}



	/**
	 * Logs into Zimbra-ajax using the random-name and opens compose in
	 * new-window by shift-clicking on New-button
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String zLoginAndOpenMailComposeInNewWindowByShiftClick()
			throws Exception {
		return zLoginAndOpenMailComposeInNewWindowByShiftClick("");

	}

	/**
	 * Logs into Zimbra-ajax using the username and opens compose in new-window
	 * by shift-clicking on New-button
	 * 
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public static String zLoginAndOpenMailComposeInNewWindowByShiftClick(
			String username) throws Exception {
		if (username.equals(""))
			username = Stafzmprov.getRandomAccount();

		page.zLoginpage.zLoginToZimbraAjax(username);
		zNavigateToComposeByShiftClick();
		return username;

	}

	// ===========================
	// ENTER VALUES
	// ===========================
	/**
	 * Fills most of the compose fields.
	 * 
	 * @param to
	 *            to-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param cc
	 *            cc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param bcc
	 *            bcc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param subject
	 *            subject-text
	 * @param body
	 *            body-text
	 * @param attachments
	 *            comma separated attachments name ex: myfile.txt,pdffile.pdf
	 *            (path is automatically constructed)
	 * @throws Exception
	 */
	public static void zEnterComposeValues(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		zEnterComposeValuesWithAttachment(to, cc, bcc, subject, body,
				attachments, false);

	}

	/**
	 * Helper
	 * 
	 * @throws Exception
	 */
	private static void zEnterComposeValuesWithAttachment(String to, String cc,
			String bcc, String subject, String body, String attachments,
			boolean inlineAttachment) throws Exception {
		if (to.equals("_selfAccountName_"))
			to = SelNGBase.selfAccountName.get();
		else if (cc.equals("_selfAccountName_"))
			cc = SelNGBase.selfAccountName.get();
		else if (bcc.equals("_selfAccountName_"))
			bcc = SelNGBase.selfAccountName.get();
		obj.zTextAreaField.zType(zToField, to);
		obj.zTextAreaField.zType(zCcField, cc);
		if (ClientSessionFactory.session().selenium().isElementPresent(
				"link=" + localize(locator.showBCC)))
			ClientSessionFactory.session().selenium().click("link=" + localize(locator.showBCC));
		obj.zTextAreaField.zType(zBccField, bcc);
		obj.zEditField.zType(zSubjectField, subject);
		if (attachments != "")
			zAddAttachments(attachments, inlineAttachment);
		obj.zEditor.zType(body);

	}

	/**
	 * Attaches attachments as inline.
	 * 
	 * @see zAddAttachments for more details
	 * @param to
	 *            to-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param cc
	 *            cc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param bcc
	 *            bcc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param subject
	 * @param body
	 * @param attachments
	 *            comma separated attachments name ex: myfile.txt,pdffile.pdf
	 *            (path is automatically constructed)
	 * @throws Exception
	 * @throws Exception
	 */
	public static void zEnterComposeValuesInlineAttachment(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		zEnterComposeValuesWithAttachment(to, cc, bcc, subject, body,
				attachments, true);

	}

	/**
	 * Enters fully qualified attachment-names into either directly in
	 * attachment's field(IE6,IE7 & IE8) or, in uploadFile dialog in browsers
	 * like(FF2, FF3, SF3). This physically moves the mouse to enter values, so
	 * make sure the computer is not in locked-state. If you have accessed the
	 * test-machine using remote-desktop, that puts it locked state as well. Fix
	 * it by logging into it using either vnc then unlock it OR restart the
	 * computer.
	 * 
	 * @param attachments
	 *            attachments names separated by comma(myfile1.xls,text.txt)
	 * @param inlineAttachment
	 *            If true, it tries to attache the files as inline
	 * @throws InterruptedException
	 */
	public static void zAddAttachments(String attachments,
			boolean inlineAttachment) throws Exception {
		obj.zButton.zClick(ComposeView.zAddAttachmentIconBtn);
		String[] attList = attachments.split(",");
		for (int i = 0; i < attList.length; i++) {
			File f = new File("src/java/projects/zcs/data/" + attList[i]);
			String path = f.getAbsolutePath();
			obj.zBrowseField.zTypeInDlgWithKeyboard(localize(locator.attach)
					+ ":", path, "" + (i + 1) + "");
		}
		if (inlineAttachment) {
			obj.zCheckbox.zClickInDlg("name=inlineimages");
		}
		obj.zButton.zClickInDlg(localize(locator.attach));
		SleepUtil.sleep(1500); // test fails in safari
		String dlgExists = obj.zDialog
				.zExistsDontWait(localize(locator.attachFile));
		for (int i = 0; i <= 20; i++) {
			SleepUtil.sleep(1000);
			if (dlgExists.equals("true")) {
			} else {
				break;
			}
		}
	}

	// ===========================
	// VERIFY METHODS
	// ===========================
	/**
	 * Verifies if attachment links exists
	 * 
	 * @param attachmentList
	 *            comma separated attachment names(myfile.txt,foo.xml)
	 */
	public static void zVerifyAttachmentsExists(String attachmentList) {
		String[] attList = attachmentList.split(",");
		for (int i = 0; i < attList.length; i++) {
			boolean b = ClientSessionFactory.session().selenium().isElementPresent(
					"link=" + attList[0]);
			Assert.assertTrue(b, "Attachment link for: (" + attList[0]
					+ ") doesnt exist");
		}
	}

	/**
	 * Verifies if all the attachment's checkboxes are checked
	 * 
	 * @param attachmentList
	 *            comma separated attachment names
	 */
	public static void zVerifyAttachmentsSelected(String attachmentList) {
		String[] attList = attachmentList.split(",");
		for (int i = 0; i < attList.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attList[0]);
		}
	}

	/**
	 * Verifies if the message's header section contains all proper values. Call
	 * this after you open an email/calendar(read-only).
	 * 
	 * @param to
	 *            to-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param cc
	 *            cc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param bcc
	 *            bcc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param subject
	 * @param body
	 * @param attachments
	 *            comma separated attachments name ex: myfile.txt,pdffile.pdf
	 *            (path is automatically constructed)
	 * @throws Exception
	 */
	public static void zVerifyMsgHeaders(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		SleepUtil.sleep(1500);
		if (to.equals("_selfAccountName_"))
			to = SelNGBase.selfAccountName.get();
		else if (cc.equals("_selfAccountName_"))
			cc = SelNGBase.selfAccountName.get();
		else if (bcc.equals("_selfAccountName_"))
			bcc = SelNGBase.selfAccountName.get();
		String headerTxt = obj.zMessageItem.zGetCurrentMsgHeaderText();
		Assert.assertTrue(
				headerTxt.indexOf(MailApp.zGetNameFromEmail(to)) >= 0,
				"To field mismatched" + formatExpActValues(headerTxt, to));
		Assert.assertTrue(
				headerTxt.indexOf(MailApp.zGetNameFromEmail(cc)) >= 0,
				"Cc field mismatched" + formatExpActValues(headerTxt, cc));
		Assert.assertTrue(headerTxt.indexOf(subject) >= 0,
				"Subject field mismatched"
						+ formatExpActValues(headerTxt, subject));
		if (attachments != "") {
			String[] attList = attachments.split(",");
			for (int i = 0; i < attList.length; i++) {
				String exp = attList[i].toLowerCase();
				Assert.assertTrue(headerTxt.toLowerCase().indexOf(exp) >= 0,
						"Attachment(" + exp + ") Not displayed in the mail"
								+ formatExpActValues(headerTxt, exp));
			}
			if (attList.length > 1) {
				Assert.assertTrue(headerTxt
						.indexOf(localize(locator.downloadAll)) >= 0,
						"'Download All Attachments' displayed in the mail header"
								+ formatExpActValues(headerTxt,
										localize(locator.downloadAll)));
			}
		}

	}

	/**
	 * Takes an expected and actual value and then tries to format it.
	 * 
	 * @param expected
	 * @param actual
	 * @return
	 */
	public static String formatExpActValues(String expected, String actual) {
		return "<br>------------------------<br>" + "EXPECTED:"
				+ "<br>------------------------<br>" + expected
				+ "<br>------------------------<br>" + "ACTUAL:"
				+ "<br>------------------------<br>" + actual;
	}

	/**
	 * Verifies if compose-view is filled with all proper values Ex: Call this
	 * to verify if values are filled properly after we hit Fwd, reply, reply
	 * all etc)
	 * 
	 * @param to
	 *            to-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param cc
	 *            cc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param bcc
	 *            bcc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param subject
	 * @param body
	 * @param attachments
	 *            comma separated attachments name ex: myfile.txt,pdffile.pdf
	 *            (path is automatically constructed)
	 * @throws Exception
	 */
	public static void zVerifyComposeFilledValues(String action, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (to.equals("_selfAccountName_"))
			to = SelNGBase.selfAccountName.get();
		else if (cc.equals("_selfAccountName_"))
			cc = SelNGBase.selfAccountName.get();
		else if (bcc.equals("_selfAccountName_"))
			bcc = SelNGBase.selfAccountName.get();
		String actualToVal = obj.zTextAreaField.zGetInnerText(zToField);
		String actualccVal = obj.zTextAreaField.zGetInnerText(zCcField);
		String actualbccVal = obj.zTextAreaField.zGetInnerText(zBccField);
		String actualSubjectVal = obj.zEditField.zGetInnerText(zSubjectField);
		String bodyVal = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actualToVal.indexOf(to) >= 0, "On " + action
				+ ", To-field isnt getting filled."
				+ formatExpActValues(to, actualToVal));
		Assert.assertTrue(actualccVal.indexOf(cc) >= 0, "On " + action
				+ ", CC-field isnt getting filled"
				+ formatExpActValues(cc, actualccVal));
		Assert.assertTrue(actualbccVal.indexOf(bcc) >= 0, "On " + action
				+ ", Bcc-field isnt getting filled"
				+ formatExpActValues(bcc, actualbccVal));
		Assert.assertTrue(actualSubjectVal.indexOf(subject) >= 0, "On "
				+ action + ", Subject-field isnt getting filled"
				+ formatExpActValues(subject, actualSubjectVal));
		Assert.assertTrue(bodyVal.indexOf(body) >= 0, "On " + action
				+ ", Body-field isnt getting filled"
				+ formatExpActValues(body, bodyVal));
		obj.zButton.zClick(zmMsg.getString("cancel"));
		SleepUtil.sleep(3000);
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_N);
		zRobot.keyRelease(KeyEvent.VK_N);
	}

	/**
	 * Verifies if an alert/warning dialog is displayed when user tries to mail
	 * with invalid information
	 * 
	 * @param to
	 *            to-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param cc
	 *            cc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param bcc
	 *            bcc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param subject
	 * @param body
	 * @param attachments
	 *            comma separated attachments name ex: myfile.txt,pdffile.pdf
	 *            (path is automatically constructed)
	 * @param errDlgName
	 * @param errMsg
	 * @throws Exception
	 */
	public static void zVerifySendThrowsError(String to, String cc, String bcc,
			String subject, String body, String attachments, String errDlgName,
			String errMsg) throws Exception {
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		obj.zDialog.zVerifyAlertMessage(errDlgName, errMsg);

	}

	// =======================================
	// ACT AND VERIFY (MISC.)METHODS
	// =======================================
	/**
	 * Sends mail to the current user, waits for the mail to arrive by clicking
	 * GetMail and selects it
	 * 
	 * @param to
	 *            to-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param cc
	 *            cc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param bcc
	 *            bcc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param subject
	 * @param body
	 * @param attachments
	 *            comma separated attachments name ex: myfile.txt,pdffile.pdf
	 *            (path is automatically constructed)
	 * @throws Exception
	 */
	public static void zSendMailToSelfAndSelectIt(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		SleepUtil.sleep(1000);
		ClientSessionFactory.session().selenium().selectWindow(null);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
	}

	/**
	 * Sends mail to the current user, waits for the mail to arrive by clicking
	 * GetMail. Finally verifies the mail's header information and body
	 * 
	 * @param to
	 *            to-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param cc
	 *            cc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param bcc
	 *            bcc-email or "_selfAccountName_"(will be replaced by current
	 *            user's email)
	 * @param subject
	 * @param body
	 * @param attachments
	 *            comma separated attachments name ex: myfile.txt,pdffile.pdf
	 *            (path is automatically constructed)
	 * @throws Exception
	 */
	public static void zSendMailToSelfAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		page.zComposeView.zSendMailToSelfAndSelectIt(to, cc, bcc, subject,
				body, attachments);
		page.zComposeView.zVerifyMsgHeaders(to, cc, bcc, subject, body,
				attachments);
		obj.zMessageItem.zVerifyCurrentMsgBodyText(body);
	}

	public static void zComposeAndSendMail(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		obj.zButton.zClick(MailApp.zNewMenuIconBtn);
		zWaitTillObjectExist("button", zSendIconBtn);
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		zWaitTillObjectExist("button", page.zMailApp.zNewMenuIconBtn);
	}
}