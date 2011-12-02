/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import java.util.EnumMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpStatus;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.DocumentItem;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.RestUtil;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogMove;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.PageMain;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogCreateFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;

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
				"css=td[id=zb__BDLV__NEW_MENU_left_icon]");
		public static final Locators zNewMenuArrowBtn = new Locators(
				"css=div[id=zb__BDLV__NEW_MENU] div[class^=ImgSelectPullDownArrow]");
		public static final Locators zUploadFileIconBtn = new Locators(
				"id=zb__BDLV__NEW_FILE_left_icon");
		public static final Locators zEditFileIconBtn = new Locators(
				"id=zb__BDLV__EDIT_FILE_left_icon");
		public static final Locators zEditFileBtn = new Locators(
				"css=div[id=zb__BDLV__EDIT_FILE]");
		public static final Locators zEditFileMenuItem = new Locators(
				"css=div[id=EDIT_FILE]");
		public static final Locators zOpenFileInSeparateWindowIconBtn = new Locators(
				"id=zb__BDLV__NEW_BRIEFCASE_WIN_left_icon");
		public static final Locators zDeleteIconBtn = new Locators(
				"id=zb__BDLV__DELETE_left_icon");
		public static final Locators zDeleteBtn = new Locators(
				"css=div[id=zb__BDLV__DELETE]");
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
		public static final Locators zHeaderCheckBox = new Locators(
				"css=div[id=zlhi__BDLV__se]");
		public static final Locators zListItemLockIcon = new Locators(
				"css=div[id^=zlif__BDLV__][id$=__loid][class=ImgPadLock]");

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
					.AccountZWC().EmailAddress;
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

		// Make sure we are logged into the Ajax app
		if (!((AppAjaxClient) MyApplication).zPageMain.zIsActive())
			((AppAjaxClient) MyApplication).zPageMain.zNavigateTo();

		// Click on Briefcase icon
		zClickAt(PageMain.Locators.zAppbarBriefcase, "0,0");

		zWaitForBusyOverlay();

		zWaitForElementPresent(Locators.zBriefcaseFolderIcon.locator);

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

			// Click on New Document icon
			this.zClickAt(locator, "0,0");

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

			locator = Locators.zEditFileBtn.locator;

			/*
			 * //Check if the button is disabled String attrs =
			 * sGetAttribute(locator + "@class");
			 * 
			 * if (attrs.contains("ZDisabled")) { throw new
			 * HarnessException(button + " is disabled " + attrs); }
			 */

			page = new DocumentBriefcaseEdit(MyApplication, (DocumentItem) item);
		} else if (button == Button.B_DELETE) {

			locator = Locators.zDeleteBtn.locator;

			// Check if the button is disabled
			String attrs = sGetAttribute(locator + "@class");

			if (attrs.contains("ZDisabled")) {
				throw new HarnessException(button + " is disabled " + attrs);
			}

			page = new DialogConfirm(DialogConfirm.Confirmation.DELETE,
					MyApplication, this);
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

	public AbsPage zToolbarPressPulldown(Button pulldown, Button option,
			IItem item) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (pulldown == Button.B_NEW) {
			pulldownLocator = Locators.zNewMenuArrowBtn.locator;
			if (option == Button.O_NEW_BRIEFCASE) {
				if (ZimbraSeleniumProperties.zimbraGetVersionString().contains(
						"7.1."))
					optionLocator = "css=tr[id=POPUP_NEW_BRIEFCASE]";
				else
					optionLocator = "css=div#zb__BDLV__NEW_MENU_NEW_BRIEFCASE";

				page = new DialogCreateBriefcaseFolder(this.MyApplication,
						((AppAjaxClient) MyApplication).zPageBriefcase);

				// FALL THROUGH
			} else if (option == Button.O_NEW_DOCUMENT) {
				if (ZimbraSeleniumProperties.zimbraGetVersionString().contains(
						"7.1."))
					optionLocator = "css=tr[id=POPUP_NEW_DOC]";
				else
					optionLocator = "css=div#zb__BDLV__NEW_MENU_NEW_DOC";

				page = new DocumentBriefcaseNew(this.MyApplication);

				// FALL THROUGH
			} else if (option == Button.O_NEW_FOLDER) {
				throw new HarnessException("implement me!");
			} else if (option == Button.O_NEW_TAG) {
				if (ZimbraSeleniumProperties.zimbraGetVersionString().contains(
						"7.1."))
					optionLocator = "css=tr[id=POPUP_NEW_TAG]>td[id$=_title]:contains(Tag)";
				else
					optionLocator = "css=div#zb__BDLV__NEW_MENU_NEW_TAG";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}
		} else if (pulldown == Button.B_TAG) {
			if (option == Button.O_TAG_NEWTAG) {

				pulldownLocator = "css=td[id=zb__BDLV__TAG_MENU_dropdown]>div[class='ImgSelectPullDownArrow']";
				optionLocator = "css=td[id^=briefcase_newtag]";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else if (option == Button.O_TAG_REMOVETAG) {
				// Using General shortcuts: Type "u" shortcut
				// zKeyboard.zTypeCharacters(Shortcut.S_MAIL_REMOVETAG.getKeys());

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id^=briefcase_removetag]";

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

				page = new DialogConfirm(DialogConfirm.Confirmation.SENDLINK,
						this.MyApplication, this);

				// FALL THROUGH
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}
		} else if (pulldown == Button.B_ACTIONS) {

			pulldownLocator = "css=td[id=zb__BDLV__ACTIONS_MENU_dropdown]>div[class='ImgSelectPullDownArrow']";

			if (option == Button.B_LAUNCH_IN_SEPARATE_WINDOW) {

				optionLocator = "css=td[id$=NEW_BRIEFCASE_WIN_title]:contains('Launch in a separate window')";

				page = new DocumentBriefcaseOpen(this.MyApplication, item);

			} else if (option == Button.O_SEND_AS_ATTACHMENT) {

				optionLocator = "css=td[id$='_title']:contains('Send as attachment')";

				page = new FormMailNew(this.MyApplication);

				// FALL THROUGH
			} else if (option == Button.O_SEND_LINK) {

				optionLocator = "css=td[id$='_title']:contains('Send link')";

				page = new DialogConfirm(DialogConfirm.Confirmation.SENDLINK,
						this.MyApplication, this);

				// FALL THROUGH
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}
		} else if (pulldown == Button.B_MOVE) {

			if (option == Button.O_NEW_FOLDER) {

				// Check if we are CLV or MV
				if (this.zIsVisiblePerPosition("css=div#ztb__CLV2-main", 0, 0)) {
					pulldownLocator = "css=td#zb__CLV2-main__MOVE_MENU_dropdown>div";
				} else {
					pulldownLocator = "css=td#zb__TV-main__MOVE_MENU_dropdown>div";
				}
				optionLocator = "css=div[class='DwtFolderChooser'] div[id$='_newButtonDivId'] td[id$='_title']";
				page = new DialogCreateFolder(this.MyApplication, this);

			} else {
				throw new HarnessException("no logic defined for B_MOVE and "
						+ option);
			}

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException(pulldownLocator + " not present!");
			}

			// 8.0 change ... need zClickAt()
			// this.zClick(pulldownLocator);
			this.zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (!this.sIsElementPresent(optionLocator)) {
				throw new HarnessException(optionLocator + " not present!");
			}

			this.zClick(optionLocator);

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			page.zWaitForActive();

			return (page);

		} else {
			throw new HarnessException("no logic defined for pulldown/option "
					+ pulldown + "/" + option);
		}

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " option "
						+ option + " pulldownLocator " + pulldownLocator
						+ " not present!");
			}

			// this.sMouseOver(pulldownLocator);
			// this.sFocus(pulldownLocator);
			// this.zRightClickAt(pulldownLocator, "0,0");
			// sMouseDownRight(pulldownLocator);
			// sMouseUpRight(pulldownLocator);

			if (zIsBrowserMatch(BrowserMasks.BrowserMaskIE)) {
				if (pulldown == Button.B_NEW) {
					sGetEval("if(document.createEventObject()){var evObj = document.createEventObject(); "
							+ "var x = selenium.browserbot.findElementOrNull('"
							+ pulldownLocator
							+ "');"
							+ "x.focus();x.blur();x.fireEvent('onmouseup');}"
							+ "else{var evObj = document.createEvent('MouseEvents');"
							+ "evObj.initMouseEvent( 'mouseup', true, true, window, 0, 0, 0, 0, 0, false, false, false, false, 0, null);"
							+ "var x = selenium.browserbot.findElementOrNull('"
							+ pulldownLocator
							+ "');"
							+ "x.focus();x.blur();x.dispatchEvent(evObj);}");
				} else
					zClick(pulldownLocator);
			} else
				zClickAt(pulldownLocator, "0,0");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException("Button " + pulldown
							+ " option " + option + " optionLocator "
							+ optionLocator + " not present!");
				}

				// work around for bug 59722
				if (optionLocator.contains("Dc")) {
					for (int i = 0; i < 6; i++) {
						zKeyEvent(optionLocator, "40", "keydown");
					}
					zKeyEvent(optionLocator, "13", "keydown");
				} else if (optionLocator.contains("Tg")) {
					for (int i = 0; i < 8; i++) {
						zKeyEvent(optionLocator, "40", "keydown");
					}
					zKeyEvent(optionLocator, "13", "keydown");
				} else
					this.zClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
			}

			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if (page != null) {
				if (option == Button.O_SEND_AS_ATTACHMENT) {
					String locator = "css=div[id$=_attachments_div] a[class='AttLink']";
					if (!this.zWaitForElementPresent(locator, "3000")) {
						throw new HarnessException(locator + " not present");
					}
				} else if (option == Button.O_TAG_NEWTAG) {
					String locator = "id=CreateTagDialog";
					if (!this.zWaitForElementPresent(locator, "3000")) {
						throw new HarnessException(locator + " not present");
					}
				} else
					page.zWaitForActive();
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
			}
		}
		// Return the specified page, or null if not set
		return (page);
	}

	/**
	 * Activate a pulldown with dynamic values, such as "Move to folder" and
	 * "Add a tag".
	 * 
	 * @param pulldown
	 *            the toolbar button to press
	 * @param dynamic
	 *            the toolbar item to click such as FolderItem or TagItem
	 * @throws HarnessException
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Object dynamic)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("
				+ pulldown + ", " + dynamic + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + dynamic);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (dynamic == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_MOVE) {

			if (!(dynamic instanceof FolderItem))
				throw new HarnessException("if pulldown = " + Button.B_MOVE
						+ ", then dynamic must be FolderItem");

			FolderItem folder = (FolderItem) dynamic;

			pulldownLocator = "css=td#zb__BDLV__MOVE_MENU_dropdown>div";
			optionLocator = "css=td#zti__DwtFolderChooser_BriefcaseBDLV__"
					+ folder.getId() + "_textCell";

			page = null;
		} else {
			throw new HarnessException("no logic defined for pulldown/dynamic "
					+ pulldown + "/" + dynamic);
		}
		// Default behavior
		if (pulldownLocator != null) {
			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown
						+ " pulldownLocator " + pulldownLocator
						+ " not present!");
			}
			this.zClickAt(pulldownLocator, "");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			SleepUtil.sleepVerySmall();

			if (optionLocator != null) {
				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException(" dynamic " + dynamic
							+ " optionLocator " + optionLocator
							+ " not present!");
				}
				this.zClickAt(optionLocator, "");

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

	public void zHeader(Action action) throws HarnessException {
		logger.info(myPageName() + " zHeader(" + action + ")");

		tracer.trace(action + " on briefcase header");

		// Validate the arguments
		if (action == null)
			throw new HarnessException("action cannot be null!");

		// Default behavior variables
		String locator = null; // If set, this will be clicked

		// Based on the action specified, take the appropriate action(s)
		if (action == Action.A_BRIEFCASE_HEADER_CHECKBOX) {

			locator = Locators.zHeaderCheckBox.locator;

			// Left-Click on the header
			this.zClickAt(locator, "0,0");
		} else {
			throw new HarnessException("implement me!  action = " + action);
		}
		zWaitForBusyOverlay();
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
		String itemNameLocator = itemLocator + " div:contains(" + itemName
				+ ")";

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

		if (!this.sIsElementPresent(itemLocator))
			throw new HarnessException("List View Rows is not present "
					+ listLocator);

		if (action == Action.A_LEFTCLICK) {
			zWaitForElementPresent(itemNameLocator);

			// Left-Click on the item
			// temporary workaround for FOSS
			if (ZimbraSeleniumProperties.zimbraGetVersionString().contains(
					"FOSS"))
				zListItem(Action.A_BRIEFCASE_CHECKBOX, item);
			else
				this.zClickAt(itemNameLocator, "0,0");

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
			zWaitForElementPresent(itemNameLocator);

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
			this.zClickAt(checkBoxLocator, "0,0");

			// No page to return
			page = null;

			// FALL THROUGH

		} else if (action == Action.A_RIGHTCLICK) {
			zWaitForElementPresent(itemNameLocator);

			// Right-Click on the item
			this.zRightClickAt(itemNameLocator, "0,0");

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

		itemlocator = listLocator + " div:contains(" + subject + ")";

		if (action == Action.A_RIGHTCLICK) {

			zWaitForElementPresent(itemlocator);

			// Right-Click on the item
			this.zRightClickAt(itemlocator, "0,0");

			// Now the ContextMenu is opened
			// Click on the specified option

			String optionLocator = null;

			if (option == Button.B_RENAME) {

				optionLocator = "css=div[id^=RENAME_FILE__] tr[id=POPUP_RENAME_FILE]>td[id^=RENAME_FILE]:contains(Rename)";

				page = null;

			} else if (option == Button.O_EDIT) {

				optionLocator = "css=td[id$=EDIT_FILE_title]:contains(Edit)";

				page = new DocumentBriefcaseEdit(MyApplication,
						(DocumentItem) item);

			} else if (option == Button.O_OPEN) {

				optionLocator = "css=td#OPEN_FILE_title:contains(Open)";

				page = new DocumentBriefcaseOpen(MyApplication,
						(DocumentItem) item);

			} else if (option == Button.O_SEND_LINK) {

				optionLocator = "css=tr[id=POPUP_SEND_FILE]>td[id^=SEND_FILE__]:contains('Send link(s)')";

				page = new DialogConfirm(DialogConfirm.Confirmation.SENDLINK,
						this.MyApplication, this);

			} else if (option == Button.O_SEND_AS_ATTACHMENT) {

				optionLocator = "css=div[id^=SEND_FILE_AS_ATT]:contains(Send as attachment(s))";

				page = new FormMailNew(this.MyApplication);

			} else if (option == Button.O_DELETE) {

				optionLocator = "css=div[id=zm__Briefcase] tr[id=POPUP_DELETE]";

				page = new DialogConfirm(DialogConfirm.Confirmation.DELETE,
						MyApplication, this);

			} else if (option == Button.O_MOVE) {

				optionLocator = "css=div[id=zm__Briefcase] tr[id=POPUP_MOVE]";

				page = new DialogMove(MyApplication, this);
			} else if (option == Button.O_TAG_FILE) {

				optionLocator = "css=td#zmi__Briefcase__TAG_MENU_title:contains('Tag File')";

				page = new DialogMove(MyApplication, this);
			} else if (option == Button.O_CHECK_IN_FILE) {

				optionLocator = "css=tr[id=POPUP_CHECKIN]>td[id^=CHECKIN__]:contains('Check In File')";

				page = new DialogCheckInFile(MyApplication, this);
			} else if (option == Button.O_DISCARD_CHECK_OUT) {

				optionLocator = "css=tr[id=POPUP_DISCARD_CHECKOUT]>td[id^=DISCARD_CHECKOUT__]:contains(Discard Check Out)";

				page = null;
			} else {
				throw new HarnessException("implement action: " + action
						+ " option:" + option);
			}

			// Make sure the button exists
			if (!this.sIsElementPresent(optionLocator)) {
				throw new HarnessException(optionLocator + " not present!");
			}

			// click on the option
			this.zClickAt(optionLocator, "0,0");

			this.zWaitForBusyOverlay();

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = " + action);
		}

		if (page != null) {
			if (option == Button.O_SEND_AS_ATTACHMENT) {
				zWaitForElementPresent("css=div[id$=_attachments_div] a[class='AttLink']");
				return page;
			} else
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

				optionLocator = "css=div[id=zm__Briefcase] tr[id=POPUP_TAG_MENU]>td[id^=TAG_MENU_dropdown]>div[class=ImgCascade]";

			} else {
				throw new HarnessException("implement action: " + action
						+ " option:" + option);
			}

			// click on the option
			this.zClickAt(optionLocator, "0,0");

			// Now the ContextMenu option is opened
			// Click on the specified sub option
			String subOptionLocator = "css=div[id^=TAG_MENU][id$=|MENU] [class=ZWidgetTitle]:contains("
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
			page = new DialogConfirm(DialogConfirm.Confirmation.DELETE,
					MyApplication, this);

			keyCode = "46";
		} else if (shortcut == Shortcut.S_BACKSPACE) {

			// "Delete Document" shortcut leads to Confirmation Dialog opening
			page = new DialogConfirm(DialogConfirm.Confirmation.DELETE,
					MyApplication, this);

			keyCode = "8";
		} else if (shortcut == Shortcut.S_NEWTAG) {

			// "NEW TAG" shortcut opens "Create New Tag" dialog
			page = new DialogTag(MyApplication, this);

			keyCode = "78,84";
		} else if (shortcut == Shortcut.S_NEWFOLDER) {

			// "NEW Folder" shortcut opens "Create New Folder" dialog
			// due to the bug #63029 it opens dialog with Mail tree view
			page = new DialogCreateBriefcaseFolder(MyApplication, this);

			keyCode = "78,70";
		} else if (shortcut == Shortcut.S_MOVE) {

			// "Move" shortcut opens "Choose Folder" dialog
			page = new DialogMove(MyApplication, this);

			keyCode = "77";
		} else {
			throw new HarnessException("implement shortcut: " + shortcut);
		}

		// zKeyboard.zTypeCharacters(shortcut.getKeys());

		zKeyDown(keyCode);

		/*
		 * for (String kc : keyCode.split(",")) {
		 * 
		 * //vare=document.createEvent('KeyboardEvent');
		 * //if(typeof(e.initKeyboardEvent)!='undefined'){e.initEvent()}
		 * //else{e.initKeyEvent()}
		 * 
		 * 
		 * sGetEval(
		 * "if(document.createEventObject){var body_locator=\"css=html>body\"; "
		 * + "var body=selenium.browserbot.findElement(body_locator);" +
		 * "var evObj = body.document.createEventObject();" + "evObj.keyCode=" +
		 * kc + ";evObj.repeat = false;" +
		 * "body.focus(); body.fireEvent(\"onkeydown\",evObj);}" +
		 * "else{if(window.KeyEvent){var evObj = document.createEvent('KeyEvents');"
		 * +
		 * "evObj.initKeyEvent( 'keydown', true, true, window, false, false, false, false,"
		 * + kc + ", 0 );}else {var evObj = document.createEvent('HTMLEvents');"
		 * + "evObj.initEvent( 'keydown', true, true, window, 1 );" +
		 * "evObj.keyCode = " + kc +
		 * ";}var x = selenium.browserbot.findElementOrNull('" + "css=html>body"
		 * + "');x.focus(); x.dispatchEvent(evObj);}"); }
		 */

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

		zKeyEvent(Locators.zRenameInput.locator, "13", "keyup");
	}

	public void typeKey(String locator, String keycode, String event)
			throws HarnessException {
		sFocus(locator);
		// hit <Enter> key
		// sKeyPressNative(Integer.toString(KeyEvent.VK_ENTER));

		sGetEval("if(document.createEventObject){var x=selenium.browserbot.findElementOrNull('"
				+ locator
				+ "');var evObj = x.document.createEventObject();"
				+ "evObj.keyCode="
				+ keycode
				+ "; evObj.repeat = false; x.focus(); x.fireEvent(\"on"
				+ event
				+ "\",evObj);}"
				+ "else{if(window.KeyEvent){var evObj = document.createEvent('KeyEvents');"
				+ "evObj.initKeyEvent( '"
				+ event
				+ "', true, true, window, false, false, false, false,"
				+ keycode
				+ ", 0 );} "
				+ "else {var evObj = document.createEvent('HTMLEvents');"
				+ "evObj.initEvent( '"
				+ event
				+ "', true, true, window, 1 ); evObj.keyCode="
				+ keycode
				+ ";} var x = selenium.browserbot.findElementOrNull('"
				+ locator + "'); x.blur(); x.focus(); x.dispatchEvent(evObj);}");
	}

	public void fireEvent(String locator, String eventName)
			throws HarnessException {
		logger.info("firing Event: " + eventName + " on " + locator);
		// ClientSessionFactory.session().selenium().fireEvent(locator,eventName);
		ClientSessionFactory
				.session()
				.selenium()
				.getEval(
						"selenium.browserbot.triggerMouseEvent(selenium.browserbot.findElement('"
								+ locator + "'),'" + eventName
								+ "', null, 0, 0, 0)");
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
				+ " div:contains(" + itemName + ")";

		return sIsElementPresent(itemLocator);
	}

	public boolean waitForPresentInListView(String itemName)
			throws HarnessException {
		String itemLocator = Locators.briefcaseListView.locator
				+ " div:contains(" + itemName + ")";

		zWaitForElementPresent(itemLocator);
		return true;
	}

	public boolean isOptionDisabled(Locators name) throws HarnessException {
		return sIsElementPresent(name.locator + "[class*=ZDisabled]");
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

	public boolean waitForDeletedFromListView(String itemName)
			throws HarnessException {
		String itemLocator = Locators.briefcaseListView.locator
				+ " div:contains(" + itemName + ")";

		zWaitForElementDeleted(itemLocator);
		return true;
	}

	public String getItemNameFromListView(String itemName)
			throws HarnessException {
		String itemLocator = Locators.briefcaseListView.locator
				+ " div:contains(" + itemName + ")";

		return sGetText(itemLocator);
	}

	public boolean isEditDocLoaded(DocumentItem docItem)
			throws HarnessException {
		zWaitForWindow(docItem.getName());

		zSelectWindow(docItem.getName());

		zWaitForElementPresent("css=div[class='ZDToolBar ZWidget']");

		zWaitForElementPresent("css=iframe[id*='DWT'][class='ZDEditor']");

		zWaitForIframeText("css=iframe[id*='DWT'][class='ZDEditor']",
				docItem.getDocText());

		return true;
	}

	public void deleteFileByName(String docName) throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();
		account.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
				+ "<query>" + docName + "</query>" + "</SearchRequest>");
		String id = account.soapSelectValue("//mail:doc", "id");
		deleteFileById(id);
	}

	public void deleteFileById(String docId) throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + docId + "' op='trash'/>"
				+ "</ItemActionRequest>");
	}

	public EnumMap<Response.ResponsePart, String> displayFile(String filename,
			Map<String, String> params) throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();

		RestUtil util = new RestUtil();

		util.setAuthentication(account);

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

	public String openUrl(String url) throws HarnessException {

		this.sOpen(url);

		return url;
	}

	public String getLocation() {
		return ClientSessionFactory.session().selenium().getLocation();
	}

	public String openUrl(String path, Map<String, String> params)
			throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();

		RestUtil util = new RestUtil();

		util.setAuthentication(account);

		if (null != path && !path.isEmpty())
			util.setPath("/" + path + "/");
		else
			util.setPath("/");

		if (null != params && !params.isEmpty()) {
			for (Map.Entry<String, String> query : params.entrySet()) {
				util.setQueryParameter(query.getKey(), query.getValue());
			}
		}

		if (util.doGet() != HttpStatus.SC_OK)
			throw new HarnessException("Unable to open " + util.getLastURI());

		String url = util.getLastURI().toString();

		if (url.endsWith("?"))
			url = url.substring(0, url.length() - 1);

		this.sOpen(url);

		return url;
	}

	public void closeWindow() throws HarnessException {
		tracer.trace("Close the separate window");

		this.sClose();
	}

	@Override
	public void zSelectWindow(String windowID) throws HarnessException {
		logger.info("zSelectWindow(" + windowID + ")");

		boolean found = false;

		String[] windowNames = ClientSessionFactory.session().selenium()
				.getAllWindowNames();

		for (int i = 0; i < windowNames.length; i++) {
			if (windowNames[i].contains(windowID.split("\\.")[0])) {
				this.sSelectWindow(windowNames[i]);
				found = true;
				break;
			}
		}

		if (!found) {
			String[] windowTitles = ClientSessionFactory.session().selenium()
					.getAllWindowTitles();
			for (int i = 0; i < windowTitles.length; i++) {
				if (windowTitles[i].contains(windowID.split("\\.")[0])) {
					this.sSelectWindow(windowTitles[i]);
					found = true;
					break;
				}
			}
		}

		if (found) {
			this.sWindowFocus();
			this.sWindowMaximize();
		}
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

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		throw new HarnessException("implement me! : pulldown=" + pulldown
				+ " option=" + option);
	}
}
