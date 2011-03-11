package com.zimbra.qa.selenium.projects.zcs.tests.features.familymailboxes;

import org.testng.Assert;

import com.zimbra.qa.selenium.framework.core.ClientSessionFactory;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.staf.Stafzmprov;
import com.zimbra.qa.selenium.projects.zcs.tests.CommonTest;
import com.zimbra.qa.selenium.projects.zcs.ui.ComposeView;
import com.zimbra.qa.selenium.projects.zcs.ui.MailApp;


public class FamilyMailboxCommonTest extends CommonTest {
	public static String PARENT_ACCOUNT = "parent@testdomain.com";
	public static String CHILD_ACCOUNT ="child@testdomain.com";
	public static String CC_EMAIL_ADDRESS ="ccuser@testdomain.com";
	public static String BCC_EMAIL_ADDRESS ="bccuser@testdomain.com";
	public static String SUBJECT="";
	public static String BODY="";




	public static void addChildAccount() throws Exception {
		/**
		 * Add child Account
		 */
		PARENT_ACCOUNT=ClientSessionFactory.session().currentUserName().toLowerCase();
		CHILD_ACCOUNT=Stafzmprov.getRandomAccount().toLowerCase();
		String childUserAccountId=Stafzmprov.getAccountPreferenceValue(CHILD_ACCOUNT, "zimbraId");

		Stafzmprov.modifyAccount(PARENT_ACCOUNT, "zimbraChildAccount", childUserAccountId);
		Stafzmprov.modifyAccount(PARENT_ACCOUNT, "zimbraPrefChildVisibleAccount", childUserAccountId);

		ClientSessionFactory.session().selenium().refresh();
		SleepUtil.sleep(3500);
		zWaitTillObjectExist("class", "ZmOverviewZimletHeader");
	}

	public void clickAt(String accountName, String tabName) throws Exception{
		ClientSessionFactory.session().selenium().clickAt("//*[contains(@id,'"+accountName+"') and contains(text(),'"+tabName+"')]","");
	}

	public String createXpath(String accountName, String tabName) throws Exception{
		return "//*[contains(@id,'"+accountName+"') and contains(text(),'"+tabName+"')]";
	}

	public void checkHeaders(String headerText) throws Exception {
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@class, 'ZOptionsHeader ImgPrefsHeader') and contains(text(), '"+headerText+"')]");
	}

	public void checkLabels(String labelText) throws Exception {
		ClientSessionFactory.session().selenium().isElementPresent("//*[contains(@class, 'ZOptionsLabel') and contains(text(), '"+labelText+"')]");
	}

	@SuppressWarnings("static-access")
	public static void sendMailAndSelect(String to, String cc, String bcc, String subject, String body) throws Exception {
		page.zComposeView.zEnterComposeValues(to, cc, bcc, subject, body,"");
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		SleepUtil.sleep(1000);
		Boolean found = false;

		for (int i = 0; i < 5; i++) {
			obj.zButton.zClick(MailApp.zGetMailIconBtn);
			String rc = obj.zMessageItem.zExistsDontWait(SUBJECT);
			if (rc.equals("false")) {
				SleepUtil.sleep(500);
			} else {
				found = true;
				obj.zMessageItem.zClick(subject);
				break;
			}
		}

		if (!found)
			Assert.fail("Mail(" + SUBJECT + ") appeared after " + 30
					+ " seconds in Inbox");
	}

}






