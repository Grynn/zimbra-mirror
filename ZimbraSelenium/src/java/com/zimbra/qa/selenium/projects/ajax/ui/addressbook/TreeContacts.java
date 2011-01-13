/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTree;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;


/**
 * @author zimbra
 *
 */
public class TreeContacts extends AbsTree {

	public static class Locators {
	}
	
		
	
	public TreeContacts(AbsApplication application) {
		super(application);
		logger.info("new " + TreeContacts.class.getCanonicalName());
	}
	
	/* (non-Javadoc)
	 * @see framework.ui.AbsTree#zTreeItem(framework.ui.Action, framework.items.FolderItem)
	 */
	public AbsPage zTreeItem(Action action, IItem addressbook) throws HarnessException {
		
		// Validate the arguments
		if ( (action == null) || (addressbook == null) ) {
			throw new HarnessException("Must define an action and addressbook");
		}
		
		if ( !(addressbook instanceof FolderItem) ) {
			throw new HarnessException("Must use FolderItem as argument, but was "+ addressbook.getClass());
		}
		
		FolderItem folder = (FolderItem)addressbook;
		
		AbsPage page = null;
		String locator = null;
		
		if ( action == Action.A_LEFTCLICK ) {
			
			locator = "id=zti__main_Contacts__"+ folder.getId() +"_textCell";
			
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Unable to locator folder in tree "+ locator);
			}

			this.zClick(locator);
			SleepUtil.sleepSmall();
			page = null;
			
			// FALL THROUGH

		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}

		return (page);
	}
	


	/* (non-Javadoc)
	 * @see framework.ui.AbsTree#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
