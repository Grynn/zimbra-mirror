package projects.html.ui;

import projects.html.tests.CommonTest;

/**
 * This Class have UI-level methods related to preference-general tab
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class GeneralPrefUI extends CommonTest {
	public static final String zFindEditFiled = "id=searchField";
	public static final String zSearchBtn = "name=search";

	// general tab related id's
	public static final String zAdvancedRadioBtn = "id=clientA";
	public static final String zStandardRadioBtn = "id=clientS";
	public static final String zThemesDropdown = "id=skinPref";
	public static final String zTimezoneDropdown = "id=timeZone";

	public static final String zIncludeJunkFolderChkBox = "id=zimbraPrefIncludeSpamInSearch";
	public static final String zIncludeTrashFolderChkBox = "id=zimbraPrefIncludeTrashInSearch";
	public static final String zAlwaysShowSrchStrngChkBox = "id=zimbraPrefShowSearchString";

	// Change pwd window related id's
	public static final String zOldPassword = "id=oldPassword";
	public static final String zNewPassword = "id=newPassword";
	public static final String zConfirm = "id=confirm";

	/**
	 * To navigate to preference general tab
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToPrefGeneral() throws Exception {
		obj.zButton.zClick("id=TAB_OPTIONS");
		// obj.zTab.zClick(localize(locator.preferences));
		Thread.sleep(2000);
		obj.zTab.zClick(localize(locator.general));
		Thread.sleep(1000);
	}

	/**
	 * To navigate to pref general and to select the search folder to search in
	 * 
	 * @param searchFolder
	 * @throws Exception
	 */
	public static void zNavigateToPrefGenralAndSelectSearchFolder(
			String searchFolder) throws Exception {
		zNavigateToPrefGeneral();
		if (searchFolder.equals("Junk")) {
			obj.zCheckbox.zClick(zIncludeJunkFolderChkBox);
		} else if (searchFolder.equals("Trash")) {
			obj.zCheckbox.zClick(zIncludeTrashFolderChkBox);
		}
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);

	}

	/**
	 * To navigate to pref general and to select always show search string
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToPrefGenralAndSelectAlwaysShowSrchString()
			throws Exception {
		zNavigateToPrefGeneral();
		obj.zCheckbox.zClick(zAlwaysShowSrchStrngChkBox);
		Thread.sleep(500);
		obj.zButton.zClick(page.zAccPref.zSaveIconBtn);
		Thread.sleep(1000);
	}

	/**
	 * To navigate to change password window
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToChangePasswordWindow() throws Exception {

		zNavigateToPrefGeneral();
		Thread.sleep(1000);
		selenium.click("link=" + localize(locator.changePassword));
		Thread.sleep(2000);
	}

	/**
	 * To enter change password data
	 * 
	 * @param oldPwd
	 * @param newPwd
	 * @param confirmPwd
	 */
	public static void zEnterChangePWData(String oldPwd, String newPwd,
			String confirmPwd) {

		selenium.selectWindow("_blank");
		obj.zPwdField.zType(zOldPassword, oldPwd);
		obj.zPwdField.zType(zNewPassword, newPwd);
		obj.zPwdField.zType(zConfirm, confirmPwd);

	}

	/**
	 * To search by entering search data in main search field at the top
	 * 
	 * @param itemToBeSearched
	 */
	public static void zSearchUsingMainSearchField(String itemToBeSearched) {
		obj.zEditField.zType(zFindEditFiled, itemToBeSearched);
		obj.zButton.zClick(zSearchBtn);

	}
}