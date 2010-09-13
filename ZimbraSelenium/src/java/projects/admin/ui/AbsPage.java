package projects.admin.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.DocumentException;

import com.zimbra.common.soap.Element;

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
	
	protected AppAdminConsole MyApplication = null;

	public AbsPage(AbsApplication application) {
		logger.info("new AbsPage");
		MyApplication = (AppAdminConsole)application;
	}
	
	/**
	 * Return the unique name for this page
	 * @return
	 */
	public abstract String myPageName();
	
	/**
	 * Determines if this page is active
	 * @return true if active.  false if not.
	 * @throws HarnessException
	 */
	public abstract boolean isActive() throws HarnessException;

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
	
	/**
	 * Navigate to this page
	 * @throws HarnessException
	 */
	public abstract void navigateTo() throws HarnessException;
	

	//// ***
	// Start: Selenium methods
	//// ***
	
	protected final String getHtmlSource() throws HarnessException {
		String htmlString = ClientSessionFactory.session().selenium().getHtmlSource();
		return (htmlString);
	}
	
	protected final String getSelectedId(String locator) {
		String id = ClientSessionFactory.session().selenium().getSelectedId(locator);
		logger.info("getSelectedId(" + locator + ") = "+ id);
		return (id);
	}
	

	protected final void click(String locator) {
		ClientSessionFactory.session().selenium().click(locator);
		logger.info("click(" + locator + ")");
	}
	
	protected final void focus(String locator) {
		ClientSessionFactory.session().selenium().focus(locator);
		logger.info("focus(" + locator + ")");
	}
	
	protected final boolean isElementPresent(String locator) {
		boolean present = ClientSessionFactory.session().selenium().isElementPresent(locator);
		logger.info("isElementPresent(" + locator + ") = " + present);
		return (present);
	}

	protected final boolean isVisible(String locator) {
		boolean visible = ClientSessionFactory.session().selenium().isVisible(locator);
		logger.info("isVisible(" + locator + ") = " + visible);
		return (visible);
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
