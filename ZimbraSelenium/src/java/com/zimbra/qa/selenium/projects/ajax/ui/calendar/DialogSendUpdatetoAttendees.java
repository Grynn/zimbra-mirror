package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.calendar.FormApptNew.Locators;

public class DialogSendUpdatetoAttendees extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "SEND_UPDATES_DIALOG";
		
	public DialogSendUpdatetoAttendees(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogSendUpdatetoAttendees.class.getCanonicalName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");
		
		tracer.trace("Click dialog button " + button);
		if ( button == null )
			throw new HarnessException("button cannot be null");
	
		String locator = null;
		AbsPage page = null;

		if (button == Button.B_SEND_UPDATES_ONLY_TO_ADDED_OR_REMOVED_ATTENDEES) {

			locator = Locators.SendUpdatesToAddedRemovedRadioButton;
			page = null;

		} else if (button == Button.B_SEND_UPDATES_TO_ALL_ATTENDEES) {

			locator = Locators.SendUpdatesToAllRadioButton;
			page = null;
		
		} else if (button == Button.B_OK) {

			locator = "css=div[id='SEND_NOTIFY_DIALOG'][class='DwtDialog'] div[id='SEND_NOTIFY_DIALOG_buttons'] td[id='SEND_NOTIFY_DIALOG_button2_title']";
			page = null;
		
		} else if (button == Button.B_CANCEL) {

			locator = "css=div[id='SEND_NOTIFY_DIALOG'][class='DwtDialog'] div[id='SEND_NOTIFY_DIALOG_buttons'] td[id='SEND_NOTIFY_DIALOG_button1_title']";
			page = null;
			                              
		} else {
			
			return ( super.zClickButton(button) );

		}
		
		// Make sure the locator was set
		if (locator == null) {
			throw new HarnessException("Button " + button + " not implemented");
		}

		SleepUtil.sleepMedium();
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not present!");
		}
		
		this.sClickAt(locator, "");
		this.zWaitForBusyOverlay();
		
		// If page was specified, make sure it is active
		if ( page != null ) {
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		// This dialog could send messages, so wait for the queue
		Stafpostqueue sp = new Stafpostqueue();
		sp.waitForPostqueue();

		return (page);
	}

}

