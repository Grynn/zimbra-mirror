package framework.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import framework.items.IItem;
import framework.util.HarnessException;

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
public abstract class AbsWizard extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsWizard.class);

	public enum WizardButton {
		Help, Cancel, Next, Previous, Finish
	}
	
	/**
	 * A pointer to the page that created this object
	 */
	protected AbsPage MyPage = null;
	
	/**
	 * Create a new wizard from the specified page
	 * @param page
	 */
	public AbsWizard(AbsPage page) {
		logger.info("new "+ AbsWizard.class.getName());
		MyPage = page;
	}
	
	/**
	 * Determine whether this wizard is open or not
	 */
	public abstract boolean zIsOpen() throws HarnessException;
	
	/**
	 * Fill out this wizard with the specified Item data
	 * @param item
	 * @throws HarnessException
	 */
	public abstract IItem zCompleteWizard(IItem item) throws HarnessException;
	
	public void clickHelp() throws HarnessException {
		clickWizardButton(WizardButton.Help);
	}
	
	public void clickCancel() throws HarnessException {
		clickWizardButton(WizardButton.Cancel);
	}
	
	public void clickPrevious() throws HarnessException {
		clickWizardButton(WizardButton.Previous);
	}

	public void clickNext() throws HarnessException {
		clickWizardButton(WizardButton.Next);
	}

	public void clickFinish() throws HarnessException {
		clickWizardButton(WizardButton.Finish);
	}
	
	protected void clickWizardButton(WizardButton button) throws HarnessException {
		throw new HarnessException("implement me");

		// TODO: If possible, define in the abstract class
		
		// Check if the button is enabled
		// throw HarnessException if not enabled
		
		// Click on the button
	}

}
