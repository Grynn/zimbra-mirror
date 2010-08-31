package projects.zcs.tests.preferences.zimlets;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import projects.zcs.clients.ProvZCS;
import projects.zcs.tests.CommonTest;

/**
 * @author Jitesh Sojitra
 */
public class ZimletPreferences extends CommonTest {

	public static final String zZimletDateChkBox = "id=com_zimbra_date_zimletCheckbox";
	public static final String zZimletURLChkBox = "id=com_zimbra_url_zimletCheckbox";
	public static final String zZimletHighlightPhoneChkBox = "id=com_zimbra_phone_zimletCheckbox";
	public static final String zZimletEmailChkBox = "id=com_zimbra_email_zimletCheckbox";
	public static final String zZimletEmoticonsChkBox = "id=com_zimbra_ymemoticons_zimletCheckbox";
	public static final String zZimletLocalChkBox = "id=com_zimbra_local_zimletCheckbox";
	public static final String zZimletDnDChkBox = "id=com_zimbra_dnd_zimletCheckbox";
	//--------------------------------------------------------------------------
	// SECTION 1: SETUP
	//--------------------------------------------------------------------------

	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.NAVIGATION_TAB="preferences";
		super.zLogin();
		zGoToPreferences("Zimlets");
	}

	//--------------------------------------------------------------------------
	// SECTION 2: TEST-METHODS
	//--------------------------------------------------------------------------
	@SuppressWarnings("static-access")
	@Test(groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void zimletPreferenceTest() throws Exception {
		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		zWaitTillObjectExist("checkbox", zZimletDateChkBox);
		SleepUtil.sleep(3000);
		SelNGBase.selenium.get().clickAt(zZimletEmailChkBox,"");
		SelNGBase.selenium.get().clickAt(zZimletURLChkBox,"");
		SelNGBase.selenium.get().clickAt(zZimletEmoticonsChkBox,"");
		SelNGBase.selenium.get().clickAt(zZimletDateChkBox,"");
		SelNGBase.selenium.get().clickAt(zZimletHighlightPhoneChkBox,"");
		SelNGBase.selenium.get().clickAt(zZimletDnDChkBox,"");
		//Some times zActivate  and selenium.uncheck method is not working for IE so added above code
		/*obj.zCheckbox.zActivate(zZimletEmailChkBox);
		obj.zCheckbox.zActivate(zZimletURLChkBox);
		obj.zCheckbox.zActivate(zZimletEmoticonsChkBox);
		obj.zCheckbox.zActivate(zZimletDateChkBox);
		obj.zCheckbox.zActivate(zZimletLocalChkBox);
		obj.zCheckbox.zActivate(zZimletHighlightPhoneChkBox);*/
		obj.zButton.zClick("id=zb__PREF__SAVE_left_icon");
		SleepUtil.sleep(3000);
		obj.zButton.zClickInDlgByName(localize(locator.yes),
				localize(locator.warningMsg));
		SleepUtil.sleep(5000);

		zGoToApplication("Preferences");
		zGoToPreferences("Zimlets");
		System.out.println(ProvZCS.getAccountPreferenceValue(
				SelNGBase.selfAccountName.get(), "zimbraPrefZimlets"));
		obj.zCheckbox.zVerifyIsNotChecked(zZimletEmailChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletURLChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletEmoticonsChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletDateChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletDnDChkBox);
		obj.zCheckbox.zVerifyIsNotChecked(zZimletHighlightPhoneChkBox);
		
		// verify zimlets stuff if possible
		//page.zComposeView.zNavigateToMailCompose();
		//page.zComposeView.zSendMailToSelfAndSelectIt(SelNGBase.selfAccountName,
		//		"", "", "subject", "body", "");
		

		SelNGBase.needReset.set(false);
	}
}