package staf;

import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFResult;
import com.ibm.staf.STAFUtil;
import com.ibm.staf.service.STAFCommandParser;
import com.ibm.staf.service.STAFServiceInterfaceLevel30;

public class StafIntegration implements STAFServiceInterfaceLevel30 {
    static private Logger mLog = Logger.getLogger(StafIntegration.class);

	// STAF Specifics
	private final String kVersion = "1.1.0";
    private static final int kDeviceInvalidSerialNumber = 4001;
    private String stafServiceName;
    private STAFHandle stafServiceHandle;

    // SERVICE Specifics
    private STAFCommandParser stafParserExecute;
    private STAFCommandParser stafParserQuery;
    private STAFCommandParser stafParserHelp;
    private STAFCommandParser stafParserHalt;

    private String optionExecute = "execute";
    private String optionQuery= "query";
    private String optionHelp = "help";
    private String optionHalt = "halt";
    
    
	@Override
	public STAFResult acceptRequest(RequestInfo info) {
        mLog.info("StafIntegration: acceptRequest ...");


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
		return (new STAFResult(STAFResult.JavaError, "Implement me!"));
	}
	
	private STAFResult handleQuery(RequestInfo info) {
		return (new STAFResult(STAFResult.JavaError, "Implement me!"));
	}
	
	private STAFResult handleHalt(RequestInfo info) {
		return (new STAFResult(STAFResult.JavaError, "Implement me!"));
	}
	
	private STAFResult handleHelp() {

        mLog.info("StafTestStaf: handleHelp ...");

    	// TODO: Need to convert the help command into the variables, aEXECUTE, aHELP, etc.
        return new STAFResult(STAFResult.Ok,
         "StafTest Service Help\n\n" + 
         "EXECUTE <TBD> -- TBD: should execute tests \n\n" +
         "QUERY <TBD> -- TBD: should return statistics on active jobs \n\n" +
         "HALT <TBD> -- TBD: should stop any executing tests\n\n" +
         "HELP\n\n");
	}
	

	@Override
	public STAFResult init(InitInfo info) {
        mLog.info("StafIntegration: init ...");


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
        // TODO: Create any execute options here
        
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

	@Override
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
