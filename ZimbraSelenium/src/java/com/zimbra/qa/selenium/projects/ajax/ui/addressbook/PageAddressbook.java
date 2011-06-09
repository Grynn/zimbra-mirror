package  com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.PageMain;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;

import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.DialogCreateFolder;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail;
import com.zimbra.qa.selenium.projects.ajax.ui.search.PageAdvancedSearch;

public class PageAddressbook extends AbsTab {

	
	
	public static class CONTEXT_MENU {
		public static final String LOCATOR		= "id='zm__Contacts'";
		
		//contact's context menu	
		public static final ContextMenuItem CONTACT_SEARCH = new ContextMenuItem("zmi__Contacts__SEARCH_MENU","Find Emails...","div[class*='ImgSearch']"," div[class*='ImgCascade']");	
		public static final ContextMenuItem CONTACT_ADVANCED_SEARCH = new ContextMenuItem("zmi__Contacts__BROWSE","Advanced Search","div[class*='ImgSearchBuilder']","");	
		public static final ContextMenuItem CONTACT_NEW_EMAIL = new ContextMenuItem("zmi__Contacts__NEW_MESSAGE","New Email","div[class*='ImgNewMessage']",":contains('nm')");  	
    
		//TODO: contact group: "Edit Group" instead of "Edit Contact"
		public static final ContextMenuItem CONTACT_EDIT = new ContextMenuItem("zmi__Contacts__CONTACT","Edit Contact","div[class*='ImgEdit']","");	
		public static final ContextMenuItem CONTACT_FORWARD = new ContextMenuItem("zmi__Contacts__SEND_CONTACTS_IN_EMAIL","Forward Contact","div[class*='ImgMsgStatusSent']","");	
	
		//TODO: contact group: "Tag Group" instead of "Tag Contact"
		public static final ContextMenuItem CONTACT_TAG = new ContextMenuItem("zmi__Contacts__TAG_MENU","Tag Contact","div[class*='ImgTag']"," div[class='ImgCascade']");	
		public static final ContextMenuItem CONTACT_DELETE = new ContextMenuItem("zmi__Contacts__DELETE","Delete","div[class*='ImgDelete']",":contains('Del')");
		public static final ContextMenuItem CONTACT_MOVE = new ContextMenuItem("zmi__Contacts__MOVE","Move","div[class*='ImgMoveToFolder']","");
		public static final ContextMenuItem CONTACT_PRINT = new ContextMenuItem("zmi__Contacts__PRINT_CONTACT","Print","div[class*='ImgPrint']",":contains('p')");
	 		
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

		boolean active=sIsElementPresent("css=div[id='zb__App__Contacts'][class*=ZSelected]");
			
		String locator = null;
		// On Zimbra Desktop, there is no Address book folder, but there is only
		// account root folder
      if(ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
	      locator = TreeMail.Locators.zTreeItems.replace(TreeMail.stringToReplace,
	            AjaxCommonTest.defaultAccountName);
	   } else {
		   //make sure Addressbook folder is displayed
		   locator = "css=div#ztih__main_Contacts__ADDRBOOK_div";
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
		zClickAt(PageMain.Locators.zAppbarContact,"0,0");

		zWaitForActive();

	}

	public void zRefresh() throws HarnessException {
		sRefresh();
		sWaitForPageToLoad();
								
		zWaitForElementPresent(PageMain.Locators.zAppbarContact);		
		
		// Click on Addressbook tab
		zClickAt(PageMain.Locators.zAppbarContact,"0,0");
		zWaitForElementPresent("css=div#ztih__main_Contacts__ADDRBOOK_div");
			
	}
	
	public List<FolderItem> zListGetFolders() throws HarnessException {
		List <FolderItem> list = new ArrayList<FolderItem>();
		
		//ensure it is in Addressbook main page
		zNavigateTo();
		
		if ( !this.sIsElementPresent("css=div#ztih__main_Contacts__ADDRBOOK div.DwtTreeItemLevel1ChildDiv") )			
			//maybe return empty list?????
				throw new HarnessException("Folder List is not present "+ "div#ztih__main_Contacts__ADDRBOOK div.DwtTreeItemLevel1ChildDiv");

		String startId = FolderItem.SystemFolder.Contacts.getId();
		    
		FolderItem folderItem = new FolderItem();
	    folderItem.setId(startId);
	    folderItem.setName(FolderItem.SystemFolder.Contacts.getName());
	    
		String id=startId;		    
		do {
		    id=sGetNextSiblingId(id);
		    if ((id.length() >0)) {
		    	  String folderName = ClientSessionFactory.session().selenium().getText("id="+id);
		    	  
		    	  folderItem = new FolderItem();
		    	  folderItem.setId(id);
		    	  folderItem.setName(folderName);
		    	  		    	  
		    	  logger.info(" found folder:" + folderName + " ,id:" + id);
		    	  list.add(folderItem);
		    }
		}		    		    
		while ((id.length() >0)) ;
		    
		id=startId;		    
		do {
			try {
		    id=sGetPreviousSiblingId(id);
			}
			catch (Exception e) {id="";};
			if ((id.length() >0)) {
		    	  String folderName = ClientSessionFactory.session().selenium().getText("id="+id);
		    	  
		    	  folderItem = new FolderItem();
		    	  folderItem.setId(id);
		    	  folderItem.setName(folderName);
		    	  		    	  
		    	  logger.info(" found folder:" + folderName + " ,id:" + id);
		    	  list.add(folderItem);
		    }
		}		    
		while ((id.length() >0)) ;
		    								
		return list;
	}
 	
	
	public List<ContactItem> zListGetContacts() throws HarnessException {

		List <ContactItem> list= new ArrayList<ContactItem>();

		//ensure it is in Addressbook main page
		zNavigateTo();
		if ( !this.sIsElementPresent("id=zv__CNS") )			
		//maybe return empty list?????
			throw new HarnessException("Contact List is not present "+ "id='zv__CNS'");

		//Get the number of contacts (String) 
		int count = this.sGetCssCount("css=div[id='zv__CNS']>div[id^=zli__CNS__]");
		
		logger.info(myPageName() + " zListGetContacts: number of contacts: "+ count);

		// Get each contact's data from the table list
		for (int i = 1; i <= count; i++) {
			String commonLocator = "css=div[id='zv__CNS'] div:nth-child("+ i +")";

			String contactType = getContactType(commonLocator);
		    
			ContactItem ci=null;
			String contactDisplayedLocator = commonLocator + " table tbody tr td:nth-child(3)";
			String fileAs = ClientSessionFactory.session().selenium().getText(contactDisplayedLocator);
			
			//check if it is a contact or a contactgroup item
			if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
                ci=new ContactGroupItem(fileAs);
			}
			else if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
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

			//locator = "//div[@id='ztb__CNS']//td[@id='zb__CNS__NEW_MENU_title']";
			
			locator = "css=td#zb__CNS__NEW_MENU_title";			
			page = new FormContactNew(this.MyApplication);

	
		} else if ( button == Button.B_DELETE ) {

			String id = "zb__CNS__DELETE_left_icon";

			if (sIsElementPresent("css=td#" + id + " div[class*=ZDisabledImage]")) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ id);
			}

			locator = "id="+ id;

		} else if ( button == Button.B_EDIT ) {

			String id = "zb__CNS__EDIT_left_icon";

			
			if (sIsElementPresent("css=td#" + id + " div[class*=ZDisabledImage]")) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ id);
			}

			locator = "id="+ id;
			page = newFormSelected();	
			
	    } else if ( button == Button.B_MOVE) {

		    String id = "zb__CNS__MOVE_left_icon";

		    if (sIsElementPresent("css=td#" + id + " div[class*=ZDisabledImage]")) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ id);
			}
		    
		   locator = "id="+ id;
		   page = new DialogMove(MyApplication, this);
	    } else if ( button == Button.B_FORWARD) {
		    String id = "zb__CNS__SEND_CONTACTS_IN_EMAIL_left_icon";
	
		    if (sIsElementPresent("css=td#" + id + " div[class*=ZDisabledImage]")) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled ");
			}
		   locator = "id="+ id;
		   page = new FormMailNew(MyApplication);	
		   
	    } else if ( button == Button.B_CANCEL) {
 	    	String id ="zb__CN__CANCEL";
		   
		    if (sIsElementPresent("css=div[id=" + id + "][class*=ZDisabledImage]")) {
				throw new HarnessException("Tried clicking on "+ id +" but it was disabled ");
		    }

		    locator = "id="+ id;
			page = new DialogWarning(DialogWarning.DialogWarningID.CancelCreateContact, this.MyApplication, ((AppAjaxClient)this.MyApplication).zPageAddressbook);

	    } else if (isAlphabetButton(button))
          {
       	   locator=DisplayContactGroup.ALPHABET_PREFIX + button.toString() + DisplayContactGroup.ALPHABET_POSTFIX;
       	   
       	   //TODO
       	   //page = ???
	    }

      

	    if ( locator == null )
			throw new HarnessException("locator was null for button "+ button);

		// Default behavior, process the locator by clicking on it
		//

		// Make sure the button exists
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);

		// Click it
		zClickAt(locator,"0,0");
		
		if (isAlphabetButton(button)) {
 		  //for addressbook alphabet button only
		  sClick(locator);
		}
		zWaitForBusyOverlay();
	
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		return (page);
	}

	
	public AbsPage zKeyboardKeyEvent(int keyEvent) throws HarnessException {
		logger.info(myPageName() + " zKeyboardKeyEvent("+ keyEvent +")");

		tracer.trace("Click the shortcut "+ keyEvent );
		
		// Default behavior variables
		AbsPage page = null;	// If set, this page will be returned

		// Click it
		this.zKeyboard.zTypeKeyEvent(keyEvent);		
		zWaitForBusyOverlay();
	
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		return (page);
	}

	public AbsPage zKeyboardShortcut(Shortcut shortcut) throws HarnessException {
		logger.info(myPageName() + " zKeyboardShortcut("+ shortcut.getKeys() +")");

		tracer.trace("Click the shortcut "+ shortcut.getKeys() );
		

		// Default behavior variables
		AbsPage page = null;	// If set, this page will be returned
		
		if ( shortcut == Shortcut.S_NEWTAG) {
			page = new DialogTag(MyApplication,((AppAjaxClient) MyApplication).zPageAddressbook);	
		}
		else if (shortcut == Shortcut.S_MOVE) {
			page = new DialogMove(MyApplication, this);	
			
		} 
		
		// Click it
		zKeyboardTypeString(shortcut.getKeys());	
		
		zWaitForBusyOverlay();
		
		
		if ( page != null ) {
			page.zWaitForActive();
		}
		return (page);
	}

	public ContactGroupItem createUsingSOAPSelectContactGroup(AppAjaxClient app, Action action, String ... tagIDArray)  throws HarnessException {	
	  // Create a contact group via Soap
	  ContactGroupItem group = ContactGroupItem.createUsingSOAP(app, tagIDArray);
		             
	  group.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
	  String[] dlist = app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn/mail:a[@n='dlist']", null).split(","); //a[2]   
	  for (int i=0; i<dlist.length; i++) {
		  group.addDListMember(dlist[i]);
	  }
	  
	  
      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
    
      // Select the item
      zListItem(action, group.fileAs);
      
      return group;
    }

	public ContactItem createUsingSOAPSelectContact(AppAjaxClient app, Action action, String ... tagIDArray)  throws HarnessException {	
		  // Create a contact via Soap
		  ContactItem contactItem = ContactItem.createUsingSOAP(app, tagIDArray);			             
		  contactItem.setId(app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn", "id"));
		  		  
		  
	      // Refresh the view, to pick up the new contact
	      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
	      GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());
	      app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
	    
	      // Select the item
	      zListItem(action, contactItem.fileAs);
	      
	      return contactItem;
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
						
			pulldownLocator = "css=td[id$='__TAG_MENU_dropdown'] div[class='ImgSelectPullDownArrow']";
			optionLocator = "css=div[id$='zb__CNS__TAG_MENU|MENU|REMOVETAG']"; 
			page = null;
			
			zWaitForBusyOverlay();
	      }

	   } else if ( pulldown == Button.B_NEW ) {
		   pulldownLocator = "css=div#zb__CNS__NEW_MENU td#zb__CNS__NEW_MENU_dropdown";

		   if ( option == Button.O_NEW_CONTACT ) {

			    // TODO: Bug 58365 for Desktop
			    if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
                   optionLocator="css=div[class='ActionMenu ZHasIcon'] div[class*='ZMenuItem ZWidget ZHasLeftIcon ZHasText'] table[class*='ZWidgetTable ZMenuItemTable']:contains('Contact')";                
			    } else {
                  optionLocator="css=div#zb__CNS__NEW_MENU_NEW_CONTACT";
                }
			    page = new FormContactNew(this.MyApplication);
		   }
		   else if ( option == Button.O_NEW_CONTACTGROUP) {
			   
			    // TODO: Bug 58365 for Desktop
			    if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			       optionLocator="css=div[class='ActionMenu ZHasIcon'] div[class*='ZMenuItem ZWidget ZHasLeftIcon ZHasText'] table[class*='ZWidgetTable ZMenuItemTable']:contains('Contact Group')";
			    } else {
			       optionLocator="css=div#zb__CNS__NEW_MENU_NEW_GROUP";
			    }
				page = new FormContactGroupNew(this.MyApplication);		   
		   }
		   else if ( option == Button.O_NEW_TAG ) {			   
		        optionLocator = "css=div#zb__CNS__NEW_MENU_NEW_TAG";
		        page = new DialogTag(this.MyApplication, this);
		   }    
		   else if ( option == Button.O_NEW_ADDRESSBOOK ) {					   
			    optionLocator = "css=div#zb__CNS__NEW_MENU_NEW_ADDRBOOK";
				page = new DialogCreateFolder(MyApplication, ((AppAjaxClient)MyApplication).zPageAddressbook);			    
							    

		   } else {
			   //option not suppored
			   pulldownLocator=null;
		   }
		   
	   }
	// Default behavior
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" option "+ option +" pulldownLocator "+ pulldownLocator +" not present!");
			}

			if (ClientSessionFactory.session().currentBrowserName().contains("IE")) {
				//IE			
				sClickAt(pulldownLocator,"0,0");
			}
			else {
			    //others
			    zClickAt(pulldownLocator,"0,0");
			}
			
			zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {
                
				// Make sure the locator exists
				zWaitForElementPresent(optionLocator);
				
				zClick(optionLocator);
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
		String imageLocator = locator +" div[class*=";
        
	    
		if (sIsElementPresent(imageLocator + ContactGroupItem.IMAGE_CLASS + "]"))
		{
			return ContactGroupItem.IMAGE_CLASS;
		}
		else if (sIsElementPresent(imageLocator + ContactItem.IMAGE_CLASS + "]"))
		{
			return ContactItem.IMAGE_CLASS;		
		}
		logger.info("contact type for " + locator + " is not a contact or contactgroup");
		return null;
	}
	
    // return the xpath locator of a contact
	private String getContactLocator(String contact) throws HarnessException {
		String listLocator = "div[id='zv__CNS']";
		
		String rowLocator = "div[id^='zli__CNS__']";
	    
		

		String contactLocator = null;
		
		if ( !this.sIsElementPresent("css=" + listLocator) )
			throw new HarnessException("List View Rows is not present "+ listLocator);

		if ( !this.sIsElementPresent("css=" + rowLocator) )
			throw new HarnessException("List does not contain any items "+ rowLocator);

		//Get the number of contacts (String) 
	    int count = this.sGetCssCount("css=" + listLocator + ">" + rowLocator);
		//int count = this.sGetXpathCount("xpath=//div[@id=zv__CNS]/div[contains(@id,zli__CNS__)]");
		//int count = this.sGetXpathCount("//div[@id='zv__CNS']//div[contains(@id, 'zli__CNS__')]");
	    logger.debug(myPageName() + " zListItem: number of contacts: "+ count);

		if ( count == 0 )
			throw new HarnessException("List count was zero");

		// Get each contact's data from the table list
		for (int i = 1; i<=count; i++) { 

			String itemLocator = "css=" + listLocator + " div:nth-child(" + i +")";
			if ( !this.sIsElementPresent(itemLocator) ) {
				throw new HarnessException("unable to locate item " + itemLocator);
			}

			String contactDisplayedLocator = itemLocator + " table tbody tr td:nth-child(3)";

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
		String listLocator = "div#zv__CNS";				
		String rowLocator = "div[id^='zli__CNS__']";
		
		
	    ArrayList<String> arrayList = new ArrayList<String>();
		
		if ( !sIsElementPresent("css=" + listLocator) )
			throw new HarnessException("List View Rows is not present "+ listLocator);

		if ( !sIsElementPresent("css=" + rowLocator) )
		    return arrayList; //an empty arraylist
			
		//Get the number of contacts (String) 
		int count = sGetCssCount("css=" + listLocator + ">" + rowLocator);

		logger.debug(myPageName() + " getSelectedContactLocator: number of contacts: "+ count);

		if ( count == 0 )
			throw new HarnessException("List count was zero");

		// Get each contact's data from the table list
		for (int i = 1; i<=count; i++) {
			String itemLocator = "css=" + listLocator + " div:nth-child(" + i +")";
        			
			if ( !sIsElementPresent(itemLocator) ) {
				logger.info("reach the end of list - unable to locate item " + itemLocator);
				break;
			}
			
			if (sIsElementPresent(itemLocator+ "[class*=Row-selected]")) {
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
		    zClick(contactLocator);
		    //zKeyboard.zTypeCharacters(Shortcut.S_RIGHTCLICK.getKeys());
			zRightClickAt(contactLocator,"0,0");
			
			
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
				
				//For Chrome and Safari only
				// as an alternative for sMouseOver(locator) 
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				
				ArrayList<String> selectedContactArrayList=getSelectedContactLocator();			
		        String contactType = getContactType(selectedContactArrayList.get(0));
			
		        //check if it is a contact 
                if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
    				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
    				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
    				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
			    }
				
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_RIGHT);
				
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
			zWaitForElementPresent(locator) ;
			
			// Check if the item is enabled
			if (sIsElementPresent("css=div[id=" + id + "][class*=ZDisabled]")) {
				throw new HarnessException("Tried clicking on "+ cmi.text +" but it was disabled ");
			}

			
			// Mouse over the option
			sFocus(locator);
			sMouseOver(locator);
	
			id = sub_cmi.locator;
			locator = "id="+ id;
		
			
			//  Make sure the sub context menu exists			
			zWaitForElementPresent(locator) ;
			
			// make sure the sub context menu enabled			
			zWaitForElementEnabled(id);
			
        } 
			
        
		// Click option
        zClickAt(locator,"0,0");
		
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
								
		    zRightClickAt(contactLocator,"0,0");
		    
		
			if (option == Button.B_DELETE){
                cmi=CONTEXT_MENU.CONTACT_DELETE;				
			}
			else if (option == Button.B_MOVE) {
				cmi=CONTEXT_MENU.CONTACT_MOVE;
				page = new DialogMove(MyApplication, this);	
			}
            
			else if (option == Button.B_EDIT) {
				cmi=CONTEXT_MENU.CONTACT_EDIT;				 
				page = newFormSelected();	
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
			else if (option == Button.B_FORWARD) {
				cmi=CONTEXT_MENU.CONTACT_FORWARD;				
				page = new FormMailNew(MyApplication);	
			}
			else {
				throw new HarnessException("option " + option + " not supported");
			}
			
			id = cmi.locator;
			locator = "id="+ id;

			//  Make sure the context menu exists
			zWaitForElementPresent(locator) ;
			
			// Check if the item is enabled
			if (sIsElementPresent("css=div[id=" + id + "][class*=ZDisabled]")) {
				throw new HarnessException("Tried clicking on "+cmi.text +" but it was disabled ");
			}

		}
		
				
		zClickAt(locator,"0,0");
		
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
			//zWaitForBusyOverlay();
			
			ArrayList<String> selectedContactArrayList=getSelectedContactLocator();			
	        String contactType = getContactType(selectedContactArrayList.get(0));
		
	        //check if it is a contact or a contact group item
		    if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
			  return  new DisplayContactGroup(MyApplication);		
		    }
		    else if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
			  return new DisplayContact(MyApplication);
		    }
		    else {
			  throw new HarnessException(" Error: not support the contact type");						    	
		    }
			
		}
		else if ( action == Action.A_CHECKBOX) {
			//get the checkbox locator
			contactLocator=contactLocator.substring(0, contactLocator.length()-2) + "1" + ")>center>div.ImgCheckboxUnchecked";
			
			//check the box			
			this.zClick(contactLocator);
			
			//zWaitForBusyOverlay();
						
			ArrayList<String> selectedContactArrayList=getSelectedContactLocator();			
	        String contactType = getContactType(selectedContactArrayList.get(0));
		
	        //check if it is a contact or a contact group item
		    if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
			  return  new DisplayContactGroup(MyApplication);		
		    }
		    else if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
			  return new DisplayContact(MyApplication);
		    }
		    else {
			  throw new HarnessException(" Error: not support the contact type");						    	
		    }
			
		}
		else if (action == Action.A_RIGHTCLICK ) {
			
            zRightClickAt(contactLocator,"0,0"); 
            //zWaitForBusyOverlay();
    		return (new ContextMenu(MyApplication));			
		}
			
		
		throw new HarnessException("action not supported ");
	
	}
	
	


	private AbsPage newFormSelected() throws HarnessException {
	    AbsPage page = null;
		ArrayList<String> selectedContactArrayList=getSelectedContactLocator();
	
	    if (selectedContactArrayList.size() == 0) {
		  throw new HarnessException("No selected contact/contact group ");				
	    }
	
	    /*if (selectedContactArrayList.size() > 1) {
	      for (int i=0; i<selectedContactArrayList.size(); i++) {
	    	  logger.info(selectedContactArrayList.get(i));
	      }
		  throw new HarnessException("Cannot edit more than one contact/contact group ");				
	    }*/
	
        String contactType = getContactType(selectedContactArrayList.get(0));
	
        //check if it is a contact or a contact group item
	    if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
		  page = new FormContactGroupNew(MyApplication);		
	    }
	    else if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
		  page = new FormContactNew(MyApplication);
	    }
	
	    return page;
	
	}

	private boolean isAlphabetButton(Button button) {
	  return (button == Button.B_AB_ALL) || (button == Button.B_AB_123) 
		|| (button == Button.B_AB_A) || (button == Button.B_AB_B) || (button == Button.B_AB_C) || (button == Button.B_AB_D) 
	    || (button == Button.B_AB_E) || (button == Button.B_AB_F) || (button == Button.B_AB_G) || (button == Button.B_AB_H)
	    || (button == Button.B_AB_I) || (button == Button.B_AB_J) || (button == Button.B_AB_K) || (button == Button.B_AB_L)
	    || (button == Button.B_AB_M) || (button == Button.B_AB_N) || (button == Button.B_AB_O) || (button == Button.B_AB_P)
	    || (button == Button.B_AB_Q) || (button == Button.B_AB_R) || (button == Button.B_AB_S) || (button == Button.B_AB_T)
	    || (button == Button.B_AB_U) || (button == Button.B_AB_V) || (button == Button.B_AB_W) || (button == Button.B_AB_X)
	    || (button == Button.B_AB_Y) || (button == Button.B_AB_Z);
	}
}
