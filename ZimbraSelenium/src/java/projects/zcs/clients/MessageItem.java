package projects.zcs.clients;

import org.testng.Assert;

import framework.core.SelNGBase;

public class MessageItem extends ListItem{
	public MessageItem() {
		super("listItemCore", "MessageItem");
	} 
	public void zExpand(String objNameOrId) {
		ZObjectCore(objNameOrId, "expand");
	}		
	public void zVerifyIsUnRead(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "isUnread");
		Assert.assertEquals("true", actual);
	}
	public void zVerifyIsRead(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "isRead");
		Assert.assertEquals("true", actual);
	}	
	public void zVerifyIsFlagged(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "isFlagged");
		Assert.assertEquals("true", actual);
	}
	public void zVerifyIsNotFlagged(String objNameOrId) {
		String actual = ZObjectCore(objNameOrId, "isNotFlagged");
		Assert.assertEquals("true", actual);
	}	

	public void zVerifyCurrentMsgHeaderText(String requiredTxt) {
		String actual = SelNGBase.selenium.get().call("msgHeaderCore", "", "gettext", true, "", "");
		Assert.assertTrue(actual.indexOf(requiredTxt)>=0);
	}
	public String zGetCurrentMsgHeaderText() {
		return SelNGBase.selenium.get().call("msgHeaderCore", "", "gettext", true, "", "");
	}
	public void zVerifyCurrentMsgBodyText(String requiredTxt) {
		String actual =  SelNGBase.selenium.get().call("msgBodyCore", "", "gettext", true, "", "");
		Assert.assertTrue(actual.indexOf(requiredTxt)>=0);
	}
	public void zVerifyCurrentMsgBodyDoesNotHaveText(String requiredTxt) {
		String actual =  SelNGBase.selenium.get().call("msgBodyCore", "", "gettext", true, "", "");
		Assert.assertFalse(actual.indexOf(requiredTxt)>=0);
	}
	public String zGetCurrentMsgBodyText() {
		return SelNGBase.selenium.get().call("msgBodyCore", "", "gettext", true, "", "");
	}
	public void zVerifyCurrentMsgBodyHasImage() {
		String actual =  SelNGBase.selenium.get().call("msgBodyCore", "", "gethtml", true, "", "");		
		Assert.assertTrue(actual.indexOf("dfsrc=")>=0);
	}	
	public String zGetMsgBodyHTML() {
		return SelNGBase.selenium.get().call("msgBodyCore", "", "gethtml", true, "", "");		
	}
}

