package com.zimbra.qa.selenium.projects.html2.clients;

import org.testng.Assert;

import com.zimbra.qa.selenium.framework.core.*;
import com.zimbra.qa.selenium.framework.util.HarnessException;


public class MessageItem extends ListItem{
	public MessageItem() {
		super("listItemCore_html", "MessageItem");
	} 
	public void zExpand(String objNameOrId) throws HarnessException   {
		ZObjectCore(objNameOrId, "expand");
	}		
	public void zVerifyIsUnRead(String objNameOrId)  throws HarnessException  {
		String actual = ZObjectCore(objNameOrId, "isUnread");
		Assert.assertEquals("true", actual);
	}
	public void zVerifyIsRead(String objNameOrId)  throws HarnessException  {
		String actual = ZObjectCore(objNameOrId, "isRead");
		Assert.assertEquals("true", actual);
	}	
	public void zVerifyIsFlagged(String objNameOrId)  throws HarnessException  {
		String actual = ZObjectCore(objNameOrId, "isFlagged");
		Assert.assertEquals("true", actual);
	}
	public void zVerifyIsNotFlagged(String objNameOrId)  throws HarnessException  {
		String actual = ZObjectCore(objNameOrId, "isNotFlagged");
		Assert.assertEquals("true", actual);
	}	

	public void zVerifyCurrentMsgHeaderText(String requiredTxt) throws HarnessException   {
		String actual = ClientSessionFactory.session().selenium().call("msgZHdrBodyCore_html", "", "gettext", true, "", "");
		Assert.assertTrue(actual.indexOf(requiredTxt)>=0);
	}
	public String zGetCurrentMsgHeaderText() throws HarnessException   {
		return ClientSessionFactory.session().selenium().call("msgZHdrBodyCore_html", "MsgHdr", "gettext", true, "", "");
	}
	public void zVerifyCurrentMsgBodyText(String requiredTxt)  throws HarnessException  {
		String actual =  zGetCurrentMsgBodyText();
		Assert.assertTrue(actual.indexOf(requiredTxt)>=0);
	}
	public String zGetCurrentMsgBodyText()  throws HarnessException  {
		return ClientSessionFactory.session().selenium().call("msgZHdrBodyCore_html", "MsgBody", "gettext", true, "", "");
	}
	public void zVerifyCurrentMsgBodyHasImage()  throws HarnessException  {
		String actual =  ClientSessionFactory.session().selenium().call("msgZHdrBodyCore_html", "MsgBody", "gethtml", true, "", "");		
		Assert.assertTrue(actual.indexOf("dfsrc=")>=0);
	}	
	public String zGetMsgBodyHTML()  throws HarnessException  {
		return ClientSessionFactory.session().selenium().call("msgZHdrBodyCore_html", "MsgBody", "gethtml", true, "", "");		
	}
}

