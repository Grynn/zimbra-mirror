package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

import java.util.*;


public class FormContactGroupNew extends AbsForm {
	public static final String SELECT_OPTION_TEXT_GAL = "Global Address List";
	public static final String SELECT_OPTION_TEXT_CONTACTS = "Contacts";
	public static final String SELECT_OPTION_TEXT_SHARED_CONTACTS = "Personal and Shared Contacts";
	
	public static class Locators {
		
		public static final String zNewContactGroupMenuIconBtn = "css=id=^_left_icon div[class=ImgNewGroup]";
		public static String       zActiveEditForm = "";
			
		public static String zGroupnameField               = " div.companyName>input[id$='_groupName']";		
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
        
        
        //TODO
	    public static final String zDropdownSelectContacts       = "css=DYNAMIC_ID";
	    public static final String zDropdownSelectSharedContacts = "css=DYNAMIC_ID";
	    public static final String zDropdownSelectGAL            = "css=DYNAMIC_ID";
	    
	    public static String zListView                     = " div[id$='_listView'] div#z1__GRP__rows";
	    public static String zEmailView                    = " div[id$='_groupMembers'] div#z1__GRP__rows";
	
	    public static String zDeleteAllButton              = " td[id$='_delAllButton'] td[id$='_title']";
	    public static String zDeleteButton                 = " td[id$='_delButton'] td[id$='_title']";
        
	    
	} 

	public static class Toolbar extends  AbsSeleniumObject{
		
		public static String CANCEL="css=[id^=zb__CN][id$=__CANCEL]";
		public static String SAVE="css=[id^=zb__CN][id$=__SAVE_left_icon]";

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
		logger.info("FormMailGroupNew.zReset()");
//		String[] fieldList = {getLocator(Locators.zGroupAddNewTextArea), 
//				              getLocator(Locators.zGroupnameField) };
		                  
		String[] fieldList = {getLocator(Locators.zGroupAddNewTextArea.replaceFirst("=_","=" +Locators.zActiveEditForm + "_")), 
	              getLocator(Locators.zGroupnameField.replaceFirst("=_","=" +Locators.zActiveEditForm + "_")) };
		
		for (int i=0; i < fieldList.length; i++) {
		  sType(fieldList[i], "");
		  
		}
		//TODO: remove existing contacts members
		//zl__GRP__rows
		
	}
	
	public static String getLocator(String locator) {   
		if (locator.startsWith("css=")) {
		    locator=locator.substring(locator.indexOf(" "));
		}
		
		return "css=div#" + Locators.zActiveEditForm + locator;
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
			sType(getLocator(Locators.zGroupnameField),group.groupName);
			
		}
		else {
			throw new HarnessException("Empty group name - group name is required");			
		}
		
		if ( group.getDList().length() > 0 ) {
							
			sType(getLocator(Locators.zGroupAddNewTextArea),group.getDList());
	
			//click Add button
		    zClick(getLocator(Locators.zAddNewButton));
		    zWaitForBusyOverlay();
				
			}
		else {
			throw new HarnessException("Empty group members - group members are required");			
		}

					
	}
	
	/*
	 * check if the list group is empty
	 */
	public boolean zIsListGroupEmpty() throws HarnessException {
		return sIsElementPresent(getLocator(" div#[id$=_listView].groupMembers div#zl__GRP__rows>div>table>tbody>tr>td.NoResults"));			
	}

	/* return an array list of contact items displayed in the group list view
	 * 
	 */
	public ArrayList<ContactItem> zListGroupRows() {
		ArrayList<ContactItem> ciArray = new ArrayList<ContactItem>();
		ContactItem ci=null;
		
	    try {
	      int count=1;
	      
	      while (true) {
	    	  String cssCommon= getLocator(" div#zl__GRP__rows>div:nth-child(" + count + ")>table>tbody>tr>");
		      String cssName = cssCommon + "td:nth-child(2)"; 	
		      String cssEmail= cssCommon + "td:nth-child(3)";
		      
	    	  ci= new ContactItem(sGetText(cssName));
	    	  ci.setAttribute("email", sGetText(cssEmail));
	    	  
	    	  ciArray.add(ci);
	    	  count++;
	      }
	    	
	    } 
	    catch (Exception e) {
	       logger.info("reach the end of the node list");    	
	    }
	
		return ciArray;
	}
	
	
	//TODO verify the list of email with separator , included in the email view
	public boolean zIsContainedInEmailView(String list) throws HarnessException {		
		throw new HarnessException("IMplement me");		
	}
	private void replaceLocators() {
		Locators.zGroupnameField               = getLocator(Locators.zGroupnameField);
		Locators.zGroupAddNewTextArea          = getLocator(Locators.zGroupAddNewTextArea);
		Locators.zAddNewButton                 = getLocator(Locators.zAddNewButton);        
		Locators.zAddButton                    = getLocator(Locators.zAddButton);        
		Locators.zAddAllButton                 = getLocator(Locators.zAddAllButton);
		Locators.zPrevButton                   = getLocator(Locators.zPrevButton);
		Locators.zNextButton                   = getLocator(Locators.zNextButton);
	    
		Locators.zFindField                    = getLocator(Locators.zFindField);
		Locators.zSearchButton                 = getLocator(Locators.zSearchButton);
		Locators.zSearchDropdown               = getLocator(Locators.zSearchDropdown);
		Locators.zFolderDropdown               = getLocator(Locators.zFolderDropdown);
        	   
		Locators.zListView                     = getLocator(Locators.zListView);
		Locators.zEmailView                    = getLocator(Locators.zEmailView);
	
		Locators.zDeleteAllButton              = getLocator(Locators.zDeleteAllButton);
		Locators.zDeleteButton                 = getLocator(Locators.zDeleteButton);
        
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
			
	  	if ((zIsVisiblePerPosition(Locators.zActiveEditForm, 0, 0)) && 
	  	    (sGetEval("window.document.getElementById('" + Locators.zActiveEditForm + "').getAttribute('class')")).equals("ZmContactView"))
	  	{
    		logger.info("id = " + Locators.zActiveEditForm + " already active");
    		return true;
    	}	
	  	
		//set parameter zActiveEditForm		
		
		try {		
		    int length = Integer.parseInt(sGetEval("window.document.getElementById('z_shell').children.length"))-1;
			for (int i=length;i>=0; i--) {
		    	String className=sGetEval("window.document.getElementById('z_shell').children[" + i + "].getAttribute('class')" );		    	
		    	
		    	if (className.equals("ZmContactView")) {
		    		String id = sGetEval("window.document.getElementById('z_shell').children[" + i + "].id" );			    	
		    		if (zIsVisiblePerPosition(id, 0, 0)) {		    	
		    			Locators.zActiveEditForm = id;
		    			logger.info("found active id = " + id);
		    			replaceLocators();
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
	
	public DialogMove clickFolder() throws HarnessException {
		//click the location
		zClick(Locators.zFolderDropdown);
		zWaitForBusyOverlay();
		
		DialogMove dialog = new DialogMove(MyApplication, ((AppAjaxClient) MyApplication).zPageAddressbook);				
		
		if ( dialog != null ) {
			dialog.zWaitForActive();
		}
		return (dialog);
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
