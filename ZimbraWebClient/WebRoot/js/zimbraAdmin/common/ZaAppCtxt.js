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
* @constructor ZaAppCtxt
* @class ZaAppCtxt
*/
ZaAppCtxt = function() {
}

ZaAppCtxt.LABEL = "appCtxt";

ZaAppCtxt.prototype.toString = 
function() {
	return "ZaAppCtxt";
}

/**
* Gets the app context from the given shell.
*
* @param shell		the shell
* @return			the app context
*/
ZaAppCtxt.getFromShell =
function(shell) {
	return shell.getData(ZaAppCtxt.LABEL);
}



ZaAppCtxt.prototype.setAppController =
function(appController) {
	this._appController = appController;
}

ZaAppCtxt.prototype.getAppController =
function() {
	return this._appController;
}


ZaAppCtxt.prototype.getApp =
function() {
	return this._appController.getApp();
}

ZaAppCtxt.prototype.getAppViewMgr =
function() {
	return this._appController.getAppViewMgr();
}

ZaAppCtxt.prototype.setClientCmdHdlr =
function(clientCmdHdlr) {
	this._clientCmdHdlr = clientCmdHdlr;
}

ZaAppCtxt.prototype.getClientCmdHdlr =
function() {
	return this._clientCmdHdlr;
}

ZaAppCtxt.prototype.getSearchController =
function() {
	return this._appController.getSearchController();
}

ZaAppCtxt.prototype.getLoginDialog =
function() {
	if (!this._loginDialog)
		this._loginDialog = new ZaLoginDialog(this.getShell(), null, null, this);
	return this._loginDialog;
}

ZaAppCtxt.prototype.getMsgDialog =
function(refresh) {
	if (!this._msgDialog || refresh)
		this._msgDialog = new ZaMsgDialog(this.getShell());
	return this._msgDialog;
}

ZaAppCtxt.prototype.getConfirmMsgDialog = function (refresh) {
	if(!this._confirmMsgDialog || refresh) {
		this._confirmMsgDialog = new ZaMsgDialog(this.getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON, DwtDialog.CANCEL_BUTTON], null, ZaId.CTR_GLOBAL + "_confirm3btn");
	}
	return this._confirmMsgDialog;
}
 
ZaAppCtxt.prototype.getConfirmMsgDialog2 = function (refresh) {
	if(!this._confirmMessageDialog2 || refresh) {
		this._confirmMessageDialog2 = new ZaMsgDialog(this.getShell(), null, [DwtDialog.YES_BUTTON, DwtDialog.NO_BUTTON], null, ZaId.CTR_GLOBAL + "_confirm2btn");
	}
	return this._confirmMessageDialog2;
}


ZaAppCtxt.prototype.getErrorDialog = 
function(refresh) {
	if (!this._errorDialog || refresh)
		this._errorDialog = new ZaErrorDialog(this.getShell());
	return this._errorDialog;
}

ZaAppCtxt.prototype.getShell =
function() {
	return this._shell;
}

ZaAppCtxt.prototype.setShell =
function(shell) {
	this._shell = shell;
	shell.setData(ZaAppCtxt.LABEL, this);
}


ZaAppCtxt.prototype.getFolderTree =
function() {
	return this._folderTree;
}

ZaAppCtxt.prototype.setFolderTree =
function(folderTree) {
	this._folderTree = folderTree;
}

ZaAppCtxt.prototype.getUsername = 
function() { 
	return this._username;
}

ZaAppCtxt.prototype.setUsername = 
function(username) {
	this._username = username;
}

ZaAppCtxt.prototype.getCurrentSearch =
function() { 
	return this._currentSearch;
}

ZaAppCtxt.prototype.setCurrentSearch =
function(search) {
	this._currentSearch = search;
}

ZaAppCtxt.prototype.getSettings =
function() {
	if (!this._settings)
		this._settings = new ZaSettings(this);
	return this._settings;
}

// NOTE: this is only to be used by any child windows!
ZaAppCtxt.prototype.setSettings = 
function(settings) {
	this._settings = settings;
}

ZaAppCtxt.prototype.getRootTabGroup =
function() {
	if (!this._rootTabGrp)
		this._rootTabGrp = new DwtTabGroup("ROOT");
	return this._rootTabGrp;
}

ZaAppCtxt.getLogoURI =
function () {
    if (skin && skin.hints && skin.hints.banner) {
        return skin.hints.banner.url ;
    } else {
        return ZaSettings.LOGO_URI ;
    }
}
