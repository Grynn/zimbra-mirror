package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.items.ContactGroupItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.FormContactGroupNew.Locators;


public class SimpleFormContactGroupNew extends AbsForm {
	
	public static final String WINDOW_DIALOGNAME = "css=div#CreateContactGroupDialog";
	public static final String INPUT_GROUPNAME = "css=input#CreateContactGroupDialog_name";
	public static final String BUTTON_SAVE     = "css=div#CreateContactGroupDialog_button2";
	public static final String BUTTON_CANCEL   = "css=div#CreateContactGroupDialog_button1";
	
	public SimpleFormContactGroupNew(AbsApplication application) {
		super(application);			
		logger.info("new " + SimpleFormContactGroupNew.class.getCanonicalName());
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	
	@Override
	public void zSubmit() throws HarnessException {
		logger.info("SimpleFormContactNew.zSubmit()");
		// Click on it
		zClick(BUTTON_SAVE);			
		zWaitForBusyOverlay();			
	}

	public void zCancel() throws HarnessException {
		logger.info("SimpleFormContactNew.zCancel()");
		// Click on it
		zClick(BUTTON_CANCEL);			
		zWaitForBusyOverlay();			
	}
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
						
		if ( !this.sIsElementPresent(WINDOW_DIALOGNAME) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(WINDOW_DIALOGNAME, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}
	

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info("SimpleFormContactGroupNew.fill(IItem)");
		logger.info(item.prettyPrint());
				
		
		// Make sure the item is a ContactGroupItem
		if ( !(item instanceof ContactGroupItem) ) {
			throw new HarnessException("Invalid item type - must be ContactGroupItem");
		}
		
		// Convert object to ContactGroupItem
		ContactGroupItem group = (ContactGroupItem) item;
		
		// Fill out the form		
		if (( group.groupName != null )  && (group.groupName.trim().length() >0)){
			sType(INPUT_GROUPNAME,group.groupName);
			
		}
		else {
			throw new HarnessException("Empty group name - group name is required");			
		}
				
					
	}
}
