/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
package com.zimbra.qa.selenium.staf;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParseResult;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;
import com.zimbra.qa.selenium.framework.core.ExecuteHarnessMain;
import com.zimbra.qa.selenium.framework.util.*;
import com.zimbra.qa.selenium.framework.util.ZimbraSeleniumProperties.AppType;


public class StafIntegration implements STAFServiceInterfaceLevel30 {
    static private Logger mLog = Logger.getLogger(StafIntegration.class);

	// STAF Specifics
//	private final String kVersion = "1.1.0";
    private static final int kDeviceInvalidSerialNumber = 4001;
    private String stafServiceName;
    private STAFHandle stafServiceHandle;

    // SERVICE Specifics
    private static class Parsers {
        public static STAFCommandParser stafParserExecute;
        public static STAFCommandParser stafParserQuery;
        public static STAFCommandParser stafParserHelp;
        public static STAFCommandParser stafParserHalt;
    }

    private static class Arguments {
    	
    	public static final String optionExecute = "execute";
    	public static final String argServer = "server";
    	public static final String argConfig = "config";
    	public static final String argConfigHost = "host";
    	public static final String argRoot = "root";
    	public static final String argJarfile = "jarfile";
    	public static final String argPattern = "pattern";
    	public static final String argGroup = "group";
    	public static final String argDesktopURL = "url";
    	public static final String argLog = "log";
    	public static final String argLog4j = "log4j";
     
    	public static final String optionQuery= "query";
        public static final String optionHelp = "help";
        public static final String optionHalt = "halt";

    }
    

    
    private static final String defaultLog4jProperties = "/tmp/log4j.properties";

    // 
    private boolean serviceIsRunning = false;
    
	public STAFResult acceptRequest(RequestInfo info) {
        mLog.info("StafIntegration: acceptRequest ...");


        File f = new File(defaultLog4jProperties);
        if ( f.exists() ) {
            PropertyConfigurator.configure(defaultLog4jProperties);
        } else {
        	BasicConfigurator.configure();
        }
        	
        // Convert the request to all lower case
        StringTokenizer requestTokenizer = new StringTokenizer(info.request);
        
        
        // Determine what the first argument is
        String request = requestTokenizer.nextToken().toLowerCase();

        // call the appropriate method to handle the command
        if (request.equals(Arguments.optionExecute.toLowerCase()))
        {
            return handleExecute(info);
        }
        else if (request.equals(Arguments.optionQuery.toLowerCase()))
        {
            return handleQuery(info);
        }
        else if (request.equals(Arguments.optionHalt.toLowerCase()))
        {
            return handleHalt(info);
        }
        else if (request.equals(Arguments.optionHelp.toLowerCase()))
        {
            return handleHelp();
        }
        else
        {
            return new STAFResult(STAFResult.InvalidRequestString, "Unknown (STAF) Request: " + request);
        }
    }
	
	private STAFResult parseExecute(STAFCommandParseResult request, ExecuteHarnessMain harness) {
		

        // Convert the args to variables
        String valueServer = request.optionValue(Arguments.argServer);
        String valueRoot = request.optionValue(Arguments.argRoot);
        String valueJarfile = request.optionValue(Arguments.argJarfile);
        String valuePattern = request.optionValue(Arguments.argPattern);
        String valueConfig = request.optionValue(Arguments.argConfig);
        String valueConfigHost = request.optionValue(Arguments.argConfigHost);
        String valueURL = request.optionValue(Arguments.argDesktopURL);
        String valueLog = request.optionValue(Arguments.argLog);
		        

        mLog.info("valueServer="+ valueServer);
        mLog.info("valueRoot="+ valueRoot);
        mLog.info("valueJarfile="+ valueJarfile);
        mLog.info("valuePattern="+ valuePattern);
        mLog.info("valueConfig="+ valueConfig);
        mLog.info("valueConfigHost="+ valueConfigHost);
        mLog.info("valueURL="+ valueURL);
        mLog.info("valueLog="+ valueLog);
        
        // Since multiple GROUP arguments can be specified, process each one
        ArrayList<String> valueGroup = new ArrayList<String>();
        for (int i = 1; i <= request.optionTimes(Arguments.argGroup); i++) {
        	String g = request.optionValue(Arguments.argGroup, i);
        	valueGroup.add(g);
            mLog.info("valueGroup="+ g);

        }
        if ( valueGroup.isEmpty() ) {
        	// If no groups were specified, default to sanity
        	valueGroup = new ArrayList<String>(Arrays.asList("always", "sanity"));
            mLog.info("valueGroup=always,sanity");
        }
        
        //// Configure the harness based on the arguments
        //
        
        // Set the base folder name
        ZimbraSeleniumProperties.setBaseDirectory(valueRoot);
        
        
        if ( (valueConfig != null) && (valueConfig.trim().length() > 0) ) { 
        	
        	////
        	// Grab the specified config.properties from the remote client
        	////
        	
        	// Determine which remote host to grab the config.properties from
        	String host = valueServer;
        	if ( (valueConfigHost != null) && (valueConfigHost.trim().length() > 0) ) {
        		host = valueConfigHost;
        	}
        	
            // Set the config.properties values
            try {
            	

	        	// Get the remote file and initialize the properties
	        	StafDevProperties configProperties = new StafDevProperties();
	        	
	        	// Get the remote file contents
	        	configProperties.load(host, valueConfig, valueLog);
	        	
				// Save the temp file in the log folder for the records
				String filename = configProperties.save(valueLog);
				
				// Tell the harness to load the temp file
		        ZimbraSeleniumProperties.setConfigProperties(filename);
	        
	        
            } catch (HarnessException e) {
            	return (new STAFResult(STAFResult.JavaError, getStackTrace(e)));
    		} catch (IOException e) {
            	return (new STAFResult(STAFResult.JavaError, getStackTrace(e)));
    		}

        	
        } else {
        	
        	////
        	// Use the 'local' config.properties
        	////
        	
            // Set the config.properties values
            try {
            	
            	// Load the original properties
    			StafProperties configProperties = new StafProperties(valueRoot + "/conf/config.properties");
    			
    			configProperties.setProperty("server.host", valueServer);
    			configProperties.setProperty("adminName", "globaladmin@" + valueServer);

    			configProperties.setProperty("seleniumMode", "Local");
    			configProperties.setProperty("serverName", "localhost");
    			configProperties.setProperty("serverPort", "4444");

    			if ( valueURL != null )
    				configProperties.setProperty("desktop.buildUrl", valueURL);


    			// Save the temp file in the log folder for the records
    			String filename = configProperties.save(valueLog);
    			
    			// Tell the harness to load the temp file
    	        ZimbraSeleniumProperties.setConfigProperties(filename);
    	        
    		} catch (FileNotFoundException e) {
            	return (new STAFResult(STAFResult.JavaError, getStackTrace(e)));
    		} catch (IOException e) {
            	return (new STAFResult(STAFResult.JavaError, getStackTrace(e)));
    		}

            
        }

        // Set the app type on the properties
		// Must happen before setTestOutputFolderName()
        for (AppType t : AppType.values()) {
        	// Look for ".type." (e.g. ".ajax.") in the pattern
        	if ( valuePattern.contains(t.toString().toLowerCase()) ) {
        		ZimbraSeleniumProperties.setAppType(t);
            	break;
        	}
        }

        harness.setTestOutputFolderName(valueLog);

		// If specified, load the log4j property file first
		// so that we start logging immediately
        if (request.optionTimes(Arguments.argLog4j) > 0 ) {
        	
        	// Even if a log4j.properties file was specified, check 
        	// for a local log4j file, since it is the first priority.
        	// The user should delete the local file, if that's not
        	// what is desired
        	//
            File f = new File(defaultLog4jProperties);
            if ( f.exists() ) {
            	
            	// Priority 1: a local log4j.properties file
                PropertyConfigurator.configure(defaultLog4jProperties);
                mLog.warn("Using "+ defaultLog4jProperties +".  Delete the file to use "+ Arguments.argLog4j);

            } else {
            	
            	// Priority 2: a specified log4j.properties file
            	PropertyConfigurator.configure(request.optionValue(Arguments.argLog4j));

            }
        }


		// Set the harness parameters
        harness.jarfilename = valueJarfile;
        harness.classfilter = valuePattern;
        harness.groups = valueGroup;


		// Done!
		return (new STAFResult(STAFResult.Ok));

	}
	
	private STAFResult handleExecute(RequestInfo info) {

        mLog.info("STAF: handleExecute ...");

    	// Check whether Trust level is sufficient for this command.
        if (info.trustLevel < 4)
        {   
            
        	return new STAFResult(STAFResult.AccessDenied, 
                "Trust level 4 required for "+ Arguments.optionExecute +" request.\n" +
                "The requesting machine's trust level: " +  info.trustLevel); 
            
        }    

        
        
        
        // Make sure the request is valid
        STAFCommandParseResult parsedRequest = Parsers.stafParserExecute.parse(info.request);
        if (parsedRequest.rc != STAFResult.Ok)
        {
            return new STAFResult(STAFResult.InvalidRequestString, parsedRequest.errorBuffer);   
        }
        

        if (serviceIsRunning) {
        	return (new STAFResult(STAFResult.Ok, "already running"));
        }
        
        StringBuilder resultString = new StringBuilder();
        
		try {
			
			serviceIsRunning = true;
			
					
			// Reset all static references
			this.reset();
			
	        // Create the execution object
	        ExecuteHarnessMain harness = new ExecuteHarnessMain();
	        

			// Parse Arguments
			STAFResult parseResult = parseExecute(parsedRequest, harness);
			if (parseResult.rc != STAFResult.Ok) {
				return (parseResult);
			}	        
	        
	        // Execute!
			try {
				
				String response = harness.execute();
		        resultString.append(response);
		        
			} catch (FileNotFoundException e) {
	        	return (new STAFResult(STAFResult.JavaError, getStackTrace(e)));
			} catch (IOException e) {
	        	return (new STAFResult(STAFResult.JavaError, getStackTrace(e)));
			}
	        

		} catch (HarnessException e) {
			return (new STAFResult(STAFResult.JavaError, getStackTrace(e)));
		} finally {
			serviceIsRunning = false;
		}

		// Return ok code with the parsable return string
		return (new STAFResult(STAFResult.Ok, resultString.toString()));

	}
	
	/**
	 *  Convert a stack trace to a string
	 * @param t
	 * @return
	 */
	private String getStackTrace(Throwable t) {
		String s = t.getMessage();
		try {

			Writer writer = null;
			PrintWriter printer = null;
			try {
				writer = new StringWriter();
				printer = new PrintWriter(writer);
				t.printStackTrace(printer);
				s = writer.toString();
			} finally {
				if ( printer != null ) {
					printer.close();
					printer = null;
				}
				if ( writer != null ) {
					writer.close();
					writer = null;
				}
			}
			
		} catch (IOException e) {
			mLog.warn("IOException while closing writer ", e);
		}
		return (s);
	}
	

	private STAFResult handleQuery(RequestInfo info) {

        mLog.info("STAF: handleExecute ...");

    	// Check whether Trust level is sufficient for this command.
        if (info.trustLevel < 2)
        {   
            
        	return new STAFResult(STAFResult.AccessDenied, 
                "Trust level 2 required for "+ Arguments.optionQuery +" request.\n" +
                "The requesting machine's trust level: " +  info.trustLevel); 
            
        }    

        String status = "Not running";
        
        if ( ExecuteHarnessMain.currentResultListener != null ) {
        	status = ExecuteHarnessMain.currentResultListener.getResults();
        }

        return (new STAFResult(STAFResult.Ok, status));
        
	}
	
	private STAFResult handleHalt(RequestInfo info) {
		return (new STAFResult(STAFResult.JavaError, "handleHalt: Implement me!"));
	}
	
	private STAFResult handleHelp() {


        mLog.info("StafTestStaf: handleHelp ...");

    	// TODO: Need to convert the help command into the variables, aEXECUTE, aHELP, etc.
        return new STAFResult(STAFResult.Ok,
         "StafTest Service Help\n\n" + 
         "EXECUTE SERVER <servername|IP address> ROOT <ZimbraSelenium path> JARFILE <path> PATTERN <projects.ajax.tests> [ GROUP <always|sanity|smoke|functional> ]* [ CONFIG <path> [ HOST <host> ] ] [ URL <desktop installer folder> ] [ LOG <folder> ] [ LOG4J <properties file> ]\n\n" +
         "QUERY -- TBD: should return statistics on active jobs \n\n" +
         "HALT <TBD> -- TBD: should stop any executing tests\n\n" +
         "HELP\n\n");
	}

	/**
	 * Reset any static refrences between executions
	 **/
	protected void reset() {
		ZimbraAdminAccount.reset();
		ZimbraAccount.reset();
	}
	
	private void createBundles(String jarfilename) {
		List<String> names = Arrays.asList("AjxMsg", "I18nMsg", "ZaMsg", "ZbMsg", "ZhMsg", "ZmMsg", "ZsMsg", "ZMsg");
		Locale locale = Locale.ENGLISH;
		for (String name : names) {
			try {
				ResourceBundle rb = ResourceBundle.getBundle(name, locale, this.getClass().getClassLoader());
				if ( rb == null ) {
					mLog.error("Unable to load resource bundle: "+ name);
					continue;
				}
				mLog.info("Loaded resource bundle: "+ name);
			} catch (MissingResourceException e) {
				mLog.error("Unable to load resource bundle: "+ name, e);
			}
		}
//		try {
//			ResourceBundle rb1 = ResourceBundle.getBundle("ZaMsg", Locale.ENGLISH, this.getClass().getClassLoader());
//			for (Enumeration<String> e = rb1.getKeys(); e.hasMoreElements(); ) {
//				mLog.info("key: "+ e.nextElement());
//			}
//		} catch (MissingResourceException e) {
//			mLog.error("unable to load resource bundle", e);
//		}
		
	}

	public STAFResult init(InitInfo info) {
        mLog.info("StafIntegration: init ...");

        File f = new File(defaultLog4jProperties);
        if ( f.exists() ) {
            PropertyConfigurator.configure(defaultLog4jProperties);
        } else {
        	BasicConfigurator.configure();
        }

        mLog.info("serviceJar.getName(): " + info.serviceJar.getName());
        
        // STAF specific stuff ...
		
		try
        {
            stafServiceName = info.name;
            stafServiceHandle = new STAFHandle("STAF/SERVICE/" + info.name);
        }
        catch (STAFException e)
        {
            return (new STAFResult(STAFResult.STAFRegistrationError));
        }
        
        
        // EXECUTE parser
        Parsers.stafParserExecute = new STAFCommandParser();
        Parsers.stafParserExecute.addOption(Arguments.optionExecute, 1, STAFCommandParser.VALUENOTALLOWED);
        Parsers.stafParserExecute.addOption(Arguments.argServer, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argRoot, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argJarfile, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argPattern, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argGroup, 0, STAFCommandParser.VALUEREQUIRED); // Can be specified infinite amount of times
        Parsers.stafParserExecute.addOption(Arguments.argConfig, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argConfigHost, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argDesktopURL, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argLog, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOption(Arguments.argLog4j, 1, STAFCommandParser.VALUEREQUIRED);
        Parsers.stafParserExecute.addOptionNeed(Arguments.optionExecute, Arguments.argRoot +" "+ Arguments.argJarfile +" "+ Arguments.argPattern +" "+ Arguments.argGroup);

        // QUERY parser
        Parsers.stafParserQuery = new STAFCommandParser();
        Parsers.stafParserQuery.addOption(Arguments.optionQuery, 1, STAFCommandParser.VALUENOTALLOWED);
        
        // HELP parser
        Parsers.stafParserHelp = new STAFCommandParser();
        Parsers.stafParserHelp.addOption(Arguments.optionHelp, 1, STAFCommandParser.VALUENOTALLOWED);

        // HALT parser
        Parsers.stafParserHalt = new STAFCommandParser();
        Parsers.stafParserHalt.addOption(Arguments.optionHalt, 1, STAFCommandParser.VALUENOTALLOWED);


        createBundles(info.serviceJar.getName());
        

        // Register Help Data
        registerHelpData(
            kDeviceInvalidSerialNumber,
            "Invalid serial number", 
            "A non-numeric value was specified for serial number");

        
        
		// Now, do the Selenium specific setup ...
        BasicConfigurator.configure();
        
		
		// Now, the service is ready ...
		mLog.info("STAF Selenium: Ready ...");
        
        
        return (new STAFResult(STAFResult.Ok));
	}

	public STAFResult term() {
        mLog.info("StafIntegration: term ...");


        try
        {
            // Un-register Help Data
            unregisterHelpData(kDeviceInvalidSerialNumber);

            // Un-register the service handle
            stafServiceHandle.unRegister();
            
        }
        catch (STAFException ex)
        {            
            return (new STAFResult(STAFResult.STAFRegistrationError));
        }
        
        return (new STAFResult(STAFResult.Ok));


	}

    // Register error codes for the STAX Service with the HELP service
    private void registerHelpData(int errorNumber, String info, String description)
    {
        stafServiceHandle.submit2("local", "HELP",
                         "REGISTER SERVICE " + stafServiceName +
                         " ERROR " + errorNumber +
                         " INFO " + STAFUtil.wrapData(info) +
                         " DESCRIPTION " + STAFUtil.wrapData(description));
    }

    // Un-register error codes for the STAX Service with the HELP service
    private void unregisterHelpData(int errorNumber)
    {
    	stafServiceHandle.submit2("local", "HELP",
                         "UNREGISTER SERVICE " + stafServiceName +
                         " ERROR " + errorNumber);
    }

}
