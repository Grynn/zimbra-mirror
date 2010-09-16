/**
 * 
 */
package projects.admin.ui;

import projects.admin.items.Item;
import framework.ui.AbsWizard;
import framework.util.HarnessException;

/**
 * @author zimbra
 *
 */
public class WizardCreateAccount extends AbsWizard {

	// Wizard Navigation Buttons
	public static final String DWT279_title = "xpath=//*[@id='DWT279_title']"; // "Cancel" button
	public static final String DWT280_title = "xpath=//*[@id='DWT280_title']"; // "Help" button
	public static final String DWT281_title = "xpath=//*[@id='DWT281_title']"; // "Previous" button
	public static final String DWT282_title = "xpath=//*[@id='DWT282_title']"; // "Next" button
	public static final String DWT283_title = "xpath=//*[@id='DWT283_title']"; // "Finish" button

	public WizardCreateAccount(AbsAdminPage page) {
		super(page);
		logger.info("New "+ WizardCreateAccount.class.getName());
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
