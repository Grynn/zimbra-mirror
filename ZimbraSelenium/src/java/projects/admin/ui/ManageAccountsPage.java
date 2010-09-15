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
	public static final String DWT93 = "xpath=//*[@id='DWT93']";
	public static final String DWT93_classAttr = "xpath=(//*[@id='DWT93'])@class";
	
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
		
		// Make sure the Admin Console is loaded in the browser
		if ( !MyApplication.isLoaded() )
			throw new HarnessException("Admin Console application is not active!");

		
		boolean present = isElementPresent(DWT93);
		if ( !present ) {
			return (false);
		}
		
		String attrs = getAttribute(DWT93_classAttr);
		if ( !attrs.contains("ZSelected") ) {
			return (false);
		}
		
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
		
		// Click on Addresses -> Accounts
		click(zti__AppAdmin__ADDRESS__ACCOUNT_textCell);
		
		waitForActive();

	}

	/**
	 * Create the specified account using the Admin Console
	 * @param account
	 * @return
	 * @throws HarnessException
	 */
	public AccountItem createAccount(AccountItem account) throws HarnessException {
		logger.debug("createAccount(AccountItem account)" + account.EmailAddress);

		// Get the New Account Wizard
		CreateAccountWizard wizard = getNewAccountWizard(zb__ACLV__NEW_MENU_title);
		AccountItem a = (AccountItem)wizard.completeWizard(account);
		
		// Return the account
		return (a);
	}

	/**
	 * Get the "New Account" wizard by clicking on the specified locator
	 * @param locator
	 * @return
	 * @throws HarnessException
	 */
	public CreateAccountWizard getNewAccountWizard(String locator) throws HarnessException {
		
		// Make sure the Manage Accounts page is showing
		navigateTo();

		// Click on "New"
		click(zmi__ACLV__NEW_WIZARD_title);

		CreateAccountWizard wizard = new CreateAccountWizard(this);
		if ( !wizard.isOpen() )
			throw new HarnessException("Clicking on locator "+ locator +" did not open wizard");
		
		// Return the Wizard object
		return (wizard);
	}
}
