<%@ page language="java" import="java.security.MessageDigest" %>
<%@ page language="java" import="java.security.NoSuchAlgorithmException" %>
<%@ page language="java" import="java.util.ArrayList" %>
<%@ page language="java" import="java.util.Collections" %>
<%@ page language="java" import="java.util.HashMap" %>
<%@ page language="java" import="java.util.List" %>
<%@ page language="java" import="java.util.Map" %>
<%--
/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
 */

//Author: Raja Rao DV (rrao@zimbra.com)
--%>


<%
    String FB_API_KEY = "7af6d68c9d453e392c1ed68b27f95506";//facebook api key(this must match social_facebook_api_key variable in config_template xml file)
    String FB_SECRET_KEY = "498158ba928b7f41ec0f5038f8f3bdd9";//facebook secret key
    Map<String, String> map = new HashMap<String, String>();
    String method = request.getParameter("method");
    String auth_token = request.getParameter("auth_token");
    map.put("api_key", FB_API_KEY);
    map.put("v", "1.0");
    map.put("format", "json");
    if (method.contains("createToken")) {
        map.put("method", "Auth.createToken");
    } else if (method.contains("getSession")) {
        map.put("method", "auth.getSession");
        map.put("auth_token", auth_token);
    }
    String sig = generateSignature(map, FB_SECRET_KEY);
    String resp = "{"
            + "\"method\": \"" + method + "\","
            + "\"signature\": \"" + sig + "\","
            + "\"auth_token\": \"" + auth_token + "\""
            + "}";
%>
<%=  resp %>

<%! public String md5Encode(String params) {
    try {
        MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        StringBuffer sig = new StringBuffer();
        for (byte b : md.digest(params.getBytes())) {
            sig.append(Integer.toHexString((b & 0xf0) >>> 4));
            sig.append(Integer.toHexString(b & 0x0f));
        }
        return sig.toString();
    } catch (NoSuchAlgorithmException ex) {
        throw new RuntimeException(
                "JDK installation problem; MD5 not found.");
    }
}
%>


<%! public String generateSignature(Map<String, String> params,
                                    String secretKey) {
    StringBuffer sb = new StringBuffer();
    List<String> keys = new ArrayList<String>(params.keySet());
    Collections.sort(keys);
    for (String key : keys) {
        String value = params.get(key);
        sb.append(key);
        sb.append("=");
        sb.append(value);
    }
    sb.append(secretKey);
    String p = sb.toString();
    return md5Encode(p);
}
%>