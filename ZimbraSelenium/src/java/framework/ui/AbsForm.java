package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.items.IItem;
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
public abstract class AbsForm extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsForm.class);


	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsForm(AbsApplication application) {
		super(application);
		
		logger.info("new AbsForm");
	}
	
	/**
	 * Fill out the form (but don't submit)
	 * @throws HarnessException on error
	 */
	public abstract void zFill(IItem item) throws HarnessException;
	
	
	/**
	 * Click on "submit" button
	 * @throws HarnessException on error
	 */
	public abstract void zSubmit() throws HarnessException;
	
	
	/**
	 * Fill and submit the form
	 * @throws HarnessException on error
	 */
	public void zComplete(IItem item) throws HarnessException {
		zFill(item);
		zSubmit();
	}
	
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
}
