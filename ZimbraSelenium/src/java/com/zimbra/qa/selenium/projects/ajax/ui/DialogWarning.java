package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * A <code>DialogWarning</code> object represents a "Warning" dialog, such as "Save 
 * current message as draft", etc.
 * <p>
 * During construction, the div ID attribute must be specified, such as "YesNoCancel".
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogWarning extends AbsDialog {

	public static class DialogWarningID {
		
		public static DialogWarningID SaveCurrentMessageAsDraft = new DialogWarningID("YesNoCancel");
		
		private String Id;
		private DialogWarningID(String id) {
			Id = id;
		}
	}
	
	protected String MyDivId = null;
	
	
	public DialogWarning(DialogWarningID dialogId, AbsApplication application) {
		super(application);
		
		// Remember which div this object is pointing at
		/*
		 * Example:
		 * <div id="YesNoCancel" style="position: absolute; overflow: visible; left: 229px; top: 208px; z-index: 700;" class="DwtDialog" parentid="z_shell">
		 *   <div class="DwtDialog WindowOuterContainer">
		 *   ...
		 *   </div>
		 * </div>
		 */
		MyDivId = dialogId.Id;
		
	}
	
	public String zGetWarningTitle() throws HarnessException {
		String locator = "css=div[id='"+ MyDivId +"'] td[id='"+ MyDivId +"_title']";
		return (zGetDisplayedText(locator));
	}
	
	public String zGetWarningContent() throws HarnessException {
		String locator = "css=div[id='"+ MyDivId +"'] td[id='"+ MyDivId +"_content']";
		return (zGetDisplayedText(locator));
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
		

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=54560
		// Need unique id's for the buttons
		String buttonsTableLocator = "//div[@id='"+ MyDivId +"']//div[contains(@id, '_buttons')]";
		
		if ( button == Button.B_YES ) {

			locator = buttonsTableLocator + "//table//table//tr/td[1]";

		} else if ( button == Button.B_NO ) {

			locator = buttonsTableLocator + "//table//table//tr/td[2]";

		} else if ( button == Button.B_CANCEL ) {

			locator = buttonsTableLocator + "//table//table//tr/td[3]";

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
		
		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();
		
		// If page was specified, make sure it is active
		if ( page != null ) {
			
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		if ( locator == null )
			throw new HarnessException("locator cannot be null");
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("locator cannot be found");
		
		return (this.sGetText(locator));
		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		if ( !this.sIsElementPresent(MyDivId) )
			return (false);
		if ( !this.zIsVisiblePerPosition(MyDivId, 225, 650) )
			return (false);
		return (true);
	}

}
