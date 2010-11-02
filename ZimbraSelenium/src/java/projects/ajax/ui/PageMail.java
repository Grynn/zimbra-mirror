/**
 * 
 */
package projects.ajax.ui;

import java.awt.event.KeyEvent;
import java.util.List;

import framework.items.ConversationItem;
import framework.items.MailItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsAjaxPage {


	
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
	public AbsForm zMenuNew(String type) throws HarnessException {
		// TODO: Don't use String for type, set an enum
		
		// Initialize the return object
		AbsForm form = null;
		
		if ( type.equalsIgnoreCase("mail") ) {

			this.zPressKeyboardShortcut(KeyEvent.VK_N);

			form = new FormMailNew(this.MyApplication);
			
		} else {
			throw new HarnessException("implement me with option "+ type +"!");
		}
		
		return (form);
	}
	
	/**
	 * Refresh the inbox list by clicking "Get Mail"
	 * @throws HarnessException 
	 */
	public void getMail() throws HarnessException {
		this.sClick(PageMain.appbarMail);
	}


	/**
	 * Return a list of all messages in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<MailItem> getMessageList() throws HarnessException {
		
		throw new HarnessException("implement me!");

	}

	/**
	 * Return a list of all conversations in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<ConversationItem> getConversationList() throws HarnessException {
		
		throw new HarnessException("implement me!");

	}



}
