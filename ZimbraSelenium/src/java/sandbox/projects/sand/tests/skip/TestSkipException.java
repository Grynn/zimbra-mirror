package sandbox.projects.sand.tests.skip;

import java.lang.reflect.Method;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.util.HarnessException;

public class TestSkipException {
	private static Logger logger = LogManager.getLogger(TestSkipException.class);
	
	public TestSkipException() {
	}
	
	@Test(
		description = "A passing test case"
	)
	public void TestPass() throws HarnessException {
		logger.info("PASS");
		Assert.assertEquals("equal", "equal", "Verify the strings are equal");
	}

	@Test(
			description = "A failed test case"
		)
	public void TestFail() throws HarnessException {
		logger.info("FAIL");
		Assert.assertEquals("not equal", "equal", "Verify the strings are equal");
	}

	@Test(
			description = "A skipped test case"
		)
	public void TestSkipped01() throws HarnessException {
		logger.info("SKIP");
		Assert.assertEquals("not equal", "equal", "Verify the strings are equal");
	}
	
	@Test(
			description = "Another skipped test case"
		)
	public void TestSkipped02() throws HarnessException {
		logger.info("SKIP");
		Assert.assertEquals("equal", "equal", "Verify the strings are equal");
		throw new SkipException("Skip this method too!");
	}
	
	@BeforeMethod()
	public void beforeMethod(Method m) {
		logger.info("Method: " + m.getName());
		
		if ( m.getName().equals("TestSkipped01"))
			throw new SkipException("Skip this method");
	}
}
