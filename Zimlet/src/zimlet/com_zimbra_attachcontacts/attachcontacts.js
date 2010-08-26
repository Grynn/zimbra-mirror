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


/**
 * Called by framework when compose toolbar is being initialized
 */
AttachContactsZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("COMPOSE") >= 0 && !this._addedToMainWindow) {
		var btn = toolbar.getOp("ATTACHMENT");
		btn.addSelectionListener(new AjxListener(this, this._addTab));	
	} else	if (viewId == "CNS") {
		this._initContactsReminderToolbar(toolbar, controller);
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
	for (var indx in tabs) {
		if (tabs[indx].title == tabLabel) {
			return;
		}
	}
	this.AttachContactsView = new AttachContactsTabView(tabview, this);

	this._tabkey = attachDialog.addTab("AttachContacts", tabLabel, this.AttachContactsView);
	this.AttachContactsView.attachDialog = attachDialog;

	var callback = new AjxCallback(this.AttachContactsView, this.AttachContactsView.uploadFiles);
	attachDialog.addOkListener(this._tabkey, callback);
	this._addedToMainWindow = true;
};

/**
 * Called by Framework when an email is about to be sent
 * @param request
 * @param isDraft
 */
AttachContactsZimlet.prototype.addExtraMsgParts =
function(request, isDraft) {
	if (!isDraft || !this._isDrafInitiatedByThisZimlet) {
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
	this._isDrafInitiatedByThisZimlet = false;
};


/**
 *  Called by Framework and adds toolbar button
 */
AttachContactsZimlet.prototype._initContactsReminderToolbar = function(toolbar, controller) {
	if (!toolbar.getButton("SEND_CONTACTS_IN_EMAIL")) {
		var opList = toolbar.opList;
		for (var i = 0; i < opList.length; i++) {
			if (opList[i] == "TAG_MENU") {
				buttonIndex = i + 1;
				break;
			}
		}
		var btn = toolbar.createOp("SEND_CONTACTS_IN_EMAIL", {image:"MsgStatusSent", text:this.getMessage("ACZ_Send"), tooltip:this.getMessage("ACZ_SendContactsAsAttachments"), index:buttonIndex});
		var buttonIndex = 0;


		this._composerCtrl = controller;
		this._composerCtrl._AttachContactsZimlet = this;
		btn.addSelectionListener(new AjxListener(this._composerCtrl, this._getIdsAndOpenCompose));
	}
};

/**
 * Fethes Contacts information(if any), save the contact and shows Contacts Reminder dialog w/ this Contacts information
 */
AttachContactsZimlet.prototype._getIdsAndOpenCompose = function() {
	var items = this.getCurrentView().getSelection();
	 this._AttachContactsZimlet.contactIdsToAttach = [];
	 for(var i=0; i < items.length; i++) {
		 this._AttachContactsZimlet.contactIdsToAttach.push(items[i].id);
	 }

	var action = ZmOperation.NEW_MESSAGE;
	var msg = new ZmMailMsg();
	AjxDispatcher.run("Compose", {action: action, inNewWindow: false, msg: msg});
    var controller = appCtxt.getApp(ZmApp.MAIL).getComposeController(appCtxt.getApp(ZmApp.MAIL).getCurrentSessionId(ZmId.VIEW_COMPOSE));
	this._AttachContactsZimlet._isDrafInitiatedByThisZimlet = true;   //set this to true
	controller.saveDraft(ZmComposeController.DRAFT_TYPE_MANUAL);
};