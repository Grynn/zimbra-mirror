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
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail;
import com.zimbra.qa.selenium.projects.ajax.ui.search.PageAdvancedSearch;

public class PageAddressbook extends AbsTab {

	// image class name for contact and group
	private static final String GROUP_IMAGE   = "ImgGroup";
	private static final String CONTACT_IMAGE = "ImgContact";
	
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
			
		public static final ContextMenuItem CONTACT_SUB_RECEIVED_FROM_CONTACT = new ContextMenuItem("POPUP_SEARCH","Received From Contact","div[class='ImgSearch']","");
	    public static final ContextMenuItem CONTACT_SUB_SENT_TO_CONTACT = new ContextMenuItem("POPUP_SEARCH_TO","Sent To Contact","div[class='ImgSearch']","");
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

		tracer.trace("Navigate to "+ this.myPageName());

		if (!GeneralUtility.waitForElementPresent(this,PageMain.Locators.zAppbarContact))  {
			throw new HarnessException("Can't locate addressbook icon");
		}

		
		// Click on Addressbook icon
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
			String commonLocator = "//div[@id='zv__CNS']/div["+ i +"]";
			String contactType = getContactType(commonLocator);
		    
			ContactItem ci=null;
			String contactDisplayedLocator = commonLocator + "/table/tbody/tr/td[3]";
			String fileAs = ClientSessionFactory.session().selenium().getText(contactDisplayedLocator);
			
			//check if it is a contactgroup or a contactgroup item
			if ( contactType.equals(GROUP_IMAGE)) {
                ci=new ContactGroupItem(fileAs);
			}
			else if (  contactType.equals(CONTACT_IMAGE) ) {
				ci=new ContactItem(fileAs);		    			
			}
			else {
				throw new HarnessException("Image not neither conntact group nor contact.");		
			}
			
			list.add(ci);	    	      
		}


		return list;		
	}

				
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton("+ button +")");

		tracer.trace("Press the "+ button +" button");

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
			ArrayList<String> selectedContactArrayList=getSelectedContactLocator();
			
			if (selectedContactArrayList.size() == 0) {
				throw new HarnessException("No selected contact/contact group ");				
			}
			
			if (selectedContactArrayList.size() > 1) {
				throw new HarnessException("Cannot edit more than one contact/contact group ");				
			}
			
		    String contactType = getContactType(selectedContactArrayList.get(0));
			
		    //check if it is a contactgroup or a contactgroup item
			if ( contactType.equals(GROUP_IMAGE)) {
				page = new FormContactGroupNew(MyApplication);		
			}
			else if (  contactType.equals(CONTACT_IMAGE) ) {
				page = new FormContactNew(MyApplication);
			}
			
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

		tracer.trace("Click pulldown "+ pulldown +" then "+ option);

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
			
			zWaitForBusyOverlay();
			// FALL THROUGH

		 } else {
			throw new HarnessException("no logic defined for pulldown/option "+ pulldown +"/"+ option);
		 }
	    }		
	   else if ( pulldown == Button.B_NEW ) {
		   if ( option == Button.O_NEW_CONTACT ) {
			    pulldownLocator = "css=div[id='zb__CNS__NEW_MENU'] td[id='zb__CNS__NEW_MENU_dropdown']";
			    optionLocator="css=tr[id='POPUP_NEW_CONTACT']";
				page = new FormContactNew(this.MyApplication);		   
		   }
		   if ( option == Button.O_NEW_CONTACTGROUP) {
			    pulldownLocator = "css=div[id='zb__CNS__NEW_MENU'] td[id='zb__CNS__NEW_MENU_dropdown']";
			    optionLocator="css=tr[id='POPUP_NEW_GROUP']";
				page = new FormContactGroupNew(this.MyApplication);		   
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

	// return the type of a contact
	private String getContactType(String locator) {
		String imageLocator = "xpath=(" + locator +"/table/tbody/tr/td[2]/center/div)@class";
        String attrs = sGetAttribute(imageLocator);
		
		//check if it is a contactgroup or a contactgroup item
		if ( attrs.contains(GROUP_IMAGE) ) {
			return GROUP_IMAGE;
		}	
		else if ( attrs.contains(CONTACT_IMAGE) ) {
			return CONTACT_IMAGE;
		}
		
		return null;
	}
	
    // return the xpath locator of a contact
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
	
    //get selected contacts locators
	private ArrayList<String> getSelectedContactLocator() throws HarnessException {
        
		
		String listLocator = "//div[@id='zv__CNS']";
		String rowLocator = "//div[contains(@id, 'zli__CNS__')]";
		
        ArrayList<String> arrayList = new ArrayList<String>();
		
		if ( !sIsElementPresent(listLocator) )
			throw new HarnessException("List View Rows is not present "+ listLocator);

		if ( !sIsElementPresent(rowLocator) )
		    return arrayList; //an empty arraylist
			
		//Get the number of contacts (String) 
		int count = sGetXpathCount(listLocator + rowLocator);
		logger.debug(myPageName() + " getSelectedContactLocator: number of contacts: "+ count);

		if ( count == 0 )
			throw new HarnessException("List count was zero");

		// Get each contact's data from the table list
		for (int i = 1; i <= count; i++) {

			String itemLocator = listLocator + rowLocator + "[" + i +"]";

			if ( !sIsElementPresent(itemLocator) ) {
				throw new HarnessException("unable to locate item " + itemLocator);
			}

			if (sGetAttribute("xpath=(" +itemLocator+ ")@class").contains("Row-selected")) {
			    arrayList.add(itemLocator);
			}

			// Log this item to the debug output
			LogManager.getLogger("projects").info("getSelectedContactLocator: found selected contact "+ itemLocator);
     		
		} 
			
		return arrayList;
	}
	

	
	public AbsPage zListItem(Action action, Button option ,Button subOption, String contact) throws HarnessException {
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		String id = null;
        String contactLocator = getContactLocator(contact);
        
		tracer.trace(action +" then "+ option +" then "+ subOption +" on contact = "+ contact);

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
		    		page = ((AppAjaxClient)MyApplication).zPageMail;
				}
			
				else if (subOption == Button.O_SEARCH_MAIL_RECEIVED_FROM_CONTACT) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_RECEIVED_FROM_CONTACT;
					page = ((AppAjaxClient)MyApplication).zPageMail;
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
        
		tracer.trace(action +" then "+ option +" on contact = "+ contact);

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
        
		tracer.trace(action +" on contact = "+ contact);

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

}
