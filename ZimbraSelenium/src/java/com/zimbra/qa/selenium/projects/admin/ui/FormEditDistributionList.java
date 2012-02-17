package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsForm;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class FormEditDistributionList extends AbsForm {

	public static class TreeItem {
		public static final String MEMBERS="Members";
		public static final String Notes="Notes";
		public static final String MEMBERS_OF="Members Of";
		public static final String ALIASES="Aliases";
		public static final String OWNERS="Owners";
		public static final String PREFERENCES="Preferences";
		public static final String ACL="ACL";
	}
	
	public static class Locators {
		public static final String NAME_TEXT_BOX="css=input.admin_xform_name_input";
		public static final String SAVE_BUTTON="css=td[id^='zb__ZaCurrentAppBar__SAVE']";
		public static final String CLOSE_BUTTON="css=td[id^='zb__ZaCurrentAppBar__CLOSE']";
	}

	public FormEditDistributionList(AbsApplication application) {
		super(application);
		
		logger.info("new " + myPageName());

	}

	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the Admin Console is loaded in the browser
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");

		
		boolean present = sIsElementPresent("");
		if ( !present ) {
			return (false);
		}
		
		String attrs = sGetAttribute("");
		if ( !attrs.contains("ZSelected") ) {
			return (false);
		}

		return (true);
		
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zFill(IItem item) throws HarnessException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void zSubmit() throws HarnessException {
		sClickAt(Locators.SAVE_BUTTON,"");
		sClickAt(Locators.CLOSE_BUTTON,"");
	}
	
	public void zClickTreeItem(String treeItem) throws HarnessException {
		sClickAt("css=td:contains('" + treeItem + "')", "");
	}
	
	public void setName(String name) throws HarnessException {
		sType(Locators.NAME_TEXT_BOX, name);
	}
}
