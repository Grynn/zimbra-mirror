package projects.zcs.ui;


/**
 * This Class have UI-level methods related to Address book app. e.g:
 * zVerifyContactExist,zVerifyContactNotExist
 * zClickContact,zDoubleClickContact,zDeleteContact etc.It also has static-final
 * variables that holds ids of icons on the AB-toolbar(like
 * zNewContactMenuIconBtn, zEditContactIconBtn etc). If you are dealing with the
 * toolbar buttons, use these icons since in vmware resolutions and in some
 * languages button-labels are not displayed(but just their icons)
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class ABApp extends AppPage {

	public static final String zNewContactMenuIconBtn = "id=zb__CNS__NEW_MENU_left_icon";
	public static final String zEditContactIconBtn = "id=zb__CNS__EDIT_left_icon";
	public static final String zEditContactIcon = "id=zb__CNS__EDIT";
	public static final String zDeleteContactIconBtn = "id=zb__CNS__DELETE_left_icon";
	public static final String zDeleteContactIcon = "id=zb__CNS__DELETE";
	public static final String zMoveContactIconBtn = "id=zb__CNS__MOVE_left_icon";
	public static final String zPrintContactMenuIconBtn = "id=zb__CNS__MOVE_left_icon";
	public static final String zTagContactMenuIconBtn = "id=zb__CNS__TAG_MENU_left_icon";
	public static final String zTagContactBtn_EditContact = "id=zb__CN__TAG_MENU_left_icon";
	public static final String zTagGroupBtn_EditGroup = "id=zb__GRP__TAG_MENU_left_icon";
	public static final String zViewContactMenuIconBtn = "id=zb__CNS__VIEW_MENU_left_icon";

	// right click menu icon btns
	public static final String zRtClickContactSearchMenuIconBtn = "id=zmi__Contacts__SEARCH_left_icon";
	public static final String zRtClickContactAdvSearchMenuIconBtn = "id=zmi__Contacts__BROWSE_left_icon";
	public static final String zRtClickContactNewEmailMenuIconBtn = "id=zmi__Contacts__NEW_MESSAGE_left_icon";
	public static final String zRtClickContactNewIMMenuIconBtn = "id=zmi__Contacts__IM_left_icon";
	public static final String zRtClickContactEditMenuIconBtn = "id=zmi__Contacts__CONTACT_left_icon";
	public static final String zRtClickContactTagMenuIconBtn = "id=zmi__Contacts__TAG_MENU_left_icon";
	public static final String zRtClickContactDeleteMenuIconBtn = "id=zmi__Contacts__DELETE_left_icon";
	public static final String zRtClickContactMoveMenuIconBtn = "id=zmi__Contacts__MOVE_left_icon";
	public static final String zRtClickContactPrintMenuIconBtn = "id=zmi__Contacts__PRINT_CONTACT_left_icon";

	/**
	 * @param contactName
	 *            : name of the contact that is to be deleted
	 * @param Type
	 *            : the type of delete either using Toolbar or
	 *            rightClck.Type="ToolbarDelete/RightClickDelete"
	 * @throws Exception
	 */
	public static void zDeleteContactAndVerify(String contactName, String type)
			throws Exception {
		obj.zContactListItem.zClick(contactName);
		if (type.equals("ToolbarDelete")) {
			obj.zButton.zClick(zDeleteContactIconBtn);
		} else if (type.equals("RightClickDelete")) {
			obj.zContactListItem.zRtClick(contactName);
			obj.zMenuItem.zClick(zRtClickContactDeleteMenuIconBtn);
		}
		obj.zContactListItem.zNotExists(contactName);
	}

	/**
	 * This method is to test whether the menu item is exist and/or is enabled
	 * 
	 * @param enabledItemsSeparatedByComma
	 *            : name of the menu items to checked as is enable
	 * @param disabledItemsSeparatedByComma
	 *            :name of the menu items to checked as is disable
	 * @param ignoreContext
	 *            :if true then check for menu item exist or not : if false then
	 *            check for menu item is enable or not
	 */
	public static void zVerifyAllMenuItems(String enabledItemsSeparatedByComma,
			String disabledItemsSeparatedByComma, String ignoreContext) {
		String[] enabledArray = enabledItemsSeparatedByComma.split(",");
		for (int i = 0; i < enabledArray.length; i++) {
			if (ignoreContext == "false") {
				obj.zMenuItem.zIsEnabled(enabledArray[i]);
			} else {
				obj.zMenuItem.zExistsDontWait(enabledArray[i]);
			}
		}
	}

	/**
	 * To select the contact and click on move either from toolbar or right
	 * click menu
	 * 
	 * @param contactName
	 * @param type
	 *            : "ToolbarMove" for move using toolbar btn/ "RightClickMove"
	 *            for move using rght click
	 * @throws Exception
	 */
	public static void zSelectAndClickMove(String contactName, String type)
			throws Exception {
		obj.zContactListItem.zClick(contactName);
		if (type.equals("ToolbarMove")) {
			obj.zButton.zClick(zMoveContactIconBtn);
		} else if (type.equals("RightClickMove")) {
			obj.zContactListItem.zRtClick(contactName);
			obj.zMenuItem.zClick(zRtClickContactMoveMenuIconBtn);
		}
	}

	/**
	 * To move contact to other addressbook folder and verify
	 * 
	 * @param contactName
	 * @param targetAB
	 *            : name of the AB to be moved in
	 * @param type
	 *            :"ToolbarMove" for move using toolbar btn/ "RightClickMove"
	 *            for move using rght click
	 * @throws Exception
	 */
	public static void zMoveContactAndVerify(String contactName,
			String targetAB, String type) throws Exception {
		zSelectAndClickMove(contactName, type);
		obj.zFolder.zClickInDlg(targetAB);
		obj.zButton.zClickInDlg(localize(locator.ok));
		obj.zFolder.zClick(targetAB);
		obj.zContactListItem.zExistsDontWait(contactName);
	}

	public static void zCreateContactGroup(String groupName,
			String commaSeparatedMembers) throws Exception {
		obj.zButtonMenu.zClick(page.zABCompose.zNewMenuDropdownIconBtn);
		obj.zMenuItem.zClick(localize(locator.group));
		obj.zEditField.zType(
				getNameWithoutSpace(localize(locator.groupNameLabel)),
				groupName);
		String membersArray[];
		membersArray = commaSeparatedMembers.split(",");
		for (int i = 0; i <= membersArray.length - 1; i++) {
			obj.zEditField.zType(localize(locator.findLabel), membersArray[i]
					.trim());
			obj.zButton.zClick(localize(locator.search), "2");
			Thread.sleep(2500);
			if (currentBrowserName.contains("Safari")) {
				obj.zButton.zClick(localize(locator.search), "2");
				Thread.sleep(1000);
			}
			obj.zListItem.zDblClickItemInSpecificList(membersArray[i], "2");
			obj.zButton.zClick(localize(locator.add));
		}
		obj.zButton.zClick(localize(locator.save), "2");
		obj.zContactListItem.zExists(groupName);
	}
}
