package framework.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;

public class StafAbstract {
	private static Logger logger = LogManager.getLogger(StafAbstract.class);
	
	// STAF command settings
	public String StafServer = null;
	public String StafService = null;
	public String StafParms = null;
	public int StafTimeoutMillis = 30000;
	
	// STAF response
	public String StafResponse = null;
	
	public StafAbstract() {
		logger.info("new StafAbstract");
		
		StafServer = ZimbraSeleniumProperties.getStringProperty("server", "local");
		StafService = "PROCESS";
		StafTimeoutMillis = 30000;
		StafParms = "START SHELL COMMAND \"ls\" RETURNSTDOUT RETURNSTDERR WAIT "+ StafTimeoutMillis;
		
	}
	
	/**
	 * Get the STAF command being used
	 * @return
	 */
	public String getStafCommand() {
		StringBuilder sb = new StringBuilder();
		sb.append("STAF ");
		sb.append(StafServer + " ");
		sb.append(StafService + " ");
		sb.append(StafParms + " ");
		return (sb.toString());
	}
	
	/**
	 * After using execute(), get the STAF response
	 * @return
	 */
	public String getStafResponse() {
		return (StafResponse);
	}
	
	/**
	 * Execute the STAF request
	 * @return 
	 * @throws HarnessException 
	 */
	public boolean execute() throws HarnessException {
		
		STAFResult StafResult = null;
		STAFHandle handle = null;
		
		try
		{
			
			handle = new STAFHandle(StafAbstract.class.getName());
			
	        try
	        {
	        	
	        	logger.info(getStafCommand());
	        	
	            StafResult = handle.submit2(StafServer, StafService, StafParms);
	
	            if ( (StafResult.result != null) && (!StafResult.result.trim().equals("")) )
	            {
	            	if ( STAFMarshallingContext.isMarshalledData(StafResult.result) )
	            	{
	            		STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(StafResult.result);
	            		StafResponse = STAFMarshallingContext.formatObject(mc);
	            	}
	            	else
	            	{
	            		StafResponse = StafResult.result;
	            	}
	            }
	
	            return (StafResult.rc != STAFResult.Ok);
 
			} finally {
	        	
				logger.info(StafResponse);
				
	            try {
	            	
					handle.unRegister();
			    	
				} catch (STAFException e) {
		        	throw new HarnessException("Error unregistering with STAF, RC:" + e.rc, e);
				}
				
			}
        
		}
		catch (STAFException e)
		{
        	throw new HarnessException("Error registering or unregistering with STAF, RC:" + e.rc, e);
		}
	        	
            

	}
}
