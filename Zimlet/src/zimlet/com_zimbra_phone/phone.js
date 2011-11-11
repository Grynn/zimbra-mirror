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

// Consts
Com_Zimbra_Phone.PEOPLE_SEARCH_TOOLBAR_ID	= "phone";



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
	var subs = {contentObjText: contentObjText, callStr: this.getMessage("call")};
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

Com_Zimbra_Phone.prototype.onPeopleSearchShow =
function(peopleSearch, contact, rowId) {
    peopleSearch._clearText(rowId+"-phone");
	var phone = contact && contact.getAttr(ZmContact.F_workPhone);
    if(phone){
        var phoneTxt = new DwtText({parent:appCtxt.getShell(), parentElement:rowId+"-phone", index:0, id:"NewCall", className:"FakeAnchor"});
        phoneTxt.isLinkText = true;
        phoneTxt.setText(phone);
        phoneTxt.addListener(DwtEvent.ONMOUSEDOWN, new AjxListener(this, this._peopleSearchItemListener));
        phoneTxt.addListener(DwtEvent.ONMOUSEOVER, new AjxListener(this, peopleSearch.peopleItemMouseOverListener));
        phoneTxt.addListener(DwtEvent.ONMOUSEOUT, new AjxListener(this, peopleSearch.peopleItemMouseOutListener));
    }

};

Com_Zimbra_Phone.prototype._peopleSearchItemListener =
function(ev) {
	var workPhone = ev.target.innerHTML;
	var phone = Com_Zimbra_Phone.getCallToLink(workPhone);
	Com_Zimbra_Phone.unsetOnbeforeunload();
	window.location = phone;
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
		if(this.countryCode == 1) {//use countrycode(when its missing) only for US(for now)
			phone = "+1" + phone;
		}
	}
	return "callto:" + phone;
};
