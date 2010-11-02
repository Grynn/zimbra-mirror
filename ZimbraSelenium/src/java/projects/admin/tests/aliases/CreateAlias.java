package projects.admin.tests.aliases;

import org.testng.annotations.Test;

import projects.admin.core.AdminCommonTest;
import framework.util.HarnessException;

public class CreateAlias extends AdminCommonTest {
	
	public CreateAlias() {
		logger.info("New "+ CreateAlias.class.getCanonicalName());
	}
	
	@Test(	description = "Create a basic alias",
			groups = { "sanity" })
	public void CreateAlias_01() throws HarnessException {
		throw new HarnessException("implement me!");
	}


}
