/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;

/**
 * The "Edit Alias" page opens the "Edit Account" page
 * @author Matt Rhoades
 *
 */
public class PageEditAlias extends PageEditAccount {

	public PageEditAlias(AbsApplication application) {
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
