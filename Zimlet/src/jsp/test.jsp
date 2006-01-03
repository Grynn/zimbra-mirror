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

    <hr /> <z:property zimlet="com_zimbra_sforce" name="passwd" action="set" value="foobar"/>

    <hr /> 
      <h3>Config</h3>
      <table border="1">
        <tr>
          <td>zimlet</td>
          <td>name</td>
          <td>value</td>
        </tr>
        <tr>
          <td>com_zimbra_bugz</td>
          <td>url</td>
          <td><z:zimletconfig zimlet="com_zimbra_bugz" name="url" scope="global"/></td>
        </tr>
        <tr>
          <td>com_zimbra_bugz</td>
          <td>url</td>
          <td><z:zimletconfig zimlet="com_zimbra_bugz" name="url" scope="local"/></td>
        </tr>
        <tr>
          <td>com_zimbra_bugz</td>
          <td>url</td>
          <td><z:zimletconfig zimlet="com_zimbra_bugz" name="url"/></td>
        </tr>
      </table>
  </body>
</html>
