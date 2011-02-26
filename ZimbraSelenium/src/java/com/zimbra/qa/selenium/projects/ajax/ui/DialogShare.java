/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.Stafpostqueue;


/**
 * Represents a "Rename Folder" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogShare extends AbsDialog {

	public static class Locators {
	

	}
	
	
	public DialogShare(AbsApplication application, AbsTab tab) {
		super(application, tab);
	}
	
	public static class ShareWith {
		public static ShareWith InternalUsers	 = new ShareWith("InternalUsers");
		public static ShareWith ExternalGuests	 = new ShareWith("ExternalGuests");
		public static ShareWith Public			 = new ShareWith("Public");
		
		protected String ID;
		protected ShareWith(String id) {
			ID = id;
		}
		public String toString() {
			return (ID);
		}

	}
	
	public void zSetShareWith(ShareWith name) throws HarnessException {
		logger.info(myPageName() + " zSetShareWith("+ name +")");

		String locator = "implement me";
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("zSetShareWith "+ locator +" is not present");
		}
		
	}
	
	public void zSetEmailAddress(String email) throws HarnessException {
		logger.info(myPageName() + " zSetEmailAddress("+ email +")");

		String locator = "implement me";
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("zSetEmailAddress "+ locator +" is not present");
		}

	}
	
	public static class ShareRole {
		public static ShareRole None	 = new ShareRole("None");
		public static ShareRole Viewer	 = new ShareRole("Viewer");
		public static ShareRole Manager	 = new ShareRole("Manager");
		public static ShareRole Admin	 = new ShareRole("Admin");
		
		protected String ID;
		protected ShareRole(String id) {
			ID = id;
		}
		
		public String toString() {
			return (ID);
		}

	}
	
	public void zSetRole(ShareRole role) throws HarnessException {
		logger.info(myPageName() + " zSetRole("+ role +")");

		String locator = "implement me";
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("zSetRole "+ locator +" is not present");
		}

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

		String locator = "implement me";
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("zSetRole "+ locator +" is not present");
		}

	}
	

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_OK ) {
			
			locator =  "implement me";
			
		} else if ( button == Button.B_CANCEL ) {
			
			locator =  "implement me";

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
		
		// This dialog sends a message, so we need to check the queue
		try {
		
			// Check the message queue
			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();
		
		} catch (Exception e) {
			throw new HarnessException("Unable to wait for message queue", e);
		}

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
		
		String locator = "css=[]";
		return ( this.sIsElementPresent(locator) );
	}



}
