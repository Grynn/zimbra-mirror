package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.filters;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.TreePreferences.TreeItem;


public class CreateFilter extends AjaxCommonTest {

	public CreateFilter() {
		
		super.startingPage = app.zPagePreferences;
		super.startingAccountPreferences = null;
		
	}


	@Test(
			description = "Create a basic filter",
			groups = { "smoke" }
			)
	public void CreateFilter_01() throws HarnessException {

		String filterName = "filter"+ ZimbraSeleniumProperties.getUniqueString();

		
		// Navigate to preferences -> mail -> composing
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailFilters);

		// See https://bugzilla.zimbra.com/show_bug.cgi?id=62323
		// **
		
		// Click "Add New"
		
		// Give a name
		
		// Give a criteria
		
		// Give an action (if necessary)
		
		// Save
		
		
		// Click save
		
		
		// Verify the filter is created
		app.zGetActiveAccount().soapSend(
						"<GetFilterRulesRequest xmlns='urn:zimbraMail'/>");
		
		Element[] rules = app.zGetActiveAccount().soapSelectNodes("//mail:GetFilterRulesResponse//mail:filterRule[@name='" + filterName +"']");
		ZAssert.assertEquals(rules.length, 1, "Verify the rule exists in the server");
		
	}
}
