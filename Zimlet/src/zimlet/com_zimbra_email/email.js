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
function com_zimbra_email_handlerObject() {
	this.isPrimaryEmailTooltip = true;
}

com_zimbra_email_handlerObject.prototype = new ZmZimletBase();
com_zimbra_email_handlerObject.prototype.constructor = com_zimbra_email_handlerObject;

var EmailTooltipZimlet = com_zimbra_email_handlerObject;


// static Contst
EmailTooltipZimlet.IM_NEW_IM = "im new im";
EmailTooltipZimlet.IM_NEW_BUDDY = "im new buddy";
EmailTooltipZimlet.NEW_FILTER = "__new__";
EmailTooltipZimlet.MAILTO_RE = /^mailto:[\x27\x22]?([^@?&\x22\x27]+@[^@?&]+\.[^@?&\x22\x27]+)[\x27\x22]?/;
EmailTooltipZimlet.tooltipWidth = 270;
EmailTooltipZimlet.tooltipHeight = 300;

EmailTooltipZimlet.prototype.init =
function() {
	if (appCtxt.get(ZmSetting.CONTACTS_ENABLED)) {
		AjxDispatcher.require(["ContactsCore", "Contacts"]);
		this._composeTooltipHint = ZmMsg.leftClickComposeHint + "<br>" + ZmMsg.rightClickHint;
		this._newTooltipHint = ZmMsg.leftClickNewContactHint + "<br>" + ZmMsg.rightClickHint;

		if (appCtxt.get(ZmSetting.IM_ENABLED)) {
			this._presenceCache = [];
		}
	} else {
		this._newTooltipHint = ZmMsg.leftClickComposeHint + "<br>" + ZmMsg.rightClickHint;
	}
	this._prefDialog = new EmailToolTipPrefDialog(this);

	this._subscriberZimlets = [];
	this._preLoadBusyImg();
};

/**
 * This method is called when the panel item is double-clicked.
 *
 */
EmailTooltipZimlet.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * This method is called when the panel item is single-clicked.
 *
 */
EmailTooltipZimlet.prototype.singleClicked =
function() {
	this._prefDialog.popup();
};

EmailTooltipZimlet.prototype._getRoster =
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

EmailTooltipZimlet.prototype._rosterChangeListener =
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
				if (img && b.getPresence().getShow() == ZmRosterPresence.SHOW_ONLINE) {
					AjxImg.setImage(img, b.getPresence().getIcon(true), true);
				} else {
					// no longer visible, remove from cache?
					// cache.splice(i, 1);
					// better not: will fail if we collapse/expand headers
				}
			}
		}
	}
};

EmailTooltipZimlet.prototype._getHtmlContent =
function(html, idx, obj, context, spanId) {
	if (obj instanceof AjxEmailAddress) {
		var context = window.parentAppCtxt || window.appCtxt;
		var contactsApp = context.getApp(ZmApp.CONTACTS);
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
EmailTooltipZimlet.prototype._updateHtmlContent =
function(html, idx, obj, contact, buddy, spanId) {

	var content;
	var pres = buddy && buddy.getPresence();
	if (pres && pres.getShow() == ZmRosterPresence.SHOW_ONLINE) {
		var pres_id = Dwt.getNextId();

		content = [
			AjxStringUtil.htmlEncode(buddy.getDisplayName()),
			AjxImg.getImageHtml(pres.getIcon(true), "display:inline; padding-left:13px", "id=" + pres_id)
		].join("");

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
		if (contact && contact.toString() == "ZmContact") {
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

EmailTooltipZimlet.prototype._handleResponseGetContact =
function(html, idx, obj, spanId, contact) {
	if (contact) {
		var buddy = this._getBuddy(contact, obj.address);
		this._updateHtmlContent(html, idx, obj, contact, buddy, spanId);
	}
};

EmailTooltipZimlet.prototype._getBuddy =
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

EmailTooltipZimlet.prototype.hoverOut =
function(object, context, x, y, span) {
	setTimeout(AjxCallback.simpleClosure(this._popDownIfMouseNotOnSlide, this), 300);
	//override to ignore hoverout. 
};

EmailTooltipZimlet.prototype._popDownIfMouseNotOnSlide =
function() {
	if(this.slideShow && this.slideShow.isVeilShown) {
		return;
	} else if(this.tooltip) {
		this.tooltip.popdown();
	}
};

EmailTooltipZimlet.prototype.addSubscriberZimlet =
function(subscriberZimlet, isPrimary) {
	this._subscriberZimlets.push(subscriberZimlet);	
	if(isPrimary) {
		this.primarySubscriberZimlet = subscriberZimlet;
	}
};

EmailTooltipZimlet.prototype.hoverOver =
function(object, context, x, y, span) {
	this._initializeProps(object, context, x, y, span);
	appCtxt.notifyZimlets("onEmailHoverOver", [this], {waitUntilLoaded:true});
	if (this.primarySubscriberZimlet) {
		this.primarySubscriberZimlet.showTooltip();
	}
	else if (this._subscriberZimlets.length > 0 && !this.primarySubscriberZimlet) {
		this._unknownPersonSlide = new UnknownPersonSlide();
		this._unknownPersonSlide.onEmailHoverOver(this);
		this._unknownPersonSlide.showTooltip();
	}
	else { // if no subscribers..
		this._showTooltip(object, context, x, y, span);
	}
};

EmailTooltipZimlet.prototype._showTooltip =
function(object, context, x, y, span) {
	var shell = DwtShell.getShell(window);
	var tooltip = shell.getToolTip();
	tooltip.setContent('<div id="zimletTooltipDiv"/>', true);
	this.toolTipPoppedUp(span, object, context, document.getElementById("zimletTooltipDiv"));
	tooltip.popup(x, y, true, new AjxCallback(this, this.hoverOut, object, context, span));
};


EmailTooltipZimlet.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var addr = (contentObjText instanceof AjxEmailAddress)
		? contentObjText.address : contentObjText;

	var isMailTo = this.isMailToLink(addr);
	if (isMailTo) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}

	var toolTip;
	var contactList = AjxDispatcher.run("GetContacts");
	var contact = contactList ? contactList.getContactByEmail(addr) : null;
	if (contact) {
		var hint = this._composeTooltipHint;
		toolTip = contact.getToolTip(addr, false, hint);
	} else {
		var hint = this._newTooltipHint;
		if (isMailTo) {
			hint = this._composeTooltipHint;
		}
		var subs = {
			addrstr: addr.toString(),
			hint: hint
		};
		toolTip = AjxTemplate.expand("abook.Contacts#TooltipNotInAddrBook", subs);
	}
	canvas.innerHTML = toolTip;
};

EmailTooltipZimlet.prototype._initializeProps =
function(object, context, x, y, span) {
	if (!this.seriesAnimation) {
		this.seriesAnimation = new SeriesAnimation();
	}
	this.seriesAnimation.reset();
	var shell = DwtShell.getShell(window);
	var tooltip = shell.getToolTip();
	tooltip.setContent("<div id=\"zimletTooltipDiv\"></div>", true);
	this.x = x;
	this.y = y;
	this.tooltip = tooltip;
	var addr = (object instanceof AjxEmailAddress) ? object.address : object;
	var isMailTo = this.isMailToLink(addr);
	if (isMailTo) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	this.emailAddress = addr;
	this.fullName = (object instanceof AjxEmailAddress) ? object.name : "";
	this.canvas =   document.getElementById("zimletTooltipDiv");
	this.slideShow = new EmailToolTipSlideShow(this, this.canvas);
	this.contextMenu = this.getActionMenu(object, span, context, false);
};

EmailTooltipZimlet.prototype._preLoadBusyImg =
function() {
	this._busyImg = new Image();
	this._busyImg.src = this.getResource("img/EmailZimlet_busy.gif");
	this.getShell().getHtmlElement().appendChild(this._busyImg);
};
EmailTooltipZimlet.prototype.showBusyImg =
function(timeoutCallback, xOffset, yOffset) {
	var top = yOffset ? this.y + yOffset : this.y;
	var left = xOffset ? this.x + xOffset : this.x;

	this._busyImg.style.top = top;
	this._busyImg.style.left = left;
	this._busyImg.style.display = "block";
	this._busyImg.style.position = "absolute";
	this._busyImg.style.zIndex = "500";
	this._busyImgTimer = setTimeout(AjxCallback.simpleClosure(this._handleNoImg, this, timeoutCallback), 5000);//hide busyImg after 5 secs
};

EmailTooltipZimlet.prototype._handleNoImg =
function(timeoutCallback) {
	clearTimeout(this._busyImgTimer);
	this._busyImg.style.zIndex = "100";
	this._busyImg.style.display = "none";
	if (timeoutCallback) {
		timeoutCallback.run();
	}
};

EmailTooltipZimlet.prototype.hideBusyImg =
function() {
	clearTimeout(this._busyImgTimer);
	this._busyImg.style.zIndex = "100";
	this._busyImg.style.display = "none";
};



EmailTooltipZimlet.prototype.createFilterMenu =
function(actionMenu) {
	if (this._filterMenu) { return; }

	this._newFilterMenuItem = actionMenu.getOp("ADDTOFILTER");
	this._filterMenu = new ZmPopupMenu(actionMenu);
	this._newFilterMenuItem.setMenu(this._filterMenu);

	this._rules = AjxDispatcher.run("GetFilterRules");
	this._rules.addChangeListener(new AjxListener(this, this._rulesChangeListener));
	this._resetFilterMenu();
};

EmailTooltipZimlet.prototype._resetFilterMenu =
function() {
	var filterItems = this._filterMenu.getItems();
	while (filterItems.length > 0) {
		this._filterMenu.removeChild(filterItems[0]);
	}
	this._rules.loadRules(false, new AjxCallback(this, this._populateFiltersMenu));
};

EmailTooltipZimlet.prototype._populateFiltersMenu =
function(results){
	var filters = results.getResponse();
	var menu = this._filterMenu;

	var miNew = new DwtMenuItem({parent:menu});
	miNew.setText(this.getMessage("newFilter"));
	miNew.setImage("Plus");
	miNew.setData(Dwt.KEY_OBJECT, EmailTooltipZimlet.NEW_FILTER);
	miNew.addSelectionListener(new AjxListener(this, this._filterItemSelectionListener));

	if (filters.size()) {
		menu.createSeparator();
	}

	for (var i = 0; i < filters.size(); i++) {
		this._addFilter(menu, filters.get(i));
	}
};

EmailTooltipZimlet.prototype._rulesChangeListener =
function(ev){
	if (ev.type != ZmEvent.S_FILTER) { return; }

	if (!ev.handled) {
		this._resetFilterMenu();
		ev.handled = true;
	}
};

EmailTooltipZimlet.prototype._filterItemSelectionListener =
function(ev){
	var filterMenuItem = ev.item;
	var editMode = true;

	var rule = filterMenuItem.getData(Dwt.KEY_OBJECT);

	if (rule == EmailTooltipZimlet.NEW_FILTER) {
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

EmailTooltipZimlet.prototype._addFilter =
function(menu, rule, index) {
	var mi = new DwtMenuItem({parent:menu, index:index});
	mi.setText(AjxStringUtil.clipByLength(rule.name, 20));
	mi.setData(Dwt.KEY_OBJECT, rule);
	mi.addSelectionListener(new AjxListener(this, this._filterItemSelectionListener));
};

EmailTooltipZimlet.prototype.getActionMenu =
function(obj, span, context) {
	// call base class first to get the action menu
	var actionMenu = ZmZimletBase.prototype.getActionMenu.call(this, obj, span, context);
	var isDetachWindow = appCtxt.isChildWindow;

	if (!isDetachWindow && appCtxt.get(ZmSetting.FILTERS_ENABLED) && actionMenu.getOp("ADDTOFILTER") ) {
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
		if (isDetachWindow || !appCtxt.get(ZmSetting.IM_ENABLED)) {
			actionMenu.removeOp("NEWIM");
		} else {
			var addrObj = obj instanceof AjxEmailAddress ? obj : new AjxEmailAddress(obj);
			ZmImApp.updateImMenuItemByAddress(imItem, addrObj);
		}
	}

	if (actionMenu.getOp("SEARCH") && (isDetachWindow || !appCtxt.get(ZmSetting.SEARCH_ENABLED))) {
		ZmOperation.removeOperation(actionMenu, "SEARCH", actionMenu._menuItems);
	}

	if (actionMenu.getOp("SEARCHBUILDER") && (isDetachWindow || !appCtxt.get(ZmSetting.BROWSE_ENABLED))) {
		ZmOperation.removeOperation(actionMenu, "SEARCHBUILDER", actionMenu._menuItems);
	}

	if (actionMenu.getOp("ADDTOFILTER") && (isDetachWindow || !appCtxt.get(ZmSetting.FILTERS_ENABLED))) {
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

EmailTooltipZimlet.prototype._handleResponseGetContact1 =
function(actionMenu, contact) {
	var newOp = contact ? ZmOperation.EDIT_CONTACT : ZmOperation.NEW_CONTACT;
	var newText = contact ? null : ZmMsg.AB_ADD_CONTACT;
	ZmOperation.setOperation(actionMenu, "NEWCONTACT", newOp, newText);
};

EmailTooltipZimlet.prototype.isMailToLink =
function (str){
	return (!!(str.search(/mailto/i) != -1));
};

EmailTooltipZimlet.prototype.parseMailToLink =
function(str){
	var parts = {};
	var match = str.match(/\bsubject=([^&]+)/i);
	parts.subject = match ? decodeURIComponent(match[1]) : null;

	match = str.match(/\bto\:([^&]+)/);
	if(!match) match = str.match(/\bmailto\:([^\?]+)/i);
	parts.to = match ? decodeURIComponent(match[1]) : null;

	match = str.match(/\bbody=([^&]+)/i);
	parts.body = match ? decodeURIComponent(match[1]) : null;

	return parts;
};

EmailTooltipZimlet.prototype.clicked =
function(spanElement, contentObjText, matchContext, ev) {
	if(this.tooltip) {
		this.tooltip.popdown();
	}
	var addr = (contentObjText instanceof AjxEmailAddress)
		? contentObjText.address : contentObjText;

	var contact = addr;
	var isMailTo = this.isMailToLink(addr);
	//extract mailid from mailto:mailid?params
	if (isMailTo && EmailTooltipZimlet.MAILTO_RE.test(addr)) {
		contact = RegExp.$1;
	}

	var contactList = AjxDispatcher.run("GetContacts");
	var addrContact = contactList ? contactList.getContactByEmail(contact) : null;
	// if contact found or there is no contact list (i.e. contacts app is disabled), go to compose view
	if (isMailTo || addrContact || contactList == null)
	{
		this._actionObject = null;
		this._composeListener(ev, addr);
	}
	else
	{
		// otherwise, no contact in addrbook means go to contact edit view
		this._actionObject = contentObjText;
		this._contactListener(true);
	}
};

EmailTooltipZimlet.prototype.menuItemSelected =
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

EmailTooltipZimlet.prototype._getYahooHint =
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

EmailTooltipZimlet.prototype._getAddress =
function(obj) {
	return (obj.constructor == AjxEmailAddress) ? obj.address : obj;
};

EmailTooltipZimlet.prototype._contactListener =
function(isDirty) {
	var loadCallback = new AjxCallback(this, this._handleLoadContact, [isDirty]);
	AjxDispatcher.require(["ContactsCore", "Contacts"], false, loadCallback, null, true);
};

EmailTooltipZimlet.prototype._newImListener =
function(ev) {
	ZmImApp.getImMenuItemListener().handleEvent(ev);
};

EmailTooltipZimlet.prototype._getActionedContact =
function(create) {
	// actionObject can be a ZmContact, a String, or a generic Object (phew!)
	var contact;
	var addr = this._actionObject;
	if (this._actionObject) {
		if (this._actionObject.toString() == "ZmContact") {
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

EmailTooltipZimlet.prototype._handleLoadContact =
function(isDirty) {
	var contact = this._getActionedContact(true);

	if (window.parentAppCtxt) {
		var capp = window.parentAppCtxt.getApp(ZmApp.CONTACTS);
		capp.getContactController().show(contact, isDirty);
	} else {
		AjxDispatcher.run("GetContactController").show(contact, isDirty);
	}
};

EmailTooltipZimlet.prototype._composeListener =
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

EmailTooltipZimlet.prototype._browseListener =
function() {
	var addr = this._getAddress(this._actionObject);
	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	appCtxt.getSearchController().fromBrowse(addr);
};

EmailTooltipZimlet.prototype._searchListener =
function() {
	var addr = this._getAddress(this._actionObject);
	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	appCtxt.getSearchController().fromSearch(this._getAddress(addr));
};

EmailTooltipZimlet.prototype._filterListener =
function() {
	var loadCallback = new AjxCallback(this, this._handleLoadFilter);
	AjxDispatcher.require(["PreferencesCore", "Preferences"], false, loadCallback, null, true);
};

EmailTooltipZimlet.prototype._handleLoadFilter =
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

EmailTooltipZimlet.prototype._goToUrlListener =
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

/**
 * Helper function
 */
EmailTooltipZimlet.prototype.animateOpacity =
function(id, opacStart, opacEnd, millisec) {
	// create a starting point
	this.changeOpac(opacStart, document.getElementById(id).style);

	//speed for each frame
	var speed = Math.round(millisec / 100);
	var timer = 0;
	var styleObj = document.getElementById(id).style;

	// determine the direction for the blending, if start and end are the same nothing happens
	if (opacStart > opacEnd) {
		for (i = opacStart; i >= opacEnd; i--) {
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i, styleObj), (timer * speed));
			timer++;
		}
	} else if (opacStart < opacEnd) {
		for (i = opacStart; i <= opacEnd; i++)
		{
			setTimeout(AjxCallback.simpleClosure(this.changeOpac, this, i, styleObj), (timer * speed));
			timer++;
		}
	}
};

/**
 * Change the opacity for different browsers
 */
EmailTooltipZimlet.prototype.changeOpac =
function(opacity, styleObj) {
	styleObj.opacity = (opacity / 100);
	styleObj.MozOpacity = (opacity / 100);
	styleObj.KhtmlOpacity = (opacity / 100);
	styleObj.zoom = 1;
	styleObj.filter = "progid:DXImageTransform.Microsoft.Alpha(opacity=" + opacity + ")";
};
