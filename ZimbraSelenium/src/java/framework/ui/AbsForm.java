package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.items.ZimbraItem;
import framework.util.HarnessException;

/**
 * A <code>AbsForm</code> object represents a "compose page", 
 * such as a new message, new contact, new appointment, new document, etc.
 * <p>
 * Form objects are usually returned after clicking NEW from the toolbar.
 * <p>
 * As a shortcut, form objects take a {@link ZimbraItem} object in the 
 * {@link AbsForm#zFill(ZimbraItem)} and attempts to fill in the form
 * automatically based on the item's previously set properties.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsForm extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsForm.class);

	/**
	 * A pointer to the application that created this object
	 */
	protected AbsApplication MyAbsApplication = null;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsForm(AbsApplication application) {
		logger.info("new AbsForm");
		MyAbsApplication = application;
	}
	
	/**
	 * Fill out the form (but don't submit)
	 * @throws HarnessException on error
	 */
	public abstract void zFill(ZimbraItem item) throws HarnessException;
	
	
	/**
	 * Click on "submit" button
	 * @throws HarnessException on error
	 */
	public abstract void zSubmit() throws HarnessException;
	
	
	/**
	 * Fill and submit the form
	 * @throws HarnessException on error
	 */
	public void zComplete(ZimbraItem item) throws HarnessException {
		zFill(item);
		zSubmit();
	}
	
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
}
