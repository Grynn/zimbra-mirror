/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.1
 *
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.1 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 *
 * The Original Code is: Zimbra Collaboration Suite.
 *
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

////////////////////////////////////////////////////////////////
///  Zimlet to handle integration with a SalesForce account  ///
///  @author Mihai Bazon, <mihai@zimbra.com>                 ///
////////////////////////////////////////////////////////////////
function Com_Zimbra_SForce() {
	// that's for good practice, though useless for now
	ZmZimletBase.call(this);

	// Let the framework know that we want to handle 500 and other errors
	// ourselves (in particular, for a simple "login failure" the SForce
	// server likes to return 500.  This IMO is dumb, but whatever.
	//this._passRpcErrors = true;

	this.SERVER = Com_Zimbra_SForce.LOGIN_SERVER;

	// we MUST use the enterprise URN in order for certain "advanced"
	// features such as adding a contact to an account
	this.XMLNS = "urn:enterprise.soap.sforce.com";
};

/// Zimlet handler objects, such as Com_Zimbra_SForce, must inherit from
/// ZmZimletBase.  The 2 lines below achieve this.
Com_Zimbra_SForce.prototype = new ZmZimletBase;
Com_Zimbra_SForce.prototype.constructor = Com_Zimbra_SForce;

/// Store the default SOAP server.  Note that after a successful login, the URL
/// may change--which is why we store it in an object instance too (this.XMLNS)
Com_Zimbra_SForce.LOGIN_SERVER = "https://www.salesforce.com/services/Soap/u/6.0";

// SOAP utils

/// Utility function that creates a SOAP envelope.  This will also insert the
/// session header if we already have a session.
Com_Zimbra_SForce.prototype._makeEnvelope = function(method) {
	var soap = AjxSoapDoc.create(
		method, this.XMLNS, null,
		"http://schemas.xmlsoap.org/soap/envelope/");
	var envEl = soap.getDoc().firstChild;
	// Seems we need to set these or otherwise will get a "VersionMismatch"
	// message from SForce
	envEl.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
	envEl.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
	if (this.sessionId) {
		var header = soap.ensureHeader();
		var sessionEl = soap.getDoc().createElement("SessionHeader");
		header.appendChild(sessionEl);
		sessionEl.setAttribute("xmlns", this.XMLNS);
		soap.set("sessionId", this.sessionId, sessionEl);
	}
	return soap;
};

Com_Zimbra_SForce.prototype.xmlToObject = function(result) {
	try {
		// var xd = new AjxXmlDoc.createFromXml(xml).toJSObject(true, false);
		var xd = new AjxXmlDoc.createFromDom(result.xml).toJSObject(true, false);
	} catch(ex) {
		alert(ex.dump() + "\n" + ex);
	}
	return xd;
};

/// Utility function that calls the SForce server with the given SOAP data
Com_Zimbra_SForce.prototype.rpc = function(soap, callback) {
	this.sendRequest(soap, this.SERVER, { SOAPAction: "m" }, callback);
};

// SOAP METHOD: login

/// Login to SForce.  The given callback will be called in the case of a
/// successful login.  Note that callback is a plain function (not AjxCallback)
Com_Zimbra_SForce.prototype.login = function(callback) {
	var soap = this._makeEnvelope("login");
	soap.set("username", this.getUserProperty("user"));
	soap.set("password", this.getUserProperty("passwd"));
	if (callback == null)
		callback = false;
	this.rpc(soap, new AjxCallback(this, this.done_login, callback));
};

Com_Zimbra_SForce.prototype.done_login = function(params) {
	var callback = params[0];
	var result = params[1];
	var ans = this.xmlToObject(result);
	if (ans.Body.loginResponse) {
		ans = ans.Body.loginResponse.result;
		this.SERVER = String(ans.serverUrl);
		this.sessionId = String(ans.sessionId);
		this.userId = String(ans.userId);
		this.userInfo = ans.userInfo;
		//alert(this.userInfo.toSource());
		this.displayStatusMessage("SForce: " + this.userInfo.userFullName + " logged in.");
		if (callback)
			callback.call(this);
	} else {
		ans = ans.Body.Fault;
		if (ans) {
			this.displayErrorMessage("<b>Login to SalesForce failed:</b><br />&nbsp;&nbsp;&nbsp;&nbsp;"
						 + ans.faultstring + "</b><br />"
						 + "Please review your preferences.");
		}
	}
};

// SOAP METHOD: logout

/// There's no explicit logout command to SForce, we just clear session data
/// and user information.
Com_Zimbra_SForce.prototype.logout = function() {
	this.SERVER = Com_Zimbra_SForce.LOGIN_SERVER;
	this.sessionId = null;
	this.userId = null;
	this.userInfo = null;
};

// SOAP METHOD: query

/// Executes a SOQL (SalesForce Object Query Language) and calls the given
/// callback upon successful execution.
Com_Zimbra_SForce.prototype.query = function(query, limit, callback) {
	if (typeof limit == "undefined")
		limit = 1;
	var soap = this._makeEnvelope("query");
	soap.set("queryString", query);
	var qo = soap.set("QueryOptions", { batchSize : limit },
			  soap.ensureHeader());
	qo.setAttribute("xmlns", this.XMLNS);
	// we sure have a lot of indirection going on..
	this.rpc(soap, new AjxCallback(this, this.done_query, callback));
};

Com_Zimbra_SForce.__query_result_get = function() {
	for (var i = 0; i < arguments.length; ++i) {
		var attr = arguments[i];
		if (this[attr] != null) {
			return this[attr].toString();
		}
	}
	return "";
};

Com_Zimbra_SForce.prototype.done_query = function(params) {
	var callback = params[0];
	var result = params[1];
	var xd = this.xmlToObject(result);
 	try {
		var qr = xd.Body.queryResponse.result.records;
		if (qr != null) {
			if (!(qr instanceof Array))
				qr = [ qr ];
			// sometimes SForce returns a duplicate <Id> tag
			for (var i = qr.length; --i >= 0;) {
				if (qr[i].Id && (qr[i].Id instanceof Array))
					qr[i].Id = qr[i].Id[0];
				qr[i].get = Com_Zimbra_SForce.__query_result_get;
			}
		} else {
			qr = [];
		}
		callback.call(this, qr);
 	} catch(ex) {
 		this.displayErrorMessage(ex + "<br />" + ex.dump(), result.text);
 	}
};

// SOAP METHOD: create

Com_Zimbra_SForce.prototype.createSFObject = function(props, type, callback) {
	if (callback == null)
		callback = false;
	var soap = this._makeEnvelope("create");
	var a = props;
	if (!(a instanceof Array))
		a = [ a ];
	for (var j = 0; j < a.length; ++j) {
		var createData = {};
		props = a[j];
		for (var i in props) {
			if (i.indexOf("_") == -1 && props[i] != null) {
				if (i.indexOf(":") == -1)
					createData["ns3:" + i] = props[i];
				else
					createData[i] = props[i];
			}
		}
		var el = soap.set("sObjects", createData);
		el.setAttribute("xsi:type", "ns3:" + type);
		el.setAttribute("xmlns:ns3", this.XMLNS);
		// el.setAttribute("xmlns:tns", this.XMLNS);
	}
	//alert(soap.getXml());
	this.rpc(soap, new AjxCallback(this, this.done_createSFObject, callback));
};

Com_Zimbra_SForce.prototype.done_createSFObject = function(params) {
	var callback = params[0];
	var result = params[1];
	var xd = this.xmlToObject(result);
	if (callback) {
		result = xd.Body.createResponse.result;
		var id;
		if (result instanceof Array) {
			id = [];
			for (var i = 0; i < result.length; ++i)
				id.push(result.id.toString());
		} else {
			id = result.id.toString();
		}
		callback.call(this, id);
	}
};





/// Called when a new contact has been dropped onto the Zimlet panel item, this
/// function will analyze data and take appropriate actions to insert a new
/// contact.
Com_Zimbra_SForce.prototype.contactDropped = function(contact) {
	// Note that since all communication is required to be asynchronous,
	// the only way we can write this function is using a series of
	// callbacks.  The main entry point is when we call this.query(...),
	// but then execution will vary depending on the response.

	var acct_Website = "";

	// augment contact with a helper function
	contact.get = Com_Zimbra_SForce.__query_result_get;

	// this is called after a successful query that should retrieve a
	// matching account (company).
	function $search_acct(records) {
		if (records.length > 0) {
			// we found a matching account
			this.dlg_createAccount(records[0], contact);
		} else {
			var props = {
				Name     : contact.get("company"),
				Website  : acct_Website,
				Phone    : contact.get("workPhone", "workPhone2"),

				// utility function
				get      : Com_Zimbra_SForce.__query_result_get
			};
			this.dlg_createAccount(props, contact);
		}
	};

	if (contact.email || contact.company) {
		// try to determine an existing account first
		var q = [ "select Id, Website, Name, Phone from Account where " ];
		if (contact.email) {
			acct_Website = contact.email.replace(/^[^@]+@/, "").replace(/\x27/, "\\'");
			q.push("Website like '%", acct_Website, "%'");
		} else if (contact.company) {
			q.push("Name = '", contact.company, "'");
		}
		q = q.join("");

		// starting point: we're looking for an account that matches
		// this contact
		this.query(q, 1, $search_acct);
	} else {
		// clearly we can't search for a matching account, so let's
		// create one
		this.dlg_createAccount({ get: Com_Zimbra_SForce.__query_result_get }, contact);
	}
};

Com_Zimbra_SForce.prototype.dlg_createAccount = function(acct_data, contact_data) {
	var view = new DwtComposite(this.getShell());

	/// Create a PropertyEditor for the Account data

	var pe_acct = new DwtPropertyEditor(view, true);
	var pe_props = [

		{ label    : "Account Name",
		  name     : "Name",
		  type     : "string",
		  value    : acct_data.get("Name"),
		  required : true },

		{ label    : "Website",
		  name     : "Website",
		  type     : "string",
		  value    : acct_data.get("Website") },

		{ label    : "Phone",
		  name     : "Phone",
		  type     : "string",
		  value    : acct_data.get("Phone") }

	];
	if (acct_data.Id) {
		var tmp = [

			{ label     : "Use existing account?",
			  name      : "_reuse",
			  type      : "select",
			  value     : "yes",
			  item      : [ { label : "Yes, please",
					  value : "yes" },
				        { label : "No, create a new one",
					  value : "no" }
				  ] },

			{ label     : "Account Id",
			  name      : "Id",
			  readonly  : true,
			  value     : acct_data.get("Id"),
			  type      : "string",
			  visible   : false }

		];

		pe_props.unshift(tmp[0], tmp[1]);
	}
	pe_acct.initProperties(pe_props);
	var dialog_args = {
		title : "Create Account/Contact at SForce",
		view  : view
	};

	var tmp = document.createElement("h3");
	tmp.className = "SForce-sec-label";
	tmp.innerHTML = acct_data.Id
		? "An existing account matches"
		: "Add to a new account";
	var el = pe_acct.getHtmlElement();
	el.parentNode.insertBefore(tmp, el);

	/// Create a PropertyEditor for the new contact data

	pe_contact = new DwtPropertyEditor(view, true);
	pe_props = [

		{ label    : "First name",
		  name     : "FirstName",
		  type     : "string",
		  value    : contact_data.get("firstName") },

		{ label    : "Last name",
		  name     : "LastName",
		  type     : "string",
		  value    : contact_data.get("lastName"),
		  required : true },

		{ label    : "Email",
		  name     : "Email",
		  type     : "string",
		  value    : contact_data.get("email", "email2", "email3") },

		{ label    : "Phone",
		  name     : "Phone",
		  type     : "string",
		  value    : contact_data.get("phone") }

	];
	pe_contact.initProperties(pe_props);

	tmp = document.createElement("h3");
	tmp.className = "SForce-sec-label";
	tmp.innerHTML = "New contact info";
	var el = pe_contact.getHtmlElement();
	el.parentNode.insertBefore(tmp, el);

	var dlg = this._createDialog(dialog_args);
 	pe_acct.setFixedLabelWidth();
 	pe_acct.setFixedFieldWidth();
 	pe_contact.setFixedLabelWidth(pe_acct.maxLabelWidth);
 	pe_contact.setFixedFieldWidth();
	dlg.popup();

	// handle some events

	dlg.setButtonListener(
		DwtDialog.OK_BUTTON,
		new AjxListener(this, function() {
			if (!( pe_acct.validateData() && pe_contact.validateData() ))
				return;
			var acct = pe_acct.getProperties();
			var contact = pe_contact.getProperties();
			function $create_contact() {
				this.createSFObject(contact, "Contact", function(id) {
					var name = contact.LastName || contact.FirstName || contact.Email || id;
					this.displayStatusMessage("New contact created: " + name);
				});
			};
			////
			// TODO: we should have some checking going on here
			////
			if (acct.Id && acct._reuse == "yes") {
 				contact.AccountId = acct.Id;
				$create_contact.call(this);
			} else {
				// alert("TODO: need implementation of createAccount");
				delete acct.Id;
				this.createSFObject(acct, "Account", function(id) {
					var name = acct.Name || contact.Website || id;
					this.displayStatusMessage("New account created: " + name);
					contact.AccountId = id;
					$create_contact.call(this);
				});
			}
			dlg.popdown();
			dlg.dispose();
		}));

	// We don't really want to mess with things like cache-ing this
	// dialog...
	dlg.setButtonListener(
		DwtDialog.CANCEL_BUTTON,
		new AjxListener(this, function() {
			dlg.popdown();
			dlg.dispose();
		}));
};

// UI handlers

/// Called by the Zimbra framework upon an accepted drag'n'drop
Com_Zimbra_SForce.prototype.doDrop = function(obj) {
	switch (obj.TYPE) {
	    case "ZmMailMsg":
		this.displayErrorMessage("You dropped an email message!");
		break;

	    case "ZmConv":
		this.displayErrorMessage("You dropped a conversation!");
		break;

	    case "ZmContact":
		this.contactDropped(obj);
		break;

	    default:
		this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE
					 + "\" but however the SForce Zimlet does't support it for drag'n'drop.");
	}
};

/// Called by the Zimbra framework when the SForce panel item was clicked
Com_Zimbra_SForce.prototype.panelItemClicked = function() {
	this.login();
};

/// Called by the Zimbra framework when some menu item that doesn't have an
/// <actionURL> was selected
Com_Zimbra_SForce.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
		this.createPropertyEditor();
		break;
	    case "LOGIN":
		this.login();
		break;
	}
};
