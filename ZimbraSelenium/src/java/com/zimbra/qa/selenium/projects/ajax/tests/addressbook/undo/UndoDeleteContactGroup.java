package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.undo;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;

public class UndoDeleteContactGroup extends AjaxCommonTest {
	public UndoDeleteContactGroup() {
		logger.info("New " + UndoDeleteContactGroup.class.getCanonicalName());
		// All tests start at the Address page
		super.startingPage = app.zPageAddressbook;
		// Enable user preference checkboxes
		super.startingAccountPreferences = new HashMap<String, String>() {
			private static final long serialVersionUID = -263733102718446576L;
			{
				put("zimbraPrefShowSelectionCheckbox", "TRUE");
			}
		};

	}

	@Test(description = "Undone deleted a contact group", groups = { "functional" })
	public void UndoDeleteContactGroup_01() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		// delete contact group by click Delete button on toolbar
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		// Click undo from the toaster message
		Toaster toast = app.zPageMain.zGetToaster();		
		toast.zClickUndo();

		app.zPageAddressbook.zRefresh();

		//Verify contact group come back into Contacts folder		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
		ZAssert.assertNotNull(actual, "Verify the contact group exists");

	}

	@Test(description = "Undone deleted contact group by clicking Delete on Context Menu", groups = { "functional" })
	public void UndoDeleteContactGroup_02() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Refresh
		app.zPageAddressbook.zRefresh();

		// delete contact group by click Delete on Context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE,group.getName());

		SleepUtil.sleepSmall();

		// Click undo from the toaster message
		Toaster toast = app.zPageMain.zGetToaster();		
		toast.zClickUndo();

		app.zPageAddressbook.zRefresh();

		//Verify contact group come back into Contacts folder		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
		ZAssert.assertNotNull(actual, "Verify the contact group exists");
	}

	@Test(description = "Undone deleted  contact group selected by checkbox", groups = { "functional" })
	public void UndoDeleteContactGroup_03()throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group.getName());

		// delete contact group by click Delete button on toolbar
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);
		SleepUtil.sleepSmall();

		// Click undo from the toaster message
		Toaster toast = app.zPageMain.zGetToaster();		
		toast.zClickUndo();

		app.zPageAddressbook.zRefresh();

		//Verify contact group come back into Contacts folder		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
		ZAssert.assertNotNull(actual, "Verify the contact group exists");

	}

	@Test(description = "undone deleted a contact group use shortcut Del", groups = { "functional" })
	public void UndoDeleteContactGroup_04() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// -- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		// delete contact group by click shortcut Del
		app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);

		// Click undo from the toaster message
		Toaster toast = app.zPageMain.zGetToaster();		
		toast.zClickUndo();

		app.zPageAddressbook.zRefresh();

		//Verify contact group come back into Contacts folder		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
		ZAssert.assertNotNull(actual, "Verify the contact group exists");
	}


	@Test(description = "Undone deleted multiple contact groups at once", groups = { "functional" })
	public void UndoDeleteContactGroup_05() throws HarnessException {

		// Create a contact group
		ContactGroupItem group1 = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		ContactGroupItem group2 = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		ContactGroupItem group3 = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// -- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group1.getName());
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group2.getName());
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group3.getName());

		// delete contact group by click Delete button on toolbar
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		// Click undo from the toaster message
		Toaster toast = app.zPageMain.zGetToaster();		
		toast.zClickUndo();

		app.zPageAddressbook.zRefresh();

		//Verify 3 contact groups are come back into Contacts folder

		ContactGroupItem actual1 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group1.getName());
		ZAssert.assertNotNull(actual1, "Verify the contact group exists");
		ContactGroupItem actual2 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group2.getName());
		ZAssert.assertNotNull(actual2, "Verify the contact group exists");
		ContactGroupItem actual3 = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group3.getName());
		ZAssert.assertNotNull(actual3, "Verify the contact group exists");

	}

	@Test(description = "Undone Deleted contact + contact group at once", groups = { "functional" })
	public void UndoDeleteContactGroup_06() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());
		ContactItem contact = ContactItem.createContactItem(app.zGetActiveAccount());

		// -- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group.getName());
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, contact.getName());

		// delete contact group by click Delete button on toolbar
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		// Click undo from the toaster message
		Toaster toast = app.zPageMain.zGetToaster();		
		toast.zClickUndo();

		app.zPageAddressbook.zRefresh();

		//Verify contact group as well as contact come back into Contacts folder		
		ContactGroupItem actual = ContactGroupItem.importFromSOAP(app.zGetActiveAccount(), "is:anywhere #nickname:"+ group.getName());
		ZAssert.assertNotNull(actual, "Verify the contact group is not deleted from the addressbook");

		ContactItem actual1 = ContactItem.importFromSOAP(app.zGetActiveAccount(), "#firstname:"+ contact.firstName);
		ZAssert.assertNotNull(actual1, "Verify the contact is not deleted from the addressbook");

	}

}

