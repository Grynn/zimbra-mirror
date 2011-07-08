/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;


import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;



/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogShareAccept extends AbsDialog {

	public static class Locators {
		public static final String zDialogLocator = "css=div[class='ZmAcceptShareDialog']";
		public static final String zButtonsId = "ShareDialog_buttons";
	}
	
	
	public DialogShareAccept(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}
	
	public void zSetMountpointName(String name) throws HarnessException {
		logger.info(myPageName() + " zSetMountpointName("+ name +")");

//		String locator = "css=div#ShareDialog_grantee>input";
//
//		// Make sure the locator exists
//		if (!this.sIsElementPresent(locator)) {
//			throw new HarnessException("zSetEmailAddress " + locator
//					+ " is not present");
//		}
//		this.sFocus(locator);
//		this.sKeyPress(locator, "\13");
//		this.sType(locator, name);
//		this.sKeyUp(locator, "\13");		

		throw new HarnessException("implement me: See bug 61724");
		
	}
	
	public void zSetMountpointColor(String color) throws HarnessException {
		logger.info(myPageName() + " zSetMountpointColor("+ color +")");

		throw new HarnessException("implement me!");
	}
	
	
	public static class ShareMessageType {
		public static ShareMessageType SendStandardMsg		 = new ShareMessageType("SendStandardMsg");
		public static ShareMessageType DoNotSendMsg			 = new ShareMessageType("DoNotSendMsg");
		public static ShareMessageType AddNoteToStandardMsg	 = new ShareMessageType("AddNoteToStandardMsg");
		public static ShareMessageType ComposeInNewWindow	 = new ShareMessageType("ComposeInNewWindow");
		
		protected String ID;
		protected ShareMessageType(String id) {
			ID = id;
		}
		
		public String toString() {
			return (ID);
		}
	}
	
	public void zSetMessageType(ShareMessageType type) throws HarnessException {
		logger.info(myPageName() + " zSetMessageType("+ type +")");

		throw new HarnessException("implement me!");

	}
	

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_YES ) {
			
			locator = "css=div[class='ZmAcceptShareDialog'] td[id^='Yes_'] td[id$='_button5_title']";
			
		} else if ( button == Button.B_NO ) {
			
			locator = "css=div[class='ZmAcceptShareDialog'] td[id^='No_'] td[id$='_button4_title']";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		this.zClick(locator);
		
		zWaitForBusyOverlay();
		
		// This dialog (could) send a message, so we need to check the queue
		Stafpostqueue sp = new Stafpostqueue();
		sp.waitForPostqueue();

		return (null);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		
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

		String locator = "css=div[class='ZmAcceptShareDialog']";
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}



}
