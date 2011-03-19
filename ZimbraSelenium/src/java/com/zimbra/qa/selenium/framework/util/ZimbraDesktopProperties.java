package com.zimbra.qa.selenium.framework.util;

import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.zimbra.qa.selenium.framework.util.OperatingSystem.OsType;

public class ZimbraDesktopProperties {

   private String _serialNumber = null;
   private String _localConfigFileLocation = null;

   private static ZimbraDesktopProperties _instance = null;

   private static final Logger logger = LogManager.getLogger(ZimbraDesktopProperties.class);

   private ZimbraDesktopProperties() {
      logger.debug("New ZimbraDesktopProperties");
   }

   public static ZimbraDesktopProperties getInstance() {
      if (_instance == null) {
         synchronized (ZimbraDesktopProperties.class) {
            if (_instance == null) {
               _instance = new ZimbraDesktopProperties();
               _instance.init();
            }
         }
      }

      return _instance;
   }

   private final static String [] _possibleFiles = {
      "/opt/zimbra/zdesktop/conf/localconfig.xml",
      "/home/<USER_NAME>/zdesktop/conf/localconfig.xml",
      "C:\\Documents and Settings\\<USER_NAME>\\Local Settings\\Application Data\\Zimbra\\Zimbra Desktop\\conf\\localconfig.xml"
   };

   private void init() {
      OsType osType = OperatingSystem.getOSType();

      for (int i = 0; i < _possibleFiles.length; i++) {
         String currentLoggedInUser = System.getProperty(
               "user.name");
         _possibleFiles[i] = _possibleFiles[i].replace(
               "<USER_NAME>", currentLoggedInUser);
         if (osType == OsType.WINDOWS || osType == OsType.WINDOWS_XP) {
            if (!_possibleFiles[i].contains("C:\\")) {
               continue;
            } else {
               logger.info("currentLoggedInUser: " +
                     currentLoggedInUser);
               
            }
         } else {
            if (_possibleFiles[i].contains("C:\\")) {
               continue;
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
