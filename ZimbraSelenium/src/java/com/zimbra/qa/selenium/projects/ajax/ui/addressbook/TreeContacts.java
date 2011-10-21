/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.Robot;
import java.awt.event.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;





/**
 * @author zimbra
 *
 */
public class TreeContacts extends AbsTree {
    public static final String NEW_FOLDER="css=#ztih__main_Contacts__ADDRBOOK_table tbody tr td:nth-child(4)";
    public static final String COLLAPSE_TREE="css#ztih__main_Contacts__ADDRBOOK_nodeCell";
	public static class Locators {
		public static final String EXPAND_NODE  = "ImgNodeExpanded";
		public static final String COLLAPSE_NODE= "ImgNodeCollapsed";
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
			
			locator = "id=zti__main_Contacts__"+ folder.getId() +"_textCell";
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Unable to locator folder in tree "+ locator);
		    }
		
			zClick(locator);
			zWaitForBusyOverlay();									
			page = null;
		}  
		else if ( action == Action.A_RIGHTCLICK ) {
				
			locator = "id=zti__main_Contacts__"+ folder.getId() +"_textCell";				
			if (!this.sIsElementPresent(locator) ) {
					throw new HarnessException("Unable to locator folder in tree "+ locator);
			}
			
			zClick(locator);			
			zKeyboard.zTypeCharacters(Shortcut.S_RIGHTCLICK.getKeys());			
			zWaitForBusyOverlay();
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

		if ( folder instanceof FolderMountpointItem ) {
			return (zTreeItem(action, option, (FolderMountpointItem)folder));
		} else if ( folder instanceof SavedSearchFolderItem ) {
			return (zTreeItem(action, option, (SavedSearchFolderItem)folder));
		//} else if ( folder instanceof ZimletItem ) {
		//	return (zTreeItem(action, option, (ZimletItem)folder));
		}else if ( folder instanceof TagItem ) {
			return (zTreeItem(action, option, (TagItem)folder));
		} else if ( folder instanceof FolderItem ) { // FolderItem needs to go last
			return (zTreeItem(action, option, (FolderItem)folder));
		}

		throw new HarnessException("Must use TagItem FolderItem or SavedSearchFolderItem or ZimletItem as argument, but was "+ folder.getClass());
	}

	protected AbsPage zTreeItem(Action action, Button option, FolderMountpointItem folderItem) 
	throws HarnessException 
	{
		logger.info(myPageName() + " zTreeItem("+ action +", "+ option + "," + folderItem.getName() +")");
		tracer.trace(action +" then "+ option +" on Folder Item = "+ folderItem.getName());

		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = null;

		if ((action == null) || (option == null) || (folderItem == null)) {
			throw new HarnessException("Must define an action, option, and addressbook");
		}
		
		

	
		if (folderItem.getName().equals("USER_ROOT")) {
			actionLocator = "css=div#ztih__main_Contacts__ADDRBOOK_div";
		} else {
			actionLocator = "css=div#zti__main_Contacts__" + folderItem.getId() +"_div";			
		}
		
		
		if ( action == Action.A_RIGHTCLICK ) {
			
			
			optionLocator = "css=div[id='ZmActionMenu_contacts_ADDRBOOK']";
			
			if (option == Button.B_TREE_NEWFOLDER) {
				
				optionLocator += " div[id='NEW_ADDRBOOK'] td[id$='_title']";
				page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageAddressbook);			    

			}			
			else if (option == Button.B_DELETE) {
				
				optionLocator += " div[id='DELETE_WITHOUT_SHORTCUT'] td[id$='_title']";
				page = null;
				
		    } 
			else if (option == Button.B_RENAME) {
				
				optionLocator += " div[id='RENAME_FOLDER'] td[id$='_title']";
			    page = new DialogRenameFolder(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);

			}   			
			else if (option == Button.B_TREE_EDIT) {

				optionLocator += " div[id='EDIT_PROPS'] td[id$='_title']";
			    page = new DialogEditFolder(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);

			} 
			else if (option == Button.B_TREE_FOLDER_EMPTY) {
				
				optionLocator += " div[id='EMPTY_FOLDER'] td[id$='_title']";
				page = new DialogWarning(DialogWarning.DialogWarningID.EmptyFolderWarningMessage,
						MyApplication, ((AppAjaxClient) MyApplication).zPageAddressbook);

			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}
			
			// Default right-click behavior
			zRightClickAt(actionLocator,"0,0");
			zWaitForBusyOverlay();

			zClickAt(optionLocator, "0,0");
			zWaitForBusyOverlay();

			if ( page != null ) {
				page.zWaitForActive();
			}
			
			return (page);
			
		} else {
			throw new HarnessException("implement action:"+ action +" option:"+ option);
		}


	}
	
	protected AbsPage zTreeItem(Action action, Button option, FolderItem folderItem)
	throws HarnessException {

		AbsPage page = null;
		//String actionLocator = null;
		String optionLocator = null;

		if ((action == null) || (option == null) || (folderItem == null)) {
			throw new HarnessException(
			"Must define an action, option, and addressbook");
		}
		logger.info(myPageName() + " zTreeItem("+ action +", "+ option + "," + folderItem.getName() +")");
		tracer.trace(action +" then "+ option +" on Folder Item = "+ folderItem.getName());

		String treeItemLocator = null;
	
		if (folderItem.getName().equals("USER_ROOT")) {
			treeItemLocator = "css=div#ztih__main_Contacts__ADDRBOOK_div";
		} else {
			treeItemLocator = "css=div#zti__main_Contacts__" + folderItem.getId() +"_div";			
		}
		
		
		if ( action == Action.A_RIGHTCLICK ) {
			zClick(treeItemLocator);			
			zRightClickAt(treeItemLocator,"0,0");
			zWaitForBusyOverlay();
			
			if (option == Button.B_TREE_NEWFOLDER) {
				optionLocator="css=tr#POPUP_NEW_ADDRBOOK";
				
				if (zIsElementDisabled(optionLocator + ">td.ZLeftIcon>div.ImgNewContactsFolder")) {			    		
					return null;
				}			
				if (!this.zIsBrowserMatch(BrowserMasks.BrowserMaskChrome)) {
				  zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				  zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);
				}
				else {
				   try {
					   Robot robot=new Robot();					   
					   String itemId="zov__main_Contacts" ;
				       robot.mouseMove(sGetElementPositionLeft("id=" + itemId) + sGetElementWidth(optionLocator)/2,							            
							           sGetElementPositionTop("id=zb__App__Contacts") + sGetElementHeight("id=zb__App__Contacts") +							           
							           sGetElementPositionTop("id=" + itemId)  + sGetElementHeight(optionLocator)/2							                  
							           );
				       robot.mousePress(InputEvent.BUTTON1_MASK);
                       robot.mouseRelease(InputEvent.BUTTON1_MASK);
				   }
				   catch (Exception e) {logger.info(e.getMessage());}				
				}
				zWaitForBusyOverlay();
				page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageAddressbook);			    
			}			
			else if (option == Button.B_DELETE) {												
				//if option is disabled
				
				optionLocator="tr#POPUP_DELETE_WITHOUT_SHORTCUT";
				if (zIsElementDisabled(optionLocator + ">td[id$=_left_icon]>div")) {
					return null;
				}
				
				//zClickAt("css=" + optionLocator,"0,0");				
			
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);				
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_ENTER);	
				
				zWaitForBusyOverlay();
				
				page= ((AppAjaxClient)MyApplication).zPageAddressbook;						
		    } 
			else if (option == Button.B_RENAME) {
				optionLocator="tr#POPUP_RENAME_FOLDER";
				if (zIsElementDisabled(optionLocator + " div")) {
					return null;
				}
				
				zClickAt("css=" + optionLocator,"0,0");				
				zWaitForBusyOverlay();				
			    page = new DialogRenameFolder(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);

			}   			
			else if (option == Button.B_TREE_EDIT) {
				optionLocator="tr#POPUP_EDIT_PROPS";
				if (zIsElementDisabled(optionLocator + " div")) {
					return null;
				}
				
				zClickAt("css=" + optionLocator,"0,0");				
				zWaitForBusyOverlay();				
			    page = new DialogEditFolder(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);

			} 
			else if (option == Button.B_TREE_FOLDER_EMPTY) {
				optionLocator="tr#POPUP_EMPTY_FOLDER";
				if (zIsElementDisabled(optionLocator + " div")) {
					return null;
				}
				
				zClickAt("css=" + optionLocator,"0,0");				
				zWaitForBusyOverlay();						
				page = new DialogWarning(DialogWarning.DialogWarningID.EmptyFolderWarningMessage,
						MyApplication, ((AppAjaxClient) MyApplication).zPageAddressbook);

			}
			else if (option == Button.B_SHARE) {
				
				optionLocator = "css=div[id='ZmActionMenu_contacts_ADDRBOOK'] div[id='SHARE_ADDRBOOK'] td[id$='_title']";
				page = new DialogShare(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);
				
				zClickAt(optionLocator,"0,0");				
				zWaitForBusyOverlay();						

			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}
		} else if (action == Action.A_LEFTCLICK) {
			if (option == Button.B_TREE_NEWFOLDER) {
				
				zClickAt("css=div[class^=ImgNewContactsFolder][class*=ZWidget]","0,0");
				
				page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageAddressbook);
			      				
			} else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}
		} else {
			throw new HarnessException("implement action:"+ action +" option:"+ option);
		}

		return page;
	}
	
	
	protected AbsPage zTreeItem(Action action, Button option, TagItem t)
	throws HarnessException {

		if ((action == null) || (option == null) || (t == null)) {
			throw new HarnessException(
			"Must define an action, option, and addressbook");
		}
	
		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = null;

		
		tracer.trace("processing " + t.getName());

		if (action == Action.A_LEFTCLICK) {

			throw new HarnessException("Action Left click not yet implemented");

		 } else if (action == Action.A_RIGHTCLICK) {
			actionLocator = "css=div#zti__main_Contacts__" + t.getId() + "_div";				
			zRightClickAt(actionLocator,"0,0");		
			//SleepUtil.sleepMedium();
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
	
	public AbsPage zTreeItem(Action action, String locator) throws HarnessException {
		AbsPage page = null;


		if ( locator == null )
			throw new HarnessException("locator is null for action "+ action);

		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Unable to locator folder in tree "+ locator);

		if ( action == Action.A_LEFTCLICK ) {

			// FALL THROUGH
		} else if ( action == Action.A_RIGHTCLICK ) {

			// Select the folder
			zRightClickAt(locator,"0,0");
            zWaitForBusyOverlay();
			// return a context menu
			return (new ContextMenu(MyApplication));

		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}

		zClickAt(locator,"0,0");

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

	//expand the folder to show folder's children
	public void zExpand(FolderItem folderItem) throws HarnessException{
		
	    String locator="css=td#zti__main_Contacts__" + folderItem.getId() +"_nodeCell" + ">div." ;
		//already expanded or not have sub folders
	    if (!sIsElementPresent(locator+ Locators.COLLAPSE_NODE)) {
		  return;
	    }
	    SleepUtil.sleepMedium();
	    if (this.sIsElementPresent(locator+ Locators.COLLAPSE_NODE)) {
		   sMouseDown(locator+ Locators.COLLAPSE_NODE);
		}
	    zWaitForElementPresent(locator+ Locators.EXPAND_NODE);
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
