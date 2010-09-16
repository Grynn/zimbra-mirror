package projects.admin.ui;

import framework.ui.AbsApplication;


/**
 * The "Manage Zimlets" has the same functionality as "Manage Admin Extensions"
 * @author Matt Rhoades
 *
 */
public class PageManageZimlets extends PageManageAdminExtensions {

	public PageManageZimlets(AbsApplication application) {
		super(application);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsAdminPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}


}
