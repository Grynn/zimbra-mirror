package com.zimbra.cs.versioncheck;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.account.Key;
import com.zimbra.common.account.Key.ServerBy;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.common.util.CliUtil;
import com.zimbra.cs.account.Config;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.client.LmcSession;
import com.zimbra.cs.client.soap.LmcSoapClientException;
import com.zimbra.cs.client.soap.LmcVersionCheckRequest;
import com.zimbra.cs.client.soap.LmcVersionCheckResponse;
import com.zimbra.cs.util.BuildInfo;
import com.zimbra.cs.util.SoapCLI;
import com.zimbra.common.util.DateUtil;
/**
 * @author Greg Solovyev
 */
public class VersionCheckUtil extends SoapCLI {
    private static final String OPT_CHECK_VERSION = "c";
    private static final String OPT_MANUAL_CHECK_VERSION = "m";
    private static final String SHOW_LAST_STATUS = "r";
    
    protected VersionCheckUtil() throws ServiceException {
        super();
    }
    
    public static void main(String[] args) {
        CliUtil.toolSetup();
        SoapTransport.setDefaultUserAgent("zmcheckversion", BuildInfo.VERSION);
        VersionCheckUtil util = null;
        try {
            util = new VersionCheckUtil();
        } catch (ServiceException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }

        try {
            util.setupCommandLineOptions();
            CommandLine cl = null;
            try {
                cl = util.getCommandLine(args);
            } catch (ParseException e) {
                System.out.println(e.getMessage());
                util.usage();
                System.exit(1);
            }
            
            if (cl == null) {
                System.exit(1);
            }
            
            if (cl.hasOption(OPT_CHECK_VERSION)) {
            	//check schedule
        		Provisioning prov = Provisioning.getInstance();
        		Config config;
        		config = prov.getConfig();
            	String updaterServerId = config.getAttr(Provisioning.A_zimbraVersionCheckServer);
            	
                if (updaterServerId != null) {
                    Server server = prov.get(Key.ServerBy.id, updaterServerId);
                    if (server != null) {
                    	Server localServer = prov.getLocalServer();
                    	if (localServer!=null) { 
                    		if(!localServer.getId().equalsIgnoreCase(server.getId())) {
                    			System.out.println("Wrong server");
                    			System.exit(0);
                    		}
                    	}
                    }
                }        		
        		String versionInterval = config.getAttr(Provisioning.A_zimbraVersionCheckInterval);
        		if(versionInterval == null || versionInterval.length()==0 || versionInterval.equalsIgnoreCase("0")) {
        			System.out.println("Automatic updates are disabled");
        			System.exit(0);
        		} else {
        			long checkInterval = DateUtil.getTimeIntervalSecs(versionInterval,0);
        			String lastAttempt = config.getAttr(Provisioning.A_zimbraVersionCheckLastAttempt);
        			if(lastAttempt != null) {
        				Date lastChecked = DateUtil.parseGeneralizedTime(config.getAttr(Provisioning.A_zimbraVersionCheckLastAttempt));
        				Date now = new Date();
        				if	(now.getTime()/1000- lastChecked.getTime()/1000 >= checkInterval) {
        					util.doVersionCheck();
        				} else {
        					System.out.println("Too early");
        					System.exit(0);
        				}
        			} else {
        				util.doVersionCheck();
        			}
        		}
            } else if (cl.hasOption(OPT_MANUAL_CHECK_VERSION)) {
                util.doVersionCheck();
            } else if (cl.hasOption(SHOW_LAST_STATUS)) {
                util.doResult();
                System.exit(0);
            } else {
                util.usage();
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            ZimbraLog.extensions.error("Error in versioncheck util", e);
            util.usage(null);
            System.exit(1);
        }
    }
    
    private void doVersionCheck() throws SoapFaultException, IOException, ServiceException, LmcSoapClientException {
        LmcSession session = auth();
        LmcVersionCheckRequest req = new LmcVersionCheckRequest();
        req.setAction(AdminConstants.VERSION_CHECK_CHECK);
        req.setSession(session);
        req.invoke(getServerUrl());
    }
    
    private void doResult() throws SoapFaultException, IOException, ServiceException, LmcSoapClientException {
    	try {
	    	LmcSession session = auth();
	        LmcVersionCheckRequest req = new LmcVersionCheckRequest();
	        req.setAction(AdminConstants.VERSION_CHECK_STATUS);
	        req.setSession(session);
	        LmcVersionCheckResponse res = (LmcVersionCheckResponse) req.invoke(getServerUrl());
	    	List <VersionUpdate> updates = res.getUpdates();
	    	
	    	for(Iterator <VersionUpdate> iter = updates.iterator();iter.hasNext();){
	    		VersionUpdate update = iter.next();
	    		String critical;
	    		if(update.isCritical()) {
	    			critical = "critical";
	    		} else { 
	    			critical = "not critical";
	    		}
	    		System.out.println(
	    				String.format("Found a %s update. Update is %s . Update version: %s. For more info visit: %s", 
	    						update.getType(),critical,update.getVersion(),update.getUpdateURL())
	   			);   		
	    	}   
    	} catch (SoapFaultException soape) {
    		System.out.println("Cought SoapFaultException");
    		System.out.println(soape.getStackTrace().toString());
    		throw (soape);
    	}  catch (LmcSoapClientException lmce) {
    		System.out.println("Cought LmcSoapClientException");
    		System.out.println(lmce.getStackTrace().toString());
    		throw (lmce);
    	} catch (ServiceException se) {
    		System.out.println("Cought ServiceException");
    		System.out.println(se.getStackTrace().toString());
    		throw (se);
    	} catch (IOException ioe) {
    		System.out.println("Cought IOException");
    		System.out.println(ioe.getStackTrace().toString());
    		throw (ioe);
    	}
    }
    
    protected void setupCommandLineOptions() {
       // super.setupCommandLineOptions();
        Options options = getOptions();
        Options hiddenOptions = getHiddenOptions();
        hiddenOptions.addOption(OPT_CHECK_VERSION, "autocheck", false, "Initiate version check request (exits if zimbraVersionCheckInterval==0)");        
        options.addOption(SHOW_LAST_STATUS, "result", false, "Show results of last version check.");
        options.addOption(OPT_MANUAL_CHECK_VERSION, "manual", false, "Initiate version check request.");
    }
    
    protected String getCommandUsage() {
        return "zmcheckversion <options>";
    }

}
