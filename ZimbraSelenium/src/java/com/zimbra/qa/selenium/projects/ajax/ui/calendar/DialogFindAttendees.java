package com.zimbra.qa.selenium.projects.ajax.ui.calendar;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;

public class DialogFindAttendees extends DialogWarning {

	// The ID for the main Dialog DIV
	public static final String LocatorDivID = "SEND_UPDATES_DIALOG";
		
	public DialogFindAttendees(AbsApplication application, AbsTab page) {
		super(new DialogWarningID(LocatorDivID), application, page);
				
		logger.info("new " + DialogFindAttendees.class.getCanonicalName());
	}
	public static class Locators {

		public static final String LocationPickerSerach="css=div[class='DwtDialog'] td[id$='_title']:contains('Search')";
		public static final String SelectLocationFromPicker="css=div[class='DwtDialog'] td[id$='_title']:contains('Select')";
		public static final String AddLocationFromPicker="css=div[class='DwtDialog']  td[id^='OK'] td[id$='_button2_title']";
		
		
		public static final String ShowOptionaAttendees = "css= td#DWT273_show_optional.fakeAnchor";
		public static final String ContactPickerSerachField = "id=ZmContactPicker_searchField";
		public static final String ContactPickerSerachButton = "css=td[id$='_title']:contains('Search')";
		public static final String ContactPickerFirstContact = "css=nobr";
		public static final String SelectContactFromPicker = "css=td[id^='DwtChooserButton']:contains('To:')";
		public static final String AddContactFromPicker = "css=td[id^='ZmContactPicker_button']:contains('OK')";
	
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

		if (button == Button.B_SEARCH) {

			locator = Locators.ContactPickerSerachButton;
			page = null;

		} else if (button == Button.B_SELECT_LOCATION) {

			locator = Locators.SelectLocationFromPicker;
			page = null;
		
		} else if (button == Button.B_OK) {

			locator = Locators.AddContactFromPicker;
			page = null;
	
		} else if (button == Button.B_CANCEL) {

			locator = "css=div[class='DwtDialog'] td[id$='_button1_title']";
			page = null;
			                              
		} else if (button == Button.B_CHOOSE_CONTACT_FROM_PICKER) {

			locator = Locators.SelectContactFromPicker;
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

