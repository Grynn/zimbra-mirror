package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

public class DialogConfirmModification extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "SEND_UPDATES_DIALOG";
	public static class Locators {
	public static final String SaveAndSendUpdates = "css=div[class='DwtDialog WindowOuterContainer'] input[id$='_send']";
	public static final String DontSaveAndKeepOpen = "css=div[class='DwtDialog WindowOuterContainer'] input[id$='_cancel']";
	public static final String DiscardAndClose = "css= div[class='DwtDialog WindowOuterContainer'] input[id$='_discard']";
	public static final String Ok_changes = "css=td[id='CHNG_DLG_ORG_1_button2_title']";
	public static final String Cancel_changes =  "css=td[id='CHNG_DLG_ORG_1_button1_title']";
	}
	public DialogConfirmModification(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogConfirmModification.class.getCanonicalName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);
		if ( button == null )
			throw new HarnessException("button cannot be null");
	
		String locator = null;
		AbsPage page = null;
		boolean waitForPostfix = false;

		if (button == Button.B_SAVE_SEND_UPDATES) {

			locator = Locators.SaveAndSendUpdates;
			page = null;

		} else if (button == Button.B_DONTSAVE_KEEP_OPEN) {

			locator = Locators.DontSaveAndKeepOpen;
			page = null;
		
		} else if (button == Button.B_DISCARD_CLOSE) {

			locator = Locators.DiscardAndClose;
			page = null;
		
		} else if (button == Button.B_OK) {
			locator = Locators.Ok_changes;
			page = null;

		} else if (button == Button.B_CANCEL) {

			locator =Locators.Cancel_changes;
			page = null;
			                              
		} else {
			
			return ( super.zClickButton(button) );

		}
		
		// Make sure the locator was set
		if (locator == null) {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not present!");
		}
		this.sFocus(locator);
		this.sClickAt(locator, "");
		this.zWaitForBusyOverlay();
		// If page was specified, make sure it is active
		if ( page != null ) {
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		// This dialog could send messages, so wait for the queue
		if ( waitForPostfix ) {
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		}

		return (page);
	}

}

