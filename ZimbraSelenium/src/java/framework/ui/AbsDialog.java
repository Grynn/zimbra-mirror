/**
 * 
 */
package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.HarnessException;

/**
 * A <code>AbsDialog</code> object represents a "popup dialog", 
 * such as a new folder, new tag, error message, etc.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsDialog extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsDialog.class);

	/**
	 * A pointer to the application that created this object
	 */
	protected AbsApplication MyAbsApplication = null;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsDialog(AbsApplication application) {
		logger.info("new AbsDialog");
		MyAbsApplication = application;
	}
	
	/**
	 * Get the dialog displayed text
	 */
	public abstract String zGetDisplayedText(String locator) throws HarnessException;
	
	/**
	 * Click on a button in the dialog
	 **/
	public abstract void zClickButton(Button button) throws HarnessException;
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	

}
