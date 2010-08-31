package projects.zcs.tests.addressbook.contacts;

import java.io.File;
import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import projects.zcs.Locators;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.items.FolderItem;
import framework.items.ContactItem.GenerateItemType;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

public class CreateContact extends CommonTest {
	
	private FolderItem EmailedContacts;
	
	public CreateContact() {
		
		EmailedContacts = new FolderItem();
		EmailedContacts.name = localize(Locators.emailedContacts);
		
	}
	
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "ABDataProvider")
	public Object[][] createData(Method method) {
		String test = method.getName();
		if (test.equals("addRemoveContactPhotoAndVerify")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(), "structure.jpg" } };
		} else if (test.equals("verifyAllFileAsOptionForContact")) {
			return new Object[][] { { getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar(),
					getLocalizedData_NoSpecialChar() } };
		} else if (test.equals("createContactWithAllFieldsAndVerify")) {
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
		} else {
			return new Object[][] { { "" } };
		}
	}

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
	}
	
	/**
	 * Enters some basic fields to create a contact in address book and verifies
	 * the contact exist or not
	 */
	@Test(
			description = "Enters some basic fields to create a contact in address book and verifies the contact exist or not",
			groups = { "sanity", "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void createBasicContact() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");
		
		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact);
		obj.zContactListItem.zExists(contact.lastName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void createContactWithAllFieldsAndVerify(String prefix,
			String firstName, String middleName, String maidenName,
			String lastName, String suffix, String nickName, String jobTitle,
			String department, String company, String email, String phone,
			String iM, String street, String city, String state,
			String postalCode, String country, String uRL, String other,
			String notes) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

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

		SelNGBase.needReset.set(false);
	}

	/**
	 *Test to verify the contact right click menus exits and are enabled or not
	 */
	@Test(
			description = "Create new contact, but cancel before saving",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void cancelCreateContact() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);
		
		obj.zFolder.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		page.zABCompose.zEnterBasicABData(contact);
		obj.zButton.zClick(page.zABCompose.zCancelContactMenuIconBtn);
		obj.zButton.zClickInDlgByName(localize(locator.no), localize(locator.warningMsg));
		SleepUtil.sleep(500);
		obj.zContactListItem.zNotExists(contact.lastName);

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addRemoveContactPhotoAndVerify(String cnLastName,
			String cnFirstname, String filename) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		Boolean viewLinkPresent, removeLinkPresent;
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			SleepUtil.sleep(2500);
		} else {
			SleepUtil.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		SleepUtil.sleep(1500);
		Assert
				.assertTrue(
						SelNGBase.selenium.get()
								.isElementPresent("xpath=//div[contains(@id,'editcontactform_REMOVE_IMAGE_row') and contains(@style,'display: none')]"),
						"View/Remove image link exist even photo is not uploaded");

		obj.zEditField.zActivateAndType(page.zABCompose.zLastEditField,
				cnLastName);
		obj.zEditField.zActivateAndType(page.zABCompose.zFirstEditField,
				cnFirstname);
		obj.zButton.zClick("id=editcontactform_IMAGE_badge");
		SleepUtil.sleep(2000);
		File f = new File("src/java/projects/zcs/data/" + filename);
		String path = f.getAbsolutePath();
		obj.zBrowseField.zTypeInDlgWithKeyboard(localize(locator.fileLabel),
				path, "1");
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(2000);
		String dlgExists = obj.zDialog
				.zExistsDontWait(localize(locator.fileLabel));
		for (int i = 0; i <= 20; i++) {
			if (dlgExists.equals("true")) {
				SleepUtil.sleep(1000);
			} else {
				SleepUtil.sleep(1000);
				break;
			}
		}
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zClick(cnLastName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zEditContactIconBtn);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			SleepUtil.sleep(2500);
		} else {
			SleepUtil.sleep(2000);
		}
		Assert
				.assertTrue(
						SelNGBase.selenium.get()
								.isElementPresent("xpath=//div[contains(@id,'editcontactform_REMOVE_IMAGE_row') and contains(@style,'display: block')]"),
						"View/Remove image link not exist after uploading photo");

		// Remove photo and re verify
		SelNGBase.selenium.get().click("id=editcontactform_REMOVE_IMAGE");
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		SleepUtil.sleep(1000);
		obj.zContactListItem.zClick(cnLastName);
		SleepUtil.sleep(1000);
		obj.zButton.zClick(page.zABCompose.zEditContactIconBtn);
		
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			SleepUtil.sleep(2500);
		} else {
			SleepUtil.sleep(2000);
		}
		viewLinkPresent = SelNGBase.selenium.get()
				.isVisible("id=editcontactform_VIEW_IMAGE");
		removeLinkPresent = SelNGBase.selenium.get()
				.isVisible("id=editcontactform_REMOVE_IMAGE");
		assertReport("false", viewLinkPresent.toString(),
				"View link exist after uploading contact photo");
		assertReport("false", removeLinkPresent.toString(),
				"Remove link exist after uploading contact photo");

		SelNGBase.needReset.set(false);
	}

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAllFileAsOptionForContact(String cnLastName,
			String cnFirstname, String company) throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");
		checkForSkipException("all", "na", "47184", "'Contact must have at least one field set.' exception while changing File as:");
		

		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			SleepUtil.sleep(2500);
		} else {
			SleepUtil.sleep(2000);
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
		SleepUtil.sleep(2500);
		editContactUpdateFileAsAndVerify(cnLastName, cnFirstname, company);

		SelNGBase.needReset.set(false);
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
			SleepUtil.sleep(2000); // required because selenium immediately clicks
			// new before previous action perform its action
			obj.zButton.zClick(page.zABCompose.zEditContactIconBtn);
			SleepUtil.sleep(2000); // required because selenium immediately clicks
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
			SleepUtil.sleep(2500); // give sufficient time after saving as it
			// fails because lot of time opening and saving same contact
		}
	}
}