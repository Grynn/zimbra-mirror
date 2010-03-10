package com.zimbra.examples.extns.samlprovider;

import com.zimbra.common.localconfig.LC;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.soap.SoapProtocol;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.service.AuthProvider;
import com.zimbra.cs.service.AuthProviderException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.dom4j.Namespace;
import org.dom4j.QName;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author vmahajan
 */
public class SamlAuthProvider extends AuthProvider {

    private static final Namespace SAML_PROTOCOL_NS = new Namespace("samlp", "urn:oasis:names:tc:SAML:2.0:protocol");

    protected SamlAuthProvider() {
        super("SAML_AUTH_PROVIDER");
    }

    /**
     * Returns an AuthToken by auth data in http request
     * <p/>
     * Should never return null.
     * Throws AuthProviderException.NO_AUTH_TOKEN if auth data for the provider is not present
     * Throws AuthTokenException if auth data for the provider is present but cannot be resolved into a valid AuthToken
     *
     * @param req
     * @param isAdminReq
     * @return
     * @throws com.zimbra.cs.account.AuthTokenException
     *
     */
    protected AuthToken authToken(HttpServletRequest req, boolean isAdminReq) throws AuthProviderException, AuthTokenException {
        throw AuthProviderException.NO_AUTH_DATA();
    }

    /**
     * Returns an AuthToken by auth data in http request
     * <p/>
     * Should never return null.
     * Throws AuthProviderException.NO_AUTH_TOKEN if auth data for the provider is not present
     * Throws AuthTokenException if auth data for the provider is present but cannot be resolved into a valid AuthToken
     *
     * @param soapCtxt
     * @param engineCtxt
     * @return
     * @throws com.zimbra.cs.account.AuthTokenException
     *
     */
    protected AuthToken authToken(Element soapCtxt, Map engineCtxt) throws AuthProviderException, AuthTokenException {

        if (soapCtxt == null)
            throw AuthProviderException.NO_AUTH_DATA();

        Element authTokenElt;
        String type;
        try {
            authTokenElt = soapCtxt.getElement("authToken");
            if (authTokenElt == null)
                throw AuthProviderException.NO_AUTH_DATA();
            type = authTokenElt.getAttribute("type");
        } catch (AuthProviderException ape) {
            throw ape;
        } catch (ServiceException se) {
            ZimbraLog.extensions.error(SystemUtil.getStackTrace(se));
            throw AuthProviderException.NO_AUTH_DATA();
        }
        if (!"SAML_AUTH_PROVIDER".equals(type)) {
            throw AuthProviderException.NOT_SUPPORTED();
        }

        String samlAssertionId = authTokenElt.getTextTrim();
        if (samlAssertionId == null || "".equals(samlAssertionId))
            throw AuthProviderException.NO_AUTH_DATA();

        String samlAuthorityUrl = LC.get("saml_authority_url");
        if (samlAuthorityUrl == null)
            throw new AuthTokenException("SAML authority URL has not been specified in localconfig.zml");

        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(samlAuthorityUrl);
        Element samlAssertionReq = getSamlAssertionRequest(samlAssertionId);
        String samlAssertionReqStr = samlAssertionReq.toString();
        if (ZimbraLog.extensions.isDebugEnabled()) {
            ZimbraLog.extensions.debug("SAML assertion request: " + samlAssertionReqStr);
        }
        try {
            post.setRequestEntity(new StringRequestEntity(samlAssertionReqStr, "text/xml", "utf-8"));
            client.executeMethod(post);
            Element samlResp = Element.parseXML(post.getResponseBodyAsStream());
            Element soapBody = samlResp.getElement("Body");
            Element responseElt = soapBody.getElement("Response");
            Element samlAssertionElt = responseElt.getElement("Assertion");
            if (samlAssertionElt == null) {
                throw new AuthTokenException("SAML response does not contain a SAML token");
            }
            return new SamlAuthToken(samlAssertionElt);
        } catch (AuthTokenException ate) {
            throw ate;
        } catch (Exception e) {
            ZimbraLog.extensions.error(SystemUtil.getStackTrace(e));
            throw new AuthTokenException("Exception in executing SAML assertion request", e);
        }
    }

    private static Element getSamlAssertionRequest(String samlAssertionId) {
        Element envelope = new Element.XMLElement(new QName("Envelope", SoapProtocol.Soap11.getNamespace()));
        Element body = envelope.addElement(new QName("Body", SoapProtocol.Soap11.getNamespace()));
        Element requestElt = body.addElement(new QName("AssertionIDRequest", SAML_PROTOCOL_NS));
        Date now = new Date();
        requestElt.addAttribute("ID", "id-" + now.getTime());
        requestElt.addAttribute("Version", "2.0");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        requestElt.addAttribute("IssueInstant", dateFormat.format(now));
        Element idRefElt = requestElt.addElement(new QName("AssertionIDRef", SAML_PROTOCOL_NS));
        idRefElt.addText(samlAssertionId);
        return envelope;
    }
}
