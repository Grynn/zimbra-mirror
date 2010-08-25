package projects.zcs.tests.mail.messageactions;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class MessagePreviewPaneActionTests extends CommonTest {
	protected int j = 0;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("attachBriefcaseFileInMail")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5),
					"testexcelfile.xls,testwordfile.doc" } };
		} else if (test.equals("attachBriefcaseFileInMail_NewWindow")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5),
					"testexcelfile.xls,testwordfile.doc" } };
		} else if (test.equals("addingAttachFromMsgToBriefcaseFolder")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5),
					"testtextfile.txt" } };
		} else if (test.equals("removingAttachmentFromMessage")
				|| test.equals("removingAttachmentFromMessage_NewWindow")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5), "bug22417.ics" } };
		} else if (test.equals("removingAllAttachmentFromMessage")
				|| test.equals("removingAllAttachmentFromMessage_NewWindow")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5),
					"structure.jpg, contact25.pst" } };
		} else if (test.equals("attachingFilesFromBothWayAndVerifyAllLinks")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5),
					"MultiLingualContact.csv" } };
		} else if (test.equals("addToCalendarFromICSAttachmentMail_Bug27959")
				|| test
						.equals("jsErrorOnClickingAddToCalendarInNewWindow_Bug49734")) {
			return new Object[][] { {} };
		} else {
			return new Object[][] { { "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
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

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachBriefcaseFileInMail(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String[] attachment = attachments.split(",");
		uploadFile(attachments);

		zGoToApplication("Mail");

		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName.get());
		obj.zTextAreaField.zType(page.zComposeView.zCcField, cc);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		obj.zEditor.zType(body);

		// adding briefcase file as an attachment & sending mail to self
		obj.zButton.zClick(page.zComposeView.zAddAttachmentIconBtn);
		obj.zTab.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		obj.zFolder.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		if (attachment.length == 2) {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__258__se",
					localize(locator.attachFile));
		} else {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
		}
		obj.zButton.zClickInDlgByName(localize(locator.attach),
				localize(locator.attachFile));
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(3000);

		// verification
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
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
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachBriefcaseFileInMail_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String[] attachment = attachments.split(",");
		uploadFile(attachments);

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToComposeByShiftClick();
		obj.zTextAreaField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName.get());
		obj.zTextAreaField.zType(page.zComposeView.zCcField, cc);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		obj.zEditor.zType(body);

		// adding briefcase file as an attachment & sending mail to self
		obj.zButton.zClick(page.zComposeView.zAddAttachmentIconBtn);
		obj.zTab.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		obj.zFolder.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		if (attachment.length == 2) {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__258__se",
					localize(locator.attachFile));
		} else {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
		}
		obj.zButton.zClickInDlgByName(localize(locator.attach),
				localize(locator.attachFile));
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(3000);

		// verification
		SelNGBase.selenium.get().selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
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
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addingAttachFromMsgToBriefcaseFolder(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName
				.get(), cc, bcc, subject, body, attachments);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
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
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("nl")) {
			SelNGBase.selenium.get().click("link=Aktetas");
		} else {
			SelNGBase.selenium.get().click(
					"link=" + localize(locator.briefcase));
		}
		obj.zFolder.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.addToBriefcaseTitle));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToBriefcaseTitle));

		zGoToApplication("Briefcase");
		obj.zFolder.zClick(page.zBriefcaseApp.zBriefcaseFolder);
		obj.zBriefcaseItem.zExists(attachments);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAttachmentFromMessage(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName
				.get(), cc, bcc, subject, body, attachments);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
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
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		SelNGBase.selenium.get().click("link=" + localize(locator.remove));
		assertReport(localize(locator.attachmentConfirmRemove), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachment from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		SleepUtil.sleep(3000);
		Assert
				.assertFalse(SelNGBase.selenium.get().isElementPresent(
						"link=" + localize(locator.download)),
						"Download link is not removed from message after removing attachment");
		Assert
				.assertFalse(SelNGBase.selenium.get().isElementPresent(
						"link=" + localize(locator.briefcase)),
						"Briefcase link is not removed from message after removing attachment");
		Assert
				.assertFalse(SelNGBase.selenium.get().isElementPresent(
						"link=" + localize(locator.remove)),
						"Remove link is not removed from message after removing attachment");

		// verify reply
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		SleepUtil.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		// verify reply all
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		SleepUtil.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		// verify forward
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		SleepUtil.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		// verify edit as new
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(page.zMailApp.zEditAsNewMenuIconBtn);
		SleepUtil.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAttachmentFromMessage_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName
				.get(), cc, bcc, subject, body, attachments);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		SleepUtil.sleep(1000);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn);
		SleepUtil.sleep(2500);
		SelNGBase.selenium.get().selectWindow("_blank");
		SleepUtil.sleep(1000);
		zWaitTillObjectExist("button", page.zMailApp.zCloseIconBtn_newWindow);
		SelNGBase.selenium.get().click("link=" + localize(locator.remove));
		SleepUtil.sleep(1000);
		assertReport(localize(locator.attachmentConfirmRemove), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachment from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		SleepUtil.sleep(1000);
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.download)),
						"Download link is not removed from new window after removing attachment from message");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.briefcase)),
						"Briefcase link is not removed from new window after removing attachment from message");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.addToCalendar)),
						"Add to Calendar link is not removed from new window after removing attachment from message");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.remove)),
						"Remove link is not removed from new window after removing attachment from message");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SleepUtil.sleep(1000);
		SelNGBase.selenium.get().selectWindow(null);
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.download)),
						"Download link is not removed from same window after removing attachment from new window");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.briefcase)),
						"Briefcase link is not removed from same window after removing attachment from new window");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.addToCalendar)),
						"Add to Calendar link is not removed from same window after removing attachment from new window");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.remove)),
						"Remove link is not removed from same window after removing attachment from new window");
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));
		SleepUtil.sleep(1000);
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.download)),
						"Download link is not removed from same window after removing attachment from new window");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.briefcase)),
						"Briefcase link is not removed from same window after removing attachment from new window");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.addToCalendar)),
						"Add to Calendar link is not removed from same window after removing attachment from new window");
		Assert
				.assertFalse(
						SelNGBase.selenium.get().isElementPresent(
								"link=" + localize(locator.remove)),
						"Remove link is not removed from same window after removing attachment from new window");

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAllAttachmentFromMessage_NewWindow(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName
				.get(), cc, bcc, subject, body, attachments);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		SleepUtil.sleep(2500);
		SelNGBase.selenium.get().selectWindow("_blank");
		SleepUtil.sleep(1000);
		SelNGBase.selenium.get().click(
				"link=" + localize(locator.removeAllAttachments));
		SleepUtil.sleep(1000);
		SelNGBase.selenium.get().selectWindow(null);
		assertReport(localize(locator.attachmentConfirmRemoveAll), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachments from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		SelNGBase.selenium.get().selectWindow("_blank");
		Boolean removeLink = SelNGBase.selenium.get().isElementPresent(
				localize(locator.removeAllAttachments));
		assertReport(
				"false",
				removeLink.toString(),
				"Verifying Remove All Attachments link exist or not after removing all attachments from message");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SleepUtil.sleep(1000);
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAllAttachmentFromMessage(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String[] attachment = attachments.split(",");

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName
				.get(), cc, bcc, subject, body, attachments);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
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
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		SelNGBase.selenium.get().click(
				"link=" + localize(locator.removeAllAttachments));
		assertReport(localize(locator.attachmentConfirmRemoveAll), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachments from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		Boolean removeLink = SelNGBase.selenium.get().isElementPresent(
				localize(locator.removeAllAttachments));
		assertReport(
				"false",
				removeLink.toString(),
				"Verifying Remove All Attachments link exist or not after removing all attachments from message");

		// verify reply
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		SleepUtil.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		// verify reply all
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		SleepUtil.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		// verify forward
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		SleepUtil.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		// verify edit as new
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(page.zMailApp.zEditAsNewMenuIconBtn);
		SleepUtil.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachingFilesFromBothWayAndVerifyAllLinks(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String[] attachment = attachments.split(",");
		uploadFile(attachments);

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName
				.get(), cc, bcc, subject, body, "putty.log");
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
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
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		Boolean downloadLink = SelNGBase.selenium.get().isElementPresent(
				"Link=" + localize(locator.download));
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

		// verify reply
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		SleepUtil.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsNotChecked("putty.log");
			obj.zCheckbox.zClick("putty.log");
		}

		// adding briefcase file as an attachment & sending mail to self
		obj.zButton.zClick(page.zComposeView.zAddAttachmentIconBtn);
		obj.zTab.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		obj.zFolder.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		if (attachment.length == 2) {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__258__se",
					localize(locator.attachFile));
		} else {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
		}
		obj.zButton.zClickInDlgByName(localize(locator.attach),
				localize(locator.attachFile));
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(2000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
			obj.zCheckbox.zVerifyIsChecked("putty.log");
		}
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(2000);

		// verification
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Re: " + subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
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
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		Boolean downloadAllAttachmentsLink = SelNGBase.selenium.get()
				.isElementPresent("link=" + localize(locator.downloadAll));
		Boolean removeAllAttachmentsLink = SelNGBase.selenium.get()
				.isElementPresent(
						"link=" + localize(locator.removeAllAttachments));
		assertReport("true", downloadAllAttachmentsLink.toString(),
				"Verify Download all attachments link exists for message");
		assertReport("true", removeAllAttachmentsLink.toString(),
				"Verify Remove all attachments link exists for message");

		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(2000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
			obj.zCheckbox.zVerifyIsChecked("putty.log");
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		SelNGBase.needReset.set(false);
	}

	/**
	 * bug 27959 - Create appointment from ics attachment
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addToCalendarFromICSAttachmentMail_Bug27959() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject1, subject2;
		subject1 = page.zMailApp
				.zInjectMessage("createApptFromICSAttachment1_Bug27959");
		subject2 = page.zMailApp
				.zInjectMessage("createApptFromICSAttachment2_Bug27959");
		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject1);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject2);

		obj.zMessageItem.zClick(subject1);
		SleepUtil.sleep(1000);
		SelNGBase.selenium.get().click(
				"link=" + localize(locator.addToCalendar));
		obj.zFolder.zClickInDlgByName(localize(locator.calendar),
				localize(locator.addToCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToCalendar));
		SleepUtil.sleep(1000);
		zGoToApplication("Calendar");
		obj.zAppointment.zDblClick("Critical");
		obj.zRadioBtn.zClickInDlgByName(localize(locator.openSeries),
				localize(locator.openRecurringItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.openRecurringItem));
		obj.zButton.zClick(localize(locator.close));

		zGoToApplication("Mail");
		obj.zMessageItem.zClick(subject2);
		SleepUtil.sleep(1000);
		SelNGBase.selenium.get().click(
				"link=" + localize(locator.addToCalendar));
		obj.zFolder.zClickInDlgByName(localize(locator.calendar),
				localize(locator.addToCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToCalendar));
		SleepUtil.sleep(1000);
		zGoToApplication("Calendar");
		obj.zAppointment.zDblClick("every week");
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.openRecurringItem));
		obj.zButton.zClick(localize(locator.close));

		SelNGBase.needReset.set(false);
	}

	/**
	 * bug 49734 - JS error (t is undefined) while click to 'Add to Calendar'
	 * when viewing in separate window
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void jsErrorOnClickingAddToCalendarInNewWindow_Bug49734()
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject;
		subject = page.zMailApp
				.zInjectMessage("jsErrorOnClickingAddToCalendarInNewWindow_Bug49734");

		zGoToApplication("Mail");
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		SelNGBase.selenium.get().selectWindow("_blank");
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().click(
				"link=" + localize(locator.addToCalendar));
		obj.zFolder.zClickInDlgByName(localize(locator.calendar),
				localize(locator.addToCalendar));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToCalendar));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		SelNGBase.selenium.get().selectWindow(null);

		String startDate = "20060119";
		String calView = "workWeek";
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
		Thread.sleep(2000);
		obj.zAppointment.zExists("iCalBasic");

		SelNGBase.needReset.set(false);
	}

	private void uploadFile(String attachments) throws Exception {
		if (j == 0) {
			zGoToApplication("Briefcase");
			String[] attachment = attachments.split(",");
			for (int i = 0; i < attachment.length; i++) {
				page.zBriefcaseApp.zBriefcaseFileUpload(attachment[i], "");
			}
			j = j + 1;
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
		j = 0;
	}
}