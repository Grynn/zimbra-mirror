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
 * LogFormatter.java --
 * <p>
 * This class specifies the format for the log to control how log messages 
 * are displayed. This has no timestamp so that it can be used with testware, 
 * which provides timestamp.
 * <p>
 */
package com.zimbra.qa.selenium.framework.util;

import java.util.logging.*;

/**
 *
 * @author nguyenc
 */
public class LogFormatter extends java.util.logging.Formatter {

   /**
    * Set the actual format using LOG_TIME_FORMAT from SviConstants.
    * @param record - a line of log.
    * @return the format.
    */
   @Override
   public synchronized String 
   format( LogRecord record ) 
   {      
      // get a 4 digit thread ID as StringBuilder
      StringBuilder sb = new StringBuilder();
      java.util.Formatter f = new java.util.Formatter(sb);
      f.format("[%4d]", Thread.currentThread().getId());
      
      return " " + record.getLevel() 
             + " : " 
             + sb.toString()                               
             + " [" 
             + record.getSourceClassName() 
             + ":" 
             + record.getSourceMethodName() 
             + "] " 
             + record.getMessage() 
             + "\n";
   }
}
