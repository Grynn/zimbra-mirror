package projects.zcs.ui;

import java.io.File;

import framework.util.SleepUtil;

/**
 * @author Jitesh Sojitra
 * 
 *         This class have UI-level methods related to briefcase application
 */

@SuppressWarnings("static-access")
public class BriefcaseApp extends AppPage {
	public static String zNewBriefcaseOverviewPaneIcon = "id=ztih__main_Briefcase__BRIEFCASE_textCell";
	public static String zBriefcaseFolder = "id=zti__main_Briefcase__16_textCell";
	public static String zTrashFolder = "id=zti__main_Briefcase__3_textCell";
	public static final String zBriefcaseAppIconBtn = "id=zb__App__Briefcase_left_icon";
	public static final String zNewMenuIconBtn = "id=zb__BCC__NEW_MENU_left_icon";
	public static final String zUploadFileIconBtn = "id=zb__BCC__NEW_FILE_left_icon";
	public static final String zDeleteIconBtn = "id=zb__BCC__DELETE_left_icon";
	public static final String zDeleteBtn = "id=zb__BCC__DELETE";
	public static final String zMoveItemIconBtn = "id=zb__BCC__MOVE_left_icon";
	public static final String zTagItemIconBtn = "id=zb__BCC__TAG_MENU_left_icon";
	public static final String zViewIconBtn = "id=zb__BCC__VIEW_MENU_left_icon";
	public static final String zSendBtnIconBtn = "id=zb__BCC__SEND_FILE_left_icon";

	public static final String zNewDocumentIconBtn = "id=zb__BCC__NEW_DOC_left_icon";
	public static final String zNewSpreadsheetIconBtn = "id=zb__BCC__NEW_SPREADSHEET_left_icon";
	public static final String zNewPresentationIconBtn = "id=zb__BCC__NEW_PRESENTATION_left_icon";

	/**
	 * This method navigates to briefcase application
	 */

	public static void zGoToBriefcaseApp() throws Exception {
		zGoToApplication("Briefcase");
	}

	public static void zBriefcaseFileUpload(String filename,
			String OtherBFFolder) throws Exception {
		if (OtherBFFolder.equals("")) {
			obj.zFolder.zClick(replaceUserNameInStaticId(zBriefcaseFolder));
		} else {
			obj.zFolder.zClick(OtherBFFolder);
		}
		obj.zButton.zClick(zNewMenuIconBtn);
		zWaitTillObjectExist("dialog", localize(locator.uploadFileToBriefcase));
		File f = new File("src/java/projects/zcs/data/" + filename);
		String path = f.getAbsolutePath();
		obj.zBrowseField.zTypeInDlgWithKeyboard(localize(locator.uploadChoose),
				path, "1");
		obj.zButton.zClickInDlg(localize(locator.ok));
		String dlgExists;
		for (int i = 0; i <= 20; i++) {
			dlgExists = obj.zDialog
					.zExistsDontWait(localize(locator.uploadFileToBriefcase));
			SleepUtil.sleep(1000);
			if (dlgExists.equals("true")) {
				System.out.println(i);
			} else {
				break;
			}
		}

		if (OtherBFFolder.equals("")) {
			obj.zFolder.zClick(localize(locator.briefcase));
		} else {
			obj.zFolder.zClick(OtherBFFolder);
		}
	}

	public void zCreateNewBriefcaseFolder(String newBriefcaseFolder)
			throws Exception {
		obj.zButton.zRtClick(zNewBriefcaseOverviewPaneIcon);
		obj.zMenuItem.zClick(localize(locator.newBriefcase));
		SleepUtil.sleep(1000);
		obj.zFolder.zClickInDlgByName(localize(locator.folders),
				localize(locator.createNewBriefcaseItem));
		obj.zEditField.zTypeInDlgByName(localize(locator.name),
				newBriefcaseFolder, localize(locator.createNewBriefcaseItem));
		obj.zButton.zClickInDlgByName(localize(locator.ok),
				localize(locator.createNewBriefcaseItem));
		SleepUtil.sleep(1000);
	}
}