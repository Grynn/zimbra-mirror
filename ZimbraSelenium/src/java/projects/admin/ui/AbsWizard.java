package projects.admin.ui;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import projects.admin.clients.Item;

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
public abstract class AbsWizard extends AbsSeleniumObject {
	protected static Logger logger = LogManager.getLogger(AbsWizard.class);

	public enum NavButton {
		Help, Cancel, Next, Previous, Finish
	}
	
	/**
	 * The Page that creates this wizard
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
	public abstract boolean isOpen() throws HarnessException;
	
	/**
	 * Fill out this wizard with the specified Item data
	 * @param item
	 * @throws HarnessException
	 */
	public abstract Item completeWizard(Item item) throws HarnessException;
	
	public void clickHelp() throws HarnessException {
		clickWizardButton(NavButton.Help);
	}
	
	public void clickCancel() throws HarnessException {
		clickWizardButton(NavButton.Cancel);
	}
	
	public void clickPrevious() throws HarnessException {
		clickWizardButton(NavButton.Previous);
	}

	public void clickNext() throws HarnessException {
		clickWizardButton(NavButton.Next);
	}

	public void clickFinish() throws HarnessException {
		clickWizardButton(NavButton.Finish);
	}
	
	protected void clickWizardButton(NavButton button) throws HarnessException {
		throw new HarnessException("implement me");

		// TODO: If possible, define in the abstract class
		
		// Check if the button is enabled
		// throw HarnessException if not enabled
		
		// Click on the button
	}

}
