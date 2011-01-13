package com.zimbra.qa.selenium.projects.html.clients;

import org.testng.Assert;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class Dialog extends SelNGBase{
	//note: this doesnt extend from ZObject since click,rtclick, indlg etc doesnt make sense
	private String coreName;
	public Dialog () {
		this.coreName = "dialogCore";
	}
	public String zExistsDontWait(String dialogNameOrId) throws HarnessException   {
		//this method doesnt wait and also doesnt fail if object doesnt exist
		return  dialogCore(dialogNameOrId, "exists", false);
	}
	public void zNotExists(String dialogNameOrId)  throws HarnessException  {
		String actual = dialogCore(dialogNameOrId, "notexists", false);
		Assert.assertEquals("true", actual, "Dialog(" + dialogNameOrId
				+ ") Found, which should not be present.");
	}
	public String zGetMessage(String dialogNameOrId) throws HarnessException   {
		return dialogCore(dialogNameOrId, "getmessage");
	}
	public String zIsNotPresent(String dialogNameOrId) throws HarnessException   {
		return  dialogCore(dialogNameOrId, "notexists", false);

	}	
	public void zWait(String dialogNameOrId) throws HarnessException   {
		this.zWait(dialogNameOrId, "", "");
	}
	public void zWait(String dialogNameOrId, String panel, String param1) throws HarnessException   {
		//don't call core(since it could go one of the core might be calling this(chicken and egg)
		ClientSessionFactory.session().selenium().call(coreName,  dialogNameOrId, "wait", true, panel, param1);
	}	
	public void zExists(String dialogNameOrId) throws HarnessException   {
		String actual = dialogCore(dialogNameOrId, "exists");
		Assert.assertEquals("true", actual, "Dialog(" + dialogNameOrId
				+ ") Not Found.");
	}

	public void zVerifyAlertMessage(String dialogNameOrId, String expectedMessage) throws HarnessException  {
		String actual = dialogCore(dialogNameOrId, "getmessage");
		if(actual.indexOf("\r\n")>=0)
			actual = actual.replace("\r\n", "");
		
		Assert.assertEquals(actual.trim(), expectedMessage.trim(), "Actual("+actual.trim()+") didnt match expected("+expectedMessage.trim()+")");
	}
	public void zVerifyContainsText(String dialogNameOrId, String expectedText) throws HarnessException  {
		String actual = dialogCore(dialogNameOrId, "getalltxt");
		if(actual.indexOf(expectedText)== -1)
			Assert.fail("Expected text("+expectedText+") not found in actual("+actual+")");
	}	
	protected String dialogCore(String dialogNameOrId, String action) throws HarnessException   {
		return dialogCore(dialogNameOrId, action, true);
	}
	protected String dialogCore(String dialogNameOrId, String action, Boolean retryOnFalse)  throws HarnessException  {
		return dialogCore(dialogNameOrId, action, retryOnFalse, "", "");
	}
	protected String dialogCore(String dialogNameOrId, String action, Boolean retryOnFalse,
			String panel, String param1)  throws HarnessException  {
		return ClientSessionFactory.session().selenium().call(coreName, dialogNameOrId, action, retryOnFalse, panel, param1);
	}
}
