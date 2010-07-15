package projects.html.clients;

import framework.core.SelNGBase;
import org.testng.Assert;

public class MenuButton extends SelNGBase {
	public static void zClick(String menuBtnNameOrId) {
		MenuButtonCore(menuBtnNameOrId, "click");
	}	
	public static void zExists(String menuBtnNameOrId) {
		String actual = MenuButtonCore(menuBtnNameOrId, "exists");
		Assert.assertEquals(actual, "true", "Menu(" + menuBtnNameOrId
				+ ") Not Found.");
	}
	public static void zNotExists(String menuBtnNameOrId) {
		String actual = MenuButtonCore(menuBtnNameOrId, "notexists");
		Assert.assertEquals(actual, "true", "Menu(" + menuBtnNameOrId
				+ ") Found, which should not be present.");
	}
	public static void zIsEnabled(String menuBtnNameOrId) {
		String actual = MenuButtonCore(menuBtnNameOrId, "enabled");
		Assert.assertEquals(actual, "true", "Menu(" + menuBtnNameOrId
				+ ") is disabled");
	}
	public static void zIsDisabled(String menuBtnNameOrId) {
		String actual = MenuButtonCore(menuBtnNameOrId, "disabled");
		Assert.assertEquals(actual, "true", "Menu(" + menuBtnNameOrId
				+ ") is enabled(instead of disabled)");
	}	
	private static String MenuButtonCore(String menuBtnNameOrId, String action) {
		return MenuButtonCore(menuBtnNameOrId, action, true);
	}
	private static String MenuButtonCore(String menuBtnNameOrId, String action, Boolean retryOnFalse) {
		return MenuButtonCore(menuBtnNameOrId, action, retryOnFalse, "", "");
	}
	private static String MenuButtonCore(String menuBtnNameOrId, String action, Boolean retryOnFalse, String param1, String param2) {
		return selenium.call("buttonMenuCore",  menuBtnNameOrId, action, retryOnFalse, param1, param2);
	}
}
