package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.AbsWizard;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;

public class WizardCreateDL extends AbsWizard {
	public static class Locators {
		public static final String zdlg_DL_NAME = "zdlgv__NEW_DL_name";
		public static final String zdlg_DOMAIN_NAME="zdlgv__NEW_DL_name_2_display";
	}

	public WizardCreateDL(AbsTab page) {
		super(page);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {

		if ( !(item instanceof DistributionListItem) )
			throw new HarnessException("item must be an DistributionListItem, was "+ item.getClass().getCanonicalName());

		DistributionListItem dl = (DistributionListItem)item;

		String CN = dl.getLocalName();
		String domain = dl.getDomainName();


		zType(Locators.zdlg_DL_NAME, CN);
		zType(Locators.zdlg_DOMAIN_NAME, "");
		this.zKeyboard.zTypeCharacters(domain);
		
		clickFinish(AbsWizard.Locators.DL_DIALOG);

		return (dl);
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

	public void zComplete(DistributionListItem dl) {
		// TODO Auto-generated method stub
		
	}

}
