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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.CertMgrConstants;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.ByteUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Provisioning;
import com.zimbra.cs.rmgmt.RemoteManager;
import com.zimbra.cs.rmgmt.RemoteResult;
import com.zimbra.cs.service.admin.AdminDocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;

public class VerifyCertKey extends AdminDocumentHandler {
        final static String CERT_TYPE_SELF= "self" ;
        final static String CERT_TYPE_COMM = "comm" ;
    	private Provisioning prov = null;
	private boolean verifyResult = false;

    @Override
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
   		ZimbraSoapContext lc = getZimbraSoapContext(context);
   		prov = Provisioning.getInstance();
   		String certBuffer = request.getAttribute(CertMgrConstants.E_cert) ;
   		String prvkeyBuffer = request.getAttribute(CertMgrConstants.A_privkey) ;
   		Element response = lc.createElement(CertMgrConstants.VERIFY_CERTKEY_RESPONSE);

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
				response.addAttribute(CertMgrConstants.A_verifyResult, "invalid");
				return response;
			}

			byte [] certByte = certBuffer_t.getBytes();

            File comm_path = new File(storedPath);
            if (!comm_path.exists()) {
                if (!comm_path.mkdirs()) {
                    throw ServiceException.FAILURE("IOException, can't " +
                            "create dir " + comm_path.getAbsolutePath().toString()
                            , null);
                }
            } else if (!comm_path.isDirectory()) {
                throw ServiceException
                        .FAILURE("IOException occurred: Now exist directory '"
                                + ZimbraCertMgrExt.COMM_CRT_KEY_DIR + "'", null);
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
	        	response.addAttribute(CertMgrConstants.A_verifyResult, "true");
		else response.addAttribute(CertMgrConstants.A_verifyResult, "false");
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


