package com.zimbra.extension;

import org.dom4j.Namespace;
import org.dom4j.QName;

import com.zimbra.common.soap.AccountConstants;
import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;

public class ZimbraHelloWorldService implements DocumentService {
	public static final String E_HELLO_WORLD_REQUEST = "HelloWorldRequest";
	public static final String E_HELLO_WORLD_RESPONSE = "HelloWorldResponse";
	public static final QName HELLO_WORLD_REQUEST = QName.get(E_HELLO_WORLD_REQUEST, AccountConstants.NAMESPACE);
	public static final QName HELLO_WORLD_RESPONSE = QName.get(E_HELLO_WORLD_RESPONSE, AccountConstants.NAMESPACE);
	
	/**
	 * register new SOAP handlers here
	 */
	@Override
	public void registerHandlers(DocumentDispatcher dispatcher) {
		dispatcher.registerHandler(HELLO_WORLD_REQUEST, new HelloWorld());
	}

}
