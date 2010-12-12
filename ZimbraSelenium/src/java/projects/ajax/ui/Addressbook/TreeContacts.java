/**
 * 
 */
package projects.ajax.ui.Addressbook;

import framework.items.FolderItem;
import framework.items.IItem;
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
	
	/* (non-Javadoc)
	 * @see framework.ui.AbsTree#zTreeItem(framework.ui.Action, framework.items.FolderItem)
	 */
	public AbsSeleniumObject zTreeItem(Action action, IItem addressbook) throws HarnessException {
		
		// Validate the arguments
		if ( (action == null) || (addressbook == null) ) {
			throw new HarnessException("Must define an action and addressbook");
		}
		
		if ( !(addressbook instanceof FolderItem) ) {
			throw new HarnessException("Must use FolderItem as argument, but was "+ addressbook.getClass());
		}
		
		FolderItem folder = (FolderItem)addressbook;
		
		AbsSeleniumObject page = null;
		String locator = null;
		
		if ( action == Action.A_LEFTCLICK ) {
			
			locator = "id=zti__main_Contacts__"+ folder.getId() +"_textCell";
			
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Unable to locator folder in tree "+ locator);
			}

			this.zClick(locator);
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
