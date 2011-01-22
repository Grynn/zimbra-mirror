/**
 * 
 */
package com.zimbra.qa.selenium.projects.ajax.ui.addressbook;

import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsDialog;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.projects.ajax.ui.mail.DialogMove.Locators;


/**
 * Represents a "Move Message" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogContactMove extends AbsDialog {

	public static class Locators {
			
		public static final String zDialogId			= "ChooseFolderDialog";
	}
	
	public DialogContactMove(AbsApplication application) {
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
	public AbsPage zClickButton(Button button) throws HarnessException {
		String locator=null;
		if (button == Button.B_OK) {			
			locator="//div[contains(@id, '_buttons')]/table/tbody/tr/td[2]/table/tbody/tr/td/div";			
			this.zClick(locator);	 	   			
		}
		
		return (null);
	}


	@Override
	public String zGetDisplayedText(String locator) throws HarnessException {
		throw new HarnessException("implement me!");		
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
		
		SleepUtil.sleepSmall();
		
		
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
		logger.info(myPageName() + " zIsVisible() = true");
		return (true);
	}



}
