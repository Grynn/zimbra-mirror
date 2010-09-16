/**
 * 
 */
package projects.admin.ui;

import projects.admin.items.AccountItem;
import framework.util.HarnessException;

/**
 * Admin Console -> Addresses -> Accounts
 * @author Matt Rhoades
 *
 */
public class PageManageAccounts extends AbsPage {

	// ** OverviewTreePanel -> Addresses -> Accounts
	public static final String zti__AppAdmin__ADDRESS__ACCOUNT_textCell = "xpath=//*[@id='zti__AppAdmin__ADDRESS__ACCOUNT_textCell']";
	
	// ** "Manage Accounts" Tab Title
	public static final String DWT93 = "xpath=//*[@id='DWT93']";
	public static final String DWT93_classAttr = "xpath=(//*[@id='DWT93'])@class";
	
	// ** Menus
	public static final String zb__ACLV__NEW_MENU_title = "xpath=//*[@id='zb__ACLV__NEW_MENU_title']";		// New Button
	public static final String zb__ACLV__EDIT_title = "xpath=//*[@id='zb__ACLV__EDIT_title']";
	public static final String zb__ACLV__DELETE_title = "xpath=//*[@id='zb__ACLV__DELETE_title']";
	public static final String zb__ACLV__CHNG_PWD_title = "xpath=//*[@id='zb__ACLV__CHNG_PWD_title']";
	public static final String zb__ACLV__EXPIRE_SESSION_title = "xpath=//*[@id='zb__ACLV__EXPIRE_SESSION_title']";
	public static final String zb__ACLV__VIEW_MAIL_title = "xpath=//*[@id='zb__ACLV__VIEW_MAIL_title']";
	public static final String zb__ACLV__UNKNOWN_66_title = "xpath=//*[@id='zb__ACLV__UNKNOWN_66_title']"; // Search Mail
	public static final String zb__ACLV__UNKNOWN_72_title = "xpath=//*[@id='zb__ACLV__UNKNOWN_72_title']"; // Move Mailbox
	public static final String zb__ACLV__MORE_ACTIONS_title = "xpath=//*[@id='zb__ACLV__MORE_ACTIONS_title']";
	public static final String zb__ACLV__PAGE_BACK_title = "xpath=//*[@id='zb__ACLV__PAGE_BACK_title']";
	public static final String zb__ACLV__PAGE_FORWARD_title = "xpath=//*[@id='zb__ACLV__PAGE_FORWARD_title']";
	public static final String zb__ACLV__HELP_title = "xpath=//*[@id='zb__ACLV__HELP_title']";

	
	// NEW Menu
	// TODO: define these locators
	public static final String zmi__ACLV__NEW_WIZARD_title = "xpath=//*[@id='zmi__ACLV__NEW_WIZARD_title']";	// New -> Account (<td class="ZWidgetTitle" id="zmi__ACLV__NEW_WIZARD_title">Account</td>)
	

	
	
	
	
	public PageManageAccounts(AbsApplication application) {
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
	 * Click Previous/Next in the list
	 * @param button
	 * @throws HarnessException If the button is not active, throw Exception
	 */
	public void clickNavigation(ListNavButton button) throws HarnessException {
		// TODO: If button is not enabled, thrown HarnessException
		
		// TODO: click on the button
		
		throw new HarnessException("implement me");
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
		WizardCreateAccount wizard = getNewAccountWizard(zb__ACLV__NEW_MENU_title);
		AccountItem a = (AccountItem)wizard.completeWizard(account);
		
		// Return the account
		return (a);
	}

	/**
	 * Get the "New Account" wizard by clicking on the specified locator
	 * @param locator "New" or "New -> Account"
	 * @return
	 * @throws HarnessException
	 */
	public WizardCreateAccount getNewAccountWizard(String locator) throws HarnessException {
		
		// Make sure the Manage Accounts page is showing
		navigateTo();

		// Click on "New"
		click(locator);

		WizardCreateAccount wizard = new WizardCreateAccount(this);
		if ( !wizard.isOpen() )
			throw new HarnessException("Clicking on locator "+ locator +" did not open wizard");
		
		// Return the Wizard object
		return (wizard);
	}

	/**
	 * Edit the specified account by selecting the account in the list and clicking "Edit"
	 * @param emailaddress
	 * @throws HarnessException
	 */
	public void editAccount(String emailaddress) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/**
	 * Delete the specified account by selecting the account in the list and clicking "Delete"
	 * @param emailaddress
	 * @param button Click on this button in the dialog
	 * @throws HarnessException
	 */
	public void deleteAccount(String emailaddress, PopupButton button) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/**
	 * Change the password for the specified account by selecting the account in the list and clicking "Change Password"
	 * @param emailaddress
	 * @param password
	 * @param confirm
	 * @param mustChangePassword
	 * @param button Click on this button in the dialog
	 * @throws HarnessException
	 */
	public void changePasswordAccount(String emailaddress, String password, String confirm, boolean mustChangePassword, PopupButton button) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/**
	 * Expire the account sessions for the specified account by selecting the account in the list and clicking "Expire Sessions"
	 * @param emailaddress
	 * @param button Click on this button in the dialog
	 * @throws HarnessException
	 */
	public void expireSessionsAccount(String emailaddress, PopupButton button) throws HarnessException {
		throw new HarnessException("implement me");
	}


	/**
	 * View the mailbox for the specified account by selecting the account in the list and clicking "View Mail"
	 * @param emailaddress
	 * @throws HarnessException
	 */
	public void viewMailAccount(String emailaddress) throws HarnessException {
		throw new HarnessException("implement me");
	}

	/**
	 * Search Mail (opens PageEditSearch object)
	 * @param emailaddress
	 * @throws HarnessException
	 */
	public void searchMailAccount(String emailaddress) throws HarnessException {
		throw new HarnessException("implement me");
	}


}
