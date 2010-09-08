package projects.html.tests.compose;

import java.lang.reflect.Method;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.core.*;
import framework.util.RetryFailedTests;

import projects.html.tests.CommonTest;

@SuppressWarnings({"static-access", "unused"})
public class SendBtnNegativeTests extends CommonTest {

	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) {

		return new Object[][]{
				{"", "", "", "NoAddress:" + getLocalizedData(2), "", "",
						localize(locator.criticalMsg),
						localize(locator.noAddresses)},
				{"$!~", "", "", "InvalidAddress " + getLocalizedData(2), "",
						"", localize(locator.warningMsg),
						localize(locator.compBadAddresses, "$!~", "")},
				{"_selfAccountName_", "", "", "", "", "",
						localize(locator.warningMsg),
						localize(locator.compSubjectMissing)}};

	}

	@BeforeClass(groups = {"always"})
	private void zLogin() throws Exception {
		zLoginIfRequired();
		SelNGBase.isExecutionARetry.set(false);
	}

	@BeforeMethod(groups = {"always"})
	private void zResetIfRequired() throws Exception {
		if (SelNGBase.needReset.get() && !SelNGBase.isExecutionARetry.get()) {
			zLogin();
		}
		SelNGBase.needReset.set(true);
	}
	@Test( groups = {"smoke", "test"})
	public void test()
			throws Exception {

		// if we are retrying the test, run cleanup and re-login etc
		if (SelNGBase.isExecutionARetry.get())
			handleRetry();
		
		
		obj.zButton.zClick("Compose");
		obj.zTextAreaField.zType(page.zComposeView.zToField, "admin@jitesh.com");
		obj.zButton.zClick("Send");
		ClientSessionFactory.session().selenium().selectWindow("The page at http://jitesh.com says:");
		ClientSessionFactory.session().selenium().click("OK");
		
		obj.zTab.zClick("Calendar");
	
		obj.zAppointment.zClick("test me", "2"); //clicks second appointment(we can use this to count)
		String str3 = obj.zMiscObj.zGetInnerText("ZhApptRecurrInfo", "2"); //gets some text from some object with classname ZhApptRecurrInfo
		String str4 = obj.zMiscObj.zGetInnerText("ZhCalDaySEP ZhCalDayHeaderToday");//get the text from Today's header (in week view)
		ClientSessionFactory.session().selenium().click("link=Edit the series.");//click on edit the series link
		obj.zFolder.zClick("first");
		//menus
		ClientSessionFactory.session().selenium().select("name=actionOp", "Mark as read");//html menu that selects mark as read
		String message = obj.zToastAlertMessage.zGetMsg();//Toast message
	
		//folders
		ClientSessionFactory.session().selenium().select("name=folderId", "Sent");
		obj.zFolder.zEdit("Folders");
		obj.zFolder.zClick("Sent");
		obj.zFolder.zExpand("Inbox");
		obj.zFolder.zCollapse("Inbox");
		
		//button
		obj.zButton.zClick("Compose");
		
		// editor
		obj.zEditor.zType("GPH"); //enter something to html/text editor
		
		//message item
		obj.zMessageItem.zClick("test");
		String str5 = obj.zMessageItem.zGetCurrentMsgHeaderText();
		String str = obj.zMessageItem.zGetCurrentMsgBodyText();
		obj.zMessageItem.zClickCheckBox ("someSubject");// click on message checkbox
		String rc = obj.zMessageItem.zIsChecked("someSubject number2", "2"); //check if a message is checked
		
		try {
			obj.zButton.zClick("Mark All Read");
		} catch (Exception e) {
			obj.zButton.zClick("Mark All Read");
		}

		obj.zMessageItem.zClick("test");
		obj.zButton.zClick(localize(locator.compose), "2");
		SelNGBase.needReset.set(false);
	}

	

	// since all the tests are independent, retry is simply kill and re-login
	private void handleRetry() throws Exception {
		SelNGBase.isExecutionARetry.set(false);
		page.zComposeView.zGoToMailAppFromCompose();
		zLogin();
	}

}
