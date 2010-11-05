/**
 * 
 */
package projects.ajax.ui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

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

	
	
	// Locators
	public static final String lCLVrows			= "id=zl__CLV__rows";

	public static final String lGetMailIconBtn	= "id=zb__CLV__CHECK_MAIL_left_icon";
	public static final String lGetMailBtn		= "id=zb__CLV__CHECK_MAIL";

	public static final String lDeleteBtn		= "id=zb__CLV__DELETE";

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
		sClick(PageMain.appbarMail);
		
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
			this.zPressKeyboardShortcut(KeyEvent.VK_N);
			page = new FormMailNew(this.MyApplication);
			
		} else if ( button == Buttons.B_GETMAIL ) {
			
			locator = lGetMailIconBtn;
			
		} else if ( button == Buttons.B_DELETE ) {
			
			locator = lDeleteBtn;
			
		} else if ( button == Buttons.B_MOVE ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_PRINT ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_REPLY ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_REPLYALL ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_FORWARD ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_RESPORTSPAM ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_TAG ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_NEWWINDOW ) {
			
			throw new HarnessException("implement me!");
			
		} else if ( button == Buttons.B_LISTVIEW ) {
			
			throw new HarnessException("implement me!");
			
		}

		if ( locator == null ) {
			throw new HarnessException("no logic defined for button "+ button);
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


	


	

	/**
	 * Return a list of all messages in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<MailItem> zListGetMessages() throws HarnessException {
		SleepUtil.sleepMedium();
		throw new HarnessException("implement me!");

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
		if ( !this.sIsElementPresent(lCLVrows) )
			throw new HarnessException("Conversation List View Rows is not present "+ lCLVrows);
		
		// How many items are in the table?
		int count = this.sGetXpathCount("//div[@id='zl__CLV__rows']//div[contains(@id, 'zli__CLV__')]");
		logger.debug(myPageName() + " getConversationList: number of conversations: "+ count);

		// Get each conversation's data from the table list
		for (int i = 0; i < count; i++) {
			final String convlocator = "//div[@id='zl__CLV__rows']/div["+ count +"]";
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
			locator = "//div[@id='zl__CLV__rows']/div["+ count +"]//td[contains(@id, '__su')]";
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

	/**
	 * Expand a conversation
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListExpandConversation(String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Select (left-click) a conversation/message
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListSelectItem(String subject) throws HarnessException {
		logger.info(myPageName() + " zListSelectItem("+ subject +")");
				
		// TODO: how to handle both messages and conversations, maybe check the view first?
		if ( !this.sIsElementPresent(lCLVrows) )
			throw new HarnessException("Conversation List View Rows is not present "+ lCLVrows);
		
		// How many items are in the table?
		int count = this.sGetXpathCount("//div[@id='zl__CLV__rows']//div[contains(@id, 'zli__CLV__')]");
		logger.debug(myPageName() + " zListSelectItem: number of conversations: "+ count);

		StringBuilder sb = new StringBuilder();
		
		// Get each conversation's data from the table list
		for (int i = 0; i < count; i++) {
			
			final String convlocator = "//div[@id='zl__CLV__rows']/div["+ count +"]";
			String locator;
			
			// Look for the subject
			
			// Subject - Fragment
			locator = "//div[@id='zl__CLV__rows']/div["+ count +"]//td[contains(@id, '__su')]";
			String s = this.sGetText(locator).trim();
			sb.append(s).append(", ");
			
			if ( !s.contains(subject) ) {
				continue;	// No match
			}

			// The subject matched!
			// Left-Click on the item
			this.sClick(convlocator);
			
			// Done!
			return;
			
		}

		// Failed!
		throw new HarnessException("Unable to locate item with subject("+ subject +" in ("+ sb.toString() +")");
	}

	/**
	 * Select (shift-left-click) a conversation/message
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListShiftSelectItem(String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Select (ctrl-left-click) a conversation/message
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListCtrlSelectItem(String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Right click on a conversation/message
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListRightClickItem(String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Right click on a conversation/message and select option
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListRightClickItem(String subject, String option) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Click on the checkbox next to a conversation/message
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListCheckItem(String subject) throws HarnessException {
		// TODO: should this method just toggle it?
		throw new HarnessException("implement me!");
	}

	/**
	 * Click on the flag next to a conversation/message
	 * @param subject
	 * @throws HarnessException
	 */
	public void zListFlagItem(String subject) throws HarnessException {
		// TODO: should this method just toggle it?
		throw new HarnessException("implement me!");
	}



}
