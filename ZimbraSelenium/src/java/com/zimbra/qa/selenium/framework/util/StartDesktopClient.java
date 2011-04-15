package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class StartDesktopClient extends Thread {
   protected static Logger logger = LogManager.getLogger(StartDesktopClient.class);
   public String[] executablePath = null;
   public String[] params = null;

   public StartDesktopClient(String[] executablePath, String[] params) {
      this.executablePath = executablePath;
      this.params = params;
   }

   public void run() {
      try {
         logger.info(CommandLine.cmdExecWithOutput(executablePath, params));
      } catch (HarnessException e) {
         logger.info("Getting Harness Exception ");
         logger.info(e.getMessage());
         e.printStackTrace();
         
      } catch (IOException e) {
         e.printStackTrace();
      } catch (InterruptedException e) {
         e.printStackTrace();
      }
   }
}