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

import java.io.*;
import java.util.List;
import java.util.Map;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.AdminConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.common.util.StringUtil;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.account.Server;
import com.zimbra.cs.account.Provisioning.ServerBy;
import com.zimbra.cs.account.accesscontrol.AdminRight;
import com.zimbra.cs.account.accesscontrol.Rights.Admin;
import com.zimbra.cs.rmgmt.RemoteCommands;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.rmgmt.RemoteResultParser;
import com.zimbra.cs.service.FileUploadServlet;
import com.zimbra.cs.service.FileUploadServlet.Upload;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.cs.service.admin.AdminRightCheckPoint;
import com.zimbra.soap.ZimbraSoapContext;


public class InstallCert extends AdminDocumentHandler {           
    private final static String TYPE = "type" ;
    private final static String SUBJECT = "subject" ;
    final static String CERT_TYPE_SELF= "self" ;
    final static String CERT_TYPE_COMM = "comm" ;
    private final static String COMM_CERT = "comm_cert" ;
    private final static String AID = "aid" ;
    //private final static String ALLSERVER = "allserver" ;
    private final static String ALLSERVER_FLAG = "-allserver" ;
    private final static String VALIDATION_DAYS = "validation_days" ;
    private final static String KEYSIZE = "keysize" ;
    private Server server = null;
    
    private Provisioning prov = null;
     
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        ZimbraSoapContext lc = getZimbraSoapContext(context);
        
        prov = Provisioning.getInstance();
        
        String serverId = request.getAttribute("server") ;
        boolean isTargetAllServer = false ;
        if (serverId != null && serverId.equals(ZimbraCertMgrExt.ALL_SERVERS)) {
        	server = prov.getLocalServer() ;
        	isTargetAllServer = true ;
        }else {
        	server = prov.get(ServerBy.id, serverId);
        }
    	
        if (server == null) {
            throw ServiceException.INVALID_REQUEST("Server with id " + serverId + " could not be found", null);
        }
        checkRight(lc, context, server, Admin.R_installCertificate);
        ZimbraLog.security.debug("Install the certificateion for server:  " + server.getName()) ;
        //the deployment of certs should happen on the target server
        RemoteManager rmgr = RemoteManager.getRemoteManager(server);
        String cmd = ZimbraCertMgrExt.CREATE_CRT_CMD ;
        String deploycrt_cmd = ZimbraCertMgrExt.DEPLOY_CERT_CMD ;         
        String certType = request.getAttribute(TYPE) ;
        if (certType == null || certType.length() == 0 ) {
            throw ServiceException.INVALID_REQUEST("No valid certificate type is set", null);
        }else if (certType.equals(CERT_TYPE_SELF) || certType.equals(CERT_TYPE_COMM)) {
            //cmd += " " + certType  ;   //createcrt implies self signed
            deploycrt_cmd += " " + certType ;
        }else {
            throw ServiceException.INVALID_REQUEST("Invalid certificate type: " + certType + ". Must be (self|comm).", null);
        }
        
        if (certType.equals("comm")) {
            checkUploadedCommCert(request, lc, isTargetAllServer) ;
        }
        
        //always set the -new flag for the cmd since the ac requests for a new cert always
        cmd += " -new " ;
        
        Element valDayEl = request.getElement(VALIDATION_DAYS) ;
        String validation_days = null ;
                
        if ((valDayEl != null) && (!certType.equals("comm"))) {
            validation_days = valDayEl.getText() ;
            if (validation_days != null && validation_days.length() > 0) {
                cmd += " -days " + validation_days ;
            }
        }

        Element subjectEl = request.getElement(SUBJECT)  ;
        String subject = GenerateCSR.getSubject(subjectEl) ;

        String subjectAltNames = GenerateCSR.getSubjectAltNames(request) ;

        if (certType.equals("self")) {
            Element keysizeEl = request.getElement (KEYSIZE) ;
            String keysize = null ;

            if (keysizeEl != null)  {
                if (certType.equals("self")) {
                    keysize = keysizeEl.getText() ;
                    if (!(keysize.equalsIgnoreCase("1024") || keysize.equalsIgnoreCase("2048"))) {
                        keysize = "2048";
                    }
                }
            } else {
                keysize = "2048";
            }
            cmd += " -keysize " + keysize + " " ;

            if (subject != null && subject.length() > 0) {
                cmd += "-subject \"" + subject +"\"";
            }

            if (subjectAltNames != null && subjectAltNames.length() >0) {
                cmd += " -subjectAltNames \"" + subjectAltNames + "\"" ;
            }
        } else if (certType.equals("comm")) {
            deploycrt_cmd += " " + ZimbraCertMgrExt.UPLOADED_CRT_FILE +  " "  + ZimbraCertMgrExt.UPLOADED_CRT_CHAIN_FILE ;
        }
        
        if (isTargetAllServer) {
           if (certType.equals("self")) { //self -allserver install - need to pass the subject to the createcrt cmd
                if (subject != null && subject.length() > 0) {
                    ZimbraLog.security.debug("Subject for allserver: " + subject);
                    cmd += " -subject " + " \"" + subject +"\"";
                }
            }

            cmd += " " + ALLSERVER_FLAG;
            deploycrt_cmd += " " + ALLSERVER_FLAG;
        }

        RemoteResult rr ;
        if (certType.equals("self")) {
            ZimbraLog.security.debug("***** Executing the cmd = " + cmd) ;
            rr = rmgr.execute(cmd);
            //ZimbraLog.security.info("***** Exit Status Code = " + rr.getMExitStatus()) ;
            try {
                OutputParser.parseOuput(rr.getMStdout()) ;
            }catch (IOException ioe) {
                throw ServiceException.FAILURE("exception occurred handling command", ioe);
            }
        }
        
        //need to deploy the crt now
        ZimbraLog.security.debug("***** Executing the cmd = " + deploycrt_cmd) ;
        rr = rmgr.execute(deploycrt_cmd);
        try {
            OutputParser.parseOuput(rr.getMStdout()) ;
        }catch (IOException ioe) {
            throw ServiceException.FAILURE("exception occurred handling command", ioe);
        }
        
        Element response = lc.createElement(ZimbraCertMgrService.INSTALL_CERT_RESPONSE);
        response.addAttribute("server", server.getName());
        return response;    
    }

    
    private boolean checkUploadedCommCert (Element request, ZimbraSoapContext lc, boolean isAllServer) throws ServiceException {
        Upload up = null ;
        InputStream is = null ;
        //the verification commands are all executed on the local server
        RemoteManager rmgr = RemoteManager.getRemoteManager(prov.getLocalServer());
        
        try {
            //read the cert file
            ByteArrayOutputStream completeCertChain = new ByteArrayOutputStream(8192);
            Element certEl = request.getPathElement(new String [] {"comm_cert", "cert"});
            String attachId = certEl.getAttribute(AID) ;
            String filename = certEl.getAttribute("filename") ;
            ZimbraLog.security.debug("Certificate Filename  = " + filename + "; attid = " + attachId );
            
            up = FileUploadServlet.fetchUpload(lc.getAuthtokenAccountId(), attachId, lc.getAuthToken());
            if (up == null)
                throw ServiceException.FAILURE("Uploaded file " + filename + " with " + attachId + " was not found.", null);
          
            is = up.getInputStream() ;
            byte [] cert = ByteUtil.getContent(is, 1024) ;
            ZimbraLog.security.debug ("Put the uploaded commercial crt  to " + ZimbraCertMgrExt.UPLOADED_CRT_FILE) ;
            ByteUtil.putContent(ZimbraCertMgrExt.UPLOADED_CRT_FILE, cert) ;
            is.close();
            completeCertChain.write(cert);
            completeCertChain.write('\n') ;
                    
            //read the CA
            ByteArrayOutputStream baos = new ByteArrayOutputStream(8192);


            Element rootCAEl = request.getPathElement(new String [] {"comm_cert", "rootCA"});
            attachId = rootCAEl.getAttribute(AID) ;
            filename = rootCAEl.getAttribute("filename") ;
            
            ZimbraLog.security.debug("Certificate Filename  = " + filename + "; attid = " + attachId );
            
            up = FileUploadServlet.fetchUpload(lc.getAuthtokenAccountId(), attachId, lc.getAuthToken());
            if (up == null)
                throw ServiceException.FAILURE("Uploaded file " + filename + " with " + attachId + " was not found.", null);
            is = up.getInputStream();
            byte [] rootCA = ByteUtil.getContent(is, 1024) ;
            is.close();
            
            //read interemediateCA
            byte [] intermediateCA ;
            List<Element> intermediateCAElList = request.getPathElementList(new String [] {"comm_cert", "intermediateCA"});
            if (intermediateCAElList != null && intermediateCAElList.size() > 0) {
                for (int i=0; i < intermediateCAElList.size(); i ++ ) {
                    Element intemediateCAEl = intermediateCAElList.get(i);
                    attachId = intemediateCAEl.getAttribute(AID) ;
                    filename = intemediateCAEl.getAttribute("filename") ;
                    
                    if (attachId != null && filename != null) {
                        ZimbraLog.security.debug("Certificate Filename  = " + filename + "; attid = " + attachId );
                        
                        up = FileUploadServlet.fetchUpload(lc.getAuthtokenAccountId(), attachId, lc.getAuthToken());
                        if (up == null)
                            throw ServiceException.FAILURE("Uploaded file " + filename + " with " + attachId + " was not found.", null);
                        is = up.getInputStream();
                        intermediateCA = ByteUtil.getContent(is, 1024);
                        is.close();
                        
                        baos.write(intermediateCA);
                        baos.write('\n');

                        completeCertChain.write(intermediateCA);
                        completeCertChain.write('\n');
                    }
                }
            }
            
            baos.write(rootCA);
            baos.write('\n');


            byte [] chain = baos.toByteArray() ;
            baos.close();

            completeCertChain.write(rootCA);
            completeCertChain.write('\n');
            completeCertChain.close();
            
            ZimbraLog.security.debug ("Put the uploaded crt chain  to " + ZimbraCertMgrExt.UPLOADED_CRT_CHAIN_FILE) ;
            ByteUtil.putContent(ZimbraCertMgrExt.UPLOADED_CRT_CHAIN_FILE, chain) ;

            String privateKey = null;
            if (isAllServer) {
                ZimbraLog.security.debug ("Retrieving zimbraSSLPrivateKey from Global Config.");
                privateKey = prov.getConfig().getAttr(ZimbraCertMgrExt.A_zimbraSSLPrivateKey);
                //Note: We do this because zmcertmgr don't save the private key to global config
                //since -allserver is not supported by createcsr
                // and deploycrt has to take the hard path of cert and CA chain
                if (privateKey == null || privateKey.length() <= 0) {
                    //permission is denied for the  COMM_CRT_KEY_FILE which is readable to root only
                    //ZimbraLog.security.debug ("Retrieving commercial private key from " + ZimbraCertMgrExt.COMM_CRT_KEY_FILE);
                    //privateKey = new String (ByteUtil.getContent(new File(ZimbraCertMgrExt.COMM_CRT_KEY_FILE))) ;

                    //retrieve the key from the local server  since the key is always saved in the local server when createcsr is called
                    ZimbraLog.security.debug ("Retrieving zimbraSSLPrivateKey from server: " + server.getName());
                    privateKey = server.getAttr(ZimbraCertMgrExt.A_zimbraSSLPrivateKey) ;
                }
            } else {
                ZimbraLog.security.debug ("Retrieving zimbraSSLPrivateKey from server: " + server.getName());
                privateKey = server.getAttr(ZimbraCertMgrExt.A_zimbraSSLPrivateKey) ;
            }

            if (privateKey != null && privateKey.length() > 0) {
                ZimbraLog.security.debug ("Saving zimbraSSLPrivateKey to  " + ZimbraCertMgrExt.SAVED_COMM_KEY_FROM_LDAP) ;
            }   else {
                 throw ServiceException.FAILURE("zimbraSSLPrivateKey is not present.", new Exception());
            }
            ByteUtil.putContent(ZimbraCertMgrExt.SAVED_COMM_KEY_FROM_LDAP, privateKey.getBytes());
            
            try {
                //run zmcertmgr verifycrt to validate the cert and key
                String cmd = ZimbraCertMgrExt.VERIFY_CRTKEY_CMD + " comm "
                            //+ " " + ZimbraCertMgrExt.COMM_CRT_KEY_FILE
                            + " " + ZimbraCertMgrExt.SAVED_COMM_KEY_FROM_LDAP
                            + " " + ZimbraCertMgrExt.UPLOADED_CRT_FILE ;
              
                String verifychaincmd = ZimbraCertMgrExt.VERIFY_CRTCHAIN_CMD
                            + " " + ZimbraCertMgrExt.UPLOADED_CRT_CHAIN_FILE
                            + " " + ZimbraCertMgrExt.UPLOADED_CRT_FILE ;
                
          
                ZimbraLog.security.debug("*****  Executing the cmd: " + cmd);
                RemoteResult rr = rmgr.execute(cmd) ;
          
                OutputParser.parseOuput(rr.getMStdout()) ;
                
                //run zmcertmgr verifycrtchain to validate the certificate chain
                ZimbraLog.security.debug("*****  Executing the cmd: " + verifychaincmd);
                rr = rmgr.execute(verifychaincmd) ;
                OutputParser.parseOuput(rr.getMStdout()) ;

                //Certs are validated and Save the uploaded certificate to the LDAP
                String [] zimbraSSLCertificate =  {
                        ZimbraCertMgrExt.A_zimbraSSLCertificate, completeCertChain.toString()};

                ZimbraLog.security.debug("Save complete cert chain to " +  ZimbraCertMgrExt.A_zimbraSSLCertificate +
                    completeCertChain.toString()) ;

                if (isAllServer) {
                    prov.modifyAttrs(prov.getConfig(),
                        StringUtil.keyValueArrayToMultiMap(zimbraSSLCertificate, 0), true);
                }   else {
                    prov.modifyAttrs(server,
                        StringUtil.keyValueArrayToMultiMap(zimbraSSLCertificate, 0), true);
                }

                
            }catch (IOException ioe) {
                throw ServiceException.FAILURE("IOException occurred while running cert verification command", ioe);
            }
        } catch (IOException ioe) {
            throw ServiceException.FAILURE("IOException while handling uploaded certificate", ioe);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ioe) {
                    ZimbraLog.security.warn("exception closing uploaded certificate:", ioe);
                }
            }

            //delete the key file

            try {
                File comm_priv = new File (ZimbraCertMgrExt.SAVED_COMM_KEY_FROM_LDAP)  ;
                if (!comm_priv.delete()) {
                     throw new SecurityException ("Deleting temporary private key file failed.")  ;
                }
            }catch (SecurityException se) {
                ZimbraLog.security.error ("File " + ZimbraCertMgrExt.SAVED_COMM_KEY_FROM_LDAP + " was not deleted", se ) ;
            }
        }


        return true ;
    }
    
    @Override
    public void docRights(List<AdminRight> relatedRights, List<String> notes) {
    	relatedRights.add(Admin.R_installCertificate);
    }
}
