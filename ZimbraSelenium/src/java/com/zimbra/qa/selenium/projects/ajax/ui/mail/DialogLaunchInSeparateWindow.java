/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialogSeparateWindow;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogLaunchInSeparateWindow extends AbsDialogSeparateWindow {

	public static class Locators {

	}
	

	// TODO: need to I18N
	public String DialogLaunchInSeparateWindowTitle = null;

	public DialogLaunchInSeparateWindow(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		// Set the title to null to start.
		// Set the title with zSetSubject()
		this.DialogWindowTitle = null;
		
	}
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		throw new HarnessException("zClickButton("+ button +") not implemented");

	}

	public static enum Field {
		ReceivedTime,	// Message received time
		ReceivedDate,	// Message received date
		From,
		ResentFrom,
		ReplyTo,
		To,
		Cc,
		OnBehalfOf,
		OnBehalfOfLabel,
		Bcc,			// Does this show in any mail views?  Maybe in Sent?
		Subject,
		Body
	}
	
	public String zGetMailProperty(Field field) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedValue(" + field + ")");

		String locator = null;
		
		if ( field == Field.From ) {
			
			locator = "css=tr[id$='_from'] span[id$='_com_zimbra_email']";
			if ( !this.myIsElementPresent(locator) ) {
				locator = "css=tr[id$='_from']"; // No bubbles
			}
			
		} else if ( field == Field.To ) {
			
			locator = "css=tr[id$='_to'] span[id$='_com_zimbra_email']";
			if ( !this.myIsElementPresent(locator) ) {
				locator = "css=tr[id$='_to']"; // No bubbles
			}
			
		} else if ( field == Field.Subject ) {
			
			locator = "css=div[id='zv__MSG1__MSG'] tr[id='zv__MSG__MSG1_hdrTableTopRow'] td[class*='SubjectCol']";
			
		} else {
			
			throw new HarnessException("No logic defined for Field: "+ field);
			
		}


		// Make sure something was set
		if ( locator == null )
			throw new HarnessException("locator was null for field = "+ field);
		
		// Default behavior
		//
		String value = this.myGetText(locator);
		
		logger.info("zGetDisplayedValue(" + field + ") = " + value);
		return(value);

	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		return (this.myGetText(locator));
	}

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsPage#zWaitForActive()
	 */
	public void zWaitForActive() throws HarnessException {
		zWaitForActive(PageLoadDelay);
		
		try {
			this.sSelectWindow(this.DialogWindowID);
			this.sWindowFocus();

			GeneralUtility.waitForElementPresent(this, "css=div[id='zv__MSG1__MSG'] tr[id='zv__MSG__MSG1_hdrTableTopRow'] td[class*='SubjectCol']");

		} finally {
			this.sSelectWindow(MainWindowID);
			this.sWindowFocus();
		}

	}

}
