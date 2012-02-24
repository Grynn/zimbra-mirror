/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import java.util.ArrayList;
import java.util.List;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.items.FileItem;
import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.items.IOctListViewItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError.DialogErrorID;
import com.zimbra.soap.mail.type.Folder;

public class PageOctopus extends AbsTab {

	public static class Locators {
		public static final Locators zUserNamePullDown = new Locators(
				"css=div.user-display-name");
		public static final Locators zSignOutButton = new Locators(
		// "css=a.(headerLink signOutLink):contains(sign out)");
		// temporary omitting first letter in the locator's name
				"css=div.signOutLink:contains(ign out)");
		public static final Locators zSettingsButton = new Locators(
		// "css=a.(headerLink settingsLink):contains(settings)");
		// temporary omitting first letter in the locator's name
				"css=div.settingsLink:contains(ettings)");
		public static final Locators zTabMyFiles = new Locators(
				"css=div#octopus-tab-myfiles");
		public static final Locators zTabSharing = new Locators(
				"css=div#octopus-tab-share");
		public static final Locators zTabFavorites = new Locators(
				"css=div#octopus-tab-favorites");
		public static final Locators zTabHistory = new Locators(
				"css=div#octopus-tab-history");
		public static final Locators zTabTrash = new Locators(
				"css=div#octopus-tab-trash");
		public static final Locators zTabSearch = new Locators(
				"css=div#octopus-tab-search");
		public static final Locators zMyFilesListViewItems = new Locators(
				"css=div[class*=my-files-list-view]>div.my-files-list-item");
		public static final Locators zRenameInput = new Locators(
				"css=div[class*=edit-pane-panel-shim] input[class=field]");
		public static final Locators zLeaveThisSharedFolder = new Locators(
				"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains('Leave this Shared Folder')");
		public static final Locators zShareItem = new Locators(
				"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains(Share)");
		public static final Locators zFavoriteItem = new Locators(
				"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains(Favorite)");
		public static final Locators zNotFavoriteItem = new Locators(
				"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains('Not Favorite')");
		public static final Locators zRenameItem = new Locators(
				"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains(Rename)");
		public static final Locators zMoveItem = new Locators(
				"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains(Move)");
		public static final Locators zDeleteItem = new Locators(
				"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains(Delete)");

		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public PageOctopus(AbsApplication application) {
		super(application);

		logger.info("new " + PageOctopus.class.getCanonicalName());

	}

	public Toaster zGetToaster() throws HarnessException {
		return (new Toaster(this.MyApplication));
	}

	public DialogError zGetErrorDialog(DialogErrorID octopus) {
		return (new DialogError(octopus, this.MyApplication, this));
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// Look for the Sign Out button
		boolean present = sIsElementPresent(Locators.zUserNamePullDown.locator);

		if (!present) {
			logger.debug("zIsActive(): " + present);
			return (false);
		}
		logger.debug("isActive() = " + true);
		return (true);
	}

	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public void zNavigateTo() throws HarnessException {

		if (zIsActive()) {
			// This page is already active
			return;
		}

		// Login as the default account
		if (!((AppOctopusClient) MyApplication).zPageLogin.zIsActive()) {
			((AppOctopusClient) MyApplication).zPageLogin.zNavigateTo();
		}
		((AppOctopusClient) MyApplication).zPageLogin.zLogin(ZimbraAccount
				.AccountZWC());
		zWaitForActive();
	}

	/**
	 * Open url with logout attribute
	 * 
	 * @throws HarnessException
	 */
	public void zLogout() throws HarnessException {
		logger.debug("PageOctopus logout()");

		tracer.trace("Logout of the " + MyApplication.myApplicationName());

		zNavigateTo();

		// Click on the sign out button
		zToolbarPressPulldown(Button.B_USER_NAME, Button.O_SIGN_OUT);

		sWaitForPageToLoad();
		((AppOctopusClient) MyApplication).zPageLogin.zWaitForActive();

		((AppOctopusClient) MyApplication).zSetActiveAcount(null);

	}

	public AbsPage zToolbarPressPulldown(Button pulldown, Button option,
			String itemName) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null){
			throw new HarnessException("Pulldown cannot be null!");
		}
		if (option == null){
			throw new HarnessException("Option cannot be null!");
		}
		if (itemName == null){
			throw new HarnessException("Item name cannot be null!");
		}
		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_MY_FILES_LIST_ITEM) {

			pulldownLocator = Locators.zMyFilesListViewItems.locator
					+ ":contains(" + itemName
					+ ") span[class^=my-files-list-item-action-button]";

			if (!this.zWaitForElementPresent(pulldownLocator, "3000")){
				throw new HarnessException("Button is not present locator="
						+ pulldownLocator);
			}

			zClick(pulldownLocator);

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();

			if (option == Button.B_LEAVE_THIS_SHARED_FOLDER) {
				optionLocator = Locators.zLeaveThisSharedFolder.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}

				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				return page;
			} else if (option == Button.O_FOLDER_SHARE) {
				optionLocator = Locators.zShareItem.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}
				
				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				page = new DialogFolderShare(MyApplication, this);

				page.zWaitForActive();

				return page;
			} else if (option == Button.O_FILE_SHARE) {
				optionLocator = Locators.zShareItem.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}
				
				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				page = new DialogFileShare(MyApplication, this);

				page.zWaitForActive();

				return page;
			} else if (option == Button.O_FAVORITE) {
				optionLocator = Locators.zFavoriteItem.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}
				
				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				return page;
			} else if (option == Button.O_NOT_FAVORITE) {
				optionLocator = Locators.zNotFavoriteItem.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}
				
				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				return page;
			} else if (option == Button.O_RENAME) {
				optionLocator = Locators.zRenameItem.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}
				
				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				return page;
			} else if (option == Button.O_MOVE) {
				optionLocator = Locators.zMoveItem.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}

				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				page = new DialogMove(MyApplication, this);

				return page;
			} else if (option == Button.O_DELETE) {
				optionLocator = Locators.zDeleteItem.locator;

				if (!this.zWaitForElementPresent(optionLocator, "2000")){
					throw new HarnessException("Button is not present locator="
							+ optionLocator);
				}
				
				this.sClickAt(optionLocator, "0,0");

				// If the app is busy, wait for it to become active
				this.zWaitForBusyOverlayOctopus();

				return page;
			} else {
				logger.info("no logic defined for " + option);
			}
		} else {
			logger.info("no logic defined for " + pulldown + "/" + option);
		}

		/*
		 * // Default behavior if (pulldownLocator != null) {
		 * 
		 * // Make sure the locator exists if
		 * (!this.sIsElementPresent(pulldownLocator)) { throw new
		 * HarnessException("Button " + pulldown + " option " + option +
		 * " pulldownLocator " + pulldownLocator + " not present!"); }
		 * 
		 * zClick(pulldownLocator);
		 * 
		 * // If the app is busy, wait for it to become active
		 * zWaitForBusyOverlay(); }
		 * 
		 * if (optionLocator != null) {
		 * 
		 * // Make sure the locator exists if
		 * (!this.sIsElementPresent(optionLocator)) { throw new
		 * HarnessException(optionLocator + " not present!"); }
		 * 
		 * this.sClick(optionLocator);
		 * 
		 * // If the app is busy, wait for it to become active
		 * zWaitForBusyOverlay(); }
		 * 
		 * // If we click on pulldown/option and the page is specified, then //
		 * wait for the page to go active if (page != null) {
		 * page.zWaitForActive(); }
		 * 
		 * // Return the specified page, or null if not set
		 */
		return (page);
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
			throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null){
			throw new HarnessException("Pulldown cannot be null!");
		}
		
		if (option == null){
			throw new HarnessException("Option cannot be null!");
		}
		
		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (pulldown == Button.B_USER_NAME) {
			pulldownLocator = Locators.zUserNamePullDown.locator;

			if (option == Button.O_SIGN_OUT) {
				optionLocator = Locators.zSignOutButton.locator;

				// sGetCssCount("css=div[class*=my-files-list-view]>div.my-files-list-item");
				// this.zClick(Locators.zMyFilesListView.locator +
				// ">div.my-files-list-item:last-child");
				// this.zClick(Locators.zMyFilesListView.locator +
				// ">div.my-files-list-item:nth-child(1)");
				
				zClick(pulldownLocator);

				zWaitForElementPresent(optionLocator,"3000");

				sMouseUp(optionLocator);

				return page;

			} else if (option == Button.O_SETTINGS) {
				optionLocator = Locators.zSettingsButton.locator;
				page = new DialogSettings(MyApplication, this);
				zClick(pulldownLocator);

				zWaitForElementPresent(optionLocator,"3000");

				sMouseUp(optionLocator);

				return page;
			} else {
				logger.info("no logic defined for " + option);
			}
		} else {
			logger.info("no logic defined for " + pulldown + "/" + option);
		}

		// default behavior
		zClick(pulldownLocator);

		zWaitForBusyOverlay();

		zClick(optionLocator);

		return page;
	}

	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressButton(" + button + ")");

		tracer.trace("Press the " + button + " button");

		if (button == null){
			throw new HarnessException("Button cannot be null!");
		}
		
		// Default behavior variables
		//
		String locator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		//

		if (button == Button.B_TAB_MY_FILES) {
			locator = Locators.zTabMyFiles.locator;
			page = new PageMyFiles(MyApplication);
		} else if (button == Button.B_TAB_SHARING) {
			locator = Locators.zTabSharing.locator;
			page = new PageSharing(MyApplication);
		} else if (button == Button.B_TAB_FAVORITES) {
			locator = Locators.zTabFavorites.locator;
			page = new PageFavorites(MyApplication);
		} else if (button == Button.B_TAB_HISTORY) {
			locator = Locators.zTabHistory.locator;
			page = new PageHistory(MyApplication);
		} else if (button == Button.B_TAB_TRASH) {
			locator = Locators.zTabTrash.locator;
			page = new PageTrash(MyApplication);
		} else if (button == Button.B_TAB_SEARCH) {
			locator = Locators.zTabSearch.locator;
			page = new PageSearch(MyApplication);
		} else if (button == Button.B_SETTINGS) {
			locator = Locators.zSettingsButton.locator;
			page = new DialogSettings(MyApplication, this);
		} else {
			throw new HarnessException("no logic defined for button " + button);
		}

		if (locator == null) {
			throw new HarnessException("locator was null for button " + button);
		}

		// Default behavior, process the locator by clicking on it
		//

		// Make sure the button exists
		if (!this.sIsElementPresent(locator)){
			throw new HarnessException("Button is not present locator="
					+ locator + " button=" + button);
		}
		
		// Click it
		this.zClick(locator);

		// If the app is busy, wait for it to become active
		zWaitForBusyOverlay();

		if (page != null){
			page.zWaitForActive();
		}
		
		return (page);
	}

	public boolean zIsFolderParent(FolderItem folderItem, String childFolderName)
			throws HarnessException {
		if (folderItem == null || childFolderName == null)
			throw new HarnessException("folder or item cannot be null");

		boolean found = false;
		FolderItem childFolderItem;

		for (int i = 0; i < 5; i++) {
			childFolderItem = FolderItem.importFromSOAP(
					MyApplication.zGetActiveAccount(), childFolderName);
			if (childFolderItem != null
					&& folderItem.getId().contentEquals(
							childFolderItem.getParentId())){
				return true;
			}
			
			SleepUtil.sleepVerySmall();
		}
		return found;
	}

	public boolean zIsFolderChild(FolderItem folderItem, String parentFolderName)
			throws HarnessException {
		if (folderItem == null || parentFolderName == null){
			throw new HarnessException("folder or item cannot be null");
		}
		
		boolean found = false;
		FolderItem parentFolderItem;

		for (int i = 0; i < 5; i++) {
			parentFolderItem = FolderItem.importFromSOAP(
					MyApplication.zGetActiveAccount(), parentFolderName);
			if (parentFolderItem != null) {
				List<Folder> subfolders = parentFolderItem.getSubfolders();
				String name = folderItem.getName();
				for (Folder folder : subfolders){
					if (folder.getName().contains(name)) {
						return true;
					}
				}
			}
			SleepUtil.sleepVerySmall();
		}
		return found;
	}

	public boolean zIsItemInCurentListView(String itemName)
			throws HarnessException {
		if (itemName == null)
			throw new HarnessException("item cannot be null");

		boolean found = false;
		for (int i = 0; i < 5; i++) {
			List<IOctListViewItem> items = zGetListViewItems();

			for (IOctListViewItem item : items){
				if (item.getListViewName().contains(itemName)) {
					return (true);
				}
			}
			
			SleepUtil.sleepVerySmall();
		}
		return found;
	}
	
	public List<FolderItem> zGetListViewFolderItems() throws HarnessException {
		List<FolderItem> folderItems = new ArrayList<FolderItem>();
		List<IOctListViewItem> items = zGetListViewItems();
		for(IOctListViewItem f : items){
			if(f instanceof FolderItem){
				folderItems.add((FolderItem) f);
			}
		}
		return folderItems;
	}
	
	public List<FileItem> zGetListViewFileItems() throws HarnessException {
		List<FileItem> fileItems = new ArrayList<FileItem>();
		List<IOctListViewItem> items = zGetListViewItems();
		for(IOctListViewItem f : items){
			if(f instanceof FileItem){
				fileItems.add((FileItem) f);
			}
		}
		return fileItems;
	}
	
	public List<IOctListViewItem> zGetListViewItems() throws HarnessException {
		String locator = Locators.zMyFilesListViewItems.locator;

		int count = sGetCssCount(locator);

		List<IOctListViewItem> items = new ArrayList<IOctListViewItem>();
		for (int i = 1; i <= count; i++) {
			
			IOctListViewItem item = null;
			
			String icon = this.sGetAttribute(locator + ":nth-child(" + i + ") span.my-files-list-item-icon>span@class");
			if ( icon.equalsIgnoreCase("ImgFolder") || icon.equalsIgnoreCase("ImgSharedMailFolder") ) {
				item = new FolderItem();
			} else {
				item = new FileItem();
			}
			item.setListViewIcon(icon);
			
			String name = this.sGetText(locator + ":nth-child(" + i + ") span.my-files-list-item-name");
			item.setListViewName(name);
			
			items.add(item);
		}
		return items;
	}

	public String searchFile(String fileName) throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();
		account.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
				+ "<query>" + fileName + "</query>" + "</SearchRequest>");

		String id = account.soapSelectValue("//mail:SearchResponse//mail:doc",
				"id");

		return id;
	}

	public String searchFileIn(String fileName, String folderName)
			throws HarnessException {
		ZimbraAccount account = MyApplication.zGetActiveAccount();
		account.soapSend("<SearchRequest xmlns='urn:zimbraMail' types='document'>"
				+ "<query>in:"
				+ folderName
				+ " "
				+ fileName
				+ "</query>"
				+ "</SearchRequest>");

		String id = account.soapSelectValue("//mail:SearchResponse//mail:doc",
				"id");

		return id;
	}

	public boolean waitForResponse(ZimbraAccount account, String request, String pattern, int timeout) throws HarnessException {
		String response;
		boolean found = false;
		for(int i = 0; i < timeout; i ++){
			account.soapSend(request);		
			response = account.soapLastResponse();
			if(response != null && response.contains(pattern)){
				found = true;
				break;
			}
			logger.info(i + " soap response doesn't contain: " + pattern);
			SleepUtil.sleepSmall();
		}
		return found;
	}
	
	public void trashItemUsingSOAP(String itemId, ZimbraAccount account)
			throws HarnessException {
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + itemId + "' op='trash'/>"
				+ "</ItemActionRequest>");
	}

	public void deleteItemUsingSOAP(String itemId, ZimbraAccount account)
			throws HarnessException {
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + itemId + "' op='delete'/>"
				+ "</ItemActionRequest>");
	}

	public void moveItemUsingSOAP(String itemId, String targetId,
			ZimbraAccount account) throws HarnessException {
		account.soapSend("<ItemActionRequest xmlns='urn:zimbraMail'>"
				+ "<action id='" + itemId + "' l='" + targetId
				+ "' op='move'/>" + "</ItemActionRequest>");
	}

	public void rename(String text) throws HarnessException {
		// ClientSessionFactory.session().selenium().getEval("var x = selenium.browserbot.findElementOrNull(\""+Locators.zFrame.locator+"\");if(x!=null)x=x.contentWindow.document.body;if(browserVersion.isChrome){x.textContent='"+text+"';}else if(browserVersion.isIE){x.innerText='"+text+"';}");
		logger.info("renaming to: " + text);

		// sSelectFrame("relative=top");
		if (zWaitForElementPresent(Locators.zRenameInput.locator, "3000")) {
			sType(Locators.zRenameInput.locator, text);
			/*
			 * ClientSessionFactory .session() .selenium() .getEval(
			 * "var text=\"" + text + "\";" + "var locator=\"" +
			 * Locators.zRenameInput.locator + "\";" +
			 * "var input=selenium.browserbot.findElement(locator);" +
			 * "input.value=text;");
			 */
		} else
			throw new HarnessException(Locators.zRenameInput.locator
					+ " not present");

		zKeyEvent(Locators.zRenameInput.locator, "39", "keydown");
		zKeyEvent(Locators.zRenameInput.locator, "39", "keydown");
		zKeyEvent(Locators.zRenameInput.locator, "13", "keydown");
	}

	public boolean zVerifyElementText(ZimbraAccount account, String xPath,
			String text) throws HarnessException {
		try {
			Element[] nodes = account.soapSelectNodes(xPath);

			for (Element element : nodes) {
				if (element.getText().equals(text)) {
					return true;
				}
			}
		} catch (Exception ex) {
			logger.info(ex);
		}
		return false;
	}

	@Override
	public AbsPage zListItem(Action action, String item)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, String item)
			throws HarnessException {
		throw new HarnessException("Implement me");
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		throw new HarnessException("Implement me");
	}
}
