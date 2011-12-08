package com.zimbra.qa.selenium.projects.desktop.ui.accounts;

import com.zimbra.qa.selenium.framework.items.DesktopAccountItem;
import com.zimbra.qa.selenium.framework.items.DesktopAccountItem.SECURITY_TYPE;
import com.zimbra.qa.selenium.framework.ui.AbsApplication;
import com.zimbra.qa.selenium.framework.ui.AbsPage;
import com.zimbra.qa.selenium.framework.ui.AbsTab;
import com.zimbra.qa.selenium.framework.ui.Action;
import com.zimbra.qa.selenium.framework.ui.Button;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;
import com.zimbra.qa.selenium.projects.desktop.ui.AppAjaxClient;
import com.zimbra.qa.selenium.projects.desktop.ui.PageLogin;

public class PageAddNewAccount extends AbsTab{

   public static class Locators {
      public static final String zAccountDropDown = "css=select[id='accountFlavor']";
      public static final String zSelectAccountTypeOption = zAccountDropDown + " option[value='']";
      public static final String zZimbraAccountOption = zAccountDropDown + " option[value='Zimbra']";
      public static final String zGmailAccountOption = zAccountDropDown + " option[value='Gmail']";
      public static final String zYahooAccountOption = zAccountDropDown + " option[value='YMP']";
      public static final String zMicrosoftExchangeIMAPOption = zAccountDropDown + " option[value='MSE']";
      public static final String zIMAPOption = zAccountDropDown + " option[value='Imap']";
      public static final String zPopOption = zAccountDropDown + " option[value='Pop']";
      public static final String zDisplayedMessage = "css=div[id='message']";
   }

   public enum DROP_DOWN_OPTION {
      ZIMBRA,
      GMAIL,
      YAHOO,
      MICROSOFT_EXCHANGE_IMAP,
      IMAP,
      POP
   }

   public PageAddNewAccount(AbsApplication application) {
      super(application);
      logger.info("new " + PageAddNewAccount.class.getCanonicalName());
   }


   public AbsPage zDropDownListSelect(DROP_DOWN_OPTION option) throws HarnessException {

      String valueLocator = null;
      AbsPage result = null;

      switch (option) {
      case ZIMBRA:
         valueLocator = "value=Zimbra";
         result = new FormAddZimbraAccount(MyApplication);
         break;
      case GMAIL:
         valueLocator = "value=Gmail";
         result = new FormAddGmailAccount(MyApplication);
         break;
      case IMAP:
         valueLocator = "value=Imap";
         result = new FormAddImapAccount(MyApplication);
         break;
      case MICROSOFT_EXCHANGE_IMAP:
         valueLocator = "value=MSE";
         result = new FormAddImapAccount(MyApplication);
         break;
      case POP:
         valueLocator = "value=Pop";
         result = new FormAddPopAccount(MyApplication);
         break;
      case YAHOO:
         valueLocator = "value=YMP";
         result = new FormAddYahooAccount(MyApplication);
         break;
      default:
         throw new HarnessException("Impelement me!");
      }

      GeneralUtility.waitForElementPresent(this, Locators.zAccountDropDown);
      sSelectDropDown(Locators.zAccountDropDown, valueLocator);

      if (result != null){
         result.zWaitForActive();
      }
      return result;
   }

   @Override
   public AbsPage zListItem(Action action, String item) throws HarnessException {
      throw new HarnessException("Add New Account page does not have lists");
   }

   @Override
   public AbsPage zListItem(Action action, Button option, String item)
         throws HarnessException {
      throw new HarnessException("Add New Account page does not have lists");
   }

   @Override
   public AbsPage zListItem(Action action, Button option, Button subOption,
         String item) throws HarnessException {
      throw new HarnessException("Add New Account page does not have lists");
   }

   @Override
   public void zNavigateTo() throws HarnessException {

      if (zIsActive()) {
         logger.info("Add New Account page is already active");
      } else {
         ((AppAjaxClient)MyApplication).zPageLogin.zNavigateTo();
         
         String locator = PageLogin.Locators.zAddNewAccountButton;
         GeneralUtility.waitForElementPresent(this, locator);
         sClick(locator);
         GeneralUtility.waitForElementPresent(this, Locators.zAccountDropDown);
      }
   }

   @Override
   public AbsPage zToolbarPressButton(Button button) throws HarnessException {
      throw new HarnessException("Add New Account page does not have a Toolbar");
   }

   @Override
   public AbsPage zToolbarPressPulldown(Button pulldown, Button option)
         throws HarnessException {
      throw new HarnessException("Add New Account page does not have a Toolbar");
   }

   @Override
   public String myPageName() {
      return (this.getClass().getName());
   }

   @Override
   public boolean zIsActive() throws HarnessException {
      if (!sIsElementPresent(Locators.zAccountDropDown)) {
         logger.info("Account Drop down list is not present");
         return false;
      } else {
         logger.info("Account Drop down list is present");
      }

      if (!zIsVisiblePerPosition(Locators.zAccountDropDown, 0, 0)) {
         logger.info("Account Drop down list is not visible per position (0, 0)");
         return false;
      } else {
         logger.info("Account Drop down list is visible per position (0, 0)");
      }

      return true;
   }

   /**
    * Adding Yahoo Account through UI Interaction
    * @return DestkopAccountItem of added Yahoo account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddYahooAccountThruUI() throws HarnessException {
      zNavigateTo();

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopYahooAccountItem(
            AjaxCommonTest.yahooUserName, AjaxCommonTest.yahooPassword);

      FormAddYahooAccount accountForm = (FormAddYahooAccount)((AppAjaxClient)MyApplication).
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.YAHOO);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      return desktopAccountItem;
   }

   /**
    * Adding Gmail Account through UI Interaction
    * @return DestkopAccountItem of added Gmail account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddGmailAccountThruUI() throws HarnessException {
      zNavigateTo();

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopGmailAccountItem(
            AjaxCommonTest.gmailUserName, AjaxCommonTest.gmailPassword);

      FormAddGmailAccount accountForm = (FormAddGmailAccount)((AppAjaxClient)MyApplication).
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.GMAIL);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      return desktopAccountItem;
   }

   /**
    * Adding Gmail IMAP Account through UI Interaction
    * @return DestkopAccountItem of added Gmail IMAP account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddGmailImapAccountThruUI() throws HarnessException {
      zNavigateTo();

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            AjaxCommonTest.gmailUserName,
            AjaxCommonTest.gmailUserName,
            AjaxCommonTest.gmailPassword,
            AjaxCommonTest.gmailImapReceivingServer,
            SECURITY_TYPE.SSL,
            "993",
            AjaxCommonTest.gmailImapSmtpServer,
            true,
            "465",
            AjaxCommonTest.gmailUserName,
            AjaxCommonTest.gmailPassword);

      FormAddImapAccount accountForm = (FormAddImapAccount)((AppAjaxClient)MyApplication).
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      return desktopAccountItem;
   }

   /**
    * Adding Zimbra IMAP Account through UI Interaction
    * @param zimbraEmailAddress Zimbra Email address to be added
    * @param zimbraPassword Zimbra Password to be added
    * @param serverName ZCS server that hosts the Zimbra account
    * @param sendSsl Sending Mail through SSL?
    * @param sendPort Port for sending mail
    * @return DestkopAccountItem of added Zimbra IMAP account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddZimbraImapAccountThruUI(String zimbraEmailAddress,
         String zimbraPassword, String serverName, boolean sendSsl, String sendPort)
   throws HarnessException {
      return zAddZimbraImapAccountThruUI(
            null,
            zimbraEmailAddress,
            zimbraPassword,
            serverName,
            sendSsl,
            sendPort);
   }

   /**
    * Adding Zimbra POP Account through UI Interaction
    * @param zimbraEmailAddress Zimbra Email address to be added
    * @param zimbraPassword Zimbra Password to be added
    * @param serverName ZCS server that hosts the Zimbra account
    * @param sendSsl Sending Mail through SSL?
    * @param sendPort Port for sending mail
    * @return DestkopAccountItem of added Zimbra POP account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddZimbraPopAccountThruUI(String zimbraEmailAddress,
         String zimbraPassword, String serverName, boolean sendSsl, String sendPort)
   throws HarnessException {
      return zAddZimbraPopAccountThruUI(null,
            zimbraEmailAddress,
            zimbraPassword,
            serverName,
            sendSsl,
            sendPort);
   }

   /**
    * Adding Zimbra IMAP Account through UI Interaction
    * @param accountName Account name to be given to ZD Client
    * @param zimbraEmailAddress Zimbra Email address to be added
    * @param zimbraPassword Zimbra Password to be added
    * @param serverName ZCS server that hosts the Zimbra account
    * @param sendSsl Sending Mail through SSL?
    * @param sendPort Port for sending mail
    * @return DestkopAccountItem of added Zimbra IMAP account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddZimbraImapAccountThruUI(String accountName, String zimbraEmailAddress,
         String zimbraPassword, String serverName, boolean sendSsl, String sendPort)
   throws HarnessException {
      zNavigateTo();

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopImapAccountItem(
            zimbraEmailAddress,
            zimbraEmailAddress,
            zimbraPassword,
            serverName,
            SECURITY_TYPE.SSL,
            null,
            serverName,
            sendSsl,
            sendPort,
            zimbraEmailAddress,
            zimbraPassword);

      if (accountName != null) {
         if (!accountName.equals("")) {
            desktopAccountItem.accountName = accountName;
         }
      }

      FormAddImapAccount accountForm = (FormAddImapAccount)((AppAjaxClient)MyApplication).
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.IMAP);
      accountForm.zFill(desktopAccountItem);

      accountForm.zSubmit(true);

      return desktopAccountItem;
   }

   /**
    * Adding Zimbra POP Account through UI Interaction
    * @param accountName Account name to be given to ZD Client
    * @param zimbraEmailAddress Zimbra Email address to be added
    * @param zimbraPassword Zimbra Password to be added
    * @param serverName ZCS server that hosts the Zimbra account
    * @param sendSsl Sending Mail through SSL?
    * @param sendPort Port for sending mail
    * @return DestkopAccountItem of added Zimbra POP account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddZimbraPopAccountThruUI(String accountName, String zimbraEmailAddress,
         String zimbraPassword, String serverName, boolean sendSsl, String sendPort)
   throws HarnessException {
      zNavigateTo();

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopPopAccountItem(
            zimbraEmailAddress,
            zimbraEmailAddress,
            zimbraPassword,
            serverName,
            SECURITY_TYPE.SSL,
            "995",
            serverName,
            sendSsl,
            sendPort,
            zimbraEmailAddress,
            zimbraPassword);

      if (accountName != null) {
         if (!accountName.equals("")) {
            desktopAccountItem.accountName = accountName;
         }
      }

      FormAddPopAccount accountForm = (FormAddPopAccount)((AppAjaxClient)MyApplication).
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);

      accountForm.zSubmit(true);

      return desktopAccountItem;
   }

   /**
    * Adding POP Account through UI Interaction
    * @return DestkopAccountItem of added POP account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddPopAccountThruUI() throws HarnessException {
      zNavigateTo();

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopPopAccountItem(
            AjaxCommonTest.hotmailUserName,
            AjaxCommonTest.hotmailUserName,
            AjaxCommonTest.hotmailPassword,
            AjaxCommonTest.hotmailPopReceivingServer,
            SECURITY_TYPE.SSL,
            "995",
            AjaxCommonTest.hotmailPopSmtpServer,
            false,
            "25",
            AjaxCommonTest.hotmailUserName,
            AjaxCommonTest.hotmailPassword);

      FormAddPopAccount accountForm = (FormAddPopAccount)((AppAjaxClient)MyApplication).
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.POP);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit();

      return desktopAccountItem;
   }

   /**
    * Adding Zimbra Account through UI Interaction with the default SSL disabled and server port
    * @return DestkopAccountItem of added Zimbra account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddZimbraAccountThruUI() throws HarnessException {
      return zAddZimbraAccountThruUI(ZimbraAccount.AccountZDC().EmailAddress,
            ZimbraAccount.AccountZDC().Password, false,
            ZimbraSeleniumProperties.getStringProperty("server.port", "80"));
   }

   /**
    * Adding Zimbra Account through UI Interaction
    * @param emailAddress Email Address
    * @param password Password
    * @param ssl SSL is enabled?
    * @param port Port Number to connect
    * @return DestkopAccountItem of added Zimbra account
    * @throws HarnessException
    */
   public DesktopAccountItem zAddZimbraAccountThruUI(String emailAddress, String password,
         boolean ssl, String port) throws HarnessException {
      zNavigateTo();

      DesktopAccountItem desktopAccountItem = DesktopAccountItem.generateDesktopZimbraAccountItem(
            emailAddress,
            password,
            port,
            ssl);

      FormAddZimbraAccount accountForm = (FormAddZimbraAccount)((AppAjaxClient)MyApplication).
            zPageAddNewAccount.zDropDownListSelect(DROP_DOWN_OPTION.ZIMBRA);
      accountForm.zFill(desktopAccountItem);
      accountForm.zSubmit(ssl);

      return desktopAccountItem;
   }

   public String zGetMessage() throws HarnessException {
      return zGetMessage(false);
   }

   public String zGetMessage(boolean negativeTest) throws HarnessException {
      if (negativeTest) {
         GeneralUtility.waitForElementPresent(this, Locators.zDisplayedMessage, 60000);
      } else {
         GeneralUtility.waitForElementPresent(this, Locators.zDisplayedMessage);
      }

      return sGetText(Locators.zDisplayedMessage);
   }

   /**
    * To see the message contains specified substring message
    * @param substring Substring message to be looked in the message
    * @return true if the message contains specified substring message, otherwise false
    */
   public boolean zMessageContains(String substring) {
      try {
      String message = sGetText(Locators.zDisplayedMessage);
      return message.contains(substring);
      } catch (Exception e) {
         return false;
      }
   }

}
