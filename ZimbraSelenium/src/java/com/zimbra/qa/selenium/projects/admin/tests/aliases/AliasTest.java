package com.zimbra.qa.selenium.projects.admin.tests.aliases;

import org.testng.annotations.BeforeMethod;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.projects.admin.core.AdminCommonTest;
import com.zimbra.qa.selenium.projects.admin.items.AccountItem;

public class AliasTest extends AdminCommonTest {
	protected String targetAccountEmail = "";
	protected String targetAccountId= "";
	
	public AliasTest () {
		logger.info("New "+ CreateAlias.class.getCanonicalName());
		
		// All tests start at the "Aliases" page
		super.startingPage=app.zPageManageAliases;
	}

	@BeforeMethod 	( groups = { "always" } )
	public void createTargetAccount() throws HarnessException {
		AccountItem account = new AccountItem();
		AccountItem.createUsingSOAP(account);
		targetAccountEmail=account.getEmailAddress();
		targetAccountId=account.getID();
	}

}
