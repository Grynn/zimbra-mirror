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
		Subject,
		Location,
		Priority,
		TaskList,
		Status,
		Percentage,
		StartDate,
		DueDate,
		ReminderEnabled,
		ReminderDate,
		ReminderTime,
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
	
	@Override
	public boolean zIsActive() throws HarnessException {
		logger.warn("implement me", new Throwable());
		return (true);
	}

	public String zGetTaskProperty(Field field) throws HarnessException {
		logger.info(myPageName() + ".zGetTaskProperty(" + field + ")");

		String locator = null;
		
		if ( field == Field.Subject ) {
			
			locator = "implement me";
			
		} else if ( field == Field.Location ) {

			locator = "implement me";

		} else if ( field == Field.Priority ) {

			locator = "implement me";

		} else if ( field == Field.TaskList ) {

			locator = "implement me";

		} else if ( field == Field.Status ) {

			locator = "implement me";

		} else if ( field == Field.Percentage ) {

			locator = "implement me";

		} else if ( field == Field.StartDate ) {

			locator = "implement me";

		} else if ( field == Field.DueDate ) {

			locator = "implement me";

		} else if ( field == Field.ReminderEnabled ) {

			locator = "implement me";

		} else if ( field == Field.ReminderDate ) {

			locator = "implement me";

		} else if ( field == Field.ReminderTime ) {

			locator = "implement me";

		} else {
			
			throw new HarnessException("no logic defined for field "+ field);
			
		}

		// Make sure something was set
		if ( locator == null )
			throw new HarnessException("locator was null for field = "+ field);
		
		// Default behavior: return the text
		//
		
		// Get the subject value
		String value = this.sGetText(locator).trim();
		
		logger.info(myPageName() + ".zGetTaskProperty(" + field + ") = " + value);
		return(value);

		
	}





}
