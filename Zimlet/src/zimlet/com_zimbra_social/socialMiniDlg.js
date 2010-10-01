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
		var callback = new AjxCallback(this, this._processContent, {longUrl : lastUrl, subject : message.subject});
		this.zimlet._postToUrlShortner({longUrl:lastUrl, callback:callback});
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
		this.getMailContents(controller);
		this.socialMiniDialog.popup();
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

	Dwt.setHandler(document.getElementById("social_statusTextArea_miniDlg"), DwtEvent.ONKEYUP, AjxCallback.simpleClosure(this.zimlet.showNumberOfLetters, this.zimlet));
	Dwt.setHandler(document.getElementById("social_statusTextArea_miniDlg"), DwtEvent.ONKEYPRESS, AjxCallback.simpleClosure(this.zimlet._postToTweetOrFB, this.zimlet));
	this._addAccountCheckBoxListeners_miniDlg();
	this.zimlet.toggleFields();
	this.zimlet.updateUIWidgets();

	this._addUrlShortenButton_miniDlg();
	this.getMailContents(controller);
	this.socialMiniDialog.popup();
};

com_zimbra_socialMiniDlg.prototype._SocialMiniCancelBtnListener =
function() {
	this.miniDlgON = false;//set this first
	this.zimlet.toggleFields();
	this.socialMiniDialog.popdown();
};

com_zimbra_socialMiniDlg.prototype._addAccountCheckBoxListeners_miniDlg =
function() {
	for (var accntId in this.zimlet.allAccounts) {
		var callback = AjxCallback.simpleClosure(this.zimlet._saveToAccountCheckboxesPref, this.zimlet, accntId);
		Dwt.setHandler(document.getElementById(this.zimlet.allAccounts[accntId].checkboxId_miniDlg), DwtEvent.ONCLICK, callback);
	}
};

com_zimbra_socialMiniDlg.prototype._addUrlShortenButton_miniDlg =
function() {
	var shortenButton = new DwtButton({parent:this.zimlet.getShell()});
	shortenButton.setText(this.getMessage("shortenUrl"));
	shortenButton.addSelectionListener(new AjxListener(this.zimlet, this.zimlet._shortenUrlButtonListener));
	document.getElementById("social_shortenUrlButtonDIV_miniDlg").appendChild(shortenButton.getHtmlElement());
};

com_zimbra_socialMiniDlg.prototype._createSocialMiniView =
function() {
	var html = [];
	var idx = 0;
	html[idx++] = "<DIV>";
	html[idx++] = this._addUpdateToCheckboxes_miniDlg();
	html[idx++] = "<TABLE width=100%>";
	html[idx++] = "<TR><TD style=\"width:90%;\" >";
	html[idx++] = "<input  style=\"width:100%;height:25px\" autocomplete=\"off\" id=\"social_statusTextArea_miniDlg\" ></input>";
	html[idx++] = "</TD>";

	html[idx++] = "<TD rowspan=2 align=center valign='middle'>";
	html[idx++] = "<table width=100%><tr><td align=center>";
	html[idx++] = "<label style=\"font-size:18px;color:green;font-weight:bold\" id='social_numberOfLettersAllowed_miniDlg'>140</label>";
	html[idx++] = "</td></tr><tr><td align=center>";
	html[idx++] = "<label>"+this.zimlet.getMessage("charactersLeft")+"</label></td></tr></table>";
	html[idx++] = "</TD>";
	html[idx++] = "</TR>";

	html[idx++] = "<TR><TD>";
	html[idx++] = "<table width=100%><tr>";
	html[idx++] = "<td align=left> <div id='social_shortenUrlButtonDIV_miniDlg' /></td>";
	html[idx++] = "<td align=left><input type='checkbox'  id='social_autoShortenCheckbox_miniDlg'></input></td><td  nowrap=''><label style='color:#252525'>"+this.zimlet.getMessage("autoShortenUrl")+"</label></td>";
	html[idx++] = "<td align=left width=90%><div id='social_undoShortenURLDIV_miniDlg' style='display:none'><a  href='#' id='social_undoShortenURLLink_miniDlg' style='text-decoration:underline;font-weight:bold'>"+this.zimlet.getMessage("undo")+"</a></div></td>";
	html[idx++] = "</tr></table>";
	html[idx++] = "</TD></TR>";
	html[idx++] = "</TABLE>";
	html[idx++] = "</DIV>";
	return html.join("");
};

com_zimbra_socialMiniDlg.prototype._addUpdateToCheckboxes_miniDlg =
function() {
	var html = [];
	var idx = 0;
	var hasAccounts = false;
	html[idx++] = "<TABLE>";
	html[idx++] = "<TR><td>";
	html[idx++] = "<label style=\"font-size:12px;color:black;font-weight:bold\">update to: ";
	html[idx++] = "</label>";
	html[idx++] = "</TD>";
	for (var id in this.zimlet.allAccounts) {
		hasAccounts = true;
		var turnOnStr = "";
		if (this.zimlet.allAccounts[id].__on == "true") {
			turnOnStr = "checked";
		}
		var chkbxId = this.zimlet.allAccounts[id].checkboxId_miniDlg = "social_updateToCheckbox_miniDlg" + id;
		html[idx++] = "<TD valign='middle' align=center>";
		html[idx++] = "<input type='checkbox'  " + turnOnStr + "  id='" + chkbxId + "'>";
		html[idx++] = "</TD><TD valign='middle' align=center>";
		html[idx++] = this.zimlet.allAccounts[id].name;
		html[idx++] = " &nbsp;&nbsp;&nbsp;&nbsp;";
	}

	html[idx++] = "</TR></TABLE>";

	if (hasAccounts)
		return html.join("");
	else {
		return "<label style=\"font-size:12px;color:#555555;font-style:italic\">"+this.zimlet.getMessage("goToSocialTabAndAddAccounts")+"</label>";
	}
};
