package  com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.PageMain;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail;
import com.zimbra.qa.selenium.projects.ajax.ui.search.PageAdvancedSearch;

public class PageAddressbook extends AbsTab {

	public static class CONTEXT_MENU {
		public static final String LOCATOR		= "id='zm__Contacts'";
		
		//contact's context menu	
		public static final ContextMenuItem CONTACT_SEARCH = ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP ?
		      new ContextMenuItem("zmi__Contacts__SEARCH","Find Emails From Contact","div[class='ImgSearch']"," div") :             
		      new ContextMenuItem("zmi__Contacts__SEARCH_MENU","Find Emails...","div[class='ImgSearch']"," div[class='ImgCascade']");	
		public static final ContextMenuItem CONTACT_ADVANCED_SEARCH = new ContextMenuItem("zmi__Contacts__BROWSE","Advanced Search","div[class='ImgSearchBuilder']","");	
		public static final ContextMenuItem CONTACT_NEW_EMAIL = new ContextMenuItem("zmi__Contacts__NEW_MESSAGE","New Email","div[class='ImgNewMessage']",":contains('nm')");  	
		public static final ContextMenuItem CONTACT_EDIT = new ContextMenuItem("zmi__Contacts__CONTACT","Edit Contact","div[class='ImgEdit']","");	
		public static final ContextMenuItem CONTACT_FORWARD = new ContextMenuItem("zmi__Contacts__SEND_CONTACTS_IN_EMAIL","Forward Contact","div[class='ImgMsgStatusSent']","");	
		public static final ContextMenuItem CONTACT_TAG = new ContextMenuItem("zmi__Contacts__TAG_MENU","Tag Contact","div[class='ImgTag']"," div[class='ImgCascade']");	
		public static final ContextMenuItem CONTACT_DELETE = new ContextMenuItem("zmi__Contacts__DELETE","Delete","div[class='ImgDelete']",":contains('Del')");
		public static final ContextMenuItem CONTACT_MOVE = new ContextMenuItem("zmi__Contacts__MOVE","Move","div[class='ImgMoveToFolder']","");
		public static final ContextMenuItem CONTACT_PRINT = new ContextMenuItem("zmi__Contacts__PRINT_CONTACT","Print","div[class='ImgPrint']",":contains('p')");
	   		
	}

	public static class CONTEXT_SUB_MENU {
				
		public static final ContextMenuItem CONTACT_SUB_NEW_TAG = new ContextMenuItem("zmi__Contacts__TAG_MENU|MENU|NEWTAG","New Tag","div[class='ImgNewTag']",":contains('nt')");
		public static final ContextMenuItem CONTACT_SUB_REMOVE_TAG = new ContextMenuItem("zmi__Contacts__TAG_MENU|MENU|REMOVETAG","Remove Tag","div[class='ImgDeleteTag']","");
		
		//TODO: Need fixed id for the following:
		public static final ContextMenuItem CONTACT_SUB_RECEIVED_FROM_CONTACT = new ContextMenuItem("zmi__Contacts__BROWSE","Advanced Search","div[class='ImgSearchBuilder']","");
	    public static final ContextMenuItem CONTACT_SUB_SENT_TO_CONTACT = new ContextMenuItem("zmi__Contacts__BROWSE","Advanced Search","div[class='ImgSearchBuilder']","");
	}
	
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

		String locator = null;
		// On Zimbra Desktop, there is no Address book folder, but there is only
		// account root folder
      if(ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
	      locator = TreeMail.Locators.zTreeItems.replace(TreeMail.stringToReplace,
	            AjaxCommonTest.defaultAccountName);
	   } else {
		   //make sure Addressbook folder is displayed
		   locator = "xpath=//div[@id='ztih__main_Contacts__ADDRBOOK_div']";
		}

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

		GeneralUtility.waitForElementPresent(this,
		      PageMain.Locators.zAppbarBriefcase);

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
		   page = new DialogMove(MyApplication, this);
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
		zWaitForBusyOverlay();
	
		
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
			page = new DialogTag(this.MyApplication, this);

			
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
			zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {

				// Make sure the locator exists
				if ( !this.sIsElementPresent(optionLocator) ) {
					throw new HarnessException("Button "+ pulldown +" option "+ option +" optionLocator "+ optionLocator +" not present!");
				}
				
				this.zClick(optionLocator);
				zWaitForBusyOverlay();

			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
				page.zWaitForActive();
			}
			
		}
	    return page;
	}

	private String getContactLocator(String contact) throws HarnessException {
		String listLocator = "//div[@id='zv__CNS']";
		String rowLocator = "//div[contains(@id, 'zli__CNS__')]";
		String contactLocator = null;
		
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

			if ( contact.equals(displayAs) ) {
			   contactLocator = contactDisplayedLocator;
			   break;
			}
     		
		} 
	
		if (contactLocator == null) {
			throw new HarnessException("Never found the contact "+ contact);
		}
		
		return contactLocator;
	}
	
	
	public AbsPage zListItem(Action action, Button option ,Button subOption, String contact) throws HarnessException {
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		String id = null;
        String contactLocator = getContactLocator(contact);
        
        if ( action == Action.A_RIGHTCLICK ) {
			ContextMenuItem cmi=null;
		    ContextMenuItem sub_cmi = null;
		    this.zClick(contactLocator);
		    this.zKeyboard.zTypeCharacters(Shortcut.S_RIGHTCLICK.getKeys());
			//this.zRightClick(contactLocator);
			
			
			if (option == Button.B_TAG) {
		        
				cmi=CONTEXT_MENU.CONTACT_TAG;
													
				if (subOption == Button.O_TAG_NEWTAG) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_NEW_TAG;
					page = new DialogTag(this.MyApplication, this);
				}
				
				else if (subOption == Button.O_TAG_REMOVETAG) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_REMOVE_TAG;
					page = null;	
				}
			}
			else if (option == Button.B_SEARCH) {
				cmi=CONTEXT_MENU.CONTACT_SEARCH;
				
				
				if (subOption == Button.O_SEARCH_MAIL_SENT_TO_CONTACT) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_SENT_TO_CONTACT;
				    //TODO change DisplayMail constructor to public??					
					//page = new DisplayMail(this.MyApplication); 
				}
			
				else if (subOption == Button.O_SEARCH_MAIL_RECEIVED_FROM_CONTACT) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_RECEIVED_FROM_CONTACT;
					//TODO change DisplayMail constructor to public??
					//page = new DisplayMail(this.MyApplication); 
				}
			}
			id = cmi.locator;
			locator = "id="+ id;
			//  Make sure the context menu exists
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("contextmenu is not present locator="+ locator +" context menu item="+ cmi.text);
			
			// Check if the item is enabled
			String attrs = sGetAttribute("xpath=(//div[@id='"+ id +"'])@class");
			if ( attrs.contains("ZDisabled") ) {
				throw new HarnessException("Tried clicking on "+ cmi.text +" but it was disabled "+ attrs);
			}
			

			// Mouse over the option
			sMouseOver(locator);
						
			id = sub_cmi.locator;
			locator = "id="+ id;
		
			
			//  Make sure the sub context menu exists			
			zWaitForElementPresent(locator) ;
			
			// make sure the sub context menu enabled			
			zWaitForElementEnabled(id);
			
	
        }
        
		// Click option
		this.zClick(locator);
		zWaitForBusyOverlay();
		
		
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		return (page);
    
	}
	
	@Override
	public AbsPage zListItem(Action action, Button option, String contact) throws HarnessException {
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		String id = null;
        String contactLocator = getContactLocator(contact);
        
		if ( action == Action.A_RIGHTCLICK ) {
			ContextMenuItem cmi=null;
								
			this.zRightClick(contactLocator);
		
			if (option == Button.B_DELETE){
                cmi=CONTEXT_MENU.CONTACT_DELETE;				
			}
			else if (option == Button.B_MOVE) {
				cmi=CONTEXT_MENU.CONTACT_MOVE;
				page = new DialogMove(MyApplication, this);	
			}
            
			else if (option == Button.B_EDIT) {
				cmi=CONTEXT_MENU.CONTACT_EDIT;
				page = new FormContactNew(MyApplication);	
			}

			else if (option == Button.B_NEW) {
				cmi=CONTEXT_MENU.CONTACT_NEW_EMAIL;
				page = new FormMailNew(MyApplication);	
			}

			else if (option == Button.B_SEARCHADVANCED) {
				cmi=CONTEXT_MENU.CONTACT_ADVANCED_SEARCH;
				page = new PageAdvancedSearch(MyApplication);	
			}
			else if (option == Button.B_PRINT) {
				cmi=CONTEXT_MENU.CONTACT_PRINT;				
				page = new PagePrint(MyApplication);	
			}
			else {
				throw new HarnessException("option " + option + " not supported");
			}
			
			id = cmi.locator;
			locator = "id="+ id;
			//  Make sure the context menu exists
			if ( !this.sIsElementPresent(locator) )
				throw new HarnessException("contextmenu is not present locator="+ locator +" context menu item="+ cmi.text);
			
			// Check if the item is enabled
			String attrs = sGetAttribute("xpath=(//div[@id='"+ id +"'])@class");
			if ( attrs.contains("ZDisabled") ) {
				throw new HarnessException("Tried clicking on "+ cmi.text +" but it was disabled "+ attrs);
			}

			
		}
		
				
		// Click option
		this.zClick(locator);
		zWaitForBusyOverlay();
		
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		return (page);

		
	}

	@Override
	public AbsPage zListItem(Action action, String contact) throws HarnessException {
		logger.info(myPageName() + " zListItem("+ action +", "+ contact +")");
        String contactLocator=getContactLocator(contact);
        
		if ( action == Action.A_LEFTCLICK ) {
			//click
			this.zClick(contactLocator);
			zWaitForBusyOverlay();
			return (new DisplayContact(MyApplication)); 
			
		}
		else if (action == Action.A_RIGHTCLICK ) {
			
            this.zRightClick(contactLocator); 
            zWaitForBusyOverlay();
    		return (new ContextMenu(MyApplication));			
		}
			
		
		throw new HarnessException("action not supported ");
	
	}

	/**
	 * Sync Desktop to ZCS through SOAP and wait for spinner to disappear
	 * @throws HarnessException
	 */
	public void zSyncDesktopToZcs() throws HarnessException {
	   if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
         GeneralUtility.syncDesktopToZcsWithSoap(MyApplication.zGetActiveAccount());

         // Wait for the spinner image
         if (GeneralUtility.waitForElementPresent(this,
               PageMail.Locators.zLoadingImage_Desktop, 5000)) {
            Object[] params = {PageMail.Locators.zLoadingImage_Desktop};
            GeneralUtility.waitFor(null, this, false, "sIsElementPresent",
                  params, WAIT_FOR_OPERAND.EQ, false, 30000, 1000);
         }
	   }
	}
}
