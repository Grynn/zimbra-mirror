/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Server
 * Copyright (C) 2011 Zimbra, Inc.
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
package com.zimbra.cs.offline.util.yc.oauth;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.google.gdata.client.authn.oauth.OAuthUtil;
import com.google.gdata.util.common.util.Base64;

public class OAuthHelper {

    private static final String UTF_8 = "UTF-8";
    private static final String PREAMBLE = "OAuth ";

    public static String extractHeader(Map<String, String> parameters) throws OAuthException {
        StringBuffer header = new StringBuffer(parameters.size() * 20);
        header.append(PREAMBLE);
        for (String key : parameters.keySet()) {
            if (header.length() > PREAMBLE.length()) {
                header.append(", ");
            }
            try {
                header.append(String.format("%s=\"%s\"", key, URLEncoder.encode(parameters.get(key), UTF_8)));
            } catch (UnsupportedEncodingException e) {
                throw new OAuthException("encoding error when extracting header", "", false, e, null);
            }
        }
        return header.toString();
    }

    public static String getStreamContents(InputStream is) {
        try {
            final char[] buffer = new char[4096];
            StringBuilder out = new StringBuilder();
            Reader in = new InputStreamReader(is, UTF_8);
            int read;
            do {
                read = in.read(buffer, 0, buffer.length);
                if (read > 0) {
                    out.append(buffer, 0, read);
                }
            } while (read >= 0);
            in.close();
            return out.toString();
        } catch (IOException ioe) {
            throw new IllegalStateException("Error while reading response body", ioe);
        }
    }

    public static String getBaseString(Map<String, String> params, String url, String httpMethod, String phase)
            throws OAuthException {
        List<String> keys = new ArrayList<String>(params.keySet());
        Map<String, String> sortedMap = new LinkedHashMap<String, String>();
        Collections.sort(keys);
        for (String key : keys) {
            sortedMap.put(key, params.get(key));
        }
        try {
            return OAuthUtil.getSignatureBaseString(url, httpMethod, sortedMap);
        } catch (com.google.gdata.client.authn.oauth.OAuthException e) {
            throw new OAuthException("Generate base string error at " + phase, "", false, e, null);
        }
    }

    public static String getSignature(String baseString, String consumerSecret, String tokenSecret)
            throws OAuthException {
        try {
            StringBuilder keyString = new StringBuilder();
            keyString.append(OAuthUtil.encode(consumerSecret)).append("&").append(OAuthUtil.encode(tokenSecret));
            SecretKey key = new SecretKeySpec(keyString.toString().getBytes(UTF_8), "HmacSHA1");
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(key);
            return Base64.encode(mac.doFinal(baseString.getBytes(UTF_8)));
        } catch (Exception e) {
            throw new OAuthException("error while generating signature", "", false, e, null);
        }
    }

    public static String getSignatureMethod() {
        return "HMAC-SHA1";
    }
}
