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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.rmgmt.RemoteResultParser;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;


public class GetCert extends AdminDocumentHandler {
    final static String CERT_TYPE_SELF= "self" ;
    final static String CERT_TYPE_COMM = "comm" ;
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException{
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Provisioning prov = Provisioning.getInstance();
        try {
            //get a server
            List<Server> serverList =  prov.getAllServers();
            Server server = ZimbraCertMgrExt.getCertServer(serverList);
            
            if (server == null) {
                throw ServiceException.INVALID_REQUEST("No valid server was found", null);
            }
            
            String certType = request.getAttribute("type");
            RemoteManager rmgr = RemoteManager.getRemoteManager(server);
            Element response = lc.createElement(ZimbraCertMgrService.GET_CERT_RESPONSE);
//          TODO: for now, we return all by default.
            addCertInfo(response, rmgr.execute(ZimbraCertMgrExt.GET_CERT_CMD)) ;
            /*
            if (certType == null || certType.length() == 0 ) {
                throw ServiceException.INVALID_REQUEST("No valid certificate type is set", null);
            }else if (certType.equals(CERT_TYPE_SELF) || certType.equals(CERT_TYPE_COMM)) {
                addCertInfo(response, rmgr.execute(ZimbraCertMgrExt.GET_CERT_CMD + " " + certType)) ;
            }else {
                throw ServiceException.INVALID_REQUEST("Invalid certificate type: " + certType + ". Must be (self|comm).", null);
            }*/
            
            
             
                       
            return response;
        }catch (IOException ioe) {
            throw ServiceException.FAILURE("exception occurred handling command", ioe);
        }
    }
    
    public void addCertInfo(Element parent, RemoteResult rr) throws ServiceException, IOException{
        Element el = parent.addElement("cert");
        byte[] stdOut = rr.getMStdout() ;
        //String out = new String (stdOut) ;
        //el.addText(out) ;
        
        HashMap <String, String> output = OutputParser.parseOuput(stdOut) ;
        for (String k: output.keySet()) {
            //System.out.println("Adding attribute " + k + " = " + output.get(k)) ;
            el.addAttribute(k, output.get(k));
        }
    }
}
