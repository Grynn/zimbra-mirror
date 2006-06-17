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
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

//////////////////////////////////////////////////////////////
//  Zimlet to handle SMS alerts                             //
//  @author Satish Dharmaraj                                //
//////////////////////////////////////////////////////////////

function Com_Zimbra_sms() {
}

Com_Zimbra_sms.prototype = new ZmZimletBase();
Com_Zimbra_sms.prototype.constructor = Com_Zimbra_sms;

// Panel Zimlet Methods
// Called by the Zimbra framework upon an accepted drag'n'drop
Com_Zimbra_sms.prototype.doDrop = 
function(obj) {
	switch (obj.TYPE) {
	    case "ZmContact":
		this._contactDropped(obj);
		break;
	    case "ZmMailMsg":
		this._msgDropped(obj.from, obj);
		break;
	    case "ZmAppt":
		this._apptDropped(obj);
		break;
	    case "ZmConv":
		var from = obj.participants[obj.participants.length - 1];
		this._msgDropped(from, obj);
		break;

	    default:
		this.displayErrorMessage("You somehow managed to drop a \"" + obj.TYPE + "\" but however the SMS Zimlet does't support it for drag'n'drop.");
	}
};

// Called by the Zimlet framework when the SMS panel item was double clicked
Com_Zimbra_sms.prototype.init = function() {
	if (ZmAssistant && ZmAssistant.register) ZmAssistant.register(new Com_Zimbra_sms_Asst(this._appCtxt, this));
};

// Called by the Zimlet framework when the SMS panel item was double clicked
Com_Zimbra_sms.prototype.doubleClicked = function() {
	this.singleClicked();
};

// Called by the Zimlet framework when the SMS panel item was single clicked
Com_Zimbra_sms.prototype.singleClicked = function(toValue, bodyValue) {
	var view = new DwtComposite(this.getShell());
	var el = view.getHtmlElement();
	var div = document.createElement("div");
	var toId = Dwt.getNextId();
	var bodyId = Dwt.getNextId();
	
	if (bodyValue) {
		// replace any appostrophes ...to avoid catastrophe ..
		bodyValue = bodyValue.replace(/\x27/, ""); 
		bodyValue = AjxStringUtil.htmlEncode(bodyValue);
		DBG.println(AjxDebug.DBG2, "body: " + bodyValue);
	}
	if (!toValue) {
		toValue = this.getUserProperty("cellNum");
	}
	div.innerHTML =
		[ "<table><tbody>",
		  "<tr>",
		  "<td align='right'><label for='", toId, "'>Cell:</td>",
		  "<td>",
		  "<input autocomplete='off' style='width:21em' type='text' id='", toId, "' value='", toValue, "'/>",
		  "</td>",
		  "</tr>",
          "<tr>",
		  "<td colspan='2'>",
		  "<textarea style='width:25em;height:50px' id='", bodyId, "'>", bodyValue,  "</textarea>",
		  "</td>",
		  "</tr></tbody></table>" ].join("");
	el.appendChild(div);

	var dialog_args = {
		title : "Send SMS Zimlet",
		view  : view
	};
	var dlg = this._createDialog(dialog_args);
	dlg.popup();

	if (!bodyValue) {
		el = document.getElementById(bodyId);
		el.select();
		el.focus();
	} else {
		el = document.getElementById(toId);
		el.select();
		el.focus();
	}

	dlg.setButtonListener(DwtDialog.OK_BUTTON,
		      new AjxListener(this, function() {
			      this._sendSMS(document.getElementById(toId).value, document.getElementById(bodyId).value);
			      dlg.popdown();
			      dlg.dispose();
		      }));

	dlg.setButtonListener(DwtDialog.CANCEL_BUTTON,
		      new AjxListener(this, function() {
			      dlg.popdown();
			      dlg.dispose();
		      }));
};

Com_Zimbra_sms.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
	    case "PREFERENCES":
		this.createPropertyEditor();
		break;
        }
};

// Private Methods
Com_Zimbra_sms.prototype._apptDropped = function(appt) {

	var body = "time: " + appt.startDate;
	if (appt.location) {
		body = body + " location: " + appt.location;
	}
	if (appt.notes) {
		body = body + " - " + appt.notes;
	}
	this.singleClicked(null, body);
};

Com_Zimbra_sms.prototype._msgDropped = function(from, note) {

	var body = "from: " + from + " subject: " + note.subject;
	if (note.fragment) {
		body = body + "-" + note.fragment;
	}
	this.singleClicked(null, body);
};

Com_Zimbra_sms.prototype._contactDropped = 
function(contact) {

	var cf = contact.firstName?contact.firstName:" ";
	var cl = contact.lastName?contact.lastName:" ";
	var ce = contact.email?contact.email:" ";
	var chp = contact.homePhone?" h:" + contact.homePhone:" ";
	var cwp = contact.workPhone?" w:" + contact.workPhone:" ";
	var cmp = contact.mobilePhone?" c:" + contact.mobilePhone:" ";
	var body = cf + " " + cl + " " + ce + chp + cwp + cmp;
	if (contact.homeStreet) {
		var chst = contact.homeState?contact.homeState:" ";
		var chz =  contact.homePostalCode?contact.homePostalCode:" ";
		body  =  body + " addr:" + contact.homeStreet + " " + chst + " " + chz;
	}
	this.singleClicked(contact.mobilePhone, body);
};
	
Com_Zimbra_sms.prototype._sendSMS = 
function(to, body) {
	var url = this.getResource('sms.jsp');

	to = AjxStringUtil.urlEncode(to);
	body = AjxStringUtil.urlEncode(body);
	var reqParam = 'to=' + to + '&body=' + body;
	var reqHeader = {"Content-Type":"application/x-www-form-urlencoded"};

	AjxRpc.invoke(reqParam, url, reqHeader, new AjxCallback(this, this._resultCallback));
};

Com_Zimbra_sms.prototype._resultCallback=
function(result) {
	var r = result.text;
	DBG.println(AjxDebug.DBG2, "result:" + r);
	this.displayStatusMessage(r);
};


//////////////////////////////////////////////////////////////////////////
// Zimlet assistant class
// - used by the Assistant dialog to run games via "command-line"
//////////////////////////////////////////////////////////////////////////
function Com_Zimbra_sms_Asst(appCtxt, zimlet) {
	if (arguments.length == 0) return;
	// XXX: localize later (does NOT belong in ZmMsg.properties)
	ZmAssistant.call(this, appCtxt, "Send SMS", "sms");
	this._zimlet = zimlet;
};

Com_Zimbra_sms_Asst.prototype = new ZmAssistant();
Com_Zimbra_sms_Asst.prototype.constructor = Com_Zimbra_sms_Asst;

Com_Zimbra_sms_Asst.prototype.okHandler =
function(dialog) {
	// get reference to the sms zimlet
	var zm = this._appCtxt.getSettings().getZimletManager();
	var zimlet = zm ? zm._ZIMLETS_BY_ID["com_zimbra_sms"] : null;
	if (zimlet && this._body) {
		var toValue = (this._to != null) ? this._to : zimlet.handlerObject.getUserProperty("cellNum");		
		//alert(toValue+":"+this._body);
		zimlet.handlerObject._sendSMS(toValue, this._address);
	}
	return true;
};

Com_Zimbra_sms_Asst.prototype.handle =
function(dialog, verb, args) {

	var match = this._objectManager.findMatch(args, ZmObjectManager.PHONE);
	if (match != null && match != "") {
		this._to = match[0];
		args = args.replace(match[0], " ");	
	} else {
		this._to = this._zimlet.getUserProperty("cellNum");
	}
	this._body = args.replace(/^\s+/, '').replace(/\s+$/, '');

	dialog._setOkButton("Send SMS", true, this._body != "");
	this._setField("Cell", this._to == null ? "enter a phone number" : this._to, this._to == null, true, 0);
	this._setField("Text", this._body == "" ? "just type to enter text message": this._body, this._body == null, true, 1);	
};
