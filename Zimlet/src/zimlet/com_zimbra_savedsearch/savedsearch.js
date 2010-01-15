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
 *@Author Raja Rao DV
 * Automatically saves a search after certain repeated search for the same query.
 */
com_zimbra_savedsearch.maxHistoryItems = 300;//max unique search items to store

function com_zimbra_savedsearch() {
}

com_zimbra_savedsearch.prototype = new ZmZimletBase();
com_zimbra_savedsearch.prototype.constructor = com_zimbra_savedsearch;

com_zimbra_savedsearch.prototype.init =
function() {
	this.savedsearchCompleteON = this.getUserProperty("turnONAutoSavedSearch") == "true";
};

com_zimbra_savedsearch.prototype.onSearchButtonClick =
function(val) {
	this._mouseOrKeySelection = false;
	this.onKeyPressSearchField(val);
};

com_zimbra_savedsearch.prototype.onKeyPressSearchField =
function(val) {
	if (!this.savedsearchCompleteON)
		return;
	if (appCtxt.getSearchController()._searchFor != ZmId.SEARCH_MAIL)//if its not mail/conv search.. dont show
		return;
	if (val == undefined || val == "")
		return;

	if (this._firsttime == undefined) {
		this.savedsearch_maximumrepeat = parseInt(this.getUserProperty("savedsearch_maximumrepeat"));
		this._firsttime = true;
	}
	var currHistory = this.getUserProperty("savedsearch_searchhistory");
	if ((":==:" + currHistory + ":==:").indexOf(":==:" + val + ":==:") == -1) {//dont add dupes
		//load to the front of the string to get latest
		this.setUserProperty("savedsearch_searchhistory", val + ":==:1::" + currHistory, true);
		this._manageHistory();//make sure to store upto 1000 unique items, remove older ones
	} else {
		var repeatCnt = parseInt(currHistory.split(val + ":==:")[1].replace("::", ""));
		repeatCnt++;
		if (repeatCnt > this.savedsearch_maximumrepeat) {
			var name = this._fixName(val);
			if (!this._nameAlreadyExists(name)) {//only create if the search by that name doesnt exist
				var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT];
				appCtxt.getAppController().setStatusMsg("This Search is used frequently and has been automatically saved by name'" + name + "'", ZmStatusView.LEVEL_INFO, null, transitions);
				ZmSearchFolder.create({name:name, parent:appCtxt.getFolderTree().root, search:appCtxt.getSearchController().currentSearch, type:"SEARCH"});
			}
			this._replaceCount(currHistory, val, -500);//reset to -500 so we dont have to deal with it anytime soon
		} else {
			this._replaceCount(currHistory, val, repeatCnt);
		}
	}
};

com_zimbra_savedsearch.prototype._nameAlreadyExists =
function(name) {
	var arry = appCtxt.getFolderTree().root.getByType("SEARCH");
	for (var i = 0; i < arry.length; i++) {
		if (arry[i].name == name)
			return true;
	}
	return false;
};

com_zimbra_savedsearch.prototype._fixName =
function(oldName) {
	return oldName.replace(/\*/g, "").replace(/\[/g, "").replace(/\]/g, "").replace(/\</g, "").replace(/\>/g, "").replace(/\=/g, "").replace(/\+/g, "").replace(/\'/g, "").replace(/\"/g, "").replace(/\\/g, "").replace(/\//g, "").replace(/\,/g, "").replace(/\./g, "").replace(/\:/g, "").replace(/\;/g, "").replace(/ /g, "").replace(/!/g, "");
};

com_zimbra_savedsearch.prototype._replaceCount =
function(currHistory, val, newCount) {
	var items = currHistory.split("::");
	for (var j = 0; j < items.length; j++) {
		if (items[j].indexOf(val + ":==:") == 0) {
			items[j] = val + ":==:" + newCount;
			break;
		}
	}
	this.setUserProperty("savedsearch_searchhistory", items.join("::"), true);
};

//if history count goes >1000, this restores it back to 800(i.e. 200 new space)
com_zimbra_savedsearch.prototype._manageHistory =
function() {
	var tmp = this.getUserProperty("savedsearch_searchhistory").split("::");
	if (tmp.length > com_zimbra_savedsearch.maxHistoryItems) {//store a max of 1000 unique items
		var newHistory = "";
		for (var i = 0; i < (com_zimbra_savedsearch.maxHistoryItems - 200); i++) {
			newHistory = newHistory + "::" + tmp[i];
		}
		this.setUserProperty("savedsearch_searchhistory", newHistory, true);

	}
};

//------------------------------------------------------------------------------------------
//			SHOW PREFERENCES DIALOG
//------------------------------------------------------------------------------------------

com_zimbra_savedsearch.prototype.doubleClicked = function() {
	this.singleClicked();
};

com_zimbra_savedsearch.prototype.singleClicked = function() {
	this.showPrefDialog();
};

com_zimbra_savedsearch.prototype.showPrefDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this.createPrefView();

	if (this.getUserProperty("turnONAutoSavedSearch") == "true") {
		document.getElementById("turnONAutoSavedSearchId").checked = true;
	}
	document.getElementById("savedsearch_maximumrepeat").value = this.getUserProperty("savedsearch_maximumrepeat");
	var clrHistoryBtn = new DwtDialog_ButtonDescriptor("sahvedsearch_clearHistoryBtnId", "Clear History", DwtDialog.ALIGN_LEFT);
	this.pbDialog = this._createDialog({title:"'Auto Saved Search' Zimlet Preferences", view:this.pView, standardButtons:[DwtDialog.OK_BUTTON],extraButtons:[clrHistoryBtn]});
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this.pbDialog.setButtonListener("sahvedsearch_clearHistoryBtnId", new AjxListener(this, this._clrHisBtnListner));
	this.pbDialog.popup();
};

com_zimbra_savedsearch.prototype._clrHisBtnListner =
function() {
	this.setUserProperty("savedsearch_searchhistory", "", true);
};

com_zimbra_savedsearch.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById("turnONAutoSavedSearchId").checked) {
		if (!this.savedsearchCompleteON) {
			this._reloadRequired = true;
		}
		this.setUserProperty("turnONAutoSavedSearch", "true");

	} else {
		this.setUserProperty("turnONAutoSavedSearch", "false");
		if (this.savedsearchCompleteON)
			this._reloadRequired = true;
	}

	if (parseInt(document.getElementById("savedsearch_maximumrepeat").value) != parseInt(this.getUserProperty("savedsearch_maximumrepeat"))) {
		this.savedsearch_maximumrepeat = document.getElementById("savedsearch_maximumrepeat").value;
		this.setUserProperty("savedsearch_maximumrepeat", this.savedsearch_maximumrepeat);
		this._reloadRequired = true;
	}

	this.pbDialog.popdown();
	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE,  ZmToast.PAUSE, ZmToast.FADE_OUT ];
		appCtxt.getAppController().setStatusMsg("Browser will be refreshed for changes to take effect..", ZmStatusView.LEVEL_INFO, null, transitions);
		this.saveUserProperties(new AjxCallback(this, this._refreshBrowser));
	}
};

com_zimbra_savedsearch.prototype.createPrefView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = "Automatically save the search when I repeat the same search for more than <input id='savedsearch_maximumrepeat' style=\"width: 20px\"  type='text'/> times.";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='turnONAutoSavedSearchId'  type='checkbox'/>Turn ON Zimlet";
	html[i++] = "</DIV>";
	return html.join("");
};

com_zimbra_savedsearch.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};