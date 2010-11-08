package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsDisplay;
import framework.util.HarnessException;
import framework.util.SleepUtil;

public class DisplayMail extends AbsDisplay {

	public static class Locators {
		public static final String zSubject = "xpath=//td[@class='LabelColValue SubjectCol']";
		public static final String zDate = "xpath=//td[@class='LabelColValue DateCol']";

		
		public static final String zViewEntireMessage = "id=zv__CLV__MSG_msgTruncation_link";
		public static final String zHighlightObjects = "id=zv__CLV_highlightObjects_link";

	}

	/**
	 * The various displayed fields in a message
	 * @author Matt Rhoades
	 *
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
	

	
	public DisplayMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + DisplayMail.class.getCanonicalName());
		
		// Let the reading pane load
		SleepUtil.sleepLong();


	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	public Object zClickViewEntireMessage() throws HarnessException {
		logger.info(myPageName() + " zViewEntireMessage");
		
		if ( this.sIsElementPresent(Locators.zViewEntireMessage) )
			throw new HarnessException("'View Entire Message' link does not exist: "+ Locators.zViewEntireMessage);
		
		this.sClick(Locators.zViewEntireMessage);
		
		SleepUtil.sleepLong();	// Messages are usually large, let it load

		// TODO: return the new window?
		return (null);
	}

	public Object zClickHighlightObjects() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
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
				
				String bodyLocator = "//body";
				
				// Make sure the subject is present
				if ( !this.sIsElementPresent(bodyLocator) )
					throw new HarnessException("Unable to find the message body!");
				
				// Get the subject value
				String body = this.sGetText(bodyLocator).trim();
				
				logger.info("DisplayMail.zGetBody(" + bodyLocator + ") = " + body);
				return(body);

			} finally {
				// Make sure to go back to the original iframe
				this.sSelectFrame("relative=top");
			}

		} else if ( field == Field.Cc ) {
			
			locator = "//span[@id='OBJ_PREFIX_DWT32']";
			
		} else if ( field == Field.From ) {
			
			locator = "//span[@id='OBJ_PREFIX_DWT29']";

		} else if ( field == Field.ReceivedDate ) {
			
			locator = "//span[@id='OBJ_PREFIX_DWT30']";

		} else if ( field == Field.ReceivedTime ) {
			
			String timeAndDateLocator = "//td[contains(@class, 'DateCol')]";

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
			
			locator = "//tr[contains(@id, '__MSG_hdrTableTopRow')]//td[contains(@class,'SubjectCol')]";

		} else if ( field == Field.To ) {
			
			locator = "//span[@id='OBJ_PREFIX_DWT31']";

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
			throw new HarnessException("Unable to find the locator for field = "+ field);
		
		// Get the subject value
		String value = this.sGetText(locator).trim();
		
		logger.info("DisplayMail.zGetDisplayedValue(" + field + ") = " + value);
		return(value);

		
	}
	




}
