/**
 * 
 */
package projects.mobile.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsPage;


/**
 * This class sits between the page classes and the AbsPage abstract classes. It
 * defines the "MyApplication" object, which is an Mobile Client application
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsMobilePage extends AbsPage {

	protected AppMobileClient MyApplication = null;

	public AbsMobilePage(AbsApplication application) {
		super(application);
		
		logger.info("new " + AppMobileClient.class.getCanonicalName());
		
		MyApplication = (AppMobileClient)application;
		
	}


}
