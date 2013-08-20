/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */
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
