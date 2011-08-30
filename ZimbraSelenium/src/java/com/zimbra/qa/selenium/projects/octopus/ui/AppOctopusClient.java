/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.octopus.ui.PageMain;

public class AppOctopusClient extends AbsApplication {

	// Pages
	public PageLogin zPageLogin = null;
	public PageMain zPageMain = null;

	public AppOctopusClient() {
		super();

		logger.info("new " + AppOctopusClient.class.getCanonicalName());

		// Login page
		zPageLogin = new PageLogin(this);
		pages.put(zPageLogin.myPageName(), zPageLogin);

		// Main page
		zPageMain = new PageMain(this);
		pages.put(zPageMain.myPageName(), zPageMain);

		// Configure the localization strings
		getL10N().zAddBundlename(I18N.Catalog.I18nMsg);
		getL10N().zAddBundlename(I18N.Catalog.AjxMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZsMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZmMsg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see projects.admin.ui.AbsApplication#isLoaded()
	 */
	@Override
	public boolean zIsLoaded() throws HarnessException {
		if (this.zPageMain.zIsActive() || this.zPageLogin.zIsActive()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String myApplicationName() {
		return ("Octopus Client");
	}

	@Override
	protected ZimbraAccount zSetActiveAcount(ZimbraAccount account)
			throws HarnessException {
		return (super.zSetActiveAcount(account));
	}

}
