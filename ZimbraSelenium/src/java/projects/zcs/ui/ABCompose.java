package projects.zcs.ui;

import framework.items.ContactItem;
import framework.items.ZimbraItem;
import framework.util.HarnessException;
import framework.util.ZimbraSeleniumProperties;

/**
 * This Class have UI-level methods related composing a contact and verifying
 * the mail's contents. e.g: zNavigateToContact,
 * zCreateNewAddBook,zCreateBasicContact etc It also has static-final variables
 * that holds ids of icons on the compose-toolbar(like zNewAddressBookIconBtn,
 * zNewContactMenuIconBtn etc). If you are dealing with the toolbar buttons, use
 * these icons since in vmware resolutions and in some languages button-labels
 * are not displayed(but just their icons)
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class ABCompose extends AppPage {
	
	public static class ABComposeActionMethod extends ActionMethod {
		public static final ABComposeActionMethod ToolbarEdit = new ABComposeActionMethod("ToolbarEdit");
		public static final ABComposeActionMethod RightClickEdit = new ABComposeActionMethod("ToolbarEdit");
		protected ABComposeActionMethod(String method) {
			super(method);
		}
	}
	
	public static final String zNewABOverviewPaneIcon = "id=ztih__main_Contacts__ADDRBOOK_textCell";

	public static final String zContactTabIconBtn = "id=zb__App__Contacts_left_icon";
	public static final String zMailTabIconBtn = "id=zb__App__Mail_left_icon";
	public static final String zPreferencesTabIconBtn = "id=zb__App__Options_left_icon";

	public static final String zPreferencesABIconBtn = "id=ztab__PREF__"
			+ localize(locator.addressBook) + "_title";
	public static final String zPreferencesSaveIconBtn = "id=zb__PREF__SAVE_left_icon";

	public static final String zContactsFolder = "id=zti__main_Contacts__7_textCell";
	public static final String zEmailedContactsFolder = "id=zti__main_Contacts__13_textCell";

	public static final String zNewAddressBookIconBtn = "id=zb_newFolder_left_icon";

	public static final String zEditContactIconBtn = "id=zb__CNS__EDIT_left_icon";

	public static final String zNewContactMenuIconBtn = "id=zb__CNS__NEW_MENU_left_icon";
	public static final String zNewMenuDropdownIconBtn = "id=zb__CNS__NEW_MENU";

	public static final String zSaveContactMenuIconBtn = "id=zb__CN__SAVE_left_icon";
	public static final String zCancelContactMenuIconBtn = "id=zb__CN__CANCEL_left_icon";
	public static final String zPrintContactMenuIconBtn = "id=zb__CN__PRINT_left_icon";
	public static final String zDeleteContactMenuIconBtn = "id=zb__CN__DELETE_left_icon";
	public static final String zTagContactMenuIconBtn = "id=zb__CN__TAG_MENU_left_icon";

	public static final String zPersonalContactTabIconBtn = "id=ztab__CN__personal_title";
	public static final String zWorkContactTabIconBtn = "id=ztab__CN__work_title";
	public static final String zHomeContactTabIconBtn = "id=ztab__CN__home_title";
	public static final String zOtherContactTabIconBtn = "id=ztab__CN__other_title";
	public static final String zNotesContactTabIconBtn = "id=ztab__CN__notes_title";

	// New contact UI ids
	public static final String zPrefixEditField = "id=editcontactform_PREFIX_input";
	public static final String zFirstEditField = "id=editcontactform_FIRST_input";
	public static final String zMiddleEditField = "id=editcontactform_MIDDLE_input";
	public static final String zMaidenEditField = "id=editcontactform_MAIDEN_input";
	public static final String zLastEditField = "id=editcontactform_LAST_input";
	public static final String zSuffixEditField = "id=editcontactform_SUFFIX_input";
	public static final String zNicknameEditField = "id=editcontactform_NICKNAME_input";
	public static final String zCompanyEditField = "id=editcontactform_COMPANY_input";
	public static final String zJobTitleEditField = "id=editcontactform_TITLE_input";
	public static final String zDepartmentEditField = "id=editcontactform_DEPARTMENT_input";
	public static final String zUploadImageIcon = "id=editcontactform_IMAGE_img";
	public static final String zViewImageLink = "id=editcontactform_VIEW_IMAGE";
	public static final String zRemoveImageLink = "id=editcontactform_REMOVE_IMAGE";
	public static final String zContactsFolder_NewUI = "id=editcontactform_FOLDER_left_icon";
	public static final String zContactDetailsIconBtn = "id=editcontactform_DETAILS";

	public static final String zEmail1EditField = "id=*_EMAIL";
	public static final String zWorkEmail1EditField = "xpath=//div[@id='editcontactform_EMAIL_1']/input[contains(@id,'editcontactform_EMAIL_DWT')]";
	public static final String zPhone1EditField = "id=*_PHONE";
	public static final String zIM1EditField = "id=*_IM";
	public static final String zStreet1TextArea = "id=*STREET";
	public static final String zCity1EditField = "id=*_CITY";
	public static final String zState1EditField = "id=*_STATE";
	public static final String zPostalCode1EditField = "id=*_ZIP";
	public static final String zCountry1EditField = "id=*_COUNTRY";
	public static final String zURL1EditField = "id=*_URL";
	public static final String zOther1EditField = "id=*_OTHER";
	public static final String zNotesEditField = "id=editcontactform_NOTES_input";

	/**
	 * Waits for 2000ms and then Clicks on Address Book Tab
	 * 
	 * @throws Exception
	 */
	private void zNavigateToContact() throws Exception {
		zGoToApplication("Address Book");
	}

	public static void zNavigateToPrefImportExport() throws Exception {
		obj.zButton.zClick(zPreferencesTabIconBtn);
		obj.zTab.zClick(localize(locator.importExport));
	}

	public static void zNavigateToPreferenceAB() throws Exception {
		obj.zButton.zClick(zPreferencesTabIconBtn);
		obj.zTab.zClick(localize(locator.addressBook), "2");
	}

	/**
	 * Logs in and navigates to Address Book
	 * 
	 * @param username
	 *            :to pass the user name to be logged in
	 * @return
	 * @throws Exception
	 */
	public String zLoginAndNavigateToContact(String username)
			throws Exception {
		page.zLoginpage.zLoginToZimbraAjax(username);
		zNavigateToContact();
		return username;
	}

	/**
	 * This method is to create new Address Book
	 * 
	 * @param NewAddBookName
	 * @throws Exception
	 */
	public static void zCreateNewAddBook(String NewAddBookName)
			throws Exception {
		zWaitTillObjectExist("button",
				replaceUserNameInStaticId(zNewABOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(replaceUserNameInStaticId(zNewABOverviewPaneIcon)));
		obj.zMenuItem.zClick(localize(locator.newAddrBook));
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), NewAddBookName);
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
	}

	public static void zCreateNewAddBook(String NewAddBookName, String subFolder)
			throws Exception {
		zWaitTillObjectExist("button",
				replaceUserNameInStaticId(zNewABOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(replaceUserNameInStaticId(zNewABOverviewPaneIcon)));
		obj.zMenuItem.zClick(localize(locator.newAddrBook));
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), NewAddBookName);
		obj.zFolder.zClickInDlgByName(subFolder,
				localize(locator.createNewAddrBook));
		obj.zButton.zClickInDlg(localize(locator.ok));
		Thread.sleep(1000);
	}

	/**
	 * This method is to enter data in contact's field while creating contact
	 * 
	 * @param AddBook
	 * @param lastName
	 * @param MiddleName
	 * @param firstName
	 */
	public void zEnterBasicABData(ContactItem c) throws Exception {
		
		if ((c.lastName != null) && (!c.lastName.equals("")))
			obj.zEditField.zActivateAndType(zLastEditField, c.lastName);
		
		if ((c.middleName != null) && (!c.middleName.equals("")))
			obj.zEditField.zActivateAndType(zMiddleEditField, c.middleName);
		
		if ((c.firstName != null) && (!c.firstName.equals("")))
			obj.zEditField.zActivateAndType(zFirstEditField, c.firstName);
		
		if ((c.AddressBook != null) && (!c.AddressBook.name.equals("")) && (c.AddressBook.name.equals(localize(locator.contacts))) ) {
			obj.zButton.zClick(zContactsFolder_NewUI);
			Thread.sleep(1500);
			obj.zFolder.zClickInDlgByName(c.AddressBook.name, localize(locator.chooseAddrBook));
			obj.zButton.zClickInDlgByName(localize(locator.ok), localize(locator.chooseAddrBook));
		}
	}

	/*
	public static void zCreateContactInAddressBook(String AddBook,
			String lastName, String MiddleName, String firstName,
			String emailAddress) throws Exception {
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		obj.zButton.zClick(zNewContactMenuIconBtn);
		zWaitTillObjectExist("editfield", zLastEditField);
		if (lastName != "")
			obj.zEditField.zActivateAndType(zLastEditField, lastName);
		if (MiddleName != "")
			obj.zEditField.zActivateAndType(zMiddleEditField, MiddleName);
		if (firstName != "")
			obj.zEditField.zActivateAndType(zFirstEditField, firstName);
		if (emailAddress != "")
			obj.zEditField.zActivateAndType(zEmail1EditField, emailAddress);
		if (AddBook != "" && AddBook != localize(locator.contacts)) {
			obj.zButton.zClick(zContactsFolder_NewUI);
			Thread.sleep(1500);
			obj.zFolder.zClickInDlgByName(AddBook,
					localize(locator.chooseAddrBook));
			obj.zButton.zClickInDlgByName(localize(locator.ok),
					localize(locator.chooseAddrBook));
		}

		obj.zButton.zClick(zSaveContactMenuIconBtn);
		Thread.sleep(1500);
	}

*/
	public void navigateTo(ActionMethod method) throws HarnessException {
		try {
			zGoToApplication("Address Book");
		} catch (Exception e) {
			throw new HarnessException(e);
		}
	}
	

	/**
	 * Create a new contact item
	 */
	public ZimbraItem createItem(ActionMethod method, ZimbraItem item) throws HarnessException {
		try
		{
			ContactItem c = (ContactItem)item;
			
			// ??
			obj.zFolder.zClick(replaceUserNameInStaticId(zContactsFolder));
	
			if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
				Thread.sleep(2500);
			} else {
				Thread.sleep(2000);
			}
			
			// Click on "New" -> "Contact"
			obj.zButton.zClick(zNewContactMenuIconBtn);
			zWaitTillObjectExist("editfield", zLastEditField);
			
			// Enter the data
			zEnterBasicABData(c);
			
			// Click "Save"
			obj.zButton.zClick(zSaveContactMenuIconBtn);
	
			// TODO: wait for page to return
			Thread.sleep(2000);
			
			// ??
			obj.zFolder.zClick(localize(locator.contacts));
			
			return (c);	
			
		} catch (Exception e) {
			throw new HarnessException("Unable to create contact", e);
		}
	}
	
	public ZimbraItem modifyItem(ActionMethod method, ZimbraItem oldItem, ZimbraItem newItem) throws HarnessException {
		try {
			ContactItem oldContact = (ContactItem)oldItem;
			ContactItem newContact = (ContactItem)newItem;
			
			// Find the old contact and open it
			zSelectAndClickEdit(method, oldContact);
			
			// Update the field values
			zEnterBasicABData(newContact);
			
			// If a new email was specified, enter it.  
			// ??? why not in zEnterBasicABData?
			if ((newContact.email != null) && (!newContact.email.equals("")) )
				obj.zEditField.zActivateAndType(zEmail1EditField, newContact.email);
			
			Thread.sleep(1000);
			
			// Click Save
			obj.zButton.zClick(zSaveContactMenuIconBtn);

			return (newContact);
			
		}catch (Exception e) {
			throw new HarnessException("Unable to create contact", e);
		}

	}

	public void deleteItem(ActionMethod method, ZimbraItem item) throws HarnessException {
		throw new HarnessException("implement me");
	}
	

	/**
	 * This method is to select the contact and click on Edit button
	 * 
	 * @param contactName
	 * @throws Exception
	 */
	public static void zSelectAndClickEdit(ActionMethod m, ContactItem c)
			throws Exception {
		
		// Select the contact
		obj.zContactListItem.zClick(c.lastName);
		
		if ( m == ABComposeActionMethod.ToolbarEdit ) {
			obj.zButton.zClick(page.zABApp.zEditContactIconBtn);
		} else if ( m == ABComposeActionMethod.RightClickEdit) {
			obj.zContactListItem.zRtClick(c.lastName);
			obj.zMenuItem.zClick(page.zABApp.zRtClickContactEditMenuIconBtn);
		}
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
	}


	public static void zCreateContactWithAllFields(String prefix,
			String firstName, String middleName, String maidenName,
			String lastName, String suffix, String nickName, String jobTitle,
			String department, String company, String email, String phone,
			String iM, String street, String city, String state,
			String postalCode, String country, String uRL, String other,
			String notes) throws Exception {
		obj.zFolder
				.zClick(replaceUserNameInStaticId(page.zABCompose.zContactsFolder));
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Thread.sleep(2500);
		} else {
			Thread.sleep(2000);
		}
		obj.zButton.zClick(page.zABCompose.zNewContactMenuIconBtn);
		zWaitTillObjectExist("editfield", page.zABCompose.zLastEditField);

		obj.zEditField.zActivateAndType(page.zABCompose.zPrefixEditField,
				prefix);
		obj.zEditField.zActivateAndType(page.zABCompose.zFirstEditField,
				firstName);
		obj.zEditField.zActivateAndType(page.zABCompose.zMiddleEditField,
				middleName);
		obj.zEditField.zActivateAndType(page.zABCompose.zMaidenEditField,
				maidenName);
		obj.zEditField.zActivateAndType(page.zABCompose.zLastEditField,
				lastName);
		obj.zEditField.zActivateAndType(page.zABCompose.zSuffixEditField,
				suffix);
		obj.zEditField.zActivateAndType(page.zABCompose.zNicknameEditField,
				nickName);
		obj.zEditField.zActivateAndType(page.zABCompose.zJobTitleEditField,
				jobTitle);
		obj.zEditField.zActivateAndType(page.zABCompose.zDepartmentEditField,
				department);
		obj.zEditField.zActivateAndType(page.zABCompose.zCompanyEditField,
				company);
		obj.zEditField
				.zActivateAndType(page.zABCompose.zEmail1EditField, email);
		obj.zEditField
				.zActivateAndType(page.zABCompose.zPhone1EditField, phone);
		obj.zEditField.zActivateAndType(page.zABCompose.zIM1EditField, iM);
		obj.zTextAreaField.zActivateAndType(page.zABCompose.zStreet1TextArea,
				street);
		obj.zEditField.zActivateAndType(page.zABCompose.zCity1EditField, city);
		obj.zEditField
				.zActivateAndType(page.zABCompose.zState1EditField, state);
		obj.zEditField.zActivateAndType(page.zABCompose.zPostalCode1EditField,
				postalCode);
		obj.zEditField.zActivateAndType(page.zABCompose.zCountry1EditField,
				country);
		obj.zEditField.zActivateAndType(page.zABCompose.zURL1EditField, uRL);
		obj.zEditField
				.zActivateAndType(page.zABCompose.zOther1EditField, other);
		obj.zTextAreaField.zActivateAndType(page.zABCompose.zNotesEditField,
				notes);

		obj.zButton.zClick(page.zABCompose.zSaveContactMenuIconBtn);
		Thread.sleep(2000);
	}

	public static void zVerifyContactWithAllFields(String prefix,
			String firstName, String middleName, String maidenName,
			String lastName, String suffix, String nickName, String jobTitle,
			String department, String company, String email, String phone,
			String iM, String street, String city, String state,
			String postalCode, String country, String uRL, String other,
			String notes) throws Exception {
		assertReport(prefix, obj.zEditField
				.zGetInnerText(page.zABCompose.zPrefixEditField),
				"Prefix value is not saved properly");
		assertReport(firstName, obj.zEditField
				.zGetInnerText(page.zABCompose.zFirstEditField),
				"FirstName value is not saved properly");
		assertReport(middleName, obj.zEditField
				.zGetInnerText(page.zABCompose.zMiddleEditField),
				"MiddleName value is not saved properly");
		assertReport(maidenName, obj.zEditField
				.zGetInnerText(page.zABCompose.zMaidenEditField),
				"MaidenName value is not saved properly");
		assertReport(lastName, obj.zEditField
				.zGetInnerText(page.zABCompose.zLastEditField),
				"LastName value is not saved properly");
		assertReport(suffix, obj.zEditField
				.zGetInnerText(page.zABCompose.zSuffixEditField),
				"Suffix value is not saved properly");
		assertReport(nickName, obj.zEditField
				.zGetInnerText(page.zABCompose.zNicknameEditField),
				"NickName value is not saved properly");
		assertReport(jobTitle, obj.zEditField
				.zGetInnerText(page.zABCompose.zJobTitleEditField),
				"Jobtitle value is not saved properly");
		assertReport(department, obj.zEditField
				.zGetInnerText(page.zABCompose.zDepartmentEditField),
				"Department value is not saved properly");
		assertReport(company, obj.zEditField
				.zGetInnerText(page.zABCompose.zCompanyEditField),
				"Company value is not saved properly");
		assertReport(email, obj.zEditField
				.zGetInnerText(page.zABCompose.zEmail1EditField),
				"Email value is not saved properly");
		assertReport(phone, obj.zEditField
				.zGetInnerText(page.zABCompose.zPhone1EditField),
				"Phone value is not saved properly");
		assertReport(iM, obj.zEditField
				.zGetInnerText(page.zABCompose.zIM1EditField),
				"IM value is not saved properly");
		assertReport(street, obj.zTextAreaField
				.zGetInnerText(page.zABCompose.zStreet1TextArea),
				"Street value is not saved properly");
		assertReport(city, obj.zEditField
				.zGetInnerText(page.zABCompose.zCity1EditField),
				"City value is not saved properly");
		assertReport(state, obj.zEditField
				.zGetInnerText(page.zABCompose.zState1EditField),
				"State value is not saved properly");
		assertReport(postalCode, obj.zEditField
				.zGetInnerText(page.zABCompose.zPostalCode1EditField),
				"PostalCode value is not saved properly");
		assertReport(country, obj.zEditField
				.zGetInnerText(page.zABCompose.zCountry1EditField),
				"Country value is not saved properly");
		assertReport(uRL, obj.zEditField
				.zGetInnerText(page.zABCompose.zURL1EditField),
				"URL value is not saved properly");
		assertReport(other, obj.zEditField
				.zGetInnerText(page.zABCompose.zOther1EditField),
				"Other value is not saved properly");
		assertReport(notes, obj.zEditField
				.zGetInnerText(page.zABCompose.zNotesEditField),
				"Notes value is not saved properly");
	}

	/**
	 * This method is to verify the contact's last
	 * Name,MiddleName,firstName,email are edited successfully or not
	 * 
	 * @param NewlastName
	 * @param NewMiddleName
	 * @param NewfirstName
	 * @param NeweMail
	 * @return
	 * @throws Exception
	 */
	public boolean zVerifyEditContact(ZimbraItem item) throws Exception {
		
		ContactItem c = (ContactItem)item;
		ContactItem actual = new ContactItem();

		zSelectAndClickEdit(ABComposeActionMethod.ToolbarEdit, c);

		// TODO: would be great to create 'actual' from a GUI method
		
		if ((c.lastName != null) && (!c.lastName.equals("")) ) {
			actual.lastName = obj.zEditField.zGetInnerText(zLastEditField);
			if (!actual.lastName.equals(c.lastName))
				return (false);
		}
		
		if ((c.middleName != null) && (!c.middleName.equals(""))) {
			actual.middleName = obj.zEditField.zGetInnerText(zMiddleEditField);
			if (!actual.middleName.equals(c.middleName))
				return (false);
		}
		
		if ((c.firstName != null) && (!c.firstName.equals(""))) {
			actual.firstName = obj.zEditField.zGetInnerText(zFirstEditField);
			if (!actual.firstName.equals(c.firstName))
				return (false);
		}
		
		if ((c.email != null) && (!c.email.equals(""))) {
			actual.email = obj.zEditField.zGetInnerText(zEmail1EditField);
			if (!actual.email.equals(c.email))
				return (false);
		}
		
		// Made it here, all fields were the same.
		return (true);

	}
}