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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;
import com.zimbra.common.util.ZimbraLog;


public class GenerateCSR extends AdminDocumentHandler {
    //private final static String TYPE = "type" ;
    private final static String SUBJECT = "subject" ;
    private final static String [] SUBJECT_ATTRS=  {"C", "ST", "L", "O", "OU", "CN"} ;
    final static String SUBJECT_ALT_NAME = "SubjectAltName" ;
    
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
        String cmd = ZimbraCertMgrExt.GENERATE_CSR_CMD  ;
        String newCSR = request.getAttribute("new") ;
        if (newCSR.equalsIgnoreCase("1")) {
            cmd +=  " -new " ;
        }
        
        String subject = getSubject (request);
       
        if (subject != null && subject.length() > 0) {
            cmd += " \"" + subject +"\"";
        }
        
        String subjectAltNames = getSubjectAltNames(request) ;
        if (subjectAltNames != null && subjectAltNames.length() >0) {
            cmd += " -subjectAltNames \"" + subjectAltNames + "\"" ;
        }
        
        RemoteManager rmgr = RemoteManager.getRemoteManager(server);
        ZimbraLog.security.info("***** Executing the cmd = " + cmd) ;
        rmgr.execute(cmd);
        Element response = lc.createElement(ZimbraCertMgrService.GEN_CSR_RESPONSE);
        
        return response;  
    }

    String getSubject (Element req) {
        Element e = null ;
        String value = null ;
        String subject = "" ;
        for (int i=0; i < SUBJECT_ATTRS.length ; i ++) {
            try {
                e = req.getElement(SUBJECT_ATTRS[i]);
            }catch (ServiceException se) {
                e = null ; //the current attribute doesn't exist
            }
            if (e != null) {
                value = e.getText();
                if (value != null && value.length() > 0) {
                    subject += "/" + SUBJECT_ATTRS[i] + "=" + value.replace("/", "\\/") ;
                }
            }
        }
        
        return subject ;
    }
    
    String getSubjectAltNames (Element request) {
        Element e = null ;
        String subjectAltNames = "" ;
      
        for (Element a : request.listElements(SUBJECT_ALT_NAME)) {
            String value = a.getText();
            if (value != null && value.length() > 0) {
                if (subjectAltNames.length() > 0) {
                    subjectAltNames += "," ;
                }
                subjectAltNames += value ;
            }
        }
   
        return subjectAltNames ;
    }
}
