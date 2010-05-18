package projects.zcs.ui;

import projects.zcs.tests.CommonTest;

/**
 * This Class have UI-level methods related to Documents app like
 * zSelectPageAndClickDelete() etc. It also has static-final variables that
 * holds ids of icons on the Documents-toolbar(like zEditPageIconBtn,
 * zDeletePageIconBtn etc).If you are dealing with the toolbar buttons, use
 * these icons since in vmware resolutions and in some languages button-labels
 * are not displayed(but just their icons)
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class DocumentApp extends CommonTest {
	public static final String zNewNotebookOverviewPaneIcon = "id=ztih__main_Notebook__NOTEBOOK_textCell";
	public static final String zNotebookFolder = "id=zti__main_Notebook__12_textCell";

	public static final String zEditPageIconBtn = "id=zb__NBP__EDIT_left_icon";
	public static final String zDeletePageIconBtn = "id=zb__NBP__DELETE_left_icon";
	public static final String zPrintPageIconBtn = "id=zb__NBP__PRINT_left_icon";
	public static final String zTagPageIconBtn = "id=zb__NBP__TAG_MENU_left_icon";

	/**
	 * To select a page and click on delete either using toolbar or delete link
	 * 
	 * @param pageName
	 * @param type
	 *            : "ToolbarDelete" for deleting using toolbar delete btn /
	 *            "LinkDelete" to delete using delete link
	 * @throws Exception
	 */
	public static void zSelectPageAndClickDelete(String pageName, String type)
			throws Exception {
		if (type.equals("ToolbarDelete")) {
			obj.zDocumentPage.zClick(pageName);
			zWaitTillObjectExist("button", zDeletePageIconBtn);
			obj.zButton.zClick(zDeletePageIconBtn);
		} else if (type.equals("LinkDelete")) {
			obj.zDocumentPage.zClick(pageName, localize(locator.del));
		}
	}

	/**
	 * To delete a notebook page
	 * 
	 * @param noteBookName
	 * @param pageName
	 * @param type
	 *            : "ToolbarDelete" for deleting using toolbar delete btn /
	 *            "LinkDelete" to delete using delete link
	 * @throws Exception
	 */
	public static void zDeleteNotebookPage(String noteBookName,
			String pageName, String type) throws Exception {
		obj.zFolder.zClick(noteBookName);
		zSelectPageAndClickDelete(pageName, type);
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.confirmTitle));
	}

	/**
	 * To delete notebook folder
	 * 
	 * @param noteBookName
	 */
	public static void zDeleteNotebookFolder(String noteBookName) {
		obj.zFolder.zRtClick(noteBookName);
		obj.zMenuItem.zClick(localize(locator.del));
		obj.zButton.zClickInDlg(localize(locator.yes));
	}
}
