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
 * Represents a "Launch in New Window" display of a message
 * <p>
 * @author Matt Rhoades
 *
 */
public class SeparateWindowDisplayMail extends AbsSeparateWindow {

	public static class Locators {

	}
	

	public SeparateWindowDisplayMail(AbsApplication application) {
		super(application);
		
		// Set the title to null to start.
		// Set the title with zSetSubject()
		this.DialogWindowTitle = null;
		
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

	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String container = "css=div[id^='ztb__MSG']";
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_CLOSE ) {

			locator = container + " div[id$='__CLOSE'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_DELETE ) {

			locator = container + " div[id$='__DELETE'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_REPLY ) {

			locator = container + " div[id$='__REPLY'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_REPLYALL ) {

			locator = container + " div[id$='__REPLY_ALL'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else if ( button == Button.B_FORWARD ) {

			locator = container + " div[id$='__FORWARD'] td[id$='_title']";
			page = null;

			// FALL THROUGH

		} else {
			
			throw new HarnessException("no logic defined for button "+ button);
			
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClickAt(locator,"0,0");

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if ( page != null ) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}


		return (page);
	}

	
	
	public AbsPage zToolbarPressPulldown(Button button, Object dynamic) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +", "+ dynamic +")");

		tracer.trace("Click pulldown "+ button +" then "+ dynamic);

		if ( button == null )
			throw new HarnessException("Button cannot be null!");

		if ( dynamic == null )
			throw new HarnessException("Dynamic cannot be null!");


		// Default behavior variables
		//
		String container = "css=div[id^='ztb__MSG']";
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_TAG ) {

			if ( !(dynamic instanceof String) ) 
				throw new HarnessException("if button = B_TAG, then dynamic should be a tag name");
			String tagname = (String)dynamic;
			
			pulldownLocator = container + " div[id$='__TAG_MENU'] td[id$='_dropdown']>div";
			optionLocator = "css=div[id$='__TAG_MENU|MENU'] td[id$='_title']:contains("+ tagname +")";
			page = null;

			// FALL THROUGH

		} else {
			
			throw new HarnessException("no logic defined for button "+ button);
			
		}

		if (pulldownLocator != null) {

			this.zClickAt(pulldownLocator,"");

			if (optionLocator != null) {

				this.zClickAt(optionLocator,"");

			}
			
		}
			
		return (page);

	}
}
