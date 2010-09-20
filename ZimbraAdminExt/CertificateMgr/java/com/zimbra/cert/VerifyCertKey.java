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




public class VerifyCertKey extends AdminDocumentHandler {
        private final static String CERT = "cert";
        private final static String PRIVKEY = "privkey";
        private final static String TYPE = "type";
        final static String CERT_TYPE_SELF= "self" ;
        final static String CERT_TYPE_COMM = "comm" ;
    	private Provisioning prov = null;
	private boolean verifyResult = false;
   	
   	public Element handle(Element request, Map<String, Object> context) throws ServiceException {
   		ZimbraSoapContext lc = getZimbraSoapContext(context);
   		prov = Provisioning.getInstance();
   		String certBuffer = request.getAttribute(CERT) ;
   		String prvkeyBuffer = request.getAttribute(PRIVKEY) ;
   		
		try {
   			if(certBuffer == null) {
   				throw ServiceException.INVALID_REQUEST("Input Certificate is null", null);
   			}
   			if(prvkeyBuffer == null) {
   				throw ServiceException.INVALID_REQUEST("Input PrivateKey is null",null);
   			}
   			
			//String serverPrvKey = prov.getLocalServer().getAttr(ZimbraCertMgrExt.A_zimbraSSLPrivateKey);
			//ZimbraLog.security.debug(" server prvkey = " + serverPrvKey);

			RemoteManager rmgr = RemoteManager.getRemoteManager(prov.getLocalServer());

			// replace the space character with '\n'
			String certBuffer_t = stringFix(certBuffer,true);
			String prvkeyBuffer_t = stringFix(prvkeyBuffer,false);
			byte [] certByte = certBuffer_t.getBytes();
			ByteUtil.putContent(ZimbraCertMgrExt.COMM_CRT_FILE, certByte);
			ByteUtil.putContent(ZimbraCertMgrExt.COMM_CRT_CA_FILE, certByte);
			
			byte [] prvkeyByte = prvkeyBuffer_t.getBytes();
			ByteUtil.putContent(ZimbraCertMgrExt.COMM_CRT_KEY_FILE, prvkeyByte) ;
		
		
			String cmd = ZimbraCertMgrExt.VERIFY_COMM_CRTKEY_CMD + " comm "
				+ " " + ZimbraCertMgrExt.COMM_CRT_KEY_FILE
				+ " " + ZimbraCertMgrExt.COMM_CRT_FILE
				+ " " + ZimbraCertMgrExt.COMM_CRT_CA_FILE;
				
			RemoteResult rr = rmgr.execute(cmd);
			verifyResult = OutputParser.parseVerifyResult(rr.getMStdout());
		}catch (IOException ioe) {
			throw ServiceException.FAILURE("IOException occurred while running cert verification command", ioe);
		}

            	try {
                	File comm_priv = new File (ZimbraCertMgrExt.COMM_CRT_KEY_FILE);
	                if (!comm_priv.delete()) {
        	             throw new SecurityException ("Deleting commercial private key file failed.")  ;
                	}
                        File comm_cert = new File (ZimbraCertMgrExt.COMM_CRT_FILE);
                        if (!comm_cert.delete()) {
                             throw new SecurityException ("Deleting commercial certificate file failed.")  ;
                        }
                        File comm_ca = new File (ZimbraCertMgrExt.COMM_CRT_CA_FILE);
                        if (!comm_ca.delete()) {
                             throw new SecurityException ("Deleting commercial CA certificate file failed.")  ;
                        }

 	        }catch (SecurityException se) {
        	        ZimbraLog.security.error ("File(s) of commercial certificates/prvkey was not deleted", se ) ;
            	}

 
        	Element response = lc.createElement(ZimbraCertMgrService.VERIFY_CERTKEY_RESPONSE);
		if(verifyResult)
	        	response.addAttribute("verifyResult", "true");
		else response.addAttribute("verifyResult", "false");
	        return response;

  		
   	}

	private String stringFix(String in, boolean isCert) {
		if(in.length() < 0) return new String("");

		String HEADER_CERT = "-----BEGIN CERTIFICATE-----";
		String END_CERT = "-----END CERTIFICATE-----";
		String HEADER_KEY = "-----BEGIN RSA PRIVATE KEY-----";
		String END_KEY = "-----END RSA PRIVATE KEY-----";
		String header, end;
		String out = new String("");;
		
		if(isCert){
			header = HEADER_CERT;
			end = END_CERT;
		}else {
			header = HEADER_KEY;
			end = END_KEY;
		}
			
		String [] strArr = in.split(end);
		for(int i = 0; i < strArr.length; i++){
			int l = strArr[i].indexOf(header);
			if(l == -1) continue;
			String subStr = strArr[i].substring(l + header.length());
			String repStr = subStr.replace(' ','\n');
			out += (header + repStr + end + "\n");
		}
		return out;
		
	}
}


