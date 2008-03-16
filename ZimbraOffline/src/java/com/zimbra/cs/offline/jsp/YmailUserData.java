package com.zimbra.cs.offline.jsp;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.account.AccountServiceException;
import com.zimbra.cs.account.AccountServiceException.AuthFailedServiceException;
import com.zimbra.cs.offline.OfflineLC;
import com.zimbra.cs.offline.util.yauth.Auth;
import com.zimbra.cs.offline.util.yauth.AuthenticationException;
import com.zimbra.cs.offline.util.yauth.RawAuth;

public class YmailUserData {

	private static String YAUTH_APPID = OfflineLC.zdesktop_yauth_appid.value();
	private static String YMAIL_URI = OfflineLC.zdesktop_ymail_baseuri.value();
	private static String YMAIL_GETUSERDATA_REQUEST = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"urn:yahoo:ymws\"><SOAP-ENV:Body><ns1:GetUserData/></SOAP-ENV:Body></SOAP-ENV:Envelope>";
	private static String YMAIL_PLUS_INDICATOR = "<hasmailplus>true</hasmailplus>";

	private static Map<String, String> tokenMap = Collections.synchronizedMap(new HashMap<String, String>());
	
	private boolean isPlus;
	
	public YmailUserData(String username, String password) throws AuthenticationException, IOException {
		String token = null;
		synchronized (tokenMap) {
			token = tokenMap.get(username + "/" + password);
		}
		if (token == null) {
			token = RawAuth.getToken(YAUTH_APPID, username, password);
			synchronized (tokenMap) {
				tokenMap.put(username + "/" + password, token);
			}
		}
		
		Auth auth = null;
		try {
			auth = RawAuth.authenticate(YAUTH_APPID, token);
		} catch (AuthenticationException x) {
			//token revoked?
			token = RawAuth.getToken(YAUTH_APPID, username, password);
			synchronized (tokenMap) {
				tokenMap.put(username + "/" + password, token);
			}
			auth = RawAuth.authenticate(YAUTH_APPID, token);
		}
		getUserData(auth);
	}
	
	private void getUserData(Auth auth) throws IOException {
        PostMethod method = new PostMethod(YMAIL_URI);
        NameValuePair[] nvps = new NameValuePair[2];
        nvps[0] = new NameValuePair("appid", YAUTH_APPID);
        nvps[1] = new NameValuePair("WSSID", auth.getWSSID());
        method.setQueryString(nvps);
        method.addRequestHeader("Cookie", auth.getCookie());
        try {
        	method.setRequestEntity(new StringRequestEntity(YMAIL_GETUSERDATA_REQUEST, "application/soap+xml", "utf-8"));
        } catch (UnsupportedEncodingException x) {
        	throw new IOException(x.getMessage());
        }
        
        int code = new HttpClient().executeMethod(method);
        if (code != 200) {
            throw new HttpException("HTTP request failed: " + code + ": " + HttpStatus.getStatusText(code));
        }
        
        String resp = method.getResponseBodyAsString();
        if (resp.toLowerCase().indexOf(YMAIL_PLUS_INDICATOR) > 0)
        	isPlus = true;
	}
	
	public boolean isPlus() {
		return isPlus;
	}
	
    public static void checkYmailPlusStatus(String username, String password) throws ServiceException {
		try {
			YmailUserData yud = new YmailUserData(username, password);
			if (!yud.isPlus())
				throw AccountServiceException.ACCOUNT_INACTIVE(username);
		} catch (AuthenticationException x) {
			throw AuthFailedServiceException.AUTH_FAILED(username, x.getMessage(), x);
		} catch (IOException x) {
			throw ServiceException.FAILURE("error communicating with server", x);
		}
    }
	
	public static void main(String[] args) throws Exception {
		System.out.println("isPlus=" + new YmailUserData("jmehoo", "test1234").isPlus());
	}
}