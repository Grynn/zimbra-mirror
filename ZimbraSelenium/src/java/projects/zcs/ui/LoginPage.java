package projects.zcs.ui;

import static org.testng.Assert.assertTrue;

import java.util.Map;

import com.zimbra.common.service.ServiceException;

import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

import framework.core.SelNGBase;
import framework.util.BrowserUtil;

@SuppressWarnings("static-access")
public class LoginPage extends CommonTest {
	public static final String zSearchFldr = "id=zb__Search__SEARCH_left_icon";

	/**
	 * Creates a random-user(user's locale is set to locale thats in the
	 * config.properties. And logs into Zimbra using this random-user
	 * 
	 * @return random-user that was created
	 * @throws ServiceException
	 */
	public String zLoginToZimbraAjax() throws ServiceException {
		String username = ProvZCS.getRandomAccount();
		zLoginToZimbraAjax(username);
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
	public String zLoginToZimbraAjax(Map<String, Object> accntAttrs)
			throws ServiceException {
		String username = "";
		// if we are retrying the execution, then use the same account.
		if (SelNGBase.isExecutionARetry)
			username = SelNGBase.selfAccountName;
		else
			username = ProvZCS.getRandomAccount(accntAttrs);
		SelNGBase.selfAccountName = username;
		zLoginToZimbraAjax(username);
		return username;
	}

	/**
	 * Logs into Zimbra using the given username. Also sets
	 * currentBrowserName-variable
	 * 
	 * @param username
	 */
	public void zLoginToZimbraAjax(String username) {
		zLoginToZimbraAjax(username, "test123");
	}

	/**
	 * Logs into Zimbra using the given username. Also sets
	 * currentBrowserName-variable
	 * 
	 * @param username
	 * @param password
	 */
	public void zLoginToZimbraAjax(String username, String password) {
		try {
			openApplication();
			Thread.sleep(1500);
			currentBrowserName = BrowserUtil.getBrowserName();
			obj.zEditField.zType("Username:", username);
			obj.zPwdField.zType("Password:", password);
			obj.zButton.zClick("class=zLoginButton");
			zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public static void zCustomLoginToZimbraAjax(String applicationTab) {
		try {
			String username = ProvZCS.getRandomAccount();
			SelNGBase.selfAccountName = username;
			customLogin(applicationTab);
			Thread.sleep(1000);
			currentBrowserName = BrowserUtil.getBrowserName();
			obj.zEditField.zType("Username:", username);
			obj.zPwdField.zType("Password:", "test123");
			obj.zButton.zClick("class=zLoginButton");
			zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
			Thread.sleep(1000);
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
		selenium.click("link=" + localize("logOff"));
		Thread.sleep(1000);
		assertTrue(true);
	}
}
