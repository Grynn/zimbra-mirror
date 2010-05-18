package projects.zcs.tests.others;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * Method injects various type of mails from particular directory and verifies
 * UI accordingly
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings( { "static-access" })
public class lmtpInjectTests extends CommonTest {
	static Boolean foundFlag = false;
	static File dir = new File("projects/zcs/data/lmtpInject");

	// Before Class
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	// Before method
	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ViewEntireMessage_Bug39246() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		subject = injectMessage("ViewEntireMessage_Bug39246");

		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(5000);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick("id=zv__CLV__MSG_msgTruncation_link");
		Thread.sleep(5000);
		selenium.selectWindow("_blank");
		String msgBody = null;
		msgBody = selenium.getBodyText();
		assertReport(msgBody, localize(locator.from), "Verifying From header");
		assertReport(msgBody, localize(locator.subject),
				"Verifying Subject header");
		assertReport(msgBody, localize(locator.to), "Verifying To header");
		assertReport(msgBody, localize(locator.cc), "Verifying Cc header");
		assertReport(msgBody, "jitesh sojitra <jitesh.sojitra@zimbra.com>",
				"Verifying From field value");
		assertReport(
				msgBody,
				"Re: QTPResult_FULL_IE7(FRANKLIN 5.0.5_GA_2184.RHEL4.NETWORK qa62 QAFEPERF-1): Tests Ran:1446 Passed: 1208 Failed: 238 QTPIssues: 1",
				"Verifying Subject field value");
		assertReport(
				msgBody,
				"Raja Rao <rrao@zimbra.com>, Prashant Jaiswal <pjaiswal@zimbra.com>, Krishnakumar Sure <krishnakumar.sure@zimbra.com>",
				"Verifying To field value");
		assertReport(
				msgBody,
				"suryakant <suryakant@zimbra.com>, Matt Rhoades <matt.rhoades@zimbra.com>",
				"Verifying Cc field value");

		Assert
				.assertFalse(msgBody.contains("HTTP ERROR: 404"),
						"Clicking to 'View entire message' link throws HTTP ERROR: 404");
		Assert
				.assertFalse(
						msgBody
								.contains("The page you were trying to access does not exist."),
						"Clicking to 'View entire message' link doesn't open message");
		Assert
				.assertFalse(msgBody.contains("Internal Server Error"),
						"Verifying message data after clicking to 'View entire message' link");
		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ParsingErrorWhileAcceptingInvite_Bug38564() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		subject = injectMessage("ParsingErrorWhileAcceptingInvite_Bug38564");

		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zButton.zClick(localize(locator.accept));
		Thread.sleep(1500);
		String calView = "workWeek";
		String startDate = "20080924";
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=calendar&view=" + calView
				+ "&date=" + startDate);
		zNavigateAgainIfRequired(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=calendar&view=" + calView
				+ "&date=" + startDate);
		obj.zAppointment.zDblClick("happyhappyjoyjoy fun time");
		Thread.sleep(2000);
		obj.zButton.zExists(localize(locator.close));
		obj.zButton.zExists(localize(locator.today));
		selenium.open(config.getString("mode") + "://"
				+ config.getString("server") + "/?app=calendar");
		Thread.sleep(3000);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void NotAbletoViewRfc822Msg_Bug40561() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		subject = injectMessage("NotAbletoViewRfc822Msg_Bug40561");

		obj.zMessageItem.zClick(subject);
		Thread.sleep(3500); // give time for link to appear
		zWaitTillObjectExist("link",
				"Broadsoft zimlet for demo account on dogfood");
		verifyRfc822Attachment();
		selenium.selectWindow(null);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(1500);
		String newUser = ProvZCS.getRandomAccount();
		obj.zEditField.zType(page.zComposeView.zToField, newUser);
		obj.zCheckbox
				.zVerifyIsChecked("Broadsoft zimlet for demo account on dogfood");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);

		zKillBrowsers();
		SelNGBase.selfAccountName = newUser;
		page.zLoginpage.zLoginToZimbraAjax(newUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Fwd: **CORP UPGRADE");
		obj.zMessageItem.zClick("Fwd: **CORP UPGRADE");
		Thread.sleep(3500); // give time for link to appear
		zWaitTillObjectExist("link",
				"Broadsoft zimlet for demo account on dogfood");
		verifyRfc822Attachment();
		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cantViewMessage_Bug4738() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		subject = injectMessage("CantViewMessage_Bug4738");

		obj.zMessageItem.zClick(subject);
		String msgHTML = obj.zMessageItem.zGetInnerHTML(subject);
		Boolean ImgCheckboxUnchecked = msgHTML.contains("ImgCheckboxUnchecked");
		Boolean ImgPriorityNormal_list = msgHTML
				.contains("ImgPriorityNormal_list");
		Boolean SandyZimmer = msgHTML.contains("Sandy Zimmer");
		Boolean ImgAttachment = msgHTML.contains("ImgAttachment");
		Boolean REfunnymessages = msgHTML.contains("RE: funny messages");
		assertReport("true", ImgCheckboxUnchecked.toString(),
				"'ImgCheckboxUnchecked' not found in message HTML");
		assertReport("true", ImgPriorityNormal_list.toString(),
				"'ImgPriorityNormal_list' not found in message HTML");
		assertReport("true", SandyZimmer.toString(),
				"'Sandy Zimmer' not found in message HTML");
		assertReport("true", ImgAttachment.toString(),
				"'ImgAttachment' not found in message HTML");
		assertReport("true", REfunnymessages.toString(),
				"'RE: funnymessages' not found in message HTML");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void ViewAttachment_Bug37352() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		subject = injectMessage("ViewEntireMessage_Bug37352");

		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick("link=GT-RNOC 3p.26.pdf");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void msgShowsBlank_Bug42127() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		subject = injectMessage("MsgShowsBlank_Bug42127");

		obj.zMessageItem.zClick(subject);
		if (config.getString("browser").equals("IE")) {
			Assert
					.assertTrue(
							selenium
									.isElementPresent("xpath=//div[contains(@id,'zlif__CLV') and contains(@class,'ImgAttachment')]"),
							"Attachment symbol does not found");
		} else {
			obj.zMessageItem.zVerifyHasAttachment(subject);
		}
		String msgHTML = obj.zMessageItem.zGetInnerHTML(subject);
		Boolean ImgCheckboxUnchecked = msgHTML.contains("ImgCheckboxUnchecked");
		Boolean ImgPriorityNormal_list = msgHTML
				.contains("ImgPriorityNormal_list");
		Boolean MarkNichols = msgHTML.contains("Mark Nichols");
		Boolean fwdBadLink = msgHTML
				.contains("Fwd: Bad Link to 6.0 User Guide on Zimbra Site");
		Boolean MarkNicholsYahoo = msgHTML
				.contains("Mark Nichols Yahoo!/Zimbra Professional Services");
		Boolean ImgAttachment = msgHTML.contains("ImgAttachment");
		assertReport("true", ImgCheckboxUnchecked.toString(),
				"'ImgCheckboxUnchecked' not found in message HTML");
		assertReport("true", ImgPriorityNormal_list.toString(),
				"'ImgPriorityNormal_list' not found in message HTML");
		assertReport("true", MarkNichols.toString(),
				"'Mark Nichols' not found in message HTML");
		assertReport("true", fwdBadLink.toString(),
				"'Fwd: Bad Link to 6.0 User Guide on Zimbra Site' not found in message HTML");
		assertReport("true", MarkNicholsYahoo.toString(),
				"'Mark Nichols Yahoo!/Zimbra Professional Services' not found in message HTML");
		assertReport("true", ImgAttachment.toString(),
				"'ImgAttachment' not found in message HTML");
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Test Case:-Mail View of large mail message do not display attachments
	 * 1.Inject large Mail to self 2.Verify same msg in inbox 3.Verify
	 * Download,Briefcase,Remove,DownloadAll,RemoveAll Links. 4.Verify Rffc
	 * attachment. Expected:-Very long mail msg should display attachments in
	 * mail msg view.
	 * 
	 * @throws Exception
	 * @author Girish
	 */

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cantSeeAttachForLargeMsg_Bug30893() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		subject = injectMessage("CantSeeAttachForLargeMsg_Bug30893");

		obj.zMessageItem.zClick(subject);
		Thread.sleep(3000);
		zWaitTillObjectExist("link", localize(locator.saveFile));
		if (config.getString("browser").contains("FF")) {
			obj.zMessageItem.zVerifyHasAttachment(subject);
		}
		Boolean downloadLink = selenium.isElementPresent("Link="
				+ localize(locator.saveFile));
		Boolean briefcaseLink;
		if (config.getString("locale").equals("nl")) {
			briefcaseLink = selenium.isElementPresent("Link=Aktetas");
		} else {
			briefcaseLink = selenium.isElementPresent("Link="
					+ localize(locator.briefcase));
		}
		Boolean removeLink = selenium.isElementPresent("Link="
				+ localize(locator.remove));
		assertReport("true", downloadLink.toString(),
				"Verify Download link exists for message");
		assertReport("true", briefcaseLink.toString(),
				"Verify Briefcase link exists for message");
		assertReport("true", removeLink.toString(),
				"Verify Remove link exists for message");

		Boolean downloadAllAttachmentsLink = selenium.isElementPresent("Link="
				+ localize(locator.downloadAll));
		Boolean removeAllAttachmentsLink = selenium.isElementPresent("Link="
				+ localize(locator.removeAllAttachments));
		assertReport("true", downloadAllAttachmentsLink.toString(),
				"Verify Download all attachments link exists for message");
		assertReport("true", removeAllAttachmentsLink.toString(),
				"Verify Remove all attachments link exists for message");
		Assert.assertTrue(selenium
				.isElementPresent("Link=YOUR TRAVEL INFORMATION"));

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void continouslyPromptingReadReceipt_Bug44128() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subject;
		zGoToApplication("Preferences");
		zGoToPreferences("Mail");
		obj.zRadioBtn.zClick(localize(locator.readReceiptAsk));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(1000);
		zGoToApplication("Mail");
		subject = injectMessage("ContinouslyPromptingReadReceipt_Bug44128");
		obj.zMessageItem.zClick(subject);
		zWaitTillObjectExist("dialog", localize(locator.warningMsg));
		if (!config.getString("browser").equals("IE")) {
			assertReport(localize(locator.readReceiptSend).replaceAll("<br>",
					""), obj.zDialog.zGetMessage(localize(locator.warningMsg)),
					"Verifying dialog text for notifying read receipt");
		}
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zDialog.zNotExists(localize(locator.warningMsg));
		obj.zMessageItem.zClick(subject);
		obj.zDialog.zNotExists(localize(locator.warningMsg));

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void unableToOpenMessage_Bug45852() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String subject;

		zGoToApplication("Address Book");
		if (config.getString("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		zWaitTillObjectExist("editfield", page.zABCompose.zLastEditField);
		obj.zEditField.zActivateAndType(page.zABCompose.zFirstEditField,
				"Maria");
		obj.zEditField.zActivateAndType(page.zABCompose.zLastEditField, "Lo");
		obj.zEditField.zActivateAndType(page.zABCompose.zEmail1EditField,
				"mlo@zimbra.com");
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		Thread.sleep(1500);

		zGoToApplication("Address Book");
		subject = injectMessage("UnableToOpenMessage_Bug45852");
		obj.zMessageItem.zClick(subject);
		Thread.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(5000); // taking too much time to open msg
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("link",
				"Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		selenium
				.click("Link=Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		verifyRfc822Attachment2();
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);

		selenium.selectWindow(null);
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(1500);
		String newUser = ProvZCS.getRandomAccount();
		obj.zEditField.zType(page.zComposeView.zToField, newUser);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);

		zKillBrowsers();
		SelNGBase.selfAccountName = newUser;
		page.zLoginpage.zLoginToZimbraAjax(newUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Fwd: attached messages");
		obj.zMessageItem.zClick("Fwd: attached messages");
		Thread.sleep(3500); // give time for link to appear
		zWaitTillObjectExist("link",
				"Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		selenium
				.click("Link=Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		verifyRfc822Attachment2();
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		selenium.selectWindow(null);

		needReset = false;
	}

	public static String injectMessage(String fileName) throws Exception {
		String subject = null;
		StringBuffer contents = new StringBuffer();
		File file = new File(dir.getAbsolutePath() + "/" + fileName + ".txt");
		BufferedReader reader = null;
		reader = new BufferedReader(new FileReader(file));
		String text = null;
		while ((text = reader.readLine()) != null) {
			contents.append(text).append(System.getProperty("line.separator"));
			if (text.contains("Subject:") && foundFlag == false) {
				if (text.length() >= 19) {
					subject = text.substring(9, 19).trim();
					foundFlag = true;
				} else {
					subject = text.substring(9).trim();
					foundFlag = true;
				}
			}
		}
		String[] accounts = { SelNGBase.selfAccountName };
		ProvZCS.addMessageLmtp(accounts, SelNGBase.selfAccountName, contents
				.toString());
		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);

		foundFlag = false;
		return subject;
	}

	private static void verifyRfc822Attachment() throws Exception {
		selenium.click("Link=Broadsoft zimlet for demo account on dogfood");
		Thread.sleep(5000); // taking too much time to open msg
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zCloseIconBtn_newWindow);
		String msgBody = selenium.getBodyText();
		obj.zButton.zExists(page.zMailApp.zReplyIconBtn_newWindow);
		obj.zButton.zExists(page.zMailApp.zReplyAllIconBtn_newWindow);
		obj.zButton.zExists(page.zMailApp.zForwardIconBtn_newWindow);
		assertReport(msgBody, "Broadsoft zimlet for demo account on dogfood",
				"Verifying subject field");
		assertReport(msgBody, localize(locator.from), "Verifying from header");
		assertReport(msgBody, "rrao@zimbra.com", "Verifying from field value");
		assertReport(msgBody, "zimletConfiguration.PNG",
				"Verifying image attachment1");
		assertReport(msgBody, "BroadWorksAnyWhere.PNG",
				"Verifying image attachment2");
		assertReport(msgBody, "broadSoft-sipPhone.PNG",
				"Verifying image attachment3");
		Boolean download = selenium.isElementPresent("Link="
				+ localize(locator.download));
		Boolean viewAllImages = selenium.isElementPresent("Link="
				+ localize(locator.viewAllImages));
		Boolean downloadAllAttachments = selenium.isElementPresent("Link="
				+ localize(locator.downloadAll));
		Boolean removeAllAttachments = selenium.isElementPresent("Link="
				+ localize(locator.removeAllAttachments));
		assertReport("true", download.toString(),
				"Verifying Download link exist");
		assertReport("true", viewAllImages.toString(),
				"Verifying View All Images link exist");
		assertReport("true", downloadAllAttachments.toString(),
				"Verifying Download All Attachments link exist");
		assertReport("true", removeAllAttachments.toString(),
				"Verifying Remove All Attachments link exist");
	}

	private static void verifyRfc822Attachment2() throws Exception {
		Thread.sleep(5000); // taking too much time to open msg
		selenium.selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zCloseIconBtn_newWindow);
		String msgBody = selenium.getBodyText();
		System.out.println(msgBody);
		obj.zButton.zExists(page.zMailApp.zReplyIconBtn_newWindow);
		obj.zButton.zExists(page.zMailApp.zReplyAllIconBtn_newWindow);
		obj.zButton.zExists(page.zMailApp.zForwardIconBtn_newWindow);
		assertReport(msgBody,
				"Re: SF: Case 00051542: - Timezone conversion 24 hour error",
				"Verifying subject field");
		assertReport(msgBody, "rking@zimbra.com", "Verifying from field value");
		assertReport(msgBody, "jhahm@zimbra.com", "Verifying to field value");
		assertReport(
				msgBody,
				"support-team@zimbra.com; calendar-team@zimbra.com; support@zimbra.com",
				"Verifying cc field values");
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}