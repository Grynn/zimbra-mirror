package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.AbsWizard;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.CosItem;

public class WizardCreateCos extends AbsWizard {
	
	public static class Locators {
		public static final String zdlg_COS_NAME="zdlgv__NEW_COS_cn";
	}

	public WizardCreateCos(AbsTab page) {
		super(page);
		// TODO Auto-generated constructor stub
	}

	@Override
	public IItem zCompleteWizard(IItem item) throws HarnessException {

		if ( !(item instanceof CosItem) )
			throw new HarnessException("item must be an COSItem, was "+ item.getClass().getCanonicalName());


		CosItem domain = (CosItem)item;

		String cosName = domain.getName();


		/**
		 * If you use normal type method domain is taken as default domain name.
		 * Below line of code is not grid friendly but this is only solution working currently. 
		 */
		zType(Locators.zdlg_COS_NAME,"");
		this.zKeyboard.zTypeCharacters(cosName);

		clickFinish(AbsWizard.Locators.COS_DIALOG);

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
