package  com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;

import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.ui.Shortcut;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


public class PageAddressbook extends AbsTab {




	public PageAddressbook(AbsApplication application) {
		super(application);		
		logger.info("new " + PageAddressbook.class.getCanonicalName());

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

		//make sure Addressbook  tab is selected		
		String attrs = sGetAttribute("xpath=(//div[@id='zb__App__Contacts'])@class");		

		boolean active=attrs.contains("ZSelected");

		//make sure Addressbook folder is displayed
		String locator = "xpath=//div[@id='ztih__main_Contacts__ADDRBOOK_div']";

		active &= this.sIsElementPresent(locator);

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


		if ( !((AppAjaxClient)MyApplication).zPageMain.zIsActive() ) {
			((AppAjaxClient)MyApplication).zPageMain.zNavigateTo();
		}

		// Click on Addressbook icon
		if ( !sIsElementPresent(PageMain.Locators.zAppbarContact) ) {
			throw new HarnessException("Can't locate addressbook icon");
		}

		zClick(PageMain.Locators.zAppbarContact);


		zWaitForActive();


		//ClientSessionFactory.session().selenium().click(PageMain.Locators.zAppbarContact);
		//zClick(PageMain.Locators.zAppbarContact);
		//SleepUtil.sleepMedium();

	}
	public List<ContactItem> zListGetContacts() throws HarnessException {

		List <ContactItem> list= new ArrayList<ContactItem>();

		//ensure it is in Addressbook main page
		zNavigateTo();
		if ( !this.sIsElementPresent("//div[@id='zv__CNS']") )
			//maybe return empty list?????
			throw new HarnessException("Contact List is not present "+ "//div[@id='zv__CNS']");

		//Get the number of contacts (String) 
		int count = this.sGetXpathCount("//div[@id='zv__CNS']//div[contains(@id, 'zli__CNS__')]");
		logger.debug(myPageName() + " zListGetContacts: number of contacts: "+ count);

		// Get each contact's data from the table list
		for (int i = 1; i <= count; i++) {
			String contactDisplayedLocator = "//div[@id='zv__CNS']/div["+ i +"]/table/tbody/tr/td[3]";

			ContactItem ci=new ContactItem(ClientSessionFactory.session().selenium().getText(contactDisplayedLocator));		    			
			list.add(ci);	    	      
		}


		return list;		
	}

		
	//TODO: subContextMenuItem
	//right click contactItem
	public AbsPage zContextMenu(ContextMenuItem cmi) throws HarnessException {
		logger.info(myPageName() + " zContextMenu"+ " (" + cmi.text  + ")");
	
		//ensure only contacts' context menu items 
	    if (! ( cmi == ContextMenuItem.C_CONTACT_SEARCH || 	    		
	    		cmi == ContextMenuItem.C_CONTACT_ADVANCED_SEARCH ||
	    		cmi == ContextMenuItem.C_CONTACT_DELETE ||
	    		cmi == ContextMenuItem.C_CONTACT_EDIT ||
	    		cmi == ContextMenuItem.C_CONTACT_FORWARD ||
	    		cmi == ContextMenuItem.C_CONTACT_MOVE ||
	    		cmi == ContextMenuItem.C_CONTACT_NEW_EMAIL ||
	    		cmi == ContextMenuItem.C_CONTACT_PRINT ||
	    		cmi == ContextMenuItem.C_CONTACT_TAG ||
	    		cmi == ContextMenuItem.C_SEPARATOR 
	         )){
	    	throw new HarnessException("Not allow to call with non-contact contex-menu item "+ cmi.text );
	    }
	    	
		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		String id = cmi.locator;

		// Check if the item is enabled
		String attrs = sGetAttribute("xpath=(//div[@id='"+ id +"'])@class");
		if ( attrs.contains("ZDisabled") ) {
			throw new HarnessException("Tried clicking on "+ cmi.text +" but it was disabled "+ attrs);
		}

		locator = "id="+ id;

		if (cmi == ContextMenuItem.C_CONTACT_MOVE) {				
			page = new DialogContactMove(MyApplication);
					
		}
		else if (cmi == ContextMenuItem.C_CONTACT_EDIT) {				
			page = new FormContactNew(MyApplication);								
		}
	    // TODO other options
		
		
		if ( locator == null )
			throw new HarnessException("locator was null for context menu "+ cmi.text);

		// Default behavior, process the locator by clicking on it
		//

		// Make sure the context menu exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("contextmenu is not present locator="+ locator +" context menu item="+ cmi.text);

		// Click it
		this.zClick(locator);
        SleepUtil.sleepSmall();
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		return (page);
	} 
	
	
	// click folderItem
	public AbsPage zContextMenu(FolderItem folderItem, ContextMenuItem cmi ) throws HarnessException {
		logger.info(myPageName() + " zContextMenu"+ " ");
		
		return null;
	}
		
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		if ( button == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned

		if ( button == Button.B_NEW ) {

			// For "NEW" without a specified pulldown option, just return the default item
			// To use "NEW" with a pulldown option, see  zToolbarPressPulldown(Button, Button)

			locator = "//div[@id='ztb__CNS']//td[@id='zb__CNS__NEW_MENU_title']";
			page = new FormContactNew(this.MyApplication);

			// FALL THROUGH

		} else if ( button == Button.B_DELETE ) {

			String id = "zb__CNS__DELETE_left_icon";

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ id +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id="+ id;

		} else if ( button == Button.B_EDIT ) {

			String id = "zb__CNS__EDIT_left_icon";

			// Check if the button is enabled
			String attrs = sGetAttribute("xpath=(//td[@id='"+ id +"']/div)@class");
			if ( attrs.contains("ZDisabledImage") ) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
			}

			locator = "id="+ id;
			page = new FormContactNew(MyApplication);
		
	    } else if ( button == Button.B_MOVE) {

		    String id = "zb__CNS__MOVE_left_icon";

		    // Check if the button is enabled
		    String attrs = sGetAttribute("xpath=(//td[@id='"+ id +"']/div)@class");
		    if ( attrs.contains("ZDisabledImage") ) {
			  throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ attrs);
		    }

		   locator = "id="+ id;
		   page = new DialogContactMove(MyApplication);
	    }


		if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		// Default behavior, process the locator by clicking on it
		//

		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);

		// Click it
		this.zClick(locator);
		SleepUtil.sleepSmall();
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option +")");

		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");


		// Default behavior variables
		//
		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
	   if ( pulldown == Button.B_TAG ) {
		
		 if ( option == Button.O_TAG_NEWTAG ) {

		    pulldownLocator = "css=td[id$='__TAG_MENU_dropdown'] div[class='ImgSelectPullDownArrow']";
    		optionLocator = "css=div[id$='__TAG_MENU|MENU|NEWTAG']";
			page = new DialogTag(this.MyApplication);

			
		 } else if ( option == Button.O_TAG_REMOVETAG ) {

			zKeyboard.zTypeCharacters(Shortcut.S_MAIL_REMOVETAG.getKeys());
					
			pulldownLocator = null;	
			optionLocator = null;
			page = null;

			// FALL THROUGH

		 } else {
			throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
		 }
	    }		
	
	// Default behavior
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !this.sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}
			
			this.zClick(pulldownLocator);
			SleepUtil.sleepSmall();
			
			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
				
				this.zClick(optionLocator);
				SleepUtil.sleepSmall();

			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
				page.zWaitForActive();
			}
			
		}
	    return page;
	}

	@Override
	public AbsPage zListItem(Action action, Action option, String subject) throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Override
	public AbsPage zListItem(Action action, String contact) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ contact +")");

		AbsPage page = null;

		if (( action == Action.A_LEFTCLICK ) || ( action == Action.A_RIGHTCLICK )){

			String listLocator = "//div[@id='zv__CNS']";
			String rowLocator = "//div[contains(@id, 'zli__CNS__')]";

			if ( !this.sIsElementPresent(listLocator) )
				throw new HarnessException("List View Rows is not present "+ listLocator);

			if ( !this.sIsElementPresent(rowLocator) )
				throw new HarnessException("List does not contain any items "+ rowLocator);

			//Get the number of contacts (String) 
			int count = this.sGetXpathCount(listLocator + rowLocator);
			logger.debug(myPageName() + " zListItem: number of contacts: "+ count);

			if ( count == 0 )
				throw new HarnessException("List count was zero");

			// Get each contact's data from the table list
			for (int i = 1; i <= count; i++) {

				String itemLocator = listLocator + rowLocator + "[" + i +"]";

				if ( !this.sIsElementPresent(itemLocator) ) {
					throw new HarnessException("unable to locate item " + itemLocator);
				}

				String contactDisplayedLocator = itemLocator + "//td[3]";
				String displayAs = this.sGetText(contactDisplayedLocator);

				// Log this item to the debug output
				LogManager.getLogger("projects").info("zListItem: found contact "+ displayAs);

				if ( !contact.equals(displayAs) )
					continue;

				//click
				this.zClick(contactDisplayedLocator);
				SleepUtil.sleepSmall();

				// right-click
				if ( action == Action.A_RIGHTCLICK ) {
					zKeyboard.zTypeCharacters(Shortcut.S_RIGHTCLICK.getKeys());															
				 				
					//return a context menu
					return (new ContextMenu(MyApplication));
				
				}
				// All done
				return (new DisplayContact(MyApplication));

			}

			throw new HarnessException("Never found the contact "+ contact);


		}
		
		else {
			throw new HarnessException("implement me!");
		}

		// return (new DisplayContact(MyApplication));

	}


}
