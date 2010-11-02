/**
 * 
 */
package projects.ajax.ui;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import framework.items.ConversationItem;
import framework.items.MailItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsAjaxPage {

	public static final String lCLVrows = "id=zl__CLV__rows";

	public static final String lGetMailIconBtn = "id=zb__CLV__CHECK_MAIL_left_icon";
	public static final String lGetMailBtn = "id=zb__CLV__CHECK_MAIL";

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

	/**
	 * Open a new item by clicking in the New menu
	 * @param type "Mail", "Contact", "Appointment", "Task", etc.
	 * @return the corresponding form object
	 * @throws HarnessException on error
	 */
	public AbsForm zToolbarNew(ItemType type) throws HarnessException {
		logger.debug(myPageName() + " zToolbarNew "+ type);
		
		// Initialize the return object
		AbsForm form = null;
		
		if ( type == ItemType.Mail ) {

			this.zPressKeyboardShortcut(KeyEvent.VK_N);

			form = new FormMailNew(this.MyApplication);
			
		} else {
			throw new HarnessException("implement me with option "+ type +"!");
		}
		
		return (form);
	}
	
	public void zToolbarGetMail() throws HarnessException {
		logger.debug(myPageName() + " zToolbarGetMail");
		
		// Make sure the button exists
		if ( !this.sIsElementPresent(lGetMailIconBtn) )
			throw new HarnessException("Get Mail Button is not present "+ lGetMailIconBtn);
		
		// Click it
		this.zClick(lGetMailIconBtn);
	}
	
	public void zToolbarDelete() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	/**
	 * Probably needs to return some sort of abstract dialog object
	 * @throws HarnessException
	 */
	public void zToolbarMove() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public void zToolbarPrint() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public void zToolbarReply() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public void zToolbarReplyAll() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	

	public void zToolbarForward() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	public void zToolbarReportSpam() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	/**
	 * Need to implement ItemTag
	 * @param tag
	 * @throws HarnessException
	 */
	public void zToolbarTag(Object tag) throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	/**
	 * Probably needs to return the separate browser window object
	 * @throws HarnessException
	 */
	public void zToolbarOpenInSeparateWindow() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	/**
	 * Need to define view enum
	 * @param tag
	 * @throws HarnessException
	 */
	public void zToolbarChangeView(Object view) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	

	/**
	 * Return a list of all messages in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<MailItem> getMessageList() throws HarnessException {
		SleepUtil.sleepMedium();
		throw new HarnessException("implement me!");

	}

	/**
	 * Return a list of all conversations in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<ConversationItem> getConversationList() throws HarnessException {
		logger.debug(myPageName() + " getConversationList");
		
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

			// Get the fragment
			locator = convlocator + "//span[contains(@id, '__fm')]";
			item.fragment = this.sGetText(locator).trim();

			// Get the subject
			locator = "//div[@id='zl__CLV__rows']/div["+ count +"]//td[contains(@id, '__su')]";
			String s = this.sGetText(locator).trim();
			
			// The subject contains the fragment, e.g. "subject - fragment", so
			// strip it off
			item.subject = s.replace(item.fragment, "").trim();
			
			// TODO: Folder: <nobr id='zlif__CLV__XXX__fo'/>
			// TODO: Count: __sz
			// TODO: Date: __dt
			
			// Add the new item to the list
			items.add(item);
		}
		
		// Return the list of items
		return (items);
	}



}
