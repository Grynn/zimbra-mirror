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
     * @param request
     * @param context
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
     * @param context
     * @return
     */
    @Override
    public boolean needsAuth(Map<String, Object> context) {
        return false;
    }
}
