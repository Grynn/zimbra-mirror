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
	this._bubbleParams = {};
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
		if (appCtxt.get(ZmSetting.IM_ENABLED)) {
			this._presenceCache = [];
		}
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

EmailTooltipZimlet.prototype.onFindMsgObjects =
function() {
	
	if (appCtxt.get(ZmSetting.USE_ADDR_BUBBLES)) {
		// TODO: dispose old bubbles
		this._bubbleList = new ZmAddressBubbleList();
		this._bubbleList.addSelectionListener(new AjxListener(this, this._bubbleSelectionListener));
		this._bubbleList.addActionListener(new AjxListener(this, this._bubbleActionListener));
		this._bubbleParams = {};
	}
};

// create bubble for address in header
EmailTooltipZimlet.prototype.generateSpan =
function(html, idx, obj, spanId, context, options) {
	options = options || {};
	if (options.addrBubbles) {
		this._isBubble[spanId] = true;
		var canExpand = obj.isGroup && obj.canExpand && appCtxt.get("EXPAND_DL_ENABLED");
		if (canExpand && !this._aclv) {
			// create a ZmAutocompleteListView to handle DL expansion; it's never shown
			var aclvParams = {
				dataClass:		appCtxt.getAutocompleter(),
				matchValue:		ZmAutocomplete.AC_VALUE_FULL,
				options:		{addrBubbles:true, massDLComplete:true},
				compCallback:	this._dlAddrSelected.bind(this),
				contextId:		this.name
			};
			this._aclv = new ZmAutocompleteListView(aclvParams);
		}

		// We'll be creating controls (bubbles) later, so we provide the tooltip now and let the control manage
		// it instead of the zimlet framework.
		var bubbleParams = {
			parent:		appCtxt.getShell(),
			parentId:	this._internalId,
			addrObj:	obj,
			id:			spanId,
			canExpand:	canExpand,
			email:		this._getAddress(obj),
			separator:	AjxEmailAddress.SEPARATOR
		};
		ZmAddressInputField.BUBBLE_OBJ_ID[spanId] = this._internalId;	// pretend to be a ZmAddressInputField
		this._bubbleParams[spanId] = bubbleParams;
		
		// placeholder SPAN
		html[idx++] = "<span id='" + spanId + "'>";
		html[idx++] = "</span>";
		return idx;
	} else {
		return ZmObjectHandler.prototype.generateSpan.apply(this, arguments);
	}
};

EmailTooltipZimlet.prototype.onMsgView =
function() {
	this._createBubbles();
};

// Called from conversation view - used there instead of onMsgView because onMsgView is a
// commonly implemented call whose use would invoke numerous Zimlets that are inappropriate
// for a conversation view.
EmailTooltipZimlet.prototype.onConvView =
function() {
	this._createBubbles();
};

EmailTooltipZimlet.prototype._createBubbles =
function() {
	for (var id in this._bubbleParams) {
		// make sure SPAN was actually added to DOM (may have been ignored by template, for example)
		if (!document.getElementById(id)) {
			continue;
		}
		var bubbleParams = this._bubbleParams[id];
		var bubble = new ZmAddressBubble(bubbleParams);
		bubble.replaceElement(id);
		if (this._bubbleList) {
			this._bubbleList.add(bubble);
		}
	}
}

EmailTooltipZimlet.prototype._bubbleSelectionListener =
function(ev) {

	var bubble = ev.item;
	if (ev.detail == DwtEvent.ONDBLCLICK) {
		this._composeListener(ev, bubble.address);
	}
	else if (this._bubbleList && this._bubbleList.selectAddressText) {
		this._bubbleList.selectAddressText();
	}
};

EmailTooltipZimlet.prototype._bubbleActionListener =
function(ev) {

	var bubble = ev.item;
	var menu = this.getActionMenu(bubble.addrObj);
	if (menu) {
		this._actionBubble = bubble;
		menu.popup(0, ev.docX, ev.docY);
	}
};

EmailTooltipZimlet.prototype._menuPopdownListener =
function() {

	if (!appCtxt.get(ZmSetting.USE_ADDR_BUBBLES)) { return; }
	
	if (this._actionBubble) {
		this._actionBubble.setClassName(this._bubbleClassName);
	}
	
	// use a timer since popdown happens before listeners are called; alternatively, we could put the
	// code below at the end of every menu action listener
	AjxTimedAction.scheduleAction(new AjxTimedAction(this,
		function() {
			this._actionBubble = null;
			if (this._bubbleList) {
				this._bubbleList.clearRightSelection();
			}
		}), 10);
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
function(object, context, span, spanId) {

	if(!this.tooltip) {	return;	}
	if (spanId && this._bubbleParams[spanId]) { return; }

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

// This is called by the zimlet framework.
EmailTooltipZimlet.prototype.hoverOver =
function(object, context, x, y, span) {
	var shell = DwtShell.getShell(window);
	var tooltip = shell.getToolTip();
	tooltip.setContent('<div id="zimletTooltipDiv"/>', true);
	this.x = x;
	this.y = y;
	if (!this.toolTipPoppedUp(span, object, context, document.getElementById("zimletTooltipDiv"))) {
		tooltip.popup(x, y, true, new AjxCallback(this, this.hoverOut, object, context, span));
	}
};

EmailTooltipZimlet.prototype.toolTipPoppedUp =
function(spanElement, contentObjText, matchContext, canvas) {
	var tooltip = appCtxt.getToolTipMgr().getToolTip(ZmToolTipMgr.PERSON, {address:contentObjText});
	if (tooltip) {
		// for some reason canvas is not the live element, need to fetch it from DOM here
		var tooltipDiv = document.getElementById("zimletTooltipDiv");
		if (tooltipDiv) {
			tooltipDiv.innerHTML = tooltip;
		}
		return false;
	}
	return true;
};

// This is called from the core tooltip manager.
EmailTooltipZimlet.prototype.onHoverOverEmailInList =
function(object, ev, noRightClick) {

	if (!object || !object.address) {
		return false;
	}
	var x = ev ? ev.docX : this.x;
	var y = ev ? ev.docY : this.y;
	this.noRightClick = noRightClick;
	return this.handleHover(object, null, x, y);
};

// return true if we have handled the hover
EmailTooltipZimlet.prototype.handleHover =
function(object, context, x, y, span, spanId) {

	if (spanId && this._bubbleParams[spanId]) { return false; }

	this._hoverOver = true;
	this._initializeProps(object, context, x, y, span);
	appCtxt.notifyZimlets("onEmailHoverOver", [this], {waitUntilLoaded:true});
	if (this.primarySubscriberZimlet) {
		this.primarySubscriberZimlet.showTooltip();
		return true;
	}
	else if (this._subscriberZimlets.length > 0 && !this.primarySubscriberZimlet) {
		this._unknownPersonSlide = new UnknownPersonSlide();
		this._unknownPersonSlide.onEmailHoverOver(this);
		this._unknownPersonSlide.showTooltip();
		return true;
	}

	return false;
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
	else {
		rule = this._rules.getRuleByName(rule.name) || rule;
	}

	var addr = this._getAddress(this._actionObject);
	if (AjxUtil.isString(addr) && this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}
	var subjMod = ZmFilterRule.C_ADDRESS_VALUE[ZmFilterRule.C_FROM];
	rule.addCondition(ZmFilterRule.TEST_ADDRESS, ZmFilterRule.OP_IS, addr, subjMod);

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
	var addr = this._getAddress(obj);
	if (this.isMailToLink(addr)) {
		addr = (this.parseMailToLink(addr)).to || addr;
	}

	if (!(appCtxt.get(ZmSetting.CONTACTS_ENABLED) || appCtxt.isOffline)) {
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

	if (actionMenu.getOp("ADDTOFILTER") && (isDetachWindow || !appCtxt.get(ZmSetting.FILTERS_ENABLED))) {
		ZmOperation.removeOperation(actionMenu, "ADDTOFILTER", actionMenu._menuItems);
	}

	var contactsApp = appCtxt.getApp(ZmApp.CONTACTS);
	var contact = contactsApp && contactsApp.getContactByEmail(addr);
	var newContactAction = actionMenu.getOp("NEWCONTACT");
	if (newContactAction) {
		newContactAction.setVisible(true);
	}
	if (contact) {
		// contact for this address was found in the cache
		if (contact.isDistributionList() && newContactAction) {
			//do not allow editing a DL in this way (if user is owner, they can edit via the DL folder/toolbar)
			// And most likley this is a regular user that is not the owner anyway. So let's keep it simple
			newContactAction.setVisible(false);
		}
		ZmOperation.setOperation(actionMenu, "NEWCONTACT", ZmOperation.EDIT_CONTACT);
	} else {
		// contact not found, do a search
		if (contactsApp && !contact && contact !== null) {
            if (actionMenu.getOp("NEWCONTACT")) {
			    actionMenu.getOp("NEWCONTACT").setText(ZmMsg.loading);
            }
			var respCallback = new AjxCallback(this, this._handleResponseGetContact1, [actionMenu]);
			contactsApp.getContactByEmail(addr, respCallback);
		} else {
			ZmOperation.setOperation(actionMenu, "NEWCONTACT", ZmOperation.NEW_CONTACT, ZmMsg.AB_ADD_CONTACT);
		}
	}
	
	if (!actionMenu.isListenerRegistered(DwtEvent.POPDOWN)) {
		actionMenu.addPopdownListener(new AjxListener(this, this._menuPopdownListener));
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
	if (!match) {
		match = str.match(/\bmailto\:([^\?]+)/i);
	}
	parts.to = match ? decodeURIComponent(match[1]) : null;

	match = str.match(/\bbody=([^&]+)/i);
	parts.body = match ? decodeURIComponent(match[1]) : null;

	return parts;
};

EmailTooltipZimlet.prototype.clicked =
function(spanElement, contentObjText, matchContext, ev) {

	var spanId = spanElement && spanElement.id;
	if (spanId && this._bubbleParams[spanId]) { return; }

	if (this.tooltip) {
		this.tooltip.popdown();
	}

	this._actionObject = contentObjText;
	this._composeListener(ev, this._getAddress(contentObjText));
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
	addr = addr ? this._getAddress(addr) : (obj ? this._getAddress(obj) : "");

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
		params.toOverride = obj;
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
	var subjMod = ZmFilterRule.C_ADDRESS_VALUE[ZmFilterRule.C_FROM];
	rule.addCondition(ZmFilterRule.TEST_ADDRESS, ZmFilterRule.OP_IS, addr, subjMod);
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
		this._aclv.expandDL({email:email, loc:loc});
	}
};

// handle click on an address (or "Select All") in popup DL expansion list
EmailTooltipZimlet.prototype._dlAddrSelected =
function(text, el, match, ev) {
	this._composeListener(ev, text);
};
