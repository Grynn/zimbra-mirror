package com.zimbra.examples.extns.samlprovider;

import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.extension.ExtensionHttpHandler;
import org.dom4j.Namespace;
import org.dom4j.QName;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author vmahajan
 */
public class SamlRequestHandler extends ExtensionHttpHandler {

    private static final Namespace SAML_PROTOCOL_NS = new Namespace("samlp", "urn:oasis:names:tc:SAML:2.0:protocol");

    private static Map<String, Element> samlAssertionsMap = new HashMap<String, Element>();

    static {
        try {
            Element issuedAssertionsElt = Element.parseXML(new FileInputStream("/opt/zimbra/conf/issued-saml-assertions.xml"));
            List<Element> assertionsList = issuedAssertionsElt.getPathElementList(new String[]{"Assertion"});
            for (Element assertionElt : assertionsList) {
                samlAssertionsMap.put(assertionElt.getAttribute("ID"), assertionElt);
            }
        } catch (Exception e) {
            ZimbraLog.extensions.error(SystemUtil.getStackTrace(e));
            ZimbraLog.extensions.error("Exception in loading issued assertions");
        }
    }

    /**
     * The path under which the handler is registered for an extension.
     *
     * @return
     */
    @Override
    public String getPath() {
        return "/samlAuthority";
    }

    /**
     * Processes HTTP POST requests.
     *
     * @param req
     * @param resp
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        Element respEnv;
        try {
            Element reqEnv = Element.parseXML(req.getInputStream());
            Element reqBody = reqEnv.getElement("Body");
            Element requestElt = reqBody.getElement("AssertionIDRequest");
            Element idRefElt = requestElt.getElement("AssertionIDRef");

            respEnv = new Element.XMLElement(new QName("Envelope", SoapProtocol.Soap11.getNamespace()));
            Element respBody = respEnv.addElement(new QName("Body", SoapProtocol.Soap11.getNamespace()));
            Element respElt = respBody.addElement(new QName("Response", SAML_PROTOCOL_NS));
            Date now = new Date();
            respElt.addAttribute("ID", "id-" + now.getTime());
            respElt.addAttribute("Version", "2.0");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            respEnv.addAttribute("IssueInstant", dateFormat.format(now));
            respElt.addAttribute("InResponseTo", requestElt.getAttribute("ID"));
            Element statusElt = respElt.addElement(new QName("Status", SAML_PROTOCOL_NS));
            Element statusCodeElt = statusElt.addElement(new QName("StatusCode", SAML_PROTOCOL_NS));

            if (requestElt != null) {
                String assertionId = idRefElt.getTextTrim();
                Element assertion = samlAssertionsMap.get(assertionId);
                if (assertion == null) {
                    statusCodeElt.addAttribute("Value", "urn:oasis:names:tc:SAML:2.0:status:Requester");
                    Element statusMsgElt = statusElt.addElement(new QName("StatusMessage", SAML_PROTOCOL_NS));
                    statusMsgElt.addText("No assertion found corresponding to the id: " + assertionId);
                } else {
                    statusCodeElt.addAttribute("Value", "urn:oasis:names:tc:SAML:2.0:status:Success");
                    respElt.addElement(assertion.clone());
                }
            }
        } catch (Exception e) {
            ZimbraLog.extensions.error(SystemUtil.getStackTrace(e));
            throw new IOException(e);
        }

        String respEnvStr = respEnv.toString();
        if (ZimbraLog.extensions.isDebugEnabled())
            ZimbraLog.extensions.debug("SAML response: " + respEnvStr);
        resp.getOutputStream().print(respEnvStr);
    }
}
