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
   
      
    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(INSTALL_CERT_REQUEST, new InstallCert());
        dispatcher.registerHandler(GET_CERT_REQUEST, new GetCert());
    }
  
}
