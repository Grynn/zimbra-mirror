/*
 * ***** BEGIN LICENSE BLOCK *****
 *
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2006, 2007 Zimbra, Inc.
 *
 * The contents of this file are subject to the Yahoo! Public License
 * Version 1.0 ("License"); you may not use this file except in
 * compliance with the License.  You may obtain a copy of the License at
 * http://www.zimbra.com/license.
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied.
 *
 * ***** END LICENSE BLOCK *****
 *@Author Raja Rao DV
 */

function com_zimbra_hellofolders_zd() {
}
com_zimbra_hellofolders_zd.prototype = new ZmZimletBase();
com_zimbra_hellofolders_zd.prototype.constructor = com_zimbra_hellofolders_zd;

com_zimbra_hellofolders_zd.prototype._getAccountId =
function(accountType) {
	if(accountType == "defaultAccount") {//first non-local account (could be a Zimbra account as well)
		return appCtxt.accountList.defaultAccount.id;
	} else if(accountType == "zimbraAccount") {// first Zimbra account
		if(this._zimbraAccountId) {
			return this._zimbraAccountId;
		}
		var aList = appCtxt.accountList.visibleAccounts;
	   	for(var i=0; i < aList.length; i++) {
			   var acc = aList[i];
			   var accId = acc.id;
	   		if(acc.isZimbraAccount && accId != "ffffffff-ffff-ffff-ffff-ffffffffffff") {
	   			this._zimbraAccountId = accId;
	   			return accId;
	   		}
	   	}
	} else if(accountType = "activeAccount") {// currently active account
		return appCtxt.accountList.activeAccount.id;
	}
};

com_zimbra_hellofolders_zd.prototype.singleClicked =
function(accountType) {
	if(!accountType) {
		accountType = "defaultAccount";
	}
	//var parentFolderId =  "1";	
	var parentFolderId = appCtxt.multiAccounts ? this._getAccountId(accountType) + ":" + "1" : "1";	
	var folderName = 'FolderFromZimlet' + (new Date()).getTime();
	var view = "message";
	var params = {color:null, name:folderName, url:null, view:view, l: parentFolderId, postCallback:null};
	this._createFolder(params);
};


com_zimbra_hellofolders_zd.prototype.doubleClicked =
function() {
	this.singleClicked();
};

com_zimbra_hellofolders_zd.prototype.menuItemSelected = 
function(itemId) {
	var msg = "";
	switch (itemId) {
		case "hellofolderszd_defaultAccount":
			this.singleClicked("defaultAccount");
			break;
		case "hellofolderszd_activeAccount":
			this.singleClicked("activeAccount");
			break;
		case "hellofolderszd_zimbraAccount":
			this.singleClicked("zimbraAccount");
			break;
	}
};

com_zimbra_hellofolders_zd.prototype._createRSSFeed =
function() {
		var folderName = 'FolderFromZimlet' + (new Date()).getTime();
		var view = "message";
		var params = {color:null, name:folderName, url:"http://news.google.com/news?pz=1&cf=all&ned=us&hl=en&topic=h&num=3&output=rss", view:view, l:"1", postCallback:null};
		this._createFolder(params);
};


com_zimbra_hellofolders_zd.prototype._createFolder =
function(params) {
	var jsonObj = {CreateFolderRequest:{_jsns:"urn:zimbraMail"}};
	var folder = jsonObj.CreateFolderRequest.folder = {};
	//add properties in name=values
	for (var i in params) {
		if (i == "callback" || i == "errorCallback" || i == "postCallback") { 
			continue; 
		}
		var value = params[i];
		if (value) {
			folder[i] = value;
		}
	}
	var _createFldrCallback =  new AjxCallback(this, this._createFldrCallback, params);//should be called on success
	var _createFldrErrCallback = new AjxCallback(this, this._createFldrErrCallback, params); //should be called on failure
	return appCtxt.getAppController().sendRequest({jsonObj:jsonObj, asyncMode:true, errorCallback:_createFldrErrCallback, callback:_createFldrCallback});
};


com_zimbra_hellofolders_zd.prototype._createFldrCallback =
function(params, response) {
	if(params.name == com_zimbra_hellofolders_zd.hellofoldersFolder) {
		this.mainRssFeedFldrId = response.getResponse().CreateFolderResponse.folder[0].id;
		if (params.postCallback){
			params.postCallback.run(this);
		}
	} else {
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg("Folder Created", ZmStatusView.LEVEL_INFO, null, transitions);
	}
};

com_zimbra_hellofolders_zd.prototype._createFldrErrCallback =
function(params, ex) {
	if (!params.url && !params.name) {
		return false; 
	}
	
	var msg;
	if (params.name && (ex.code == ZmCsfeException.MAIL_ALREADY_EXISTS)) {
		var type = appCtxt.getFolderTree(appCtxt.getActiveAccount()).getFolderTypeByName(params.name);
		msg = AjxMessageFormat.format(ZmMsg.errorAlreadyExists, [params.name,type.toLowerCase()]);
	} else if (params.url) {
		var errorMsg = (ex.code == ZmCsfeException.SVC_RESOURCE_UNREACHABLE) ? ZmMsg.feedUnreachable : ZmMsg.feedInvalid;
		msg = AjxMessageFormat.format(errorMsg, params.url);
	}
		var transitions = [ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.FADE_OUT];
		appCtxt.getAppController().setStatusMsg("Could Not Create RSS Feed", ZmStatusView.LEVEL_WARNING, null, transitions);
	if (msg) {
		this._showErrorMsg(msg);
		return true;
	}

	return false;
};

com_zimbra_hellofolders_zd.prototype._showErrorMsg =
function(msg) {
	var msgDialog = appCtxt.getMsgDialog();
	msgDialog.reset();
	msgDialog.setMessage(msg, DwtMessageDialog.CRITICAL_STYLE);
	msgDialog.popup();
};

