/**
 * 
 */
package projects.admin.ui;

import projects.admin.items.Item;
import framework.ui.AbsWizard;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class WizardCreateResource extends AbsWizard {

	public WizardCreateResource(AbsAdminPage page) {
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
