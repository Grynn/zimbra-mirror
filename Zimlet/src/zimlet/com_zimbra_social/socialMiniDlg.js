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

//Author: Raja Rao DV (rrao@zimbra.com)

function com_zimbra_socialMiniDlg(zimlet) {
	this.zimlet = zimlet;
	this.miniDlgON = false;
	this.urlRegEx = /((telnet:)|((https?|ftp|gopher|news|file):\/\/)|(www.[\w\.\_\-]+))[^\s\<\>\[\]\{\}\'\"]*/gi;

}

com_zimbra_socialMiniDlg.prototype._buttonListener =
function(controller) {
	this._showSocialMiniDlg(controller);
};

com_zimbra_socialMiniDlg.prototype.getMailContents =
function(controller) {
	var message = controller.getMsg();
	var lastUrl = "";
	if (!message) {
		return;
	}
	var body = message.getBodyContent();
	if (message.isHtmlMail()) {
		this._parseBodyDiv = new DwtComposite(this.zimlet.getShell());
		this._parseBodyDiv.getHtmlElement().innerHTML = body;
		body = this._parseBodyDiv.getHtmlElement().textContent;
		body = body.replace("\n", " ").replace("\r", " ");
	}

	while (match = this.urlRegEx.exec(body)) {
		lastUrl = match[0];
		var end = this.urlRegEx.lastIndex;
		start = end;
	}
	subject = message ? message.subject : "";
	if(lastUrl == "") {
		this._processContent({longUrl : lastUrl, subject : message.subject});
	} else {
		var urlsToShorten = this.zimlet.getUrlsToShorten([lastUrl]);
		if(urlsToShorten.length == 0) {
			this._processContent({longUrl : "", subject : message.subject});
		} else {
			var callback = new AjxCallback(this, this._processContent, {longUrl : lastUrl, subject : message.subject});
			this.zimlet._postToUrlShortner({longUrl:lastUrl, callback:callback});
		}
	}
};

com_zimbra_socialMiniDlg.prototype._processContent =
function(params, response) {
	var content = "";
	var subject = params.subject;
	var longUrl = params.longUrl;
	if(longUrl == "") {
		if(subject.length >140) {
			content = subject.substring(0, 137) + "...";
		}else {
			content = subject;
		}
	}else if(response.success) {
		var text = eval("(" + response.text + ")");
		var shortUrl = text.results[longUrl].shortUrl;
		if(!shortUrl) {
			shortUrl = "";
		}
		var leftOverLen = 140 - shortUrl.length;
		if (leftOverLen < subject.length) {
			subject = subject.substring(0, (leftOverLen - 4)) + "...";
		}
		content = subject + " " + shortUrl;
	} else if(!response.success) {
		appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("couldNotShortenUrl"), ZmStatusView.LEVEL_WARNING);
	} 
	this.zimlet.updateField.value = content;
	this.zimlet.showNumberOfLetters();
};

com_zimbra_socialMiniDlg.prototype._showSocialMiniDlg = function(controller) {
	this.miniDlgON = true;
	if (this.socialMiniDialog) {
		this.zimlet.toggleFields();
		this.zimlet._updateMaxAllowedCharsToUpdate();
		this.zimlet.updateUIWidgets();
		this.zimlet._showHideMaxAlowedCharsDiv();
		this.getMailContents(controller);
		this.socialMiniDialog.popup();
		this.zimlet.setFieldFocused(this.zimlet.updateField);
		return;
	}
	this._socialMiniDlgView = new DwtComposite(this.zimlet.getShell());
	this._socialMiniDlgView.getHtmlElement().style.overflow = "auto";
	this._socialMiniDlgView.setSize("800", "100");
	this._socialMiniDlgView.getHtmlElement().innerHTML = this._createSocialMiniView();

	var _socialMiniUpdateBtnId = Dwt.getNextId();
	var socialMiniUpdateBtn = new DwtDialog_ButtonDescriptor(_socialMiniUpdateBtnId, (this.zimlet.getMessage("update")), DwtDialog.ALIGN_RIGHT);

	this.socialMiniDialog = this.zimlet._createDialog({title:this.zimlet.getMessage("whatAreYouDoing"), view:this._socialMiniDlgView,  standardButtons:[DwtDialog.CANCEL_BUTTON], extraButtons:[socialMiniUpdateBtn]});
	this.socialMiniDialog.setButtonListener(_socialMiniUpdateBtnId, new AjxListener(this.zimlet, this.zimlet._postToTweetOrFB));
	this.socialMiniDialog.setButtonListener(DwtDialog.CANCEL_BUTTON, new AjxListener(this, this._SocialMiniCancelBtnListener));
	this.updateButton_miniDlg = this.socialMiniDialog.getButton(_socialMiniUpdateBtnId);

	this.zimlet.toggleFields();
	Dwt.setHandler(this.zimlet.updateField, DwtEvent.ONKEYUP, AjxCallback.simpleClosure(this.zimlet.showNumberOfLetters, this.zimlet));
	this.zimlet.updateField.onfocus = AjxCallback.simpleClosure(this.zimlet._handleFieldFocusBlur, this.zimlet, this.zimlet.updateField, this.zimlet.getMessage("whatAreYouDoing"));
	this.zimlet.updateField.onblur = AjxCallback.simpleClosure(this.zimlet._handleFieldFocusBlur, this.zimlet, this.zimlet.updateField, this.zimlet.getMessage("whatAreYouDoing"));
	this.zimlet.updateUIWidgets();
	this.zimlet._addSocialcastGroupsMenuHndler();
	this.zimlet._addAccountCheckBoxListeners();
	this.zimlet._updateMaxAllowedCharsToUpdate();
	this.zimlet._showHideMaxAlowedCharsDiv();

	this._addUrlShortenButton_miniDlg();
	this.getMailContents(controller);
	this.socialMiniDialog.popup();
	this.zimlet.setFieldFocused(this.zimlet.updateField);
};

com_zimbra_socialMiniDlg.prototype._SocialMiniCancelBtnListener =
function() {
	this.miniDlgON = false;//set this first
	this.zimlet.toggleFields();
	this.socialMiniDialog.popdown();
};


com_zimbra_socialMiniDlg.prototype._addUrlShortenButton_miniDlg =
function() {
	var shortenButton = new DwtButton({parent:this.zimlet.getShell()});
	shortenButton.setText(this.zimlet.getMessage("shortenUrl"));
	shortenButton.addSelectionListener(new AjxListener(this.zimlet, this.zimlet._shortenUrlButtonListener));
	document.getElementById("social_shortenUrlButtonDIV_miniDlg").appendChild(shortenButton.getHtmlElement());
};

com_zimbra_socialMiniDlg.prototype._createSocialMiniView =
function() {
	var subs = {
		undo: this.zimlet.getMessage("undo"),
		autoShortenUrl: this.zimlet.getMessage("autoShortenUrl"),
		charactersLeft: this.zimlet.getMessage("charactersLeft"),
		whatAreYouDoingMsg: this.zimlet.getMessage("whatAreYouDoing"),
		updateCheckBoxesHtml: this.zimlet._addUpdateToCheckboxes()
	};
	return AjxTemplate.expand("com_zimbra_social.templates.Social#miniDlg", subs);
};

