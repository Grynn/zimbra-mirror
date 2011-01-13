package com.zimbra.qa.selenium.projects.zcs.clients;

import org.testng.Assert;

import com.zimbra.qa.selenium.framework.util.HarnessException;


public class ContactListItem extends ListItem{
	public ContactListItem() {
		super("listItemCore", "ContactListItem");
	} 
			
		
	public void zVerifyIsSelected(String messageOrId)  throws HarnessException  {
		String actual = ZObjectCore(messageOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}
	
	
		
}

