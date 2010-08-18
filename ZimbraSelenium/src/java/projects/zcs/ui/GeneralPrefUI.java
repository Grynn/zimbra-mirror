package projects.zcs.ui;

import org.testng.Assert;

import framework.core.SelNGBase;
import framework.util.ZimbraSeleniumProperties;

/**
 * This Class have UI-level methods related to preference-general tab
 * 
 * @author Prashant Jaiswal
 * 
 */
@SuppressWarnings("static-access")
public class GeneralPrefUI extends AppPage {
	public static final String zOldPassword = "id=oldPassword";
	public static final String zNewPassword = "id=newPassword";
	public static final String zConfirm = "id=confirm";

	public static void zNavigateToChangePasswordWindow() throws Exception {
		zGoToApplication("Preferences");
		obj.zButton.zClick(localize(locator.changePassword));
		Thread.sleep(2000);
	}

	public static void zEnterChangePWData(String oldPwd, String newPwd,
			String confirmPwd) {
		SelNGBase.selenium.get().selectWindow("_blank");
		obj.zPwdField.zType(zOldPassword, oldPwd);
		obj.zPwdField.zType(zNewPassword, newPwd);
		obj.zPwdField.zType(zConfirm, confirmPwd);
	}

	public static void zVerifyChangePwdErrMsg(String type, String oldPwd,
			String newPwd, String confirmPwd) throws Exception {
		String errorMessage;
		page.zGenPrefUI.zEnterChangePWData(oldPwd, newPwd, confirmPwd);
		obj.zButton.zClick("class=zLoginButton");
		Thread.sleep(2000);
		errorMessage = SelNGBase.selenium.get().getText("class=errorText");

		String expectedMsg = localize(locator.loginError);
		if (ZimbraSeleniumProperties.getStringProperty("locale").equals("en_US")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("en_GB")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("ar")
				|| ZimbraSeleniumProperties.getStringProperty("locale").equals("en_AU")) {
			expectedMsg = expectedMsg + ".";
		}
		System.out.println(type);
		System.out.println(errorMessage);
		System.out.println(expectedMsg);

		if (type.equals("WrongOldPassword"))
			Assert.assertTrue(errorMessage.equals(expectedMsg),
					"expeted message is " + localize(locator.loginError)
							+ "but the actual message is " + errorMessage);
		else if (type.equals("New&ConfirmPwdMismatch"))
			Assert.assertTrue(errorMessage
					.contains(localize(locator.bothNewPasswordsMustMatch)),
					"expeted message is "
							+ localize(locator.bothNewPasswordsMustMatch)
							+ "but the actual message is " + errorMessage);
		else if (type.equals("InvalidPwd"))
			Assert.assertTrue(errorMessage
					.contains(localize(locator.errorInvalidPass)),
					"expeted message is " + localize(locator.errorInvalidPass)
							+ "but the actual message is " + errorMessage);
		else if (type.equals("LessThan6Char"))
			Assert
					.assertTrue(
							errorMessage
									.contains("New password must have minimum length of 6 chars. Contact your System Administrator for more information."),
							"expeted message is 'New password must have minimum length of 6 chars. Contact your System Administrator for more information.'"
									+ " but the actual message is "
									+ errorMessage);
	}
}