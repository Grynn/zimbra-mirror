package projects.zcs.tests.addressbook.print;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.core.*;
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
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="address book";
		super.zLogin();
	}
	
	@Test(
			description = "Print a contact group",
			groups = { "smoke", "full" }
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

}
