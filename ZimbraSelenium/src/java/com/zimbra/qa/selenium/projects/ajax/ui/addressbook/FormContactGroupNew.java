package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class FormContactGroupNew extends AbsForm {
	public static final String SELECT_OPTION_TEXT_GAL = "Global Address List";
	public static final String SELECT_OPTION_TEXT_CONTACTS = "Contacts";
	public static final String SELECT_OPTION_TEXT_SHARED_CONTACTS = "Personal and Shared Contacts";
	
	public static class Locators {
		
		public static final String zNewContactGroupMenuIconBtn = "css=id=^_left_icon div[class=ImgNewGroup]";
		public static String       zActiveEditForm = "";
			
		public static String zGroupnameField               = " div.companyName input[id$='_groupName']";		
	    public static String zGroupAddNewTextArea          = " textarea[id$='_addNewField']";
	    public static String zAddNewButton                 = " td[id$='_addNewButton'] td[id$='_title']";        
	    public static String zAddButton                    = " td[id$='_addButton'] td[id$='_title']";        
	    public static String zAddAllButton                 = " td[id$='_addAllButton'] td[id$='_title']";
	    public static String zPrevButton                   = " td[id$='_prevButton'] td[id$='_title']";
	    public static String zNextButton                   = " td[id$='_nextButton'] td[id$='_title']";
	    
	    public static String zFindField                    = " input[id$='_searchField']";
        public static String zSearchButton                 = " td[id$='_searchButton'][id^='DWT'] td[id$='_title']";
        public static String zSearchDropdown               = " td[id$='_listSelect'] td[id$='_select_container'] ";
        public static String zFolderDropdown               = ">table.contactHeaderTable td[id$='_title']";
        
		public static final String SAVE					   = "css=[id^=zb__CN][id$=__SAVE_left_icon]";
		public static final String CANCEL				   = "css=[id^=zb__CN][id$=__CANCEL]";

        
        //TODO
	    public static final String zDropdownSelectContacts       = "css=DYNAMIC_ID";
	    public static final String zDropdownSelectSharedContacts = "css=DYNAMIC_ID";
	    public static final String zDropdownSelectGAL            = "css=DYNAMIC_ID";
	    
	    public static String zListView                     = " div[id$='_listView'] div#z1__GRP__rows";
	    public static String zEmailView                    = " div[id$='_groupMembers'] div#z1__GRP__rows";
	
	    public static String zDeleteAllButton              = " td[id$='_delAllButton'] td[id$='_title']";
	    public static String zDeleteButton                 = " td[id$='_delButton'] td[id$='_title']";
        
	    
	} 


	public static class Field {
		
		public static final Field GroupName = new Field("GroupName");
		public static final Field SearchField = new Field("SearchField");
		public static final Field FreeFormAddress = new Field("FreeFormAddress");
		
		
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
		There are a lot of 'duplicate' divs involved with the Contact Group New/Edit forms
		We need to determine which div is active to make the code work correctly
	 */
	protected String MyDivID = null;
	
	
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
		this.zToolbarPressButton(Button.B_SAVE);
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
			
			locator = "css=div#" + getToolbarID() + " div[id$='__SAVE'] td[id$='_title']";
			page = null;

			// FALL THROUGH
		
		} else if ( button == Button.B_CLOSE ) {

			locator = "css=div#" + getToolbarID() + " div[id$='__CANCEL'] td[id$='_title']";
			page = new DialogWarning(DialogWarning.DialogWarningID.CancelCreateContact, this.MyApplication, ((AppAjaxClient)this.MyApplication).zPageAddressbook);

			// If there are pending changes, the dialog will appear.
			// If no pending changes, the dialog will not appear.
			// Click the button and blindly return the page (no checking for active)
			
			this.zClick(locator);
			
			return (page);
			
		} else if ( button == Button.B_DELETE ) {

			locator = "css=div#" + getToolbarID() + " div[id$='__DELETE'] td[id$='_title']";
			page = null;
		
			// FALL THROUGH
		
		} else if ( button == Button.B_CHOOSE_ADDRESSBOOK ) {

			// click the folder menu
			locator = "css=div#"+ MyDivID +" td[id$='_LOCATION_FOLDER'] td[id$='_title']";
			page = new DialogMove(MyApplication, ((AppAjaxClient) MyApplication).zPageAddressbook);
			

			// FALL THROUGH
			
		} else if ( button == Button.B_CONTACTGROUP_ADD_ADDRESS ) {

			// Add the 'free form' addresses
			
			locator = "css=div#"+ MyDivID +" td[id$='_addNewButton'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_CONTACTGROUP_ADD_SEARCH_RESULT ) {

			// Add the 'selected search result' address
			locator = "css=div#"+ MyDivID +" td[id$='_addButton'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_CONTACTGROUP_ADD_ALL_SEARCH_RESULT ) {

			// Add all the 'search result' addresses
			locator = "css=div#"+ MyDivID +" td[id$='_addAllButton'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_SEARCH ) {

			locator = "css=div#"+ MyDivID +" td[id$='_searchButton'] td[id$='_title']";
			page = null;
		
			// FALL THROUGH
		
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		// Make sure a locator was set
		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		
		// Default behavior, process the locator by clicking on it
		//
		
		// Click it
		this.zClickAt(locator,"");
		
		SleepUtil.sleepMedium();

		// if the app is busy, wait for it to become active again
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			
			// Make sure the page becomes active
			page.zWaitForActive();
			
		}
		
		// Return the page, if specified
		return (page);

	}

	/**
	 * Press the toolbar pulldown and the menu option
	 * @param pulldown
	 * @param option
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {

		logger.info(myPageName() + " zToolbarPressPulldown("+ pulldown +", "+ option +")");
		
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		if ( pulldown == null )
			throw new HarnessException("Pulldown cannot be null!");
		
		if ( option == null )
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		//AbsPage page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( pulldown == Button.B_CONTACTGROUP_SEARCH_TYPE ) {
			
			pulldownLocator = "css=div#"+ MyDivID +" td[id$='_select_container'] td[id$='_dropdown']>div";

			// Make sure the locator exists
			if ( !this.sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}
			
			this.zClick(pulldownLocator);

			this.zWaitForBusyOverlay();

			// See: https://bugzilla.zimbra.com/show_bug.cgi?id=77791
			optionLocator = "css=div#"+ this.getSearchPulldownID();
			

			if ( option == Button.O_CONTACTGROUP_SEARCH_GAL ) {
				
	            optionLocator = optionLocator + " td[id$='_title']:contains('Global Address List')"; // TODO: INTL
	            
	            // FALL THROUGH
	            
			} else if (option == Button.O_CONTACTGROUP_SEARCH_CONTACTS) {
				
	            optionLocator = optionLocator + " td[id$='_title']:contains('Contacts')"; // TODO: INTL
	            
	            // FALL THROUGH
	            
			} else if (option == Button.O_CONTACTGROUP_SEARCH_PERSONAL_AND_SHARED) {
				
	            optionLocator = optionLocator + " td[id$='_title']:contains('Personal and Shared Contacts')"; // TODO: INTL
	            
	            // FALL THROUGH
	            
			} else {
				throw new HarnessException("implement "+ pulldown +" and "+ option);
			}
			
			// Make sure the locator exists
			if ( !this.sIsElementPresent(optionLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
			}
			
			this.zClick(optionLocator);

			this.zWaitForBusyOverlay();

			return (null);

		}

		throw new HarnessException("no logic defined for pulldown "+ pulldown);

	}
	
	public static String getLocator(String locator) {   
		if (locator.startsWith("css=")) {
		    locator=locator.substring(locator.indexOf(" "));
		}
		
		return "css=div#" + Locators.zActiveEditForm + locator;
	}
	
	public void zFillField(Field field, String value) throws HarnessException {
		
		tracer.trace("Set "+ field +" to "+ value);

		String locator = "css=div#"+ MyDivID;
		
		if ( field == Field.GroupName ) {
			
			locator += " table.contactHeaderTable input[id$='_groupName']";

			// There seems to be an issue with changing the value during edit
			
			// workaround
			if(ZimbraSeleniumProperties.isWebDriver()){
			    clearField(locator);
			    sType(locator, value);
			}else{
				
				this.sFocus(locator);
				this.sClickAt(locator,"");
				Robot zRobot;
				try {
					zRobot = new Robot();
					zRobot.keyPress(KeyEvent.VK_CONTROL);
					zRobot.keyPress(KeyEvent.VK_A);
					zRobot.keyRelease(KeyEvent.VK_CONTROL);
					zRobot.keyRelease(KeyEvent.VK_A);				
				} catch (AWTException e) {
					e.printStackTrace();
				}
				this.zKeyboard.zTypeCharacters(value);
				this.zWaitForBusyOverlay();
			}
					
			return;
			
		
		} else if ( field == Field.SearchField ) {
			
			locator += "  table.ZPropertySheet input[id$='_searchField']";
			
			// FALL THROUGH

		} else if ( field == Field.FreeFormAddress ) {
			
			locator += " table.ZPropertySheet textarea[id$='_addNewField']";
			
			this.sType(locator, ""); // clear the contents
			this.sType(locator, value);
			this.zWaitForBusyOverlay();

			// Click Add
			this.zToolbarPressButton(Button.B_CONTACTGROUP_ADD_ADDRESS);
			this.zWaitForBusyOverlay();

			return;
			
			
		} else {
			throw new HarnessException("not implemented for field " + field);
		}
		
		// Default behavior, enter value into locator field
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Click at the field
		this.sClickAt(locator, "");
		
		// Type the value
		this.sType(locator, value);
		
		this.zWaitForBusyOverlay();


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
		
		// Fill out the name
		if ( (group.getName() == null) || (group.getName().trim().length() == 0) ) {
			throw new HarnessException("Empty group name - group name is required");			
		}
		zFillField(Field.GroupName, group.getName());			


		// Add addresses
		if ( group.getMemberList().size() == 0 ) {
			throw new HarnessException("Empty group members - group members are required");			
		}
		
		for (ContactGroupItem.MemberItem m : group.getMemberList()) {
			
			// Depending on the type, fill out the form appropriately.
			
			if ( m instanceof ContactGroupItem.MemberItemAddress ) {
				zFillField(Field.FreeFormAddress, m.getValue());
			} else if ( m instanceof ContactGroupItem.MemberItemContact ) {
				throw new HarnessException("implement me!");
			} else if ( m instanceof ContactGroupItem.MemberItemGAL ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("implement me!");			
			}
			
		}
		
					
	}
	
	/*
	 * check if the list group is empty
	 */
	public boolean zIsListGroupEmpty() throws HarnessException {
		return sIsElementPresent(getLocator(" div#[id$=_listView].groupMembers div[id^=zl__DWT][id$=__rows] td.NoResults"));			
	}

	/**
	 * Return the list of group members currently showing in the group member list
	 * @return
	 */
	public ArrayList<ContactItem> zListGetMembers() throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	
	/**
	 * Return the list of contacts/groups/GAL currently showing in the search results
	 * @return
	 */
	public ArrayList<ContactItem> zListGetSearchResults() throws HarnessException {
		logger.info("zListGetSearchResults()");
		
		
		String locator = null;
		ArrayList<ContactItem> items = new ArrayList<ContactItem>();

		int count = this.sGetCssCount("css=div#"+ MyDivID +" div[id$='_listView'] div[id$='__rows'] div[id^='zli__']");
		for ( int i = 1; i <= count; i++) {
			
			locator = "css=div#"+ MyDivID +" div[id$='_listView'] div[id$='__rows']>div:nth-child(" + i + ")";
			
			String name = this.sGetText(locator + " td:nth-child(2)");
			String email = this.sGetText(locator + " td:nth-child(3)");
			
			ContactItem item = new ContactItem();
			item.setAttribute("fileAs", name);
			item.setAttribute("email", email);
			
			logger.info(item.prettyPrint());
			items.add(item);
			
		}

		return items;
	}
	
	
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		if ( MyDivID == null ) {

			// Determine which ZmContactView div is visible (if any)
			String locator = "//div[@id='z_shell']/div[contains(@class, 'ZmContactView')]";
			int count = this.sGetXpathCount(locator);

			for (int i = 1; i <= count; i++) {
				String id = this.sGetAttribute(locator + "["+ i +"]@id");
				if ( this.zIsVisiblePerPosition("css=div#"+ id, 0, 0) ) {
					MyDivID = id;
					return (true);
				}
			}

			// No ZmContactView is active
			return (false);
		}

		// Div ID is set, check it.

		String locator = "css=div#" + MyDivID;

		boolean present = this.sIsElementPresent(locator);
		if ( !present ) {
			return (false);
		}

		boolean visible = this.zIsVisiblePerPosition(locator, 0, 0);
		if ( !visible ) {
			return (false);
		}

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

	private String MySearchPulldownID = null;
	/**
	 * Determine the z-shell <div/> that contains the Search Contacts, GAL, Personal and Shared
	 * menu.
	 * 
	 * See https://bugzilla.zimbra.com/show_bug.cgi?id=77791
	 * @return The z_shell Child ID
	 * @throws HarnessException 
	 */
	protected String getSearchPulldownID() throws HarnessException {
		logger.info("getSearchPulldownID()");
		
		if ( MySearchPulldownID != null ) {
			logger.info("getSearchPulldownID() - Re-using "+ MySearchPulldownID);
			return (MySearchPulldownID);
		}
		
		String locator = "//div[@id='z_shell']/div[contains(@class, 'DwtMenu')]";
		int count = this.sGetXpathCount(locator);
		
		for (int i = 1; i <= count; i++) {
			String id = this.sGetAttribute(locator + "["+ i +"]@id");
			if ( this.zIsVisiblePerPosition("css=div#"+ id, 0, 0) ) {
				MySearchPulldownID = id;
				return (id);
			}
		}

		throw new HarnessException("Unable to determine the Search Pulldown ID "+ this.sGetHtmlSource());
	}
}
