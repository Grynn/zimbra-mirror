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
		if (m && (ret == null || m.index < ret.index)) {
			if (re.useParen) {
				for (var j = 1; j < re.useParen; ++j)
					m.index += m[j].length;
				m[0] = m[re.useParen];
			}
			if (!/^000/.test(m[0]))
				ret = m;
		}
	}
	return ret;
};

Com_Zimbra_Phone.prototype.init =
function() {
	var regexps = [];
	var o = this.xmlObj().contentObject.matchOn[0];
	var a = o.regex;
	for (var i = 0; i < a.length; ++i) {
		o = a[i];
		var attrs = o.attrs;
		if (!attrs)
			attrs = "ig";
		var re = new RegExp(o._content, attrs);
		if (o.paren != null)
			re.useParen = parseInt(o.paren);
		regexps.push(re);
	}
	this.regexps = regexps;
};
Com_Zimbra_Phone.prototype.getActionMenu =
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
