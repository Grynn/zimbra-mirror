package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;



public class FormContactNew extends AbsForm {
	
	public static class Locators {
		
		public static final String zNewContactMenuIconBtn = "id=zb__CNS__NEW_MENU_left_icon";
		public static String zActiveEditForm = "editcontactform";
		
		public static String zPrefixEditField = "id=EDITCONTACTFORM_PREFIX_input";
		public static String zFirstEditField = "id=EDITCONTACTFORM_FIRST_input";
		public static String zMiddleEditField = "id=EDITCONTACTFORM_MIDDLE_input";
		public static String zMaidenEditField = "id=EDITCONTACTFORM_MAIDEN_input";
		public static String zLastEditField = "id=EDITCONTACTFORM_LAST_input";
		public static String zSuffixEditField = "id=EDITCONTACTFORM_SUFFIX_input";
		public static String zNicknameEditField = "id=EDITCONTACTFORM_NICKNAME_input";
		public static String zCompanyEditField = "id=EDITCONTACTFORM_COMPANY_input";
		public static String zJobTitleEditField = "id=EDITCONTACTFORM_TITLE_input";
		public static String zDepartmentEditField = "id=EDITCONTACTFORM_DEPARTMENT_input";
		public static String zUploadImageIcon = "id=EDITCONTACTFORM_IMAGE_img";
		public static String zViewImageLink = "id=EDITCONTACTFORM_VIEW_IMAGE";
		public static String zRemoveImageLink = "id=EDITCONTACTFORM_REMOVE_IMAGE";
		public static String zContactsFolder_NewUI = "id=EDITCONTACTFORM_FOLDER_left_icon";
		public static String zContactDetailsIconBtn = "id=EDITCONTACTFORM_DETAILS";
        // TODO need fixed id for email
		public static String zEmail1EditField = "css=input[id^=EDITCONTACTFORM_EMAIL_]";
		public static String zWorkEmail1EditField = "css=div#EDITCONTACTFORM_EMAIL_1 input[id^=EDITCONTACTFORM_EMAIL_DWT]";
		public static String zPhone1EditField = "css=div#EDITCONTACTFORM_PHONE_0 input";
		public static String zIM1EditField = "css=div#EDITCONTACTFORM_IM_0 input";
		public static String zStreet1TextArea = "css=div#EDITCONTACTFORM_ADDRESS_0_STREET textarea";
		public static String zCity1EditField = "css=div#EDITCONTACTFORM_ADDRESS_0_CITY input";
		public static String zState1EditField = "css=div#EDITCONTACTFORM_ADDRESS_0_STATE input";
		public static String zPostalCode1EditField = "css=div#EDITCONTACTFORM_ADDRESS_0_ZIP input";
		public static String zCountry1EditField = "css=div#EDITCONTACTFORM_ADDRESS_0_COUNTRY input";
		public static String zURL1EditField = "css=div#EDITCONTACTFORM_URL_0 input";
		public static String zOther1EditField = "css=div#EDITCONTACTFORM_OTHER_0 input";
		public static String zNotesEditField = "css=textarea#EDITCONTACTFORM_NOTES_input";

			
		public static final String zPrefixCheckbox = "td.ZWidgetTitle:contains('Prefix')";
		public static final String zMiddleCheckbox = "td.ZWidgetTitle:contains('Middle')";
		public static final String zMaidenCheckbox = "td.ZWidgetTitle:contains('Maiden')";
		public static final String zSuffixCheckbox = "td.ZWidgetTitle:contains('Suffix')";
		public static final String zNicknameCheckbox = "td.ZWidgetTitle:contains('Nickname')";
		public static final String zDepartmentCheckbox = "td.ZWidgetTitle:contains('Department')";

	}

	public static class Toolbar extends  AbsSeleniumObject{
		public static final String DELETE="id=zb__CNS__DELETE";
		public static final String PRINT="id=zb__CNS__PRINT";
		public static final String TAG="id=zb__CNS__TAG_MENU";
		public static final String FORWARD="id=zb__CNS__SEND_CONTACTS_IN_EMAIL";
		
		public static final String NEWTAG="id=zb__CNS__TAG_MENU|MENU|NEWTAG";
		public static final String REMOVETAG="id=zb__CNS__TAG_MENU|MENU|REMOVETAG";
	
		public static final String CLOSE="css=[id^=zb__CN][id$=__CANCEL]";
		public static String SAVE="css=[id^=zb__CN][id$=__SAVE]";

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
				
		try {		
		    for (int i=0; ; i++) {
		    	String id = sGetEval("window.document.getElementsByClassName('ZToolbarTable')[" + i + "].offsetParent.id" );
		    	if (id.startsWith("ztb") && zIsVisiblePerPosition(id, 0, 0)) {
		    		Toolbar.SAVE = id.replaceFirst("ztb","zb") + "__SAVE";		    		
		    		logger.info("active toolbar save = " + Toolbar.SAVE);
		    		break;
		    	}		    					    	
	        }	
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}

		// Look for "Save"		
		// Check if the item is enabled
		if (zIsElementDisabled(Toolbar.SAVE )) {
			throw new HarnessException("Tried clicking on "+ Toolbar.SAVE +" but it was disabled ");
		}

		// Click on it
		zClick(Toolbar.SAVE);
		
		// Need to wait for the contact save
		zWaitForBusyOverlay();		
	}

	// reset the form
	public void zReset() throws HarnessException {
		logger.info("FormMailNew.zReset()");
		String[] fieldList = {getLocator(Locators.zFirstEditField), 
				              getLocator(Locators.zLastEditField) };
		                      //TODO: ,getLocators(Locators.zEmail1EditField};
		
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
		Locators.zContactDetailsIconBtn = getLocator(Locators.zContactDetailsIconBtn);
		zClick(Locators.zContactDetailsIconBtn); 
		SleepUtil.sleepVerySmall();
		
		String prefix="css=div#";
		try {		
		    for (int i=0; ; i++) {				    
		    	String id = sGetEval("window.document.getElementsByClassName('DwtMenu ZHasCheck')[" + i + "].id" );
		    	if (zIsVisiblePerPosition(id, 0, 0)) {
		    		prefix = prefix + id + " ";		    		
		    		logger.info("active menu id = " + id);
		    		break;
		    	}		    					    	
	        }	
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
				

		zClick(prefix + Locators.zPrefixCheckbox);
		zWaitForBusyOverlay();
		zClick(Locators.zContactDetailsIconBtn);
		SleepUtil.sleepVerySmall();
		zClick(prefix + Locators.zMiddleCheckbox);
		zWaitForBusyOverlay();
		zClick(Locators.zContactDetailsIconBtn); 
		SleepUtil.sleepVerySmall();
		zClick(prefix + Locators.zMaidenCheckbox);
		zWaitForBusyOverlay();
		zClick(Locators.zContactDetailsIconBtn); 
		SleepUtil.sleepVerySmall();
		zClick(prefix + Locators.zSuffixCheckbox);
		zWaitForBusyOverlay();
		zClick(Locators.zContactDetailsIconBtn); 
		SleepUtil.sleepVerySmall();
		zClick(prefix + Locators.zNicknameCheckbox);
		zWaitForBusyOverlay();
		zClick(Locators.zContactDetailsIconBtn); 
		SleepUtil.sleepVerySmall();
		zClick(prefix + Locators.zDepartmentCheckbox);
		zWaitForBusyOverlay();
	}

	public void zFillField(String locator, String value) throws HarnessException {
		tracer.trace("Set "+ locator +" to "+ value);
	
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ locator +" locator="+ value);
			
		
		if (zIsBrowserMatch(BrowserMasks.BrowserMaskChrome)) { 
	        sType(locator,value);
	        sTypeKeys(locator,value);			
		}
		else {
		   //reset note 
			sType(getLocator(Locators.zNotesEditField) ,"");
		
			//The following code to simulate paste action from user (Ctrl-V) bug #
			//Use "Notes" to store text which will be entered into clipboard (Ctrl-X)	 
			sType(getLocator(Locators.zNotesEditField) ,value); //

			//highlight text
			String id= Locators.zActiveEditForm + "_NOTES_input";
			ClientSessionFactory.session().selenium().getEval(
					"this.browserbot.getUserWindow().document.getElementById('"
					+ id + "')" + ".select()");
			
			//cut text and put into clipboard
			sKeyDownNative(KeyEvent.VK_CONTROL+"");
			sKeyPressNative(KeyEvent.VK_X+"");				
			sKeyUpNative(KeyEvent.VK_CONTROL+"");

			//paste text to the target locator
			sFocus(locator);	
			SleepUtil.sleepVerySmall();
			sKeyDownNative(KeyEvent.VK_CONTROL+"");
			sKeyPressNative(KeyEvent.VK_V+"");				
			sKeyUpNative(KeyEvent.VK_CONTROL+"");
						
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
			zFillField(getLocator(Locators.zFirstEditField), contact.firstName);

		}
		
		if ( contact.lastName != null ) {			
			zFillField(getLocator(Locators.zLastEditField), contact.lastName);	    
		}
		
		if ( contact.middleName != null ) {			
			zFillField(getLocator(Locators.zMiddleEditField), contact.lastName);
		}
		
		if ( contact.email != null ) {			
			zFillField(getLocator(Locators.zEmail1EditField), contact.email);
		}
		
		if (contact.ContactAttributes.size() >0) {
			for ( String key:contact.ContactAttributes.keySet()) {
				zFillField(getLocator(key), contact.ContactAttributes.get(key));
			}
		}
		
		//TODO: need fix xpath for zEmail1EditField
		//if ( contact.email != null ) {			
		//	this.sType(getLocator(Locators.zEmail1EditField, contact.email);
		//}
		
			
	}
	
	public static String getLocator(String locator) {
         
		return locator.replaceAll("EDITCONTACTFORM", Locators.zActiveEditForm);
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		//set parameter zActiveEditForm		
		try {		
		    for (int i=0; ; i++) {	  		   
		    	String id = sGetEval("window.document.getElementsByClassName('zmEditContactView')[" + i + "].id" );
		    	if (zIsVisiblePerPosition(id, 0, 0)) {
		    		Locators.zActiveEditForm = id;
		    		logger.info("active id = " + id);
		    		return true;
		    	}		    					    	
	        }	
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		
		return false;					
	}

}
