//Test cases related to address book
//Prashant Jaiswal

package projects.html.tests.addressbook;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.omg.CORBA.PUBLIC_MEMBER;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.SleepUtil;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;
import projects.zcs.clients.ProvZCS;
import sun.misc.GC.LatencyRequest;

@SuppressWarnings( { "static-access", "unused" })
public class AddressBookTestHtml extends CommonTest {

	@DataProvider(name = "ABDataProvider")
	private Object[][] createData(Method method) {
		String test = method.getName();
		String targetAddressBookFolder = localize(locator.emailedContacts);
		// final String lastName = getLocalizedData_NoSpecialChar();
		if (test.equals("createContactInHtml")
				|| test.equals("deleteContactAndVerify")
				|| test.equals("searchContact")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(), "", "",
					"" } };
		} else if (test.equals("moveContactAndVerify")) {
			return new Object[][] { {
					"lastNameForMove:" + getLocalizedData_NoSpecialChar(), "",
					"", "", targetAddressBookFolder } };
		} else if (test.equals("importFromCSVAndVerify")) {
			return new Object[][] { { "contactZimbra.csv", "contactsGmail.csv",
					"contactYahoo.csv", "contactOutlook.CSV" } };
		} else if (test.equals("createContactGroupAndVerify")) {
			return new Object[][] { {
					"GrpName:" + getLocalizedData_NoSpecialChar(), 3 } };
		} else if (test.equals("verifyFileAS")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar(), "",
					getLocalizedData_NoSpecialChar() } };
		}

		else

			return new Object[][] { {} };
	}

	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	private void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Test to verify UI objects in Contact tab.
	 * 
	 * @throws Exception
	 *             
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyContactsUI() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zVerifyABUI();

		needReset = false;
	}

	/**
	 * Test to create a basic contact in html client and verify it
	 * 
	 * @param lastName
	 * @param middleName
	 * @param firstName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContactInHtml(String lastName, String middleName,
			String firstName, String email) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zCreateBasicContact(lastName, middleName,
				firstName, "", "");
		SleepUtil.sleepSmall();		
		// To verify Contact created toaster message
		page.zABComposeHTML.zVerifyContactToasterMsgs(obj.zToastAlertMessage
				.zGetMsg(), localize(locator.contactCreated));

		// To verify contact is created
		obj.zCheckbox.zExists("link=" + lastName);

		needReset = false;
	}

	/**
	 * To edit and verify the contact is edited correctly or not
	 * 
	 * @param lastName
	 * @param middleName
	 * @param firstName
	 * @param newLastName
	 * @param newMiddleName
	 * @throws Exception
	 */

	@Test(dataProvider = "ABDataProvider", dependsOnMethods = "createContactInHtml", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editAndVerifyContactInHtml() throws Exception {
		if (isExecutionARetry)
			createContactInHtmlReusable();

		obj.zButton.zClick(page.zABComposeHTML.zEditIconBtn);
		SleepUtil.sleepSmall();
		//page.zABComposeHTML.zSelectAndClickEdit("oldlastName","upperToolbar");
		String newLastName = getLocalizedData(1);
		page.zABComposeHTML.zModifyContact(newLastName, "", "", "", "");
		SleepUtil.sleepMedium();

		// To verify Contact Modified toaster message
		page.zABComposeHTML.zVerifyContactToasterMsgs(obj.zToastAlertMessage
				.zGetMsg(), localize(locator.contactModified));

		// To verify edited contact
		Assert.assertTrue(page.zABComposeHTML.zVerifyEditContact(newLastName,
				"", "", ""), "The contact is not modified successfully");
		obj.zButton.zClick(page.zABComposeHTML.zSaveNewContactIconBtn);
		needReset = false;
	}

	/**
	 * To delete contact and verify
	 * 
	 * @param lastName
	 * @param middleName
	 * @param firstName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteContactAndVerify(String lastName, String middleName,
			String firstName, String email) throws Exception {
		if (isExecutionARetry)
			createContactInHtmlReusable();
		String tmp = lastName;
		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zCreateBasicContact(lastName, middleName,
				firstName, email, "");
		page.zABComposeHTML.zDeleteContactAndVerify(lastName);
		// to add toaster message check for delete contact

		needReset = false;
	}

	/**
	 * test to create and verify a new address book folder
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAddressBook() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String addressBookName = getLocalizedData(1);
		page.zABComposeHTML.zNavigateToContact();

		page.zABComposeHTML.zCreateAB(addressBookName);
		SleepUtil.sleepMedium();

		// To verify the toaster message "Address book moved to Trash
		String expectedToastMsg = localize(locator.actionAddressBookCreated);
		String[] splitedExpectedToastMsg = expectedToastMsg.split("{0}");

		obj.zToastAlertMessage.zAlertMsgExists(splitedExpectedToastMsg[0],
				"1st part of the Created address book moved is not proper");

		obj.zToastAlertMessage.zAlertMsgExists(splitedExpectedToastMsg[1],
				"2nd part of the Created address book is not proper");

		// obj.zFolder.zExists(addressBookName); //zFolder.zExists is not
		// working and has been mailed to raja by kk on 24-10.
		needReset = false;
	}

	/**
	 * To delete AB folder and verify the toaster message
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteAddressBook() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String addressBookName = getLocalizedData(1);
		page.zABComposeHTML.zNavigateToContact();

		page.zABComposeHTML.zCreateAB(addressBookName);

		page.zABComposeHTML.zDeleteAB(addressBookName);

		// To verify the toaster message "Address book moved to Trash
		String expectedToastMsg = localize(locator.actionAddressBookMovedToTrash);
		String[] splitedExpectedToastMsg = expectedToastMsg.split("{0}");

		obj.zToastAlertMessage.zAlertMsgExists(splitedExpectedToastMsg[0],
				"1st part of the Address Book moved to Trash is not proper");

		obj.zToastAlertMessage.zAlertMsgExists(splitedExpectedToastMsg[1],
				"2nd part of the Address Book moved to Trash is not proper");

		needReset = false;
	}

	/**
	 * Test to move contact from one AB folder to other and verify
	 * 
	 * @param lastName
	 * @param middleName
	 * @param firstName
	 * @param targetAB
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveContactAndVerify(String lastName, String middleName,
			String firstName, String email, String targetAB) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		String addressBookName = getLocalizedData(1);
		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zCreateBasicContact(lastName, middleName,
				firstName, email, "");
		page.zABComposeHTML.zMoveContactAndVerify(lastName, targetAB);

		needReset = false;
	}

	/**
	 * 
	 * To modify a AB and verify the toaster message
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void modifyABColorAndVerifyToasterMsg() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zNavigateToNewABPage();
		SleepUtil.sleepSmall();
		page.zABComposeHTML.zChangeABColor(localize(locator.purple));

		// To verify the toaster message for AB updated
		page.zABComposeHTML.zVerifyContactToasterMsgs(obj.zToastAlertMessage
				.zGetMsg(), localize(locator.addressBookUpdated));

		needReset = false;
	}

	/**
	 * To permanently delete all the contacts from Contacts AB and verify
	 * toaster message
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void permanentlyDeleteAllContacts() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zNavigateToNewABPage();

		page.zABComposeHTML.zPermanentDelAllContact();

		// To verify address book emptied toaster message
		String expectedToastMsg = localize(locator.addressBookEmptied);
		String[] splitedExpectedToastMsg = expectedToastMsg.split("{0}");
		SleepUtil.sleepMedium();
		obj.zToastAlertMessage.zAlertMsgExists(splitedExpectedToastMsg[0],
				"1st part of the Address Book contacts emptied is not proper");

		obj.zToastAlertMessage.zAlertMsgExists(splitedExpectedToastMsg[1],
				"2nd part of the Address Book contacts emptied is not proper");

		needReset = false;
	}

	/**
	 * This test is to search the contact using upper toolbar with last name and
	 * bottom toolbar with email. First it creates contact with last name only
	 * for upper toolbar and then search Second it creates contact with email
	 * also for bottom toolbar and then search
	 * 
	 * @param lastName
	 * @param middleName
	 * @param firstName
	 * @param email
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void searchContact(String lastName, String middleName,
			String firstName, String email) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zCreateContactAndSearch(lastName, "", "", "", "",
				"UpperToolbar");
		SleepUtil.sleepSmall();
		obj.zCheckbox.zExists("link=" + lastName);

		obj.zButton.zClick(localize(locator.refresh), "2");
		SleepUtil.sleepLong();
		lastName = getLocalizedData_NoSpecialChar();
		String email1 = ProvZCS.getRandomAccount();
		page.zABComposeHTML.zCreateContactAndSearch(lastName, "", "", email1,
				"", "BottomToolbar");
		obj.zCheckbox.zExists("link=" + lastName);

		needReset = false;
	}

	/**
	 * This negative test is to test the toaster message when trying to save a
	 * empty contact
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContactNegativeTest() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		page.zABComposeHTML.zNavigateToContact();

		// to verify "Saving empty contact"

		page.zABComposeHTML.zNegativeTestSaveEmptyContactsOrGroup("Contact");

		needReset = false;
	}

	/**
	 * 
	 * To import contact from zimbra,yahoo,gmail and outlook CSV files and to
	 * check the imported contact has corresponding fields displayed properly in
	 * Zimbra
	 * 
	 * @param csvFileName
	 * @param englishContact
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void importFromCSVAndVerify(String zimbraCSV, String gmailCSV,
			String yahooCSV, String outLookCSV) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABComposeHTML.zNavigateToContact();

		page.zABComposeHTML.zNavigateToNewABPageAndImportContact(zimbraCSV);
		SleepUtil.sleepMedium();
		page.zABComposeHTML.zNavigateToContactAndVerifyImportedContactDisplay(
				"link=" + "lastName" + "," + " " + "firstName", "Zimbra");

		page.zABComposeHTML.zNavigateToNewABPageAndImportContact(gmailCSV);
		SleepUtil.sleepMedium();
		page.zABComposeHTML.zNavigateToContactAndVerifyImportedContactDisplay(
				"NameGmail", "Gmail");

		page.zABComposeHTML.zNavigateToNewABPageAndImportContact(outLookCSV);
		SleepUtil.sleepMedium();
		page.zABComposeHTML.zNavigateToContactAndVerifyImportedContactDisplay(
				"link=" + "lastNameOutlook" + "," + " " + "firstNameOutlook",
				"Outlook");

		page.zABComposeHTML.zNavigateToNewABPageAndImportContact(yahooCSV);
		SleepUtil.sleepMedium();
		page.zABComposeHTML.zNavigateToContactAndVerifyImportedContactDisplay(
				"lastNameYahoo" + "," + " " + "firstNameYahoo", "Yahoo");

		needReset = false;
	}

	/**
	 * Test to create a contact group with the specified no of contacts and to
	 * verify by going to email-compose. Select the item from the contact-list.
	 * Then verify if all contacts are displayed.
	 * 
	 * @param contactGroupName
	 * @param noOfAcc
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContactGroupAndVerify(String contactGroupName, int noOfAcc)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String acc;
		String commaSeparatedAccForGroup = "";

		// Following for loop is to create some no of acc and append these acc
		// in comma separated string which is passed to create contact
		// group function
		for (int i = 0; i < noOfAcc; i++) {
			acc = ProvZCS.getRandomAccount();
			if (i == noOfAcc - 1) {
				commaSeparatedAccForGroup = commaSeparatedAccForGroup + acc;
			} else {
				commaSeparatedAccForGroup = commaSeparatedAccForGroup + acc
						+ ",";
			}
		}

		page.zABComposeHTML.zNavigateToContact();

		page.zABComposeHTML.zCreateContactGroup(contactGroupName,
				commaSeparatedAccForGroup, noOfAcc);
		SleepUtil.sleepMedium();
		page.zABComposeHTML.zVerifyContactToasterMsgs(obj.zToastAlertMessage
				.zGetMsg(), localize(locator.contactGroupCreated));
		obj.zCheckbox.zExists("link=" + contactGroupName);

		page.zABComposeHTML.zVerifyContactGrpContactsInCompose(
				contactGroupName, commaSeparatedAccForGroup, noOfAcc);
		
		obj.zButton.zClick(page.zComposeView.zSendBtn);

		needReset = false;

	}

	/**
	 *This negative test is to verify the toaster message when tried to save a
	 * empty contact group
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void negativeTestSaveEmptypContactGroup() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		page.zABComposeHTML.zNavigateToContact();

		// to verify "saving empty contact group"
		page.zABComposeHTML
				.zNegativeTestSaveEmptyContactsOrGroup("ContactGroup");

		needReset = false;

	}

	/**
	 * This negative test is to verify the toaster message when tried to save
	 * contact group without any member in it.This test depends on test
	 * negativeTestSaveEmptypContactGroup()
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", dependsOnMethods = "negativeTestSaveEmptypContactGroup", groups = {
			"smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void negativeTestGroupWithoutMember() throws Exception {
		if (isExecutionARetry)
			negativeTestContactGroup();

		obj.zEditField.zType(page.zABComposeHTML.zGroupNameEditfield,
				getLocalizedData(1));
		obj.zButton.zClick(page.zABComposeHTML.zSaveNewContactIconBtn);
		page.zABComposeHTML.zVerifyContactToasterMsgs(obj.zToastAlertMessage
				.zGetMsg(), localize(locator.noContactGroupMembers));
		obj.zButton.zClick(page.zABComposeHTML.zCancelNewContactIconBtn);

		needReset = false;

	}

	/**
	 * Test to verify all contacts select check box
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void allItemsCheckUncheck() throws Exception {
		if (isExecutionARetry)
			handleRetry();

		int noOfContacts = 3;
		String commaSeparatedContacts = page.zABComposeHTML
				.zCreateCommaSeparatedString(noOfContacts);

		page.zABComposeHTML.zNavigateToCnctAndCreateMultipleCncts(noOfContacts,
				commaSeparatedContacts);

		page.zABComposeHTML.zAllItemsCheckBoxSelectUnSelectAndVerify(
				noOfContacts, commaSeparatedContacts, "select");

		page.zABComposeHTML.zAllItemsCheckBoxSelectUnSelectAndVerify(
				noOfContacts, commaSeparatedContacts, "unselect");

		page.zABComposeHTML.zSelectAllContactsAndDelete();

		page.zABComposeHTML
				.zNavigateToTrashAndVerifyDeletedContacts(commaSeparatedContacts);

		needReset = false;

	}

	/**
	 * To create a tag,then apply it to two contacts .Verify those two contacts
	 * has tag.Then verify clicking on tag shows only two contacts.Then remove
	 * tag and verify that contacts does not have tag
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zCreateAndVerifyTagForContacts() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		String tagName = getLocalizedData_NoSpecialChar();
		String commaSeparatedContacts = page.zABComposeHTML
				.zCreateCommaSeparatedString(2);

		zGoToApplication("Mail");
		page.zMailApp.zCreateTag(tagName);

		// Create a simple tag and apply it to 2 contacts and and verify if both
		// of them have tags applied
		page.zABComposeHTML.zNavigateToCnctAndCreateMultipleCncts(2,
				commaSeparatedContacts);
		page.zABComposeHTML.zSelectContactAndApplyTag(tagName,
				commaSeparatedContacts);
		SleepUtil.sleepSmall();
		page.zABComposeHTML.zVerifyContactHasTag(commaSeparatedContacts);

		// Click on Tag and see if only those 2 with tags exists
		page.zABComposeHTML.zClickTagAndVerifyAttachedContacts(tagName,
				commaSeparatedContacts);

		// select those 2 contacts and remove tag and verify tag is removed
		page.zABComposeHTML.zSelectContactAndRemoveTag(tagName,
				commaSeparatedContacts);
		SleepUtil.sleepSmall();
		page.zABComposeHTML.zVerifyContactHasNoTag(commaSeparatedContacts);
		needReset = false;

	}

	/**
	 * To create a contact with all the details and then to verify after save
	 * whether all the details are displayed properly or not
	 * 
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zVerifyDisplayedValues() throws Exception {
		if (isExecutionARetry)
			handleRetry();
		page.zABComposeHTML.zNavigateToContact();
		obj.zButton.zClick(page.zABComposeHTML.zNewContactIconBtn);
		SleepUtil.sleepSmall();
		page.zABComposeHTML.zCreateContactWithAllDetailsAndVerifyDisplay();

		needReset = false;

	}

	/**
	 * To verify different file as options in contact compose
	 * 
	 * @param lastName
	 * @param middleName
	 * @param firstName
	 * @param email
	 * @param company
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyFileAS(String lastName, String middleName,
			String firstName, String email, String company) throws Exception {
		if (isExecutionARetry)
			handleRetry();
		page.zABComposeHTML.zNavigateToContact();
		page.zABComposeHTML.zCreateBasicContact(lastName, middleName,
				firstName, "", company);
		SleepUtil.sleepSmall();
		page.zABComposeHTML.zVerifyFileAsOptions(lastName, firstName, company);

		needReset = false;
	}

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	}

	private void createContactInHtmlReusable() throws Exception {
		handleRetry();
		createContactInHtml("lastName:" + getLocalizedData(1), "middleName:"
				+ getLocalizedData(1), "", "");
	}

	private void negativeTestContactGroup() throws Exception {
		handleRetry();
		negativeTestSaveEmptypContactGroup();
	}
}
