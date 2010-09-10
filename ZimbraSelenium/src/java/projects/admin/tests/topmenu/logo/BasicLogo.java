package projects.admin.tests.topmenu.logo;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;

public class BasicLogo extends CommonTest {
	
	public BasicLogo() {
		logger.info("New "+ BasicLogo.class.getCanonicalName());
	}
	
	@Test(	description = "Verify the Top Menu displays the Logo image correctly",
			groups = { "smoke" })
	public void TopMenu_BasicLogo_01() throws HarnessException {
		throw new HarnessException("Implement me!");
	}


}
