/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2007, 2008, 2009, 2010, 2011 Zimbra, Inc.
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

import com.zimbra.common.soap.CertMgrConstants;
import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;


public class ZimbraCertMgrService implements DocumentService {

    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(CertMgrConstants.INSTALL_CERT_REQUEST, new InstallCert());
        dispatcher.registerHandler(CertMgrConstants.GET_CERT_REQUEST, new GetCert());
        dispatcher.registerHandler(CertMgrConstants.GEN_CSR_REQUEST, new GenerateCSR());
        dispatcher.registerHandler(CertMgrConstants.GET_CSR_REQUEST, new GetCSR());
	    dispatcher.registerHandler(CertMgrConstants.VERIFY_CERTKEY_REQUEST, new VerifyCertKey());
        dispatcher.registerHandler(CertMgrConstants.UPLOAD_DOMCERT_REQUEST, new UploadDomCert());
        dispatcher.registerHandler(CertMgrConstants.UPLOAD_PROXYCA_REQUEST, new UploadProxyCA());
    }
}
