/**
 * 
 */
package com.zimbra.qa.selenium.projects.mobile.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * @author Matt Rhoades
 *
 */
public class PageMain extends AbsTab {

	public static class Locators {
	
		public static final String zBtnLogout = "xpath=//*[@id='_logout']";
		
		public static final String zMainCopyright = "//div[@id='copyright_notice']";
		public static final String zMainCopyrightText = "//div[@id='copyright_notice']//a";
		
		public static final String zAppbarMail = "//div[@id='appbar']//a[@id='mail']";
		public static final String zAppbarContact = "//div[@id='appbar']//a[@id='contact']";
		public static final String zAppbarCal = "//div[@id='appbar']//a[@id='cal']";
		public static final String zAppbarDocs = "//div[@id='appbar']//a[@id='docs']";
		public static final String zAppbarSearch = "//div[@id='appbar']//a[@id='search']";
		
		public static final String zBtnCompose = "//a[@href='zmain?st=newmail']";
		
		public static final String zPreferences = "//a[@href='?st=prefs']";
	
	}
	
	
	public PageMain(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMain.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the Mobile Client is loaded in the browser
		if ( !MyApplication.zIsLoaded() )
			throw new HarnessException("Admin Console application is not active!");
		

		// Look for the Logout button
		boolean present = sIsElementPresent(Locators.zBtnLogout);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		
		logger.debug("isActive() = "+ true);
		return (true);

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {


		if ( zIsActive() ) {
			// This page is already active
			return;
		}
		
		
		// 1. Logout
		// 2. Login as the default account
		if ( !((AppMobileClient)MyApplication).zPageLogin.zIsActive() ) {
			((AppMobileClient)MyApplication).zPageLogin.zNavigateTo();
		}
		((AppMobileClient)MyApplication).zPageLogin.zLogin();

		zWaitForActive();
		
	}

	/**
	 * Click the logout button
	 * @throws HarnessException
	 */
	public void zLogout() throws HarnessException {
		logger.debug("zLogout()");
		
		zNavigateTo();

		if ( !sIsElementPresent(Locators.zBtnLogout) ) {
			throw new HarnessException("The logoff button is not present " + Locators.zBtnLogout);
		}
				
		// Click on logout
		sClick(Locators.zBtnLogout);
				
		((AppMobileClient)MyApplication).zPageLogin.zWaitForActive();
		
		((AppMobileClient)MyApplication).zSetActiveAcount(null);

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
		throw new HarnessException("Mobile page does not have context menu");
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
