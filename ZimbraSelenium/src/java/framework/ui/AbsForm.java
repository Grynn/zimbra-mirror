package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.items.ZimbraItem;
import framework.util.HarnessException;

/**
 * This class defines an abstract Zimbra Admin Console Application "Manage Object" page
 * @author Matt Rhoades
 *
 */
public abstract class AbsForm extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsForm.class);

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
	public abstract void fill(ZimbraItem item) throws HarnessException;
	
	
	/**
	 * Click on "submit" button
	 * @throws HarnessException on error
	 */
	public abstract void submit() throws HarnessException;
	
	
	/**
	 * Fill and submit the form
	 * @throws HarnessException on error
	 */
	public void complete(ZimbraItem item) throws HarnessException {
		fill(item);
		submit();
	}
	
	
	/**
	 * Return the unique name for this page
	 * @return
	 */
	public abstract String myPageName();
	
}
