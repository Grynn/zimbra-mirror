/**
 * 
 */
package com.zimbra.qa.selenium.projects.desktop.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.ContextMenuItem.CONTEXT_MENU_ITEM_NAME;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.framework.util.staf.Stafpostqueue;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.desktop.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogMove;
import com.zimbra.qa.selenium.projects.desktop.ui.DialogTag;
import com.zimbra.qa.selenium.projects.desktop.ui.PageMain;

/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsTab {


	public static class Locators {

		public static final String zNewFolderOverviewPaneIcon = "id=ztih__main_Mail__FOLDER_textCell";

		// folders
		public static final String zInboxFldr		= "id=zti__main_Mail__2_textCell";
		public static final String zSentFldr		= "id=zti__main_Mail__5_textCell";
		public static final String zDraftsFldr 		= "id=zti__main_Mail__6_textCell";
		public static final String zJunkFldr 		= "id=zti__main_Mail__4_textCell";
		public static final String zTrashFldr 		= "id=zti__main_Mail__3_textCell";

		public static final String zInboxFldrMoveDlg 	= "id=zti__ZmChooseFolderDialog_Mail__2_textCell";
		public static final String zSentFldrMoveDlg 	= "id=zti__ZmChooseFolderDialog_Mail__5_textCell";
		public static final String zJunkFldrMoveDlg 	= "id=zti__ZmChooseFolderDialog_Mail__4_textCell";
		public static final String zTrashFldrMoveDlg 	= "id=zti__ZmChooseFolderDialog_Mail__3_textCell";

		public static final String zFoldersNewFolderDlg = "id=ztih__ZmNewFolderDialog__FOLDER_textCell";

		public static final String zNewIconBtn 			= "id=zb__TV-main__NEW_MENU_left_icon";
		public static final String zNewMenuIconBtn 		= "id=zb__CLV__NEW_MENU_left_icon";
		public static final String zNewMenuBtn 			= "id=zb__CLV__NEW_MENU";
		public static final String zNewMenuDropDown 	= "id=zb__CLV__NEW_MENU_dropdown";
		public static final String zGetMailIconBtnCLVID 	= "zb__CLV__CHECK_MAIL_left_icon";
		public static final String zGetMailIconBtnTVID 		= "zb__TV-main__CHECK_MAIL_left_icon";
		public static final String zGetMailIconBtn 		= "id=zb__CLV__CHECK_MAIL_left_icon";
		public static final String zGetMailBtn 			= "id=zb__CLV__CHECK_MAIL";
		public static final String zDeleteIconBtnID 	= "zb__CLV__DELETE_left_icon";
		public static final String zDeleteBtn 			= "id=zb__CLV__DELETE";
		public static final String zMoveIconBtnID 		= "zb__CLV__MOVE_left_icon";
		public static final String zMoveBtn 			= "id=zb__CLV__MOVE";
		public static final String zPrintIconBtnID 		= "zb__CLV__PRINT_left_icon";
		public static final String zPrintBtn 			= "id=zb__CLV__PRINT";
		public static final String zReplyIconBtnID 		= "zb__CLV__REPLY_left_icon";
		public static final String zReplyBtn 			= "id=zb__CLV__REPLY";
		public static final String zReplyAllIconBtnID 	= "zb__CLV__REPLY_ALL_left_icon";
		public static final String zReplyAllBtn 		= "id=zb__CLV__REPLY_ALL";
		public static final String zForwardIconBtnID 	= "zb__CLV__FORWARD_left_icon";
		public static final String zForwardBtn 			= "id=zb__CLV__FORWARD";
		public static final String zJunkIconBtnID 		= "zb__CLV__SPAM_left_icon";
		public static final String zJunkBtn 			= "id=zb__CLV__SPAM";
		public static final String zTagIconBtn 			= "id=zb__CLV__TAG_MENU_left_icon";
		public static final String zTagBtn 				= "id=zb__CLV__TAG_MENU";
		public static final String zTagMenuDropdownBtnID	= "zb__CLV__TAG_MENU_dropdown";
		public static final String zDetachIconBtnID		= "zb__TV-main__DETACH_left_icon";
		public static final String zDetachBtn 			= "id=zb__TV-main__DETACH";
		public static final String zDetachIconBtn2 		= "id=zb__CLV__DETACH_left_icon";
		public static final String zDetachBtn2 			= "id=zb__CLV__DETACH";
		public static final String zDetachBtn_ComposedMessage = "id=zb__COMPOSE1__DETACH_COMPOSE";
		public static final String zViewIconBtnID 		= "zb__CLV__VIEW_MENU_left_icon";
		public static final String zViewBtn 			= "id=zb__CLV__VIEW_MENU";
		public static final String zViewMenuDropdownBtnID	= "zb__CLV__VIEW_MENU_dropdown";

		public static final String zViewMenuCLVBtnID	= zViewIconBtnID;
		public static final String zViewMenuTVBtnID		= "zb__TV-main__VIEW_MENU_left_icon";


		public static final String zCloseIconBtn_newWindow 		= "id=zb__MSG1__CLOSE_left_icon";
		public static final String zDeleteIconBtn_newWindow 	= "id=zb__MSG1__DELETE_title";
		public static final String zReplyIconBtn_newWindow 		= "id=zb__MSG1__REPLY_left_icon";
		public static final String zReplyAllIconBtn_newWindow 	= "id=zb__MSG1__REPLY_ALL_left_icon";
		public static final String zForwardIconBtn_newWindow 	= "id=zb__MSG1__FORWARD_left_icon";
		public static final String zJunkIconBtn_newWindow 		= "id=zb__MSG1__SPAM_left_icon";
		public static final String zTagIconBtn_newWindow 		= "id=zb__MSG1__TAG_MENU_left_icon";

		public static final String zSendBtn_newWindow 			= "id=zb__COMPOSE1__SEND";
		public static final String zCancelBtn_newWindow 		= "id=zb__COMPOSE1__CANCEL";
		public static final String zSaveDraftsBtn_newWindow 	= "id=zb__COMPOSE1__SAVE_DRAFT";
		public static final String zAddAttachmentBtn_newWindow 	= "id=zb__COMPOSE1__ATTACHMENT";
		public static final String zSpellCheckBtn_newWindow 	= "id=zb__COMPOSE1__SPELL_CHECK";
		public static final String zSignatureBtn_newWindow 		= "id=zb__COMPOSE1__ADD_SIGNATURE";
		public static final String zOptionsBtn_newWindow 		= "id=zb__COMPOSE1__COMPOSE_OPTIONS";

		public static final String zEditDraftIconBtn 	= "id=zb__CLV__EDIT_left_icon";
		public static final String zEditDraftBtn 		= "id=zb__CLV__EDIT";
		public static final String zMailTabIconBtn 		= "id=zb__App__Mail_left_icon";
		public static final String zMailViewIconBtn 	= "id=zb__CLV__VIEW_MENU_left_icon";
		public static final String zCancelIconBtn 		= "id=zb__COMPOSE1__CANCEL_left_icon";
		public static final String zTagOverViewHeader 	= "id=ztih__main_Mail__TAG_div";
		public static final String zSearchIconBtn 		= "id=zb__Search__SEARCH_left_icon";

		public static final String zSearchMenuIconBtn 			= "id=zmi__CLV__Par__SEARCH_left_icon";
		public static final String zAdvancedSearchMenuIconBtn 	= "id=zmi__CLV__Par__BROWSE_left_icon";
		public static final String zNewEmailMenuIconBtn 		= "id=zmi__CLV__Par__NEW_MESSAGE_left_icon";
		public static final String zAddToContactsMenuIconBtn 	= "id=zmi__CLV__Par__CONTACT_left_icon";
		public static final String zMarkReadMenuIconBtn 		= "id=zmi__CLV__MARK_READ_left_icon";
		public static final String zMarkReadMenuEnaDisaBtn 		= "id=zmi__CLV__MARK_READ";
		public static final String zMarkUnReadMenuIconBtn 		= "id=zmi__CLV__MARK_UNREAD_left_icon";
		public static final String zMarkUnReadMenuEnaDisaBtn 	= "id=zmi__CLV__MARK_UNREAD";
		public static final String zReplyMenuIconBtn 			= "id=zmi__CLV__REPLY_left_icon";
		public static final String zReplyMenuEnaDisaBtn 		= "id=zmi__CLV__REPLY";
		public static final String zReplyAllMenuIconBtn 		= "id=zmi__CLV__REPLY_ALL_left_icon";
		public static final String zReplyAllMenuEnaDisaBtn 		= "id=zmi__CLV__REPLY_ALL";
		public static final String zForwardMenuIconBtn 			= "id=zmi__CLV__FORWARD_left_icon";
		public static final String zForwardMenuEnaDisaBtn 		= "id=zmi__CLV__FORWARD";
		public static final String zEditAsNewMenuIconBtn 		= "id=zmi__CLV__EDIT_left_icon";
		public static final String zEditAsNewMenuEnaDisaBtn 	= "id=zmi__CLV__EDIT";
		public static final String zTagMenuIconBtn 				= "id=zmi__CLV__TAG_MENU_left_icon";
		public static final String zNewTagMenuIconBtn 			= "id=zmi__CLV__TAG_MENU|MENU|NEWTAG_title";
		public static final String zRemoveTagMenuIconBtn 		= "id=zmi__CLV__TAG_MENU|MENU|REMOVETAG_title";
		public static final String zDeleteMenuIconBtn 			= "id=zmi__CLV__DELETE_left_icon";
		public static final String zMoveMenuIconBtn 			= "id=zmi__CLV__MOVE_left_icon";
		public static final String zPrintMenuIconBtn 			= "id=zmi__CLV__PRINT_left_icon";
		public static final String zPrintMenuEnaDisaBtn 		= "id=zmi__CLV__PRINT";
		public static final String zJunkMenuIconBtn 			= "id=zmi__CLV__SPAM_left_icon";
		public static final String zShowOriginalMenuIconBtn 	= "id=zmi__CLV__SHOW_ORIG_left_icon";
		public static final String zShowOriginalMenuEnaDisaBtn 	= "id=zmi__CLV__SHOW_ORIG";
		public static final String zNewFilterMenuIconBtn 		= "id=zmi__CLV__ADD_FILTER_RULE_left_icon";
		public static final String zNewFilterMenuEnaDisaBtn 	= "id=zmi__CLV__ADD_FILTER_RULE";
		public static final String zCreateApptMenuIconBtn 		= "id=zmi__CLV__CREATE_APPT_left_icon";
		public static final String zCreateApptEnaDisaBtn 		= "id=zmi__CLV__CREATE_APPT";
		public static final String zCreateTaskMenuEnaDisaBtn 	= "id=zmi__CLV__CREATE_TASK_left_icon";
		public static final String zCreateTaskEnaDisaBtn 		= "id=zmi__CLV__CREATE_TASK";

		public static final String zGeneralPrefFolder 		= "id=zti__main_Options__PREF_PAGE_GENERAL_textCell";
		public static final String zMailPrefFolder 			= "id=zti__main_Options__PREF_PAGE_MAIL_textCell";
		public static final String zComposingPrefFolder 	= "id=zti__main_Options__PREF_PAGE_COMPOSING_textCell";
		public static final String zSignaturesPrefFolder 	= "id=zti__main_Options__PREF_PAGE_SIGNATURES_textCell";
		public static final String zAccountsPrefFolder 		= "id=zti__main_Options__PREF_PAGE_ACCOUNTS_textCell";
		public static final String zFiltersPrefFolder 		= "id=zti__main_Options__PREF_PAGE_FILTERS_textCell";

		public static final String zAddressBookPrefFolder 	= "id=zti__main_Options__PREF_PAGE_CONTACTS_textCell";
		public static final String zCalendarPrefFolder 		= "id=zti__main_Options__PREF_PAGE_CALENDAR_textCell";
		public static final String zSharingPrefFolder 		= "id=zti__main_Options__PREF_PAGE_SHARING_textCell";
		public static final String zImportExportPrefFolder 	= "id=zti__main_Options__PREF_PAGE_IMPORT_EXPORT_textCell";
		public static final String zShortcutsPrefFolder 	= "id=zti__main_Options__PREF_PAGE_SHORTCUTS_textCell";
		public static final String zZimletsPrefFolder 		= "id=zti__main_Options__PREF_PAGE_PREF_ZIMLETS_textCell";
		public static final String zShowOriginalDraftMenuIconBtn = "id=zmi__CLV__Dra__SHOW_ORIG_left_icon";

		public static final String zPreferencesTabIconBtn = "id=zb__App__Options_left_icon";

		public static final String zRssFolderName = "id=zb__App__Options_left_icon";

		//	public static final String zPreferencesMailIconBtn = "id=ztab__PREF__"
		//			+ localize(locator.mail) + "_title";


		public static final String zCLVRows			= "zl__CLV__rows";
		public static final String zTVRows			= "zl__TV-main__rows";

		public static class CONTEXT_MENU {
			// TODO: Until https://bugzilla.zimbra.com/show_bug.cgi?id=56273 is fixed, ContextMenuItem will be defined using the text content
			public static String stringToReplace = "<ITEM_NAME>";
			public static final String zDesktopContextMenuItems = new StringBuffer("css=table[class$='MenuTable'] td[id$='_title']:contains(")
			.append(stringToReplace).append(")").toString();

			// Folder's context menu
			public static final ContextMenuItem NEW_FOLDER = new ContextMenuItem(
					zDesktopContextMenuItems.replace(stringToReplace, I18N.CONTEXT_MENU_ITEM_NEW_FOLDER),
					I18N.CONTEXT_MENU_ITEM_NEW_FOLDER,
					"div[class='ImgNewFolder']",
			":contains('nf')"); 

		}
	}


   public String zGetFolderLocator(String folderName) throws HarnessException {
      return "css=div[id$='main_Mail-parent-FOLDER'] div[class='DwtTreeItemLevel1ChildDiv'] div td:contains('" + folderName + "')";
   }


	public PageMail(AbsApplication application) {
		super(application);

		logger.info("new " + PageMail.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}

		// If the "folders" tree is visible, then mail is active
		String locator = "css=div[id$='__CHECK_MAIL']";

		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (false);

		boolean active = this.zIsVisiblePerPosition(locator, 4, 74);
		return (active);

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#navigateTo()
	 */
	@Override
	public void zNavigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( zIsActive() ) {
			return;
		}

		// Make sure we are logged into the Mobile app
		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}

		tracer.trace("Navigate to "+ this.myPageName());

		this.zClick(PageMain.Locators.zAppbarMail);

		this.zWaitForBusyOverlay();

		zWaitForActive();

	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if ( button == Button.B_NEW ) {

			// New button
			locator = "css=div[id^='ztb__'] td[id$='__NEW_MENU_title']";

			// Create the page
			page = new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_GETMAIL || button == Button.B_LOADFEED ) {

			if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
				locator = "id="+ Locators.zGetMailIconBtnTVID;
			} else {
				locator = "id="+ Locators.zGetMailIconBtnCLVID;
			}

		} else if ( button == Button.B_DELETE ) {

			String id;
			if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
				id = "zb__TV-main__DELETE_left_icon";
			} else {
				id = "zb__CLV__DELETE_left_icon";
			}

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ id +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id="+ id;


		} else if ( button == Button.B_MOVE ) {

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[contains(@id, '__MOVE_left_icon')]/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "css=td[id$='__MOVE_left_icon']";

			page = new DialogMove(MyApplication, this);

			// FALL THROUGH

		} else if ( button == Button.B_PRINT ) {

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zPrintIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zPrintIconBtnID;
			page = null;	// TODO
			throw new HarnessException("implement Print dialog");

		} else if ( button == Button.B_REPLY ) {

			page = new FormMailNew(this.MyApplication);;
			locator = "css=div[id$='__REPLY']";

			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Reply icon not present "+ button);
			}

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//div[contains(@id,'__REPLY')])@class");
			if ( attrs.contains("ZDisabled") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

		} else if ( button == Button.B_REPLYALL ) {

			page = new FormMailNew(this.MyApplication);;
			locator = "css=div[id$='__REPLY_ALL']";

			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Reply All icon not present "+ button);
			}

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//div[contains(@id,'__REPLY_ALL')])@class");
			if ( attrs.contains("ZDisabled") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

		} else if ( button == Button.B_FORWARD ) {

			page = new FormMailNew(this.MyApplication);;
			locator = "css=div[id$='__FORWARD']";

			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Forward icon not present "+ button);
			}

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//div[contains(@id,'__FORWARD')])@class");
			if ( attrs.contains("ZDisabled") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

		} else if ( (button == Button.B_RESPORTSPAM) || (button == Button.B_RESPORTNOTSPAM) ) {

			page = null;
			locator = "css=div[id$='__SPAM']";
			if ( !this.sIsElementPresent(locator) ) {
				throw new HarnessException("Spam icon not present "+ button);
			}

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//div[contains(@id,'__SPAM')])@class");
			if ( attrs.contains("ZDisabled") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

		} else if ( button == Button.B_TAG ) {

			// For "TAG" without a specified pulldown option, just click on the pulldown
			// To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zTagMenuDropdownBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zTagMenuDropdownBtnID +"'";

		} else if ( button == Button.B_NEWWINDOW ) {

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zDetachIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zDetachIconBtnID;
			page = null;	// TODO
			throw new HarnessException("implement new window page ... probably just DisplayMail object?");


		} else if ( button == Button.B_LISTVIEW ) {

			// For "TAG" without a specified pulldown option, just click on the pulldown
			// To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zViewMenuDropdownBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zViewMenuDropdownBtnID +"'";

		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClick(locator);

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP &&
				button == Button.B_GETMAIL) {

			Stafpostqueue sp = new Stafpostqueue();
			sp.waitForPostqueue();

			// Wait for the spinner image
			zWaitForDesktopLoadingSpinner(5000);
		}

		// If page was specified, make sure it is active
		if ( page != null ) {

			// This function (default) throws an exception if never active
			page.zWaitForActive();

		}


		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");
		tracer.trace("Click pulldown "+ pulldown +" then "+ option);
		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");
		// Default behavior variables

		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_TAG) {
			if (option == Button.O_TAG_NEWTAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id$='__TAG_MENU|MENU|NEWTAG_title']";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else if (option == Button.O_TAG_REMOVETAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=td[id$='__TAG_MENU|MENU|REMOVETAG_title']";

				page = null;

				// FALL THROUGH
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
								+ "/" + option);
			}
		} else if ((pulldown == Button.B_NEW) && (option == Button.O_NEW_TAG)) {

			pulldownLocator = "css=td[id$='__NEW_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
			optionLocator = "//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgNewTag')]";

			page = new DialogTag(this.MyApplication, this);
		} else if ((pulldown == Button.B_NEW)&& (option == Button.O_NEW_FOLDER)) {

			pulldownLocator = "css=td[id$='__NEW_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
			optionLocator = "//td[contains(@id,'_left_icon')]/div[contains(@class,'ImgNewFolder')]";

			page = new DialogCreateFolder(this.MyApplication, this);

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

   public AbsPage zToolbarPressPulldown(Button pulldown, Button option,String dynamic) throws HarnessException {
      //logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");
      tracer.trace("Click pulldown "+ pulldown +" then "+ option);
      if (pulldown == null)
         throw new HarnessException("Pulldown cannot be null!");

      if (option == null)
         throw new HarnessException("Option cannot be null!");
      if (dynamic == null)
         throw new HarnessException("dynamic string cannot be null!");
      // Default behavior variables

      String pulldownLocator = null; // If set, this will be expanded
      String optionLocator = null; // If set, this will be clicked
      AbsPage page = null; // If set, this page will be returned

      if ((pulldown == Button.B_SIGNATURE)&& (option == Button.O_ADD_SIGNATURE)) {
         String name = (String)dynamic;
         logger.info(name);
         pulldownLocator = "css=td[id$='_ADD_SIGNATURE_dropdown']>div[class='ImgSelectPullDownArrow']";
         dynamic ="css=td[id*='_title']td:contains('"+ name + "')";
         page = null;

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
         if (dynamic != null) {

            GeneralUtility.waitForElementPresent(this, dynamic);
            // Make sure the locator exists
            if (!this.sIsElementPresent(dynamic)) {
               throw new HarnessException("Button " + pulldown
                     + " option " + option + " optionLocator "
                     + dynamic + " not present!");
            }

            this.zClick(dynamic);

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


	public enum PageMailView {
		BY_MESSAGE, BY_CONVERSATION
	}

	/**
	 * Get the Page Property: ListView = By message OR By Conversation
	 * @return
	 * @throws HarnessException
	 */
	public PageMailView zGetPropMailView() throws HarnessException {
		if ( sIsElementPresent( "id="+ Locators.zViewMenuTVBtnID ) &&
		      zIsVisiblePerPosition("id="+ Locators.zViewMenuTVBtnID, 0, 0)) {
			return (PageMailView.BY_MESSAGE);
		} else if ( sIsElementPresent( "id="+ Locators.zViewMenuCLVBtnID ) &&
		      zIsVisiblePerPosition("id="+ Locators.zViewMenuCLVBtnID, 0, 0)) {
			return (PageMailView.BY_CONVERSATION);
		}

		throw new HarnessException("Unable to determine the Page Mail View");
	}

	/**
	 * Return a list of all messages in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<MailItem> zListGetMessages() throws HarnessException {

		List<MailItem> items = new ArrayList<MailItem>();

		String listLocator = null;
		String rowLocator = null;
		if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
         listLocator = "//div[@id='zl__TV-main__rows']";
         rowLocator = "//div[contains(@id, 'zli__TV-main__')]";
      } else {
         listLocator = "//div[@id='zl__CLV__rows']";
         rowLocator = "//div[contains(@id, 'zli__CLV__')]";
      }

		// Make sure the button exists
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("Message List View Rows is not present: "+ listLocator);

		String tableLocator = listLocator + rowLocator;
		// How many items are in the table?
		int count = this.sGetXpathCount(tableLocator);
		logger.debug(myPageName() + " zListGetMessages: number of messages: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			final String msglocator = listLocator + "/div["+ i +"]";
			String locator;

			MailItem item = new MailItem();

			// Is it checked?
			locator = msglocator + "//div[contains(@class, 'ImgCheckboxChecked')]";
			item.gIsSelected = this.sIsElementPresent(locator);

			// Is it flagged
			// TODO: probably can't have boolean, need 'blank', 'disabled', 'red', and other states
			locator = msglocator + "//div[contains(@class, 'ImgFlagRed')]";
			item.gIsFlagged = this.sIsElementPresent(locator);

			locator = "xpath=("+ msglocator +"//div[contains(@id, '__pr')])@class";
			String priority = this.sGetAttribute(locator);
			if ( priority.equals("ImgPriorityHigh_list") ) {
				item.gPriority = "high";
			} else {
				// TODO - handle other priorities
			}


			locator = msglocator + "//div[contains(@id, '__tg')]";
			// TODO: handle tags

			// Get the From
			locator = msglocator + "//*[contains(@id, '__fr')]";
			item.gFrom = this.sGetText(locator).trim();

			// Get the attachment
			locator = "xpath=("+ msglocator +"//div[contains(@id, '__at')])@class";
			String attach = this.sGetAttribute(locator);
			if ( attach.equals("ImgBlank_16") ) {
				item.gHasAttachments = false;
			} else {
				// TODO - handle other attachment types
			}

			// Get the fragment
			locator = msglocator + "//span[contains(@id, '__fm')]";
			item.gFragment = this.sGetText(locator).trim();

			// Get the subject
			locator = msglocator + "//td[contains(@id, '__su')]";
			String subject = this.sGetText(locator).trim();

			// The subject contains the fragment, e.g. "subject - fragment", so
			// strip it off
			item.gSubject = subject.replace(item.gFragment, "").trim();

			// Get the folder
			locator = msglocator + "//nobr[contains(@id, '__fo')]";
			if ( this.sIsElementPresent(locator) ) {
				item.gFolder = this.sGetText(locator).trim();
			} else {
				item.gFolder = "";
			}

			// Get the size
			locator = msglocator + "//nobr[contains(@id, '__sz')]";
			if ( this.sIsElementPresent(locator) ) {
				item.gSize = this.sGetText(locator).trim();
			} else {
				item.gSize = "";
			}

			// Get the received date
			locator = msglocator + "//td[contains(@id, '__dt')]";
			item.gReceived = this.sGetText(locator).trim();

			// Add the new item to the list
			items.add(item);
			logger.info(item.prettyPrint());
		}

		// Return the list of items
		return (items);
	}

	/**
	 * Return a list of all conversations in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<ConversationItem> zListGetConversations() throws HarnessException {
		logger.info(myPageName() + " getConversationList");

		List<ConversationItem> items = new ArrayList<ConversationItem>();

		// Make sure the button exists
		if ( !this.sIsElementPresent(Locators.zCLVRows) )
			throw new HarnessException("Conversation List View Rows is not present "+ Locators.zCLVRows);

		// How many items are in the table?
		int count = this.sGetXpathCount("//div[@id='zl__CLV__rows']//div[contains(@id, 'zli__CLV__')]");
		logger.debug(myPageName() + " zListGetConversations: number of conversations: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			final String convlocator = "//div[@id='zl__CLV__rows']/div["+ i +"]";
			String locator;

			ConversationItem item = new ConversationItem();

			// Is it checked?
			locator = convlocator + "//div[contains(@class, 'ImgCheckboxChecked')]";
			item.gIsSelected = this.sIsElementPresent(locator);

			// Is it expanded?
			locator = convlocator + "//div[contains(@class, 'ImgNodeExpanded')]";
			// item.gIsExpanded = this.sIsElementPresent(locator);

			// Is it flagged
			// TODO: probably can't have boolean, need 'blank', 'disabled', 'red', and other states
			locator = convlocator + "//div[contains(@class, 'ImgFlagRed')]";
			item.gIsFlagged = this.sIsElementPresent(locator);

			// What's the priority?
			locator = convlocator +"//div[contains(@id, '__pr')]";
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("Unable to locator priority field");
			String priority = this.sGetAttribute("xpath="+locator+"@class");
			if ( priority.equals("ImgPriorityHigh_list") ) {
				item.gPriority = "high";
			} else {
				// TODO - handle other priorities
			}


			locator = convlocator + "//div[contains(@id, '__tg')]";
			// TODO: handle tags

			// Get the From
			locator = convlocator + "//td[contains(@id, '__fr')]";
			item.gFrom = this.sGetText(locator).trim();

			// Get the attachment
			locator = "xpath=("+ convlocator +"//div[contains(@id, '__at')])@class";
			String attach = this.sGetAttribute(locator);
			if ( attach.equals("ImgBlank_16") ) {
				item.gHasAttachments = false;
			} else {
				// TODO - handle other attachment types
			}

			// Get the fragment
			locator = convlocator + "//span[contains(@id, '__fm')]";
			item.gFragment = this.sGetText(locator).trim();

			// Get the subject
			locator = convlocator + "//td[contains(@id, '__su')]";
			String s = this.sGetText(locator).trim();

			// The subject contains the fragment, e.g. "subject - fragment", so
			// strip it off
			item.gSubject = s.replace(item.gFragment, "").trim();

			// Get the folder
			locator = convlocator + "//nobr[contains(@id, '__fo')]";
			if ( this.sIsElementPresent(locator) ) {
				item.gFolder = this.sGetText(locator).trim();
			} else {
				item.gFolder = "";
			}

			// Get the size
			locator = convlocator + "//nobr[contains(@id, '__sz')]";
			if ( this.sIsElementPresent(locator) ) {
				item.gSize = this.sGetText(locator).trim();
			} else {
				item.gSize = "";
			}

			// Get the received date
			locator = convlocator + "//td[contains(@id, '__dt')]";
			item.gReceived = this.sGetText(locator).trim();

			// Add the new item to the list
			items.add(item);
			logger.info(item.prettyPrint());
		}

		// Return the list of items
		return (items);
	}

	/**
	 * This method is meant for synching and waiting for new email especially for
	 * non-Zimbra account since there is no control/indicator of when the new email will
	 * arrive
	 * @param subject Subject of email to be searched for
	 * @throws HarnessException
	 */
	public void zSyncAndWaitForNewEmail(String subject) throws HarnessException {

	   String listLocator;
	   String rowLocator;
	   String itemlocator = null;
	   
	   
	   // Find the item locator
	   //
	   
	   if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
	      listLocator = "//div[@id='zl__TV-main__rows']";
	      rowLocator = "//div[contains(@id, 'zli__TV-main__')]";
	   } else {
	      listLocator = "//div[@id='zl__CLV__rows']";
	      rowLocator = "//div[contains(@id, 'zli__CLV__')]";
	   }
	   
	   if ( !this.sIsElementPresent(listLocator) )
	      throw new HarnessException("List View Rows is not present "+ listLocator);
	   
	   // How many items are in the table?
	   int count = this.sGetXpathCount(listLocator + rowLocator);

	   int retry = 0;
	   int maxRetry = 30;
	   boolean found = false;
	   for (retry = 0; retry < maxRetry; retry++) {
	      GeneralUtility.syncDesktopToZcsWithSoap(((AppAjaxClient)MyApplication).zGetActiveAccount());
	      zWaitForDesktopLoadingSpinner(5000);

	      logger.debug(myPageName() + " zListSelectItem: number of list items: "+ count);

	      // Get each conversation's data from the table list
	      for (int i = 1; i <= count; i++) {

	         itemlocator = listLocator + "/div["+ i +"]";
	         String subjectlocator;

	         // Look for the subject

	         // Subject - Fragment
	         subjectlocator = itemlocator + "//td[contains(@id, '__su')]";
	         String s = this.sGetText(subjectlocator).trim();

	         if ( s.contains(subject) ) {
	            found = true;
	            break;
	         }

	         itemlocator = null;
	      }

	      if (found) {
	         break;
	      }

	      count = this.sGetXpathCount(listLocator + rowLocator);
	   }
	   
	}



	@Override
	public AbsPage zListItem(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");

		tracer.trace(action +" on subject = "+ subject);

		AbsPage page = null;
		String listLocator;
		String rowLocator;
		String itemlocator = null;


		// Find the item locator
		//

		if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
			listLocator = "//div[@id='zl__TV-main__rows']";
			rowLocator = "//div[contains(@id, 'zli__TV-main__')]";
		} else {
			listLocator = "//div[@id='zl__CLV__rows']";
			rowLocator = "//div[contains(@id, 'zli__CLV__')]";
		}

		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("List View Rows is not present "+ listLocator);

		// How many items are in the table?
		int count = this.sGetXpathCount(listLocator + rowLocator);
		logger.debug(myPageName() + " zListSelectItem: number of list items: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			itemlocator = listLocator + "/div["+ i +"]";
			String subjectlocator;

			// Look for the subject

			// Subject - Fragment
			subjectlocator = itemlocator + "//td[contains(@id, '__su')]";
			String s = this.sGetText(subjectlocator).trim();

			if ( s.contains(subject) ) {
				break; // found it
			}

			itemlocator = null;
		}

		if ( itemlocator == null ) {
			throw new HarnessException("Unable to locate item with subject("+ subject +")");
		}

		if ( action == Action.A_LEFTCLICK ) {

			// Left-Click on the item
			this.zClick(itemlocator);

			this.zWaitForBusyOverlay();

			// Return the displayed mail page object
			page = new DisplayMail(MyApplication);

			// FALL THROUGH

		} else if ( action == Action.A_CTRLSELECT ) {

			throw new HarnessException("implement me!  action = "+ action);

		} else if ( action == Action.A_SHIFTSELECT ) {

			throw new HarnessException("implement me!  action = "+ action);

		} else if ( action == Action.A_RIGHTCLICK ) {

			// Right-Click on the item
			this.zRightClick(itemlocator);

			// Return the displayed mail page object
			page = new ContextMenu(MyApplication);

			// FALL THROUGH

		} else if ( action == Action.A_MAIL_CHECKBOX ) {

			String selectlocator = itemlocator + "//div[contains(@id, '__se')]";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);

			String image = this.sGetAttribute("xpath="+ selectlocator +"@class");
			if ( image.equals("ImgCheckboxChecked") )
				throw new HarnessException("Trying to check box, but it was already enabled");

			// Left-Click on the flag field
			this.zClick(selectlocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else if ( action == Action.A_MAIL_UNCHECKBOX ) {

			String selectlocator = itemlocator + "//div[contains(@id, '__se')]";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);

			String image = this.sGetAttribute("xpath="+ selectlocator +"@class");
			if ( image.equals("ImgCheckboxUnchecked") )
				throw new HarnessException("Trying to uncheck box, but it was already disabled");

			// Left-Click on the flag field
			this.zClick(selectlocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else if ( action == Action.A_MAIL_EXPANDCONVERSATION ) {

			throw new HarnessException("implement me!  action = "+ action);

		} else if ( (action == Action.A_MAIL_FLAG) || (action == Action.A_MAIL_UNFLAG) ) {
			// Both FLAG and UNFLAG have the same action and result

			String flaglocator = itemlocator + "//div[contains(@id, '__fg')]";

			// Left-Click on the flag field
			this.zClick(flaglocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}


		if ( page != null ) {
			page.zWaitForActive();
		}

		// default return command
		return (page);

	}

	public AbsPage zListItem(Action action, Button option, FolderItem folderItem)
	throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ option +")");
		tracer.trace(action +" then "+ option +" on Folder Item = "+ folderItem);

		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("button cannot be null");
		if ( folderItem == null )
			throw new HarnessException("folderItem cannot be null");

		String treeItemLocator = ((AppAjaxClient)MyApplication).zTreeMail.zGetTreeFolderLocator(folderItem);
		boolean onRootFolder = false;

		if (folderItem.getName().equals("USER_ROOT")) {
		   onRootFolder = true;
		}

		AbsPage page = null;
		if (treeItemLocator == null) throw new HarnessException("treeItemLocator is null, please check!");

		GeneralUtility.waitForElementPresent(this, treeItemLocator);

		if ( action == Action.A_RIGHTCLICK ) {

			if (option == Button.B_TREE_NEWFOLDER) {
				ContextMenu contextMenu = (ContextMenu)((AppAjaxClient)MyApplication).zTreeMail.zTreeItem(
						action, treeItemLocator);
				page = contextMenu.zSelect(CONTEXT_MENU_ITEM_NAME.NEW_FOLDER);
			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}
		} else if (action == Action.A_LEFTCLICK) {
			if (option == Button.B_TREE_NEWFOLDER) {
				if (ZimbraSeleniumProperties.getAppType() == AppType.AJAX) {
					if (((AppAjaxClient)MyApplication).zTreeMail.isCollapsed()) {
						// Expand it
						((AppAjaxClient)MyApplication).zTreeMail.zClick(
								TreeMail.Locators.treeExpandCollapseButton);
						GeneralUtility.waitFor(null, ((AppAjaxClient)MyApplication).zTreeMail, false,
								"isCollapsed", null, WAIT_FOR_OPERAND.EQ, false, 30000, 1000);
					} else {
						if (onRootFolder) {
							// TODO: Bug 57414
							// Collapse the tree and expand it again to select the root folder
							((AppAjaxClient)MyApplication).zTreeMail.zClick(
									TreeMail.Locators.treeExpandCollapseButton);

							GeneralUtility.waitFor(null, ((AppAjaxClient)MyApplication).zTreeMail, false,
									"isCollapsed", null, WAIT_FOR_OPERAND.EQ, true, 30000, 1000);

							((AppAjaxClient)MyApplication).zTreeMail.zClick(
									TreeMail.Locators.treeExpandCollapseButton);

							page = ((AppAjaxClient)MyApplication).zTreeMail.zPressButton(option);
						}  else {
							// Fall Through
						}
					}

				} else {
					// Not available for Desktop
					throw new HarnessException("Not Supported! Action:" + action + " Option:" + option);
				}

			} else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}
		} else {
			throw new HarnessException("implement action:"+ action +" option:"+ option);
		}

		return page;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption ,String item)
	throws HarnessException {
		tracer.trace(action +" then "+ option + "," + subOption + " on item = "+ item);

		throw new HarnessException("implement me!");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ option +", "+ subject +")");

		tracer.trace(action +" then "+ option +" on subject = "+ subject);


		if ( action == null )
			throw new HarnessException("action cannot be null");
		if ( option == null )
			throw new HarnessException("button cannot be null");
		if ( subject == null || subject.trim().length() == 0)
			throw new HarnessException("subject cannot be null or blank");

		AbsPage page = null;
		String listLocator;
		String rowLocator;
		String itemlocator = null;


		// Find the item locator
		//

		if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
			listLocator = "//div[@id='zl__TV-main__rows']";
			rowLocator = "//div[contains(@id, 'zli__TV-main__')]";
		} else {
			listLocator = "//div[@id='zl__CLV__rows']";
			rowLocator = "//div[contains(@id, 'zli__CLV__')]";
		}

		// TODO: how to handle both messages and conversations, maybe check the view first?
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("List View Rows is not present "+ listLocator);

		// How many items are in the table?
		int count = this.sGetXpathCount(listLocator + rowLocator);
		logger.debug(myPageName() + " zListSelectItem: number of list items: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			itemlocator = listLocator + "/div["+ i +"]";
			String subjectlocator;

			// Look for the subject

			// Subject - Fragment
			subjectlocator = itemlocator + "//td[contains(@id, '__su')]";
			String s = this.sGetText(subjectlocator).trim();

			if ( s.contains(subject) ) {
				break; // found it
			}

			itemlocator = null;
		}

		if ( itemlocator == null ) {
			throw new HarnessException("Unable to locate item with subject("+ subject +")");
		}

		if ( action == Action.A_RIGHTCLICK ) {

			// Right-Click on the item
			this.zRightClick(itemlocator);

			// Now the ContextMenu is opened
			// Click on the specified option

			String optionLocator = null;

			if (option == Button.B_DELETE) {

				// <div id="zmi__TV_DELETE" ... By Message
				// <div id="zmi__CLV__Par__DELETE" ... By Conversation

				if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
					optionLocator = "zmi__TV__DELETE";
				} else {
					optionLocator = "zmi__CLV__Par__DELETE";
				}

				page = null;

			} else if (option == Button.B_TREE_NEWFOLDER) {

				String treeItemLocator = null;
				if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
					treeItemLocator = TreeMail.Locators.zTreeItems.replace(TreeMail.stringToReplace,
							AjaxCommonTest.defaultAccountName);
				} else {
					treeItemLocator = TreeMail.Locators.ztih_main_Mail__FOLDER_ITEM_ID.replace(TreeMail.stringToReplace, "FOLDER");
				}

				GeneralUtility.waitForElementPresent(this, treeItemLocator);
				ContextMenu contextMenu = (ContextMenu)((AppAjaxClient)MyApplication).zTreeMail.zTreeItem(Action.A_RIGHTCLICK, treeItemLocator);
				page = contextMenu.zSelect(CONTEXT_MENU_ITEM_NAME.NEW_FOLDER);

			} else if ( option == Button.O_MARK_AS_READ ) {

				if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
					optionLocator = "zmi__TV__MARK_READ_title";
				} else {
					optionLocator = "zmi__CLV__MARK_READ_title";
				}

				page = null;

				// FALLTHROUGH

			} else if ( option == Button.O_MARK_AS_UNREAD ) {

				if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
					optionLocator = "zmi__TV__MARK_UNREAD_title";
				} else {
					optionLocator = "zmi__CLV__MARK_UNREAD_title";
				}

				page = null;


				// FALLTHROUGH

			}
			else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

			// click on the option
			this.zClick(optionLocator);

			this.zWaitForBusyOverlay();

			// FALL THROUGH

		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}



		if ( page != null ) {
			page.zWaitForActive();
		}


		// Default behavior
		return (page);

	}

	@Override
	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {

		if (shortcut == null)
			throw new HarnessException("Shortcut cannot be null");

		tracer.trace("Using the keyboard, press the "+ shortcut.getKeys() +" keyboard shortcut");

		AbsPage page = null;

		if ( (shortcut == Shortcut.S_NEWITEM) ||
				(shortcut == Shortcut.S_NEWMESSAGE) ||
				(shortcut == Shortcut.S_NEWMESSAGE2) )
		{
			// "New Message" shortcuts result in a compose form opening
			page = new FormMailNew(this.MyApplication);
		}else if ( (shortcut == Shortcut.S_NEWTAG) ){

			// "New Message" shortcuts result in a compose form opening
			//page = new FormMailNew(this.MyApplication);
			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageMail);
		}else if ( (shortcut == Shortcut.S_NEWFOLDER) ){

			// "New Message" shortcuts result in a compose form opening
			//page = new FormMailNew(this.MyApplication);
			page = new DialogCreateFolder(MyApplication,((AppAjaxClient) MyApplication).zPageMail);
		}


		zKeyboard.zTypeCharacters(shortcut.getKeys());

		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		// If a page is specified, wait for it to become active
		if ( page != null ) {
			page.zWaitForActive();	// This method throws a HarnessException if never active
		}
		return (page);
	}




}
