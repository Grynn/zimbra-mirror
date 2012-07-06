/**
 * 
 */
package com.zimbra.qa.selenium.projects.octopus.ui;

import java.io.File;
import java.util.ArrayList;

import com.zimbra.qa.selenium.framework.items.UploadProgressItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.octopus.ui.DialogError.DialogErrorID;

public class PageMyFiles extends PageOctopus {

	public static class Locators {
		public static final Locators zTabMyFiles = new Locators(
		"css=div.octopus-tab-label:contains(My Files)");
		public static final Locators zMyFilesCurrentMenuLabel = new Locators(
		"css=span[class*=my-files-menu-bar-current-folder-label]:contains(My Files)");
		public static final Locators zTabMyFilesSelected = new Locators(
		"css=div[class^=octopus-tab sc-collection-item sel]>div.octopus-tab-label:contains(My Files)");
		public static final Locators zMyFilesView = new Locators(
		"css=div[id=octopus-myfiles-view]");
		public static final Locators zMyFilesArrowButton = new Locators(
		"css=span[class*='my-files-list-item-action-button myfiles-button']");
		public static final Locators zNewFolderOption = new Locators(
		"css=div[class^='octopus-template-context-menu-item action-new-folder']:contains('New Folder')");
		public static final Locators zMyFilesListView = new Locators(
		"css=div[class*=my-files-list-view]");
		public static final Locators zMyFilesListViewItems = new Locators(
		"css=div[class*=my-files-list-view]>div[class^=my-files-list-item]");
		public static final Locators zDownloadItem = new Locators(
		"css=div[class^=sc-view sc-menu-item] a[class=menu-item]>span:contains(Download)");
		public static final Locators zHistory = new Locators(
		"css=div[id=my-files-preview] div[id=my-files-preview-toolbar]>button[id=show-activitystream-button])");
		public static final Locators zComments = new Locators(
		"css=div[id=my-files-preview] div[id=my-files-preview-toolbar]>button[id=my-files-preview-show-comments-button])");
		public static final Locators zBackButton = new Locators(
		"css=button[class*='my-files-menu-bar-back-button myfiles-button button']");
		public static final Locators zUploadFileButton = new Locators(
		"css=div[class^='octopus-template-context-menu-item action-upload']:contains('Upload') input[type='file']");
		public static final Locators zUploadButtonDisabled = new Locators(
		"css=div[class^='octopus-template-context-menu-item action-upload disabled']:contains('Upload')");
		public static final Locators zNewFolderButtonDisabled = new Locators(
		"css=div[class^='octopus-template-context-menu-item action-new-folder disabled']:contains('New Folder')");
		public static final Locators zUploadProgressBarCollection  = new Locators(
		"css=div[id='upload-progress-view'] div[class='upload-progress-item-view']");
		public final String locator;

		private Locators(String locator) {
			this.locator = locator;
		}
	}

	public PageMyFiles(AbsApplication application) {
		super(application);

		logger.info("new " + PageMyFiles.class.getCanonicalName());

	}

	@Override
	public Toaster zGetToaster() throws HarnessException {
		return (new Toaster(this.MyApplication));
	}

	@Override
	public DialogError zGetErrorDialog(DialogErrorID zimbra) {
		return (new DialogError(zimbra, this.MyApplication, this));
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		// Look for the My Files tab
		boolean selected = sIsElementPresent(Locators.zMyFilesCurrentMenuLabel.locator);

		if (!selected) {
			logger.debug("zIsActive(): " + selected);
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

		// Make sure PageOctopus page is active
		if (!((AppOctopusClient) MyApplication).zPageOctopus.zIsActive()) {
			((AppOctopusClient) MyApplication).zPageOctopus.zNavigateTo();
		}

		String locator = Locators.zTabMyFiles.locator;

		if (!zWaitForElementPresent(locator, "10000")) {
			throw new HarnessException(locator + " Not Present!");
		}

		// Click on My Files tab
		zClick(locator);

		zWaitForBusyOverlay();

		zWaitForActive();
	}

	@Override
	public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
	throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (pulldown == Button.B_MY_FILES) {
			pulldownLocator = Locators.zMyFilesArrowButton.locator;

			if (option == Button.O_NEW_FOLDER) {
				optionLocator = Locators.zNewFolderOption.locator;

				/*
				 * sGetCssCount(
				 * "css=div[class*=my-files-list-view]>div.my-files-list-item");
				 * this.zClick(Locators.zMyFilesListView.locator +
				 * ">div.my-files-list-item:last-child");
				 * this.zClick(Locators.zMyFilesListView.locator +
				 * ">div.my-files-list-item:nth-child(1)");
				 * 
				 * if (this.zWaitForElementPresent(
				 * "css=div[role=dialog]>label[class^=edit]", "3000")) { //
				 * this.sClick("css=div[role=dialog]>label[class^=edit]");
				 * zKeyEvent("css=div[role=dialog]>label[class^=edit]", "13",
				 * "keydown"); //
				 * zKeyEvent("css=div[role=dialog]>label[class^=edit]", // "13",
				 * "keyup"); }
				 * 
				 * return page;
				 */

			}else {
				logger.info("no logic defined for " + option);
			}
		} else {
			logger.info("no logic defined for " + pulldown + "/" + option);
		}

		// default behavior
		if (!this.zWaitForElementPresent(pulldownLocator, "2000"))
			throw new HarnessException("Button is not present locator="
					+ pulldownLocator);
		zClick(pulldownLocator);

		zWaitForBusyOverlay();

		if (!this.zWaitForElementPresent(optionLocator, "2000"))
			throw new HarnessException("Option is not present locator="
					+ optionLocator);
		sClick(optionLocator);

		return page;
	}
	//Method uses Java script to click element.Opens up the upload file Dialog.
	public AbsPage zToolbarPressUpload(Button pulldown, Button option) throws HarnessException
	{
		logger.info(myPageName() + " zToolbarPressPulldown(" + pulldown + ", "
				+ option + ")");

		tracer.trace("Click pulldown " + pulldown + " then " + option);

		if (pulldown == null)
			throw new HarnessException("Pulldown cannot be null!");

		if (option == null)
			throw new HarnessException("Option cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		String optionLocator = null; // If set, this will be clicked
		AbsPage page = null; // If set, this page will be returned

		if (pulldown == Button.B_MY_FILES) {
			pulldownLocator = Locators.zMyFilesArrowButton.locator;
			if(option == Button.B_UPLOAD_FILE){
				optionLocator= Locators.zUploadFileButton.locator;
			}
			else {
				logger.info("no logic defined for " + option);
			}
		} else {
			logger.info("no logic defined for " + pulldown + "/" + option);
		}

		if (!this.zWaitForElementPresent(pulldownLocator, "2000"))
			throw new HarnessException("Button is not present locator="
					+ pulldownLocator);
		zClick(pulldownLocator);

		zWaitForBusyOverlay();

		if (!this.zWaitForElementPresent(optionLocator, "2000"))
			throw new HarnessException("Option is not present locator="
					+ optionLocator);
		// Evaluate Java script to perform action of clicking upload button.Currently works only for FF.
		sGetEval("var uploadForm = window.document.forms['uploadForm']; " +
				"if(uploadForm){var inputs = uploadForm.getElementsByTagName('input'); " +
				"for(var i=0; i<inputs.length; i++) " +
				"{ if(inputs[i] && inputs[i].type === 'file') " +" { inputs[i].click(); }}}");


		return page;

	}
	@Override
	public AbsPage zToolbarPressButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zToolbarPressPulldown(" + button + ")");

		tracer.trace("Click button " + button);

		if (button == null)
			throw new HarnessException("Pulldown cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		AbsPage page = null; // If set, this page will be returned

		// Based on the button specified, take the appropriate action(s)
		if (button == Button.B_MY_FILES) {
			pulldownLocator = Locators.zMyFilesArrowButton.locator;

			zClick(pulldownLocator);

			zWaitForBusyOverlay();

		} else if (button == Button.B_HISTORY) {
			pulldownLocator = Locators.zHistory.locator;

			zClick(pulldownLocator);

			zWaitForBusyOverlay();

		} else {
			logger.info("no logic defined for " + button);
		}

		return page;
	}

	@Override
	public AbsPage zListItem(Action action, String itemName)
	throws HarnessException {
		logger.info(myPageName() + " zListItem(" + action + ", " + itemName
				+ ")");

		tracer.trace("Click list item " + itemName);

		// Validate the arguments
		if (action == null) {
			throw new HarnessException("Action cannot be null");
		}

		if (itemName == null)
			throw new HarnessException("Item name cannot be null!");

		// Default behavior variables
		String listItemLocator = null; // If set, this will be expanded
		AbsPage page = null; // If set, this page will be returned

		// Based on action and the button specified, take the appropriate
		// action(s)
		if (action == Action.A_LEFTCLICK) {

			listItemLocator = Locators.zMyFilesListViewItems.locator
			+ ":contains(" + itemName + ")";

			if (!this.zWaitForElementPresent(listItemLocator, "2000"))
				throw new HarnessException("Item is not present locator="
						+ listItemLocator);

			zClick(listItemLocator);

			page = new DisplayFilePreview(MyApplication);

			// If the app is busy, wait for it to become active
			zWaitForBusyOverlay();
		} else {
			logger.info("no logic defined for " + action);
		}

		if (page != null) {
			page.zWaitForActive();
		}

		return page;
	}

	@Override
	public AbsPage zListItem(Action action, Button button, String itemName)
	throws HarnessException {
		logger.info(myPageName() + " zListItem(" + action + ", " + button
				+ ", " + itemName + ")");

		tracer.trace("Click button " + button);

		// Validate the arguments
		if (action == null) {
			throw new HarnessException("Action cannot be null");
		}
		if (button == null)
			throw new HarnessException("Button cannot be null!");

		if (itemName == null)
			throw new HarnessException("Item name cannot be null!");

		// Default behavior variables
		String pulldownLocator = null; // If set, this will be expanded
		AbsPage page = null; // If set, this page will be returned

		// Based on action and the button specified, take the appropriate
		// action(s)
		if (action == Action.A_LEFTCLICK) {

			if (button == Button.B_MY_FILES_LIST_ITEM) {

				pulldownLocator = Locators.zMyFilesListViewItems.locator
				+ ":contains(" + itemName
				+ ") span[class^=my-files-list-item-action-button]";

				if (!this.zWaitForElementPresent(pulldownLocator, "2000"))
					throw new HarnessException("Button is not present locator="
							+ pulldownLocator);

				zClick(pulldownLocator);

				// If the app is busy, wait for it to become active
				zWaitForBusyOverlay();
			} else {
				logger.info("no logic defined for " + button);
			}
		} else {
			logger.info("no logic defined for " + action);
		}
		return page;
	}

	@Override
	public AbsPage zListItem(Action action, Button option, Button subOption,
			String item) throws HarnessException {
		throw new HarnessException("Implement me");
	}
	//List down all the ongoing upload and sets the required properties
	public ArrayList<UploadProgressItem> zListUploadItem() throws HarnessException
	{
		//Create array list of upload progress Item.
		ArrayList<UploadProgressItem> uploadItemCollection= new ArrayList<UploadProgressItem>();

		String UploadCollectionLocator = Locators.zUploadProgressBarCollection.locator;
		//Get the available ongoing upload progress bar

		int count = sGetCssCount(UploadCollectionLocator);

		logger.debug("total items= " + count);
		// set the size,name and progressbar value for each ongoing upload item.

		for(int i=1;i<=count;i++)
		{
			String itemLocator = UploadCollectionLocator + ":nth-of-type("+ i +")";
			sIsElementPresent(itemLocator);
			UploadProgressItem uploaditem = new UploadProgressItem();

			String nameLocator=itemLocator+ " div[class='octopus-progress-name']";
			if(sIsElementPresent(nameLocator)){
				uploaditem.setFileName(sGetText(nameLocator));
			}

			String sizeLocator=itemLocator+ " span[class='octopus-progress-bar-size']";
			if(sIsElementPresent(sizeLocator)){
				uploaditem.setUploadFileSize(sGetText(sizeLocator));
			}

			String uploadProgessBarLocator = itemLocator+" div[class='octopus-progress-bar-container'] progress@value";
			if(sIsElementPresent(uploadProgessBarLocator)){
				uploaditem.setUploadBarValue(sGetAttribute(uploadProgessBarLocator));
			}

			uploadItemCollection.add(uploaditem);
			uploaditem.prettyPrint();
		}
		return uploadItemCollection;
	}
	//Method for uploading a file using Dialog box. Needs fileName as parameter.
	public void uploadFileUsingDialogBox(String fileName) throws HarnessException
	{
		String filePath = ZimbraSeleniumProperties.getBaseDirectory()
		+ "/data/public/other/" +fileName;

		File fileTobeUploaded = new File(filePath);

		String completePath = fileTobeUploaded.getAbsolutePath();
		//check if required file exists.
		if(fileTobeUploaded.exists())
		{//initialize DialogHandler instance
			OSDialogHandler handle = new OSDialogHandler();
			try {
				handle.uploadFileUsingDialog(completePath);
			} catch (Exception e) {
				e.printStackTrace();
				e.notify();
			}

		}else
		{
			throw new HarnessException("File does not exsist");
		}
		waitForFileUpload();
	}
	//Wait method will wait for the upload to complete. Refer the progress bar value for upload progress.
	public void waitForFileUpload() throws HarnessException
	{
		// create an array list
		ArrayList<UploadProgressItem> items;
		//get the list of all ongoing upload.
		items= zListUploadItem();
		// Total file percentage after upload
		int percentageComplete=100;

		for(UploadProgressItem item : items){
			// Intial file upload percentage
			int percentageUploaded =1;

			if(item.uploadBarValue!= null){
				// Convert Upload bar value to Integer.
				percentageUploaded = Integer.parseInt(item.uploadBarValue);
			}
			for(int j=1;j<=items.size();j++)
			{
				//Locators for upload size,progress bar locator
				String itemLocator = Locators.zUploadProgressBarCollection.locator + ":nth-of-type("+ j +")";
				String sizeLocator=itemLocator+ " span[class='octopus-progress-bar-size']";
				String uploadProgessBarLocator = itemLocator+" div[class='octopus-progress-bar-container']";
				String uploadProgressBarValue= uploadProgessBarLocator+" progress@value";

				logger.info("Waiting for file Upload:"+item.uploadFileName);
				//Check if percentage uploaded reaches to complete percentage.
				while(percentageUploaded<percentageComplete && sIsElementPresent(itemLocator)==true)
				{

					if(sIsElementPresent(sizeLocator)==true&&sIsVisible(sizeLocator)==true)
					{
						logger.info("File uploading size :"+ sGetText(sizeLocator)+"Total percentage uploaded:"+percentageUploaded);
						if(sIsVisible(uploadProgessBarLocator)){
							percentageUploaded = Integer.parseInt(sGetAttribute(uploadProgressBarValue));
						}
					}else
					{
						break;
					}
				}
			}
			//Additional sleep required because after upload save document request is generated which takes sometime to complete the upload.
			SleepUtil.sleep(10000);
			logger.info("File :"+item.uploadFileName+"Successfully!!");
		}
	}

}
