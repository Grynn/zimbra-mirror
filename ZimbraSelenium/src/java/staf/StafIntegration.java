package staf;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
    private String argDirectory = "seleniumroot";
    private String argSuite = "suite";
    private String argClient = "client";
    
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
        	
            return new STAFResult(STAFResult.InvalidRequestString,
                                  parsedRequest.errorBuffer);
            
        }

        // Initialize the return result
        StringBuffer resultString = new StringBuffer();
        

        if (serviceIsRunning) {
        	return (new STAFResult(STAFResult.Ok, "already running"));
        }
        
		try {
			
			serviceIsRunning = true;
		
			// Parse Arguments
	        if (parsedRequest.optionTimes(argDirectory) != 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Only one "+ argDirectory +" can be specified"));	        		        	
	        }
			if ( parsedRequest.optionTimes(argClient) != 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Must specify one "+ argClient +", either zcs or html"));	        					
			}
	        if ( parsedRequest.optionTimes(argSuite) != 1 ) {
	        	return (new STAFResult(STAFResult.JavaError, "Only one "+ argSuite +" can be specified"));	        	
	        }
	        
	        // Set ZimbraSelenium root
	        String root = parsedRequest.optionValue(argDirectory);
	        projects.html.bin.ExecuteTests.WorkingDirectory = root;
	        projects.zcs.bin.ExecuteTests.WorkingDirectory = root;
	        
	        // Which client, zcs or html or other?
	        String client = parsedRequest.optionValue(argClient).toLowerCase();
	        
	        // Which suite, fullsuite or debugsuite or other?
	        String suite = parsedRequest.optionValue(argSuite);
	        String[] args = suite.split(",");
	        resultString.append("Running ExecuteTests.main(args) for client "+ argClient +", where args is:\n");
	        for (String a : args) {
	        	resultString.append("\t"+ a +"\n");
	        }
	        
	        String result = invokeHarnessMethod(client, args);
	        resultString.append(result).append('\n');

		} finally {
			serviceIsRunning = false;
		}

		// Return ok code with the parsable return string
		return (new STAFResult(STAFResult.Ok, resultString.toString()));

	}
	
	public String invokeHarnessMethod(String client, String[] args) {
		String classname = "projects."+ client +".bin.ExecuteTests";
		mLog.info("getHarnessMethod: classname = "+ classname);
		try {
			Class<?> c = Class.forName(classname);
			Method[] methods = c.getDeclaredMethods();
			for (Method m : methods) {
				if (m.getName().equals("main")) {
					Object[] objects = new Object[1];
					objects[0] = args;
					m.invoke(null, objects);
					return ("done");
				}
			}
		} catch (ClassNotFoundException e) {
			mLog.error("Unable to instantiate class: "+ classname, e);
		} catch (IllegalAccessException e) {
			mLog.error("Unable to instantiate class: "+ classname, e);
		} catch (IllegalArgumentException e) {
			mLog.error("Unable to invoke class main method: "+ classname, e);
		} catch (InvocationTargetException e) {
			mLog.error("Unable to invoke class main method: "+ classname, e);
		}
		return ("failed");
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
         "EXECUTE SeleniumRoot <path> client [ zcs|html ] suite [ fullSuite|debugSuite ] -- execute tests \n\n" +
         "QUERY -- TBD: should return statistics on active jobs \n\n" +
         "HALT <TBD> -- TBD: should stop any executing tests\n\n" +
         "HELP\n\n");
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
        stafParserExecute.addOption(argDirectory, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argClient, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOption(argSuite, 1, STAFCommandParser.VALUEREQUIRED);
        stafParserExecute.addOptionNeed(optionExecute, argDirectory);
        stafParserExecute.addOptionNeed(optionExecute, argClient);
        stafParserExecute.addOptionNeed(optionExecute, argSuite);

        // QUERY parser
        stafParserQuery = new STAFCommandParser();
        stafParserQuery.addOption(optionQuery, 1, STAFCommandParser.VALUENOTALLOWED);
        // TODO: Create any query options here
        
        // HELP parser
        stafParserHelp = new STAFCommandParser();
        stafParserHelp.addOption(optionHelp, 1, STAFCommandParser.VALUENOTALLOWED);

        // HALT parser
        stafParserHalt = new STAFCommandParser();
        stafParserHalt.addOption(optionHalt, 1, STAFCommandParser.VALUENOTALLOWED);

                                       

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
