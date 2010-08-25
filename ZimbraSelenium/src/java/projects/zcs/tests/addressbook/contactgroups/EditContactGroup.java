package projects.zcs.tests.addressbook.contactgroups;

import org.testng.annotations.Test;

import framework.util.HarnessException;
import projects.zcs.tests.CommonTest;

public class EditContactGroup extends CommonTest {
	
	public EditContactGroup() {
	}

	@Test(
			description = "Rename a contact group",
			groups = { "smoke", "full" }
		)
	public void editContactGroup01() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Test(
			description = "Add another contact to an existing contact group",
			groups = { "smoke", "full" }
		)
	public void editContactGroup02() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Test(
			description = "Remove a contact from an existing contact group",
			groups = { "smoke", "full" }
		)
	public void editContactGroup03() throws HarnessException {
		throw new HarnessException("implement me!");
	}


}
