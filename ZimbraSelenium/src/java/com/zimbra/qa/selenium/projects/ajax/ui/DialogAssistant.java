package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;

/**
 * A <code>DialogError</code> object represents a "Error" dialog, such as "Permission 
 * denied", etc.
 * <p>
 * During construction, the div ID attribute must be specified, such as "Zimbra".
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogAssistant extends AbsDialog {

	public static class Locators {
		
		public static final String Locator_Assistant_DIV_css = "css=div[class='ZmAssistantDialog']";
		
	}
	
	public DialogAssistant(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {

		if ( button == null )
			throw new HarnessException("button cannot be null");
		
		String locator = null;
		AbsPage page = null; 		// Does this ever result in a page being returned?
		boolean postqueue = false;	// Does this event result in a postfix queue check
		
		// See http://bugzilla.zimbra.com/show_bug.cgi?id=54560
		// Need unique id's for the buttons
		String buttonsTableLocator = Locators.Locator_Assistant_DIV_css + " div[id$='_buttons']";
		
		if ( button == Button.B_HELP ) {
			
			locator = buttonsTableLocator + " td[id^='Help_'] td[id$='_title']";
			page = null; // TODO
			
			// FALL THROUGH

		} else if ( button == Button.B_OK ) {
			
			locator = buttonsTableLocator + " td[id^='OK_'] td[id$='_title']";
			page = null; // TODO
			postqueue = true; // The Assistant could send a message, so check the queue
			
			// FALL THROUGH

		} else if ( button == Button.B_CANCEL ) {			
			
			locator = buttonsTableLocator + " td[id^='Cancel_'] td[id$='_title']";
			page = null; // TODO
			
			// FALL THROUGH

		}else if ( button == Button.B_MORE_DETAILS ) {			
			
			locator = buttonsTableLocator + " td[id^='More Details'] td[id$='_title']";
			page = null; // TODO
			
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
		zClickAt(locator,"0,0");
		
		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();
		
		// If page was specified, make sure it is active
		if ( page != null ) {
			
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}
		
		if ( postqueue ) {
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		}

		return (page);

	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		throw new HarnessException("impelment me!");
	}

	public void zEnterCommand(String command) throws HarnessException {
		
		if ( (command == null) || (command.trim().length() == 0) ) {
			throw new HarnessException("command cannot be null or empty");
		}

		String locator = Locators.Locator_Assistant_DIV_css + " div[id$='_content'] textarea";
		
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Unable to locate command area");
		
		this.sFocus(locator);
		this.zClick(locator);
		this.zKeyboard.zTypeCharacters(command); // Need to use keyboard for some reason, to activate the OK button
		this.zWaitForBusyOverlay();

		return;

		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		
		boolean present = this.sIsElementPresent(Locators.Locator_Assistant_DIV_css);
		if ( !present ) {
			logger.info("Zimbra Assistant is not present");
			return (false);
		}
		
		boolean visible = this.zIsVisiblePerPosition(Locators.Locator_Assistant_DIV_css, 0, 0);
		if ( !visible ) {
			logger.info("Zimbra Assistant is not visible");
			return (false);
		}
		if ( this.sIsElementPresent(Locators.Locator_Assistant_DIV_css) ) {
			
		}

		return (true);
	}

}
