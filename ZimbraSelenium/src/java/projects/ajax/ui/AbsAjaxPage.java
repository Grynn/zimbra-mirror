/**
 * 
 */
package projects.ajax.ui;

import framework.ui.AbsApplication;
import framework.ui.AbsPage;
import framework.ui.AbsSeleniumObject;
import framework.ui.Action;
import framework.ui.Button;
import framework.util.HarnessException;


/**
 * The <code>AbsAjaxPage</code> abstract class defines common functionality
 * of all Ajax Client page classes.
 * <p>
 * All Ajax page classes must extend this class.
 * <p>
 * The derived classes are created in the {@link AppAjaxClient} class.
 * Individual test cases should not create AbsAjaxPage objects, but 
 * rather re-use the existing object in the client object.  For example:
 * <pre>
 * {@code
 * 
 * public void TestCase1() throws HarnessException {
 *   FormMailNew compose = app.zMailPage.zToolbarPressButton(Button.B_NEW);
 * }
 * 
 * }
 * </pre>
 * <p>
 * For organization, classes that extend this class should use
 * the name format <code>Page<Name></code>.
 * <p>
 * <pre>
 * {@code
 * 	public class PageSample extends AbsAjaxPage {
 * 
 *     	public PageSample(AbsApplication application) {
 *			super(application);
 *
 *			// ...
 *
 *		}
 *     
 *     	// ...
 * 
 * 	}
 * }
 * </pre>
 * <p>
 * It has been suggested to move the AbsAjaxPage class into
 * the AbsPage class.
 * </p>
 * 
 * @author Matt Rhoades
 *
 */
public abstract class AbsAjaxPage extends AbsPage {

	protected AppAjaxClient MyApplication = null;

	public AbsAjaxPage(AbsApplication application) {
		super(application);
		
		logger.info("new " + AppAjaxClient.class.getCanonicalName());
		
		MyApplication = (AppAjaxClient)application;
		
	}

	/**
	 * Take action on list items
	 * 
	 * (mainly applies to mail, contacts, tasks)
	 * For mail, item identifier is the subject.
	 * For contacts, item identifier is the email.
	 * For tasks, item identifier is the summary.
	 * 
	 * @param action See Actions class
	 * @param item The item identifier
	 * @return
	 * @throws HarnessException
	 */
	public abstract AbsSeleniumObject zListItem(Action action, String item) throws HarnessException;
	
	/**
	 * Take action on list items with optional action
	 * (mainly right-click -> context menu)
	 */
	public abstract AbsSeleniumObject zListItem(Action action, Action option, String item) throws HarnessException;
	
	/**
	 * Click on a button
	 * @param button the button to press
	 * @return Returns the resulting Page, Wizard, etc. or null
	 * @throws HarnessException
	 */
	public abstract AbsSeleniumObject zToolbarPressButton(Button button) throws HarnessException;
	
	
	/**
	 * Click on a pulldown with the specified option in the pulldown
	 * @param pulldown
	 * @param option
	 * @return Returns the resulting Page, Wizard, etc. or null
	 * @throws HarnessException
	 */
	public abstract AbsSeleniumObject zToolbarPressPulldown(Button pulldown, Button option) throws HarnessException;
	

}
