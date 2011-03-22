package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;

public class StartDesktopClient extends Thread {
   public String executablePath = null;
   public String[] params = null;

   public StartDesktopClient(String executablePath, String[] params) {
      this.executablePath = executablePath;
      this.params = params;
   }

   public void run() {
      try {
         CommandLine.CmdExec(executablePath, params);
      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      } catch (HarnessException e) {
         e.printStackTrace();
      }
   }
}