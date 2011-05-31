package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

/**
 * This class defines the Downloads page (click on "Downloads" in the header)
 * @author Matt Rhoades
 *
 */
public class PageDownloads extends AbsTab {

	public static class Locators {
		
		// Downloads link
		public static final String DownloadsLink = "css=td[id='skin_container_dw'] span";
		public static final String TabLoaded = "//div[contains(text(),'Zimbra Utilities Downloads')]";

		// index.html
		public static final String IndexHtmlTitleLocator = "css=title:contains('Downloads')";

	}
	
	public PageDownloads(AbsApplication application) {
		super(application);

		logger.info("new " + myPageName());

	}

	@Override
	public void zNavigateTo() throws HarnessException {

		
		if ( zIsActive() ) {
			// This page is already active.
			return;
		}
		
		// Make sure we are logged into the Mobile app
		if ( !((AppAdminConsole)MyApplication).zPageMain.zIsActive() ) {
			((AppAdminConsole)MyApplication).zPageMain.zNavigateTo();
		}

		tracer.trace("Navigate to "+ this.myPageName());

		String locator = Locators.DownloadsLink;

		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Downloads link is not present");

		this.sClick(locator);

		zWaitForActive();

	}

	@Override
	public boolean zIsActive() throws HarnessException {

		// If the "folders" tree is visible, then mail is active
		String locator = Locators.TabLoaded;

		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);

		return (true);

	}


	@Override
	public AbsPage zListItem(Action action, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not contain lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not contain lists");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption, String item) throws HarnessException {
		throw new HarnessException(myPageName() + " does not contain lists");
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		throw new HarnessException(myPageName() + " does not contain a toolbar");
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		throw new HarnessException(myPageName() + " does not contain a toolbar");
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	
	/**
	 * Open http://server.com/zimbra/downloads/index.html
	 * @throws HarnessException 
	 */
	public void zOpenIndexHTML() throws HarnessException {

		String base = ZimbraSeleniumProperties.getBaseURL();
		String path = "/zimbra/downloads/index.html";
		String id = ZimbraSeleniumProperties.getUniqueString();
		
		this.sOpenWindow(base + path, id);
		this.zSelectWindow(id);
		SleepUtil.sleepLong();
		
		// Make sure the page is active
		if ( !this.sIsElementPresent(Locators.IndexHtmlTitleLocator) )
			throw new HarnessException("index.html never became active/focused");
		
	}


	
	
	


}
