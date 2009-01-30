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

com_zimbra_dirtywordalert.prototype.initializeRegEx =
function() {
	if (this._dWordsRegEx)
		return;

	this._dWordsList = ["shit","piss","fuck","cunt","cocksucker","motherfucker","tits","fart","turd","twat"];
	this._dWordsRegEx = [];
	for (var n = 0; n < this._dWordsList.length; n++) {
		this._dWordsRegEx.push(new RegExp("^" + this._dWordsList[n], "i"));
	}
};

com_zimbra_dirtywordalert.prototype.emailErrorCheck =
function(mail, boolAndErrorMsgArray) {
	if (!this.turnONdirtywordalertZimlet)
		return;

	this.initializeRegEx();
	this._ignoreWords = [];
	if (mail.isReplied || mail.isForwarded) {
		this._createIgnoreList(mail._origMsg);
	}
	var dWordsThatExists = "";
	for (var k = 0; k < this._dWordsRegEx.length; k++) {
		var dWord = this._dWordsRegEx[k];
		var newMailContent = mail.textBodyContent;
		var newMailArry = dWord.exec(newMailContent);
		if (!newMailArry)
			continue;

		var newMailLen = newMailArry.length;
		//if the number of dwords in the new mail is same as origMail, skip it
		if (this._ignoreWords[dWord] != undefined) {
			if (newMailLen <= this._ignoreWords[dWord]) {
				hasDWordStr = false;
				continue;
			}
		}
		hasDWordStr = true;
		if (hasDWordStr) {
			if (dWordsThatExists == "") {
				dWordsThatExists = dWord.source.replace(/\^/g, "");
			} else {
				dWordsThatExists = dWordsThatExists + ", " + dWord.source.replace(/\^/g, "");
			}
		}
	}

	if (dWordsThatExists == "")
		return null;

	//there is  some dirtyword in new mail (but not necessarily in old-mail)
	return boolAndErrorMsgArray.push({hasError:true, errorMsg:"You are perhaps upset or unknowingly using <b>'" + dWordsThatExists + "'</b> <a href=\"http://en.wikipedia.org/wiki/Seven_dirty_words\" target=\"_blank\"> dirty word(s)</a> in the mail. You are better off not using these words.<BR> Still, send anyway?", zimletName:"com_zimbra_dirtywordalert"});
};


com_zimbra_dirtywordalert.prototype._createIgnoreList =
function(origMail) {
	var bodyContent = origMail.getBodyContent();
	for (var k = 0; k < this._dWordsRegEx.length; k++) {
		var dWord = this._dWordsRegEx[k];
		var mailArry = dWord.exec(bodyContent);
		if (!mailArry)
			continue;

		this._ignoreWords[dWord] = mailArry.length;
	}
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