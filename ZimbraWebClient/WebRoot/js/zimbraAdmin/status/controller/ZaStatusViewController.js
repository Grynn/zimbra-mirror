/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007, 2008, 2009, 2010 Zimbra, Inc.
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
* @class ZaStatusViewController 
* @contructor ZaStatusViewController
* @param appCtxt
* @param container
* @param app
* @author Roland Schemers
* @author Greg Solovyev
**/
ZaStatusViewController = function(appCtxt, container) {
	ZaController.call(this, appCtxt, container,"ZaStatusViewController");
	this._helpURL = location.pathname + ZaUtil.HELP_URL + "managing_servers/monitoring_zimbra_collaboration_suite.htm?locid="+AjxEnv.DEFAULT_LOCALE;
	this._helpButtonText = ZaStatusViewController.helpButtonText;	
   	this._popupOperations = new Array();
	this._UICreated = false;	
}

ZaStatusViewController.prototype = new ZaController();
ZaStatusViewController.prototype.constructor = ZaStatusViewController;
ZaController.initToolbarMethods["ZaStatusViewController"] = new Array();
ZaController.initPopupMenuMethods["ZaStatusViewController"] = new Array();
ZaStatusViewController.helpButtonText = ZaMsg.helpEditDomains;

ZaStatusViewController.prototype.show = function(openInNewTab) {
	try {
		this._createUI(openInNewTab);
		var statusObj = new ZaStatus();
		statusObj.load();
		var statusVector = statusObj.getStatusVector();
		this._contentView.set(statusVector);
		ZaApp.getInstance().pushView(this.getContentViewId());
		var now = new Date();
	} catch (ex) {
		this._handleException(ex, "ZaStatusViewController.prototype.show", null, false);
		return;
	}	
};

ZaStatusViewController.prototype.getAppBarAction =function () {
    if (AjxUtil.isEmpty(this._appbarOperation)) {
    	this._appbarOperation[ZaOperation.HELP]=new ZaOperation(ZaOperation.HELP,ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
        this._appbarOperation[ZaOperation.CLOSE] = new ZaOperation(ZaOperation.CLOSE, ZaMsg.TBB_Close, ZaMsg.ALTBB_Close_tt, "", "", new AjxListener(this, this.closeButtonListener));
    }

    return this._appbarOperation;
}

ZaStatusViewController.prototype.getAppBarOrder = function () {
    if (AjxUtil.isEmpty(this._appbarOrder)) {
    	this._appbarOrder.push(ZaOperation.HELP);
        this._appbarOrder.push(ZaOperation.CLOSE);
    }

    return this._appbarOrder;
}

ZaStatusViewController.prototype._createUI = function (openInNewTab) {
	try {
		var elements = new Object();
		this._contentView = new ZaServicesListView(this._container);
		this._initPopupMenu();
		if(this._popupOperations && this._popupOperations.length) {
			this._acctionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_STATUSLIST, ZaId.MENU_POP);
		}
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		//ZaApp.getInstance().createView(ZaZimbraAdmin._STATUS, elements);
		ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
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
		this.popupErrorDialog(ZaMsg.SERVER_ERROR, ex);
	} else {
		ZaController.prototype._handleException.call(this, ex, method, params, restartOnError, obj);
	}
}	
