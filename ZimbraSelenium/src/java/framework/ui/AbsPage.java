package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * A <code>AbsPage</code> object represents any of the GUI classes, such as
 * Dialogs, Tabs, Forms, etc.
 * <p>
 * Implementing AbsPage classes must define the {@link AbsPage#zIsActive()} and
 * {@link AbsPage#zNavigateTo()} methods.  The test method classes can set a
 * "startingapp", which the harness will attempt to navigate-to before running
 * each test method.
 * <p>
 * @author Matt Rhoades
 *
 */
public abstract class AbsPage extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsPage.class);

	protected static final int PageLoadDelay = 30000; // wait 30 seconds for pages to load

	
	public enum PopupButton {
		Yes, No, Cancel, Help
	}
	
	public enum ListNavButton {
		Previous, Next
	}
		

	/**
	 * A pointer to the application that created this object
	 */
	protected AbsApplication MyApplication = null;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsPage(AbsApplication application) {
		logger.info("new AbsAdminPage");
		MyApplication = application;
	}
	
	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
	/**
	 * Determines if this page is active, usually by detecting
	 * whether a GUI element is present or not.
	 * <p>
	 * @return true if active.  false if not.
	 * @throws HarnessException
	 */
	public abstract boolean zIsActive() throws HarnessException;

	/**
	 * Wait for this page to become active (default PageLoadDelay)
	 * @throws HarnessException
	 */
	public void zWaitForActive() throws HarnessException {
		zWaitForActive(PageLoadDelay);
	}
	
	/**
	 * Wait for this page to become active
	 * @throws HarnessException
	 */
	public void zWaitForActive(long millis) throws HarnessException {
		
		if ( zIsActive() ) {
			return; // Page became active
		}
		
		do {
			SleepUtil.sleep(SleepUtil.SleepGranularity);
			millis = millis - SleepUtil.SleepGranularity;
			if ( zIsActive() ) {
				return; // Page became active
			}
		} while (millis > SleepUtil.SleepGranularity);
		
		SleepUtil.sleep(millis);
		if ( zIsActive() ) {
			return;	// Page became active
		}

		throw new HarnessException("Page never became active");
	}
	

}
