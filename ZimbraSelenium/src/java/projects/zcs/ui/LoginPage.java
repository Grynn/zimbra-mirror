package projects.zcs.ui;

import static org.testng.Assert.assertTrue;

import java.util.Map;

import com.zimbra.common.service.ServiceException;

import framework.core.*;
import framework.util.BrowserUtil;
import framework.util.HarnessException;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;

@SuppressWarnings("static-access")
public class LoginPage extends AppPage {
	public static final String zSearchFldr = "id=zb__Search__SEARCH_left_icon";

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
	public String zLoginToZimbraAjax() throws ServiceException, HarnessException {
		
		String username = "";
		// if we are retrying the execution, then use the same account.
		if (SelNGBase.isExecutionARetry.get()) {
			username = SelNGBase.selfAccountName.get();
		} else {
			username = Stafzmprov.getRandomAccount();
		}
		SelNGBase.selfAccountName.set(username);
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
			SleepUtil.sleep(1500);
			currentBrowserName = BrowserUtil.getBrowserName();
			obj.zEditField.zType("Username:", username);
			obj.zPwdField.zType("Password:", password);
			obj.zButton.zClick("class=zLoginButton");
			zWaitTillObjectExist("id", "ztih__main_Mail__ZIMLET_textCell");
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
	}

	public static void zCustomLoginToZimbraAjax(String parameter)
			throws Exception {
		try {
			String username = Stafzmprov.getRandomAccount();
			SelNGBase.selfAccountName.set(username);
			customLogin(parameter);
			SleepUtil.sleep(1000);
			currentBrowserName = BrowserUtil.getBrowserName();
			obj.zEditField.zType("Username:", username);
			obj.zPwdField.zType("Password:", "test123");
			obj.zButton.zClick("class=zLoginButton");
			SleepUtil.sleep(3000);
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
		ClientSessionFactory.session().selenium().click("link=" + localize("logOff"));
		SleepUtil.sleep(1000);
		assertTrue(true);
	}
}
