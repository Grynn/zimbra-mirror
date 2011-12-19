package com.zimbra.qa.selenium.projects.desktop.ui.tasks;

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
		
		public static final String IsActive 			= "css=[parentid='zv__TKL-main']";

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
		
		String locator = null;
		
		if ( field == Field.Subject ) {
			
			//locator = "css=[parentid='zv__TKL'][class^='SubjectCol']";
			locator="class=SubjectCol LabelColValue";
			
		} else if ( field == Field.Location ) {

			locator = "//tr[contains(@id,'__lo')]/td[2]";

		} else if ( field == Field.Priority ) {

			locator = "//tr[contains(@id,'__pr')]/td[2]";

		} else if ( field == Field.Status ) {

			locator = "//tr[contains(@id,'__st')]/td[2]";

		} else if ( field == Field.Percentage ) {

			locator = "//tr[contains(@id,'__pc')]/td[2]";

		} else if ( field == Field.StartDate ) {

			locator = "//tr[contains(@id,'__sd')]/td[2]";

		} else if ( field == Field.DueDate ) {

			locator = "//tr[contains(@id,'__ed')]/td[2]/span";

		} else if ( field == Field.Reminder ) {

			locator = "//tr[contains(@id,'__al')]/td[2]";

		} else if ( field == Field.Body ) {

			locator = "class=MsgBody MsgBody-html";

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





}
