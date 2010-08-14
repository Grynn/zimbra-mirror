/**
 * 
 */
package projects.zcs.ui;

/**
 * This abstract class is used to define end-user actions.
 * 
 * In the Zimbra application, there are several methods to
 * create, modify, and delete objects.  This class defines
 * those methods.
 * 
 * 
 * For example, to create a new contact you can:
 * 1. Click "New" - "Contact"
 * 2. Right click on email address -> "Add to contacts"
 * 3. Right click on folder -> "New Contact"
 * 4. etc.
 * 
 * @author Matt Rhoades
 *
 */
public class ActionMethod  {
	
	/**
	 * Apply the default method
	 */
	public static final ActionMethod DEFAULT = new ActionMethod("Default");
	
	
	protected String myMethod = null;
	protected ActionMethod(String method) {
		myMethod = method;
	}

}
