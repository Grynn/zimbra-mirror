package projects.zcs.tests.mail.messageactions;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;

/**
 * @author Jitesh Sojitra
 */
/**
 * @author jsojitra
 * 
 */
@SuppressWarnings("static-access")
public class AddRemoveAttachmentAndAddToBriefcaseTests extends CommonTest {
	protected int j = 0;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("attachBriefcaseFileInMail")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "testexcelfile.xls,testwordfile.doc" } };
		} else if (test.equals("attachBriefcaseFileInMail_NewWindow")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "testexcelfile.xls,testwordfile.doc" } };
		} else if (test.equals("addingAttachFromMsgToBriefcaseFolder")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "testtextfile.txt" } };
		} else if (test.equals("removingAttachmentFromMessage")
				|| test.equals("removingAttachmentFromMessage_NewWindow")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "bug22417.ics" } };
		} else if (test.equals("removingAllAttachmentFromMessage")
				|| test.equals("removingAllAttachmentFromMessage_NewWindow")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "structure.jpg, contact25.pst" } };
		} else if (test.equals("attachingFilesFromBothWayAndVerifyAllLinks")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "MultiLingualContact.csv" } };
		} else if (test.equals("senderAndFromInShowOriginal_Bug30438")) {
			return new Object[][] { { "admin@" + config.getString("server"),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar() + "Bug30438_Subject",
					getLocalizedData_NoSpecialChar() + "Bug30438_Body", "" } };
		} else if (test.equals("saveSearch_Bug34872")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData_NoSpecialChar(), getLocalizedData(5), "" } };
		} else if (test.equals("jsErrorOnClickingDetachIcon_Bug35948")) {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "" } };
		} else if (test.equals("msgBodyLostWhileChooseNotToAttachSig_Bug40559")
				|| test.equals("dontSelectFirstMsgByDefault_Bug39908_Bug43335")) {
			return new Object[][] { { "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5), "" } };
		} else if (test.equals("fixLineWrapping_Bug425")) {
			return new Object[][] { {
					selfAccountName,
					"ccuser@testdomain.com",
					"bccuser@testdomain.com",
					"fixLineWrapping_Bug425",
					"Should someone decide to implement this for this task, I'd advise picking a good set of default command flags, and letting power users override them in their preferences (preferably by pre-populating such a field with the default, and supplying a Reset to default button... with the appropriate COS and system default stacking as with other options). P.S. Why there is that 10 chars difference between 70 and 80? It solves this bug for other clients ;-) Because it makes nice 10 chars reserve which prevents non smart clients from wrapping and creating those lonely one-word orphans.",
					"" } };
		} else {
			return new Object[][] { { selfAccountName, "ccuser@testdomain.com",
					"bccuser@testdomain.com", getLocalizedData(5),
					getLocalizedData(5), "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachBriefcaseFileInMail(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] attachment = attachments.split(",");
		uploadFile(attachments);

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName);
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
		Thread.sleep(3000);

		// verification
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachBriefcaseFileInMail_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] attachment = attachments.split(",");
		uploadFile(attachments);

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToComposeByShiftClick();
		obj.zTextAreaField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName);
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
		Thread.sleep(3000);

		// verification
		selenium.selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addingAttachFromMsgToBriefcaseFolder(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName,
				cc, bcc, subject, body, attachments);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		if (config.getString("locale").equals("nl")) {
			selenium.click("link=Aktetas");
		} else {
			selenium.click("link=" + localize(locator.briefcase));
		}
		obj.zFolder.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.addToBriefcaseTitle));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.addToBriefcaseTitle));

		zGoToApplication("Briefcase");
		obj.zFolder.zClick(page.zBriefcaseApp.zBriefcaseFolder);
		obj.zBriefcaseItem.zExists(attachments);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAttachmentFromMessage(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName,
				cc, bcc, subject, body, attachments);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		selenium.click("link=" + localize(locator.remove));
		assertReport(localize(locator.attachmentConfirmRemove), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachment from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		Thread.sleep(3000);
		Boolean removeLink = selenium
				.isElementPresent(localize(locator.remove));
		assertReport("false", removeLink.toString(),
				"Verifying Remove link exist or not after removing attachment from message");

		// verify reply
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		// verify reply all
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		// verify forward
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		// verify edit as new
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(page.zMailApp.zEditAsNewMenuIconBtn);
		Thread.sleep(1000);
		obj.zCheckbox.zNotExists(attachments);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAttachmentFromMessage_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName,
				cc, bcc, subject, body, attachments);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		Thread.sleep(1000);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn);
		Thread.sleep(2500);
		selenium.selectWindow("_blank");
		Thread.sleep(1000);
		zWaitTillObjectExist("button", page.zMailApp.zCloseIconBtn_newWindow);
		selenium.click("link=" + localize(locator.remove));
		Thread.sleep(1000);
		assertReport(localize(locator.attachmentConfirmRemove), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachment from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		selenium.selectWindow("_blank");
		Boolean removeLink = selenium
				.isElementPresent(localize(locator.remove));
		assertReport("false", removeLink.toString(),
				"Verifying Remove link exist or not after removing attachment from message");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(1000);
		selenium.selectWindow(null);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byConversation));

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAllAttachmentFromMessage_NewWindow(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName,
				cc, bcc, subject, body, attachments);
		obj.zButton.zClick(page.zMailApp.zViewIconBtn);
		obj.zMenuItem.zClick(localize(locator.byMessage));
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDetachIconBtn2);
		Thread.sleep(2500);
		selenium.selectWindow("_blank");
		Thread.sleep(1000);
		selenium.click("link=" + localize(locator.removeAllAttachments));
		Thread.sleep(1000);
		selenium.selectWindow(null);
		assertReport(localize(locator.attachmentConfirmRemoveAll), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachments from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		selenium.selectWindow("_blank");
		Boolean removeLink = selenium
				.isElementPresent(localize(locator.removeAllAttachments));
		assertReport(
				"false",
				removeLink.toString(),
				"Verifying Remove All Attachments link exist or not after removing all attachments from message");
		obj.zButton.zClick(page.zMailApp.zCloseIconBtn_newWindow);
		Thread.sleep(1000);
		selenium.selectWindow(null);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void removingAllAttachmentFromMessage(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] attachment = attachments.split(",");

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName,
				cc, bcc, subject, body, attachments);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		selenium.click("link=" + localize(locator.removeAllAttachments));
		assertReport(localize(locator.attachmentConfirmRemoveAll), obj.zDialog
				.zGetMessage(localize(locator.warningMsg)),
				"Verifying dialog text for removing attachments from the message");
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		Boolean removeLink = selenium
				.isElementPresent(localize(locator.removeAllAttachments));
		assertReport(
				"false",
				removeLink.toString(),
				"Verifying Remove All Attachments link exist or not after removing all attachments from message");

		// verify reply
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		// verify reply all
		obj.zButton.zClick(page.zMailApp.zReplyAllIconBtn);
		Thread.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		// verify forward
		obj.zButton.zClick(page.zMailApp.zForwardIconBtn);
		Thread.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		// verify edit as new
		obj.zMessageItem.zRtClick(subject);
		obj.zMenuItem.zClick(page.zMailApp.zEditAsNewMenuIconBtn);
		Thread.sleep(1000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zNotExists(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachingFilesFromBothWayAndVerifyAllLinks(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String[] attachment = attachments.split(",");
		uploadFile(attachments);

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName,
				cc, bcc, subject, body, "putty.log");
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		Boolean downloadLink = selenium.isElementPresent("Link="
				+ localize(locator.download));
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

		// verify reply
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		Thread.sleep(1000);
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
		Thread.sleep(2000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
			obj.zCheckbox.zVerifyIsChecked("putty.log");
		}
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);

		// verification
		page.zMailApp.ClickCheckMailUntilMailShowsUp("Re: " + subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(2000);
		obj.zMessageItem.zVerifyHasAttachment(subject);
		Boolean downloadAllAttachmentsLink = selenium.isElementPresent("Link="
				+ localize(locator.downloadAll));
		Boolean removeAllAttachmentsLink = selenium.isElementPresent("Link="
				+ localize(locator.removeAllAttachments));
		assertReport("true", downloadAllAttachmentsLink.toString(),
				"Verify Download all attachments link exists for message");
		assertReport("true", removeAllAttachmentsLink.toString(),
				"Verify Remove all attachments link exists for message");

		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		Thread.sleep(2000);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
			obj.zCheckbox.zVerifyIsChecked("putty.log");
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		Thread.sleep(1000);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void senderAndFromInShowOriginal_Bug30438(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("en_GB")) {
			// Show original
			zKillBrowsers();
			page.zLoginpage.zLoginToZimbraAjax("admin@"
					+ config.getString("server"));
			zGoToApplication("Preferences");
			zGoToPreferences("Accounts");
			Thread.sleep(1500);
			obj.zButton.zClick("admin@" + config.getString("server"));
			obj.zMenuItem.zClick("root@" + config.getString("server"));
			obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
			Thread.sleep(2000);

			zGoToApplication("Mail");
			obj.zButton.zClick(localize(locator.view));
			obj.zMenuItem.zClick(localize(locator.byConversation));
			page.zComposeView.zNavigateToMailCompose();
			obj.zTextAreaField.zType(page.zComposeView.zToField, to);
			obj.zTextAreaField.zType(page.zComposeView.zCcField, cc);
			obj.zTextAreaField.zType(page.zComposeView.zBccField, bcc);
			obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
			obj.zEditor.zType(body);
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			Thread.sleep(2000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			Thread.sleep(1000);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zClick("id=zmi__CLV__Dra__SHOW_ORIG_left_icon");
			Thread.sleep(4000);
			selenium.selectWindow("_blank");
			String showOrigText = selenium.getBodyText();
			Thread.sleep(1000);
			Assert
					.assertFalse(showOrigText.contains("Sender: root@"
							+ config.getString("server")),
							"Show original contains Sender if sender is alias from - Bug 30438");
			verifyShowOriginalMsgBody(showOrigText, "From: root@"
					+ config.getString("server"), "To: admin@"
					+ config.getString("server"), "Cc: ccuser@testdomain.com",
					"Bcc: bcc@" + config.getString("server"), subject, body);
			System.out.println(selenium.getAllWindowTitles());
			if (!config.getString("browser").equals("IE")) {
				selenium.close();
			}
			selenium.selectWindow(null);
			obj.zMessageItem.zClick(subject);
			obj.zButton.zClick("id=zb__CLV__EDIT_left_icon");
			Thread.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zSendIconBtn);
			page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
			obj.zButton.zClick(page.zMailApp.zViewBtn);
			obj.zMenuItem.zClick(localize(locator.byMessage));
			Thread.sleep(1000);
			obj.zMessageItem.zRtClick(subject);
			obj.zMenuItem.zClick(localize(locator.showOrig));
			Thread.sleep(1000);
			selenium.selectWindow("_blank");
			showOrigText = selenium.getBodyText();
			Thread.sleep(1000);
			Assert
					.assertFalse(showOrigText.contains("Sender: root@"
							+ config.getString("server")),
							"Show original contains Sender if sender is alias from - Bug 30438");
			verifyShowOriginalMsgBody(showOrigText, "From: root@"
					+ config.getString("server"), "To: admin@"
					+ config.getString("server"), "Cc: ccuser@testdomain.com",
					"Bcc: bcc@" + config.getString("server"), subject, body);
			if (!config.getString("browser").equals("IE")) {
				selenium.close();
			}
			selenium.selectWindow(null);
			obj.zButton.zClick(localize(locator.view));
			obj.zMenuItem.zClick(localize(locator.byConversation));

			zKillBrowsers();
			String newUser = ProvZCS.getRandomAccount();
			SelNGBase.selfAccountName = newUser;
			page.zLoginpage.zLoginToZimbraAjax(newUser);
		}

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void saveSearch_Bug34872(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		to = SelNGBase.selfAccountName;
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject, body);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		selenium.type("xpath=//input[@class='search_input']", subject);
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zMessageItem.zExists(subject);
		obj.zButton.zClick("id=zb__Search__SAVE_left_icon");
		obj.zEditField.zTypeInDlgByName("id=*nameField", "Srch" + subject,
				localize(locator.saveSearch));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.saveSearch));
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		Thread.sleep(1000);
		String msgExists = obj.zMessageItem.zExistsDontWait(subject);
		assertReport("false", msgExists, "Sent folder doesn't refresh properly");
		obj.zFolder.zClick("Srch" + subject);
		obj.zMessageItem.zClick(subject);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void jsErrorOnClickingDetachIcon_Bug35948(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		obj.zButton.zClick("id=zb__COMPOSE1__DETACH_COMPOSE_left_icon");
		Thread.sleep(2000);
		selenium.selectWindow("_blank");
		obj.zTextAreaField.zType(page.zComposeView.zToField, to);
		obj.zTextAreaField.zType(page.zComposeView.zCcField, cc);
		obj.zTextAreaField.zType(page.zComposeView.zBccField, bcc);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		obj.zEditor.zType(body);
		obj.zButton.zClick(localize(locator.send));
		selenium.selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);

		needReset = false;
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void msgBodyLostWhileChooseNotToAttachSig_Bug40559(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Preferences");
		zGoToPreferences("Signatures");
		page.zSignaturePref.zCreateSignature("testSignature", "signatureBody",
				"text");
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(2000);
		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick("testSignature");
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(1000);
		verifySignatureWithMsgbody(subject);

		zGoToApplication("Preferences");
		zGoToPreferences("Composing");
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(2000);
		verifySignatureWithMsgbody(subject);

		needReset = false;
	}

	/**
	 * Test Case:-Message is automatically
	 * selected(checkMsgStatusOfSelectedMsgWhileMovingOneFolderToOther)
	 * 1.Compose Mail to self 2.Go To Inbox 3.Click on Get Mail until it show up
	 * in Inbox. 4.Verify Msg In Reading Pane. 5.It should disply
	 * "To view a message, click on it" 6.Go To another folder and check for the
	 * same msg "To view a message, click on it" 7. Means in short It should not
	 * display the content of the mail while going from one folder to another.OR
	 * Clicking on folder should NOT open the first message at any time
	 * 
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void dontSelectFirstMsgByDefault_Bug39908_Bug43335(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zFolder.zClick(localize(locator.inbox));
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[@id='zv__CLV__MSG']/table/tbody/tr/td"),
						"To view a message, click on it.Msg does not present");
		String BodyText = selenium
				.getText("xpath=//div[@id='zv__CLV__MSG']/table/tbody/tr/td");
		Assert.assertTrue(BodyText.contains(localize(locator.viewMessage)));
		obj.zFolder.zClick(localize(locator.sent));
		Assert.assertTrue(selenium.getText(
				"xpath=//div[@id='zv__CLV__MSG']/table/tbody/tr/td").contains(
				localize(locator.viewMessage)));

		needReset = false;
	}

	/**
	 * Test Case:User cannot save preferences due to empty signature field Steps
	 * to replicate Login as user to Ajax webclient (as user with no existing
	 * sigs) Click Preferences Click Signatures Click another pref section, eg
	 * Instant messaging Change something and click save
	 * "Signature value is empty. It's required." appears at the top.
	 * Validation(Expected) above msg should not appear while saving empty
	 * signature field
	 * 
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void UserCanAbleToSavePrefWithEmptySignatureField_Bug44607(
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		zGoToApplication("Preferences");
		zGoToPreferences("Signatures");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@class,'ZOptionsHeader ImgPrefsHeader') and contains(text(),'"
										+ localize(locator.signatures) + "')]"),
						"Signatures label is not present");
		zGoToPreferences("Composing");
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.optionsSaved),
				"Pref Save Message shows correctly");
		String[] signatureValueMissingRequired = localize(
				locator.signatureValueMissingRequired).split("'");
		Assert
				.assertFalse(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'z_toast_text') and contains(text(),'"
										+ signatureValueMissingRequired[0]
										+ "')]"),
						"Signature value is empty. It's required. this msg still present");

		needReset = false;
	}

	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void emptyTrash_Bug41209() throws Exception {

		// String[] recipients = { selfAccountName };
		if (isExecutionARetry)
			handleRetry();

		String subject = "Empty Trash Test";
		String body = "This message will be deleted.";

		/**
		 * 1. Send Mail to self. 2. Select it. 3. Delete it. Move to Trash. *
		 */
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(selfAccountName, "", "",
				subject, body, "");

		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(page.zMailApp.zDeleteBtn);
		Thread.sleep(1000);

		/**
		 * 1. Check deleted message is present in Trash. 2. Right click on trash
		 * and select Empty Trash.
		 */
		obj.zFolder.zClick(localize(locator.trash));
		obj.zMessageItem.zExists(subject);
		obj.zFolder.zRtClick(localize(locator.trash));
		obj.zMenuItem.zClick(localize(locator.emptyTrash));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.warningMsg));

		/**
		 * 1. Go To Trash. 2. Check message is deleted from Trash.
		 */
		obj.zFolder.zClick(localize(locator.trash));
		obj.zMessageItem.zNotExists(subject);

		needReset = false;
	}

	/**
	 * bug 425 - Fix line wrapping (when we reply/forward to mail which contains
	 * large body, the body was getting wrapped upto 80 chracters). Written test
	 * for plain text and html mode both
	 */
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void fixLineWrapping_Bug425(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String actual;
		to = SelNGBase.selfAccountName;
		String recipients[] = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		Thread.sleep(1000);
		actual = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertEquals(actual, body, "Verifying message body text");

		obj.zButton.zClick(page.zMailApp.zReplyBtn);
		Thread.sleep(1000);
		actual = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");
		obj.zEditField.zType(page.zComposeView.zSubjectField, "verifyReply");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		MailApp.ClickCheckMailUntilMailShowsUp("verifyReply");
		obj.zMessageItem.zClick("verifyReply");
		Thread.sleep(1000);
		actual = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");

		ProvZCS.modifyAccount(selfAccountName, "zimbraPrefComposeFormat",
				"html");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		obj.zMessageItem.zClick("verifyReply");

		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		Thread.sleep(1000);
		actual = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");
		obj.zTextAreaField.zType(page.zComposeView.zToField, selfAccountName);
		obj.zEditField.zType(page.zComposeView.zSubjectField, "verifyForward");
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		MailApp.ClickCheckMailUntilMailShowsUp("verifyForward");
		obj.zMessageItem.zClick("verifyForward");
		Thread.sleep(1000);
		actual = obj.zMessageItem.zGetCurrentMsgBodyText();
		Assert.assertTrue(actual.indexOf(body) >= 0,
				"Body-field value mismatched");

		needReset = false;
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

	private void verifySignatureWithMsgbody(String subject) throws Exception {
		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		Boolean signatureContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		assertReport("true", signatureContains.toString(),
				"Message body doesn't contain signature if set from account preferences");
		obj.zEditor.zType(subject);
		obj.zButton.zClick(localize(locator.signature));
		obj.zMenuItem.zClick(localize(locator.signatureDoNotAttach));
		signatureContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		assertReport(subject, obj.zEditor.zGetInnerText(""),
				"Message body contain lost after choosing not to attach signature");
		assertReport("false", signatureContains.toString(),
				"Message body signature is not removed after choose not to attach signature");
		obj.zButton.zClick(localize(locator.signature));
		obj.zMenuItem.zClick("testSignature");
		Boolean subjectContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		signatureContains = obj.zEditor.zGetInnerText("").contains(
				"signatureBody");
		assertReport("true", signatureContains.toString(),
				"Message body signature lost after choose to attach signature");
		assertReport("true", subjectContains.toString(),
				"Message body signature lost after choose to attach signature");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
		j = 0;
	}
}