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
	this._op = ZmOperation.registerOp(AttachContactsZimlet.SEND_CONTACTS, {image:"MsgStatusSent", text:this.getMessage("ACZ_Send"), tooltip:this.getMessage("ACZ_SendContactsAsAttachments")});
	this._contactSendListener = new AjxListener(this, this._contactListSendListener);
	this.overrideAPI(ZmListController.prototype, "_setContactText", this._setContactText);

	this.setEmailActionMenu();
};

/**
 * Called by framework when attach is clicked
 */

AttachContactsZimlet.prototype.initializeAttachPopup =
function(menu, controller) {
    var mi = controller._createAttachMenuItem(menu, ZmMsg.contacts,
                    new AjxListener(this, this.showAttachmentDialog ));
};

AttachContactsZimlet.prototype.removePrevAttDialogContent =
function(contentDiv) {
    var elementNode =  contentDiv && contentDiv.firstChild;
    if (elementNode && elementNode.className == "DwtComposite" ){
        contentDiv.removeChild(elementNode);
    }
};

AttachContactsZimlet.prototype.showAttachmentDialog =
function() {

	var attachDialog = this._attachDialog = appCtxt.getAttachDialog();
    // To be changed
    this.removePrevAttDialogContent(attachDialog._getContentDiv().firstChild);

    if (!this.AttachContactsView || !this.AttachContactsView.attachDialog){
	    this.AttachContactsView = new AttachContactsTabView(this._attachDialog, this);

    }
    this.AttachContactsView.reparentHtmlElement(attachDialog._getContentDiv().childNodes[0], 0);
    this.AttachContactsView.attachDialog = attachDialog;
	attachDialog.setOkListener(new AjxCallback(this, this._okListener));
    this.AttachContactsView.attachDialog.popup();
    this.AttachContactsView.attachDialog.enableInlineOption(this._composeMode == DwtHtmlEditor.HTML);
    this._addedToMainWindow = true;
};

AttachContactsZimlet.prototype.onShowView =
function(viewId)  {
	var viewType = appCtxt.getViewTypeFromId(viewId);
	if (viewType == ZmId.VIEW_CONTACT_SIMPLE) {
		this._addContactActionMenuItem();
	}
};

AttachContactsZimlet.prototype._okListener =
function() {
	this.AttachContactsView.uploadFiles();
	this.AttachContactsView.setClosed(true);
    this.AttachContactsView.attachDialog.popdown();
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
	var op = AttachContactsZimlet.SEND_CONTACTS;
	if (toolbar.getButton(op) || !this._isOkayToAttach()) {
		return;
	}

	var opData = AjxUtil.hashCopy(ZmOperation.SETUP[op]);
	opData.text = this.getMessage("ACZ_Send");
	var button = toolbar.createZimletOp(op, opData);
	button.addSelectionListener(this._contactSendListener);

};


/**
 * Reset the toolbar
 *
 * @param	{ZmButtonToolBar}	toolbar			the toolbar
 * @param	{int}			    num		        number of items selected
 */
AttachContactsZimlet.prototype.resetToolbarOperations =
function(parent, num){
  if (parent.getOp(AttachContactsZimlet.SEND_CONTACTS)){
      parent.enable(AttachContactsZimlet.SEND_CONTACTS, num > 0 && this._isOkayToAttach());
  }
};


AttachContactsZimlet.prototype._isOkayToAttach = function() {
    this._getContactListIds();
    return this.contactIdsToAttach && this.contactIdsToAttach.length > 0;
};

AttachContactsZimlet.prototype._contactListSendListener = function() {
	this._getContactListIds();
	if (this.contactIdsToAttach && this.contactIdsToAttach.length)
		this._openCompose();
};

AttachContactsZimlet.prototype._getContactListIds = function() {
	var controller = appCtxt.getApp(ZmApp.CONTACTS).getContactListController();
	this.contactIdsToAttach = [];
	var listView = controller.getListView();
	if (listView) {
		var items = listView.getSelection();
		for (var i=0; i<items.length; i++) {
			if (!items[i].isGroup()) {
				this.contactIdsToAttach.push(items[i].id);
			}
		}
	}
	return this.contactIdsToAttach;
};

AttachContactsZimlet.prototype._openCompose = function() {
	var action = ZmOperation.NEW_MESSAGE;
	var msg = new ZmMailMsg();
	AjxDispatcher.run("Compose", {action: action, inNewWindow: false, msg: msg});
	var controller = appCtxt.getApp(ZmApp.MAIL).getComposeController(appCtxt.getApp(ZmApp.MAIL).getCurrentSessionId(ZmId.VIEW_COMPOSE));
	this._isDraftInitiatedByThisZimlet = true;
	controller.saveDraft(ZmComposeController.DRAFT_TYPE_AUTO);
};

/**
 * Overrides method in ZmListController
 */
AttachContactsZimlet.prototype._setContactText =
function(isContact) {
	if (this._participantActionMenu) {
		this._participantActionMenu.enable(AttachContactsZimlet.SEND_CONTACTS, isContact); // Set enabled/disabled depending on whether we have a contact for the participant
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
	this.addMenuButton(new AjxCallback(this, this._getContactFromController, [controller]), menu, ZmOperation.CONTACT);
};

AttachContactsZimlet.prototype._addContactActionMenuItem =
function() {
	var controller = appCtxt.getApp(ZmApp.CONTACTS).getContactListController();
	this.addMenuButton(new AjxCallback(this, this._getContactFromController, [controller]), controller.getActionMenu(), ZmOperation.CONTACT);
};

AttachContactsZimlet.prototype._getContactFromController =
function(controller) {
	if (controller) {
		if (controller instanceof ZmContactListController) {
			var view = controller.getListView();
			if (view) {
				var selection = view.getSelection();
				if (selection && selection.length>1)
					return selection;
			}
		}
		return (controller && controller._actionEv && controller._actionEv.contact) || null;
	}
};

/**
 * Adds a menu item for emails
 * @param {ZmMsgController} controller
 * @param {object} menu  Menu object
 */
AttachContactsZimlet.prototype.addMenuButton = function(contactCallback, menu, after) {
	if (contactCallback && menu && !menu.getMenuItem(AttachContactsZimlet.SEND_CONTACTS)) {
		var index = null;
		if (AjxUtil.isString(after)) {
			var afterItem = menu.getMenuItem(after);
			if (afterItem) {
				for (index = 0, c = menu.getChildren(); index < c.length && c[index] !== afterItem; index++) ; // Find index of the afterItem
			}
		} else if (AjxUtil.isNumber(after)) {
			index = after;
		}
		if (!after || index !== null) {
			var op = {
				id:			AttachContactsZimlet.SEND_CONTACTS,
				text:		this.getMessage("ACZ_SendContact"),
				image:		"MsgStatusSent"
			};
			if (index!==null)
				op.index = index+1;
			var opDesc = ZmOperation.defineOperation(null, op);
			menu.addOp(AttachContactsZimlet.SEND_CONTACTS);
			menu.addSelectionListener(AttachContactsZimlet.SEND_CONTACTS, new AjxListener(this, this._contactActionMenuListener, contactCallback));
		}
	}
};

AttachContactsZimlet.prototype._contactActionMenuListener = function(contactCallback, ev) {
	var contact = contactCallback.run();
	if (contact) {
		var contacts = AjxUtil.toArray(contact);
		if (contacts.length) {
			this.contactIdsToAttach = [];
			for (var i=0; i<contacts.length; i++) {
                if (!contacts[i].isGroup()){
				    this.contactIdsToAttach.push(contacts[i].id);
                }
			}
            if (this.contactIdsToAttach.length > 0)
			    this._openCompose();
		}
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

//---------------------------------------------------

/**
 * Try to add to add to the actionmenu provided by com_zimbra_email
 */
AttachContactsZimlet.prototype.setEmailActionMenu = function() {
	if (window.com_zimbra_email_handlerObject) { // Other zimlet exists
		this.overrideAPI(com_zimbra_email_handlerObject.prototype, "getActionMenu", AjxCallback.simpleClosure(AttachContactsZimlet._getEmailActionMenu, null, this));
	}
};

/**
 * Will override EmailTooltipZimlet.prototype.getActionMenu in com_zimbra_email
 */
AttachContactsZimlet._getEmailActionMenu = function(attachContactsZimlet) {
	var args = Array.prototype.slice.call(arguments, 1); // Cut off our own argument
	var menu = this.getActionMenu.func.apply(this, args); // Call overridden function
	var contactCallback = new AjxCallback(this, this._getActionedContact, [false]); // Callback to method in com_zimbra_email
	var contact = contactCallback.run();
	if (contact && !contact.isGal && contact.id) {
		attachContactsZimlet.addMenuButton(contactCallback, menu, "NEWCONTACT");
	} else {
		if (menu.getOp(AttachContactsZimlet.SEND_CONTACTS))
			menu.enable(AttachContactsZimlet.SEND_CONTACTS, false);
	}
	return menu;
};

