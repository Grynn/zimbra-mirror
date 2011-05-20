package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;

public class ZimbraDesktopProperties {

   private String _serialNumber = null;
   private String _localConfigFileLocation = null;
   private String _userName = null;

   private static ZimbraDesktopProperties _instance = null;

   private static final Logger logger = LogManager.getLogger(ZimbraDesktopProperties.class);

   private ZimbraDesktopProperties() {
      logger.debug("New ZimbraDesktopProperties");
      switch (OperatingSystem.getOSType()) {
      case WINDOWS: case WINDOWS_XP:
         this._userName = System.getProperty("user.name");;
         break;
      case LINUX: case MAC:
         // For Linux and MAC, enforce it to "zimbra" user because
         // TMS will shoot the staf command as root and installation & launch
         // must be done using non-root user
         this._userName = "zimbra";
         break;
      }
   }

   public String getUserName() {
      return this._userName;
   }

   public static synchronized void reset() {
      _instance = null;
   }

   public static ZimbraDesktopProperties getInstance() {
      try {
         if (_instance == null ||
               _instance.getLocalConfigFileLocation() == null ||
               !_instance.getSerialNumber().equals(XmlStringUtil.parseXmlFile(_instance.getLocalConfigFileLocation(),
               "zdesktop_installation_key"))) {
            synchronized (ZimbraDesktopProperties.class) {
               _instance = new ZimbraDesktopProperties();
               _instance.init();
            }
         }
         return _instance;

      } catch (IOException ie) {
         logger.info(
               "Getting IO Exception while getting instance of ZimbraDesktopProperties...");
      }

      return null;
   }

   public static String getUserFolder() {
      if (_instance == null ||
            _instance._localConfigFileLocation == null) {
         logger.debug("User Folder is NULL!");
         return null;
      } else {
         String output = null;

         switch(OperatingSystem.getOSType()) {
         case WINDOWS: case WINDOWS_XP:
            output = _instance._localConfigFileLocation.split("Zimbra Desktop")[0]; 
            break;
         case LINUX: case MAC:
            output = _instance._localConfigFileLocation.split("conf")[0];
            break;
         }

         logger.debug("User Folder is: " + output);
         return output;
      }
   }

   private final static String [] _possibleFiles = {
      "/opt/zimbra/zdesktop/conf/localconfig.xml",
      "/home/<USER_NAME>/zdesktop/conf/localconfig.xml",
      "C:\\Documents and Settings\\<USER_NAME>\\Local Settings\\Application Data\\Zimbra\\Zimbra Desktop\\conf\\localconfig.xml",
      "/opt/<USER_NAME>/Library/Zimbra\\ Desktop/conf/localconfig.xml",
      "/opt/<USER_NAME>/Library/Zimbra Desktop/conf/localconfig.xml"
   };

   private void init() {
      OsType osType = OperatingSystem.getOSType();
      logger.info("currentLoggedInUser: " +
            _userName);

      for (int i = 0; i < _possibleFiles.length; i++) {
         _possibleFiles[i] = _possibleFiles[i].replace(
               "<USER_NAME>", _userName);
         if (osType == OsType.WINDOWS || osType == OsType.WINDOWS_XP) {
            if (!_possibleFiles[i].contains("C:\\")) {
               continue;
            } else {
               // Fall Through

            }
         } else {
            if (_possibleFiles[i].contains("C:\\")) {
               continue;
            } else {
               // Fall Through

            }
         }
         logger.info("Parsing XML file: " + _possibleFiles[i]);
         try {
            this._setSerialNumber(XmlStringUtil.parseXmlFile(_possibleFiles[i],
                  "zdesktop_installation_key"));
            this._setLocalConfigFileLocation(_possibleFiles[i]);
         } catch (IOException ioe) {
            if (i != (_possibleFiles.length - 1)) {
               continue;
            } else {
               logger.info("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX------------------->>>>>>>>>>>>>>>" +
               		"Couldn't find the local config file for Desktop");
            }
         }
      }
   }

   private void _setSerialNumber(String serialNumber) {
      _serialNumber = serialNumber;
   }
   public String getSerialNumber() {
      return _serialNumber;
   }

   // There is no setter for ConnectionPort because connection port keeps on changing
   // when re-initializing Zimbra Desktop App.
   public String getConnectionPort() {
      try {
         return XmlStringUtil.parseXmlFile(getLocalConfigFileLocation(),
         "zimbra_admin_service_port");
      } catch (IOException e) {
         logger.error("Local Config File location is not found.");
         return null;
      }
   }

   private void _setLocalConfigFileLocation(String localConfigFileLocation) {
      _localConfigFileLocation = localConfigFileLocation;
   }
   public String getLocalConfigFileLocation() {
      return _localConfigFileLocation;
   }

}
