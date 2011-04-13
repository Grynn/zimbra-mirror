package com.zimbra.qa.selenium.projects.admin.ui;

import java.util.*;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;

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
	 * This class refers to the http://server.com/zimbra/downloads/index.html page
	 * @author Matt Rhoades
	 *
	 */
	public static class DownloadsIndex extends AbsSeleniumObject {
	
		public static class Locators {
			public static String isActive = "css=title:contains('Downloads')";
		}
		protected List<String> windowList = Collections.synchronizedList(new ArrayList<String>());
	
	
		public DownloadsIndex() {
		
			logger.info("new " + DownloadsIndex.class.getName());

		}

		public void zCloseWindows() throws HarnessException {
			
			if ( windowList.isEmpty() ) {
				logger.info("No open download windows.");
				return;
			}
			
			try {
				
				for (String id : windowList) {
				
					logger.info("Closing Downloads at ID: "+ id);
					// this.zSeparateWindowClose(id);
	
				}
				
			} finally {
				
				// All windows should be closed.  Clear the ID list.
				windowList.clear();
				
				// Select the main window
				this.zSelectWindow("null");
				ClientSessionFactory.session().selenium().windowFocus();
				
			}
			
		}
	
	}


}
