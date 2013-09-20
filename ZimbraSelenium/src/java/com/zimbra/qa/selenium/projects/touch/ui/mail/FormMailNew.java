/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.projects.touch.ui.mail;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem.RecipientType;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.touch.ui.AutocompleteEntry;
import com.zimbra.qa.selenium.projects.touch.ui.AutocompleteEntry.Icon;

/**
 * The <code>FormMailNew<code> object defines a compose new message view
 * in the Zimbra Ajax client.
 * <p>
 * This class can be used to compose a new message.
 * <p>
 * 
 * @author Matt Rhoades
 * @see http://wiki.zimbra.com/wiki/Testing:_Selenium:_ZimbraSelenium_Overview#Mail_Page
 */
public class FormMailNew extends AbsForm {
	
	/**
	 * Defines Selenium locators for various objects in {@link FormMailNew}
	 */
	public static class Locators {
		
		public static final String zSendButton			= "css=div[class=x-body'] span[class='x-button-label']:contains('Send')";
		public static final String zCancelButton		= "css=div[class=x-body'] span[class='x-button-label']:contains('Cancel')";
		public static final String zSaveDraftButton		= "css=div[class=x-body'] span[class='x-button-label']:contains('Save Draft')";
				
		public static final String zToField				= "css=div[class='x-container x-layout-box-item x-sized'] div[class^='x-innerhtml'] input";
		public static final String zCcField				= "css=div[id='ext-contactfield-2'] div[class^='x-innerhtml'] input";
		public static final String zBccField			= "css=div[id='ext-contactfield-3'] div[class^='x-innerhtml'] input";
		public static final String zSubjectField		= "css=input[name=subject]";
		public static final String zBodyField			= "css=div[class$='zcs-fully-editable']";
		
		public static final String zYesWarningDialog	= "css=div[class^='x-dock x-dock-vertical x-unsized'] div[class^='x-button-normal'] span[class='x-button-label']:contains('Yes')";
		public static final String zNoWarningDialog		= "css=div[class^='x-dock x-dock-vertical x-unsized'] div[class^='x-button-normal'] span[class='x-button-label']:contains('No')";
		public static final String zCancelWarningDialog	= "css=div[class^='x-dock x-dock-vertical x-unsized'] div[class^='x-button-normal'] span[class='x-button-label']:contains('Cancel')";
		
	}

	public static class Field {
	
		public static final Field To = new Field("To");
		public static final Field Cc = new Field("Cc");
		public static final Field Bcc = new Field("Bcc");
		public static final Field From = new Field("From");
		public static final Field Subject = new Field("Subject");
		public static final Field Body = new Field("Body");
		
		
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
	 * Protected constuctor for this object.  Only classes within
	 * this package should create DisplayMail objects.
	 * 
	 * @param application
	 */
	public FormMailNew(AbsApplication application) {
		super(application);
		
		logger.info("new " + FormMailNew.class.getCanonicalName());

	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}
	

	@Override
	public void zSubmit() throws HarnessException {
		logger.info("FormMailNew.submit()");
		
		zToolbarPressButton(Button.B_SEND);

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
		
		if ( button == Button.B_SEND ) {
			
			locator = Locators.zSendButton;
			page = this;
		
		} else if ( button == Button.B_CANCEL ) {

			locator = Locators.zCancelButton;
			page = this;
			
		} else if ( button == Button.B_SAVE_DRAFT ) {

			locator = Locators.zSaveDraftButton;
			page = this;
			
		} else if ( button == Button.B_SHOWCC || button == Button.B_SHOWBCC) {

			locator = "css=div[class^='x-innerhtml']:contains('Show Cc/Bcc')";
			page = this;

			if ( zCcBccIsActive() )
				return (this);
			
		}
		else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		this.sClickAt(locator, "");
		SleepUtil.sleepMedium();
		this.zWaitForBusyOverlay();
		
		// Wait for the message to be delivered
		Stafpostqueue sp = new Stafpostqueue();
		sp.waitForPostqueue();
		
		return (page);

	}
	
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		tracer.trace("Click button "+ button);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
		// Fallthrough objects
		AbsPage page = null;
		String locator = null;
		
		if ( button == Button.B_YES ) {
			
			locator = Locators.zYesWarningDialog;
			page = this;
			
		} else if ( button == Button.B_NO ) {
			
			locator = Locators.zNoWarningDialog;
			page = this;
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = Locators.zCancelWarningDialog;
			page = this;
			
		}
		else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		this.sClickAt(locator, "");
		SleepUtil.sleepMedium();
		this.zWaitForBusyOverlay();
		
		// Wait for the message to be delivered
		Stafpostqueue sp = new Stafpostqueue();
		sp.waitForPostqueue();
		
		return (page);

	}
	
	/**
	 * Set the 'From' value
	 * @param value
	 */
	public void zSetFromIdentity(String value) throws HarnessException {
		logger.info(myPageName() + " zSetFrom("+ value +")");

		String pulldownLocator = "css=div[id^='zv__COMPOSE'] tr[id$='_identity_row'] td[id$='_dropdown']";
		String optionLocator = "css=td[id$='_title']:contains("+ value +")";
		
		// Default behavior
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !this.sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("pulldownLocator not present! "+ pulldownLocator);
			}
			
			this.zClick(pulldownLocator);

			this.zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("optionLocator not present! "+ optionLocator);
				}
				
				this.zClick(optionLocator);

				this.zWaitForBusyOverlay();

			}
			
		}
		
	}
	
	/**
	 * Fill in the form field with the specified text
	 * @param field
	 * @param value
	 * @throws HarnessException
	 */
	public void zFillField(Field field, String value) throws HarnessException {
			
		tracer.trace("Set "+ field +" to "+ value);
		
		SleepUtil.sleepMedium();

		String locator = null;
		
		if ( field == Field.To ) {
			
			locator = Locators.zToField;
			
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
			
			this.sClickAt(locator, "");
			this.sType(locator, value);
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_TAB);
			SleepUtil.sleepSmall();

			return;

		} else if ( field == Field.Cc ) {
			
			locator = Locators.zCcField;

			if ( !zCcBccIsActive() ) {
				this.zToolbarPressButton(Button.B_SHOWCC);
			}
			
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
			
			this.sClickAt(locator, "");
			this.sType(locator, value);
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_TAB);
			SleepUtil.sleepSmall();

			return;
						
		} else if ( field == Field.Bcc ) {
			
			locator = Locators.zBccField;
			
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
			
			this.sClickAt(locator, "");
			this.sType(locator, value);
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_TAB);
			SleepUtil.sleepSmall();

			return;
			
		} else if ( field == Field.From ) {
			
			zSetFromIdentity(value);
			return;
						
		} else if ( field == Field.Subject ) {
			
			locator = Locators.zSubjectField;
			this.sFocus(locator);
			this.sClickAt(locator, "");
			this.zKeyboard.zTypeKeyEvent(KeyEvent.VK_TAB);
			
		} else if (field == Field.Body) {
			
			locator = Locators.zBodyField;
			
			this.sClickAt(locator, "");
			this.sFocus(locator);
			this.zMouseClick(500, 500);
			
			SleepUtil.sleepSmall();
			this.zKeyboard.zTypeCharacters(value);
			
			if (!this.sIsElementPresent(locator))
				throw new HarnessException("Unable to locate compose body");
			return;
			
		} else {
			throw new HarnessException("not implemented for field " + field);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator was null for field "+ field);
		}
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		this.sFocus(locator);
		this.zClick(locator);
		this.sType(locator, value);

	}
	
	
	private boolean zCcBccIsActive() throws HarnessException {
		logger.info(myPageName() + ".zCcBccIsActive()");

		String locator;
		locator = "css=div[class^='x-innerhtml']:contains('Cc/Bcc')";
		
		if ( !sIsElementPresent(locator) )
			throw new HarnessException("Unable to locate the BCC field "+ locator);
		
		return (!sIsElementPresent(locator));
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		logger.info(myPageName() + ".zFill(ZimbraItem)");
		logger.info(item.prettyPrint());

		// Make sure the item is a MailItem
		if ( !(item instanceof MailItem) ) {
			throw new HarnessException("Invalid item type - must be MailItem");
		}
		
		// Convert object to MailItem
		MailItem mail = (MailItem) item;
		
		// Handle the subject
		if ( mail.dSubject != null ) {
			zFillField(Field.Subject, mail.dSubject);
		}
		
		if ( mail.dBodyText != null ) {
			zFillField(Field.Body, mail.dBodyText);
		}
		
		// Handle the Recipient list, which can be a combination of To, Cc, Bcc, and From
		StringBuilder to = null;
		StringBuilder cc = null;
		StringBuilder bcc = null;
		StringBuilder from = null;
		
		// Convert the list of recipients to a semicolon separated string
		List<RecipientItem> recipients = mail.dAllRecipients();
		if ( recipients != null ) {
			if ( !recipients.isEmpty() ) {
				
				for (RecipientItem r : recipients) {
					if ( r.dType == RecipientType.To ) {
						if ( to == null ) {
							to = new StringBuilder();
							to.append(r.dEmailAddress);
						} else {
							to.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.Cc ) {
						if ( cc == null ) {
							cc = new StringBuilder();
							cc.append(r.dEmailAddress);
						} else {
							cc.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.Bcc ) {
						if ( bcc == null ) {
							bcc = new StringBuilder();
							bcc.append(r.dEmailAddress);
						} else {
							bcc.append(";").append(r.dEmailAddress);
						}
					}
					if ( r.dType == RecipientType.From ) {
						if ( from == null ) {
							from = new StringBuilder();
							from.append(r.dEmailAddress);
						} else {
							from.append(";").append(r.dEmailAddress);
						}
					}
				}
				
			}
		}
		
		// Fill out the To field
		if ( to != null ) {
			this.zFillField(Field.To, to.toString());
		}
		
		if ( cc != null ) {
			this.zFillField(Field.Cc, cc.toString());
		}
		
		if ( bcc != null ) {
			this.zFillField(Field.Bcc, bcc.toString());
		}
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsActive()");
		
		// Look for the div
		String locator = Locators.zSendButton;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false);	
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);
		}
		
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}
	
	public boolean zHasAttachment() throws HarnessException {
	
		String locator = "css=span[id$='_attachments_div'] div.attBubbleContainer";
		
		boolean hasBubble = this.sIsElementPresent(locator);
		
		return (hasBubble);
		
	}
	
	public boolean zHasAttachment(String name)  throws HarnessException {
	    
	    // Is the bubble there?
		if ( !zHasAttachment() ) {
			return (false);
		}
		
		// Find the attachment name
		String locator = "css=span[id$='_attachments_div'] div.attBubbleContainer a.AttLink";
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false);
		}
		
		
		String filename = this.sGetText(locator);
		
		if ( filename == null || filename.trim().length() == 0 ) {
			return (false);
		}
	    
		return (filename.contains(name));
		
	}

	/**
	 * Autocompleting is more complicated than zFillField().  Use this
	 * method when filling out a field that will autocomplete.
	 * @param field The form field to use (to, cc, bcc, etc.)
	 * @param value The partial string to use to autocomplete
	 * @throws HarnessException 
	 */
	public List<AutocompleteEntry> zAutocompleteFillField(Field field, String value) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteFillField("+ field +", "+ value +")");

		tracer.trace("Set "+ field +" to "+ value);

		String locator = null;
		
		if ( field == Field.To ) {
			
			locator = Locators.zToField;
			
			// FALL THROUGH
			
		} else if ( field == Field.Cc ) {
			
			locator = Locators.zCcField;
			
			if ( !zCcBccIsActive() ) {
				this.zToolbarPressButton(Button.B_SHOWCC);
			}
			
		} else if ( field == Field.Bcc ) {
			
			locator = Locators.zBccField;
			
			if ( !zCcBccIsActive() ) {
				this.zToolbarPressButton(Button.B_SHOWBCC);
			}
			
			// FALL THROUGH
			
		} else {
			throw new HarnessException("Unsupported field: "+ field);
		}
		
		if ( locator == null ) {
			throw new HarnessException("locator was null for field "+ field);
		}
		
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Field is not present field="+ field +" locator="+ locator);
		
		// Seems that the client can't handle filling out the new mail form too quickly
		// Click in the "To" fields, etc, to make sure the client is ready
		this.sFocus(locator);
		this.sClickAt(locator,"");
		this.zWaitForBusyOverlay();

		// Instead of sType() use zKeyboard
		// this.zKeyboard.zTypeCharacters(value);
		
		// workaround
		if(ZimbraSeleniumProperties.isWebDriver()){
		    clearField(locator);
		    sType(locator, value);
		}else{
		    if(value.length() > 0){
			sType(locator, value.substring(0, value.length()-1));
			sFireEvent(locator, "keyup");
		    }
		    zWaitForBusyOverlay();
		    sType(locator, value);
		    sFireEvent(locator, "keyup");
		}
				
		this.zWaitForBusyOverlay();

		waitForAutocomplete();
		
		return (zAutocompleteListGetEntries());
		
	}

	/**
	 * Wait for the autocomplete spinner to go away
	 */
	protected void waitForAutocomplete() throws HarnessException {
		String locator = "css=div[class='acWaiting'][style*='display: none;']";
		for (int i = 0; i < 30; i++) {
			if ( this.sIsElementPresent(locator) )
				return; // Found it!
			SleepUtil.sleep(1000);
		}
		throw new HarnessException("autocomplete never completed");
	}
	
	protected AutocompleteEntry parseAutocompleteEntry(String itemLocator) throws HarnessException {
		logger.info(myPageName() + " parseAutocompleteEntry()");

		String locator = null;
		
		// Get the icon
		locator = itemLocator + " td.Icon div@class";
		String image = this.sGetAttribute(locator);
		
		// Get the address
		locator = itemLocator + " td + td";
		String address = this.sGetText(locator);
		
		AutocompleteEntry entry = new AutocompleteEntry(
									Icon.getIconFromImage(image), 
									address, 
									false,
									itemLocator);

		return (entry);
	}
	
	public List<AutocompleteEntry> zAutocompleteListGetEntries() throws HarnessException {
		logger.info(myPageName() + " zAutocompleteListGetEntries()");
		
		List<AutocompleteEntry> items = new ArrayList<AutocompleteEntry>();
		
		String containerLocator = "css=div[id^='zac__COMPOSE-'][style*='display: block;']";

		if ( !this.zWaitForElementPresent(containerLocator,"5000") ) {
			// Autocomplete is not visible, return an empty list.
			return (items);
		}

		
		String rowsLocator = containerLocator + " tr[id*='_acRow_']";
		int count = this.sGetCssCount(rowsLocator);
		for (int i = 0; i < count; i++) {
			
			items.add(parseAutocompleteEntry(containerLocator + " tr[id$='_acRow_"+ i +"']"));
			
		}
		
		return (items);
	}

	public void zAutocompleteSelectItem(AutocompleteEntry entry) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteSelectItem("+ entry +")");
		
		// Click on the address
		this.sMouseDown(entry.getLocator() + " td + td");
		this.zWaitForBusyOverlay();
		
	}
	
	public void zAutocompleteForgetItem(AutocompleteEntry entry) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteForgetItem("+ entry +")");
		
		// Mouse over the entry
		zAutocompleteMouseOverItem(entry);
		
		// Click on the address
		this.sMouseDown(entry.getLocator() + " div[id*='_acForgetText_']");
		this.zWaitForBusyOverlay();
		
	}

	public void zAutocompleteMouseOverItem(AutocompleteEntry entry) throws HarnessException {
		logger.info(myPageName() + " zAutocompleteMouseOverItem("+ entry +")");
		
		// Click on the address
		this.sMouseOver(entry.getLocator() + " td + td");
		this.zWaitForBusyOverlay();
		
	}

	
}
