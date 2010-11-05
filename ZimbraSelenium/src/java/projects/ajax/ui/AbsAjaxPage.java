/**
 * 
 */
package projects.ajax.ui;

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
