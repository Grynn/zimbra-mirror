package projects.admin.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.core.ClientSessionFactory;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * This class defines an abstract Zimbra Application "Page"
 * @author Matt Rhoades
 *
 */
public abstract class AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsPage.class);
	
	public static final String PageName = "AbsPage";
	protected AppAdminConsole MyApplication = null;

	public AbsPage(AbsApplication application) {
		logger.info("new AbsPage");
		MyApplication = (AppAdminConsole)application;
	}
	
	/**
	 * Determines if this page is active
	 * @return true if active.  false if not.
	 * @throws HarnessException
	 */
	public boolean isActive() throws HarnessException {
		throw new HarnessException("isActive() not defined for this page");
	}

	public void waitForActive(long millis) throws HarnessException {
		
		if ( isActive() ) {
			return; // Page became active
		}
		
		do {
			SleepUtil.sleep(SleepUtil.SleepGranularity);
			millis = millis - SleepUtil.SleepGranularity;
			if ( isActive() ) {
				return; // Page became active
			}
		} while (millis > SleepUtil.SleepGranularity);
		
		SleepUtil.sleep(millis);
		if ( isActive() ) {
			return;	// Page became active
		}

		throw new HarnessException("Page never became active");
	}
	
	//// ***
	// Start: Selenium methods
	//// ***
	
	protected final void click(String locator) {
		ClientSessionFactory.session().selenium().click(locator);
		logger.info("click(" + locator + ")");
	}
	
	protected final boolean isVisible(String locator) {
		boolean active = ClientSessionFactory.session().selenium().isVisible(locator);
		logger.info("isVisible(" + locator + ") = " + active);
		return (active);
	}


	protected final String getText(String locator) {
		String text = ClientSessionFactory.session().selenium().getText(locator);
		logger.info("DefaultSelenium.getText(" + locator + ") = " + text);
		return (ClientSessionFactory.session().selenium().getText(locator));
	}
	
	protected final void type(String locator, String text) {
		ClientSessionFactory.session().selenium().type(locator, text);
		logger.info("type(" + locator + ", " + text + ")");
	}
	
	//// ***
	// End: Selenium methods
	//// ***

}
