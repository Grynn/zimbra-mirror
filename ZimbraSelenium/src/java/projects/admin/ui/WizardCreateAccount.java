/**
 * 
 */
package projects.admin.ui;

import projects.admin.items.AccountItem;
import projects.admin.items.Item;
import framework.ui.AbsWizard;
import framework.util.HarnessException;

/**
 * @author zimbra
 *
 */
public class WizardCreateAccount extends AbsWizard {

	public static final String zdlg__NEW_ACCT = "xpath=//*[@id='zdlg__NEW_ACCT']";

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
		
		AccountItem account = (AccountItem)item;
		
		String CN = account.EmailAddress.split("@")[0];
		String domain = account.EmailAddress.split("@")[1];
		
		type("xpath=//@[@id='_XForm_3_name_2']", CN);
		type("xpath=//@[@id='_XForm_3_name_3_display']", domain);
		
		for (String key : account.AccountAttrs.keySet()) {
			
			// TODO: Handle Previous/Next to find the input field, if necessary
			
			if ( key.equals("givenName")) {
				type("xpath=//@[@id='_XForm_3_givenName']", account.AccountAttrs.get(key));
				continue;
			}

			// TODO: add all account keys
			
			throw new HarnessException("Unknown account attribute key "+ key);
			
		}
		
		
		return (account);
		
	}

	@Override
	public boolean isOpen() throws HarnessException {
		
		boolean present = isElementPresent(zdlg__NEW_ACCT);
		if ( !present ) {
			return (false);
		}
		
		boolean visible = this.isVisiblePerPosition(zdlg__NEW_ACCT, 0, 0);
		if ( !visible ) {
			return (false);
		}

		return (true);
	}

}
