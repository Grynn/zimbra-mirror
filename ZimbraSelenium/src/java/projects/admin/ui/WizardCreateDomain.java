/**
 * 
 */
package projects.admin.ui;

import framework.items.IItem;
import framework.ui.AbsWizard;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class WizardCreateDomain extends AbsWizard {

	public WizardCreateDomain(AbsAdminPage page) {
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

}
