package com.zimbra.qa.selenium.projects.ajax.tests.preferences.mail.filters;

import org.testng.annotations.Test;

import com.zimbra.common.soap.Element;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.ajax.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.DialogEditFilter;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.DialogEditFilter.ConditionConstraint;
import com.zimbra.qa.selenium.projects.ajax.ui.preferences.DialogEditFilter.ConditionType;
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
		String conditionValue = "contains"+ ZimbraSeleniumProperties.getUniqueString();

		
		// Navigate to preferences -> mail -> composing
		app.zTreePreferences.zTreeItem(Action.A_LEFTCLICK, TreeItem.MailFilters);

		// See https://bugzilla.zimbra.com/show_bug.cgi?id=62323
		// **
		
		// Click "Add New"
		DialogEditFilter dialog = (DialogEditFilter)app.zPagePreferences.zToolbarPressButton(Button.B_NEW_FILTER);
		
		// Give a name
		dialog.zSetFilterName(filterName);
		
		// Give a criteria
		dialog.zAddFilterCriteria(ConditionType.Subject, ConditionConstraint.Contains, conditionValue);
		
		// Give an action (if necessary)
		// dialog.zAddFilterAction();

		// Save
		dialog.zClickButton(Button.B_OK);
		
				
		
		// Verify the filter is created
		app.zGetActiveAccount().soapSend(
						"<GetFilterRulesRequest xmlns='urn:zimbraMail'/>");
		
		Element[] rules = app.zGetActiveAccount().soapSelectNodes("//mail:GetFilterRulesResponse//mail:filterRule[@name='" + filterName +"']");
		ZAssert.assertEquals(rules.length, 1, "Verify the rule exists in the server");
		
	}
}
