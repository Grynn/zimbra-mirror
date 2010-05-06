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
 * Constructor.
 */
function com_zimbra_skinchanger_HandlerObject() {
}

com_zimbra_skinchanger_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_skinchanger_HandlerObject.prototype.constructor = com_zimbra_skinchanger_HandlerObject;

/**
 * Simplify handler object
 *
 */
var SkinChangerZimlet = com_zimbra_skinchanger_HandlerObject;

/**
 * Defines the "enable zimlet" user property.
 */
SkinChangerZimlet.USER_PROP_ENABLE_ZIMLET = "turnONSkinChangerZimletNew";
/**
 * Defines the "frequency" user property.
 */
SkinChangerZimlet.USER_PROP_FREQUENCY = "skinc_selectedFreq";
/**
 * Defines the "changed date" user property.
 */
SkinChangerZimlet.USER_PROP_CHANGED_DATE = "skinc_skinWasChangedOnDate";

/**
 * Defines the "frequency" element id.
 */
SkinChangerZimlet.ELEMENT_ID_FREQUENCY = "skinc_skinUpdateFrequencyList";
/**
 * Defines the "enable" element id.
 */
SkinChangerZimlet.ELEMENT_ID_ENABLE = "turnONSkinChangerZimletNew_chkbx";

/**
 * Defines the index to the everyday frequency key.
 */
SkinChangerZimlet.FREQUENCY_IDX_EVERYDAY = 7;

/**
 * Initializes the zimlet.
 * 
 */
SkinChangerZimlet.prototype.init =
function() {
	this.turnONSkinChangerZimletNew = this.getUserProperty(SkinChangerZimlet.USER_PROP_ENABLE_ZIMLET) == "true";
	if(!this.turnONSkinChangerZimletNew)
		return;

	this.skinc_selectedFreq = this.getUserProperty(SkinChangerZimlet.USER_PROP_FREQUENCY);
	this.skinc_skinWasChangedOnDate = this.getUserProperty(SkinChangerZimlet.USER_PROP_CHANGED_DATE);

	// index frequency list to day keys...do NOT i18n the keys for backwards compat
	this._frequencyDaysList = [
	                	["Sunday", this.getMessage("SkinChangerZimlet_weekday_sunday")], // idx=0
	                	["Monday", this.getMessage("SkinChangerZimlet_weekday_monday")], // idx=1
	                	["Tuesday", this.getMessage("SkinChangerZimlet_weekday_tuesday")], // idx=2
	                	["Wednesday", this.getMessage("SkinChangerZimlet_weekday_wednesday")], // idx=3
	                	["Thursday", this.getMessage("SkinChangerZimlet_weekday_thursday")], // idx=4
	                	["Friday", this.getMessage("SkinChangerZimlet_weekday_friday")], // idx=5
	                	["Saturday", this.getMessage("SkinChangerZimlet_weekday_saturday")], // idx=6
	                	["Everyday", this.getMessage("SkinChangerZimlet_weekday_everyday")], // idx=7
	              ];
	
	var todayDay = this._frequencyDaysList[new Date().getDay()][0];
	// was the skin already changed today? if not, just return
	if (this.skinc_skinWasChangedOnDate == this._formatChangedOnDate())
		return;

	// if frequency is not everyday or today is not the day to update, just return
	if (this.skinc_selectedFreq != this._frequencyList[SkinChangerZimlet.FREQUENCY_IDX_EVERYDAY][0] && this.skinc_selectedFreq != todayDay)
		return;
		
	this._loadAndUpdateSkin();
};

/**
 * Called by framework on double-click.
 * 
 */
SkinChangerZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called by framework on single-click.
 * 
 */
SkinChangerZimlet.prototype.singleClicked = function() {
	this._showChangerDialog();
};

/**
 * Loads and updates the skin.
 * 
 */
SkinChangerZimlet.prototype._loadAndUpdateSkin =
function() {
	var soapDoc = AjxSoapDoc.create("GetAvailableSkinsRequest", "urn:zimbraAccount");
	
	var loadAndUpdateCallback = new AjxCallback(this, this._handleResponseLoadAndUpdateSkin);
	
	var params = {
			soapDoc		: soapDoc,
			asyncMode	: true,
			callback	: loadAndUpdateCallback
			};
	
	appCtxt.getAppController().sendRequest(params);
};

/**
 * Handles the load skins response.
 * 
 * @param	{hash}	response		the response
 */
SkinChangerZimlet.prototype._handleResponseLoadAndUpdateSkin =
function(response) {
	this._availableSkins = [];
	var resp = response.getResponse().GetAvailableSkinsResponse;
	var skins = resp.skin;
	if (skins && skins.length) {
		for (var i = 0; i < skins.length; i++) {
			this._availableSkins.push(skins[i].name);
		}
	}

	var randomnumber = Math.floor(Math.random() * this._availableSkins.length);
	var soapDoc = AjxSoapDoc.create("ModifyPrefsRequest", "urn:zimbraAccount");
	var node = soapDoc.set("pref", this._availableSkins[randomnumber]);
	node.setAttribute("name", "zimbraPrefSkin");

	var respCallback = new AjxCallback(this, this._handleSkinUpdateResponse);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
};

/**
 * Handles the skin update response.
 * 
 * @see		_handleResponseLoadAndUpdateSkin
 */
SkinChangerZimlet.prototype._handleSkinUpdateResponse =
function() {
	this.setUserProperty(SkinChangerZimlet.USER_PROP_CHANGED_DATE, this._formatChangedOnDate(), true);
};

/**
 * Shows the changer dialog.
 * 
 */
SkinChangerZimlet.prototype._showChangerDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this._createChangerView();

	if (this.getUserProperty(SkinChangerZimlet.USER_PROP_ENABLE_ZIMLET) == "true") {
		document.getElementById(SkinChangerZimlet.ELEMENT_ID_ENABLE).checked = true;
	}

	var dialog_args = {
			title	: this.getMessage("SkinChangerZimlet_dialog_title"),
			view 	: this.pView,
			parent	: this.getShell(),
			standardButtons : [DwtDialog.CANCEL_BUTTON, DwtDialog.OK_BUTTON]
		};
	
	this.pbDialog = new ZmDialog(dialog_args);
	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this._updateSelectedFrequency();
	this.pbDialog.popup();

};

/**
 * Creates the changer dialog view.
 * 
 * @see	_showChangerDialog
 */
SkinChangerZimlet.prototype._createChangerView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV>";
	html[i++] = this.getMessage("SkinChangerZimlet_selectDay");
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<select id='";
	html[i++] = SkinChangerZimlet.ELEMENT_ID_FREQUENCY;
	html[i++] = "'>";
	var len = this._frequencyDaysList.length;
	for (var j = 0; j < len; j++) {
		html[i++] = "<option value=" + this._frequencyDaysList[j][0] + ">" + this._frequencyDaysList[j][1] + "</option>";
	}
	html[i++] = "</select>";
	html[i++] = "</DIV>";
	html[i++] = "<BR>";
	if (this.skinc_skinWasChangedOnDate != "") {
		html[i++] = "<DIV>";
		var lastChangeStr = AjxMessageFormat.format(this.getMessage("SkinChangerZimlet_lastChange"), this.skinc_skinWasChangedOnDate);
		html[i++] = lastChangeStr;
		html[i++] = "</DIV>";
	}
	html[i++] = "<BR>";
	html[i++] = "<DIV>";
	html[i++] = "<input id='";
	html[i++] = SkinChangerZimlet.ELEMENT_ID_ENABLE;
	html[i++] = "' type='checkbox'/>";
	html[i++] = this.getMessage("SkinChangerZimlet_enable");
	html[i++] = "</DIV>";
	
	return html.join("");
};

/**
 * Updates the selected frequency list.
 * 
 */
SkinChangerZimlet.prototype._updateSelectedFrequency =
function() {
	var optn = document.getElementById(SkinChangerZimlet.ELEMENT_ID_FREQUENCY).options;
	for (var i = 0; i < optn.length; i++) {
		if (optn[i].value == this.skinc_selectedFreq) {
			optn[i].selected = true;
			break;
		}
	}
};

/**
 * Formats changed-on date.
 * 
 * @return	{string}	the changed-on date
 */
SkinChangerZimlet.prototype._formatChangedOnDate =
	function() {
		var dArry = (new Date()).toString().split(" ");
		return dArry[0] + " " + dArry[1] + " " + dArry[2] + " " + dArry[3];

	};

/**
 * Handles the OK button.
 * 
 * @see	_showChangerDialog
 */
SkinChangerZimlet.prototype._okBtnListner =
function() {
	this._reloadRequired = false;
	if (document.getElementById(SkinChangerZimlet.ELEMENT_ID_ENABLE).checked) {
		if (!this.turnONSkinChangerZimletNew) {
			this._reloadRequired = true;
		}
		this.setUserProperty(SkinChangerZimlet.USER_PROP_ENABLE_ZIMLET, "true", true);
	} else {
		this.setUserProperty(SkinChangerZimlet.USER_PROP_ENABLE_ZIMLET, "false", true);
		if (this.turnONSkinChangerZimletNew)
			this._reloadRequired = true;
	}
	var lst = document.getElementById(SkinChangerZimlet.ELEMENT_ID_FREQUENCY);
	if (lst.value != this.skinc_selectedFreq) {
		this.setUserProperty(SkinChangerZimlet.USER_PROP_FREQUENCY, lst.value, true);
		this._reloadRequired = true;
	}

	this.pbDialog.popdown();

	if (this._reloadRequired) {
		var transitions = [ ZmToast.FADE_IN, ZmToast.PAUSE, ZmToast.PAUSE, ZmToast.FADE_OUT ];
		var browserRefreshMsg = this.getMessage("SkinChangerZimlet_refreshBrowser");
		appCtxt.getAppController().setStatusMsg(browserRefreshMsg,
				ZmStatusView.LEVEL_INFO, null, transitions);
		setTimeout(AjxCallback.simpleClosure(this._refreshBrowser, this), 2000);
	}
};

/**
 * Refreshes the browser.
 * 
 */
SkinChangerZimlet.prototype._refreshBrowser =
function() {
	window.onbeforeunload = null;
	var url = AjxUtil.formatUrl({});
	ZmZimbraMail.sendRedirect(url);
};