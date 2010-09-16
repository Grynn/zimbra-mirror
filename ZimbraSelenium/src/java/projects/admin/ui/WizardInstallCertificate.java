/**
 * 
 */
package projects.admin.ui;

import projects.admin.items.Item;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class WizardInstallCertificate extends AbsWizard {

	public WizardInstallCertificate(AbsPage page) {
		super(page);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsWizard#completeWizard(projects.admin.clients.Item)
	 */
	@Override
	public Item completeWizard(Item item) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsWizard#isOpen()
	 */
	@Override
	public boolean isOpen() throws HarnessException {
		throw new HarnessException("implement me");
	}

}
