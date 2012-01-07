package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew.Locators;



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

		if ( contact.company != null ) {			
			zFillField(getLocator(Locators.zCompanyEditField), contact.company);
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

		if (zIsVisiblePerPosition(Locators.zActiveEditForm, 0, 0) && 
		   (sGetEval("window.document.getElementById('" + Locators.zActiveEditForm + "').getAttribute('class')")).equals("ZmEditContactView"))		
		{
    		logger.info("id = " + Locators.zActiveEditForm + " already active");
    		return true;
    	}		    
		
		//set parameter zActiveEditForm				
		try {		
		    int length = Integer.parseInt(sGetEval("window.document.getElementById('z_shell').children.length"))-1;
			for (int i=length;i>=0; i--) {
		    	String className=sGetEval("window.document.getElementById('z_shell').children[" + i + "].getAttribute('class')" );		    	
		    	
		    	if (className.equals("ZmEditContactView")) {				    		 
		    		String id = sGetEval("window.document.getElementById('z_shell').children[" + i + "].id" );
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

}
