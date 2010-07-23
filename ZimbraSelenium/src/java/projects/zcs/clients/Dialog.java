package projects.zcs.clients;

import org.testng.Assert;

import framework.core.SelNGBase;
import framework.util.ZimbraUtil;

public class Dialog extends SelNGBase{
	//note: this doesnt extend from ZObject since click,rtclick, indlg etc doesnt make sense
	private String coreName;
	public Dialog () {
		this.coreName = "dialogCore";
	}
	public String zExistsDontWait(String dialogNameOrId) {
		//this method doesnt wait and also doesnt fail if object doesnt exist
		return  dialogCore(dialogNameOrId, "exists", false);
	}
	public void zNotExists(String dialogNameOrId) {
		String actual = dialogCore(dialogNameOrId, "notexists");
		Assert.assertEquals("true", actual, "Dialog(" + dialogNameOrId
				+ ") Found, which should not be present.");
	}
	public String zGetMessage(String dialogNameOrId) {
		return dialogCore(dialogNameOrId, "getmessage");
	}
	public String zIsNotPresent(String dialogNameOrId) {
		return  dialogCore(dialogNameOrId, "notexists");

	}	
	public void zWait(String dialogNameOrId) {
		this.zWait( dialogNameOrId, "", "");
	}
	public void zWait(String dialogNameOrId, String panel, String param1) {
		//don't call core(since it could go one of the core might be calling this(chicken and egg)
		selenium.call(coreName,  dialogNameOrId, "wait", true, panel, param1);
	}	
	public void zExists(String dialogNameOrId) {
		String actual = dialogCore(dialogNameOrId, "exists");
		Assert.assertEquals("true", actual, "Dialog(" + dialogNameOrId
				+ ") Not Found.");
	}

	public void zVerifyAlertMessage(String dialogNameOrId, String expectedMessage) {
		String actual = dialogCore(dialogNameOrId, "getmessage");
		if(actual.indexOf("\r\n")>=0)
			actual = actual.replace("\r\n", "");

		expectedMessage = expectedMessage.replace((char)160, (char)32);
		Assert.assertEquals(actual.trim(), expectedMessage.trim(), ZimbraUtil.printUnMatchedTextWithIndex(actual.trim(),expectedMessage.trim()));
	}
	public void zVerifyContainsText(String dialogNameOrId, String expectedText) {
		String actual = dialogCore(dialogNameOrId, "getalltxt");
		if(actual.indexOf(expectedText)== -1)
			Assert.fail("Expected text("+expectedText+") not found in actual("+actual+")");
	}	
	protected String dialogCore(String dialogNameOrId, String action) {
		return dialogCore(dialogNameOrId, action, true);
	}
	protected String dialogCore(String dialogNameOrId, String action, Boolean retryOnFalse) {
		return dialogCore(dialogNameOrId, action, retryOnFalse, "", "");
	}
	protected String dialogCore(String dialogNameOrId, String action, Boolean retryOnFalse,
			String panel, String param1) {
		return selenium.call(coreName, dialogNameOrId, action, retryOnFalse, panel, param1);

	}
	

}
