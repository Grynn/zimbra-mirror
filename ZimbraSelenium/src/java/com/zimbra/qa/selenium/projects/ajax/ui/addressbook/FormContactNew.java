package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;



/**
 * @author zimbra
 *
 */
public class FormContactNew extends AbsForm {
	
	public static class Locators {
		
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
		
	private String activeMenuId="";
	
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
	
	//click on a button such as Locations/File As
	public AbsPage zClick(Button button, AbsTab tab) throws HarnessException {
		logger.info("FormContactNew.zClick(" + button.toString() + ",...)");
		AbsPage page=null;
		String locator="";
		
		if (button == Button.B_MOVE) {
			page = new DialogMove(MyApplication, tab);
			locator=getLocator(Locators.zLocation);
		}
		//
		else if (button == Button.B_FILEAS) {			
			locator=getLocator(Locators.zFileAsDropdown);			
		}
		
		zClick(locator);
		zWaitForBusyOverlay();		
		
		findActiveMenuId();
		
		if (page != null) {
			page.zWaitForActive();
		}
		return page;
	}
	
	protected void save() throws HarnessException {
		logger.info("FormContactNew.save()");
				
		//TODO: implement code for IE
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
		public static final Field MiddleName = new Field("MiddleName");
		public static final Field LastName = new Field("LastName");
		public static final Field Email = new Field("Email");
		public static final Field JobTitle = new Field("JobTitle");
		public static final Field Company = new Field("Company");
		
		
		private String field;
		private Field(String name) {
			field = name;
		}
		
		@Override
		public String toString() {
			return (field);
		}

	}
	
	
	/**
	 * Expand all the hidden name fields
	 * @throws HarnessException
	 */
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
	
	private void findActiveMenuId() {
		try {		
		    for (int i=0; ; i++) {				    
		    	String id = sGetEval("window.document.getElementsByClassName('DwtMenu')[" + i + "].id" );
		    	if (zIsVisiblePerPosition(id, 0, 0)) {
		    		logger.info("active menu id = " + id);
		    		activeMenuId=id;
		    		break;
		    	}		    					    	
	        }	
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}		
	}
	
    public void selectFileAs(String fileAsOption) throws HarnessException {
    	if (sIsVisible("css=" + fileAsOption)) {
    		if (!zIsVisiblePerPosition(activeMenuId, 0, 0)) {	    			    				
    			findActiveMenuId();
    		}
    		zClick("css=div#" + activeMenuId + " " + fileAsOption);
    		zWaitForBusyOverlay();
    	}
    	else {
    		throw new HarnessException(fileAsOption + " is not visible" );
    	}
    }
	
    public String contactFullName(ContactItem contactItem, String fileAsOption) throws HarnessException{
    	String fullName=null;
    	
        //Last, First
    	if (fileAsOption == Locators.zFileAsLastCommaFirst) {
    		fullName = contactItem.lastName + ", " + contactItem.firstName;
    	} 
    	//First Last
    	else if (fileAsOption == Locators.zFileAsFirstLast) {
    		fullName = contactItem.firstName + " " + contactItem.lastName;
    	}     	     	
    	//Company
    	else if (fileAsOption == Locators.zFileAsCompany) {
    		fullName = contactItem.company;
    	}
    	//Last, First (Company)
    	else if (fileAsOption == Locators.zFileAsLastCommaFirstCompany) {
    		fullName = contactItem.lastName + ", " + contactItem.firstName + " ("
    		+ contactItem.company + ")";
    	}
    	//First Last (Company)
    	else if (fileAsOption == Locators.zFileAsFirstLastCompany) {
    		fullName = contactItem.firstName + " " + contactItem.lastName + " ("
    		+ contactItem.company + ")";
    	} 
       	//Company (Last, First)
    	else if (fileAsOption == Locators.zFileAsCompanyLastCommaFirst) {
    		fullName = contactItem.company + " (" 
    		         + contactItem.lastName + ", " + contactItem.firstName + ")";
    	} 
    	//Company (First Last)
    	else if (fileAsOption == Locators.zFileAsCompanyFirstLast) {
    		fullName = contactItem.company + " (" 
    		         + contactItem.firstName + " " + contactItem.lastName + ")";
    	}
    	else {
    		throw new HarnessException(fileAsOption + " not supported");
    	}
    	return fullName;
    }
    
    
	public String getDisplayedContactHeader() throws HarnessException {
		return sGetText(getLocator(Locators.zFullnameField));
	}
	
	
	public void zFillField(String locator, String value) throws HarnessException {
		tracer.trace("Set "+ locator +" to "+ value);
	
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ locator +" locator="+ value);
			
		if(ZimbraSeleniumProperties.isWebDriver()){
		    sType(locator,value);
		}else{
		    if (zIsBrowserMatch(BrowserMasks.BrowserMaskChrome)) { 
			sType(locator,value);
			sTypeKeys(locator,value);			
		    }
		    else {
		
			//The following code to simulate paste action from user (Ctrl-V) bug #
			//Use "Notes" to store text which will be entered into clipboard (Ctrl-X)	 
			sFocus(getLocator(Locators.zNotesEditField));
			sType(getLocator(Locators.zNotesEditField) ,value); //

			//highlight text
			String id= Locators.zActiveEditForm + "_NOTES_input";
			this.sGetEval(
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

	}
	
	public void zFillField(Field field, String value) throws HarnessException {
		tracer.trace("Set "+ field +" to "+ value);

		
		
		
		String locator = null;
		
		if ( field == Field.FirstName ) {

			locator = FormContactNew.getLocator(Locators.zFirstEditField);
			
		} else if ( field == Field.MiddleName ) {
			
			locator = FormContactNew.getLocator(Locators.zMiddleEditField);

		} else if ( field == Field.LastName ) {
			
			locator = FormContactNew.getLocator(Locators.zLastEditField);

		} else if ( field == Field.Email ) {
			
			
			locator = FormContactNew.getLocator(Locators.zEmail1EditField);

			// Make sure the button exists
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
			
			// Email is a bit different, since there is an auto-complete action
			// 
			// For auto-complete, some extra events must occur
			//
			this.sFocus(locator);
			this.zClick(locator);
			this.zWaitForBusyOverlay();

			// Enter text
			this.sType(locator, value);
			this.sFireEvent(locator, "keyup");
			
			// Wait for any busy overlay
			this.zWaitForBusyOverlay();
			
			return;

		} else if ( field == Field.JobTitle ) {
			
			locator = FormContactNew.getLocator(Locators.zJobTitleEditField);

		} else if ( field == Field.Company ) {
			
			locator = FormContactNew.getLocator(Locators.zCompanyEditField);

		} else {
			throw new HarnessException("not implemented for field " + field);
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
		this.sType(locator, value);
		
		// For some reason, contact fields need a keyup event to
		// determine that text has been added.
		//
		this.sFireEvent(locator, "keyup");
		
		// Wait for any busy overlay
		this.zWaitForBusyOverlay();

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
		
		if ( contact.middleName != null ) {			
			zFillField(Field.MiddleName, contact.middleName);
		}
		
		if ( contact.email != null ) {			
			zFillField(Field.Email, contact.email);
		}

		if ( contact.company != null ) {			
			zFillField(Field.Company, contact.company);
		}

		if ( contact.jobTitle != null ) {			
			zFillField(Field.JobTitle, contact.jobTitle);
		}

		if (contact.ContactAttributes.size() >0) {
			for ( String key:contact.ContactAttributes.keySet()) {
				zFillField(getLocator(key), contact.ContactAttributes.get(key));
			}
		}
		
	}
	
	public static String getLocator(String locator) {
         
		return locator.replaceAll("EDITCONTACTFORM", Locators.zActiveEditForm);
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
		String script = "window.document.getElementById('" + Locators.zActiveEditForm + "').getAttribute('class')";
		if(ZimbraSeleniumProperties.isWebDriver()){
		    script = "return " + script;
		}
		if (zIsVisiblePerPosition(Locators.zActiveEditForm, 0, 0) && 
		   (sGetEval(script)).equals("ZmEditContactView"))		
		{
    		logger.info("id = " + Locators.zActiveEditForm + " already active");
    		return true;
    	}		    
		
		//set parameter zActiveEditForm				
		try {		
		    int length = 0;
		    script = "window.document.getElementById('z_shell').children.length";
		    if(ZimbraSeleniumProperties.isWebDriver()){
			script = "return " + script;
		    }
		    
		    length = Integer.parseInt(sGetEval(script))-1;
		    
			for (int i=length;i>=0; i--) {
			    script = "window.document.getElementById('z_shell').children[" + i + "].getAttribute('class')" ;
			    if(ZimbraSeleniumProperties.isWebDriver()){
				     script = "return " + script;
				 }
		    	String className=sGetEval(script);		    	
		    	
		    	if (className.equals("ZmEditContactView")) {	
		    	    script = "window.document.getElementById('z_shell').children[" + i + "].id";
			    if(ZimbraSeleniumProperties.isWebDriver()){
				 script = "return " + script;
			     }

		    		String id = sGetEval(script);
		    		if (zIsVisiblePerPosition(id, 0, 0)) {
		    			Locators.zActiveEditForm = id;
		    			logger.info("active id = " + id);
		    			return true;
		    		}
		    	}
	        }	
		}
		catch (Exception e) {
			logger.info(e.getMessage());
		}
		
		return false;					
	}

	/**
	 * Press the toolbar button
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
			
			// TODO
			
		} else if ( button == Button.B_CANCEL ) {

 	    	//String id ="dizb__CN__CANCEL";
 	    	locator = "css=div[id^=zb__CN][id$=__CANCEL]" ;
		    if (zIsElementDisabled(locator)) {
				throw new HarnessException("Tried clicking on "+ locator +" but it was disabled ");
		    }
		    
			page = new DialogWarning(DialogWarning.DialogWarningID.CancelCreateContact, this.MyApplication, ((AppAjaxClient)this.MyApplication).zPageAddressbook);
			
		} else if ( button == Button.B_PRINT ) {
			
			// TODO
			
		} else if ( button == Button.B_DELETE ) {
			
			// TODO
			
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
