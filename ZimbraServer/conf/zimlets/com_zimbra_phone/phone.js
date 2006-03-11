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
 * Portions created by Zimbra are Copyright (C) 2005 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_Phone() {
}

Com_Zimbra_Phone.prototype = new ZmZimletBase();
Com_Zimbra_Phone.prototype.constructor = Com_Zimbra_Phone;

Com_Zimbra_Phone.prototype.match =
function(line, startIndex) {
	this.RE.lastIndex = startIndex;

	var m = this.RE.exec(line);
	if (m) {
		if (m[1] !== "" || m[2] !== "") {
			var from = 0;
			var to = m[0].length;
			if (m[1] !== "") {
				from++;
			}
			if (m[2] !== "") {
				to--;
			}
			var m2 = {index: m.index + from};
			m2[0] = m[0].substring(from, to);
			m = m2;
		}
	}
	return m;
};

Com_Zimbra_Phone.prototype._getHtmlContent =
function(html, idx, phone, context) {
	var call = Com_Zimbra_Phone.getCallToLink(phone);
	html[idx++] = '<a href="' + call + '" onclick="window.top.Com_Zimbra_Phone.unsetOnbeforeunload()">' + AjxStringUtil.htmlEncode(phone) + '</a>';
	return idx;
};

Com_Zimbra_Phone.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var html = [];
	var i = 0;
	html[i++] = "<table cellpadding=2 cellspacing=0 border=0><tr valign='center'>";
	html[i++] = "<td>";
	html[i++] = AjxImg.getImageHtml("Telephone");
	html[i++] = "</td>";
	html[i++] = "<td><b><div style='white-space:nowrap'>" + "Phone Number:" + "</div></b></td>";
	html[i++] = "<td><div style='white-space:nowrap'>" + AjxStringUtil.htmlEncode(contentObjText) + "</div></td></tr></table>";
	canvas.innerHTML =  html.join("");
};

Com_Zimbra_Phone.prototype.menuItemSelected = function(itemId) {
	switch (itemId) {
		case "SEARCH":
			this._searchListener();
			break;
		case "ADDCONTACT":
			this._contactListener();
			break;
		case "CALL":
			this._callListener();
			break;
	}
};

Com_Zimbra_Phone.prototype._searchListener =
function() {
	this._appCtxt.getSearchController().search({query: this._actionObject});
};

Com_Zimbra_Phone.prototype._contactListener =
function() {
	var contact = new ZmContact(this._appCtxt);
	contact.initFromPhone(this._actionObject);
	this._appCtxt.getApp(ZmZimbraMail.CONTACTS_APP).getContactController().show(contact);
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

// TODO: Regex assumes 10 or 11 digit US number.  Need to support intl numbers.
Com_Zimbra_Phone.getCallToLink =
function(phoneIn) {
	if (!phoneIn) {
		return "";
	}
	var phone = AjxStringUtil.trim(phoneIn, true);
	return 'callto:+1' + phone.replace('+1', '');
};