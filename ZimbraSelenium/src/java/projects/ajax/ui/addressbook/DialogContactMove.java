/**
 * 
 */
package projects.ajax.ui.addressbook;

import framework.ui.AbsApplication;
import framework.ui.AbsDialog;
import framework.ui.AbsPage;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * Represents a "Move Message" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogContactMove extends AbsDialog {

	public static class Locators {
	

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
		throw new HarnessException("implement me!");
	}



}
