package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

/**
 * This class defines the Downloads page (click on "Downloads" in the header)
 * @author Matt Rhoades
 *
 */
public class PageDownloads extends AbsTab {

	public static class Locators {
		
		public static final String TOOLS_AND_MIGRATION_ICON="css=div.ImgToolsAndMigration";
		public static final String DOWNLOADS="css=div[id^='zti__AppAdmin__magHV__download'][id$='div']";
		public static final String HOME="Home";
		public static final String TOOLS_AND_MIGRATION="Tools and Migration";
		public static final String DOWNLOAD="Downloads";
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

		// Click on Tools and Migration -> Downloads
		zClickAt(Locators.TOOLS_AND_MIGRATION_ICON,"");
		if(sIsElementPresent(Locators.DOWNLOADS));
		sClickAt(Locators.DOWNLOADS, "");
		
		zWaitForActive();
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the Admin Console is loaded in the browser
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");


		boolean present = sIsElementPresent("css=span:contains('" + Locators.TOOLS_AND_MIGRATION + "')");
		if ( !present ) {
			return (false);
		}

		boolean visible = zIsVisiblePerPosition("css=span:contains('" + Locators.TOOLS_AND_MIGRATION + "')", 0, 0);
		if ( !visible ) {
			logger.debug("isActive() visible = "+ visible);
			return (false);
		}

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
	

	public boolean zVerifyHeader (String header) throws HarnessException {
		if(this.sIsElementPresent("css=span:contains('" + header + "')"))
			return true;
		return false;
	}


}
