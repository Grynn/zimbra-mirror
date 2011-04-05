package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.ui.PageManageAccounts.Locators;



public class PageSearchResults extends AbsTab {

	public static final String SEARCH_INPUT_TEXT_BOX="_XForm_2_query";
	public static final String SEARCH_BUTTON="css=div.ImgSearch";
	

	public PageSearchResults(AbsApplication application) {
		super(application);
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		throw new HarnessException("implement me");
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	public Boolean getSearchResults(String query) throws HarnessException {
		sType(SEARCH_INPUT_TEXT_BOX, query);
		sClick(SEARCH_BUTTON);
		if(sIsElementPresent("css=div#zl__SEARCH_MANAGE td:contains('"+query+"')")) {
			zClick(Locators.zti__AppAdmin__ADDRESS__ACCOUNT_textCell);
			return true;
		}
		zClick(Locators.zti__AppAdmin__ADDRESS__ACCOUNT_textCell);
		return false;
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
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
			throws HarnessException {
		// TODO Auto-generated method stub
		return null;	
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

}
