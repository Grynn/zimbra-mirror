/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
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
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Entry;
import com.zimbra.cs.extension.ZimbraExtension;
import com.zimbra.soap.SoapServlet;


public class ZimbraCertMgrExt implements ZimbraExtension {
    public static final String EXTENSION_NAME_CERTMGR = "com_zimbra_cert_manager";
    
    //Remote commands
    public static final String GET_STAGED_CERT_CMD = "zmcertmgr viewstagedcrt" ;
    public static final String GET_DEPLOYED_CERT_CMD = "zmcertmgr viewdeployedcrt" ;
    public static final String CREATE_CSR_CMD = "zmcertmgr createcsr" ;
    public static final String CREATE_CRT_CMD = "zmcertmgr createcrt"   ;
    public static final String DEPLOY_CERT_CMD = "zmcertmgr deploycrt" ;
    public static final String GET_CSR_CMD = "zmcertmgr viewcsr" ;
    public static final String VERIFY_CRTKEY_CMD = "zmcertmgr verifycrtkey" ;
    public static final String VERIFY_CRTCHAIN_CMD = "zmcertmgr verifycrtchain" ;
    public static final String UPLOADED_CRT_FILE = LC.mailboxd_directory.value() + "/webapps/zimbraAdmin/tmp/current.crt" ;
    public static final String UPLOADED_CRT_CHAIN_FILE = LC.mailboxd_directory.value() + "/webapps/zimbraAdmin/tmp/current_chain.crt" ;
    public static final String SAVED_COMM_KEY_FROM_LDAP = LC.mailboxd_directory.value() + "/webapps/zimbraAdmin/tmp/current_comm.key" ;
   // public static final String COMM_CRT_KEY_FILE = LC.zimbra_home.value() + "/ssl/zimbra/commercial/commercial.key" ;
    public static final String ALL_SERVERS = "--- All Servers ---" ;
    public static final String A_zimbraSSLPrivateKey = "zimbraSSLPrivateKey" ;
    public static final String A_zimbraSSLCertificate = "zimbraSSLCertificate" ;
    public void destroy() {
    }

    public String getName() {
        return EXTENSION_NAME_CERTMGR ;
    }

    public void init() throws ServiceException {
        SoapServlet.addService("AdminServlet", new ZimbraCertMgrService());
    }

}


