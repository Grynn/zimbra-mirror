package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.contactgroups;


import java.util.*;


import org.testng.annotations.Test;

import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.ui.*;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.*;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.FormMailNew;

public class ContextMenuContactGroup extends AjaxCommonTest  {
	public ContextMenuContactGroup() {
		logger.info("New "+ ContextMenuContactGroup.class.getCanonicalName());
		
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;

		super.startingAccountPreferences = null;		
		
	}
	
	private ContactGroupItem createSelectAContactGroup(String ... tagIdArray) throws HarnessException {
		        
        ContactGroupItem group = ContactGroupItem.createUsingSOAP(app, tagIdArray);
        
        // Refresh the view, to pick up the new contact
        FolderItem contactFolder = FolderItem.importFromSOAP(app.zGetActiveAccount(), "Contacts");
        app.zTreeContacts.zTreeItem(Action.A_LEFTCLICK, contactFolder);
                 
        return group;		
	
		
	}


	@Test(	description = "Right click a contact group to show a menu",
			groups = { "smoke" })
	public void ShowContextMenu() throws HarnessException {
		
		ContactGroupItem group =  createSelectAContactGroup();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		// Select the item
        // Right click to show the menu
        ContextMenu contextMenu= (ContextMenu) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, group.fileAs); 
      
        // Get the list of context menu items   
        ArrayList <ContextMenuItem> list = contextMenu.zListGetContextMenuItems(PageAddressbook.CONTEXT_MENU.class);
        
         //verify all items in the context menu list
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_SEARCH),"Verify contact search in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_NEW_EMAIL),"Verify new email in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_EDIT),"Verify edit contact group  in context menu");
        
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_FORWARD),"Verify forward email in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_TAG),"Verify tag group option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_DELETE),"Verify delete option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_MOVE),"Verify move option in context menu");
        ZAssert.assertTrue(list.contains(PageAddressbook.CONTEXT_MENU.CONTACT_PRINT),"Verify print option in context menu");

        //Verify only Edit Group, New Email, Tag Group, Delete, Move, Print enabled
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_NEW_EMAIL),"Verify new email is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_EDIT),"Verify edit contact is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_TAG),"Verify tag group option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_DELETE),"Verify delete option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_MOVE),"Verify move option is enabled");
        ZAssert.assertTrue(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_PRINT),"Verify print option is enabled");

        //Verify Find Email & Advanced SEarch is disable 
        ZAssert.assertFalse(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_SEARCH),"Verify contact search is disabled");
        ZAssert.assertFalse(contextMenu.isEnable(PageAddressbook.CONTEXT_MENU.CONTACT_FORWARD),"Verify forward email is disabled");
        
        
    }
	
	@Test(	description = "Right click then click New Email",
			groups = { "smoke" })
	public void NewEmail() throws HarnessException {
		ContactGroupItem group =  createSelectAContactGroup();		
		
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

		//Click New Email
        FormMailNew formMailNew = (FormMailNew) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_NEW, group.fileAs);        
        
        //Verify Form New mail is active
        ZAssert.assertTrue(formMailNew.zIsActive(),"Verify Form New Mail is active");
        
        //Verify group's emails displayed in the "To" field
        for (int i=0; i<group.dlist.size(); i++) {
           ZAssert.assertTrue(app.zPageAddressbook.sGetText(FormMailNew.Locators.zBubbleToField).contains(group.dlist.get(i).email), "Verify contact email displayed in field To - expected " + group.dlist.get(i) + " - was " + app.zPageAddressbook.sGetText(FormMailNew.Locators.zBubbleToField));
        }
        
        //TODO: Verify send email
	}
	

/*
	@Test(	description = "Right click then click Print",
			groups = { "smoke-not-run-for-now" })	
	public void Print() throws HarnessException {
		ContactItem contactItem = createSelectARandomContactItem();
		GeneralUtility.syncDesktopToZcsWithSoap(app.zGetActiveAccount());

        PagePrint pagePrint = (PagePrint) app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_PRINT, contactItem.fileAs);        
                
        //close Print Dialog 
        pagePrint.cancelPrintDialog();
        
        //verify first,last,email displayed in Print View
	    Assert.assertTrue(pagePrint.isContained("css=td[class='contactHeader']", contactItem.lastName + ", " + contactItem.firstName )," expected: " + contactItem.lastName + "," + contactItem.firstName + " not displayed in Print Page" + " was:"  );

	    Assert.assertTrue(pagePrint.isContained("css=td[class='contactOutput']", contactItem.email ), contactItem.firstName + " not displayed in Print Page");
	    
	}

*/
	

}

