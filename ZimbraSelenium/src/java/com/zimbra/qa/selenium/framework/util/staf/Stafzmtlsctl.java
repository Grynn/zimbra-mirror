package com.zimbra.qa.selenium.framework.util.staf;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties;

public class Stafzmtlsctl extends StafServicePROCESS {

   public enum SERVER_ACCESS {
      HTTP,
      HTTPS,
      BOTH
   }

   public void setServerAccess(SERVER_ACCESS serverAccess)
   throws HarnessException {
      String setting = null;

      StafServicePROCESS stafServicePROCESS = new StafServicePROCESS();
      stafServicePROCESS.execute("zmprov gs `zmhostname` ZimbraMailMode");
      String serverName = ZimbraSeleniumProperties.getStringProperty("server.host", "localhost");
      String mode = stafServicePROCESS.getStafResponse().split(serverName)[1].split("}")[0].split("zimbraMailMode:")[1].trim();

      logger.info("Current server access mode: " + mode);

      switch (serverAccess) {
      case HTTP:
         setting = "http";
         break;
      case HTTPS:
         setting = "https";
         break;
      case BOTH:
         setting = "both";
         break;
      }

      logger.info("Expected server access: " + setting);
      
      if (!mode.equals(setting)) {
         logger.debug("Set the server access mode to " + setting);
         execute("zmtlsctl " + setting);
         //execute("zmconfigdctl reload");
         SleepUtil.sleep(60000);
         stafServicePROCESS.execute("zmmailboxdctl restart");
         
         // Hardcoded 20 seconds sleep is required here, if this still doesn't work, then
         // we have to do the most robust way, wait for HTTP GET to return status 200 
         SleepUtil.sleep(20000);         
      } else {
         logger.info("Current and expected server access modes are the same already");
      }
   }

   public boolean execute(String command) throws HarnessException {
      setCommand(command);
      boolean output = super.execute();

      Stafpostqueue stafpostqueue = new Stafpostqueue();
      stafpostqueue.waitForPostqueue();

      return output;
   }

   protected String setCommand(String command) {

      // Make sure the full path is specified
      if ( command.trim().startsWith("zmtlsctl") ) {
         command = "/opt/zimbra/bin/" + command;
      }
      // Running a command as 'zimbra' user.
      // We must convert the command to a special format
      // START SHELL COMMAND "su - zimbra -c \'<cmd>\'" RETURNSTDOUT RETURNSTDERR WAIT 30000</params>

      StafParms = String.format("START SHELL COMMAND \"su - zimbra -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, this.getTimeout());
      return (getStafCommand());
   }
}
