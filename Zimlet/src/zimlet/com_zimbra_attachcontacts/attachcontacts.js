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

AttachContactsZimlet.prototype.init =
function() {
	if (appCtxt.isChildWindow) {
		setTimeout(AjxCallback.simpleClosure(this._delayedAddTab, this), 1000);
	}
};

AttachContactsZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("COMPOSE") >= 0) {
		setTimeout(AjxCallback.simpleClosure(this._delayedAddTab, this), 1000);
	}
};

AttachContactsZimlet.prototype._delayedAddTab =
function() {
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