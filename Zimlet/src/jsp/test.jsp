<!--
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2009, 2010 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.3 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * ***** END LICENSE BLOCK *****
-->
<%@ taglib uri="/WEB-INF/zimbra.tld" prefix="z" %>
<html>
  <head>
    <title>Zimbra Tag Library</title>
  </head>
  <body bgcolor="#ffffff">
    <hr /> 
      <h3>Message</h3>
      <table border="1">
        <tr>
          <td>id</td>
          <td>subject</td>
          <td>from</td>
          <td>to</td>
          <td>cc</td>
          <td>bcc</td>
        </tr>
<%
	String msgid;
    String[] msgids = { "488", "489", "405" };
	for (int i = 0; i < msgids.length; i++) {
		msgid = msgids[i];
%>
        <tr>
          <td><%= msgid %></td>
          <td><z:message id='<%= msgid %>' field="subject"/></td>
          <td><z:message id='<%= msgid %>' field="from"/></td>
          <td><z:message id='<%= msgid %>' field="to"/></td>
          <td><z:message id='<%= msgid %>' field="cc"/></td>
          <td><z:message id='<%= msgid %>' field="bcc"/></td>
        </tr>
<%
	}
%>
      </table>

      <h3>Raw Message</h3>
      <table border="1">
        <tr>
          <td><z:message id="488" field="raw"/></td>
        </tr>
      </table>

    <hr /> 
      <h3>Conversation</h3>
      <table border="1">
        <tr>
          <td>cid</td>
          <td>index</td>
          <td>subject</td>
          <td>from</td>
          <td>to</td>
          <td>cc</td>
          <td>bcc</td>
        </tr>
<%
	String cid = "347";
	for (int index = 0; index < 3; index++) {
		String i = Integer.toString(index);
%>
        <tr>
          <td><%= cid %></td>
          <td><z:conversation id='<%= cid %>' index='<%= i %>' field="subject"/></td>
          <td><z:conversation id='<%= cid %>' index='<%= i %>' field="from"/></td>
          <td><z:conversation id='<%= cid %>' index='<%= i %>' field="to"/></td>
          <td><z:conversation id='<%= cid %>' index='<%= i %>' field="cc"/></td>
          <td><z:conversation id='<%= cid %>' index='<%= i %>' field="bcc"/></td>
        </tr>
<%
	}
%>
      </table>

    <hr /> 
      <h3>Contact</h3>
      <table border="1">
        <tr>
          <td>id</td>
          <td>email</td>
          <td>firstName</td>
          <td>lastName</td>
          <td>company</td>
        </tr>
<%
	String id;
    String[] ids = { "268", "281", "264" };
	for (int i = 0; i < ids.length; i++) {
		id = ids[i];
%>
        <tr>
          <td><%= id %></td>
          <td><z:contact id='<%= id %>' field="email"/></td>
          <td><z:contact id='<%= id %>' field="firstName"/></td>
          <td><z:contact id='<%= id %>' field="lastName"/></td>
          <td><z:contact id='<%= id %>' field="company"/></td>
        </tr>
<%
	}
%>
      </table>

    <hr /> 
      <h3>Appointment</h3>
      <table border="1">
        <tr>
          <td>id</td>
          <td>starttime</td>
          <td>endtime</td>
          <td>name</td>
          <td>comment</td>
          <td>status</td>
        </tr>
<%
    String[] apptids = { "481", "483", "485" };
	for (int i = 0; i < apptids.length; i++) {
		id = ids[i];
%>
        <tr>
          <td><%= id %></td>
          <td><z:appointment id='<%= id %>' field="starttime"/></td>
          <td><z:appointment id='<%= id %>' field="endtime"/></td>
          <td><z:appointment id='<%= id %>' field="name"/></td>
          <td><z:appointment id='<%= id %>' field="comment"/></td>
          <td><z:appointment id='<%= id %>' field="status"/></td>
        </tr>
<%
	}
%>
      </table>

    <hr /> 
      <h3>Property</h3>
      <table border="1">
        <tr>
          <td>name</td>
          <td>value</td>
        </tr>
<%
	String key;
    String[] keys = { "user", "passwd" };
	for (int i = 0; i < keys.length; i++) {
		key = keys[i];
%>
        <tr>
          <td><%= key %></td>
          <td><z:property zimlet="com_zimbra_sforce" name='<%= key %>'/></td>
        </tr>
<%
	}
%>
      </table>

    <z:property zimlet="com_zimbra_sforce" name="passwd" action="set" value="foobar"/>

    <hr /> <z:property zimlet="com_zimbra_sforce" action="list" var="prop"/>
      <h3>ListProperty</h3>
      <table border="1">
        <tr>
          <td>name</td>
          <td>value</td>
        </tr>
<%@page import="java.util.Map"%>
<%@page import="java.util.Iterator"%>
<%
	Map prop = (Map) pageContext.getRequest().getAttribute("prop");
	Iterator iter = prop.keySet().iterator();
	while (iter.hasNext()) {
		key = (String)iter.next();
		String val = (String)prop.get(key);
%>
        <tr>
          <td><%= key %></td>
          <td><%= val %></td>
        </tr>
<%
	}
%>
      </table>

    <hr /> 
      <h3>Config</h3>
      <table border="1">
        <tr>
          <td>scope</td>
          <td>name</td>
          <td>value</td>
        </tr>
        <tr>
          <td>global</td>
          <td>url</td>
          <td><z:zimletconfig zimlet="com_zimbra_bugz" name="url" scope="global"/></td>
        </tr>
        <tr>
          <td>local</td>
          <td>url</td>
          <td><z:zimletconfig zimlet="com_zimbra_bugz" name="url" scope="local"/></td>
        </tr>
        <tr>
          <td></td>
          <td>url</td>
          <td><z:zimletconfig zimlet="com_zimbra_bugz" name="url"/></td>
        </tr>
      </table>

    <hr /> <z:zimletconfig zimlet="com_zimbra_bugz" action="list" var="conf"/>
      <h3>ListConfig</h3>
      <table border="1">
        <tr>
          <td>scope</td>
          <td>name</td>
          <td>value</td>
        </tr>
<%
	Map conf = (Map) pageContext.getRequest().getAttribute("conf");
	Map gconf = (Map) conf.get("global");
	Map lconf = (Map) conf.get("local");
	iter = gconf.keySet().iterator();
	while (iter.hasNext()) {
		key = (String)iter.next();
		String val = (String)gconf.get(key);
%>
        <tr>
          <td>global</td>
          <td><%= key %></td>
          <td><%= val %></td>
        </tr>
<%
	}
	iter = lconf.keySet().iterator();
	while (iter.hasNext()) {
		key = (String)iter.next();
		String val = (String)lconf.get(key);
%>
        <tr>
          <td>local</td>
          <td><%= key %></td>
          <td><%= val %></td>
        </tr>
<%
	}
%>
      </table>
  </body>
</html>
