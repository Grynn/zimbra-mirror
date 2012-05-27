package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.interactions.Actions;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

/**
 * Represents a "Check In File to Briefcase" dialog box
 * <p>
 */
public class DialogUploadFile extends AbsDialog {
	public static class Locators {
		public static final String zDialogClass = "css=div.ZmUploadDialog";
		public static final String zTitleCLass =  "DwtDialogTitle";
		public static final String zDialogButtonsClass = "DwtDialogButtonBar";		
	}

	public DialogUploadFile(AbsApplication application,AbsTab page) {
		super(application,page);		
		logger.info("new "+ DialogCheckInFile.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		logger.info(myPageName() + " zIsActive()");

		String locator = Locators.zDialogClass;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);
	}
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");
		tracer.trace("Click dialog button " + button);
		
		String locator = null;

		if (button == Button.B_CANCEL) {
		    locator = "//div[@class='ZmUploadDialog']"
			    + "//*[contains(@class,'ZWidgetTitle') and contains(text(),'Cancel')]";
		} else if (button == Button.B_OK) {
		    locator = "//div[@class='ZmUploadDialog']"
			    + "//*[contains(@class,'ZWidgetTitle') and contains(text(),'OK')]";
		}else if (button == Button.B_BROWSE) {
		    locator = "css=td>input[id*=_input][type=file][name=uploadFile]";
		    if (ZimbraSeleniumProperties.isWebDriver()){ 
			WebElement el = this.getElement(locator);
		    	if (webDriver() instanceof InternetExplorerDriver) {
			    Actions action = new Actions(webDriver());
			    action.moveToElement(el,1,1).doubleClick(el).build().perform();
		    	}else{
		    	    executeScript("arguments[0].click()",el);
		    	}			    
		    }else{
			if (zIsBrowserMatch(BrowserMasks.BrowserMaskIE)) {
			    sDoubleClick(locator);
			}else{
			    sGetEval("selenium.browserbot.findElement('" + locator + "').click();");
			}
		    }
		    return null;
		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Default behavior, click the locator
		
		// Make sure the locator was set
	
		// Make sure the locator exists
		if (!this.sIsVisible(locator)) {
			throw new HarnessException("Button " + button + " locator "
					+ locator + " not visible!");
		}
		
		this.zClickAt(locator,"0,0");
		
		this.zWaitForBusyOverlay();

		return (null);
	}
	
	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText(" + locator + ")");

		if (locator == null)
			throw new HarnessException("locator was null");

		return (this.sGetText(locator));
	}
}
