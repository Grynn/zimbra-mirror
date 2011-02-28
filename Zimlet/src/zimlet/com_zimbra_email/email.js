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

	// support for showing address objects in the msg header as bubbles
	this._isBubble = {};
	this._bubbleClassName = "addrBubble";
	this._internalId = Dwt.getNextId();
	DwtControl.ALL_BY_ID[this._internalId] = this;
}

com_zimbra_email_handlerObject.prototype = new ZmZimletBase();
com_zimbra_email_handlerObject.prototype.constructor = com_zimbra_email_handlerObject;

var EmailTooltipZimlet = com_zimbra_email_handlerObject;


// static Contst
EmailTooltipZimlet.IM_NEW_IM = "im new im";
EmailTooltipZimlet.IM_NEW_BUDDY = "im new buddy";
EmailTooltipZimlet.NEW_FILTER = "__new__";
EmailTooltipZimlet.MAILTO_RE = /^mailto:[\x27\x22]?([^@?&\x22\x27]+@[^@?&]+\.[^@?&\x22\x27]+)[\x27\x22]?/;
EmailTooltipZimlet.tooltipWidth = 280;
EmailTooltipZimlet.tooltipHeight = 150;

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
	this._preLoadImgs();
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

// create bubble for address in header
EmailTooltipZimlet.prototype.generateSpan =
function(html, idx, obj, spanId, context, options) {

	options = options || {};
	if (options.addrBubbles) {
		this._isBubble[spanId] = true;
		var canExpand = obj.isGroup && obj.canExpand;
		if (canExpand && !this._aclv) {
			// create a ZmAutocompleteListView to handle DL expansion; it's never shown
			var aclvParams = {
				dataClass:		appCtxt.getAutocompleter(),
				matchValue:		ZmAutocomplete.AC_VALUE_FULL,
				options:		{addrBubbles:true, massDLComplete:true},
				compCallback:	new AjxCallback(this, this._dlAddrSelected)
			};
			this._aclv = new ZmAutocompleteListView(aclvParams);
		}

		var bubbleParams = {
			addrObj:	obj,
			id:			spanId,
			canExpand:	canExpand,
			email:		obj.getAddress(),
			separator:	AjxEmailAddress.SEPARATOR
		};
		ZmAddressInputField.BUBBLE_OBJ_ID[spanId] = this._internalId;	// pretend to be a ZmAddressInputField
		html[idx++] = "<span class='addrBubble' id='" + spanId + "'>";
		html[idx++] = ZmAddressBubble.getContent(bubbleParams);
		html[idx++] = "</span>";
		return idx;
	} else {
		return ZmObjectHandler.prototype.generateSpan.apply(this, arguments);
	}
};

EmailTooltipZimlet.prototype.getClassName =
function(obj, context, spanId) {
	return (this._isBubble[spanId]) ? this._bubbleClassName :
				   					  ZmObjectHandler.prototype.getClassName.apply(this, arguments);
};

EmailTooltipZimlet.prototype.getHoveredClassName =
function(obj, context, spanId) {
	return (this._isBubble[spanId]) ? this._bubbleClassName :
				   					  ZmObjectHandler.prototype.getHoveredClassName.apply(this, arguments);
};

EmailTooltipZimlet.prototype.getActiveClassName =
function(obj, context, spanId) {
	return (this._isBubble[spanId]) ? this._bubbleClassName :
				   					  ZmObjectHandler.prototype.getActiveClassName.apply(this, arguments);
};

EmailTooltipZimlet.prototype._getHtmlContent =
function(html, idx, obj, context, spanId, options) {
	if (obj instanceof AjxEmailAddress) {
		var context = window.parentAppCtxt || window.appCtxt;
		var contactsApp = context.getApp(ZmApp.CONTACTS);
		var contact = contactsApp && contactsApp.getContactByEmail(obj.address); // contact in cache?
		var buddy = this._getBuddy(contact, obj.address);
		if (contactsApp && !contact && contact !== null) {
			// search for contact
			var respCallback = new AjxCallback(this, this._handleResponseGetContact, [html, idx, obj, spanId, options]);
			contactsApp.getContactByEmail(obj.address, respCallback);
		}
		// return content for what we have now (may get updated after search)
		return this._updateHtmlContent(html, idx, obj, contact, buddy, spanId, options);
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
function(html, idx, obj, contact, buddy, spanId, options) {

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
		content = AjxStringUtil.htmlEncode(obj.toString(options && options.shortAddress));
	}

	var span = spanId && document.getElementById(spanId);
	if (span) {
		span.innerHTML = content;
	} else {
		html[idx++] = content;
		return idx;
	}
};

EmailTooltipZimlet.prototype._handleResponseGetContact =
function(html, idx, obj, spanId, options, contact) {
	if (contact) {
		var buddy = this._getBuddy(contact, obj.address);
		this._updateHtmlContent(html, idx, obj, contact, buddy, spanId, options);
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
	if(!this.tooltip) {
		return;
	}
	this._hoverOver =  false;
	this.tooltip._poppedUp = false;//makes the tooltip sticky
	setTimeout(AjxCallback.simpleClosure(this.popDownIfMouseNotOnSlide, this), 700);
	//override to ignore hoverout. 
};

EmailTooltipZimlet.prototype.popDownIfMouseNotOnSlide =
function() {
	if(this._hoverOver) {
		return;
	} else if(this.slideShow && this.slideShow.isMouseOverTooltip) {
		return;
	} else if(this.tooltip) {
		this.tooltip._poppedUp = true;//makes the tooltip non-sticky
		this.tooltip.popdown();
	}
};

EmailTooltipZimlet.prototype.popdown =
function() {
	this._hoverOver =  false;
	
	if(this.tooltip) {
		this.tooltip._poppedUp = true;
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
/**
* This is called from core-zimbra when the user hover-over's an email within msg/conv lists.
*
**/
EmailTooltipZimlet.prototype.onHoverOverEmailInList =
function(object, ev) {
	this.hoverOver(object, null, ev.docX, ev.docY);
};

EmailTooltipZimlet.prototype.hoverOver =
function(object, context, x, y, span) {
	this._hoverOver = true;
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
	//this is used by mail/conv list
    Dwt.setHandler(tooltip._div, DwtEvent.ONMOUSEOUT, AjxCallback.simpleClosure(this.hoverOut, this));

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

EmailTooltipZimlet.prototype._preLoadImgs =
function() {
	this._busyImg = new Image();
	this._busyImg.src = this.getResource("img/EmailZimlet_busy.gif");
	this.getShell().getHtmlElement().appendChild(this._busyImg);
	this._unknownPersonImg = new Image();
	this._unknownPersonImg.src = this.getResource("img/UnknownPerson_dataNotFound.jpg");
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
	this._busyImgTimer = setTimeout(AjxCallback.simpleClosure(this._handleNoImg, this, timeoutCallback), 4000);//hide busyImg after 4 secs
};

EmailTooltipZimlet.prototype.showLoadingAtId =
function(timeoutCallback, id) {
	var div = document.getElementById(id);
	div.innerHTML = ["<br/><br/><label style='color:gray'>", ZmMsg.loading, "</label>"].join("");
	this._busyImgTimer = setTimeout(AjxCallback.simpleClosure(this._handleNoImgAtId, this, timeoutCallback, id), 4000);//hide busyImg after 4 secs
};

EmailTooltipZimlet.prototype._handleNoImgAtId =
function(timeoutCallback, id) {
	clearTimeout(this._busyImgTimer);
	if (timeoutCallback) {
		timeoutCallback.run();
	}
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

EmailTooltipZimlet.prototype.createSearchMenu =
function(actionMenu) {
	if (this._searchMenu) { return; }

    var list = [ZmOperation.SEARCH, ZmOperation.SEARCH_TO];
    var overrides = {};
    overrides[ZmOperation.SEARCH] = {textKey:"findEmailFromRecipient"};
    overrides[ZmOperation.SEARCH_TO] = {textKey:"findEmailToRecipient"};

    this._searchMenu = new ZmActionMenu({parent:actionMenu, menuItems:list, overrides:overrides});
    var searchOp = actionMenu.getOp("SEARCHEMAILS");
    searchOp.setMenu(this._searchMenu);
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

    if (!isDetachWindow && appCtxt.get(ZmSetting.SEARCH_ENABLED) && actionMenu.getOp("SEARCHEMAILS")) {
        this.createSearchMenu(actionMenu);
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

	if (actionMenu.getOp("SEARCHEMAILS") && (isDetachWindow || !appCtxt.get(ZmSetting.SEARCH_ENABLED))) {
		ZmOperation.removeOperation(actionMenu, "SEARCHEMAILS", actionMenu._menuItems);
	}
    else{
        if (obj && obj.type) {
            if (actionMenu.getOp("SEARCHEMAILS")){
                 if (obj.type == "FROM"){
                    ZmOperation.setOperation(this._searchMenu, ZmOperation.SEARCH, ZmOperation.SEARCH, ZmMsg.findEmailFromSender);
                    ZmOperation.setOperation(this._searchMenu, ZmOperation.SEARCH_TO, ZmOperation.SEARCH_TO, ZmMsg.findEmailToSender);
                 } else{
                    ZmOperation.setOperation(this._searchMenu, ZmOperation.SEARCH, ZmOperation.SEARCH, ZmMsg.findEmailFromRecipient);
                    ZmOperation.setOperation(this._searchMenu, ZmOperation.SEARCH_TO, ZmOperation.SEARCH_TO, ZmMsg.findEmailToRecipient);
                 }
                 this._searchMenu.addSelectionListener("SEARCH", new AjxListener(this, this.menuItemSelected,["SEARCH",obj]));
                 this._searchMenu.addSelectionListener("SEARCH_TO", new AjxListener(this, this.menuItemSelected,["SEARCH_TO", obj]));
            }
        }
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

	this._actionObject = contentObjText;
	var contactList = AjxDispatcher.run("GetContacts");
	var addrContact = contactList ? contactList.getContactByEmail(contact) : null;
	// if contact found or there is no contact list (i.e. contacts app is disabled), go to compose view
	if (isMailTo || addrContact || contactList == null)
	{
		this._composeListener(ev, addr);
	}
	else
	{
		// otherwise, no contact in addrbook means go to contact edit view
		this._contactListener(true);
	}
};

EmailTooltipZimlet.prototype.menuItemSelected =
function(itemId, item, ev) {
	switch (itemId) {
		case "SEARCH":			this._searchListener();		break;
        case "SEARCH_TO":        this._searchToListener();   break;
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
	return obj.isAjxEmailAddress ? obj.address : obj;
};

EmailTooltipZimlet.prototype._contactListener =
function(isDirty) {
	this.popdown();
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

	this.popdown();

	var obj = this._actionObject;
	if (!addr) {
		addr = this._getAddress(obj) || "";
	}

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
	if (obj && obj.isAjxEmailAddress && obj.address == addr) {
		params.toOverrideObj = obj;
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

EmailTooltipZimlet.prototype._searchToListener =
function() {
	var addr = this._getAddress(this._actionObject);
	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	appCtxt.getSearchController().toSearch(this._getAddress(addr));
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

EmailTooltipZimlet.prototype.openCenteredWindow =
function (url) {
	this.popdown();
	var width = 800;
	var height = 600;
	var left = parseInt((screen.availWidth / 2) - (width / 2));
	var top = parseInt((screen.availHeight / 2) - (height / 2));
	var windowFeatures = "width=" + width + ",height=" + height + ",status,resizable,left=" + left + ",top=" + top + "screenX=" + left + ",screenY=" + top;
	var win = window.open(url, "subWind", windowFeatures);
	if (!win) {
		this._showWarningMsg(ZmMsg.popupBlocker);
	}
};

/**
 * Expands the distribution list address of the bubble with the given ID.
 *
 * @param {string}	bubbleId	ID of bubble
 * @param {string}	email		address to expand
 */
EmailTooltipZimlet.prototype.expandBubble =
function(bubbleId, email) {

	var bubble = document.getElementById(bubbleId);
	if (bubble) {
		var loc = Dwt.getLocation(bubble);
		loc.y += Dwt.getSize(bubble).y + 2;
		this._aclv.expandDL(email, null, null, null, loc);
	}
};

// handle click on an address (or "Select All") in popup DL expansion list
EmailTooltipZimlet.prototype._dlAddrSelected =
function(text, el, match, ev) {
	this._composeListener(ev, text);
};
