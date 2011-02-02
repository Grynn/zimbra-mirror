package com.zimbra.qa.selenium.projects.desktop.core;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import com.zimbra.qa.selenium.framework.util.BuildUtility;
import com.zimbra.qa.selenium.framework.util.CommandLine;
import com.zimbra.qa.selenium.framework.util.GeneralUtility;
import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.OperatingSystem;
import com.zimbra.qa.selenium.framework.util.BuildUtility.ARCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.BRANCH;
import com.zimbra.qa.selenium.framework.util.BuildUtility.PRODUCT_NAME;
import com.zimbra.qa.selenium.framework.util.GeneralUtility.WAIT_FOR_OPERAND;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsArch;
import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;


/**
 * This class contains methods that can be used for Zimbra Desktop Installation and Uninstallation
 * @author Jeffry Hidayat
 *
 */
public class DesktopInstallUtil {
   protected static Logger logger = LogManager.getLogger(DesktopCommonTest.class);
   private static final String _commonRegistryPath_x64 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Wow6432Node\\Microsoft\\Windows\\CurrentVersion\\Uninstall";
   private static final String _commonRegistryPath_x86 = "HKEY_LOCAL_MACHINE\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Uninstall";
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
         String registryQuery = "reg query " + location;
         // Run reg query, then read output with StreamReader (internal class)
         String[] lines = CommandLine.cmdExecWithOutput(registryQuery).split("\n");

         logger.debug("registryQuery is: " + registryQuery);
         logger.debug("Lines are: ");

         String output = null;
         String zimbraDesktopRegistryPath = null;
         if (_desktopRegistryPath == null) {
            for (int i = 0; i < lines.length; i++) {
               if (lines[i].contains("\"")) {
                  lines[i] = lines[i].replaceAll("\"", "");
               }
               if (lines[i].trim().equals("")) {
                  // Skip it
               } else {
                  String displayName = CommandLine.cmdExecWithOutput("reg query " +
                         lines[i] + " /v DisplayName");
                  logger.debug("Display Name is: " + displayName);
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
                zimbraDesktopRegistryPath + " /v " + key);
         return output;

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
      // \n<Version information>\n\n<key>    *_SZ    <value>
      if (output == null) {
         return "";
      } else {
         if (output.equals("")) {
            return output;
         } else {
            // Parse out the value
            String[] parsed = output.split("_SZ");
            return parsed[parsed.length-1].trim();
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
      OsArch osArch = OperatingSystem.getOsArch();

      switch (osType) {
      case WINDOWS: case WINDOWS_XP:
         if (isDesktopAppInstalled()) {
            CommandLine.CmdExec("TASKKILL /F /IM zdclient.exe");
            CommandLine.CmdExec("TASKKILL /F /IM zdesktop.exe");

            String uninstallCommand = null;
            switch (osArch) {
            case X64:
               uninstallCommand = DesktopInstallUtil._getRegistryValue(
                     DesktopInstallUtil._commonRegistryPath_x64, "UninstallString") + " /quiet";
               break;
            case X86:
               uninstallCommand = DesktopInstallUtil._getRegistryValue(
                     DesktopInstallUtil._commonRegistryPath_x86, "UninstallString") + " /quiet";
               break;
            }

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
      OsArch osArch = OperatingSystem.getOsArch();
      boolean isDesktopInstalled = false;
      switch (osType) {
      case WINDOWS: case WINDOWS_XP:
         String registry = null;
         switch (osArch) {
         case X64:
            registry = DesktopInstallUtil._readZimbraDesktopRegistry(DesktopInstallUtil._commonRegistryPath_x64, "UninstallString");
            break;
         case X86:
            registry = DesktopInstallUtil._readZimbraDesktopRegistry(DesktopInstallUtil._commonRegistryPath_x86, "UninstallString");
            break;
         } 

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

   /**
    * This method will forcefully install the latest build. If there is a current installed build,
    * it will uninstall and install the latest.
    * @param prodName Product Name
    * @param branch Branch Name
    * @param arch Arch Name
    * @param downloadLocation Location to download the install file, ie. C:\\download-zimbra-qa-test\\ for windows
    * @throws HarnessException 
    * @throws InterruptedException 
    * @throws IOException 
    * @throws SAXException 
    */
   public static void forceInstallLatestBuild(PRODUCT_NAME prodName, BRANCH branch, ARCH arch,
         String downloadLocation)
         throws IOException, InterruptedException, HarnessException, SAXException {
      logger.info("Forcefully install latest build");
      boolean isDesktopAppInstalled = isDesktopAppInstalled();
      logger.info("isDesktopAppInstalled: " + isDesktopAppInstalled);
      if (isDesktopAppInstalled) {
         logger.info("Uninstalling the app");
         uninstallDesktopApp();
      } else {
         // Nothing to do here
      }

      logger.info("Downloading latest build");
      String fullDownloadPath = BuildUtility.downloadLatestBuild(downloadLocation, prodName, branch, arch);
      installDesktopApp(fullDownloadPath);
   }
}
