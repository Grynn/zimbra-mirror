<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2008, 2009 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ page language="java" import="java.util.*, java.io.*, java.net.*" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
	request.setCharacterEncoding("UTF-8");
	String text = request.getParameter("text");
	String lang = request.getParameter("lang");
	String userAgent = request.getParameter("userAgent");

    URL url = new URL("http://babelfish.yahoo.com/translate_txt");
    URLConnection urlConnection = url.openConnection();
    urlConnection.setDoInput(true);
    urlConnection.setDoOutput(true);
    urlConnection.setUseCaches(false);
    urlConnection.setRequestProperty("Host", "babelfish.yahoo.com");
    urlConnection.setRequestProperty("Accept-Charset", "utf-8");
	urlConnection.setRequestProperty("User-Agent", userAgent);
	urlConnection.setRequestProperty("Referer", "http://babelfish.yahoo.com/translate_txt");

    DataOutputStream outStream = new DataOutputStream(urlConnection.getOutputStream());
    String content = "ei=UTF-8&doit=done&fr=bf-res&intl=1&tt=urltext&trtext=" +
		URLEncoder.encode(text, "UTF-8") + "&lp=" + lang + "&btnTrTxt=Translate";
    outStream.writeBytes(content);
    outStream.flush();
    outStream.close();

    DataInputStream inStream = new DataInputStream(urlConnection.getInputStream());
    String str;
    while ((str = inStream.readLine()) != null)
    {
    	out.println(new String(str.getBytes("ISO-8859-1"),"UTF-8"));
    }
    inStream.close();
%>
