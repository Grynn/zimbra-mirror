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
import java.util.Date;
import java.text.DateFormat; 
import java.text.ParseException; 
import java.text.SimpleDateFormat; 

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
   		Element response = lc.createElement(ZimbraCertMgrService.VERIFY_CERTKEY_RESPONSE);

		String timeStamp = getCurrentTimeStamp();
		String storedPath = ZimbraCertMgrExt.COMM_CRT_KEY_DIR + "." + timeStamp + "/";
		String keyFile = storedPath + ZimbraCertMgrExt.COMM_CRT_KEY_FILE_NAME;
		String certFile = storedPath + ZimbraCertMgrExt.COMM_CRT_FILE_NAME;
		String caFile = storedPath + ZimbraCertMgrExt.COMM_CRT_CA_FILE_NAME;

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

			if(certBuffer_t.length() == 0 || prvkeyBuffer_t.length() == 0) {
				// invalid certificate or privkey, return invalid
				response.addAttribute("verifyResult", "invalid");
				return response;
			}

			byte [] certByte = certBuffer_t.getBytes();

                        File comm_path = new File(storedPath);
                        if(!comm_path.exists()) {
				comm_path.mkdirs();
			} else if(!comm_path.isDirectory()) {
                                throw ServiceException.FAILURE("IOException occurred: Now exist directory '" + ZimbraCertMgrExt.COMM_CRT_KEY_DIR + "'", null);
                        }
			ByteUtil.putContent(certFile, certByte);
			ByteUtil.putContent(caFile, certByte);
			
			byte [] prvkeyByte = prvkeyBuffer_t.getBytes();
			ByteUtil.putContent(keyFile, prvkeyByte) ;
		
		
			String cmd = ZimbraCertMgrExt.VERIFY_COMM_CRTKEY_CMD + " comm "
				+ " " + keyFile + " " + certFile + " " + caFile;
			
			RemoteResult rr = rmgr.execute(cmd);
			verifyResult = OutputParser.parseVerifyResult(rr.getMStdout());
			ZimbraLog.security.info(" GetVerifyCertResponse:" + verifyResult);
		}catch (IOException ioe) {
			throw ServiceException.FAILURE("IOException occurred while running cert verification command", ioe);
		}

            	try {

                	File comm_priv = new File (keyFile);
	                if (!comm_priv.delete()) {
        	             throw new SecurityException ("Deleting commercial private key file failed.")  ;
                	}
                        File comm_cert = new File (certFile);
                        if (!comm_cert.delete()) {
                             throw new SecurityException ("Deleting commercial certificate file failed.")  ;
                        }
                        File comm_ca = new File (caFile);
                        if (!comm_ca.delete()) {
                             throw new SecurityException ("Deleting commercial CA certificate file failed.")  ;
                        }

			File comm_path = new File(storedPath);
			if(!comm_path.delete()) {
			     throw new SecurityException ("Deleting directory of certificate/key failed.")  ;
			}

 	        }catch (SecurityException se) {
        	        ZimbraLog.security.error ("File(s) of commercial certificates/prvkey was not deleted", se ) ;
            	}

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
	
	private String getCurrentTimeStamp() {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd.HHmmss.SSS");
		return 	fmt.format(new Date());
	}
}


