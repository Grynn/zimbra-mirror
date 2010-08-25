package projects.zcs.tests.addressbook.folders;

import org.testng.annotations.Test;

import framework.util.HarnessException;

import projects.zcs.tests.CommonTest;

public class AddressbookContextMenu extends CommonTest {

	public AddressbookContextMenu() {
	}
	
	/**
	 * verify context menu items match expected values from right-click on system addressbook
	 * 
	 * Steps:
	 * 1. Right click on "Contacts" addressbook folder
	 * 2. Verify context menu contains:
	 *  "New Address Book"
	 *  "Share Address Book"
	 *  "Delete" (disabled)
	 *  "Rename Folder" (disabled)
	 *  "Edit Properties"
	 *  "Expand All" (disabled)
	 * @throws HarnessException 
	 * 
	 */
	@Test(
			description="verify context menu items match expected values from right-click on system Contacts addressbook",
			groups = { "smoke", "full" }
				)
	public void addressbookContextMenu01() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	/**
	 * verify context menu items match expected values from right-click on user-defined addressbook
	 * 
	 * Steps:
	 * 1. Create new addressbook folder
	 * 2. Right click on addressbook folder
	 * 3. Verify context menu contains:
	 *  "New Address Book"
	 *  "Share Address Book"
	 *  "Delete"
	 *  "Rename Folder"
	 *  "Edit Properties"
	 *  "Expand All" (disabled)
	 * @throws HarnessException 
	 * 
	 */
	@Test(
			description="verify context menu items match expected values from right-click on user-defined addressbook",
			groups = { "smoke", "full" }
				)
	public void addressbookContextMenu02() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	
}
