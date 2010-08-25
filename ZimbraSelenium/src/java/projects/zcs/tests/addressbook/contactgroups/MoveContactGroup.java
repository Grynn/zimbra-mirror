package projects.zcs.tests.addressbook.contactgroups;

import org.testng.annotations.Test;

import framework.util.HarnessException;
import projects.zcs.tests.CommonTest;

public class MoveContactGroup extends CommonTest {
	
	public MoveContactGroup() {
	}

	@Test(
			description = "Move a contact group - Locations menu",
			groups = { "smoke", "full" }
		)
	public void moveContactGroup01() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Test(
			description = "Move a contact group - Right-click context menu",
			groups = { "smoke", "full" }
		)
	public void moveContactGroup02() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Test(
			description = "Move a contact group - Drag and Drop",
			groups = { "smoke", "full" }
		)
	public void moveContactGroup03() throws HarnessException {
		throw new HarnessException("implement me!");
	}


}
