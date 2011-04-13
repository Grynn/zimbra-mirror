/**
 * 
 */
package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * @author Matt Rhoades
 *
 */
public class PageManageServerStatus extends AbsTab {

	public static class Locators {
		public static final String zti__MONITORING__SSTATUS	= "zti__AppAdmin__MONITORING__SSTATUS_textCell";
		public static final String zRefreshButton			= "zb__STALV__REFRESH_title";
	}
	
	public PageManageServerStatus(AbsApplication application) {
		super(application);
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {
		// If the "Refresh" button is visible, assume the ServerStatus page is active

		// Look for the Refresh Button
		boolean present = sIsElementPresent(Locators.zRefreshButton);
		if ( !present ) {
			logger.debug("isActive() present = "+ present);
			return (false);
		}
		

		// Look for the Refresh Button. 
		boolean visible = zIsVisiblePerPosition(Locators.zRefreshButton, 0, 0);
		if ( !visible ) {
			logger.debug("isActive() visible = "+ visible);
			return (false);
		}
		
		logger.debug("isActive() = "+ true);
		return (true);
	}


	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsTab#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {

		if ( zIsActive() ) {
			// This page is already active.
			return;
		}
		
		// Click on Addresses -> Accounts
		zClick(Locators.zti__MONITORING__SSTATUS);
		
		zWaitForActive();
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
		// TODO Auto-generated method stub
		return null;	
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
