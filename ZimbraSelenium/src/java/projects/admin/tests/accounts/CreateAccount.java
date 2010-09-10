package projects.admin.tests.accounts;

import org.testng.annotations.Test;

import projects.admin.tests.CommonTest;
import framework.util.HarnessException;

public class CreateAccount extends CommonTest {
	
	public CreateAccount() {
		logger.info("New "+ CreateAccount.class.getCanonicalName());
	}
	
	@Test(	description = "Create a basic account",
			groups = { "sanity" })
	public void CreateAccount_01() throws HarnessException {
		throw new HarnessException("implement me!");
	}


}
