package projects.zcs.tests.preferences.filters;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 * 
 *         Below parameter used to pass values from data provider
 * 
 *         filterName, activeStatus, conditionType, condition11, condition12,
 *         conditionValue1, condition21, condition22, conditionValue2,
 *         condition31, condition32, conditionValue3, action1, action2, action3,
 *         additionalFilterIfAny
 */
@SuppressWarnings( { "static-access" })
public class FilterTests extends CommonTest {

	protected String fileIntoNewFolder, forwardtoUser, randomAccount1,
			randomAccount2;
	protected String dialogName = localize(locator.addFilter);

	public static String zRunFilterButton = "id=zb__FRV__RUN_FILTER_RULE_left_icon";
	public static String zInboxChkBoxInChooseFolderDlg = "id=zti__ZmFilterRulesController__2_checkbox";
	public static String zSentChkBoxInChooseFolderDlg = "id=zti__ZmFilterRulesController__5_checkbox";
	public static String zJunkChkBoxInChooseFolderDlg = "id=zti__ZmFilterRulesController__4_checkbox";
	public static String zTrashChkBoxInChooseFolderDlg = "id=zti__ZmFilterRulesController__3_checkbox";

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "FilterDataProvider")
	protected Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("FromContains_FileToFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.from),
					localize(locator.contains), SelNGBase.selfAccountName.get(), "",
					"", "", "", "", "", localize(locator.fileIntoFolder), "",
					"", localize(locator.stopFilterProcessing) } };
		} else if (test.equals("SubjectContains_FileToFolder")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.subject),
					localize(locator.contains),
					getLocalizedData_NoSpecialChar(), "", "", "", "", "", "",
					localize(locator.fileIntoFolder), "", "",
					localize(locator.stopFilterProcessing) } };
		} else if (test.equals("SubjectContains_ForwardTo_And_Bug41693")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.subject),
					localize(locator.contains),
					getLocalizedData_NoSpecialChar(), "", "", "", "", "", "",
					localize(locator.forwardToAddress), "", "",
					localize(locator.stopFilterProcessing) } };
		} else if (test.equals("FromContains_Discard")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.from),
					localize(locator.contains), SelNGBase.selfAccountName.get(), "",
					"", "", "", "", "", localize(locator.discard), "", "",
					localize(locator.stopFilterProcessing) } };
		} else if (test.equals("CCContains_Discard")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.cc),
					localize(locator.contains), SelNGBase.selfAccountName.get(), "",
					"", "", "", "", "", localize(locator.discard), "", "",
					localize(locator.stopFilterProcessing) } };
		} else if (test.equals("SubjectContains_FileToFolder_PriorityTest")) {
			return new Object[][] { { "1" + getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.subject),
					localize(locator.contains),
					getLocalizedData_NoSpecialChar(), "", "", "", "", "", "",
					localize(locator.fileIntoFolder), "", "",
					localize(locator.stopFilterProcessing) } };
		} else if (test.equals("RunFilter")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.cc),
					localize(locator.contains), SelNGBase.selfAccountName.get(), "",
					"", "", "", "", "", localize(locator.discard), "", "",
					localize(locator.stopFilterProcessing) } };
		} else if (test.equals("MsgBodyShouldNotBeCaseSensitive_Bug36905")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.body),
					localize(locator.contains),
					"MsgBodyShouldNotBeCaseSensitive_Bug36905", "", "", "", "",
					"", "", localize(locator.fileIntoFolder), "", "",
					localize(locator.stopFilterProcessing) } };
		} else if (test.equals("SubjectDoesntContains_FileToFolder_Bug42149")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.subject),
					localize(locator.notContain),
					getLocalizedData_NoSpecialChar(), "", "", "", "", "", "",
					localize(locator.fileIntoFolder), "", "",
					localize(locator.stopFilterProcessing) } };
		} else {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					localize(locator.active), "any", localize(locator.from),
					localize(locator.contains), "subjectcontains_runfilter",
					"", "", "", "", "", "", localize(locator.fileIntoFolder),
					"", "", localize(locator.stopFilterProcessing) } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Preferences");
		obj.zTab.zClick(localize(locator.filterRules));
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
	 * This test creates filter by setting from user as condition and file into
	 * new folder as action. It verifies created folder in mail & filing mail
	 * into that folder according to user set by filter.
	 */
	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void FromContains_FileToFolder(String filterName,
			String activeStatus, String conditionType, String condition11,
			String condition12, String conditionValue1, String condition21,
			String condition22, String conditionValue2, String condition31,
			String condition32, String conditionValue3, String action1,
			String action2, String action3, String additionalFilterIfAny)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zGoToApplication("Preferences");
		obj.zTab.zClick(localize(locator.filterRules));
		setFilterConditionsActions(filterName, activeStatus, conditionType,
				condition11, condition12, conditionValue1, condition21,
				condition22, conditionValue2, condition31, condition32,
				conditionValue3, action1, action2, action3,
				additionalFilterIfAny);

		// Go to mail application tab and verify created folder
		zGoToApplication("Mail");
		obj.zFolder.zExists(fileIntoNewFolder);

		// Send mail and verify filter
		String body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zComposeAndSendMail(conditionValue1, "", "",
				conditionValue1, body, "");
		MailApp.ClickCheckMailUntilMailShowsUp(fileIntoNewFolder,
				conditionValue1);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates filter by setting subject as condition and file into
	 * new folder as action. It verifies created folder in mail & filing mail
	 * into that folder according to specific subject set by filter.
	 */
	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void SubjectContains_FileToFolder(String filterName,
			String activeStatus, String conditionType, String condition11,
			String condition12, String conditionValue1, String condition21,
			String condition22, String conditionValue2, String condition31,
			String condition32, String conditionValue3, String action1,
			String action2, String action3, String additionalFilterIfAny)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		setFilterConditionsActions(filterName, activeStatus, conditionType,
				condition11, condition12, conditionValue1, condition21,
				condition22, conditionValue2, condition31, condition32,
				conditionValue3, action1, action2, action3,
				additionalFilterIfAny);

		// Go to mail application tab and verify created folder
		zGoToApplication("Mail");

		// Send mail and verify filter
		String body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zComposeAndSendMail(SelNGBase.selfAccountName.get(), "",
				"", conditionValue1, body, "");
		MailApp.ClickCheckMailUntilMailShowsUp(fileIntoNewFolder,
				conditionValue1);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates filter by setting subject as condition and forward mail
	 * to particular user as action. It verifies forwarded mail by logging to
	 * that user according to set filter.
	 */
	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void SubjectContains_ForwardTo_And_Bug41693(String filterName,
			String activeStatus, String conditionType, String condition11,
			String condition12, String conditionValue1, String condition21,
			String condition22, String conditionValue2, String condition31,
			String condition32, String conditionValue3, String action1,
			String action2, String action3, String additionalFilterIfAny)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("en_AU")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("en_GB")) {
			setFilterConditionsActions(filterName, activeStatus, conditionType,
					condition11, condition12, conditionValue1, condition21,
					condition22, conditionValue2, condition31, condition32,
					conditionValue3, action1, action2, action3,
					additionalFilterIfAny);

			zGoToApplication("Preferences");
			zGoToPreferences("Filters");
			obj.zButton.zClick(zRunFilterButton);
			SleepUtil.sleep(2000);
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.infoMsg));
			obj.zButton.zClickInDlgByName(localize(locator.cancel),
					localize(locator.chooseFolder));

			/*
			 * Go to mail application tab and send mail to that user to verify
			 * forward filter
			 */
			zGoToApplication("Mail");
			String body = getLocalizedData_NoSpecialChar();
			page.zComposeView.zComposeAndSendMail(SelNGBase.selfAccountName.get(),
					"", "", conditionValue1, body, "");

			// log off, login to that user to whom forwarded mail & verify mail
			resetSession();
			SelNGBase.selfAccountName.set(forwardtoUser);
			page.zLoginpage.zLoginToZimbraAjax(forwardtoUser);
			MailApp.ClickCheckMailUntilMailShowsUp(conditionValue1);
		}

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates filter by specifying from contain as condition and
	 * discarding action. It verifies whether mail has discarded or not.
	 */
	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void FromContains_Discard(String filterName, String activeStatus,
			String conditionType, String condition11, String condition12,
			String conditionValue1, String condition21, String condition22,
			String conditionValue2, String condition31, String condition32,
			String conditionValue3, String action1, String action2,
			String action3, String additionalFilterIfAny) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		setFilterConditionsActions(filterName, activeStatus, conditionType,
				condition11, condition12, conditionValue1, condition21,
				condition22, conditionValue2, condition31, condition32,
				conditionValue3, action1, action2, action3,
				additionalFilterIfAny);

		// Go to mail application tab and verify created folder
		zGoToApplication("Mail");

		// To user as discard
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zComposeAndSendMail(conditionValue1, "", "", subject,
				body, "");
		obj.zButton.zClick(page.zMailApp.zGetMailIconBtn);
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates filter by specifying cc contain as condition and
	 * discarding action. It verifies whether mail has discarded or not.
	 */
	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void CCContains_Discard(String filterName, String activeStatus,
			String conditionType, String condition11, String condition12,
			String conditionValue1, String condition21, String condition22,
			String conditionValue2, String condition31, String condition32,
			String conditionValue3, String action1, String action2,
			String action3, String additionalFilterIfAny) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		setFilterConditionsActions(filterName, activeStatus, conditionType,
				condition11, condition12, conditionValue1, condition21,
				condition22, conditionValue2, condition31, condition32,
				conditionValue3, action1, action2, action3,
				additionalFilterIfAny);

		// Go to mail application tab and verify created folder
		zGoToApplication("Mail");

		// Cc user as discard
		String to = ProvZCS.getRandomAccount();
		String subject = getLocalizedData_NoSpecialChar();
		String body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zComposeAndSendMail(to, SelNGBase.selfAccountName.get(),
				"", subject, body, "");
		obj.zButton.zClick(page.zMailApp.zGetMailIconBtn);
		obj.zFolder.zClick(localize(locator.inbox));
		obj.zMessageItem.zNotExists(subject);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates filter by setting from user as condition and file into
	 * new folder as action. It verifies created folder in mail & filing mail
	 * into that folder according to user set by filter.
	 */
	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void SubjectContains_FileToFolder_PriorityTest(String filterName,
			String activeStatus, String conditionType, String condition11,
			String condition12, String conditionValue1, String condition21,
			String condition22, String conditionValue2, String condition31,
			String condition32, String conditionValue3, String action1,
			String action2, String action3, String additionalFilterIfAny)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		setFilterConditionsActions(filterName, activeStatus, conditionType,
				condition11, condition12, conditionValue1, condition21,
				condition22, conditionValue2, condition31, condition32,
				conditionValue3, action1, action2, action3,
				additionalFilterIfAny);

		// Go to preferences
		String FolderName = fileIntoNewFolder;
		zGoToApplication("Preferences");
		obj.zTab.zClick(localize(locator.filterRules));

		String newFilterName = "2" + getLocalizedData_NoSpecialChar();
		setFilterConditionsActions(newFilterName, activeStatus, conditionType,
				localize(locator.subject), localize(locator.contains),
				conditionValue1, condition21, condition22, conditionValue2,
				condition31, condition32, conditionValue3, action1, action2,
				action3, additionalFilterIfAny);

		// Go to preferences and set priority
		String newFolderName = fileIntoNewFolder;
		zGoToApplication("preferences");
		obj.zListItem.zClick(filterName);
		for (int i = 0; i < 3; i++) {
			obj.zButton.zClick(localize(locator.filterMoveUp));
			SleepUtil.sleep(500);
		}

		/*
		 * 1.Go to mail and verify both folder exists 2.Send mail and verify
		 * filter according to priority - mail should file in the correct folder
		 */
		zGoToApplication("mail");
		obj.zFolder.zExists(FolderName);
		obj.zFolder.zExists(newFolderName);
		page.zComposeView.zComposeAndSendMail(SelNGBase.selfAccountName.get(), "",
				"", conditionValue1, getLocalizedData_NoSpecialChar(), "");
		MailApp.ClickCheckMailUntilMailShowsUp(FolderName, conditionValue1);
		obj.zButton.zClick(page.zMailApp.zGetMailIconBtn);
		obj.zFolder.zClick(newFolderName);
		SleepUtil.sleep(1500);
		obj.zMessageItem.zNotExists(conditionValue1);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void RunFilter(String filterName, String activeStatus,
			String conditionType, String condition11, String condition12,
			String conditionValue1, String condition21, String condition22,
			String conditionValue2, String condition31, String condition32,
			String conditionValue3, String action1, String action2,
			String action3, String additionalFilterIfAny) throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		checkForSkipException("na", "IE", "na", "not able to mark ON to inbox folder using zactivate method in IE (selenium bug)");


		/* Preparing for test data to run filter */
		zGoToApplication("Mail");
		randomAccount1 = ProvZCS.getRandomAccount();
		randomAccount2 = ProvZCS.getRandomAccount();

		// 1. subject contains(subjectcontains_filetofolder) file to
		// folder
		String to = SelNGBase.selfAccountName.get();
		String subject1 = "subjectcontains_filetofolder";
		String body1 = "body_subjectcontains_filetofolder";
		String[] recipients = { to };
		ProvZCS.injectMessage(to, recipients, "ccuser@testdomain.com",
				subject1, body1);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject1);

		// 2. from contains (randomaccount1) file to folder
		String subject2 = "fromcontains_filetofolder";
		String body2 = "body_fromcontains_filetofolder";
		ProvZCS.injectMessage(randomAccount1, recipients,
				"ccuser@testdomain.com", subject2, body2);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject2);

		// 3. to contains(selfaccount) and subject
		// contains(subjectandtocontains_filetofolder) file to folder
		String subject3 = "subjectandtocontains_filetofolder";
		String body3 = "body_subjectandtocontains_filetofolder";
		ProvZCS.injectMessage(to, recipients, "ccuser@testdomain.com",
				subject3, body3);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject3);

		// 4. subject contains(subjectcontains_discard) discard
		String subject4 = "subjectcontains_discard";
		String body4 = "body_subjectcontains_discard";
		ProvZCS.injectMessage(to, recipients, "ccuser@testdomain.com",
				subject4, body4);
		MailApp.ClickCheckMailUntilMailShowsUp(
				replaceUserNameInStaticId(page.zMailApp.zInboxFldr), subject4);

		/* Creating filter according to test data */
		// 1. subject contains(subjectcontains_filetofolder) file to
		// folder
		setFilterConditionsActions(subject1, localize(locator.active), "any",
				localize(locator.subject), localize(locator.contains),
				"subjectcontains_filetofolder", "", "", "", "", "", "",
				localize(locator.fileIntoFolder), "", "",
				localize(locator.stopFilterProcessing));
		String folder1 = fileIntoNewFolder;

		// 2. from contains (randomaccount1) file to folder
		setFilterConditionsActions(subject2, localize(locator.active), "any",
				localize(locator.from), localize(locator.contains),
				randomAccount1, "", "", "", "", "", "",
				localize(locator.fileIntoFolder), "", "",
				localize(locator.stopFilterProcessing));
		String folder2 = fileIntoNewFolder;

		// 3. to contains(selfaccount) file to folder
		setFilterConditionsActions(subject3, localize(locator.active), "all",
				localize(locator.subject), localize(locator.contains),
				"subjectandtocontains_filetofolder", localize(locator.to),
				localize(locator.contains), SelNGBase.selfAccountName.get(), "", "",
				"", localize(locator.fileIntoFolder), "", "",
				localize(locator.stopFilterProcessing));
		String folder3 = fileIntoNewFolder;

		// 4. subject contains(subjectcontains_discard) discard
		setFilterConditionsActions(subject4, localize(locator.active), "any",
				localize(locator.subject), localize(locator.contains),
				"subjectcontains_discard", "", "", "", "", "", "",
				localize(locator.discard), "", "",
				localize(locator.stopFilterProcessing));

		obj.zListItem.zClick(subject4);
		SleepUtil.sleep(1000);
		obj.zListItem.zShiftClick(subject1);
		SleepUtil.sleep(1000);

		obj.zButton.zClick(zRunFilterButton);
		SleepUtil.sleep(2000);
		obj.zCheckbox.zActivate(zInboxChkBoxInChooseFolderDlg);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("FF")
				|| ZimbraSeleniumProperties.getStringProperty("browser").equals("FF3")) {
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.chooseFolder));
		}
		SleepUtil.sleep(2000);
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.infoMsg));

		// Verifiction
		zGoToApplication("Mail");

		// bug 41662
		// obj.zMessageItem.zNotExists(subject1);
		// obj.zMessageItem.zNotExists(subject2);
		// obj.zMessageItem.zNotExists(subject3);
		// obj.zMessageItem.zNotExists(subject4);
		// obj.zMessageItem.zNotExists(subject5);

		// 1. subject contains(subjectcontains_filetofolder) file to
		// folder
		obj.zFolder.zClick(folder1);
		obj.zMessageItem.zExists(subject1);

		// 2. from contains (randomaccount1) file to folder
		obj.zFolder.zClick(folder2);
		obj.zMessageItem.zExists(subject2);

		// 3. to contains(selfaccount) file to folder
		obj.zFolder.zClick(folder3);
		obj.zMessageItem.zExists(subject3);

		// 4. subject contains(subjectcontains_discard) discard
		obj.zFolder.zClick(page.zMailApp.zInboxFldr);
		obj.zMessageItem.zNotExists(subject4);

		SelNGBase.needReset.set(false);
	}

	/**
	 * This test creates filter by setting subject as condition and file into
	 * new folder as action. It verifies created folder in mail & filing mail
	 * into that folder according to specific subject set by filter.
	 */
	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void MsgBodyShouldNotBeCaseSensitive_Bug36905(String filterName,
			String activeStatus, String conditionType, String condition11,
			String condition12, String conditionValue1, String condition21,
			String condition22, String conditionValue2, String condition31,
			String condition32, String conditionValue3, String action1,
			String action2, String action3, String additionalFilterIfAny)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		setFilterConditionsActions(filterName, activeStatus, conditionType,
				condition11, condition12, conditionValue1, condition21,
				condition22, conditionValue2, condition31, condition32,
				conditionValue3, action1, action2, action3,
				additionalFilterIfAny);

		// Go to mail application tab and verify created folder
		zGoToApplication("Mail");

		// Send mail and verify filter
		page.zComposeView.zComposeAndSendMail(SelNGBase.selfAccountName.get(), "",
				"", conditionValue1, conditionValue1.toLowerCase(), "");
		MailApp.ClickCheckMailUntilMailShowsUp(fileIntoNewFolder,
				conditionValue1);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "FilterDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void SubjectDoesntContains_FileToFolder_Bug42149(String filterName,
			String activeStatus, String conditionType, String condition11,
			String condition12, String conditionValue1, String condition21,
			String condition22, String conditionValue2, String condition31,
			String condition32, String conditionValue3, String action1,
			String action2, String action3, String additionalFilterIfAny)
			throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		setFilterConditionsActions(filterName, activeStatus, conditionType,
				condition11, condition12, conditionValue1, condition21,
				condition22, conditionValue2, condition31, condition32,
				conditionValue3, action1, action2, action3,
				additionalFilterIfAny);

		// Go to mail application tab and verify created folder
		zGoToApplication("Mail");

		// Send mail and verify filter
		String body = getLocalizedData_NoSpecialChar();
		page.zComposeView.zComposeAndSendMail(SelNGBase.selfAccountName.get(), "",
				"", "testing doesn't contains", body, "");
		MailApp.ClickCheckMailUntilMailShowsUp(fileIntoNewFolder,
				"testing doesn't contains");
		zGoToApplication("Preferences");
		zGoToPreferences("Filters");
		obj.zListItem.zClick(filterName);
		obj.zButton.zClick("id=zb__FRV__EDIT_FILTER_RULE_left_icon");
		obj.zButton.zExistsInDlgByName(localize(locator.subject),
				localize(locator.editFilter));
		obj.zButton.zExistsInDlgByName(localize(locator.notContain),
				localize(locator.editFilter));
		obj.zButton.zExistsInDlgByName(localize(locator.fileIntoFolder),
				localize(locator.editFilter));
		obj.zButton.zExistsInDlgByName(fileIntoNewFolder,
				localize(locator.editFilter));
		obj.zCheckbox.zClickInDlgByName(localize(locator.active),
				localize(locator.editFilter));
		obj.zButton.zClickInDlgByName(localize(locator.cancel),
				localize(locator.editFilter));

		SelNGBase.needReset.set(false);
	}

	// private method to select various filter conditions and actions
	private void setFilterConditionsActions(String filterName,
			String activeStatus, String conditionType, String condition11,
			String condition12, String conditionValue1, String condition21,
			String condition22, String conditionValue2, String condition31,
			String condition32, String conditionValue3, String action1,
			String action2, String action3, String additionalFilterIfAny)
			throws Exception {

		String filterSavedToastMessage = obj.zToastAlertMessage.zGetMsg();
		obj.zButton.zClick(localize(locator.newFilter));
		obj.zDialog.zExists(dialogName);

		// Filter name
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")
				&& ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			obj.zEditField.zTypeInDlgByName(localize(locator.filterName),
					filterName, dialogName);
		} else {
			obj.zEditField.zTypeInDlgByName(localize(locator.filterNameLabel),
					filterName, dialogName);
		}

		// Active or Inactive
		if (!activeStatus.equals("")
				&& !activeStatus.equals(localize(locator.active))) {
			obj.zCheckbox.zClickInDlgByName(localize(locator.active),
					dialogName);
		}

		// Condition type
		if (conditionType.equals("all")) {
			String filterCondition, anyCondition;
			anyCondition = page.zFilterPreferences.zGetLocalizedAllOrAny("any");
			filterCondition = page.zFilterPreferences
					.zGetLocalizedAllOrAny("all");
			obj.zButton.zClickInDlgByName(anyCondition, dialogName);
			obj.zMenuItem.zClickInDlgByName(filterCondition, dialogName);
		}

		// Condition11
		if (!condition11.equals("")
				&& !condition11.equals(localize(locator.subject))) {
			obj.zButton
					.zClickInDlgByName(localize(locator.subject), dialogName);
			obj.zMenuItem.zClickInDlgByName(condition11, dialogName);
		}

		// Condition12
		if (!condition12.equals("")
				&& !condition12.equals(localize(locator.exactMatch))) {
			obj.zButton.zClickInDlgByName(localize(locator.contains),
					dialogName);
			obj.zMenuItem.zClickInDlgByName(condition12, dialogName);
		}

		// Condition value1
		SelNGBase.fieldLabelIsAnObject = true;
		SelNGBase.selenium.get()
				.type(
						"xpath=//table[contains(@id,'_conditions')]/tbody/tr[contains(@id,'DWT')]/td/div[contains(@id,'DWT')]/input",
						conditionValue1);
		// obj.zEditField.zTypeInDlgByName(condition12, conditionValue1,
		// dialogName);
		SelNGBase.fieldLabelIsAnObject = false;

		// If there are 2 Conditions
		if ((!condition21.equals("")) && (!condition22.equals(""))) {
			obj.zButton.zClickInDlgByName("ImgPlus", dialogName);
			obj.zButton.zClickInDlgByName(condition11, dialogName, "2");
			obj.zMenuItem.zClickInDlgByName(condition21, dialogName, "2");
			obj.zButton.zClickInDlgByName(condition12, dialogName, "2");
			obj.zMenuItem.zClickInDlgByName(condition22, dialogName);
			SelNGBase.fieldLabelIsAnObject = true;
			obj.zEditField.zTypeInDlgByName(condition22, conditionValue2,
					dialogName, "2");
			SelNGBase.fieldLabelIsAnObject = false;
		}

		// If there are 3 Conditions
		if ((!condition31.equals("")) && (!condition32.equals(""))) {
			obj.zButton.zClickInDlgByName("ImgPlus", dialogName);
			obj.zButton.zClickInDlgByName(condition21, dialogName, "3");
			obj.zMenuItem.zClickInDlgByName(condition31, dialogName);
			obj.zButton.zClickInDlgByName(condition22, dialogName, "3");
			obj.zMenuItem.zClickInDlgByName(condition32, dialogName);
			SelNGBase.fieldLabelIsAnObject = true;
			obj.zEditField.zTypeInDlgByName(condition32, conditionValue3,
					dialogName, "3");
			SelNGBase.fieldLabelIsAnObject = false;
		}

		// Action1
		if (!action1.equals("")
				&& !action1.equals(localize(locator.keepInInbox))) {
			obj.zButton.zClickInDlgByName(localize(locator.keepInInbox).split(
					" ")[0], dialogName);
			obj.zMenuItem.zClickInDlgByName(action1, dialogName);
			filterActions(action1);
		}

		// If there are 2 Actions
		if (!action2.equals("")) {
			obj.zButton.zClickInDlgByName("ImgPlus", dialogName, "3");
			obj.zButton.zClickInDlgByName(action1, dialogName);
			obj.zMenuItem.zClickInDlgByName(action2, dialogName);
			filterActions(action2);
		}

		// If there are 3 Actions
		if (!action3.equals("")) {
			obj.zButton.zClickInDlgByName("ImgPlus", dialogName, "4");
			obj.zButton.zClickInDlgByName(action2, dialogName);
			obj.zMenuItem.zClickInDlgByName(action3, dialogName);
			filterActions(action3);
		}

		// Pressing OK button in add filter dialog
		obj.zButton.zClickInDlgByName(localize(locator.ok), dialogName);
		Assert
				.assertNotSame(filterSavedToastMessage,
						localize(locator.filtersSaved),
						"Filter action related toast message not shown properly after creating filter");
		SleepUtil.sleep(2000); /*
							 * test fails, because it tries to verify filter
							 * after pressing ok button
							 */
		zGoToApplication("Preferences");
		obj.zTab.zClick(localize(locator.filterRules));
		SleepUtil.sleep(1500);
		obj.zCheckbox.zExists(filterName);
		obj.zCheckbox.zVerifyIsChecked(filterName);
	}

	private void filterActions(String currentAction) throws Exception {
		if (currentAction.equals(localize(locator.fileIntoFolder))) {
			fileIntoNewFolder = getLocalizedData_NoSpecialChar();
			obj.zButton.zClickInDlgByName(localize(locator.browse), dialogName);
			obj.zDialog.zExists(localize(locator.chooseFolder));
			obj.zButton.zClickInDlgByName(localize(locator._new),
					localize(locator.chooseFolder));
			obj.zDialog.zExists(localize(locator.createNewFolder));
			obj.zEditField.zTypeInDlgByName(localize(locator.nameLabel),
					fileIntoNewFolder, localize(locator.createNewFolder));

			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.createNewFolder));
			SleepUtil.sleep(1000);
			obj.zFolder.zExistsInDlgByName(fileIntoNewFolder,
					localize(locator.chooseFolder));
			obj.zFolder.zClickInDlgByName(fileIntoNewFolder,
					localize(locator.chooseFolder));
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.chooseFolder));
			obj.zButton.zExistsInDlgByName(fileIntoNewFolder, dialogName);
		} else if (currentAction.equals(localize(locator.forwardToAddress))) {
			forwardtoUser = ProvZCS.getRandomAccount();
			SelNGBase.fieldLabelIsAnObject = true;
			obj.zEditField.zTypeInDlgByName(localize(locator.forwardTo),
					forwardtoUser, dialogName);
			SelNGBase.fieldLabelIsAnObject = false;
		}
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