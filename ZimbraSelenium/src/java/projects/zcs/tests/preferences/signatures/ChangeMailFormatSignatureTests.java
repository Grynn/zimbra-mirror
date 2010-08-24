package projects.zcs.tests.preferences.signatures;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.html.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 * 
 */

@SuppressWarnings("static-access")
public class ChangeMailFormatSignatureTests extends CommonTest {
	public static String bodyVal;
	public static String rplyFwdSubject = "rplyFwdSubject";

	@DataProvider(name = "SigPrefDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("newMail_AttachDontAttachSignatureAndChangeBodyFormat")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							"_selfAccountName_", "", "", getLocalizedData(2),
							getLocalizedData(5), "", "text" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							"_selfAccountName_", "", "", getLocalizedData(2),
							getLocalizedData(5), "", "html" } };
		} else if (test
				.equals("draft_AttachDontAttachSignatureAndChangeBodyFormat")) {
			return new Object[][] {
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							"_selfAccountName_", "", "", getLocalizedData(2),
							getLocalizedData(5), "", "text" },
					{ getLocalizedData_NoSpecialChar(),
							getLocalizedData_NoSpecialChar(),
							"_selfAccountName_", "", "", getLocalizedData(2),
							getLocalizedData(5), "", "html" } };
		} else if (test
				.equals("rply_AttachDontAttachSignatureAndChangeBodyFormat")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "_selfAccountName_", "",
					"", getLocalizedData(2), getLocalizedData(5), "", "text" } };
		} else if (test
				.equals("fwd_AttachDontAttachSignatureAndChangeBodyFormat")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "_selfAccountName_", "",
					"", getLocalizedData(2), getLocalizedData(5), "", "html" } };
		} else
			return new Object[][] { {} };
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SelNGBase.isExecutionARetry.set(false);
	}

	@SuppressWarnings("unused")
	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}

	// Tests
	/**
	 * Test verifies signature by changing mail format (html/plain text) & also
	 * verifies body by attaching/removing signature in composed mail. It
	 * verifies all these steps for html and plain text signature both.
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void newMail_AttachDontAttachSignatureAndChangeBodyFormat(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments,
			String signatureType) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// format as text
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(), "zimbraPrefComposeFormat",
				"text");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				signatureType);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		// setting default signature
		setDefaultSignature(signatureName);
		// verifying signature
		verifySignature(signatureBody, "", "");
		// don't attach signature and verify mail body
		dontAttachSignatureAndVerifyMailBody(signatureBody, "", "");
		// changing mail format as html and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsHtml),
				signatureBody, "", "", "", "");
		// attach signature back and verify mail body
		attachSignatureAndVerifyMailBody(signatureName, signatureBody, "", "");
		// changing mail format as text and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsText),
				signatureBody, "true", "true", "", "");
		cancelMailCompose();

		// change compose format as html
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(), "zimbraPrefComposeFormat",
				"html");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		// keeping mail format as html and verifying signature
		verifySignature(signatureBody, "", "");
		// don't attach signature and verify mail body
		dontAttachSignatureAndVerifyMailBody(signatureBody, "", "");
		// changing mail format as text and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsText),
				signatureBody, "true", "", "", "");
		// attach signature back and verify mail body
		attachSignatureAndVerifyMailBody(signatureName, signatureBody, "", "");
		// changing mail format as html and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsHtml),
				signatureBody, "true", "true", "", "");
		cancelMailCompose();

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test verifies signature by changing mail format (html/plain text) & also
	 * verifies body by attaching/removing signature for draft mail. It verifies
	 * all these steps for html and plain text signature both.
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void draft_AttachDontAttachSignatureAndChangeBodyFormat(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments,
			String signatureType) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// format as text
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(), "zimbraPrefComposeFormat",
				"text");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				signatureType);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		// setting default signature
		setDefaultSignature(signatureName);
		// verifying signature
		verifySignature(signatureBody, "true", "");
		// don't attach signature and verify mail body
		dontAttachSignatureAndVerifyMailBody(signatureBody, "true", "");
		// changing mail format as html and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsHtml),
				signatureBody, "true", "", "true", "");
		// attach signature back and verify mail body
		attachSignatureAndVerifyMailBody(signatureName, signatureBody, "true",
				"");
		// changing mail format as text and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsText),
				signatureBody, "", "true", "true", "");
		obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		// change compose format as html
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(), "zimbraPrefComposeFormat",
				"html");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		// keeping mail format as html and verifying signature
		verifySignature(signatureBody, "true", "");
		// don't attach signature and verify mail body
		dontAttachSignatureAndVerifyMailBody(signatureBody, "true", "");
		// changing mail format as text and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsText),
				signatureBody, "true", "", "true", "");
		// attach signature back and verify mail body
		attachSignatureAndVerifyMailBody(signatureName, signatureBody, "true",
				"");
		// changing mail format as html and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsHtml),
				signatureBody, "true", "true", "true", "");
		obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test verifies signature by changing mail format (html/plain text) & also
	 * verifies body by attaching/removing signature for replied draft mail.
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rply_AttachDontAttachSignatureAndChangeBodyFormat(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments,
			String signatureType) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// format as text
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(), "zimbraPrefComposeFormat",
				"text");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				signatureType);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		// setting default signature
		setDefaultSignature(signatureName);
		// verifying signature
		verifySignature(signatureBody, "", "true");
		// don't attach signature and verify mail body
		dontAttachSignatureAndVerifyMailBody(signatureBody, "", "true");
		// changing mail format as html and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsHtml),
				signatureBody, "true", "", "", "true");
		// attach signature back and verify mail body
		attachSignatureAndVerifyMailBody(signatureName, signatureBody, "",
				"true");
		// changing mail format as text and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsText),
				signatureBody, "", "true", "", "true");
		obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Test verifies signature by changing mail format (html/plain text) & also
	 * verifies body by attaching/removing signature for forwarded draft mail.
	 */
	@Test(dataProvider = "SigPrefDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void fwd_AttachDontAttachSignatureAndChangeBodyFormat(
			String signatureName, String signatureBody, String to, String cc,
			String bcc, String subject, String body, String attachments,
			String signatureType) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		// change compose format as html
		ProvZCS.modifyAccount(SelNGBase.selfAccountName.get(), "zimbraPrefComposeFormat",
				"html");
		SelNGBase.selenium.get().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zSignaturePref.zNavigateToPreferenceSignature();
		page.zSignaturePref.zCreateSignature(signatureName, signatureBody,
				signatureType);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
		// setting default signature
		setDefaultSignature(signatureName);
		// verifying signature
		verifySignature(signatureBody, "", "true");
		// don't attach signature and verify mail body
		dontAttachSignatureAndVerifyMailBody(signatureBody, "", "true");
		// changing mail format as text and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsText),
				signatureBody, "true", "", "", "true");
		// attach signature back and verify mail body
		attachSignatureAndVerifyMailBody(signatureName, signatureBody, "",
				"true");
		// changing mail format as html and verifying signature
		changeFormatAndVerifySignature(localize(locator.formatAsHtml),
				signatureBody, "true", "true", "", "true");
		obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		SelNGBase.needReset.set(false);
	}

	private static void setDefaultSignature(String signatureName)
			throws Exception {
		zGoToApplication("Preferences");
		zGoToPreferences("Accounts");
		obj.zButton.zClick(localize(locator.signatureDoNotAttach));
		obj.zMenuItem.zClick(signatureName);
		obj.zButton.zClick(page.zABCompose.zPreferencesSaveIconBtn);
		SleepUtil.sleep(1000);
	}

	private static void verifySignature(String signatureBody, String isDraft,
			String isRplyFwd) throws Exception {
		if (isDraft.equals("true")) {
			page.zComposeView.zNavigateToMailCompose();
			obj.zEditField
					.zType(page.zComposeView.zSubjectField, signatureBody);
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			obj.zMessageItem.zClick(signatureBody);
			obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
			SleepUtil.sleep(1000);
		} else if (isRplyFwd.equals("true")) {
			String to;
			to = SelNGBase.selfAccountName.get();
			String[] recipients = { to };
			ProvZCS.injectMessage(to, recipients, "ccuser@testdomain.com",
					rplyFwdSubject, "");
			MailApp.ClickCheckMailUntilMailShowsUp(rplyFwdSubject);
			obj.zMessageItem.zClick(rplyFwdSubject);
			obj.zButton.zClick(page.zMailApp.zReplyBtn);
		} else {
			page.zComposeView.zNavigateToMailCompose();
		}
		SleepUtil.sleep(1500);
		bodyVal = obj.zEditor.zGetInnerText("");
		Assert.assertTrue(bodyVal.indexOf(signatureBody) >= 0,
				"On setting signature, body-field isnt getting filled");
	}

	private static void dontAttachSignatureAndVerifyMailBody(
			String signatureBody, String isDraft, String isRplyFwd)
			throws Exception {
		if (isDraft.equals("true")) {
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			obj.zMessageItem.zClick(signatureBody);
			obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
			SleepUtil.sleep(1000);
		} else if (isRplyFwd.equals("true")) {
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			obj.zMessageItem.zClick(rplyFwdSubject);
			obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
			SleepUtil.sleep(1000);
		}
		obj.zButton.zClick(localize(locator.signature));
		obj.zMenuItem.zClick(localize(locator.signatureDoNotAttach));
		SleepUtil.sleep(1500);
		bodyVal = obj.zEditor.zGetInnerText("");
		Assert.assertFalse(bodyVal.indexOf(signatureBody) >= 0,
				"On setting signature, body-field getting filled");
	}

	private static void changeFormatAndVerifySignature(String format,
			String signatureBody, String isWarnigDlgExists,
			String verifyMailBody, String isDraft, String isRplyFwd)
			throws Exception {
		if (isDraft.equals("true")) {
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			obj.zMessageItem.zClick(signatureBody);
			obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
			SleepUtil.sleep(1000);
		} else if (isRplyFwd.equals("true")) {
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			obj.zMessageItem.zClick(rplyFwdSubject);
			obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
			SleepUtil.sleep(1000);
		}
		obj.zButton.zClick(localize(locator.options));
		obj.zMenuItem.zClick(format);
		SleepUtil.sleep(1000);
		if (isWarnigDlgExists.equals("true")) {
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.warningMsg));
			SleepUtil.sleep(1000);
		}
		bodyVal = obj.zEditor.zGetInnerText("");
		SleepUtil.sleep(1000);
		if (verifyMailBody.equals("true")) {
			Assert.assertTrue(bodyVal.indexOf(signatureBody) >= 0,
					"On setting signature, body-field isnt getting filled");
		} else {
			Assert.assertFalse(bodyVal.indexOf(signatureBody) >= 0,
					"On setting signature, body-field getting filled");
		}
	}

	private static void attachSignatureAndVerifyMailBody(String signatureName,
			String signatureBody, String isDraft, String isRplyFwd)
			throws Exception {
		if (isDraft.equals("true")) {
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			obj.zMessageItem.zClick(signatureBody);
			obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
			SleepUtil.sleep(1000);
		} else if (isRplyFwd.equals("true")) {
			obj.zButton.zClick(page.zComposeView.zSaveDraftsIconBtn);
			SleepUtil.sleep(1000);
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
			obj.zFolder.zClick(page.zMailApp.zDraftsFldr);
			obj.zMessageItem.zClick(rplyFwdSubject);
			obj.zButton.zClick(page.zMailApp.zEditDraftIconBtn);
			SleepUtil.sleep(1000);
		}
		obj.zButton.zClick(localize(locator.signature));
		obj.zMenuItem.zClick(signatureName);
		bodyVal = obj.zEditor.zGetInnerText("");
		SleepUtil.sleep(1000);
		Assert.assertTrue(bodyVal.indexOf(signatureBody) >= 0,
				"On setting signature, body-field isnt getting filled");
	}

	private static void cancelMailCompose() throws Exception {
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
	}

	private void handleRetry() throws Exception {
		// TODO Auto-generated method stub
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}

}
