package projects.html.clients;

import org.testng.Assert;

public class MessageItem extends ListItem{
	public MessageItem() {
		super("listItemCore_html", "MessageItem");
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
		String actual = selenium.call("msgZHdrBodyCore_html", "", "gettext", "", "");
		Assert.assertTrue(actual.indexOf(requiredTxt)>=0);
	}
	public String zGetCurrentMsgHeaderText() {
		return selenium.call("msgZHdrBodyCore_html", "MsgHdr", "gettext", "", "");
	}
	public void zVerifyCurrentMsgBodyText(String requiredTxt) {
		String actual =  zGetCurrentMsgBodyText();
		Assert.assertTrue(actual.indexOf(requiredTxt)>=0);
	}
	public String zGetCurrentMsgBodyText() {
		return selenium.call("msgZHdrBodyCore_html", "MsgBody", "gettext", "", "");
	}
	public void zVerifyCurrentMsgBodyHasImage() {
		String actual =  selenium.call("msgZHdrBodyCore_html", "MsgBody", "gethtml", "", "");		
		Assert.assertTrue(actual.indexOf("dfsrc=")>=0);
	}	
	public String zGetMsgBodyHTML() {
		return selenium.call("msgZHdrBodyCore_html", "MsgBody", "gethtml", "", "");		
	}
}

