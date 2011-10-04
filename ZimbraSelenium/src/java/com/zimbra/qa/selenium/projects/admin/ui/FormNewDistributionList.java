package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;
import com.zimbra.qa.selenium.projects.admin.ui.WizardCreateAlias.Locators;

public class FormNewDistributionList extends AbsForm {
	public static class Locators {
		public static final String ztb_ACCT_NAME = "css=input[id^='ztabv__UNDEFINE_']";
		public static final String ztb_DOMAIN_NAME="css=input[id$='_name_3_display']";
		public static final String zb_SAVE="zb__DLV__SAVE_title";
		public static final String zb_CLOSE="zb__DLV__CLOSE_title";
	}

	public FormNewDistributionList(AbsApplication application) {
		super(application);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		if ( !(item instanceof DistributionListItem) )
			throw new HarnessException("item must be an DistributionListItem, was "+ item.getClass().getCanonicalName());

		DistributionListItem dl = (DistributionListItem)item;

		String CN = dl.getLocalName();
		String domain = dl.getDomainName();

		sType(Locators.ztb_ACCT_NAME, CN);

		/**
		 * If you use normal type method domain is taken as default domain name.
		 * Below line of code is not grid friendly but this is only solution working currently. 
		 */
		zType(Locators.ztb_DOMAIN_NAME,"");
		this.zKeyboard.zTypeCharacters(domain);

	}

	@Override
	public void zSubmit() throws HarnessException {		zClick(Locators.zb_SAVE);
		zClick(Locators.zb_CLOSE);
	}

	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
