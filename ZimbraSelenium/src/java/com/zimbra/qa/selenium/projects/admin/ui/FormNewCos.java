package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.items.CosItem;


public class FormNewCos extends AbsForm {


	public static class Locators {
		public static final String ztav_COS_NAME = "ztabv__COS_EDIT_cn_2";
		public static final String zb_SAVE="zb__COSV__SAVE_title";
		public static final String zb_CLOSE="zb__COSV__CLOSE_title";
	}


	public FormNewCos(AbsApplication application) {
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
		if ( !(item instanceof CosItem) )
			throw new HarnessException("item must be an CosItem, was "+ item.getClass().getCanonicalName());

		CosItem cos = (CosItem)item;

		String CN = cos.getName();

		sType(Locators.ztav_COS_NAME, CN);
	}

	@Override
	public void zSubmit() throws HarnessException {
		zClick(Locators.zb_SAVE);
		zClick(Locators.zb_CLOSE);
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
