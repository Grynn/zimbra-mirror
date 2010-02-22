package com.zimbra.examples.extns.soapservice;

import com.zimbra.soap.DocumentDispatcher;
import com.zimbra.soap.DocumentService;

/**
 * Registers <code>HelloWorld<code> handler with SOAP document dispatcher.
 *
 * @author vmahajan
 */
public class SoapExtnService implements DocumentService {

    /**
     * Registers <code>DocumentHandler<code>.
     *
     * @param dispatcher
     */
    public void registerHandlers(DocumentDispatcher dispatcher) {
        dispatcher.registerHandler(HelloWorld.REQUEST_QNAME, new HelloWorld());
    }
}
