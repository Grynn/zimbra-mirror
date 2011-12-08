package com.zimbra.qa.selenium.projects.desktop.tests.preferences.mail.trustedaddresses;

import java.io.File;
import java.util.HashMap;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;
import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.LmtpInject;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.preferences.trustedaddresses.DisplayTrustedAddress;

public class UnTrustedDomainConvView extends AjaxCommonTest {

   @SuppressWarnings("serial")
   public UnTrustedDomainConvView() throws HarnessException {
      super.startingPage = app.zPageMail;

      // Make sure we are using an account with conversation view
      super.startingAccountPreferences = new HashMap<String, String>() {
         {
            put("zimbraPrefGroupMailBy", "conversation");
            put("zimbraPrefMessageViewHtmlPreferred", "TRUE");

         }
      };
   }
/**
 * TestCase : UnTrusted Domain with Conversation view
 * 1.Don't add any domain in Preference/Mail/Trusted Addresses
 * 2.In Conv View Inject mail with external image
 * 3.Verify To,From,Subject through soap
 * 4.Click on same mail
 * 5.Yellow color Warning Msg Info bar should show warning icon with 'Display Image' and Domain  link for untrusted domains.
 * 
 * @throws HarnessException
 */
   @Test(description = "Verify Display Image link in UnTrusted doamin for conversation view", groups = { "smoke" })
   public void UnTrustedDomainConvView_01() throws HarnessException {

      final String subject = "TestTrustedAddress";
      final String from = "admintest@testdoamin.com";
      final String to = "admin@testdoamin.com";
      final String mimeFolder = ZimbraSeleniumProperties.getBaseDirectory()
            + "/data/public/mime/ExternalImg.txt";

      // Inject the external image message(s)
      LmtpInject.injectFile(app.zGetActiveAccount().EmailAddress, new File(mimeFolder));

      MailItem mail = MailItem.importFromSOAP(app.zGetActiveAccount(),subject);

      ZAssert.assertNotNull(mail, "Verify message is received");
      ZAssert.assertEquals(from, mail.dFromRecipient.dEmailAddress,"Verify the from matches");
      ZAssert.assertEquals(to, mail.dToRecipients.get(0).dEmailAddress,"Verify the to address");

      // Click Get Mail button
      app.zPageMail.zToolbarPressButton(Button.B_GETMAIL);

      // Select the message so that it shows in the reading pane
      app.zPageMail.zListItem(Action.A_LEFTCLICK, subject);
      
      DisplayTrustedAddress actual = new DisplayTrustedAddress(app);
      
      //Verify Warning info bar with other links
      //ZAssert.assertTrue(actual.zDisplayImageLink("message").equals(""),"Verify Display Image link is present");
      ZAssert.assertTrue(actual.zHasWDDLinks("conversation"),"Verify Display Image,Domain link  and warning icon are present");

   }

   @AfterMethod(alwaysRun=true)
   public void cleanUp() {
      ZimbraAccount.ResetAccountZDC();
   }
}
