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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.rmgmt.RemoteCommands;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.rmgmt.RemoteResultParser;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.FileUploadServlet.Upload;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;


public class InstallCert extends AdminDocumentHandler {
    private final static String TYPE = "type" ;
    private final static String AID = "aid" ;
    private final static String VALIDATION_DAYS = "validation_days" ;
    final static String COMM_CRT_FILE = LC.zimbra_home.value() + "/ssl/csr/comm.crt" ;
    
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        Provisioning prov = Provisioning.getInstance();
        
        //get a server
        List<Server> serverList =  prov.getAllServers();
        Server server = ZimbraCertMgrExt.getCertServer(serverList);
        
        if (server == null) {
            throw ServiceException.INVALID_REQUEST("No valid server was found", null);
        }
        RemoteManager rmgr = RemoteManager.getRemoteManager(server);
        
        String type = request.getAttribute(TYPE) ;
     
        //move the uploaded files to /opt/zimbra/ssl/csr/comm.crt
        if (type.equals("comm")) {
            String attachId = request.getAttribute(AID);
            Upload up = FileUploadServlet.fetchUpload(lc.getAuthtokenAccountId(), attachId, lc.getRawAuthToken());
            if (up == null)
                throw ServiceException.FAILURE("Uploaded file with " + attachId + " was not found.", null);
           
            checkUploadedCommCert(attachId, up, rmgr) ;
        }
        
        
        Element valDayEl = request.getElement(VALIDATION_DAYS) ;
        String validation_days = null ;
        String cmd = ZimbraCertMgrExt.INSTALL_CERT_CMD + " " + type ;
        
        if (valDayEl != null) {
            validation_days = valDayEl.getText() ;
            if (validation_days != null && validation_days.length() > 0) {
                cmd += " " + validation_days ;
            }
        }
                
        ZimbraLog.security.info("***** Executing the cmd = " + cmd) ;
        RemoteResult rr = rmgr.execute(cmd);
        //ZimbraLog.security.info("***** Exit Status Code = " + rr.getMExitStatus()) ;
        try {
            OutputParser.parseOuput(rr.getMStdout()) ;
        }catch (IOException ioe) {
            throw ServiceException.FAILURE("exception occurred handling command", ioe);
        }
        
        /** getMExitStatus code is not properly implemented
        if (rr.getMExitStatus() != 0) {
            String error = new String (rr.getMStderr()) ;
            throw ServiceException.FAILURE("exception occurred handling command", new Exception (error));
        }**/
        
        Element response = lc.createElement(ZimbraCertMgrService.INSTALL_CERT_RESPONSE);
        
        return response;    
    }
    
    private boolean checkUploadedCommCert (String aid, Upload up, RemoteManager rmgr) throws ServiceException {
        InputStream is = null ;
        try {
            is = up.getInputStream() ;
            byte [] content = ByteUtil.getContent(is, 1024) ;
            ZimbraLog.security.info ("Put the uploaded commercial crt  to " + COMM_CRT_FILE) ;
            ByteUtil.putContent(COMM_CRT_FILE, content) ;
            
            //run zmcertmgr verifycrt to validate the cert and key
            ZimbraLog.security.info("***** verifying cert with key: ZimbraCertMgrExt.VERIFY_CRT_CMD ");
            RemoteResult rr = rmgr.execute(ZimbraCertMgrExt.VERIFY_CRT_CMD) ;
            try {
                OutputParser.parseOuput(rr.getMStdout()) ;
            }catch (IOException ioe) {
                throw ServiceException.FAILURE("exception occurred handling command", ioe);
            }
        } catch (IOException ioe) {
            throw ServiceException.FAILURE("error reading uploaded certificate", ioe);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    ZimbraLog.security.warn("exception closing uploaded certificate:", ioe);
                }
            }
        }

        return true ;
    }
}
