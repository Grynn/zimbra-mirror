package projects.html.clients;

import org.testng.Assert;

public class CheckBox extends ZObject{
	protected boolean isCheckbox = true;//if true then moveMouse's xy is adjusted differently for zActivate

	public CheckBox() {
		super("checkBoxCore", "Checkbox");
	} 
	public void zActivate(String objNameOrId, String objNumber) {
		String xy = ZObjectCore(objNameOrId, "getcoord", "", "", objNumber,
		"");
		moveMouseAndClick(xy, this.isCheckbox);
	}
	/**
	 * Literally clicks and activates on the object(using JAVA api)
	 * @param objNameOrId
	 */
	public void zActivate(String objNameOrId) {
		String xy = ZObjectCore(objNameOrId, "getcoord", "", "", "",
		"");
		moveMouseAndClick(xy, this.isCheckbox);
	}	
	/**
	 * Literally clicks and activates on the object(using JAVA api) in dialog
	 * @param objNameOrId
	 * @param objNumber
	 */	
	public void zActivateInDlg(String objNameOrId, String objNumber) {
		String xy = ZObjectCore(objNameOrId, "getcoord", "", "dialog", objNumber,
		"");
		moveMouseAndClick(xy, this.isCheckbox);
	}
	/**
	 * Literally clicks and activates on the object(using JAVA api) in dialog
	 * @param objNameOrId
	 */	
	public void zActivateInDlg(String objNameOrId) {
		String xy = ZObjectCore(objNameOrId, "getcoord", "", "dialog", "",
		"");
		moveMouseAndClick(xy, this.isCheckbox);
	}	

	public void zClick(String objNameOrId) {
		ZObjectCore(objNameOrId, "click",
				 "",  "",  "",  "");
	}
	public void zClickInDlg(String objNameOrId) {
		ZObjectCore(objNameOrId, "click",
				 "",  "dialog",  "",  "");
	}	
	public void zClickInDlgByName(String objNameOrId, String dialogName) {
		ZObjectCore(objNameOrId, "click",
				 "",  "__dialogByName__"+dialogName,  "",  "");
	}	


	public void zVerifyIsChecked(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "checked",
				 "",  "",  "",  "");
		Assert.assertEquals(actual, "true", objTypeName+"(" + objNameOrId
				+ ") doesn't exist in dialog or no dialog was found");
	}
	
	public boolean zGetStatus(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "checked",
				 "",  "",  "",  "");
		if (actual.equals("true"))
			return true;
		else
			return false;
	}
	
	public void zExists(String objNameOrId) {
		String actual =ZObjectCore(objNameOrId, "exists",
				 "",  "",  "",  "");
		Assert.assertEquals("true", actual, objTypeName+"(" + objNameOrId
				+ ") Not Found.");
	}	
	public void zExistsInDlg(String objNameOrId) {
		String actual =ZObjectCore(objNameOrId, "exists",
				 "",  "dialog",  "",  "");
		Assert.assertEquals(actual, "true", objTypeName+"(" + objNameOrId
				+ ") doesn't exist in dialog or no dialog was found");
	}
	public void zExistsInDlgByName(String objNameOrId, String dialogName) {
		String actual =ZObjectCore(objNameOrId, "exists",
				 "",  "__dialogByName__"+dialogName,  "",  "");
		Assert.assertEquals(actual, "true", objTypeName+"(" + objNameOrId
				+ ") doesn't exist in dialog("+dialogName+")");
	}	
	
}
