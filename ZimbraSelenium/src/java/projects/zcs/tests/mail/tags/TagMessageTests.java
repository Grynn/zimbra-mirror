package projects.zcs.tests.mail.tags;

import java.lang.reflect.Method;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class TagMessageTests extends CommonTest {
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "tagDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("createRenameDeleteTagForMessageAndVerify")
				|| test.equals("verifyTagFunctionalityFor2MessageAndRemoveTag")
				|| test.equals("applyMutlipleTagToMessageAndVerify")
				|| test.equals("applyTagByDnDTagToMessageAndViceVersa")
				|| test.equals("tryToCreateDuplicateTagInMail")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(), "ccuser@testdomain.com",
					"bccuser@testdomain.com", "tagMessageSubject",
					"tagMessageBody", "" } };
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
		zGoToApplication("Mail");
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

	/**
	 * Verify create, rename & delete functionality for tag for message
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createRenameDeleteTagForMessageAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject1, tag1, newTag1;
		subject1 = "tagSubject1";
		tag1 = getLocalizedData_NoSpecialChar();
		newTag1 = getLocalizedData_NoSpecialChar();
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject1, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject1);
		zCreateTag(tag1);
		obj.zMessageItem.zClick(subject1);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zVerifyIsTagged(subject1);
		SleepUtil.sleep(1000);

		zRenameTag(tag1, newTag1);
		obj.zFolder.zNotExists(tag1);
		obj.zFolder.zClick(newTag1);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zVerifyIsTagged(subject1);

		zDeleteTag(newTag1);
		obj.zMessageItem.zClick(subject1);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Create 2 tag, apply 1 tag to each message and verify message exist / not
	 * exist by clicking to tag, Inbox and Sent folder
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyTagFunctionalityFor2MessageAndRemoveTag(String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject1, subject2, tag1, tag2;
		subject1 = "tagSubject2";
		subject2 = "tagSubject3";
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject1, body);
		ProvZCS.injectMessage(to, recipients, cc, subject2, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject1);
		MailApp.ClickCheckMailUntilMailShowsUp(subject2);
		zCreateTag(tag1);
		obj.zMessageItem.zClick(subject1);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zVerifyIsTagged(subject1);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zClick(subject2);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zMessageItem.zVerifyIsTagged(subject2);
		obj.zFolder.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zExists(subject1);
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject2),
				"Verify message2 not exists");
		obj.zFolder.zClick(tag2);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zExists(subject2);
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject1),
				"Verify message1 not exists");
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		SleepUtil.sleep(1000);
		assertReport("true", obj.zMessageItem.zExistsDontWait(subject1),
				"Verify message1 not exists");
		assertReport("true", obj.zMessageItem.zExistsDontWait(subject2),
				"Verify message2 not exists");
		obj.zFolder.zClick(page.zMailApp.zSentFldr);
		SleepUtil.sleep(1000);
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject1),
				"Verify message1 not exists");
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject2),
				"Verify message2 not exists");

		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zClick(subject2);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zClick(localize(locator.removeTag));
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zIsEnabled(localize(locator.newTag));
		obj.zMenuItem.zIsDisabled(localize(locator.removeTag));

		SelNGBase.needReset.set(false);
	}

	/**
	 * Create 2 tag, apply both tag to message and verify both message exists
	 * after clicking to tag
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyMutlipleTagToMessageAndVerify(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject1, tag1, tag2;
		subject1 = "tagSubject4";
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject1, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject1);
		obj.zMessageItem.zClick(subject1);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag1);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zMessageItem.zVerifyIsTagged(subject1);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), tag2);
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(1000);
		obj.zMessageItem.zVerifyIsTagged(subject1);
		obj.zButton.zClick(page.zMailApp.zTagIconBtn);
		obj.zMenuItem.zNotExists(tag1);
		obj.zMenuItem.zNotExists(tag2);
		obj.zFolder.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zExists(subject1);
		obj.zFolder.zClick(tag2);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zExists(subject1);

		SelNGBase.needReset.set(false);
	}

	/**
	 * Verify drag n drop functionality for tag and message. Drag message to tag
	 * and verify tag applied & same way drag tag to message and verify tag
	 * applied
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyTagByDnDTagToMessageAndViceVersa(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String subject1, subject2, tag1, tag2;
		subject1 = "tagSubject5";
		subject2 = "tagSubject6";
		tag1 = getLocalizedData_NoSpecialChar();
		tag2 = getLocalizedData_NoSpecialChar();
		to = SelNGBase.selfAccountName.get();
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, cc, subject1, body);
		ProvZCS.injectMessage(to, recipients, cc, subject2, body);
		MailApp.ClickCheckMailUntilMailShowsUp(subject1);
		MailApp.ClickCheckMailUntilMailShowsUp(subject2);
		zCreateTag(tag1);
		zCreateTag(tag2);
		zDragAndDrop("//td[contains(@id, 'zlif__CLV__') and contains(text(), '"
				+ subject1 + "')]",
				"//td[contains(@id, 'zti__main_Mail') and contains(text(), '"
						+ tag1 + "')]");
		obj.zMessageItem.zVerifyIsTagged(subject1);
		SleepUtil.sleep(1000);
		obj.zFolder.zClick(page.zMailApp.zTrashFldr);
		SleepUtil.sleep(1000);
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject1),
				"Verify message1 not exists");
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject2),
				"Verify message2 not exists");
		obj.zFolder.zClick(tag1);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zExists(subject1);

		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		zDragAndDrop(
				"//td[contains(@id, 'zti__main_Mail') and contains(text(), '"
						+ tag2 + "')]",
				"//td[contains(@id, 'zlif__CLV__') and contains(text(), '"
						+ subject2 + "')]");
		obj.zMessageItem.zVerifyIsTagged(subject2);
		SleepUtil.sleep(1000);
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		SleepUtil.sleep(1000);
		obj.zFolder.zClick(tag2);
		SleepUtil.sleep(1000);
		obj.zMessageItem.zExists(subject2);
		assertReport("false", obj.zMessageItem.zExistsDontWait(subject1),
				"Verify message1 not exists");

		SelNGBase.needReset.set(false);
	}

	/**
	 * Try to create duplicate tag and verify its not allowed
	 */
	@Test(dataProvider = "tagDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void tryToCreateDuplicateTagInMail(String to, String cc, String bcc,
			String subject, String body, String attachments) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		String tag1;
		tag1 = getLocalizedData_NoSpecialChar();
		zCreateTag(tag1);
		zDuplicateTag(tag1);

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