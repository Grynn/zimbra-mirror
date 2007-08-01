package com.zimbra.cert;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.soap.SoapServlet;


public class ZimbraCertMgrExt implements ZimbraExtension {
    public static final String EXTENSION_NAME_CERTMGR = "zimbra_cert_manager";
    
    //Remote commands
    public static final String INSTALL_CERT_CMD = "zmcertmgr install" ;
    public static final String GET_CERT_CMD = "zmcertmgr view" ;
    public static final String GENERATE_CSR_CMD = "zmcertmgr gencsr" ;
    public static final String GET_CSR_CMD = "zmcertmgr viewcsr" ;
    //public static final String CMD_LOG = " &> /tmp/cert.log.`date +%Y%m%d%H%M%S`" ;
    public void destroy() {
    }

    public String getName() {
        return EXTENSION_NAME_CERTMGR ;
    }

    public void init() throws ServiceException {
        SoapServlet.addService("AdminServlet", new ZimbraCertMgrService());
    }
    
    public static Server getCertServer (List<Server> serverList) {
        Server server = null ;
        for (int i = 0 ; i < serverList.size(); i ++) {
            server = serverList.get(i) ;
            if (server != null) { 
                break ;
            }
        }
        return server ;
    }
    
   
}


