package com.zimbra.qa.selenium.framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.items.IItem;
import com.zimbra.qa.selenium.framework.util.HarnessException;


/**
 * This class defines an abstract Zimbra Admin Console Application "Wizard"
 * 
 * Examples: "New Account" Wizard, "New COS" Wizard
 * 
 * 
 * @author Matt Rhoades
 *
 */
/**
 * A <code>AbsWizard</code> object represents a "wizard widget", 
 * such as a create account, create folder, new tag, etc.
 * <p>
 * Wizards usually display in a panel and include one or more steps
 * to create an object.
 * <p>
 * 
 * @see <a href="http://wiki.zimbra.com/wiki/Testing:_Selenium:_ZimbraSelenium_Overview#Folder_Page">Create a new folder</a>
 * @author Matt Rhoades
 *
 */
public abstract class AbsWizard extends AbsPage {
	protected static Logger logger = LogManager.getLogger(AbsWizard.class);
	public static class Locators {
	public static final String CANCEL_BUTTON = "zdlg__NEW_ACCT_button1_title";
	public static final String HELP_BUTTON = "zdlg__NEW_ACCT_button10_title";
	public static final String PREVIOUS_BUTTON = "zdlg__NEW_ACCT_button11_title";
	public static final String NEXT_BUTTON = "_button12_title";
	public static final String FINISH_BUTTON = "_button13_title";
	public static final String ACCOUNT_DIALOG="ACCT";
	public static final String DL_DIALOG="DL";
	public static final String DOMAIN_DIALOG="DOMAIN";
	public static final String COS_DIALOG="COS";
	public static final String RESOURCE_DIALOG="RES";
	}
	public static String buttonPath="";
	
	
	

	public enum WizardButton {
		Help, Cancel, Next, Previous, Finish
	}
	
	/**
	 * A pointer to the page that created this object
	 */
	protected AbsTab MyPage = null;
	
	/**
	 * Create a new wizard from the specified page
	 * @param page
	 */
	public AbsWizard(AbsTab page) {
		super(page.MyApplication);
		
		logger.info("new "+ AbsWizard.class.getName());
		MyPage = page;
	}
	
	
	/**
	 * Fill out this wizard with the specified Item data
	 * @param item
	 * @throws HarnessException
	 */
	public abstract IItem zCompleteWizard(IItem item) throws HarnessException;
	
	public void clickHelp(String dialogName) throws HarnessException {
		clickWizardButton(WizardButton.Help,dialogName);
	}
	
	public void clickCancel(String dialogName) throws HarnessException {
		clickWizardButton(WizardButton.Cancel,dialogName);
	}
	
	public void clickPrevious(String dialogName) throws HarnessException {
		clickWizardButton(WizardButton.Previous,dialogName);
	}

	public void clickNext(String dialogName) throws HarnessException {
		clickWizardButton(WizardButton.Next,dialogName);
	}

	public void clickFinish(String dialogName) throws HarnessException {
		clickWizardButton(WizardButton.Finish,dialogName);
	}
	
	protected void clickWizardButton(WizardButton button, String dialogName) throws HarnessException {

		// TODO: If possible, define in the abstract class

		// Check if the button is enabled
		// throw HarnessException if not enabled
		// Click on the button
		switch(button) {
		case Finish : 
				buttonPath="css=td[id$='_" +  dialogName + Locators.FINISH_BUTTON + "']";
				if(sIsElementPresent(buttonPath)) 
					zClickAt(buttonPath,""); 
				break;
		case Next:
				buttonPath="css=td[id$='_" +  dialogName + Locators.NEXT_BUTTON + "']";
				if(sIsElementPresent(buttonPath)) 
					zClickAt(buttonPath,"");
				break;				
		case Previous:
				buttonPath="css=td[id$='_" +  dialogName + Locators.PREVIOUS_BUTTON + "']";
				if(sIsElementPresent(buttonPath))
					zClickAt(buttonPath,"");
				break;
		case Cancel:
				buttonPath="css=td[id$='_" +  dialogName + Locators.CANCEL_BUTTON + "']";
				if(sIsElementPresent(buttonPath))
					zClickAt(buttonPath,"");	
				break;
		case Help:
				buttonPath="css=td[id$='_" +  dialogName + Locators.HELP_BUTTON + "']";
				if(sIsElementPresent(buttonPath))
					zClickAt(buttonPath,"");
			}
	}

}
