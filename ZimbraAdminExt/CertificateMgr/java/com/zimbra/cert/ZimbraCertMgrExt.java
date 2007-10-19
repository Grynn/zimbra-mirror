/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */
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

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.soap.SoapServlet;


public class ZimbraCertMgrExt implements ZimbraExtension {
    public static final String EXTENSION_NAME_CERTMGR = "com_zimbra_cert_manager";
    
    //Remote commands
    public static final String INSTALL_CERT_CMD = "zmcertmgr install" ;
    public static final String GET_CERT_CMD = "zmcertmgr viewcrt" ;
    public static final String GENERATE_CSR_CMD = "zmcertmgr gencsr" ;
    public static final String GET_CSR_CMD = "zmcertmgr viewcsr" ;
    public static final String VERIFY_CRT_CMD = "zmcertmgr verifycrt" ;
    public static final String COMM_CSR_FILE = LC.mailboxd_directory.value() + "/zimbraAdmin/tmp/commercial.csr" ;
    public static final String COMM_CRT_FILE = LC.mailboxd_directory.value() + "/zimbraAdmin/tmp/commercial.crt" ;
    //final static String COMM_CRT_FILE =  "/zimbra/server/commmercial.crt" ;
    
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


