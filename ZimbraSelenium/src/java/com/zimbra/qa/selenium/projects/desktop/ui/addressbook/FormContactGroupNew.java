package com.zimbra.qa.selenium.projects.desktop.ui.addressbook;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.desktop.ui.*;



public class FormContactGroupNew extends AbsForm {
	public static final String SELECT_OPTION_TEXT_GAL = "Global Address List";
	public static final String SELECT_OPTION_TEXT_CONTACTS = "Contacts";
	public static final String SELECT_OPTION_TEXT_SHARED_CONTACTS = "Personal and Shared Contacts";
	
	public static class Locators {
		
		public static final String zNewContactGroupMenuIconBtn = "css=id=^_left_icon div[class=ImgNewGroup]";
		
		public static final String zGroupnameField               = "css=input[id$='_groupName']";		
	    public static final String zGroupAddNewTextArea          = "css=textarea[id$='_addNewField']";
	    public static final String zAddNewButton                 = "css=td[id$='_addNewButton'] td[id$='_title']";        
	    public static final String zAddButton                    = "css=td[id$='_addButton'] td[id$='_title']";        
	    public static final String zAddAllButton                 = "css=td[id$='_addAllButton'] td[id$='_title']";
	    public static final String zPrevButton                   = "css=td[id$='_prevButton'] td[id$='_title']";
	    public static final String zNextButton                   = "css=td[id$='_nextButton'] td[id$='_title']";
	    
	    public static final String zFindField                    = "css=input[id$='_searchField']";
        public static final String zSearchButton                 = "css=td[id$='_searchButton'][id^='DWT'] td[id$='_title']";
        public static final String zSearchDropdown               = "css=td[id$='_listSelect'] td[id$='_select_container'] ";
        public static final String zFolderDropdown               = "css=td[id$='_folderSelect'] td[id$='_select_container'] td[id$='_dropdown'] div[class='ImgSelectPullDownArrow']";
        
        
        //TODO
	    public static final String zDropdownSelectContacts       = "css=DYNAMIC_ID";
	    public static final String zDropdownSelectSharedContacts = "css=DYNAMIC_ID";
	    public static final String zDropdownSelectGAL            = "css=DYNAMIC_ID";
	    
	    public static final String zListView                     = "css=div[id$='_listView'] div#z1__GRP__rows";
	    public static final String zEmailView                    = "css=div[id$='_groupMembers'] div#z1__GRP__rows";
	
	    public static final String zDeleteAllButton              = "css=td[id$='_delAllButton'] td[id$='_title']";
	    public static final String zDeleteButton                 = "css=td[id$='_delButton'] td[id$='_title']";
        
	    
	} 

	public static class Toolbar extends  AbsSeleniumObject{
		
		public static final String CANCEL="id=zb__GRP__CANCEL";
		public static final String SAVE="id=zb__GRP__SAVE_left_icon";

	}
		
	
	public FormContactGroupNew(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormContactGroupNew.class.getCanonicalName());

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
	
	
	public void save() throws HarnessException {
		logger.info("FormContactNew.save()");
		
		// Look for "Save"
		boolean visible = this.sIsElementPresent(Toolbar.SAVE);
		if ( !visible )
			throw new HarnessException("Save button is not visible "+ Toolbar.SAVE);
		
		// Click on it
		zClick(Toolbar.SAVE);
		
		zWaitForBusyOverlay();
		
	}

	// reset the form
	public void zReset() throws HarnessException {
		logger.info("FormMailGroupNew.zReset()");
		String[] fieldList = {Locators.zGroupAddNewTextArea, 
				              Locators.zGroupnameField };
		                  
		
		for (int i=0; i < fieldList.length; i++) {
		  sType(fieldList[i], "");
		  
		}
		
		zClick(Locators.zDeleteAllButton);
	}
	
	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("FormContactGroupNew.fill(IItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a ContactGroupItem
		if ( !(item instanceof ContactGroupItem) ) {
			throw new HarnessException("Invalid item type - must be ContactGroupItem");
		}
		
		// Convert object to ContactGroupItem
		ContactGroupItem group = (ContactGroupItem) item;
		
		// Fill out the form		
		if (( group.groupName != null )  && (group.groupName.trim().length() >0)){
			sFocus(Locators.zGroupnameField);
			zClick(Locators.zGroupnameField);		
			zType(Locators.zGroupnameField, group.groupName);
		}
		else {
			throw new HarnessException("Empty group name - group name is required");			
		}
		
		if ( group.getDList().length() > 0 ) {
							
			sFocus(Locators.zGroupAddNewTextArea);
			zClick(Locators.zGroupAddNewTextArea);
			zType(Locators.zGroupAddNewTextArea, group.getDList());

			//click Add button
		    zClick(Locators.zAddNewButton);
		    zWaitForBusyOverlay();
				
			}
		else {
			throw new HarnessException("Empty group members - group members are required");			
		}

					
	}

	//TODO verify the list of email with separator , included in the email view
	public boolean zIsContainedInEmailView(String list) throws HarnessException {		
		throw new HarnessException("IMplement me");		
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zGroupnameField;
		
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

	public void select(AppAjaxClient app, String dropdown, String option) throws HarnessException {
		
		String postfix = " td[id$='_dropdown'] div[class='ImgSelectPullDownArrow']";
		String textLocator = " td[id$='_title']";
		
		if (this.sGetText(dropdown + textLocator).equals(option)) {
			return;
		}
		
		//select contact dropdown 
		zClick(dropdown + postfix);
		SleepUtil.sleepSmall();		
		
		
		//assume contact is one arrow key down away from top
		//assume shared contact is two arrow key down away from top
		//assume GAL is three arrow key down away from top
		app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DOWN);
		if (!option.equals(SELECT_OPTION_TEXT_CONTACTS)) {
			app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DOWN);			
			if (!option.equals(SELECT_OPTION_TEXT_SHARED_CONTACTS)) {
				app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DOWN);			
			}	
		}
		
		app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_ENTER);		

		
		//formGroup.zClick(FormContactGroupNew.Locators.zDropdownSelectContacts);
		
		
		return ;
	}
}
