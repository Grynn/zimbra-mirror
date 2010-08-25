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
function com_zimbra_dropio_HandlerObject() {
};

com_zimbra_dropio_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_dropio_HandlerObject.prototype.constructor = com_zimbra_dropio_HandlerObject;

/**
 * Simplify handler object
 *
 */
var DropioZimlet = com_zimbra_dropio_HandlerObject;


var DropioZimletTabKey = "dropio";

/**
 * Called by framework when compose toolbar is being initialized
 */
DropioZimlet.prototype.initializeToolbar =
function(app, toolbar, controller, viewId) {
	if (viewId.indexOf("COMPOSE") >= 0 && !this._addedToMainWindow) {
		var btn = toolbar.getOp("ATTACHMENT");
		btn.addSelectionListener(new AjxListener(this, this._addTab));	
	}
};

/**
 * Adds Drop.io tab
 */
DropioZimlet.prototype._addTab =
function() {
	if(this._addedToMainWindow) {
		return;
	}
	this.metaData = appCtxt.getActiveAccount().metaData;
	this._allDropioFileMetaData = null;
	var attachDialog = this._attachDialog = appCtxt.getAttachDialog();
	var tabview = attachDialog ? attachDialog.getTabView() : null;
	this.dropioView = new DropioTabView(tabview, this);
	var tabLabel = this.getMessage("DropioZimlet_label");
	var tabkey = attachDialog.addTab(DropioZimletTabKey, tabLabel, this.dropioView);
	this.dropioView.attachDialog = attachDialog;

	var callback = new AjxCallback(this.dropioView, this.dropioView.uploadFiles);
	attachDialog.addOkListener(tabkey, callback);
	this._addedToMainWindow = true;
};

/**
 * Zimlet framework calls this when the overview panel icon is single clicked
 */
DropioZimlet.prototype.singleClicked =
function() {
	if (!this.prefDlg) {
		this.prefDlg = new DropioZimletPrefDialog(this);
	}
	this.prefDlg.popup();
};

/**
 * Zimlet framework calls this when the overview panel icon is double clicked
 */
DropioZimlet.prototype.doubleClicked =
function() {
	this.singleClicked();
};

/**
 * Saves File metadata into database
 * @param {hash} dropJsonObj A hash containing drop information
 * @param  {hash} fileJsonObj A hash containing file information
 */
DropioZimlet.prototype._addFileMetaData =
function(dropJsonObj, fileJsonObj) {
	if (!this._allDropioFileMetaData) {
		this._allDropioFileMetaData = {}
	}
	if (!fileJsonObj.original_filename) {
		var expnMsg = "";
		if (fileJsonObj.response && fileJsonObj.response.message) {
			expnMsg = "(" + fileJsonObj.response.message + ")";
		}
		this._showErrorMessage(this.getMessage("DropioZimlet_tryAgain")+ expnMsg + "");
		return;
	}
	this._allDropioFileMetaData[dropJsonObj.name + "_" + fileJsonObj.original_filename] = this._getFileMetadataInJson(dropJsonObj, fileJsonObj);
};

/**
 * Gets metadata in JSON format to be svaed in DB
 * @param {hash} dropJsonObj A hash containing drop information
 * @param  {hash} fileJsonObj A hash containing file information
 * @return {string} JSON string
 */
DropioZimlet.prototype._getFileMetadataInJson =
function(dropJsonObj, fileJsonObj) {
	var createdAtTime = this._getTimeInMilliSecs(fileJsonObj.created_at);
	var html = new Array();
	var i = 0;
	html[i++] = "{";
	html[i++] = "dn:";
	html[i++] = "\"" + dropJsonObj.name + "\"";
	html[i++] = ",gPwd:";
	html[i++] = "\"" + dropJsonObj.gPwd + "\"";
	html[i++] = ",aPwd:";
	html[i++] = "\"" + dropJsonObj.aPwd + "\"";
	html[i++] = ",fn:";
	html[i++] = "\"" + fileJsonObj.original_filename + "\"";
	html[i++] = ",fs:";
	html[i++] = "\"" + fileJsonObj.filesize + "\"";
	html[i++] = ",ct:";
	html[i++] = "\"" + createdAtTime + "\"";
	html[i++] = ",hUrl:";
	html[i++] = "\"" + fileJsonObj.hidden_url + "\"";
	html[i++] = "}";
	return html.join("");
};

/**
 * Gets time in millisecond
 * @param timeStr Date & time string
 */
DropioZimlet.prototype._getTimeInMilliSecs =
function(timeStr) {
	var date = new Date();
	if (!timeStr) {
		return date.getTime();
	}
	var arry1 = timeStr.split(" ");
	if (arry1.length != 3) {
		return date.getTime();
	}
	var dateArry = arry1[0].split("/");
	date.setFullYear(dateArry[0]);
	date.setMonth(dateArry[1] - 1);
	date.setDate(dateArry[2]);

	var timeArry = arry1[1].split(":");
	date.setHours(timeArry[0]);
	date.setMinutes(timeArry[1]);
	date.setSeconds(timeArry[2]);

	return date.getTime();
};

/**
 * Gets Mark As Read card preferences meta data.
 * @param {AjxCallback} postCallback  a callback
 */
DropioZimlet.prototype._getDropioFileMetaData =
function(postCallback) {
	this.metaData.get("DropioZimlet_DropioFile_metadata", null, new AjxCallback(this, this._handleGetDropioFileMetaData, postCallback));
};

/**
 * Handles  Mark As Read card preferences metadata callback.
 * @param {AjxCallback} postCallback  a callback
 * @param {object} result	 the response
 */
DropioZimlet.prototype._handleGetDropioFileMetaData =
function(postCallback, result) {
	this._allDropioFileMetaData = null; //nullify old data
	try {
		this._allDropioFileMetaData = result.getResponse().BatchResponse.GetMailboxMetadataResponse[0].meta[0]._attrs;
		if (postCallback != "") {
			postCallback.run(this._allDropioFileMetaData);
		} else {
			return this._allDropioFileMetaData;
		}
	} catch(ex) {
		this._showErrorMessage(ex);
	}
};

/**
 *Saves  Mark as all read metadata
 */
DropioZimlet.prototype._saveDropioFileMetaData =
function() {
	var cleanMetaData = [];
	for (var id in this._allDropioFileMetaData) {
		if (id.indexOf("undefined") >= 0) {
			continue;
		}
		cleanMetaData[id] = this._allDropioFileMetaData[id];
	}
	this.metaData.set("DropioZimlet_DropioFile_metadata", cleanMetaData, null, new AjxCallback(this.dropioView, this.dropioView._createdropioList));
};

/**
 * Gets a random password
 */
DropioZimlet.GetPassword = function() {
	var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
	var string_length = 8;
	var randomstring = '';
	for (var i = 0; i < string_length; i++) {
		var rnum = Math.floor(Math.random() * chars.length);
		randomstring += chars.substring(rnum, rnum + 1);
	}
	return randomstring;
}

/**
 * Converts bytes into readable format
 * @param bytes number of bytes
 */
DropioZimlet.ConvertBytes = function(bytes) {
	if (bytes == "undefined" || bytes == undefined) {
		return;
	}
	var ext = new Array('B', 'KB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB');
	var unitCount = 0;
	for (; bytes > 1024; unitCount++) bytes /= 1024;
	return DropioZimlet.roundNumber(bytes, 2) + " " + ext[unitCount];
};

/**
 * Returns a round number
 * @param rnum  Number to be rounded
 * @param rlength  To what decimal place
 */
DropioZimlet.roundNumber = function(rnum, rlength) {
	var newnumber = Math.round(rnum * Math.pow(10, rlength)) / Math.pow(10, rlength);
	return newnumber;
};

/**
 * Displays error message.
 * @param {string} expnMsg Exception message string
 */
DropioZimlet.prototype._showErrorMessage =
function(expnMsg, detailString) {
	var msg = "";
	if (expnMsg instanceof AjxException) {
		msg = expnMsg.msg;
	} else {
		msg = expnMsg;
	}
	if (!detailString) {
		var dlg = appCtxt.getMsgDialog();
	} else {
		var dlg = appCtxt.getErrorDialog();
	}
	dlg.reset();
	dlg.setMessage(msg, DwtMessageDialog.WARNING_STYLE);
	if (detailString) {
		dlg.setDetailString(detailString);
	}
	dlg.popup();
};

/**
 * Sorts Drop objects based on creation time
 * @param a Drop object A
 * @param b Drop object B
 */
function DropioZimlet_sortDropObjs(a, b) {
	var objA = eval("(" + a + ")");
	var objB = eval("(" + b + ")");
	var x = parseInt(objA.ct);
	var y = parseInt(objB.ct);
	return ((x > y) ? -1 : ((x < y) ? 1 : 0));
}