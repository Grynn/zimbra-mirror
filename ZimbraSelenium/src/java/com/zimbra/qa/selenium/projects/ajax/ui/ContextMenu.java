/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

/*
 * Right-click menu
 * 
 */

public class ContextMenu extends AbsDisplay {

	public static final String zContacts		= "id='zm__Contacts'";
	
	
	public ContextMenu(AbsApplication application) {
		super(application);
	}
	
	

	public void  zSelect(ContextMenuItem cmi) throws HarnessException {
		logger.info(myPageName() + " zSelect("+ cmi.text +")");
				
		this.zClick(cmi.locator);
        SleepUtil.sleepSmall();		
	}

	
	public List<ContextMenuItem> zListGetContextMenuItems(String typeLocator) throws HarnessException {

		List <ContextMenuItem> list= new ArrayList<ContextMenuItem>();
		
		//TODO: check for visible		
		if ( !this.sIsElementPresent("xpath=//div[@" +typeLocator + "]"))
			throw new HarnessException("Context Menu List is not present(visible) "+ "//div[@id='zm__Contacts']");

		//Get the number of context menu item including separator  
		int count = this.sGetXpathCount("//div[@" + typeLocator + "]/table/tbody/tr");
		
		logger.debug(myPageName() + " zListGetContextMenuItems: number of context menu item including separators: "+ count);
		System.out.println(myPageName() + " zListGetContextMenuItems: number of context menu item including separators: "+ count);

		// Get each context item data's data from the table list
		for (int i = 1; i <= count; i++) {
			//get id attribute
			String id = sGetAttribute("xpath=(//div[@" + typeLocator + "]/table/tbody/tr["+ i +"]/td/div)@id");
						
			ContextMenuItem ci  = ContextMenuItem.getContextMenuItem(id);		    			
			
			list.add(ci);	    	      
		}


		return list;		
	}
	
	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		//TODO return true for now		
		return true; 
		//return ( this.sIsElementPresent(Locators.zTagDialogId) );
	}



}
