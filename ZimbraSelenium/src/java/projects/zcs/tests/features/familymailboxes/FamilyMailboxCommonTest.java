package projects.zcs.tests.features.familymailboxes;

import framework.core.ClientSessionFactory;
import framework.util.SleepUtil;
import framework.util.Stafzmprov;
import projects.zcs.tests.CommonTest;

public class FamilyMailboxCommonTest extends CommonTest {
		public static String PARENT_ACCOUNT = "";
		public static String CHILD_ACCOUNT ="";
		
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

	
}
