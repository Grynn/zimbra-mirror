/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;


import java.util.ArrayList;
import java.util.List;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogShareFind extends AbsDialog {

	public static class Locators {
		
		public static final String zDialogLocator		= "css=div[class*='ZmShareSearchDialog']";
		
		public static final String zEmailInputLocator	= zDialogLocator + " input[id$='_EMAIL_input']";
		
		public static final String zButtonSearchLocator	= zDialogLocator + " td[id$='_SEARCH_title']";
		public static final String zButtonAddLocator	= zDialogLocator + " td[id$='_button2_title']";
		public static final String zButtonCancelLocator	= zDialogLocator + " td[id$='_button1_title']";

	}
	
	
	public DialogShareFind(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}
	
	
	
	
	public static class ShareIncludeType {
		public static ShareIncludeType All			= new ShareIncludeType("All");
		public static ShareIncludeType Mail			= new ShareIncludeType("Mail");
		public static ShareIncludeType Addressbook	= new ShareIncludeType("Addressbook");
		public static ShareIncludeType Calendar		= new ShareIncludeType("Calendar");
		public static ShareIncludeType Tasks		= new ShareIncludeType("Tasks");
		public static ShareIncludeType Briefcase	= new ShareIncludeType("Briefcase");
		
		protected String ID;
		protected ShareIncludeType(String id) {
			ID = id;
		}
		
		public String toString() {
			return (ID);
		}
	}
	
	public void zSetIncludeType(ShareIncludeType type) throws HarnessException {
		logger.info(myPageName() + " zSetIncludeType("+ type +")");

		throw new HarnessException("implement me!");

	}
	
	/**
	 * "Enter text to filter results"
	 * @param filter
	 * @throws HarnessException
	 */
	public void zSetFilter(String filter) throws HarnessException {
		logger.info(myPageName() + " zSetFilter("+ filter +")");

		/**
		 * <div id="DWT257_FILTER" style="position: static; overflow: visible;" class="DwtInputField-hint" parentid="DWT257">
		 * 	<input type="text" autocomplete="off" title="" id="DWT257_FILTER_input">
		 * </div>
		 * 
		 */
		
		throw new HarnessException("implement me!");
	}
	
	/**
	 * Find shares: email
	 * @param email
	 * @throws HarnessException
	 */
	public void zSetFindEmail(String email) throws HarnessException {
		logger.info(myPageName() + " zSetFindEmail("+ email +")");

		String locator = Locators.zEmailInputLocator;
		
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Locator "+ locator +" not present");
		}
		
		// To activate the Search button, need to focus/click
		this.sFocus(locator);
		this.zClick(locator);
		this.zKeyboard.zTypeCharacters(email);
		if (!(sGetValue(locator).equalsIgnoreCase(email))) {
			this.sType(locator, email);
		}
		
		this.zWaitForBusyOverlay();

	}
	
	
	/**
	 * Get the list of displayed folders
	 * @return
	 * @throws HarnessException 
	 */
	public List<FolderItem> zListGetFolders() throws HarnessException {
		logger.info(myPageName() + " zListGetFolders()");
		
		List<FolderItem> items = new ArrayList<FolderItem>();
		
		items.addAll(this.zListGetFolders("//div[contains(@class,'ZmShareSearchDialog')"));

		return (items);

	}
			
	/**
	 * Used for recursively building the tree list for Mail Folders
	 * @param top
	 * @return
	 * @throws HarnessException
	 */
	private List<FolderItem>zListGetFolders(String top) throws HarnessException {

		// See example in "TreeMail.zListGetFolder(string top)"
		// See http://bugzilla.zimbra.com/show_bug.cgi?id=62470
		
		throw new HarnessException("implement me: http://bugzilla.zimbra.com/show_bug.cgi?id=62470");


	}

	/**
	 * Take an action on an item in the tree list (e.g. ACTION.A_TREE_CHECKBOX)
	 * @param Action
	 * @param FolderItem
	 * @throws HarnessException 
	 */
	public AbsPage zTreeItem(Action action, FolderItem folder) throws HarnessException {
		AbsPage page = null;
		String locator = null;

		if ( action == Action.A_TREE_CHECKBOX ) {
			
			locator = "TODO#TODO";

			// FALL THROUGH

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


	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		AbsPage page = null;
		
		if ( button == Button.B_ADD ) {
			
			locator = Locators.zButtonAddLocator;
			
		} else if ( button == Button.B_CANCEL ) {
				
			locator = Locators.zButtonCancelLocator;
				
		} else if ( button == Button.B_SEARCH ) {
			
			locator = Locators.zButtonSearchLocator;

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Make sure the locator is present
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Locator "+ locator +" not present");
		}
		
		this.zClick(locator);
		
		zWaitForBusyOverlay();

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
		throw new HarnessException("implement me");
		
	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zDialogLocator;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}



}
