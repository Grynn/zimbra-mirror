/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.AbsWizard;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;


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

	public WizardCreateAccount(AbsTab page) {
		super(page);
		logger.info("New "+ WizardCreateAccount.class.getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsWizard#completeWizard(projects.admin.clients.Item)
	 */
	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {
		
		if ( !(item instanceof AccountItem) )
			throw new HarnessException("item must be an AccountItem, was "+ item.getClass().getCanonicalName());
		
		AccountItem account = (AccountItem)item;
		
		String CN = account.EmailAddress.split("@")[0];
		String domain = account.EmailAddress.split("@")[1];
		
		sType("xpath=//@[@id='_XForm_3_name_2']", CN);
		sType("xpath=//@[@id='_XForm_3_name_3_display']", domain);
		
		for (String key : account.AccountAttrs.keySet()) {
			
			// TODO: Handle Previous/Next to find the input field, if necessary
			
			if ( key.equals("givenName")) {
				sType("xpath=//@[@id='_XForm_3_givenName']", account.AccountAttrs.get(key));
				continue;
			}

			// TODO: add all account keys
			
			throw new HarnessException("Unknown account attribute key "+ key);
			
		}
		
		
		return (account);
		
	}

	@Override
	public boolean zIsOpen() throws HarnessException {
		
		boolean present = sIsElementPresent(zdlg__NEW_ACCT);
		if ( !present ) {
			return (false);
		}
		
		boolean visible = this.zIsVisiblePerPosition(zdlg__NEW_ACCT, 0, 0);
		if ( !visible ) {
			return (false);
		}

		return (true);
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
