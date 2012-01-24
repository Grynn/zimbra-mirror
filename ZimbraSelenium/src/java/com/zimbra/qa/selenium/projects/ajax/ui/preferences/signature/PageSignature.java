package com.zimbra.qa.selenium.projects.ajax.ui.preferences.signature;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.I18N;
import com.zimbra.qa.selenium.framework.util.HarnessException;



public class PageSignature extends AbsTab{

	public PageSignature(AbsApplication application) {
		super(application);
		// TODO Auto-generated constructor stub
	}
	public static class Locators {

		// Preferences Toolbar: Save, Cancel
		public static final String zToolbarSaveID = "zb__PREF__SAVE_title";
		public static final String zToolbarCancelID = "zb__PREF__CANCEL_title";
		public static final String zSignatureListView = "//div[@class='ZmSignatureListView']";
		//public static final String zNewSignature ="//td[contains(@id,'_title') and contains (text(),'"+I18N.NEW_SIGNATURE+"')]";
		public static final String zNewSignature ="css=td[class='ZOptionsField'] td[id$='_title']:contains('"+I18N.NEW_SIGNATURE+"')";
		
		
		//public static final String zDeleteSignature ="//td[contains(@id,'DWT') and contains (text(),'"+I18N.DELETE+"')]";
		public static final String zDeleteSignature ="css=td[class='ZOptionsField'] td[id$='_title']:contains('"+I18N.DELETE+"')";
		
	}


	@Override
	public AbsPage zListItem(Action action, String item)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	public String zGetSignatureNameFromListView() throws HarnessException{
		String locator = null;
		locator = Locators.zSignatureListView;
		String sigListViewName= this.sGetText(locator);
		return sigListViewName;

	}
	public String zGetSignatureBodyText() throws HarnessException{
		
		//bug 59078
		String locator = null;
		locator="selenium.browserbot.getCurrentWindow().document.getElementById('TEXTAREA_SIGNATURE').value";
		String textsig= this.sGetEval(locator);
		return textsig;

	}
	public String zGetHtmlSignatureBody() throws HarnessException {
		try {
			sSelectFrame("css=iframe[id='TEXTAREA_SIGNATURE_ifr']");
			String sigbodyhtml = this.sGetHtmlSource();
			return sigbodyhtml;
		} finally {
			this.sSelectFrame("relative=top");
		}

	}
	
	@Override
	public AbsPage zListItem(Action action, Button option, String item)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void zNavigateTo() throws HarnessException {
		// TODO Auto-generated method stub

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the "+ button +" button");

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//
		if (button == Button.B_NEW) {

			// New button
			locator = Locators.zNewSignature;

			page = new FormSignatureNew(this.MyApplication);


		}else if(button== Button.B_DELETE){
			locator = Locators.zDeleteSignature;
			page = null;
			
		}else {
		
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
	
		this.zClickAt(locator,"");

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();
		
		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
	throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// TODO Auto-generated method stub
		return false;
	}

}
