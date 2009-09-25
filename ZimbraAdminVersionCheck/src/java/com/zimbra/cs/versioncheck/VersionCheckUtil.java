package com.zimbra.cs.versioncheck;

import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.SoapFaultException;
import com.zimbra.common.soap.SoapTransport;
import com.zimbra.common.util.CliUtil;
import com.zimbra.cs.client.LmcSession;
import com.zimbra.cs.client.soap.LmcSoapClientException;
import com.zimbra.cs.client.soap.LmcVersionCheckRequest;
import com.zimbra.cs.client.soap.LmcVersionCheckResponse;
import com.zimbra.cs.service.versioncheck.VersionCheckService;
import com.zimbra.cs.util.BuildInfo;
import com.zimbra.cs.util.SoapCLI;

/**
 * @author Greg Solovyev
 */
public class VersionCheckUtil extends SoapCLI {
    private static final String OPT_CHECK_VERSION = "c";
    private static final String SHOW_LAST_RESULT = "r";
    
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
                util.doVersionCheck();
            } else if (cl.hasOption(SHOW_LAST_RESULT)) {
                util.result();
            } else {
                util.usage();
                System.exit(1);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            util.usage(null);
            System.exit(1);
        }
    }
    
    private void doVersionCheck() throws SoapFaultException, IOException, ServiceException, LmcSoapClientException {
        LmcSession session = auth();
        LmcVersionCheckRequest req = new LmcVersionCheckRequest();
        req.setAction(VersionCheckService.VERSION_CHECK_CHECK);
        req.setSession(session);
        req.invoke(getServerUrl());
    }
    
    private void result() throws SoapFaultException, IOException, ServiceException, LmcSoapClientException {
        LmcSession session = auth();
        LmcVersionCheckRequest req = new LmcVersionCheckRequest();
        req.setAction(VersionCheckService.VERSION_CHECK_STATUS);
        req.setSession(session);
        LmcVersionCheckResponse res = (LmcVersionCheckResponse) req.invoke(getServerUrl());        
    }
    
    protected void setupCommandLineOptions() {
        super.setupCommandLineOptions();
        Options options = getOptions();
        options.addOption(OPT_CHECK_VERSION, "check", false, "Initiate version check request.");
        options.addOption(SHOW_LAST_RESULT, "result", false, "Show results of last version check.");
    }
    
    protected String getCommandUsage() {
        return "zmcheckversion <options>";
    }

}
