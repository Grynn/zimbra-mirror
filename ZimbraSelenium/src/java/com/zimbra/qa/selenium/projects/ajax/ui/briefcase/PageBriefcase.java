/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.*;

/**
 * @author
 * 
 */
public class PageBriefcase extends AbsTab {

	public static class Locators {
		public static final String zNewBriefcaseOverviewPaneIcon = "id=ztih__main_Briefcase__BRIEFCASE_textCell";
		public static final String zBriefcaseFolder = "id=zti__main_Briefcase__16_textCell";
		public static final String zBriefcaseFolderIcon = "id=zti__main_Briefcase__16";
		public static final String zTrashFolder = "id=zti__main_Briefcase__3_textCell";
		public static final String zBriefcaseAppIconBtn = "id=zb__App__Briefcase_left_icon";
		public static final String zNewMenuIconBtn = "id=zb__BCD__NEW_FILE_left_icon";
		public static final String zNewMenuLeftIconBtn = "id=zb__BDLV__NEW_MENU_left_icon";
		public static final String zUploadFileIconBtn = "id=zb__BDLV__NEW_FILE_left_icon";
		public static final String zEditFileIconBtn = "id=zb__BDLV__EDIT_FILE_left_icon";
		public static final String zOpenFileInSeparateWindowIconBtn = "id=zb__BDLV__NEW_BRIEFCASE_WIN_left_icon";
		public static final String zDeleteIconBtn = "id=zb__BDLV__DELETE_left_icon";
		public static final String zDeleteBtn = "id=zb__BDLV__DELETE";
		public static final String zMoveIconBtn = "id=zb__BDLV__MOVE_left_icon";
		public static final String zMoveBtn = "id=zb__BDLV__MOVE";
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
		waitForCondition(
				"selenium.isElementPresent(\"xpath=//div[@id='zov__main_Mail']\")",
				"20000");

		// Click on Briefcase icon
		zClick(PageMain.Locators.zAppbarBriefcase);

		zWaitForBusyOverlay();

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
			locator = Locators.zNewMenuLeftIconBtn;

			// Click on New Document icon
			this.zClick(locator);

			zWaitForBusyOverlay();
			
			isEditDocLoaded("Zimbra Docs", "");

			page = new DocumentBriefcaseNew(this.MyApplication);
			return (page);
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
			String attrs = sGetAttribute("css=td[" + Locators.zMoveIconBtn
					+ "]>div@class");
			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException("Tried clicking on " + button
						+ " but it was disabled " + attrs);
			}
			// locator = "css=td[id='zb__BDLV__MOVE_left_icon']";
			locator = Locators.zMoveIconBtn;
			page = new DialogChooseFolder(MyApplication);
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

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

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
		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		// Return the specified page, or null if not set
		return (page);
	}

	@Override
	public AbsPage zListItem(Action action, String name)
			throws HarnessException {
		logger.info(myPageName() + " zListItem(" + action + ", " + name + ")");
		AbsPage page = null;
		String listLocator;
		String rowLocator;
		String itemlocator;

		listLocator = "div[id='zl__BDLV__rows'][class='DwtListView-Rows']";
		rowLocator = "div[id^='zli__BDLV__']";
		// rowLocator = "css=div:contains[id^='zli__BDLV__']";
		// rowLocator = "css=div:contains[id:contains('zli__BDLV__')]";
		if (!this.sIsElementPresent("css=" + listLocator))
			throw new HarnessException("List View Rows is not present "
					+ listLocator);
		/*
		 * // How many items are in the table? int count =this.sGetXpathCount(
		 * "//div[@id='zl__BDLV__rows']//div[contains(@id, 'zli__BDLV__')]");
		 * logger.debug(myPageName() +
		 * " zListSelectItem: number of list items: "+ count);
		 * 
		 * for (int i = 1; i <= count; i++) { itemlocator = "css=" + listLocator
		 * + ">div:nth-child(" + i + ")"; String namelocator; namelocator =
		 * itemlocator + ">table>tbody>tr>td>div[id*='__na']"; String s =
		 * this.sGetText(namelocator).trim(); s =
		 * this.sGetText("css=div[id='zl__BDLV__rows']>div:nth-child(" + i +
		 * ")").trim();
		 * 
		 * if ( s.contains(name) ) { break; // found it } itemlocator = null; }
		 * if ( itemlocator == null ) { throw new
		 * HarnessException("Unable to locate item with name("+ name +")"); }
		 */
		itemlocator = "css=" + listLocator + " td[width='auto'] div:contains("
				+ name + ")";
		if (!this.sIsElementPresent(itemlocator))
			throw new HarnessException("Unable to locate item with name("
					+ name + ")");
		if (action == Action.A_LEFTCLICK) {
			// Left-Click on the item
			this.zClick(itemlocator);
			page = new DocumentPreview(MyApplication);
		}
		return page;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String subject)
			throws HarnessException {
		throw new HarnessException("implement me!");
	}

	public void pageRefresh(boolean includeRow) throws HarnessException {
		// ClientSessionFactory.session().selenium().refresh();
		zClick(Locators.zBriefcaseFolderIcon);
		String condition = "selenium.isElementPresent(\"css=[id='zti__main_Briefcase__16_div'][class='DwtTreeItem-selected']\")&&"
				+ "selenium.isElementPresent(\"css=[id='zl__BDLV__rows']";
		zWaitForBusyOverlay();
		if (includeRow)
			waitForCondition(condition + " div[class^='Row']\");", "5000");
		else
			waitForCondition(condition + "\");", "5000");
	}

	public boolean isOpenDocLoaded(String windowName, String text)
			throws HarnessException {
		waitForWindow(windowName, "5000");

		zSelectWindow(windowName);

		boolean loaded = waitForElement(
				"css=td[class='ZhAppContent'] div:contains('" + text + "')",
				"60000");

		return loaded;
	}

	public boolean isEditDocLoaded(String windowName, String text)
			throws HarnessException {
		waitForWindow(windowName, "5000");

		zSelectWindow(windowName);
		
		waitForElement("css=div[class='ZDToolBar ZWidget']", "30000");

		waitForElement("css=iframe[id*='DWT'][class='ZDEditor']", "30000");

		boolean loaded = waitForIframeText(
				"css=iframe[id*='DWT'][class='ZDEditor']", text, "5000");

		return loaded;
	}

	public boolean waitForIframeText(String iframe, String text, String timeout) {
		try {
			ClientSessionFactory
					.session()
					.selenium()
					.waitForCondition(
							"var x = selenium.browserbot.findElementOrNull(\""
									+ iframe
									+ "\");if(x!=null){x=x.contentWindow.document.body;}if(browserVersion.isChrome){x.textContent.indexOf('"
									+ text
									+ "') >= 0;}else if(browserVersion.isIE){x.innerText.indexOf('"
									+ text
									+ "') >= 0;}else{x.textContent.indexOf('"
									+ text + "') >= 0;}", timeout);
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
