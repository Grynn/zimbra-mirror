/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

function Com_Zimbra_Phone() {
}

Com_Zimbra_Phone.prototype = new ZmZimletBase();
Com_Zimbra_Phone.prototype.constructor = Com_Zimbra_Phone;

Com_Zimbra_Phone.prototype.match =
function(line, startIndex) {
	var a = this.regexps;
	var ret = null;
	for (var i = 0; i < a.length; ++i) {
		var re = a[i];
		re.lastIndex = startIndex;
		var m = re.exec(line);
        if (m) {
            if (!ret || m.index < ret.index) {
                ret = m;
            }
        }
	}
	return ret;
};

Com_Zimbra_Phone.prototype.init =
function() {
	var regexps = [
        new RegExp(this.getMessage("northAmericanNumberingPlan"),"ig"),
        new RegExp(this.getMessage("genericInternational"), "ig")
    ];
    var localNumbers = this.getMessage("localNumbers");
    if (localNumbers && localNumbers != "###") {
        regexps.push(new RegExp(localNumbers, "ig"));
    }
	this.regexps = regexps;
};
/**
* Not needed anymore as labels are refered from zimlet specific props file now
* Com_Zimbra_Phone.prototype.getActionMenu =
	function(obj, span, context) {
        var actionMenu = ZmZimletBase.prototype.getActionMenu.call(this, obj, span, context);

        var op = actionMenu.getOp("SEARCH");
        if (op) {
            op.setText(ZmMsg.search);
        }
        op = actionMenu.getOp("ADDCONTACT");
        if (op) {
            op.setText(ZmMsg.AB_ADD_CONTACT);
        }
        op = actionMenu.getOp("CALL");
        if (op) {
            op.setText(ZmMsg.call);
        }
        return actionMenu;
    };
*/
Com_Zimbra_Phone.prototype._getHtmlContent =
function(html, idx, phone, context) {
	var call = Com_Zimbra_Phone.getCallToLink(phone);

	html[idx++] = [
			'<a href="',
			call,
			'" onclick="window.top.Com_Zimbra_Phone.unsetOnbeforeunload()">',
			AjxStringUtil.htmlEncode(phone),
			'</a>'
	].join("");

	return idx;
};

Com_Zimbra_Phone.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var subs = {contentObjText: contentObjText};
	canvas.innerHTML = AjxTemplate.expand("com_zimbra_phone.templates.Phone#Tooltip", subs);
};

Com_Zimbra_Phone.prototype.menuItemSelected =
function(itemId) {
	switch (itemId) {
		case "SEARCH":		this._searchListener(); break;
		case "ADDCONTACT":	this._contactListener(); break;
		case "CALL":		this._callListener(); break;
	}
};

Com_Zimbra_Phone.prototype._searchListener =
function() {
	appCtxt.getSearchController().search({query: this._actionObject});
};

Com_Zimbra_Phone.prototype._contactListener =
function() {
	var contact = new ZmContact(null);
	contact.initFromPhone(this._actionObject,this.getConfig("defaultContactField"));
	AjxDispatcher.run("GetContactController").show(contact);
};

Com_Zimbra_Phone.prototype._callListener =
function() {
	var phone = Com_Zimbra_Phone.getCallToLink(this._actionObject.toString());
	Com_Zimbra_Phone.unsetOnbeforeunload();
	window.location = phone;
};

Com_Zimbra_Phone.resetOnbeforeunload =
function() {
	window.onbeforeunload = ZmZimbraMail._confirmExitMethod;
};

Com_Zimbra_Phone.unsetOnbeforeunload =
function() {
	window.onbeforeunload = null;
	this._timerObj = new AjxTimedAction(null, Com_Zimbra_Phone.resetOnbeforeunload);
	AjxTimedAction.scheduleAction(this._timerObj, 3000);
};

Com_Zimbra_Phone.getCallToLink =
function(phoneIn) {
	if (!phoneIn) { return ""; }

	var phone = AjxStringUtil.trim(phoneIn, true);
	if (!/^(?:\+|00)/.test(phone)) {
		phone = "+1" + phone;
	}
	return "callto:" + phone;
};
