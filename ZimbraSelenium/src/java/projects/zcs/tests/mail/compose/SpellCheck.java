package projects.zcs.tests.mail.compose;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

/**
 * @author Amit Jagtap
 */
@SuppressWarnings("static-access")
public class SpellCheck extends CommonTest {
	private static String DRAFT_NEW_WINDOW_BUTTON = "zb__COMPOSE1__DETACH_COMPOSE_left_icon";

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		return new Object[][] { { "_selfAccountName_", "ccuser@testdomain.com",
				"bccuser@testdomain.com", getLocalizedData(5),
				getLocalizedData(5), "" } };
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
	public void mandatorySpellCheck_And_Send_Bug36365(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"ar,da,de,el,es,fr,he,hi,it,id,ja,ko,nl,pl,pt_BR,ru,sv,tr,zh_CN,zh_HK",
				"na", "na", "Skipped for all charset other than english");

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zCheckbox.zClick(localize(locator.mandatorySpellcheck));
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject,
				"This is test.", attachments);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void spellCheck_For_TextAppt_Bug4345(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"ar,da,de,el,es,fr,he,hi,it,id,ja,ko,nl,pl,pt_BR,ru,sv,tr,zh_CN,zh_HK",
				"na", "na", "Skipped for all charset other than english");

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsText));
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		page.zCalApp.zNavigateToCalendar();
		page.zCalApp.zNavigateToApptCompose();
		page.zCalCompose.zCalendarEnterSimpleDetails(subject, subject,
				SelNGBase.selfAccountName.get(), "onee twoo");
		obj.zButton.zClick(localize(locator.spellCheck));

		obj.zToastAlertMessage.zAlertMsgExists("2 Misspellings",
				"Strings did not match.");
		// obj.zToastAlertMessage.zAlertMsgExists("2 "+localize(locator.
		// misspellings), "Strings did not match.");
		obj.zButton.zClick(page.zCalCompose.zApptSaveBtn);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void spellCheck_forDraft_inNewWindow_Bug5769_And_Bug39130(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		/**
		 * Extra steps added to automate bug 39130
		 */
		page.zMailApp.zNavigateToComposingPreferences();
		obj.zTab.zClick(page.zMailApp.zPreferencesTabIconBtn);
		page.zComposeView.zNavigateToMailCompose();

		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject,
				"onee twoo", attachments);
		obj.zButton.zClick(ComposeView.zSaveDraftsIconBtn);
		obj.zFolder.zClick(localize(locator.drafts));
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(500);
		obj.zButton.zClick(localize(locator.edit));
		SelNGBase.selenium.get().mouseOver(DRAFT_NEW_WINDOW_BUTTON);
		SelNGBase.selenium.get().clickAt(DRAFT_NEW_WINDOW_BUTTON, "");
		SleepUtil.sleep(2000);
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zButton.zClick(localize(locator.spellCheck));
		obj.zToastAlertMessage.zAlertMsgExists("2 Misspellings",
				"Strings did not match.");
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void objError_inSpellcheck_Bug26037(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"ar,da,de,el,es,fr,he,hi,it,id,ja,ko,nl,pl,pt_BR,ru,sv,tr,zh_CN,zh_HK",
				"na", "na", "Skipped for all charset other than english");

		subject = "anoother mostake";
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject,
				"onee twoo", "");
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.reply));
		SleepUtil.sleep(2000);
		SelNGBase.selenium.get().mouseOver(DRAFT_NEW_WINDOW_BUTTON);
		SelNGBase.selenium.get().clickAt(DRAFT_NEW_WINDOW_BUTTON, "");
		SelNGBase.selenium.get().selectWindow("_blank");
		zWaitTillObjectExist("button", localize(locator.spellCheck));
		obj.zButton.zClick(localize(locator.spellCheck));
		SleepUtil.sleep(2000);
		obj.zToastAlertMessage.zAlertMsgExists("6 Misspellings",
				"Strings did not match.");
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void illegalCharacters_and_replyAfterSpellCheck_Bug29432_and_Bug41760(
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"ar,da,de,el,es,fr,he,hi,it,id,ja,ko,nl,pl,pt_BR,ru,sv,tr,zh_CN,zh_HK",
				"na", "na", "Skipped for all charset other than english");

		subject = "anoother mostake";

		page.zMailApp.zNavigateToComposingPreferences();
		obj.zRadioBtn.zClick(localize(locator.composeAsHTML));
		SleepUtil.sleep(500);
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject,
				"onee twoo", "");
		obj.zMessageItem.zClick(subject);
		obj.zButton.zClick(localize(locator.reply));
		obj.zEditor.zType("onee twoo  threee");
		obj.zButton.zClick(localize(locator.spellCheck));
		SleepUtil.sleep(2000);
		SelNGBase.selenium.get().click(
				"xpath=//span[contains(@class,'SpellCheckLink') and contains(text(),'"
						+ localize(locator.checkAgain) + "')]");
		obj.zToastAlertMessage.zAlertMsgExists("3 Misspellings",
				"Strings did not match.");
		obj.zButton.zClick(ComposeView.zSaveDraftsIconBtn);
		obj.zFolder.zClick(localize(locator.drafts));
		obj.zMessageItem.zClick(subject);
		if ((SelNGBase.currentBrowserName.indexOf("MSIE 8") >= 0)) {
			Assert.assertTrue(obj.zMessageItem.zGetCurrentMsgBodyText()
					.contains("onee twoo threee"));
		} else {
			Assert.assertTrue(obj.zMessageItem.zGetCurrentMsgBodyText()
					.contains("onee twoo  threee"));
		}
		SelNGBase.selenium.get().selectWindow(null);

		SelNGBase.needReset.set(false);
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		zLogin();
	}
}