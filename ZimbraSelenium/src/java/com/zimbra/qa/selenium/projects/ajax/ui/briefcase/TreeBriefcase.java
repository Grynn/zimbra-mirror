/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import java.util.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

/**
 * @author zimbra
 * 
 */

public class TreeBriefcase extends AbsTree {

	public static class Locators {
		public static final String briefcaseListView = "css=[id='zl__BDLV__rows']";
		public static final String briefcaseTreeView = "css=[id*=zti__main_Briefcase__";
		public static final String briefcaseTreeView_Desktop = "css=td[id*='main_Briefcase']";
	}

	public TreeBriefcase(AbsApplication application) {
		super(application);
		logger.info("new " + TreeBriefcase.class.getCanonicalName());
	}

	protected AbsPage zTreeItem(Action action, Button option, FolderItem folder)
			throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public AbsPage zTreeItem(Action action, IItem item, boolean isRowAdded)
	throws HarnessException {

		AbsPage page = zTreeItem(action, item);

		String treeItemLocator = null;
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
		   treeItemLocator = Locators.briefcaseTreeView_Desktop
               + "[class='DwtTreeItem-Text']";
		   ((AppAjaxClient) MyApplication).zPageMail.zNavigateTo();
		   ((AppAjaxClient) MyApplication).zPageBriefcase.zNavigateTo();
		} else {
		   treeItemLocator = Locators.briefcaseTreeView
		         + "][class='DwtTreeItem-selected']";
		}

		zWaitForElementPresent(treeItemLocator);

		String listItemLocator = "css=[id='zl__BDLV__rows']";

		if (isRowAdded)
			listItemLocator += " div[class^='Row']";

		zWaitForElementPresent(listItemLocator);

		return page;
	}

	protected AbsPage zTreeItem(Action action, FolderItem folder)
			throws HarnessException {
		AbsPage page = null;
		String locator = null;
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
		   locator = Locators.briefcaseTreeView_Desktop +
		         "[id*='" + MyApplication.zGetActiveAccount().EmailAddress + "']"
		         + "[id$='" + folder.getId() + "_imageCell']";
		} else {
		   locator = Locators.briefcaseTreeView + folder.getId()		   
            + "_imageCell]";
		}

		if (action == Action.A_LEFTCLICK) {

			zWaitForBusyOverlay();

			//ClientSessionFactory.session().selenium().clickAt(locator,"0,0");

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

		if (button == null)
			throw new HarnessException("Button cannot be null");

		AbsPage page = null;
		String locator = null;

		if (button == Button.B_TREE_NEWBRIEFCASE) {

			locator = "id=overviewHeader-Text FakeAnchor";
			page = new DialogCreateBriefcaseFolder(MyApplication);

			if (!this.sIsElementPresent(locator)) {
				throw new HarnessException(
						"Unable to locate folder in the tree " + locator);
			}

			this.zClick(locator);

			zWaitForBusyOverlay();

			return page;

			// FALL THROUGH
		} else if (button == Button.B_TREE_BRIEFCASE_EXPANDCOLLAPSE) {

			locator = null;
			page = null;

			// TODO: implement me

			// FALL THROUGH
		} else {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see framework.ui.AbsTree#zTreeItem(framework.ui.Action,
	 * framework.items.FolderItem)
	 */
	public AbsPage zTreeItem(Action action, IItem briefcaseFolder)
			throws HarnessException {

		// Validate the arguments
		if ((action == null) || (briefcaseFolder == null)) {
			throw new HarnessException(
					"Must define an action and briefcaseFolder");
		}

		if (briefcaseFolder instanceof FolderItem) {
			return (zTreeItem(action, (FolderItem) briefcaseFolder));
		} else
			throw new HarnessException(
					"Must use FolderItem as argument, but was "
							+ briefcaseFolder.getClass());
	}

	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem briefcaseFolder)
			throws HarnessException {
		// Validate the arguments
		if ((action == null) || (option == null) || (briefcaseFolder == null)) {
			throw new HarnessException(
					"Must define an action, option, and addressbook");
		}

		if (briefcaseFolder instanceof FolderItem) {
			return (zTreeItem(action, option, (FolderItem) briefcaseFolder));
		} else
			throw new HarnessException(
					"Must use FolderItem as argument, but was "
							+ briefcaseFolder.getClass());
	}

	public List<TagItem> zListGetTags() throws HarnessException {

		List<TagItem> items = new ArrayList<TagItem>();

		// TODO: implement me!

		// Return the list of items
		return (items);
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
