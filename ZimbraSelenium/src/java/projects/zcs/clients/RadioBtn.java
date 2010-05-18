package projects.zcs.clients;

import org.testng.Assert;

public class RadioBtn extends ZObject{
	public RadioBtn() {
		super("radioBtnCore", "RadioBtn");
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


	public void zVerifyIsChecked(String objNameOrId, String data) {
		String actual = ZObjectCore(objNameOrId, "checked",
				 data,  "",  "",  "");
		Assert.assertEquals(actual, "true", objTypeName+"(" + objNameOrId
				+ ") doesn't exist in dialog or no dialog was found");
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
