/**
 * 
 */
package projects.admin.ui;

import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageManageAccountMigration extends AbsPage {

	public PageManageAccountMigration(AbsApplication application) {
		super(application);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {
		throw new HarnessException("implement me");
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void navigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}

}
