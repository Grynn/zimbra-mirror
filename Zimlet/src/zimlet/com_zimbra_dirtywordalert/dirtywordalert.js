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
// Zimlet that checks for dirty words and alerts
// @author Zimlet author: Raja Rao DV(rrao@zimbra.com)
//////////////////////////////////////////////////////////////////////////////

function com_zimbra_dirtywordalert() {
}

com_zimbra_dirtywordalert.prototype = new ZmZimletBase();
com_zimbra_dirtywordalert.prototype.constructor = com_zimbra_dirtywordalert;

com_zimbra_dirtywordalert.prototype.init =
function() {
	this.turnONdirtywordalertZimlet = this.getUserProperty("turnONdirtywordalertZimlet") == "true";
};

com_zimbra_dirtywordalert.prototype.emailErrorCheck =
function(mail, boolAndErrorMsgArray) {

	if (!this.turnONdirtywordalertZimlet)
		return;

	var newMailContent = mail.textBodyContent;
	var dWords = ["shit", "piss", "fuck", "cunt", "cocksucker", "motherfucker", "tits", "fart", "turd", "twat", "son of a bitch", "sob", "asshole", "bastard", "nigro", "nigga", "white trash"];
	var dWordsThatExists = "";
	for (var k = 0; k < dWords.length; k++) {
		var dWord = dWords[k];
		var newMailArry = this._getBeforeAfterArray(newMailContent, dWord);
		var newMailLen = newMailArry.length;
		if (newMailLen == 0)
			continue;

		var origMailArry = [];
		var origMailLen = 0;
		if (mail.isReplied || mail.isForwarded) {
			origMailArry = this._getBeforeAfterArray(mail._origMsg.getBodyContent(), dWord);
			origMailLen = origMailArry.length;
			var hasDWordStr = "";
			for (var i = 0; i < newMailLen; i++) {
				hasDWordStr = true;
				var newMailStr = newMailArry[i];
				for (var j = 0; j < origMailLen; j++) {
					var origMailStr = origMailArry[j];
					if (origMailStr == newMailStr) {
						hasDWordStr = false;
						break;
					}
				}
			}
			if (hasDWordStr) {
				if (dWordsThatExists == "") {
					dWordsThatExists = dWord;
				} else {
					dWordsThatExists = dWordsThatExists + ", " + dWord;
				}
			}
		}

	}

	if (dWordsThatExists == "")
		return null;

	//there is  some dirtyword in new mail (but not necessarily in old-mail)
	return boolAndErrorMsgArray.push({hasError:true, errorMsg:"You are perhaps upset or unknowingly using <b>'" + dWordsThatExists + "'</b> dirty word(s) in the mail. You are better off not using these words.<BR> Still, send anyway?", zimletName:"com_zimbra_dirtywordalert"});
};

com_zimbra_dirtywordalert.prototype._getBeforeAfterArray =
function(content, wordToMatch) {
	var arry = content.toLowerCase().split(wordToMatch.toLowerCase());
	var len = arry.length;
	var beforeAfterTxtArry = new Array();
	if (len == 1)
		return beforeAfterTxtArry;

	for (var i = 0; i < len / 2; i = i + 2) {
		var beforeTxt = "";
		var afterTxt = "";
		var txt1 = arry[i];
		var txt2 = arry[i + 1];
		//get before and after text of 'attach'
		if (txt1.length >= 5)
			beforeTxt = txt1.substring(txt1.length - 6, txt1.length - 1);
		if (txt2.length >= 5)
			afterTxt = txt2.substring(0, 4);

		if (txt2.indexOf("?") == -1)//ignore questions(use txt2 for complete string)
			beforeAfterTxtArry.push(beforeTxt + afterTxt);//store 5-chars before and 5-char after 'attach'
	}

	return beforeAfterTxtArry;
};

com_zimbra_dirtywordalert.prototype.doubleClicked = function() {
	this.singleClicked();
};

com_zimbra_dirtywordalert.prototype.singleClicked = function() {
	this.showPrefDialog();
};

com_zimbra_dirtywordalert.prototype.showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this.createPrefView();

	if (this.getUserProperty("turnONdirtywordalertZimlet") == "true") {
		document.getElementById("turnONdirtywordalertZimlet_chkbx").checked = true;
	}

	this.pbDialog = this._createDialog({title:"'Dirty words in compose Alert' Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.popup();
};

com_zimbra_dirtywordalert.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "<input id='turnONdirtywordalertZimlet_chkbx'  type='checkbox'/>Enable 'Dirty words in compose Alert' Zimlet (Changing this would refresh browser)";
	html[i++] = "</DIV>";
	return html.join("");

};

com_zimbra_dirtywordalert.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById("turnONdirtywordalertZimlet_chkbx").checked) {
		if (!this.turnONdirtywordalertZimlet) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnONdirtywordalertZimlet", "true", true);
	} else {
		this.setUserProperty("turnONdirtywordalertZimlet", "false", true);
		if (this.turnONdirtywordalertZimlet)
			this._reloadRequired = true;
	}
	this.pbDialog.popdown();

	if (this._reloadRequired) {
		window.onbeforeunload = null;
		var url = AjxUtil.formatUrl({});
		ZmZimbraMail.sendRedirect(url);
	}
};