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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.testng.ITestResult;
import org.testng.Reporter;

/**
 * Saves all log messages for tests into a temporary log file. This is meant to
 * be a replacement for org.testng.Reporter that logs everything into memory.
 * 
 * @author twu
 * 
 */
public class TestNGLogFileReporter {
    private static Map<ITestResult, File> fileMap = Collections.synchronizedMap(new HashMap<ITestResult, File>());
    private static Map<ITestResult, Writer> writerMap = Collections.synchronizedMap(new HashMap<ITestResult, Writer>());
    
    /**
     * Start logging to a file for the test
     * 
     * This method can be called multiple times for the same test. Subsequent calls
     * will be no-ops. This allows for multiple listeners to subscribe to the
     * log file.
     * 
     * @param result ITestResult corresponding to the test to log for
     * @throws IOException
     */
    public static void startLogging(ITestResult result) throws IOException {
        if(!fileMap.containsKey(result)) {
            File tempLogFile = File.createTempFile("vum-temp-log", ".log");
            tempLogFile.deleteOnExit();
            fileMap.put(result, tempLogFile);
            writerMap.put(result, new BufferedWriter(new FileWriter(tempLogFile)));
        }
    }
    
    /**
     * Log a message for the current thread
     * 
     * @param s Message to be logged
     * @throws IOException
     */
    public static void log(String s) throws IOException {
        ITestResult currentResult = Reporter.getCurrentTestResult();
        if(fileMap.containsKey(currentResult) && writerMap.containsKey(currentResult)) {
            writerMap.get(currentResult).write(s + "\n");
        }
    }
    
    /**
     * Close the log file at the end of the test
     * 
     * @param result ITestResult for the test that has finished
     * @throws IOException
     */
    public static void endLogging(ITestResult result) throws IOException {
        if(fileMap.containsKey(result) && writerMap.containsKey(result))  {
            writerMap.get(result).close();
            writerMap.remove(result);
        }
    }
    
    /**
     * Get the log file associated with the given test.
     * 
     * @param result ITestResult for the test to get the log file from
     * @return temporary log file
     */
    public static File getOutput(ITestResult result) {
        return fileMap.get(result);
    }
}
