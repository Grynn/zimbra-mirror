package projects.zcs.tests.features.familymailboxes.mail;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import projects.zcs.tests.features.familymailboxes.FamilyMailboxCommonTest;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
public class Mail extends FamilyMailboxCommonTest {

	// --------------
	// section 2 BeforeClass
	// --------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="mail";
		super.zLogin();
	}

	@SuppressWarnings("static-access")
	@Test(groups = { "smoke", "test" }, retryAnalyzer = RetryFailedTests.class)
	public void familyMail_1() throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		addChildAccount();
		page.zComposeView.zNavigateToMailCompose();
		sendMailAndSelect(PARENT_ACCOUNT, CC_EMAIL_ADDRESS, BCC_EMAIL_ADDRESS, SUBJECT=getLocalizedData(5), BODY=getLocalizedData(15));
		
		clickAt(CHILD_ACCOUNT, localize(locator.inbox));
		page.zComposeView.zNavigateToMailCompose();
		sendMailAndSelect(CHILD_ACCOUNT, PARENT_ACCOUNT, BCC_EMAIL_ADDRESS, SUBJECT=getLocalizedData(5), BODY=getLocalizedData(15));
		clickAt(CHILD_ACCOUNT, localize(locator.inbox));
		obj.zMenuItem.zClick(SUBJECT);
		
		SelNGBase.needReset.set(false);
	}
}