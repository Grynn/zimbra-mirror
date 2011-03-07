package com.zimbra.qa.selenium.projects.html2.clients;

import org.testng.Assert;

import com.zimbra.qa.selenium.framework.util.HarnessException;


public class BriefcaseItem extends ListItem{
	public BriefcaseItem() {
		super("listItemCore", "BriefcaseItem");
	}		
	public void zVerifyBFItemIsSelected(String briefcaseItemOrId) throws HarnessException   {
		String actual = ZObjectCore(briefcaseItemOrId, "isSelected");
		Assert.assertEquals("true", actual);
	}
}