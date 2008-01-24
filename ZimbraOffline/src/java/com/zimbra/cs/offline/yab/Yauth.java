package com.zimbra.cs.offline.yab;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;

import com.zimbra.common.service.ServiceException;
import com.zimbra.cs.mailbox.OfflineServiceException;
import com.zimbra.cs.offline.OfflineLC;

public class Yauth {
	
	public String cookie;
	public String wssid;
	public long expiration;
	
	private static final String GET_AUTH_TOKEN = "/get_auth_token";
	private static final String GET_AUTH = "/get_auth";
	
	private static final String LOGIN = "login";
	private static final String PASSWD = "passwd";
	private static final String APPID = "appid";
	private static final String TOKEN = "token";
	
	private static final String AUTHTOKEN_PREFIX = "AuthToken=";
	private static final String COOKIE_PREFIX = "Cookie=";
	private static final String WSSID_PREFIX = "WSSID=";
	private static final String EXPIRATION_PREFIX = "Expiration=";

	public static Yauth authenticate(String yid, String ypw) throws ServiceException {
        GetMethod get = new GetMethod(OfflineLC.zdesktop_yauth_baseuri.value() + GET_AUTH_TOKEN);
        NameValuePair[] nvps = new NameValuePair[3];
        nvps[0] = new NameValuePair(LOGIN, yid);
        nvps[1] = new NameValuePair(PASSWD, ypw);
        nvps[2] = new NameValuePair(APPID, OfflineLC.zdesktop_yauth_appid.value());
        get.setQueryString(nvps);
        
    	String token = doGet(get, yid).trim();
    	if (!token.startsWith(AUTHTOKEN_PREFIX))
			throw OfflineServiceException.UNEXPECTED("get_auth_token bad response: " + token);
    	token = token.substring(AUTHTOKEN_PREFIX.length());
        
		get = new GetMethod(OfflineLC.zdesktop_yauth_baseuri.value() + GET_AUTH);
        nvps = new NameValuePair[2];
        nvps[0] = new NameValuePair(TOKEN, token);
        nvps[1] = new NameValuePair(APPID, OfflineLC.zdesktop_yauth_appid.value());
        get.setQueryString(nvps);
	
        String resp = doGet(get, yid).trim();
        String[] auth = resp.split("\n");
        if (auth == null || auth.length != 3 ||
        		!auth[0].startsWith(COOKIE_PREFIX) || !auth[1].startsWith(WSSID_PREFIX) || !auth[2].startsWith(EXPIRATION_PREFIX))
        	throw OfflineServiceException.UNEXPECTED("get_auth bad response: " + resp);
        
        Yauth yauth = new Yauth();
        yauth.cookie = auth[0].substring(COOKIE_PREFIX.length());
        yauth.wssid = auth[1].substring(WSSID_PREFIX.length());
        yauth.expiration = Long.parseLong(auth[2].substring(EXPIRATION_PREFIX.length())) + System.currentTimeMillis();
        
        return yauth;
	}
	
	private static String doGet(GetMethod get, String yid) throws ServiceException {
		try {
	    	int code = new HttpClient().executeMethod(get);
			if (code == 200) {
				String body = get.getResponseBodyAsString();
				if (body == null || body.length() == 0)
					throw OfflineServiceException.UNEXPECTED("yauth bad response");
				return body;
			} else if (code == 403) {
				String body = get.getResponseBodyAsString();
				throw OfflineServiceException.AUTH_FAILED(yid, body);
			} else
				throw OfflineServiceException.UNEXPECTED("get_auth_token bad response code: " + code);
		} catch (IOException x) {
			throw ServiceException.FAILURE("IOException in yauth", x);
		}
	}
	
	public static void main(String[] args) throws Exception {
		Yauth yauth = authenticate(args[0], args[1]);
		System.out.println("cookie=" + yauth.cookie);
		System.out.println("wssid=" + yauth.wssid);
		System.out.println("expiration=" + yauth.expiration);
	}
}
