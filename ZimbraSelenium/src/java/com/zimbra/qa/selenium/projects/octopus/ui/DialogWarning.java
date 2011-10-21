package com.zimbra.qa.selenium.projects.octopus.ui;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;

public class DialogWarning extends AbsDialog {

	public static class DialogWarningID {

		public static DialogWarningID EmptyFolderWarningMessage = new DialogWarningID(
				"OkCancel");
		public static DialogWarningID PermanentlyDeleteTheItem = new DialogWarningID(
				"OkCancel");

		protected String Id;

		protected DialogWarningID(String id) {
			Id = id;
		}
	}

	protected String MyDivId = null;

	public DialogWarning(DialogWarningID dialogId, AbsApplication application,
			AbsTab tab) {
		super(application, tab);

		// Remember which div this object is pointing at
		/*
		 * Example: <div id="YesNoCancel" style=
		 * "position: absolute; overflow: visible; left: 229px; top: 208px; z-index: 700;"
		 * class="DwtDialog" parentid="z_shell"> <div
		 * class="DwtDialog WindowOuterContainer"> ... </div> </div>
		 */
		MyDivId = dialogId.Id;

		logger.info("new " + DialogWarning.class.getCanonicalName());

	}

	public String zGetWarningTitle() throws HarnessException {
		String locator = "css=div[id='" + MyDivId + "'] td[id='" + MyDivId
				+ "_title']";
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
		if (button == null)
			throw new HarnessException("button cannot be null");

		String locator = null;
		AbsPage page = null; // Does this ever result in a page being returned?

		if (button == Button.B_YES) {

			locator = " td[id^='Yes_'] td[id$='_title']";

		} else if (button == Button.B_NO) {

			locator = " td[id^='No_'] td[id$='_title']";

		} else if (button == Button.B_CANCEL) {

			locator =  " td[id^='Cancel_'] td[id$='_title']";

		} else if (button == Button.B_OK) {

			locator = " td[id^='OK_'] td[id$='_title']";

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Click it
		zClickAt(locator, "0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		// If page was specified, make sure it is active
		/*
		 * if ( page != null ) {
		 * 
		 * // This function (default) throws an exception if never active
		 * page.zWaitForActive();
		 * 
		 * }
		 */

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		if (locator == null)
			throw new HarnessException("locator cannot be null");

		if (!this.sIsElementPresent(locator))
			throw new HarnessException("locator cannot be found");

		return (this.sGetText(locator));

	}

	@Override
	public boolean zIsActive() throws HarnessException {

		if (!this.sIsElementPresent(MyDivId))
			return (false);

		if (!this.zIsVisiblePerPosition(MyDivId, 0, 0))
			return (false);

		return (true);
	}

}
