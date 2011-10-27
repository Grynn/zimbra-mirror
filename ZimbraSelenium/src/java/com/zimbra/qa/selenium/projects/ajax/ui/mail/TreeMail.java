/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.items.SavedSearchFolderItem;
import com.zimbra.qa.selenium.framework.items.TagItem;
import com.zimbra.qa.selenium.framework.items.ZimletItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTree;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogRenameTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShare;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogShareFind;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.FormRecoverDeletedItems;



/**
 * @author zimbra
 *
 */
public class TreeMail extends AbsTree {
	public final static String stringToReplace = "<TREE_ITEM_NAME>";
	public static class Locators {
		// For desktop, Bug 56273:
		public final static String zTreeItems = new StringBuffer("//td[text()='").
		append(stringToReplace).append("']").toString();

		public static final String createNewFolderButton = "css=div[id='zov__main_Mail'] td[id='ztih__main_Mail__FOLDER_optCell'] td[id$='_title']";
		public static final String ztih__main_Mail__ZIMLET_ID = "ztih__main_Mail__ZIMLET";
		public static final String ztih__main_Mail__ZIMLET_ID_Desktop = "zt__main_Mail_zimlets__ZIMLET";
		public static final String ztih__main_Mail__ZIMLET_nodeCell_ID = "ztih__main_Mail__ZIMLET_nodeCell";
		public static final String ztih_main_Mail__FOLDER_ITEM_ID = new StringBuffer("ztih__main_Mail__").
		append(stringToReplace).append("_textCell").toString();
		public static final String zNewTagIcon = "//td[contains(@class,'overviewHeader-Text FakeAnchor')]/div[contains(@class,'ImgNewTag')]";
		public static final String zShowRemainingFolders = "css=td#zti__main_Mail__-3_textCell";

		// TODO: Implement for Desktop after bug 56273 is fixed
		public static final String treeExpandCollapseButton = "css=div[id='ztih__main_Mail__FOLDER_div'] div[class^='ImgNode']";

		public static final String zDeleteTreeMenuItem = "//div[contains(@class,'ZMenuItem')]//tbody//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgDelete')]";
		public static final String zRenameTreeMenuItem = "//div[contains(@class,'ZMenuItem')]//tbody//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgRename')]";
		public static final String zEditTreeMenuItem = "//td[contains(@id,'_title') and contains(text(),'Edit Properties')]";

		// Context menus
		public static final String ContextMenuCLVFoldersCSS = "css=div[id='ZmActionMenu_conversationList_FOLDER']";
		public static final String ContextMenuCLVSearchesCSS = "css=div[id='ZmActionMenu_conversationList_SEARCH']";
		public static final String ContextMenuCLVTagsCSS = "css=div[id='ZmActionMenu_conversationList_TAG']";
		public static final String ContextMenuCLVTagsCSS2 = "css=div[id^='ZmActionMenu_conversationList_TAG__']";
		public static final String ContextMenuTVFoldersCSS = "css=div[id='ZmActionMenu_mail_FOLDER']";
		public static final String ContextMenuTVSearchesCSS = "css=div[id='ZmActionMenu_mail_SEARCH']";
		public static final String ContextMenuTVTagsCSS = "css=div[id='ZmActionMenu_mail_TAG']";
		public static final String ContextMenuTVTagsCSS2 = "css=div[id^='ZmActionMenu_mail_TAG__']";

	}

	public TreeMail(AbsApplication application) {
		super(application);
		logger.info("new " + TreeMail.class.getCanonicalName());
	}

	protected AbsPage zTreeItem(Action action, Button option, FolderItem folder) throws HarnessException {

		if ( (action == null) || (option == null) || (folder == null) ) {
			throw new HarnessException("Must define an action, option, and folder");
		}
		
		AbsPage page = null;
		String actionLocator = "zti__main_Mail__" + folder.getId() + "_textCell";;
		String optionLocator = Locators.ContextMenuTVFoldersCSS;
		
		tracer.trace("processing " + folder.getName());

		if (action == Action.A_RIGHTCLICK) {

			if ( folder.getName().equals("USER_ROOT") ) {
				// Special case when the root folder is used.
				// Click on the "Folders" header
				actionLocator = "css=td[id='ztih__main_Mail__FOLDER_textCell']";
			}

			GeneralUtility.waitForElementPresent(this, actionLocator);
			this.zRightClickAt(actionLocator,"");
			this.zWaitForBusyOverlay();
			
			
			optionLocator = Locators.ContextMenuTVFoldersCSS; // css=div[id='ZmActionMenu_mail_FOLDER']
			if ( !(this.sIsElementPresent(optionLocator) && this.zIsVisiblePerPosition(optionLocator, 0, 0)) ) {
				// The app could use the conversation div, if it was ever activated previously
				optionLocator = Locators.ContextMenuCLVFoldersCSS;
			}
			
			
			if ( (option == Button.B_NEW) || (option == Button.O_NEW_FOLDER) ) {

				optionLocator += " div[id^='NEW_FOLDER'] td[id$='_title']";
				page = new DialogCreateFolder(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

				// FALL THROUGH

			} else if ( (option == Button.O_MARK_AS_READ) || (option == Button.B_TREE_FOLDER_MARKASREAD) ) {

				optionLocator += " div[id^='MARK_ALL_READ'] td[id$='_title']";
				page = null;

				// FALL THROUGH

			} else if (option == Button.B_DELETE) {

				// See http://bugzilla.zimbra.com/show_bug.cgi?id=64023
				optionLocator += " div[id^='DELETE_WITHOUT_SHORTCUT'] td[id$='_title']";
				page= null;

				// FALL THROUGH

			} else if (option == Button.B_RENAME) {

				optionLocator += " div[id^='RENAME_FOLDER'] td[id$='_title']";
				page = new DialogRenameFolder(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

				// FALL THROUGH

			} else if (option == Button.B_MOVE) {

				optionLocator += " div[id^='MOVE'] td[id$='_title']";
				page = new DialogMove(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

				// FALL THROUGH

			} else if (option == Button.B_SHARE) {
				
				optionLocator += " div[id^='SHARE_FOLDER'] td[id$='_title']";
				page = new DialogShare(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

				// FALL THROUGH

			} else if (option == Button.B_TREE_EDIT) {

				optionLocator += " div[id^='EDIT_PROPS'] td[id$='_title']";
				page = new DialogEditFolder(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

				// FALL THROUGH

			} else if (option == Button.B_TREE_FOLDER_EXPANDALL) {

				optionLocator += " div[id^='EXPAND_ALL'] td[id$='_title']";
				page = null;

				// FALL THROUGH

			} else if (option == Button.B_TREE_FOLDER_GET_EXTERNAL) {

				optionLocator += " div[id^='SYNC'] td[id$='_title']";
				page = null;

				// FALL THROUGH

//			} else if (option == Button.B_TREE_FOLDER_SYNC_ALL) {
//
//				optionLocator += " div[id^='SYNC_ALL'] td[id$='_title']";
//				page = null;
//
//				// FALL THROUGH
//
//			} else if (option == Button.B_TREE_FOLDER_SYNC_OFFLINE) {
//
//				optionLocator += " div[id^='SYNC_OFFLINE_FOLDER'] td[id$='_title']";
//				page = null;
//
//				// FALL THROUGH
//
			} else if (option == Button.B_TREE_FOLDER_EMPTY) {
				
				optionLocator += " div[id^='EMPTY_FOLDER'] td[id$='_title']";
				page = new DialogWarning(DialogWarning.DialogWarningID.EmptyFolderWarningMessage,
						MyApplication, ((AppAjaxClient) MyApplication).zPageMail);

			} else if (option == Button.B_RECOVER_DELETED_ITEMS) {

				optionLocator += " div[id^='RECOVER_DELETED_ITEMS'] td[id$='_title']";
				page = new FormRecoverDeletedItems(MyApplication);

			} else {
				throw new HarnessException("button " + option + " not yet implemented");
			}


		} else {
			throw new HarnessException("Action " + action+ " not yet implemented");
		}



		if (actionLocator == null)
			throw new HarnessException("locator is null for action " + action);
		if (optionLocator == null)
			throw new HarnessException("locator is null for option " + option);

		// Default behavior. Click the locator
		zClickAt(optionLocator,"");

		// If there is a busy overlay, wait for that to finish
		this.zWaitForBusyOverlay();

		if (page != null) {

			// Wait for the page to become active, if it was specified
			page.zWaitForActive();

		}

		return page;
	}

	protected AbsPage zTreeItem(Action action, Button option, SavedSearchFolderItem savedSearchFolder) throws HarnessException {
		if ( (action == null) || (option == null) || (savedSearchFolder == null) ) {
			throw new HarnessException("Must define an action, option, and addressbook");
		}
		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = Locators.ContextMenuTVSearchesCSS; // css=div[id='ZmActionMenu_mail_SEARCH'];
		SavedSearchFolderItem f= (SavedSearchFolderItem) savedSearchFolder;
		tracer.trace("processing " + f.getName());

		if (action == Action.A_LEFTCLICK) {

			actionLocator = "implement me";

		} else if (action == Action.A_RIGHTCLICK) {

			actionLocator = "zti__main_Mail__" + f.getId() + "_textCell";

			GeneralUtility.waitForElementPresent(this, actionLocator);
			// actionLocator= Locators.zTagsHeader;
			this.zRightClick(actionLocator);
			this.zWaitForBusyOverlay();
			
			optionLocator = Locators.ContextMenuTVSearchesCSS; // css=div[id='ZmActionMenu_mail_SEARCH']
			
			if ( !(this.sIsElementPresent(optionLocator) && this.zIsVisiblePerPosition(optionLocator, 0, 0)) ) {
				// The app could use the conversation div, if it was ever activated previously
				optionLocator = Locators.ContextMenuCLVSearchesCSS;
			}

		} else {

			throw new HarnessException("Action " + action+ " not yet implemented");

		}

		
		if (option == Button.B_DELETE) {

			// See http://bugzilla.zimbra.com/show_bug.cgi?id=64023
			optionLocator += " div[id^='DELETE_WITHOUT_SHORTCUT'] td[id$='_title']";
			page= null;

		} else if (option == Button.B_RENAME) {

			optionLocator += " div[id^='RENAME_SEARCH'] td[id$='_title']";
			page = new DialogRenameFolder(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

		}  else if (option == Button.B_EDIT) {

			optionLocator += " div[id^='EDIT_PROPS'] td[id$='_title']";
			page = new DialogMove(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

		}  else if (option == Button.B_MOVE) {

			optionLocator += " div[id^='MOVE'] td[id$='_title']";
			page = new DialogMove(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

		}

		if (actionLocator == null)
			throw new HarnessException("locator is null for action " + action);
		if (optionLocator == null)
			throw new HarnessException("locator is null for option " + option);

		// Default behavior. Click the locator
		zClick(optionLocator);

		// If there is a busy overlay, wait for that to finish
		this.zWaitForBusyOverlay();

		if (page != null) {

			// Wait for the page to become active, if it was specified
			page.zWaitForActive();
		}
		return page;
	}

	protected AbsPage zTreeItem(Action action, Button option, ZimletItem zimlet) throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	
	protected AbsPage zTreeItem(Action action, Button option, TagItem folder)
	throws HarnessException {

		if ((action == null) || (option == null) || (folder == null)) {
			throw new HarnessException(
			"Must define an action, option, and addressbook");
		}
		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = Locators.ContextMenuTVTagsCSS; // css=div[id='ZmActionMenu_conversationList_TAG']

		TagItem t = (TagItem) folder;
		tracer.trace("processing " + t.getName());

		if (action == Action.A_LEFTCLICK) {

			actionLocator = "implement me";

		} else if (action == Action.A_RIGHTCLICK) {

			actionLocator = "zti__main_Mail__" + t.getId() + "_textCell";

			GeneralUtility.waitForElementPresent(this, actionLocator);
			// actionLocator= Locators.zTagsHeader;
			this.zRightClickAt(actionLocator,"");
			
			this.zWaitForBusyOverlay();

			page = new DialogTag(MyApplication,
					((AppAjaxClient) MyApplication).zPageMail);
			
			// REF: http://bugzilla.zimbra.com/show_bug.cgi?id=64616#c9
			// 
			// The context menu has 4 forms:
			//
			// css=div[id='ZmActionMenu_mail_TAG']
			// css=div[id='ZmActionMenu_mail_TAG_DWTxyz']
			// css=div[id='ZmActionMenu_conversationList_TAG']
			// css=div[id='ZmActionMenu_conversationList_TAG_DWTxyz']
			//
			// Choose whichever is visible
			//
			optionLocator = Locators.ContextMenuTVTagsCSS; // css=div[id='ZmActionMenu_mail_TAG']
			if ( !(this.sIsElementPresent(optionLocator) && this.zIsVisiblePerPosition(optionLocator, 0, 0)) ) {
				optionLocator = Locators.ContextMenuTVTagsCSS2;  // css=div[id^='ZmActionMenu_mail_TAG__']
				if ( !(this.sIsElementPresent(optionLocator) && this.zIsVisiblePerPosition(optionLocator, 0, 0)) ) {
					optionLocator = Locators.ContextMenuCLVTagsCSS;  // css=div[id='ZmActionMenu_conversationList_TAG']
					if ( !(this.sIsElementPresent(optionLocator) && this.zIsVisiblePerPosition(optionLocator, 0, 0)) ) {
						optionLocator = Locators.ContextMenuCLVTagsCSS2;  // css=div[id^='ZmActionMenu_conversationList_TAG__']
						if ( !(this.sIsElementPresent(optionLocator) && this.zIsVisiblePerPosition(optionLocator, 0, 0)) ) {
							throw new HarnessException("No context menu!");
						}
					}
				}
			}
			


		} else {
			throw new HarnessException("Action " + action
					+ " not yet implemented");
		}
		

		if (option == Button.B_TREE_NEWTAG) {

			// optionLocator = "//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgNewTag')]";
			// optionLocator="//div[contains(@id,'POPUP_DWT') and contains(@class,'ZHasSubMenu')]//tbody/tr[@id='POPUP_NEW_TAG']";
			// optionLocator = css=div[id='ZmActionMenu_conversationList_TAG'] div[id='NEW_TAG'] td[id$='_title']
			optionLocator += " div[id^='NEW_TAG'] td[id$='_title']";

		} else if (option == Button.B_DELETE) {

			// optionLocator = Locators.zDeleteTreeMenuItem;
			optionLocator += " div[id^='DELETE_WITHOUT_SHORTCUT'] td[id$='_title']";

			page = new DialogWarning(
					DialogWarning.DialogWarningID.DeleteTagWarningMessage,
					MyApplication, ((AppAjaxClient) MyApplication).zPageMail);

		} else if (option == Button.O_MARK_AS_READ) {

			optionLocator += " div[id^='MARK_ALL_READ'] td[id$='_title']";

		} else if (option == Button.B_RENAME) {

			// optionLocator = Locators.zRenameTreeMenuItem;
			optionLocator += " div[id^='RENAME_TAG'] td[id$='_title']";

			page = new DialogRenameTag(MyApplication,
					((AppAjaxClient) MyApplication).zPageMail);

		} else {
			throw new HarnessException("button " + option
					+ " not yet implemented");
		}
		if (actionLocator == null)
			throw new HarnessException("locator is null for action " + action);
		if (optionLocator == null)
			throw new HarnessException("locator is null for option " + option);

		// Default behavior. Click the locator
		zClickAt(optionLocator,"");

		// If there is a busy overlay, wait for that to finish
		this.zWaitForBusyOverlay();

		if (page != null) {

			// Wait for the page to become active, if it was specified
			page.zWaitForActive();
		}

		return (page);

	}

	protected AbsPage zTreeItem(Action action, FolderItem folder) throws HarnessException {
		AbsPage page = null;
		String locator = null;

		if ( action == Action.A_LEFTCLICK ) {
			
			locator = "css=td[id='zti__main_Mail__"+ folder.getId() +"_textCell']";

			// FALL THROUGH

		} else if ( action == Action.A_RIGHTCLICK ) {

			locator = "css=td[id='zti__main_Mail__"+ folder.getId() +"_textCell']";

			// Select the folder
			this.zRightClickAt(locator,"");

			// return a context menu
			return (new ContextMenu(MyApplication));

		} else if ( action == Action.A_TREE_EXPAND ) {

			locator = "css=[id='zti__main_Mail__"+ folder.getId() +"_nodeCell'] div[class='ImgNodeCollapsed']";
			if ( !this.sIsElementPresent(locator) ) {
				logger.warn("Trying to expand a folder that probably has no subfolders or is already expanded");
				return (page);
			}

			this.sMouseDown(locator);

			this.zWaitForBusyOverlay();

			// No page to return
			return (null);

		} else if ( action == Action.A_TREE_COLLAPSE ) {

			locator = "css=[id='zti__main_Mail__"+ folder.getId() +"_nodeCell'] div[class='ImgNodeExpanded']";
			if ( !this.sIsElementPresent(locator) ) {
				logger.warn("Trying to collapse a folder that probably has no subfolders or is already collapsed");
				return (page);
			}

			this.sMouseDown(locator);

			this.zWaitForBusyOverlay();

			// No page to return
			return (null);

		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}


		if ( locator == null )
			throw new HarnessException("locator is null for action "+ action);


		// Default behavior.  Click the locator
		zClickAt(locator,"");

		// If there is a busy overlay, wait for that to finish
		this.zWaitForBusyOverlay();

		if ( page != null ) {

			// Wait for the page to become active, if it was specified
			page.zWaitForActive();
		}

		return (page);

	}

	/**
	 * To get whether the tree is collapsed or not
	 * @return true if tree is collapsed, otherwise false
	 * @throws HarnessException 
	 */
	public boolean isCollapsed() throws HarnessException {
		if (sIsElementPresent(Locators.treeExpandCollapseButton.replace(
				"ImgNode", "ImgNodeCollapsed"))) {
			return true;
		} else {
			return false;
		}
	}

	protected AbsPage zTreeItem(Action action, SavedSearchFolderItem savedSearch) throws HarnessException {
		AbsPage page = null;
		String locator = null;

		if ( action != Action.A_LEFTCLICK ) 
			throw new HarnessException("No implementation for Action = "+ action);

		// TODO: implement me!
		locator = "css=td#zti__main_Mail__"+ savedSearch.getId() + "_textCell";

		// Default behavior.  Click the locator
		zClickAt(locator,"");

		// If the app is busy, wait until it is ready again
		this.zWaitForBusyOverlay();

		if ( page != null ) {

			// Wait for the page to become active, if it was specified
			page.zWaitForActive();
		}

		return (page);
	}

	protected AbsPage zTreeItem(Action action, ZimletItem zimlet) throws HarnessException {
		throw new HarnessException("implement me");
	}

	public AbsPage zPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zPressPulldown("+ pulldown +", "+ option +")");

		tracer.trace("Click "+ pulldown +" then "+ option);

		if ( pulldown == null )
			throw new HarnessException("Pulldown cannot be null");

		if ( option == null )
			throw new HarnessException("Option cannot be null");


		AbsPage page = null;
		String pulldownLocator = null;
		String optionLocator = null;
		
		
		if ( pulldown == Button.B_TREE_FOLDERS_OPTIONS ) {
			
			pulldownLocator = "css=div[id='zov__main_Mail'] td[id='ztih__main_Mail__FOLDER_optCell'] td[id$='_title']";
			
			if ( option == Button.B_TREE_NEWFOLDER ) {
				
				optionLocator = "css=div[id='ZmActionMenu_mail_FOLDER'] div[id='NEW_FOLDER'] td[id$='_title']";
				page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageMail);
			
			} else {
				throw new HarnessException("Pulldown/Option "+ pulldown +"/"+ option +" not implemented");
			}

			// FALL THROUGH
			
		} else if ( pulldown == Button.B_TREE_TAGS_OPTIONS ) {
			
			pulldownLocator = "css=div[id='zov__main_Mail'] td[id='ztih__main_Mail__TAG_optCell'] td[id$='_title']";
			
			if ( option == Button.B_TREE_NEWTAG ) {

				optionLocator = "css=div[id='ZmActionMenu_mail_TAG'] div[id='NEW_TAG'] td[id$='_title']";
				page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

			} else {
				throw new HarnessException("Pulldown/Option "+ pulldown +"/"+ option +" not implemented");
			}

			// FALL THROUGH
			
		} else {
			throw new HarnessException("Pulldown/Option "+ pulldown +"/"+ option +" not implemented");
		}
		
		

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option " + option + " pulldownLocator " + pulldownLocator + " not present!");
			}

			// 8.0 change ... need zClickAt()
			// this.zClick(pulldownLocator);
			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown + " option " + option + " optionLocator " + optionLocator + " not present!");
				}

				// 8.0 change ... need zClickAt()
				// this.zClick(optionLocator);
				this.zClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
			}
		}
		
		
		// Return the specified page, or null if not set
		return (page);


	}
	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsTree#zPressButton(com.zimbra.qa.selenium.framework.ui.Button)
	 */
	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zPressButton("+ button +")");

		tracer.trace("Click "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null");

		AbsPage page = null;
		String locator = null;

		if ( button == Button.B_TREE_NEWFOLDER ) {
			
			// 8.0 behavior - new Pulldown menu
			return (zPressPulldown(Button.B_TREE_FOLDERS_OPTIONS, Button.B_TREE_NEWFOLDER));

		}else if (button == Button.B_TREE_NEWTAG) {

			return (zPressPulldown(Button.B_TREE_TAGS_OPTIONS, Button.B_TREE_NEWTAG));

		} else if ( button == Button.B_TREE_FIND_SHARES ) {

			locator = "css=a[id$='_addshare_link']";
			if (!this.sIsElementPresent(locator)) {
				throw new HarnessException("Unable to locator folder in tree " + locator);
			}

			page = new DialogShareFind(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

			// Use sClick, not default zClick
			this.sClick(locator);

			// If the app is busy, wait for that to finish
			this.zWaitForBusyOverlay();

			// This function (default) throws an exception if never active
			page.zWaitForActive();

			return (page);

		}else if (button == Button.B_TREE_SHOW_REMAINING_FOLDERS ) {

			locator = Locators.zShowRemainingFolders;
			page = null;

			if (!this.sIsElementPresent(locator)) {
				throw new HarnessException("Unable to find 'show remaining folders' in tree " + locator);
			}

			// FALL THROUGH

		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//

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

	protected AbsPage zTreeItem(Action action, String locator) throws HarnessException {
		AbsPage page = null;


		if ( locator == null )
			throw new HarnessException("locator is null for action "+ action);

		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Unable to locator folder in tree "+ locator);

		if ( action == Action.A_LEFTCLICK ) {

			// FALL THROUGH
		} else if ( action == Action.A_RIGHTCLICK ) {

			// Select the folder
			this.zRightClick(locator);

			// return a context menu
			return (new ContextMenu(MyApplication));

		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}

		// Default behavior.  Click the locator
		zClick(locator);

		return (page);
	}

	/* (non-Javadoc)
	 * @see framework.ui.AbsTree#zTreeItem(framework.ui.Action, framework.items.FolderItem)
	 */
	public AbsPage zTreeItem(Action action, IItem folder) throws HarnessException {
		logger.info(myPageName() + " zTreeItem("+ action +", "+ folder.getName() +")");

		tracer.trace("Click "+ action +" on folder "+ folder.getName());

		// Validate the arguments
		if ( (action == null) || (folder == null) ) {
			throw new HarnessException("Must define an action and addressbook");
		}

		// Bug 65234
		// Sometimes the folder refresh takes a while.  Sleep a bit here.
		// If bug 65234 is fixed, this delay can likely be removed
		SleepUtil.sleepVerySmall();
	
		
		if ( folder instanceof FolderItem ) {
			return (zTreeItem(action, (FolderItem)folder));
		} else if ( folder instanceof SavedSearchFolderItem ) {
			return (zTreeItem(action, (SavedSearchFolderItem)folder));
		} else if ( folder instanceof ZimletItem ) {
			return (zTreeItem(action, (ZimletItem)folder));
		}

		throw new HarnessException("Must use FolderItem or SavedSearchFolderItem or ZimletItem as argument, but was "+ folder.getClass());
	}

	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem folder) throws HarnessException {
		logger.info(myPageName() + " zTreeItem("+ action +", "+ option +", "+ folder.getName() +")");

		tracer.trace("Click "+ action +" then "+ option +" on folder "+ folder.getName());

		// Validate the arguments
		if ( (action == null) || (option == null) || (folder == null) ) {
			throw new HarnessException("Must define an action, option, and addressbook");
		}

		if ( folder instanceof FolderItem ) {
			return (zTreeItem(action, option, (FolderItem)folder));
		} else if ( folder instanceof SavedSearchFolderItem ) {
			return (zTreeItem(action, option, (SavedSearchFolderItem)folder));
		} else if ( folder instanceof ZimletItem ) {
			return (zTreeItem(action, option, (ZimletItem)folder));
		}else if ( folder instanceof TagItem ) {
			return (zTreeItem(action, option, (TagItem)folder));
		}

		throw new HarnessException("Must use TagItem FolderItem or SavedSearchFolderItem or ZimletItem as argument, but was "+ folder.getClass());
	}


	private FolderItem parseFolderRow(String id) throws HarnessException {

		String locator;

		FolderItem item = new FolderItem();

		item.setId(id);

		// Set the name
		locator = "css=div[id='zti__main_Mail__"+ id +"'] td[id$='_textCell']";
		item.setName(this.sGetText(locator));

		// Set the expanded boolean
		locator = "css=div[id='zti__main_Mail__"+ id +"'] td[id$='_nodeCell']>div";
		if ( sIsElementPresent(locator) ) {
			// The image could be hidden, if there are no subfolders
			item.gSetIsExpanded("ImgNodeExpanded".equals(sGetAttribute(locator + "@class")));
		}

		// Set the selected boolean
		locator = "css=div[id='zti__main_Mail__"+ id +"'] div[id='zti__main_Mail__"+ id +"_div']";
		if ( sIsElementPresent(locator) ) {
			item.gSetIsSelected("DwtTreeItem-selected".equals(sGetAttribute(locator + "@class")));
		}

		// TODO: color

		return (item);
	}

	/**
	 * Used for recursively building the tree list for Mail Folders
	 * @param css
	 * @return
	 * @throws HarnessException
	 */
	private List<FolderItem> zListGetFolders(String css) throws HarnessException {
		List<FolderItem> items = new ArrayList<FolderItem>();

		String searchLocator = css + " div[class='DwtComposite']";

		int count = this.sGetCssCount(searchLocator);
		logger.debug(myPageName() + " zListGetFolders: number of folders: "+ count);

		for ( int i = 1; i <= count; i++) {
			String itemLocator = searchLocator + ":nth-child("+i+")";

			if ( !this.sIsElementPresent(itemLocator) ) {
				continue;
			}

			String identifier = sGetAttribute(itemLocator +"@id");
			logger.debug(myPageName() + " identifier: "+ identifier);

			if ( identifier == null || identifier.trim().length() == 0 || !(identifier.startsWith("zti__main_Mail__")) ) {
				// Not a folder
				// Maybe "Find Shares ..."
				count++; // Add one more to the total 'count' for this 'unknown' item
				continue;
			}

			// Set the locator
			// TODO: This could probably be made safer, to make sure the id matches an int pattern
			String id = identifier.replace("zti__main_Mail__", "");

			FolderItem item = this.parseFolderRow(id);
			items.add(item);
			logger.info(item.prettyPrint());

			// Add any sub folders
			items.addAll(zListGetFolders(itemLocator));


		}

		return (items);

	}

	/**
	 * Used for recursively building the tree list for Saved Search Folders
	 * @param top
	 * @return
	 * @throws HarnessException
	 */
	private List<SavedSearchFolderItem>zListGetSavedSearchFolders(String top) throws HarnessException {
		List<SavedSearchFolderItem> items = new ArrayList<SavedSearchFolderItem>();

		String searchLocator = top + "//div[@class='DwtComposite']";

		int count = this.sGetXpathCount(searchLocator);
		for ( int i = 1; i <= count; i++) {
			String itemLocator = searchLocator + "["+ i + "]";

			if ( !this.sIsElementPresent(itemLocator) ) {
				continue;
			}

			String locator;

			String id = sGetAttribute("xpath=("+ itemLocator +"/.)@id");
			if ( id == null || id.trim().length() == 0 || !(id.startsWith("zti__main_Mail__")) ) {
				// Not a folder
				// Maybe "Find Shares ..."
				continue;
			}

			SavedSearchFolderItem item = new SavedSearchFolderItem();

			// Set the locator
			// TODO: This could probably be made safer, to make sure the id matches an int pattern
			item.setId(id.replace("zti__" + ((AppAjaxClient)MyApplication).zGetActiveAccount().EmailAddress +
					":main_Mail__", ""));

			// Set the name
			locator = itemLocator + "//td[contains(@id, '_textCell')]";
			item.setName(this.sGetText(locator));

			// Set the expanded boolean
			locator = itemLocator + "//td[contains(@id, '_nodeCell')]/div";
			if ( sIsElementPresent(locator) ) {
				// The image could be hidden, if there are no subfolders
				//item.gSetIsExpanded("ImgNodeExpanded".equals(sGetAttribute("xpath=("+ locator + ")@class")));
			}

			items.add(item);

			// Add any sub folders
			items.addAll(zListGetSavedSearchFolders(itemLocator));


		}

		return (items);

	}

	public List<FolderItem> zListGetFolders() throws HarnessException {

		// Bug 65234
		// Sleep for a bit to load up the new folders
		SleepUtil.sleepVerySmall();
		
		
		List<FolderItem> items = new ArrayList<FolderItem>();

		// Recursively fill out the list, starting with all mail folders
		items.addAll(zListGetFolders("css=div[id='ztih__main_Mail__FOLDER']"));

		return (items);

	}

	public List<SavedSearchFolderItem> zListGetSavedSearches() throws HarnessException {

		List<SavedSearchFolderItem> items = new ArrayList<SavedSearchFolderItem>();

		// Recursively fill out the list, starting with all mail folders
		items.addAll(zListGetSavedSearchFolders("//div[@id='ztih__main_Mail__SEARCH']"));

		// Return the list of items
		return (items);

	}

	public List<TagItem> zListGetTags() throws HarnessException {


		List<TagItem> items = new ArrayList<TagItem>();

		// TODO: implement me!

		// Return the list of items
		return (items);


	}

	public List<ZimletItem> zListGetZimlets() throws HarnessException {


		// Create a list of items to return
		List<ZimletItem> items = new ArrayList<ZimletItem>();

		String treeLocator = ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ?
				Locators.ztih__main_Mail__ZIMLET_ID_Desktop :
					Locators.ztih__main_Mail__ZIMLET_ID;

		// Make sure the button exists
		if ( !this.sIsElementPresent(treeLocator) )
			throw new HarnessException("Zimlet Tree is not present "+ treeLocator);

		// Zimlet's div ID seems to start with -999
		for (int zimletNum = -999; zimletNum < 0; zimletNum++ ) {

			String zimletLocator = null;
			String imageLocator = null;
			String nameLocator = null;
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
				zimletLocator = "zti__main_Mail_zimlets__" + zimletNum +"_z_div";
				imageLocator = "xpath=(//*[@id='zti__main_Mail_zimlets__"+ zimletNum +"_z_imageCell']/div)@class";
				nameLocator = "zti__main_Mail_zimlets__"+ zimletNum +"_z_textCell";
			} else {
				zimletLocator = "zti__main_Mail__"+ zimletNum +"_z_div";
				imageLocator = "xpath=(//*[@id='zti__main_Mail__"+ zimletNum +"_z_imageCell']/div)@class";
				nameLocator = "zti__main_Mail__"+ zimletNum +"_z_textCell";
			}

			if ( !this.sIsElementPresent(zimletLocator) ) {
				// No more items to parse
				return (items);
			}

			// Parse this div element into a ZimletItem object

			ZimletItem item = new ZimletItem();

			// Get the image
			item.setFolderTreeImage(this.sGetAttribute(imageLocator));

			// Get the display name
			item.setFolderTreeName(this.sGetText(nameLocator));

			// Set the locator
			item.setFolderTreeLocator(zimletLocator);

			// Add this item to the list
			items.add(item);

		}

		// If we get here, there were over 1000 zimlets or something went wrong
		throw new HarnessException("Too many zimlets!");

	}

	public enum FolderSectionAction {
		Expand,
		Collapse
	}

	public enum FolderSection {
		Folders,
		Searches,
		Tags,
		Zimlets
	}

	/**
	 * Apply an expand/collpase to the Folders, Searches, Tags and Zimlets sections
	 * @param a
	 * @param section
	 * @throws HarnessException
	 */
	public AbsPage zSectionAction(FolderSectionAction action, FolderSection section) throws HarnessException {

		AbsPage page = null;
		String locator = null;
		boolean expanded = false;

		if ( section == FolderSection.Zimlets ) {

			// What is the current state of the section?
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
				locator = "css=div[class*='ZmOverviewZimletHeader'] div[class^='ImgNode']@class";
			} else {
				locator = "xpath=(//td[@id='"+ Locators.ztih__main_Mail__ZIMLET_nodeCell_ID +"']/div)@class"; 
			}

			// Image is either ImgNodeExpanded or ImgNodeCollapsed
			expanded = sGetAttribute(locator).equals("ImgNodeExpanded");


			if ( action == FolderSectionAction.Expand ) {

				if ( expanded ) {
					logger.info("section is already expanded");
					return (page);
				}

				if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
					locator = "css=div[class*='ZmOverviewZimletHeader'] div[class^='ImgNode']";
				} else {
					locator = "css=td[id="+ Locators.ztih__main_Mail__ZIMLET_nodeCell_ID +"] div";
				}

			}

			// Fall through

		}

		if ( locator == null ) {
			throw new HarnessException("no locator defined for "+ action +" "+ section);
		}

		// Default behavior
		this.zClick(locator);

		this.zWaitForBusyOverlay();

		if ( page != null ) {
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

		// Make sure the main page is active
		if ( !((AppAjaxClient)MyApplication).zPageMail.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMail.zNavigateTo();
		}

		// Zimlets seem to be loaded last
		// So, wait for the zimlet div to load
		String locator = ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ?
				Locators.ztih__main_Mail__ZIMLET_ID_Desktop :
					Locators.ztih__main_Mail__ZIMLET_ID;

		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);

		return (loaded);

	}


}
