/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.AliasItem;


/**
 * @author Matt Rhoades
 *
 */
public class WizardCreateAlias extends AbsWizard {

	public static final String zdlg_NEW_ALIAS = "zdlg__NEW_ALIAS";
	public static final String zdlg_ALIAS_NAME = "zdlgv__NEW_ALIAS_name_2";
	public static final String zdlg_ALIAS_DOMAIN_NAME="zdlgv__NEW_ALIAS_name_3_display";
	public static final String zdlg_TARGET_ACCOUNT_NAME="zdlgv__NEW_ALIAS_targetName_display";
	public static final String zdlg_OK="zdlg__NEW_ALIAS_button2_title";

	public WizardCreateAlias(AbsTab page) {
		super(page);
	}

	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {

		if ( !(item instanceof AliasItem) )
			throw new HarnessException("item must be an AliasItem, was "+ item.getClass().getCanonicalName());

		AliasItem alias = (AliasItem)item;

		String CN = alias.getLocalName();
		String domain = alias.getDomainName();
		String targetAccount = alias.getTargetAccountEmail();

		sType(zdlg_ALIAS_NAME, CN);
		sType(zdlg_ALIAS_DOMAIN_NAME, domain);
		sType(zdlg_TARGET_ACCOUNT_NAME, targetAccount);
		sClick(zdlg_OK);

		return alias;

	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		boolean present = sIsElementPresent(zdlg_NEW_ALIAS);
		if ( !present ) {
			return (false);
		}

		boolean visible = this.zIsVisiblePerPosition(zdlg_NEW_ALIAS, 0, 0);
		if ( !visible ) {
			return (false);
		}

		return (true);
	}

}
