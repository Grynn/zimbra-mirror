/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;


/**
 * @author Matt Rhoades
 *
 */
public class TreeTasks extends AbsTree {

	public static class Locators {
		public static final String ztih__main_Tasks__ZIMLET_ID = "ztih__main_Tasks__ZIMLET";
		public static final String ztih__main_Mail__ZIMLET_nodeCell_ID = "ztih__main_Mail__ZIMLET_nodeCell";
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
		
		if ( button == null )
			throw new HarnessException("Button cannot be null");
			
		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_TREE_NEWFOLDER ) {
			
			locator = "id=overviewHeader-Text FakeAnchor";
			page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageMail);
			
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Unable to locator folder in tree "+ locator);
			}

			this.zClick(locator);
			
			this.zWaitForBusyOverlay();
			
			// No result page is returned in this case ... use app.zPageMail
			page = null;
			
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

	/* (non-Javadoc)
	 * @see framework.ui.AbsTree#zTreeItem(framework.ui.Action, framework.items.FolderItem)
	 */
	public AbsPage zTreeItem(Action action, IItem folder) throws HarnessException {
		
		AbsPage page = null;
		String locator = null;
		
		if ( !(folder instanceof FolderItem) )
			throw new HarnessException("folder must be of type FolderItem");
		
		FolderItem f = (FolderItem) folder;
		
		if ( action == Action.A_LEFTCLICK ) {
			
			locator = "zti__main_Tasks__"+ f.getId() +"_textCell";
			
			// FALL THROUGH

		} else if ( action == Action.A_RIGHTCLICK ) {
			
			locator = "zti__main_Tasks__"+ f.getId() +"_textCell";

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
	
		return (page);

	}
		
	@Override
	public AbsPage zTreeItem(Action action, Button option, IItem folder) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ option +", "+ folder +")");
		
		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("button cannot be null");
		if ( folder == null )
			throw new HarnessException("folder cannot be null");

		AbsPage page = null;
		String actionLocator = null;
		String optionLocator = null;
		String itemLocator = null;
		
		if ( !(folder instanceof FolderItem) )
			throw new HarnessException("folder must be of type FolderItem");
		
		FolderItem f = (FolderItem) folder;
		logger.debug("processing "+ f.getName());

		if ( action == Action.A_LEFTCLICK ) {
			actionLocator = "implement me";
		} else if ( action == Action.A_RIGHTCLICK ) {
			actionLocator = "implement me";
		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}

		if ( actionLocator == null )
			throw new HarnessException("locator is null for action "+ action);
		if ( optionLocator == null )
			throw new HarnessException("locator is null for option "+ option);
		if ( itemLocator == null )
			throw new HarnessException("locator is null for item "+ folder);
		
		
		// Default behavior.  Click the locator
		zClick(actionLocator);

		// If there is a busy overlay, wait for that to finish
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			
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
