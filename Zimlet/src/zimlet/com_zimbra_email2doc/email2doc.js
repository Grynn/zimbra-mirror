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
 */

//////////////////////////////////////////////////////////////////////////////
// Drag an email to save it as a document page(including all its attachments)
// @author Zimlet author: Raja Rao DV
//////////////////////////////////////////////////////////////////////////////

function com_zimbra_email2doc() {
}

com_zimbra_email2doc.prototype = new ZmZimletBase();
com_zimbra_email2doc.prototype.constructor = com_zimbra_email2doc;


com_zimbra_email2doc.prototype.doDrop =
function(zmObject) {

	if (!zmObject.srcObj)
		return;

	zmObject = zmObject.srcObj;
	if (zmObject.type == "CONV") {
		var msg = zmObject.getFirstHotMsg();
		this._postLoadCB(msg);
	} else if (zmObject.type == "MSG") {
		this._postLoadCB(zmObject);
	}
};

com_zimbra_email2doc.prototype._postLoadCB =
function(msg, response) {

	AjxDispatcher.run("GetNotebookController");
	if (!this._fldrDlg) {
		this._fldrDlg = appCtxt.getChooseFolderDialog();
	}

	var params = {description:"Select a Document to save message(s)",treeIds:[ZmOrganizer.NOTEBOOK], overviewId:"ZmListController", title:"Document Overview"};
	this._selectDocCb = new AjxCallback(this, this._selectDocCallBack, msg);

	ZmController.showDialog(this._fldrDlg, this._selectDocCb, params);
};


com_zimbra_email2doc.prototype._selectDocCallBack =
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

com_zimbra_email2doc.prototype._createFromAttachment =
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

com_zimbra_email2doc.prototype._handleResponseCreateItem =
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

com_zimbra_email2doc.prototype._addFailedToAttachNames =
function(name) {
	if (this._failedToAttachNames == "")
		this._failedToAttachNames = this._failedToAttachNames + name;
	else
		this._failedToAttachNames = this._failedToAttachNames + ", " + name;

};

com_zimbra_email2doc.prototype._handleErrorCreateItem =
function(origParams, response) {

	this._addFailedToAttachNames(origParams.attachment.filename);
	if (this._attachmentCntr < this._attachments.length) {
		this._createFromAttachment(origParams.msg, this._attachments[this._attachmentCntr], origParams.fldr);
		return;
	}
	this._createPageAndShow(origParams.msg, origParams.fldr);
	var dlg = appCtxt.getErrorDialog();
	dlg.setMessage("The following attachment(s) could not be attached since attachment with same name already exists(may be attached to a different page)<br><br>" + this._failedToAttachNames);
	dlg.setButtonVisible(ZmErrorDialog.REPORT_BUTTON, false);
	dlg.popup();
	return true;
};

com_zimbra_email2doc.prototype._createPageAndShow =
function(zmObject, fldr) {
	var page = new ZmPage();
	page.name = zmObject.subject;
	page.folderId = fldr.id;
	var body = zmObject.getBodyContent().replace(/\r\n|\n/g, "<br>");//notebook page is always html
	if (this._attachmentLnksStr != "") {
		body = body + "<br>--------------------<br>Attachments:<br>--------------------" + this._attachmentLnksStr;
	}
	page.setContent(body);
	AjxDispatcher.run("GetPageEditController").show(page);
};