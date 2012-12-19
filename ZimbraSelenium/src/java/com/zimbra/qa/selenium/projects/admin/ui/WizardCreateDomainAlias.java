/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.AbsWizard;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.admin.items.DomainItem;


/**
 * @author Matt Rhoades
 *
 */
public class WizardCreateDomainAlias extends AbsWizard {
	public static class Locators {
		public static final String DOMAIN_ALIAS_DLG = "zdlgv__UNDEFINE1_zimbraDomainName";
		public static final String DOMAIN_ALIAS_NAME = "zdlgv__UNDEFINE1_zimbraDomainName";
		public static final String TARGET_DOMAIN_NAME="zdlgv__UNDEFINE1_zimbraDomainAliasTargetName_display";
		public static final String zdlg_OK="zdlg__UNDEFINE1_button2_title";
	}

	public WizardCreateDomainAlias(AbsTab page) {
		super(page);
	}

	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {

		if ( !(item instanceof DomainItem) )
			throw new HarnessException("item must be an AliasItem, was "+ item.getClass().getCanonicalName());

		DomainItem alias = (DomainItem)item;

		String domainAlias = alias.getName();
		String targetDomain = ZimbraSeleniumProperties.getStringProperty("testdomain");

		sType(Locators.DOMAIN_ALIAS_NAME, domainAlias);		
		sType(Locators.TARGET_DOMAIN_NAME, targetDomain);
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

		boolean present = sIsElementPresent(Locators.DOMAIN_ALIAS_DLG);
		if ( !present ) {
			return (false);
		}

		boolean visible = this.zIsVisiblePerPosition(Locators.DOMAIN_ALIAS_DLG, 0, 0);
		if ( !visible ) {
			return (false);
		}

		return (true);
	}

}
