/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 VMware, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.framework.core;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import com.zimbra.qa.selenium.framework.util.HarnessException;
import com.zimbra.qa.selenium.framework.util.Log;

import com.zimbra.qa.selenium.framework.util.RacetrackWebservice;

public class Repository {
   private static String DbHostURL = null;
   private static String DbName = "results";
   private static String DbDriver = Constants.MYSQL_DRIVER;
   private static String conString;
   private static String DbUser = "results";
   private static String DbPassword = "r35u175";
   private final static Constants.DB_TYPE type = Constants.DB_TYPE.MYSQL;
   private static Class jdbc;
   private static Connection conn;
   private static String buildId = null;
   private static String username = null;
   private static String product = null;
   private static String description = null;
   private static String hostOs = null;
   private static String serverBuildId = null;
   private static String branch = null;
   private static String branchType =  null;
   private static String buildType = null;
   private static String testType = null;
   private static String language = null;
   private static String testCaseId = null;
   private static String resultId = null;
   private static boolean recordToRacetrack = false;
   private static boolean connectionEstablished = false;
   private static boolean appendToExisting = false;
   private static Logger logger = LogManager.getLogger(Repository.class);

   public static class Constants {
      public static String MYSQL_DRIVER =
            "com.mysql.jdbc.Driver";
      public static final String SQL_DRIVER =
            "com.microsoft.sqlserver.jdbc.SQLServerDriver";
      public static final String ORACLE_DRIVER = 
            "oracle.jdbc.driver.OracleDriver";
      public static enum DB_TYPE {
         SQL, ORACLE, MYSQL
      }
   }
   static String getHostOs() {
      return hostOs;
   }

   static String getLanguage() {
      return language;
   }

   static String getResultId() {
      return resultId;
   }

   /**
    * Sets the TestCaseId
    */
   static void setTestCaseId(String id){
      Repository.testCaseId = id;
   }

   /**
    * Gets the TestCaseId
    */
   static String getTestCaseId() {
      return Repository.testCaseId;
   }

   /**
    * This method gets the type of JdbcDriver
    * and connects to the racetrack
    * @throws com.vmware.qalib.HarnessException
    */
   public void
   connectingToRacetrack(String DbHostURL) throws HarnessException
   {
      Repository.DbHostURL = DbHostURL;
      //Load the Jdbc Driver and then connecting to Racetrack
      Db( Repository.DbHostURL, Repository.type, Repository.DbName,
            Repository.DbUser, Repository.DbPassword);
      Repository.connectionEstablished = connect();
   }

   /**
    * This method begins the testSet recording to racetrack
    * @throws com.vmware.qalib.HarnessException
    */
   public void beginTestSet(String buildNumber,
                            String username,
                            String product,
                            String description,
                            String branch,
                            String buildType,
                            String testType,
                            boolean recordToRacetrack,
                            boolean appendToExisting,
                            String resultId) throws HarnessException
   {
      Repository.buildId = buildNumber;
      Repository.username = username;
      Repository.product = product;
      Repository.description = description;
      Repository.branch = branch;
      Repository.buildType = buildType;
      Repository.testType = testType;
      Repository.recordToRacetrack = recordToRacetrack;
      Repository.appendToExisting = appendToExisting;

      Repository.hostOs = System.getProperty("os.name");
      Repository.language = Locale.getDefault().getDisplayLanguage();
      Repository.branchType = "";
      Repository.serverBuildId = "";

      if (!Repository.recordToRacetrack) {
         return;
      }

      if (!Repository.appendToExisting && Repository.connectionEstablished
            && Repository.recordToRacetrack)
      {
         resultId = RacetrackWebservice.getInstance().testSetBegin( Repository.buildId,
               Repository.username, Repository.product, Repository.description,
               Repository.hostOs, Repository.serverBuildId, Repository.branch,
               Repository.branchType, Repository.buildType, Repository.testType,
               Repository.language);
      }
      Repository.resultId = resultId;
      Log.info("Racetrack URL: http://" +  DbHostURL + "/result.php?id=" +
                           Repository.resultId);
   }

   /**
    * Method  logs the results of the complete testSet
    * whether Complete or Waiting To Triage or Running
    */
   public void endRepository()
   {
      if (!Repository.recordToRacetrack) {
         return;
      }

      try {
         RacetrackWebservice.getInstance().testSetEnd(Repository.resultId);
      } catch (HarnessException e) {
         e.printStackTrace();
      }
   }

   /**
   * Connect to the DB
   * @throws com.vmware.qalib.HarnessException
   */
   public boolean connect() throws HarnessException
   {

      if( Repository.jdbc == null) {
         loadJdbcDriver( DbDriver );
      }
      String connectString = "connect using connection string '" + conString + "' " +
            "and user '" + DbUser + "/" + DbPassword + "'";
      try {
         conn = DriverManager.getConnection(conString, DbUser, DbPassword);
         conn.createStatement();
         Log.info("Connected successfully using connect string: " + connectString);
         return true;
      } catch (SQLException e){
         Log.info("Connection failed using connect string: " + connectString);
         e.printStackTrace();
         return false;
      }
   }

   /**
    * Load the JDBC driver
    * @throws com.vmware.qalib.HarnessException
    */

   @Parameters({"DbDriver"})
   public void
   loadJdbcDriver(@Optional("LibConstants.MYSQL_DRIVER") String DbDriver1)
                  throws HarnessException
   {
      if (!Repository.recordToRacetrack) {
         return;
      }
      try {
         Repository.jdbc = Class.forName(DbDriver);
         Log.info("Successfully loaded jdbc driver: " + DbDriver);
      } catch (ClassNotFoundException e) {
         Log.info("Failed to load jdbc driver: " + DbDriver);
         e.printStackTrace();
      }
   }

   /**
    * @param dbHost The databse host
    * @param type The type of database
    * @param db The name of a database to connect to
    * @param user The db user
    * @param pwd The db password
    */
   public void Db( String DbHostURL,
                   Constants.DB_TYPE type,
                   String db,
                   String user,
                   String pwd) throws HarnessException
   {

      switch(type) {
         case ORACLE:
               conString = "jdbc:oracle:thin:@" + DbHostURL + ":1521:" + db;
               DbDriver = Constants.ORACLE_DRIVER;

               break;
         case SQL:
               conString = "jdbc:sqlserver://" + DbHostURL + ";databaseName=" + db;
               DbDriver = Constants.SQL_DRIVER;

               break;
         default :
               conString = "jdbc:mysql://" + DbHostURL + "/" + db;
               DbDriver = Constants.MYSQL_DRIVER;

               break;
      }
      Log.info(" Successfully got DATABASE Host " + DbHostURL);
   }

   /**
    * TestCaseBegin
    */
   public static String testCaseBegin(String methodName, String packageName, String description)
   {
      if (!Repository.recordToRacetrack) {
         return null;
      }

      try {
         testCaseId = RacetrackWebservice.getInstance().testCaseBegin(Repository.getResultId(),
               methodName, packageName, description, Repository.getHostOs(),
               "", Repository.getLanguage());
         Repository.setTestCaseId(testCaseId);
         logger.info("testCaseId:" + testCaseId);
      } catch (HarnessException e) {
         e.printStackTrace();
      }
      return testCaseId;
   }

   /**
    * TestCaseEnd
    */
   public static void testCaseEnd(String testCaseResult)
   {
      if (!Repository.recordToRacetrack) {
         return;
      }

      try {
        RacetrackWebservice.getInstance().testCaseEnd(Repository.getTestCaseId(),
              testCaseResult);
      } catch (HarnessException e) {
         e.printStackTrace();
      }
   }

   /**
    * TestCaseVerification
    */
   public static void testCaseVerification( String description,
                                            String actualValue,
                                            String expectedValue,
                                            boolean verificationResult)
   {
      if (!Repository.recordToRacetrack) {
         return;
      }

      try {
      if ((actualValue == null) || (expectedValue == null)) {
         if (actualValue == null)
         {
            actualValue = "null";
         }
         if (expectedValue == null)
         {
            expectedValue = "null";
         }
      } else if (actualValue.trim().length() <= 0 || expectedValue.trim().length() <= 0) {
         if (actualValue.trim().length() <= 0) {
            actualValue = "<empty string>";
         }
         if (expectedValue.trim().length() <= 0) {
            expectedValue = "<empty string>";
         }
      }
         RacetrackWebservice.getInstance().testCaseVerification(
         Repository.getTestCaseId(), description, actualValue,
               expectedValue, verificationResult);
      } catch (HarnessException e) {
         e.printStackTrace();
      }
   }

   /**
    * TestCaseCaptureScreenShot
    */
   public static void testCaseCaptureScreenShot(String screenShot)
   {
      if (!Repository.recordToRacetrack) {
         return;
      }

      try {
         RacetrackWebservice.getInstance().testCaseScreenshot(
               Repository.getTestCaseId(), " ", screenShot);
      } catch (HarnessException e) {
         e.printStackTrace();
      }
   }

   /**
    * TestCaseComment
    */
   public static void testCaseComment(String sMessage)
   {
      if (!Repository.recordToRacetrack) {
         return;
      }

      try {
         RacetrackWebservice.getInstance().testCaseComment(
            testCaseId, sMessage);
         Log.info(testCaseId);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
   /**
    * To change the description of the testSet
    */
   public static void testSetDescription( String methodName)
   {
      if (!Repository.recordToRacetrack) {
         return;
      }

      try {
         conn = DriverManager.getConnection(conString, DbUser, DbPassword);
         try {
            String sql = " UPDATE Result SET Description = ? WHERE  Id = ?";
            PreparedStatement prest = conn.prepareStatement(sql);
            prest.setString(1, methodName);
            prest.setString(2, testCaseId);
            prest.executeUpdate();
         } finally {
            if (conn != null) {
               conn.close();
            }
         }
      } catch (Exception e){
         e.printStackTrace();
      }
   }
}



