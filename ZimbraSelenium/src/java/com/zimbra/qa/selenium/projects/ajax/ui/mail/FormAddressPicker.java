package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;




/**
 * The <code>FormAddressPicker<code> object defines an addresspicker dialog
 * <p>
 * 
 * @author Matt Rhoades
 * @see http://wiki.zimbra.com/wiki/File:ZimbraSeleniumScreenshotAjaxMail6.JPG
 * 
 */
public class FormAddressPicker extends AbsForm {
	
	/**
	 * Defines Selenium locators for various objects in {@link FormAddressPicker}
	 */
	public static class Locators {
		public static final String ZmContactPickerLocatorCSS = "css=div[id='ZmContactPicker']";
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
	public FormAddressPicker(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormAddressPicker.class.getCanonicalName());

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
		
		if ( button == Button.B_OK ) {
			
			locator = "css=div[id='ZmContactPicker_buttons'] td[id^='OK_'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH

		} else if ( button == Button.B_CANCEL ) {

			locator = "css=div[id='ZmContactPicker_buttons'] td[id^='Cancel_'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH

		} else if ( button == Button.B_TO ) {

			locator = "css=div[id='ZmContactPicker'] div[id='DwtChooserButtonDiv_1'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH

		} else if ( button == Button.B_CC ) {

			locator = "css=div[id='ZmContactPicker'] div[id='DwtChooserButtonDiv_2'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH

		} else if ( button == Button.B_BCC ) {

			locator = "css=div[id='ZmContactPicker'] div[id='DwtChooserButtonDiv_3'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH

		} else if ( button == Button.B_REMOVE ) {

			locator = "css=div[id='DwtChooserRemoveButton_1'] td[id$='_title']";
			page = null;
			
			// FALL THROUGH
			
		} else if ( button == Button.B_SEARCH ) {

			locator = "css=td[id='ZmContactPicker_searchButton'] td[id$='_title']";
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

		if ( pulldown == null )
			throw new HarnessException("Pulldown cannot be null!");
		
		if ( option == null )
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( pulldown == Button.B_SHOW_NAMES_FROM ) {
			
			pulldownLocator = "css=td[id='ZmContactPicker_listSelect'] div[class='ImgSelectPullDownArrow']";

			if ( option == Button.O_CONTACTS ) {
				
	            optionLocator = "css=TODO#TODO";
				page = null;

				// FALL THROUGH

			} else if ( option == Button.O_PERSONAL_AND_SHARED_CONTACTS ) {
				
	            optionLocator = "css=TODO#TODO";
				page = null;

				// FALL THROUGH

			} else if ( option == Button.O_GLOBAL_ADDRESS_LIST ) {
				
	            optionLocator = "css=TODO#TODO";
				page = null;

				// FALL THROUGH

			} else {
				throw new HarnessException("unsupported priority option "+ option);
			}
		
		} else {
			throw new HarnessException("no logic defined for pulldown "+ pulldown);
		}

		// Default behavior
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !this.sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}
			
			this.zClick(pulldownLocator);

			this.zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
				
				this.zClick(optionLocator);

				this.zWaitForBusyOverlay();

			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
				page.zWaitForActive();
			}
			
		}
		
		// Return the specified page, or null if not set
		return (page);
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
			
			locator = "css=input[id='ZmContactPicker_searchField']";
			
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
		// this.zKeyboard.zTypeCharacters(value);
		// this.sType(locator, value);
		
		// June 29, 2012 - the first character is getting stolen.  Type a space first.
		this.zKeyboard.zTypeCharacters(" "+ value);
		
		this.zWaitForBusyOverlay();

	}
	
	
	@Override
	public void zFill(IItem item) throws HarnessException {

		throw new HarnessException("No item associated with this dialog - use zFillField(Field.Search, 'value') instead");

	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
		
		// https://bugzilla.zimbra.com/show_bug.cgi?id=62021

		String locator = Locators.ZmContactPickerLocatorCSS;
		
		if ( !this.sIsElementPresent(locator) )
			return (false);
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) )
			return (false);
		
		return (true);
		
	}
	

}
