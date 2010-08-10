package projects.zcs.ui;

import framework.util.ZimbraSeleniumProperties;
import projects.zcs.tests.CommonTest;

/**
 * This Class have UI-level methods related composing a document and verifying
 * the mail's contents. e.g: zNavigateToDocuments,etc.
 * It also has static-final variables that holds ids of icons on the compose-toolbar(like zNewNotebookIconBtn,
 * zNewPageIconBtn etc). If you are dealing with the toolbar buttons, use
 * these icons since in vmware resolutions and in some languages button-labels
 * are not displayed(but just their icons)
 * 
 * @author Prashant Jaiswal
 * 
 */
/**
 * @author VICKY JAISWAL
 * 
 */
@SuppressWarnings("static-access")
public class DocumentCompose extends CommonTest {
	public static final String zDocumentTabIconBtn = "id=zb__App__Notebook_left_icon";
	public static final String zNewPageIconBtn = "id=zb__NBP__NEW_MENU_left_icon";
	public static final String zEditPageIconBtn = "id=zb__NBP__EDIT_left_icon";
	public static final String zDeletePageIconBtn = "id=zb__NBP__DELETE_left_icon";
	public static final String zPrintPageIconBtn = "id=zb__NBP__PRINT_left_icon";
	public static final String zTagPageIconBtn = "id=zb__NBP__TAG_MENU_left_icon";

	public static final String zSavePageIconBtn = "id=zb__NBPE__SAVE_left_icon";
	public static final String zClosePageIconBtn = "id=zb__NBPE__CLOSE_left_icon";

	public static final String zRefreshIconBtn = "id=zb__NBP__REFRESH_left_icon";

	// ===========================
	// NAVIGATE METHODS
	// ===========================

	/**
	 * Waits for 2000ms and then Clicks on Document Tab
	 * 
	 * @throws Exception
	 */
	public static void zNavigateToDocument() throws Exception {
		zGoToApplication("Documents");
	}

	/**
	 * Logs in and navigates to Document
	 * 
	 * @param username
	 *            :to pass the user name to be logged in
	 * @return
	 * @throws Exception
	 */
	public static String zLoginAndNavigateToDocument(String username)
			throws Exception {
		page.zLoginpage.zLoginToZimbraAjax(username);
		zNavigateToDocument();
		return username;
	}

	/**
	 * @param newNotebookName
	 * @param color
	 *            : name of color.localize it before passing
	 * @param targetFolder
	 *            :pass "" to create notebook at the topmost level.i.e not as
	 *            sub folder of any notebook. localize it before passing
	 * @throws Exception
	 */
	public static void zCreateNewNotebook(String newNotebookName, String color,
			String targetFolder) throws Exception {
		zWaitTillObjectExist(
				"button",
				replaceUserNameInStaticId(DocumentApp.zNewNotebookOverviewPaneIcon));
		obj.zButton
				.zRtClick(replaceUserNameInStaticId(DocumentApp.zNewNotebookOverviewPaneIcon));
		obj.zMenuItem.zClick(localize(locator.newNotebook));
		// zNewNotebookIconBtn); the icon ID is same for NewNotebook Folder and
		// for NewFolder.hence using Locator instead of ID
		obj.zEditField.zTypeInDlg(localize(locator.nameLabel), newNotebookName);
		if (!color.equals("")) {
			obj.zFeatureMenu.zClick(localize(locator.colorLabel));
			obj.zMenuItem.zClick(color);
		}
		if (!targetFolder.equals("")) {
			obj.zFolder.zClickInDlg(targetFolder);
		}
		obj.zButton.zClickInDlg(localize(locator.ok));

		obj.zFolder.zExists(newNotebookName);
	}

	/**
	 * @param pageName
	 *            : name of the page to be created
	 * @param bodyContent
	 *            : content of the page body
	 */
	public static void zEnterBasicPageData(String pageName, String bodyContent) {
		if (pageName != "")
			if (ZimbraSeleniumProperties.getStringProperty("locale").equals("fr")
					&& ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
				obj.zEditField.zType(localize(locator.page), pageName);
			} else {
				obj.zEditField.zType(localize(locator.pageLabel), pageName);
			}
		if (bodyContent != "")
			obj.zEditor.zType(bodyContent);

	}

	/**
	 * To create a page with pageName and some text in body
	 * 
	 * @param pageName
	 * @param bodyContent
	 * @throws Exception
	 */
	public static void zCreateBasicPage(String pageName, String bodyContent)
			throws Exception {
		zCreatePageInSpecificNotebook("", pageName, bodyContent);
	}

	/**
	 * To create page in specific notebook folder. If notebook not specified
	 * then it will create the page in default notebook.
	 * 
	 * @param notebookName
	 * @param pageName
	 * @param bodyContent
	 * @throws Exception
	 */
	public static void zCreatePageInSpecificNotebook(String notebookName,
			String pageName, String bodyContent) throws Exception {
		if (!notebookName.equals("")) {
			obj.zFolder.zClick(notebookName);
		}
		obj.zButton.zClick(zNewPageIconBtn);
		zWaitTillObjectExist("button", zSavePageIconBtn);
		zEnterBasicPageData(pageName, bodyContent);
		obj.zButton.zClick(zSavePageIconBtn);
		Thread.sleep(1500);
		obj.zButton.zClick(zClosePageIconBtn);
		Thread.sleep(1500);
		String isDlgExists = obj.zDialog
				.zExistsDontWait(localize(locator.warningMsg));
		if (isDlgExists.equals("true")) {
			obj.zButton.zClickInDlgByName(localize(locator.yes),
					localize(locator.warningMsg));
		}
		zWaitTillObjectExist("button", zRefreshIconBtn);
		obj.zButton.zClick(zRefreshIconBtn);
	}

	/**
	 * To select a page and click on Edit either using Toobar Edit Button or
	 * Edit Link
	 * 
	 * @param pageName
	 * @param type
	 *            : "Toolbar" for edit using toolbar btn /"LinkEdit" for edit
	 *            using Edit link
	 * @throws Exception
	 */
	public static void zSelectPageAndClickEdit(String pageName, String type)
			throws Exception {
		if (type.equals("Toolbar")) {
			obj.zDocumentPage.zClick(pageName);
			Thread.sleep(1000);
			obj.zButton.zClick(zEditPageIconBtn);
		} else if (type.equals("LinkEdit")) {
			Thread.sleep(1500);
			obj.zDocumentPage.zClick(pageName, localize(locator.edit));
			Thread.sleep(1500);
		}
	}

	/**
	 * To modify the page name and body of the page
	 * 
	 * @param noteBookName
	 * @param pageName
	 * @param newPageName
	 * @param newBodyContent
	 * @param type
	 *            : "Toolbar" for edit using toolbar btn /"LinkEdit" for edit
	 *            using Edit link
	 * @throws Exception
	 */
	public static void zModifyPageNameAndBody(String noteBookName,
			String pageName, String newPageName, String newBodyContent,
			String type) throws Exception {
		obj.zFolder.zClick(noteBookName);
		zSelectPageAndClickEdit(pageName, type);
		if (newPageName.equals(pageName) || newPageName != "")
			zEnterBasicPageData(pageName, newBodyContent);
		else
			zEnterBasicPageData(newPageName, newBodyContent);
		obj.zButton.zClick(zSavePageIconBtn);
		Thread.sleep(1500);
		obj.zToastAlertMessage.zAlertMsgExists(localize(locator.pageSaved),
				"Page Saved message should be displayed");
		obj.zButton.zClick(zClosePageIconBtn);
		Thread.sleep(1000);
		obj.zButton.zClick((page.zDocumentCompose.zRefreshIconBtn));
		Thread.sleep(1000);
	}

	/**
	 * To verify the page modification
	 * 
	 * @param noteBookName
	 * @param pageName
	 * @param newPageName
	 * @param modifiedBodyContent
	 * @return
	 * @throws Exception
	 */
	public static boolean zVerifyEditPage(String noteBookName, String pageName,
			String newPageName, String modifiedBodyContent) throws Exception {
		String actualPageName = null;
		String actualBodyContent = null;
		boolean flag = true;
		obj.zFolder.zDblClick(noteBookName);
		zSelectPageAndClickEdit(pageName, "LinkEdit");

		if (newPageName != "") {
			actualPageName = obj.zEditField
					.zGetInnerText(localize(locator.page));
			if (!actualPageName.equals(newPageName))
				flag = false;
		}

		if (modifiedBodyContent != "") {
			actualBodyContent = obj.zEditor.zGetInnerText("");
			if (!actualBodyContent.contains(modifiedBodyContent))
				flag = false;
		}
		if (flag == true)
			return true;
		else
			return false;
	}
}