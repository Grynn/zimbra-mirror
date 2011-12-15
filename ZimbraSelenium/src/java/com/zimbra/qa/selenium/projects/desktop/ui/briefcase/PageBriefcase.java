/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui.briefcase;

import java.util.*;

import org.apache.commons.httpclient.HttpStatus;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount.SOAP_DESTINATION_HOST_TYPE;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.desktop.ui.*;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;

/**
 * @author
 * 
 */
public class PageBriefcase extends AbsTab {

	public static final String pageTitle = "Zimbra: Briefcase";

	public static class Locators {
		public static final Locators zNewBriefcaseOverviewPaneIcon = new Locators(
				"id=ztih__main_Briefcase__BRIEFCASE_textCell");
		public static final Locators zBriefcaseFolder = new Locators(
				"id=zti__main_Briefcase__16_textCell");
		public static final Locators briefcaseListView = new Locators(
				"css=div[id='zl__BDLV__rows'][class='DwtListView-Rows']");
		public static final Locators zBriefcaseFolderIcon = new Locators(
				"id=zti__main_Briefcase__16");
		public static final Locators zBriefcaseFolderIcon_Desktop = new Locators(
				"css=div[id*='Briefcase'][id$='16_div']");
		public static final Locators zTrashFolder = new Locators(
				"id=zti__main_Briefcase__3_textCell");
		public static final Locators zBriefcaseAppIconBtn = new Locators(
				"id=zb__App__Briefcase_left_icon");
		public static final Locators zNewMenuIconBtn = new Locators(
				"id=zb__BCD__NEW_FILE_left_icon");
		public static final Locators zNewMenuLeftIconBtn = new Locators(
				"id=zb__BDLV__NEW_MENU_left_icon");
		public static final Locators zNewMenuArrowBtn = new Locators(
				"css=div[id=zb__BDLV__NEW_MENU] div[class^=ImgSelectPullDownArrow]");
		public static final Locators zUploadFileIconBtn = new Locators(
				"id=zb__BDLV__NEW_FILE_left_icon");
		public static final Locators zEditFileIconBtn = new Locators(
				"id=zb__BDLV__EDIT_FILE_left_icon");
		public static final Locators zEditFileMenuItem = new Locators(
				"id=zmi__Briefcase__EDIT_FILE_left_icon");
		public static final Locators zOpenFileInSeparateWindowIconBtn = new Locators(
				"id=zb__BDLV__NEW_BRIEFCASE_WIN_left_icon");
		public static final Locators zDeleteIconBtn = new Locators(
				"id=zb__BDLV__DELETE_left_icon");
		public static final Locators zDeleteBtn = new Locators(
				"id=zb__BDLV__DELETE");
		public static final Locators zMoveIconBtn = new Locators(
				"id=zb__BDLV__MOVE_left_icon");
		public static final Locators zMoveBtn = new Locators(
				"id=zb__BDLV__MOVE");
		public static final Locators zTagItemIconBtn = new Locators(
				"id=zb__BCD__TAG_MENU_left_icon");
		public static final Locators zViewIconBtn = new Locators(
				"id=zb__BCD__VIEW_MENU_left_icon");
		public static final Locators zSendBtnIconBtn = new Locators(
				"id=zb__BCD__SEND_FILE_left_icon");
		public static final Locators zNewDocumentIconBtn = new Locators(
				"id=zb__BCD__NEW_DOC_left_icon");
		public static final Locators zNewSpreadsheetIconBtn = new Locators(
				"id=zb__BCD__NEW_SPREADSHEET_left_icon");
		public static final Locators zNewPresentationIconBtn = new Locators(
				"id=zb__BCD__NEW_PRESENTATION_left_icon");
		public static final Locators zRenameInput = new Locators(
				"css=div[class^=RenameInput]>input");
		public static final Locators zFileBodyField = new Locators(
				"css=html>body");

		private final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public static class Response {
		public static enum ResponsePart {
			BODY, HEADERS
		}

		public static enum Format {
			HTML("html"), TEXT("text"), TGZ("tgz"), XML("xml"), JSON("json"), NATIVE(
					"native"), RAW("raw");

			private String fmt;

			private Format(String fmt) {
				this.fmt = fmt;
			}

			public String getFormat() {
				return fmt;
			}
		}
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
		// if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive())
		// ((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();

		// If the "folders" tree is visible, then Briefcase tab is active

		String locator = null;
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			String currentActiveEmailAddress = MyApplication
					.zGetActiveAccount() != null ? MyApplication
					.zGetActiveAccount().EmailAddress : ZimbraAccount
					.AccountZDC().EmailAddress;
			locator = Locators.zBriefcaseFolderIcon_Desktop.locator + "[id*='"
					+ currentActiveEmailAddress + "']";
		} else {
			locator = Locators.zBriefcaseFolderIcon.locator;
		}

		boolean loaded = this.sIsElementPresent(locator);

		if (!loaded)
			return (loaded);
		boolean active = this.zIsVisiblePerPosition(locator, 0, 0);
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

		tracer.trace("Navigate to " + this.myPageName());

		String locator = "css=[id='zov__main_Mail']";
		// Make sure we are logged into the Ajax app
		// if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive())
		// ((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();

		// make sure mail page is loaded
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			((AppAjaxClient) MyApplication).zPageMail.zNavigateTo();
			GeneralUtility.waitForElementPresent(this,
					PageMain.Locators.zAppbarBriefcase, 20000);
		} else {
			zWaitForElementPresent(locator);
		}
		// Click on Briefcase icon
		zClick(PageMain.Locators.zAppbarBriefcase);

		zWaitForBusyOverlay();

		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			zWaitForActive();
		} else {
			zWaitForElementPresent(Locators.zBriefcaseFolderIcon.locator);
		}
	}

	public AbsPage zToolbarPressButton(Button button, IItem item)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the " + button + " button");

		if (button == null)
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (button == Button.B_NEW) {
			// Check if the button is disabled
			locator = Locators.zNewMenuLeftIconBtn.locator;

			String attrs = sGetAttribute("css=td[" + locator + "]>div@class");

			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException(button + " is disabled " + attrs);
			}

			// Click on New Document icon
			this.zClick(locator);

			zWaitForBusyOverlay();

			// isEditDocLoaded("Zimbra Docs", "");

			page = new DocumentBriefcaseNew(this.MyApplication);

			page.zIsActive();

			return page;
		} else if (button == Button.B_UPLOAD_FILE) {
			// Check if the button is disabled
			locator = Locators.zUploadFileIconBtn.locator;

			String attrs = sGetAttribute("css=td[" + locator + "]>div@class");

			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException(button + " is disabled " + attrs);
			}

			page = null;
		} else if (button == Button.B_EDIT_FILE) {
			// Check if the button is disabled
			locator = Locators.zEditFileIconBtn.locator;

			String attrs = sGetAttribute("css=td[" + locator + "]>div@class");

			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException(button + " is disabled " + attrs);
			}

			page = new DocumentBriefcaseEdit(MyApplication, (DocumentItem) item);
		} else if (button == Button.B_DELETE) {
			// Check if the button is disabled
			locator = Locators.zDeleteIconBtn.locator;

			String attrs = sGetAttribute("css=td[" + locator + "]>div@class");

			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException(button + " is disabled " + attrs);
			}
			page = new DialogConfirm(DialogConfirm.Confirmation.DELETE, MyApplication, this);
		} else if (button == Button.B_OPEN_IN_SEPARATE_WINDOW) {
			// Check if the button is disabled
			locator = Locators.zOpenFileInSeparateWindowIconBtn.locator;

			String attrs = sGetAttribute("css=td[" + locator + "]>div@class");

			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException(button + " is disabled " + attrs);
			}

			page = new DocumentBriefcaseOpen(this.MyApplication);
		} else if (button == Button.B_MOVE) {
			// Check if the button is disabled
			locator = Locators.zMoveIconBtn.locator;

			String attrs = sGetAttribute("css=td[" + locator + "]>div@class");

			if (attrs.contains("ZDisabledImage")) {
				throw new HarnessException(button + " is disabled " + attrs);
			}

			page = new DialogMove(MyApplication, this);
		} else if (button == Button.B_PRINT) {
			throw new HarnessException("implement Print dialog");
		} else if (button == Button.B_TAG) {
			// For "TAG" without a specified pulldown option, just click on the
			// button
			// To use "TAG" with a pulldown option, see
			// zToolbarPressPulldown(Button, Button)
			throw new HarnessException("implement Print dialog");
		} else if (button == Button.B_LISTVIEW) {
			throw new HarnessException("implement Print dialog");
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
		this.zClickAt(locator, "0,0");

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("
				+ pulldown + ", " + option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (pulldown == Button.B_NEW) {
			if (option == Button.O_NEW_BRIEFCASE) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_DOCUMENT) {
				pulldownLocator = Locators.zNewMenuArrowBtn.locator;
			
				optionLocator = "css=table[class='DwtMenuTable'] td[id$='_title']:contains('Document')";

				page = new DocumentBriefcaseNew(this.MyApplication);
				// FALL THROUGH
			} else if (option == Button.O_NEW_FOLDER) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_TAG) {
				pulldownLocator = Locators.zNewMenuArrowBtn.locator;

				optionLocator = "css=td[id$='_title'][class=ZWidgetTitle]:contains('Tag')";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}
		} else if (pulldown == Button.B_TAG) {
			if (option == Button.O_TAG_NEWTAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id$='__TAG_MENU|MENU|NEWTAG_title']";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else if (option == Button.O_TAG_REMOVETAG) {
				// Using General shortcuts: Type "u" shortcut
				// zKeyboard.zTypeCharacters(Shortcut.S_MAIL_REMOVETAG.getKeys());

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id$='__TAG_MENU|MENU|REMOVETAG_title']";

				page = null;

				// FALL THROUGH
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}
		} else if (pulldown == Button.B_SEND) {
			if (option == Button.O_SEND_AS_ATTACHMENT) {

				pulldownLocator = "css=td[id$='__SEND_FILE_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id$='_title']:contains('Send as attachment')";

				page = new FormMailNew(this.MyApplication);

				// FALL THROUGH
			} else if (option == Button.O_SEND_LINK) {

				pulldownLocator = "css=td[id$='__SEND_FILE_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id$='_title']:contains('Send link')";

				page = new DialogConfirm(
						DialogConfirm.Confirmation.SENDLINK,
						this.MyApplication, this);

				// FALL THROUGH
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

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
				}

				this.zClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
				if (option == Button.O_SEND_AS_ATTACHMENT)
					zWaitForElementPresent("css=div[id$=_attachments_div] a[class='AttLink']");
			}
		}
		// Return the specified page, or null if not set
		return (page);
	}

	public AbsPage zToolbarPressPulldown(Button pulldown, String option)
			throws HarnessException {

		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("
				+ pulldown + ", " + option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (pulldown == Button.B_TAG) {
			if (option.length() > 0) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[class=ZWidgetTitle]:contains(" + option
						+ ")";

				page = null;

				// FALL THROUGH
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
			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			this.zClick(pulldownLocator);

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
				}

				this.zClick(optionLocator);

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				page.zWaitForActive();
			}
		}
		// Return the specified page, or null if not set
		return (page);
	}

	public AbsPage zListItem(Action action, IItem item) throws HarnessException {

		// Validate the arguments
		if ((action == null) || (item == null)) {
			throw new HarnessException("Must define an action and item");
		}

		if (!((item instanceof FolderItem) || (item instanceof FileItem) || (item instanceof DocumentItem))) {
			throw new HarnessException("Not supported item: " + item.getClass());
		}

		String itemName = item.getName();

		tracer.trace(action + " on briefcase item = " + itemName);

		logger.info(myPageName() + " zListItem(" + action + ", " + itemName
				+ ")");

		AbsPage page = null;
		String listLocator = Locators.briefcaseListView.locator;
		String itemLocator = listLocator
				+ " div[id^='zli__BDLV__'][class^='Row']";
		String itemNameLocator = itemLocator
				+ " td[width*='auto'] div:contains(" + itemName + ")";

		/*
		 * listLocator = "div[id='zl__BDLV__rows'][class='DwtListView-Rows']";
		 * String rowLocator = rowLocator = "div[id^='zli__BDLV__']"; rowLocator
		 * = "css=div:contains[id^='zli__BDLV__']"; rowLocator =
		 * "css=div:contains[id:contains('zli__BDLV__')]";
		 * 
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

		if (!this.sIsElementPresent(listLocator))
			throw new HarnessException("List View Rows is not present "
					+ listLocator);

		if (!GeneralUtility.waitForElementPresent(this, itemNameLocator))
			throw new HarnessException("Unable to locate item with name("
					+ itemName + ")");
		if (action == Action.A_LEFTCLICK) {

			zWaitForElementPresent(itemNameLocator);

			// Left-Click on the item
			this.zClick(itemNameLocator);

			// page = new DocumentPreview(MyApplication);

		} else if (action == Action.A_DOUBLECLICK) {
			zWaitForElementPresent(itemNameLocator);

			// double-click on the item
			this.sDoubleClick(itemNameLocator);

			if (item instanceof DocumentItem) {
				page = new DocumentBriefcaseOpen(MyApplication,
						(DocumentItem) item);
			} else
				page = null;
		} else if (action == Action.A_BRIEFCASE_CHECKBOX) {
			String checkBoxLocator = "";

			int count = sGetCssCount(itemLocator);

			for (int i = 1; i <= count; i++) {
				if (sIsElementPresent(itemLocator + ":nth-child(" + i
						+ "):contains(" + itemName + ")")) {
					checkBoxLocator = itemLocator + ":nth-child(" + i
							+ ") div[class^=ImgCheckbox]";
					break;
				}
			}

			if (!this.sIsElementPresent(checkBoxLocator))
				throw new HarnessException("Checkbox locator is not present "
						+ checkBoxLocator);

			String image = this.sGetAttribute(checkBoxLocator + "@class");

			if (image.equals("ImgCheckboxChecked"))
				throw new HarnessException(
						"Trying to check box, but it was already enabled");

			// Left-Click on the Check box field
			this.zClick(checkBoxLocator);

			// No page to return
			page = null;

			// FALL THROUGH

		} else if (action == Action.A_RIGHTCLICK) {
			zWaitForElementPresent(itemNameLocator);

			// Right-Click on the item
			this.zRightClick(itemNameLocator);

			// Now the ContextMenu is opened
			// Click on the specified option

		} else {
			throw new HarnessException("implement me!  action = " + action);
		}

		zWaitForBusyOverlay();

		if (page != null) {
			page.zWaitForActive();
		}
		return page;
	}

	public AbsPage zListItem(Action action, Button option, IItem item)
			throws HarnessException {

		if (action == null)
			throw new HarnessException("action cannot be null");
		if (option == null)
			throw new HarnessException("button cannot be null");
		if (item == null)
			throw new HarnessException("Item cannot be null or blank");

		String subject = item.getName();

		tracer.trace(action + " then " + option + " on briefcase item = "
				+ subject);

		logger.info(myPageName() + " zListItem(" + action + ", " + option
				+ ", " + subject + ")");

		AbsPage page = null;
		String listLocator = Locators.briefcaseListView.locator;
		// String rowLocator;
		String itemlocator = null;

		if (!this.sIsElementPresent(listLocator))
			throw new HarnessException("List View Rows is not present "
					+ listLocator);

		itemlocator = listLocator + " td[width*='auto'] div:contains("
				+ subject + ")";

		if (action == Action.A_RIGHTCLICK) {

			zWaitForElementPresent(itemlocator);

			// Right-Click on the item
			this.zRightClick(itemlocator);

			// Now the ContextMenu is opened
			// Click on the specified option

			String optionLocator = null;

			if (option == Button.B_RENAME) {

				optionLocator = "css=td#zmi__Briefcase__RENAME_FILE_title:contains(Rename)";

				page = null;

			} else if (option == Button.O_EDIT) {

				optionLocator = "css=td#zmi__Briefcase__EDIT_FILE_title:contains(Edit)";

				page = new DocumentBriefcaseEdit(MyApplication,
						(DocumentItem) item);

			} else if (option == Button.O_OPEN) {

				optionLocator = "css=td#zmi__Briefcase__OPEN_FILE_title:contains(Open)";

				page = new DocumentBriefcaseOpen(MyApplication,
						(DocumentItem) item);

			} else if (option == Button.O_DELETE) {

				optionLocator = "css=td#zmi__Briefcase__DELETE_title:contains(Delete)";

				page = new DialogConfirm(DialogConfirm.Confirmation.DELETE, MyApplication, this);

			} else if (option == Button.O_MOVE) {

            optionLocator = "css=td#zmi__Briefcase__MOVE_title:contains(Move)";

            page = new DialogMove(MyApplication, this);

			} else if (option == Button.O_SEND_AS_ATTACHMENT) {

            optionLocator = "css=div#zmi__Briefcase__SEND_FILE_AS_ATT:contains(Send as attachment(s))";

            page = new FormMailNew(this.MyApplication);

			} else if (option == Button.O_SEND_LINK) {

            optionLocator = "css=td#zmi__Briefcase__SEND_FILE_title:contains('Send link')";

            page = new DialogConfirm(DialogConfirm.Confirmation.SENDLINK,
                  this.MyApplication, this);
			} else {
				throw new HarnessException("implement action: " + action
						+ " option:" + option);
			}

			// click on the option
			this.zClick(optionLocator);

			this.zWaitForBusyOverlay();

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = " + action);
		}

		if (page != null) {
			page.zWaitForActive();
		}

		// Default behavior
		return (page);
	}

	public AbsPage zListItem(Action action, Button option, String subOption,
	      IItem item) throws HarnessException {

	   if (action == null)
	      throw new HarnessException("action cannot be null");
	   if (option == null)
	      throw new HarnessException("button cannot be null");
	   if (subOption == null)
	      throw new HarnessException("subOption button cannot be null");
	   if (item == null)
	      throw new HarnessException("Item cannot be null or blank");

	   String rowItem = item.getName();

	   tracer.trace(action + " then " + option + " then " + subOption
	         + " on briefcase item = " + rowItem);

	   logger.info(myPageName() + " zListItem(" + action + ", " + option
	         + ", " + subOption + ", " + rowItem + ")");

	   AbsPage page = null;

	   String listLocator = Locators.briefcaseListView.locator;

	   String itemlocator = null;

	   if (!this.sIsElementPresent(listLocator))
	      throw new HarnessException("List View Rows is not present "
	            + listLocator);

	   itemlocator = listLocator + " div:contains(" + rowItem + ")";

	   if (action == Action.A_RIGHTCLICK) {

	      zWaitForElementPresent(itemlocator);

	      // Right-Click on the item
	      this.zRightClickAt(itemlocator, "0,0");

	      // Now the ContextMenu is opened
	      // Click on the specified option

	      String optionLocator = null;

	      if (option == Button.O_TAG_FILE) {

	         optionLocator = "css=td#zmi__Briefcase__TAG_MENU_dropdown>div[class=ImgCascade]";

	      } else {
	         throw new HarnessException("implement action: " + action
	               + " option:" + option);
	      }

	      // click on the option
	      this.zClickAt(optionLocator, "0,0");

	      // Now the ContextMenu option is opened
	      // Click on the specified sub option

	      String subOptionLocator = "css=div[id=zmi__Briefcase__TAG_MENU|MENU] [class=ZWidgetTitle]:contains("
	         + subOption + ")";

	      // click on the sub option
	      this.zClickAt(subOptionLocator, "0,0");

	      this.zWaitForBusyOverlay();

	      page = null;

	      // FALL THROUGH

	   } else {
	      throw new HarnessException("implement me!  action = " + action);
	   }

	   if (page != null) {
	      page.zWaitForActive();
	   }

	   // Default behavior
	   return (page);
	}

	@Override
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {

		if (shortcut == null)
			throw new HarnessException("Shortcut cannot be null");

		tracer.trace("Using the keyboard, press the " + shortcut.getKeys()
				+ " keyboard shortcut");

		AbsPage page = null;

		String keyCode = "";

		if ((shortcut == Shortcut.S_NEWITEM)
				|| (shortcut == Shortcut.S_NEWDOCUMENT)) {

			// "New Document" shortcut result in a new document page opening
			page = new DocumentBriefcaseNew(this.MyApplication);

			keyCode = "78";
		} else if (shortcut == Shortcut.S_DELETE) {

			// "Delete Document" shortcut leads to Confirmation Dialog opening
			page = new DialogConfirm(DialogConfirm.Confirmation.DELETE, MyApplication, this);

			keyCode = "46";
		} else if (shortcut == Shortcut.S_BACKSPACE) {

			// "Delete Document" shortcut leads to Confirmation Dialog opening
			page =  new DialogConfirm(DialogConfirm.Confirmation.DELETE,MyApplication, this);

			keyCode = "8";
		} else if (shortcut == Shortcut.S_NEWTAG) {

			// "NEW TAG" shortcut opens "Create New Tag" dialog
			page = new DialogTag(MyApplication, this);

			keyCode = "78,84";
		} else if (shortcut == Shortcut.S_MOVE) {

			// "Move" shortcut opens "Choose Folder" dialog
			page = new DialogMove(MyApplication, this);

			keyCode = "77";
		} else if (shortcut == Shortcut.S_NEWFOLDER) {

         // "NEW Folder" shortcut opens "Create New Folder" dialog
         // due to the bug #63029 it opens dialog with Mail tree view
         page = new DialogCreateBriefcaseFolder(MyApplication, this);

         keyCode = "78,70";
		} else {
			throw new HarnessException("implement shortcut: " + shortcut);
		}

		// zKeyboard.zTypeCharacters(shortcut.getKeys());

		for (String kc : keyCode.split(",")) {
			/*
			 * vare=document.createEvent('KeyboardEvent');
			 * if(typeof(e.initKeyboardEvent)!='undefined'){e.initEvent()}
			 * else{e.initKeyEvent()}
			 */
			sGetEval("if(window.KeyEvent)"
					+ "{var evObj = document.createEvent('KeyEvents');"
					+ "evObj.initKeyEvent( 'keydown', true, true, window, false, false, false, false,"
					+ kc + ", 0 );} "
					+ "else {var evObj = document.createEvent('HTMLEvents');"
					+ "evObj.initEvent( 'keydown', true, true, window, 1 );"
					+ "evObj.keyCode = " + kc + ";}"
					+ "var x = selenium.browserbot.findElementOrNull('"
					+ "css=html>body" + "'); "
					+ "x.focus(); x.dispatchEvent(evObj);");
		}

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If a page is specified, wait for it to become active
		if (page != null) {
			page.zWaitForActive(); // throws a HarnessException if never active
		}
		return (page);
	}

	public void rename(String text) throws HarnessException {
		// ClientSessionFactory.session().selenium().getEval("var x = selenium.browserbot.findElementOrNull(\""+Locators.zFrame.locator+"\");if(x!=null)x=x.contentWindow.document.body;if(browserVersion.isChrome){x.textContent='"+text+"';}else if(browserVersion.isIE){x.innerText='"+text+"';}");
		logger.info("renaming to: " + text);
		zSelectWindow("Zimbra: Briefcase");
		// sSelectFrame("relative=top");
		sType(Locators.zRenameInput.locator, text);
		sFocus(Locators.zRenameInput.locator);
		// hit <Enter> key
		// sKeyPressNative(Integer.toString(KeyEvent.VK_ENTER));

		sGetEval("if(window.KeyEvent)"
				+ "{var evObj = document.createEvent('KeyEvents');"
				+ "evObj.initKeyEvent( 'keyup', true, true, window, false, false, false, false, 13, 0 );} "
				+ "else {var evObj = document.createEvent('HTMLEvents');"
				+ "evObj.initEvent( 'keyup', true, true, window, 1 );"
				+ "evObj.keyCode = 13;}"
				+ "var x = selenium.browserbot.findElementOrNull('"
				+ Locators.zRenameInput.locator + "'); "
				+ "x.blur(); x.focus(); x.dispatchEvent(evObj);");
	}

	public void isOpenDocLoaded(DocumentItem docItem) throws HarnessException {
		zWaitForWindow(docItem.getName());

		zSelectWindow(docItem.getName());

		zWaitForElementPresent("css=td[class='ZhAppContent'] div:contains('"
				+ docItem.getDocText() + "')");
	}

	public void isOpenFileLoaded(String windowName, String text)
			throws HarnessException {
		zWaitForWindow(windowName);

		zSelectWindow(windowName);

		zWaitForElementPresent(Locators.zFileBodyField.locator + ":contains('"
				+ text + "')");
	}

	public boolean isPresentInListView(String itemName) throws HarnessException {
		String itemLocator = Locators.briefcaseListView.locator
				+ " td[width*='auto'] div:contains(" + itemName + ")";

		return sIsElementPresent(itemLocator);
	}

	public boolean waitForPresentInListView(String itemName)
			throws HarnessException {
		String itemLocator = Locators.briefcaseListView.locator
				+ " td[width*='auto'] div:contains(" + itemName + ")";

		zWaitForElementPresent(itemLocator);
		return true;
	}

	public boolean isOptionDisabled(Locators name) throws HarnessException {
		return sIsElementPresent("css=td[" + name.locator
				+ "]>div[class*=ZDisabledImage]");
	}

	public boolean waitForDeletedFromListView(String itemName)
			throws HarnessException {
		String itemLocator = Locators.briefcaseListView.locator
				+ " td[width*='auto'] div:contains(" + itemName + ")";

		zWaitForElementDeleted(itemLocator);
		return true;
	}

	public String getItemNameFromListView(String itemName) throws HarnessException {
		String itemLocator = Locators.briefcaseListView.locator
				+ " td[width*='auto'] div:contains(" + itemName + ")";

		return sGetText(itemLocator);
	}

	public boolean isEditDocLoaded(DocumentItem docItem)
			throws HarnessException {
		zWaitForWindow(docItem.getName());

		zSelectWindow(docItem.getName());

		zWaitForElementPresent("css=div[class='ZDToolBar ZWidget']");

		zWaitForElementPresent("css=iframe[id*='DWT'][class='ZDEditor']");

		zWaitForIframeText("css=iframe[id*='DWT'][class='ZDEditor']", docItem
				.getDocText());

		return true;
	}

	public boolean isLockIconPresent(IItem item) throws HarnessException {
	   boolean present = false;
	   String itemName = item.getName();

	   String listLocator = Locators.briefcaseListView.locator;
	   String itemLocator = listLocator
	         + " div[id^='zli__BDLV__'][class^='Row']";
	   String itemNameLocator = itemLocator + " div:contains(" + itemName
	         + ")";

	   zWaitForElementPresent(itemNameLocator);

	   String lockIconLocator = "";

	   int count = sGetCssCount(itemLocator);

	   for (int i = 1; i <= count; i++) {
	      if (sIsElementPresent(itemLocator + ":nth-child(" + i
	            + "):contains(" + itemName + ")")) {
	         lockIconLocator = itemLocator + ":nth-child(" + i
	               + ") div[id^=zlif__BDLV__][id$=__loid][class^=Img]";
	         break;
	      }
	   }
	   
	   if (!this.sIsElementPresent(lockIconLocator))
	      throw new HarnessException("Lock icon locator is not present "
	            + lockIconLocator);

	   String image = this.sGetAttribute(lockIconLocator + "@class");

	   if (image.equals("ImgPadLock"))
	      present = true;

	   return present;      
	}

	public void deleteFileByName(String docName) throws HarnessException {
	   deleteFileByName(docName, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	public void deleteFileByName(String docName,
	      SOAP_DESTINATION_HOST_TYPE destType,
         String accountName) throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();
		account
				.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
						+ "<query>" + docName + "</query>" + "</SearchRequest>",
						destType, accountName);
		String id = account.soapSelectValue("//mail:doc", "id");
		deleteFileById(id, destType, accountName);
	}

	public void deleteFileById(String docId) throws HarnessException {
	   deleteFileById(docId, SOAP_DESTINATION_HOST_TYPE.SERVER, null);
	}

	public void deleteFileById(String docId,
	      SOAP_DESTINATION_HOST_TYPE destType,
         String accountName) throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + docId + "' op='trash'/>"
				+ "</ItemActionRequest>",
				destType, accountName);
	}

	public EnumMap<Response.ResponsePart, String> displayFile(String filename,
         Map<String, String> params) throws HarnessException {
	   return displayFile(filename, params, SOAP_DESTINATION_HOST_TYPE.SERVER);
	}

	public EnumMap<Response.ResponsePart, String> displayFile(String filename,
			Map<String, String> params,
			SOAP_DESTINATION_HOST_TYPE destType) throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();
		
		RestUtil util = new RestUtil();

		switch (destType) {
		case SERVER:
		   util.setAuthentication(account);
		   break;
		case CLIENT:
		   util.setZDAuthentication(account);
		   break;
		}
		
		util.setPath("/home/~/Briefcase/" + filename);

		for (Map.Entry<String, String> query : params.entrySet()) {
			util.setQueryParameter(query.getKey(), query.getValue());
		}

		if (util.doGet() != HttpStatus.SC_OK)
			throw new HarnessException("Unable to open " + filename + " in "
					+ util.getLastURI());

		final String responseHeaders = util.getLastResponseHeaders();
		final String responseBody = util.getLastResponseBody();
	
		return new EnumMap<Response.ResponsePart, String>(
				Response.ResponsePart.class) {
			private static final long serialVersionUID = 1L;
			{
				put(Response.ResponsePart.HEADERS, responseHeaders);
				put(Response.ResponsePart.BODY, responseBody);
			}
		};
	}

	public void closeWindow() throws HarnessException {
		tracer.trace("Close the separate window");

		this.sClose();		
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		throw new HarnessException("implement me!  button = " + button);
	}

	@Override
	public AbsPage zListItem(Action action, String name)
			throws HarnessException {
		throw new HarnessException("implement me! : action = " + action
				+ " name = " + name);
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String subject)
			throws HarnessException {
		throw new HarnessException("implement me! : action=" + action
				+ " option=" + option + " subject=" + subject);
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		throw new HarnessException("implement me! : action=" + action
				+ " subOption=" + subOption + "item=" + item);
	}
}
