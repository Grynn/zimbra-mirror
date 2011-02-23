/**
 * 
 */
package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;


/**
 * A <code>AbsDialog</code> object represents a "popup dialog", 
 * such as a new folder, new tag, error message, etc.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsDialog extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsDialog.class);
	protected AbsTab MyTab;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 * @param page
	 */
	public AbsDialog(AbsApplication application, AbsTab page) {

		super(application);

		MyTab = page;

		logger.info("new " + AbsDialog.class.getCanonicalName());

	}
	
	/**
	 * Get the dialog displayed text
	 */
	public abstract String zGetDisplayedText(String locator) throws HarnessException;
	
	/**
	 * Click on a button in the dialog
	 **/
	public abstract AbsPage zClickButton(Button button) throws HarnessException;
	
	/**
	 * Wait for this dialog to close
	 * @throws HarnessException
	 */
	public void zWaitForClose()  throws HarnessException {
		zWaitForClose(PageLoadDelay);
	}

	/**
	 * Wait for this dialog to close
	 * @throws HarnessException
	 */
	public void zWaitForClose(long millis) throws HarnessException {
		
		if ( !zIsActive() ) {
			return; // Dialog closed
		}
		
		do {
			SleepUtil.sleep(SleepUtil.SleepGranularity);
			millis = millis - SleepUtil.SleepGranularity;
			if ( !zIsActive() ) {
				return; // Dialog closed
			}
		} while (millis > SleepUtil.SleepGranularity);
		
		SleepUtil.sleep(millis);
		if ( !zIsActive() ) {
			return;	// Page became active
		}

		throw new HarnessException("Dialog never closed");
	}
	

	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	

}
