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

function Com_Zimbra_Email() {
};

Com_Zimbra_Email.prototype = new ZmZimletBase();
Com_Zimbra_Email.prototype.constructor = Com_Zimbra_Email;

// static Contst
Com_Zimbra_Email.IM_NEW_IM = "im new im";
Com_Zimbra_Email.IM_NEW_BUDDY = "im new buddy";
Com_Zimbra_Email.NEW_FILTER = "__new__";

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

	this._yahooSocialEnabled = this.getBoolConfig("yahooSocialEnabled");
};

Com_Zimbra_Email.prototype._getRoster =
function() {
	if (!this._roster && appCtxt.get(ZmSetting.IM_ENABLED) &&
		!(!appCtxt.get(ZmSetting.IM_PREF_AUTO_LOGIN) &&
		  !appCtxt.getApp(ZmApp.IM).hasRoster())) // If not AUTO_LOGIN enabled, don't LOGIN
	{
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

Com_Zimbra_Email.prototype._getHtmlContent =
function(html, idx, obj, context, spanId) {

	if (obj instanceof AjxEmailAddress) {
		var contactsApp = appCtxt.getApp(ZmApp.CONTACTS);
		var contact = contactsApp && contactsApp.getContactByEmail(obj.address); // contact in cache?
		var buddy = this._getBuddy(contact, obj.address);
		if (contactsApp && !contact && contact !== null) {
			// search for contact
			var respCallback = new AjxCallback(this, this._handleResponseGetContact, [html, idx, obj, spanId]);
			contactsApp.getContactByEmail(obj.address, respCallback);
		}
		// return content for what we have now (may get updated after search)
		return this._updateHtmlContent(html, idx, obj, contact, buddy);
	} else {
		html[idx++] = AjxStringUtil.htmlEncode(obj);
		return idx;
	}
};

/**
 * Returns content for this object's <span> element based on a contact and/or buddy.
 * If given a spanId, it will instead replace the content of the <span>, for example,
 * with the results of a search.
 */
Com_Zimbra_Email.prototype._updateHtmlContent =
function(html, idx, obj, contact, buddy, spanId) {

	var content;
	if (buddy) {
		var pres = buddy.getPresence();
		var pres_id = Dwt.getNextId();
		var tmp = [
			AjxStringUtil.htmlEncode(buddy.getDisplayName()), " ",
			AjxImg.getImageHtml(pres.getIcon(), "display: inline; padding: 1px 8px;", "id=" + pres_id)
		];
		content = tmp.join("");
		var params = {
			contact : contact,
			buddy   : buddy,
			im_addr : buddy.getAddress(),
			img_id  : pres_id
		};
		this._presenceCache.push(params);

		if (this._presenceCache.length > 50) {
			// 50 should be enough.. maybe should be even smaller?
			this._presenceCache.splice(0, 1);
		}

		this._getRoster();
	} else {
		if (contact && contact instanceof ZmContact) {
			content = AjxStringUtil.htmlEncode(contact.getFullName());
		}
		if (!content) {
			content = AjxStringUtil.htmlEncode(obj.toString());
		}
	}

	if (spanId) {
		var span = document.getElementById(spanId);
		if (span) {
			span.innerHTML = content;
		}
	} else {
		html[idx++] = content;
		return idx;
	}
};

Com_Zimbra_Email.prototype._handleResponseGetContact =
function(html, idx, obj, spanId, contact) {
	if (contact) {
		var buddy = this._getBuddy(contact, obj.address);
		this._updateHtmlContent(html, idx, obj, contact, buddy, spanId);
	}
};

Com_Zimbra_Email.prototype._getBuddy =
function(contact, address) {

	if (appCtxt.isChildWindow) { return; }

	var buddy;
	if (appCtxt.get(ZmSetting.IM_ENABLED) && !(!appCtxt.get(ZmSetting.IM_PREF_AUTO_LOGIN) &&
		!appCtxt.getApp(ZmApp.IM).hasRoster())) {	// If not AUTO_LOGIN enabled, don't LOGIN

		buddy = contact && contact.getBuddy();
		if (!buddy) {
			buddy = AjxDispatcher.run("GetRoster").getRosterItem(address);
		}
	}
	return buddy;
};

Com_Zimbra_Email.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {

	var addr = (contentObjText instanceof AjxEmailAddress)
		? contentObjText.address : contentObjText;

	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}

	var toolTip;

	// @yahoo.com love
	var isYahoo = false;
	if (this._yahooSocialEnabled) {
		var parts = addr.split("@");
		var domain = (parts.length > 0) ? parts[1] : null;
		isYahoo = (domain && domain == "yahoo.com");
	}

	var contactList = AjxDispatcher.run("GetContacts");
	var contact = contactList ? contactList.getContactByEmail(addr) : null;
	if (contact) {
		var hint = isYahoo ? this._getYahooHint() : this._composeTooltipHint;
		toolTip = contact.getToolTip(addr, false, hint);
	} else {
		var hint = isYahoo ? this._getYahooHint() : this._newTooltipHint;
		var subs = {
			addrstr: addr.toString(),
			hint: hint
		};
		toolTip = AjxTemplate.expand("abook.Contacts#TooltipNotInAddrBook", subs);
	}

	canvas.innerHTML = toolTip;
};

Com_Zimbra_Email.prototype.createFilterMenu =
function(actionMenu) {
	if (this._filterMenu) { return; }

	this._newFilterMenuItem = actionMenu.getOp("ADDTOFILTER");
	this._filterMenu = new ZmPopupMenu(actionMenu);
	this._newFilterMenuItem.setMenu(this._filterMenu);

	this._rules = AjxDispatcher.run("GetFilterRules");
	this._rules.addChangeListener(new AjxListener(this, this._rulesChangeListener));
	this._resetFilterMenu();
};

Com_Zimbra_Email.prototype._resetFilterMenu =
function(){
	var filterItems = this._filterMenu.getItems();
	while (filterItems.length > 0) {
		this._filterMenu.removeChild(filterItems[0]);
	}
	this._rules.loadRules(false, new AjxCallback(this, this._populateFiltersMenu));
};

Com_Zimbra_Email.prototype._populateFiltersMenu =
function(results){
	var filters = results.getResponse();
	var menu = this._filterMenu;

	var miNew = new DwtMenuItem({parent:menu});
	miNew.setText(this.getMessage("newFilter"));
	miNew.setImage("Plus");
	miNew.setData(Dwt.KEY_OBJECT, Com_Zimbra_Email.NEW_FILTER);
	miNew.addSelectionListener(new AjxListener(this, this._filterItemSelectionListener));

	if (filters.size()) {
		menu.createSeparator();
	}

	for (var i = 0; i < filters.size(); i++) {
		this._addFilter(menu, filters.get(i));
	}
};

Com_Zimbra_Email.prototype._rulesChangeListener =
function(ev){
	if (ev.type != ZmEvent.S_FILTER) { return; }

	if (!ev.handled) {
		this._resetFilterMenu();
		ev.handled = true;
	}
};

Com_Zimbra_Email.prototype._filterItemSelectionListener =
function(ev){
	var filterMenuItem = ev.item;
	var editMode = true;

	var rule = filterMenuItem.getData(Dwt.KEY_OBJECT);

	if (rule == Com_Zimbra_Email.NEW_FILTER) {
		editMode = false;
		rule = new ZmFilterRule();
		rule.addAction(ZmFilterRule.A_KEEP);
	}

	var addr = this._getAddress(this._actionObject);
	if (AjxUtil.isString(addr) && this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	var subjMod = ZmFilterRule.C_HEADER_VALUE[ZmFilterRule.C_FROM];
	rule.addCondition(ZmFilterRule.TEST_HEADER, ZmFilterRule.OP_IS, addr, subjMod);

	appCtxt.getFilterRuleDialog().popup(rule, editMode);
};

Com_Zimbra_Email.prototype._addFilter =
function(menu, rule, index) {
	var mi = new DwtMenuItem({parent:menu, index:index});
	mi.setText(AjxStringUtil.clipByLength(rule.name, 20));
	mi.setData(Dwt.KEY_OBJECT, rule);
	mi.addSelectionListener(new AjxListener(this, this._filterItemSelectionListener));
};

Com_Zimbra_Email.prototype.getActionMenu =
function(obj, span, context) {
	// call base class first to get the action menu
	var actionMenu = ZmZimletBase.prototype.getActionMenu.call(this, obj, span, context);

	if (appCtxt.get(ZmSetting.FILTERS_ENABLED) && actionMenu.getOp("ADDTOFILTER")) {
		this.createFilterMenu(actionMenu);
	}

	var addr = (obj instanceof AjxEmailAddress) ? obj.getAddress() : obj;
	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}

	if (!appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		// make sure to remove adding new contact menu item if contacts are disabled
		if (actionMenu.getOp("NEWCONTACT")) {
			actionMenu.removeOp("NEWCONTACT");
		}
	}

	var imItem = actionMenu.getOp("NEWIM");
	if (imItem) {
		if (!appCtxt.get(ZmSetting.IM_ENABLED)) {
			actionMenu.removeOp("NEWIM");
		} else {
			var addrObj = obj instanceof AjxEmailAddress ? obj : new AjxEmailAddress(obj);
			ZmImApp.updateImMenuItemByAddress(imItem, addrObj);
		}
	}

	if (actionMenu.getOp("SEARCH") && !appCtxt.get(ZmSetting.SEARCH_ENABLED)) {
		ZmOperation.removeOperation(actionMenu, "SEARCH", actionMenu._menuItems);
	}

	if (actionMenu.getOp("SEARCHBUILDER") && !appCtxt.get(ZmSetting.BROWSE_ENABLED)) {
		ZmOperation.removeOperation(actionMenu, "SEARCHBUILDER", actionMenu._menuItems);
	}

	if (actionMenu.getOp("ADDTOFILTER") && !appCtxt.get(ZmSetting.FILTERS_ENABLED)) {
		ZmOperation.removeOperation(actionMenu, "ADDTOFILTER", actionMenu._menuItems);
	}

	var contactsApp = appCtxt.getApp(ZmApp.CONTACTS);
	var contact = contactsApp && contactsApp.getContactByEmail(addr);
	if (contact) {
		// contact for this address was found in the cache
		ZmOperation.setOperation(actionMenu, "NEWCONTACT", ZmOperation.EDIT_CONTACT);
	} else {
		// contact not found, do a search
		if (contactsApp && !contact && contact !== null) {
			actionMenu.getOp("NEWCONTACT").setText(ZmMsg.loading);
			var respCallback = new AjxCallback(this, this._handleResponseGetContact1, [actionMenu]);
			contactsApp.getContactByEmail(addr, respCallback);
		} else {
			ZmOperation.setOperation(actionMenu, "NEWCONTACT", ZmOperation.NEW_CONTACT, ZmMsg.AB_ADD_CONTACT);
		}
	}

	return actionMenu;
};

Com_Zimbra_Email.prototype._handleResponseGetContact1 =
function(actionMenu, contact) {
	var newOp = contact ? ZmOperation.EDIT_CONTACT : ZmOperation.NEW_CONTACT;
	var newText = contact ? null : ZmMsg.AB_ADD_CONTACT;
	ZmOperation.setOperation(actionMenu, "NEWCONTACT", newOp, newText);
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

	if (this._yahooSocialEnabled){
		var parts = addr.split("@");
		var domain = (parts.length > 0) ? parts[1] : null;
		if (domain && domain == "yahoo.com") {
			var yProfileUrl = "http://profiles.yahoo.com/" + parts[0];
			window.open(yProfileUrl, "_blank");
			return;
		}
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
		this._contactListener(true);
	}
};

Com_Zimbra_Email.prototype.menuItemSelected =
function(itemId, item, ev) {
	switch (itemId) {
		case "SEARCH":			this._searchListener();		break;
		case "SEARCHBUILDER":	this._browseListener();		break;
		case "NEWEMAIL":		this._composeListener(ev);	break;
		case "NEWIM":			this._newImListener(ev);	break;
		case "NEWCONTACT":		this._contactListener(true);	break;
		case "ADDTOFILTER":		this._filterListener();		break;
		case "GOTOURL":			this._goToUrlListener();	break;
	}
};

Com_Zimbra_Email.prototype._getYahooHint =
function() {
	var html = [];
	var idx = 0;
	html[idx++] = "<center><table border=0><tr><td valign=top><div class='ImgWebSearch'></div></td><td>";
	html[idx++] = ZmMsg.leftClickYahoohint;
	html[idx++] = "<div class='TooltipHint'>";
	html[idx++] = ZmMsg.rightClickHint;
	html[idx++] = "</div></td></tr></table></center>";
	return html.join("");
};

Com_Zimbra_Email.prototype._getAddress =
function(obj) {
	return (obj.constructor == AjxEmailAddress) ? obj.address : obj;
};

Com_Zimbra_Email.prototype._contactListener =
function(isDirty) {
	var loadCallback = new AjxCallback(this, this._handleLoadContact, [isDirty]);
	AjxDispatcher.require(["ContactsCore", "Contacts"], false, loadCallback, null, true);
};

Com_Zimbra_Email.prototype._newImListener =
function(ev) {
	ZmImApp.getImMenuItemListener().handleEvent(ev);
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
			contact = AjxDispatcher.run("GetContacts").getContactByEmail(addr);
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
function(isDirty) {
	var contact = this._getActionedContact(true);

	if (window.parentAppCtxt) {
		var capp = window.parentAppCtxt.getApp(ZmApp.CONTACTS);
		capp.getContactController().show(contact, isDirty);
	} else {
		AjxDispatcher.run("GetContactController").show(contact, isDirty);
	}
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
	appCtxt.getAppViewMgr().popView(true, ZmId.VIEW_LOADING);	// pop "Loading..." page
	var rule = new ZmFilterRule();

	var addr = this._getAddress(this._actionObject);
	if (AjxUtil.isString(addr) && this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	var subjMod = ZmFilterRule.C_HEADER_VALUE[ZmFilterRule.C_FROM];
	rule.addCondition(ZmFilterRule.TEST_HEADER, ZmFilterRule.OP_IS, addr, subjMod);
	rule.addAction(ZmFilterRule.A_KEEP);

	appCtxt.getFilterRuleDialog().popup(rule);
};

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
		window.open(this._actionUrl, "_blank");
	} else {
		this.displayStatusMessage(ZmMsg.errorCreateUrl);
	}
};
