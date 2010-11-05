/**
 * 
 */
package projects.ajax.ui;

import projects.ajax.ui.Actions.Action;
import projects.ajax.ui.Buttons.Button;
import framework.ui.AbsApplication;
import framework.ui.AbsPage;
import framework.ui.AbsSeleniumObject;
import framework.util.HarnessException;


/**
 * This class sits between the page classes and the AbsPage abstract classes. It
 * defines the "MyApplication" object, which is an Mobile Client application
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsAjaxPage extends AbsPage {

	public enum ItemType {
		Mail, Contact, ContactGroup, Appointment, Task, Document, Folder, Tag, Addressbook, Calendar, TaskFolder, Briefcase
	}
	
	protected AppAjaxClient MyApplication = null;

	public AbsAjaxPage(AbsApplication application) {
		super(application);
		
		logger.info("new " + AppAjaxClient.class.getCanonicalName());
		
		MyApplication = (AppAjaxClient)application;
		
	}

	/**
	 * Take action on list items
	 * 
	 * (mainly applies to mail, contacts, tasks)
	 * For mail, item identifier is the subject.
	 * For contacts, item identifier is the email.
	 * For tasks, item identifier is the summary.
	 * 
	 * @param action See Actions class
	 * @param item The item identifier
	 * @return
	 * @throws HarnessException
	 */
	public abstract AbsSeleniumObject zListItem(Action action, String item) throws HarnessException;
	
	/**
	 * Take action on list items with optional action
	 * (mainly right-click -> context menu)
	 */
	public abstract AbsSeleniumObject zListItem(Action action, Action option, String item) throws HarnessException;
	
	/**
	 * Click on a button
	 * @param button the button to press
	 * @return Returns the resulting Page, Wizard, etc. or null
	 * @throws HarnessException
	 */
	public abstract AbsSeleniumObject zToolbarPressButton(Button button) throws HarnessException;
	
	
	/**
	 * Click on a pulldown with the specified option in the pulldown
	 * @param pulldown
	 * @param option
	 * @return Returns the resulting Page, Wizard, etc. or null
	 * @throws HarnessException
	 */
	public abstract AbsSeleniumObject zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException;
	

}
