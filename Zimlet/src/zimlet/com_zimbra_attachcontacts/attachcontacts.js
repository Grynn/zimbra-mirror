/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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

/**
 * Constructor.
 *
 * @author Raja Rao DV
 */
function com_zimbra_attachcontacts_HandlerObject() {
}
;

com_zimbra_attachcontacts_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_attachcontacts_HandlerObject.prototype.constructor = com_zimbra_attachcontacts_HandlerObject;

/**
 * Simplify handler object
 *
 */
var AttachContactsZimlet = com_zimbra_attachcontacts_HandlerObject;

AttachContactsZimlet.SEND_CONTACTS = "SEND_CONTACTS_IN_EMAIL";

AttachContactsZimlet.prototype.init = function() {
	this._op = ZmOperation.registerOp(AttachContactsZimlet.SEND_CONTACTS, {image:"MsgStatusSent", textKey:"forward", tooltip:this.getMessage("ACZ_SendContactsAsAttachments")});
	this._contactSendListener = new AjxListener(this, this._contactListSendListener);
	this.overrideAPI(ZmListController.prototype, "_setContactText", this._setContactText);
};

/**
 * Called by framework when compose toolbar is being initialized
 */
AttachContactsZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("COMPOSE") >= 0 && !this._addedToMainWindow) {
		var btn = toolbar.getOp("ATTACHMENT");
		btn.addSelectionListener(new AjxListener(this, this._addTab));	
	} else if (viewId == "CNS" || viewId == "CN") {
		this._initContactsReminderToolbar(toolbar, controller);
	}
};

AttachContactsZimlet.prototype.onShowView =
function(viewId)  {
	if (viewId == "CNS") {
		this._addContactActionMenuItem();
	}
};

AttachContactsZimlet.prototype._addTab =
function() {
	if(this._addedToMainWindow) {
		return;
	}
	var tabLabel = this.getMessage("ACZ_tab_label");
	var attachDialog = this._attachDialog = appCtxt.getAttachDialog();

	var tabview = attachDialog ? attachDialog.getTabView() : null;
	var tabs = attachDialog.getTabView()._tabs;
	for (var index in tabs) {
		if (tabs[index].title == tabLabel) {
			return;
		}
	}
	this.AttachContactsView = new AttachContactsTabView(tabview, this);

	this._tabkey = attachDialog.addTab("AttachContacts", tabLabel, this.AttachContactsView);
	this.AttachContactsView.attachDialog = attachDialog;

	attachDialog.addOkListener(this._tabkey, new AjxCallback(this, this._okListener));
	this._addedToMainWindow = true;
};

AttachContactsZimlet.prototype._okListener =
function() {
	this.AttachContactsView.uploadFiles();
	this.AttachContactsView.setClosed(true);
}

/**
 * Called by Framework when an email is about to be sent
 * @param request
 * @param isDraft
 */
AttachContactsZimlet.prototype.addExtraMsgParts =
function(request, isDraft) {
	if (!isDraft || !this._isDraftInitiatedByThisZimlet) {
		return;
	}
	if (request && request.m) {
		if (!request.m.attach) {
			request.m.attach = {};
			request.m.attach.cn = [];
		} else if (!request.m.attach.cn) {
			request.m.attach.cn = [];
		}
		var attmnts = this.contactIdsToAttach;
		if (attmnts) {
			for (var i = 0; i < attmnts.length; i++) {
				request.m.attach.cn.push({id:attmnts[i]});
			}
		}
	}
	this._isDraftInitiatedByThisZimlet = false;
};


/**
 *  Called by Framework and adds toolbar button
 */
AttachContactsZimlet.prototype._initContactsReminderToolbar = function(toolbar, controller) {
	if (!toolbar.getButton(AttachContactsZimlet.SEND_CONTACTS)) {
		var opList = toolbar.opList;
		var buttonIndex = 0;
		for (var i = 0; i < opList.length; i++) {
			if (opList[i] == "TAG_MENU") {
				buttonIndex = i + 1;
				break;
			}
		}

		var op = AttachContactsZimlet.SEND_CONTACTS;
		var opData = AjxUtil.hashCopy(ZmOperation.SETUP[op]);
		opData.index = buttonIndex;
		opData.text = ZmMsg[ZmOperation.getProp(op, "textKey")];
		var btn = toolbar.createOp(op, opData);
		btn.addSelectionListener(this._contactSendListener);
	}
};

AttachContactsZimlet.prototype._contactListSendListener = function() {
	this._getContactListIds();	
	this._openCompose();
};

AttachContactsZimlet.prototype._getContactListIds = function() {
	var controller = appCtxt.getApp(ZmApp.CONTACTS).getContactListController();
	var items = controller.getCurrentView().getSelection();
	this.contactIdsToAttach = [];
	for (var i=0; i<items.length; i++) {
		this.contactIdsToAttach.push(items[i].id);
	}
};

AttachContactsZimlet.prototype._openCompose = function() {
	var action = ZmOperation.NEW_MESSAGE;
	var msg = new ZmMailMsg();
	AjxDispatcher.run("Compose", {action: action, inNewWindow: false, msg: msg});
	var controller = appCtxt.getApp(ZmApp.MAIL).getComposeController(appCtxt.getApp(ZmApp.MAIL).getCurrentSessionId(ZmId.VIEW_COMPOSE));
	this._isDraftInitiatedByThisZimlet = true;   //set this to true
	controller.saveDraft(ZmComposeController.DRAFT_TYPE_MANUAL);
};

/**
 * Overrides method in ZmListController
 */
AttachContactsZimlet.prototype._setContactText =
function(isContact) {
	if (this._participantActionMenu) {
		this._participantActionMenu.enable(AttachContactsZimlet.SEND_CONTACTS, isContact);
	}
	arguments.callee.func.apply(this, arguments); // Call overridden function
};

//------------------------------------------------
// Context menu / clear highlight related
//------------------------------------------------
/**
 *  Called by Framework to add a context-menu item for emails
 */
AttachContactsZimlet.prototype.onParticipantActionMenuInitialized =
function(controller, menu) {
	this.onActionMenuInitialized(controller, menu);
};

/**
 *  Called by Framework to add a context-menu item for emails
 */
AttachContactsZimlet.prototype.onActionMenuInitialized =
function(controller, menu) {
	this.addMenuButton(controller, menu, ZmOperation.CONTACT);
};

AttachContactsZimlet.prototype._addContactActionMenuItem =
function() {
	var controller = appCtxt.getApp(ZmApp.CONTACTS).getContactListController();
	this.addMenuButton(controller, controller.getActionMenu(), ZmOperation.CONTACT);
};

/**
 * Adds a menu item for emails
 * @param {ZmMsgController} controller
 * @param {object} menu  Menu object
 */
AttachContactsZimlet.prototype.addMenuButton = function(controller, menu, after) {
	if (!menu.getMenuItem(AttachContactsZimlet.SEND_CONTACTS)) {
		var index = null;
		if (AjxUtil.isString(after)) {
			var afterItem = menu.getMenuItem(after);
			if (afterItem) {
				for (index = 0, c = menu.getChildren(); index < c.length && c[index] !== afterItem; index++) ; // Find index of the afterItem
			}
		} else if (AjxUtil.isNumber(after)) {
			index = after;
		}
		if (index !== null) {
			var op = {
				id:			AttachContactsZimlet.SEND_CONTACTS,
				text:		this.getMessage("ACZ_SendContact"),
				image:		"MsgStatusSent",
				index:		index+1
			};
			var opDesc = ZmOperation.defineOperation(null, op);
			menu.addOp(AttachContactsZimlet.SEND_CONTACTS);
			menu.addSelectionListener(AttachContactsZimlet.SEND_CONTACTS, new AjxListener(this, this._contactActionMenuListener, controller));
		}
	}
};

ZmZimletBase.prototype.onMsgView = function(msg, oldMsg) {
console.log("ZmZimletBase.prototype.onMsgView",arguments);
};

AttachContactsZimlet.prototype._contactActionMenuListener = function(controller, ev) {
	var contact = controller._actionEv.contact;
	if (contact) {
		this.contactIdsToAttach = [contact.id];
		this._openCompose();
	}
};

AttachContactsZimlet.prototype.overrideAPI = function(object, funcname, newfunc) {
    newfunc = newfunc || this[funcname];
    if (newfunc) {
        var oldfunc = object[funcname];
        object[funcname] = function() {
            newfunc.func = oldfunc; 
            return newfunc.apply(this, arguments);
        }
        object[funcname].func = oldfunc;
    }
};
