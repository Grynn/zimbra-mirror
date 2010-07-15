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

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;


public class ZimbraCertMgrService implements DocumentService {
    public static final String NAMESPACE_STR = "urn:zimbraAdmin";
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);
    
    public static final QName INSTALL_CERT_REQUEST = QName.get("InstallCertRequest", NAMESPACE);
    public static final QName INSTALL_CERT_RESPONSE = QName.get("InstallCertResponse", NAMESPACE);
      
    public static final QName GET_CERT_REQUEST = QName.get("GetCertRequest", NAMESPACE);
    public static final QName GET_CERT_RESPONSE = QName.get("GetCertResponse", NAMESPACE);
   
    public static final QName GEN_CSR_REQUEST = QName.get("GenCSRRequest", NAMESPACE);
    public static final QName GEN_CSR_RESPONSE = QName.get("GenCSRResponse", NAMESPACE);
   
    public static final QName GET_CSR_REQUEST = QName.get("GetCSRRequest", NAMESPACE);
    public static final QName GET_CSR_RESPONSE = QName.get("GetCSRResponse", NAMESPACE);
   
      
    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(INSTALL_CERT_REQUEST, new InstallCert());
        dispatcher.registerHandler(GET_CERT_REQUEST, new GetCert());
        dispatcher.registerHandler(GEN_CSR_REQUEST, new GenerateCSR());
        dispatcher.registerHandler(GET_CSR_REQUEST, new GetCSR());
    }
}
