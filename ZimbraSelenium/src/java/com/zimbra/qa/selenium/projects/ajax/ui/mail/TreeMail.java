/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

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
public class TreeMail extends AbsTree {
   public final static String stringToReplace = "<TREE_ITEM_NAME>";
	public static class Locators {
	   // For desktop, Bug 56273:
	   public final static String zTreeItems = new StringBuffer("//td[text()='").
            append(stringToReplace).append("']").toString();

	   public static final String ztih__main_Mail__ZIMLET_ID = "ztih__main_Mail__ZIMLET";
		public static final String ztih__main_Mail__ZIMLET_nodeCell_ID = "ztih__main_Mail__ZIMLET_nodeCell";
		public static final String ztih_main_Mail__FOLDER_ITEM_ID = new StringBuffer("ztih__main_Mail__").
		      append(stringToReplace).append("_textCell").toString();
	}
	
		
	
	public TreeMail(AbsApplication application) {
		super(application);
		logger.info("new " + TreeMail.class.getCanonicalName());
	}

	protected AbsPage zTreeItem(Action action, Button option, FolderItem folder) throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	protected AbsPage zTreeItem(Action action, Button option, SavedSearchFolderItem savedSearchFolder) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	protected AbsPage zTreeItem(Action action, Button option, ZimletItem zimlet) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	protected AbsPage zTreeItem(Action action, FolderItem folder) throws HarnessException {
		AbsPage page = null;
		String locator = null;
		
		if ( action == Action.A_LEFTCLICK ) {
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			   locator = new StringBuffer("css=td[id^='zti__").
			         append(MyApplication.zGetActiveAccount().EmailAddress).
			         append(":main_Mail__']").append("[id$='").
			         append(folder.getId()).append("_textCell']").toString();
			} else {
			   locator = "id=zti__main_Mail__"+ folder.getId() +"_textCell";
			}
			
			// FALL THROUGH

		} else if ( action == Action.A_RIGHTCLICK ) {
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			   locator = new StringBuffer("css=td[id^='zti__").
                  append(MyApplication.zGetActiveAccount().EmailAddress).
                  append(":main_Mail__']").append("[id$='").
                  append(folder.getId()).append("_textCell']").toString();
			} else {
			   locator = "id=zti__main_Mail__"+ folder.getId() +"_textCell";
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
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Unable to locator folder in tree "+ locator);
		
		
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
	

	protected AbsPage zTreeItem(Action action, SavedSearchFolderItem savedSearch) throws HarnessException {
		AbsPage page = null;
		String locator = null;
	
		// TODO: implement me!
		
		if ( locator == null )
			throw new HarnessException("locator is null for action "+ action);
				
		
		// Default behavior.  Click the locator
		zClick(locator);
		
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

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsTree#zPressButton(com.zimbra.qa.selenium.framework.ui.Button)
	 */
	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		
		tracer.trace("Click "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null");
			
		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_TREE_NEWFOLDER ) {
			
			page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageMail);
			
			locator = "id=overviewHeader-Text FakeAnchor";
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
		
		tracer.trace("Click "+ action +" on folder "+ folder.getName());

		// Validate the arguments
		if ( (action == null) || (folder == null) ) {
			throw new HarnessException("Must define an action and addressbook");
		}
		
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
		}
		
		throw new HarnessException("Must use FolderItem or SavedSearchFolderItem or ZimletItem as argument, but was "+ folder.getClass());
	}


	
	public List<SavedSearchFolderItem> zListGetSavedSearches() throws HarnessException {
		
		List<SavedSearchFolderItem> items = new ArrayList<SavedSearchFolderItem>();
		
		// TODO: implement me!
		
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
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(Locators.ztih__main_Mail__ZIMLET_ID) )
			throw new HarnessException("Zimlet Tree is not present "+ Locators.ztih__main_Mail__ZIMLET_ID);
		
		// Zimlet's div ID seems to start with -999
		for (int zimletNum = -999; zimletNum < 0; zimletNum++ ) {
			
			String zimletLocator = "zti__main_Mail__"+ zimletNum +"_z_div";
			String locator;

			if ( !this.sIsElementPresent(zimletLocator) ) {
				// No more items to parse
				return (items);
			}
			
			// Parse this div element into a ZimletItem object

			ZimletItem item = new ZimletItem();
			
			// Get the image
			locator = "xpath=(//*[@id='zti__main_Mail__"+ zimletNum +"_z_imageCell']/div)@class";
			item.setFolderTreeImage(this.sGetAttribute(locator));

			
			// Get the display name
			locator = "zti__main_Mail__"+ zimletNum +"_z_textCell";
			item.setFolderTreeName(this.sGetText(locator));
						
			// Set the locator
			item.setFolderTreeLocator(zimletLocator);
			
			// Add this item to the list
			items.add(item);
			
		}
		
		// If we get here, there were over 1000 zimlets or something went wrong
		throw new HarnessException("Too many zimlets!");
		
	}

	public void zExpandFolders() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public boolean zIsFoldersExpanded() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public void zExpandSavedSearchFolders() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public boolean zIsSavedSearchFoldersExpanded() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public void zExpandTags() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public boolean zIsTagsExpanded() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public void zExpandZimlets() throws HarnessException {
		
		if ( zIsZimletsExpanded() ) {
			return; // Nothing more to do.  Already expanded
		}
		
		// Click on the arrow
		String locator = "css=td[id="+ Locators.ztih__main_Mail__ZIMLET_nodeCell_ID +"] div";
		this.zClick(locator);
		
		this.zWaitForBusyOverlay();

	}
	
	public boolean zIsZimletsExpanded() throws HarnessException {
		// Image is either ImgNodeExpanded or ImgNodeCollapsed
		String locator = "xpath=(//td[@id='"+ Locators.ztih__main_Mail__ZIMLET_nodeCell_ID +"']/div)@class";
		String image = this.sGetAttribute(locator);
		return ( image.equals("ImgNodeExpanded") );
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
		String locator = Locators.ztih__main_Mail__ZIMLET_ID;
		
		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);
		
		return (loaded);

	}


}
