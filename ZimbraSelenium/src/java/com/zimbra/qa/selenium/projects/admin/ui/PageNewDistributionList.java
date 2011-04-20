package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.DistributionListItem;

public class PageNewDistributionList extends AbsTab {

	//public static final String zb_INITIAL_ACCT_NAME="ztabv__UNDEFINE_2_name";
	public static final String ztb_ACCT_NAME = "ztabv__UNDEFINE_name_2";
	public static final String ztb_DOMAIN_NAME="ztabv__UNDEFINE_name_3_display";
	public static final String zb_SAVE="zb__DLV__SAVE_title";
	public static final String zb_CLOSE="zb__DLV__CLOSE_title";
	
	public PageNewDistributionList(AbsApplication application) {
		super(application);
		// TODO Auto-generated constructor stub
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
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void zNavigateTo() throws HarnessException {
		// TODO Auto-generated method stub

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;
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
	
	
	public IItem zEnterDetails(IItem item) throws HarnessException {

		if ( !(item instanceof DistributionListItem) )
			throw new HarnessException("item must be an AliasItem, was "+ item.getClass().getCanonicalName());

		DistributionListItem dl = (DistributionListItem)item;

		String CN = dl.getLocalName();
		String domain = dl.getDomainName();

		sType(ztb_ACCT_NAME, CN);
		sType(ztb_DOMAIN_NAME, domain);
		sClick(zb_SAVE);
		sClick(zb_CLOSE);

		return dl;

	}

}
