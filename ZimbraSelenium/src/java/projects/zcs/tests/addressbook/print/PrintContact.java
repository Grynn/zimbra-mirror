package projects.zcs.tests.addressbook.print;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.HarnessException;
import projects.zcs.PageObjects;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;

public class PrintContact extends CommonTest {

	public PrintContact() {
		
	}
	
	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	private void zLogin() throws Exception {
		zLoginIfRequired();
		PageObjects.zABCompose.navigateTo(ActionMethod.DEFAULT);
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = { "always" })
	public void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}
	
	@Test(
			description = "Print a contact",
			groups = { "smoke", "full" }
		)
	public void printContact01() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Test(
			description = "Print two contacts by shift-select",
			groups = { "smoke", "full" }
		)
	public void printContact02() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	@Test(
			description = "Print three contacts by shift select",
			groups = { "smoke", "full" }
		)
	public void printContact03() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Test(
			description = "Print one contact and one contact group by shift select",
			groups = { "smoke", "full" }
		)
	public void printContact04() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	//--------------------------------------------------------------------------
	// SECTION 4: RETRY-METHODS
	//--------------------------------------------------------------------------
	// for those tests that just needs relogin..
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);// reset this to false
		zLogin();
	}


}
