<!--
***** BEGIN LICENSE BLOCK *****
Zimbra Collaboration Suite Web Client
Copyright (C) 2005, 2006, 2007, 2010 Zimbra, Inc.

The contents of this file are subject to the Zimbra Public License
Version 1.3 ("License"); you may not use this file except in
compliance with the License.  You may obtain a copy of the License at
http://www.zimbra.com/license.

Software distributed under the License is distributed on an "AS IS"
basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
***** END LICENSE BLOCK *****
-->
<html xmlns="http://www.w3.org/1999/xhtml"><!-- -*- nxml -*- -->
<%
   String vers = (String)request.getAttribute("version");
   String ext = (String)request.getAttribute("fileExtension");
   String contextPath = (String)request.getContextPath();
   if (vers == null){
      vers = "";
   }
   if (ext == null){
      ext = "";
   }
%>
  <head>
    <title>Test ZmSugarCrm</title>
    <script type="text/javascript" src="<%= contextPath %>/messages/I18nMsg,AjxMsg,ZMsg,ZmMsg.js<%= ext %>?v=<%= vers %>"></script>
    <jsp:include page="../../public/Ajax.jsp" />
    <script type="text/javascript" src="<%= contextPath %>/js/ajax/core/AjxSoapDoc.js<%= ext %>?v=<%= vers %>"></script>
    <script type="text/javascript" src="<%= contextPath %>/js/ajax/util/AjxMD5.js<%= ext %>?v=<%= vers %>"></script>
    <script type="text/javascript" src="<%= contextPath %>/zimlets/sugarcrm/sugarcrm.js<%= ext %>?v=<%= vers %>"></script>
  <script type="text/javascript">

DBG = new AjxDebug(AjxDebug.NONE, null, false);
var server, user, passwd, sugar;

function getValues() {
	server = document.getElementById("server").value;
	user = document.getElementById("user").value;
	passwd = document.getElementById("passwd").value;
	if (!sugar)
		sugar = new ZmSugarCrm(server, callback);
}

function callback(args) {
	var answer = this.answer;
	switch (this.method) {
	    case "login":
		if (answer.error.number == 0) {
			document.getElementById("session-id").innerHTML = "Logged in: " + answer.id;
			break;
		}
		alert(answer.error.description);
		// continues...
	    case "logout":
		document.getElementById("session-id").innerHTML = "";
		break;

	    case "test":
		alert("Returned string: " + answer);
		break;

	    case "search":
		var contacts = answer.contacts;
		var el = document.getElementById("search-results");
		if (contacts.length) {
			el.innerHTML = "<p>";
			for (var i = 0; i < contacts.length; ++i) {
				var c = contacts[i];
				el.innerHTML += c.type + " :: " + "<tt>" + c.email_address + "</tt> / " +
					c.name1 + " " + c.name2 + "<br />---- <tt>" + c.id + "</tt><br />";
			}
		} else {
			el.innerHTML = "<p>No search results</p>";
		}
		break;

	    case "create_contact":
	    case "create_lead":
		alert("ID: " + answer);
		break;
	}
}

function testCall() {
	getValues();
	sugar.test(document.getElementById("testStr").value);
}

function testMD5() {
	var str = document.getElementById("md5Str").value;
	alert(AjxMD5.hex_md5(str));
}

function login() {
	try {
		getValues();
		sugar.login(user, passwd);
	} catch(ex) {
		alert(ex.toSource());
	}
}

function logout() {
	try {
		getValues();
		sugar.logout();
	} catch(ex) {
		alert(ex.toSource());
	}
}

function search() {
	try {
		getValues();
		sugar.search(document.getElementById("searchStr").value);
	} catch(ex) {
		alert(ex.toSource());
	}
}

function addContact() {
	try {
		getValues();
		var first_name = document.getElementById("add-contact.first_name").value;
                var last_name = document.getElementById("add-contact.last_name").value;
                var email_address = document.getElementById("add-contact.email_address").value;
                sugar.createContact({ first_name    : first_name,
                                      last_name     : last_name,
                                      email_address : email_address });
	} catch(ex) {
		alert(ex.toSource());
		// alert(ex.toSource());
	}
}

function addLead() {
	try {
		getValues();
		var first_name = document.getElementById("add-contact.first_name").value;
                var last_name = document.getElementById("add-contact.last_name").value;
                var email_address = document.getElementById("add-contact.email_address").value;
                sugar.createLead({ first_name    : first_name,
                                   last_name     : last_name,
                                   email_address : email_address });
	} catch(ex) {
		alert(ex.toSource());
		// alert(ex.toSource());
	}
}

    </script>
  </head>
  <body>
    <h2>Connection</h2>

    Server: <input type="text" id="server" size="100" value="http://work.dynarch.com.d/Sugar/SugarSuite-Full-3.5.0b/soap.php" /><br />
    User: <input type="text" id="user" value="admin" /><br />
    Passwd: <input type="text" id="passwd" value="sugar" />

    <p>
    If you want to change these values, you need to do it first thing after the page finished loading.
    </p>

    <h2>Tests</h2>

    <h3>Test MD5</h3>

    <input type="text" id="md5Str" value="test md5" />
    <button onclick="testMD5()">Test MD5</button>

    <h3>SugarCRM::test()</h3>

    <input type="text" id="testStr" value="test string" />
    <button onclick="testCall()">Test connection</button>

    <h3>SugarCRM::login()</h3>

    <button onclick="login()">Login</button> | <button onclick="logout()">Logout</button> | <span id="session-id"></span>

    <h3>Search</h3>

    <input type="text" id="searchStr" /> <button onclick="search()">Go</button>
    <div id="search-results"></div>

    <h3>Add contact</h3>

    First name: <input type="text" id="add-contact.first_name" /><br />
    Last name: <input type="text" id="add-contact.last_name" /><br />
    Email: <input type="text" id="add-contact.email_address" /><br />
    <button onclick="addContact()">Add contact</button> | <button onclick="addLead()">Add lead</button>
  </body>
</html>
