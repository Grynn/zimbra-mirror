package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;


/**
 * A <code>DialogWarning</code> object represents a "Warning" dialog, such as "Save 
 * current message as draft", etc.
 * <p>
 * During construction, the div ID attribute must be specified, such as "YesNoCancel".
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogWarning extends AbsDialog {

	public static class DialogWarningID {
		

		public static final DialogWarningID SaveCurrentMessageAsDraft = new DialogWarningID("YesNoCancel");

		public static final DialogWarningID SaveTaskChangeMessage = new DialogWarningID("YesNoCancel");
		
		public static final DialogWarningID SendLink = new DialogWarningID("css=div[class=DwtConfirmDialog]");
		public static final DialogWarningID DeleteTagWarningMessage = new DialogWarningID("YesNoMsgDialog");
		public static final DialogWarningID EmptyFolderWarningMessage = new DialogWarningID("OkCancel");
		public static final DialogWarningID SaveSignatureChangeMessage = new DialogWarningID("YesNoCancel");
		public static final DialogWarningID CancelCreateContact = new DialogWarningID("YesNoCancel");
		public static final DialogWarningID PermanentlyDeleteTheItem = new DialogWarningID("OkCancel");
		
		public static final DialogWarningID DeleteAppointment = new DialogWarningID("YesNo");

		// See bug: http://bugzilla.zimbra.com/show_bug.cgi?id=63353
		public static final DialogWarningID SelectedTimeIsInPast = new DialogWarningID("ShowDelayPastDialog");
		
		// See http://bugzilla.zimbra.com/show_bug.cgi?id=64081
		public static final DialogWarningID SendReadReceipt = new DialogWarningID("YesNoMsgDialog");;

		public static final DialogWarningID QuickCommandConfirmDelete = new DialogWarningID("ZmQuickCommandConfirmation1");

		public static final DialogWarningID PreferencesSaveChanges = new DialogWarningID("YesNoCancel");
		public static final DialogWarningID SwitchingToTextWillDiscardHtmlFormatting = new DialogWarningID("css=div[class='DwtMsgDialog']");

		public static final DialogWarningID SmsVerificationCodeSent = new DialogWarningID("ZmMsgDialog");

		public static final DialogWarningID ZmAcceptShare = new DialogWarningID("ZmAcceptShare");

		protected String Id;
		protected DialogWarningID(String id) {
			Id = id;
		}
	}
	
	protected String MyDivId = null;
	
	
	public DialogWarning(DialogWarningID dialogId, AbsApplication application, AbsTab tab) {
		super(application, tab);
		
		// Remember which div this object is pointing at
		/*
		 * Example:
		 * <div id="YesNoCancel" style="position: absolute; overflow: visible; left: 229px; top: 208px; z-index: 700;" class="DwtDialog" parentid="z_shell">
		 *   <div class="DwtDialog WindowOuterContainer">
		 *   ...
		 *   </div>
		 * </div>
		 */
		MyDivId = dialogId.Id;
		
		logger.info("new " + DialogWarning.class.getCanonicalName());

	}
	
	public String zGetWarningTitle() throws HarnessException {
		String locator = "css=div[id='"+ MyDivId +"'] td[id='"+ MyDivId +"_title']";
		return (zGetDisplayedText(locator));
	}
	
	public String zGetWarningContent() throws HarnessException {
		String locator = "css=td[id=MessageDialog_1_Msg]";
		return (zGetDisplayedText(locator));
	}
	
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		if ( button == null )
			throw new HarnessException("button cannot be null");

		String locator = null;
		AbsPage page = null; 		// Does this ever result in a page being returned?

		// See http://bugzilla.zimbra.com/show_bug.cgi?id=54560
		// Need unique id's for the buttons
		String buttonsTableLocator = "css=div[id='"+ MyDivId +"'] div[id$='_buttons']";

		if ( button == Button.B_YES ) {

			locator = buttonsTableLocator + " td[id^='Yes_'] td[id$='_title']";

			if(MyDivId.contains("css=div[class=DwtConfirmDialog]")){
				page = 	new FormMailNew(this.MyApplication);
			}


		} else if ( button == Button.B_NO ) {

			locator = buttonsTableLocator + " td[id^='No_'] td[id$='_title']";

		} else if ( button == Button.B_CANCEL ) {

			locator = buttonsTableLocator + " td[id^='Cancel_'] td[id$='_title']";

		} else if (button == Button.B_OK) {

			locator = buttonsTableLocator + " td[id^='OK_'] td[id$='_title']";

		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Click it
		zClickAt(locator,"0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		if ( page != null ) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		if ( locator == null )
			throw new HarnessException("locator cannot be null");
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("locator cannot be found");
		
		return (this.sGetText(locator));
		
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		
		if ( !this.sIsElementPresent(MyDivId) )
			return (false);
		
		// mountpionts.viewer.FlagMail seems to keep failing on this dialog, even
		// though the PERM_DENIED dialog is showing correctly
		//
		// 7.X: 		if ( !this.zIsVisiblePerPosition(MyDivId, 225, 300) )
		// 8.X: dev says any dialogs with non-negative positions should be visible, so using (0,0)
		//
		if ( !this.zIsVisiblePerPosition(MyDivId, 0, 0) )
			return (false);
		
		return (true);
	}

}
