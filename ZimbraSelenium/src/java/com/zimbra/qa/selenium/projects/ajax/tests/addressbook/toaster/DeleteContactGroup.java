package com.zimbra.qa.selenium.projects.ajax.tests.addressbook.toaster;

import java.awt.event.KeyEvent;
import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.core.Bugs;
import com.zimbra.qa.selenium.framework.items.*;
import com.zimbra.qa.selenium.framework.items.FolderItem.SystemFolder;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.Toaster;

public class DeleteContactGroup extends AjaxCommonTest {
	public DeleteContactGroup() {
		logger.info("New " + DeleteContactGroup.class.getCanonicalName());

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

	@Test(description = "Delete a contact group by click Delete button on toolbar and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_01() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		// delete contact group by click Delete button on toolbar
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"1 contact group moved to Trash","Verify toast message: Contact group Moved to Trash");

	}

	@Test(description = "Delete a contact group by click Delete on Context Menu and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_02() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// -- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// delete contact group by click Delete on Context menu
		app.zPageAddressbook.zListItem(Action.A_RIGHTCLICK, Button.B_DELETE,
				group.getName());

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"1 contact group moved to Trash","Verify toast message: Contact group Moved to Trash");
	}

	@Test(description = "Delete a contact group selected by checkbox by click Delete button on toolbar and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_03()throws HarnessException {
		
		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// -- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_CHECKBOX, group.getName());

		// delete contact group by click Delete button on toolbar
		app.zPageAddressbook.zToolbarPressButton(Button.B_DELETE);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"1 contact group moved to Trash","Verify toast message: Contact group Moved to Trash");


	}

	@Test(description = "Delete a contact group use shortcut Del and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_04() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// -- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		// delete contact group by click shortcut Del
		app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_DELETE);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"1 contact group moved to Trash","Verify toast message: Contact group Moved to Trash");


	}

	@Test(description = "Delete a contact group use shortcut backspace and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_05() throws HarnessException {

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		// delete contact group by click shortcut Del
		app.zPageAddressbook.zKeyboardKeyEvent(KeyEvent.VK_BACK_SPACE);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"1 contact group moved to Trash","Verify toast message: Contact group Moved to Trash");
	}
	
	@Bugs(ids="78829")
	@Test(description = "Delete multiple contact groups at once and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_06() throws HarnessException {

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

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"3 contact groups moved to Trash","Verify toast message: 3 Contact groups Moved to Trash: Failing due to the bug http://bugzilla.zimbra.com/show_bug.cgi?id=78829");
	
	}

	@Test(description = "Delete contact + contact group at once and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_07() throws HarnessException {

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

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"2 contacts moved to Trash","Verify toast message: Contact group Moved to Trash");
	

	}

	@Test(description = "Move a contact group to folder Trash by expand Move dropdown then select Trash and verify toast msg", groups = { "functional" })
	public void DeleteContactGroupToastMsg_08() throws HarnessException {

		// The trash folder
		FolderItem trash = FolderItem.importFromSOAP(app.zGetActiveAccount(),SystemFolder.Trash);

		// Create a contact group
		ContactGroupItem group = ContactGroupItem.createContactGroupItem(app.zGetActiveAccount());

		// -- GUI

		// Refresh
		app.zPageAddressbook.zRefresh();

		// Select the contact group
		app.zPageAddressbook.zListItem(Action.A_LEFTCLICK, group.getName());

		// delete contact group by click Delete button on toolbar
		app.zPageAddressbook.zToolbarPressPulldown(Button.B_MOVE, trash);

		// Verifying the toaster message
		Toaster toast = app.zPageMain.zGetToaster();
		String toastMsg = toast.zGetToastMessage();
		ZAssert.assertStringContains(toastMsg,"1 contact group moved to Trash","Verify toast message: Contact group Moved to Trash");

	}

}
