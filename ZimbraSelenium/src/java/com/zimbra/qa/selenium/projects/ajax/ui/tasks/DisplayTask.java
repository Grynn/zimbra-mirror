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
		
		public static final String IsActive 			= "css=[parentid='zv__TKL']";

	}

	/**
	 * The various displayed fields in a message
	 */
	public static enum Field {
		Subject,
		Location,
		StartDate,
		DueDate,
		Priority,
		Status,
		Percentage, // "Completed"
		Reminder,
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

	@Override
	public AbsPage zPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zDisplayPressButton("+ button +")");
		
		tracer.trace("Click "+ button);

		throw new HarnessException("no logic defined for button: "+ button);
		
	}
	

	@Override
	public boolean zIsActive() throws HarnessException {
		String locator = Locators.IsActive;
		return (sIsElementPresent(locator));
	}

	public String zGetTaskProperty(Field field) throws HarnessException {
		logger.info(myPageName() + ".zGetTaskProperty(" + field + ")");

		//**
		// See https://bugzilla.zimbra.com/show_bug.cgi?id=56657 - "Need unique id for "view task" pane"
		//**
		
		String locator = "css=div[id='zv__TKL'] div[class='ZmMailMsgView']";
		
		if ( field == Field.Subject ) {
			
			//locator = "css=[parentid='zv__TKL'][class^='SubjectCol']";
			locator += " div[id$='__su']";
			
		} else if ( field == Field.Location ) {

			locator += " tr[id$='__lo'] td[class='LabelColValue']";

		} else if ( field == Field.Priority ) {

			locator += " tr[id$='__pr'] td[class='LabelColValue']";

		} else if ( field == Field.Status ) {

			locator += " tr[id$='__st'] td[class='LabelColValue']";

		} else if ( field == Field.Percentage ) {

			locator += " tr[id$='__pc'] td[class='LabelColValue']";

		} else if ( field == Field.StartDate ) {

			locator += " tr[id$='__sd'] td[class='LabelColValue']";

		} else if ( field == Field.DueDate ) {

			locator += " tr[id$='__ed'] td[class='LabelColValue']";

		} else if ( field == Field.Reminder ) {

			locator += " tr[id$='__al'] td[class='LabelColValue']";

		} else if ( field == Field.Body ) {

			/*
			 * To get the body contents, need to switch iframes
			 */
			try {
				
				//this.sSelectFrame("css=iframe[id='zv__MSG_body__iframe']");
				this.sSelectFrame("css=iframe[id='zv__TKL_body__iframe']");
				
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

		}else {
			
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

	public String zGetTaskListViewProperty(Field field) throws HarnessException {
		String locator = "css=div[id='zl__TKL__rows'] div[id^='zli__TKL'] tr[id^='zlif__TKL']";

		if (field == Field.Subject) {
		
			locator += " div[id$='__su']";

		} else if (field == Field.Status) {

			locator += " td[id$='__st']";

		} else if (field == Field.Percentage) {

			locator += " td[id$='__pc']";

		} else if (field == Field.DueDate) {

			locator += " td[id$='__dt']";

		} else {

			throw new HarnessException("no logic defined for field " + field);

		}

		// Make sure something was set
		if (locator == null)
			throw new HarnessException("locator was null for field = " + field);


		// Get the subject value
		String value = this.sGetText(locator).trim();

		logger.info(myPageName() + ".zGetTaskListViewProperty(" + field
				+ ") = " + value);
		return (value);

	}





}
