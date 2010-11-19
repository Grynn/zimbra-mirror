/**
 * 
 */
package projects.mobile.ui;

import java.util.List;

import framework.items.ContactItem;
import framework.ui.AbsApplication;
import framework.ui.AbsForm;
import framework.ui.Button;
import framework.ui.Button;
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
	public AbsForm zToolbarPressButton(Button button) throws HarnessException {
		logger.debug(myPageName() + " zToolbarPressButton("+ button +")");
		
		if ( !isActive() ) {
			throw new HarnessException("Contacts page is not active");
		}
		
		String locator = null;
		AbsForm form = null;
		
		if ( button == Button.B_NEW ) {
			
			locator = Locators.zNewContact;
			form = new FormContactNew(this.MyApplication);
			
		} else {
			
			throw new HarnessException("zToolbarPressButton() not defined for button "+ button);
			
		}
		
		if ( locator == null ) {
			throw new HarnessException("zToolbarPressButton() no locator defined for button "+ button);
		}
		
		// Default behavior
		
		// Click on "Add"
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("locator is not present " + locator);
		}
		this.sClick(locator);
						
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
