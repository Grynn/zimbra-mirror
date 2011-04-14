/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import java.util.*;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;


/**
 * Admin Console -> Addresses -> Accounts
 * @author Matt Rhoades
 *
 */
public class PageManageAccounts extends AbsTab {

	public static class Locators {

		// ** OverviewTreePanel -> Addresses -> Accounts
		public static final String zti__ACCOUNTS = "zti__AppAdmin__ADDRESS__ACCOUNT_textCell";

		// ** "Manage Accounts" Tab Title
		public static final String ztab__MANAGE_ACCOUNT_ICON = "css=tr#ztab__MAIN_TAB_row div.ImgAccount";

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


	}





	public PageManageAccounts(AbsApplication application) {
		super(application);

		logger.info("new " + myPageName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the Admin Console is loaded in the browser
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");


		boolean present = sIsElementPresent(Locators.ztab__MANAGE_ACCOUNT_ICON);
		if ( !present ) {
			return (false);
		}

		boolean visible = zIsVisiblePerPosition(Locators.ztab__MANAGE_ACCOUNT_ICON, 0, 0);
		if ( !visible ) {
			logger.debug("isActive() visible = "+ visible);
			return (false);
		}

		return (true);

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {

		if ( zIsActive() ) {
			// This page is already active.
			return;
		}

		// Click on Addresses -> Accounts
		zClick(Locators.zti__ACCOUNTS);

		zWaitForActive();

	}

	@Override
	public AbsPage zListItem(Action action, String item)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;	
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {

		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_NEW ) {

			// New button
			locator = Locators.zb__ACLV__NEW_MENU_title;

			// Create the page
			page = new WizardCreateAccount(this);

			// FALL THROUGH

		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);

		// If the app is busy, wait for it to become active
		// Is this applicable for the Admin Console?
		// this.zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		//	if ( page != null ) {
		//	zIsActive();
		//	}


		return (page);


	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");

		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");


		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_NEW) {

			if (option == Button.O_ACCOUNTS_ACCOUNT) {

				pulldownLocator = Locators.zb__ACLV__NEW_MENU_title; // TODO: Probably need to change this to the triangle icon/pulldown
				optionLocator = PageManageAccounts.Locators.zmi__ACLV__NEW_WIZARD_title;

				page = new WizardCreateAccount(this);

				// FALL THROUGH

			} else {
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
			}

		} else {
			throw new HarnessException("no logic defined for pulldown/option "
					+ pulldown + "/" + option);
		}

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option " + option + " pulldownLocator " + pulldownLocator + " not present!");
			}

			this.zClick(pulldownLocator);

			// If the app is busy, wait for it to become active
			//zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown + " option " + option + " optionLocator " + optionLocator + " not present!");
				}

				this.zClick(optionLocator);

				// If the app is busy, wait for it to become active
				//zWaitForBusyOverlay();
			}

		}

		// Return the specified page, or null if not set
		return (page);

	}

	/**
	 * Return a list of all accounts in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<AccountItem> zListGetAccounts() {

		/*

		Need to process the div list:

		<div id="zl__ACCT_MANAGE" ...>

			<div id="zl__DWT98_rows ...>

				<div id="zli__DWT98_<accountid>" ...>

					<table>
						<td nowrap="" width="220"> <<<=== Selenium will need some sort of identifier here
							<nobr>email@domain.com</nobr>
						</td>
					</table>

				</div>
			</div>
		</div>
		 */

		List<AccountItem> accounts = new ArrayList<AccountItem>();
		return (accounts);

	}


}
