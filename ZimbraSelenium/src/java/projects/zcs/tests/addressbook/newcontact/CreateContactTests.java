package projects.zcs.tests.addressbook.newcontact;

import java.io.File;
import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import framework.util.RetryFailedTests;
import framework.util.ZimbraSeleniumProperties;

/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class CreateContactTests extends CommonTest {
	protected boolean isCheckbox = false;

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

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void addRemoveContactPhotoAndVerify(String cnLastName,
			String cnFirstname, String filename) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		Boolean viewLinkPresent, removeLinkPresent;
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
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
		File f = new File("src/java/projects/zcs/data/" + filename);
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
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
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
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
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

	@Test(dataProvider = "ABDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void verifyAllFileAsOptionForContact(String cnLastName,
			String cnFirstname, String company) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
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