package projects.html.tests.mail;

import java.lang.reflect.Method;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.SleepUtil;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.zcs.clients.ProvZCS;

/**
 * Class file contains mail tag related tests
 * 
 * @author Jitesh Sojitra
 * 
 */
@SuppressWarnings("static-access")
public class MailTagTests extends CommonTest {

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) throws Exception {
		String test = method.getName();
		if (test.equals("applyRemoveTagToMultipleMails")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "applyRemoveTagtest", "", "" } };
		} else if (test.equals("renameTagAndVerifyTaggedUnTaggedMail")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "renameTagtest", "", "" } };
		} else if (test.equals("deleteTagAndVerifyTaggedUnTaggedMail")) {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "deleteTagtest", "", "" } };
		} else {
			return new Object[][] { { ProvZCS.getRandomAccount(),
					"_selfAccountName_", "ccuser@testdomain.com",
					"bccuser@testdomain.com", "testtagmail", "", "" } };
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

	/**
	 * Apply/Remove tag to the mail and verify it
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void applyRemoveTagToMultipleMails(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// apply tag
		String tagName = getLocalizedData_NoSpecialChar();
		zGoToApplication("Mail");
		page.zMailApp.zCreateTag(tagName);
		zGoToApplication("Mail");
		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		String newSubject = "applyRemoveTagToMultipleMails";
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject, body,
				attachments);
		obj.zCheckbox.zClick(subject);
		SleepUtil.sleepSmall();
		obj.zCheckbox.zClick(newSubject);
		page.zMailApp.zMoreActions(tagName);
		obj.zMessageItem.zVerifyIsTagged(subject);
		obj.zMessageItem.zVerifyIsTagged(newSubject);

		// remove tag
		obj.zCheckbox.zClick(subject);
		SleepUtil.sleepSmall();
		obj.zCheckbox.zClick(newSubject);
		obj.zHtmlMenu.zClick("name=actionOp", tagName, "2");
		SleepUtil.sleepSmall();
		obj.zMessageItem.zVerifyIsNotTagged(subject);
		obj.zMessageItem.zVerifyIsNotTagged(newSubject);

		needReset = false;
	}

	/**
	 * 1. Apply tag to 2 mail, don't apply tag to 3rd mail. Click on Tag folder
	 * and verify only 2 search result comes
	 * 
	 * 2. Rename tag and check the same result
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void renameTagAndVerifyTaggedUnTaggedMail(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// apply tag
		String tagName = getLocalizedData_NoSpecialChar();
		zGoToApplication("Mail");
		page.zMailApp.zCreateTag(tagName);
		zGoToApplication("Mail");
		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		String newSubject = "renameTagAndVerifyTaggedUnTaggedMail1";
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject, body,
				attachments);
		String newSubject1 = "renameTagAndVerifyTaggedUnTaggedMail2";
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject1, body,
				attachments);
		obj.zCheckbox.zClick(subject);
		obj.zCheckbox.zClick(newSubject);
		SleepUtil.sleepSmall();
		page.zMailApp.zMoreActions(tagName);
		obj.zFolder.zClick(tagName);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zExists(newSubject);
		obj.zMessageItem.zNotExists(newSubject1);

		// rename tag should still show same result
		String newTagName = "1" + getLocalizedData_NoSpecialChar();
		page.zMailApp.zRenameTag(tagName, newTagName);
		zGoToApplication("Mail");
		obj.zFolder.zClick(newTagName);
		SleepUtil.sleepSmall();
		obj.zMessageItem.zExists(subject);
		obj.zMessageItem.zExists(newSubject);
		obj.zMessageItem.zNotExists(newSubject1);
		obj.zMessageItem.zClick(page.zMailApp.zSelectAllMailChkBox);
		SleepUtil.sleepSmall();
		page.zMailApp.zMoreActions("all");
		SleepUtil.sleepSmall();

		needReset = false;
	}

	/**
	 * 1. Apply tag to 2 mail, don't apply tag to 3rd mail. Click on Tag folder
	 * and verify only 2 search result comes
	 * 
	 * 2. Rename tag and check the same result
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteTagAndVerifyTaggedUnTaggedMail(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// apply tag
		String tagName = "0" + getLocalizedData_NoSpecialChar();
		zGoToApplication("Mail");
		page.zMailApp.zCreateTag(tagName);
		zGoToApplication("Mail");
		page.zMailApp.zInjectMessage(from, to, cc, bcc, subject, body,
				attachments);
		String newSubject = "deleteTagAndVerifyTaggedUnTaggedMail1";
		page.zMailApp.zInjectMessage(from, to, cc, bcc, newSubject, body,
				attachments);
		obj.zCheckbox.zClick(subject);
		page.zMailApp.zMoreActions(tagName);

		// delete tag
		page.zMailApp.zDeleteTag(tagName);
		zGoToApplication("Mail");
		obj.zFolder.zNotExists(tagName);
		obj.zMessageItem.zVerifyIsNotTagged(subject);
		obj.zMessageItem.zVerifyIsNotTagged(newSubject);

		needReset = false;
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}
}
