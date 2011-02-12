package com.zimbra.qa.selenium.projects.ajax.ui.briefcase;

import com.zimbra.qa.selenium.framework.items.FolderItem;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;
import com.zimbra.qa.selenium.projects.ajax.ui.AppAjaxClient;

/**
 * Represents a "Choose Folder" dialog box for move operation
 * <p>
 */
public class DialogChooseFolder extends AbsDialog {

	public static class Locators {
	
		public static final String zDialogId			= "ChooseFolderDialog";
		
		public static final String zTitleId	 			= "ChooseFolderDialog_title";

		public static final String zDialogContentId		= "ChooseFolderDialog_content";

		public static final String zDialogInputId		= "ChooseFolderDialog_inputDivId";

		public static final String zDialogInputLocator	= "css=div[id='"+ zDialogId +"'] div[id='"+ zDialogInputId +"'] > div > input";

		public static final String zDialogButtonsId		= "ChooseFolderDialog_buttons";

	}
	
	
	public DialogChooseFolder(AbsApplication application) {
		super(application);
	}
	

	/* (non-Javadoc)
	 * @see framework.ui.AbsDialog#myPageName()
	 */
	@Override
	public String myPageName() {
		return (this.getClass().getName());
	}

	@Override
	public boolean zIsActive() throws HarnessException {
		logger.info(myPageName() + " zIsVisible()");

		String locator = "id="+ Locators.zDialogId;
		
		if ( !this.sIsElementPresent(locator) ) {
			return (false); // Not even present
		}
		
		if ( !this.zIsVisiblePerPosition(locator, 0, 0) ) {
			return (false);	// Not visible per position
		}
	
		// Yes, visible
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
		
	}
	
	
	@Override
	public AbsPage zClickButton(Button button) throws HarnessException {
		logger.info(myPageName() + " zClickButton("+ button +")");

		String locator = null;
		
		if ( button == Button.B_NEW ) {
			
			// TODO: L10N this		
			locator = "//div[@id='"+ Locators.zDialogId +"']//div[@id='"+ Locators.zDialogButtonsId +"']//td[text()='New']";
			throw new HarnessException("implement me!");

		} else if ( button == Button.B_OK ) {
			
			// TODO: L10N this		
			locator = "//div[@id='"+ Locators.zDialogId +"']//div[@id='"+ Locators.zDialogButtonsId +"']//td[text()='OK']";

		} else if ( button == Button.B_CANCEL ) {
			
			// TODO: L10N this
			locator = "//div[@id='"+ Locators.zDialogId +"']//div[@id='"+ Locators.zDialogButtonsId +"']//td[text()='Cancel']";

		} else {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Default behavior, click the locator
		//
		
		// Make sure the locator was set
		if ( locator == null ) {
			throw new HarnessException("Button "+ button +" not implemented");
		}
		
		// Make sure the locator exists
		if ( !this.sIsElementPresent(locator) ) {
			throw new HarnessException("Button "+ button +" locator "+ locator +" not present!");
		}
		
		this.zClick(locator);
		
		return (null);
	}


	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		logger.info(myPageName() + " zGetDisplayedText("+ locator +")");
		
		if ( locator == null )
			throw new HarnessException("locator was null");
		
		return (this.sGetText(locator));
	}


	/**
	 * Click on the folder in the dialog tree
	 * @param folder
	 * @throws HarnessException
	 */
	public void zClickTreeFolder(FolderItem folder) throws HarnessException {
		logger.info(myPageName() + " zClickTreeFolder("+ folder +")");
		
		if ( folder == null ) 
			throw new HarnessException("folder must not be null");
		String locator = null;
		if (ZimbraSeleniumProperties.getAppType() == AppType.DESKTOP) {
		   locator = "css=div[id='"+ Locators.zDialogId +"'] td[id^='zti__" +
		         MyApplication.zGetActiveAccount().EmailAddress +
		         ":ZmChooseFolderDialog_Briefcase__']" +
		         "[id$='" + folder.getId() +
		         "_textCell']";
		} else {
		   locator = "css=div[id='"+ Locators.zDialogId +"'] td[id='zti__ZmChooseFolderDialog_Briefcase__"+ folder.getId() +"_textCell']";
		}
		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder in tree "+ locator);
	
		this.zClick(locator);		
	}

	/**
	 * Enter text into the move message dialog folder name field
	 * @param folder
	 */
	public void zEnterFolderName(String folder) throws HarnessException {
		logger.info(myPageName() + " zEnterFolderName("+ folder +")");
		
		if ( folder == null ) 
			throw new HarnessException("folder must not be null");
		
		String locator = Locators.zDialogInputLocator;

		if ( !this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder name field "+ locator);
		
		// For some reason, the text doesn't get entered on the first try
		this.sFocus(locator);
		this.zClick(locator);
		zKeyboard.zTypeCharacters(folder);	
	}
}
