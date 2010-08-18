package parallel;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Test;

public class TestObject2 extends BaseTest {
	private static Logger logger = LogManager.getLogger(TestObject2.class);
		
	public int id = 0;
	protected ZimbraAccount classAccount = null;
	protected ZimbraApplication app = null;

	public TestObject2() {
		super();
		
		// debug logging
		id = myCounter();
		logger.info("instance "+ id +" base "+ baseID);
		
		classAccount = new ZimbraAccount();
		logger.info("classAccount: "+ classAccount.emailAddress);

		app = new ZimbraApplication();
	}
	
		
	
	@Test(groups = { "parallel" })
	public void testMethod01() throws InterruptedException {
		ZimbraAccount account = new ZimbraAccount();
		logger.info("testMethod01: local account "+ account.emailAddress);
		Thread.sleep(10000);
		logger.info("testMethod01: local account "+ account.emailAddress);
		logger.info("testMethod01: class account "+ classAccount.emailAddress);
	}
	
	@Test(groups = { "parallel" })
	public void testMethod02() throws InterruptedException {
		logger.info("testMethod02: class account "+ classAccount.emailAddress);
		Thread.sleep(10000);
		logger.info("testMethod02: class account "+ classAccount.emailAddress);
	}
	

	@Test(groups = { "parallel" })
	public void testMethod03() throws InterruptedException {
		ZimbraAccount account = new ZimbraAccount();
		app.login(account);
		logger.info("testMethod03: loggedInAccount "+ app.loggedInAccount.emailAddress);
		Thread.sleep(10000);
		logger.info("testMethod03: loggedInAccount "+ app.loggedInAccount.emailAddress);
	}
	

}
