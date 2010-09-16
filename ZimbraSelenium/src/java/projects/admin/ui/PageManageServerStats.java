/**
 * 
 */
package projects.admin.ui;

import framework.ui.AbsApplication;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageManageServerStats extends AbsAdminPage {

	public PageManageServerStats(AbsApplication application) {
		super(application);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsAdminPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {
		throw new HarnessException("implement me");
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsAdminPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsAdminPage#navigateTo()
	 */
	@Override
	public void navigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}

}
