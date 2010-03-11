/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
package com.zimbra.examples.extns.soapservice;

import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.soap.DocumentHandler;
import com.zimbra.soap.ZimbraSoapContext;
import org.dom4j.Namespace;
import org.dom4j.QName;

import java.util.Map;

/**
 * A simple "Hello World" SOAP method implementation.
 *
 * @author vmahajan
 */
public class HelloWorld extends DocumentHandler {

    static QName REQUEST_QNAME = new QName("HelloWorldRequest", Namespace.get("urn:zimbra:examples"));
    static QName RESPONSE_QNAME = new QName("HelloWorldResponse", Namespace.get("urn:zimbra:examples"));

    /**
     * Handles request.
     *
     * @param request request element
     * @param context context map
     * @return response
     * @throws ServiceException
     */
    public Element handle(Element request, Map<String, Object> context) throws ServiceException {
        Element callerElt = request.getElement("caller");
        String caller = callerElt.getTextTrim();

        ZimbraSoapContext zsc = getZimbraSoapContext(context);
        Element response = zsc.createElement(HelloWorld.RESPONSE_QNAME);
        Element replyElt = response.addElement("reply");
        replyElt.setText("Hello " + caller + "!");
        return response;
    }

    /**
     * Returns whether the command's caller must be authenticated.
     *
     * @param context context map
     * @return needs auth or not
     */
    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return false;
    }
}
