package com.zimbra.qa.selenium.framework.items;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.ZimbraAccount;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class DesktopAccountItem implements IItem {
   protected static Logger logger = LogManager.getLogger(IItem.class);
   public String accountName = null;
   public String emailAddress = null;
   public String password = null;
   public String incomingServer = null;
   public String port = null;
   public boolean ssl = false;
   
   public DesktopAccountItem() {
      super();
   }

   /**
    * Generate Desktop account Item with specified email address
    * @param emailAddress
    * @return
    */
   public static DesktopAccountItem generateDesktopZimbraAccountItem(String emailAddress,
         String password, String port, boolean ssl) {
      DesktopAccountItem desktopAccountItem = new DesktopAccountItem();
      desktopAccountItem.accountName = "name" + ZimbraSeleniumProperties.getUniqueString();
      desktopAccountItem.emailAddress = emailAddress;
      desktopAccountItem.password = password;
      desktopAccountItem.incomingServer = ZimbraAccount.AccountZWC().ZimbraMailHost;
      desktopAccountItem.port = port;
      desktopAccountItem.ssl = ssl;

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
