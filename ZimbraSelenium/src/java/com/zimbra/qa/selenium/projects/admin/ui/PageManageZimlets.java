package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;


/**
 * The "Manage Zimlets" has the same functionality as "Manage Admin Extensions"
 * @author Matt Rhoades
 *
 */
public class PageManageZimlets extends PageManageAdminExtensions {

	public PageManageZimlets(AbsApplication application) {
		super(application);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}


}
