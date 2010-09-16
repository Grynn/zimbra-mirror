/**
 * 
 */
package projects.admin.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsPage;


/**
 * This class sits between the page classes and the AbsPage abstract classes. It
 * defines the "MyApplication" object, which is an Admin Console application
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsAdminPage extends AbsPage {

	protected AppAdminConsole MyApplication = null;
	
	public AbsAdminPage(AbsApplication application) {
		super(application);
		
		MyApplication = (AppAdminConsole)application;
	}


}
