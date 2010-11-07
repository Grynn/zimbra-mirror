/**
 * 
 */
package projects.mobile.ui;

import java.util.List;

import projects.ajax.ui.AbsAjaxPage.ItemType;
import framework.items.ContactItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.util.HarnessException;

/**
 * @author Matt Rhoades
 *
 */
public class PageContacts extends AbsMobilePage {

	public static class Locators {
	
		// TODO: Need better locator that doesn't have content text
		public static final String zContactsIsActive = "xpath=//a[contains(.,'Address Books')]";
	
		// TODO: Need better locator that doesn't have content text
		public static final String zNewContact = "xpath=//a[contains(.,'Add')]";
	
	}
	
	public PageContacts(AbsApplication application) {
		super(application);
		
		logger.info("new " + PageContacts.class.getCanonicalName());

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
	public void navigateTo() throws HarnessException {

		// Check if this page is already active.
		if ( isActive() ) {
			return;
		}
		
		// Make sure we are logged into the Mobile app
		if ( !MyApplication.zPageMain.isActive() ) {
			MyApplication.zPageMain.navigateTo();
		}
		
		// Click on Contact icon
		sClick(PageMain.Locators.zAppbarContact);
		
		waitForActive();
		
		
	}

	/**
	 * Open a "new contact" form
	 * @return
	 */
	public AbsForm zToolbarNew(ItemType type) throws HarnessException {
		logger.debug(myPageName() + " zToolbarNew "+ type);
		
		if ( !isActive() ) {
			throw new HarnessException("Contacts page is not active");
		}
		
		// Click on "Add"
		if ( !this.sIsElementPresent(Locators.zNewContact) ) {
			throw new HarnessException("'Add' contact button is not present");
		}
		this.sClick(Locators.zNewContact);
		
		FormContactNew form = new FormContactNew(this.MyApplication);
		
		// Maybe need to check if the form is opened correctly?
		
		return (form);
		
	}


	/**
	 * Return a list of all contacts in the current view
	 * @return
	 * @throws HarnessException 
	 */
	public List<ContactItem> getContactList() throws HarnessException {
		
		throw new HarnessException("implement me!");

	}


	/**
	 * Refresh to sync new server changes
	 * @throws HarnessException 
	 */
	public void refresh() throws HarnessException {
		this.sClick(PageMain.Locators.zAppbarContact);
	}


}
