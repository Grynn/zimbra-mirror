package com.zimbra.qa.selenium.projects.ajax.ui.tasks;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * The <code>DisplayMail<code> object defines a read-only view of a message
 * in the Zimbra Ajax client.
 * <p>
 * This class can be used to extract data from the message, such as To,
 * From, Subject, Received Date, message body.  Additionally, it can
 * be used to click on certain links in the message body, such as 
 * "view entire message" and "highlight objects".
 * <p>
 * Hover over objects, such as email or URL hover over, are encapsulated.
 * <p>
 * 
 * @author zimbra
 * @see http://wiki.zimbra.com/wiki/Testing:_Selenium:_ZimbraSelenium_Overview#Mail_Page
 */
public class DisplayTask extends AbsDisplay {

	/**
	 * Defines Selenium locators for various objects in {@link DisplayTask}
	 */
	public static class Locators {
		public static final String zSubject				= "css=[class='LabelColValue SubjectCol']";
		public static final String zDate 				= "css=[class='LabelColValue DateCol']";

		
		public static final String zViewEntireMessage 	= "id=zv__CLV__MSG_msgTruncation_link";
		public static final String zHighlightObjects 	= "id=zv__CLV_highlightObjects_link";

	}

	/**
	 * The various displayed fields in a message
	 */
	public static enum Field {
		ReceivedTime,	// Message received time
		ReceivedDate,	// Message received date
		From,
		To,
		Cc,
		Bcc,			// Does this show in any mail views?  Maybe in Sent?
		Subject,
		Body
	}
	

	/**
	 * Protected constuctor for this object.  Only classes within
	 * this package should create DisplayMail objects.
	 * 
	 * @param application
	 */
	protected DisplayTask(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayTask.class.getCanonicalName());

	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/**
	 * Click on "view entire message" in this message
	 * @return TBD: return the new window?
	 * @throws HarnessException
	 */
	public AbsPage zClickViewEntireMessage() throws HarnessException {
		logger.info(myPageName() + " zViewEntireMessage");
		
		AbsPage page = null;
		String locator = Locators.zViewEntireMessage;
		
		if ( this.sIsElementPresent(locator) )
			throw new HarnessException("'View Entire Message' link does not exist: "+ Locators.zViewEntireMessage);
		
		this.sClick(locator);
		
		this.zWaitForBusyOverlay();
		
		return (page);
	}

	/**
	 * Click on "highlight objects" in this message
	 * @return TBD: return the new window?
	 * @throws HarnessException
	 */
	public AbsPage zClickHighlightObjects() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	/**
	 * Get the string value of the specified field
	 * @return the displayed string value
	 * @throws HarnessException
	 */
	public String zGetMailProperty(Field field) throws HarnessException {
		logger.info("DisplayMail.zGetDisplayedValue(" + field + ")");

		String locator = null;
		
		if ( field == Field.Bcc ) {
			
			// Does this show in any mail views?  Maybe in Sent?
			throw new HarnessException("implement me!");
			
		} else if ( field == Field.Body ) {

			/*
			 * To get the body contents, need to switch iframes
			 */
			try {
				
				this.sSelectFrame("//iframe[contains(@id, '__MSG_body__iframe')]");
				
				String bodyLocator = "css=body";
				
				// Make sure the body is present
				if ( !this.sIsElementPresent(bodyLocator) )
					throw new HarnessException("Unable to find the message body!");
				
				// Get the body value
				// String body = this.sGetText(bodyLocator).trim();
				String html = this.zGetHtml(bodyLocator);
				
				logger.info("DisplayMail.zGetBody(" + bodyLocator + ") = " + html);
				return(html);

			} finally {
				// Make sure to go back to the original iframe
				this.sSelectFrame("relative=top");
			}

		} else if ( field == Field.Cc ) {
			
			locator = "css=tr[id$='_cc'] td[class~='LabelColValue']";
			
		} else if ( field == Field.From ) {
			
			locator = "css=tr[id$='_from']";

		} else if ( field == Field.ReceivedDate ) {
			
			locator = "css=tr[id$='__MSG_hdrTableTopRow'] td[class~='DateCol']";

		} else if ( field == Field.ReceivedTime ) {
			
			String timeAndDateLocator = "css=td[class~='DateCol']";

			// Make sure the subject is present
			if ( !this.sIsElementPresent(timeAndDateLocator) )
				throw new HarnessException("Unable to find the time and date field!");
			
			// Get the subject value
			String timeAndDate = this.sGetText(timeAndDateLocator).trim();
			String date = this.zGetMailProperty(Field.ReceivedDate);
			
			// Strip the date so that only the time remains
			String time = timeAndDate.replace(date, "").trim();
			
			logger.info("DisplayMail.zGetDisplayedValue(" + field + ") = " + time);
			return(time);

		} else if ( field == Field.Subject ) {
			
			locator = "css=tr[id$='__MSG_hdrTableTopRow'] td[class~='SubjectCol']";

		} else if ( field == Field.To ) {
			
			locator = "css=tr[id$='_to'] td[class~='LabelColValue']";

		} else {
			
			throw new HarnessException("no logic defined for field "+ field);
			
		}

		// Make sure something was set
		if ( locator == null )
			throw new HarnessException("locator was null for field = "+ field);
		
		// Default behavior, process the locator by clicking on it
		//
		
		// Make sure the subject is present
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Unable to find the field = "+ field +" using locator = "+ locator);
		
		// Get the subject value
		String value = this.sGetText(locator).trim();
		
		logger.info("DisplayMail.zGetDisplayedValue(" + field + ") = " + value);
		return(value);

		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.warn("implement me", new Throwable());
		return (true);
	}




}
