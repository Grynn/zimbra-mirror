package projects.html.ui;

import java.io.File;

import java.util.Map;
import org.testng.Assert;

import framework.core.SelNGBase;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

import projects.html.clients.ProvZCS;
import projects.html.tests.CommonTest;

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
public class ComposeView extends CommonTest {
	// Compose toolbar buttons
	public static final String zSendBtn = "id=IOPSEND";
	public static final String zSendBtnBtmToolBar = "id=SDOPSEND";
	public static final String zCancelBtn = "id=SOPCANCEL";
	public static final String zCancelBtnBtmToolBar = "id=SDOPCANCEL";
	public static final String zSaveDraftsBtn = "id=SOPDRAFT";
	public static final String zSaveDraftsBtnBtmToolBar = "id=SDOPDRAFT";
	public static final String zAddAttachmentBtn = "id=SOPATTACH";
	public static final String zAddAttachmentBtnBtmToolBar = "id=SDOPATTACH";
	public static final String zAddReceipientsBtn = "id=SOPADDRECIP";
	public static final String zAddReceipientsBtnBtmToolBar = "id=SDOPADDRECIP";
	public static final String zAttachInineChkbox = "id=inline";

	// Mail compose objects
	public static final String zToField = "id=toField";
	public static final String zCcField = "id=ccField";
	public static final String zBccField = "id=bccField";
	public static final String zSubjectField = "id=subjectField";
	public static final String zBodyTextAreaField = "id=body"; /*
																 * will work
																 * only for
																 * plain text
																 */
	public static final String zPriorityListBox = "name=priority";
	public static final String zToBtn = "id=Stobutton";
	public static final String zCcBtn = "id=Sccbutton";
	public static final String zBccBtn = "id=Sbccbutton";

	// Add Attachments page objects
	public static final String zAddAttachDoneBtn = "name=actionAttachDone";
	public static final String zAddAttachCancelBtn = "name=actionAttachCancel";
	
	// Add Receipients page objects
	public static final String zAddReceipFindEditField = "id=findField";
	public static final String zAddReceipContactsSrchWebList = "name=contactLocation";
	public static final String zAddReceipSearchContactsBtn = "id=doSearch";
	public static final String zAddReceipDoneBtn = "name=actionContactDone";
	public static final String zAddReceipAddSelectedBtn = "name=actionContactAdd";
	public static final String zAddReceipCancelBtn = "name=actionContactCancel";
	public static final String zAddReceipToChkBox = "name=addTo";
	public static final String zAddReceipCcChkBox = "name=addCc";
	public static final String zAddReceipBccChkBox = "name=addBcc";

	// Reply/Reply All/Forward objects
	public static final String zReplyBtn = "id=OPREPLY";
	public static final String zReplyAllBtn = "id=OPREPLYALL";
	public static final String zForwardBtn = "id=OPFORW";

	// ===========================
	// NAVIGATE METHODS
	// ===========================

	/**
	 * Navigates to mailcompose from MailApp
	 */
	public static void zNavigateToMailCompose() throws Exception {
		obj.zTab.zClick(localize(locator.compose));
		SleepUtil.sleep(2000); // timing issues
	}

	/**
	 * Navigates to mailcompose using top toolbar
	 */
	public static void zNavigateToMailComposeBtmToolBar() throws Exception {
		obj.zButton.zClick(localize(locator.compose), "1");
		SleepUtil.sleep(2000); // timing issues
	}

	/**
	 * Navigates to mailcompose using bottom toolbar
	 */
	public static void zNavigateToMailComposeTopToolBar() throws Exception {
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("sv")) {
			obj.zButton.zClick(localize(locator.compose), "3");
		} else {
			obj.zButton.zClick(localize(locator.compose), "2");
		}
		SleepUtil.sleep(2000); // timing issues
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
			obj.zButton.zClick(zhMsg.getString("cancel"));
		}
		SelNGBase.selenium.get().selectWindow(null);
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
		page.zLoginpage.zLoginToZimbraHTML(username);
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
		String user1 = ProvZCS.getRandomAccount();
		return zLoginAndNavigateToCompose(user1);

	}

	/**
	 * dynamically creates account. You can also pass preferences(like: html
	 * editor, readingpane ON etc)
	 * 
	 * @param accntAttrs
	 * @return
	 * @throws Exception
	 */
	public static String zLoginAndNavigateToCompose(
			Map<String, Object> accntAttrs) throws Exception {
		String user1 = ProvZCS.getRandomAccount(accntAttrs);
		return zLoginAndNavigateToCompose(user1);

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
	 */
	private static void zEnterComposeValuesWithAttachment(String to, String cc,
			String bcc, String subject, String body, String attachments,
			boolean inlineAttachment) throws Exception {
		if (to.equals("_selfAccountName_"))
			to = SelNGBase.selfAccountName.get();
		if (cc.equals("_selfAccountName_"))
			cc = SelNGBase.selfAccountName.get();
		if (bcc.equals("_selfAccountName_"))
			bcc = SelNGBase.selfAccountName.get();
		obj.zTextAreaField.zType(zToField, to);
		obj.zTextAreaField.zType(zCcField, cc);
		// temporarily comment this out -- due to bug 32611
		if (!bcc.equals("")) {
			SleepUtil.sleep(2000);
			if (SelNGBase.selenium.get().isElementPresent("link=" + localize(locator.showBcc)))
				SelNGBase.selenium.get().click("link=" + localize(locator.showBcc));
			obj.zTextAreaField.zType(zBccField, bcc);
		}
		obj.zEditField.zType(zSubjectField, subject);
		if (attachments != "")
			zAddAttachments(attachments, inlineAttachment);
		SleepUtil.sleep(3000);// allow loading html editor(if it is one) todo:
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
	 * @throws Exception
	 */
	public static void zAddAttachments(String attachments,
			boolean inlineAttachment) throws Exception {
		SleepUtil.sleep(1000); // selenium failure here
		obj.zButton.zClick(ComposeView.zAddAttachmentBtn);
		SleepUtil.sleep(2000); // selenium failure here
		zWaitTillObjectExist("button", zAddAttachDoneBtn);
		String[] attList = attachments.split(",");
		for (int i = 0; i < attList.length; i++) {
			File f = new File("src/java/projects/html/data/" + attList[i]);
			String path = f.getAbsolutePath();
			//obj.zBrowseField.zTypeInDlgWithKeyboard((i + 1) + ".", path, "");
			//obj.zBrowseField.zTypeInDlgWithKeyboard("css=table.Compose tr:nth-child(" + (i + 2) + "):contains(" + (i + 1) + ".)", path, "");
			obj.zBrowseField.zTypeInDlgWithKeyboard("css=table.Compose td:contains(" + (i+1) + ".)+td:contains[colspan=2]>input[name=fileUpload]", path, "");	
		}
		
		obj.zButton.zClick(zAddAttachDoneBtn);
		SleepUtil.sleep(3000); // wait till main compose UI come
		if (!attachments.equals("mail700.pst")) { /*
												 * customization for big attach
												 */
			for (int i = 0; i < attList.length; i++) {
				zWaitTillObjectExist("text", attList[i]);// timing issue				
			}
			obj.zCheckbox.zVerifyIsChecked("css=[name=originalAttachment]");
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
			boolean b = SelNGBase.selenium.get().isElementPresent("link=" + attList[0]);
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
		if (cc.equals("_selfAccountName_"))
			cc = SelNGBase.selfAccountName.get();
		if (bcc.equals("_selfAccountName_"))
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
			String htmlBody = obj.zMessageItem.zGetMsgBodyHTML();
			String[] attList = attachments.split(",");
			for (int i = 0; i < attList.length; i++) {
				Assert.assertTrue(htmlBody.indexOf(attList[i]) >= 0,
						"Subject field mismatched"
								+ formatExpActValues(htmlBody, attList[i]));
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
		to = to.toLowerCase();
		if (cc.equals("_selfAccountName_"))
			cc = SelNGBase.selfAccountName.get();
		cc = cc.toLowerCase();
		if (bcc.equals("_selfAccountName_"))
			bcc = SelNGBase.selfAccountName.get();
		bcc = bcc.toLowerCase();
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
				+ ", Subject-field isnt getting filled"
				+ formatExpActValues(body, bodyVal));
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
		obj.zButton.zClick(localize(locator.send));
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
		obj.zButton.zClick(localize(locator.send));
		SleepUtil.sleep(2000);
		SelNGBase.selenium.get().selectWindow(null);
		try {
			obj.zButton.zNotExists(localize(locator.send));
		} catch (Exception e) {
			e.printStackTrace();
		}
		SelNGBase.selenium.get().selectWindow(null);
		MailApp.zClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000); // takes some time to open message - test fails here
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

	public static void zSendMail(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(page.zComposeView.zSendBtn);
	}

	public static void zVerifyToCcBccFields(String fieldToVerify,
			String action, String toCommaSeparated, String ccCommaSeparated,
			String bccCommaSeparated) throws Exception {
		if (fieldToVerify.toLowerCase().equals("to")
				|| fieldToVerify.toLowerCase().equals("toccbcc")) {
			if (toCommaSeparated.equals("_selfAccountName_"))
				toCommaSeparated = SelNGBase.selfAccountName.get();

			toCommaSeparated = toCommaSeparated.trim();
			if (!toCommaSeparated.equals("")) {
				String[] toCommaSeparatedArray = toCommaSeparated.split(",");
				for (int i = 0; i < toCommaSeparatedArray.length; i++) {
					String actualToVal = obj.zTextAreaField
							.zGetInnerText(page.zComposeView.zToField);
					Assert.assertTrue(
							actualToVal.indexOf(toCommaSeparatedArray[i]
									.toLowerCase()) >= 0, "On "
									+ action
									+ ", To-field isnt getting filled."
									+ page.zComposeView
											.formatExpActValues(
													toCommaSeparatedArray[i]
															.toLowerCase(),
													actualToVal));
				}
			}

		}

		if (fieldToVerify.toLowerCase().equals("cc")
				|| fieldToVerify.toLowerCase().equals("toccbcc")) {
			if (ccCommaSeparated.equals("_selfAccountName_"))
				ccCommaSeparated = SelNGBase.selfAccountName.get();

			ccCommaSeparated = ccCommaSeparated.trim();
			if (!ccCommaSeparated.equals("")) {
				String[] ccCommaSeparatedArray = ccCommaSeparated.split(",");
				for (int i = 0; i < ccCommaSeparatedArray.length; i++) {
					String actualCcVal = obj.zTextAreaField
							.zGetInnerText(page.zComposeView.zCcField);
					Assert.assertTrue(
							actualCcVal.indexOf(ccCommaSeparatedArray[i]
									.toLowerCase()) >= 0, "On "
									+ action
									+ ", Cc-field isnt getting filled."
									+ page.zComposeView
											.formatExpActValues(
													ccCommaSeparatedArray[i]
															.toLowerCase(),
													actualCcVal));
				}
			}

		}

		if (fieldToVerify.toLowerCase().equals("bcc")
				|| fieldToVerify.toLowerCase().equals("toccbcc")) {
			if (bccCommaSeparated.equals("_selfAccountName_"))
				bccCommaSeparated = SelNGBase.selfAccountName.get();

			bccCommaSeparated = bccCommaSeparated.trim();
			if (!bccCommaSeparated.equals("")) {
				String[] bccCommaSeparatedArray = bccCommaSeparated.split(",");
				for (int i = 0; i < bccCommaSeparatedArray.length; i++) {
					if (SelNGBase.selenium.get().isElementPresent("link="
							+ localize(locator.showBcc)))
						SelNGBase.selenium.get().click("link=" + localize(locator.showBcc));
					String actualBccVal = obj.zTextAreaField
							.zGetInnerText(page.zComposeView.zBccField);
					Assert.assertTrue(
							actualBccVal.indexOf(bccCommaSeparatedArray[i]
									.toLowerCase()) >= 0, "On "
									+ action
									+ ", Bcc-field isnt getting filled."
									+ page.zComposeView.formatExpActValues(
											bccCommaSeparatedArray[i]
													.toLowerCase(),
											actualBccVal));
				}
			}

		}
	}

	public static void zAddReceipientsThroughToCcBccBtns(String toorCcOrBccbtn,
			String toRecepientsCommaSeparated,
			String ccRecepientsCommaSeparated,
			String bccRecepientsCommaSeparated, String findIn_DefaultGAL)
			throws Exception {
		if (toorCcOrBccbtn.toLowerCase().equals("to")
				|| toorCcOrBccbtn.equals(page.zComposeView.zToBtn)) {
			obj.zButton.zClick(page.zComposeView.zToBtn);
		} else if (toorCcOrBccbtn.toLowerCase().equals("cc")
				|| toorCcOrBccbtn.equals(page.zComposeView.zCcBtn)) {
			obj.zButton.zClick(page.zComposeView.zCcBtn);
		} else if (toorCcOrBccbtn.toLowerCase().equals("bcc")
				|| toorCcOrBccbtn.equals(page.zComposeView.zBccBtn)) {
			if (SelNGBase.selenium.get().isElementPresent("link=" + localize(locator.showBcc)))
				SelNGBase.selenium.get().click("link=" + localize(locator.showBcc));
			obj.zButton.zClick(page.zComposeView.zBccBtn);
		}

		if (findIn_DefaultGAL.equals("")) {
			findIn_DefaultGAL = localize(locator.GAL);
		}

		toRecepientsCommaSeparated = toRecepientsCommaSeparated.trim();
		if (!toRecepientsCommaSeparated.equals("")) {
			String[] toRecepientsCommaSeparatedArray = toRecepientsCommaSeparated
					.split(",");
			for (int i = 0; i < toRecepientsCommaSeparatedArray.length; i++) {
				obj.zEditField.zType(page.zComposeView.zAddReceipFindEditField,
						toRecepientsCommaSeparatedArray[i]);
				SelNGBase.selenium.get().select(
						page.zComposeView.zAddReceipContactsSrchWebList,
						findIn_DefaultGAL);
				obj.zButton
						.zClick(page.zComposeView.zAddReceipSearchContactsBtn);
				SleepUtil.sleep(1500);
				obj.zCheckbox.zClick(page.zComposeView.zAddReceipToChkBox);
				obj.zButton.zClick(page.zComposeView.zAddReceipAddSelectedBtn);
				SleepUtil.sleep(1000);
			}
		}

		ccRecepientsCommaSeparated = ccRecepientsCommaSeparated.trim();
		if (!ccRecepientsCommaSeparated.equals("")) {
			String[] ccRecepientsCommaSeparatedArray = ccRecepientsCommaSeparated
					.split(",");
			for (int i = 0; i < ccRecepientsCommaSeparatedArray.length; i++) {
				obj.zEditField.zType(page.zComposeView.zAddReceipFindEditField,
						ccRecepientsCommaSeparatedArray[i]);
				SelNGBase.selenium.get().select(
						page.zComposeView.zAddReceipContactsSrchWebList,
						findIn_DefaultGAL);
				obj.zButton
						.zClick(page.zComposeView.zAddReceipSearchContactsBtn);
				SleepUtil.sleep(1500);
				obj.zCheckbox.zClick(page.zComposeView.zAddReceipCcChkBox);
				obj.zButton.zClick(page.zComposeView.zAddReceipAddSelectedBtn);
				SleepUtil.sleep(1000);
			}
		}

		bccRecepientsCommaSeparated = bccRecepientsCommaSeparated.trim();
		if (!bccRecepientsCommaSeparated.equals("")) {
			String[] bccRecepientsCommaSeparatedArray = bccRecepientsCommaSeparated
					.split(",");
			for (int i = 0; i < bccRecepientsCommaSeparatedArray.length; i++) {
				obj.zEditField.zType(page.zComposeView.zAddReceipFindEditField,
						bccRecepientsCommaSeparatedArray[i]);
				SelNGBase.selenium.get().select(
						page.zComposeView.zAddReceipContactsSrchWebList,
						findIn_DefaultGAL);
				obj.zButton
						.zClick(page.zComposeView.zAddReceipSearchContactsBtn);
				SleepUtil.sleep(1500);
				obj.zCheckbox.zClick(page.zComposeView.zAddReceipBccChkBox);
				obj.zButton.zClick(page.zComposeView.zAddReceipAddSelectedBtn);
				SleepUtil.sleep(1000);
			}
		}

		obj.zButton.zClick(page.zComposeView.zAddReceipDoneBtn);
		SleepUtil.sleep(1000);
	}
}