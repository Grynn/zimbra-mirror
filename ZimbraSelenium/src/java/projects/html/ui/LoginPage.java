package projects.html.ui;

import static org.testng.Assert.assertTrue;

import java.util.Map;

import com.zimbra.common.service.ServiceException;

import projects.html.clients.ProvZCS;
import projects.html.tests.CommonTest;

import framework.core.SelNGBase;
import framework.util.BrowserUtil;

@SuppressWarnings("static-access")
public class LoginPage extends CommonTest {
	/**
	 * Creates a random-user(user's locale is set to locale thats in the
	 * config.properties. And logs into Zimbra using this random-user
	 * 
	 * @return random-user that was created
	 * @throws ServiceException
	 */
	public String zLoginToZimbraAjax() throws ServiceException {
		String username = ProvZCS.getRandomAccount();
		zLoginToZimbraHTML(username);
		return username;
	}

	/**
	 * Creates a random-user with the preferences passed in as Map. PS: user's
	 * locale is automatically set to locale in config.properties
	 * 
	 * @param accntAttrs
	 *            Map of key=value, where key is zmprov name, value is zmprov
	 *            value
	 * @return username of the random-user
	 * @throws ServiceException
	 */
	public synchronized String zLoginToZimbraHTML(Map<String, Object> accntAttrs)
			throws ServiceException {
		String username = "";
		// if we are retrying the execution, then use the same account.
		if (SelNGBase.isExecutionARetry.get())
			username = SelNGBase.selfAccountName.get();
		else
			username = ProvZCS.getRandomAccount(accntAttrs);
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
			Thread.sleep(1500);
			currentBrowserName = BrowserUtil.getBrowserName();
			obj.zEditField.zType("Username:", username);
			obj.zPwdField.zType("Password:", password);
			obj.zButton.zClick("class=zLoginButton");
			Thread.sleep(2000);// without this we get permission denied error
			zWaitForElement("id=searchField");
			Thread.sleep(2000);// wait another 2 secs after we see the search
			// icon
		} catch (Exception e) {
			e.printStackTrace(System.out);

		}

	}

	public static boolean zWaitForElement(String elementId) {
		for (int i = 0; i < 10; i++) {
			if (SelNGBase.selenium.get().isElementPresent(elementId))
				return true;
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return false;
	}

	public static void zCustomLoginToZimbraAjax(String parameter) {
		try {
			String username = ProvZCS.getRandomAccount();
			SelNGBase.selfAccountName.set(username);
			customLogin(parameter);
			Thread.sleep(1500);
			currentBrowserName = BrowserUtil.getBrowserName();
			obj.zEditField.zType("Username:", username);
			obj.zPwdField.zType("Password:", "test123");
			obj.zButton.zClick("class=zLoginButton");
			Thread.sleep(2000);// without this we get permission denied error
			obj.zButton.zExists("id=zb__Search__MENU_left_icon");
			Thread.sleep(2000);// wait another 2 secs after we see the search
			// icon
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * clicks logoff link
	 * 
	 * @throws Exception
	 */
	public static void logoutOfZimbraAjax() throws Exception {
		SelNGBase.selenium.get().click("link=" + localize("logOff"));
		Thread.sleep(1000);
		assertTrue(true);
	}
}
