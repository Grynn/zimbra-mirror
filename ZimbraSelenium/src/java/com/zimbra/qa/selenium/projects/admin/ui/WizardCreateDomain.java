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
public class WizardCreateDomain extends AbsWizard {

	public static class Locators {
		public static final String zdlg_DOMAIN_NAME="zdlgv__NEW_DOMAIN_zimbraDomainName";
		public static final String MAIL_SERVER_DROPDOWN="css=div[id$='zimbraMailHost_arrow_button']/div";
		public static final String MAIL_SERVER_DROPDOWN_TABLE="css=div#___OSELECT_MENU___";
		public static final String ADD_A_GAL_ACCOUNT_BUTTON="css=button.xform_button:contains('Add a GAL account')";
	}

	public WizardCreateDomain(AbsTab page) {
		super(page);
	}

	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {

		if ( !(item instanceof DomainItem) )
			throw new HarnessException("item must be an DomainItem, was "+ item.getClass().getCanonicalName());


		DomainItem domain = (DomainItem)item;

		String domainName = domain.getName();


		/**
		 * If you use normal type method domain is taken as default domain name.
		 * Below line of code is not grid friendly but this is only solution working currently. 
		 */
		zType(Locators.zdlg_DOMAIN_NAME,"");
		this.zKeyboard.zTypeCharacters(domainName);

		clickNext(AbsWizard.Locators.DOMAIN_DIALOG);
		
		sClickAt(Locators.ADD_A_GAL_ACCOUNT_BUTTON,"");
		sClickAt(Locators.MAIL_SERVER_DROPDOWN, "");
		sClickAt(Locators.MAIL_SERVER_DROPDOWN_TABLE+" div:contains('"+
					ZimbraSeleniumProperties.getStringProperty("server.host")+
					"')", "");
		
		
		clickFinish(AbsWizard.Locators.DOMAIN_DIALOG);

		return (domain);


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
