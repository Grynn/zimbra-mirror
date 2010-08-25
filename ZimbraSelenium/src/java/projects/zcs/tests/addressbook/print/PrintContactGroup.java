package projects.zcs.tests.addressbook.print;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.HarnessException;
import projects.zcs.PageObjects;
import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ActionMethod;

public class PrintContactGroup extends CommonTest {

	public PrintContactGroup() {
		
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
			description = "Print a contact group",
			groups = { "sanity", "smoke", "full" }
		)
	public void printContactGroup01() throws HarnessException {
		throw new HarnessException("implement me!");
	}

	@Test(
			description = "Print two contact groups by shift-select",
			groups = { "smoke", "full" }
		)
	public void printContactGroup02() throws HarnessException {
		throw new HarnessException("implement me!");
	}
	
	@Test(
			description = "Print three contact groupss by shift select",
			groups = { "smoke", "full" }
		)
	public void printContactGroup03() throws HarnessException {
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
