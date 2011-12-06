/**
 *
 */
package com.zimbra.qa.selenium.projects.ajax.ui.mail;

import java.util.*;

import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;



/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsTab {


	public static class Locators {

		public static final String IsConViewActiveCSS 			= "css=div[id='zv__CLV-main']";
		public static final String IsMsgViewActiveCSS 			= "css=div[id='zv__TV-main']";

		public static final String zPrintIconBtnID 		= "zb__CLV-main__PRINT_left_icon";
		public static final String zTagMenuDropdownBtnID	= "zb__CLV-main__TAG_MENU_dropdown";
		public static final String zDetachIconBtnID		= "zb__TV-main__DETACH_left_icon";
		public static final String zViewMenuDropdownBtnID	= "zb__CLV-main__VIEW_MENU_dropdown";

		public static final String zCloseIconBtn_messageWindow 	= "css=td[id=zb__MSG__CLOSE_left_icon]";
		public static final String cssTVRowsLocator	= "css=div#zl__TV-main__rows";
		
		// Accept, Decline & Tentative button, menus and dropdown locators
		public static final String AcceptDropdown = "css=td[id$='__Inv__REPLY_ACCEPT_dropdown']";
		public static final String AcceptNotifyOrganizerMenu = "id=REPLY_ACCEPT_NOTIFY_title";
		public static final String AcceptEditReplyMenu = "id=INVITE_REPLY_ACCEPT_title";
		public static final String AcceptDontNotifyOrganizerMenu = "id=REPLY_ACCEPT_IGNORE_title";

		public static final String TentativeDropdown = "css=td[id$='__Inv__REPLY_TENTATIVE_dropdown']";
		public static final String TentativeNotifyOrganizerMenu = "id=REPLY_TENTATIVE_NOTIFY_title";
		public static final String TentativeEditReplyMenu = "id=INVITE_REPLY_TENTATIVE_title";
		public static final String TentativeDontNotifyOrganizerMenu = "id=REPLY_TENTATIVE_IGNORE_title";
		
		public static final String DeclineDropdown = "css=td[id$='__Inv__REPLY_DECLINE_dropdown']";
		public static final String DeclineNotifyOrganizerMenu = "id=REPLY_DECLINE_NOTIFY_title";
		public static final String DeclineEditReplyMenu = "id=INVITE_REPLY_DECLINE_title";
		public static final String DeclineDontNotifyOrganizerMenu = "id=REPLY_DECLINE_IGNORE_title";
		
		public static final String ProposeNewTimeButtonMsgView = "id=zb__TV-main__Inv__PROPOSE_NEW_TIME_title";

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

		String locator;
		boolean loaded, visible;


		/**
		 * 8.0
		 * MLV:
		 * <div id="zb__TV-main__NEW_MENU" style="position: absolute; overflow: visible; z-index: 300; left: 5px; top: 78px; width: 159px; height: 24px;" class="ZToolbarButton ZWidget   ZHasDropDown       ZHasLeftIcon ZHasText" parentid="z_shell">
		 * CLV:
		 * <div id="zb__CLV-main__NEW_MENU" style="position: absolute; overflow: visible; z-index: 300; left: 5px; top: 78px; width: 159px; height: 24px;" class="ZToolbarButton ZWidget   ZHasDropDown       ZHasLeftIcon ZHasText" parentid="z_shell">
		 * 
		 */

		// If the "NEW" button is visible, then the app is visible

		// Check MLV first
		locator = "css=div#zb__TV-main__NEW_MENU";

		loaded = this.sIsElementPresent(locator);
		visible = this.zIsVisiblePerPosition(locator, 4, 74);
		if ( loaded && visible )
			return (true);

		// Check CLV next
		locator = "css=div#zb__CLV-main__NEW_MENU";
		loaded = this.sIsElementPresent(locator);
		visible = this.zIsVisiblePerPosition(locator, 4, 74);
		if ( loaded && visible )
			return (true);


		// We made it here, neither were active
		return (false);
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
			locator = "css=div[id$='__NEW_MENU'] td[id$='__NEW_MENU_title']";

			// Create the page
			page = new FormMailNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_NEW_IN_NEW_WINDOW ) {

			// New button
			locator = "css=div[id$='__NEW_MENU'] td[id$='__NEW_MENU_title']";

			// Create the page
			page = new SeparateWindowFormMailNew(this.MyApplication);

			// Don't fall through - the new window may need additional information from the test case
			// such as "Zimbra: Compose" or "Zimbra: Reply" to determine if the window is open
			
			this.zClickAt(locator,"0,0");
			
			return (page);

		} else if ( button == Button.B_GETMAIL || button == Button.B_LOADFEED || button == Button.B_REFRESH ) {

			return (((AppAjaxClient)this.MyApplication).zPageMain.zToolbarPressButton(Button.B_REFRESH));

		} else if ( button == Button.B_DELETE ) {

			String id;
			if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
				id = "zb__TV-main__DELETE_left_icon";
			} else {
				id = "zb__CLV-main__DELETE_left_icon";
			}

			// Check if the button is enabled
			locator = "css=td[id='"+ id +"']>div[class*='ZDisabledImage']";
			if ( sIsElementPresent(locator) ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled: ZDisabledImage");
			}

			locator = "css=td#" + id;


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

			return (this.zToolbarPressPulldown(Button.B_ACTIONS, button));

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

		} else if ( button == Button.B_MAIL_LIST_SORTBY_FLAGGED ) {

			locator = "css=td[id='zlh__TV-main__fg'] div[class='ImgFlagRed']";
			this.zClick(locator);
			this.zWaitForBusyOverlay();
			return (null);

		} else if ( button == Button.B_MAIL_LIST_SORTBY_FROM ) {

			locator = "css=td[id='zlh__TV-main__fr'] td[id='zlhl__TV-main__fr']";
			this.zClick(locator);
			this.zWaitForBusyOverlay();
			return (null);

		} else if ( button == Button.B_MAIL_LIST_SORTBY_ATTACHMENT ) {

			locator = "css=td[id='zlh__TV-main__at'] div[class='ImgAttachment']";
			this.zClick(locator);
			this.zWaitForBusyOverlay();
			return (null);

		} else if ( button == Button.B_MAIL_LIST_SORTBY_SUBJECT ) {

			locator = "css=td[id='zlh__TV-main__su'] td[id='zlhl__TV-main__su']";
			this.zClick(locator);
			this.zWaitForBusyOverlay();
			return (null);

		} else if ( button == Button.B_MAIL_LIST_SORTBY_SIZE ) {

			locator = "css=td[id='zlh__TV-main__sz'] td[id='zlhl__TV-main__sz']";
			this.zClick(locator);
			this.zWaitForBusyOverlay();
			return (null);

		} else if ( button == Button.B_MAIL_LIST_SORTBY_RECEIVED ) {

			locator = "css=td[id='zlh__TV-main__dt'] td[id='zlhl__TV-main__dt']";
			this.zClick(locator);
			this.zWaitForBusyOverlay();
			return (null);
			
		} else {
			throw new HarnessException("no logic defined for button "+ button);
		}

		if ( locator == null ) {
			throw new HarnessException("locator was null for button "+ button);
		}

		// Default behavior, process the locator by clicking on it
		//
		this.zClickAt(locator,"0,0");

		//need small wait so that next element gets appeared/visible  after click
		SleepUtil.sleepMedium();
		// If the app is busy, wait for it to become active
		this.zWaitForBusyOverlay();

		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP &&
				button == Button.B_GETMAIL) {


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
		
		// CLV vs. MLV
		boolean isCLV = this.zIsVisiblePerPosition("css=div#ztb__CLV-main", 0, 0);

		if (pulldown == Button.B_TAG) {
			if (option == Button.O_TAG_NEWTAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=div[id$='__TAG_MENU|MENU'] td[id='mail_newtag_title']";

				page = new DialogTag(this.MyApplication, this);

				// FALL THROUGH
			} else if (option == Button.O_TAG_REMOVETAG) {

				pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

				optionLocator = "css=div[id$='__TAG_MENU|MENU'] td[id='mail_removetag_title']";

				page = null;

				// FALL THROUGH
			} else {
				throw new HarnessException(
						"no logic defined for pulldown/option " + pulldown
						+ "/" + option);
			}
		} else if (pulldown == Button.B_NEW) {

			pulldownLocator = "css=td[id$='__NEW_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";

			if (option == Button.O_NEW_TAG) {

				optionLocator = "css=td[id$='__NEW_MENU_NEW_TAG_left_icon']>div[class='ImgNewTag']";
				page = new DialogTag(this.MyApplication, this);

			} else if (option == Button.O_NEW_FOLDER) {

				optionLocator = "css=td[id$='__NEW_MENU_NEW_FOLDER_left_icon']>div[class='ImgNewFolder']";
				page = new DialogCreateFolder(this.MyApplication, this);

			}

		} else if ( pulldown == Button.B_ACTIONS ) {
			
			if (isCLV) {
				pulldownLocator = "css=td[id='zb__CLV-main__ACTIONS_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
				optionLocator = "css=div[id='zm__CLV-main']";
			} else {
				pulldownLocator = "css=td[id='zb__TV-main__ACTIONS_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
				optionLocator = "css=div[id='zm__TV-main']";
			}

			if ( option == Button.B_PRINT ) {
				
				optionLocator += " div[id^='PRINT'] td[id$='_title']";
				page = new DialogRedirect(this.MyApplication, this);
				
				// FALL THROUGH

				
			} else if ((option == Button.B_RESPORTSPAM) || (option == Button.B_RESPORTNOTSPAM)) {
				
				optionLocator += " div[id^='SPAM'] td[id$='_title']";
				page = null;

				// FALL THROUGH

			} else if ( option == Button.B_LAUNCH_IN_SEPARATE_WINDOW ) {
				
				optionLocator += " div[id^='DETACH'] td[id$='_title']";
				page = new SeparateWindowDisplayMail(this.MyApplication);

				// We don't know the window title at this point (However, the test case should.)
				// Don't check that the page is active, let the test case do that.

				this.zClickAt(pulldownLocator, "0,0");
				zWaitForBusyOverlay();

				this.zClickAt(optionLocator, "0,0");
				zWaitForBusyOverlay();

				return (page);

			} else if ( option == Button.O_MARK_AS_READ ) {
				
				optionLocator += " div[id^='MARK_READ'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if ( option == Button.O_MARK_AS_UNREAD ) {
				
				optionLocator += " div[id^='MARK_UNREAD'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if ( option == Button.O_SHOW_ORIGINAL ) {
				
				optionLocator += " div[id^='SHOW_ORIG'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if ( option == Button.B_REDIRECT ) {
				
				optionLocator += " div[id^='REDIRECT'] td[id$='_title']";
				page = new DialogRedirect(this.MyApplication, this);
				
				// FALL THROUGH

			} else if ( option == Button.B_MUTE ) {
				
				optionLocator += " div[id^='MUTE_CONV'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if ( option == Button.O_EDIT_AS_NEW ) {
				
				optionLocator += " div[id^='EDIT_AS_NEW'] td[id$='_title']";
				page = new FormMailNew(this.MyApplication);
				
				// FALL THROUGH

			} else if ( option == Button.O_NEW_FILTER ) {
				
				optionLocator += " div[id^='ADD_FILTER_RULE'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if ( option == Button.O_NEW_APPOINTMENT ) {
				
				optionLocator += " div[id^='CREATE_APPT'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if ( option == Button.O_NEW_TASK ) {
				
				optionLocator += " div[id^='CREATE_TASK'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else if ( option == Button.O_QUICK_COMMANDS_MENU ) {
				
				optionLocator += " div[id^='QUICK_COMMANDS'] td[id$='_title']";
				page = null;
				
				// FALL THROUGH

			} else {
				
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);

			}
			
		} else if ((pulldown == Button.B_OPTIONS)&& (option == Button.O_ADD_SIGNATURE)) {

			pulldownLocator = "css=td[id$='_ADD_SIGNATURE_dropdown']>div[class='ImgSelectPullDownArrow']";
			//optionLocator = "//td[contains(@id,'_title') and contains (text(),'sigName')]";

			page = null;

		} else if ( pulldown == Button.B_MOVE ) {

			if ( option == Button.O_NEW_FOLDER ) {

				// Check if we are CLV or MV
				if ( this.zIsVisiblePerPosition("css=div#ztb__CLV-main", 0, 0) ) {
					pulldownLocator = "css=td#zb__CLV-main__MOVE_MENU_dropdown>div";
				} else {
					pulldownLocator = "css=td#zb__TV-main__MOVE_MENU_dropdown>div";
				}
				optionLocator = "css=div[class='DwtFolderChooser'] div[id$='_newButtonDivId'] td[id$='_title']";
				page = new DialogCreateFolder(this.MyApplication, this);

			} else {
				throw new HarnessException("no logic defined for B_MOVE and " + option);
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

			// 8.0 change ... need zClickAt()
			// this.zClick(pulldownLocator);
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

				// 8.0 change ... need zClickAt()
				// this.zClick(optionLocator);
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




	public enum PageMailView {
		BY_MESSAGE, BY_CONVERSATION
	}

	/**
	 * Get the Page Property: ListView = By message OR By Conversation
	 * @return
	 * @throws HarnessException
	 */
	public PageMailView zGetPropMailView() throws HarnessException {
		if ( this.zIsVisiblePerPosition(Locators.IsConViewActiveCSS, 0, 0) ) {
			return (PageMailView.BY_CONVERSATION);
		} else if ( this.zIsVisiblePerPosition(Locators.IsMsgViewActiveCSS, 0, 0) ) {
			return (PageMailView.BY_MESSAGE);
		}

		throw new HarnessException("Unable to determine the Page Mail View");
	}

	private MailItem parseMessageRow(String top) throws HarnessException {
		MailItem item = null;

		if ( top.contains("CLV") ) {
			item = new ConversationItem();

			if ( this.sIsElementPresent(top.trim() + "[class*='ZmConvExpanded']"))
				((ConversationItem)item).gIsConvExpanded = true;

		} else if ( top.contains("TV") )  {
			item = new MailItem();
		} else {
			throw new HarnessException("Unknown message row type "+ top);
		}

		String msglocator = top;
		String locator;

		// Is it checked?
		locator = msglocator + " div[class*='ImgCheckboxChecked']";
		item.gIsSelected = this.sIsElementPresent(locator);

		// Is it flagged
		// TODO: probably can't have boolean, need 'blank', 'disabled', 'red', and other states
		locator = msglocator + " div[class*='ImgFlagRed']";
		item.gIsFlagged = this.sIsElementPresent(locator);

		// Is it high priority?
		item.gPriority = MailItem.Priority.Normal;
		if ( this.sIsElementPresent(msglocator + " div[id$='__pr'][class*=ImgPriorityHigh_list]") )
			item.gPriority = MailItem.Priority.High;
		if ( this.sIsElementPresent(msglocator + " div[id$='__pr'][class*=ImgPriorityLow_list]") )
			item.gPriority = MailItem.Priority.Low;


		locator = msglocator + " div[id$='__tg']";
		// TODO: handle tags

		// Get the From
		locator = msglocator + " [id$='__fr']";
		item.gFrom = this.sGetText(locator).trim();

		// Get the attachment
		locator = msglocator + " div[id$='__at'][class*=ImgBlank_16]";
		if ( this.sIsElementPresent(locator) ) {
			item.gHasAttachments = false;
		} else {
			// TODO - handle other attachment types
		}

		// Get the fragment and the subject
		locator = msglocator + " span[id$='__fm']";
		if ( this.sIsElementPresent(locator) ) {

			item.gFragment = this.sGetText(locator).trim();

			// Get the subject
			locator = msglocator + " td[id$='__su']";
			String subject = this.sGetText(locator).trim();

			// The subject contains the fragment, e.g. "subject - fragment", so
			// strip it off
			item.gSubject = subject.replace(item.gFragment, "").trim();


		} else {

			// Conversation items's fragment is in the subject field
			locator = msglocator + " td[id$='__su']";
			item.gFragment = this.sGetText(locator).trim();

			// TODO: should the subject be parsed from the conversation container?
			// For now, just set it to blank
			item.gSubject = "";

		}


		// Get the folder
		locator = msglocator + " nobr[id$='__fo']";
		if ( this.sIsElementPresent(locator) ) {
			item.gFolder = this.sGetText(locator).trim();
		} else {
			item.gFolder = "";
		}

		// Get the size
		locator = msglocator + " nobr[id$='__sz']";
		if ( this.sIsElementPresent(locator) ) {
			item.gSize = this.sGetText(locator).trim();
		} else {
			item.gSize = "";
		}

		// Get the received date
		locator = msglocator + " td[id$='__dt']";
		item.gReceived = this.sGetText(locator).trim();


		return (item);
	}

	/**
	 * Return a list of all messages in the current view.<p>
	 * <p>
	 * For conversations, a ConversationItem (extends MailItem) is returned for the containing row.  If the
	 * conversation is expanded, then the expanded messages are also returned in the list.<p>
	 * <p>
	 * 
	 * @return
	 * @throws HarnessException
	 */
	public List<MailItem> zListGetMessages() throws HarnessException {

		List<MailItem> items = new ArrayList<MailItem>();

		String listLocator = null;
		String rowLocator = null;
		if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
			listLocator = "css=div[id='zl__TV-main__rows']";
			rowLocator = "div[id^='zli__TV-main__']";
		} else {
			listLocator = "css=div[id='zl__CLV__rows']";
			rowLocator = "div[id^='zli__CLV__']";
		}

		// Make sure the button exists
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("Message List View Rows is not present: " + listLocator);

		String tableLocator = listLocator + " " + rowLocator;
		// How many items are in the table?
		int count = this.sGetCssCount(tableLocator);
		logger.debug(myPageName() + " zListGetMessages: number of messages: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			// Add the new item to the list
			MailItem item = parseMessageRow(listLocator + " div:nth-of-type("+ i +") ");
			items.add(item);
			logger.info(item.prettyPrint());
		}

		// Return the list of items
		return (items);
	}



	@Override
	public AbsPage zListItem(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");

		tracer.trace(action +" on subject = "+ subject);

		if ( action == null )
			throw new HarnessException("action cannot be null");

		if ( subject == null )
			throw new HarnessException("subject cannot be null");

		AbsPage page = null;
		String listLocator;
		String rowLocator;
		String itemlocator = null;


		// Find the item locator
		//

		if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
			listLocator = "css=div[id='zl__TV-main__rows']";
			rowLocator = "div[id^='zli__TV-main__']";
		} else {
			listLocator = "css=div[id='zl__CLV__rows']";
			rowLocator = "div[id^='zli__CLV__']";
		}

		// TODO: how to handle both messages and conversations, maybe check the view first?
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("List View Rows is not present "+ listLocator);

		// How many items are in the table?
		int count = this.sGetCssCount(listLocator + " " + rowLocator);
		logger.debug(myPageName() + " zListSelectItem: number of list items: "+ count);


		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {

			itemlocator = listLocator + " div:nth-of-type("+ i +") ";
			String s = this.sGetText(itemlocator + " td[id$='__su']").trim();

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
			this.zClickAt(itemlocator,"");

			this.zWaitForBusyOverlay();

			// Return the displayed mail page object
			page = new DisplayMail(MyApplication);

			// FALL THROUGH

		} else if ( action == Action.A_DOUBLECLICK ) {

			// Double-Click on the item
			this.sDoubleClick(itemlocator);

			this.zWaitForBusyOverlay();

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

			String selectlocator = itemlocator + " div[id$='__se']";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);

			String image = this.sGetAttribute(selectlocator +"@class");
			if ( image.equals("ImgCheckboxChecked") )
				throw new HarnessException("Trying to check box, but it was already enabled");

			// Left-Click on the flag field
			this.zClick(selectlocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else if ( action == Action.A_MAIL_UNCHECKBOX ) {

			String selectlocator = itemlocator + " div[id$='__se']";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);

			String image = this.sGetAttribute(selectlocator +"@class");
			if ( image.equals("ImgCheckboxUnchecked") )
				throw new HarnessException("Trying to uncheck box, but it was already disabled");

			// Left-Click on the flag field
			this.zClick(selectlocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

			// FALL THROUGH

		} else if ( action == Action.A_MAIL_EXPANDCONVERSATION ) {

			String selectlocator = itemlocator + " div[id$='__ex']";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);

			String image = this.sGetAttribute(selectlocator +"@class");
			if ( image.equals("ImgNodeExpanded") )
				throw new HarnessException("Trying to expand, but conversation was alread expanded");

			// Left-Click on the flag field
			this.zClick(selectlocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

		} else if ( action == Action.A_MAIL_COLLAPSECONVERSATION ) {

			String selectlocator = itemlocator + " div[$id$='__ex']";
			if ( !this.sIsElementPresent(selectlocator) )
				throw new HarnessException("Checkbox locator is not present "+ selectlocator);

			String image = this.sGetAttribute(selectlocator +"@class");
			if ( image.equals("ImgNodeCollapsed") )
				throw new HarnessException("Trying to collapse, but conversation was alread collapsed");

			// Left-Click on the flag field
			this.zClick(selectlocator);

			this.zWaitForBusyOverlay();

			// No page to return
			page = null;

		} else if ( (action == Action.A_MAIL_FLAG) || (action == Action.A_MAIL_UNFLAG) ) {
			// Both FLAG and UNFLAG have the same action and result

			String flaglocator = itemlocator + " div[id$='__fg']";

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
			//page.zWaitForActive();
		}

		// default return command
		return (page);

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
			listLocator = "css=div[id='zl__TV-main__rows']";
			rowLocator = "div[id^='zli__TV-main__']";
		} else {
			listLocator = "css=div[id='zl__CLV__rows']";
			rowLocator = "div[id^='zli__CLV__']";
		}

		// TODO: how to handle both messages and conversations, maybe check the view first?
		if ( !this.sIsElementPresent(listLocator) )
			throw new HarnessException("List View Rows is not present "+ listLocator);

		// How many items are in the table?
		int count = this.sGetCssCount(listLocator + " " + rowLocator);
		logger.debug(myPageName() + " zListSelectItem: number of list items: "+ count);


		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {


			itemlocator = listLocator + " div:nth-of-type("+ i +") ";
			String s = this.sGetText(itemlocator + " td[id$='__su']").trim();

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
			this.zRightClickAt(itemlocator,"");

			// Now the ContextMenu is opened
			// Click on the specified option

			String optionLocator = "css=div[id^='zm__CLV-main__']";
			if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
				optionLocator = "div[id^='zm__TV-main__']";
			}

			if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
				optionLocator = "css=div[id^='zm__TV-main__']";
			}


			if ( option == Button.O_MARK_AS_READ ) {

				optionLocator += " div[id^='MARK_READ'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else if ( option == Button.O_MARK_AS_UNREAD ) {

				optionLocator += " div[id^='MARK_UNREAD'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else if (option == Button.B_DELETE) {

				optionLocator += " div[id^='DELETE'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else if (option == Button.B_REPLY) {

				optionLocator += " div[id^='REPLY'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else if (option == Button.B_REPLYALL) {

				optionLocator += " div[id^='REPLY'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else if (option == Button.B_FORWARD) {

				optionLocator += " div[id^='REPLY'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else if ( option == Button.B_REDIRECT ) {

				optionLocator += " div[id^='REDIRECT'] td[id$='_title']";

				page = new DialogRedirect(this.MyApplication, this);

				// FALLTHROUGH

			} else if ( option == Button.B_MUTE ) {

				optionLocator += " div[id^='MUTE_CONV'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else if ( option == Button.O_EDIT_AS_NEW ) {

				optionLocator += " div[id^='EDIT_AS_NEW'] td[id$='_title']";
				page = new FormMailNew(this.MyApplication);

				// FALLTHROUGH

			} else if ( option == Button.O_CREATE_TASK ) {

				optionLocator += " div[id^='CREATE_TASK'] td[id$='_title']";
				page = null;

				// FALLTHROUGH

			} else {
				throw new HarnessException("implement action:"+ action +" option:"+ option);
			}

			// click on the option
			this.zClickAt(optionLocator,"");

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
		String	keyCode;
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

		} else if ( (shortcut == Shortcut.S_NEWITEM_IN_NEW_WINDOW) ||
					(shortcut == Shortcut.S_NEWMESSAGE_IN_NEW_WINDOW) ||
					(shortcut == Shortcut.S_NEWMESSAGE2_IN_NEW_WINDOW) )
			{

				// These shortcuts result in a separate window opening
				page = new SeparateWindowFormMailNew(this.MyApplication);
				
				// Don't fall through.  The test case needs to make sure the separate window opens
				zKeyboard.zTypeCharacters(shortcut.getKeys());
				return (page);

		}else if ( (shortcut == Shortcut.S_NEWTAG) ){

			// "New Message" shortcuts result in a compose form opening
			//page = new FormMailNew(this.MyApplication);
			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

		}else if ( (shortcut == Shortcut.S_NEWFOLDER) ){

			// "New Message" shortcuts result in a compose form opening
			//page = new FormMailNew(this.MyApplication);
			page = new DialogCreateFolder(MyApplication,((AppAjaxClient) MyApplication).zPageMail);

		} else if ( (shortcut == Shortcut.S_MAIL_HARDELETE) ) {

			// Hard Delete shows the Warning Dialog : Are you sure you want to permanently delete it?
			page = new DialogWarning(DialogWarning.DialogWarningID.PermanentlyDeleteTheItem,
					MyApplication, ((AppAjaxClient) MyApplication).zPageMail);

		} else if ( shortcut == Shortcut.S_ASSISTANT ) {

			page = new DialogAssistant(MyApplication, ((AppAjaxClient) MyApplication).zPageMail);

		} else if(shortcut== Shortcut.S_ESCAPE) {

			page = new DialogWarning(
					DialogWarning.DialogWarningID.SaveCurrentMessageAsDraft,
					this.MyApplication,
					((AppAjaxClient)this.MyApplication).zPageMail);	

			keyCode = "27";
			zKeyDown(keyCode);
			return page;

			// By default, just type the shortcut and return null page
			//		} else {
			//			
			//			throw new HarnessException("No logic for shortcut : "+ shortcut);
			//			
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

	public AbsPage zToolbarPressPulldown(Button pulldown, Button option, Object dynamic) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +", "+ dynamic +")");
		tracer.trace("Click pulldown "+ pulldown +" then "+ option +" then "+ dynamic);


		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");
		if (option == null)
			throw new HarnessException("Option cannot be null!");
		if (dynamic == null)
			throw new HarnessException("dynamic object cannot be null!");


		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		String dynamicLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if ((pulldown == Button.B_OPTIONS)&& (option == Button.O_ADD_SIGNATURE)) {

			if ( !(dynamic instanceof String) ) 
				throw new HarnessException("dynamic must be a string!  "+ dynamic.getClass().getCanonicalName());

			String name = (String)dynamic;
			logger.info("Click on Signature: "+ name);
			
			//pulldownLocator = "css=td[id$='_ADD_SIGNATURE_dropdown']>div[class='ImgSelectPullDownArrow']";
			pulldownLocator="css=[id^=zb__COMPOSE][id$=__COMPOSE_OPTIONS_dropdown]";
			optionLocator="css=div[id='ADD_SIGNATURE'] tr[id='POPUP_ADD_SIGNATURE']> td[id='ADD_SIGNATURE_dropdown']>div[class='ImgCascade']";
			dynamicLocator ="css=td[id*='_title']td:contains('"+ name + "')";
			page = null;

		} else if ( pulldown == Button.B_ACTIONS ) {
			
			boolean isCLV = this.zIsVisiblePerPosition("css=div#ztb__CLV-main", 0, 0);

			if (isCLV) {
				pulldownLocator = "css=td[id='zb__CLV-main__ACTIONS_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
				optionLocator = "css=div[id='zm__CLV-main']";
			} else {
				pulldownLocator = "css=td[id='zb__TV-main__ACTIONS_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
				optionLocator = "css=div[id='zm__TV-main']";
			}
			
			if ( option == Button.O_QUICK_COMMANDS_MENU ) {
				
				if ( !(dynamic instanceof String) ) 
					throw new HarnessException("dynamic must be a string!  "+ dynamic.getClass().getCanonicalName());

				String quickcommand = (String)dynamic;
				logger.info("Click on Quick Command: "+ quickcommand);

				optionLocator += " div[id^='QUICK_COMMANDS'] td[id$='_title']";
				dynamicLocator	= "css=div[id^='quickCommandSubMenu_'] td[id$='_title']:contains('"+ quickcommand + "')";
				page = null;
				
				// Make sure the locator exists
				if (!this.sIsElementPresent(pulldownLocator)) {
					throw new HarnessException(pulldownLocator + " not present!");
				}

				this.zClickAt(pulldownLocator,"");
				zWaitForBusyOverlay();

				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException(optionLocator + " not present!");
				}

				this.sMouseOver(optionLocator);
				zWaitForBusyOverlay();

				// Make sure the locator exists
				// Sometimes the menu isn't drawn right away.  Wait for it.
				GeneralUtility.waitForElementPresent(this, dynamicLocator);

				this.zClickAt(dynamicLocator,"");
				zWaitForBusyOverlay();
				
				return (page);
				
			} else {
				throw new HarnessException("no logic defined for pulldown/option " + pulldown + "/" + option);
			}

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

			this.zClickAt(pulldownLocator,"");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException(" option " + option
							+ " optionLocator " + optionLocator
							+ " not present!");
				}

				this.zClickAt(optionLocator,"");

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();

			}
			if (dynamicLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(dynamicLocator)) {
					throw new HarnessException(dynamicLocator+ " not present!");
				}

				this.zClickAt(dynamicLocator,"");

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
	 * Activate a pulldown with dynamic values, such as "Move to folder" and "Add a tag".
	 * 
	 * @param pulldown the toolbar button to press
	 * @param dynamic the toolbar item to click such as FolderItem or TagItem
	 * @throws HarnessException 
	 */
	public AbsPage zToolbarPressPulldown(Button pulldown, Object dynamic) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ dynamic +")");

		tracer.trace("Click pulldown "+ pulldown +" then "+ dynamic);


		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (dynamic == null)
			throw new HarnessException("Option cannot be null!");


		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned


		if ( pulldown == Button.B_MOVE ) {

			if ( !(dynamic instanceof FolderItem) ) 
				throw new HarnessException("if pulldown = " + Button.B_MOVE +", then dynamic must be FolderItem");

			FolderItem folder = (FolderItem)dynamic;

			// Check if we are CLV or MV
			if ( this.zIsVisiblePerPosition("css=div#ztb__CLV-main", 0, 0) ) {
				pulldownLocator = "css=td#zb__CLV-main__MOVE_MENU_dropdown>div";
				optionLocator = "css=td#zti__DwtFolderChooser_MailCLV-main__"+ folder.getId() + "_textCell";
			} else {
				pulldownLocator = "css=td#zb__TV-main__MOVE_MENU_dropdown>div";
				optionLocator = "css=td#zti__DwtFolderChooser_MailTV-main__"+ folder.getId() + "_textCell";
			}


			page = null;


		} else if ( pulldown == Button.B_TAG ) {
			
			if ( !(dynamic instanceof TagItem) ) 
				throw new HarnessException("if pulldown = " + Button.B_TAG +", then dynamic must be TagItem");

			TagItem tag = (TagItem)dynamic;

			pulldownLocator = "css=td[id$='__TAG_MENU_dropdown']>div[class='ImgSelectPullDownArrow']";
			optionLocator = "css=div[id='zb__TV-main__TAG_MENU|MENU'] td[id$='_title']:contains("+ tag.getName() +")";
			page = null;

		} else {

			throw new HarnessException("no logic defined for pulldown/dynamic " + pulldown + "/" + dynamic);

		}

		// Default behavior
		if (pulldownLocator != null) {

			// Make sure the locator exists
			if (!this.sIsElementPresent(pulldownLocator)) {
				throw new HarnessException("Button " + pulldown + " pulldownLocator " + pulldownLocator + " not present!");
			}

			this.zClickAt(pulldownLocator,"");

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			SleepUtil.sleepSmall();

			if (optionLocator != null) {

				// Make sure the locator exists
				if (!this.sIsElementPresent(optionLocator)) {
					throw new HarnessException(" dynamic " + dynamic + " optionLocator " + optionLocator + " not present!");
				}

				this.zClickAt(optionLocator,"");

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

	/* (non-Javadoc)
	 * @see com.zimbra.qa.selenium.framework.ui.AbsPage#zHoverOver(com.zimbra.qa.selenium.framework.ui.Button)
	 */
	public AbsTooltip zHoverOver(Button button) throws HarnessException {
		logger.info(myPageName() + " zHoverOverButton("+ button +")");

		tracer.trace("Hover over "+ button);
		
		if ( button == null )
			throw new HarnessException("Button cannot be null");
		
		String locator = null;

		if ( button == Button.B_DELETE ) {
			
			if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
				locator = "css=td[id='zb__TV-main__DELETE_title']";
			} else {
				locator = "css=td[id='zb__CLV-main__DELETE_title']";
			}
			
		} else if ( button == Button.B_REPLY ) {
				
			if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
				locator = "css=td[id='zb__TV-main__REPLY_title']";
			} else {
				locator = "css=td[id='zb__CLV-main__REPLY_title']";
			}

		} else {
			throw new HarnessException("no logic defined for button: "+ button);
		}
		
		// If another tooltip is active, sometimes it takes a few seconds for the new text to show
		// So, wait if the tooltip is already active
		// Don't wait if the tooltip is not active
		//
		
		Tooltip tooltip = new Tooltip(this);
		if (tooltip.zIsActive()) {
			
			// Mouse over
			this.sMouseOver(locator);
			this.zWaitForActive();
			
			// Wait for the new text
			SleepUtil.sleep(5000);
			
			// Make sure the tooltip is active
			tooltip.zWaitForActive();

		} else {
			
			// Mouse over
			this.sMouseOver(locator);
			this.zWaitForActive();

			// Make sure the tooltip is active
			tooltip.zWaitForActive();

		}
		
		return (tooltip);
	}



}
