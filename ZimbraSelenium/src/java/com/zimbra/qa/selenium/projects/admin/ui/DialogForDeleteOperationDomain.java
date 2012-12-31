package com.zimbra.qa.selenium.projects.admin.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

public class DialogForDeleteOperationDomain extends AbsDialog {

	public static class Locators {
		public static final String YES_BUTTON="zdlg__MSG__CTR_DMLV_removeConfirm_button5_title";
		public static final String NO_BUTTON="zdlg__MSG__CTR_DMLV_removeConfirm_button4_title";
	}
	
	public DialogForDeleteOperationDomain(AbsApplication application, AbsTab page) {
		super(application, page);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		tracer.trace("Click dialog button " + button);
		if ( button == null )
			throw new HarnessException("button cannot be null");
	
		String locator = null;
		AbsPage page = null; 

		if (button == Button.B_YES) {
			locator = Locators.YES_BUTTON;
		} else if (button == Button.B_NO) {
			locator = Locators.NO_BUTTON;
		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Make sure the locator was set
		if (locator == null) {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Make sure the locator exists
		if (!this.sIsElementPresent(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not present!");
		}

		// if(zIsActive())
		// zGetDisplayedText("css=div[class=" + Locators.zDialogContentClassId +
		// "]");

		this.zClickAt(locator,"0,0");
		SleepUtil.sleepLong();

		// If the app is busy, wait for it to become active
		//this.zWaitForBusyOverlay();
		
		// If page was specified, make sure it is active
		if ( page != null ) {
			
			// This function (default) throws an exception if never active
			page.zWaitForActive();
			
		}

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
