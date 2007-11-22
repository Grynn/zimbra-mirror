/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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

function Com_Zimbra_Asterisk() {
}

Com_Zimbra_Asterisk.prototype = new ZmZimletBase();
Com_Zimbra_Asterisk.prototype.constructor = Com_Zimbra_Asterisk;

Com_Zimbra_Asterisk.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
		case "CALL":
			this.singleClicked();
			break;
		case "PREFERENCES":
			if (this._dlg_propertyEditor) {
				this._dlg_propertyEditor.dispose();
			}
			this._dlg_propertyEditor = null;
			this.createPropertyEditor();
			break;
	}
};

// Q&D number canonicalization.
// US centric.
//
// Strip all non-digit characters
// If it starts with a "+", do not modify
// If it has > 10 digits, do not modify
// If it has 10 digits, prepend a "1"
// If it has < 10 digits, do not modify

Com_Zimbra_Asterisk.prototype.fixNumber = function(myNumber) {
	var numStr = String(myNumber);
	var hasPlus = 0;
	numStr = numStr.replace(/[\n\r]/g,'');
	if (numStr.match(/^\+/)) {hasPlus = 1;}

	numStr = numStr.replace(/\D/g,'');

	if (numStr.length == 10) {numStr = "1"+numStr;}

	if (hasPlus) {numStr = "+"+numStr;}
	return numStr;
};

// Called by the Zimbra framework when the panel item was double clicked
Com_Zimbra_Asterisk.prototype.doDrop = function(myobj) {
	this.setupCall(myobj);
};

// Called by the Zimbra framework when the panel item was double clicked
Com_Zimbra_Asterisk.prototype.doubleClicked = function() {
	this.singleClicked();
};

Com_Zimbra_Asterisk.prototype.singleClicked = function() {
	this.setupCall();
};

Com_Zimbra_Asterisk.prototype.clicked = function(myElement, myNumber) {

	if (!this.verifyPrefs()) {
		this.displayStatusMessage("Please populate preferences"); 
		if (this._dlg_propertyEditor) {
			this._dlg_propertyEditor.dispose();
		}
		this._dlg_propertyEditor = null;
		this.createPropertyEditor();
		return;
	}
	var to = this.fixNumber(myNumber);
	if (this.getUserProperty(this.getUserProperty("defaultNum").toString()) != null) {
		var uname = AjxStringUtil.urlEncode(this.getUserProperty('pbxUname'));
		var pass = AjxStringUtil.urlEncode(this.getUserProperty('pbxPass'));
		var reqHeader = {"Content-Type":"application/x-www-form-urlencoded"};
		var url = this.getResource('asterisk.jsp');

		var from = this.fixNumber(this.getUserProperty(this.getUserProperty("defaultNum").toString()).toString());

		from = AjxStringUtil.urlEncode(from);

		to = AjxStringUtil.urlEncode(to);

		var reqParam = 'to=' + to + '&from=' + from + '&uname=' + uname + '&pass=' + pass;

		this.displayStatusMessage('Calling '+from+' to '+to); 
		AjxRpc.invoke(reqParam, url, reqHeader, new AjxCallback(this, this._resultCallback));
	} else {
		this.setupCall(myNumber);
	}
};

Com_Zimbra_Asterisk.prototype.verifyPrefs = function() {
	if (this.getUserProperty("defaultNum") == null
		|| (this.getUserProperty("myMobileNum") == null
		&& this.getUserProperty("myDeskNum") == null
		&& this.getUserProperty("myConfBridge") == null)) {
		return 0;
	}

	return 1;
};

// Called by the Zimbra framework when the panel item was clicked
Com_Zimbra_Asterisk.prototype.setupCall = function(myobj) {
	if (!this.verifyPrefs()) {
		this.displayStatusMessage("Please populate preferences"); 
		if (this._dlg_propertyEditor) {
			this._dlg_propertyEditor.dispose();
		}
		this._dlg_propertyEditor = null;
		this.createPropertyEditor();
		return;
	}
	var editorProps = [
		{ 
			label			: "Call From",
			name			: "myNumber",
			type			: "enum",
			width			: 100,
			value			: "",
			item			: [ ]
		}
	];

	var i = 0;
	if (this.getUserProperty("myMobileNum") != null) {
		editorProps[0].item[i] = {};
		editorProps[0].item[i].label = "Mobile: "+this.getUserProperty("myMobileNum");
		editorProps[0].item[i].value = this.getUserProperty("myMobileNum");
		i++;
	}
	if (this.getUserProperty("myDeskNum") != null) {
		editorProps[0].item[i] = {};
		editorProps[0].item[i].label = "Desk: "+this.getUserProperty("myDeskNum");
		editorProps[0].item[i].value = this.getUserProperty("myDeskNum");
		i++;
	}
	if (this.getUserProperty("myConfBridge") != null) {
		editorProps[0].item[i] = {};
		editorProps[0].item[i].label = "Bridge: "+this.getUserProperty("myConfBridge");
		editorProps[0].item[i].value = this.getUserProperty("myConfBridge");
		i++;
	}

	// if we get multiple numbers, we set this to the bridge below.

	if (this.getUserProperty(this.getUserProperty("defaultNum").toString()) != null) {
		editorProps[0].value = 
			this.getUserProperty(this.getUserProperty("defaultNum").toString()).toString();
	}

	editorProps[1] = 
		{ 
			label			: "Call To",
			name			: "number",
			type			: "text",
			value			: "Do not call"
		};

    if (myobj instanceof String) {
			editorProps[1].value = myobj.toString();
	} else if (myobj != null && typeof(myobj) == 'object') {
		if (myobj.TYPE == "ZmContact") {

			if (myobj instanceof Array) {

				var j = 1;

				for (i = 0; i < myobj.length ; i++) {
					var contact = myobj[i];
					var email = contact.email;
					if (contact == null) {

						editorProps[j] = 
							{ 
								label			: email,
								name			: email+"number",
								type			: "text",
								value			: "Do not call"
							};

					} else {

						editorProps[j] = 
							{ 
								label			: email,
								name			: email+"number",
								type			: "enum",
								width			: 100,
								value			: "",
								item			: [ ]
							};

						editorProps[j].item[0] = {};
						editorProps[j].item[0].label = "Do not call";
						editorProps[j].item[0].value = "Do not call";

						var k = 1;

						for (var p in contact) {
							if (p.slice(-5) == "Phone" && contact[p] != null) {
								var lb = p.slice(0,-5);
								editorProps[j].item[k] = {};
								editorProps[j].item[k].label = lb+": "+contact[p];
								editorProps[j].item[k].value = contact[p];
								k++;
							}
						}
						if (contact.mobilePhone != null) {
							editorProps[j].value = contact.mobilePhone.toString();
						}

					}

					j++;

				}

				// Add me (my default num, or mobile if that's the bridge).

				editorProps[j] = 
					{ 
						label			: "Me",
						name			: "Me"+"number",
						type			: "enum",
						width			: 100,
						value			: "",
						item			: [ ]
					};

				editorProps[j].item[0] = {};
				editorProps[j].item[0].label = "Do not call";
				editorProps[j].item[0].value = "Do not call";

				i = 1;
				if (this.getUserProperty("myMobileNum") != null) {
					editorProps[j].item[i] = {};
					editorProps[j].item[i].label = "Mobile: "+this.getUserProperty("myMobileNum");
					editorProps[j].item[i].value = this.getUserProperty("myMobileNum");
					i++;
				}
				if (this.getUserProperty("myDeskNum") != null) {
					editorProps[j].item[i] = {};
					editorProps[j].item[i].label = "Desk: "+this.getUserProperty("myDeskNum");
					editorProps[j].item[i].value = this.getUserProperty("myDeskNum");
					i++;
				}

				if (this.getUserProperty(this.getUserProperty("defaultNum").toString()) != null) {
					editorProps[j].value = 
						this.getUserProperty(
							this.getUserProperty("defaultNum").toString()).toString();
					if (this.getUserProperty("defaultNum").toString() == "myConfBridge") {
						if (this.getUserProperty("myMobileNum") != null) {
							editorProps[j].value = 
								this.getUserProperty("myMobileNum").toString();
						}
					} 
				}

			} else {

				editorProps[1] = 
					{ 
						label			: myobj.email,
						name			: "number",
						type			: "enum",
						width			: 100,
						value			: "",
						item			: [ ]
					};

				i = 0;
				for (var p in myobj) {
					if (p.slice(-5) == "Phone" && myobj[p] != null) {
						var lb = p.slice(0,-5);
						editorProps[1].item[i] = {};
						editorProps[1].item[i].label = lb+": "+myobj[p];
						editorProps[1].item[i].value = myobj[p];
						i++;
					}
				}
				if (myobj.mobilePhone != null) {
					editorProps[1].value = myobj.mobilePhone.toString();
				}

			}

		} else if (myobj.TYPE == "ZmAppt") {
			
			var attendees = myobj.attendees;
			
			if (attendees != null && attendees != "") {

				if (this._contacts == null) {
					this._contacts = AjxDispatcher.run("GetContacts");
				}

				var ar = attendees.split(/\s*;\s*/);

				var j = 1;

				for (i = 0; i < ar.length ; i++) {
					var email = ar[i];
					var jj = email.indexOf("<",0);
					if (jj != -1) {
						var k = email.indexOf(">",jj);
						email = email.slice (jj+1,k);
					}

					var contact = this._contacts.getContactByEmail(email);
					if (contact == null) {

						editorProps[j] = 
							{ 
								label			: email,
								name			: email+"number",
								type			: "text",
								value			: "Do not call"
							};

					} else {

						editorProps[j] = 
							{ 
								label			: email,
								name			: email+"number",
								type			: "enum",
								width			: 100,
								value			: "",
								item			: [ ]
							};

						editorProps[j].item[0] = {};
						editorProps[j].item[0].label = "Do not call";
						editorProps[j].item[0].value = "Do not call";

						var k = 1;

						for (var p in contact.attr) {
							if (p.slice(-5) == "Phone" && contact.attr[p] != null) {
								var lb = p.slice(0,-5);
								editorProps[j].item[k] = {};
								editorProps[j].item[k].label = lb+": "+contact.attr[p];
								editorProps[j].item[k].value = contact.attr[p];
								k++;
							}
						}
						if (contact.attr.mobilePhone != null) {
							editorProps[j].value = contact.attr.mobilePhone.toString();
						}

					}

					j++;

				}

				// Add the organizer (my default num, or mobile if that's the bridge.

				editorProps[j] = 
					{ 
						label			: "Organizer",
						name			: "Organizer"+"number",
						type			: "enum",
						width			: 100,
						value			: "",
						item			: [ ]
					};

				editorProps[j].item[0] = {};
				editorProps[j].item[0].label = "Do not call";
				editorProps[j].item[0].value = "Do not call";

				i = 1;
				if (this.getUserProperty("myMobileNum") != null) {
					editorProps[j].item[i] = {};
					editorProps[j].item[i].label = "Mobile: "+this.getUserProperty("myMobileNum");
					editorProps[j].item[i].value = this.getUserProperty("myMobileNum");
					i++;
				}
				if (this.getUserProperty("myDeskNum") != null) {
					editorProps[j].item[i] = {};
					editorProps[j].item[i].label = "Desk: "+this.getUserProperty("myDeskNum");
					editorProps[j].item[i].value = this.getUserProperty("myDeskNum");
					i++;
				}

				if (this.getUserProperty(this.getUserProperty("defaultNum").toString()) != null) {
					editorProps[j].value = 
						this.getUserProperty(
							this.getUserProperty("defaultNum").toString()).toString();
					if (this.getUserProperty("defaultNum").toString() == "myConfBridge") {
						if (this.getUserProperty("myMobileNum") != null) {
							editorProps[j].value = 
								this.getUserProperty("myMobileNum").toString();
						}
					} 
				}

			}

		} 
	} else {
		editorProps[1].value = "";
	}

	if (editorProps.length > 2) {
		if (this.getUserProperty("myConfBridge") != null) {
			editorProps[0].value = 
				this.getUserProperty("myConfBridge").toString();
		}
	}

	// Hack to reset after a cancel.
	if (this._dlg_propertyEditor) {
		this._dlg_propertyEditor.dispose();
	}
	this._dlg_propertyEditor = null;

	var view = new DwtComposite(this.getShell());
	this._propertyEditor = new DwtPropertyEditor(view, true);
	var pe = this._propertyEditor;
	pe.initProperties(editorProps);
	var dialog_args = {
		title : "Place Call",
		view  : view
	};
	this._dlg_propertyEditor = this._createDialog(dialog_args);
	var dlg = this._dlg_propertyEditor;
	pe.setFixedLabelWidth();
	pe.setFixedFieldWidth();
	dlg.setButtonListener(DwtDialog.OK_BUTTON,
						new AjxListener(this, function() {
							if (!pe.validateData()) {
								return;
							}
							this._placeCall();
						}));

	this._dlg_propertyEditor.popup();
};

Com_Zimbra_Asterisk.prototype._placeCall = function () {
	var properties = this._propertyEditor.getProperties();

	this._dlg_propertyEditor.popdown();
	this._dlg_propertyEditor.dispose();
	this._dlg_propertyEditor = null;

	//this.displayStatusMessage('encoding'); 
	var url = this.getResource('asterisk.jsp');

	var uname = AjxStringUtil.urlEncode(this.getUserProperty('pbxUname'));
	var pass = AjxStringUtil.urlEncode(this.getUserProperty('pbxPass'));

	var reqHeader = {"Content-Type":"application/x-www-form-urlencoded"};

	var mynum = AjxStringUtil.urlEncode(properties.myNumber);

	var conf = this.getUserProperty("myConfBridge");

	var from = AjxStringUtil.urlEncode(this.fixNumber(mynum));

	var finalto = "";
	for (var callee in properties) {
		if (callee == "myNumber") {
			continue;
		}
		var to = properties[callee];

		if (to == "Do not call") {
			continue;
		}

		// If it's a conf bridge, connect to the non-bridge number first
		/*
		if (mynum == conf) {
			var tmp = to;
			to = conf;
			from = tmp;
		} 
		*/

		if (finalto == "") {
			finalto = this.fixNumber(to);
		} else {
			finalto = finalto+";"+this.fixNumber(to);
		}

	}
	finalto = AjxStringUtil.urlEncode(finalto);
	var reqParam = 'to=' + finalto + '&from=' + from + '&uname=' + uname + '&pass=' + pass;
	//DBG.println(AjxDebug.DBG2, reqParam);

	this.displayStatusMessage('Connecting '+from+' to '+finalto); 
	AjxRpc.invoke(reqParam, url, reqHeader, new AjxCallback(this, this._resultCallback));
};

Com_Zimbra_Asterisk.prototype._resultCallback = function(result) {
	var r = result.text;
	//DBG.println(AjxDebug.DBG2, "result:" + r);
	this.displayStatusMessage(r); 
};

