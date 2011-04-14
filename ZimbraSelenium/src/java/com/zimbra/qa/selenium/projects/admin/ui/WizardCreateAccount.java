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

	public static final String zdlg_NEW_ACCT = "zdlg__NEW_ACCT";
	public static final String zdlg_ACCT_NAME = "zdlgv__NEW_ACCT_name_2";
	public static final String zdlg_DOMAIN_NAME="zdlgv__NEW_ACCT_name_3_display";
	public static final String zdlg_LAST_NAME="zdlgv__NEW_ACCT_sn";

	public WizardCreateAccount(AbsTab page) {
		super(page);
		logger.info("New "+ WizardCreateAccount.class.getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsWizard#completeWizard(projects.admin.clients.Item)
	 */
	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {
		if(zIsActive()) {
			if ( !(item instanceof AccountItem) )
				throw new HarnessException("item must be an AccountItem, was "+ item.getClass().getCanonicalName());

			AccountItem account = (AccountItem)item;

			String CN = account.getLocalName();
			String domain = account.getDomainName();


			zType(zdlg_ACCT_NAME, CN);

			zType(zdlg_DOMAIN_NAME, domain);

			for (String key : account.getAccountAttrs().keySet()) {

				// TODO: Handle Previous/Next to find the input field, if necessary

				if ( key.equals("sn")) {

					zType(zdlg_LAST_NAME, account.getAccountAttrs().get(key));
					continue;
				}

				// TODO: add all account keys

				throw new HarnessException("Unknown account attribute key "+ key);

			}

			clickFinish();
			return (account);
		}else 
			return null;

	}

	@Override
	public boolean zIsActive() throws HarnessException {

		boolean present = sIsElementPresent(zdlg_NEW_ACCT);
		if ( !present ) {
			return (false);
		}

		boolean visible = this.zIsVisiblePerPosition(zdlg_NEW_ACCT, 0, 0);
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


}
