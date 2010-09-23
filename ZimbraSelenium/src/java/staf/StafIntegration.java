package staf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

import framework.core.ExecuteHarnessMain;
import framework.util.ZimbraSeleniumProperties;

public class StafIntegration implements STAFServiceInterfaceLevel30 {
    static private Logger mLog = Logger.getLogger(StafIntegration.class);

	// STAF Specifics
//	private final String kVersion = "1.1.0";
    private static final int kDeviceInvalidSerialNumber = 4001;
    private String stafServiceName;
    private STAFHandle stafServiceHandle;

    // SERVICE Specifics
    private STAFCommandParser stafParserExecute;
    private STAFCommandParser stafParserQuery;
    private STAFCommandParser stafParserHelp;
    private STAFCommandParser stafParserHalt;

    private String optionExecute = "execute";
    private String argServer = "server";
    private String argRoot = "root";
    private String argJarfile = "jarfile";
    private String argPattern = "pattern";
    private String argGroup = "group";
    private String argLog = "log";
    
    private String optionQuery= "query";
    private String optionHelp = "help";
    private String optionHalt = "halt";
    
    // 
    private boolean serviceIsRunning = false;
    
	public STAFResult acceptRequest(RequestInfo info) {
        mLog.info("StafIntegration: acceptRequest ...");


        File f = new File("/log4j.properties");
        if ( f.exists() ) {
            PropertyConfigurator.configure("/log4j.properties");
        } else {
        	BasicConfigurator.configure();
        }
        	
        // Convert the request to all lower case
        StringTokenizer requestTokenizer = new StringTokenizer(info.request);
        
        
        // Determine what the first argument is
        String request = requestTokenizer.nextToken().toLowerCase();

        // call the appropriate method to handle the command
        if (request.equals(optionExecute.toLowerCase()))
        {
            return handleExecute(info);
        }
        else if (request.equals(optionQuery.toLowerCase()))
        {
            return handleQuery(info);
        }
        else if (request.equals(optionHalt.toLowerCase()))
        {
            return handleHalt(info);
        }
        else if (request.equals(optionHelp.toLowerCase()))
        {
            return handleHelp();
        }
        else
        {
            return new STAFResult(STAFResult.InvalidRequestString, "Unknown (STAF) Request: " + request);
        }
    }
	
	private STAFResult handleExecute(RequestInfo info) {

        mLog.info("STAF: handleExecute ...");

    	// Check whether Trust level is sufficient for this command.
        if (info.trustLevel < 4)
        {   
            
        	return new STAFResult(STAFResult.AccessDenied, 
                "Trust level 4 required for "+ optionExecute +" request.\n" +
                "The requesting machine's trust level: " +  info.trustLevel); 
            
        }    

        
        
        // Make sure the request is valid
        STAFCommandParseResult parsedRequest = stafParserExecute.parse(info.request);
     
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
		
			// Parse Arguments
	        if (parsedRequest.optionTimes(argRoot) != 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Only one "+ argRoot +" can be specified"));	        		        	
	        }
	        if (parsedRequest.optionTimes(argJarfile) != 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Only one "+ argJarfile +" can be specified"));	        		        	
	        }
			if ( parsedRequest.optionTimes(argPattern) != 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Only one "+ argPattern +" can be specified"));	        					
			}
	        if ( parsedRequest.optionTimes(argGroup) < 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Must specify at least one "+ argGroup));
	        }
	        if ( parsedRequest.optionTimes(argLog) != 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Only one "+ argLog +" can be specified"));
	        }
	        
	        ZimbraSeleniumProperties.configPropFile = new File(parsedRequest.optionValue(argRoot) + "/conf/config.properties");
	        try {
				mLog.info("Using "+ ZimbraSeleniumProperties.configPropFile.getCanonicalPath() + ", which exists? "+ ZimbraSeleniumProperties.configPropFile.exists());
			} catch (IOException e1) {
				mLog.error(e1);
			}
	        
	        
	        // Create the execution object
	        ExecuteHarnessMain harness = new ExecuteHarnessMain();
	        
	        harness.jarfilename = parsedRequest.optionValue(argJarfile);
	        harness.classfilter = parsedRequest.optionValue(argPattern);
	        // TODO: Parse the GROUP args
	        harness.groups = Arrays.asList("always", "sanity");
	        harness.testoutputfoldername = parsedRequest.optionValue(argLog);
	        
	        // Execute!
			try {
				
				String response = harness.execute();
		        resultString.append(response);
		        
			} catch (FileNotFoundException e) {
	        	return (new STAFResult(STAFResult.JavaError, e.getMessage()));
			} catch (IOException e) {
	        	return (new STAFResult(STAFResult.JavaError, e.getMessage()));
			}
	        

		} finally {
			serviceIsRunning = false;
		}

		// Return ok code with the parsable return string
		return (new STAFResult(STAFResult.Ok, resultString.toString()));

	}
	
	private STAFResult handleQuery(RequestInfo info) {
		return (new STAFResult(STAFResult.JavaError, "handleQuery: Implement me!"));
	}
	
	private STAFResult handleHalt(RequestInfo info) {
		return (new STAFResult(STAFResult.JavaError, "handleHalt: Implement me!"));
	}
	
	private STAFResult handleHelp() {

        mLog.info("StafTestStaf: handleHelp ...");

    	// TODO: Need to convert the help command into the variables, aEXECUTE, aHELP, etc.
        return new STAFResult(STAFResult.Ok,
         "StafTest Service Help\n\n" + 
         "EXECUTE SERVER <servername|IP address> ROOT <ZimbraSelenium path> JARFILE <path> PATTERN <projects.zcs.tests> [ GROUP <always|sanity|smoke|full> ]*\n\n" +
         "QUERY -- TBD: should return statistics on active jobs \n\n" +
         "HALT <TBD> -- TBD: should stop any executing tests\n\n" +
         "HELP\n\n");
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

        File f = new File("/log4j.properties");
        if ( f.exists() ) {
            PropertyConfigurator.configure("/log4j.properties");
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
        stafParserExecute = new STAFCommandParser();
        stafParserExecute.addOption(optionExecute, 1, STAFCommandParser.VALUENOTALLOWED);
        stafParserExecute.addOption(argServer, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argRoot, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argJarfile, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argPattern, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argGroup, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argLog, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOptionNeed(optionExecute, argServer);
        stafParserExecute.addOptionNeed(argServer, argRoot);
        stafParserExecute.addOptionNeed(argRoot, argJarfile);
        stafParserExecute.addOptionNeed(argJarfile, argPattern);
        stafParserExecute.addOptionNeed(argPattern, argGroup);
        stafParserExecute.addOptionNeed(argGroup, argLog);

        // QUERY parser
        stafParserQuery = new STAFCommandParser();
        stafParserQuery.addOption(optionQuery, 1, STAFCommandParser.VALUENOTALLOWED);
        
        // HELP parser
        stafParserHelp = new STAFCommandParser();
        stafParserHelp.addOption(optionHelp, 1, STAFCommandParser.VALUENOTALLOWED);

        // HALT parser
        stafParserHalt = new STAFCommandParser();
        stafParserHalt.addOption(optionHalt, 1, STAFCommandParser.VALUENOTALLOWED);


        createBundles(info.serviceJar.getName());
        

        // Register Help Data
        registerHelpData(
            kDeviceInvalidSerialNumber,
            "Invalid serial number", 
            "A non-numeric value was specified for serial number");

        
        
		// Now, do the Selenium specific setup ...
        
		// Set up Log4j
        // PropertyConfigurator.configure(mDefaultConfiguratorFile);
		
        // Set up SSL
		// Always accept self-signed SSL certificates.
		// SocketFactories.registerProtocols(true);
        

		
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
