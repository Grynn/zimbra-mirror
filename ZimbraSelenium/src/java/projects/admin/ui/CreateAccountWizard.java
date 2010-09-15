/**
 * 
 */
package projects.admin.ui;

import projects.admin.clients.Item;
import framework.util.HarnessException;

/**
 * @author zimbra
 *
 */
public class CreateAccountWizard extends AbsWizard {

	public CreateAccountWizard(AbsPage page) {
		super(page);
		logger.info("New "+ CreateAccountWizard.class.getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsWizard#completeWizard(projects.admin.clients.Item)
	 */
	@Override
	public Item completeWizard(Item item) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public boolean isOpen() throws HarnessException {
		throw new HarnessException("implement me!");
	}

}
