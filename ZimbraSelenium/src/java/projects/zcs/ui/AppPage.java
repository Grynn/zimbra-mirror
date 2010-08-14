/**
 * 
 */
package projects.zcs.ui;

import framework.items.ZimbraItem;
import framework.util.HarnessException;
import projects.zcs.tests.CommonTest;


/**
 * @author Matt Rhoades
 *
 */
public class AppPage extends CommonTest {

	
	/**
	 * This method navigates the GUI to this application page
	 *
	 * @param method The method to use
	 * @throws HarnessException 
	 */
	public void navigateTo(ActionMethod method)throws HarnessException {
		throw new HarnessException("implement me");
	}

	/**
	 * This method creates an item
	 * 
	 * The method to use must correspond to the page-specific
	 * object type, e.g. a ABComposeActionMethod must be passed to 
	 * ABCompose.createItem()
	 * 
	 * The item to create must correspond to the page-specific
	 * object type, e.g. a ContactItem must be passed to 
	 * ABCompose.createItem()
	 * 
	 * 
	 * @param method The method to use
	 * @param item The object (page-specific type) to create
	 * @return
	 * @throws HarnessException 
	 */
	public ZimbraItem createItem(ActionMethod method, ZimbraItem item) throws HarnessException {
		throw new HarnessException("implement me");
	}
	
	/**
	 * This method modifies an item
	 * 
	 * @param method The method to use
	 * @param oldItem The old object (page-specific type) to modify
	 * @param newItem The new object containing values to set
	 * @return
	 * @throws HarnessException 
	 */
	public ZimbraItem modifyItem(ActionMethod method, ZimbraItem oldItem, ZimbraItem newItem) throws HarnessException {
		throw new HarnessException("implement me");
	}


	/**
	 * This method deletes an item
	 * 
	 * @param method The method to use
	 * @param item The object (page-specific type) to delete
	 * @throws HarnessException 
	 */
	public void deleteItem(ActionMethod method, ZimbraItem item) throws HarnessException  {
		throw new HarnessException("implement me");
	}
	
}
