package com.zimbra.qa.selenium.framework.util.staf;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ibm.staf.STAFException;
import com.ibm.staf.STAFHandle;
import com.ibm.staf.STAFMarshallingContext;
import com.ibm.staf.STAFResult;
import com.zimbra.qa.selenium.framework.util.*;

/**
 * A wrapper class to create STAF classes from
 * @author Matt Rhoades
 *
 */
public class StafAbstract {
	protected static Logger logger = LogManager.getLogger(StafAbstract.class);
	
	// STAF command settings
	protected String StafServer = null;
	protected String StafService = null;
	protected String StafParms = null;
	
	// STAF response
	protected STAFResult StafResult = null;
	protected String StafResponse = null;
	
	
	public StafAbstract() {
		logger.info("new "+ StafAbstract.class.getCanonicalName());
		
		StafServer = ZimbraSeleniumProperties.getStringProperty("server.host", "local");
		StafService = "PING";
		StafParms = "PING";
		
	}
	
	public STAFResult getSTAFResult() {
		return (StafResult);
	}
	
	public String getSTAFResponse() {
		return (StafResponse);
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
		
		STAFHandle handle = null;
		
		try
		{
			
			handle = new STAFHandle(StafAbstract.class.getName());
			
	        try
	        {
	        	
	        	logger.info("STAF Command: " + getStafCommand());
	        	
	            StafResult = handle.submit2(StafServer, StafService, StafParms);
	            
	            if (StafResult == null)
	            	throw new HarnessException("StafResult was null");
	            
            	logger.info("STAF Response Code: "+ StafResult.rc);

            	if ( StafResult.rc == STAFResult.AccessDenied ) {
            		// Common error in WDC.  Log a helper message.
            		logger.error("On the server, use: staf local trust set machine *.eng.vmware.com level 5");
            	}

            	if ( StafResult.rc != STAFResult.Ok ) {
            		throw new HarnessException("Invalid STAF response code ("+ StafResult.rc +"): "+ StafResult.result);
            	}

	            if ( (StafResult.result != null) && (!StafResult.result.trim().equals("")) ) {
	            	
	            	logger.debug(StafResult.result);
	            		        	
	            	if ( STAFMarshallingContext.isMarshalledData(StafResult.result) )
	            	{
	            		STAFMarshallingContext mc = STAFMarshallingContext.unmarshall(StafResult.result);
	            		
	            		// Get the entire response
	            		StafResponse = STAFMarshallingContext.formatObject(mc);
	            		
	            	}
	            	else
	            	{
	            		StafResponse = StafResult.result;
	            	}
	            	
	            }
	
	            return (StafResult.rc == STAFResult.Ok);
 
			} finally {
	        	
				logger.info("STAF Response: " + StafResponse);
				
				if (handle != null )
					handle.unRegister();
				
			}
        
		}
		catch (STAFException e)
		{
        	throw new HarnessException("Error registering or unregistering with STAF, RC: " + e.rc, e);
		}
	        	
            

	}
}
