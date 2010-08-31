package projects.zcs.tests.mail.compose.newwindow;

import java.lang.reflect.Method;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import projects.zcs.tests.CommonTest;
import com.zimbra.common.service.ServiceException;
import framework.core.SelNGBase;
import framework.util.RetryFailedTests;
import framework.util.SleepUtil;
import framework.util.ZimbraSeleniumProperties;

/**
 * @author Jitesh Sojitra
 */

@SuppressWarnings("static-access")
public class AttachBriefcaseFile extends CommonTest {
	protected int j = 0;

	//--------------------------------------------------------------------------
	// SECTION 1: DATA-PROVIDERS
	//--------------------------------------------------------------------------
	@DataProvider(name = "mailDataProvider")
	public Object[][] createData(Method method) throws ServiceException {
		String test = method.getName();
		if (test.equals("attachBriefcaseFileInMail_NewWindow")) {
			return new Object[][] { { SelNGBase.selfAccountName.get(),
					"ccuser@testdomain.com", "bccuser@testdomain.com",
					getLocalizedData(5), getLocalizedData(5),
					"testexcelfile.xls,testwordfile.doc" } };
		} else {
			return new Object[][] { { "" } };
		}
	}


	//--------------------------------------------------------------------------
	// SECTION 2: SETUP
	//--------------------------------------------------------------------------
	@BeforeClass(groups = { "always" })
	public void zLogin() throws Exception {
		super.zLogin();
	}

	//--------------------------------------------------------------------------
	// SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------
	@Test(dataProvider = "mailDataProvider", groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public void attachBriefcaseFileInMail_NewWindow(String to, String cc,
			String bcc, String subject, String body, String attachments)
			throws Exception {
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();

		checkForSkipException(
				"na",
				"SF",
				"39446",
				"New window goes blank while typing SHIFT C suddenly after login to web client (SF only)");

		String[] attachment = attachments.split(",");
		uploadFile(attachments);

		zGoToApplication("Mail");
		page.zComposeView.zNavigateToComposeByShiftClick();
		obj.zTextAreaField.zType(page.zComposeView.zToField,
				SelNGBase.selfAccountName.get());
		obj.zTextAreaField.zType(page.zComposeView.zCcField, cc);
		obj.zEditField.zType(page.zComposeView.zSubjectField, subject);
		obj.zEditor.zType(body);

		// adding briefcase file as an attachment & sending mail to self
		obj.zButton.zClick(page.zComposeView.zAddAttachmentIconBtn);
		obj.zTab.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		obj.zFolder.zClickInDlgByName(localize(locator.briefcase),
				localize(locator.attachFile));
		if (attachment.length == 2) {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__258__se",
					localize(locator.attachFile));
		} else {
			obj.zCheckbox.zClickInDlgByName("id=zlif__BCI__257__se",
					localize(locator.attachFile));
		}
		obj.zButton.zClickInDlgByName(localize(locator.attach),
				localize(locator.attachFile));
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zSendIconBtn);
		SleepUtil.sleep(3000);

		// verification
		SelNGBase.selenium.get().selectWindow(null);
		page.zMailApp.ClickCheckMailUntilMailShowsUp(subject);
		obj.zMessageItem.zClick(subject);
		SleepUtil.sleep(2000);
		if (ZimbraSeleniumProperties.getStringProperty("browser").equals("IE")) {
			Assert
					.assertTrue(
							SelNGBase.selenium
									.get()
									.isElementPresent(
											"xpath=//div[contains(@id,'zlif__CLV') and contains(@class,'ImgAttachment')]"),
							"Attachment symbol does not found");
		} else {
			obj.zMessageItem.zVerifyHasAttachment(subject);
		}
		// obj.zMessageItem.zVerifyHasAttachment(subject);
		obj.zButton.zClick(page.zMailApp.zForwardBtn);
		zWaitTillObjectExist("button", page.zComposeView.zSendIconBtn);
		for (int i = 0; i < attachment.length; i++) {
			obj.zCheckbox.zVerifyIsChecked(attachment[i].toLowerCase());
		}
		obj.zButton.zClick(page.zComposeView.zCancelIconBtn);
		SleepUtil.sleep(1000);

		SelNGBase.needReset.set(false);
	}

	private void uploadFile(String attachments) throws Exception {
		if (j == 0) {
			zGoToApplication("Briefcase");
			String[] attachment = attachments.split(",");
			for (int i = 0; i < attachment.length; i++) {
				page.zBriefcaseApp.zBriefcaseFileUpload(attachment[i], "");
			}
			j = j + 1;
		}
	}
}