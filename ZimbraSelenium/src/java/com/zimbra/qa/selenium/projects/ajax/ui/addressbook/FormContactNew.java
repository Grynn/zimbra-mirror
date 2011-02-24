package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;



public class FormContactNew extends AbsForm {
	
	public static class Locators {
		
		public static final String zNewContactMenuIconBtn = "id=zb__CNS__NEW_MENU_left_icon";
		
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
        // TODO need fixed id for email
		public static final String zEmail1EditField = "id=editcontactform_EMAIL_*";
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

			
	}

	public static class Toolbar extends  AbsSeleniumObject{
		public static final String DELETE="id=zb__CNS__DELETE";
		public static final String PRINT="id=zb__CNS__PRINT";
		public static final String TAG="id=zb__CNS__TAG_MENU";
		public static final String FORWARD="id=zb__CNS__SEND_CONTACTS_IN_EMAIL";
		
		public static final String NEWTAG="id=zb__CNS__TAG_MENU|MENU|NEWTAG";
		public static final String REMOVETAG="id=zb__CNS__TAG_MENU|MENU|REMOVETAG";
	
		public static final String CLOSE="id=zb__CN__CANCEL";
		public static final String SAVE="id=zb__CN__SAVE";

	}
		
	
	public FormContactNew(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormContactNew.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zSubmit() throws HarnessException {
		logger.info("FormContactNew.submit()");
		save();
	}
	
	
	protected void save() throws HarnessException {
		logger.info("FormContactNew.save()");
		
		// Look for "Save"
		boolean visible = this.sIsElementPresent(Toolbar.SAVE);
		if ( !visible )
			throw new HarnessException("Save button is not visible "+ Toolbar.SAVE);
		
		// Click on it
		zClick(Toolbar.SAVE);
		
		// Need to wait for the contact save
		zWaitForBusyOverlay();		
	}

	// reset the form
	public void zReset() throws HarnessException {
		logger.info("FormMailNew.zReset()");
		String[] fieldList = {Locators.zFirstEditField, 
				              Locators.zLastEditField };
		                      //TODO: ,Locators.zEmail1EditField};
		
		for (int i=0; i < fieldList.length; i++) {
		  this.sType(fieldList[i], "");
		}
	}
	
	public static class Field {
		
		public static final Field FirstName = new Field("FirstName");
		public static final Field LastName = new Field("LastName");
		
		
		private String field;
		private Field(String name) {
			field = name;
		}
		
		@Override
		public String toString() {
			return (field);
		}

	}
	

	public void zFillField(Field field, String value) throws HarnessException {
		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
		
		if ( field == Field.FirstName ) {
			
			locator = Locators.zFirstEditField;
			
			// FALL THROUGH
			
		} else if ( field == Field.LastName ) {
			
			locator = Locators.zLastEditField;
			
			// FALL THROUGH
			
		} else {
			throw new HarnessException("not implemented for field "+ field);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator was null for field "+ field);
		}
		
		// Default behavior, enter value into locator field
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Enter text
		this.sFocus(locator);
		this.zClick(locator);			
		zKeyboard.zTypeCharacters(value);


	}
	
	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("FormMailNew.fill(IItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a ContactItem
		if ( !(item instanceof ContactItem) ) {
			throw new HarnessException("Invalid item type - must be ContactItem");
		}
		
		// Convert object to ContactItem
		ContactItem contact = (ContactItem) item;
		
		// Fill out the form		
		if ( contact.firstName != null ) {
			
			zFillField(Field.FirstName, contact.firstName);

		}
		
		if ( contact.lastName != null ) {
			
			zFillField(Field.LastName, contact.lastName);


		}
		
		//TODO: need fix xpath for zEmail1EditField
		//if ( contact.email != null ) {			
		//	this.sType(Locators.zEmail1EditField, contact.email);
		//}

		SleepUtil.sleepMedium();
			
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zFirstEditField;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}

}
