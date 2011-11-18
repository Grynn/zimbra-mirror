package  com.zimbra.qa.selenium.projects.ajax.ui.addressbook;


import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.LogManager;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.ajax.ui.ContextMenu;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogAssistant;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogTag;
import com.zimbra.qa.selenium.projects.ajax.ui.DialogWarning;
import com.zimbra.qa.selenium.projects.ajax.ui.PageMain;

import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
import com.zimbra.qa.selenium.framework.items.*;

import com.zimbra.qa.selenium.framework.ui.*;


import com.zimbra.qa.selenium.framework.util.*;

import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.*;


import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.TreeMail;
import com.zimbra.qa.selenium.projects.ajax.ui.search.PageAdvancedSearch;

public class PageAddressbook extends AbsTab {

	
	
	public static class CONTEXT_MENU {
		public static final String LOCATOR		= "id='zm__Contacts'";
		
		//contact's context menu	
		public static final ContextMenuItem CONTACT_SEARCH = new ContextMenuItem("POPUP_SEARCH_MENU","Find Emails...","div[class*='ImgSearch']"," div[class*='ImgCascade']");	
		public static final ContextMenuItem CONTACT_NEW_EMAIL = new ContextMenuItem("POPUP_NEW_MESSAGE","New Email","div[class*='ImgNewMessage']",":contains('nm')");  	
    
		//TODO: contact group: "Edit Group" instead of "Edit Contact"
		public static final ContextMenuItem CONTACT_EDIT = new ContextMenuItem("POPUP_CONTACT","Edit Contact","div[class*='ImgEdit']","");	
		public static final ContextMenuItem CONTACT_FORWARD = new ContextMenuItem("POPUP_SEND_CONTACTS_IN_EMAIL","Forward Contact","div[class*='ImgMsgStatusSent']","");	
	
		//TODO: contact group: "Tag Group" instead of "Tag Contact"
		public static final ContextMenuItem CONTACT_TAG = new ContextMenuItem("POPUP_TAG_MENU","Tag Contact","div[class*='ImgTag']"," div[class='ImgCascade']");	
		public static final ContextMenuItem CONTACT_DELETE = new ContextMenuItem("POPUP_DELETE","Delete","div[class*='ImgDelete']",":contains('Del')");
		public static final ContextMenuItem CONTACT_MOVE = new ContextMenuItem("POPUP_MOVE","Move","div[class*='ImgMoveToFolder']","");
		public static final ContextMenuItem CONTACT_PRINT = new ContextMenuItem("POPUP_PRINT_CONTACT","Print","div[class*='ImgPrint']",":contains('p')");
	 		
		public static final ContextMenuItem CONTACT_GROUP = new ContextMenuItem("POPUP_CONTACTGROUP_MENU","Contact Group","div[class*='ImgGroup']","");
		public static final ContextMenuItem CONTACT_QUICK_COMMAND =  new ContextMenuItem("POPUP_QUICK_COMMANDS","Quick Commands","div[class='ImgQuickCommand']","");
		
	}

	
	public static class CONTEXT_SUB_MENU {
				
		public static final ContextMenuItem CONTACT_SUB_NEW_TAG = new ContextMenuItem("div#contacts_newtag","New Tag","div[class='ImgNewTag']",":contains('nt')");
		public static final ContextMenuItem CONTACT_SUB_REMOVE_TAG = new ContextMenuItem("div[id^=contacts_removetag]","Remove Tag","div[class='ImgDeleteTag']","");
		//public static final ContextMenuItem CONTACT_SUB_REMOVE_TAG = new ContextMenuItem("td#zmi__Contacts__TAG_MENU|MENU|REMOVETAG_title","Remove Tag","div[class='ImgDeleteTag']","");

		
		public static final ContextMenuItem CONTACT_SUB_RECEIVED_FROM_CONTACT = new ContextMenuItem("tr#POPUP_SEARCH","Received From Contact","div[class='ImgSearch']","");
	    public static final ContextMenuItem CONTACT_SUB_SENT_TO_CONTACT = new ContextMenuItem("tr#POPUP_SEARCH_TO","Sent To Contact","div[class='ImgSearch']","");
	
	    public static final ContextMenuItem CONTACT_SUB_NEW_CONTACT_GROUP = new ContextMenuItem("div[id^='CONTACTGROUP_MENU__DWT'][id$='|GROUP_MENU|NEWGROUP']","New Contact Group","div[class='ImgNewGroup']","");
	    
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

	
	//get subFolders
	public List<FolderItem> zListGetFolders(ZimbraAccount account, FolderItem parentFolder) throws HarnessException {
		List <FolderItem> list = new ArrayList<FolderItem>();
		String folderId = "zti" + ((parentFolder.getName().equals("USER_ROOT"))?"h":"") + "__main_Contacts__" + ((parentFolder.getName().equals("USER_ROOT"))?"ADDRBOOK":parentFolder.getId()) +"_div";
	
		//ensure it is in Addressbook main page
		zNavigateTo();
	
		String elements="window.document.getElementById('" + folderId + "').nextSibling.childNodes";
	    int length = Integer.parseInt(sGetEval(elements + ".length"));
	   
	    
	    for (int i=0; i<length; i++) {
	        String id= sGetEval(elements + "[" + i +"].id");
	        
	        if (id.contains("Contacts")) {
		       list.add(FolderItem.importFromSOAP(account, sGetText("css=td#" + id + "_textCell")));
	        }
	      }
		
	    return list;
	}
	
	public boolean zIsContactDisplayed(ContactItem contactItem) throws HarnessException {
        boolean isContactFound = false;
        
		//ensure it is in Addressbook main page
		zNavigateTo();
		if ( !sIsElementPresent("id=ZV__CNS-main") )			
		//maybe return empty list?????
			throw new HarnessException("Contact List is not present "+ "id='ZV__CNS-main'");

		//Get the number of contacts (String) 
		int count = this.sGetCssCount("css=div[id='ZV__CNS-main']>div[id^=zli__CNS__]");
		
		logger.info(myPageName() + " zIsContactDisplayed: number of contacts: "+ count);

		// Get each contact's data from the table list
		for (int i = 1; i <= count && !isContactFound; i++) {
			String commonLocator = "css=div[id='ZV__CNS-main'] div:nth-child("+ i +")";

			String contactType = getContactType(commonLocator);
		    
			String contactDisplayedLocator = commonLocator + " table tbody tr td:nth-child(3)";
			String fileAs = sGetText(contactDisplayedLocator);
			logger.info("...found "+ contactType + " - " + fileAs );
			isContactFound = ((contactType.equals(ContactGroupItem.IMAGE_CLASS) &&  contactItem instanceof ContactGroupItem) ||
				  (contactType.equals(ContactItem.IMAGE_CLASS) &&  contactItem instanceof ContactItem)) &&
				  (contactItem.fileAs.equals(fileAs));
			
				    	      
		}


		return isContactFound;		
	}

    // only return the list with a certain contact type				
	// contactType should be one of ContactGroupItem.IMAGE_CLASS , ContactItem.IMAGE_CLASS	
	public List<ContactItem> zListGetContacts(String contactType) throws HarnessException {

		List <ContactItem> list= new ArrayList<ContactItem>();

		//ensure it is in Addressbook main page
		zNavigateTo();
		if ( !this.sIsElementPresent("id=ZV__CNS-main") )			
		//maybe return empty list?????
			throw new HarnessException("Contact List is not present "+ "id='ZV__CNS-main'");

		//Get the number of contacts (String) 
		int count = this.sGetCssCount("css=div[id='ZV__CNS-main']>div[id^=zli__CNS__]");
		
		logger.info(myPageName() + " zListGetContacts: number of contacts: "+ count);

		// Get each contact's data from the table list
		for (int i = 1; i <= count; i++) {
			String commonLocator = "css=div[id='ZV__CNS-main'] div:nth-child("+ i +")";

		    
			if (sIsElementPresent(commonLocator + " div[class*=" + contactType + "]")) {
				
			    ContactItem ci=null;
			    String contactDisplayedLocator = commonLocator + " table tbody tr td:nth-child(3)";
			    String fileAs = sGetText(contactDisplayedLocator);
		        logger.info(" found " + fileAs);
		    
		        //check contact type
		        if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
		        	ci=new ContactGroupItem(fileAs);
		        }
		        else if (  contactType.equals(ContactItem.IMAGE_CLASS) ||
		        		   contactType.equals(ContactItem.GAL_IMAGE_CLASS)) {
		        	ci=new ContactItem(fileAs);		    			
		        }
		        else {
		        	throw new HarnessException("Image not neither conntact group nor contact.");		
		        }
			
		        list.add(ci);	    	      
			}
		}


		return list;		
	}

	
	public List<ContactItem> zListGetContacts() throws HarnessException {

		List <ContactItem> list= new ArrayList<ContactItem>();

		//ensure it is in Addressbook main page
		zNavigateTo();
		if ( !this.sIsElementPresent("id=ZV__CNS-main") )			
		//maybe return empty list?????
			throw new HarnessException("Contact List is not present "+ "id='ZV__CNS-main'");

		//Get the number of contacts (String) 
		int count = this.sGetCssCount("css=div[id='ZV__CNS-main']>div[id^=zli__CNS__]");
		
		logger.info(myPageName() + " zListGetContacts: number of contacts: "+ count);

		// Get each contact's data from the table list
		for (int i = 1; i <= count; i++) {
			String commonLocator = "css=div[id='zv__CNS-main'] div:nth-child("+ i +")";

			String contactType = getContactType(commonLocator);
		    
			ContactItem ci=null;
			String contactDisplayedLocator = commonLocator + " table tbody tr td:nth-child(3)";
			String fileAs = sGetText(contactDisplayedLocator);
		    logger.info(" found " + fileAs);
		    
			//check if it is a contact or a contactgroup item
			if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
                ci=new ContactGroupItem(fileAs);
			}
			else if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
				ci=new ContactItem(fileAs);		    			
			}
			else if (  contactType.equals(DistributionListItem.IMAGE_CLASS) ) {
				ci=new DistributionListItem(fileAs);		    			
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

		if ( button == Button.B_REFRESH ) {
			
			return (((AppAjaxClient)this.MyApplication).zPageMain.zToolbarPressButton(Button.B_REFRESH));
			
		} else if ( button == Button.B_NEW ) {

			// For "NEW" without a specified pulldown option, just return the default item
			// To use "NEW" with a pulldown option, see  zToolbarPressPulldown(Button, Button)

			
			locator = "css=div#zb__CNS-main__NEW_MENU td#zb__CNS-main__NEW_MENU_title";			
			page = new FormContactNew(this.MyApplication);

	
		} else if ( button == Button.B_DELETE ) {

			String id = "zb__CNS-main__DELETE";

			if (this.zIsElementDisabled("css=div#" + id)) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled "+ id);
			}

			locator = "id="+ id;

		} else if ( button == Button.B_EDIT ) {

			String id = "zb__CNS-main__EDIT";

			
			if (zIsElementDisabled("css=div#" + id )) {
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
		    String id = "zb__CNS-main__SEND_CONTACTS_IN_EMAIL";
	
		    if (zIsElementDisabled("css=div#" + id)) {
				throw new HarnessException("Tried clicking on "+ button +" but it was disabled ");
			}
		   locator = "id="+ id;
		   page = new FormMailNew(MyApplication);	
		   
	    } else if ( button == Button.B_CANCEL) {
 	    	//String id ="dizb__CN__CANCEL";
 	    	locator = "css=div[id^=zb__CN][id$=__CANCEL]" ;
		    if (zIsElementDisabled(locator)) {
				throw new HarnessException("Tried clicking on "+ locator +" but it was disabled ");
		    }
		    
			page = new DialogWarning(DialogWarning.DialogWarningID.CancelCreateContact, this.MyApplication, ((AppAjaxClient)this.MyApplication).zPageAddressbook);
	    //click close without changing contact contents
	    } else if ( button == Button.B_CLOSE){
 	    	locator = "css=div[id^=zb__CN][id$=__CANCEL]" ;
		    if (zIsElementDisabled(locator)) {
				throw new HarnessException("Tried clicking on "+ locator +" but it was disabled ");
		    }		    			

		    		
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
		if ( !sIsElementPresent(locator) )
			throw new HarnessException("Button is not present locator="+ locator +" button="+ button);

		// Click it
		zClickAt(locator,"0,0");
		  ExecuteHarnessMain.ResultListener.captureScreen();
		if (isAlphabetButton(button)) {
 		  //for addressbook alphabet button only
		  sClick(locator);
		}
		zWaitForBusyOverlay();
	
		
		if ( page != null ) {
			//sWaitForPageToLoad();			
			  ExecuteHarnessMain.ResultListener.captureScreen();
			page.zWaitForActive();
			  ExecuteHarnessMain.ResultListener.captureScreen();
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
		else if ( shortcut == Shortcut.S_ASSISTANT ) {			
			page = new DialogAssistant(MyApplication, ((AppAjaxClient) MyApplication).zPageAddressbook);
		}
		else if ( shortcut == Shortcut.S_MAIL_REMOVETAG ) {			
			page = null;
		}
	    else {		
		   throw new HarnessException("No logic for shortcut : "+ shortcut);
	    }
		
		// Click it
		//zKeyboardTypeString(shortcut.getKeys());	
		zKeyboard.zTypeCharacters(shortcut.getKeys());
		
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
	  //String[] dlist = app.zGetActiveAccount().soapSelectValue("//mail:CreateContactResponse/mail:cn/mail:a[@n='dlist']", null).split(","); //a[2]   
	  //for (int i=0; i<dlist.length; i++) {
	  //	  group.addDListMember(dlist[i]);
	  //}
	  
	  
      // Refresh the view, to pick up the new contact
      FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
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

	         pulldownLocator = "css=td#zb__CNS-main__TAG_MENU_dropdown div.ImgSelectPullDownArrow";
	         optionLocator = "css=td#contacts_newtag_title";
	         
	         page = new DialogTag(this.MyApplication, this);

	      } else if ( option == Button.O_TAG_REMOVETAG ) {
						
	    	 pulldownLocator = "css=td#zb__CNS-main__TAG_MENU_dropdown div.ImgSelectPullDownArrow";
		     optionLocator = "css=div[id='zb__CNS-main__TAG_MENU|MENU'] div[id='contacts_removetag'] td[id='contacts_removetag_title']"; 
			 page = null;
			
			
	      }

	   } else if ( pulldown == Button.B_NEW ) {
		   
		   pulldownLocator = "css=div#zb__CNS-main__NEW_MENU td#zb__CNS-main__NEW_MENU_dropdown";
		   if ( option == Button.O_NEW_CONTACT ) {

			    // TODO: Bug 58365 for Desktop
			    if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
                   optionLocator="css=div[class='ActionMenu ZHasIcon'] div[class*='ZMenuItem ZWidget ZHasLeftIcon ZHasText'] table[class*='ZWidgetTable ZMenuItemTable']:contains('Contact')";                
			    } else {
                  optionLocator="css=div#zb__CNS-main__NEW_MENU_NEW_CONTACT";
                }
			    page = new FormContactNew(this.MyApplication);
		   }
		   else if ( option == Button.O_NEW_CONTACTGROUP) {
			   
			    // TODO: Bug 58365 for Desktop
			    if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			       optionLocator="css=div[class='ActionMenu ZHasIcon'] div[class*='ZMenuItem ZWidget ZHasLeftIcon ZHasText'] table[class*='ZWidgetTable ZMenuItemTable']:contains('Contact Group')";
			    } else {
			       optionLocator="css=div#zb__CNS-main__NEW_MENU_NEW_GROUP";
			    }
				page = new FormContactGroupNew(this.MyApplication);		   
		   }
		   else if ( option == Button.O_NEW_TAG ) {			   
		        optionLocator = "css=td#ztih__main_Contacts__TAG_headerCell td[id^=DWT][id$=title]";
		        page = new DialogTag(this.MyApplication, this);
		   }    
		   else if ( option == Button.O_NEW_ADDRESSBOOK ) {					   
			    optionLocator = "css=td#ztih__main_Contacts__ADDRBOOK_headerCell td[id^=DWT][id$=title]";
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

			//central coordinate "x,y" 
			String center= sGetElementWidth(pulldownLocator)/2 + "," + sGetElementHeight(pulldownLocator)/2;
			if ( this.zIsBrowserMatch(BrowserMasks.BrowserMaskIE)){
			 				 
				// TODO check if the following code make the test case CreateContactGroup.GroupOfNewEmail() pass in wdc			
			    sGetEval("var evObj = document.createEventObject();" 
						+ "var x = selenium.browserbot.findElementOrNull('" + pulldownLocator + "');"
						+ "x.focus();x.blur();x.fireEvent('onclick');");

				//the following code failed in wdc, but pass in my machine :
				//sClickAt(pulldownLocator,center);
			}
			else {
			    //others
			    zClickAt(pulldownLocator,center);
			}
			
			zWaitForBusyOverlay();
			
			if ( optionLocator != null ) {
             	// Make sure the locator exists and visible
				zWaitForElementPresent(optionLocator);
					
				if (!zIsElementDisabled(optionLocator)) {
				   zClick(optionLocator);
				   zWaitForBusyOverlay();
				}

			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			if ( page != null ) {
				//sWaitForPageToLoad();
				page.zWaitForActive();
			}
			
		}
	    return page;
	}

	
	public AbsPage zToolbarPressPulldown(Button pulldown, IItem item) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ item +")");

		tracer.trace("Click pulldown "+ pulldown +" then "+ item);

		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");

		String pulldownLocator = null;	// If set, this will be expanded
		String optionLocator = null;	// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
	   if ( pulldown == Button.B_MOVE ) {
		
	      if ( item instanceof FolderItem) {
             FolderItem folder = (FolderItem) item;
	         pulldownLocator = "css=td#zb__CNS-main__MOVE_MENU_dropdown.ZDropDown";
	         optionLocator   = "css=td#zti__DwtFolderChooser_ContactsCNS-main__" + folder.getId() + "_textCell.DwtTreeItem-Text";
	         //TODO page=?	         
	      }
	   }
	   else if ( pulldown == Button.B_TAG ) {			
		   if ( item instanceof TagItem) {
			 pulldownLocator = "css=td#zb__CNS-main__TAG_MENU_dropdown div.ImgSelectPullDownArrow";
			
			 //Selenium cannot find the following optionLocator
             //optionLocator = "css=div#zb__CNS-main__TAG_MENU|MENU div:contains('" +((TagItem)item).getName()   + "'"; 
			    
		     page = null;
	       }
	   }
	  	   
	   if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" folder "+ item +" pulldownLocator "+ pulldownLocator +" not present!");
			}

			//central coordinate "x,y" 
			String center= sGetElementWidth(pulldownLocator)/2 + "," + sGetElementHeight(pulldownLocator)/2;
			zClickAt(pulldownLocator,center);
			
			zWaitForBusyOverlay();
            
			// find optionLocator
			if ( pulldown == Button.B_TAG ) {	
				String tagName = ((TagItem)item).getName();  
			     
				//get number of menu's options
				int countOption= Integer.parseInt(sGetEval("window.document.getElementById('zb__CNS-main__TAG_MENU|MENU').children[0].children[0].children.length"));
				String id= null;
				
				//find option id contains the tag name
				for (int i=0; i <countOption; i++) {
				 id= sGetEval("window.document.getElementById('zb__CNS-main__TAG_MENU|MENU').children[0].children[0].children[" + i + "].children[0].children[0].id");
		     
				 if (sGetText("css=div#" + id).contains(tagName)) {
					 optionLocator = "css=div#" + id ; 
					 break;
				 }
				}			 	
			}	
			
			if ( optionLocator != null ) {              
				// Make sure the locator exists and visible
				zWaitForElementPresent(optionLocator);
				
				if (zIsVisiblePerPosition(optionLocator,0,0)) {
				   zClick(optionLocator);
				   zWaitForBusyOverlay();
				}

			}
			
			// If we click on pulldown/option and the page is specified, then
			// wait for the page to go active
			//if ( page != null ) {
			//	page.zWaitForActive();
			//}
			
		}
	    return page;
	   	   
	}
	
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option, Object item) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButtonWithPulldown("+ pulldown +", "+ option + " , " + item +")");

		tracer.trace("Click pulldown "+ pulldown +" then "+ option + " and " + item);

		if ( pulldown == null )
			throw new HarnessException("Button cannot be null!");

		String pulldownLocator  = null;	// If set, this will be expanded
		String optionLocator    = null;	// If set, this will be clicked
		String subOptionLocator = null;	// If set, this will be clicked
		
		AbsPage page = null;	// If set, this page will be returned
		
		if ( pulldown == Button.B_TAG ) {			
			 pulldownLocator = "css=td#zb__CNS-main__TAG_MENU_dropdown div.ImgSelectPullDownArrow";
			
			 if (option == Button.O_TAG_REMOVETAG) {
				  optionLocator = "css=div[id='zb__CNS-main__TAG_MENU|MENU'] div[id='contacts_removetag'] td[id='contacts_removetag_title']"; 
					
				
			 }	  
		     page = null;
	    }
	   	
	  	   
		if ( pulldownLocator != null ) {
						
			// Make sure the locator exists
			if ( !sIsElementPresent(pulldownLocator) ) {
				throw new HarnessException("Button "+ pulldown +" folder "+ item +" pulldownLocator "+ pulldownLocator +" not present!");
			}

			//central coordinate "x,y" 
			String center= sGetElementWidth(pulldownLocator)/2 + "," + sGetElementHeight(pulldownLocator)/2;
			zClickAt(pulldownLocator,center);
			
			zWaitForBusyOverlay();
            
			// find optionLocator
		
			if ( optionLocator != null ) {              
				// Make sure the locator exists and visible
				zWaitForElementPresent(optionLocator);
				
				if (zIsVisiblePerPosition(optionLocator,0,0)) {
				   sMouseOver(optionLocator);
				   zWaitForBusyOverlay();
				   
				   if (item instanceof TagItem) {
					   String tagName="";
					   
					   if (item == TagItem.Remove_All_Tags) {
						  tagName = "All Tags"; // DWT291_dropdown ‌·[u]?
					   }
					   else {						   
						  tagName = ((TagItem) item).getName();
					   }  
						  
						// find active menu id
						  
					   //get number of z_shell's children
					   int countOption= Integer.parseInt(sGetEval("window.document.getElementById('z_shell').children.length"));
					   String parentMenuid= null;
							
					   //find id of the active menu
					   for (int i=countOption-1; i>0;  i--) {
						   parentMenuid= sGetEval("window.document.getElementById('z_shell').children[" + i + "].id");
					     
						   if (sGetEval("window.document.getElementById('" + parentMenuid + "').getAttribute('class')").contains("ActionMenu ZHasIcon")
								 && sIsVisible(parentMenuid)){
								 subOptionLocator = "css=div#" + parentMenuid + " td[id$=title]:contains(" + tagName + ")";
								 break;
						   }
					   }					          				     
				   }
				   
				   if (subOptionLocator != null) {
					   // Make sure the locator exists and visible
						zWaitForElementPresent(subOptionLocator);
						
						if (zIsVisiblePerPosition(subOptionLocator,0,0)) {
						   zClick(subOptionLocator);
						   zWaitForBusyOverlay();
						}	   					   
				   }
				}				
			}
						
		}
		
		//if ( page != null ) {
		//	page.zWaitForActive();
		//}

	    return page;
	   	   
	}
	// return the type of a contact
	private String getContactType(String locator) throws HarnessException {
		String imageLocator = locator +" div[class*=";
        
	    
		if (sIsElementPresent(imageLocator + ContactGroupItem.IMAGE_CLASS + "]"))
		{
			return ContactGroupItem.IMAGE_CLASS;
		}
		else if (sIsElementPresent(imageLocator + ContactItem.IMAGE_CLASS + "]"))
		{
			return ContactItem.IMAGE_CLASS;		
		}
		else if (sIsElementPresent(imageLocator + DistributionListItem.IMAGE_CLASS + "]"))
		{
			return DistributionListItem.IMAGE_CLASS;		
		}	
		logger.info(sGetAttribute(locator+ " div@class") + " not contain neither " + ContactGroupItem.IMAGE_CLASS + " nor " + ContactItem.IMAGE_CLASS );
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

			String itemLocator = "css=" + listLocator + ">div:nth-child(" + i +")";
			if ( !this.sIsElementPresent(itemLocator) ) {
				throw new HarnessException("unable to locate item " + itemLocator);
			}

		
			String displayAs = sGetText(itemLocator);

			// Log this item to the debug output
			LogManager.getLogger("projects").info("zListItem: found contact "+ displayAs);

			if ( contact.equals(displayAs) ) {
			   contactLocator = itemLocator;
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
		String parentLocator = null;
		String extraLocator="";
		
		tracer.trace(action +" then "+ option +" then "+ subOption +" on contact = "+ contact);

        if ( action == Action.A_RIGHTCLICK ) {
			ContextMenuItem cmi=null;
		    ContextMenuItem sub_cmi = null;
		
		    zRightClickAt(getContactLocator(contact),"0,0");
		      
		    
			if (option == Button.B_TAG) {
		        
				cmi=CONTEXT_MENU.CONTACT_TAG;
													
				if (subOption == Button.O_TAG_NEWTAG) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_NEW_TAG;
					page = new DialogTag(this.MyApplication, this);
				}
				
				else if (subOption == Button.O_TAG_REMOVETAG) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_REMOVE_TAG;					
					parentLocator= "div[id^=TAG_MENU__DWT][id$=|MENU]";
					page = null;	
				}
				
				
			    
			}
			else if (option == Button.B_CONTACTGROUP) {
				if (subOption == Button.O_NEW_CONTACTGROUP) {
					cmi= CONTEXT_MENU.CONTACT_GROUP;
					sub_cmi= CONTEXT_SUB_MENU.CONTACT_SUB_NEW_CONTACT_GROUP;
					page = new SimpleFormContactGroupNew(MyApplication);
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
			//id = cmi.locator;
			locator = "css=div#zm__Contacts tr#"+ cmi.locator;
						
			//locator = "id="+ id;
			
			//  Make sure the context menu exists
			zWaitForElementPresent(locator) ;
			
			// TODO: Check if the item is enabled
			//if (zIsElementDisabled("div#" + id )) {
			//	throw new HarnessException("Tried clicking on "+ cmi.text +" but it was disabled ");
			//}

			//For Safari 
			// as an alternative for sMouseOver(locator) 
		    if (zIsBrowserMatch(BrowserMasks.BrowserMaskSafari)) {
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
				
				ArrayList<String> selectedContactArrayList=getSelectedContactLocator();			
		        String contactType = getContactType(selectedContactArrayList.get(0));
			
		        //check if it is a contact 
                if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
    				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
    				zKeyboard.zTypeKeyEvent(KeyEvent.VK_DOWN);
    				
			    }
				
				zKeyboard.zTypeKeyEvent(KeyEvent.VK_RIGHT);
		    }		
		    else {    			
			 // Mouse over the option
			 sFocus(locator);
			 sMouseOver(locator);
		    }
			 
		    zWaitForBusyOverlay();
	
			if (option == Button.B_SEARCH) {
				
				//find parent locators
		    	
				try {
					
					int total= Integer.parseInt(sGetEval("window.document.getElementById('z_shell').childNodes.length")) -1;
				
				    for (int i=total; i>=0 ; i--, parentLocator=null) {	  		   
				    	parentLocator = sGetEval("window.document.getElementById('z_shell').childNodes[" + i + "].id" );
				    	if ( parentLocator.startsWith("POPUP_DWT") && zIsVisiblePerPosition(parentLocator, 0, 0))
				        {					    		
				    		logger.info("parent = " + parentLocator);
				    		parentLocator = "div#" + parentLocator;
				    		break;
				    	}		    					    	
			        }
			
				  
				}
			    catch (Exception e) {
					parentLocator=null;
					logger.info("cannot find parent id for " + sub_cmi.locator + " " + e.getMessage());
				}
				
			}			
			
	    	if (parentLocator != null) {
				locator = "css=" + parentLocator + " " + sub_cmi.locator + extraLocator;		        			
			}
	    	else {
	            locator = "css=" + sub_cmi.locator + extraLocator;
	    	}
	    	
	    	
			//  Make sure the sub context menu exists			
			zWaitForElementPresent(locator) ;
			
			// make sure the sub context menu enabled			
			zWaitForElementEnabled(locator);
			
        } 
		
        ExecuteHarnessMain.ResultListener.captureScreen();
   
    
    	//else {
    		sFocus(locator);
            sMouseOver(locator);
            //jClick(locator);
            zClickAt(locator, "0,0");
     	//}
       zWaitForBusyOverlay();
		
		
		if ( page != null ) {
			//sWaitForPageToLoad();
			page.zWaitForActive();
		}
		return (page);
    
	}
	
  
	public AbsPage zListItem(Action action, Button option ,Button subOption, String tagName, String contact) throws HarnessException {
		String locator = null;			// If set, this will be clicked
		AbsPage page = null;	// If set, this page will be returned
		String id = null;
		String parentLocator = null;
		String extraLocator="";
		
		tracer.trace(action +" then "+ option +" then "+ subOption + " and tag " + tagName + " on contact = "+ contact);

        if ( action == Action.A_RIGHTCLICK ) {
			ContextMenuItem cmi=null;
		    ContextMenuItem sub_cmi = null;
		
		    zRightClickAt(getContactLocator(contact),"0,0");
		      
		    
			if (option == Button.B_TAG) {		        
				cmi=CONTEXT_MENU.CONTACT_TAG;
						
				if (subOption == Button.O_TAG_REMOVETAG) {
					sub_cmi = CONTEXT_SUB_MENU.CONTACT_SUB_REMOVE_TAG;					
					parentLocator= "div[id^=TAG_MENU__DWT][id$=|MENU]";
				
					//id = cmi.locator;
					locator = "css=div#zm__Contacts tr#"+ cmi.locator;
									
					//  Make sure the context menu exists
					zWaitForElementPresent(locator) ;
			
					// Mouse over the option
					sFocus(locator);
					sMouseOver(locator);		    			 
					zWaitForBusyOverlay();
	
										    	
					locator = "css=" + parentLocator + " " + sub_cmi.locator + extraLocator;		        			
				    	
					//  Make sure the sub context menu exists			
					zWaitForElementPresent(locator) ;
			
					// 	make sure the sub context menu enabled			
					zWaitForElementEnabled(locator);
						
					// mouse over the sub menu
					sFocus(locator);
				    sMouseOver(locator);
				     	
				    zWaitForBusyOverlay();
					
				    //find the parent id
				    
				    //reset locator
				    locator =null;
				    
				    //get number of z_shell's children
					int countOption= Integer.parseInt(sGetEval("window.document.getElementById('z_shell').children.length"));
							
					//find id of the active menu
					for (int i=countOption-1; i>0;  i--) {
						 id= sGetEval("window.document.getElementById('z_shell').children[" + i + "].id");
				     
						 if (id.startsWith("DWT") 					
							 && sGetEval("window.document.getElementById('" + id + "').getAttribute('class')").contains("ActionMenu ZHasIcon")
							 && sIsVisible(id)){
							 locator="css=div#" + id + " td[id^=DWT][id$=_title]:contains('" + tagName + "')";							 
							 break;
						 }
					}		
			          				     
				    if (locator != null) {
				    
				    	//  Make sure the sub context menu exists			
				    	zWaitForElementPresent(locator) ;
			
				    	// 	make sure the sub context menu enabled			
				    	zWaitForElementEnabled(locator);
					
				    	// select the tag name
				    	zClick(locator);
				    				     	
				    	zWaitForBusyOverlay();
				    }	
				}
			}     		       		
	    }
		return (page);    
	}
	
	public void zListItem(Action action, Button option ,IItem item, String contact) throws HarnessException {
		String locator = null;			// If set, this will be clicked
	
		String itemLocator = null;
		String id = null;
        
		tracer.trace(action +" then "+ option +" then "+ item +" on contact = "+ contact);
        if ( action == Action.A_RIGHTCLICK ) {
			ContextMenuItem cmi=null;
		    
			zRightClickAt(getContactLocator(contact),"0,0");

			
			if (option == Button.B_TAG) {		        
				cmi=CONTEXT_MENU.CONTACT_TAG;
													
				if (item instanceof TagItem) {
					TagItem ti = (TagItem) item;
					itemLocator = "css=td[id$=title]:contains('" + ti.getName() + "')";
					
				}																	
			}
			else if (option == Button.B_CONTACTGROUP) {
				if ( item instanceof ContactGroupItem) {
					ContactGroupItem cgi= (ContactGroupItem) item;
					cmi= CONTEXT_MENU.CONTACT_GROUP;
				    itemLocator = "css=td[id$=title]:contains('" + cgi.fileAs + "')";
				}				
			}
			
			id = cmi.locator;
			locator = "css=div#zm__Contacts tr#"+ id;
			
			//  Make sure the context menu exists
			zWaitForElementPresent(locator) ;
			
			// Check if the item is enabled
			//if (sIsElementPresent("css=div[id=" + id + "][class*=ZDisabled]")) {
			//	throw new HarnessException("Tried clicking on "+ cmi.text +" but it was disabled ");
			//}
			
			// Mouse over the option
			sFocus(locator);
			sMouseOver(locator);
							
			//  Make sure the sub context menu exists			
			zWaitForElementPresent(itemLocator) ;
			
			// make sure the sub context menu enabled			
			zWaitForElementEnabled("div#" + itemLocator);
			
        } 

             
        zClickAt(itemLocator,"0,0");		
        zWaitForBusyOverlay();
       
        
    
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
				// select the item only
				zClickAt(contactLocator,"0,0");			    
			}

			else if (option == Button.B_NEW) {
				cmi=CONTEXT_MENU.CONTACT_NEW_EMAIL;
				page = new FormMailNew(MyApplication);	
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
			
		    zRightClickAt(contactLocator,"0,0");
		    
			
			id = cmi.locator;
			//locator = "id="+ id;
			locator = "css=div#zm__Contacts tr#"+ id;
			
			//  Make sure the context menu exists
			zWaitForElementPresent(locator) ;
			
			// Check if the item is enabled
			if (sIsElementPresent(locator + "[class*=ZDisabled]")) {
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
    	AbsPage page = null;	
		tracer.trace(action +" on contact = "+ contact);

		if ( action == Action.A_LEFTCLICK ) {
			//click
			zClick(contactLocator);
			zWaitForBusyOverlay();
			
			ArrayList<String> selectedContactArrayList=getSelectedContactLocator();			
	        String contactType = getContactType(selectedContactArrayList.get(0));
		
	        //check if it is a contact or a contact group item
		    if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
			  page = new DisplayContactGroup(MyApplication);		
		    }
		    else if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
			  page = new DisplayContact(MyApplication);
		    }
		    else {
			  throw new HarnessException(" Error: not support the contact type");						    	
		    }
			
		}
		else if ( action == Action.A_CHECKBOX) {
			//get the checkbox locator
//			contactLocator=contactLocator.substring(0, contactLocator.length()-2) + "1" + ") div.ImgCheckboxUnchecked";
			contactLocator=contactLocator + " div.ImgCheckboxUnchecked";
					
			//check the box			
			zClick(contactLocator);
			
			//zWaitForBusyOverlay();
						
			ArrayList<String> selectedContactArrayList=getSelectedContactLocator();			
	        String contactType = getContactType(selectedContactArrayList.get(0));
		
	        //check if it is a contact or a contact group item
		    if ( contactType.equals(ContactGroupItem.IMAGE_CLASS)) {
			  page = new DisplayContactGroup(MyApplication);		
		    }
		    else if (  contactType.equals(ContactItem.IMAGE_CLASS) ) {
			  page = new DisplayContact(MyApplication);
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
		else if (action == Action.A_DOUBLECLICK) {
		    sDoubleClick(contactLocator) ;		    
		    page = newFormSelected();   
		}
		else {
			throw new HarnessException("Action " + action + " not supported");
		}
		
		if (page != null) {
		    page.zWaitForActive();
		}
		return page;
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
