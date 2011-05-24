package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;

public class FormDistributionListsNew extends AbsForm {
	
	public static final String ztb_ACCT_NAME = "css=input[id^='ztabv__UNDEFINE_']";
	public static final String ztb_DOMAIN_NAME="css=input[id$='_name_3_display']";
	public static final String zb_SAVE="zb__DLV__SAVE_title";
	public static final String zb_CLOSE="zb__DLV__CLOSE_title";

	public FormDistributionListsNew(AbsApplication application) {
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

		sType(ztb_ACCT_NAME, CN);
		sType(ztb_DOMAIN_NAME, domain);

	}

	@Override
	public void zSubmit() throws HarnessException {		sClick(zb_SAVE);
		sClick(zb_CLOSE);
	}

	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
