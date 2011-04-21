package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.CosItem;

public class PageNewCos extends AbsTab {

	public static final String ztav_COS_NAME = "ztabv__COS_EDIT_cn_2";
	public static final String zb_SAVE="zb__COSV__SAVE_title";
	public static final String zb_CLOSE="zb__COSV__CLOSE_title";

	public PageNewCos(AbsApplication application) {
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

		if ( !(item instanceof CosItem) )
			throw new HarnessException("item must be an CosItem, was "+ item.getClass().getCanonicalName());

		CosItem cos = (CosItem)item;

		String CN = cos.getName();

		sType(ztav_COS_NAME, CN);
		sClick(zb_SAVE);
		sClick(zb_CLOSE);

		return cos;

	}

}
