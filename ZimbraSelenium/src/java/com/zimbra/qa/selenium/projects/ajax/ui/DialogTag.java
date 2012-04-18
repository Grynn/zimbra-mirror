/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.common.soap.Element;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;



/**
 * Represents a "New Tag", "Rename Tag" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogTag extends AbsDialog {

	public static class Locators {
	
		// TODO:  See https://bugzilla.zimbra.com/show_bug.cgi?id=54173
		public static final String zTagDialogId		= "CreateTagDialog";
		
		public static final String zTitleId	 		= "CreateTagDialog_title";

		public static final String zTagNameFieldId	= "CreateTagDialog_name";

		public static final String zTagColorPulldownId = "ZmTagColorMenu_dropdown";
		
		public static final String zButtonsId 		= "CreateTagDialog_buttons";
		public static final String zButtonOkId 		= "DWT178_title";
		public static final String zButtonCancelId 	= "DWT179_title";


	}
	
	
	public DialogTag(AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		logger.info("new " + DialogTag.class.getCanonicalName());
	}
	
	public void zSetTagName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetTagName("+ name +")");

		String locator = "css=input#"+ Locators.zTagNameFieldId ;
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Tag name locator "+ locator +" is not present");
		}
		
		this.sType(locator, name);
		
	}
	
	public void zSetTagColor(String color) throws HarnessException {
		logger.info(myPageName() + " zSetTagColor("+ color +")");

		throw new HarnessException("implement me!");
		
	}
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {		
			locator = "css=div#CreateTagDialog_button2";
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator = "css=div#CreateTagDialog_button1";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Button "+ button +" locator "+ locator +" not present!");
		}
		
		zClickAt(locator,"0,0");
		
		zWaitForBusyOverlay();
		return (null);
	}

	public void zSubmit(String tagName) throws HarnessException {
	   zSetTagName(tagName);
	   zClickButton(Button.B_OK);

	   SOAP_DESTINATION_HOST_TYPE destType = null;
	   if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
	      destType = SOAP_DESTINATION_HOST_TYPE.CLIENT;
	   } else {
	      destType = SOAP_DESTINATION_HOST_TYPE.SERVER;
	   }

	   int maxRetry = 30;
	   int retry = 0;
	   boolean found = false;

	   while (retry < maxRetry && !found) {
	      SleepUtil.sleep(1000);
	      MyApplication.zGetActiveAccount().soapSend("<GetTagRequest xmlns='urn:zimbraMail'/>",
	            destType, MyApplication.zGetActiveAccount().EmailAddress);
	      Element[] results = MyApplication.zGetActiveAccount().soapSelectNodes(
	            "//mail:GetTagResponse//mail:tag[@name='" + tagName +"']");
	      if (results.length == 1) {
	         found = true;
	      }
	      retry ++;
	   }

	   if (retry == maxRetry) {
	      throw new HarnessException("The tag is never created after submit");
	   }
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
		// Need to implement for:
		
		// "Create New Tag"
		// "Tag name:"
		// "Blue", "Cyan", ..., "Orange", "More colors ..." (Tag color pulldown)
		// OK
		// Cancel
		
		throw new HarnessException("implement me");
	}


	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		logger.info(myPageName() + " zIsActive()");

		String locator = "css=div[id="+ Locators.zTagDialogId + "]";

		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}

		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}

		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);

		//return ( this.sIsElementPresent(Locators.zTagDialogId) );
	}



}
