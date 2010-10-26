/**
 * 
 */
package projects.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import framework.items.ConversationItem;
import framework.items.MailItem;
import framework.ui.AbsApplication;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageMail extends AbsMobilePage {

	// TODO: Need better locator that doesn't have content text
	public static final String lMailIsActive = "xpath=//a[contains(.,'Folders')]";

	public static final String DList_View = "xpath=//div[@id='dlist-view']";
	public static final String DList_View_2 = "//div[@id='dlist-view']/div";
	
	public PageMail(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageMail.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean isActive() throws HarnessException {

		boolean active = this.sIsElementPresent(lMailIsActive);
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
		List<ConversationItem> conversations = new ArrayList<ConversationItem>();
		
		if (!sIsElementPresent(DList_View))
			throw new HarnessException("Unable to find the message list!");
		
		int count = sGetXpathCount("//div[contains(@id, 'conv')]");
		logger.info(count + " conversations found");

		// TODO: get all current conversations, create them in
		// ConversationItem objects, and add the to ArrayList
		
		return (conversations);
	}


	/**
	 * Refresh the inbox list by clicking "Get Mail"
	 * @throws HarnessException 
	 */
	public void getMail() throws HarnessException {
		this.sClick(PageMain.appbarMail);
	}


}
