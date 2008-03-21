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

function Com_Zimbra_Email() {
}

Com_Zimbra_Email.prototype = new ZmZimletBase();
Com_Zimbra_Email.prototype.constructor = Com_Zimbra_Email;

Com_Zimbra_Email.prototype.init =
function() {
	if (appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		this._composeTooltipHint = ZmMsg.leftClickComposeHint + "<br>" + ZmMsg.rightClickHint;
		this._newTooltipHint = ZmMsg.leftClickNewContactHint + "<br>" + ZmMsg.rightClickHint;

		if (appCtxt.get(ZmSetting.IM_ENABLED)) {
			this._presenceCache = [];
		}
	} else {
		this._newTooltipHint = ZmMsg.leftClickComposeHint + "<br>" + ZmMsg.rightClickHint;
	}
};

Com_Zimbra_Email.prototype._getRoster =
function() {
	if (!this._roster && appCtxt.get(ZmSetting.IM_ENABLED) && ZmImApp.loggedIn()) {
		this._roster = AjxDispatcher.run("GetRoster");
		var list = this._roster.getRosterItemList();
		list.addChangeListener(new AjxListener(this, this._rosterChangeListener));
	}
	return this._roster;
};

Com_Zimbra_Email.prototype._rosterChangeListener =
function(ev) {
	if (ev.event != ZmEvent.E_MODIFY) { return; }

	var fields = ev.getDetail("fields");
	var doPresence = ZmRosterItem.F_PRESENCE in fields;
	if (doPresence) {
		var buddies = ev.getItems();
		var hash = {};
		for (var i = buddies.length; --i >= 0;) {
			var b = buddies[i];
			hash[b.getAddress()] = b;
		}
		var cache = this._presenceCache;
		for (var i = cache.length; --i >= 0;) {
			var el = cache[i];
			var b = hash[el.im_addr];
			if (b) {
				// try to update presence state
				var img = document.getElementById(el.img_id);
				if (img) {
					AjxImg.setImage(img, b.getPresence().getIcon(), true);
				} else {
					// no longer visible, remove from cache?
					// cache.splice(i, 1);
					// better not: will fail if we collapse/expand headers
				}
			}
		}
	}
};

/*
 * Look for the contact and buddy for the email address string.  
 */
Com_Zimbra_Email.prototype._lookup =
function(address) {
	var result = {
		contact: null,
		buddy: null
	};
	// bug fix #25215 - always fetch contact list in case of multi-account
	var contactList = AjxDispatcher.run("GetContacts");
	if (contactList && appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		result.contact = contactList.getContactByEmail(address);
	}
	if (!result.buddy && appCtxt.get(ZmSetting.IM_ENABLED)) {
		if (result.contact) {
			result.buddy = result.contact.getBuddy();
		}
		if (!result.buddy) {
			result.buddy = AjxDispatcher.run("GetRoster").getRosterItem(address);
		}
	}
	return result;
};

Com_Zimbra_Email.prototype._getHtmlContent =
function(html, idx, obj) {
	var content;
	if (obj instanceof AjxEmailAddress) {
		var person = this._lookup(obj.address);
		var contactList = AjxDispatcher.run("GetContacts");
		if (contactList && appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
			var content;

			if (person.buddy) {
				var pres = person.buddy.getPresence();
				var pres_id = Dwt.getNextId();
				var tmp = [
					AjxStringUtil.htmlEncode(person.buddy.getDisplayName()),
					" ",
					AjxImg.getImageHtml(pres.getIcon(), "display: inline; padding: 1px 8px;", "id=" + pres_id)
				];
				content = tmp.join("");
				var params = {
					contact : person.contact,
					buddy   : person.buddy,
					im_addr : person.buddy.getAddress(),
					img_id  : pres_id
				};
				this._presenceCache.push(params);

				if (this._presenceCache.length > 50) {
					// 50 should be enough.. maybe should be even smaller?
					this._presenceCache.splice(0, 1);
				}

				this._getRoster();
			}
			else if (person.contact) {
				content = AjxStringUtil.htmlEncode(person.contact.getFullName());
			}
		}
		if (!content) {
			content = AjxStringUtil.htmlEncode(obj.toString());
		}
	} else {
		content = AjxStringUtil.htmlEncode(obj);
	}
	html[idx++] = content;
	return idx;
};

Com_Zimbra_Email.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {

	var addr = (contentObjText instanceof AjxEmailAddress)
		? contentObjText.address : contentObjText;
	var name = (contentObjText instanceof AjxEmailAddress)
		? contentObjText.dispName : contentObjText;

	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}

	var toolTip;

	// @yahoo.com love
	var parts = addr.split("@");
	var domain = (parts.length > 0) ? parts[1] : null;
	var isYahoo = (domain && domain == "yahoo.com");

	var contactList = AjxDispatcher.run("GetContacts");
	var contact = contactList ? contactList.getContactByEmail(addr) : null;
	if (contact) {
		var hint = isYahoo ? this._getYahooHint(name) : this._composeTooltipHint;
		toolTip = contact.getToolTip(addr, false, hint);
	} else {
		var hint = isYahoo ? this._getYahooHint(name) : this._newTooltipHint;
		var subs = {
			addrstr: addr.toString(),
			hint: hint
		};
		toolTip = AjxTemplate.expand("abook.Contacts#TooltipNotInAddrBook", subs);
	}

	canvas.innerHTML = toolTip;
};

Com_Zimbra_Email.prototype.getActionMenu =
function(obj, span, context) {

	// call base class first to get the action menu
	var actionMenu = ZmZimletBase.prototype.getActionMenu.call(this, obj, span, context);

	var addr = (obj instanceof AjxEmailAddress) ? obj.getAddress() : obj;
	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	var person = this._lookup(addr);
    // start hack for bug 23917
    var op = actionMenu.getOp("NEWCONTACT");
    if (op) {
        op.setText(ZmMsg.newContact);
    }
    op = actionMenu.getOp("SEARCH");
    if (op) {
        op.setText(ZmMsg.search);
    }
    op = actionMenu.getOp("SEARCHBUILDER");
    if (op) {
        op.setText(ZmMsg.advancedSearch);
    }
    op = actionMenu.getOp("NEWIM");
    if (op) {
        op.setText(ZmMsg.newIM);
    }
    op = actionMenu.getOp("NEWFILTER");
    if (op) {
        op.setText(ZmMsg.newFilter);
    }
    op = actionMenu.getOp("NEWEMAIL");
    if (op) {
        op.setText(ZmMsg.newEmail);
    }
    op = actionMenu.getOp("GOTOURL");
    if (op) {
        op.setText(ZmMsg.goToUrl.replace("{0}",ZmMsg.url));
    }
    // ends
    if (!appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		// make sure to remove adding new contact menu item if contacts are disabled
		if (actionMenu.getOp("NEWCONTACT")) {
			ZmOperation.removeOperation(actionMenu, "NEWCONTACT", actionMenu._menuItems);
		}
	} else {
		// bug fix #5262 - Change action menu item for contact depending on
		// whether email address is found in address book or not.
		var contactList = AjxDispatcher.run("GetContacts");
		if (contactList) {
			var newOp = person.contact ? ZmOperation.EDIT_CONTACT : ZmOperation.NEW_CONTACT;
			var newText = person.contact ? null : ZmMsg.AB_ADD_CONTACT;
			ZmOperation.setOperation(actionMenu, "NEWCONTACT", newOp, newText);

		}
	}
	if (actionMenu.getOp("NEWIM")) {
		actionMenu.getOp("NEWIM").setEnabled(person.buddy != null);
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

Com_Zimbra_Email.prototype.isMailToLink =
function (str){
	return (!!(str.search(/mailto/i) != -1));
};

Com_Zimbra_Email.prototype.parseMailToLink =
function(str){
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

	var parts = addr.split("@");
	var domain = (parts.length > 0) ? parts[1] : null;
	if (domain && domain == "yahoo.com") {
		var yProfileUrl = "http://profiles.yahoo.com/" + parts[0];
		window.open(yProfileUrl, "_blank");
		return;
	}

	var contactList = AjxDispatcher.run("GetContacts");
	var contact = contactList ? contactList.getContactByEmail(addr) : null;
	// if contact found or there is no contact list (i.e. contacts app is disabled), go to compose view
	if (contact ||
		contactList == null ||
		(AjxUtil.isString(addr) && this.isMailToLink(addr)))
	{
		this._actionObject = null;
		this._composeListener(ev,addr);
	}
	else
	{
		// otherwise, no contact in addrbook means go to contact edit view
		this._actionObject = contentObjText;
		this._contactListener();
	}
};

Com_Zimbra_Email.prototype.menuItemSelected =
function(itemId, item, ev) {
	switch (itemId) {
	    case "SEARCH":			this._searchListener();		break;
	    case "SEARCHBUILDER":	this._browseListener();		break;
	    case "NEWEMAIL":		this._composeListener(ev);	break;
		case "NEWIM":			this._newImListener();		break;
		case "NEWCONTACT":		this._contactListener();	break;
		case "NEWFILTER":		this._filterListener();		break;
		case "GOTOURL":			this._goToUrlListener();	break;
	}
};

Com_Zimbra_Email.prototype._getYahooHint =
function(name) {
	var lastLetter = name.charAt(name.length-1);
	var html = [];
	var idx = 0;
	html[idx++] = "<center><table border=0><tr><td valign=top><div class='ImgWebSearch'></div></td><td>Click to visit ";
	html[idx++] = name;
	html[idx++] = (lastLetter == "S" || lastLetter == "s") ? "' " : "'s ";
	html[idx++] = "Yahoo! profile<div class='TooltipHint'>";
	html[idx++] = ZmMsg.rightClickHint;
	html[idx++] = "</div></td></tr></table></center>";
	return html.join("");
};

Com_Zimbra_Email.prototype._getAddress =
function(obj) {
	return (obj.constructor == AjxEmailAddress) ? obj.address : obj;
};

Com_Zimbra_Email.prototype._contactListener =
function() {
	var loadCallback = new AjxCallback(this, this._handleLoadContact);
	AjxDispatcher.require(["ContactsCore", "Contacts"], false, loadCallback, null, true);
};

Com_Zimbra_Email.prototype._newImListener =
function() {
	var person = this._lookup(this._getAddress(this._actionObject));
	if (person.buddy) {
		AjxDispatcher.run("GetChatListController").chatWithRosterItem(person.buddy);
	}
};

Com_Zimbra_Email.prototype._getActionedContact =
function(create) {
	// actionObject can be a ZmContact, a String, or a generic Object (phew!)
	var contact;
	var addr = this._actionObject;
	if (this._actionObject) {
		if (this._actionObject instanceof ZmContact) {
			contact = this._actionObject;
		} else if (AjxUtil.isString(this._actionObject)) {
			addr = this._getAddress(this._actionObject);
			if (this.isMailToLink(addr)) {
				addr = (this.parseMailToLink(addr)).to || addr;
			}
			contact = AjxDispatcher.run("GetContacts").getContactByEmail(addr)
		} else {
			contact = AjxDispatcher.run("GetContacts").getContactByEmail(this._actionObject.address);
		}
	}
	if (contact == null && create) {
		contact = new ZmContact(null);
		contact.initFromEmail(addr);
	}
	return contact;
};

Com_Zimbra_Email.prototype._handleLoadContact =
function() {
	var contact = this._getActionedContact(true);
	AjxDispatcher.run("GetContactController").show(contact);
};

Com_Zimbra_Email.prototype._composeListener =
function(ev, addr) {

	addr = (this._actionObject) ? this._getAddress(this._actionObject) : addr ;
	if (!addr) addr = "";
	var params = {};

	var inNewWindow = (!appCtxt.get(ZmSetting.NEW_WINDOW_COMPOSE) && ev && ev.shiftKey) ||
					  (appCtxt.get(ZmSetting.NEW_WINDOW_COMPOSE) && ev && !ev.shiftKey);

	if (this.isMailToLink(addr)) {
		var mailToParams = this.parseMailToLink(addr);
		params.toOverride = mailToParams.to;
		params.subjOverride = mailToParams.subject;
		params.extraBodyText = mailToParams.body;
		addr = mailToParams.to || addr;
	}

	params.action = ZmOperation.NEW_MESSAGE;
	params.inNewWindow = inNewWindow;
	if (!params.toOverride) {
		params.toOverride = addr + AjxEmailAddress.SEPARATOR;
	}

	AjxDispatcher.run("Compose", params );
};

Com_Zimbra_Email.prototype._browseListener =
function() {
	var addr = this._getAddress(this._actionObject);
 	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	appCtxt.getSearchController().fromBrowse(addr);
};

Com_Zimbra_Email.prototype._searchListener =
function() {
	var addr = this._getAddress(this._actionObject);
	if (this.isMailToLink(addr)) {
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

	var addr = this._getAddress(this._actionObject);
	if (AjxUtil.isString(addr) && this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	rule.addCondition(new ZmCondition(ZmFilterRule.C_FROM, ZmFilterRule.OP_IS, addr ));
	rule.addAction(new ZmAction(ZmFilterRule.A_KEEP));

	appCtxt.getFilterRuleDialog().popup(rule);
}

Com_Zimbra_Email.prototype._goToUrlListener =
function() {
	var addr  = this._getAddress(this._actionObject);
	if (AjxUtil.isString(addr) && this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}

	var parts = addr.split("@");
	if (parts.length) {
		var domain = parts[parts.length - 1];
		var pieces = domain.split(".");
		this._actionUrl = "http://" + ((pieces.length <= 2) ? 'www.' + domain : domain);
	}

	if (this._actionUrl) {
		window.open(this._actionUrl, "_blank", "menubar=yes,resizable=yes,scrollbars=yes");
	} else {
		this.displayStatusMessage(ZmMsg.errorCreateUrl);
	}
};
