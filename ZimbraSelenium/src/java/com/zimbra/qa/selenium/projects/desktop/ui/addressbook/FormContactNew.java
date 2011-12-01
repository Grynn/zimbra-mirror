package com.zimbra.qa.selenium.projects.desktop.ui.addressbook;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
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

		public static final String zEmail1EditField = "css=input[id^=editcontactform_EMAIL_]";
      public static final String zWorkEmail1EditField = "css=div#editcontactform_EMAIL_1 input[id^=editcontactform_EMAIL_DWT]";
      public static final String zPhone1EditField = "css=div#editcontactform_PHONE_0 input";
      public static final String zIM1EditField = "css=div#editcontactform_IM_0 input";
      public static final String zStreet1TextArea = "css=div#editcontactform_ADDRESS_0_STREET textarea";
      public static final String zCity1EditField = "css=div#editcontactform_ADDRESS_0_CITY input";
      public static final String zState1EditField = "css=div#editcontactform_ADDRESS_0_STATE input";
      public static final String zPostalCode1EditField = "css=div#editcontactform_ADDRESS_0_ZIP input";
      public static final String zCountry1EditField = "css=div#editcontactform_ADDRESS_0_COUNTRY input";
      public static final String zURL1EditField = "css=div#editcontactform_URL_0 input";
      public static final String zOther1EditField = "css=div#editcontactform_OTHER_0 input";
      public static final String zNotesEditField = "css=textarea#editcontactform_NOTES_input";

      public static final String zPrefixCheckbox = "css=td.ZWidgetTitle:contains('Prefix')";
      public static final String zMiddleCheckbox = "css=td.ZWidgetTitle:contains('Middle')";
      public static final String zMaidenCheckbox = "css=td.ZWidgetTitle:contains('Maiden')";
      public static final String zSuffixCheckbox = "css=td.ZWidgetTitle:contains('Suffix')";
      public static final String zNicknameCheckbox = "css=td.ZWidgetTitle:contains('Nickname')";
      public static final String zDepartmentCheckbox = "css=td.ZWidgetTitle:contains('Department')";

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

	public void zDisplayHiddenName() throws HarnessException {
      zClick(Locators.zContactDetailsIconBtn); 
      zClick(Locators.zPrefixCheckbox);
      zClick(Locators.zContactDetailsIconBtn); 
      zClick(Locators.zMiddleCheckbox);
      zClick(Locators.zContactDetailsIconBtn); 
      zClick(Locators.zMaidenCheckbox);
      zClick(Locators.zContactDetailsIconBtn); 
      zClick(Locators.zSuffixCheckbox);
      zClick(Locators.zContactDetailsIconBtn); 
      zClick(Locators.zNicknameCheckbox);
      zClick(Locators.zContactDetailsIconBtn); 
      zClick(Locators.zDepartmentCheckbox);
   }

   public void zFillField(String locator, String value) throws HarnessException {
      tracer.trace("Set "+ locator +" to "+ value);
   
      // Make sure the button exists
      if ( !this.sIsElementPresent(locator) )
         throw new HarnessException("Field is not present field="+ locator +" value="+ value);
         
      
      if (zIsBrowserMatch(BrowserMasks.BrowserMaskChrome)) { 
           sType(locator,value);
           sTypeKeys(locator,value);         
      }
      else {
         //highlight text
         String id= "editcontactform_NOTES_input";
         ClientSessionFactory.session().selenium().getEval(
               "this.browserbot.getUserWindow().document.getElementById('"
               + id + "')" + ".select()");

         this.sFocus(locator);
         this.zClick(locator);
         // Set the text property to empty before typing
         this.sType(locator, "");
         this.zKeyboard.zTypeCharacters(value);

      }
        
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
         zFillField(Locators.zFirstEditField, contact.firstName);

      }
      
      if ( contact.lastName != null ) {         
         zFillField(Locators.zLastEditField, contact.lastName);       
      }
      
      if ( contact.middleName != null ) {       
         zFillField(Locators.zMiddleEditField, contact.middleName);
      }
      
      if ( contact.email != null ) {         
         zFillField(Locators.zEmail1EditField, contact.email);
      }
      
      if (contact.ContactAttributes.size() >0) {
         for ( String key:contact.ContactAttributes.keySet()) {
            zFillField(key, contact.ContactAttributes.get(key));
         }
      }
      
      //TODO: need fix xpath for zEmail1EditField
      //if ( contact.email != null ) {       
      // this.sType(Locators.zEmail1EditField, contact.email);
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
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}

}
