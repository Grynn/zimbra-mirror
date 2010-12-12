/**
 * 
 */
package projects.ajax.ui.Mail;

import framework.ui.AbsApplication;
import framework.ui.AbsDialog;
import framework.ui.Button;
import framework.util.HarnessException;
import framework.util.SleepUtil;

/**
 * Represents a "Move Message" dialog box
 * <p>
 * @author Matt Rhoades
 *
 */
public class DialogMove extends AbsDialog {

	public static class Locators {
	

	}
	
	
	public DialogMove(AbsApplication application) {
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
	public void zClickButton(Button button) throws HarnessException {
		throw new HarnessException("implement me!");		
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
		String locator = "//div[contains(@id, '_inputDivId')]";
		if ( this.sIsElementPresent(locator) )
			throw new HarnessException("unable to find folder name field "+ locator);
		
		this.sType(locator, folder);
		SleepUtil.sleepSmall();
		// TODO Auto-generated method stub
		
	}



}
