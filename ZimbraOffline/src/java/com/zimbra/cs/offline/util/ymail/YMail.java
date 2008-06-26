package com.zimbra.cs.offline.util.ymail;

import com.yahoo.mail.YmwsPortType;
import com.yahoo.mail.Ymws;
import com.yahoo.mail.UserData;
import com.zimbra.cs.offline.util.yauth.Auth;
import com.zimbra.cs.offline.util.yauth.RawAuth;
import com.zimbra.cs.offline.OfflineLC;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.MessageContext;
import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Arrays;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public final class YMail {
    private static final String ENDPOINT_URL =
        "http://mail.yahooapis.com/ws/mail/v1.1/soap";

    public static YmwsPortType getStub(Auth auth) {
        Ymws service = new Ymws();
        YmwsPortType stub = service.getYmws();
        Map<String, Object> rc = ((BindingProvider) stub).getRequestContext();
        rc.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, getEndpoint(auth));
        rc.put(MessageContext.HTTP_REQUEST_HEADERS, getHeaders(auth));
        return stub;
    }

    private static String getEndpoint(Auth auth) {
        try {
            return ENDPOINT_URL +
                "?appid=" + URLEncoder.encode(auth.getAppId(), "UTF-8") +
                "&wssid=" + URLEncoder.encode(auth.getWSSID(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("Encoding error", e);
        }
    }

    private static Map<String, List<String>> getHeaders(Auth auth) {
        Map<String, List<String>> headers = new HashMap<String, List<String>>();
        headers.put("Cookie", Arrays.asList(auth.getCookie()));
        return headers;
    }
}
