package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

public class DialogFindEquipment extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "SEND_UPDATES_DIALOG";
		
	public DialogFindEquipment(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogFindEquipment.class.getCanonicalName());
	}
	public static class Locators {
		public static final String EquipmentPickerSerach="css=div[class='DwtDialog'] td[id$='_title']:contains('Search')";
		public static final String SelectEquipmentFromPicker="css=div[class='DwtDialog'] td[id$='DwtChooserButton_1_title']:contains('Add')";
		public static final String AddEquipmentFromPicker="css=div[class='DwtDialog']  td[id^='OK'] td[id$='_button2_title']";
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

		if (button == Button.B_SEARCH_EQUIPMENT) {

			locator = Locators.EquipmentPickerSerach;
			page = null;

		} else if (button == Button.B_SELECT_EQUIPMENT) {

			locator = Locators.SelectEquipmentFromPicker;
			page = null;
		
		} else if (button == Button.B_OK) {

			locator= Locators.AddEquipmentFromPicker;
			page = null;
		
		} else if (button == Button.B_CANCEL) {

			locator = "css=div[class='DwtDialog'] td[id$='_button1_title']";
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
		this.sGetCssCount(locator);
		this.sClickAt(locator, "");
		this.zWaitForBusyOverlay();
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

