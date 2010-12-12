package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.items.IItem;
import framework.util.HarnessException;

/**
 * A <code>AbsTree</code> object represents a "tree panel", 
 * such as a Folder tree, Addressbook tree, Calendar tree, etc.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsTree extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsTree.class);


	/**
	 * A pointer to the application that created this object
	 */
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
	 * Apply the specified action on the specified address book
	 * @param action
	 * @param addressbook
	 * @return
	 * @throws HarnessException
	 */
	public abstract AbsSeleniumObject zTreeItem(Action action, IItem item) throws HarnessException;

	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
	
}
