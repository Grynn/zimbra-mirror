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
 * Portions created by Zimbra are Copyright (C) 2006, 2007 Zimbra, Inc.
 * All Rights Reserved.
 *
 * Contributor(s):
 *
 * ***** END LICENSE BLOCK *****
 */

function Com_Zimbra_Email() {
}

Com_Zimbra_Email.prototype = new ZmZimletBase();
Com_Zimbra_Email.prototype.constructor = Com_Zimbra_Email;

Com_Zimbra_Email.prototype.init =
function() {
	if (appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		this._contacts = AjxDispatcher.run("GetContacts");

		this._composeTooltipHint = ZmMsg.leftClickComposeHint + "<br>" + ZmMsg.rightClickHint;
		this._newTooltipHint = ZmMsg.leftClickNewContactHint + "<br>" + ZmMsg.rightClickHint;
	} else {
		this._newTooltipHint = ZmMsg.leftClickComposeHint + "<br>" + ZmMsg.rightClickHint;
	}
};

Com_Zimbra_Email.prototype._getHtmlContent =
function(html, idx, obj) {
	var content;
	if (obj instanceof AjxEmailAddress) {
		if (this._contacts && appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
			var contact = this._contacts.getContactByEmail(obj.address);
			if (contact) {
				content = contact.getFullName();
			}
		}
		if (!content) {
			content = obj.toString();
		}
	} else {
		content = obj;
	}
	html[idx++] = AjxStringUtil.htmlEncode(content);
	return idx;
};

Com_Zimbra_Email.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	
	var toolTip;
	var addr = (contentObjText instanceof AjxEmailAddress)
		? contentObjText.address : contentObjText;
	
	if(this.isMailToLink(addr)){
		addr = (this.parseMailToLink(addr)).to || addr;
	}	
	
	var contact = this._contacts ? this._contacts.getContactByEmail(addr) : null;
	if (contact) {
		toolTip = contact.getToolTip(addr, false, this._composeTooltipHint);
	} else {
		var subs = {
			addrstr: addr.toString(),
			hint: this._newTooltipHint
		};
		toolTip = AjxTemplate.expand("abook.Contacts#TooltipNotInAddrBook", subs);
	}
	canvas.innerHTML = toolTip;
};

Com_Zimbra_Email.prototype.getActionMenu =
function(obj, span, context) {
	
	// call base class first to get the action menu
	var actionMenu = ZmZimletBase.prototype.getActionMenu.call(this, obj, span, context);

	if (!appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		// make sure to remove adding new contact menu item if contacts are disabled
		if (actionMenu.getOp("NEWCONTACT"))
			ZmOperation.removeOperation(actionMenu, "NEWCONTACT", actionMenu._menuItems);
	} else {
		// bug fix #5262 - Change action menu item for contact depending on whether
		// email address is found in address book or not.
		if (this._contacts) {
			var addr = (obj instanceof AjxEmailAddress) ? obj.getAddress() : obj;
			if(this.isMailToLink(addr)){
				addr = (this.parseMailToLink(addr)).to || addr;
			}
			var found = (this._contacts.getContactByEmail(addr) != null);
			var newOp = found ? ZmOperation.EDIT_CONTACT : ZmOperation.NEW_CONTACT;
			var newText = found ? null : ZmMsg.AB_ADD_CONTACT;
			ZmOperation.setOperation(actionMenu, "NEWCONTACT", newOp, newText);
		}
	}
	if (actionMenu.getOp("SEARCH") && !appCtxt.get(ZmSetting.SEARCH_ENABLED)) {
		ZmOperation.removeOperation(actionMenu, "SEARCH", actionMenu._menuItems);
	}
	if (actionMenu.getOp("SEARCHBUILDER") && !appCtxt.get(ZmSetting.BROWSE_ENABLED)) {
		ZmOperation.removeOperation(actionMenu, "SEARCHBUILDER", actionMenu._menuItems);
	}
	if (actionMenu.getOp("NEWFILTER") && !appCtxt.get(ZmSetting.FILTERS_ENABLED)) {
		ZmOperation.removeOperation(actionMenu, "NEWFILTER", actionMenu._menuItems);
	}

	return actionMenu;
};

Com_Zimbra_Email.prototype.isMailToLink = function (str){
	if(str.search(/mailto/i) != -1){
		return true;
	}
	return false;
};

Com_Zimbra_Email.prototype.parseMailToLink = function(str){
		
	var parts = {};
	
	var match = str.match(/\bsubject=([^&]+)/);
	parts.subject = match ? decodeURIComponent(match[1]) : null;
	match = str.match(/\bto\:([^&]+)/);
	if(!match) match = str.match(/\bmailto\:([^\?]+)/i);
	parts.to = match ? decodeURIComponent(match[1]) : null;
	match = str.match(/\bbody=([^&]+)/);
	parts.body = match ? decodeURIComponent(match[1]) : null;
	
	return parts;
};

Com_Zimbra_Email.prototype.clicked =
function(spanElement, contentObjText, matchContext, ev) {
	
	var addr = (contentObjText instanceof AjxEmailAddress) 
		? contentObjText.address : contentObjText;
	
	var contact = this._contacts ? this._contacts.getContactByEmail(addr) : null;
	// if contact found or there is no contact list (i.e. contacts app is disabled), go to compose view
	if (contact || this._contacts == null || (AjxUtil.isString(addr) && this.isMailToLink(addr)) ) {
		this._composeListener(ev,addr);
	} else {
		// otherwise, no contact in addrbook means go to contact edit view
		this._actionObject = contentObjText;
		this._contactListener();
	}
};

Com_Zimbra_Email.prototype.menuItemSelected =
function(itemId, item, ev) {
	switch (itemId) {
		case "SEARCH":
			this._searchListener();
			break;
		case "SEARCHBUILDER":
			this._browseListener();
			break;
		case "NEWEMAIL":
			this._composeListener(ev);
			break;
		case "NEWCONTACT":
			this._contactListener();
			break;
		case "NEWFILTER":
			this._filterListener();
			break;
		case "GOTOURL":
			this._goToUrlListener();
			break;
	}
};

Com_Zimbra_Email.prototype._getAddress =
function(obj) {
	if (obj.constructor == AjxEmailAddress) {
		return obj.address;
	} else {
		return obj;
	}
};

Com_Zimbra_Email.prototype._contactListener =
function() {
	var loadCallback = new AjxCallback(this, this._handleLoadContact);
	AjxDispatcher.require(["ContactsCore", "Contacts"], false, loadCallback, null, true);
};

Com_Zimbra_Email.prototype._handleLoadContact =
function() {
	// actionObject can be a ZmContact, a String, or a generic Object (phew!)
	var contact;
	var addr = this._actionObject;
	if (this._actionObject) {
		if (this._actionObject instanceof ZmContact) {
			contact = this._actionObject;
		} else if (AjxUtil.isString(this._actionObject)) {
			addr  = this._getAddress(this._actionObject);
			if(this.isMailToLink(addr)){
				addr = (this.parseMailToLink(addr)).to || addr;
			}
			contact = AjxDispatcher.run("GetContacts").getContactByEmail(addr)
		} else {
			contact = AjxDispatcher.run("GetContacts").getContactByEmail(this._actionObject.address);
		}
	}

	if (contact == null) {
		contact = new ZmContact(null);
		contact.initFromEmail(addr);
	}

	AjxDispatcher.run("GetContactController").show(contact);
};

Com_Zimbra_Email.prototype._composeListener =
function(ev,addr) {
		
	addr = (this._actionObject) ? this._getAddress(this._actionObject) : addr ;
	if(!addr) addr = "";
	var params = {};
	
	var inNewWindow = (!appCtxt.get(ZmSetting.NEW_WINDOW_COMPOSE) && ev && ev.shiftKey) ||
					  (appCtxt.get(ZmSetting.NEW_WINDOW_COMPOSE) && ev && !ev.shiftKey);
		
	if(this.isMailToLink(addr)){
		var mailToParams = this.parseMailToLink(addr);
		params.toOverride    = mailToParams.to;
		params.subjOverride  = mailToParams.subject; 
		params.extraBodyText = mailToParams.body;
		addr = mailToParams.to || addr;
	}
	
	params.action 			= ZmOperation.NEW_MESSAGE;
	params.inNewWindow 		= inNewWindow;
	if(!params.toOverride)
		params.toOverride 	= addr + AjxEmailAddress.SEPARATOR;
					  
	AjxDispatcher.run("Compose", params );
};



Com_Zimbra_Email.prototype._browseListener =
function() {
	var addr  = this._getAddress(this._actionObject);
	if(this.isMailToLink(addr)){
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	appCtxt.getSearchController().fromBrowse(addr);
};

Com_Zimbra_Email.prototype._searchListener =
function() {
	var addr  = this._getAddress(this._actionObject);
	if(this.isMailToLink(addr)){
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	appCtxt.getSearchController().fromSearch(this._getAddress(addr));
};

Com_Zimbra_Email.prototype._filterListener =
function() {
	var loadCallback = new AjxCallback(this, this._handleLoadFilter);
	AjxDispatcher.require(["PreferencesCore", "Preferences"], false, loadCallback, null, true);
};

Com_Zimbra_Email.prototype._handleLoadFilter =
function() {
	appCtxt.getAppViewMgr().popView(true, ZmController.LOADING_VIEW);	// pop "Loading..." page
	var rule = new ZmFilterRule();
	
	var addr  = this._getAddress(this._actionObject);
	if(AjxUtil.isString(addr) && this.isMailToLink(addr)){
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	rule.addCondition(new ZmCondition(ZmFilterRule.C_FROM, ZmFilterRule.OP_IS, addr ));
	rule.addAction(new ZmAction(ZmFilterRule.A_KEEP));
	var dialog = appCtxt.getFilterRuleDialog();
	dialog.popup(rule);
}

Com_Zimbra_Email.prototype._goToUrlListener =
function() {
	var addr  = this._getAddress(this._actionObject);
	if(AjxUtil.isString(addr) && this.isMailToLink(addr)){
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	var parts = addr.split("@");
	if (parts.length) {
		var domain = parts[parts.length - 1];
		var pieces = domain.split(".");
		var url = (pieces.length <= 2) ? 'www.' + domain : domain;
		this._actionUrl = "http://" + url;
	}
	if (this._actionUrl) {
		window.open(this._actionUrl, "_blank", "menubar=yes,resizable=yes,scrollbars=yes");
	} else {
		this.displayStatusMessage("Unable to create URL from email.");
	}
};