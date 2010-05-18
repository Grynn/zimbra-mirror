package projects.zcs.tests.addressbook;

import java.io.File;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

import framework.util.RetryFailedTests;

/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class AddressBookTest extends CommonTest {
	protected boolean isCheckbox = false;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();

		if (test.equals("editNameAndVerify")
				|| test.equals("rghtClickEditNameAndVerify")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					"NewLastName:" + getLocalizedData(1),
					"NewMiddleName:" + getLocalizedData(1) } };
		}

		// following
		String itemsEnabled = localize(locator.AB_EDIT_CONTACT) + ","
				+ localize(locator.AB_TAG_CONTACT) + ","
				+ localize(locator.del) + "," + localize(locator.move) + ","
				+ localize(locator.print) + "," + localize(locator.search)
				+ "," + localize(locator.advancedSearch) + ","
				+ localize(locator.newEmail);
		String itemsDisabled = "";

		if (test.equals("rtClickContactAndVerify")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "", itemsEnabled,
					itemsDisabled, "false" } };
		}
		if (test.equals("deleteContactAndVerify")
				|| test.equals("rghtClkDeleteContactAndVerify")) {
			return new Object[][] { { "lastName:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "" } };
		}
		String targetAddressBookFolder = localize(locator.emailedContacts);
		if (test.equals("moveContactAndVerify")
				|| test.equals("rghtClickMoveContactAndVerify")) {
			return new Object[][] { { "lastNameForMove:" + getLocalizedData(1),
					"middleName:" + getLocalizedData(1), "",
					targetAddressBookFolder } };
		}
		if (test.equals("createAndRenameABFolder")) {
			return new Object[][] { {
					"newAB" + getLocalizedData_NoSpecialChar(),
					"renamedAB" + getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("createAndQDeleteABFolder")) {
			return new Object[][] { { "newAB"
					+ getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("createContactGroupAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("CreateContactWhileSrchFldrAndTagSelected_Bug40517")) {
			return new Object[][] { {
					"lastName  " + getLocalizedData_NoSpecialChar(),
					"middleName " + getLocalizedData_NoSpecialChar(), "",
					"NewLastName " + getLocalizedData_NoSpecialChar(),
					"NewMiddleName " + getLocalizedData_NoSpecialChar(),
					"NewLastName1 " + getLocalizedData_NoSpecialChar(),
					"NewMiddleName1 " + getLocalizedData_NoSpecialChar(), } };
		}

		if (test.equals("addRemoveContactPhotoAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "structure.jpg" } };
		}

		if (test.equals("verifyAllFileAsOptionForContact")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		}

		if (test.equals("createContactWithAllFieldsAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "1983-03-10",
					getLocalizedData_NoSpecialChar() } };
		}
		if (test.equals("checkAutoFillForConatctWorkAddr_41144")) {
			return new Object[][] { {
					"lastName  " + getLocalizedData_NoSpecialChar(),
					"middleName " + getLocalizedData_NoSpecialChar(), "",
					"touser@testdomain.com" } };
		}

		return new Object[][] { { "lastName:" + getLocalizedData(1),
				"middleName:" + getLocalizedData(1), "" } };
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		page.zABCompose.zNavigateToContact();
		isExecutionARetry = false;
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (needReset && !isExecutionARetry) {
			zLogin();
		}
		needReset = true;
	}

	/**
	 * Enters some basic fields to create a contact in address book and verifies
	 * the contact exist or not
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createBasicContact(String cnLastName, String cnMiddleName,
			String cnFirstname) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);
		obj.zContactListItem.zExists(cnLastName);

		needReset = false;

	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void scrollbarTest_Bug43227(String cnLastName, String cnMiddleName,
			String cnFirstname) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		for (int i = 0; i <= 30; i++) {
			page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
					cnFirstname);
		}

		needReset = false;

	}

	/**
	 * Creates a contact with basic fields,edits the contacts using the ToolBar
	 * delete last name and verifies the change
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void editNameAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname, String newcnLastName, String newMiddleName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);
		page.zABCompose.zModifyContact(cnLastName, newcnLastName,
				newMiddleName, "", "", "ToolbarEdit");

		Assert
				.assertTrue(page.zABCompose.zVerifyEditContact(newcnLastName,
						newMiddleName, "", ""),
						"The Contact is not modified correctly");
		needReset = false;

	}

	/**
	 * Creates a contact with basic fields,edits the contacts using Right Click
	 * last name and verifies the change
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rghtClickEditNameAndVerify(String cnLastName,
			String cnMiddleName, String cnFirstname, String newcnLastName,
			String newMiddleName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);
		page.zABCompose.zModifyContact(cnLastName, newcnLastName,
				newMiddleName, "", "", "RightClickEdit");

		Assert
				.assertTrue(page.zABCompose.zVerifyEditContact(newcnLastName,
						newMiddleName, "", ""),
						"The Contact is not modified correctly");
		needReset = false;

	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact does not exist after ToolBar delete
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void deleteContactAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);
		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zDeleteContactAndVerify(cnLastName, "ToolbarDelete");

		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact gets moved to target AB folder using tool bar move button
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void moveContactAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname, String targetAB) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);
		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zMoveContactAndVerify(cnLastName, targetAB,
					"ToolbarMove");

		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact gets moved to target AB folder using tool bar move button
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rghtClickMoveContactAndVerify(String cnLastName,
			String cnMiddleName, String cnFirstname, String targetAB)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);

		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zMoveContactAndVerify(cnLastName, targetAB,
					"RightClickMove");
		needReset = false;
	}

	/**
	 * Creates a contact with basic fields,deletes the contact and verifies the
	 * contact does not exist after Right Click delete
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rghtClkDeleteContactAndVerify(String cnLastName,
			String cnMiddleName, String cnFirstname) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);

		if (obj.zContactListItem.zExistsDontWait(cnLastName).equals("true"))
			page.zABApp.zDeleteContactAndVerify(cnLastName, "RightClickDelete");
		needReset = false;
	}

	/**
	 *Test to verify the contact right click menus exits and are enabled or not
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstname
	 * @param enabledItemsSeparatedByComma
	 * @param disabledItemsSeparatedByComma
	 * @param ignoreContext
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void rtClickContactAndVerify(String cnLastName, String cnMiddleName,
			String cnFirstname, String enabledItemsSeparatedByComma,
			String disabledItemsSeparatedByComma, String ignoreContext)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateBasicContact(cnLastName, cnMiddleName,
				cnFirstname);
		obj.zContactListItem.zRtClick(cnLastName);
		page.zABApp.zVerifyAllMenuItems(enabledItemsSeparatedByComma,
				disabledItemsSeparatedByComma, ignoreContext);

		needReset = false;
	}

	/**
	 *Test to verify the contact right click menus exits and are enabled or not
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstname
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void negativeTestCreateContact(String cnLastName,
			String cnMiddleName, String cnFirstName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		page.zABCompose.zEnterBasicABData("", cnLastName, cnMiddleName,
				cnFirstName);
		obj.zButton.zClick(page.zABCompose.zCancelContactMenuIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no),
				localize(locator.warningMsg));
		Thread.sleep(500);
		obj.zContactListItem.zNotExists(cnLastName);

		needReset = false;
	}

	/**
	 * Test to create a notebook folder and then rename the notebook folder and
	 * verify
	 * 
	 * @param newAddBookName
	 *            :name of the new AB to be created
	 * @param renamedABName
	 *            :name of the AB to be renamed
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAndRenameABFolder(String newAddBookName,
			String renamedABName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateNewAddBook(newAddBookName);
		zWaitTillObjectExist("folder", newAddBookName);
		obj.zFolder.zRtClick(newAddBookName);
		obj.zMenuItem.zClick(localize(locator.renameFolder));
		obj.zEditField.zTypeInDlg(localize(locator.newName), renamedABName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		obj.zFolder.zExists(renamedABName);

		needReset = false;
	}

	/**
	 * 
	 * To create AB folder and then delete the same.Verify the creation and
	 * deletion of the AB folder
	 * 
	 * @param newAddBookName
	 *            : Name of the AB folder to be created
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createAndQDeleteABFolder(String newAddBookName)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateNewAddBook(newAddBookName);
		zWaitTillObjectExist("folder", newAddBookName);
		page.zMailApp.zDeleteFolder(newAddBookName);
		obj.zFolder.zClick(localize(locator.trash));
		obj.zFolder.zExists(newAddBookName);

		needReset = false;
	}

	/**
	 * Test to create Contact Group and to verify
	 * 
	 * @param groupName
	 * @throws Exception
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContactGroupAndVerify(String groupName) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zButtonMenu.zClick(page.zABCompose.zNewMenuDropdownIconBtn);

		obj.zMenuItem.zClick(localize(locator.group));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.groupNameLabel)),
				groupName);
		for (int i = 1; i <= 2; i++) {
			ProvZCS.createAccount("acc" + i + "@testdomain.com");
			obj.zEditField.zType(localize(locator.findLabel), "acc" + i
					+ "@testdomain.com");
			obj.zButton.zClick(localize(locator.search), "2");
			Thread.sleep(2500);
			if (currentBrowserName.contains("Safari")) {
				obj.zButton.zClick(localize(locator.search), "2");
				obj.zButton.zClick(localize(locator.search), "2");
				Thread.sleep(1000);
			}

			obj.zListItem.zDblClickItemInSpecificList("acc" + i
					+ "@testdomain.com", "2");

			obj.zButton.zClick(localize(locator.add));
		}
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.groupCreated),
				"Group Created message should be shown");
		obj.zContactListItem.zExists(groupName);

		needReset = false;
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContactWithAllFieldsAndVerify(String prefix,
			String firstName, String middleName, String maidenName,
			String lastName, String suffix, String nickName, String jobTitle,
			String department, String company, String email, String phone,
			String iM, String street, String city, String state,
			String postalCode, String country, String uRL, String other,
			String notes) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateContactWithAllFields(prefix, firstName,
				middleName, maidenName, lastName, suffix, nickName, jobTitle,
				department, company, email, phone, iM, street, city, state,
				postalCode, country, uRL, other, notes);
		obj.zListItem.zClick(lastName);
		obj.zButton.zClick(page.zABCompose.zEditContactIconBtn);
		page.zABCompose.zVerifyContactWithAllFields(prefix, firstName,
				middleName, maidenName, lastName, suffix, nickName, jobTitle,
				department, company, email, phone, iM, street, city, state,
				postalCode, country, uRL, other, notes);
		obj.zButton.zClick(localize(locator._close));

		needReset = false;
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAllFileAsOptionForContact(String cnLastName,
			String cnFirstname, String company) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		if (config.getString("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		zWaitTillObjectExist("editfield", page.zABCompose.zLastEditField);
		obj.zEditField.zActivateAndType(page.zABCompose.zLastEditField,
				cnLastName);
		obj.zEditField.zActivateAndType(page.zABCompose.zFirstEditField,
				cnFirstname);
		obj.zEditField.zActivateAndType(page.zABCompose.zCompanyEditField,
				company);
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		Thread.sleep(2500);
		editContactUpdateFileAsAndVerify(cnLastName, cnFirstname, company);

		needReset = false;
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addRemoveContactPhotoAndVerify(String cnLastName,
			String cnFirstname, String filename) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		Boolean viewLinkPresent, removeLinkPresent;
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		if (config.getString("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		Thread.sleep(1500);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'editcontactform_REMOVE_IMAGE_row') and contains(@style,'display: none')]"),
						"View/Remove image link exist even photo is not uploaded");

		obj.zEditField.zActivateAndType(page.zABCompose.zLastEditField,
				cnLastName);
		obj.zEditField.zActivateAndType(page.zABCompose.zFirstEditField,
				cnFirstname);
		obj.zButton.zClick("id=editcontactform_IMAGE_badge");
		Thread.sleep(2000);
		File f = new File("projects/zcs/data/" + filename);
		String path = f.getAbsolutePath();
		obj.zBrowseField.zTypeInDlgWithKeyboard(localize(locator.uploadImage),
				path, "1");
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(2000);
		String dlgExists = obj.zDialog
				.zExistsDontWait(localize(locator.uploadImage));
		for (int i = 0; i <= 20; i++) {
			if (dlgExists.equals("true")) {
				Thread.sleep(1000);
			} else {
				Thread.sleep(1000);
				break;
			}
		}
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		obj.zContactListItem.zClick(cnLastName);
		obj.zButton.zClick(page.zABCompose.zEditContactIconBtn);
		if (config.getString("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'editcontactform_REMOVE_IMAGE_row') and contains(@style,'display: block')]"),
						"View/Remove image link not exist after uploading photo");

		// Remove photo and re verify
		selenium.click("id=editcontactform_REMOVE_IMAGE");
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		obj.zContactListItem.zClick(cnLastName);
		obj.zButton.zClick(page.zABCompose.zEditContactIconBtn);
		if (config.getString("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		viewLinkPresent = selenium
				.isElementPresent("id=editcontactform_VIEW_IMAGE");
		removeLinkPresent = selenium
				.isElementPresent("id=editcontactform_REMOVE_IMAGE");
		assertReport("false", viewLinkPresent.toString(),
				"View link exist after uploading contact photo");
		assertReport("false", removeLinkPresent.toString(),
				"Remove link exist after uploading contact photo");

		needReset = false;
	}

	/**
	 * Scenario 1: Login to Web Client. Create a new contact and create Saved
	 * Search with a Contact. Go to Searches -> Click on New Contact. Enter
	 * Details and try to save it. Expected :Should able to add new contact
	 * 
	 *Scenario 2: Assign Tag to any contact. Go to Tags -> Click on New
	 * Contact. Enter details and try to save it.
	 * 
	 *Expected :Should able to add new contact
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param newcnLastName
	 * @param newMiddleName
	 * @param newcnLastName1
	 * @param newMiddleName1
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void CreateContactWhileSrchFldrAndTagSelected_Bug40517(
			String cnLastName, String cnMiddleName, String cnFirstName,
			String newcnLastName, String newMiddleName, String newcnLastName1,
			String newMiddleName1) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zABCompose.zCreateContactInAddressBook("", cnLastName,
				cnMiddleName, cnFirstName);
		obj.zFolder.zClick(page.zABCompose.zContactsFolder);
		obj.zContactListItem.zExists(cnLastName);
		selenium.type("xpath=//input[@class='search_input']", cnLastName);
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zContactListItem.zExists(cnLastName);
		obj.zButton.zClick(localize(locator.save));
		obj.zDialog.zExists(localize(locator.saveSearch));
		selenium.type("xpath=//td/input[contains(@id,'_nameField')]",
				"savecontact");
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(2000);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'savecontact')]"),
						"savecontact folder does not present");
		selenium
				.clickAt(
						"xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'savecontact')]",
						"");
		page.zABCompose.zCreateContactInAddressBook("", newcnLastName,
				newMiddleName, "");
		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zRtClick(newcnLastName);
		selenium.mouseOver("id=zmi__Contacts__TAG_MENU_title");
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), "tagName");
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(2000);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'tagName')]"),
						"tagName folder does not present");
		selenium
				.clickAt(
						"xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'tagName')]",
						"");
		obj.zContactListItem.zExists(newcnLastName);
		page.zABCompose.zCreateContactInAddressBook("", newcnLastName1,
				newMiddleName1, "");
		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zExists(newcnLastName1);

		needReset = false;
	}

	/**
	 * Test Case:-Contact added as Work Address is not autofilled Login and go
	 * to Address book tab. Create a new contact and add the email address as
	 * "Work Address". Save the contact and compose a new mail. When a letter is
	 * placed in a "To" or "Cc' field it is suppose to auto fill Expected
	 * result:-The contact must get auto filled.
	 * 
	 * @param cnLastName
	 * @param cnMiddleName
	 * @param cnFirstName
	 * @param email
	 * @throws Exception
	 * @author Girish
	 */
	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void checkAutoFillForConatctWorkAddr_41144(String cnLastName,
			String cnMiddleName, String cnFirstName, String email)
			throws Exception {
		if (isExecutionARetry)
			handleRetry();

		if (config.getString("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		page.zABCompose.zEnterBasicABData("", cnLastName, cnMiddleName,
				cnFirstName);
		Thread.sleep(1000);
		selenium.clickAt("id=editcontactform_EMAIL_0_add", "");
		Thread.sleep(2000);
		obj.zEditField.zActivateAndType(page.zABCompose.zWorkEmail1EditField,
				email);
		obj.zButton.zClick(localize(locator.save), "2");
		Thread.sleep(1500);
		obj.zContactListItem.zExists(cnLastName);
		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		System.out.println(email);
		if (email.contains("@")) {
			email = email.substring(0, email.indexOf('@'));
		}
		selenium.typeKeys("id=zv__COMPOSE1_to_control", email);
		selenium.keyDown("id=zv__COMPOSE1_to_control", "\\13");
		selenium.keyUp("id=zv__COMPOSE1_to_control", "\\13");
		Thread.sleep(1000);
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[contains(@id,'DWT') and contains(@style,'display: block') and @class='ZmAutocompleteListView']/div"),
						"Auto complete not showing");

		needReset = false;
	}

	private void editContactUpdateFileAsAndVerify(String cnLastName,
			String cnFirstname, String company) throws Exception {
		String[] fileAsArray = { cnLastName + ", " + cnFirstname,
				cnFirstname + " " + cnLastName, company,
				cnLastName + ", " + cnFirstname + " (" + company + ")",
				cnFirstname + " " + cnLastName + " (" + company + ")",
				company + " (" + cnLastName + ", " + cnFirstname + ")",
				company + " (" + cnFirstname + " " + cnLastName + ")" };
		int totalFileAs = fileAsArray.length;
		for (int i = 0; i < totalFileAs; i++) {
			obj.zListItem.zClick(fileAsArray[i]);
			Thread.sleep(2000); // required because selenium immediately clicks
			// new before previous action perform its action
			obj.zButton.zClick(page.zABCompose.zEditContactIconBtn);
			Thread.sleep(2000); // required because selenium immediately clicks
			// new before previous action perform its action
			obj.zButton.zClick("id=editcontactform_FILE_AS_left_icon");
			switch (i) {
			case 0:
				obj.zMenuItem.zClick(localize(locator.AB_FILE_AS_firstLast));
				break;
			case 1:
				obj.zMenuItem.zClick(localize(locator.AB_FILE_AS_company));
				break;
			case 2:
				obj.zMenuItem
						.zClick(localize(locator.AB_FILE_AS_lastFirstCompany));
				break;
			case 3:
				obj.zMenuItem
						.zClick(localize(locator.AB_FILE_AS_firstLastCompany));
				break;
			case 4:
				obj.zMenuItem
						.zClick(localize(locator.AB_FILE_AS_companyLastFirst));
				break;
			case 5:
				obj.zMenuItem
						.zClick(localize(locator.AB_FILE_AS_companyFirstLast));
				break;
			default:
				System.out.println("Invalid option");
				break;
			}
			obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
			Thread.sleep(2500); // give sufficient time after saving as it
			// fails because lot of time opening and saving same contact
		}
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		isExecutionARetry = false;// reset this to false
		zLogin();
	}

}