/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogEditFolder;





/**
 * @author Matt Rhoades
 *
 */
public class TreeTasks extends AbsTree {
	
	public static class Locators {
		public static final String ztih__main_Tasks__ZIMLET_ID = "ztih__main_Tasks__ZIMLET";
		public static final String ztih__main_Mail__ZIMLET_nodeCell_ID = "ztih__main_Mail__ZIMLET_nodeCell";
		public static final String zNewTagIcon = "//td[contains(@class,'overviewHeader-Text FakeAnchor')]/div[contains(@class,'ImgNewTag')]";
		public static final String zTagsHeader = "//td[contains(@id,'ztih__main_Tasks__TAG_textCell')]";
	//	public static final String zDeleteTreeMenuItem = "//div[contains(@class,'ZMenuItem')]//tbody//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgDelete')]";
	//	public static final String zRenameTreeMenuItem = "//div[contains(@class,'ZMenuItem')]//tbody//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgRename')]";
		public static final String zDeleteTreeMenuItem = "css=div[id^='DELETE_WITHOUT_SHORTCUT'] tr[id='POPUP_DELETE_WITHOUT_SHORTCUT']";
		public static final String zRenameTreeMenuItem ="css=tr#POPUP_RENAME_FOLDER";
		public static final String zEditTreeMenuItem ="css=tr#POPUP_EDIT_PROPS";
		public static final String zRenameTagTreeMenuItem ="css=div[id='RENAME_TAG'] tr[id='POPUP_RENAME_TAG']";
		public static final String zNewTagTreeMenuItem="css=div[id='NEW_TAG'] tr[id='POPUP_NEW_TAG']";
		public static final String zShareTreeMenuItem ="css=div[id='SHARE_TASKFOLDER'] tr[id='POPUP_SHARE_TASKFOLDER']";
	}
	
		
	
	public TreeTasks(AbsApplication application) {
		super(application);
		logger.info("new " + TreeTasks.class.getCanonicalName());
	}
	


	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsTree#zPressButton(com.zimbra.qa.selenium.framework.ui.Button)
	 */
	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		tracer.trace("Press the " + button + " button");

		if (button == null)
			throw new HarnessException("Button cannot be null");

		AbsPage page = null;
		String locator = null;
		if (button == Button.B_TREE_NEWTAG) {

			locator = Locators.zNewTagIcon;

			if (!this.sIsElementPresent(locator)) {
				throw new HarnessException("Unable to locator folder in tree "
						+ locator);
			}
			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);
		}else if (button == Button.B_TREE_NEWTASKLIST) {

			locator = "css=div[id=ztih__main_Tasks__TASK] div[class^=ImgNewTaskList ZWidget]";
			page = new DialogCreateTaskFolder(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);

			if (!this.sIsElementPresent(locator)) {
				throw new HarnessException(
						"Unable to locate folder in the tree " + locator);
			}

			this.zClickAt(locator, "0,0");

			zWaitForBusyOverlay();

			return page;

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Click it
		this.zClickAt(locator,"");

		// If the app is busy, wait for that to finish
		this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if (page != null) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}

		return (page);

	}
	
	public AbsPage zPressPulldown(Button pulldown, Button option)
	throws HarnessException {
		logger.info(myPageName() + " zPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null");

		if (option == null)
			throw new HarnessException("Option cannot be null");

		AbsPage page = null;
		String pulldownLocator = null;
		String optionLocator = null;

		if (pulldown == Button.B_TREE_FOLDERS_OPTIONS) {

			pulldownLocator = "css=div[id='zov__main_Tasks'] td[id='ztih__main_Tasks__TASK_optCell'] td[id$='_title']";

			if (option == Button.B_TREE_NEWTASKLIST) {

				optionLocator = "css=div[id='ZmActionMenu_tasks_TASK'] div[id='NEW_TASK_FOLDER'] td[id$='_title']";
				page = new DialogCreateTaskFolder(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);

			} else {
				throw new HarnessException("Pulldown/Option " + pulldown + "/"
						+ option + " not implemented");
			}

			// FALL THROUGH

		} else if (pulldown == Button.B_TREE_TAGS_OPTIONS) {

			pulldownLocator = "css=div[id='zov__main_Tasks'] td[id='ztih__main_Tasks__TAG_optCell'] td[id$='_title']";

			if (option == Button.B_TREE_NEWTAG) {

				optionLocator = "css=div[id='ZmActionMenu_tasks_TAG'] div[id='NEW_TAG'] td[id$='_title']";
				page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);

			} else {
				throw new HarnessException("Pulldown/Option " + pulldown + "/"
						+ option + " not implemented");
			}

			// FALL THROUGH

		} else {
			throw new HarnessException("Pulldown/Option " + pulldown + "/"
					+ option + " not implemented");
		}

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			// 8.0 change ... need zClickAt()
			// this.zClick(pulldownLocator);
			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
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
	 * @see framework.ui.AbsTree#zTreeItem(framework.ui.Action, framework.items.FolderItem)
	 */
	public AbsPage zTreeItem(Action action, IItem tasklist) throws HarnessException {
		
		tracer.trace(action +" on folder = "+ tasklist.getName());

		AbsPage page = null;
		String locator = null;
		
		if ( !(tasklist instanceof FolderItem) )
			throw new HarnessException("folder must be of type FolderItem");
		
		// TODO: should be TaskListItem?
		FolderItem f = (FolderItem) tasklist;
		
		if ( action == Action.A_LEFTCLICK ) {
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			   locator = "css=[id^='zti__" + MyApplication.zGetActiveAccount().EmailAddress +
			         ":main_Tasks__'][id$=':" + f.getId() + "_textCell']";
			} else {
			   locator = "zti__main_Tasks__" + f.getId() + "_textCell";
			}
			
			// FALL THROUGH

		} else if ( action == Action.A_RIGHTCLICK ) {
			
		   if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
            locator = "css=[id^='zti__" + MyApplication.zGetActiveAccount().EmailAddress +
                  ":main_Tasks__'][id$=':" + f.getId() + "_textCell']";
         } else {
            locator = "zti__main_Tasks__" + f.getId() + "_textCell";
         }

			// Select the folder
			this.zRightClick(locator);
			
			// return a context menu
			return (new ContextMenu(MyApplication));

		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}

		
		if ( locator == null )
			throw new HarnessException("locator is null for action "+ action);
		
		
		// Default behavior.  Click the locator
		zClick(locator);

		// If there is a busy overlay, wait for that to finish
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			
			// Wait for the page to become active, if it was specified
			page.zWaitForActive();
		}

		((AppAjaxClient)MyApplication).zPageTasks.zWaitForDesktopLoadingSpinner(5000);

		return (page);

	}
		
	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem tasklist)
			throws HarnessException {
		logger.info(myPageName() + " zListItem(" + action + ", " + option
				+ ", " + tasklist + ")");

		tracer.trace(action + " then " + option + " on task = "
				+ tasklist.getName());

		if (action == null)
			throw new HarnessException("action cannot be null");
		if (option == null)
			throw new HarnessException("button cannot be null");
		if (tasklist == null)
			throw new HarnessException("folder cannot be null");

		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = null;
		// String itemLocator = null;

		// TODO: should be TaskList item?
		if (!(tasklist instanceof TagItem))
			throw new HarnessException("folder must be of type FolderItem");

		TagItem t = (TagItem) tasklist;

		tracer.trace("processing " + t.getName());

		if (action == Action.A_LEFTCLICK) {

			actionLocator = "implement me";

		} else if (action == Action.A_RIGHTCLICK) {

		   if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
		      actionLocator = "css=[id^='zti__" + MyApplication.zGetActiveAccount().EmailAddress +
		            ":main_Tasks__'][id$=':" + t.getId() + "_textCell']";
		   } else {
		      actionLocator = "zti__main_Tasks__" + t.getId() + "_textCell";
		   }

		   GeneralUtility.waitForElementPresent(this, actionLocator);
		   // actionLocator= Locators.zTagsHeader;
			this.zRightClickAt(actionLocator,"");

			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);

		} else {
			throw new HarnessException("Action " + action+ " not yet implemented");
		}
		if (option == Button.B_TREE_NEWTAG) {
			
			optionLocator=Locators.zNewTagTreeMenuItem;
			
		} else if (option == Button.B_DELETE) {

			optionLocator = Locators.zDeleteTreeMenuItem;
			//optionLocator = "css=tr#POPUP_DELETE";

			page = new DialogWarning(
					DialogWarning.DialogWarningID.DeleteTagWarningMessage,
					MyApplication, ((AppAjaxClient) MyApplication).zPageTasks);

		} else if (option == Button.B_RENAME) {

			optionLocator = Locators.zRenameTagTreeMenuItem;

			page = new DialogRenameTag(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);

		}else {
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

	public AbsPage zTreeItem(Action action, Button option, FolderItem folderItem)
			throws HarnessException {

		tracer.trace(action + " on folder = " + folderItem.getName());
		if (action == null)
			throw new HarnessException("action cannot be null");
		if (option == null)
			throw new HarnessException("button cannot be null");
		if (folderItem == null)
			throw new HarnessException("folder cannot be null");

		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = null;
		// String locator = null;

		if (!(folderItem instanceof FolderItem))
			throw new HarnessException("folder must be of type FolderItem");

		// TODO: should be TaskListItem?
		FolderItem f = (FolderItem) folderItem;

		if (action == Action.A_LEFTCLICK) {
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
				actionLocator = "css=[id^='zti__"
						+ MyApplication.zGetActiveAccount().EmailAddress
						+ ":main_Tasks__'][id$=':" + f.getId() + "_textCell']";
			} else {
				actionLocator = "zti__main_Tasks__" + f.getId() + "_textCell";
			}

			// FALL THROUGH

		} else if (action == Action.A_RIGHTCLICK) {

			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
				actionLocator = "css=[id^='zti__"
						+ MyApplication.zGetActiveAccount().EmailAddress
						+ ":main_Tasks__'][id$=':" + f.getId() + "_textCell']";
			} else {
				actionLocator = "zti__main_Tasks__" + f.getId() + "_textCell";
			}

			// Select the folder
			this.zRightClickAt(actionLocator,"0,0");

		} else {
			throw new HarnessException("Action " + action
					+ " not yet implemented");
		}

		if (option == Button.B_DELETE) {

			 optionLocator = Locators.zDeleteTreeMenuItem;
			//optionLocator = "css=tr#POPUP_DELETE";
			//page = new DialogConfirm(DialogConfirm.Confirmation.DELETE,
					//MyApplication, ((AppAjaxClient) MyApplication).zPageTasks);

			 page=null;
		} else if (option == Button.B_RENAME) {

			optionLocator = Locators.zRenameTreeMenuItem;

			page = new DialogRenameFolder(MyApplication,
					((AppAjaxClient) MyApplication).zPageTasks);

		}else if (option == Button.B_TREE_EDIT) {

			optionLocator = Locators.zEditTreeMenuItem;
			page = new DialogEditFolder(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

		}else if (option == Button.B_SHARE) {
			
			optionLocator =Locators.zShareTreeMenuItem;
			page = new DialogShare(MyApplication,((AppAjaxClient) MyApplication).zPageTasks);

			// FALL THROUGH

		}  else {
			throw new HarnessException("button " + option
					+ " not yet implemented");
		}
		if (actionLocator == null)
			throw new HarnessException("locator is null for action " + action);
		if (optionLocator == null)
			throw new HarnessException("locator is null for option " + option);

		// Default behavior. Click the locator
		zClickAt(optionLocator, "");

		// If there is a busy overlay, wait for that to finish
		this.zWaitForBusyOverlay();

		if (page != null) {

			// Wait for the page to become active, if it was specified
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
		if ( !((AppAjaxClient)MyApplication).zPageTasks.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageTasks.zNavigateTo();
		}
		
		// Zimlets seem to be loaded last
		// So, wait for the zimlet div to load
		String locator = Locators.ztih__main_Tasks__ZIMLET_ID;
		
		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);
		
		return (loaded);

	}


}
