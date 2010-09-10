package projects.admin.tests.topmenu.search;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;

public class BasicSearch extends CommonTest {
	
	public BasicSearch() {
		logger.info("New "+ BasicSearch.class.getCanonicalName());
	}
	
	@Test(	description = "Verify the Top Menu displays the Search bar correctly",
			groups = { "smoke" })
	public void TopMenu_BasicSearch_01() throws HarnessException {
		throw new HarnessException("Implement me!");
	}


}
