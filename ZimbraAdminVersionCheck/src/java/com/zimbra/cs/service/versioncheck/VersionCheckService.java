package com.zimbra.cs.service.versioncheck;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;

/**
 * @author Greg Solovyev
 */
public class VersionCheckService implements DocumentService {
	public static final String NAMESPACE_STR = "urn:zimbraAdmin";
	
    public static final Namespace NAMESPACE = Namespace.get(NAMESPACE_STR);
    
    public static final QName VC_REQUEST = QName.get("VersionCheckRequest", NAMESPACE);
    public static final QName VC_RESPONSE = QName.get("VersionCheckResponse", NAMESPACE);
	
	public static String VERSION_CHECK_STATUS = "status";
	public static String VERSION_CHECK_CHECK = "check";
	
	
	public void registerHandlers(DocumentDispatcher dispatcher) {
		dispatcher.registerHandler(VC_REQUEST, new VersionCheck());

	}

}
