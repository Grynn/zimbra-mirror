/**
 * 
 */
package projects.ajax.ui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import projects.ajax.ui.Actions.Action;
import projects.ajax.ui.Buttons.Button;
import framework.items.ConversationItem;
import framework.items.MailItem;
import framework.ui.AbsApplication;
import framework.ui.AbsSeleniumObject;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsAjaxPage {

	
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

		public static final String zNewIconBtn 			= "id=zb__TV__NEW_MENU_left_icon";
		public static final String zNewMenuIconBtn 		= "id=zb__CLV__NEW_MENU_left_icon";
		public static final String zNewMenuBtn 			= "id=zb__CLV__NEW_MENU";
		public static final String zNewMenuDropDown 	= "id=zb__CLV__NEW_MENU_dropdown";
		public static final String zGetMailIconBtnCLVID 	= "zb__CLV__CHECK_MAIL_left_icon";
		public static final String zGetMailIconBtnTVID 		= "zb__TV__CHECK_MAIL_left_icon";
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
		public static final String zDetachIconBtnID		= "zb__TV__DETACH_left_icon";
		public static final String zDetachBtn 			= "id=zb__TV__DETACH";
		public static final String zDetachIconBtn2 		= "id=zb__CLV__DETACH_left_icon";
		public static final String zDetachBtn2 			= "id=zb__CLV__DETACH";
		public static final String zDetachBtn_ComposedMessage = "id=zb__COMPOSE1__DETACH_COMPOSE";
		public static final String zViewIconBtnID 		= "zb__CLV__VIEW_MENU_left_icon";
		public static final String zViewBtn 			= "id=zb__CLV__VIEW_MENU";
		public static final String zViewMenuDropdownBtnID	= "zb__CLV__VIEW_MENU_dropdown";
		
		public static final String zViewMenuCLVBtnID	= zViewIconBtnID;
		public static final String zViewMenuTVBtnID		= "zb__TV__VIEW_MENU_left_icon";

		
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
	//	public static final String zPreferencesMailIconBtn = "id=ztab__PREF__"
	//			+ localize(locator.mail) + "_title";


		public static final String zCLVRows			= "zl__CLV__rows";
		public static final String zTVRows			= "zl__TV__rows";

	}
	
	



	public PageMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMail.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {

		// Make sure the main page is active
		if ( !this.MyApplication.zPageMain.isActive() ) {
			this.MyApplication.zPageMain.navigateTo();
		}
		
		// If the "folders" tree is visible, then mail is active
		String locator = "xpath=//div[@id='zov__main_Mail']";
		
		boolean loaded = this.sIsElementPresent(locator);
		if ( !loaded )
			return (loaded);
		
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
	public void navigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( isActive() ) {
			return;
		}
		
		// Make sure we are logged into the Mobile app
		if ( !MyApplication.zPageMain.isActive() ) {
			MyApplication.zPageMain.navigateTo();
		}
		
		// Click on Mail icon
		sClick(PageMain.Locators.zAppbarMail);
		
		waitForActive();

	}
	
	
	@Override
	public AbsSeleniumObject zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");
		
		if ( button == null )
			throw new HarnessException("Button cannot be null!");
		
				
		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsSeleniumObject page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( button == Buttons.B_NEW ) {
			
			// For "NEW" without a specified pulldown option, just return the default item
			// To use "NEW" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//
			
			this.zPressKeyboardShortcut(KeyEvent.VK_N);
			
			// Not default behavior (zPressKeyboardShortcut vs. zClick).
			// Do not fall through.
			return (new FormMailNew(this.MyApplication));
			
		} else if ( button == Buttons.B_GETMAIL ) {
			
			if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
				locator = "id="+ Locators.zGetMailIconBtnTVID;
			} else {
				locator = "id="+ Locators.zGetMailIconBtnCLVID;
			}

			
		} else if ( button == Buttons.B_DELETE ) {
			
			String id;
			if ( zGetPropMailView() == PageMailView.BY_MESSAGE ) {
				id = "zb__TV__DELETE_left_icon";
			} else {
				id = "zb__CLV__DELETE_left_icon";
			}
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ id +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id="+ id;
				
			
		} else if ( button == Buttons.B_MOVE ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zMoveIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zMoveIconBtnID;
			page = null;	// TODO
			throw new HarnessException("implement Move dialog");
			
		} else if ( button == Buttons.B_PRINT ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zPrintIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zPrintIconBtnID;
			page = null;	// TODO
			throw new HarnessException("implement Print dialog");
			
		} else if ( button == Buttons.B_REPLY ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zReplyIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zReplyIconBtnID;
			page = new FormMailNew(this.MyApplication);
			
		} else if ( button == Buttons.B_REPLYALL ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zReplyAllIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zReplyAllIconBtnID;
			page = new FormMailNew(this.MyApplication);
			
		} else if ( button == Buttons.B_FORWARD ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zForwardIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zForwardIconBtnID;
			page = new FormMailNew(this.MyApplication);
			
		} else if ( button == Buttons.B_RESPORTSPAM ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zJunkIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zJunkIconBtnID;			
			
		} else if ( button == Buttons.B_TAG ) {
			
			// For "TAG" without a specified pulldown option, just click on the pulldown
			// To use "TAG" with a pulldown option, see  zToolbarPressPulldown(Button, Button)
			//
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zTagMenuDropdownBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zTagMenuDropdownBtnID +"'";
			
		} else if ( button == Buttons.B_NEWWINDOW ) {
			
			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ Locators.zDetachIconBtnID +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id='"+ Locators.zDetachIconBtnID;
			page = null;	// TODO
			throw new HarnessException("implement new window page ... probably just DisplayMail object?");
			
			
		} else if ( button == Buttons.B_LISTVIEW ) {
			
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
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);
		
		// Click it
		this.zClick(locator);

		return (page);
	}

	@Override
	public AbsSeleniumObject zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");
		
		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");
		
		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");

		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsSeleniumObject page = null;	// If set, this page will be returned
		
		// Based on the button specified, take the appropriate action(s)
		//
		
		if ( pulldown == Buttons.B_NEW ) {
			
			if ( option == Buttons.O_NEW_ADDRESSBOOK ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_APPOINTMENT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_BRIEFCASE ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_CALENDAR ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_CONTACT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_CONTACTGROUP ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_DOCUMENT ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_FOLDER ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_MESSAGE ) {
				
				// TODO: should this actually click New followed by Message?
				
				pulldownLocator = null;
				optionLocator = null;
				page = zToolbarPressButton(pulldown);
				
			} else if ( option == Buttons.O_NEW_TAG ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_TASK ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_NEW_TASKFOLDER ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}
			
		} else if ( pulldown == Buttons.B_LISTVIEW ) { 

			if ( option == Buttons.O_LISTVIEW_BYCONVERSATION ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_BYMESSAGE ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_READINGPANEBOTTOM ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_READINGPANEOFF ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_LISTVIEW_READINGPANERIGHT ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}

		} else if ( pulldown == Buttons.B_TAG ) {
			
			if ( option == Buttons.O_TAG_NEWTAG ) {
				throw new HarnessException("implement me!");
			} else if ( option == Buttons.O_TAG_REMOVETAG ) {
				throw new HarnessException("implement me!");
			} else {
				throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
			}
			
		} else {
			throw new HarnessException("no logic defined for pulldown "+ pulldown);
		}

		// Default behavior
		if ( pulldownLocator != null ) {
			
			// TODO: Expand pulldownLocator
			
			if ( optionLocator != null ) {
				// TODO: Click optionLocator
			}
			
			throw new HarnessException("implement me!");
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
		if ( sIsElementPresent( "id="+ Locators.zViewMenuTVBtnID ) ) {
			return (PageMailView.BY_MESSAGE);
		} else if ( sIsElementPresent( "id="+ Locators.zViewMenuCLVBtnID ) ) {
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
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(Locators.zTVRows) )
			throw new HarnessException("Message List View Rows is not present "+ Locators.zTVRows);
		
		// How many items are in the table?
		int count = this.sGetXpathCount("//div[@id='zl__TV__rows']//div[contains(@id, 'zli__TV__')]");
		logger.debug(myPageName() + " zListGetMessages: number of conversations: "+ count);

		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			final String msglocator = "//div[@id='zl__TV__rows']/div["+ i +"]";
			String locator;
			
			MailItem item = new MailItem();

			// Is it checked?
			locator = msglocator + "//div[contains(@class, 'ImgCheckboxChecked')]";
			item.isSelected = this.sIsElementPresent(locator);
						
			// Is it flagged
			// TODO: probably can't have boolean, need 'blank', 'disabled', 'red', and other states
			locator = msglocator + "//div[contains(@class, 'ImgFlagRed')]";
			item.isFlagged = this.sIsElementPresent(locator);
			
			locator = "xpath=("+ msglocator +"//div[contains(@id, '__pr')])@class";
			String priority = this.sGetAttribute(locator);
			if ( priority.equals("ImgPriorityHigh_list") ) {
				item.priority = "high";
			} else {
				// TODO - handle other priorities
			}

			
			locator = msglocator + "//div[contains(@id, '__tg')]";
			// TODO: handle tags

			// Get the From
			locator = msglocator + "//*[contains(@id, '__fr')]";
			item.from = this.sGetText(locator).trim();
			
			// Get the attachment
			locator = "xpath=("+ msglocator +"//div[contains(@id, '__at')])@class";
			String attach = this.sGetAttribute(locator);
			if ( attach.equals("ImgBlank_16") ) {
				item.hasAttachments = false;
			} else {
				// TODO - handle other attachment types
			}
				
			// Get the fragment
			locator = msglocator + "//span[contains(@id, '__fm')]";
			item.fragment = this.sGetText(locator).trim();

			// Get the subject
			locator = msglocator + "//td[contains(@id, '__su')]";
			String s = this.sGetText(locator).trim();
			
			// The subject contains the fragment, e.g. "subject - fragment", so
			// strip it off
			item.subject = s.replace(item.fragment, "").trim();

			// Get the folder
			locator = msglocator + "//nobr[contains(@id, '__fo')]";
			if ( this.sIsElementPresent(locator) ) {
				item.folder = this.sGetText(locator).trim();
			} else {
				item.folder = "";
			}

			// Get the size
			locator = msglocator + "//nobr[contains(@id, '__sz')]";
			if ( this.sIsElementPresent(locator) ) {
				item.size = this.sGetText(locator).trim();
			} else {
				item.size = "";
			}
			
			// Get the received date
			locator = msglocator + "//td[contains(@id, '__dt')]";
			item.received = this.sGetText(locator).trim();
			
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
			item.isSelected = this.sIsElementPresent(locator);
			
			// Is it expanded?
			locator = convlocator + "//div[contains(@class, 'ImgNodeExpanded')]";
			item.isExpanded = this.sIsElementPresent(locator);
			
			// Is it flagged
			// TODO: probably can't have boolean, need 'blank', 'disabled', 'red', and other states
			locator = convlocator + "//div[contains(@class, 'ImgFlagRed')]";
			item.isFlagged = this.sIsElementPresent(locator);
			
			locator = "xpath=("+ convlocator +"//div[contains(@id, '__pr')])@class";
			String priority = this.sGetAttribute(locator);
			if ( priority.equals("ImgPriorityHigh_list") ) {
				item.priority = "high";
			} else {
				// TODO - handle other priorities
			}

			
			locator = convlocator + "//div[contains(@id, '__tg')]";
			// TODO: handle tags

			// Get the From
			locator = convlocator + "//td[contains(@id, '__fr')]";
			item.from = this.sGetText(locator).trim();
			
			// Get the attachment
			locator = "xpath=("+ convlocator +"//div[contains(@id, '__at')])@class";
			String attach = this.sGetAttribute(locator);
			if ( attach.equals("ImgBlank_16") ) {
				item.hasAttachments = false;
			} else {
				// TODO - handle other attachment types
			}
				
			// Get the fragment
			locator = convlocator + "//span[contains(@id, '__fm')]";
			item.fragment = this.sGetText(locator).trim();

			// Get the subject
			locator = convlocator + "//td[contains(@id, '__su')]";
			String s = this.sGetText(locator).trim();
			
			// The subject contains the fragment, e.g. "subject - fragment", so
			// strip it off
			item.subject = s.replace(item.fragment, "").trim();

			// Get the folder
			locator = convlocator + "//nobr[contains(@id, '__fo')]";
			if ( this.sIsElementPresent(locator) ) {
				item.folder = this.sGetText(locator).trim();
			} else {
				item.folder = "";
			}

			// Get the size
			locator = convlocator + "//nobr[contains(@id, '__sz')]";
			if ( this.sIsElementPresent(locator) ) {
				item.size = this.sGetText(locator).trim();
			} else {
				item.size = "";
			}
			
			// Get the received date
			locator = convlocator + "//td[contains(@id, '__dt')]";
			item.received = this.sGetText(locator).trim();
			
			// Add the new item to the list
			items.add(item);
			logger.info(item.prettyPrint());
		}
		
		// Return the list of items
		return (items);
	}




	@Override
	public AbsSeleniumObject zListItem(Action action, String subject) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ subject +")");
		
		AbsSeleniumObject page = null;
		
		if ( action == Actions.A_LEFTCLICK ) {
			
			String listLocator;
			String rowLocator;
			if (zGetPropMailView() == PageMailView.BY_MESSAGE) {
				listLocator = "//div[@id='zl__TV__rows']";
				rowLocator = "//div[contains(@id, 'zli__TV__')]";
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

			StringBuilder sb = new StringBuilder();
			
			// Get each conversation's data from the table list
			for (int i = 1; i <= count; i++) {
				
				String itemlocator = listLocator + "/div["+ i +"]";
				String locator;
				
				// Look for the subject
				
				// Subject - Fragment
				locator = itemlocator + "//td[contains(@id, '__su')]";
				String s = this.sGetText(locator).trim();
				sb.append(s).append(", ");
				
				if ( !s.contains(subject) ) {
					continue;	// No match
				}

				// The subject matched!
				// Left-Click on the item
				this.zClick(itemlocator);
				// this.sClick(convlocator);
				
				// No page to return
				return (new DisplayMail(MyApplication));
			}
			
			throw new HarnessException("Unable to locate item with subject("+ subject +") in ("+ sb.toString() +")");

		} else if ( action == Actions.A_CTRLSELECT ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else if ( action == Actions.A_SHIFTSELECT ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else if ( action == Actions.A_RIGHTCLICK ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else if ( action == Actions.A_MAIL_CHECKBOX ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else if ( action == Actions.A_MAIL_EXPANDCONVERSATION ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else if ( action == Actions.A_MAIL_FLAG ) {
			
			throw new HarnessException("implement me!  action = "+ action);
			
		} else {
			throw new HarnessException("implement me!  action = "+ action);
		}
		
		// TODO: once more actions are implemented, may need to enable this
		// default return command
		// return (page);
	}

	@Override
	public AbsSeleniumObject zListItem(Action action, Action option, String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Get the Reading Pane object
	 * @return
	 */
	public DisplayMail zGetReadingPane() {
		
		// TODO: check if something is displayed in the reading pane?
		
		return (new DisplayMail(this.MyApplication));
		
	}




}
