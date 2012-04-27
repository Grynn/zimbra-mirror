package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;




/**
 * The <code>FormRecoverDeletedItems<code> object defines an "Recover Deleted Items" dialog
 * <p>
 * 
 * @author Matt Rhoades
 * @see http://wiki.zimbra.com/wiki/File:ZimbraSeleniumScreenshotAjaxMail7.JPG
 * 
 */
public class FormRecoverDeletedItems extends AbsForm {
	
	/**
	 * Defines Selenium locators for various objects in {@link FormRecoverDeletedItems}
	 */
	public static class Locators {
		
	}

	public static class Field {
	
		public static final Field Search = new Field("Search = new");
		
		
		private String field;
		private Field(String name) {
			field = name;
		}
		
		@Override
		public String toString() {
			return (field);
		}

	}
	
	
	/**
	 * @param application
	 */
	public FormRecoverDeletedItems(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormRecoverDeletedItems.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	

	@Override
	public void zSubmit() throws HarnessException {
		logger.info("FormMailNew.submit()");
		
		zToolbarPressButton(Button.B_OK);

		this.zWaitForBusyOverlay();

	}

	/**
	 * Press the toolbar button
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		tracer.trace("Click button "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
		// Fallthrough objects
		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_CLOSE ) {
			
			locator = "css=div[class='ZmDumpsterDialog'] td[id$='_button1_title']";
			page = null;
			
			// FALL THROUGH

		} else if ( button == Button.B_SEARCH ) {

			locator = "css=td#searchDumpsterButton_title";
			page = null;
			
			// FALL THROUGH

		} else if ( button == Button.B_RECOVER_TO ) {

			locator = "css=td#zb__dumpsterMail__MOVE_title";
			page = new DialogMove(this.MyApplication, ((AppAjaxClient)this.MyApplication).zPageMail);
			
			// FALL THROUGH

		} else if ( button == Button.B_DELETE ) {

			locator = "css=td#zb__dumpsterMail__DELETE_title";
			page = null;
			
			// FALL THROUGH

		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		// Make sure a locator was set
		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		
		// Default behavior, process the locator by clicking on it
		//
		if ( !this.sIsElementPresent(locator) ) 
			throw new HarnessException("locator was not present for button "+ button);

		// Click it
		this.zClick(locator);

		// if the app is busy, wait for it to become active again
		this.zWaitForBusyOverlay();
		
		if ( page != null ) {
			
			// Make sure the page becomes active
			page.zWaitForActive();
			
		}
		
		// Return the page, if specified
		return (page);

	}
	
	/**
	 * Press the toolbar pulldown and the menu option
	 * @param pulldown
	 * @param option
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown("+ pulldown +", "+ option +")");
		
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		throw new HarnessException("Dumpster recovery dialog does not have this functionality");
	}
	
	
	@Override
	public void zFill(IItem item) throws HarnessException {

		throw new HarnessException("No item associated with this dialog - use zFillField(Field.Search, 'value') instead");

	}

	/**
	 * Fill in the form field with the specified text
	 * @param field
	 * @param value
	 * @throws HarnessException
	 */
	public void zFillField(Field field, String value) throws HarnessException {
	
		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
		
		if ( field == Field.Search ) {
			
			locator = "css=td[class='DumpsterSearchInput'] input";
			
			// FALL THROUGH
			
		} else {
			throw new HarnessException("not implemented for field " + field);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator was null for field "+ field);
		}
		
		// Default behavior, enter value into locator field
		//
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Seems that the client can't handle filling out the new mail form too quickly
		// Click in the "To" fields, etc, to make sure the client is ready
		this.sFocus(locator);
		this.zClick(locator);
		this.zWaitForBusyOverlay();

		// Enter text
		this.sType(locator, value);
		
		this.zWaitForBusyOverlay();

	}
	
	/**
	 * Left click on an item in the dialog box
	 * @param action
	 * @param subject
	 * @return
	 * @throws HarnessException
	 */
	public AbsPage zListItem(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");

		tracer.trace(action +" on subject = "+ subject);

		AbsPage page = null;
		String listLocator = "css=div[id=zl__dumpsterMail__rows]";
		String rowLocator = listLocator + " div[id^=zli__dumpsterMail__]";
		String itemLocator = null;


		// Find the item locator
		//

		// TODO: how to handle both messages and conversations, maybe check the view first?
		if ( !this.sIsElementPresent(rowLocator) )
			throw new HarnessException("List View Rows is not present "+ rowLocator);

		// How many items are in the table?
		int count = this.sGetCssCount(rowLocator);
		logger.debug(myPageName() + " zListSelectItem: number of list items: "+ count);
				
		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			
			// Look for the subject
			String s = "";			

			// Subject - Fragment
			itemLocator = rowLocator + ":nth-child("+ i +") td[id*='__su']";
			if (sIsElementPresent(itemLocator)) {				
				s = this.sGetText(itemLocator).trim();
			}
			
			if ( s.contains(subject) ) {
				break; // found it
			}	
		
			itemLocator = null;
		}

		if ( itemLocator == null ){							
			throw new HarnessException("subject locator is not present " + itemLocator);
		}
		
		if ( action == Action.A_LEFTCLICK ) {

			// Left-Click on the item
			this.zClick(itemLocator);

			this.zWaitForBusyOverlay();

			// Return the displayed mail page object
			page = null;

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}


		if ( page != null ) {
			page.zWaitForActive();
		}

		// default return command
		return (page);

	}

	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
		
		String locator = "css=div[class='ZmDumpsterDialog']";
		
		if ( !this.sIsElementPresent(locator) )
			return (false);
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) )
			return (false);
		
		return (true);
		
	}
	

}
