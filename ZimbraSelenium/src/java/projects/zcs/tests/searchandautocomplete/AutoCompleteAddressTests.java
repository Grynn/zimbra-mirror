package projects.zcs.tests.searchandautocomplete;

import java.awt.Robot;
import java.awt.event.KeyEvent;
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
public class AutoCompleteAddressTests extends CommonTest {
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
				|| test.equals("unableToGetRidOfEmailedContacts_Bug40081")) {
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
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
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
		zGoToApplication("Mail");
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
		zGoToApplication("Mail");
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
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
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
		zGoToApplication("Mail");
		typeKeyboardKeys();
		verifyAutocomplete(false);
		pressKeys("1");
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5);
		pressKeys("space");
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5);
		pressKeys(config.getString("locale").substring(0, 1));
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5);
		pressKeys(config.getString("locale").substring(1, 2));
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5);
		pressKeys("x");
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1);
		page.zMailApp.zVerifyAutocompleteNotExists(acc2, 2);
		page.zMailApp.zVerifyAutocompleteNotExists(acc3, 3);
		page.zMailApp.zVerifyAutocompleteNotExists(acc4, 4);
		page.zMailApp.zVerifyAutocompleteNotExists(acc5, 5);
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
		pressKeys("v,m");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
		pressKeys("1");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
		pressKeys("w,a,r,e");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
		pressKeys("space");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
		pressKeys("f");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
		pressKeys("i");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
		pressKeys("3");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
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

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		obj.zTextAreaField.zActivate(page.zComposeView.zToField);
		pressKeys("i,t");
		pressKeys("space");
		pressKeys("d");
		page.zMailApp.zVerifyAutocompleteExists(groupName, 1);
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
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1);
		zRobot.keyPress(KeyEvent.VK_COMMA);
		zRobot.keyRelease(KeyEvent.VK_COMMA);
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1);
		pressKeys("space");
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1);
		pressKeys(char6 + "," + char7 + "," + char8 + "," + char9 + ","
				+ char10);
		page.zMailApp.zVerifyAutocompleteExists(displayName, 1);
		page.zMailApp.zVerifyAutocompleteExists(selfAccountName.toLowerCase(),
				1);

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
	public void autocompleteOnSpecialCharacters(String from, String to,
			String cc, String bcc, String subject, String body,
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
		zGoToApplication("Mail");
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
	 * delete contact from Trash folder, verify autocomplete not exists. Also
	 * checked by refreshing UI.
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
		page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
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

		zGoToApplication("Mail");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		zGoToApplication("Address Book");
		obj.zFolder.zClick(localize(locator.trash));
		Thread.sleep(1000);
		zRobot.keyPress(KeyEvent.VK_CONTROL);
		zRobot.keyPress(KeyEvent.VK_A);
		zRobot.keyRelease(KeyEvent.VK_CONTROL);
		zRobot.keyRelease(KeyEvent.VK_A);
		pressKeys("delete");

		zGoToApplication("Mail");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1);
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);

		selenium.refresh();
		Thread.sleep(3500);
		zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		typeKeyboardKeys();
		page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1);
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
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
			pressKeys("1");
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
			pressKeys("space");
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
			pressKeys(config.getString("locale").substring(0, 1));
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
			pressKeys(config.getString("locale").substring(1, 2));
			page.zMailApp.zVerifyAutocompleteExists(acc1, 1);
			pressKeys("y");
			page.zMailApp.zVerifyAutocompleteNotExists(acc1, 1);
		} else if (verifyAcc.equals("acc2")) {
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1);
			pressKeys("2");
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1);
			pressKeys("space");
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1);
			pressKeys(config.getString("locale").substring(0, 1));
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1);
			pressKeys(config.getString("locale").substring(1, 2));
			page.zMailApp.zVerifyAutocompleteExists(acc2, 1);
			pressKeys("z");
			page.zMailApp.zVerifyAutocompleteNotExists(acc2, 1);
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