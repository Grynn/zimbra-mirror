package projects.zcs.tests.mail.bugs;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.LmtpUtil;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraSeleniumProperties;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class LmtpInject extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("viewEntireMessage_Bug39246")
				|| test.equals("parsingErrorWhileAcceptingInvite_Bug38564")
				|| test.equals("notAbletoViewRfc822Msg_Bug40561")
				|| test.equals("cantViewMessage_Bug4738")
				|| test.equals("viewAttachment_Bug37352")
				|| test.equals("msgShowsBlank_Bug42127")
				|| test.equals("cantSeeAttachForLargeMsg_Bug30893")
				|| test.equals("unableToOpenMessage_Bug45852")
				|| test.equals("cantViewMultipartMessage_Bug43586_Bug45126")) {
			return new Object[][] { {} };
		} else if (test.equals("fixLineWrapping_Bug425")) {
			return new Object[][] { {
					SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com",
					"bccuser@testdomain.com",
					"fixLineWrapping_Bug425",
					"Should someone decide to implement this for this task, I'd advise picking a good set of default command flags, and letting power users override them in their preferences (preferably by pre-populating such a field with the default, and supplying a Reset to default button... with the appropriate COS and system default stacking as with other options). P.S. Why there is that 10 chars difference between 70 and 80? It solves this bug for other clients ;-) Because it makes nice 10 chars reserve which prevents non smart clients from wrapping and creating those lonely one-word orphans.",
					"" } };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.zLogin();
	}

	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void viewEntireMessage_Bug39246() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp.zInjectMessage("viewEntireMessage_Bug39246");

		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(5000);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick("id=zv__CLV__MSG_msgTruncation_link");
		SleepUtil.sleep(5000);
		SelNGBase.selenium.get().selectWindow("_blank");
		String msgBody = null;
		msgBody = SelNGBase.selenium.get().getBodyText();
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
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void unableToOpenMessage_Bug45852() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		zGoToApplication("Address Book");
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			SleepUtil.sleep(2500);
		} else {
			SleepUtil.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		zWaitTillObjectExist("editfield", page.zABCompose.zLastEditField);
		obj.zEditField.zActivateAndType(page.zABCompose.zFirstEditField,
				"Maria");
		obj.zEditField.zActivateAndType(page.zABCompose.zLastEditField, "Lo");
		obj.zEditField.zActivateAndType(page.zABCompose.zEmail1EditField,
				"mlo@zimbra.com");
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		SleepUtil.sleep(1500);

		zGoToApplication("Address Book");
		subject = page.zMailApp.zInjectMessage("unableToOpenMessage_Bug45852");
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		SleepUtil.sleep(5000); // taking too much time to open msg
		SelNGBase.selenium.get().selectWindow("_blank");
		zWaitTillObjectExist("link",
				"Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		SelNGBase.selenium
				.get()
				.click(
						"Link=Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		verifyRfc822Attachment2();
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);

		SelNGBase.selenium.get().selectWindow(null);
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		SleepUtil.sleep(1500);
		String newUser = Stafzmprov.getRandomAccount();
		obj.zEditField.zType(page.zComposeView.zToField, newUser);
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(2000);

		resetSession();
		SelNGBase.selfAccountName.set(newUser);
		page.zLoginpage.zLoginToZimbraAjax(newUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Fwd: attached messages");
		obj.zMessageItem.zClick("Fwd: attached messages");
		SleepUtil.sleep(3500); // give time for link to appear
		zWaitTillObjectExist("link",
				"Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		SelNGBase.selenium
				.get()
				.click(
						"Link=Re: SF: Case 00051542: - Timezone conversion 24 hour error");
		verifyRfc822Attachment2();
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void parsingErrorWhileAcceptingInvite_Bug38564() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp
				.zInjectMessage("parsingErrorWhileAcceptingInvite_Bug38564");

		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
		obj.zButton.zClick(localize(locator.accept));
		SleepUtil.sleep(1500);
		String calView = "workWeek";
		String startDate = "20080924";
		SelNGBase.selenium.get().open(
				ZimbraSeleniumProperties.getStringProperty("mode") + "://"
						+ ZimbraSeleniumProperties.getStringProperty("server")
						+ "/?app=calendar&view=" + calView + "&date="
						+ startDate);
		zNavigateAgainIfRequired(ZimbraSeleniumProperties
				.getStringProperty("mode")
				+ "://"
				+ ZimbraSeleniumProperties.getStringProperty("server")
				+ "/?app=calendar&view=" + calView + "&date=" + startDate);
		obj.zAppointment.zDblClick("happyhappyjoyjoy fun time");
		SleepUtil.sleep(2000);
		obj.zButton.zExists(localize(locator.close));
		obj.zButton.zExists(localize(locator.today));
		SelNGBase.selenium.get().open(
				ZimbraSeleniumProperties.getStringProperty("mode") + "://"
						+ ZimbraSeleniumProperties.getStringProperty("server")
						+ "/?app=calendar");
		SleepUtil.sleep(3000);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void notAbletoViewRfc822Msg_Bug40561() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp
				.zInjectMessage("notAbletoViewRfc822Msg_Bug40561");

		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(3500); // give time for link to appear
		zWaitTillObjectExist("link",
				"Broadsoft zimlet for demo account on dogfood");
		verifyRfc822Attachment();
		SelNGBase.selenium.get().selectWindow(null);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		SleepUtil.sleep(1500);
		String newUser = Stafzmprov.getRandomAccount();
		obj.zEditField.zType(page.zComposeView.zToField, newUser);
		obj.zCheckbox
				.zVerifyIsChecked("Broadsoft zimlet for demo account on dogfood");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(2000);

		resetSession();
		SelNGBase.selfAccountName.set(newUser);
		page.zLoginpage.zLoginToZimbraAjax(newUser);
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Fwd: **CORP UPGRADE");
		obj.zMessageItem.zClick("Fwd: **CORP UPGRADE");
		SleepUtil.sleep(3500); // give time for link to appear
		zWaitTillObjectExist("link",
				"Broadsoft zimlet for demo account on dogfood");
		verifyRfc822Attachment();
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cantViewMessage_Bug4738() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp.zInjectMessage("cantViewMessage_Bug4738");

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
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void viewAttachment_Bug37352() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp.zInjectMessage("viewAttachment_Bug37352");
		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		zWaitTillObjectExist("link", "GT-RNOC 3p.26.pdf");
		zWaitTillObjectExist("link", localize(locator.download));
		zWaitTillObjectExist("link", localize(locator.briefcase));
		zWaitTillObjectExist("link", localize(locator.remove));

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void msgShowsBlank_Bug42127() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp.zInjectMessage("msgShowsBlank_Bug42127");

		obj.zMessageItem.zClick(subject);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Assert
					.assertTrue(
							SelNGBase.selenium
									.get()
									.isElementPresent(
											"xpath=//div[contains(@id,'zlif__CLV') and contains(@class,'ImgAttachment')]"),
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
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCancelIconBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test Case:-Mail View of large mail message do not display attachments
	 * 1.Inject large Mail to self 2.Verify same msg in inbox 3.Verify
	 * Download,Briefcase,Remove,DownloadAll,RemoveAll Links. 4.Verify Rffc
	 * attachment. Expected:-Very long mail msg should display attachments in
	 * mail msg view.
	 * 
	 * @author Girish
	 */

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cantSeeAttachForLargeMsg_Bug30893() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp
				.zInjectMessage("cantSeeAttachForLargeMsg_Bug30893");

		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(3000);
		zWaitTillObjectExist("link", localize(locator.saveFile));
		if (ZimbraSeleniumProperties.getStringProperty("browser")
				.contains("FF")) {
			obj.zMessageItem.zVerifyHasAttachment(subject);
		}
		Boolean downloadLink = SelNGBase.selenium.get().isElementPresent(
				"Link=" + localize(locator.saveFile));
		Boolean briefcaseLink;
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("nl")) {
			briefcaseLink = SelNGBase.selenium.get().isElementPresent(
					"Link=Aktetas");
		} else {
			briefcaseLink = SelNGBase.selenium.get().isElementPresent(
					"Link=" + localize(locator.briefcase));
		}
		Boolean removeLink = SelNGBase.selenium.get().isElementPresent(
				"Link=" + localize(locator.remove));
		assertReport("true", downloadLink.toString(),
				"Verify Download link exists for message");
		assertReport("true", briefcaseLink.toString(),
				"Verify Briefcase link exists for message");
		assertReport("true", removeLink.toString(),
				"Verify Remove link exists for message");

		Boolean downloadAllAttachmentsLink = SelNGBase.selenium.get()
				.isElementPresent("Link=" + localize(locator.downloadAll));
		Boolean removeAllAttachmentsLink = SelNGBase.selenium.get()
				.isElementPresent(
						"Link=" + localize(locator.removeAllAttachments));
		assertReport("true", downloadAllAttachmentsLink.toString(),
				"Verify Download all attachments link exists for message");
		assertReport("true", removeAllAttachmentsLink.toString(),
				"Verify Remove all attachments link exists for message");
		Assert.assertTrue(SelNGBase.selenium.get().isElementPresent(
				"Link=YOUR TRAVEL INFORMATION"));

		SelNGBase.needReset.set(false);
	}

	/**
	 * bug 43586 - multipart/related message is not displayed
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void cantViewMultipartMessage_Bug43586_Bug45126() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp
				.zInjectMessage("cantViewMultipartMessage_Bug43586");
		obj.zMessageItem.zClick(subject);
		String msgHTML = obj.zMessageItem.zGetInnerHTML(subject);
		Boolean body1 = msgHTML.contains("Oltre duecento trattori");
		assertReport("true", body1.toString(),
				"'Oltre duecento trattori' not found in message HTML");
		Assert.assertTrue(SelNGBase.selenium.get().isElementPresent(
				"xpath=//tr/td[1]/p/img[contains(@dfsrc,'cid:minicp')]"));

		SelNGBase.needReset.set(false);
	}

	/**
	 * bug 425 - Fix line wrapping (when we reply/forward to mail which contains
	 * large body, the body was getting wrapped upto 80 chracters). Written test
	 * for plain text and html mode both
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void fixLineWrapping_Bug425(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String actual;
		to = SelNGBase.selfAccountName.get();
		String recipients[] = { to };
		LmtpUtil.injectMessage(to, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(1000);
		actual = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertEquals(actual, body, "Verifying message body text");

		obj.zButton.zClick(page.zMailApp.zReplyBtn);
		SleepUtil.sleep(1000);
		actual = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");
		obj.zEditField.zType(page.zComposeView.zSubjectField, "verifyReply");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		MailApp.ClickCheckMailUntilMailShowsUp("verifyReply");
		obj.zMessageItem.zClick("verifyReply");
		SleepUtil.sleep(1000);
		actual = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");

		Stafzmprov.modifyAccount(SelNGBase.selfAccountName.get(),
				"zimbraPrefComposeFormat", "html");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		obj.zMessageItem.zClick("verifyReply");

		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		SleepUtil.sleep(1000);
		actual = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");
		obj.zTextAreaField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName.get());
		obj.zEditField.zType(page.zComposeView.zSubjectField, "verifyForward");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		MailApp.ClickCheckMailUntilMailShowsUp("verifyForward");
		obj.zMessageItem.zClick("verifyForward");
		SleepUtil.sleep(1000);
		actual = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");

		SelNGBase.needReset.set(false);
	}

	// --------------------------- internal wrappers ---------------------------
	private static void verifyRfc822Attachment() throws Exception {
		SelNGBase.selenium.get().click(
				"Link=Broadsoft zimlet for demo account on dogfood");
		SleepUtil.sleep(5000); // taking too much time to open msg
		SelNGBase.selenium.get().selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zCloseIconBtn_newWindow);
		String msgBody = SelNGBase.selenium.get().getBodyText();
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
		Boolean download = SelNGBase.selenium.get().isElementPresent(
				"Link=" + localize(locator.download));
		Boolean viewAllImages = SelNGBase.selenium.get().isElementPresent(
				"Link=" + localize(locator.viewAllImages));
		Boolean downloadAllAttachments = SelNGBase.selenium.get()
				.isElementPresent("Link=" + localize(locator.downloadAll));
		Boolean removeAllAttachments = SelNGBase.selenium.get()
				.isElementPresent(
						"Link=" + localize(locator.removeAllAttachments));
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
		SleepUtil.sleep(5000); // taking too much time to open msg
		SelNGBase.selenium.get().selectWindow("_blank");
		zWaitTillObjectExist("button", page.zMailApp.zCloseIconBtn_newWindow);
		String msgBody = SelNGBase.selenium.get().getBodyText();
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
}