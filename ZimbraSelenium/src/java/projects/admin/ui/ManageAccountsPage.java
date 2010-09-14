/**
 * 
 */
package projects.admin.ui;

import projects.admin.clients.AccountItem;
import framework.util.HarnessException;

/**
 * Admin Console -> Addresses -> Accounts
 * @author Matt Rhoades
 *
 */
public class ManageAccountsPage extends AbsPage {

	// ** OverviewTreePanel -> Addresses -> Accounts
	public static final String zti__AppAdmin__ADDRESS__ACCOUNT_textCell = "xpath=//*[@id='zti__AppAdmin__ADDRESS__ACCOUNT_textCell']";
	
	// ** "Manage Accounts" Title
	public static final String DWT93_title = "xpath=//*[@id='DWT93_title']";
	
	// ** Menus
	// NEW Menu
	public static final String zb__ACLV__NEW_MENU_title = "xpath=//*[@id='zb__ACLV__NEW_MENU_title']";		// New Button
	public static final String zmi__ACLV__NEW_WIZARD_title = "xpath=//*[@id='zmi__ACLV__NEW_WIZARD_title']";	// New -> Account
	

	// New Account Wizard
	public static final String DWT279_title = "xpath=//*[@id='DWT279_title']"; // "Cancel" button
	public static final String DWT280_title = "xpath=//*[@id='DWT280_title']"; // "Help" button
	public static final String DWT281_title = "xpath=//*[@id='DWT281_title']"; // "Previous" button
	public static final String DWT282_title = "xpath=//*[@id='DWT282_title']"; // "Next" button
	public static final String DWT283_title = "xpath=//*[@id='DWT283_title']"; // "Finish" button
	
	
	
	
	public ManageAccountsPage(AbsApplication application) {
		super(application);

		logger.info("new " + myPageName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {
		
		// If "Manage Accounts" tab is active, then the Accounts Pane is active
		//
		// *The tab must be _active_, i.e. not a hidden tab
		//
		// TODO: Need to figure out how to make sure this tab is active
		// TODO: I suppose the DWT93 ID will be changing as part of bug 46006
		//
//		boolean present = isElementPresent(DWT93);
//		if ( !present ) {
//			return (false);
//		}
//		
//		boolean visible = super.isVisible(DWT93);
//		if ( !visible ) {
//			return (false);
//		}
		
		return (true);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void navigateTo() throws HarnessException {

		if ( isActive() ) {
			// This page is already active.
			return;
		}
		
		// Make sure the Admin Console is loaded in the browser
		if ( MyApplication.isLoaded() )
			throw new HarnessException("Admin Console application is not active!");

		
		// Click on Addresses -> Accounts
		super.click(zti__AppAdmin__ADDRESS__ACCOUNT_textCell);
		
		waitForActive();

	}

	public AccountItem createAccount(AccountItem account) throws HarnessException {
		logger.debug("createAccount(AccountItem account)" + account.EmailAddress);

		navigateTo();
		
		// Click on "New -> Account"
		click(zmi__ACLV__NEW_WIZARD_title);
		completeNewAccountWizard(account);
		
		// TODO: add any additional AccountItem fields as necessary
		
		// Return the account
		return (account);
	}


	/**
	 * Fill out all the fields in the new account wizard, but don't
	 * "Finish" the wizard
	 * 
	 * @param account
	 * @throws HarnessException
	 */
	public AccountItem fillNewAccountWizard(AccountItem account) throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	/**
	 * Fill out all the fields in the new account wizard, and complete
	 * the wizard by clicking "Finish" 
	 * @param account
	 * @throws HarnessException
	 */
	public AccountItem completeNewAccountWizard(AccountItem account) throws HarnessException {
		
		fillNewAccountWizard(account);
		
		click(DWT283_title);
		
		return (account);
	}
}
