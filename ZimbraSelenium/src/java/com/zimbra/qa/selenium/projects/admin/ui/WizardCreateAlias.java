/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.AbsWizard;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.AliasItem;


/**
 * @author Matt Rhoades
 *
 */
public class WizardCreateAlias extends AbsWizard {
	public static class Locators {
		public static final String zdlg_NEW_ALIAS = "zdlg__NEW_ALIAS";
		public static final String zdlg_ALIAS_NAME = "zdlgv__NEW_ALIAS_name_2";
		public static final String zdlg_ALIAS_DOMAIN_NAME="zdlgv__NEW_ALIAS_name_3_display";
		public static final String zdlg_TARGET_ACCOUNT_NAME="zdlgv__NEW_ALIAS_targetName_display";
		public static final String zdlg_OK="zdlg__NEW_ALIAS_button2_title";
	}

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

		sType(Locators.zdlg_ALIAS_NAME, CN);
		
		/**
		 * If you use normal type method domain is taken as default domain name.
		 * Below line of code is not grid friendly but this is only solution working currently. 
		 */
		
		sType(Locators.zdlg_ALIAS_DOMAIN_NAME,"");
		zType(Locators.zdlg_ALIAS_DOMAIN_NAME,"");
		this.zKeyboard.zTypeCharacters(domain);
		System.out.println(domain);
		
		sType(Locators.zdlg_TARGET_ACCOUNT_NAME, targetAccount);
		zClick(Locators.zdlg_OK);

		return alias;

	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		boolean present = sIsElementPresent(Locators.zdlg_NEW_ALIAS);
		if ( !present ) {
			return (false);
		}

		boolean visible = this.zIsVisiblePerPosition(Locators.zdlg_NEW_ALIAS, 0, 0);
		if ( !visible ) {
			return (false);
		}

		return (true);
	}

}
