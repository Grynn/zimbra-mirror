package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;




public class FormContactGroupNew extends AbsForm {
	
	public static class Locators {
		
		public static final String zNewContactGroupMenuIconBtn = "css=id=^_left_icon div[class=ImgNewGroup]";
		
		public static final String zGroupnameField = "css=input[id$='_groupName']";		
	    public static final String zGroupMembers   = "css=textarea[id$='_addNewField']";
	    public static final String zAddButton      = "css=td[id$='_addNewButton'] td[id$='_title']";
         
        // more goes here
	    	    
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
		logger.info("FormMailNew.zReset()");
		String[] fieldList = {Locators.zGroupMembers, 
				              Locators.zGroupnameField };
		                      //TODO: ,Locators.zEmail1EditField};
		
		for (int i=0; i < fieldList.length; i++) {
		  this.sType(fieldList[i], "");
		  
		}
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
			zKeyboard.zTypeCharacters(group.groupName);
		}
		else {
			throw new HarnessException("Empty group name - group name is required");			
		}
		
		if ( group.getDList().length() > 0 ) {
							
			sFocus(Locators.zGroupMembers);
			zClick(Locators.zGroupMembers);		        
		    zKeyboard.zTypeCharacters(group.getDList());
			
	
			//click Add button
		    zClick(Locators.zAddButton);
		    zWaitForBusyOverlay();
				
			}
		else {
			throw new HarnessException("Empty group members - group members are required");			
		}

					
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
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}

}
