package projects.zcs.tests.mail.autocomplete;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.lang.reflect.Method;
import junit.framework.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import com.zimbra.common.service.ServiceException;
import framework.util.RetryFailedTests;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.MailApp;

/**
 * @author Jitesh Sojitra
 */
@SuppressWarnings("static-access")
public class MailAutoCompleteAddressTests extends CommonTest {
	static String acc1, acc2, acc3, acc4, acc5;
	static String first = null, second = null, third = null, fourth = null,
			fifth = null, sixth = null, seventh = null, eighth = null;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "composeDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("verifyAutoCompleteAndRankOnlyWithContactsFolder")
				|| test.equals("verifyAutoCompleteAndRankOnlyWithGAL")
				|| test.equals("verifyAutoCompleteAndRankWithContactsAndGAL")
				|| test
						.equals("verifyAutoCompleteAndRankWithContactsAndGALONOFF")
				|| test.equals("verifyAutoCompleteAndRankWithContactsFolder")
				|| test
						.equals("zwcHangsDuringAutoCompOnApostropheChar_Bug45815")
				|| test.equals("autocompleteReturnsTooManyResults_Bug40959")
				|| test.equals("verifyContactGroupAutoComplete_Bug45545")
				|| test.equals("autocompleteOnComma_Bug43179")
				|| test.equals("unableToGetRidOfEmailedContacts_Bug40081")
				|| test.equals("autocompleteOnSpecialCharacters")
				|| test.equals("luceneStopWordsAutoComplete_Bug46718")
				|| test.equals("unableToGetRidOfEmailedContacts_Bug40081")
				|| test
						.equals("galAutoCompleteDoesntWorkAfterPrefChange_Bug45337_Bug37377")
				|| test
						.equals("optionNotToAutoCompleteContactGroupsByMember_Bug44509")
				|| test.equals("autocompleteDoesNotWorkAfterPeriod_Bug47045")
				|| test
						.equals("autocompleteShowsContactFromTrashedABFolders_Bug47044")
				|| test
						.equals("verifyAutocompleteFromContactsSubAndSubSubFolders")
				|| test
						.equals("verifyAutocompleteFromSharedSubAndSubSubFolders_Bug45550")
				|| test
						.equals("verifyAutoCompleteWithLargeAddressBook_1KContacts")) {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		} else {
			return new Object[][] { { "_selfAccountName_", "_selfAccountName_",
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					"commonsubject", "commonbody", "" } };
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		zLoginIfRequired();
		zGoToApplication("Mail");
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
	 * Verify autocomplete and rank with local Contacts address book folder.
	 * Steps, 1.Create 5 accounts 2.Send 3 mails to account3, 3 mails to
	 * account5, 2 mails to account1, 2 mails to account4 and 1 mail to account2
	 * 3.Verify autocomplete and rank 4.Send few mails to those accounts again
	 * 5.Verify updated autocomplete and rank
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutoCompleteAndRankOnlyWithContactsFolder(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		first = config.getString("locale").substring(0, 1);
		second = config.getString("locale").substring(1, 2);
		acc1 = ProvZCS.getRandomAccount();
		acc2 = ProvZCS.getRandomAccount();
		acc3 = ProvZCS.getRandomAccount();
		acc4 = ProvZCS.getRandomAccount();
		acc5 = ProvZCS.getRandomAccount();
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		sendMails();
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys(first + "," + second);
		verifyAutocomplete(true);

		sendMailsUpdated();
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys(first + "," + second);
		verifyAutocompleteUpdated();

		needReset = false;
	}

	/**
	 * Verify autocomplete and rank with local Contacts address book folder.
	 * Steps, 1.Create 5 accounts 2.Send 3 mails to account3, 3 mails to
	 * account5, 2 mails to account1, 2 mails to account4 and 1 mail to account2
	 * 3.Verify autocomplete and rank 4.Send few mails to those accounts again
	 * 5.Verify updated autocomplete and rank
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutoCompleteAndRankOnlyWithContactsFolder_NewWindow(
			String from, String to, String cc, String bcc, String subject,
			String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		first = config.getString("locale").substring(0, 1);
		second = config.getString("locale").substring(1, 2);
		acc1 = config.getString("locale").replace("_", "")
				+ "neww1@testdomain.com";
		acc1 = acc1.toLowerCase();
		getKeyboardKeys(acc1);
		ProvZCS.createAccount(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zComposeView.zNavigateToComposeByShiftClick();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("zh_HK")) {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth + "," + seventh + "," + eighth);
		} else {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth);
		}
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		for (int i = 0; i <= 9; i++) {
			pressKeys("backspace");
		}
		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("zh_HK")) {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth + "," + seventh + "," + eighth);
		} else {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth);
		}
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);

		needReset = false;
	}

	/**
	 * Verify autocomplete and rank with GAL. Steps, 1.Create 5 accounts 2.Send
	 * 3 mails to account3, 3 mails to account5, 2 mails to account1, 2 mails to
	 * account4 and 1 mail to account2 3.Verify autocomplete and rank 4.Send few
	 * mails to those accounts again 5.Refresh UI 6.Verify updated autocomplete
	 * and rank
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutoCompleteAndRankOnlyWithGAL(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "blah1@testdomain.com";
		acc1 = acc1.toLowerCase();
		acc2 = config.getString("locale").replace("_", "")
				+ "blah2@testdomain.com";
		acc2 = acc2.toLowerCase();
		acc3 = config.getString("locale").replace("_", "")
				+ "blah3@testdomain.com";
		acc3 = acc3.toLowerCase();
		acc4 = config.getString("locale").replace("_", "")
				+ "blah4@testdomain.com";
		acc4 = acc4.toLowerCase();
		acc5 = config.getString("locale").replace("_", "")
				+ "blah5@testdomain.com";
		acc5 = acc5.toLowerCase();
		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		ProvZCS.createAccount(acc3);
		ProvZCS.createAccount(acc4);
		ProvZCS.createAccount(acc5);
		getKeyboardKeys(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		sendMails();
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		verifyAutocomplete(true);

		sendMailsUpdated();
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		verifyAutocompleteUpdated();

		needReset = false;
	}

	/**
	 * Verify autocomplete and rank with Contacts & GAL both. Steps, 1.Create 5
	 * accounts 2.Send 3 mails to account3, 3 mails to account5, 2 mails to
	 * account1, 2 mails to account4 and 1 mail to account2 3.Verify
	 * autocomplete and rank 4.Send few mails to those accounts again 5.Refresh
	 * UI 6.Verify updated autocomplete and rank
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutoCompleteAndRankWithContactsAndGAL(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "zimb1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "zimb2@testdomain.com";
		acc3 = config.getString("locale").replace("_", "")
				+ "zimb3@testdomain.com";
		acc4 = config.getString("locale").replace("_", "")
				+ "zimb4@testdomain.com";
		acc5 = config.getString("locale").replace("_", "")
				+ "zimb5@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		ProvZCS.createAccount(acc3);
		ProvZCS.createAccount(acc4);
		ProvZCS.createAccount(acc5);
		getKeyboardKeys(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		sendMails();
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		verifyAutocomplete(true);

		sendMailsUpdated();
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		verifyAutocompleteUpdated();

		needReset = false;
	}

	/**
	 * Verify autocomplete and rank with Contacts & GAL both by ON and OFF.
	 * Steps, 1.Create 5 accounts 2.Send 3 mails to account3, 3 mails to
	 * account5, 2 mails to account1, 2 mails to account4 and 1 mail to account2
	 * 3.Verify autocomplete and rank 4.Delete contacts from Emailed contacts
	 * folder and Trash both 5.Send few mails to those accounts again 6.Refresh
	 * UI 7.Verify updated autocomplete and rank
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutoCompleteAndRankWithContactsAndGALONOFF(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "vmwa1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "vmwa2@testdomain.com";
		acc3 = config.getString("locale").replace("_", "")
				+ "vmwa3@testdomain.com";
		acc4 = config.getString("locale").replace("_", "")
				+ "vmwa4@testdomain.com";
		acc5 = config.getString("locale").replace("_", "")
				+ "vmwa5@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		ProvZCS.createAccount(acc3);
		ProvZCS.createAccount(acc4);
		ProvZCS.createAccount(acc5);
		getKeyboardKeys(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		sendMails();
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		verifyAutocomplete(true);

		zGoToApplication("Address Book");
		obj.zFolder.zClick(page.zABCompose.zEmailedContactsFolder);
		Thread.sleep(2000);
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_CONTROL);
		zRobot.keyPress(KeyEvent.VK_A);
		Thread.sleep(500);
		zRobot.keyRelease(KeyEvent.VK_CONTROL);
		zRobot.keyRelease(KeyEvent.VK_A);
		Thread.sleep(500);
		zRobot.keyPress(KeyEvent.VK_DELETE);
		zRobot.keyRelease(KeyEvent.VK_DELETE);
		Thread.sleep(500);
		typeKeyboardKeys();
		// verification
		Assert
				.assertTrue(
						"Verifying first autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_0')]//td[contains(text(), '"
										+ acc3.toLowerCase() + "')]"));

		Assert
				.assertTrue(
						"Verifying second autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_1')]//td[contains(text(), '"
										+ acc5.toLowerCase() + "')]"));

		Assert
				.assertTrue(
						"Verifying third autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_2')]//td[contains(text(), '"
										+ acc1.toLowerCase() + "')]"));

		Assert
				.assertTrue(
						"Verifying fourth autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_3')]//td[contains(text(), '"
										+ acc4.toLowerCase() + "')]"));

		Assert
				.assertTrue(
						"Verifying fifth autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_4')]//td[contains(text(), '"
										+ acc2.toLowerCase() + "')]"));
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Verify autocomplete and rank with Contacts. Steps, 1.Create 5 contacts in
	 * Contacts address book folder 2.Send 3 mails to account3, 3 mails to
	 * account5, 2 mails to account1, 2 mails to account4 and 1 mail to account2
	 * 3.Verify autocomplete and rank 4.Send few mails to those accounts again
	 * 5.Verify updated autocomplete and rank
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutoCompleteAndRankWithContactsFolder(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "cont1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "cont2@testdomain.com";
		acc3 = config.getString("locale").replace("_", "")
				+ "cont3@testdomain.com";
		acc4 = config.getString("locale").replace("_", "")
				+ "cont4@testdomain.com";
		acc5 = config.getString("locale").replace("_", "")
				+ "cont5@testdomain.com";
		String[] contacts = { acc1, acc2, acc3, acc4, acc5 };
		createContacts(contacts, false);
		getKeyboardKeys(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		typeKeyboardKeys();
		verifyAutocomplete(false);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * ZWC hangs during autocomplete on apostrophe character - bug 45815. Steps,
	 * 1.Create 2 accounts 2.Send few mails 3.Compose mail and verify
	 * autocomplete with apostrophe character. Also verify autocomplete by
	 * removing some character and checking back autocomplete and rank works
	 * fine
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zwcHangsDuringAutoCompOnApostropheChar_Bug45815(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "hang1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "hang2@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		getKeyboardKeys(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");

		page.zComposeView.zComposeAndSendMail(acc1 + ";" + acc2, "", "",
				"testSubject", "testBody", "");

		page.zComposeView.zComposeAndSendMail(acc2, "", "", "testSubject",
				"testBody", "");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zCcField);
		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("zh_HK")) {
			pressKeys("'");
			pressKeys("backspace");
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth + "," + seventh + "," + eighth);
		} else {
			pressKeys("'");
			pressKeys("backspace");
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth);
		}
		Assert
				.assertTrue(
						"Verifying first autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_0')]//td[contains(text(), '"
										+ acc2 + "')]"));

		Assert
				.assertTrue(
						"Verifying second autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_1')]//td[contains(text(), '"
										+ acc1 + "')]"));
		pressKeys("'");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Autocomplete returns too many results - bug 40959. Steps, 1.Create 5
	 * contacts in Contacts address book folder 2.Verify autocomplete with
	 * partial character & space and verify. For e.g en US <enUS@testdomain.com>
	 * then verify autocomplete by en US and also verify autocomplete not exists
	 * where it should not
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void autocompleteReturnsTooManyResults_Bug40959(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "toom1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "toom2@testdomain.com";
		acc3 = config.getString("locale").replace("_", "")
				+ "toom3@testdomain.com";
		acc4 = config.getString("locale").replace("_", "")
				+ "toom4@testdomain.com";
		acc5 = config.getString("locale").replace("_", "")
				+ "toom5@testdomain.com";
		String[] contacts = { acc1, acc2, acc3, acc4, acc5 };
		createContacts(contacts, true);
		getKeyboardKeys(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		typeKeyboardKeys();
		verifyAutocomplete(false);
		pressKeys("1");
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5, 0);
		pressKeys("space");
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5, 0);
		pressKeys(config.getString("locale").substring(0, 1));
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5, 0);
		pressKeys(config.getString("locale").substring(1, 2));
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5, 0);
		pressKeys("x");
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Verify contact group autocomplete with 'VM1Ware Fi3nance' - bug 45545.
	 * Steps, 1.Create contact group called 'VM1Ware Fi3nance' 2.Type character
	 * one by one in email To: field and verify autocomplete works
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyContactGroupAutoComplete_Bug45545(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String groupName = "VM1Ware Fi3nance";
		zGoToApplication("Address Book");
		obj.zButtonMenu.zClick(page.zABCompose.zNewMenuDropdownIconBtn);
		obj.zMenuItem.zClick(localize(locator.group));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.groupNameLabel)),
				groupName);
		obj.zEditField.zType(localize(locator.findLabel),
				"ccuser@testdomain.com");
		obj.zButton.zClick(localize(locator.search), "2");
		Thread.sleep(1500);
		if (currentBrowserName.contains("Safari")) {
			obj.zButton.zClick(localize(locator.search), "2");
			obj.zButton.zClick(localize(locator.search), "2");
			Thread.sleep(1000);
		}
		obj.zListItem.zDblClickItemInSpecificList("ccuser@testdomain.com", "2");
		obj.zButton.zClick(localize(locator.add));
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zContactListItem.zExists(groupName);

		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys("v,m");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("1");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("w,a,r,e");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("space");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("f");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("i");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("3");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Verify lucene stop words autocomplete with 'IT Department' - bug 46718.
	 * Steps, 1.Create contact group called 'IT Department' 2.Type character one
	 * by one in email To: field and verify autocomplete works
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void luceneStopWordsAutoComplete_Bug46718(String from, String to,
			String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String groupName = "IT Department";
		zGoToApplication("Address Book");
		obj.zButtonMenu.zClick(page.zABCompose.zNewMenuDropdownIconBtn);
		obj.zMenuItem.zClick(localize(locator.group));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.groupNameLabel)),
				groupName);
		obj.zEditField.zType(localize(locator.findLabel),
				"ccuser@testdomain.com");
		obj.zButton.zClick(localize(locator.search), "2");
		Thread.sleep(2500);
		if (currentBrowserName.contains("Safari")) {
			obj.zButton.zClick(localize(locator.search), "2");
			obj.zButton.zClick(localize(locator.search), "2");
			Thread.sleep(1000);
		}
		obj.zListItem.zDblClickItemInSpecificList("ccuser@testdomain.com", "2");
		obj.zButton.zClick(localize(locator.add));
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zContactListItem.zExists(groupName);

		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys("i,t");
		pressKeys("space");
		pressKeys("d");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Verify autocomplete with comma - bug 43719 (covers 4 type of
	 * verification) Verify1.Set zimbraPrefAutoCompleteQuickCompletionOnComma to
	 * TRUE and verify autocomplete with comma. Verify2.Set
	 * zimbraPrefAutoCompleteQuickCompletionOnComma to FALSE and verify
	 * autocomplete with comma. Verify3.Set
	 * zimbraPrefAutoCompleteQuickCompletionOnComma to FALSE and verify
	 * autocomplte works fine for account whose display name contains comma for
	 * e.g John, Martin. Verify4.Verify autocomplete with tab character.
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void autocompleteOnComma_Bug43179(String from, String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		// verify1
		System.out
				.println("verify1 : Set zimbraPrefAutoCompleteQuickCompletionOnComma to TRUE");
		acc1 = config.getString("locale").replace("_", "")
				+ "comm1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "comm2@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		getKeyboardKeys(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoCompleteQuickCompletionOnComma", "TRUE");
		page.zComposeView.zComposeAndSendMail(acc1 + ";" + acc2, "", "",
				"testSubject", "testBody", "");
		page.zComposeView.zComposeAndSendMail(acc2, "", "", "testSubject",
				"testBody", "");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		Thread.sleep(1000);
		Assert
				.assertTrue(
						"Verifying first autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_0')]//td[contains(text(), '"
										+ acc2.toLowerCase() + "')]"));
		Assert
				.assertTrue(
						"Verifying second autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_1')]//td[contains(text(), '"
										+ acc1.toLowerCase() + "')]"));
		Robot zRobot = new Robot();
		zRobot.keyPress(KeyEvent.VK_COMMA);
		zRobot.keyRelease(KeyEvent.VK_COMMA);
		Thread.sleep(1000);
		Assert.assertTrue("Expected value(" + acc2 + "), Actual Value("
				+ obj.zTextAreaField.zGetInnerText(page.zComposeView.zToField)
				+ ")", obj.zTextAreaField.zGetInnerText(
				page.zComposeView.zToField).indexOf(acc2.toLowerCase()) >= 0);

		// verify2
		System.out
				.println("verify2 : Set zimbraPrefAutoCompleteQuickCompletionOnComma to FALSE");
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoCompleteQuickCompletionOnComma", "FALSE");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		Assert
				.assertTrue(
						"Verifying first autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_0')]//td[contains(text(), '"
										+ acc2.toLowerCase() + "')]"));
		Assert
				.assertTrue(
						"Verifying second autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_1')]//td[contains(text(), '"
										+ acc1.toLowerCase() + "')]"));
		zRobot.keyPress(KeyEvent.VK_COMMA);
		zRobot.keyRelease(KeyEvent.VK_COMMA);
		Thread.sleep(1000);
		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("zh_HK")) {
			Assert.assertEquals(acc1.substring(0, 8).toLowerCase() + ",",
					obj.zTextAreaField
							.zGetInnerText(page.zComposeView.zToField));
		} else {
			Assert.assertEquals(acc1.substring(0, 6).toLowerCase() + ",",
					obj.zTextAreaField
							.zGetInnerText(page.zComposeView.zToField));
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		// verify3
		System.out
				.println("verify3 : Set zimbraPrefAutoCompleteQuickCompletionOnComma to FALSE and verify autocomplte works fine for account whose display name contains comma for e.g John, Martin");
		String displayName, displayName1, displayName2;
		String char1 = getOnlyEnglishAlphabetCharAndNumber(), char2 = getOnlyEnglishAlphabetCharAndNumber(), char3 = getOnlyEnglishAlphabetCharAndNumber(), char4 = getOnlyEnglishAlphabetCharAndNumber(), char5 = getOnlyEnglishAlphabetCharAndNumber();
		String char6 = getOnlyEnglishAlphabetCharAndNumber(), char7 = getOnlyEnglishAlphabetCharAndNumber(), char8 = getOnlyEnglishAlphabetCharAndNumber(), char9 = getOnlyEnglishAlphabetCharAndNumber(), char10 = getOnlyEnglishAlphabetCharAndNumber();
		displayName1 = char1 + char2 + char3 + char4 + char5;
		displayName2 = char6 + char7 + char8 + char9 + char10;
		displayName = displayName1 + ", " + displayName2;
		ProvZCS.modifyAccount(selfAccountName, "displayName", displayName);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoCompleteQuickCompletionOnComma", "FALSE");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys(char1 + "," + char2 + "," + char3 + "," + char4 + "," + char5);
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1, 1);
		zRobot.keyPress(KeyEvent.VK_COMMA);
		zRobot.keyRelease(KeyEvent.VK_COMMA);
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1, 1);
		pressKeys("space");
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1, 1);
		pressKeys(char6 + "," + char7 + "," + char8 + "," + char9 + ","
				+ char10);
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1, 1);

		// use of tab key
		pressKeys("tab");
		Thread.sleep(1000);
		System.out.println((char) 34 + displayName + (char) 34 + " <"
				+ selfAccountName.toLowerCase() + ">;".trim());
		System.out.println(obj.zEditField
				.zGetInnerText(page.zComposeView.zToField.trim()));
		assertReport(obj.zEditField.zGetInnerText(page.zComposeView.zToField
				.trim()), (char) 34 + displayName + (char) 34 + " <"
				+ selfAccountName.toLowerCase() + ">;".trim(),
				"Verifying To field autocomplete value");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Verify autocomplete with special characters. Steps, 1.Create accounts
	 * with special character 2.Verify autocomplete with those chracters
	 * 3.Verify email address is filled automatically with semi-colon (;)
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void autocompleteOnSpecialCharacters_Bug41512(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "") + "-"
				+ "chr1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "") + "."
				+ "chr2@testdomain.com";
		String[] contacts = { acc1, acc2 };
		createContacts(contacts, true);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		verifySpecialCharAutoComplete("acc1");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		getKeyboardKeys(acc2);
		typeKeyboardKeys();
		verifySpecialCharAutoComplete("acc2");
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		if (config.getString("locale").equals("en_US")) {
			getKeyboardKeys(acc1);
			Robot zRobot = new Robot();
			zRobot.keyPress(KeyEvent.VK_SHIFT);
			typeKeyboardKeys();
			zRobot.keyRelease(KeyEvent.VK_SHIFT);
			verifySpecialCharAutoComplete("acc1");
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		}

		obj.zButton.zClick(MailApp.zNewMenuIconBtn);
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys("z, i, m, b, r, a, ;");
		Thread.sleep(1000);
		Assert.assertTrue("Expected value(" + "zimbra@testdomain.com"
				+ "), Actual Value("
				+ obj.zTextAreaField.zGetInnerText(page.zComposeView.zToField)
				+ ")",
				obj.zTextAreaField.zGetInnerText(page.zComposeView.zToField)
						.indexOf("zimbra@testdomain.com") >= 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Unable to get rid of emailed contacts - bug 40081. Steps, 1.Create
	 * account with zimbraPrefAutoAddAddressEnabled TRUE 2.Send mail to that
	 * account so automatically email address would be added in
	 * "Emailed contacts" folder 3.Compose mail and check autocomplete 4.Move
	 * contact to Trash folder 5.Verify autocomplete not exists 6.Permanently
	 * delete contact from Trash folder, verify autocomplete not exists.
	 * 6.Refresh UI and again verify autocomplete not exists
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void unableToGetRidOfEmailedContacts_Bug40081(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "ridc1@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefAutoAddAddressEnabled", "TRUE");
		zGoToApplication("Mail");
		page.zComposeView.zComposeAndSendMail(acc1, "", "", "testSubject",
				"testBody", "");
		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Address Book");
		obj.zFolder.zClick(page.zABCompose.zEmailedContactsFolder);
		Robot zRobot = new Robot();
		Thread.sleep(1000);
		zRobot.keyPress(KeyEvent.VK_CONTROL);
		zRobot.keyPress(KeyEvent.VK_A);
		zRobot.keyRelease(KeyEvent.VK_CONTROL);
		zRobot.keyRelease(KeyEvent.VK_A);
		pressKeys("delete");

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Address Book");
		obj.zFolder.zClick(localize(locator.trash));
		Thread.sleep(1000);
		zRobot.keyPress(KeyEvent.VK_CONTROL);
		zRobot.keyPress(KeyEvent.VK_A);
		zRobot.keyRelease(KeyEvent.VK_CONTROL);
		zRobot.keyRelease(KeyEvent.VK_A);
		pressKeys("delete");

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * GAL autocomplete doesn't work after pref changed - bug 45337. Steps,
	 * 1.Create account 2.Compose mail and verify autocomplete doesn't exists
	 * for gal 3.mark TRUE to address book preference
	 * "Include addresses in the Global Address List" 4.Re-compose mail and
	 * verify gal autocomplete exists.
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void galAutoCompleteDoesntWorkAfterPrefChange_Bug45337_Bug37377(
			String from, String to, String cc, String bcc, String subject,
			String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "pref1@testdomain.com";
		ProvZCS.createAccount(acc1);
		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1.toLowerCase(), 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Preferences");
		zGoToPreferences("Address Book");
		obj.zCheckbox.zClick(localize(locator.galAutocomplete));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(1000);

		getKeyboardKeys(acc1);
		page.zComposeView.zNavigateToMailCompose();
		selenium.click("link=" + localize(locator.showBCC));
		obj.zTextAreaField.zActivate(page.zComposeView.zBccField);
		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("zh_HK")) {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth + "," + seventh + "," + eighth);
		} else {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth);
		}
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Option not to AutoComplete contact groups by member - bug 44509. Steps,
	 * 1.Create 2 account and 1 contact group 2.Compose mail and verify all 3
	 * autocomplete (group followed by both account) 3.mark TRUE to address book
	 * preference "Don't show contact group if one of its members matches"
	 * 4.Re-compose mail and verify contact group not exists 5.Verify
	 * autocomplete for contactgroup using proper contact group also.
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void optionNotToAutoCompleteContactGroupsByMember_Bug44509(
			String from, String to, String cc, String bcc, String subject,
			String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "grop1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "grop2@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");

		String groupName = "1ABc 2DeF";
		zGoToApplication("Address Book");
		obj.zButtonMenu.zClick(page.zABCompose.zNewMenuDropdownIconBtn);
		obj.zMenuItem.zClick(localize(locator.group));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.groupNameLabel)),
				groupName);
		Thread.sleep(1000);
		obj.zEditField.zType(localize(locator.findLabel), "grop");
		obj.zButton.zClick(localize(locator.search), "2");
		Thread.sleep(1500);
		if (currentBrowserName.contains("Safari")) {
			obj.zButton.zClick(localize(locator.search), "2");
			obj.zButton.zClick(localize(locator.search), "2");
			Thread.sleep(1000);
		}
		obj.zButton.zClick(localize(locator.addAll));
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zContactListItem.zExists(groupName);

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 2, 0);
		page.zMailApp.zVerifyAutocompleteExists(acc2.toLowerCase(), 3, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Preferences");
		zGoToPreferences("Address Book");
		obj.zCheckbox.zClick(localize(locator.autocompleteNoGroupMatch));
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		Thread.sleep(1000);

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(groupName, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 0);
		page.zMailApp.zVerifyAutocompleteExists(acc2.toLowerCase(), 2, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys("1, A");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("b");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("c");
		page.zMailApp.zVerifyAutocompleteNotExists(acc1.toLowerCase(), 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2.toLowerCase(), 2, 0);
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("space");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("2");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("d");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("e");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		pressKeys("f");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc1.toLowerCase(), 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2.toLowerCase(), 2, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Autocomplete does not work after period(.) in domain name - bug 47045.
	 * Steps, 1.Create account 2.Modify account with
	 * zimbraPrefGalAutoCompleteEnabled to TRUE 3.Refresh UI and compose mail
	 * 4.Type complete account name for e.g. enus@testdomain.com and verify
	 * autocomplete at each typed character
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void autocompleteDoesNotWorkAfterPeriod_Bug47045(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		acc1 = config.getString("locale").replace("_", "")
				+ "peri@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		pressKeys("@");
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		pressKeys("t");
		pressKeys("e");
		pressKeys("s");
		pressKeys("t");
		pressKeys("d");
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		pressKeys("o");
		pressKeys("m");
		pressKeys("a");
		pressKeys("i");
		pressKeys("n");
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		pressKeys(".");
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		pressKeys("c");
		pressKeys("o");
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		pressKeys("m");
		page.zMailApp.zVerifyAutocompleteExists(acc1.toLowerCase(), 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Autocomplete shows contact from deleted(Trashed) address book folders -
	 * bug 47044. Steps, 1.Create account 2.Create addressbook with one contact
	 * with this email address 3.Check autocomplete exists 3.Move addressbook to
	 * Trash folder 4.Verify autocomplete not exists 5.Refresh UI and again
	 * verify autocomplete not exists
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void autocompleteShowsContactFromTrashedABFolders_Bug47044(
			String from, String to, String cc, String bcc, String subject,
			String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String newAddressBook, lastName, firstName;
		newAddressBook = getLocalizedData_NoSpecialChar();
		lastName = getLocalizedData_NoSpecialChar();
		firstName = getLocalizedData_NoSpecialChar();
		acc1 = config.getString("locale").replace("_", "")
				+ "tras1@testdomain.com";
		ProvZCS.createAccount(acc1);
		zGoToApplication("Address Book");
		page.zABCompose.zCreateNewAddBook(newAddressBook);
		page.zABCompose.zCreateContactInAddressBook(newAddressBook, lastName,
				"", firstName, acc1);

		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Address Book");
		obj.zFolder.zRtClick(newAddressBook);
		obj.zMenuItem.zClick(localize(locator.del));

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Address Book");
		obj.zFolder.zRtClick(newAddressBook);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Verify autocomplete from contacts sub and subsub folder. Steps, 1.Create
	 * 2 account 2.Create subaddressbook and subsubaddressbook folder 3.Add 1
	 * contact each to both addressbook 4.Verify autocomplete exists 5.Move
	 * subaddressbook to Trash folder and verify autocomplete not exists
	 * 6.Refresh UI and again verify autocomplete not exists
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutocompleteFromContactsSubAndSubSubFolders(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		ProvZCS.modifyAccount(selfAccountName,
				"zimbraPrefGalAutoCompleteEnabled", "TRUE");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");

		String subAddressBook, subSubAddressBook, sublastName, subSublastName;
		subAddressBook = getLocalizedData_NoSpecialChar();
		subSubAddressBook = getLocalizedData_NoSpecialChar();
		sublastName = "1" + getLocalizedData_NoSpecialChar();
		subSublastName = "2" + getLocalizedData_NoSpecialChar();
		acc1 = config.getString("locale").replace("_", "")
				+ "subf1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "subf2@testdomain.com";
		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		zGoToApplication("Address Book");
		page.zABCompose.zCreateNewAddBook(subAddressBook,
				localize(locator.contacts));
		page.zABCompose.zCreateNewAddBook(subSubAddressBook, subAddressBook);
		obj.zFolder.zClick(subAddressBook);
		page.zABCompose.zCreateContactInAddressBook("", sublastName, "", "",
				acc1);
		obj.zFolder.zClick(subSubAddressBook);
		page.zABCompose.zCreateContactInAddressBook("", subSublastName, "", "",
				acc2);

		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(acc2, 2, 0);

		zGoToApplication("Address Book");
		obj.zFolder.zRtClick(subAddressBook);
		obj.zMenuItem.zClick(localize(locator.del));

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Address Book");
		obj.zFolder.zRtClick(subAddressBook);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * Verify autocomplete from shared sub and subsub folder - Bug 45550. Steps,
	 * 1.Create 2 account 2.Create subaddressbook and subsubaddressbook folder
	 * 3.Add 1 contact each to both addressbook 4.Share parent addressbook to
	 * user2 5.Login to user2 > accept share 6.mark ON/OFF to
	 * "Include addresses in shared address books" and verify autocomplete
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutocompleteFromSharedSubAndSubSubFolders_Bug45550(
			String from, String to, String cc, String bcc, String subject,
			String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String subAddressBook, subSubAddressBook, lastName, sublastName, subSublastName, user2, mountingfoldername;
		subAddressBook = getLocalizedData_NoSpecialChar();
		subSubAddressBook = getLocalizedData_NoSpecialChar();
		lastName = "1" + getLocalizedData_NoSpecialChar();
		sublastName = "2" + getLocalizedData_NoSpecialChar();
		subSublastName = "3" + getLocalizedData_NoSpecialChar();
		mountingfoldername = getLocalizedData_NoSpecialChar();
		acc1 = config.getString("locale").replace("_", "")
				+ "shar1@testdomain.com";
		acc2 = config.getString("locale").replace("_", "")
				+ "shar2@testdomain.com";
		acc3 = config.getString("locale").replace("_", "")
				+ "shar3@testdomain.com";
		user2 = ProvZCS.getRandomAccount();

		ProvZCS.createAccount(acc1);
		ProvZCS.createAccount(acc2);
		ProvZCS.createAccount(acc3);
		zGoToApplication("Address Book");
		page.zABCompose.zCreateNewAddBook(subAddressBook,
				localize(locator.contacts));
		page.zABCompose.zCreateNewAddBook(subSubAddressBook, subAddressBook);
		obj.zFolder.zClick(page.zABCompose.zContactsFolder);
		page.zABCompose.zCreateContactInAddressBook("", lastName, "", "", acc1);
		obj.zFolder.zClick(subAddressBook);
		page.zABCompose.zCreateContactInAddressBook("", sublastName, "", "",
				acc2);
		obj.zFolder.zClick(subSubAddressBook);
		page.zABCompose.zCreateContactInAddressBook("", subSublastName, "", "",
				acc3);

		page.zSharing.zShareFolder("Address Book",
				page.zABCompose.zContactsFolder, "", user2, "", "", "", "");
		ProvZCS
				.modifyAccount(acc2, "zimbraPrefGalAutoCompleteEnabled",
						"FALSE");
		zKillBrowsers();
		page.zLoginpage.zLoginToZimbraAjax(user2);
		page.zSharing.zAcceptShare(mountingfoldername);

		getKeyboardKeys(acc1);
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);

		ProvZCS.modifyAccount(user2,
				"zimbraPrefSharedAddrBookAutoCompleteEnabled", "TRUE");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteExists(acc3, 3, 0);

		zGoToApplication("Address Book");
		obj.zFolder.zRtClick(mountingfoldername);
		obj.zMenuItem.zClick(localize(locator.del));

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Address Book");
		obj.zFolder.zRtClick(mountingfoldername);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));

		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2, 0);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	/**
	 * verify autocomplete with large addressbook - 1KContacts. Steps, 1.Import
	 * 1K contacts CSV 2.Verify few autocompletes 3.Send few mails and recheck
	 * updated ranked autocomplete
	 */
	@Test(dataProvider = "composeDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAutoCompleteWithLargeAddressBook_1KContacts(String from,
			String to, String cc, String bcc, String subject, String body,
			String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zNavigateToPrefImportExport();
		obj.zRadioBtn.zClick(localize(locator.contacts));
		File f = new File("src/java/projects/zcs/data/1000contacts.csv");
		String path = f.getAbsolutePath();
		obj.zBrowseField.zTypeWithKeyboard(localize(locator.fileLabel), path);
		obj.zButton.zClick(localize(locator._import));

		zWaitTillObjectExist("dialog", localize(locator.infoMsg));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.infoMsg));

		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zCcField);
		pressKeys("1");
		page.zMailApp.zVerifyAutocompleteExists("1@testing.com", 1, 1);
		page.zMailApp.zVerifyAutocompleteExists("19@testing.com", 20, 0);
		pressKeys("0");
		page.zMailApp.zVerifyAutocompleteExists("10@testing.com", 1, 1);
		page.zMailApp.zVerifyAutocompleteExists("100@testing.com", 2, 0);
		page.zMailApp.zVerifyAutocompleteExists("1000@testing.com", 3, 0);
		page.zMailApp.zVerifyAutocompleteExists("101@testing.com", 4, 0);
		page.zMailApp.zVerifyAutocompleteExists("102@testing.com", 5, 0);
		page.zMailApp.zVerifyAutocompleteExists("103@testing.com", 6, 0);
		page.zMailApp.zVerifyAutocompleteExists("104@testing.com", 7, 0);
		page.zMailApp.zVerifyAutocompleteExists("105@testing.com", 8, 0);
		page.zMailApp.zVerifyAutocompleteExists("106@testing.com", 9, 0);
		page.zMailApp.zVerifyAutocompleteExists("107@testing.com", 10, 0);
		page.zMailApp.zVerifyAutocompleteExists("108@testing.com", 11, 0);
		page.zMailApp.zVerifyAutocompleteExists("109@testing.com", 12, 0);
		pressKeys("backspace, backspace, 2");
		page.zMailApp.zVerifyAutocompleteExists("2@testing.com", 1, 1);
		page.zMailApp.zVerifyAutocompleteExists("29@testing.com", 20, 0);
		pressKeys("backspace, 7");
		page.zMailApp.zVerifyAutocompleteExists("7@testing.com", 1, 1);
		page.zMailApp.zVerifyAutocompleteExists("79@testing.com", 20, 0);
		pressKeys("backspace, 9");
		page.zMailApp.zVerifyAutocompleteExists("9@testing.com", 1, 1);
		page.zMailApp.zVerifyAutocompleteExists("90@testing.com", 2, 0);
		page.zMailApp.zVerifyAutocompleteExists("900@testing.com", 3, 0);
		page.zMailApp.zVerifyAutocompleteExists("901@testing.com", 4, 0);
		page.zMailApp.zVerifyAutocompleteExists("902@testing.com", 5, 0);
		page.zMailApp.zVerifyAutocompleteExists("903@testing.com", 6, 0);
		page.zMailApp.zVerifyAutocompleteExists("904@testing.com", 7, 0);
		page.zMailApp.zVerifyAutocompleteExists("905@testing.com", 8, 0);
		page.zMailApp.zVerifyAutocompleteExists("906@testing.com", 9, 0);
		page.zMailApp.zVerifyAutocompleteExists("907@testing.com", 10, 0);
		page.zMailApp.zVerifyAutocompleteExists("908@testing.com", 11, 0);
		page.zMailApp.zVerifyAutocompleteExists("91@testing.com", 12, 0);
		page.zMailApp.zVerifyAutocompleteExists("92@testing.com", 13, 0);
		page.zMailApp.zVerifyAutocompleteExists("93@testing.com", 14, 0);
		page.zMailApp.zVerifyAutocompleteExists("94@testing.com", 15, 0);
		page.zMailApp.zVerifyAutocompleteExists("95@testing.com", 16, 0);
		page.zMailApp.zVerifyAutocompleteExists("96@testing.com", 17, 0);
		page.zMailApp.zVerifyAutocompleteExists("97@testing.com", 18, 0);
		page.zMailApp.zVerifyAutocompleteExists("98@testing.com", 19, 0);
		page.zMailApp.zVerifyAutocompleteExists("99@testing.com", 20, 0);
		pressKeys("9");
		page.zMailApp.zVerifyAutocompleteExists("99@testing.com", 1, 1);
		page.zMailApp.zVerifyAutocompleteExists("990@testing.com", 2, 0);
		page.zMailApp.zVerifyAutocompleteExists("991@testing.com", 3, 0);
		page.zMailApp.zVerifyAutocompleteExists("992@testing.com", 4, 0);
		page.zMailApp.zVerifyAutocompleteExists("993@testing.com", 5, 0);
		page.zMailApp.zVerifyAutocompleteExists("994@testing.com", 6, 0);
		page.zMailApp.zVerifyAutocompleteExists("995@testing.com", 7, 0);
		page.zMailApp.zVerifyAutocompleteExists("996@testing.com", 8, 0);
		page.zMailApp.zVerifyAutocompleteExists("997@testing.com", 9, 0);
		page.zMailApp.zVerifyAutocompleteExists("998@testing.com", 10, 0);
		page.zMailApp.zVerifyAutocompleteExists("999@testing.com", 11, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		page.zComposeView.zComposeAndSendMail("999@testing.com", "", "",
				"testSubject", "testBody", "");

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys("9, 9");
		page.zMailApp.zVerifyAutocompleteExists("999@testing.com", 1, 1);
		page.zMailApp.zVerifyAutocompleteExists("99@testing.com", 2, 0);
		page.zMailApp.zVerifyAutocompleteExists("990@testing.com", 3, 0);
		page.zMailApp.zVerifyAutocompleteExists("991@testing.com", 4, 0);
		page.zMailApp.zVerifyAutocompleteExists("992@testing.com", 5, 0);
		page.zMailApp.zVerifyAutocompleteExists("993@testing.com", 6, 0);
		page.zMailApp.zVerifyAutocompleteExists("994@testing.com", 7, 0);
		page.zMailApp.zVerifyAutocompleteExists("995@testing.com", 8, 0);
		page.zMailApp.zVerifyAutocompleteExists("996@testing.com", 9, 0);
		page.zMailApp.zVerifyAutocompleteExists("997@testing.com", 10, 0);
		page.zMailApp.zVerifyAutocompleteExists("998@testing.com", 11, 0);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		needReset = false;
	}

	//------------------------------autocomplete_functions----------------------
	private static void createContacts(String[] contacts, Boolean withName)
			throws Exception {
		zGoToApplication("Address Book");
		if (config.getString("browser").contains("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		int lenAccounts = contacts.length;
		for (int i = 0; i <= lenAccounts - 1; i++) {
			obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
			zWaitTillObjectExist("editfield", page.zABCompose.zLastEditField);
			if (withName.equals(true)) {
				obj.zEditField.zActivateAndType(
						page.zABCompose.zFirstEditField,
						contacts[i].split("@")[0]);
				obj.zEditField.zActivateAndType(page.zABCompose.zLastEditField,
						config.getString("locale").replace("_", ""));
			}
			obj.zEditField.zActivateAndType(page.zABCompose.zEmail1EditField,
					contacts[i]);
			obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		}
		Thread.sleep(1500);
	}

	private static void sendMails() throws Exception {
		page.zComposeView.zComposeAndSendMail(acc2 + "; " + acc4 + "; " + acc5,
				"", "", "testSubject", "testBody", "");

		page.zComposeView.zComposeAndSendMail(acc4 + "; " + acc5, "", "",
				"testSubject", "testBody", "");

		page.zComposeView.zComposeAndSendMail(acc5 + "; " + acc1, "", "",
				"testSubject", "testBody", "");

		page.zComposeView.zComposeAndSendMail(acc3, "", "", "testSubject",
				"testBody", "");

		page.zComposeView.zComposeAndSendMail(acc3 + "; " + acc1, "", "",
				"testSubject", "testBody", "");

		page.zComposeView.zComposeAndSendMail(acc3, "", "", "testSubject",
				"testBody", "");
	}

	private static void sendMailsUpdated() throws Exception {
		page.zComposeView.zComposeAndSendMail(acc1 + "; " + acc2 + "; " + acc4
				+ "; " + acc5, "", "", "testSubject", "testBody", "");
		page.zComposeView.zComposeAndSendMail(acc1 + "; " + acc2 + "; " + acc5,
				"", "", "testSubject", "testBody", "");
		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
	}

	private static void verifyAutocomplete(Boolean rankMatters)
			throws Exception {
		Thread.sleep(2000);
		if (rankMatters.equals(true)) {
			Assert
					.assertTrue(
							"Verifying first autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_0')]//td[contains(text(), '"
											+ acc3 + "')]"));

			Assert
					.assertTrue(
							"Verifying second autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_1')]//td[contains(text(), '"
											+ acc5 + "')]"));

			Assert
					.assertTrue(
							"Verifying third autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_2')]//td[contains(text(), '"
											+ acc1 + "')]"));

			Assert
					.assertTrue(
							"Verifying fourth autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_3')]//td[contains(text(), '"
											+ acc4 + "')]"));

			Assert
					.assertTrue(
							"Verifying fifth autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_4')]//td[contains(text(), '"
											+ acc2 + "')]"));
			obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		} else {
			Assert
					.assertTrue(
							"Verifying first autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_0')]//td[contains(text(), '"
											+ acc1 + "')]"));

			Assert
					.assertTrue(
							"Verifying second autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_1')]//td[contains(text(), '"
											+ acc2 + "')]"));

			Assert
					.assertTrue(
							"Verifying third autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_2')]//td[contains(text(), '"
											+ acc3 + "')]"));

			Assert
					.assertTrue(
							"Verifying fourth autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_3')]//td[contains(text(), '"
											+ acc4 + "')]"));

			Assert
					.assertTrue(
							"Verifying fifth autocomplete list rank",
							selenium
									.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_4')]//td[contains(text(), '"
											+ acc5 + "')]"));
		}
	}

	private static void verifyAutocompleteUpdated() throws Exception {
		Thread.sleep(2000);
		Assert
				.assertTrue(
						"Verifying first autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_0')]//td[contains(text(), '"
										+ acc5 + "')]"));

		Assert
				.assertTrue(
						"Verifying second autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_1')]//td[contains(text(), '"
										+ acc1 + "')]"));

		Assert
				.assertTrue(
						"Verifying third autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_2')]//td[contains(text(), '"
										+ acc2 + "')]"));

		Assert
				.assertTrue(
						"Verifying fourth autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_3')]//td[contains(text(), '"
										+ acc3 + "')]"));

		Assert
				.assertTrue(
						"Verifying fifth autocomplete list rank",
						selenium
								.isElementPresent("//div[contains(@class, 'ZmAutocompleteListView')]//div[contains(@id, 'AutoCompleteListViewDiv_4')]//td[contains(text(), '"
										+ acc4 + "')]"));
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
	}

	private static void getKeyboardKeys(String accont) throws Exception {
		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("zh_HK")) {
			first = accont.substring(0, 1);
			second = accont.substring(1, 2);
			third = accont.substring(2, 3);
			fourth = accont.substring(3, 4);
			fifth = accont.substring(4, 5);
			sixth = accont.substring(5, 6);
			seventh = accont.substring(6, 7);
			eighth = accont.substring(7, 8);
		} else {
			first = accont.substring(0, 1);
			second = accont.substring(1, 2);
			third = accont.substring(2, 3);
			fourth = accont.substring(3, 4);
			fifth = accont.substring(4, 5);
			sixth = accont.substring(5, 6);
		}
	}

	private static void typeKeyboardKeys() throws Exception {
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		if (config.getString("locale").equals("en_US")
				|| config.getString("locale").equals("en_GB")
				|| config.getString("locale").equals("en_AU")
				|| config.getString("locale").equals("pt_BR")
				|| config.getString("locale").equals("zh_CN")
				|| config.getString("locale").equals("zh_HK")) {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth + "," + seventh + "," + eighth);
		} else {
			pressKeys(first + "," + second + "," + third + "," + fourth + ","
					+ fifth + "," + sixth);
		}
	}

	private static void verifySpecialCharAutoComplete(String verifyAcc)
			throws Exception {
		if (verifyAcc.equals("acc1")) {
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
			pressKeys("1");
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
			pressKeys("space");
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
			pressKeys(config.getString("locale").substring(0, 1));
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
			pressKeys(config.getString("locale").substring(1, 2));
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1, 1);
			pressKeys("y");
			page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1, 1);
		} else if (verifyAcc.equals("acc2")) {
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1, 1);
			pressKeys("2");
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1, 1);
			pressKeys("space");
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1, 1);
			pressKeys(config.getString("locale").substring(0, 1));
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1, 1);
			pressKeys(config.getString("locale").substring(1, 2));
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1, 1);
			pressKeys("z");
			page.zMailApp.zVerifyAutocompleteNotExists(acc2, 1, 1);
		}
	}

	public static void zVerifyIsColonAutocompleteExists(String value, int rank)
			throws Exception {
		Assert
				.assertTrue(
						"Verifying is: autocomplete list rank " + rank
								+ " for " + value,
						selenium
								.isElementPresent("//div[contains(@id, 'AutoCompleteListViewDiv_"
										+ (rank - 1)
										+ "') and contains(text(), '"
										+ value
										+ "')]"));
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