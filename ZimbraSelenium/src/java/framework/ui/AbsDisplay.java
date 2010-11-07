package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.items.ZimbraItem;
import framework.util.HarnessException;

/**
 * This class defines an abstract displayed page
 * Examples: reading pane, invitee appointment view, briefcase file
 * @author Matt Rhoades
 *
 */
public abstract class AbsDisplay extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsDisplay.class);

	protected AbsApplication MyAbsApplication = null;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsDisplay(AbsApplication application) {
		logger.info("new AbsDisplayPage");
		MyAbsApplication = application;
	}
		
	
	/**
	 * Return the unique name for this page
	 * @return
	 */
	public abstract String myPageName();
	
}
