package projects.admin.tests.cos;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;

public class CreateCos extends CommonTest {
	
	public CreateCos() {
		logger.info("New "+ CreateCos.class.getCanonicalName());
	}
	
	@Test(	description = "Create a basic COS",
			groups = { "sanity" })
	public void CreateCos_01() throws HarnessException {
		throw new HarnessException("implement me!");
	}


}
