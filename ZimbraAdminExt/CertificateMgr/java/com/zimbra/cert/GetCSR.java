package com.zimbra.cert;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sun.corba.se.spi.orbutil.fsm.Guard.Result;
import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;


public class GetCSR extends AdminDocumentHandler {
    static final String KEY_SUBJECT = "subject" ;
    private final static String CSR_FILE = LC.zimbra_home.value() + "/ssl/csr/zimbra.csr" ;
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Provisioning prov = Provisioning.getInstance();
        
        //get a server
        List<Server> serverList =  prov.getAllServers();
        Server server = ZimbraCertMgrExt.getCertServer(serverList);
          
        if (server == null) {
            throw ServiceException.INVALID_REQUEST("No valid server was found", null);
        }
        
        String cmd = ZimbraCertMgrExt.GET_CSR_CMD ;
        RemoteManager rmgr = RemoteManager.getRemoteManager(server);
        System.out.println("***** Executing the cmd = " + cmd) ;
        RemoteResult rr = rmgr.execute(cmd);
        Element response = lc.createElement(ZimbraCertMgrService.GET_CSR_RESPONSE);
        String csr_exists = "0" ;
        String isComm = "0" ;
        try {
            HashMap <String, String> output = OutputParser.parseOuput(rr.getMStdout()) ;
            HashMap <String, String> subjectDSN = null ;
            for (String k: output.keySet()) {
                if (k.equals(KEY_SUBJECT)) {
                    subjectDSN = OutputParser.parseSubject(output.get(k)) ;
                    break ;
                }
            }
            
            if (subjectDSN != null) {
                for (String k: subjectDSN.keySet()) {
                    System.out.println("Adding attribute " + k + " = " + output.get(k)) ;
                    
                    Element el = response.addElement(k);
                    el.setText(subjectDSN.get(k));
                }
                
                //check if the zimbra.csr in the csr directory exists
                if ((new File (CSR_FILE)).exists()) {
                    csr_exists = "1" ;
                    if ((new File (InstallCert.COMM_CRT_FILE)).exists() ) {
                        isComm = "1" ;
                    }
                }
            }
            response.addAttribute("csr_exists", csr_exists) ;
            response.addAttribute("isComm", isComm) ;
            
            return response ;
        }catch (IOException ioe) {
            throw ServiceException.FAILURE("exception occurred handling command", ioe);
        }
    }
}
