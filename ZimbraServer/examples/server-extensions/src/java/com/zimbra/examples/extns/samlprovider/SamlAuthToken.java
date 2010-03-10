package com.zimbra.examples.extns.samlprovider;

import com.zimbra.common.auth.ZAuthToken;
import com.zimbra.common.service.ServiceException;
import com.zimbra.common.soap.Element;
import com.zimbra.common.util.SystemUtil;
import com.zimbra.common.util.ZimbraLog;
import com.zimbra.cs.account.Account;
import com.zimbra.cs.account.AuthToken;
import com.zimbra.cs.account.AuthTokenException;
import com.zimbra.cs.account.Provisioning;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpState;

import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/**
 * @author vmahajan
 */
public class SamlAuthToken extends AuthToken {

    private String id;
    private String subjectNameId;
    private Date expires;

    public SamlAuthToken(Element samlAssertionElt) throws AuthTokenException {
        Element authnStmtElt;
        try {
            id = samlAssertionElt.getAttribute("ID");
            Element subjectElt = samlAssertionElt.getElement("Subject");
            Element nameIdElt = subjectElt.getElement("NameID");
            subjectNameId = nameIdElt.getTextTrim();
            Element conditionsElt = samlAssertionElt.getElement("Conditions");
            String notOnOrAfter = conditionsElt.getAttribute("NotOnOrAfter");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            expires = dateFormat.parse(notOnOrAfter);
            authnStmtElt = samlAssertionElt.getElement("AuthnStatement");
        } catch (Exception e) {
            ZimbraLog.extensions.error(e);
            throw new AuthTokenException("Error in parsing SAML auth token", e);
        }
        if (authnStmtElt == null)
            throw new AuthTokenException("SAML auth token does not contain any authentication statement");
    }

    public String toString() {
        return "SAML Auth Token(ID=" + id + ",NameID=" + subjectNameId + ")";
    }

    public String getAccountId() {
        Provisioning prov = Provisioning.getInstance();
        Account acct;
        try {
            acct = prov.get(Provisioning.AccountBy.name, subjectNameId);
        } catch (ServiceException e) {
            ZimbraLog.extensions.error(SystemUtil.getStackTrace(e));
            return null;
        }
        if (acct != null)
            return acct.getId();
        return null;
    }

    public String getAdminAccountId() {
        return null;
    }

    public long getExpires() {
        return expires.getTime();
    }

    public boolean isExpired() {
        return ! new Date().before(expires);
    }

    public boolean isAdmin() {
        return false;
    }

    public boolean isDomainAdmin() {
        return false;
    }

    public boolean isDelegatedAdmin() {
        return false;
    }

    public boolean isZimbraUser() {
        return true;
    }

    public String getExternalUserEmail() {
        return null;
    }

    public String getDigest() {
        return null;
    }

    public String getCrumb() throws AuthTokenException {
        return null;
    }

    /**
     * Encode original auth info into an outgoing http request.
     *
     * @param client
     * @param method
     * @param isAdminReq
     * @param cookieDomain
     * @throws com.zimbra.common.service.ServiceException
     *
     */
    public void encode(HttpClient client, HttpMethod method, boolean isAdminReq, String cookieDomain) throws ServiceException {
    }

    /**
     * Encode original auth info into an outgoing http request cookie.
     *
     * @param state
     * @param isAdminReq
     * @param cookieDomain
     * @throws com.zimbra.common.service.ServiceException
     *
     */
    public void encode(HttpState state, boolean isAdminReq, String cookieDomain) throws ServiceException {
    }

    /**
     * Encode original auth info into an HttpServletResponse
     *
     * @param resp
     * @param isAdminReq
     */
    public void encode(HttpServletResponse resp, boolean isAdminReq, boolean secureCookie) throws ServiceException {
    }

    public void encodeAuthResp(Element parent, boolean isAdmin) throws ServiceException {
    }

    public ZAuthToken toZAuthToken() throws ServiceException {
        Map<String,String> attrs = new HashMap<String, String>();
        attrs.put("ID", id);
        return new ZAuthToken("SAML_AUTH_PROVIDER", null, attrs);
    }

    public String getEncoded() throws AuthTokenException {
        return null;
    }
}
