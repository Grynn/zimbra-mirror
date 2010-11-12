package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * This class defines an abstract Zimbra Admin Console Application "Manage Object" page
 * @author Matt Rhoades
 *
 */
public abstract class AbsTree extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsTree.class);


	protected AbsApplication MyAbsApplication = null;

	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsTree(AbsApplication application) {
		logger.info("new AbsTree");
		MyAbsApplication = application;
	}
	
	/**
	 * Return the unique name for this page
	 * @return
	 */
	public abstract String myPageName();
	
	
}
