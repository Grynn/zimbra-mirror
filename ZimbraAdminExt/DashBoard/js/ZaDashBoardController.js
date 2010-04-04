/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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
* @class ZaDashBoardController 
* @contructor ZaDashBoardController
* @param appCtxt
* @param container
* @author Greg Solovyev
**/
ZaDashBoardController = function(appCtxt, container) {
	ZaXFormViewController.call(this, appCtxt, container, "ZaDashBoardController");
	this._UICreated = false;
	this.objType = ZaEvent.S_ACCOUNT;
	this._helpURL = ZaDashBoardController.helpURL;
	this.tabConstructor = ZaDashBoardView;
 	this._toolbarOperations = new Array();
}

ZaDashBoardController.prototype = new ZaXFormViewController();
ZaDashBoardController.prototype.constructor = ZaDashBoardController;
ZaController.initToolbarMethods["ZaDashBoardController"] = new Array();
ZaDashBoardController.helpURL = location.pathname + ZaUtil.HELP_URL + "managing_accounts/provisioning_accounts.htm?locid="+AjxEnv.DEFAULT_LOCALE;

ZaDashBoardController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.HELP] = new ZaOperation(ZaOperation.HELP, ZaMsg.TBB_Help, ZaMsg.TBB_Help_tt, "Help", "Help", new AjxListener(this, this._helpButtonListener));
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.HELP);
	this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.NONE);
}
ZaController.initToolbarMethods["ZaDashBoardController"].push(ZaDashBoardController.initToolbarMethod);

ZaDashBoardController.prototype.show = 
function(openInNewTab) {
    if (!this._contentView) {
    	this._initToolbar();
    	this._toolbar = new ZaToolBar(this._container, this._toolbarOperations,this._toolbarOrder);		
    	var elements = new Object();
		this._contentView = new this.tabConstructor(this._container);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
    	elements[ZaAppViewMgr.C_TOOLBAR_TOP] = this._toolbar;		
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
			
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	}
    var entry = {attrs:{}};
    var gc = ZaApp.getInstance().getGlobalConfig();
    var statusObj = new ZaStatus();
    statusObj.load();
    if(statusObj.serverMap) {
    	for(var a in statusObj.serverMap) {
    		entry.serviceMap = statusObj.serverMap[a].serviceMap;
    		break;
    	}
    }

    entry.attrs = gc.attrs;
    entry.rights = gc.rights;
    entry.setAttrs = gc.setAttrs;
    entry.getAttrs = gc.getAttrs;
	ZaApp.getInstance().pushView(this.getContentViewId());
	this._contentView.setObject(entry); 	//setObject is delayed to be called after pushView in order to avoid jumping of the view	
	this._currentObject = entry;
};
