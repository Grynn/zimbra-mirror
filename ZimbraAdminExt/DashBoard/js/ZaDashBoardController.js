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

	ZaController.call(this, appCtxt, container, "ZaDashBoardController");
	this.tabConstructor = ZaDashBoardView;
}

ZaDashBoardController.prototype = new ZaController();
ZaDashBoardController.prototype.constructor = ZaDashBoardController;
ZaController.initToolbarMethods["ZaDashBoardController"] = new Array();

ZaDashBoardController.initToolbarMethod =
function () {
	this._toolbarOperations[ZaOperation.NONE] = new ZaOperation(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.NONE);
	this._toolbarOrder.push(ZaOperation.SEP);
	this._toolbarOrder.push(ZaOperation.NONE);
}
ZaController.initToolbarMethods["ZaDashBoardController"].push(ZaDashBoardController.initToolbarMethod);

ZaDashBoardController.prototype.show = 
function(openInNewTab) {
    if (!this._contentView) {
		var elements = new Object();
		this._contentView = new this.tabConstructor(this._container);
		elements[ZaAppViewMgr.C_APP_CONTENT] = this._contentView;
		var tabParams = {
			openInNewTab: false,
			tabId: this.getContentViewId(),
			tab: this.getMainTab() 
		}
		ZaApp.getInstance().createView(this.getContentViewId(), elements, tabParams) ;
		this._UICreated = true;
		ZaApp.getInstance()._controllers[this.getContentViewId ()] = this ;
	}
	ZaApp.getInstance().pushView(this.getContentViewId());
};
