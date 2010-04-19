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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.rmgmt.RemoteResultParser;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminRightCheckPoint;
import com.zimbra.soap.ZimbraSoapContext;


public class GetCert extends AdminDocumentHandler {
    final static String CERT_TYPE_STAGED= "staged" ;
    final static String CERT_TYPE_ALL = "all" ;
    final static String [] CERT_TYPES = {"ldap", "mailboxd", "mta", "proxy"};
    final static String CERT_STAGED_OPTION_SELF = "self" ;
    final static String CERT_STAGED_OPTION_COMM = "comm" ;
    
    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException{
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        

        
        Provisioning prov = Provisioning.getInstance();
        try {
        	Server server = null;
            String serverId = request.getAttribute("server") ;
            if (serverId != null && serverId.equals(ZimbraCertMgrExt.ALL_SERVERS)) {
            	server = prov.getLocalServer() ;
            }else { 
            	server = prov.get(ServerBy.id, serverId);
            }
            
            if (server == null) {
                throw ServiceException.INVALID_REQUEST("Server with id " + serverId + " could not be found", null);
            }
            
            checkRight(lc, context, server, Admin.R_getCertificateInfo);
            ZimbraLog.security.debug("load the cert info from server:  " + server.getName()) ;
            
            String certType = request.getAttribute("type");
            String option = null ;
            String cmd = "";
            RemoteManager rmgr = RemoteManager.getRemoteManager(server);
            Element response = lc.createElement(ZimbraCertMgrService.GET_CERT_RESPONSE);
            
            if (certType == null || certType.length() == 0 ) {
                throw ServiceException.INVALID_REQUEST("No valid certificate type is set in GetCertRequest", null);
            }else if (certType.equals(CERT_TYPE_STAGED)){ 
                option = request.getAttribute("option") ;
                if (option == null || option.length() ==0) {
                    throw ServiceException.INVALID_REQUEST("No valid option type is set in GetCertRequest for staged certs", null);
                }else if (option.equals(CERT_STAGED_OPTION_SELF) || option.equals(CERT_STAGED_OPTION_COMM)){
                    cmd = ZimbraCertMgrExt.GET_STAGED_CERT_CMD + " " + option;
                    ZimbraLog.security.debug("***** Executing the cmd = " + cmd) ;
                    addCertInfo(response, rmgr.execute(cmd), certType, server.getName()) ;
                }else{
                    throw ServiceException.INVALID_REQUEST(
                           "Invalid option is set in GetCertRequest for staged certs: " 
                           + certType + ". Must be (self|comm).", null); 
                }
            }else if (certType.equals(CERT_TYPE_ALL)){
                for (int i=0; i < CERT_TYPES.length; i ++) {
                    cmd = ZimbraCertMgrExt.GET_DEPLOYED_CERT_CMD + " " + CERT_TYPES[i] ;
                    ZimbraLog.security.debug("***** Executing the cmd = " + cmd) ;
                    addCertInfo(response, rmgr.execute(cmd), CERT_TYPES[i], server.getName()) ;
                }
            }else if (CERT_TYPES.toString().contains(certType)){
                    //individual types
                cmd = ZimbraCertMgrExt.GET_DEPLOYED_CERT_CMD + " " + certType;
                ZimbraLog.security.debug("***** Executing the cmd = " + cmd) ;
                addCertInfo(response, rmgr.execute(cmd), certType, server.getName()) ;
            }else{
                throw ServiceException.INVALID_REQUEST("Invalid certificate type: " + certType + ". Must be (self|comm).", null);
            }
           
            return response;
        }catch (IOException ioe) {
            throw ServiceException.FAILURE("exception occurred handling command", ioe);
        }
    }
    
    public void addCertInfo(Element parent, RemoteResult rr, String certType, String serverName) throws ServiceException, IOException{
        try {
            byte[] stdOut = rr.getMStdout() ;
            HashMap <String, String> output = OutputParser.parseOuput(stdOut) ;
            Element el = parent.addElement("cert");
            el.addAttribute("type", certType);
            el.addAttribute("server", serverName);
            for (String k: output.keySet()) {
                ZimbraLog.security.debug("Adding element " + k + " = " + output.get(k)) ;
                Element certEl = el.addElement(k);
                certEl.setText(output.get(k));
            }
        }catch(ServiceException e) {
            ZimbraLog.security.warn ("Failed to retrieve the certificate information for " + certType + ".");
            ZimbraLog.security.error(e) ;
        }
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
        relatedRights.add(Admin.R_getCertificateInfo);
    }
}
