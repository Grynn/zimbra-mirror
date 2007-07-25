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
    
    //private static final Pattern START_CMD = Pattern.compile("^(STARTCMD:)(.*)$") ;
    //private static final Pattern END_CMD = Pattern.compile("^(ENDCMD:)(.*)$") ;
    private static final Pattern GET_CERT_OUT_PATTERN = Pattern.compile("^([^=]+)=(.*)$");
    public static HashMap<String, String> parseOuput (byte[] in) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(
                                     new ByteArrayInputStream(in))) ;
        String line ;
        HashMap<String, String> hash = new HashMap ();
        Matcher matcher ;
        String key ;
        String value ;
        while ((line = br.readLine())!=null) {
           if (line.startsWith("STARTCMD:") || line.startsWith("ENDCMD:")){
               continue ;
           }else{
               //for GetCert
               System.out.println("DEBUG: Current Line = " + line) ;
               matcher = GET_CERT_OUT_PATTERN.matcher(line) ;
               if (matcher.matches()) {
                   key = matcher.group(1) ;
                   value = matcher.group(2) ;
                   System.out.println("Key = " + key + "; value="+ value) ;
                   hash.put(key, value );
               }else{
                   continue ;
               }
           }
        }
        
        return hash;
    }
   
}


