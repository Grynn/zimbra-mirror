/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.PageMain;

/**
 * @author
 * 
 */
public class PageBriefcase extends AbsTab {

	public static class Locators {
		public static final String zNewBriefcaseOverviewPaneIcon = "id=ztih__main_Briefcase__BRIEFCASE_textCell";
		public static final String zBriefcaseFolder = "id=zti__main_Briefcase__16_textCell";
		public static final String zBriefcaseFolderIcon = "xpath=//div[@id='zti__main_Briefcase__16']";
		public static final String zTrashFolder = "id=zti__main_Briefcase__3_textCell";
		public static final String zBriefcaseAppIconBtn = "id=zb__App__Briefcase_left_icon";
		public static final String zNewMenuIconBtn = "id=zb__BCD__NEW_FILE_left_icon";
		public static final String zNewMenuLeftIconBtn = "id=zb__BDLV__NEW_MENU_left_icon";
		public static final String zUploadFileIconBtn = "id=zb__BDLV__NEW_FILE_left_icon";
		public static final String zEditFileIconBtn = "id=zb__BDLV__EDIT_FILE_left_icon";
		public static final String zOpenFileInSeparateWindowIconBtn = "id=zb__BDLV__NEW_BRIEFCASE_WIN_left_icon";
		public static final String zDeleteIconBtn = "id=zb__BDLV__DELETE_left_icon";
		public static final String zDeleteBtn = "id=zb__BDLV__DELETE";
		public static final String zMoveItemIconBtn = "id=zb__BCD__MOVE_left_icon";
		public static final String zTagItemIconBtn = "id=zb__BCD__TAG_MENU_left_icon";
		public static final String zViewIconBtn = "id=zb__BCD__VIEW_MENU_left_icon";
		public static final String zSendBtnIconBtn = "id=zb__BCD__SEND_FILE_left_icon";
		public static final String zNewDocumentIconBtn = "id=zb__BCD__NEW_DOC_left_icon";
		public static final String zNewSpreadsheetIconBtn = "id=zb__BCD__NEW_SPREADSHEET_left_icon";
		public static final String zNewPresentationIconBtn = "id=zb__BCD__NEW_PRESENTATION_left_icon";

	}

	public PageBriefcase(AbsApplication application) {
		super(application);
		logger.info("new " + PageBriefcase.class.getCanonicalName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		// If the "folders" tree is visible, then Briefcase tab is active
		boolean loaded = this.sIsElementPresent(Locators.zBriefcaseFolderIcon);
		if (!loaded)
			return (loaded);
		boolean active = this.zIsVisiblePerPosition(
				Locators.zBriefcaseFolderIcon, 4, 74);
		return (active);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if (zIsActive()) {
			return;
		}

		// Make sure we are logged into the Ajax app
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive()) {
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();
		}

		// make sure mail page is loaded
		try {
			ClientSessionFactory
					.session()
					.selenium()
					.waitForCondition(
							"selenium.isElementPresent(\"xpath=//div[@id='zov__main_Mail']\")",
							"20000");
		} catch (Exception ex) {
			logger
					.info("Error: mail page is not loaded", ex
							.fillInStackTrace());
		}
		// Click on Briefcase icon
		if (this.sIsElementPresent(PageMain.Locators.zAppbarBriefcase)
				&& this.sIsVisible(PageMain.Locators.zAppbarBriefcase))
			zClick(PageMain.Locators.zAppbarBriefcase);

		zWaitForActive();
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (button == Button.B_NEW) {

			// For "NEW" without a specified pulldown option, just return the
			// default item
			// To use "NEW" with a pulldown option, see
			// zToolbarPressPulldown(Button, Button)
			//

			zKeyboard.zTypeCharacters(Shortcut.S_NEWITEM.getKeys());

			// Not default behavior (zPressKeyboardShortcut vs. zClick)
			return (new DocumentBriefcaseNew(this.MyApplication));

		} else if (button == Button.O_NEW_BRIEFCASE) {
			locator = "id=" + Locators.zNewMenuIconBtn;
		} else if (button == Button.O_NEW_DOCUMENT) {
			// Check if the button is visible
			String attrs = sGetAttribute("xpath=(//div[@id='zb__BDLV__NEW_MENU'])@style");
			if (!attrs.contains("visible")) {
				throw new HarnessException(button + " not visible " + attrs);
			}
			String newPageTitle = "Zimbra Docs";
			locator = Locators.zNewMenuLeftIconBtn;
			// Click on New Document icon
			this.zClick(locator);

			waitForWindow(newPageTitle, "5000");

			try {
				zSelectWindow(newPageTitle);

				// if name field appears in the toolbar then document page is
				// opened
				waitForElement("//*[@id='DWT3_item_1']", "30000");

				if (!sIsVisible("//*[@id='DWT3_item_1']")) {
					throw new HarnessException("could not open a new file page");
				} else {
					DocumentBriefcaseNew.pageTitle = newPageTitle;
				}
				page = new DocumentBriefcaseNew(this.MyApplication);
				return (page);
			} catch (Exception ex) {
				zSelectWindow("Zimbra: Briefcase");
				throw new HarnessException("couldn't select window"
						+ newPageTitle, ex);
			}
		} else if (button == Button.B_UPLOAD_FILE) {
			// Check if the button is visible
			String attrs = sGetAttribute("xpath=(//div[@id='zb__BDLV__NEW_FILE'])@style");
			if (!attrs.contains("visible")) {
				throw new HarnessException(button + " not visible " + attrs);
			}
			locator = Locators.zUploadFileIconBtn;
			page = null;
		} else if (button == Button.B_EDIT_FILE) {
			// Check if the button is visible
			String attrs = sGetAttribute("xpath=(//div[@id='zb__BDLV__EDIT_FILE'])@style");
			if (!attrs.contains("visible")) {
				throw new HarnessException(button + " not visible " + attrs);
			}
			locator = Locators.zEditFileIconBtn;
			page = new DocumentBriefcaseEdit(this.MyApplication);
		} else if (button == Button.B_DELETE) {
			// Check if the button is visible
			String attrs = sGetAttribute("css=div[id='zb__BDLV__DELETE']@style");
			if (!attrs.contains("visible")) {
				throw new HarnessException(button + " not visible " + attrs);
			}
			locator = Locators.zDeleteIconBtn;
			page = new DialogDeleteConfirm(MyApplication);
		} else if (button == Button.B_OPEN_IN_SEPARATE_WINDOW) {
			// Check if the button is visible
			String attrs = sGetAttribute("css=div[id='zb__BDLV__NEW_BRIEFCASE_WIN']@style");
			if (!attrs.contains("visible")) {
				throw new HarnessException(button + " not visible " + attrs);
			}
			locator = Locators.zOpenFileInSeparateWindowIconBtn;
			page = new DocumentBriefcaseOpen(this.MyApplication);
		} else if (button == Button.B_MOVE) {

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"
					+ "Locators.zMoveIconBtnID" + "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

			locator = "id='" + "Locators.zMoveIconBtnID";
			page = null; // TODO
			throw new HarnessException("implement Move dialog");

		} else if (button == Button.B_PRINT) {

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"
					+ "Locators.zPrintIconBtnID" + "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

			locator = "id='" + "Locators.zPrintIconBtnID";
			page = null; // TODO
			throw new HarnessException("implement Print dialog");

		} else if (button == Button.B_TAG) {

			// For "TAG" without a specified pulldown option, just click on the
			// pulldown
			// To use "TAG" with a pulldown option, see
			// zToolbarPressPulldown(Button, Button)
			//

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"
					+ "Locators.zTagMenuDropdownBtnID" + "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

			locator = "id='" + "Locators.zTagMenuDropdownBtnID" + "'";

		} else if (button == Button.B_LISTVIEW) {

			// For "TAG" without a specified pulldown option, just click on the
			// pulldown
			// To use "TAG" with a pulldown option, see
			// zToolbarPressPulldown(Button, Button)
			//

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"
					+ "Locators.zViewMenuDropdownBtnID" + "']/div)@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}

			locator = "id='" + "Locators.zViewMenuDropdownBtnID" + "'";

		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Make sure the button exists
		if (!this.sIsElementPresent(locator))
			throw new HarnessException("Button is not present locator="
					+ locator + " button=" + button);

		// Click it
		this.zClick(locator);

		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("
				+ pulldown + ", " + option + ")");

		if (pulldown == null)
			throw new HarnessException("Button cannot be null!");

		if (pulldown == null)
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (pulldown == Button.B_NEW) {

			if (option == Button.O_NEW_ADDRESSBOOK) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_APPOINTMENT) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_BRIEFCASE) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_CALENDAR) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_CONTACT) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_CONTACTGROUP) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_DOCUMENT) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_FOLDER) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_MESSAGE) {

				// TODO: should this actually click New followed by Message?

				pulldownLocator = null;
				optionLocator = null;
				page = zToolbarPressButton(pulldown);

			} else if (option == Button.O_NEW_TAG) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_TASK) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_TASKFOLDER) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}

		} else if (pulldown == Button.B_LISTVIEW) {

			if (option == Button.O_LISTVIEW_BYCONVERSATION) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_LISTVIEW_BYMESSAGE) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_LISTVIEW_READINGPANEBOTTOM) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_LISTVIEW_READINGPANEOFF) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_LISTVIEW_READINGPANERIGHT) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}

		} else if (pulldown == Button.B_TAG) {

			if (option == Button.O_TAG_NEWTAG) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_TAG_REMOVETAG) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}

		} else {
			throw new HarnessException("no logic defined for pulldown "
					+ pulldown);
		}

		// Default behavior
		if (pulldownLocator != null) {

			// TODO: Expand pulldownLocator

			if (optionLocator != null) {
				// TODO: Click optionLocator
			}

			throw new HarnessException("implement me!");
		}

		// Return the specified page, or null if not set
		return (page);
	}

	@Override
	public AbsPage zListItem(Action action, String subject)
			throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public AbsPage zListItem(Action action, Action option, String subject)
			throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public boolean waitForText(String iframe, String text, String timeout) {
		try {
			ClientSessionFactory
					.session()
					.selenium()
					.waitForCondition(
							"var x = selenium.browserbot.findElementOrNull(\""
									+ iframe
									+ "\");if(x!=null){x=x.contentWindow.document.body;}if(browserVersion.isChrome){x.textContent=='"
									+ text
									+ "';}else if(browserVersion.isIE){x.innerText=='"
									+ text + "';}", timeout);
			return true;
		} catch (Exception ex) {
			logger.info("Error: text '" + text + "' not present in element: "
					+ iframe, ex.fillInStackTrace());
			return false;
		}
	}

	public boolean waitForElement(String element, String timeout) {
		try {
			ClientSessionFactory.session().selenium().waitForCondition(
					"selenium.isElementPresent(\"" + element + "\")", timeout);
			return true;
		} catch (Exception ex) {
			logger.info("Error: element not present " + element, ex
					.fillInStackTrace());
			return false;
		}
	}

	public boolean waitForWindow(String name, String timeout) {
		try {
			ClientSessionFactory
					.session()
					.selenium()
					.waitForCondition(
							"{var x; for(var windowName in selenium.browserbot.openedWindows ){"
									+ "var targetWindow = selenium.browserbot.openedWindows[windowName];"
									+ "if((!selenium.browserbot._windowClosed(targetWindow))&&"
									+ "(targetWindow.name == '" + name
									+ "' || targetWindow.document.title == '"
									+ name + "')){x=windowName;"
									+ "}}}; x!=null;", timeout);
			return true;
		} catch (Exception ex) {
			logger.info("Error: win not opened " + name, ex.fillInStackTrace());
			return false;
		}
	}

	public boolean waitForCondition(String condition, String timeout) {
		try {
			// ClientSessionFactory.session().selenium().waitForCondition("var x = selenium.browserbot.findElementOrNull(\"css=[class='ZmBriefcaseDetailListView']\"); x != null && parseInt(x.style.width) >= 0;","5000");
			ClientSessionFactory.session().selenium().waitForCondition(
					condition, timeout);
			return true;
		} catch (Exception ex) {
			logger.info("Error: " + condition, ex.fillInStackTrace());
			return false;
		}
	}

	public void closeWindow() {
		ClientSessionFactory.session().selenium().close();
	}
}
