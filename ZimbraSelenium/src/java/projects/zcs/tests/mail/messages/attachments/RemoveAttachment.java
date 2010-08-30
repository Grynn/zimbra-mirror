package projects.zcs.tests.mail.messages.attachments;

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
public class RemoveAttachment extends CommonTest {
	protected int j = 0;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("removingAttachmentFromMessage")
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

		checkForSkipException("all", "na", "na",
				"'Invalid key code' exception while upload file");
		checkForSkipException(
				"na",
				"SF",
				"39446",
				"New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

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

		checkForSkipException("all", "na", "na",
				"'Invalid key code' exception while upload file");

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