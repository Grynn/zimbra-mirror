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
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DisplayMail.Field;



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

	public String zGetMailProperty(Field field) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedValue(" + field + ")");

		String container = "css=div[id='zv__MSG1__MSG']";
		String locator = null;
		
		if ( field == Field.From ) {
			
			locator = container + " tr[id$='_from'] span[id$='_com_zimbra_email']";
			if ( !this.sIsElementPresent(locator) ) {
				locator = container + " tr[id$='_from']"; // No bubbles
			}
			
		} else if ( field == Field.To ) {
			
			locator = container + " tr[id$='_to'] span[id$='_com_zimbra_email']";
			if ( !this.sIsElementPresent(locator) ) {
				locator = container + " tr[id$='_to']"; // No bubbles
			}
			
		} else if ( field == Field.Cc ) {
			
			locator = container + " tr[id$='_cc'] span[id$='_com_zimbra_email']";
			if ( !this.sIsElementPresent(locator) ) {
				locator = container + " tr[id$='_cc']"; // No bubbles
			}
			
		} else if ( field == Field.OnBehalfOf ) {
			
			locator = container + " td[id$='_obo'] span[id$='_com_zimbra_email']";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = container + " td[id$='_obo']";
			}

		} else if ( field == Field.ResentFrom ) {
			
			locator = container + " td[id$='_bwo'] span[id$='_com_zimbra_email']";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = container + " tr[id$='_bwo']";
			}

		} else if ( field == Field.OnBehalfOfLabel ) {
			
			locator = container + " td[id$='_obo_label']";

		} else if ( field == Field.ReplyTo ) {
			
			locator = container + " tr[id$='_reply to'] span[id$='_com_zimbra_email']";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = container + " tr[id$='_reply to']";
			}

		} else if ( field == Field.Subject ) {
			
			locator = container + " tr[id='zv__MSG__MSG1_hdrTableTopRow'] td[class~='SubjectCol']";
			
		} else if ( field == Field.ReceivedDate ) {
			
			locator = container + " tr[id$='_hdrTableTopRow'] td[class~='DateCol'] span[id$='_com_zimbra_date']";

		} else if ( field == Field.ReceivedTime ) {
			
			String timeAndDateLocator = container + " tr[id$='_hdrTableTopRow'] td[class~='DateCol'] span[id$='_com_zimbra_date']";

			// Make sure the subject is present
			if ( !sIsElementPresent(timeAndDateLocator) )
				throw new HarnessException("Unable to find the time and date field!");
			
			// Get the subject value
			String timeAndDate = this.sGetText(timeAndDateLocator).trim();
			String date = this.zGetMailProperty(Field.ReceivedDate);
			
			// Strip the date so that only the time remains
			String time = timeAndDate.replace(date, "").trim();
			
			logger.info("zGetDisplayedValue(" + field + ") = " + time);
			return(time);

		} else if ( field == Field.Body ) {
			
			/*
			 * To get the body contents, need to switch iframes
			 */
			String text = sGetText("css=iframe[id$='_body__iframe']", "css=body");
			logger.info("zGetDisplayedValue(" + field + ") = " + text);
			return(text);

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
