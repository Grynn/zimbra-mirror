package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * A <code>AbsTree</code> object represents a "tree panel", 
 * such as a Folder tree, Addressbook tree, Calendar tree, etc.
 * <p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsTree extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsTree.class);



	/**
	 * Create this page object that exists in the specified application
	 * @param application
	 */
	public AbsTree(AbsApplication application) {
		super(application);
		
		logger.info("new AbsTree");
	}
	
	/**
	 * Click on a button
	 * @param button
	 * @return
	 * @throws HarnessException
	 */
	public abstract AbsPage zPressButton(Button button) throws HarnessException;

	/**
	 * Apply the specified action on the specified item
	 * @param action
	 * @param addressbook
	 * @return
	 * @throws HarnessException
	 */
	public abstract AbsPage zTreeItem(Action action, IItem item) throws HarnessException;

	/**
	 * Apply the specified action with option on the specified item
	 * <p>
	 * For example, use this method to take an action using the context method.  The
	 * Action is Action.A_LEFTCLICK and the Button would be the context menu item, such
	 * as Button.B_DELETE
	 * <p>
	 * @param action
	 * @param addressbook
	 * @return
	 * @throws HarnessException
	 */
	public abstract AbsPage zTreeItem(Action action, Button option, IItem item) throws HarnessException;


	/**
	 * Return the unique name for this page class
	 * @return
	 */
	public abstract String myPageName();
	
	
}
