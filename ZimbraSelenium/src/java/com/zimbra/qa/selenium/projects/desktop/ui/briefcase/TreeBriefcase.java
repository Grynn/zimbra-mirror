/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui.briefcase;

import java.util.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.desktop.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.DialogCreateBriefcaseFolder;
import com.zimbra.qa.selenium.projects.desktop.ui.*;

/**
 * @author zimbra
 * 
 */

public class TreeBriefcase extends AbsTree {

   public final static String stringToReplace = "<TREE_ITEM>";

   public static class Locators {
	   public final static String zTreeItems = new StringBuffer("css=div[id='zovc__main_Briefcase'] td:contains('").
            append(stringToReplace).append("')").toString();
		public static final String briefcaseListView = "css=[id='zl__BDLV__rows']";
		public static final String briefcaseTreeView = "css=[id*=zti__main_Briefcase__";
		public static final String briefcaseTreeView_Desktop = "css=td[id*='main_Briefcase']";
		public static final String zNewTagTreeMenuItem = "css=td[id$=_left_icon]>[class=ImgNewTag]";
		public static final String zNewFolderTreeMenuItem = "css=tr[id=POPUP_NEW_BRIEFCASE]:contains('New Folder')";
		public static final String zNewBriefcaseTreeMenuItem = "css=tr[id=POPUP_NEW_BRIEFCASE]:contains('New Briefcase')";
		public static final String zRenameTagTreeMenuItem = "css=td[id$=_left_icon]>[class=ImgRename]";
		public static final String zDeleteTreeMenuItem = "css=td[id$=_left_icon]>[class=ImgDelete]";
		//public static final String zDeleteTreeMenuItem = "css=div[id='DELETE_WITHOUT_SHORTCUT'] tr[id^='POPUP_DELETE']:contains(Delete)";
		public static final String treeExpandCollapseButton = "css=div[id='zovc__main_Briefcase'] div[id^='DWT'][class='DwtTreeItem'] [class^='ImgNode']";
      public static final String multipleTrees = "css=div[id='zovc__main_Briefcase'] div[id^='zovc__main_Briefcase'][id$='__SECTION'][class^='DwtComposite ZmOverview']:nth-of-type(<NUM>)";
      public static final String multipleTreesExpandCollapseButton = multipleTrees + " div[id^='DWT'] [class^='ImgNode']";
      public static final String multipleLocalFolderTreesExpandCollapseButton = multipleTrees + " div[class='DwtTreeItemLevel1ChildDiv'] [class^='ImgNode']";
	}

	public TreeBriefcase(AbsApplication application) {
		super(application);
		logger.info("new " + TreeBriefcase.class.getCanonicalName());
	}

	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem item)
			throws HarnessException {
		logger.info(myPageName() + " zTreeItem(" + action + ", " + option
				+ ", " + item.getName() + ")");

		tracer.trace("Click " + action + " then " + option + " on item "
				+ item.getName());

		// Validate the arguments
		if ((action == null) || (option == null) || (item == null)) {
			throw new HarnessException(
					"Must define an action, option, and item");
		}

		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = null;

		if (item instanceof TagItem) {
		   actionLocator = Locators.briefcaseTreeView_Desktop + "[id*='"
               + MyApplication.zGetActiveAccount().EmailAddress + "']"
               + ":contains('" + item.getName() + "')";
		}else if (item instanceof FolderItem) {
		   FolderItem folder = (FolderItem) item;

		   String emailAddress = folder.isDesktopClientLocalFolder() ? 
		         ZimbraAccount.clientAccountName :
		            MyApplication.zGetActiveAccount().EmailAddress;

		   if (folder.getName().equals("USER_ROOT")) {
		      //TODO: Bug 65039
		      actionLocator = Locators.zTreeItems.replace(TreeBriefcase.stringToReplace,
		            "Local Folders");

		   } else {
		      actionLocator = Locators.briefcaseTreeView_Desktop + "[id*='"
		            + emailAddress + "']"
		            + "[id$='" + ((FolderItem) item).getId()
		            + "_imageCell']";

		   }

		} else {
			throw new HarnessException("Must use IItem as argument, but was "
					+ item.getClass());
		}

		if (action == Action.A_RIGHTCLICK) {

			this.zRightClickAt(actionLocator, "0,0");
		} else {
			throw new HarnessException("implement me! " + action
					+ ": not implemented");
		}

		if (option == Button.B_TREE_NEWTAG) {

			optionLocator = Locators.zNewTagTreeMenuItem;

			page = new DialogTag(MyApplication,
					((AppAjaxClient) MyApplication).zPageBriefcase);

		} else if (option == Button.B_TREE_RENAMETAG) {

			optionLocator = Locators.zRenameTagTreeMenuItem;

			page = new DialogRenameTag(MyApplication,
					((AppAjaxClient) MyApplication).zPageBriefcase);
		} else if (option == Button.B_TREE_DELETE) {

			optionLocator = Locators.zDeleteTreeMenuItem;

			if (item instanceof TagItem) {
				page = new DialogWarning(
						DialogWarning.DialogWarningID.DeleteTagWarningMessage,
						MyApplication,
						((AppAjaxClient) MyApplication).zPageBriefcase);
			}
		}else if (option == Button.B_TREE_NEWFOLDER) {
		   FolderItem folder = (FolderItem) item;

		   if (folder.getName().equals("USER_ROOT")) {
		      optionLocator = Locators.zNewBriefcaseTreeMenuItem;
		   } else {
		      optionLocator = Locators.zNewFolderTreeMenuItem;		      
		   }

			page = new DialogCreateBriefcaseFolder(MyApplication,
					((AppAjaxClient) MyApplication).zPageBriefcase);

		} else {
			throw new HarnessException("button " + option
					+ " not yet implemented");
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
			//page.zWaitForActive();
		}
		return (page);
	}

	public AbsPage zTreeItem(Action action, IItem item, boolean isRowAdded)
			throws HarnessException {

		tracer.trace("Click " + action + " on folder " + item.getName());

		String treeItemLocator = null;
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			treeItemLocator = Locators.briefcaseTreeView_Desktop
					+ "[class='DwtTreeItem-Text']";
		} else {
			treeItemLocator = Locators.briefcaseTreeView
					+ "][class='DwtTreeItem-selected']";
		}
		AbsPage page = zTreeItem(action, item);

		zWaitForElementPresent(treeItemLocator);

		String listItemLocator = "css=[id='zl__BDLV__rows']";

		if (isRowAdded)
			listItemLocator += " div[class^='Row']";

		zWaitForElementPresent(listItemLocator);

		return page;
	}

	@Override
	public AbsPage zTreeItem(Action action, IItem item) throws HarnessException {
		logger.info(myPageName() + " zTreeItem(" + action + ", "
				+ item.getName() + ")");

		tracer.trace("Click " + action + " on item " + item.getName());

		// Validate the arguments
		if ((action == null) || (item == null)) {
			throw new HarnessException("Must define an action, and item");
		}

		AbsPage page = null;
		String locator = null;

		if (item instanceof TagItem) {
		   locator = Locators.briefcaseTreeView_Desktop + "[id*='"
               + MyApplication.zGetActiveAccount().EmailAddress + "']"
               + ":contains('" + item.getName() + "')";
		} else if (item instanceof FolderItem) {
		   FolderItem folder = (FolderItem) item;

		   String emailAddress = folder.isDesktopClientLocalFolder() ?
		         ZimbraAccount.clientAccountName :
		            MyApplication.zGetActiveAccount().EmailAddress;

		   locator = Locators.briefcaseTreeView_Desktop + "[id*='"
		         + emailAddress + "']"
		         + "[id$='" + ((FolderItem) item).getId()
		         + "_imageCell']";

		} else {
			throw new HarnessException("Must use IItem as argument, but was "
					+ item.getClass());
		}

		if (action == Action.A_LEFTCLICK) {

			zWaitForBusyOverlay();

			// FALL THROUGH
		} else if (action == Action.A_RIGHTCLICK) {

			if (!this.sIsElementPresent(locator))
				throw new HarnessException(
						"Unable to locate folder in the tree " + locator);

			// Select the folder
			this.zRightClick(locator);

			// return a context menu
			return (new ContextMenu(MyApplication));
		} else {
			throw new HarnessException("Action " + action
					+ " not yet implemented");
		}

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Unable to locate folder in the tree "
					+ locator);

		// Default behavior. Click the locator
		zClick(locator);

		// If there is a busy overlay, wait for that to finish
		zWaitForBusyOverlay();

		if (page != null) {
			// Wait for the page to become active, if it was specified
			page.zWaitForActive();
		}
		return (page);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.zimbra.qa.selenium.framework.ui.AbsTree#zPressButton(com.zimbra.qa
	 * .selenium.framework.ui.Button)
	 */
	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		tracer.trace("Click " + button);

		if (button == null)
			throw new HarnessException("Button cannot be null");

		AbsPage page = null;
		String locator = null;

		if (button == Button.B_TREE_NEWBRIEFCASE) {

			locator = "id=overviewHeader-Text FakeAnchor";
			page = new DialogCreateBriefcaseFolder(MyApplication,
					((AppAjaxClient) MyApplication).zPageBriefcase);

			if (!this.sIsElementPresent(locator)) {
				throw new HarnessException(
						"Unable to locate folder in the tree " + locator);
			}

			this.zClick(locator);

			zWaitForBusyOverlay();

			return page;

			// FALL THROUGH
		} else if (button == Button.B_TREE_NEWTAG) {

			locator = "css=div[class^=ImgNewTag ZWidget]";

			if (!this.sIsElementPresent(locator)) {
				throw new HarnessException("Unable to locate folder in tree "
						+ locator);
			}
			page = new DialogTag(MyApplication,
					((AppAjaxClient) MyApplication).zPageBriefcase);
		} else if (button == Button.B_TREE_BRIEFCASE_EXPANDCOLLAPSE) {

			locator = null;
			page = null;

			// TODO: implement me

			// FALL THROUGH
		} else if(button== Button.O_NEW_BRIEFCASE){
			
			locator ="css=div[id='ztb__BDLV'] div[id='zb__BDLV__NEW_MENU'] td[id='zb__BDLV__NEW_MENU_dropdown'] >div";
				if (!this.sIsElementPresent(locator)) {
					throw new HarnessException("Unable to locate folder in tree "
							+ locator);
				}
			sMouseDown(locator);
		}else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Click it
		this.zClick(locator);

		// If the app is busy, wait for that to finish
		zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if (page != null) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();
		}
		return (page);
	}

	public List<TagItem> zListGetTags() throws HarnessException {

		List<TagItem> items = new ArrayList<TagItem>();

		// TODO: implement me!

		// Return the list of items
		return (items);
	}

	  /**
    * Expand all the folders tree
    * @return
    * @throws HarnessException
    */
   public void zExpandAll() throws HarnessException {
        // Browse all inventory in case of multiple accounts situation
      int i = 1;
      String locator = null;
      String expandCollapseLocator = null;
      String expandCollapseLocalFoldersLocator = null;
      for (i = 1; i < 100; i++) {
         locator = Locators.multipleTrees.replace("<NUM>",
               Integer.toString(i));
         if (!sIsElementPresent(locator)) {
            break;
         } else {
            expandCollapseLocator = Locators.multipleTreesExpandCollapseButton.replace("<NUM>",
                  Integer.toString(i));
            expandCollapseLocator = expandCollapseLocator.replace(
                  "ImgNode", "ImgNodeCollapsed");
            expandCollapseLocalFoldersLocator = Locators.multipleLocalFolderTreesExpandCollapseButton.replace("<NUM>",
                  Integer.toString(i));
            expandCollapseLocalFoldersLocator = expandCollapseLocalFoldersLocator.replace(
                  "ImgNode", "ImgNodeCollapsed");
            while (sIsElementPresent(expandCollapseLocator)) {
               sMouseDownAt(expandCollapseLocator, "0,0");
            }

            while (sIsElementPresent(expandCollapseLocalFoldersLocator)) {
               sMouseDownAt(expandCollapseLocalFoldersLocator, "0,0");
            }
         }
      }

   }

   /**
    * Collapse all the folders tree
    * @return
    * @throws HarnessException
    */
   public void zCollapseAll() throws HarnessException {
      // Browse all inventory in case of multiple accounts situation
      int i = 1;
      String locator = null;
      String expandCollapseLocator = null;
      String expandCollapseLocalFoldersLocator = null;
      for (i = 1; i < 100; i++) {
         locator = Locators.multipleTrees.replace("<NUM>",
               Integer.toString(i));
         if (!sIsElementPresent(locator)) {
            break;
         } else {
            expandCollapseLocator = Locators.multipleTreesExpandCollapseButton.replace("<NUM>",
                  Integer.toString(i));
            expandCollapseLocator = expandCollapseLocator.replace(
                  "ImgNode", "ImgNodeExpanded");
            expandCollapseLocalFoldersLocator = Locators.multipleLocalFolderTreesExpandCollapseButton.replace("<NUM>",
                  Integer.toString(i));
            expandCollapseLocalFoldersLocator = expandCollapseLocalFoldersLocator.replace(
                  "ImgNode", "ImgNodeExpanded");

            while (sIsElementPresent(expandCollapseLocator)) {
               sMouseDownAt(expandCollapseLocator, "0,0");
            }

            while (sIsElementPresent(expandCollapseLocalFoldersLocator)) {
               sMouseDownAt(expandCollapseLocalFoldersLocator, "0,0");
            }
         }
      }

   }

   public void zExpandFolders() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public boolean zIsFoldersExpanded() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public void zExpandTags() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public boolean zIsTagsExpanded() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.ui.AbsTree#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the briefcase page is active
		if (!((AppAjaxClient) MyApplication).zPageBriefcase.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageBriefcase.zNavigateTo();
		}

		boolean loaded = this.sIsElementPresent(Locators.briefcaseListView);
		if (!loaded)
			return (false);

		return (loaded);
	}
}
