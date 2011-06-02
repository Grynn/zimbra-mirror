/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.event.KeyEvent;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


/**
 * @author zimbra
 *
 */
public class TreeContacts extends AbsTree {
    public static final String NEW_FOLDER="css=#ztih__main_Contacts__ADDRBOOK_table tbody tr td:nth-child(4)";
    public static final String COLLAPSE_TREE="css#ztih__main_Contacts__ADDRBOOK_nodeCell";
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
		tracer.trace("Click "+ action +" on addressbook "+ addressbook);

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
			
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			   locator = "css=td[id^='zti__" +
			         MyApplication.zGetActiveAccount().EmailAddress +
			         ":main_Contacts__'][id$=':" + folder.getId() +"_textCell']";
			} else {
			   locator = "id=zti__main_Contacts__"+ folder.getId() +"_textCell";
			}

			if ( !GeneralUtility.waitForElementPresent(this, locator) ) {
				throw new HarnessException("Unable to locator folder in tree "+ locator);
			}

			this.zClick(locator);
			SleepUtil.sleepSmall();
			page = null;
		}  
		else if ( action == Action.A_RIGHTCLICK ) {
				
			locator = "id=zti__main_Contacts__"+ folder.getId() +"_textCell";
				
			if ( !this.sIsElementPresent(locator) ) {
					throw new HarnessException("Unable to locator folder in tree "+ locator);
			}
			
			this.zClick(locator);			
			zKeyboard.zTypeCharacters(Shortcut.S_RIGHTCLICK.getKeys());															
			 
			//TODO
			//return a list of context menu's options
			SleepUtil.sleepSmall();
			page = null;
				
			
		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}

		return (page);
	}
	
	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem folder) throws HarnessException {

		tracer.trace("Click "+ action +" then "+ option +" on folder "+ folder.getName());

		// Validate the arguments
		if ( (action == null) || (option == null) || (folder == null) ) {
			throw new HarnessException("Must define an action, option, and addressbook");
		}

		if ( folder instanceof FolderItem ) {
			return (zTreeItem(action, option, (FolderItem)folder));
		} else if ( folder instanceof SavedSearchFolderItem ) {
			return (zTreeItem(action, option, (SavedSearchFolderItem)folder));
		//} else if ( folder instanceof ZimletItem ) {
		//	return (zTreeItem(action, option, (ZimletItem)folder));
		}else if ( folder instanceof TagItem ) {
			return (zTreeItem(action, option, (TagItem)folder));
		}

		throw new HarnessException("Must use TagItem FolderItem or SavedSearchFolderItem or ZimletItem as argument, but was "+ folder.getClass());
	}
	
	protected AbsPage zTreeItem(Action action, Button option, TagItem folder)
	throws HarnessException {

		if ((action == null) || (option == null) || (folder == null)) {
			throw new HarnessException(
			"Must define an action, option, and addressbook");
		}
	
		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = null;

		TagItem t = (TagItem) folder;
		tracer.trace("processing " + t.getName());

		if (action == Action.A_LEFTCLICK) {

			throw new HarnessException("Action Left click not yet implemented");

		 } else if (action == Action.A_RIGHTCLICK) {
			actionLocator = "css=div#zti__main_Contacts__" + t.getId() + "_div";				
			zRightClickAt(actionLocator,"0,0");		
			SleepUtil.sleepMedium();
         } else {
			throw new HarnessException("Action " + action
					+ " not yet implemented");
		 }
		
		if (option == Button.B_TREE_NEWTAG) {
			optionLocator = "css=tr#POPUP_NEW_TAG";
			page = new DialogTag(MyApplication,
					((AppAjaxClient) MyApplication).zPageAddressbook);
		    zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
		} 
		else if (option == Button.B_DELETE) {
			optionLocator = "css=tr#POPUP_DELETE";			
			page = new DialogWarning(
					DialogWarning.DialogWarningID.DeleteTagWarningMessage,
					MyApplication, ((AppAjaxClient) MyApplication).zPageAddressbook);        
			zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
			zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
			zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);		
		}
	    else if (option == Button.B_RENAME) {
	    	optionLocator= "css=tr#POPUP_RENAME_TAG";
			page = new DialogRenameTag(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);
			zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
			zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);

		} 		
		else {
			throw new HarnessException("button " + option
					+ " not yet implemented");
		}
		
		zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);						
		
			// If there is a busy overlay, wait for that to finish
		zWaitForBusyOverlay();

		if (page != null) {
			// Wait for the page to become active, if it was specified
			page.zWaitForActive();
		}

		return (page);

	}
	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		tracer.trace("Click button "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null");
			
		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_TREE_NEWADDRESSBOOK ) {
			
			locator = null;
			page = null;
			
			// TODO: implement me
			
			// FALL THROUGH

		} else if ( button == Button.B_TREE_NEWTAG ) { 			
			locator = zNewTagIcon;

			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);
				
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}
		
		// Default behavior, process the locator by clicking on it
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
		
		// Click it
		this.zClick(locator);
		
		// If the app is busy, wait for that to finish
		this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if ( page != null ) {
			
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
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
