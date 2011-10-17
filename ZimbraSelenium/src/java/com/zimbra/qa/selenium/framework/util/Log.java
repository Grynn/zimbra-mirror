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
/**
 * Log.java --
 * <p>
 * JVM will look for a log config property file, which is used to setup
 * log handlers (console, files, etc..), log levels, and log format..
 * It will look first at the command line
 *
 * -Djava.util.logging.config.file=myConfigFile
 *
 * if not found then at its default location
 *
 * JDK_HOME/jre/lib/logging.properties
 *
 * A very short config file that outputs only to the console would contain
 * the following:
 *
 * handlers=java.util.logging.ConsoleHandler
 * java.util.logging.ConsoleHandler.level=FINEST
 * java.util.logging.ConsoleHandler.formatter=com.vmware.qalib.LogFormatter
 *
 * Debug usage:
 * - Debug messages     -> Level.FINE
 * - Exception trace    -> Level.FINER
 * - Tracing messages   -> Level.FINEST
 * <p>
 *
 * @author nguyenc
 */
package com.zimbra.qa.selenium.framework.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Log {

   private static IRacetrack racetrack;
   private static boolean racetrackWarningLogged;
   private static ThreadLocal<String> threadTestCaseId = new InheritableThreadLocal<String>();
   private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd HH:mm:ss";

   // Flag indicating whether logging should go to the TestNG logger or not.
   private static boolean logToTestNg = false;


   /**
    * Initialize Racetrack functionality
    *
    * @param url The URL of the Racetrack web service
    * @param id  The test case id of the test run.
    * @deprecated use {@link Log#initRacetrack()} instead.
    */
   @Deprecated
   public static synchronized void
   initializeRacetrack(String url,
                       String id)
   {
      racetrack = RacetrackWebservice.getInstance(url, id);
   }

   /**
    * Initialize Racetrack functionality
    *
    */
   public static synchronized void
   initRacetrack()
   {
      racetrack = RacetrackWebservice.getInstance();
   }

   /**
    * Initialize Racetrack functionality
    *
    * @param testCaseId
    */
   public static synchronized void
   initRacetrack(String testCaseId)
   {
      threadTestCaseId.set(testCaseId);
      initRacetrack();
   }

   /**
    * Initialize TestNG logging
    *
    */
   public static synchronized void
   initializeTestNG()
   {
      logToTestNg = true;
   }

   /**
    * Print a label to the log
    *
    * * Ex: INFO : [   1] [:::] ##################
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] #  Test Cleanup  #
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] ##################
    *
    * @param s A label
    * @param symbol A symbol to use as border
    */
   public static synchronized void
   label( String s,
          String symbol )
   {
      label(s, symbol, Integer.MIN_VALUE);
   }

   /**
    * Print a label to the log
    *
    * * Ex: INFO : [   1] [:::] ##################
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] #  Test Cleanup  #
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] ##################
    *
    * @param s A label
    * @param width Max width
    */
   public static synchronized void
   label( String s,
          int width )
   {

      label(s, null, width);
   }

   /**
    * Print a label to the log
    *
    * * Ex: INFO : [   1] [:::] ##################
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] #  Test Cleanup  #
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] ##################
    *
    * @param s A label
    */
   public static synchronized void
   label( String s )
   {
      label(s, null, Integer.MIN_VALUE);
   }

   /**
    * Print a label to the log
    *
    * * Ex: INFO : [   1] [:::] ##################
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] #  Test Cleanup  #
    *       INFO : [   1] [:::] #                #
    *       INFO : [   1] [:::] ##################
    *
    * @param s A label
    * @param symbol A symbol to use as border
    * @param width Max width
    */
   public static synchronized void
   label( String s,
          String symbol,
          int width)
   {
      final int labelPadding = 6;
      final int spacePadding = 4;
      final int defaultWidth = 80;
      final String _symbol = null == symbol ? "#" : symbol;
      final int _width = Integer.MIN_VALUE == width ? defaultWidth : width;
      final String colon = ":";

      List<String> lines = splitStringByLength(s, _width);
      StringBuffer symbols = new StringBuffer();
      int i = 0;
      int labelLength = s.length() > _width ? _width : s.length();
      while(i++ < labelLength + labelPadding){
         symbols.append(_symbol);
      }
      StringBuffer spaces = new StringBuffer(_symbol);
      i = 0;
      while(i++ < labelLength + spacePadding){
         spaces.append(" ");
      }
      spaces.append(_symbol);
      logger.logp(Level.INFO, colon, colon, symbols.toString());
      logger.logp(Level.INFO, colon, colon, spaces.toString());
      for(String line : lines) {
         if(line.length() < labelLength){
            int j = line.length();
            while (j++ < labelLength){
               line += " ";
            }
         }
         logger.logp(Level.INFO, colon, colon, _symbol + "  " + line + "  "
                     + _symbol);
      }
      logger.logp(Level.INFO, colon, colon, spaces.toString());
      logger.logp(Level.INFO, colon, colon, symbols.toString());
   }

   /**
    * Split a string using a max length
    *
    * @param s A string
    * @param len A max length
    *
    * @return List of strings
    */
   public static List<String>
   splitStringByLength(String s,
                       int len)
    {
       List<String> r = new ArrayList<String>();
       int start = 0;
       int remain = s.length();
       while(remain > len){
         String t = s.substring(start, start + len);
         r.add(t);
         remain -= len;
         start += len;
       }
       r.add(s.substring(start, s.length()));
       return r;
    }

   /**
    * Print error messages to the log
    *
    * @param message
    */
   public static synchronized void error( String message )
   {
      p(Level.SEVERE, message);
   }

   /**
    * Print warning messages to the log
    *
    * @param message
    */
   public static synchronized void warning( String message )
   {
      p(Level.WARNING, message);
   }

   /**
    * Print info messages to the log
    *
    * @param message
    */
   public static synchronized void info( String message )
   {
      p(Level.INFO, message);

   }

   /**
    * Print config messages to the log
    *
    * @param message
    */
   public static synchronized void config( String message )
   {
      p(Level.CONFIG, message);
   }

   /**
    * Print debug messages to the log at FINE level
    *
    * @param message
    */
   public static synchronized void fineDebug( String message )
   {
      p(Level.FINE, message);
   }

   /**
    * Print debug messages to the log at FINER level
    *
    * @param message
    */
   public static synchronized void finerDebug( String message )
   {
      p(Level.FINER, message);
   }

   /**
    * Print debug messages to the log at FINEST level
    *
    * @param message
    */
   public static synchronized void finestDebug( String message )
   {
      p(Level.FINEST, message);
   }

   /**
    * Print exceptions to the log at SEVERE level
    *
    * @param e
    */
   public static synchronized void exception( Exception e )
   {
      px(Level.SEVERE, e.getMessage(), e);
   }
   /**
    * Get the logger and set log level to ALL, which means listening to
    * all log messages
    */
   private static final Logger logger =
      Logger.getLogger(Log.class.toString());

   static
   {
      logger.setLevel(Level.ALL);
      System.setErr(System.out);

      if(!System.getProperties().containsKey("java.util.logging.config.file")){
         String msg = "No log config file is specified. Setting log level to "
            + "FINEST and log formatter to com.vmware.qalib.LogFormatter";
         System.out.println(msg);
         for(Handler h : logger.getHandlers()) {
            if(h instanceof ConsoleHandler){
               h.setLevel(Level.FINEST);
               h.setFormatter(new LogFormatter());
            }
         }
      }

      // set level of apache httpclient logs to 'error'
      System.setProperty("org.apache.commons.logging.Log",
         "org.apache.commons.logging.impl.SimpleLog");
      System.setProperty("org.apache.commons.logging.simplelog.log.org.apache."
         + "commons.httpclient", "error");
   }

   /**
    * Common wrapper for logp()
    * @param level - Log levels
    * @param message - Log message
    * @param thrown - Caught exception
    */
   private static void pc( Level level,
                   String message,
                   Throwable thrown )
   {

      /**
       * Figure out the class and method that call logger
       */
      Throwable callStack = new Throwable();
      StackTraceElement[] locations = callStack.getStackTrace();
      String className = "Unknown";
      String methodName = "Unknown";
      if (null != locations && locations.length >= 3) {
         StackTraceElement caller = locations[3];
         className = caller.getClassName();
         methodName = caller.getMethodName();
      }

      if (null == thrown) {
         logger.logp(level, className, methodName, message);
      } else {
         logger.logp(level, className, methodName, message, thrown);
      }

      if (logToTestNg) {
         logToTestNg(level.toString(), className, methodName, message);
      }
   }

   /**
    * Log message to TestNG reporter
    *
    * Formats the log information and prints it out.
    *
    * @param level
    *                  one of the "java.util.logging.Level"s
    * @param className
    *                  name of the class printing the message.
    * @param methodName
    *                  name of the method printing the message.
    * @param message
    *                  message to be printed.
    */
   private static void logToTestNg(String level,
                                   String className,
                                   String methodName,
                                   String message)
   {
      SimpleDateFormat df = (SimpleDateFormat) DateFormat.getDateTimeInstance();
      df.applyPattern(TIMESTAMP_FORMAT);
      String msg = "["
                   + df.format(Calendar.getInstance().getTime())
                   + " "
                   + level.toUpperCase()
                   + " "
                   + className
                   + "."
                   + methodName
                   + " "
                   + Thread.currentThread().getId()
                   +"] "
                   + message;
      try {
          TestNGLogFileReporter.log(msg);
      } catch (IOException e) {
            logger.logp(Level.SEVERE, "Log", "logToTestNg",
                    "Error writing to temporary testng logs: " + e.getMessage());
      }
   }

   /**
    * Common wrapper for normal logs
    * @param level - Log level
    * @param message - Log message
    */
   private static void p( Level level,
                  String message )
   {
      pc(level, message, null);
   }

   /**
    * Common wrapper that can take exceptions and display their attributes
    * @param level - Log level
    * @param message - Log message
    * @param thrown - exception thrown
    */
   private static void px( Level level,
                   String message,
                   Throwable thrown )
   {

      /**
       * Print the message and brief description of the exception.
       */
      pc(level, message, thrown);

      /**
       * Redirect printStackTrace to string.
       */
      Writer sw = new StringWriter();
      PrintWriter pw = new PrintWriter(sw);
      thrown.printStackTrace(pw);

      /**
       * Print stack trace at SEVERE level.
       */
      pc(Level.SEVERE, sw.toString(), null);
   }

   /**
    * Log a comment in Racetrack
    *
    * @param msg a string to be logged in racetrack.
    * @deprecated use {@link Log#racetrackComment(String)} instead.
    */
   @Deprecated
   public static void
   commentToRacetrack(String msg)
   {
      if (null != racetrack) {
         try {
            racetrack.testCaseComment(msg);
         } catch (HarnessException e) {
            Log.warning("Failed to log to racetrack.");
            Log.warning(e.getMessage());
         }
      } else {
         logRacetrackUnavailableWarning();
      }
   }

   /**
    * Log a comment in Racetrack
    *
    * @param testCaseId
    * @param msg a string to be logged in racetrack.
    */
   public static void
   racetrackComment(String testCaseId, String msg)
   {
      if (null != racetrack) {
         try {
            racetrack.testCaseComment(testCaseId, msg);
         } catch (HarnessException e) {
            Log.warning("Failed to log to racetrack.");
            Log.warning(e.getMessage());
         }
      } else {
         logRacetrackUnavailableWarning();
      }
   }

   /**
    * Log a racetrack comment to this thread's test result.
    *
    * @param msg a message string
    */
   public static void
   racetrackComment(String msg) {
       racetrackComment(threadTestCaseId.get(), msg);
   }
   /**
    * Log a verification in Racetrack
    *
    * @param description a string description.
    * @param expected A string representing the expected result of the verification
    * @param actual A string representing the actual result of the verification
    * @param result "true" if the verification passed.
    */
   @Deprecated
   public static void
   verificationToRacetrack(String description,
                           String expected,
                           String actual,
                           boolean result)
   {
      if (null != racetrack) {
         try {
            racetrack.testCaseVerification(description,
                                           actual,
                                           expected,
                                           result);
         } catch (HarnessException e) {
            Log.warning("Failed to log to racetrack.");
            Log.warning(e.getMessage());
         }
      } else {
         logRacetrackUnavailableWarning();
      }
   }



   /**
    * Log a verification in Racetrack
    *
    * @param description a string description.
    * @param expected A string representing the expected result of the verification
    * @param actual A string representing the actual result of the verification
    * @param result "true" if the verification passed.
    */
   public static void
   racetrackVerification(String testCaseId,
                         String description,
                         String expected,
                         String actual,
                         boolean result)
   {
      if (null != racetrack) {
         try {
            racetrack.testCaseVerification(testCaseId,
                                           description,
                                           actual,
                                           expected,
                                           result);
         } catch (HarnessException e) {
            Log.warning("Failed to log to racetrack.");
            Log.warning(e.getMessage());
         }
      } else {
         logRacetrackUnavailableWarning();
      }
   }

   /**
    * Log a verification in Racetrack
    *
    * @param description a string description.
    * @param expected A string representing the expected result of the verification
    * @param actual A string representing the actual result of the verification
    * @param result "true" if the verification passed.
    */
   public static void
   racetrackVerification(String description,
                         String expected,
                         String actual,
                         boolean result)
   {
       racetrackVerification(threadTestCaseId.get(), description, expected, actual, result);
   }

   /**
    * Log a warning to console stating that racetrack reporting is unavailable
    *
    */
   private static synchronized void
   logRacetrackUnavailableWarning()
   {
      if(!racetrackWarningLogged) {
         Log.warning("Racetrack is not initialized. Racetrack logging "+
                     "will fail.");
         racetrackWarningLogged = true;
      }
   }
}
