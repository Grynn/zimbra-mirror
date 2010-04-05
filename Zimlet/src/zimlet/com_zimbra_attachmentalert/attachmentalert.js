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
 * @author Raja Rao DV
 */

/**
 * Constructor. Checks for attach* word in email and also if there is an attachment.
 * If the email does not have an attachment, throws missing-attachment alert dialog
 * 
 */
function com_zimbra_attachalert_HandlerObject() {
}

com_zimbra_attachalert_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_attachalert_HandlerObject.prototype.constructor = com_zimbra_attachalert_HandlerObject;

/**
 * Defines the "zimlet name".
 */
com_zimbra_attachalert_HandlerObject.ZIMLET_NAME = "com_zimbra_attachalert_HandlerObject";
/**
 * Defines the "alert on" user property.
 */
com_zimbra_attachalert_HandlerObject.USER_PROP_ALERT_ON = "turnONAttachmentAlertZimletNew";

/**
 * Defines the "alert on checkbox" element.
 */
com_zimbra_attachalert_HandlerObject.ELEMENT_ID_CHECKBOX_ALERT_ON = "turnONAttachmentAlertZimletNew_chkbx";


/**
 * Initializes the zimlet.
 * 
 */
com_zimbra_attachalert_HandlerObject.prototype.init =
function() {
	this.turnONAttachmentAlertZimletNew = this.getUserProperty(com_zimbra_attachalert_HandlerObject.USER_PROP_ALERT_ON) == "true";
};

/**
 * Initializes the regular expression.
 * 
 */
com_zimbra_attachalert_HandlerObject.prototype._initializeRegEx =
function() {
	if (this._attachWordsRegEx)
		return;
	this._attachStr = this.getMessage("AttachmentAlert_attach");
	this._errorMsgStr = this.getMessage("AttachmentAlert_error_noattachment");
	this._attachWordsList = [this._attachStr];
	this._attachWordsRegEx = [];
	for (var n = 0; n < this._attachWordsList.length; n++) {
		this._attachWordsRegEx.push(new RegExp("\\b" + this._attachWordsList[n], "ig"));
	}
};

/**
 * This method is called when sending an email.
 * 
 */
com_zimbra_attachalert_HandlerObject.prototype.emailErrorCheck =
function(mail, boolAndErrorMsgArray) {
	if (!this.turnONAttachmentAlertZimletNew)
		return;

	// check if we have attachments...
	if(mail._filteredFwdAttIds){
		if(mail._filteredFwdAttIds.length > 0)
			return null; // has attachments, do not bother
	}
	if(mail._forAttIds){
		if(mail._forAttIds.length > 0)
			return null; // has attachments, do not bother
	}

	this._initializeRegEx();
	this._ignoreWords = [];
	if (mail.isReplied || mail.isForwarded) {
		this._createIgnoreList(mail._origMsg);
	}
	var attachWordsThatExists = "";
	var newMailContent = mail.textBodyContent;

	var hasattachWordStr = false;
	for (var k = 0; k < this._attachWordsRegEx.length; k++) {
		var attachWord = this._attachWordsRegEx[k];
		var newMailArry = newMailContent.match(attachWord);
		if (!newMailArry)
			continue;

		var newMailLen = newMailArry.length;
		//if the number of attachWords in the new mail is same as origMail, skip it
		if (this._ignoreWords[attachWord] != undefined) {
			if (newMailLen <= this._ignoreWords[attachWord]) {
				hasattachWordStr = false;
				continue;
			}
		}
		hasattachWordStr = true;
		break;
		
	}

	if (!hasattachWordStr)
		return null;

	//there is a word "attach*" in new mail but not in old-mail
	var errParams = {
			hasError:true,
			errorMsg: this._errorMsgStr,
			zimletName:com_zimbra_attachalert_HandlerObject.ZIMLET_NAME
	};
	
	return boolAndErrorMsgArray.push(errParams);
};

/**
 * Creates an ignore list.
 * 
 */
com_zimbra_attachalert_HandlerObject.prototype._createIgnoreList =
function(origMail) {
	var bodyContent = origMail.getBodyContent();
	for (var k = 0; k < this._attachWordsRegEx.length; k++) {
		var attachWord = this._attachWordsRegEx[k];
		var mailArry = bodyContent.match(attachWord);
		if (!mailArry)
			continue;

		this._ignoreWords[attachWord] = mailArry.length;
	}
};

/**
 * Called when the zimlet is double-clicked.
 */
com_zimbra_attachalert_HandlerObject.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called when the zimlet is single-clicked.
 */
com_zimbra_attachalert_HandlerObject.prototype.singleClicked = function() {
	this._showPrefDialog();
};

/**
 * Shows the preferences dialog.
 * 
 */
com_zimbra_attachalert_HandlerObject.prototype._showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this._createPrefView();

	if (this.getUserProperty(com_zimbra_attachalert_HandlerObject.USER_PROP_ALERT_ON) == "true") {
		document.getElementById(com_zimbra_attachalert_HandlerObject.ELEMENT_ID_CHECKBOX_ALERT_ON).checked = true;
	}

	var dialog_args = {
			title	: this.getMessage("AttachmentAlert_dialog_preferences_title"),
			view	: this.pView,
			standardButtons	: [DwtDialog.OK_BUTTON],
			parent	: this.getShell()
		};

	this.pbDialog = new ZmDialog(dialog_args);
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.popup();
};

/**
 * Creates the preferences view.
 * 
 */
com_zimbra_attachalert_HandlerObject.prototype._createPrefView =
function() {
    var html = new Array();
    var i = 0;
    html[i++] = "<div>";
    html[i++] = "<input id='";
    html[i++] = com_zimbra_attachalert_HandlerObject.ELEMENT_ID_CHECKBOX_ALERT_ON;
    html[i++] = "'  type='checkbox'/>";
    html[i++] = this.getMessage("AttachmentAlert_dialog_preferences_alert_text");
    html[i++] = "</div>";
    return html.join("");
};

/**
 * Listens for the OK button event.
 * 
 * @see		_showPrefDialog
 */
com_zimbra_attachalert_HandlerObject.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById(com_zimbra_attachalert_HandlerObject.ELEMENT_ID_CHECKBOX_ALERT_ON).checked) {
		if (!this.turnONAttachmentAlertZimletNew) {
			this._reloadRequired = true;
		}
		this.setUserProperty(com_zimbra_attachalert_HandlerObject.USER_PROP_ALERT_ON, "true", true);
	} else {
		this.setUserProperty(com_zimbra_attachalert_HandlerObject.USER_PROP_ALERT_ON, "false", true);
		if (this.turnONAttachmentAlertZimletNew)
			this._reloadRequired = true;
	}
	
	this.pbDialog.popdown();

	if (this._reloadRequired) {
		window.onbeforeunload = null;
		var url = AjxUtil.formatUrl({});
		ZmZimbraMail.sendRedirect(url);
	}
};