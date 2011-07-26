package com.zimbra.qa.selenium.framework.util.staf;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;

public class Stafzmtlsctl extends StafServicePROCESS {

   public enum SERVER_ACCESS {
      HTTP,
      HTTPS,
      BOTH
   }

   public void setServerAccess(SERVER_ACCESS serverAccess)
   throws HarnessException {
      String setting = null;
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

      execute("zmtlsctl " + setting);
      execute("zmmailboxdctl restart");

      // Hardcoded 10 seconds sleep is required here, if this still doesn't work, then
      // we have to do the most robust way, wait for HTTP GET to return status 200 
      SleepUtil.sleep(10000);
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
      if ( command.trim().startsWith("zmprov") ) {
         command = "/opt/zimbra/bin/" + command;
      }
      // Running a command as 'zimbra' user.
      // We must convert the command to a special format
      // START SHELL COMMAND "su - zimbra -c \'<cmd>\'" RETURNSTDOUT RETURNSTDERR WAIT 30000</params>

      StafParms = String.format("START SHELL COMMAND \"su - zimbra -c '%s'\" RETURNSTDOUT RETURNSTDERR WAIT %d", command, this.getTimeout());
      return (getStafCommand());
   }
}
