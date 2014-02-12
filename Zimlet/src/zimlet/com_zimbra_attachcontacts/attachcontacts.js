/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2010, 2011, 2012, 2013 Zimbra Software, LLC.
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.4 ("License"); you may not use this file except in
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
function com_zimbra_attachcontacts_HandlerObject() {};

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
};

/**
 * Called by framework when attach is clicked
 */

AttachContactsZimlet.prototype.initializeAttachPopup =
function(menu, controller) {
	controller._createAttachMenuItem(menu, ZmMsg.contacts, this.showAttachmentDialog.bind(this), "ATTACH_MENU_CONTACT");
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
	attachDialog.setTitle(ZmMsg.attachContact);
	attachDialog.setButtonEnabled(DwtDialog.OK_BUTTON, false);
    this.removePrevAttDialogContent(attachDialog._getContentDiv().firstChild);

    if (!this.AttachContactsView || !this.AttachContactsView.attachDialog){
	    this.AttachContactsView = new AttachContactsTabView(this._attachDialog, this);

	    var selectionListener = this._selectionListener.bind(this);
	    this.AttachContactsView.addListener(DwtEvent.SELECTION,
	                                        selectionListener);
    }

    this.AttachContactsView.reparentHtmlElement(attachDialog._getContentDiv().childNodes[0], 0);
    this.AttachContactsView.attachDialog = attachDialog;
	attachDialog.setOkListener(new AjxCallback(this, this._okListener));
    this.AttachContactsView.attachDialog.popup();
    this.AttachContactsView.attachDialog.enableInlineOption(this._composeMode == Dwt.HTML);
    this._addedToMainWindow = true;
};

AttachContactsZimlet.prototype._okListener =
function() {
	this.AttachContactsView.uploadFiles();
	this.AttachContactsView.setClosed(true);
    this.AttachContactsView.attachDialog.popdown();
};

AttachContactsZimlet.prototype._selectionListener =
function() {
	var hasselection = this.AttachContactsView.getSelectionCount() > 0;
	this._attachDialog.setButtonEnabled(DwtDialog.OK_BUTTON, hasselection);
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
                    var attmntsId = attmnts[i];
                    if( appCtxt.multiAccounts && attmnts[i].indexOf(":") == -1) {
                        //Use fully qualified Ids for multi-accounts
                        attmntsId = [appCtxt.getActiveAccount().id, ":", attmnts[i]].join("");
                    }
                request.m.attach.cn.push({id:attmntsId});
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
 * @param	{ZmActionMenu}	 	parent			the action menu (or something else we don't care about)
 * @param	{int}			    num		        number of items selected
 */
AttachContactsZimlet.prototype.resetContactListToolbarOperations =
function(parent, num){
	var menu = parent;
	var after = null;
	if (parent.isZmButtonToolBar) {
		menu = menu.getActionsMenu();
		after = ZmOperation.NEW_MESSAGE;
	}
	this._addContactActionMenuItem(menu, after);
	menu.enable(AttachContactsZimlet.SEND_CONTACTS, num > 0 && this._isOkayToAttach());
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
	var controller = appCtxt.getCurrentController();
	this.contactIdsToAttach = [];
	var listView = controller.getListView && controller.getListView();
	if (listView) {
		var items = listView.getSelection();
		for (var i = 0; i < items.length; i++) {
			var contact = items[i];
			if (contact.isGal || contact.isGroup()) {
				continue;
			}
			this.contactIdsToAttach.push(contact.id);
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
function(contact) {
	if (this._participantActionMenu) {
		this._participantActionMenu.enable(AttachContactsZimlet.SEND_CONTACTS, contact && !contact.isGal); // Set enabled/disabled depending on whether we have a contact for the participant
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
	this.addMenuButton(this._getContact.bind(this), menu, ZmOperation.CONTACT);
};

AttachContactsZimlet.prototype._addContactActionMenuItem =
function(actionMenu, after) {
	this.addMenuButton(this._getContact.bind(this), actionMenu, after || ZmOperation.CONTACT);
};

AttachContactsZimlet.prototype._getContact =
function() {
	var controller = appCtxt.getCurrentController();
	if (!controller) {
		return;
	}

	var actionedContact = controller._actionEv && controller._actionEv.contact;

	if (controller instanceof ZmContactListController) {
		var view = controller.getListView();
		if (view) {
			var selection = view.getSelection();
			if (selection && selection.length > 0) {
				if (!actionedContact) {
					return selection;
				}
				//If there's an actioned contact, and it's part of the selection - return the entire selection.
				//otherwise only the actioned contact should be returned. That's consistent with the mail app etc.
				for (var i = 0; i < selection.length; i++) {
					if (selection[i] === actionedContact) {
						return selection;
					}
				}
			}
		}
	}
	return actionedContact;
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
