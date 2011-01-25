package com.zimbra.qa.selenium.projects.desktop.tests.mail.compose;

import java.util.HashMap;

import org.testng.annotations.Test;

import com.zimbra.qa.selenium.framework.items.MailItem;
import com.zimbra.qa.selenium.framework.items.RecipientItem;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZAssert;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.projects.desktop.core.DesktopCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.PageMail;
import com.zimbra.qa.selenium.projects.desktop.ui.mail.FormMailNew;

public class CreateMailText extends DesktopCommonTest {
   @SuppressWarnings("serial")
   public CreateMailText() throws HarnessException {
      logger.info("New " + CreateMailText.class.getCanonicalName());
      super.startingAccountPreferences = new HashMap<String , String>() {{
         put("zimbraPrefComposeFormat", "text");
     }};
   }

   @Test(description = "Send a mail using Text editor",
         groups = {"sanity"})
   public void CreateMailText_01() throws HarnessException {
      // Create the message data to be sent
      MailItem mail = new MailItem();
      mail.dToRecipients.add(new RecipientItem(ZimbraAccount.AccountA()));
      mail.dSubject = "subject" + ZimbraSeleniumProperties.getUniqueString();
      mail.dBodyText = "body" + ZimbraSeleniumProperties.getUniqueString();

      // Open the new mail form
      FormMailNew mailform = (FormMailNew) app.zPageMail.zToolbarPressButton(Button.B_NEW);
      ZAssert.assertNotNull(mailform, "Verify the new form opened");

      // Fill out the form with the data
      mailform.zFill(mail);

      // Send the message
      mailform.zSubmit();

      ZimbraSeleniumProperties.waitForElementPresent(app.zPageMail, PageMail.Locators.zSendReceiveButton);
      app.zPageMail.sClick(PageMail.Locators.zSendReceiveButton);

      Object[] params = {ZimbraAccount.AccountA(), "subject:("+ mail.dSubject +")"};
      MailItem received = (MailItem)GeneralUtility.waitFor("com.zimbra.qa.selenium.framework.items.MailItem", null, true,
            "importFromSOAP", params, WAIT_FOR_OPERAND.NEQ, null, 30000, 1000);

      // TODO: add checks for TO, Subject, Body
      ZAssert.assertEquals(received.dFromRecipient.dEmailAddress, app.zGetActiveAccount().EmailAddress, "Verify the from field is correct");
      ZAssert.assertEquals(received.dToRecipients.get(0).dEmailAddress, ZimbraAccount.AccountA().EmailAddress, "Verify the to field is correct");
      ZAssert.assertEquals(received.dSubject, mail.dSubject, "Verify the subject field is correct");
      ZAssert.assertStringContains(received.dBodyText, mail.dBodyText, "Verify the body field is correct");
      
   }
}
