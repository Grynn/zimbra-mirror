package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;
import com.zimbra.qa.selenium.projects.desktop.core.AjaxCommonTest;

public class DesktopAccountItem implements IItem {
   private static final Logger logger = LogManager.getLogger(DesktopAccountItem.class);
   
   public String accountName = null;
   public String fullName = null;
   public String emailAddress = null;
   public String password = null;
   public String incomingServer = null;
   public String port = null;
   public boolean ssl = false;

   // Needed for IMAP and POP
   public String receivingUsernname = null;
   public String receivingPassword = null;
   public String receiving = null;
   public String receivingIncomingServer = null;
   public SECURITY_TYPE receivingSecurityType = null;
   public String receivingPort = null;
   public String sendingSmtpServer = null;
   public boolean sendingThroughSsl = false;
   public String sendingPort = null;
   public String sendingUserName = null;
   public String sendingPassword = null;

   public DesktopAccountItem() {
      super();
      logger.info("new " + this.getClass().getName());
   }

   /**
    * Generate Desktop's Zimbra account Item with specified email address
    * @param emailAddress Email Address of Zimbra Account
    * @param password Password of Zimbra Account
    * @param port Port to access the Zimbra Account
    * @param incomingServer Zimbra Mail Server, if null, then it will pick the default from config.properties
    * @param ssl SSL triggered or not
    * @return Desktop Zimbra Account Item
    */
   public static DesktopAccountItem generateDesktopZimbraAccountItem(String emailAddress,
         String password, String port, String incomingServer, boolean ssl) {
      DesktopAccountItem desktopAccountItem = new DesktopAccountItem();
      desktopAccountItem.accountName = "name" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.emailAddress = emailAddress;
      desktopAccountItem.password = password;
      desktopAccountItem.incomingServer = (incomingServer == null) ?
            ZimbraAccount.AccountZDC().ZimbraMailHost : incomingServer;
      desktopAccountItem.port = port;
      desktopAccountItem.ssl = ssl;

      return desktopAccountItem;
   }

   /**
    * Generate Desktop's Zimbra account Item with specified email address with default incoming server
    * @param emailAddress Email Address of Zimbra Account
    * @param password Password of Zimbra Account
    * @param port Port to access the Zimbra Account
    * @param ssl SSL triggered or not
    * @return Desktop Zimbra Account Item
    */
   public static DesktopAccountItem generateDesktopZimbraAccountItem(String emailAddress,
         String password, String port, boolean ssl) {

      return generateDesktopZimbraAccountItem(emailAddress, password, port, null, ssl);
   }

   /**
    * Generate Desktop's Yahoo account Item with specified parameters
    * @param emailAddress Email Address of Yahoo Account
    * @param password Password of Yahoo Account
    * @return Desktop Yahoo Account Item
    * @throws HarnessException 
    */
   public static DesktopAccountItem generateDesktopYahooAccountItem(String emailAddress,
         String password) throws HarnessException {
      // TODO: Please remove this once issue in Mac is fixed.
      if (OperatingSystem.getOSType() == OsType.MAC) {
         throw new HarnessException(
               "Fail due to bug 61517, also refers to helpzilla ticket #811085");
      }
      DesktopAccountItem desktopAccountItem = new DesktopAccountItem();
      desktopAccountItem.accountName = "name" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.fullName = "Yahoo" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.emailAddress = emailAddress;
      desktopAccountItem.password = password;

      return desktopAccountItem;
   }

   /**
    * Generate Desktop's Gmail account Item with specified parameters
    * @param emailAddress Email Address of Gmail Account
    * @param password Password of Gmail Account
    * @return Desktop Gmail Account Item
    * @throws HarnessException 
    */
   public static DesktopAccountItem generateDesktopGmailAccountItem(String emailAddress,
         String password) throws HarnessException {
      // TODO: Please remove this once issue in Mac is fixed.
      if (OperatingSystem.getOSType() == OsType.MAC) {
         throw new HarnessException(
               "Fail due to bug 61517, also refers to helpzilla ticket #811085");
      }
      DesktopAccountItem desktopAccountItem = new DesktopAccountItem();
      desktopAccountItem.accountName = "name" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.fullName = "Gmail" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.emailAddress = emailAddress;
      desktopAccountItem.password = password;

      return desktopAccountItem;
   }

   public enum SECURITY_TYPE {
      NONE,
      SSL,
      TLS,
      TLS_IF_AVAILABLE
   }

   /**
    * Generate Desktop's IMAP account Item with specified parameters
    * @param emailAddress
    * @param receivingUsername
    * @param receivingPassword
    * @param receivingIncomingServer
    * @param receivingSecurityType
    * @param receivingPort
    * @param sendingSmtpServer
    * @param sendingThroughSsl
    * @param sendingPort
    * @param sendingUserName
    * @param sendingPassword
    * @return Desktop IMAP Account Item
    * @throws HarnessException
    */
   public static DesktopAccountItem generateDesktopImapAccountItem(
         String emailAddress,
         String receivingUsername,
         String receivingPassword,
         String receivingIncomingServer,
         SECURITY_TYPE receivingSecurityType,
         String receivingPort,
         String sendingSmtpServer,
         boolean sendingThroughSsl,
         String sendingPort,
         String sendingUserName,
         String sendingPassword) throws HarnessException{

      // TODO: Please remove this once issue in Mac is fixed.
      if (emailAddress.equals(AjaxCommonTest.gmailUserName) &&
            OperatingSystem.getOSType() == OsType.MAC) {
         throw new HarnessException(
               "Fail due to bug 61517, also refers to helpzilla ticket #811085");
      }

      DesktopAccountItem desktopAccountItem = new DesktopAccountItem();
      desktopAccountItem.accountName = "name" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.fullName = "Imap" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.emailAddress = emailAddress;
      desktopAccountItem.receivingUsernname = receivingUsername;
      desktopAccountItem.receivingPassword = receivingPassword;
      desktopAccountItem.receivingIncomingServer = receivingIncomingServer;
      desktopAccountItem.receivingSecurityType = receivingSecurityType;
      desktopAccountItem.receivingPort = receivingPort;
      desktopAccountItem.sendingSmtpServer = sendingSmtpServer;
      desktopAccountItem.sendingThroughSsl = sendingThroughSsl;
      desktopAccountItem.sendingPort = sendingPort;
      desktopAccountItem.sendingUserName = sendingUserName;
      desktopAccountItem.sendingPassword = sendingPassword;

      return desktopAccountItem;
   }

   /**
    * Generate Desktop's POP account Item with specified parameters
    * @param emailAddress
    * @param receivingUsername
    * @param receivingPassword
    * @param receivingIncomingServer
    * @param receivingSecurityType
    * @param receivingPort
    * @param sendingSmtpServer
    * @param sendingThroughSsl
    * @param sendingPort
    * @param sendingUserName
    * @param sendingPassword
    * @return Desktop POP Account Item
    * @throws HarnessException
    */
   public static DesktopAccountItem generateDesktopPopAccountItem(
         String emailAddress,
         String receivingUsername,
         String receivingPassword,
         String receivingIncomingServer,
         SECURITY_TYPE receivingSecurityType,
         String receivingPort,
         String sendingSmtpServer,
         boolean sendingThroughSsl,
         String sendingPort,
         String sendingUserName,
         String sendingPassword) throws HarnessException{
      DesktopAccountItem desktopAccountItem = new DesktopAccountItem();
      desktopAccountItem.accountName = "name" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.fullName = "Imap" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.emailAddress = emailAddress;
      desktopAccountItem.receivingUsernname = receivingUsername;
      desktopAccountItem.receivingPassword = receivingPassword;
      desktopAccountItem.receivingIncomingServer = receivingIncomingServer;
      desktopAccountItem.receivingSecurityType = receivingSecurityType;
      desktopAccountItem.receivingPort = receivingPort;
      desktopAccountItem.sendingSmtpServer = sendingSmtpServer;
      desktopAccountItem.sendingThroughSsl = sendingThroughSsl;
      desktopAccountItem.sendingPort = sendingPort;
      desktopAccountItem.sendingUserName = sendingUserName;
      desktopAccountItem.sendingPassword = sendingPassword;

      return desktopAccountItem;
   }

   @Override
   public void createUsingSOAP(ZimbraAccount account) throws HarnessException {
      throw new HarnessException("Can't create desktop account using SOAP!");
   }

   @Override
   public String getName() {
      return accountName;
   }

   @Override
   public String prettyPrint() {
      StringBuilder sb = new StringBuilder();
      sb.append(DesktopAccountItem.class.getSimpleName()).append('\n');
      sb.append("accountName: ").append(accountName).append('\n');
      sb.append("emailAddress: ").append(emailAddress).append('\n');
      sb.append("password: ").append(password).append('\n');
      sb.append("incomingServer: ").append(incomingServer).append('\n');
      sb.append("port: ").append(port).append('\n');
      sb.append("ssl: ").append(ssl).append('\n');
      return (sb.toString());
   }
}
