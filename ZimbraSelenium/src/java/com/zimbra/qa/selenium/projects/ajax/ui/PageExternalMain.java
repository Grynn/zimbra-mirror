package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.PageMain.Locators;

public class PageExternalMain extends PageMain {

	public PageExternalMain(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageExternalMain.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Look for the Logout button 
		// check if zimlet + minical loaded
		boolean present = sIsElementPresent(Locators.zLogoffPulldown);
		if ( !present ) {
			logger.debug("Logoff button present = "+ present);
			return (false);
		}

		logger.debug("isActive() = "+ true);
		return (true);

	}


}
