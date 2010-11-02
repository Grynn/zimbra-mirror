/**
 * 
 */
package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsPage;


/**
 * This class sits between the page classes and the AbsPage abstract classes. It
 * defines the "MyApplication" object, which is an Mobile Client application
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsAjaxPage extends AbsPage {

	protected AppAjaxClient MyApplication = null;

	public AbsAjaxPage(AbsApplication application) {
		super(application);
		
		logger.info("new " + AppAjaxClient.class.getCanonicalName());
		
		MyApplication = (AppAjaxClient)application;
		
	}


}
