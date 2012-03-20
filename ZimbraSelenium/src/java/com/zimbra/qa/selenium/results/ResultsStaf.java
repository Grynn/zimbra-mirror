package com.zimbra.qa.selenium.results;

import java.io.*;
import java.util.StringTokenizer;

import org.apache.log4j.*;
import org.dom4j.DocumentException;

import com.ibm.staf.*;
import com.ibm.staf.service.*;


public class ResultsStaf implements STAFServiceInterfaceLevel30 {
    static private Logger mLog = Logger.getLogger(ResultsStaf.class);

	// STAF Specifics
    private static final int kDeviceInvalidSerialNumber = 4001;
    private String stafServiceName;
    private STAFHandle stafServiceHandle;

    // SERVICE Specifics
    private STAFCommandParser stafParserExecute;
    private STAFCommandParser stafParserHelp;

    // REPORT Options
    private final String optionReport = "report";
    private final String argRoot = "root";    
    private final String argLog4j = "log4j";

    // HELP Options
    private final String optionHelp = "help";
    
    
    private static final String defaultLog4jProperties = "/tmp/log4j.properties";
    private String valueRoot = null;

    
	public STAFResult acceptRequest(RequestInfo info) {
        mLog.info("StafIntegration: acceptRequest ...");

        try {
        	
            File f = new File(defaultLog4jProperties);
            if ( f.exists() ) {
                PropertyConfigurator.configure(defaultLog4jProperties);
            } else {
            	BasicConfigurator.resetConfiguration();
            }
            	
            // Convert the request to all lower case
            StringTokenizer requestTokenizer = new StringTokenizer(info.request);
            
            
            // Determine what the first argument is
            String request = requestTokenizer.nextToken().toLowerCase();

            // call the appropriate method to handle the command
            if (request.equals(optionReport.toLowerCase()))
            {
                return handleExecute(info);
            }
            else if (request.equals(optionHelp.toLowerCase()))
            {
                return handleHelp();
            }
            else
            {
                return new STAFResult(STAFResult.InvalidRequestString, "Unknown (STAF) Request: " + request);
            }

        } catch (Throwable t) {
        	
        	// If any exception is thrown, log it to the output/response
        	
        	PrintWriter pr = null;
        	try {
        		
            	StringWriter sr = new StringWriter();
            	pr = new PrintWriter(sr);
            	t.printStackTrace(pr);

            	StringBuilder sb = new StringBuilder();
            	if ( t.getMessage() != null ) {
            		sb.append(t.getMessage()).append("\n");
            	}
            	sb.append(sr.toString());

            	return (new STAFResult(STAFResult.JavaError, sb.toString()));

        	} finally {
        		if ( pr != null ) {
        			pr.close();
        			pr = null;
        		}
        	}

        }

    }
	
	private STAFResult parseExecute(STAFCommandParseResult request) {
		
		// If specified, load the log4j property file first
		// so that we start logging immediately
		if (request.optionTimes(argLog4j) > 0 ) {
        	PropertyConfigurator.configure(request.optionValue(argLog4j));
		}
		        
        // Convert the args to variables
		valueRoot = request.optionValue(argRoot);
        mLog.info("valueRoot="+ valueRoot);
        

		// Done!
		return (new STAFResult(STAFResult.Ok));

	}
	
	private STAFResult handleExecute(RequestInfo info) throws IOException, DocumentException {

        mLog.info("STAF: handleExecute ...");

    	// Check whether Trust level is sufficient for this command.
        if (info.trustLevel < 4)
        {   
            
        	return new STAFResult(STAFResult.AccessDenied, 
                "Trust level 4 required for "+ optionReport +" request.\n" +
                "The requesting machine's trust level: " +  info.trustLevel); 
            
        }    

        
        
        
        // Make sure the request is valid
        STAFCommandParseResult parsedRequest = stafParserExecute.parse(info.request);
        if (parsedRequest.rc != STAFResult.Ok)
        {
            return new STAFResult(STAFResult.InvalidRequestString, parsedRequest.errorBuffer);   
        }
        
		STAFResult parseResult = parseExecute(parsedRequest);
		if (parseResult.rc != STAFResult.Ok) {
			return (parseResult);
		}	        

        ResultsCore core = new ResultsCore();
        core.execute(new File(valueRoot));
		
		// Return ok code with the parsable return string
		return (new STAFResult(STAFResult.Ok, core.getResultString()));

	}
	
	

	private STAFResult handleHelp() {


        mLog.info("StafTestStaf: handleHelp ...");

    	// TODO: Need to convert the help command into the variables, aEXECUTE, aHELP, etc.
        return new STAFResult(STAFResult.Ok,
         "ResultStaf Service Help\n\n" + 
         "REPORT ROOT <path to results> [ LOG4J <log4j.properties> ]\n" +
         "HELP\n\n");
        
	}

	

	public STAFResult init(InitInfo info) {
        mLog.info("StafIntegration: init ...");

        File f = new File(defaultLog4jProperties);
        if ( f.exists() ) {
            PropertyConfigurator.configure(defaultLog4jProperties);
        } else {
        	BasicConfigurator.resetConfiguration();
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
        stafParserExecute = new STAFCommandParser();
        stafParserExecute.addOption(optionReport, 1, STAFCommandParser.VALUENOTALLOWED);
        stafParserExecute.addOption(argRoot, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argLog4j, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOptionNeed(optionReport, argRoot);

        
        // HELP parser
        stafParserHelp = new STAFCommandParser();
        stafParserHelp.addOption(optionHelp, 1, STAFCommandParser.VALUENOTALLOWED);

        

        // Register Help Data
        registerHelpData(
            kDeviceInvalidSerialNumber,
            "Invalid serial number", 
            "A non-numeric value was specified for serial number");

        
        
		
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
