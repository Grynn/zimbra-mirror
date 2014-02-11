/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.ui.addressbook;

import java.util.*;
import java.util.Map.Entry;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.touch.ui.AppTouchClient;
import com.zimbra.qa.selenium.projects.touch.ui.mail.FormMailNew.Locators;



/**
 * @author zimbra
 *
 */
public class FormContactNew extends AbsForm {

	
	public static class Locators {
		
		public static final String zSaveButton = "css=div[id^='ext-contactpanel'] span[class='x-button-label']:contains('Save')";
		public static final String zCancelButton = "css=div[id^='ext-contactpanel'] span[class='x-button-label']:contains('Cancel')";	
		
		public static final String zNewContactMenuIconBtn = "id=zb__CNS__NEW_MENU_left_icon";
		public static String zActiveEditForm = "editcontactform";
		
		public static String zFullnameField = "id=EDITCONTACTFORM_FULLNAME";
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
        public static String zLocation = "css=td#EDITCONTACTFORM_FOLDER_title";

        //fileAs dropdown elements
        public static String zFileAsTitle = "css=td#EDITCONTACTFORM_FILE_AS_title";
        public static String zFileAsDropdown = "css=td#EDITCONTACTFORM_FILE_AS_dropdown>div.ImgSelectPullDownArrow";        
        public static String zFileAsLastCommaFirst ="td.ZWidgetTitle:contains('Last, First')";
        public static String zFileAsFirstLast = "td.ZWidgetTitle:contains('First Last')";
        public static String zFileAsCompany = "td.ZWidgetTitle:contains('Company')";     
        public static String zFileAsLastCommaFirstCompany = "td.ZWidgetTitle:contains('Last, First (Company)')";
        public static String zFileAsFirstLastCompany = "td.ZWidgetTitle:contains('First Last (Company)')";
        public static String zFileAsCompanyLastCommaFirst = "td.ZWidgetTitle:contains('Company (Last, First)')";
        public static String zFileAsCompanyFirstLast = "td.ZWidgetTitle:contains('Company (First Last)')";
       
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
		
	/**
		There are a lot of 'duplicate' divs involved with the Contact Group New/Edit forms
		We need to determine which div is active to make the code work correctly
	 */
	protected String MyDivID = null;
	
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
		zToolbarPressButton(Button.B_SAVE);
	}
	

	public static class Field {
		
		public static final Field FullName		= new Field("fullName", null);
		public static final Field NamePrefix	= new Field("namePrefix", "input[name='namePrefix']");
		public static final Field MiddleName	= new Field("middleName", "input[name='middleName']");
		public static final Field MaidenName	= new Field("maidenName", "input[name='maidenName']");
		public static final Field NameSuffix	= new Field("nameSuffix", "input[name='nameSuffix']");
		public static final Field Nickname		= new Field("nickname", "input[name='nickname']");
		public static final Field JobTitle		= new Field("jobTitle", "input[name='jobTitle']");
		public static final Field Department	= new Field("department", "input[name='department']");
		public static final Field PhoneNumber	= new Field("phone", "input[name='phone']");
		public static final Field MobilePhone	= new Field("mobilePhone", "input[name='phone']");
		public static final Field IM			= new Field("imAddress1", "input[id*='_IM_']");
		public static final Field HomeStreet	= new Field("homeStreet", "textarea[id$='_STREET_input']");
		public static final Field HomeCity		= new Field("homeCity", "input[id$='_CITY_input']");
		public static final Field HomePostalCode = new Field("homePostalCode", "input[id$='_ZIP_input']");
		public static final Field HomeCountry	= new Field("homeCountry", "input[id$='_COUNTRY_input']");
		public static final Field HomeURL		= new Field("homeURL", "input[id*='_URL_']");
		public static final Field Birthday		= new Field("birthday", "input[id*='_OTHER_']");
		public static final Field Notes			= new Field("notes", "textarea[id$='_NOTES_input']");
        
		// Basic fields
		public static final Field FirstName		= new Field("firstName", "input[name='firstName']");
		public static final Field LastName		= new Field("lastName", "input[name='lastName']");
		public static final Field Company		= new Field("company", "input[name='company']");
        
		// Hidden fields
		public static final Field Email			= new Field("email", "input[name='email']");		
		public static final Field OtherStreet	= new Field("otherStreet", "input[name='street0']");
		public static final Field OtherState	= new Field("otherState", "input[name='state0']");
		public static final Field OtherCity		= new Field("otherCity", "input[name='city0']");
		public static final Field OtherZipcode  = new Field("otherZipcode", "input[name='postalCode0']");
		public static final Field OtherCountry	= new Field("otherCountry", "input[name='country0']");
		public static final Field WorkURL		= new Field("workURL", "input[name=url]");
		

		
		private String field;
		private String partialLocator;
		private Field(String name, String locator) {
			field = name;
			partialLocator = locator;
		}
		
		/**
		 * Prepend "css=div#<ID>" to this locator to find the field in the new contact form
		 * @return
		 */
		public String getLocator() {
			return (partialLocator);
		}
		
		@Override
		public String toString() {
			return (field);
		}

		private static List<Field> fields = null;
		public static Field fromString(String key) throws HarnessException {
			if ( fields == null ) {
				fields = new ArrayList<Field>();
				fields.add(NamePrefix);
				fields.add(FirstName);
				fields.add(MiddleName);
				fields.add(MaidenName);
				fields.add(LastName);
				fields.add(NameSuffix);
				fields.add(Nickname);
				fields.add(JobTitle);
				fields.add(Company);
				fields.add(Department);
				fields.add(Email);
				fields.add(PhoneNumber);
				fields.add(MobilePhone);
				fields.add(IM);
				fields.add(HomeStreet);
				fields.add(HomeCity);
				fields.add(HomePostalCode);
				fields.add(HomeCountry);
				fields.add(HomeURL);
				fields.add(Birthday);
				fields.add(Notes);
			}
			for(Field f : fields) {
				if (f.field.equals(key)) {
					return (f);
				}
			}
			throw new HarnessException("Unknown field key: "+ key);
		}
	}
	
	
	/**
	 * Expand all the hidden contact fields
	 * @throws HarnessException
	 */
	public void zDisplayHiddenName() throws HarnessException {
		
		// Expand all hidden fields
        this.zToolbarPressPulldown(Button.B_EXPAND, Button.O_SUFFIX);
        this.zToolbarPressPulldown(Button.B_EXPAND, Button.O_MIDDLE);
        this.zToolbarPressPulldown(Button.B_EXPAND, Button.O_MAIDEN);
        this.zToolbarPressPulldown(Button.B_EXPAND, Button.O_PREFIX);
        this.zToolbarPressPulldown(Button.B_EXPAND, Button.O_NICKNAME);
        
        this.zToolbarPressPulldown(Button.B_EXPAND, Button.O_DEPARTMENT);
        this.zToolbarPressPulldown(Button.B_EXPAND, Button.O_JOB_TITLE);
        
        ArrayList<String> locators = new ArrayList<String>();
		locators.add("css=div[class='contact-form-add-field-button-text']:contains('Add Email Address')");
		locators.add("css=div[class='contact-form-add-field-button-text']:contains('Add Phone Number')");
		locators.add("css=div[class='contact-form-add-field-button-text']:contains('Add Physical Address')");
		locators.add("css=div[class='contact-form-add-field-button-text']:contains('Add Website URL')");
	    
		Iterator iter = locators.iterator();
		while( iter.hasNext() ){
			
			String lctr = (String) iter.next();
			
			// Click it
			zClickAt(lctr, "0,0");

			// if the app is busy, wait for it to become active again
			this.zWaitForBusyOverlay();
			
		}

	
	}
	
	private String MyToolbarID = null;
	/**
	 * Determine the z-shell <div/> that contains the Search Contacts, GAL, Personal and Shared
	 * menu.
	 * 
	 * See https://bugzilla.zimbra.com/show_bug.cgi?id=77791
	 * @return The z_shell Child ID
	 * @throws HarnessException 
	 */
	protected String getToolbarID() throws HarnessException {
		logger.info("getToolbarID()");
		
		if ( MyToolbarID != null ) {
			logger.info("getToolbarID() - Re-using "+ MyToolbarID);
			return (MyToolbarID);
		}
		
		String locator = "//div[@id='z_shell']/div[contains(@id, 'ztb__CN-')]";
		int count = this.sGetXpathCount(locator);
		
		for (int i = 1; i <= count; i++) {
			String id = this.sGetAttribute(locator + "["+ i +"]@id");
			if ( this.zIsVisiblePerPosition("css=div#"+ id, 0, 0) ) {
				MyToolbarID = id;
				return (id);
			}
		}

		throw new HarnessException("Unable to determine the Toolbar ID "+ this.sGetHtmlSource());
	}

	private String MyExpandHiddenID = null;
	/**
	 * Determine the z-shell <div/> that contains the hidden name menu.
	 * 
	 * See https://bugzilla.zimbra.com/show_bug.cgi?id=77791
	 * @return The z_shell Child ID
	 * @throws HarnessException 
	 */
	protected String getExpandHiddenID() throws HarnessException {
		logger.info("getExpandHiddenID()");
		
		if ( MyExpandHiddenID != null ) {
			logger.info("getExpandHiddenID() - Re-using "+ MyExpandHiddenID);
			return (MyExpandHiddenID);
		}
		
		String locator = "//div[@id='z_shell']/div[contains(@class,'DwtMenu')][contains(@class,'ZHasCheck')]";
		int count = this.sGetXpathCount(locator);
		
		for (int i = 1; i <= count; i++) {
			String id = this.sGetAttribute(locator + "["+ i +"]@id");
			if ( this.zIsVisiblePerPosition("css=div#"+ id, 0, 0) ) {
				MyExpandHiddenID = id;
				return (id);
			}
		}

		throw new HarnessException("Unable to determine the Hidden ID "+ this.sGetHtmlSource());
	}

	private String MyFileAsMenuID = null;
	/**
	 * Determine the z-shell <div/> that contains the hidden FileAs menu.
	 * 
	 * See https://bugzilla.zimbra.com/show_bug.cgi?id=77791
	 * @return The z_shell Child ID
	 * @throws HarnessException 
	 */
	protected String getFileAsMenuID() throws HarnessException {
		logger.info("getFileAsMenuID()");
		
		if ( MyFileAsMenuID != null ) {
			logger.info("getFileAsMenuID() - Re-using "+ MyFileAsMenuID);
			return (MyFileAsMenuID);
		}
		
		String locator = "//div[@id='z_shell']/div[contains(@id, '_FILE_AS_Menu')]";
		int count = this.sGetXpathCount(locator);
		
		for (int i = 1; i <= count; i++) {
			String id = this.sGetAttribute(locator + "["+ i +"]@id");
			if ( this.zIsVisiblePerPosition("css=div#"+ id, 0, 0) ) {
				MyFileAsMenuID = id;
				return (id);
			}
		}

		throw new HarnessException("Unable to determine the File As ID "+ this.sGetHtmlSource());
	}

	
    
    public String zGetFieldText(Field field) throws HarnessException {
		String locator = null;
		
		if ( field == Field.FullName ) {

			locator = "css=div#"+ MyDivID + " div[id$='_FULLNAME']";
			
		} else {
			
			throw new HarnessException("implement zGetFieldText("+ field +")");
			
		}
		
		if ( !this.sIsElementPresent(locator) ) 
			throw new HarnessException("Unable to locate field "+ field);

		return (this.sGetText(locator));
			
    }
	
	public void zFillField(Field field, String value) throws HarnessException {
		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
	
		locator = String.format("css=%s",field.getLocator());
			
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
			
		// Put the cursor on input text box
		this.sFocus(locator);
		this.zClick(locator);
		this.zWaitForBusyOverlay();

		// Enter text
		this.sType(locator, value);
		this.sFireEvent(locator, "keyup");
			
		// Wait for any busy overlay
		this.zWaitForBusyOverlay();
			
		return;

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
		
		if ( contact.email != null ) {
			zFillField(Field.fromString("email"), contact.email);
		}
		if ( contact.firstName != null ) {
			zFillField(Field.fromString("firstName"), contact.firstName);
		}
		if ( contact.lastName != null ) {
			zFillField(Field.fromString("lastName"), contact.lastName);
		}
		
		for ( Entry<String, String> entry : contact.ContactAttributes.entrySet() ) {
			zFillField(Field.fromString(entry.getKey()), entry.getValue());			
		}
		
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
		
		
        String locator = Locators.zSaveButton;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false);	
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);
		}
		
		logger.info(myPageName() + " zIsActive() = true");
		return (true);

	}

	
	
	/**
	 * Press the toolbar button
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ pulldown +", "+ option +")");
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);
		
		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		
		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned
		

		if ( pulldown == Button.B_EXPAND ) {
			   
			   if ( option == Button.O_PREFIX) {
				   
				   pulldownLocator = "css=div[id^='ext-namecontainer'] div[class='contact-form-add-field-button-text']:contains('Add Field')";
	   
	               optionLocator="css=div[id^='ext-listitem'] div[id^='ext-element']:contains('Prefix')";                
				  
				   //page = this;
			   } else if ( option == Button.O_MIDDLE) {
				   
				   pulldownLocator = "css=div[id^='ext-namecontainer'] div[class='contact-form-add-field-button-text']:contains('Add Field')";
				   
				   optionLocator="css=div[id^='ext-listitem'] div[id^='ext-element']:contains('Middle Name')"; 
				   
				   //page = this;		   
			   } else if ( option == Button.O_MAIDEN) {
				   
				   pulldownLocator = "css=div[id^='ext-namecontainer'] div[class='contact-form-add-field-button-text']:contains('Add Field')";
				   
				   optionLocator="css=div[id^='ext-listitem'] div[id^='ext-element']:contains('Maiden Name')"; 
				   
				   //page = this;		   
			   } else if ( option == Button.O_SUFFIX) {
				   
				   pulldownLocator = "css=div[id^='ext-namecontainer'] div[class='contact-form-add-field-button-text']:contains('Add Field')";
				   
				   optionLocator="css=div[id^='ext-listitem'] div[id^='ext-element']:contains('Suffix')"; 
				   
				   //page = null;		   
			   } else if ( option == Button.O_NICKNAME) {
				   
				   pulldownLocator = "css=div[id^='ext-namecontainer'] div[class='contact-form-add-field-button-text']:contains('Add Field')";
				   
				   optionLocator="css=div[id^='ext-listitem'] div[id^='ext-element']:contains('Nickname')"; 
				   
				   //page = this;		   
			   } else if ( option == Button.O_DEPARTMENT) {
				   
				   pulldownLocator = "css=div[id^='ext-companycontainer'] div[class='contact-form-add-field-button-text']:contains('Add Field')";
	               optionLocator="css=div[id^='ext-listitem'] div[id^='ext-element']:contains('Department')";                
				  
				   //page = this;		   
			   } else if ( option == Button.O_JOB_TITLE) {
				   
				   pulldownLocator = "css=div[id^='ext-companycontainer'] div[class='contact-form-add-field-button-text']:contains('Add Field')";
				   
	               optionLocator="css=div[id^='ext-listitem'] div[id^='ext-element']:contains('Job Title')";                
				  
				   //page = this;		   
			   }
			   
			   this.zClickAt(pulldownLocator, "0,0");
			   zWaitForBusyOverlay();
			   
			   this.sMouseOver(optionLocator);
			   this.zClick(optionLocator);
			   zWaitForBusyOverlay();
				
				return (page);
			   
		   } else if (pulldown == Button.B_PHONE_TYPE) {

			   pulldownLocator = "css=div[id^='ext-phonecontainer'] div[class='x-container x-layout-box-item x-stretched zcs-contact-form-multifield-field first'] div[id='ext-selectfield-1'] div[class='x-field-mask']";
			   this.zClickAt(pulldownLocator, "0,0");
			   zWaitForBusyOverlay();

			   if (option == Button.O_WORK) {

				   optionLocator = "css=div[class='x-list-item x-stretched x-list-item-tpl x-list-item-relative'] span[class='x-list-label']:contains('Work')";
				   page = null;
				
			   } else if (option == Button.O_MOBILE) {
				
				   optionLocator = "css=div[id='ext-panel-4'] div[id^='ext-simplelistitem'] span[class='x-list-label']:contains('Mobile')";
				   page = null;
				   
				
			   } else if (option == Button.O_HOME) {

				
				
			   } else if (option == Button.O_OTHER) {
			
				
			   } else {
				
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
				
			   }
			
			if ( !this.sIsVisible(optionLocator) ) {
				throw new HarnessException("menu is not visible: "+ optionLocator);
			}
			
			this.sMouseOver(optionLocator);
			this.zClick(optionLocator);
			zWaitForBusyOverlay();
			
			return (page);

		} else if (pulldown == Button.B_ADDRESS_TYPE) {

			   pulldownLocator = "css=div[id^='ext-addresscontainer'] div[id='ext-selectfield-2'] div[class='x-field-mask']";
			   this.zClickAt(pulldownLocator, "0,0");
			   zWaitForBusyOverlay();

			   if (option == Button.O_WORK) {

				   optionLocator = "css=div[class='x-list-item x-stretched x-list-item-tpl x-list-item-relative'] span[class='x-list-label']:contains('Work')";
				   page = null;
				
			   } else if (option == Button.O_MOBILE) {
				
				   optionLocator = "css=div[id^='ext-panel'] div[id^='ext-simplelistitem'] span[class='x-list-label']:contains('Mobile')";
				   page = null;
				
			   } else if (option == Button.O_HOME) {

				
				
			   } else if (option == Button.O_OTHER) {
				   optionLocator = "css=div[id^='ext-panel'] div[id^='ext-simplelistitem'] span[class='x-list-label']:contains('Other')";
				   page = null;
				
			   } else {
				
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
				
			   }
			
			   if ( !this.sIsVisible(optionLocator) ) {
				   throw new HarnessException("menu is not visible: "+ optionLocator);
			   }
			
			   this.sMouseOver(optionLocator);
			   this.zClick(optionLocator);
			   zWaitForBusyOverlay();
			
			   return (page);
		} else if (pulldown == Button.B_URL_TYPE) {

			   pulldownLocator = "css=div[id^='ext-urlcontainer'] div[id='ext-selectfield-3'] div[class='x-field-mask']";
			   this.zClickAt(pulldownLocator, "0,0");
			   zWaitForBusyOverlay();

			   if (option == Button.O_WORK) {

				   optionLocator = "css=div[id^='ext-panel'] div[id^='ext-simplelistitem'] span[class='x-list-label']:contains('Work')";
				   page = null;
				
			   } else if (option == Button.O_MOBILE) {
				
				   optionLocator = "css=div[id^='ext-panel'] div[id^='ext-simplelistitem'] span[class='x-list-label']:contains('Mobile')";
				   page = null;
				
			   } else if (option == Button.O_HOME) {

				
				
			   } else if (option == Button.O_OTHER) {
				   optionLocator = "css=div[id^='ext-panel'] div[id^='ext-simplelistitem'] span[class='x-list-label']:contains('Other')";
				   page = null;
				
			   } else {
				
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
				
			   }
			
			   if ( !this.sIsVisible(optionLocator) ) {
				   throw new HarnessException("menu is not visible: "+ optionLocator);
			   }
			
			   this.sMouseOver(optionLocator);
			   this.zClick(optionLocator);
			   zWaitForBusyOverlay();
			
			   return (page);
		} else {
			throw new HarnessException("no logic defined for pulldown/option "
					+ pulldown + "/" + option);
		}


	}

	/**
	 * Press the toolbar button.  For B_CLOSE and B_CANCEL, the
	 * test case must check for dialog active, based on the contact
	 * being dirty or not.
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		tracer.trace("Click button "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
		// Fallthrough objects
		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_SAVE ) {
			
 	    	locator = Locators.zSaveButton;
 	    	page = null;
			
		} else if ( button == Button.B_CANCEL ) {

 	    	locator = Locators.zCancelButton;
		    if (zIsElementDisabled(locator)) {
				throw new HarnessException("Tried clicking on "+ locator +" but it was disabled ");
		    }
		    
			page = new DialogWarning(DialogWarning.DialogWarningID.CancelCreateContact, this.MyApplication, ((AppTouchClient)this.MyApplication).zPageAddressbook);
			
			
			zClickAt(locator, "0,0");
			this.zWaitForBusyOverlay();
			
			return (page);

			
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		// Make sure a locator was set
		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		
		// Default behavior, process the locator by clicking on it
		//
		
		// Click it
		zClickAt(locator, "0,0");

		// if the app is busy, wait for it to become active again
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			
			// Make sure the page becomes active
			page.zWaitForActive();
			
		}
		
		// Return the page, if specified
		return (page);

	}

}
