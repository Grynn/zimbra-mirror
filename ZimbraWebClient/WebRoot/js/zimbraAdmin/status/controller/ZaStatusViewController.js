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
   	this._toolbarOperations = new Array();
   	this._toolbarOrder = new Array();
   	this._popupOperations = new Array();
	this._UICreated = false;	
}

ZaStatusViewController.prototype = new ZaController();
ZaStatusViewController.prototype.constructor = ZaStatusViewController;
ZaController.initToolbarMethods["ZaStatusViewController"] = new Array();
ZaController.initPopupMenuMethods["ZaStatusViewController"] = new Array();

ZaStatusViewController.prototype.show = function(openInNewTab) {
	try {
	    if (!this._UICreated) {
			this._createUI(openInNewTab);
		}
		var statusObj = new ZaStatus();
		statusObj.load();
		var statusVector = statusObj.getStatusVector();
		this._contentView.set(statusVector);
//		ZaApp.getInstance().pushView(ZaZimbraAdmin._STATUS);
		ZaApp.getInstance().pushView(this.getContentViewId());
		var now = new Date();
		this._toolbar.getButton("refreshTime").setText(ZaMsg.TBB_LastUpdated + " " + AjxDateUtil.computeTimeString(now));
		
		/*
		if (openInNewTab) {//when a ctrl shortcut is pressed
			
		}else{ //open in the main tab
			this.updateMainTab ("Status") ;
		
		}*/
	} catch (ex) {
		this._handleException(ex, "ZaStatusViewController.prototype.show", null, false);
		return;
	}	
};

ZaStatusViewController.initToolbarMethod =
function () {
	// first button in the toolbar is a menu.
	var newMenuOpList = new Array();
	this._toolbarOrder.push(ZaOperation.LABEL);
	this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.REFRESH);
	this._toolbarOperations[ZaOperation.LABEL] = new ZaOperation(ZaOperation.LABEL, ZaMsg.TBB_LastUpdated, ZaMsg.TBB_LastUpdated_tt, null, null, null,null,null,"ZaUpdatedLabel","refreshTime");	
	this._toolbarOperations[ZaOperation.SEP] = new ZaOperation(ZaOperation.SEP);
	this._toolbarOperations[ZaOperation.REFRESH] =new ZaOperation(ZaOperation.REFRESH, ZaMsg.TBB_Refresh, ZaMsg.TBB_Refresh_tt, "Refresh", "Refresh", new AjxListener(this, this.refreshListener));	
}
ZaController.initToolbarMethods["ZaStatusViewController"].push(ZaStatusViewController.initToolbarMethod);

ZaStatusViewController.prototype._createUI = function (openInNewTab) {
	try {
		var elements = new Object();
		this._contentView = new ZaServicesListView(this._container);
		this._initToolbar();
		if(this._toolbarOperations && this._toolbarOperations.length) {
			this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder, null, null, ZaId.VIEW_STATUSLIST); 
		}
		this._initPopupMenu();
		if(this._popupOperations && this._popupOperations.length) {
			this._acctionMenu =  new ZaPopupMenu(this._contentView, "ActionMenu", null, this._popupOperations, ZaId.VIEW_STATUSLIST, ZaId.MENU_POP);
		}
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		//ZaApp.getInstance().createView(ZaZimbraAdmin._STATUS, elements);
		if (!appNewUI) {
			elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;
			var tabParams = {
				openInNewTab: false,
				tabId: this.getContentViewId(),
				tab: this.getMainTab()
			}
			ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams);
		} else {
			ZaApp.getInstance().getAppViewMgr().createView(this.getContentViewId(), elements);
		}
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
