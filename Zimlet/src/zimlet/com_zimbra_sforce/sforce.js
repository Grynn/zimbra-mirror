/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
 * 
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 * 
 * ***** END LICENSE BLOCK *****
 */

////////////////////////////////////////////////////////////////
///  Zimlet to handle integration with SalesForce            ///
///  @author Mihai Bazon, <mihai@zimbra.com>                 ///
///  @author Kevin Henrikson, <kevinh@zimbra.com>            ///
////////////////////////////////////////////////////////////////
function Com_Zimbra_SForce() {
}

/// Zimlet handler objects, such as Com_Zimbra_SForce, must inherit from
/// ZmZimletBase.  The 2 lines below achieve this.
Com_Zimbra_SForce.prototype = new ZmZimletBase();
Com_Zimbra_SForce.prototype.constructor = Com_Zimbra_SForce;

Com_Zimbra_SForce.SFORCE = "SFORCE";

Com_Zimbra_SForce.prototype.init = function() {
    // Let the framework know that we want to handle 500 and other errors
    // ourselves (in particular, for a simple "login failure" the SForce
    // server likes to return 500.  This IMO is dumb, but whatever.
    //this._passRpcErrors = true;

    this.SERVER = Com_Zimbra_SForce.LOGIN_SERVER;

    // We MUST use the enterprise URN in order for certain "advanced"
    // features such as adding a contact to an account
    this.XMLNS = "urn:enterprise.soap.sforce.com";


    // Register with Zimbra Assistant
    var asst = new Com_Zimbra_SForce_Asst(this);
    this._asst = asst;
    if (ZmAssistant && ZmAssistant.register) ZmAssistant.register(asst);
};

Com_Zimbra_SForce.prototype.onShowView = function(viewId, isNewView) {
	 if (viewId == ZmController.COMPOSE_VIEW && !this._toolbar){ 
        this._initComposeSFToolbar();
     }
};

Com_Zimbra_SForce.prototype._initComposeSFToolbar = function(){

    if(!appCtxt.get(ZmSetting.MAIL_ENABLED)) this._toolbar = true;

    if(this._toolbar) return;

   // Add the Salesforce Button to the Compose Page
    this._composerCtrl = AjxDispatcher.run("GetComposeController");
    this._composerCtrl._sforce = this;
    if(!this._composerCtrl._toolbar) {
      // initialize the compose controller's toolbar
      this._composerCtrl._initializeToolBar();
    }

    this._toolbar = this._composerCtrl._toolbar;
    // Add button to toolbar
    if(!this._toolbar.getButton(Com_Zimbra_SForce.SFORCE)){
	    ZmMsg.sforceAdd = "Send & Add";
	    ZmMsg.sforceTooltip = "Send and add to Salesforce.";
	    var op = {
	    	id: Com_Zimbra_SForce.SFORCE,
	    	textKey: "sforceAdd",
	    	text: ZmMsg.sforceAdd,
	    	tooltipKey: "sforceTooltip",
	    	tooltip: ZmMsg.sforceTooltip,
	    	image: "SFORCE-panelIcon"
	    };
	    var opDesc = ZmOperation.defineOperation(null, op);
	    this._toolbar.addOp(opDesc.id, 1);

	    this._toolbar.addSelectionListener(opDesc.id, new AjxListener(this._composerCtrl, this._sendAddSForce));
    }

};


/// Store the default SOAP server.  Note that after a successful login, the URL
/// may change--which is why we store it in an object instance too (this.SERVER)
Com_Zimbra_SForce.LOGIN_SERVER = "https://www.salesforce.com/services/Soap/c/7.0";
Com_Zimbra_SForce._RECENT = {};

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
        var xd = new AjxXmlDoc.createFromDom(result.xml).toJSObject(true, false);
    } catch(ex) {
        this.displayErrorMessage(ex, result.text, "Problem contacting Salesforce");
    }
    return xd;
};

/// Utility function that calls the SForce server with the given SOAP data
Com_Zimbra_SForce.prototype.rpc = function(soap, callback, passErrors) {
	this.sendRequest(soap, this.SERVER, {SOAPAction: "m", "Content-Type": "text/xml"}, callback, false, passErrors);
};

// SOAP METHOD: login

/// Login to SForce.  The given callback will be called in the case of a
/// successful login.  Note that callback is a plain function (not AjxCallback)
Com_Zimbra_SForce.prototype.login = function(callback) {
	if (!callback) {
		callback = false;
    }
    var user = this.getUserProperty("user");
	var passwd = this.getUserProperty("passwd");
	if (!user || !passwd) {
		this.displayStatusMessage("Please fill your Salesforce credentials first");
		this.createPropertyEditor(new AjxCallback(this, this.login, [ callback ]));
	} else {
		this._do_login(callback, user, passwd);
	}
};

Com_Zimbra_SForce.prototype._do_login = function(callback, user, passwd) {
	var soap = this._makeEnvelope("login");
	soap.set("username", user);
	soap.set("password", passwd);
	if (callback == null)
		callback = false;
	this.rpc(soap, new AjxCallback(this, this.done_login, [ callback ]), true);
};

Com_Zimbra_SForce.prototype.done_login = function(callback, result) {
	var ans = this.xmlToObject(result);
	if (ans && ans.Body && ans.Body.loginResponse) {
		ans = ans.Body.loginResponse.result;
		this.SERVER = String(ans.serverUrl);
		this.sessionId = String(ans.sessionId);
		this.userId = String(ans.userId);
		this.userInfo = ans.userInfo;
		this.displayStatusMessage("Salesforce: " + this.userInfo.userFullName + " logged in.");
		if (callback)
			callback.call(this);
    } else {
        var fault = "";
        if (ans && ans.Body && ans.Body.Fault && ans.Body.Fault.faultstring) {
            fault = ans.Body.Fault.faultstring + "<br />";
        }
        this.displayErrorMessage("<b>Login to Salesforce failed!</b><br />&nbsp;&nbsp;&nbsp;&nbsp;" + fault + "<br />Check your internet connection and review your preferences.");
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
	// How I wish JS had continuations!  But luckily, we are smart enough
	// to overcome this limitation. ;-)
	if (!this.sessionId) {
		this.login(function() {
			this._do_query(query, limit, callback);
		});
	} else {
		this._do_query(query, limit, callback);
    }
};

Com_Zimbra_SForce.prototype._do_query = function(query, limit, callback) {
	if (!limit || limit < 1) {limit = 1;}
	var soap = this._makeEnvelope("query");
	soap.set("queryString", query);
    // SForce docs specify this is a *wish* value and that it's
    // not enforced.  Also says the valid range is 200 -> 2000.
    // Default is 500.  So callers need to handle getting more or
    // less results than they request.
    var qo = soap.set("QueryOptions", { batchSize : limit }, soap.ensureHeader());
	qo.setAttribute("xmlns", this.XMLNS);
	// we sure have a lot of indirection going on..
	this.rpc(soap, new AjxCallback(this, this.done_query, [ callback ]));
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

Com_Zimbra_SForce.prototype.done_query = function(callback, result) {
	var xd = this.xmlToObject(result);
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
};

// SOAP METHOD: create

Com_Zimbra_SForce.prototype.createSFObject = function(props, type, callback) {
	// make sure we are logged in first
	if (!this.sessionId)
		this.login(function() {
			this._do_createSFObject(props, type, callback);
		});
	else
		this._do_createSFObject(props, type, callback);
};

Com_Zimbra_SForce.prototype._do_createSFObject = function(props, type, callback) {
    if (!callback) {
        callback = false;
    }
    var soap = this._makeEnvelope("create");
    var a = props;
    if (!(a instanceof Array)) {
        a = [ a ];
    }
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
    }
    this.rpc(soap, new AjxCallback(this, this.done_createSFObject, [ callback ]));
};

Com_Zimbra_SForce.prototype.done_createSFObject = function(callback, result) {
	var xd = this.xmlToObject(result);
	if (xd && callback) {
		result = xd.Body.createResponse.result;
		var id;
		if (result instanceof Array) {
			id = [];
			for (var i = 0; i < result.length; ++i)
				id.push(result[i].id.toString());
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
		//Search for an account
		if (records.length > 0) {
			// we found a matching account
			this.dlg_createAccount(records[0], contact);
		} else {
			var props = {
				Name     : contact.get("company"),
				Website  : acct_Website,
				Phone    : contact.get("workPhone"),

				// utility function
				get      : Com_Zimbra_SForce.__query_result_get
			};
			this.dlg_createAccount(props, contact);
		}
	}
	
	function $search_acct_company(records){
		if(records.length > 0){
			$search_acct.call(this,records);
		}else{
			var q ="select Id, Website, Name, Phone from Account where Name like '"+contact.company+"%'";
			this.query(q,1,$search_acct_email);
		}
	}
	
	function $search_acct_email(records){
		if(records.length > 0){
			$search_acct.call(this,records)		
		}else{
			var email = contact.email || contact.email2 || contact.email3;
			acct_Website = email.replace(/^[^@]+@/, "").replace(/\x27/, "\\'");
			var q ="select Id, Website, Name, Phone from Account where Website like '%"+ acct_Website+ "'";
			this.query(q,1,$search_acct);
		}
	}
	
	function $search_contact(records){
		//Search Contact
		contact._exists = false;
		if(records.length>0){
			//Contact already present in Sales Force
			contact._exists = true;
			if(records[0].AccountId && records[0].AccountId!=""){
				contact.AccountId = records[0].AccountId;
				contact.Id = records[0].Id;
				var q ="select Id, Website, Name, Phone from Account where Id='"+contact.AccountId+"'";
				this.query(q,1,$search_acct);
				return;
			}
		}
		///New Contact
		//Search for an account that matches this contact
		if(contact.company){
			//Searching for the Account associated with this account
			var q = "select Id, Website, Name, Phone from Account where Name='"+contact.company+"'";
			this.query(q,1,$search_acct_company);
		}else if(contact.email || contact.email2 || contact.email3){
			//Searching for the Account that has the website like contact website.
			$search_acct_email.call(this,[]);
		}else{
			//Just go ahead
			$search_acct.call(this,[]);
		}
	}

//Search for a contact
	if(contact.email || contact.email2 || contact.email3){
		///Serach contacts with the first primary email address
		///Need to extend the query to OR every email address
		var email = contact.email || contact.email2 || contact.email3 ;
		var q = [ "select Id, FirstName, LastName, Email, AccountId from Contact where Email like '",
		contact.email,"'"].join("");
       	this.query(q,1, $search_contact);
	}else if(contact.company){
		$search_contact.call(this,[]);
	}else{
		// clearly we can't search for a matching account, so let's
		// create one
		this.dlg_createAccount({ get: Com_Zimbra_SForce.__query_result_get }, contact);
	}

};

Com_Zimbra_SForce.prototype.dlg_createAccount = function(acct_data, contact_data) {
	var view = new DwtComposite(this.getShell());

	///Disable fieldsEditable if contact already exists	
	var fieldsEditable = !(contact_data._exists);
	
	/// Create a PropertyEditor for the Account data
	var pe_acct = new DwtPropertyEditor(view, fieldsEditable);
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
            type      : "enum",
            value     : "yes",
            item      : [ { label : "Yes",  value : "yes" },
                          { label : "No, create a new one", value : "no" }
                        ] },

        { label     : "Account Id",
            name      : "Id",
            readonly  : true,
            value     : acct_data.get("Id"),
            type      : "string",
            visible   : false }

                ];                
		contact_data._exists
			? pe_props.unshift(tmp[1])
			:pe_props.unshift(tmp[0], tmp[1]);
	}
	
	///Do not display for any contact without a corresponding account.
	if(!(contact_data._exists && !acct_data.Id)){
		pe_acct.initProperties(pe_props);
	}
	
	var dialogTitle = contact_data._exists
							?(acct_data.Id?"Account/Contact in Salesforce":"Contact in Salesforce")
							:"Create Account/Contact in Salesforce";						
	var dialog_args={};
	///Displaying static content needs only OK button.
	if(contact_data._exists){
		dialog_args = {title : dialogTitle,view  : view,standardButtons : [DwtDialog.OK_BUTTON]};
	}else{
		dialog_args = {title : dialogTitle, view  : view};
	}

	var tmp = document.createElement("h3");
	tmp.className = "SForce-sec-label SForce-icon-right";
	DBG.println(AjxDebug.DBG3,"Contact Exists-"+contact_data._exists);
	tmp.innerHTML = contact_data._exists
						?  "Contact already exists"
						: "Add to a new account"; 
	var el = pe_acct.getHtmlElement();
	el.parentNode.insertBefore(tmp, el);

	/// Create a PropertyEditor for the new contact data
	pe_contact = new DwtPropertyEditor(view, fieldsEditable);
				
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

		{ label    : "Title",
		  name     : "Title",
		  type     : "string",
		  value    : contact_data.get("jobTitle") },

		{ label    : "Email",
		  name     : "Email",
		  type     : "string",
		  value    : contact_data.get("email", "email2", "email3") },

		{ label    : "Work Phone",
		  name     : "Phone",
		  type     : "string",
		  value    : contact_data.get("workPhone") },

		{ label    : "Other Phone",
		  name	   : "OtherPhone",
		  type	   : "string",
		  value	   : contact_data.get("workPhone2") },

		{ label	   : "Mobile",
		  name     : "MobilePhone",
          type	   : "string",
		  value    : contact_data.get("mobilePhone") }
	];
	pe_contact.initProperties(pe_props);

	if(!(contact_data._exists && !acct_data.Id)){
		tmp = document.createElement("h3");
		tmp.className = "SForce-sec-label";
		tmp.innerHTML = contact_data._exists? "Contact info":"New contact info";
		el = pe_contact.getHtmlElement();
		el.parentNode.insertBefore(tmp, el);
	}
	

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
			///If its only static information then its just a simple OK button
			if(contact_data._exists){
				dlg.popdown();
				dlg.dispose();
				return;
			}
			if (!( pe_acct.validateData() && pe_contact.validateData() ))
				return;
			var acct = pe_acct.getProperties();
			var contact = pe_contact.getProperties();
			function $create_contact() {
				this.createSFObject(contact, "Contact", function(id) {
					var name = contact.LastName || contact.FirstName || contact.Email || id;
					this.displayStatusMessage("SForce contact saved: " + name);
				});
			};
			////
			// TODO: we should have some checking going on here
			////
			if (acct.Id && acct._reuse == "yes") {
 				contact.AccountId = acct.Id;
				$create_contact.call(this);
			} else {
				delete acct.Id;
				this.createSFObject(acct, "Account", function(id) {
					var name = acct.Name || contact.Website || id;
					this.displayStatusMessage("SForce account created: " + name);
					contact.AccountId = id;
					$create_contact.call(this);
				});
			}
			dlg.popdown();
			dlg.dispose();
		}));
		
		// We don't really want to mess with things like cache-ing this
	// dialog...
	if(!contact_data._exists){
		dlg.setButtonListener(
			DwtDialog.CANCEL_BUTTON,
			new AjxListener(this, function() {
				dlg.popdown();
				dlg.dispose();
			}));
	}
};

Com_Zimbra_SForce.prototype.noteDropped = function(note) {
    if(!note) {return;}
    // check out some domains, exclude user's domain
    var ignoreDomain = this.getUserProperty("ignoreDomain");
    var emails = [];
    function addEmails(a) {
        if (a) {
            if (typeof a == "string") {
                if (a.indexOf(ignoreDomain) != -1) {
                    return;
                }
                emails.push(a);
            } else if (a instanceof Array) {
                for (var i = 0; i < a.length; ++i) {
                    if(a[i].address && a[i].address.indexOf(ignoreDomain) == -1) {
                        emails.push(a[i].address);
                    }
                }
            }
        }
    }
    ;
    if(note._addrs && note._addrs.length > 0) {
        for(var i=1; i < note._addrs.length; i++) {
            addEmails(note._addrs[i]._array);
        }
    } else {
        addEmails(note.participants);
        addEmails(note.from);
        addEmails(note.to);
        addEmails(note.cc);
    }
    var domains = [], tmp = {};
	for (var i = 0; i < emails.length; ++i) {
        DBG.println(AjxDebug.DBG3, emails[i]);
        if (/@([^>]+)>?$/.test(emails[i])) {
			var d = RegExp.$1;
			if (!tmp[d]) {
				tmp[d] = 1;
				// kind of pointless, but let's make sure we
				// backslash any apostrophes
				domains.push(d.replace(/\x27/, "\\'"));
			}
		}
	}
    function $search_acct(records) {
        // Split Opportunities and Contacts into Account groups
        var acctsSorted = {};
        var a = Com_Zimbra_SForce._RECENT.Accounts;
        for (var i = 0; i < a.length; ++i) {
            acctsSorted[a[i].Id] = a[i];
            acctsSorted[a[i].Id].TYPE = "A";
            acctsSorted[a[i].Id].Con = [];
            acctsSorted[a[i].Id].Opp = [];
        }
        var c = Com_Zimbra_SForce._RECENT.Contacts;
        for (var i = 0; i < c.length; ++i) {
            c[i].TYPE = "C";
            if(acctsSorted[c[i].AccountId]) {
                acctsSorted[c[i].AccountId].Con.push(c[i]);
            }
        }
        var o = records;
        for (var i = 0; i < o.length; ++i) {
            o[i].TYPE = "O";
            acctsSorted[o[i].AccountId].Opp.push(o[i]);
        }
        this.dlg_addNoteToAccounts(acctsSorted, note);
    }
	function $search_acctRelated(records) {
		if (records.length == 0) {
			this.displayErrorMessage(
				[ "There are no matching accounts for these email domains:", domains ].join("<br />"));
		} else {
            Com_Zimbra_SForce._RECENT.Accounts = records;
            var ids = [];
            for (var i = 0; i < records.length; ++i) {
                ids.push(records[i].Id);
            }
            var q = [ "select Id, Name, AccountId from Opportunity where AccountId='", ids.join("' or AccountId='"), "'" ].join("");
            this.query(q, 10, $search_acct);
		}
	}
    function $search_contact(records) {
        if (records.length == 0) {
            this.displayErrorMessage(
                    [ "There are no matching contacts for these email domains:", domains ].join("<br />"));
        } else {
            Com_Zimbra_SForce._RECENT.Contacts = records;
            var ids = [];
            for (var i = 0; i < records.length; ++i) {
                ids.push(records[i].AccountId);
            }
            var q = [ "select Id, Name, Website, Phone from Account where Id='", ids.join("' or Id='"), "'" ].join("");
            this.query(q, 10, $search_acctRelated);
        }
    }
    if (domains.length == 0) {
		this.displayErrorMessage("No email addresses or domains found.<br />"
					 + "We can't determine an Account to add this note to.");
	} else {

        var q = [ "select Id, FirstName, LastName, Email, AccountId from Contact where Email like '%",
                domains.join("%' or Email like '%"),
                "%'" ].join("");
        this.query(q, 10, $search_contact);
	}
};

Com_Zimbra_SForce.prototype.dlg_addNoteToAccounts = function(accounts, note) {
	var view = new DwtComposite(this.getShell());
	var el = view.getHtmlElement();
	var h3 = document.createElement("h3");
    var i;
    h3.className = "SForce-sec-label SForce-icon-right";
	h3.innerHTML = "A = Account, O = Opportunity, C = Contact";
	//h3.innerHTML = "A = Account or O = Opportunity";
	el.appendChild(h3);

	var checkboxes = [];

    var div = document.createElement("div");
    var html = [ "<table><tbody>" ];
    DBG.dumpObj(AjxDebug.DBG3, accounts);
    for (acctId in accounts) {
        DBG.dumpObj(AjxDebug.DBG3, acctId);
        var acct = accounts[acctId];
        DBG.dumpObj(AjxDebug.DBG3, acct);
        var cbid = Dwt.getNextId();
        checkboxes.push(cbid);
        html = this._checkBoxHtml(acct, cbid, 0, false, html);
        var chkContact = true;
        if (acct.Opp && acct.Opp.length > 0) {
            for (i = 0; i < acct.Opp.length; i++) {
                cbid = Dwt.getNextId();
                checkboxes.push(cbid);
                html = this._checkBoxHtml(acct.Opp[i], cbid, 2, true, html);
                chkContact = false;
            }
        }
        // Limit the number of contacts shown to 5
		var displayLimit = acct.Con.length;
        if(displayLimit > 5) {
            displayLimit = 5;
            DBG.println(AjxDebug.DBG3, "Setting contact limit to 5 returned " + acct.Con.length);
        }
        if (acct.Con && displayLimit > 0) {
            for (i = 0; i < displayLimit; i++) {
                cbid = Dwt.getNextId();
                checkboxes.push(cbid);
                html = this._checkBoxHtml(acct.Con[i], cbid, 2, chkContact, html);
            }
        }
    }
    html.push("</tbody></table>");
	div.innerHTML = html.join("");
	el.appendChild(div);

	h3 = document.createElement("h3");
	h3.className = "SForce-sec-label";
	h3.innerHTML = "Note details";
	el.appendChild(h3);

	div = document.createElement("div");
	var subjectId = Dwt.getNextId();
	var messageId = Dwt.getNextId();
    var body;
    if (note.body) {
        body = AjxStringUtil.htmlEncode(note.body);
    } else if (note._topPart && note._topPart.getContentForType) {
        body = AjxStringUtil.htmlEncode(note._topPart.getContentForType(ZmMimeTable.TEXT_PLAIN));
    } else {
		body = "Error - No body found!"
	}
    div.innerHTML =
		[ "<table><tbody>",
		  "<tr>",
		  "<td align='right'><label for='", subjectId, "'>Subject:</td>",
		  "<td>",
		  "<input style='width:35em' type='text' id='", subjectId, "' value='",
		  AjxStringUtil.htmlEncode(note.subject), "' autocomplete='off' />",
		  "</td>",
		  "</tr>",
		  "<td colspan='2'>",
          "<textarea style='width:40em;height:200px' id='", messageId, "'>",
           body, "</textarea>",
          "</td>",
		  "<tr>",
		  "</tr></tbody></table>" ].join("");
	el.appendChild(div);

	var dialog_args = {
		view  : view,
		title : "Adding note(s) to Salesforce"
	};
	var dlg = this._createDialog(dialog_args);
	dlg.popup();

	el = document.getElementById(subjectId);
	el.select();
	el.focus();

	dlg.setButtonListener(DwtDialog.OK_BUTTON,
			      new AjxListener(this, function() {
				      var ids = [];
				      for (i = 0; i < checkboxes.length; ++i) {
					      var cb = document.getElementById(checkboxes[i]);
					      if (cb.checked) {
							  ids.push({ WhatId: cb.value });
						  }
				      }
				      if (ids.length == 0) {
					      this.displayErrorMessage("You must select at least Account, Opportunity, or Contact!");
				      } else {
					      var props = {
						      Title : document.getElementById(subjectId).value,
						      Body  : document.getElementById(messageId).value
					      };
					      for (i = 0; i < ids.length; ++i) {
							  ids[i].Subject = props.Title;
						      ids[i].Description = props.Body;
							  ids[i].Status = 'Completed';
							  ids[i].ActivityDate = Com_Zimbra_SForce.toIsoDateTime(new Date());
					      }
					      this.createSFObject(ids, "Task", function() {
						      this.displayStatusMessage("Saved " + ids.length + " notes.");
					      });
					      dlg.popdown();
					      dlg.dispose();
				      }
			      }));

	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON,
			      new AjxListener(this, function() {
				      dlg.popdown();
				      dlg.dispose();
			      }));
};

Com_Zimbra_SForce.toIsoDate = function(theDate) {
	return AjxDateFormat.format("yyyy-MM-dd", theDate);
};

Com_Zimbra_SForce.toIsoDateTime = function(theDate) {
    var zDate = new Date(theDate.getTime());
    zDate.setMinutes(zDate.getMinutes()+zDate.getTimezoneOffset());
    var ret = AjxDateFormat.format("yyyy-MM-ddTHH:mm:ss'Z'", zDate);
    DBG.println(AjxDebug.DBG3, "ret: " + ret);
    return ret;
};

Com_Zimbra_SForce.prototype.apptDropped = function(obj) {
	var appt = {
		ActivityDate      : Com_Zimbra_SForce.toIsoDate(obj.startDate),
		ActivityDateTime  : Com_Zimbra_SForce.toIsoDateTime(obj.startDate),
		DurationInMinutes : Math.round((obj.endDate.getTime() - obj.startDate.getTime()) / 60000),
		Description       : obj.notes,
		Subject           : obj.subject,
		// we need to reverse engineer the salesforce SOAP API first :-\
		// the official docs. are almost useless.
		// Type              : "Meeting", // obj.type is always null
		Location          : obj.location
	};
    DBG.dumpObj(appt);
    this.createSFObject(appt, "Event", function(id) {
		this.displayStatusMessage("New event registered at Salesforce");
	});
};

// UI handlers

/// Called by the Zimbra framework upon an accepted drag'n'drop
Com_Zimbra_SForce.prototype.doDrop = function(obj) {
	switch (obj.TYPE) {
	    case "ZmMailMsg":
	    case "ZmConv":
		this.noteDropped(obj);
		break;

	    case "ZmContact":
		this.contactDropped(obj);
		break;

	    case "ZmAppt":
		this.apptDropped(obj);
		break;

	    default:
		this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE
					 + "\" but however the SForce Zimlet does't support it for drag'n'drop.");
	}
};

/// Called by the Zimbra framework when the SForce panel item was clicked
Com_Zimbra_SForce.prototype.singleClicked = function() {
	this.login();
};

// Com_Zimbra_SForce.prototype.onContactModified = function(contact, mods) {
// 	for (var i in mods)
// 		contact[i] = mods[i];
// 	this.doDrop(contact);	// delegate
// };

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

Com_Zimbra_SForce.prototype._sendAddSForce = function(ev) {
    var msg = this._composeView.getMsg();
    this._send();
    this._sforce.noteDropped(msg);
};

// rec - The record to generate checkbox HTML for
// cbid - DWT id of this new check box
// indent - 0 | 1 | 2, amount to indent
// checked - default the checkbox to checked?
// html - array to append html too
Com_Zimbra_SForce.prototype._checkBoxHtml = function(rec, cbid, indent, checked, html) {

    html.push("<tr><td><input type='checkbox' value='",
            rec.Id,
            "' id='",
            cbid);

    if (checked) {
        html.push("' checked='checked'/>");
    } else {
        html.push("' />");
    }

    html.push("</td>",
            "<td>",
            "<label for='", cbid, "'>");

    switch (indent) {
        case 1:
            html.push("&nbsp;&nbsp;");
            break;
        case 2:
            html.push("&nbsp;&nbsp;&nbsp;&nbsp;");
            break;
    }

    if (rec.TYPE)
        html.push(rec.TYPE + ":");

    if (rec.Name)
        html.push(" " + rec.Name);

    if (rec.FirstName)
        html.push(" " + rec.FirstName);

    if (rec.LastName)
        html.push(" " + rec.LastName);

    if (rec.Email)
        html.push(" [" + rec.Email + "]");

    if (rec.Website)
        html.push(" [" + rec.Website + "]");

    if (rec.Phone)
        html.push(" [" + rec.Phone + "]");

    html.push("</label></td></tr>");
    return html;
};

Com_Zimbra_SForce.prototype.search_contact =
function (records) {
    var contacts = [];
    if (records.length == 0) {
        this._asst._setField("Contact", "No Match", false, true);
    } else if (records.length == 1) {
        var email = Com_Zimbra_SForce.display_contact(records[0]);
        DBG.println(AjxDebug.DBG3, "single match: " + email);
        this._contactEmail = records[0].Email;
        this._parentId = records[0].Id.__msh_content;
        this._asst._setField("Contact", email, false);
    } else {
        // Limit the number of matches shown to 10
        var displayLimit = records.length;
        if(displayLimit > 10) {
            displayLimit = 10;
            DBG.println(AjxDebug.DBG3, "Setting limit to 10 returned " + records.length);
        }
        for (var i = 0; i < displayLimit; ++i) {
            var email = Com_Zimbra_SForce.display_contact(records[i]);
            contacts.push(email);
            DBG.println(AjxDebug.DBG3, "multi match: " + email);
        }
        contacts = contacts.join("<br/>");
        DBG.println(AjxDebug.DBG3, "search_contact this: " + this);
        this._asst._setField("Contact", contacts, false);
    }
};

Com_Zimbra_SForce.display_contact =
function (contact) {
    var ret = "";
    if(contact.FirstName) {
        ret = ret + contact.FirstName;
    }
    if(contact.LastName) {
        ret = ret + " " + contact.LastName;
    }
    if(contact.Email) {
        ret = ret + " (" + contact.Email + ")";
    }
    return ret;
};
//////////////////////////////////////////////////////////////////////////
// Zimlet assistant class
// - used by the Assistant dialog to run via "command-line"
//////////////////////////////////////////////////////////////////////////
function Com_Zimbra_SForce_Asst(zimlet) {
    if (arguments.length == 0) return;
    // XXX: localize later (does NOT belong in ZmMsg.properties)
    ZmAssistant.call(this, "Salesforce", "salesforce");
    this._zimlet = zimlet;
}

Com_Zimbra_SForce_Asst.prototype = new ZmAssistant();
Com_Zimbra_SForce_Asst.prototype.constructor = Com_Zimbra_SForce_Asst;

Com_Zimbra_SForce_Asst.prototype.okHandler =
function(dialog) {

    // Throw the note data in an array to add it.
    var ids = [];
    ids.push({ WhoId: this._zimlet._parentId});
    ids[0].Subject = this._zimlet._note;
    ids[0].Description = this._zimlet._note;
	ids[0].Status = 'Completed';
	this._zimlet.createSFObject(ids, "Task", function() {
        this.displayStatusMessage("Saved " + ids.length + " note to " + this._contactEmail);
    });
    return true;
};

Com_Zimbra_SForce_Asst.prototype.handle =
function(dialog, verb, args) {
    DBG.println(AjxDebug.DBG3, "args: " + args);
    DBG.println(AjxDebug.DBG3, "verb: " + verb);

    this._zimlet._asstDialog = dialog;
    this._args = args;
    var valid = false;
    this._noteFields = {};

    while (match = args.match(/((\w+)(:\s*)(.*?)\s*)(\w+:|$)/m)) {
        var strip = match[1];
        var k = match[2];
        var v = match[4];
        DBG.println(AjxDebug.DBG3, "strip: " + strip);
        DBG.println(AjxDebug.DBG3, "k: " + k);
        DBG.println(AjxDebug.DBG3, "v: " + v);
        this._noteFields[k] = v;
        if (args) {args = args.replace(strip,"");}
    }

    // Check to see if the contact search changed, before we send a new SOAP query.
    if (this._noteFields["c"] && this._noteFields["c"] != this._lastContactSearch) {
        v = this._noteFields["c"];
        var q = [ "select Id, FirstName, LastName, Email, AccountId from Contact where Email != '' and (Email like '%",
                v,"%' or FirstName like '%", v, "%' or LastName like '%", v,"%')" ].join("");
        DBG.println(AjxDebug.DBG3, "q: " + q);
        this._zimlet.query(q, 10, this._zimlet.search_contact);
    } else if (!this._noteFields["c"]) {
        this._setField("Contact", "Search for a contact, prefix it with c:", true, false);
    }
    this._lastContactSearch = this._noteFields["c"];

    if (this._noteFields["n"]) {
        v = this._noteFields["n"];
        this._setField("Note", v, false, false);
        valid = true;
        this._zimlet._note = v;
    } else {
        this._setField("Note", "Enter a note, prefix it with n:", true, false);
    }

    dialog._setOkButton("OK", true, valid);
};
