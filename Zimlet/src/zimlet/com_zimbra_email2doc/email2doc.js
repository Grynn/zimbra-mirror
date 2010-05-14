/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Zimlets
 * Copyright (C) 2009, 2010 Zimbra, Inc.
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
 * Drag-n-drop an email message to save it as a document page (including all its attachments).
 * 
 * @author Raja Rao DV
 */
function com_zimbra_email2doc_HandlerObject() {
}

com_zimbra_email2doc_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_email2doc_HandlerObject.prototype.constructor = com_zimbra_email2doc_HandlerObject;

/**
 * Simplify handler object
 *
 */
var Email2DocZimlet = com_zimbra_email2doc_HandlerObject;

/**
 * Handles an object drop action.
 * 
 * @param	{object}	zmObject		the dropped object
 */
Email2DocZimlet.prototype.doDrop =
function(zmObject) {

	if (!zmObject.srcObj)
		return;

	var type = zmObject.TYPE;
	var msg = null;
	
	switch (type) {
		case "ZmConv": {
			var conv = zmObject.srcObj; // {ZmConv}
			msg = conv.getFirstHotMsg();
			break;
		}
		case "ZmMailMsg": {
			msg = zmObject.srcObj; // {ZmMailMsg}
			break;
		}
	}
	
	if (msg != null)
		this._postLoadCB(msg);
};

/**
 * Called by framework on double-click.
 */
Email2DocZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called by framework on single-click.
 */
Email2DocZimlet.prototype.singleClicked = function() {
	var dlg = appCtxt.getErrorDialog();
	dlg.setMessage(this.getMessage("Email2DocZimlet_dialog_info"));
	dlg.setButtonVisible(ZmErrorDialog.REPORT_BUTTON, false);
	dlg.popup();
};

/**
 * Presents the document dialog for saving the message.
 * 
 * @param	{ZmMailMsg}	msg		the message
 */
Email2DocZimlet.prototype._postLoadCB =
function(msg) {

	AjxDispatcher.run("GetNotebookController");
	if (!this._fldrDlg) {
		this._fldrDlg = appCtxt.getChooseFolderDialog();
	}

	var params = {
		title:			this.getMessage("Email2DocZimlet_dialog_title_document"),
		description:	this.getMessage("Email2DocZimlet_dialog_selectdocument"),
		treeIds:		[ZmOrganizer.NOTEBOOK],
		overviewId:		this._fldrDlg.getOverviewId(ZmApp.NOTEBOOK),
		appName:		ZmApp.NOTEBOOK
	};
	this._selectDocCb = new AjxCallback(this, this._selectDocCallBack, msg);

	ZmController.showDialog(this._fldrDlg, this._selectDocCb, params);
};

/**
 * Handles the document dialog callback.
 * 
 * @param	{ZmMailMsg}	msg		the message
 * @param	{ZmNotebook}	fldr		the folder
 * 
 * @see			_postLoadCB
 */
Email2DocZimlet.prototype._selectDocCallBack =
function(msg, fldr) {

	this._attachmentLnksStr = "";
	this._failedToAttachNames = "";
	this._fldrDlg.popdown();
	this._attachments = new Array();
	if (msg.hasAttach) {
		this._attachmentCntr = 0;

		var tmpAtt = msg.attachments;
		for (var i = 0; i < tmpAtt.length; i++) {
			if (tmpAtt[i].filename != undefined) {
				this._attachments.push(tmpAtt[i]);
			}
		}
		this._createFromAttachment(msg, this._attachments[this._attachmentCntr], fldr);

	} else {
		this._createPageAndShow(msg, fldr);
	}
};

/**
 * Creates the attachment.
 * 
 * @param	{ZmMailMsg}	msg		the message
 * @param	{hash}		attachment		the attachment object
 * @param	{ZmNotebook}	fldr		the folder
 */
Email2DocZimlet.prototype._createFromAttachment =
function(msg, attachment, fldr) {

	this._attachmentCntr++;
	var origParams = {msg:msg, attachment:attachment, fldr:fldr};
	var msgId = msg.id;
	var partId = attachment.part;
	var name = attachment.filename;
	var folderId = fldr.id;
	var acctId = appCtxt.getActiveAccount().id;
	if (msgId.indexOf(acctId) == 0) {
		msgId = msgId.substr(msgId.indexOf(":") + 1);
	}
	var soapDoc = AjxSoapDoc.create("SaveDocumentRequest", "urn:zimbraMail");
	var doc = soapDoc.set("doc");
	doc.setAttribute("l", folderId);
	var mnode = soapDoc.set("m", null, doc);
	mnode.setAttribute("id", msgId);
	mnode.setAttribute("part", partId);

	var params = {
		soapDoc: soapDoc,
		asyncMode: true,
		callback: (new AjxCallback(this, this._handleResponseCreateItem, origParams)),
		errorCallback: (new AjxCallback(this, this._handleErrorCreateItem, origParams))
	};
	appCtxt.getAppController().sendRequest(params);
};

/**
 * Handles the create item.
 * 
 * @param	{hash}	origParams		a hash of original params
 * @param	{ZmMailMsg}	origParams.msg		the message
 * @param	{hash}		origParams.attachment		the attachment object
 * @param	{ZmNotebook}	origParams.fldr		the folder
 * 
 * @see			_createFromAttachment
 */
Email2DocZimlet.prototype._handleResponseCreateItem =
function(origParams, response) {
	var rest = response.getResponse().SaveDocumentResponse.doc[0].rest;
	var html = new Array();
	var i = 0;
	html[i++] = "<br><a href=\"";
	html[i++] = rest;
	html[i++] = "\">";
	html[i++] = origParams.attachment.filename;
	html[i++] = "</a>";
	this._attachmentLnksStr = this._attachmentLnksStr + html.join("");
	if (this._attachmentCntr < this._attachments.length) {
		this._createFromAttachment(origParams.msg, this._attachments[this._attachmentCntr], origParams.fldr);
		return;
	}
	this._createPageAndShow(origParams.msg, origParams.fldr);
};

/**
 * Adds the failed attachment name.
 * 
 * @param	{string}	name		the attachment name
 */
Email2DocZimlet.prototype._addFailedToAttachNames =
function(name) {
	if (this._failedToAttachNames == "")
		this._failedToAttachNames = this._failedToAttachNames + name;
	else
		this._failedToAttachNames = this._failedToAttachNames + ", " + name;

};

/**
 * Handles the create item error.
 * 
 * @param	{hash}	origParams		a hash of original params
 * @param	{ZmMailMsg}	origParams.msg		the message
 * @param	{hash}		origParams.attachment		the attachment object
 * @param	{ZmNotebook}	origParams.fldr		the folder
 * 
 * @see			_createFromAttachment
 */
Email2DocZimlet.prototype._handleErrorCreateItem =
function(origParams, response) {

	this._addFailedToAttachNames(origParams.attachment.filename);
	if (this._attachmentCntr < this._attachments.length) {
		this._createFromAttachment(origParams.msg, this._attachments[this._attachmentCntr], origParams.fldr);
		return;
	}
	this._createPageAndShow(origParams.msg, origParams.fldr);
	var dlg = appCtxt.getErrorDialog();
	
	var errorMsg = AjxMessageFormat.format(this.getMessage("Email2DocZimlet_attachmentfail"), this._failedToAttachNames);

	dlg.setMessage(errorMsg);
	dlg.setButtonVisible(ZmErrorDialog.REPORT_BUTTON, false);
	dlg.popup();
	
	return true;
};

/**
 * Creates and shows the document page.
 * 
 * @param	{ZmMailMsg}	zmObject		the message
 * @param	{ZmNotebook}	fldr		the folder
 */
Email2DocZimlet.prototype._createPageAndShow =
function(zmObject, fldr) {
	var page = new ZmPage();
	page.name = zmObject.subject;
	page.folderId = fldr.id;
	var body = zmObject.getBodyContent().replace(/\r\n|\n/g, "<br>");//notebook page is always html
	if (this._attachmentLnksStr != "") {
		var html = [];
		var idx = 0;

		html[idx++] = "<br>--------------------<br>";
		html[idx++] = this.getMessage("Email2DocZimlet_attachmenttitle");
		html[idx++] = "<br>--------------------";
		html[idx++] = this._attachmentLnksStr;
			
		body = body + html.join("");
	}
	page.setContent(body);
	AjxDispatcher.run("GetPageEditController").show(page);
};

