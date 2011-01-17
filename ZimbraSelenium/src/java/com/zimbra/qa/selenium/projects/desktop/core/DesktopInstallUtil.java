package com.zimbra.qa.selenium.projects.desktop.core;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.zimbra.qa.selenium.framework.util.CommandLine;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;

import javax.xml.parsers.ParserConfigurationException;

/**
 * This class contains methods that can be used for Zimbra Desktop Installation and Uninstallation
 * @author Jeffry Hidayat
 *
 */
public class DesktopInstallUtil {
   protected static Logger logger = LogManager.getLogger(DesktopCommonTest.class);
   private static final String _commonRegistryPath = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall\\";
   private static String _desktopRegistryPath = null;
   private static final String _zimbraDesktopDisplayName = "Zimbra Desktop";
   //private static final String _buildUrl = "http://zre-matrix.eng.vmware.com/links/WINDOWS/HELIX/20110114070101_ZDESKTOP/ZimbraBuild/i386/";

   /**
    * Reads Windows registry value based on the specified location and key
    * @param location Registry Path
    * @param key Registry Key to be queried for value
    * @return (String) Raw output of the query
    */
   private static final String _readZimbraDesktopRegistry(String location, String key){
      try {
         String registryQuery = "reg query " + '"'+ location;
         // Run reg query, then read output with StreamReader (internal class)
         String[] lines = CommandLine.cmdExecWithOutput(registryQuery).split("\n");

         logger.debug("registryQuery is: " + registryQuery);
         logger.debug("Lines are: ");

         String output = null;
         String zimbraDesktopRegistryPath = null;
         if (_desktopRegistryPath == null) {
            for (int i = 0; i < lines.length; i++) {
               if (lines[i].trim().equals("")) {
                  // Skip it
               } else {
                  String displayName = CommandLine.cmdExecWithOutput("reg query " +
                        '"'+ lines[i] + "\" /v DisplayName");
                  if (displayName.contains(_zimbraDesktopDisplayName)) {
                     zimbraDesktopRegistryPath = _desktopRegistryPath = lines[i];
                     break;
                  }
               }
            }
         } else {
            zimbraDesktopRegistryPath = _desktopRegistryPath;
         }
         output = CommandLine.cmdExecWithOutput("reg query " +
               '"'+ zimbraDesktopRegistryPath + "\" /v " + key);
         return output;
         /**String registryQuery = "reg query " + '"'+ location + "\" /v " + key;
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
         return reader.getResult();*/
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
   private static final String _getRegistryValue(String location, String key) {
      String output = _readZimbraDesktopRegistry(location, key).trim();
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
    * Uninstalls Zimbra Desktop Application, if it was uninstalled already, then
    * just exits the method, otherwise it will execute and wait dynamically until
    * the uninstallation is done (Waiting for the registry query to return nothing)
    * @throws InterruptedException 
    * @throws IOException 
    * @throws HarnessException 
    */
   public static void uninstallDesktopApp() throws IOException, InterruptedException, HarnessException {
      OsType osType = OperatingSystem.getOSType();
      switch (osType) {
      case WINDOWS: case WINDOWS_XP:
         if (isDesktopAppInstalled()) {
            String uninstallCommand = DesktopInstallUtil._getRegistryValue(
                  DesktopInstallUtil._commonRegistryPath, "UninstallString") + " /quiet";
            //TODO: Do for Linux and Mac, right now it's only for Windows
            logger.info("uninstallCommand is: " + uninstallCommand);
      
            if (uninstallCommand.trim().equals("")) {
               logger.info("Zimbra Desktop App doesn't exist, thus exiting the method");
               return;
            } else {
               logger.debug("Executing command...");
               CommandLine.CmdExec(uninstallCommand);
               logger.debug("uninstallCommand execution is successful");
               GeneralUtility.waitFor("com.zimbra.qa.selenium.projects.desktop.core.DesktopInstallUtil", null, true, "isDesktopAppInstalled", null, WAIT_FOR_OPERAND.NEQ, true,
                     300 * 1000, 5 * 1000);
               logger.debug("Successfully waiting for");
               logger.info("Zimbra Desktop Uninstallation is complete!");
            }
         } else {
            logger.info("Desktop App doesn't exist, so quitting the uninstallation...");
         }
         break;
      case LINUX: case MAC:
         //TODO: Impelements Linux and MAC uninstallation method here
         break;
      }
      _desktopRegistryPath = null;
   }

   /**
    * Installs Zimbra Desktop Application, if the app has been installed (based on the readRegistry),
    * then nothing to proceed
    * @param msiLocation Location of the installation file
    * @throws HarnessException
    * @throws IOException
    * @throws InterruptedException
    */
   public static void installDesktopApp(String installFileBinaryLocation) throws HarnessException, IOException, InterruptedException {
      //TODO: Do for Linux and Mac, right now it's only for Windows
      File file = new File(installFileBinaryLocation);
      OsType osType = OperatingSystem.getOSType();
      switch (osType) {
      case WINDOWS: case WINDOWS_XP:
         if (DesktopInstallUtil.isDesktopAppInstalled()) {
            logger.info("Zimbra Desktop App has already been installed.");
         } else {
            if (!file.exists()) {
               throw new HarnessException ("MSI file doesn't exist at given path" + installFileBinaryLocation);
            } else {
               String installCommand = "MsiExec.exe /i " + installFileBinaryLocation + " /qn";
               logger.debug("installCommand is: " + installCommand);
               CommandLine.CmdExec(installCommand);
               logger.debug("installCommand execution is successful");
               GeneralUtility.waitFor("com.zimbra.qa.selenium.projects.desktop.core.DesktopInstallUtil", null, true, "isDesktopAppInstalled", null, WAIT_FOR_OPERAND.EQ, true,
                     300 * 1000, 5 * 1000);
               logger.info("Zimbra Desktop Installation is complete!");
            }
         }

         break;
      case LINUX: case MAC:
         // TODO: Implements installation methods for Linux and Mac
         break;
      }
   }

   /**
    * Determines whether the Zimbra Desktop client is installed
    * @return true, if it is installed, otherwise, false
    */
   public static boolean isDesktopAppInstalled() {
      OsType osType = OperatingSystem.getOSType();
      boolean isDesktopInstalled = false;
      switch (osType) {
      case WINDOWS: case WINDOWS_XP:
         String registry = DesktopInstallUtil._readZimbraDesktopRegistry(DesktopInstallUtil._commonRegistryPath, "UninstallString");
         if (registry != null) {
            if (!registry.trim().equals("")) {
               isDesktopInstalled = true;
               logger.debug("readRegistry: " + registry);
            }
         }
         break;
      case LINUX: case MAC:
         //TODO:
         break;
      }

      logger.debug("isDesktopInstalled = " + isDesktopInstalled);
      return isDesktopInstalled;   
   }
}
