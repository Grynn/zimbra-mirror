/**
 * 
 */
package projects.admin.ui;

import framework.items.IItem;
import framework.ui.AbsTab;
import framework.ui.AbsWizard;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class WizardCreateAlias extends AbsWizard {

	public WizardCreateAlias(AbsTab page) {
		super(page);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsWizard#isOpen()
	 */
	@Override
	public boolean zIsOpen() throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
