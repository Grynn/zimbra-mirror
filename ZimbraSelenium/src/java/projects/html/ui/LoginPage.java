package projects.html.ui;

import static org.testng.Assert.assertTrue;

import java.util.Map;

import projects.html.tests.CommonTest;

import com.zimbra.common.service.ServiceException;

import framework.core.SelNGBase;
import framework.util.BrowserUtil;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import framework.util.ZimbraAccount;

@SuppressWarnings("static-access")
public class LoginPage extends CommonTest {


	/**
	 * Creates a random-user with the preferences passed in as Map. PS: user's
	 * locale is automatically set to locale in config.properties
	 * 
	 * @param accntAttrs
	 *            Map of key=value, where key is zmprov name, value is zmprov
	 *            value
	 * @return username of the random-user
	 * @throws ServiceException
	 * @throws HarnessException 
	 */
	public synchronized String zLoginToZimbraHTML(Map<String, Object> accntAttrs)
			throws ServiceException, HarnessException {
		String username = "";		
		// if we are retrying the execution, then use the same account.
		if (SelNGBase.isExecutionARetry.get())
			username = SelNGBase.selfAccountName.get();
		else{		
			username = Stafzmprov.getRandomAccount();			
		}
		zLoginToZimbraHTML(username);
		return username;
	}

	/**
	 * Logs into Zimbra using the given username. Also sets
	 * currentBrowserName-variable
	 * 
	 * @param username
	 */
	public void zLoginToZimbraHTML(String username) {
		zLoginToZimbraHTML(username, "test123");
	}

	/**
	 * Logs into Zimbra using the given username. Also sets
	 * currentBrowserName-variable
	 * 
	 * @param username
	 * @param password
	 */
	public void zLoginToZimbraHTML(String username, String password) {
		try {
			openZimbraHTML();
			SleepUtil.sleep(1500);
			currentBrowserName = BrowserUtil.getBrowserName();
			obj.zEditField.zType("Username:", username);
			obj.zPwdField.zType("Password:", password);
			obj.zButton.zClick("class=zLoginButton");
			SleepUtil.sleep(2000);// without this we get permission denied error
			zWaitForElement("id=searchField");
			SleepUtil.sleep(2000);// wait another 2 secs after we see the search
			// icon
		} catch (Exception e) {
			e.printStackTrace(System.out);

		}

	}

	public static boolean zWaitForElement(String elementId) {
		for (int i = 0; i < 10; i++) {
			if (SelNGBase.selenium.get().isElementPresent(elementId))
				return true;
			SleepUtil.sleep(2000);
		}
		return false;
	}


	/**
	 * clicks logoff link
	 * 
	 * @throws Exception
	 */
	public void logoutOfZimbraAjax() throws Exception {
		SelNGBase.selenium.get().click("link=" + localize("logOff"));
		SleepUtil.sleep(1000);
		assertTrue(true);
	}
	
	
	private static ZimbraAccount currentAccount = null;
	private static ZimbraAccount setCurrentAccount(ZimbraAccount account) {
		currentAccount = account;
		return (currentAccount);
	}
	
	/**
	 * Get the currently logged in account
	 * @return
	 */
	public static ZimbraAccount getCurrentAccount() {
		return (currentAccount);
	}
}
