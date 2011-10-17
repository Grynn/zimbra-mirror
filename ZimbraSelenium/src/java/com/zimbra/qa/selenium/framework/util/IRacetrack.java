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

package com.zimbra.qa.selenium.framework.util;

/**
 * This interface is used for all implementation that allows users to interact with 
 * the Racetrack database.
 * 
 * @author Jeffry Hidayat
 */

public interface IRacetrack {
   public static class Constants {
      /**
       * Types of languages
       */
      public static enum LANG_TYPE {
         EN, JP, DE
      }
   }
   /**
    * Get i18n token
    * 
    * Get the value of an i18n token in the requested language.
    * 
    * @param product Product name associated with the token
    * @param token Name of the token
    * @param lang The language required.
    * 
    * @throws HarnessException If there is an error fetching the token
    */
   String
   getI18NToken(String product,
                String token,
                Constants.LANG_TYPE lang)
                throws HarnessException;
   
   /**
    * Log a comment in racetrack for this test.
    * 
    * @param comment a test case comment for this test.
    * 
    * @throws HarnessException thrown if anything goes wrong.
    * @deprecated use {@link IRacetrack#testCaseComment(String, String)} instead
    */
   @Deprecated
   void
   testCaseComment(String comment)
                   throws HarnessException;
   
   /**
    * Add verification info to racetrack for this test.
    * 
    * @param description The description of the verification performed.
    * @param actual      The actual result of the test
    * @param expected    The expected result of the test
    * @param result      The result of the verification true if it passed.
    * 
    * @throws HarnessException thrown if anything goes wrong.
    * @deprecated use {@link IRacetrack#testCaseVerification(String, String, String, String, boolean)}
    */
   @Deprecated
   public void
   testCaseVerification(String description,
                        String actual,
                        String expected,
                        boolean result)
                        throws HarnessException;
   

   
   /**
    * Log a comment in racetrack for this test.
    * 
    * @param comment a test case comment for this test.
    * 
    * @throws HarnessException thrown if anything goes wrong.
    */
   void
   testCaseComment(String testCaseId, 
                   String comment)
                   throws HarnessException;
   
   /**
    * Add verification info to racetrack for this test.
    * 
    * @param description The description of the verification performed.
    * @param actual      The actual result of the test
    * @param expected    The expected result of the test
    * @param result      The result of the verification true if it passed.
    * 
    * @throws HarnessException thrown if anything goes wrong.
    */
   public void
   testCaseVerification(String testCaseId,
                        String description,
                        String actual,
                        String expected,
                        boolean result)
                        throws HarnessException;
}