package projects.zcs.tests.addressbook.contactgroups;

import org.testng.annotations.Test;

import framework.util.HarnessException;
import projects.zcs.tests.CommonTest;

public class ContactGroupContextMenu extends CommonTest {
	
	public ContactGroupContextMenu() {
	}

	@Test(
			description = "verify the contact group right click context menu exists and contains the correct options",
			groups = { "smoke", "full" }
		)
	public void contactGroupContextMenu01() throws HarnessException {
		throw new HarnessException("implement me!");
	}



}
