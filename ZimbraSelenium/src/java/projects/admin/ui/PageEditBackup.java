/**
 * 
 */
package projects.admin.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsPage;
import framework.ui.AbsTab;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageEditBackup extends AbsTab {

	public PageEditBackup(AbsApplication application) {
		super(application);
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {
		throw new HarnessException("implement me");
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Action option, String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

}
