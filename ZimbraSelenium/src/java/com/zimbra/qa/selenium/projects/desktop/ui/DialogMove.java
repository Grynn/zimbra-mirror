package com.zimbra.qa.selenium.projects.desktop.ui;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.desktop.ui.addressbook.PageAddressbook;
import com.zimbra.qa.selenium.projects.desktop.ui.briefcase.PageBriefcase;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.PageMail;
import com.zimbra.qa.selenium.projects.desktop.ui.tasks.PageTasks;

public class DialogMove extends AbsDialog {
	public static class Locators {

		// TODO:  See https://bugzilla.zimbra.com/show_bug.cgi?id=54173
		public static final String zDialogId			= "ChooseFolderDialog";
		public static final String zTitleId	 			= "ChooseFolderDialog_title";
		public static final String zDialogContentId		= "ChooseFolderDialog_content";
		// TODO: Tree
		public static final String zDialogInputId		= "ChooseFolderDialog_inputDivId";
		public static final String zDialogInputLocator	= "css=div[id='"+ zDialogId +"'] div[id='"+ zDialogInputId +"'] > div > input";
		public static final String zDialogButtonsId		= "ChooseFolderDialog_buttons";

	}

	public DialogMove(AbsApplication application,AbsTab page) {
		super(application,page);
		
		logger.info("new "+ DialogMove.class.getCanonicalName());
	}

	@Override
	public String myPageName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton(" + button + ")");

		AbsPage page = null;
		String locator = null;

		if (button == Button.B_NEW) {

			// TODO: L10N this
			locator = "//div[@id='" + Locators.zDialogId + "']//div[@id='"+ Locators.zDialogButtonsId + "']//td[text()='New']";
			throw new HarnessException("implement me!");

		} else if (button == Button.B_OK) {

			// TODO: L10N this
			locator = "//div[@id='" + Locators.zDialogId + "']//div[@id='"+ Locators.zDialogButtonsId + "']//td[text()='OK']";

		} else if (button == Button.B_CANCEL) {

			// TODO: L10N this
			locator = "//div[@id='" + Locators.zDialogId + "']//div[@id='"+ Locators.zDialogButtonsId + "']//td[text()='Cancel']";

		} else {
			throw new HarnessException("Button " + button + " not implemented");
		}

		// Default behavior, click the locator
		//

		// Make sure the locator was set
		if (locator == null) {
			throw new HarnessException("Button " + button + " not implemented");
		}

		this.zClick(locator);

		this.zWaitForBusyOverlay();

		return (page);
	}

	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean zIsActive() throws HarnessException {

		logger.info(myPageName() + " zIsActive()");

		String locator = "id="+ Locators.zDialogId;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsActive() = true");
		return (true);

	}
	
	/**
	 * Enter text into the move message dialog folder name field
	 * @param folder
	 */
	public void zEnterFolderName(String folder) throws HarnessException {
		String locator = "//div[contains(@id, '_inputDivId')]/div/input";
		
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder name field "+ locator);
		
		this.zClick(locator);
		zKeyboard.zTypeCharacters(folder);
		
		// SleepUtil.sleepSmall();
		this.zWaitForBusyOverlay(); 
			
	}


	/**
	 * Left-Click on a folder in the tree
	 * @param folder
	 * @throws HarnessException
	 */
	public void zClickTreeFolder(FolderItem folder) throws HarnessException {

		logger.info(myPageName() + " zClickTreeFolder(" + folder + ")");

		if (folder == null)

			throw new HarnessException("folder must not be null");

		String locator = null;

		if (MyTab instanceof PageMail) {

		   if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
		      locator = "css=div[id='" + Locators.zDialogId+ "'] td[id^='zti__" +
		            MyApplication.zGetActiveAccount().EmailAddress +
		            ":ZmChooseFolderDialog_Mail__'][id$='" +
		            folder.getId() +
		            "_textCell']"; 
		   } else {
		      locator = "css=div[id='" + Locators.zDialogId+ "'] td[id='zti__ZmChooseFolderDialog_Mail__"+ folder.getId() + "_textCell']";
		   }

		} else if (MyTab instanceof PageAddressbook) {

		   String emailAddress = folder.isDesktopClientLocalFolder() ? 
		         ZimbraAccount.clientAccountName :
		         MyApplication.zGetActiveAccount().EmailAddress;
		   String folderIdSuffix = folder.isDesktopClientLocalFolder() ? 
		         "_" + folder.getId() :
		         ":" + folder.getId();

		    locator = "css=div[id='" + Locators.zDialogId
		         + "'] td[id^='zti__" + emailAddress +
		         ":ZmChooseFolderDialog_Contacts__'][id$='" +
		         folderIdSuffix + "_textCell']";

		}else if (MyTab instanceof PageTasks){
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			   locator = "css=div[id='" + Locators.zDialogId + "'] td[id^='zti__" +
			         MyApplication.zGetActiveAccount().EmailAddress +
			         ":ZmChooseFolderDialog_Tasks__'][id$='" +
			         folder.getId() +
			         "_textCell']";
			} else {
			   locator = "css=div[id='" + Locators.zDialogId + "'] td[id='zti__ZmChooseFolderDialog_Tasks__"+ folder.getId() + "_textCell']";
			}

		}else if (MyTab instanceof PageBriefcase ) {
			if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
			   locator = "css=div[id='" + Locators.zDialogId + "'] td[id^='zti__"+
			         MyApplication.zGetActiveAccount().EmailAddress +
			         ":ZmChooseFolderDialog_Briefcase__'][id$='" +
			         folder.getId() +
			         "_textCell']";
			} else {
			   locator = "css=div[id='" + Locators.zDialogId + "'] td[id='zti__ZmChooseFolderDialog_Briefcase__"+ folder.getId() + "_textCell']";
			}

		} else {
			throw new HarnessException("Unknown app type!");
		}

		// For some reason, the text doesn't get entered on the first try

		this.zClick(locator);

		this.zWaitForBusyOverlay(); // This method call seems to be missing from
		// the briefcase function

	}
}
