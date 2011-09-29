/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.octopus.ui.PageOctopus;

public class AppOctopusClient extends AbsApplication {

	// Pages
	public PageLogin zPageLogin = null;
	public PageOctopus zPageOctopus = null;
	public PageMyFiles zPageMyFiles = null;
	public PageSharing zPageSharing = null;
	public PageFavorites zPageFavorites = null;
	public PageHistory zPageHistory = null;
	public PageTrash zPageTrash = null;
	public PageSearch zPageSearch = null;

	public AppOctopusClient() {
		super();

		logger.info("new " + AppOctopusClient.class.getCanonicalName());

		// Login page
		zPageLogin = new PageLogin(this);
		pages.put(zPageLogin.myPageName(), zPageLogin);

		// Main Octopus page
		zPageOctopus = new PageOctopus(this);
		pages.put(zPageOctopus.myPageName(), zPageOctopus);

		// My Files page
		zPageMyFiles = new PageMyFiles(this);
		pages.put(zPageMyFiles.myPageName(), zPageMyFiles);

		// Sharing page
		zPageSharing = new PageSharing(this);
		pages.put(zPageSharing.myPageName(), zPageSharing);

		// Favorites page
		zPageFavorites = new PageFavorites(this);
		pages.put(zPageFavorites.myPageName(), zPageFavorites);

		// History page
		zPageHistory = new PageHistory(this);
		pages.put(zPageHistory.myPageName(), zPageHistory);

		// Trash page
		zPageTrash = new PageTrash(this);
		pages.put(zPageTrash.myPageName(), zPageTrash);

		// Search page
		zPageSearch = new PageSearch(this);
		pages.put(zPageSearch.myPageName(), zPageSearch);

		// Configure the localization strings
		getL10N().zAddBundlename(I18N.Catalog.I18nMsg);
		getL10N().zAddBundlename(I18N.Catalog.AjxMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZsMsg);
		getL10N().zAddBundlename(I18N.Catalog.ZmMsg);
	}

	@Override
	public boolean zIsLoaded() throws HarnessException {
		if (this.zPageOctopus.zIsActive() || this.zPageLogin.zIsActive()) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String myApplicationName() {
		return ("Octopus Client");
	}

	public ZimbraAccount zSetActiveAcount(ZimbraAccount account)
			throws HarnessException {
		return (super.zSetActiveAcount(account));
	}

	public ZimbraAccount zGetActiveAcount() {
		return (super.zGetActiveAccount());
	}
}
