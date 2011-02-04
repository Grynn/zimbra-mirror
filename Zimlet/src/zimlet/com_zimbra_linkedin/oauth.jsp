<%--

 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
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

//Author: Raja Rao DV (rrao@zimbra.com): modified the below code to jsp format from java
--%>
<%--
Copyright (c) 2007-2009, Yusuke Yamamoto
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
    * Redistributions of source code must retain the above copyright
      notice, this list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright
      notice, this list of conditions and the following disclaimer in the
      documentation and/or other materials provided with the distribution.
    * Neither the name of the Yusuke Yamamoto nor the
      names of its contributors may be used to endorse or promote products
      derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY Yusuke Yamamoto ``AS IS'' AND ANY
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL Yusuke Yamamoto BE LIABLE FOR ANY
DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
(INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
--%>
<%@ page language="java" import="java.io.UnsupportedEncodingException" %>
<%@ page language="java" import="java.net.URLDecoder" %>
<%@ page language="java" import="java.net.URLEncoder" %>
<%@ page language="java" import="java.security.InvalidKeyException" %>
<%@ page language="java" import="java.security.NoSuchAlgorithmException" %>
<%@ page language="java" import="java.util.HashMap" %>
<%@ page language="java" import="java.util.Iterator" %>
<%@ page language="java" import="java.util.Map" %>
<%@ page language="java" import="java.util.TreeMap" %>
<%@ page language="java" import="java.util.Random" %>
<%@ page language="java" import="javax.crypto.Mac" %>
<%@ page language="java" import="javax.crypto.spec.SecretKeySpec" %>


<%
    Random RAND = new Random();
    String consumerKey = "QVzR2KallP5LDZcmCUfX3cuZd7nBGSLSb4FNQk3mQXGvz4MAlQWXDj3S3w8S-jnk";
    String consumerSecret = "qQD8fZqU2DCkGXcDn70ntbiq1ac6i7VKkebGYvOfMLGvw8ajoCA-96OJnBwV_qO2"; 
    String consumerKeyZD = "QVzR2KallP5LDZcmCUfX3cuZd7nBGSLSb4FNQk3mQXGvz4MAlQWXDj3S3w8S-jnk";
    String  consumerSecretZD= "qQD8fZqU2DCkGXcDn70ntbiq1ac6i7VKkebGYvOfMLGvw8ajoCA-96OJnBwV_qO2"; 
    long timeStamp = System.currentTimeMillis() / 1000;
    String nonce = request.getParameter("nonce");
	String oauth_callback = request.getParameter("oauth_callback");
    String token = request.getParameter("token");
    String tokenSecret = request.getParameter("tokenSecret");
    String method = request.getParameter("method");
    String url = request.getParameter("url");
	String oauthVerifier = request.getParameter("oauth_verifier");


    String firstName = request.getParameter("first-name");
    String lastName = request.getParameter("last-name");
    String companyName = request.getParameter("company-name");
    String keywords = request.getParameter("keywords");
	String isZD = request.getParameter("isZD");
	if(isZD.equals("true")) {
		consumerKey = consumerKeyZD;
		consumerSecret = consumerSecretZD;
	}
    HashMap<String, String> hm = new HashMap<String, String>();
    if (firstName != null) {
        hm.put("first-name", firstName);
    }
    if (lastName != null) {
        hm.put("last-name", lastName);
    }
    if (companyName != null) {
        hm.put("company-name", companyName);
    }
    if (keywords != null) {
        hm.put("keywords", keywords);
    }


    String sig = generateAuthorizationHeader(method, url, hm, nonce, String.valueOf(timeStamp), token, tokenSecret, consumerKey, consumerSecret, oauthVerifier);
%>

<%=  sig %>

<%!
    public String generateAuthorizationHeader(String method, String url, HashMap<String, String> params, String nonce, String timeStamp, String token, String tokenSecret, String consumerKey, String consumerSecret, String oauthVerifier) {
		HashMap<String, String> authHeaderParams = new HashMap<String, String>(); 
        authHeaderParams.put("oauth_consumer_key", consumerKey);
        authHeaderParams.put("oauth_signature_method", "HMAC-SHA1");
        authHeaderParams.put("oauth_timestamp", timeStamp);
        authHeaderParams.put("oauth_nonce", nonce);
        authHeaderParams.put("oauth_version", "1.0");
        if (token != null) {
            authHeaderParams.put("oauth_token", token);
        }
		if(oauthVerifier != null) {
            authHeaderParams.put("oauth_verifier", oauthVerifier);
		}
		HashMap<String, String> sigParams = new HashMap<String, String>(); 
		sigParams.putAll(params);
		sigParams.putAll(authHeaderParams);
        parseGetParameters(url, sigParams);

        String normalizedParams = normalizeRequestParameters(sigParams);
        StringBuffer base = new StringBuffer(method).append("&").append(
                encode(constructRequestURL(url))).append("&");
        base.append(encode(normalizedParams));
        String oauthBaseString = base.toString();
        String signature = generateSignature(oauthBaseString, tokenSecret, consumerSecret);
        
		authHeaderParams.put("oauth_signature", signature); //add signature to authHeaderParams

		String resp ="OAuth " + encodeParameters(authHeaderParams, ",", true);
/*

        String resp = "{"
                + "\"url\": \"" + url + "\","
				 + "\"oauthBaseString\": \"" + oauthBaseString + "\","
                + "\"params\": \"" + (normalizedParams + "&oauth_signature=" + encode(signature)) + "\","
                + "\"timeStamp\": \"" + timeStamp + "\","
                + "\"signature\": \"" + encode(signature) + "\","
				  + "\"oauthHeader\": \"" + oauthHeader + "\","
                + "\"nonce\": \"" + nonce + "\""

                + "}";
*/				

        return resp;
    }
%>

<%!
    String generateSignature(String data, String tokenSecret, String consumerSecret) {
        byte[] byteHMAC = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec spec;
            if (null == tokenSecret) {
                String oauthSignature = encode(consumerSecret) + "&";
                spec = new SecretKeySpec(oauthSignature.getBytes(), "HmacSHA1");
            } else {
                String oauthSignature = encode(consumerSecret) + "&"
                        + encode(tokenSecret);
                spec = new SecretKeySpec(oauthSignature.getBytes(), "HmacSHA1");
            }
            mac.init(spec);
            byteHMAC = mac.doFinal(data.getBytes());
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException ignore) {
            // should never happen
        }
        return encode(byteHMAC);
    }
%>

<%!
    public String constructRequestURL(String url) {
        int index = url.indexOf("?");
        if (-1 != index) {
            url = url.substring(0, index);
        }
        int slashIndex = url.indexOf("/", 8);
        String baseURL = url.substring(0, slashIndex).toLowerCase();
        int colonIndex = baseURL.indexOf(":", 8);
        if (-1 != colonIndex) {
            // url contains port number
            if (baseURL.startsWith("http://") && baseURL.endsWith(":80")) {
                // http default port 80 MUST be excluded
                baseURL = baseURL.substring(0, colonIndex);
            } else if (baseURL.startsWith("https://")
                    && baseURL.endsWith(":443")) {
                // http default port 443 MUST be excluded
                baseURL = baseURL.substring(0, colonIndex);
            }
        }
        url = baseURL + url.substring(slashIndex);

        return url;
    }
%>

<%!
    public String encode(String value) {
        String encoded = null;
        try {
            encoded = URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException ignore) {
        }
        StringBuffer buf = new StringBuffer(encoded.length());
        char focus;
        for (int i = 0; i < encoded.length(); i++) {
            focus = encoded.charAt(i);
            if (focus == '*') {
                buf.append("%2A");
            } else if (focus == '+') {
                buf.append("%20");
            } else if (focus == '%' && (i + 1) < encoded.length()
                    && encoded.charAt(i + 1) == '7'
                    && encoded.charAt(i + 2) == 'E') {
                buf.append('~');
                i += 2;
            } else {
                buf.append(focus);
            }
        }
        return buf.toString();
    }
%>

<%!
    public String normalizeRequestParameters(
            HashMap<String, String> params) {
        Map<String, String> sortedMap = new TreeMap<String, String>(params);

        return encodeParameters(sortedMap, "&", false);
    }
%>


<%!
    public String encodeParameters(Map<String, String> postParams,
                                   String splitter, boolean quot) {
        StringBuffer buf = new StringBuffer();
        Iterator<Map.Entry<String, String>> it = postParams.entrySet()
                .iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            if (buf.length() != 0) {
				if (quot) {
					buf.append("\"");
				}
                buf.append(splitter);
            }
            buf.append(encode(pairs.getKey())).append("=");
			if (quot) {
					buf.append("\"");
			}
            buf.append(encode(pairs.getValue()));

        }
		if (buf.length() != 0) {
			if (quot) {
				buf.append("\"");
			}
		}
        return buf.toString();
    }
%>

<%!
    public void parseGetParameters(String url,
                                   HashMap<String, String> signatureBaseParams) {
        int queryStart = url.indexOf("?");
        if (-1 != queryStart) {
            String[] queryStrs = url.substring(queryStart + 1).split("&");
            try {
                for (String query : queryStrs) {
                    String[] split = query.split("=");
                    if (split.length == 2) {
                        signatureBaseParams.put(URLDecoder.decode(split[0],
                                "UTF-8"), URLDecoder.decode(split[1], "UTF-8"));
                    } else {
                        signatureBaseParams.put(URLDecoder.decode(split[0],
                                "UTF-8"), "");
                    }
                }
            } catch (UnsupportedEncodingException ignore) {
            }

        }

    }
%>

<%!
    public String encode(byte[] from) {
        char last2byte = (char) Integer.parseInt("00000011", 2);
        char last4byte = (char) Integer.parseInt("00001111", 2);
        char last6byte = (char) Integer.parseInt("00111111", 2);
        char lead6byte = (char) Integer.parseInt("11111100", 2);
        char lead4byte = (char) Integer.parseInt("11110000", 2);
        char lead2byte = (char) Integer.parseInt("11000000", 2);
        char[] encodeTable = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G',
                'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S',
                'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e',
                'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q',
                'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
                '3', '4', '5', '6', '7', '8', '9', '+', '/'};

        StringBuffer to = new StringBuffer((int) (from.length * 1.34) + 3);
        int num = 0;
        char currentByte = 0;
        for (int i = 0; i < from.length; i++) {
            num = num % 8;
            while (num < 8) {
                switch (num) {
                    case 0:
                        currentByte = (char) (from[i] & lead6byte);
                        currentByte = (char) (currentByte >>> 2);
                        break;
                    case 2:
                        currentByte = (char) (from[i] & last6byte);
                        break;
                    case 4:
                        currentByte = (char) (from[i] & last4byte);
                        currentByte = (char) (currentByte << 2);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead2byte) >>> 6;
                        }
                        break;
                    case 6:
                        currentByte = (char) (from[i] & last2byte);
                        currentByte = (char) (currentByte << 4);
                        if ((i + 1) < from.length) {
                            currentByte |= (from[i + 1] & lead4byte) >>> 4;
                        }
                        break;
                }
                to.append(encodeTable[currentByte]);
                num += 6;
            }
        }
        if (to.length() % 4 != 0) {
            for (int i = 4 - to.length() % 4; i > 0; i--) {
                to.append("=");
            }
        }
        return to.toString();
    }
%>	