package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;


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
public class DisplayMail extends AbsDisplay {

	/**
	 * Defines Selenium locators for various objects in {@link DisplayMail}
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
		OnBehalfOf,
		OnBehalfOfLabel,
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
	protected DisplayMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayMail.class.getCanonicalName());

	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
		
		tracer.trace("Click "+ button);

		AbsPage page = this;
		String locator = null;

		if ( button == Button.B_VIEW_ENTIRE_MESSAGE ) {
			
			locator = Locators.zViewEntireMessage;

			this.sClick(locator);
			
			this.zWaitForBusyOverlay();

			return (page);

		} else if ( button == Button.B_HIGHLIGHT_OBJECTS ) {

			throw new HarnessException("implement me!");

		} else if ( button == Button.B_ACCEPT ) {
			
			locator = "css=td[id$='__Inv__REPLY_ACCEPT_title']";
			page = null;
			
		} else if ( button == Button.B_DECLINE ) {
			
			locator = "css=td[id$='__Inv__REPLY_TENTATIVE_title']";
			page = null;
		
		} else if ( button == Button.B_TENTATIVE ) {
			
			locator = "css=td[id$='__Inv__REPLY_DECLINE_title']";
			page = null;
			
		} else if ( button == Button.B_PROPOSE_NEW_TIME ) {
			
			locator = "css=td[id$='__Inv__PROPOSE_NEW_TIME_title']";
			page = null;
			
		} else  {
			
			throw new HarnessException("no implementation for button: "+ button);

		}
		
		if ( locator == null )
			throw new HarnessException("no locator defined for button "+ button);
		
		
		this.zClick(locator);
		
		this.zWaitForBusyOverlay();

		if ( page != null ) {
			page.zWaitForActive();
		}
		
		return (page);
	}

	/**
	 * Return TRUE/FALSE whether the Accept/Decline/Tentative buttons are present
	 * @return
	 * @throws HarnessException
	 */
	public boolean zHasADTButtons() throws HarnessException {
	
		// Haven't fully baked this method.  
		// Maybe it works.  
		// Maybe it needs to check "visible" and/or x/y/z coordinates

		List<String> locators = Arrays.asList(
				"css=td[id$='__Inv__REPLY_ACCEPT_title']",
				"css=td[id$='__Inv__REPLY_TENTATIVE_title']",
				"css=td[id$='__Inv__REPLY_DECLINE_title']",
				"css=td[id$='__Inv__PROPOSE_NEW_TIME_title']");

		for (String locator : locators) {
			if ( !this.sIsElementPresent(locator) )
				return (false);
		}
		
		return (true);
	}
	
	public HtmlElement zGetMailPropertyAsHtml(Field field) throws HarnessException {
		
		String source = null;
		
		if ( field == Field.Body) {
			
			try {
				
				this.sSelectFrame("//iframe[contains(@id, '__MSG_body__iframe')]");
				
				source = this.sGetHtmlSource();
				
				// For some reason, we don't get the <html/> tag.  Add it
				source = "<html>" + source + "</html>";
										
			} finally {
				// Make sure to go back to the original iframe
				this.sSelectFrame("relative=top");
			}

		} else {
			throw new HarnessException("not implemented for field "+ field);
		}
		
		// Make sure source was found
		if ( source == null )
			throw new HarnessException("source was null for "+ field);

		logger.info("DisplayMail.zGetMailPropertyAsHtml() = "+ HtmlElement.clean(source).prettyPrint());

		// Clean up the HTML code to be valid
		return (HtmlElement.clean(source));

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
			
			locator = "css=tr[id$='_cc'] td[class~='LabelColValue'] span[id$='_com_zimbra_email'] span span";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = "css=tr[id$='_cc'] td[class~='LabelColValue']";
			}
			
		} else if ( field == Field.From ) {
			
			locator = "css=tr[id$='_from'] span[id$='_com_zimbra_email'] span span";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = "css=tr[id$='_from']";
			}

		} else if ( field == Field.OnBehalfOf ) {
			
			locator = "css=td[id$='_obo'] span[id$='_com_zimbra_email'] span span";
			if ( !sIsElementPresent(locator) ) {
				// no email zimlet case
				locator = "css=td[id$='_obo']";
			}

		} else if ( field == Field.OnBehalfOfLabel ) {
			
			locator = "css=td[id$='_obo_label']";

		}else if ( field == Field.ReceivedDate ) {
			
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
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			   locator = "css=tr[id$='_to'] td[class~='LabelColValue'] span[id$='_com_zimbra_email']";
			} else {
			   locator = "css=tr[id$='_to'] td[class~='LabelColValue'] span[id$='_com_zimbra_email'] span span";
			}

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
	
	/**
	 * Wait for Zimlets to be rendered in the message
	 * @throws HarnessException
	 */
	public void zWaitForZimlets() throws HarnessException {
		// TODO: don't sleep.  figure out a way to query the app if zimlets are applied
		logger.info("zWaitForZimlets: sleep a bit to let the zimlets be applied");
		SleepUtil.sleepLong();
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.warn("implement me", new Throwable());
		
		zWaitForZimlets();
		
		return (true);
	}





}
