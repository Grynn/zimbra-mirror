package projects.zcs.tests.mail.compose;


import java.lang.reflect.Method;


import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import framework.util.RetryFailedTests;



import projects.zcs.tests.CommonTest;
import projects.zcs.ui.ComposeView;
import projects.zcs.ui.MailApp;


/**
 * @author Raja Rao DV
 */
@SuppressWarnings("static-access")
public class ComposeReplyFwdInPlainTextTests extends CommonTest {
    
    //--------------------------------------------------------------------------
    //		SECTION 1: DATA-PROVIDERS
    //--------------------------------------------------------------------------	
	@SuppressWarnings("unused")
	@DataProvider(name = "composeDataProvider")
	private Object[][] createData(Method method) {
		
		String test = method.getName();
		if(test.equals("sendMailToSelfAndVerify") 
			|| test.equals("sendMailToSelfAndVerify_NewWindow")) {
				return new Object[][] {						
						{"_selfAccountName_", "ccuser@testdomain.com", "bccuser@testdomain.com", getLocalizedData(2),getLocalizedData(5), ""},
						{"", "_selfAccountName_", "bccuser@testdomain.com", getLocalizedData(1),getLocalizedData(5), ""},
						{"", "", "_selfAccountName_", getLocalizedData(1),getLocalizedData(5), ""}
				};
		} else if(test.equals("fwdAMailWithAttachment")
				||(test.equals("detachRetainsAttachments"))) {//use this for tests that need a sample attachment
					return new Object[][] {							
							{"_selfAccountName_", "", "", getLocalizedData(2),"", "putty.log,samlejpg.JPG"}
														
					};
		} else if(test.equals("sendMailWithAttachmentToSelfAndVerify")) {
			return new Object[][] {
					
						{"_selfAccountName_", "", "", getLocalizedData(2),"", "putty.log"},
						{"_selfAccountName_", "", "", getLocalizedData(2),"", "putty.log,testexcelfile.xls"},							
				};
		}else if(test.equals("fwdingAMailWithAttachment_bug46375")) {//use this for tests that need a sample attachment
			return new Object[][] {							
					{"_selfAccountName_", "", "", getLocalizedData(2),getLocalizedData(5), "testexcelfile.xls"}
												
			};
		}else {//default
			return new Object[][] {
					{"_selfAccountName_", "ccuser@testdomain.com", "bccuser@testdomain.com", getLocalizedData(2),getLocalizedData(5), ""},
			};
		}
		
	}
	//--------------------------------------------------------------------------
	//		SECTION 2: SETUP
	//--------------------------------------------------------------------------	
	@BeforeClass(groups = {"always"})
	private  void zLogin() throws Exception {
		zLoginIfRequired();
		isExecutionARetry = false;
	}
	@SuppressWarnings("unused")
	@BeforeMethod(groups = {"always"})
	private  void zResetIfRequired() throws Exception {
		if(needReset && !isExecutionARetry){
			zLogin(); 
		}
		needReset = true;
	}
	
	
	//--------------------------------------------------------------------------
	//		SECTION 3: TEST-METHODS
	//--------------------------------------------------------------------------	
	//------------------------------Compose Tests..------------------------------------------------------	
	/**
	* Test: Send an email(to self) in text-mode in several ways(to-only,cc-only, etc) 
	* and verify if the received mail has all the information
	*/	
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full"}, retryAnalyzer = RetryFailedTests.class)
	public  void sendMailToSelfAndVerify(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
		    handleRetry();
	    page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify( to,  cc,  bcc,  subject,  body,  attachments);
		needReset=false;
	}
	/**
	* Test: Send an email(to self) in text-mode and in newwindow in several ways(to-only,cc-only, etc)
	*  and verify if the received mail has all the information
	*/	
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full"}, retryAnalyzer = RetryFailedTests.class)
	public  void sendMailToSelfAndVerify_NewWindow(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
		    handleRetry();
	    page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndVerify( to,  cc,  bcc,  subject,  body,  attachments);
		needReset = false;
	}

	//----------------------------------Reply tests...----------------------------------------
	/**
	* Test: Reply to an email in plain-mode   and verify if the mail-compose
	*  and verify that cc and bcc is empty,to is filled, subject has Re appended and body is intact 
	*/	
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public  void replyTest(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
		    handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_",  cc,  bcc,  subject,  body,  attachments);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Reply", "_selfAccountName_",  "",  "",  "Re: "+subject,  body,  attachments);		
		needReset = false;
	}
	

	/**
	* Test: Reply to an email in plain-mode  and in new-window and verify if the mail-compose
	*  and verify that cc and bcc is empty,to is filled, subject has Re appended  and body is intact
	*/	
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full"}, retryAnalyzer = RetryFailedTests.class)
	public  void replyTest_NewWindow(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
		    handleRetry();	    
		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_",  cc,  bcc,  subject,  body,  attachments);
		obj.zButton.zClick(MailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Reply", "_selfAccountName_",  "",  "",  "Re: "+subject,  body,  attachments);		
		needReset = false;
	}	
	
	//----------------------Reply All tests...------------------
	/**
	* Test: "Reply-all" to an email in plain-mode and verify if the mail-compose
	*  and verify that cc, bcc,to,subject and body are filled, subject has Re prepended  and body is intact
	*/	
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public  void replyAllTest(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {		
		if (isExecutionARetry)
		    handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_",  cc,  bcc,  subject,  body,  attachments);
		obj.zButton.zClick(MailApp.zReplyAllIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("ReplyAll", "_selfAccountName_",  cc,  "",  "Re: "+subject,  body,  attachments);			
		needReset = false;
	}
	
	/**
	* Test: "Reply-all" to an email in plain-mode and in-newwindow and verify if the mail-compose
	*  and verify that cc, bcc,to,subject and body are filled, subject has Re prepended  and body is intact
	*/
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full","test24"}, retryAnalyzer = RetryFailedTests.class)
	public  void replyAllTest_NewWindow(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {		
		if (isExecutionARetry)
		    handleRetry();
		page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_",  cc,  bcc,  subject,  body,  attachments);
		obj.zButton.zClick(MailApp.zReplyAllIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("ReplyAll", "_selfAccountName_",  cc,  "",  "Re: "+subject,  body,  attachments);			
		needReset = false;
	}	
	
	//-------------------Forward tests...----------------------------	
	/**
	* Test: Hit "Forward" to an email in plain-mode  and verify if the mail-compose
	*  and verify that cc, bcc and to are empty, but subject and body are filled. Also: subject has Fwd prepended 
	*/	
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)	
	public  void fwdMailTest(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {		
		if (isExecutionARetry)
		    handleRetry();
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_",  cc,  bcc,  subject,  body,  attachments);
		obj.zButton.zClick(MailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Forward", "",  "",  "",   "Fwd: "+subject,  body,  attachments);		
		needReset = false;
	}	
	
	/**
	* Test: Hit "Forward" to an email in html-mode  and in newWindow and verify if the mail-compose
	*  and verify that cc, bcc and to are empty, but subject and body are filled. Also: subject has Fwd prepended 
	*/
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)	
	public  void fwdMailTest_NewWindow(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {		
		// if we are retrying the test, run cleanup and re-login etc
		if (isExecutionARetry)
		    handleRetry();
	    	page.zComposeView.zNavigateToComposeByShiftClick();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_",  cc,  bcc,  subject,  body,  attachments);
		obj.zButton.zClick(MailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Forward", "",  "",  "",   "Fwd: "+subject,  body,  attachments);	
		needReset = false;
	}
	/**
	 * Test case :Issues with message view (MV) within a tab
	 *@Steps:-
	 *Login to web client
	 *Assume you have conversation which has lot of reply
	 *Double click to it
	 *Again double click to message so it switches its view to message view with
	 *previous/next buttons
	 *Press Next button to read mails one by one

	 *each time it should not composes new mail and  should not shows in separate tab.
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */

	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)
	public  void checkMesgViewWithInTabWithArrowPosition_40614(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {
		if (isExecutionARetry)
			handleRetry();

		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndSelectIt("_selfAccountName_",  cc,  bcc,  subject,  body,  attachments);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);
		page.zComposeView.zVerifyComposeFilledValues("Reply", "_selfAccountName_",  "",  "",  "Re: "+subject,  body,  attachments);
		obj.zButton.zClick(page.zMailApp.zReplyIconBtn);
		obj.zButton.zClick(ComposeView.zSendIconBtn);
		Thread.sleep(2000);
		MailApp.ClickCheckMailUntilMailShowsUp("Re: "+subject);
		obj.zMessageItem.zClick("Re: "+subject);
		obj.zMessageItem.zDblClick("Re: "+subject);

		Assert.assertTrue(selenium.isElementPresent("id=zv__CV_closeBtnCell"));
		/*obj.zMessageItem.zDblClick("-- Original Message --");
		System.out.println("--"  +localize(locator.origMsg)+  "--");
		obj.zMessageItem.zDblClick("--"  +localize(locator.origMsg)+  "--");*/
		obj.zMessageItem.zDblClick(localize(locator.origMsg));
		Assert.assertTrue(selenium.isElementPresent("xpath=//td[contains(@id,'zb__MSG1__Nav__PAGE_BACK_left_icon')]/div[contains(@class,'ImgLeftArrow ZDisabledImage')]"),"Left Arrow displyed as enable");
		Assert.assertTrue(selenium.isElementPresent("xpath=//td[contains(@id,'zb__MSG1__Nav__PAGE_FORWARD_left_icon')]/div[contains(@class,'ImgRightArrow')]"),"Right Arrow displyed as disable");
		Assert.assertTrue(selenium.isElementPresent("xpath=//td[contains(@id,'zb__App__DWT') and contains(text(),'Re:')]"),"Tab is not showing Re:");
		selenium.mouseOver("//td[contains(@id,'zb__MSG1__Nav__PAGE_FORWARD_left_icon')]/div[contains(@class,'ImgRightArrow')]");
		selenium.clickAt("//td[contains(@id,'zb__MSG1__Nav__PAGE_FORWARD_left_icon')]/div[contains(@class,'ImgRightArrow')]", "");
		Assert.assertTrue(selenium.isElementPresent("xpath=//td[contains(@id,'zb__App__DWT') and contains(text(),'Re:')]"),"After clicking Right arrow Tab is not showing Re:");

		needReset = false;
	}
	/**
	 * Test Case:-Issue with forwarding email as attachment
	 * Set Forwarding preference to "Include original message as an attachment"
	 *(Preferences --> Mail --> Composing)
	 * Forward a message (original shows as attachment)
	 * Click on Options in the "New Message" tool-bar and change from "Include
	 * Original As Attachment" to "Include Original Message"
	 * Expected:- Attachment msg should get convert into check box attachment with some text in body.
	 * @param to
	 * @param cc
	 * @param bcc
	 * @param subject
	 * @param body
	 * @param attachments
	 * @throws Exception
	 * @author Girish
	 */
	
	@Test(dataProvider = "composeDataProvider",groups = { "smoke", "full" }, retryAnalyzer = RetryFailedTests.class)	
	public  void fwdingAMailWithAttachment_bug46375(String to, String cc, String bcc, String subject, String body, String attachments) throws Exception {		
		if (isExecutionARetry)
		    handleRetry();
		
		page.zMailApp.zNavigateToComposingPreferences();
		/* NOTE:---Some times obj.zFeatureMenu.zClick(localize(locator.forwardingEmail), "3") doesn't work then we can use following xpath--*/
		/*	selenium.mouseOver("xpath=//html/body/div[4]/div[16]/div[2]/div[3]/table[2]/tbody/tr/td/table/tbody/tr[12]/td[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td[4]/div");
			selenium.mouseDownAt("xpath=//html/body/div[4]/div[16]/div[2]/div[3]/table[2]/tbody/tr/td/table/tbody/tr[12]/td[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td[4]/div","");
			selenium.mouseUpAt("xpath=//html/body/div[4]/div[16]/div[2]/div[3]/table[2]/tbody/tr/td/table/tbody/tr[12]/td[2]/table/tbody/tr/td/div/table/tbody/tr/td/table/tbody/tr/td[4]/div","");*/
		obj.zFeatureMenu.zClick(localize(locator.forwardingEmail), "3");
		obj.zMenuItem.zClick(localize(locator.includeOriginalAsAttach));
		if (config.getString("browser").equals("IE")) {
			Thread.sleep(1000);
		}
		obj.zButton.zClick(page.zCalApp.zPreferencesSaveIconBtn);
		Thread.sleep(2000);
		page.zComposeView.zNavigateToMailCompose();
		page.zComposeView.zSendMailToSelfAndVerify(to, cc, bcc, subject, body,
				attachments);
		obj.zButton.zClick(MailApp.zForwardIconBtn);
		obj.zButton.zExists(ComposeView.zSendIconBtn);

		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[@id='zv__COMPOSE1_attachments_div']/table/tbody/tr/td/div[contains(@class,'ImgAttachment')]"),
						"orignal msg doesn't contains attachment");
		Assert
				.assertTrue(
						selenium
								.isElementPresent("xpath=//div[@id='zv__COMPOSE1_attachments_div']/table/tbody/tr/td[2]/b[contains(text(),'"
										+ subject + "')]"),
						"orignal msg doesn't contains attachment with subject");
		obj.zCheckbox.zNotExists(attachments);
		Assert.assertFalse(obj.zEditor.zGetInnerText("").contains(
				localize(locator.origMsg)),
				"Body Should not contains any msg but still shows some msg");

		obj.zButton.zClick(ComposeView.zOptionsDownArrowBtn);
		Thread.sleep(1000);
		obj.zMenuItem.zClick(localize(locator.includeMenuBody));
		obj.zTextAreaField.zWait(localize(locator.toLabel));
		obj.zCheckbox.zVerifyIsChecked(attachments);
		Assert.assertTrue(obj.zEditor.zGetInnerText("").contains(
				localize(locator.origMsg)),
				"Body Should  contains  msg but still not showing msg");
		
		needReset = false;
	}	
	    //--------------------------------------------------------------------------
	    //		SECTION 4: RETRY-METHODS
	    //--------------------------------------------------------------------------	
	    // since all the tests are independent, retry is simply kill and re-login
	    private void handleRetry() throws Exception {
		isExecutionARetry = false;
		zLogin();
	    }
}