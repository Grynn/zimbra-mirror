package com.zimbra.qa.selenium.projects.desktop.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.CommandLine;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.SleepUtil;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class contains methods that can be used for Zimbra Desktop Installation and Uninstallation
 * @author Jeffry Hidayat
 *
 */
public class DesktopInstallUtil {
   protected static Logger logger = LogManager.getLogger(DesktopCommonTest.class);
   private static final String _desktopRegistryPath = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\{70D9DD93-2905-45F5-B89D-449465CB9246}";

   /**
    * Reads Windows registry value based on the specified location and key
    * @param location Registry Path
    * @param key Registry Key to be queried for value
    * @return (String) Raw output of the query
    */
   public static final String readRegistry(String location, String key){
      try {
         String registryQuery = "reg query " + '"'+ location + "\" /v " + key;
         logger.debug("registryQuery is: " + registryQuery);

         // Run reg query, then read output with StreamReader (internal class)
         logger.debug("Executing registry Query");
         Process process = Runtime.getRuntime().exec(registryQuery);

         StreamReader reader = new StreamReader(process.getInputStream());
         logger.debug("Starting the reader thread");
         reader.start();
         process.waitFor();
         reader.join();

         logger.debug("Getting the reader thread result");
         return reader.getResult();
      } catch (Exception e) {
         return null;
      }
   }

   /**
    * Gets the registry value from readRegistry method and parse it to only the value
    * @param location Registry Path
    * @param key Registry Key to be queried for value
    * @return (String) Value of the specified key
    */
   public static final String getRegistryValue(String location, String key) {
      String output = readRegistry(location, key).trim();
      logger.info("Registry output is: " + output);
      // Now process it
      // Output has the following format:
      // \n<Version information>\n\n<key>    <registry type>    <value>
      if (output == null) {
         return "";
      } else {
         if (output.equals("")) {
            return output;
         } else {
            // Parse out the value
            String[] parsed = output.split("    ");
            return parsed[parsed.length-1];
         }
      }
   }

   /**
    * Internal class for StreamReader, which extends Thread class
    * to ensure that it always synchronizes
    * @author Jeffry Hidayat
    *
    */
   static class StreamReader extends Thread {
      private InputStream is;
      private StringWriter sw = new StringWriter();
      public StreamReader(InputStream is) {
         this.is = is;
      }

      /**
       * Run method of the StreamReader class
       */
      public void run() {
         try {
            int c;
            while ((c = is.read()) != -1)
               sw.write(c);
         } catch (IOException e) {

         }
      }

      /**
       * Get the result of the Reader
       * @return StringWriter object that was written by run Method
       */
      public String getResult() {
         return sw.toString();
      }
   }

   /**
    * Uninstalls Zimbra Desktop Application, if it was uninstalled already, then
    * just exits the method, otherwise it will execute and wait dynamically until
    * the uninstallation is done (Waiting for the registry query to return nothing)
    * @throws InterruptedException 
    * @throws IOException 
    * @throws HarnessException 
    */
   private static void _uninstallDesktopApp() throws IOException, InterruptedException, HarnessException {
      String uninstallCommand = DesktopInstallUtil.getRegistryValue(
            DesktopInstallUtil._desktopRegistryPath, "UninstallString") + " /quiet";
      logger.info("uninstallCommand is: " + uninstallCommand);

      if (uninstallCommand.trim().equals("")) {
         logger.info("Zimbra Desktop App doesn't exist, thus exiting the method");
         return;
      } else {
         logger.debug("Executing command...");
         CommandLine.CmdExec(uninstallCommand);
         logger.debug("uninstallCommand execution is successful");
         Object [] params = {DesktopInstallUtil._desktopRegistryPath, "UninstallString"};
         GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", null, true, "readRegistry", params, WAIT_FOR_OPERAND.EQ, "",
               300 * 1000, 5 * 1000);
         logger.debug("Successfully waiting for");
      }
   }

   private static void _installDesktopApp(String msiLocation) throws HarnessException, IOException, InterruptedException {
      File file = new File(msiLocation);
      if (!file.exists()) {
         throw new HarnessException ("MSI file doesn't exist at given path" + msiLocation);
      } else {
         String installCommand = "MsiExec.exe /i " + msiLocation + " /qn";
         logger.debug("installCommand is: " + installCommand);
         CommandLine.CmdExec(installCommand);
         logger.debug("installCommand execution is successful");
         Object [] params = {DesktopInstallUtil._desktopRegistryPath, "UninstallString"};
         GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", null, true, "readRegistry", params, WAIT_FOR_OPERAND.NEQ, "",
               300 * 1000, 5 * 1000);
      }
   }
   
   // TODO: Please remove these below, before checking in
   public static void main(String[] args) throws HarnessException, SecurityException, ClassNotFoundException, IOException, InterruptedException {
      /**Object[] input = null;
      DesktopInstallUtil test = new DesktopInstallUtil();
      System.out.println(GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", null, true, "counter", input, WAIT_FOR_OPERAND.NEQ, new Integer(0), 10000, 1000));
      _count = 2;
      System.out.println(GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", test, false, "counter1", input, WAIT_FOR_OPERAND.NEQ, new Integer(4), 10000, 1000));
   }*/
      /**
      String uninstallCommand = DesktopInstallUtil.getRegistryValue(
            DesktopInstallUtil._desktopRegistryPath, "UninstallString") + " /quiet";
      System.out.println("uninstallCommand is: " + uninstallCommand);

      if (uninstallCommand.trim().equals("")) {
         System.out.println("Zimbra Desktop App doesn't exist, thus exiting the method");
         return;
      } else {
         System.out.println("Executing command...");
         CommandLine.CmdExec(uninstallCommand);
         System.out.println("Command execution successful");
         Object [] params = {DesktopInstallUtil._desktopRegistryPath, "UninstallString"};
         GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", null, true, "readRegistry", params, WAIT_FOR_OPERAND.EQ, "",
               600 * 1000, 5 * 1000);
         System.out.println("Successfully waiting for");
      }*/
      String msiLocation = "C:\\Jeff_Test\\zdesktop_7_0_dev-helix_b10684_win32.msi";
      File file = new File(msiLocation);
      if (!file.exists()) {
         throw new HarnessException ("MSI file doesn't exist at given path" + msiLocation);
      } else {
         String installCommand = "MsiExec.exe /i " + msiLocation + " /qn";
         System.out.println("installCommand is: " + installCommand);
         CommandLine.CmdExec(installCommand);
         System.out.println("installCommand execution is successful");
         Object [] params = {DesktopInstallUtil._desktopRegistryPath, "UninstallString"};
         GeneralUtility.waitFor("projects.desktop.core.DesktopInstallUtil", null, true, "readRegistry", params, WAIT_FOR_OPERAND.NEQ, "",
               300 * 1000, 5 * 1000);
      }
   }

   private static int _count = 0;
   public static int counter() {
      _count++;
      return _count;
   }
   
   public int counter1() {
      _count += 2;
      return _count;
   }
}
