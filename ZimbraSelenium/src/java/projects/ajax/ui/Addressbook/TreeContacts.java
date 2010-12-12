/**
 * 
 */
package projects.ajax.ui.Addressbook;

import framework.items.FolderItem;
import framework.ui.AbsApplication;
import framework.ui.AbsSeleniumObject;
import framework.ui.AbsTree;
import framework.ui.Action;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * @author zimbra
 *
 */
public class TreeContacts extends AbsTree {

	public static class Locators {
	}
	
		
	
	public TreeContacts(AbsApplication application) {
		super(application);
		logger.info("new " + TreeContacts.class.getCanonicalName());
	}
	
	public AbsSeleniumObject zTreeItem(Action action, FolderItem addressbook) throws HarnessException {
		
		// Validate the arguments
		if ( (action == null) || (addressbook == null) ) {
			throw new HarnessException("Must define an action and addressbook");
		}
		
		AbsSeleniumObject page = null;
		String locator = "id=zti__main_Contacts__"+ addressbook.getId() +"_textCell";
		
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Unable to locator folder in tree "+ locator);
		}
		
		if ( action == Action.A_LEFTCLICK ) {
			
			this.sClick(locator);
			SleepUtil.sleepSmall();
			page = null;
			
			// FALL THROUGH

		} else {
			throw new HarnessException("Action "+ action +" not yet implemented");
		}

		return (page);
	}
	


	/* (non-Javadoc)
	 * @see framework.ui.AbsTree#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

}
