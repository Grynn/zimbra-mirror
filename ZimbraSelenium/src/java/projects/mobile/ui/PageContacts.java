/**
 * 
 */
package projects.mobile.ui;

import java.util.ArrayList;
import java.util.List;

import framework.items.ContactItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * @author Matt Rhoades
 *
 */
public class PageContacts extends AbsMobilePage {

	public static class Locators {
	
		// TODO: Need better locator that doesn't have content text
		public static final String zContactsIsActive = "xpath=//a[contains(.,'Address Books')]";
	
		// TODO: Need better locator that doesn't have content text
		public static final String zNewContact = "//span//a[contains(.,'Add')]";
	}
	
	public PageContacts(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageContacts.class.getCanonicalName());

	}

	/* (non-Javadoc)
	 * @see projects.admin.ui.AbsPage#isActive()
	 */
	@Override
	public boolean zIsActive() throws HarnessException {

		// Make sure the main page is active
		if ( !this.MyApplication.zPageMain.zIsActive() ) {
			this.MyApplication.zPageMain.zNavigateTo();
		}

		boolean active = this.sIsElementPresent(Locators.zContactsIsActive);
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
		if ( !MyApplication.zPageMain.zIsActive() ) {
			MyApplication.zPageMain.zNavigateTo();
		}
		
		// Click on Contact icon
		sClick(PageMain.Locators.zAppbarContact);
		
		zWaitForActive();
		
		
	}

	/**
	 * Open a "new contact" form
	 * @return
	 */
	public AbsForm zToolbarPressButton(Button button) throws HarnessException {
		logger.debug(myPageName() + " zToolbarPressButton("+ button +")");
		
		if ( !zIsActive() ) {
			throw new HarnessException("Contacts page is not active");
		}
		
		int delay = 0;
		String locator = null;
		AbsForm form = null;
		
		if ( button == Button.B_NEW ) {
			
			locator = Locators.zNewContact;
			form = new FormContactNew(this.MyApplication);
			delay = 3000;
			
		} else {
			
			throw new HarnessException("zToolbarPressButton() not defined for button "+ button);
			
		}
		
		if ( locator == null ) {
			throw new HarnessException("zToolbarPressButton() no locator defined for button "+ button);
		}
		
		// Default behavior
		
		// Click on "locator"
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("locator is not present " + locator);
		}
		this.sClick(locator);
		
		// Sleep if specified
		if ( delay > 0 ) {
			SleepUtil.sleep(delay);
		}
						
		return (form);
		
	}


	/**
	 * Return a list of all contacts in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<ContactItem> zListGetContacts() throws HarnessException {
		
		List<ContactItem> items = new ArrayList<ContactItem>();
		
		
		// How many items are in the table?
		int count = this.sGetXpathCount("//div[@id='body']//div[contains(@class, 'list-row')]");
		logger.debug(myPageName() + " zListGetContacts: number of contacts: "+ count);
		SleepUtil.sleepLong();
		
		// Get each conversation's data from the table list
		for (int i = 1; i <= count; i++) {
			
			final String contactLocator = "//div[contains(@id, 'conv')]["+ i +"]";
			
			if ( !this.sIsElementPresent(contactLocator) ) {
				throw new HarnessException("Can't find contact row from locator "+ contactLocator);
			}

			String locator;
			
			ContactItem item = new ContactItem();

			// TODO: Is it checked?

			// TODO: Contact icon
			
			// TODO: Displayed name
			locator = contactLocator + "//span[@class='td m']//a/div/strong";
			if ( this.sIsElementPresent(locator) ) {
				item.gDisplayName = this.sGetText(locator);
			} else {
				item.gDisplayName = "";
			}

			// TODO: email address
			locator = contactLocator + "//span[@class='td m']//div[@class='Email']";
			if ( this.sIsElementPresent(locator) ) {
				item.gEmail = this.sGetText(locator);
			} else {
				item.gEmail = "";
			}

			
			// Add the new item to the list
			items.add(item);
			logger.info(item.prettyPrint());
		}
		
		// Return the list of items
		return (items);
	}


	/**
	 * Refresh to sync new server changes
	 * @throws HarnessException 
	 */
	public void zRefresh() throws HarnessException {
		this.sClick(PageMain.Locators.zAppbarMail);
		SleepUtil.sleepMedium();

		this.sClick(PageMain.Locators.zAppbarContact);
		SleepUtil.sleepMedium();
	}


}
