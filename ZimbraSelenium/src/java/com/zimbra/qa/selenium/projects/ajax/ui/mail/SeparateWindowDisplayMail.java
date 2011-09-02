/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsSeparateWindow;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class SeparateWindowDisplayMail extends AbsSeparateWindow {

	public static class Locators {

	}
	

	// TODO: need to I18N
	public String DialogLaunchInSeparateWindowTitle = null;

	public SeparateWindowDisplayMail(AbsApplication application) {
		super(application);
		
		// Set the title to null to start.
		// Set the title with zSetSubject()
		this.DialogWindowTitle = null;
		
	}
	
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
			if ( !this.sIsElementPresent(locator) ) {
				locator = "css=tr[id$='_from']"; // No bubbles
			}
			
		} else if ( field == Field.To ) {
			
			locator = "css=tr[id$='_to'] span[id$='_com_zimbra_email']";
			if ( !this.sIsElementPresent(locator) ) {
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
		String value = sGetText(locator);
		
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

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsPage#zWaitForActive()
	 */
	public void zWaitForActive() throws HarnessException {
		super.zWaitForActive(PageLoadDelay);
		
		// Sometimes it takes a while for the separate window to load
		// Look for the subject before returning
		String locator = "css=div[id='zv__MSG1__MSG'] tr[id='zv__MSG__MSG1_hdrTableTopRow'] td[class*='SubjectCol']";
		for(int i = 0; i < 30; i++) {

			boolean present = sIsElementPresent(locator);
			if ( present ) {
				return;
			}
			
			SleepUtil.sleep(1000);
			
		}

		throw new HarnessException("Page never became active!");

	}

}
