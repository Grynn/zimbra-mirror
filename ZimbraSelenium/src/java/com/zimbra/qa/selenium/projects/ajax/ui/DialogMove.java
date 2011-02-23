package com.zimbra.qa.selenium.projects.ajax.ui;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.ajax.ui.addressbook.PageAddressbook;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.PageMail;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogMove.Locators;
import com.zimbra.qa.selenium.projects.ajax.ui.tasks.PageTasks;

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
		// TODO Auto-generated constructor stub
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
		// TODO Auto-generated method stub
		return false;
	}
	public void zClickTreeFolder(FolderItem folder) throws HarnessException {

		logger.info(myPageName() + " zClickTreeFolder(" + folder + ")");

		if (folder == null)

			throw new HarnessException("folder must not be null");

		String locator = null;

		if (MyTab instanceof PageMail) {

			locator = "css=div[id='" + Locators.zDialogId+ "'] td[id='zti__ZmChooseFolderDialog_Mail__"+ folder.getId() + "_textCell']";

		} else if (MyTab instanceof PageAddressbook) {

			locator = "css=div[id='" + Locators.zDialogId
			+ "'] td[id='zti__ZmChooseFolderDialog_Contacts__"
			+ folder.getId() + "_textCell']";

		}else if (MyTab instanceof PageTasks){
			locator = "css=div[id='" + Locators.zDialogId+ "'] td[id='zti__ZmChooseFolderDialog_Tasks__"+ folder.getId() + "_textCell']";

		}else {
			throw new HarnessException("Unknown app type!");

		}

		// For some reason, the text doesn't get entered on the first try

		this.zClick(locator);

		this.zWaitForBusyOverlay(); // This method call seems to be missing from
		// the briefcase function

	}
}
