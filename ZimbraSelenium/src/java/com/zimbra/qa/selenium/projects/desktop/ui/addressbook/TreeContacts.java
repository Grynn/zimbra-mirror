/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui.addressbook;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.desktop.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogTag;


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
			   String emailAddress = folder.isDesktopClientLocalFolder() ? 
	               ZimbraAccount.clientAccountName :
	                  MyApplication.zGetActiveAccount().EmailAddress;
			   String folderIdSuffix = folder.isDesktopClientLocalFolder() ? 
			         "_" + folder.getId() :
			            ":" + folder.getId();
			   locator = "css=td[id^='zti__" + emailAddress +
			         ":main_Contacts__'][id$='" + folderIdSuffix +"_textCell']";
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


	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem item) throws HarnessException {
		tracer.trace("Click "+ action +" then "+ option +" on addressbook "+ item);

		throw new HarnessException("implement me!");
	}


}
