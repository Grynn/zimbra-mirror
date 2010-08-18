package projects.html.ui;

import java.io.File;

import org.testng.Assert;

import framework.core.SelNGBase;
import framework.util.ZimbraSeleniumProperties;

import projects.html.tests.CommonTest;

/**
 *This class has UI level static id's and methods for html contact related
 * tests Prashant Jaiswal
 */
/**
 * @author VICKY JAISWAL
 * 
 */
@SuppressWarnings("static-access")
public class ABComposeHtml extends CommonTest {
	public static final String zContactTabIcon = "id=tab_ikon_contacts";
	public static final String zNewContactIconBtn = "id=INEW_CONTACT";
	public static final String zPrintIconBtn = "id=IOPPRINT";
	public static final String zDeleteIconBtn = "id=IOPDELETE";
	public static final String zMoveIconBtn = "id=SOPMOVE";
	public static final String zSearchFieldIcon = "id=searchField";
	public static final String zSearchIconBtn = "id=SSEARCH_CONTACT";
	public static final String zFindContactEditfield = "name=contactsq";
	public static final String zGoIconBtn = "id=SOPGO";
	public static final String zEditIconBtn = "id=IOPEDIT";
	public static final String zNextPageArrowIcon = "id=NEXT_PAGE";
	public static final String zPrevPageArrowIcon = "id=PREV_PAGE";
	public static final String zAllItemsCheckbox = "id=OPCHALL";
	// New Contact Compose related Id's
	public static final String zCancelNewContactIconBtn = "id=IOPCANCEL";
	public static final String zSaveNewContactIconBtn = "id=IOPSAVE";

	public static final String zLastNameEditField = "id=lastName";
	public static final String zMiddleNameEditField = "id=middleName";
	public static final String zFirstNameEditField = "id=firstName";
	public static final String zEmailEditField = "id=email";
	public static final String zCompanyEditField = "id=company";

	public static final String zFileAsMenu = "id=fileAS";

	public static final String zJobTitleEditField = "id=jobTitle";
	public static final String zEmail2EditField = "id=email2";
	public static final String zEmail3EditField = "id=email3";

	public static final String zworkStreetEditField = "id=workStreet";
	public static final String zworkCityEditField = "id=workCity";
	public static final String zworkStateEditField = "id=workState";
	public static final String zworkPostalCodeEditField = "id=workPostalCode";
	public static final String zworkCountryEditField = "id=workCountry";
	public static final String zworkURLEditField = "id=workURL";
	public static final String zworkPhoneEditField = "id=workPhone";
	public static final String zworkPhone2EditField = "id=workPhone2";
	public static final String zworkFaxEditField = "id=workFax";
	public static final String zassistantPhoneEditField = "id=assistantPhone";
	public static final String zcompanyPhoneEditField = "id=companyPhone";
	public static final String zcallbackPhoneEditField = "id=callbackPhone";

	public static final String zhomeStreetEditField = "id=homeStreet";
	public static final String zhomeCityEditField = "id=homeCity";
	public static final String zhomeStateEditField = "id=homeState";
	public static final String zhomePostalCodeEditField = "id=homePostalCode";
	public static final String zhomeCountryEditField = "id=homeCountry";
	public static final String zhomeURLEditField = "id=homeURL";
	public static final String zhomePhoneEditField = "id=homePhone";
	public static final String zhomePhone2EditField = "id=homePhone2";
	public static final String zhomeFaxEditField = "id=homeFax";
	public static final String zmobilePhoneEditField = "id=mobilePhone";
	public static final String zpagerEditField = "id=pager";
	public static final String zcarPhoneEditField = "id=carPhone";

	public static final String zotherStreetEditField = "id=otherStreet";
	public static final String zotherCityEditField = "id=otherCity";
	public static final String zotherStateEditField = "id=otherState";
	public static final String zotherPostalCodeEditField = "id=otherPostalCode";
	public static final String zotherCountryEditField = "id=otherCountry";
	public static final String zotherURLEditField = "id=otherURL";
	public static final String zotherPhoneEditField = "id=otherPhone";
	public static final String zotherFaxEditField = "id=otherFax";

	public static final String znotesTextarea = "id=notes"; // textarea

	// New contact group related Id's
	public static final String zNewGroupIconBtn = "id=INEW_GROUP";
	public static final String zNewGroupNameEditfield = "id=nickname";
	public static final String zFindEditfield = "name=contactSearchQuery";
	public static final String zInContactLocationEditfield = "name=contactLocation";
	public static final String zSearchContacsBtn = "id=doSearch";
	public static final String zAddSelectedlink = "name=actionContactAdd";
	public static final String zSearchedContactForGrp = "name=addToGroup";
	public static final String zAddRecipientsBtn = "id=IOPADDRECIP";
	public static final String zComposeAddToChkbox = "name=addTo";
	public static final String zContactDoneBtn = "name=actionContactDone";
	public static final String zComposeToTextarea = "id=toField";
	public static final String zGroupNameEditfield = "name=nickname";

	// Address Book edit related Id's
	public static final String zNewABIconBtn = "id=IOPNEWADDRBOOK";
	public static final String zNewABNameEditField = "id=newName";
	public static final String zNewABColorList = "id=color";
	public static final String zCreateNewABBtn = "id=OPSAVE";
	public static final String zABColorMenu = "id=folderColor";
	public static final String zPermanentlyDelAllContact = "id=emptyConfirm";
	public static final String zDelABChckbox = "id=deleteConfirm";
	public static final String zImportContactEditField = "id=export";

	public static final String zABFolderId = "name=folderId";
	public static final String zMoreActionsMenu = "name=actionOp";
	public static final String zDeleteABBtn = "name=actionDelete";
	public static final String zDeleteAllContactsBtn = "name=actionEmptyFolderConfirm";
	public static final String zImportBtn = "name=actionImport";

	// AddressBook preferences relates id's
	public static final String zContactsPerPage = "id=itemsPP";
	public static final String zEnableAutoAdding = "id=zimbraPrefAutoAddAddressEnabled";
	public static final String zPrefSaveButton = "name=actionSave";

	/**
	 * 
	 * To navigate to contact tab
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToContact() throws Exception {

		Thread.sleep(1000);// FF breaks randomly if we dont wait
		obj.zButton.zClick(zContactTabIcon);
		Thread.sleep(2000);
		zWaitTillObjectExist("button", zNewContactIconBtn);
	}

	/**
	 * To navigate to Address Book edit page
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToNewABPage() throws Exception {
		obj.zFolder.zEdit(localize(locator.addressBooks));
		Thread.sleep(1500);
	}

	/**
	 * To navigate to prefrences-address book
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToPreferenceAB() throws Exception {
		obj.zButton.zClick("id=TAB_OPTIONS");
		// obj.zTab.zClick(localize(locator.preferences));
		Thread.sleep(2000);
		obj.zTab.zClick(localize(locator.addressBook), "2");

	}

	/**
	 * 
	 * @param cancelIcon
	 */
	public static void zVerifyCancelBtnAndClick(String cancelIcon) {
		if (obj.zButton.zExistsDontWait(cancelIcon).equals("true")) {
			obj.zButton.zClick(cancelIcon);
		}
	}

	/**
	 * To select the AB folder like Contacts,Emailed contacts
	 * 
	 * @param folderName
	 * @throws Exception
	 */
	public static void zSelectABFolder(String folderName) throws Exception {
		Thread.sleep(1500);
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("pl")) {
			obj.zHtmlMenu.zClick(zABFolderId,
					folderName.substring(0, 8) + ".*", "2");
		} else if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")) {
			obj.zHtmlMenu
					.zClick(zABFolderId, folderName.substring(0, 8) + ".*");
		} else {
			obj.zHtmlMenu.zClick(zABFolderId, folderName);
		}
	}

	/**
	 * To click select more actions from more actions menu
	 * 
	 * @param actionName
	 */
	public static void zSelectMoreActions(String actionName) throws Exception {
		Thread.sleep(500);
		obj.zHtmlMenu.zClick("name=actionOp", actionName);
		Thread.sleep(1000);
	}

	/**
	 * To verify if the button exist and to click on it
	 * 
	 * @param buttonName
	 * @throws Exception
	 */
	public static void zVerifyBtnExistAndClick(String buttonName)
			throws Exception {
		if (obj.zButton.zExistsDontWait(buttonName).equals("true")) {
			obj.zButton.zClick(buttonName);
			Thread.sleep(1000);
		}
	}

	/**
	 * To verify if the AB folder exist and to click on it
	 * 
	 * @param folderName
	 * @throws Exception
	 */
	public static void zClickOnContactFolder(String folderName)
			throws Exception {
		if (obj.zFolder.zExistsDontWait(folderName).equals("true")) {
			obj.zFolder.zClick(folderName);
			Thread.sleep(1000);
		}
	}

	/**
	 * To verify lastname/groupname edit filed exist in new contact/new group
	 * compose respectively and click on cancel button
	 * 
	 * @param type
	 * @throws Exception
	 */
	public static void zVerifyFieldExistAndClickCancel(String type)
			throws Exception {
		if (type.equals("NewContact") || type.equals("EditContact")) {
			obj.zEditField.zExists(zLastNameEditField);
			zVerifyBtnExistAndClick(zCancelNewContactIconBtn);
		} else if (type.equals("ContactGroup")) {
			obj.zEditField.zExists(zGroupNameEditfield);
			zVerifyBtnExistAndClick(zCancelNewContactIconBtn);
			Thread.sleep(1000);
		}
	}

	/**
	 * To verify different UI objects in Contacts tab in html client
	 * 
	 * @throws Exception
	 */
	public static void zVerifyABUI() throws Exception {

		obj.zButton.zClick(zNewContactIconBtn);
		Thread.sleep(1000);
		zVerifyFieldExistAndClickCancel("NewContact");

		// To click and verify bottom tool bar new contact button
		obj.zButton.zClick(zNewContactIconBtn, "2");
		Thread.sleep(1000);
		zVerifyFieldExistAndClickCancel("NewContact");

		obj.zButton.zClick(zNewGroupIconBtn);
		Thread.sleep(1000);
		zVerifyFieldExistAndClickCancel("ContactGroup");

		// To click and verify bottom tool bar new contact group button
		obj.zButton.zClick(zNewGroupIconBtn, "2");
		Thread.sleep(1000);
		zVerifyFieldExistAndClickCancel("ContactGroup");

		zVerifyBtnExistAndClick(zDeleteIconBtn);

		// to verify move to menu item selection
		zSelectABFolder(localize(locator.contacts));
		zSelectABFolder(localize(locator.emailedContacts));

		zVerifyBtnExistAndClick(zMoveIconBtn);
		Thread.sleep(2000);
		obj.zEditField.zType(zFindContactEditfield, getLocalizedData(1));
		Thread.sleep(1000);
		obj.zButton.zClick(zSearchIconBtn, "1");
		Thread.sleep(5000);

		String folderToBeClicked = localize(locator.emailedContacts);
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("nl")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("es")) {
			String[] testArray = folderToBeClicked.split(" ");
			folderToBeClicked = testArray[0] + " ...";
		} else if (!(ZimbraSeleniumProperties.getStringProperty("locale").equals("nl") || ZimbraSeleniumProperties.getStringProperty("locale").equals("hi"))) {
			if (folderToBeClicked.length() > 16) {
				String[] testArray = folderToBeClicked.split(" ");
				if (testArray[2].length() > 3) {
					folderToBeClicked = testArray[0] + " " + testArray[1] + " "
							+ "...";
				} else {
					folderToBeClicked = testArray[0] + " " + testArray[1] + " "
							+ testArray[2] + " " + "...";
				}
			}
		}
		zClickOnContactFolder(folderToBeClicked);

		zClickOnContactFolder(localize(locator.trash));

		obj.zButton.zExists(localize(locator.emptyTrash));

		zClickOnContactFolder(localize(locator.contacts));

	}

	/**
	 * To navigate to contact and to create specified number of contacts
	 * 
	 * @param numberOfContacts
	 * @param commaSeparatedContacts
	 *            -- last name of contacts separated by comma
	 * @throws Exception
	 */
	public static void zNavigateToCnctAndCreateMultipleCncts(
			int numberOfContacts, String commaSeparatedContacts)
			throws Exception {
		zNavigateToContact();
		String[] lastName = commaSeparatedContacts.split(",");
		for (int i = 0; i < numberOfContacts; i++) {
			zCreateBasicContact(lastName[i], "", "", "", "");
			Thread.sleep(2500);
		}
	}

	/**
	 * 
	 * To create basic contact in html client
	 * 
	 * @param lastName
	 * @param MiddleName
	 * @param firstName
	 * @throws Exception
	 */
	public static void zCreateBasicContact(String lastName, String MiddleName,
			String firstName, String email, String company) throws Exception {
		obj.zFolder.zClick(localize(locator.contacts));
		zCreateContactInAB(localize(locator.contacts), lastName, MiddleName,
				firstName, email, company);
		Thread.sleep(1000);
	}

	/**
	 * To create basic contact in specific addressbook
	 * 
	 * @param AddBook
	 * @param lastName
	 * @param MiddleName
	 * @param firstName
	 * @throws Exception
	 */
	public static void zCreateContactInAB(String AddBook, String lastName,
			String MiddleName, String firstName, String email, String company)
			throws Exception {
		obj.zButton.zClick(zNewContactIconBtn);

		zEnterBasicABData(AddBook, lastName, MiddleName, firstName, email,
				company);

		obj.zButton.zClick(zSaveNewContactIconBtn);

	}

	/**
	 * To enter data for the new contact in creation
	 * 
	 * @param AddBook
	 * @param lastName
	 * @param MiddleName
	 * @param firstName
	 */
	public static void zEnterBasicABData(String AddBook, String lastName,
			String MiddleName, String firstName, String email, String company) {
		if (lastName != "")
			obj.zEditField.zType(zLastNameEditField, lastName);
		if (MiddleName != "")
			obj.zEditField.zType(zMiddleNameEditField, MiddleName);
		if (firstName != "")
			obj.zEditField.zType(zFirstNameEditField, firstName);
		if (email != "")
			obj.zEditField.zType(zEmailEditField, email);
		if (company != "")
			obj.zEditField.zType(zCompanyEditField, company);
		if (AddBook != "") {
			// selenium.select("id=folderIdSelect", AddBook);

		}

	}

	/**
	 * To edit a contact
	 * 
	 * @param oldlastName
	 * @param newlastName
	 * @param newMiddleName
	 * @param newfirstName
	 * @param neweMail
	 * @throws Exception
	 */
	public static void zModifyContact(String newlastName, String newMiddleName,
			String newfirstName, String neweMail, String company)
			throws Exception {

		// zSelectAndClickEdit(oldlastName,"upperToolbar");

		zEnterBasicABData("", newlastName, newMiddleName, newfirstName,
				neweMail, company);
		if (neweMail != "")
			obj.zEditField.zType(localize(locator.AB_FIELD_email), neweMail);

		obj.zButton.zClick(zSaveNewContactIconBtn);

	}

	/**
	 * To select a contact and click on edit button
	 * 
	 * @param contactName
	 * @param type
	 *            : upperToolbar/bottomToolbar
	 * @throws Exception
	 */
	public static void zSelectAndClickEdit(String contactName, String type)
			throws Exception {
		obj.zMessageItem.zClickCheckBox(contactName);
		if (type.equals("upperToolbar")) {
			obj.zButton.zClick(zEditIconBtn);
		} else if (type.equals("bottomToolbar")) {
			obj.zButton.zClick(localize(locator.edit), "2");

		}

	}

	/**
	 * To verify contact edition
	 * 
	 * @param newlastName
	 * @param newMiddleName
	 * @param newfirstName
	 * @param neweMail
	 * @return
	 * @throws Exception
	 */
	public static boolean zVerifyEditContact(String newlastName,
			String newMiddleName, String newfirstName, String neweMail)
			throws Exception {
		String actualLastName = null;
		String actualMiddleName = null;
		String actualfirstName = null;
		String actualeMail = null;
		boolean flag = true;

		zSelectAndClickEdit(newlastName, "bottomToolbar");

		if (newlastName != "") {
			actualLastName = obj.zEditField.zGetInnerText(zLastNameEditField);
			if (!actualLastName.equals(newlastName))
				flag = false;

		}
		if (newMiddleName != "") {
			actualMiddleName = obj.zEditField
					.zGetInnerText(zMiddleNameEditField);
			if (!actualMiddleName.equals(newMiddleName))
				flag = false;
		}
		if (newfirstName != "") {
			actualfirstName = obj.zEditField.zGetInnerText(zFirstNameEditField);
			if (!actualfirstName.equals(newfirstName))
				flag = false;
		}
		if (neweMail != "") {
			actualeMail = obj.zEditField.zGetInnerText(zEmailEditField);
			if (!actualeMail.equals(neweMail))
				flag = false;
		}

		if (flag == true)
			return true;
		else
			return false;

	}

	/**
	 * Test to delete contact and verify the deletion
	 * 
	 * @param contactName
	 * @throws Exception
	 */
	public static void zDeleteContactAndVerify(String contactName)
			throws Exception {
		obj.zMessageItem.zClickCheckBox(contactName);
		obj.zButton.zClick(zDeleteIconBtn, "2");
		Thread.sleep(1000);
		obj.zCheckbox.zNotExists("link=" + contactName);
	}

	/**
	 * To create new address book
	 * 
	 * @param addressBookName
	 * @throws Exception
	 */
	public static void zCreateAB(String addressBookName) throws Exception {
		zNavigateToNewABPage();
		obj.zButton.zClick(zNewABIconBtn);
		Thread.sleep(2000);
		obj.zEditField.zType(zNewABNameEditField, addressBookName);
		obj.zButton.zClick(zCreateNewABBtn);
		Thread.sleep(2000);
	}

	/**
	 * To delete a address book folder
	 * 
	 * @param addressBookName
	 * @throws Exception
	 */
	public static void zDeleteAB(String addressBookName) throws Exception {
		Thread.sleep(2000);
		// obj.zFolder.zClick(addressBookName);
		obj.zCheckbox.zClick(zDelABChckbox);
		obj.zButton.zClick(zDeleteABBtn);
		Thread.sleep(1000);
	}

	/**
	 * Test to move contact and verify
	 * 
	 * @param contactName
	 * @param targetAB
	 * @throws Exception
	 */
	public static void zMoveContactAndVerify(String contactName, String targetAB)
			throws Exception {
		Thread.sleep(500);
		obj.zMessageItem.zClickCheckBox(contactName);
		zSelectABFolder(targetAB);
		Thread.sleep(1000);

		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("nl")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("es")) {
			String[] testArray = targetAB.split(" ");
			targetAB = testArray[0] + " ...";
		} else if (!(ZimbraSeleniumProperties.getStringProperty("locale").equals("nl") || ZimbraSeleniumProperties.getStringProperty("locale").equals("hi"))) {
			if (targetAB.length() > 16) {
				String[] testArray = targetAB.split(" ");
				if (testArray[2].length() > 3) {
					targetAB = testArray[0] + " " + testArray[1] + " " + "...";
				} else {
					targetAB = testArray[0] + " " + testArray[1] + " "
							+ testArray[2] + " " + "...";
				}
			}
		}

		zClickOnContactFolder(targetAB);
		Thread.sleep(1000);
		obj.zCheckbox.zExists("link=" + contactName);
	}

	/**
	 * To verify the toaster message
	 * 
	 * @param actualToastMsg
	 * @param expectedToastMsg
	 */
	public static void zVerifyContactToasterMsgs(String actualToastMsg,
			String expectedToastMsg) {
		Assert.assertTrue(actualToastMsg.contains(expectedToastMsg),
				"Toaster message " + actualToastMsg
						+ " does not contain expected message "
						+ expectedToastMsg);
	}

	/**
	 * To change the color of the address book
	 * 
	 * @param color
	 * @throws Exception
	 */
	public static void zChangeABColor(String color) throws Exception {
		obj.zHtmlMenu.zClick(zABColorMenu, color);
		Thread.sleep(1000);
		obj.zButton.zClick(zCreateNewABBtn);
	}

	/**
	 * To permanently delete all the contacts
	 */
	public static void zPermanentDelAllContact() {
		obj.zCheckbox.zClick(zPermanentlyDelAllContact);
		SelNGBase.selenium.get().click(zDeleteAllContactsBtn);
		// obj.zButton.zClick(localize(locator.folderEmptyAddressBook));
	}

	/**
	 * To type in the file name in import contact edit field and to click on
	 * import contact button
	 * 
	 * @param csvFileName
	 */
	public static void zImportContact(String csvFileName) {

		File f = new File("src/java/projects/html/data/" + csvFileName);
		String path = f.getAbsolutePath();
		obj.zBrowseField.zTypeWithKeyboard(zImportContactEditField, path);
		obj.zButton.zClick(zImportBtn);

	}

	/**
	 * To create contact group
	 * 
	 * @param groupName
	 * @param commaSeparatedAccForGroup
	 *            -- contacts to be added to contact group separated by comma
	 * @param numberOfContacts
	 * @throws Exception
	 */
	public static void zCreateContactGroup(String groupName,
			String commaSeparatedAccForGroup, int numberOfContacts)
			throws Exception {
		String[] accTobeAdded = commaSeparatedAccForGroup.split(",");
		obj.zButton.zClick(zNewGroupIconBtn);
		obj.zEditField.zType(zNewGroupNameEditfield, groupName);
		for (int i = 0; i < numberOfContacts; i++) {
			obj.zEditField.zType(zFindEditfield, accTobeAdded[i], "2");
			obj.zHtmlMenu.zClick(zInContactLocationEditfield,
					localize(locator.GAL));
			// Thread.sleep(1000);
			obj.zButton.zClick(zSearchContacsBtn);
			Thread.sleep(1000);// wait need as it takes time to search a contact
			obj.zCheckbox.zClick(zSearchedContactForGrp);
			SelNGBase.selenium.get().click(zAddSelectedlink);
			Thread.sleep(2000);// wait is added as it takes some time to click
			// on Add Selected link
		}
		Thread.sleep(2000);
		obj.zButton.zClick(zSaveNewContactIconBtn);

	}

	/**
	 * To verify contact group for its member contacts in compose field
	 * 
	 * @param groupName
	 * @param commaSeparatedAccForGroup
	 * @param numberOfContacts
	 * @throws Exception
	 */
	public static void zVerifyContactGrpContactsInCompose(String groupName,
			String commaSeparatedAccForGroup, int numberOfContacts)
			throws Exception {
		String[] accToBeChecked = commaSeparatedAccForGroup.split(",");
		String[] accString;
		String finalAccString = "";
		// following for loop is needed as the contacts gets added to group and
		// are displayed in mail compose in reverse order
		for (int i = 0; i < numberOfContacts; i++) {
			accString = accToBeChecked[numberOfContacts - (i + 1)].split("@");

			if (i == numberOfContacts - 1) {
				finalAccString = finalAccString
						+ accString[i - (numberOfContacts - 1)].toLowerCase()
						+ " "
						+ "<"
						+ accToBeChecked[numberOfContacts - (i + 1)]
								.toLowerCase() + ">";
			} else if (i == 0) {
				finalAccString = finalAccString
						+ accString[i].toLowerCase()
						+ " "
						+ "<"
						+ accToBeChecked[numberOfContacts - (i + 1)]
								.toLowerCase() + ">" + "," + " ";
			} else {
				finalAccString = finalAccString
						+ accString[0].toLowerCase()
						+ " "
						+ "<"
						+ accToBeChecked[numberOfContacts - (i + 1)]
								.toLowerCase() + ">" + "," + " ";
			}
		}

		obj.zTab.zClick(localize(locator.compose));// temporary using this as
		// zNavigateToMailCompose is
		// not working
		Thread.sleep(2000);
		obj.zButton.zClick(zAddRecipientsBtn);

		// commented following 3 lines for time being
		// obj.zEditField.zType(zFindEditfield, groupName);
		// selenium.select(zInContactLocationEditfield,
		// localize(locator.searchPersonalContacts));
		// obj.zButton.zClick(zSearchContacsBtn);

		obj.zCheckbox.zClick(zComposeAddToChkbox);
		obj.zButton.zClick(zContactDoneBtn);
		Thread.sleep(1000);
		String actualAccDisplayed = obj.zTextAreaField
				.zGetInnerText(zComposeToTextarea);

		Assert.assertTrue(actualAccDisplayed.equals(finalAccString),
				"Contacts in contact group are not displayed in compose mail");

	}

	/**
	 * to save empty contact/contact group and to verify corresponding toaster
	 * messages
	 * 
	 * @param type
	 * @throws Exception
	 */
	public static void zNegativeTestSaveEmptyContactsOrGroup(String type)
			throws Exception {

		if (type.equals("Contact")) {
			obj.zButton.zClick(zNewContactIconBtn);
		} else if (type.equals("ContactGroup")) {
			obj.zButton.zClick(zNewGroupIconBtn);
		}
		obj.zButton.zClick(zSaveNewContactIconBtn);

		if (type.equals("Contact")) {
			zVerifyContactToasterMsgs(obj.zToastAlertMessage.zGetMsg(),
					localize(locator.emptyContactSave));
		} else if (type.equals("ContactGroup")) {
			zVerifyContactToasterMsgs(obj.zToastAlertMessage.zGetMsg(),
					localize(locator.noContactGroupName));

		}

	}

	/**
	 * To select no of contacts displayed per page from prefrences - address
	 * book
	 * 
	 * @param noOfContactsPerPage
	 */
	public static void zSelectContactPerPage(String noOfContactsPerPage) {
		obj.zHtmlMenu.zClick(zContactsPerPage, noOfContactsPerPage);
	}

	/**
	 * To click on Next/Previous arrow and to verify contact exist on that page
	 * or not
	 * 
	 * @param type
	 *            :NextArrow/PreviousArrow
	 * @param contactName
	 * @throws Exception
	 */
	public static void zClickArrowAndVerifyContactExist(String type,
			String contactName) throws Exception {
		Thread.sleep(1000);
		if (type.equals("NextArrow")) {
			obj.zButton.zClick(zNextPageArrowIcon);
			Thread.sleep(2000);
		} else if (type.equals("PreviousArrow")) {
			obj.zButton.zClick(zPrevPageArrowIcon);
			Thread.sleep(2000);
		}
		obj.zCheckbox.zExists("link=" + contactName);
	}

	/**
	 * To create a comma separated string of specified no of words from
	 * getlocalizeddata
	 * 
	 * @param noOfContacts
	 * @return
	 */
	public static String zCreateCommaSeparatedString(int noOfWords) {

		String commaSeparatedContacts = "";
		for (int i = 0; i < noOfWords; i++) {
			String lastName = getLocalizedData_NoSpecialChar();
			if (i == noOfWords - 1) {
				commaSeparatedContacts = commaSeparatedContacts + lastName;
			} else
				commaSeparatedContacts = commaSeparatedContacts + lastName
						+ ",";

		}
		commaSeparatedContacts = commaSeparatedContacts.toLowerCase();
		return commaSeparatedContacts;
	}

	/**
	 * To search a contact using upper/bottom toolbar
	 * 
	 * @param searchType
	 *            - "UpperToolbar/BottomToolbar"
	 * @param searchString
	 * @throws Exception
	 */
	public static void zSearchContact(String searchType, String searchString)
			throws Exception {
		Thread.sleep(1000);
		if (searchType.equals("UpperToolbar")) {
			// obj.zEditField.zType(localize(locator.findLabel), searchString);
			obj.zEditField.zType(zFindContactEditfield, searchString);
			obj.zButton.zClick(zSearchIconBtn, "");
		} else if (searchType.equals("BottomToolbar")) {
			// obj.zEditField.zType(localize(locator.findLabel), searchString,
			// "2");
			obj.zEditField.zType(zFindContactEditfield, searchString, "2");
			obj.zButton.zClick(zSearchIconBtn, "2");
			// zFindContactEditfield
		}
		Thread.sleep(2000);// to wait for search to complete
	}

	/**
	 * To create contact and search using bottom/upper toolbar
	 * 
	 * @param lastName
	 * @param middlename
	 * @param firstName
	 * @param email
	 * @param searchType
	 *            -- "UpperToolbar/BottomToolbar"
	 * @throws Exception
	 */
	public static void zCreateContactAndSearch(String lastName,
			String middlename, String firstName, String email, String company,
			String searchType) throws Exception {
		zCreateBasicContact(lastName, middlename, firstName, email, company);
		if (searchType.equals("UpperToolbar")) {
			zSearchContact("UpperToolbar", lastName);
		} else if (searchType.equals("BottomToolbar")) {
			zSearchContact("BottomToolbar", email);
		}

	}

	/**
	 * To verify
	 * 
	 * @param noOfContacts
	 * @param commaSeparatedContacts
	 * @param action
	 * @throws Exception
	 */
	public static void zAllItemsCheckBoxSelectUnSelectAndVerify(
			int noOfContacts, String commaSeparatedContacts, String action)
			throws Exception {
		String[] contactLastname = commaSeparatedContacts.split(",");
		obj.zCheckbox.zClick(zAllItemsCheckbox);
		if (action.equals("select")) {
			for (int i = 0; i < noOfContacts; i++) {
				obj.zMessageItem.zIsChecked(contactLastname[i]);
			}
		} else if (action.equals("unselect")) {
			for (int i = 0; i < noOfContacts; i++) {
				obj.zMessageItem.zIsUnChecked(contactLastname[i]);
			}
		}
		Thread.sleep(1000);
	}

	/**
	 * To select all items check box and click delete
	 * 
	 * @throws Exception
	 */
	public static void zSelectAllContactsAndDelete() throws Exception {
		obj.zCheckbox.zClick(zAllItemsCheckbox);
		Thread.sleep(500);
		obj.zButton.zClick(zDeleteIconBtn);
		Thread.sleep(2000);
	}

	/**
	 * To navigate to Trash folder and to verify the deleted contact exist there
	 * 
	 * @param commaSeparatedContacts
	 * @throws Exception
	 */
	public static void zNavigateToTrashAndVerifyDeletedContacts(
			String commaSeparatedContacts) throws Exception {
		String[] contactName = commaSeparatedContacts.split(",");
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("zh_CN")) {
			obj.zFolder.zClick(localize(locator.trash).substring(0, 3));
		} else {
			obj.zFolder.zClick(localize(locator.trash));
		}
		Thread.sleep(1000);
		for (int i = 0; i < contactName.length; i++) {
			obj.zCheckbox.zExists("link=" + contactName[i]);
		}
	}

	/**
	 * To select a contact and apply tag to that contact
	 * 
	 * @param tagName
	 * @param commaSeparatedContacts
	 */
	public static void zSelectContactAndApplyTag(String tagName,
			String commaSeparatedContacts) throws Exception {
		String[] contactName = commaSeparatedContacts.split(",");
		for (int i = 0; i < contactName.length; i++) {
			obj.zMessageItem.zClickCheckBox(contactName[i]);
		}
		zSelectMoreActions(tagName);
	}

	/**
	 * To select a contact and remove tag
	 * 
	 * @param tagName
	 * @param commaSeparatedContacts
	 */
	public static void zSelectContactAndRemoveTag(String tagName,
			String commaSeparatedContacts) {
		String[] contactName = commaSeparatedContacts.split(",");
		for (int i = 0; i < contactName.length; i++) {
			obj.zMessageItem.zClickCheckBox(contactName[i]);

		}
		obj.zHtmlMenu.zClick("name=actionOp", tagName, "2");
	}

	/**
	 * To verify the contact has tag applied to it
	 * 
	 * @param commaSeparatedContacts
	 */
	public static void zVerifyContactHasTag(String commaSeparatedContacts) {
		String[] contactName = commaSeparatedContacts.split(",");

		for (int i = 0; i < contactName.length; i++) {
			obj.zMessageItem.zVerifyIsTagged(contactName[i]);

		}
	}

	/**
	 * To verify the contact has no tag applied to it
	 * 
	 * @param commaSeparatedContacts
	 */
	public static void zVerifyContactHasNoTag(String commaSeparatedContacts) {
		String[] contactName = commaSeparatedContacts.split(",");

		for (int i = 0; i < contactName.length; i++) {
			obj.zMessageItem.zVerifyIsNotTagged(contactName[i]);

		}
	}

	/**
	 * To click a tag and verify specified contacts belong to tag
	 * 
	 * @param tagName
	 * @param commaSeparatedContacts
	 * @throws Exception
	 */
	public static void zClickTagAndVerifyAttachedContacts(String tagName,
			String commaSeparatedContacts) throws Exception {
		String[] contactName = commaSeparatedContacts.split(",");

		obj.zFolder.zClick(tagName);
		zWaitTillObjectExist("link", contactName[0]);
		for (int i = 0; i < contactName.length; i++) {
			obj.zCheckbox.zExists("link=" + contactName[i]);
		}
	}

	/**
	 * To navigate to preference and to select Enabled auto adding of contacts
	 * checkbox
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToPrefABAndClickEnabledAutoAddingOfContacts()
			throws Exception {
		zNavigateToPreferenceAB();
		obj.zCheckbox.zClick(zEnableAutoAdding);
		obj.zButton.zClick(zPrefSaveButton);
	}

	/**
	 * To navigate to AB and to select specified contacts folder
	 * 
	 * @param nameOfFolder
	 * @throws Exception
	 */
	public static void zNavigateToContactAndSelectABFolder(String nameOfFolder)
			throws Exception {
		zNavigateToContact();
		zClickOnContactFolder(nameOfFolder);

	}

	/**
	 * To send mail to self account and to navigate to specific contacts folder
	 * in address book
	 * 
	 * @param contactFolderName
	 * @throws Exception
	 */
	public static void zSendMailToSelfAndNavigateToSpecificContactsFolder(
			String contactFolderName) throws Exception {
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMail(SelNGBase.selfAccountName.get(), "", "",
				getLocalizedData_NoSpecialChar(), "", "");
		// page.zComposeView.zSendMailToSelfAndVerify(SelNGBase.selfAccountName,
		// "", "", getLocalizedData_NoSpecialChar(), "", "");
		Thread.sleep(2000);
		page.zABComposeHTML
				.zNavigateToContactAndSelectABFolder(contactFolderName);

	}

	public static void zCreateContactWithAllDetailsAndVerifyDisplay()
			throws Exception {

		String[] composeContactArray = { zLastNameEditField,
				zFirstNameEditField, zEmailEditField, zCompanyEditField,
				zJobTitleEditField, zEmail2EditField, zEmail3EditField,
				zworkStreetEditField, zworkCityEditField, zworkStateEditField,
				zworkPostalCodeEditField, zworkCountryEditField,
				zworkURLEditField, zworkPhoneEditField, zworkPhone2EditField,
				zworkFaxEditField, zassistantPhoneEditField,
				zcompanyPhoneEditField, zcallbackPhoneEditField,
				zhomeStreetEditField, zhomeCityEditField, zhomeStateEditField,
				zhomePostalCodeEditField, zhomeCountryEditField,
				zhomeURLEditField, zhomePhoneEditField, zhomePhone2EditField,
				zhomeFaxEditField, zmobilePhoneEditField, zpagerEditField,
				zcarPhoneEditField, zotherStreetEditField, zotherCityEditField,
				zotherStateEditField, zotherPostalCodeEditField,
				zotherCountryEditField, zotherURLEditField,
				zotherPhoneEditField, zotherFaxEditField, znotesTextarea };

		String[] contactArray = new String[composeContactArray.length];
		for (int i = 0; i < composeContactArray.length; i++) {
			contactArray[i] = getLocalizedData_NoSpecialChar();
		}

		for (int i = 0; i < composeContactArray.length; i++) {
			if (i == 7 || i == 19 || i == 31
					|| i == composeContactArray.length - 1) {
				obj.zTextAreaField.zType(composeContactArray[i],
						contactArray[i]);
			}
			obj.zEditField.zType(composeContactArray[i], contactArray[i]);
		}

		obj.zButton.zClick(zSaveNewContactIconBtn);
		Thread.sleep(2000);
		String actualContactDisplayed = obj.zMiscObj
				.zGetInnerText("ZhAppViewContent");
		for (int i = 0; i < composeContactArray.length; i++) {
			Assert.assertTrue(actualContactDisplayed.contains(contactArray[i]),
					"The field " + contactArray[i]
							+ " is not displayed in contact display");
		}

	}

	public static void zSelectFileAsAndSave(String contactName, String fileAs)
			throws Exception {
		obj.zCheckbox.zClick(contactName);
		Thread.sleep(1000);
		obj.zButton.zClick(zEditIconBtn);
		Thread.sleep(1000);
		obj.zHtmlMenu.zClick("id=fileAs", fileAs);
		obj.zButton.zClick(zSaveNewContactIconBtn);
		Thread.sleep(1000);
	}

	public static void zVerifyDisplayedContactName(
			String expectedContactToBeDisplayed) {

		String actualContactDisplayed = obj.zMiscObj.zGetInnerText("List");

		Assert.assertTrue(actualContactDisplayed
				.contains(expectedContactToBeDisplayed), "msg");

	}

	public static void zVerifyFileAsOptions(String lastName, String firstName,
			String company) throws Exception {

		String[] fileAs = { localize(locator.AB_FILE_AS_lastFirst),
				localize(locator.AB_FILE_AS_firstLast),
				localize(locator.AB_FILE_AS_company),
				localize(locator.AB_FILE_AS_lastFirstCompany),
				localize(locator.AB_FILE_AS_firstLastCompany),
				localize(locator.AB_FILE_AS_companyLastFirst),
				localize(locator.AB_FILE_AS_companyFirstLast) };

		String contactName = lastName + "," + " " + firstName;

		for (int i = 0; i < fileAs.length; i++) {

			page.zABComposeHTML.zSelectFileAsAndSave(contactName, fileAs[i]);

			if (i == 0) {
				page.zABComposeHTML.zVerifyDisplayedContactName(contactName);
			} else if (i == 1) {
				contactName = firstName + " " + lastName;
				page.zABComposeHTML.zVerifyDisplayedContactName(contactName);
			} else if (i == 2) {
				contactName = company;
				page.zABComposeHTML.zVerifyDisplayedContactName(contactName);
			} else if (i == 3) {
				contactName = lastName + "," + " " + firstName + " " + "("
						+ company + ")";
				page.zABComposeHTML.zVerifyDisplayedContactName(contactName);
			} else if (i == 4) {
				contactName = firstName + " " + lastName + " " + "(" + company
						+ ")";
				page.zABComposeHTML.zVerifyDisplayedContactName(contactName);
			} else if (i == 5) {
				contactName = company + " " + "(" + lastName + "," + " "
						+ firstName + ")";
				page.zABComposeHTML.zVerifyDisplayedContactName(contactName);
			} else if (i == 6) {
				contactName = company + " " + "(" + firstName + " " + lastName
						+ ")";
				page.zABComposeHTML.zVerifyDisplayedContactName(contactName);
			}

		}

	}

	public static void zVerifyImportedContactDisplay(String csvFileType)
			throws Exception {

		String[] contactArrayForZimbra = { "23456", "45678", "32145",
				"companyName", "34567", "email", "email2", "email3", "1",
				"firstName", "homeCity", "homeCountry", "89123", "56789",
				"78912", "homePostalcode", "homeState", "homeStreet",
				"webPageHome", "jobTitle", "lastName", "912345", "notes",
				"otherCity", "otherCountry", "41236", "9923600361",
				"otherPostalCode", "otherState", "otherStreet", "webPageOther",
				"21345", "workCity", "workCountry", "12345", "9923600359",
				"9923600360", "workPostalCode", "workState", "workStreet",
				"webPageWork" };

		String[] contactArrayForYahoo = { "firstNameYahoo", "lastNameYahoo",
				"email@yahoo.com", "email@yahoo.com", "12345", "23456",
				"34567", "67891", "9923600359", "45678", "websiteWork",
				"jobtitleYahoo", "companyYahoo", "streeWork", "cityWork",
				"stateWork", "postalCodeWork", "countryWork", "streetHome",
				"cityHome", "stateHome", "postalCodeHome", "countryHome",
				"notesYahoo" };

		String[] contactArrayForOutlook = { "firstNameOutlook",
				"lastNameOutlook", "companyOutlook", "jobTitleOutlook",
				"businessAddressOutlook", "United States of America", "34567",
				"12345", "23456", "(992) 360-0359", "email@outlook.com",
				"notesOutlook", "http://webPageOutlook" };

		String[] contactArrayForGmail = { "NameGmail", "EmailGmail@gmail.com",
				"NotesGmail", "1234512345", "CompanyGmail", "TitleGmail",
				"Gmail  Address" };

		String actualContactDisplayed = obj.zMiscObj
				.zGetInnerText("ZhAppViewContent");
		if (csvFileType.equals("Zimbra")) {
			for (int i = 0; i < contactArrayForZimbra.length; i++) {
				Assert.assertTrue(actualContactDisplayed
						.contains(contactArrayForZimbra[i]), "The field "
						+ contactArrayForZimbra[i]
						+ " is not displayed/imported in contact");
			}
		} else if (csvFileType.equals("Gmail")) {
			for (int i = 0; i < contactArrayForGmail.length; i++) {
				Assert.assertTrue(actualContactDisplayed
						.contains(contactArrayForGmail[i]), "The field "
						+ contactArrayForGmail[i]
						+ " is not displayed/imported in contact");
			}
		} else if (csvFileType.equals("Outlook")) {
			for (int i = 0; i < contactArrayForOutlook.length; i++) {
				Assert.assertTrue(actualContactDisplayed
						.contains(contactArrayForOutlook[i]), "The field "
						+ contactArrayForOutlook[i]
						+ " is not displayed/imported in contact");
			}
		} else if (csvFileType.equals("Yahoo")) {
			for (int i = 0; i < contactArrayForYahoo.length; i++) {
				Assert.assertTrue(actualContactDisplayed
						.contains(contactArrayForYahoo[i]), "The field "
						+ contactArrayForYahoo[i]
						+ " is not displayed/imported in contact");
			}
		}

	}

	public static void zNavigateToNewABPageAndImportContact(String csvFileName)
			throws Exception {
		page.zABComposeHTML.zNavigateToNewABPage();
		page.zABComposeHTML.zImportContact(csvFileName);

	}

	public static void zNavigateToContactAndVerifyImportedContactDisplay(
			String contactName, String csvFileType) throws Exception {

		zNavigateToContact();
		obj.zMessageItem.zClick(contactName);
		zVerifyImportedContactDisplay(csvFileType);
	}

}
