package projects.zcs.tests.addressbook.bugs;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;
import framework.core.SelNGBase;
import framework.items.ContactItem;
import framework.items.ContactItem.GenerateItemType;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

/**
 * This covers some high priority test cases related to address book
 * 
 * @written by Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class AddressBookBugTests extends CommonTest {
	
	
	
	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
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
	@Test(
			description = "Contact added as Work Address is not autofilled Login and go" +
					" to Address book tab. Create a new contact and add the email address as" +
					" 'Work Address'. Save the contact and compose a new mail. When a letter is" +
					" placed in a 'To' or 'Cc' field it is suppose to auto fill Expected" +
					" result:-The contact must get auto filled.",
			groups = { "smoke", "full" }, 
			retryAnalyzer = RetryFailedTests.class)
	public void checkAutoFillForConatctWorkAddr_41144()	throws Exception {
		
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");

		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			SleepUtil.sleep(2500);
		} else {
			SleepUtil.sleep(2000);
		}
		

		ContactItem contact = ContactItem.generateContactItem(GenerateItemType.Basic);
		contact.email = "touser@testdomain.com";
		
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		page.zABCompose.zEnterBasicABData(contact);
		SleepUtil.sleep(1000);
		
		SelNGBase.selenium.get().clickAt("id=editcontactform_EMAIL_0_add", "");
		SleepUtil.sleep(2000);
		
		obj.zEditField.zActivateAndType(page.zABCompose.zWorkEmail1EditField, contact.email);
		obj.zButton.zClick(localize(locator.save), "2");
		SleepUtil.sleep(1500);
		
		obj.zContactListItem.zExists(contact.lastName);
		
		zGoToApplication("Mail");
		page.zComposeView.zNavigateToMailCompose();
		System.out.println(contact.email);
		
		SelNGBase.selenium.get().typeKeys("id=zv__COMPOSE1_to_control", contact.getCN());
		SelNGBase.selenium.get().keyDown("id=zv__COMPOSE1_to_control", "\\13");
		SelNGBase.selenium.get().keyUp("id=zv__COMPOSE1_to_control", "\\13");
		SleepUtil.sleep(1000);
		Assert
				.assertTrue(
						SelNGBase.selenium.get()
								.isElementPresent("xpath=//div[contains(@id,'DWT') and contains(@style,'display: block') and @class='ZmAutocompleteListView']"),
						"Auto complete not showing");

		SelNGBase.needReset.set(false);
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
	 * @author Girish
	 */
	@Test(
			description = "Scenario 1: Login to Web Client. Create a new contact and create Saved "+
							"Search with a Contact. Go to Searches -> Click on New Contact. Enter "+
							"Details and try to save it. Expected :Should able to add new contact",
			groups = { "smoke", "full" },
			retryAnalyzer = RetryFailedTests.class)
	public void CreateContactWhileSrchFldrAndTagSelected_Bug40517() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException("na", "IE", "44288", "Java script entered data Or right click & copy paste data into edit fields are not recognized by new AB UI");
		
		
		ContactItem contact1 = new ContactItem();
		contact1.lastName = "LN" + getLocalizedData_NoSpecialChar();
		contact1.middleName = "MN" + getLocalizedData_NoSpecialChar();
		contact1.firstName = "FN" + getLocalizedData_NoSpecialChar();
		
		ContactItem contact2 = new ContactItem();
		contact2.lastName = "NLN" + getLocalizedData_NoSpecialChar();
		contact2.middleName = "NMN" + getLocalizedData_NoSpecialChar();
		contact2.firstName = "";
		
		ContactItem contact3 = new ContactItem();
		contact2.lastName = "NFN" + getLocalizedData_NoSpecialChar();
		contact2.middleName = "NNMN" + getLocalizedData_NoSpecialChar();
		contact2.firstName = "";

		page.zABCompose.createItem(ActionMethod.DEFAULT, contact1);
		obj.zFolder.zClick(page.zABCompose.zContactsFolder);
		obj.zContactListItem.zExists(contact1.lastName);
		SelNGBase.selenium.get().type("xpath=//input[@class='search_input']", contact1.lastName);
		obj.zButton.zClick(page.zMailApp.zSearchIconBtn);
		obj.zContactListItem.zExists(contact1.lastName);
		obj.zButton.zClick(localize(locator.save));
		obj.zDialog.zExists(localize(locator.saveSearch));
		SelNGBase.selenium.get().type("xpath=//td/input[contains(@id,'_nameField')]",
				"savecontact");
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(2000);
		Assert
				.assertTrue(
						SelNGBase.selenium.get()
								.isElementPresent("xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'savecontact')]"),
						"savecontact folder does not present");
		SelNGBase.selenium.get()
				.clickAt(
						"xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'savecontact')]",
						"");
		
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact2);
		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zRtClick(contact2.lastName);
		SelNGBase.selenium.get().mouseOver("id=zmi__Contacts__TAG_MENU_title");
		obj.zMenuItem.zClick(localize(locator.newTag));
		obj.zEditField.zTypeInDlg(localize(locator.tagName), "tagName");
		obj.zButton.zClickInDlg(localize(locator.ok));
		SleepUtil.sleep(2000);
		Assert
				.assertTrue(
						SelNGBase.selenium.get()
								.isElementPresent("xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'tagName')]"),
						"tagName folder does not present");
		SelNGBase.selenium.get()
				.clickAt(
						"xpath=//td[contains(@id,'zti__main_Contacts') and contains(text(),'tagName')]",
						"");
		
		obj.zContactListItem.zExists(contact2.lastName);
		page.zABCompose.createItem(ActionMethod.DEFAULT, contact3);
		obj.zFolder.zClick(localize(locator.contacts));
		obj.zContactListItem.zExists(contact3.lastName);

		SelNGBase.needReset.set(false);
	}
}