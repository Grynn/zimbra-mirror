/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* @class ZaStatusViewController 
* @contructor ZaStatusViewController
* @param appCtxt
* @param container
* @param app
* @author Roland Schemers
* @author Greg Solovyev
**/
function ZaStatusViewController(appCtxt, container, app) {
	ZaController.call(this, appCtxt, container, app, "ZaStatusViewController");
   	this._toolbarOperations = new Array();
   	this._popupOperations = new Array();
	this._UICreated = false;	
}

ZaStatusViewController.prototype = new ZaController();
ZaStatusViewController.prototype.constructor = ZaStatusViewController;
ZaController.initToolbarMethods["ZaStatusViewController"] = new Array();
ZaController.initPopupMenuMethods["ZaStatusViewController"] = new Array();

ZaStatusViewController.prototype.show = function() {
	try {
	    if (!this._UICreated) {
			this._createUI();
		}
		var statusObj = new ZaStatus(this._app);
		statusObj.load();
		var statusVector = statusObj.getStatusVector();
		this._contentView.set(statusVector);
		this._app.pushView(ZaZimbraAdmin._STATUS);
		var now = new Date();
		this._toolbar.getButton("refreshTime").setText(ZaMsg.TBB_LastUpdated + " " + AjxDateUtil.computeTimeString(now));
	} catch (ex) {
		this._handleException(ex, "ZaStatusViewController.prototype.show", null, false);
		return;
	}	
};

ZaStatusViewController.initToolbarMethod =
function () {
	// first button in the toolbar is a menu.
	var newMenuOpList = new Array();
	this._toolbarOperations.push(new ZaOperation(ZaOperation.LABEL, ZaMsg.TBB_LastUpdated, ZaMsg.TBB_LastUpdated_tt, null, null, null,null,null,null,"refreshTime"));	
	this._toolbarOperations.push(new ZaOperation(ZaOperation.SEP));
	this._toolbarOperations.push(new ZaOperation(ZaOperation.REFRESH, ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, "Refresh", "Refresh", new AjxListener(this, this.refreshListener)));	
}
ZaController.initToolbarMethods["ZaStatusViewController"].push(ZaStatusViewController.initToolbarMethod);

ZaStatusViewController.prototype._createUI = function () {
	try {
		var elements = new Object();
		this._contentView = new ZaServicesListView(this._container, this._app);
		this._initToolbar();
		if(this._toolbarOperations && this._toolbarOperations.length) {
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations); 
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
		}
		this._initPopupMenu();
		if(this._popupOperations && this._popupOperations.length) {
			this._acctionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations);
		}
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		this._app.createView(ZaZimbraAdmin._STATUS, elements);
		this._UICreated = true;
	} catch (ex) {
		this._handleException(ex, "ZaStatusViewController.prototype._createUI", null, false);
		return;
	}	
}

ZaStatusViewController.prototype.refreshListener = function () {
	this.show();
}

ZaStatusViewController.prototype._handleException =
function(ex, method, params, restartOnError, obj) {
	if (ex.code && ex.code == ZmCsfeException.SVC_AUTH_REQUIRED) {
		this.popupErrorDialog(ZaMsg.SERVER_ERROR, ex, true);
	} else {
		ZaController.prototype._handleException.call(this, ex, method, params, restartOnError, obj);
	}
}	
