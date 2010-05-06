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
 * 
 */
function com_zimbra_skinpreviewer_HandlerObject() {
}

com_zimbra_skinpreviewer_HandlerObject.prototype = new ZmZimletBase();
com_zimbra_skinpreviewer_HandlerObject.prototype.constructor = com_zimbra_skinpreviewer_HandlerObject;

/**
 * Simplify handler object
 *
 */
var SkinPreviewerZimlet = com_zimbra_skinpreviewer_HandlerObject;

/**
 * Defines the "skin menu" element id.
 */
SkinPreviewerZimlet.ELEMENT_ID_SKIN_MENU = "skinprev_skinmenu";

/**
 * Initializes the zimlet.
 * 
 */
SkinPreviewerZimlet.prototype.init = function() {
	// do nothing
};

/**
 * Called by framework on single-click.
 * 
 */
SkinPreviewerZimlet.prototype.doubleClicked = function() {
	this.singleClicked();
};

/**
 * Called by framework on double-click.
 * 
 */
SkinPreviewerZimlet.prototype.singleClicked = function() {
	this._loadAvailableSkins();
};

/**
 * Loads the list of available skins.
 * 
 */
SkinPreviewerZimlet.prototype._loadAvailableSkins =
function() {
	var soapDoc = AjxSoapDoc.create("GetAvailableSkinsRequest", "urn:zimbraAccount");
	var respCallback = new AjxCallback(this, this._handleResponseLoadAvailableSkins);
	appCtxt.getAppController().sendRequest({soapDoc:soapDoc, asyncMode:true, callback:respCallback});
};

/**
 * Handles the load available skins response.
 * 
 * @param	{hash}	response		the response
 * 
 * @see		_loadAvailableSkins
 */
SkinPreviewerZimlet.prototype._handleResponseLoadAvailableSkins =
function(response) {
	/**
	 * An array of available skin names.
	 * @type	array
	 */
	this._availableSkins = [];
	var resp = response.getResponse().GetAvailableSkinsResponse;
	var skins = resp.skin;
	if (skins && skins.length) {
		for (var i = 0; i < skins.length; i++) {
			this._availableSkins.push(skins[i].name);
		}
	}
	this._showSkinSelectorDialog();
};

/**
 * Shows the skin selector dialog.
 * 
 */
SkinPreviewerZimlet.prototype._showSkinSelectorDialog =
function() {
	//if zimlet dialog already exists...
	if (this.pbDialog) {
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.getShell());
	this.pView.getHtmlElement().innerHTML = this._createSkinSelectorView();

	var previewButtonId = Dwt.getNextId();
	var previewButtonLabel = this.getMessage("SkinPreviewerZimlet_button_preview");
	
	var previewButton = new DwtDialog_ButtonDescriptor(previewButtonId, previewButtonLabel, DwtDialog.ALIGN_RIGHT);
	
	var dialogArgs = {
			title	:	this.getMessage("SkinPreviewerZimlet_title"),
			view	:	this.pView,
			parent	:	this.getShell(),
			standardButtons : [ DwtDialog.CANCEL_BUTTON],
			extraButtons : [previewButton]
		};
	
	this.pbDialog = new ZmDialog(dialogArgs);
	this.pbDialog.setButtonListener(previewButtonId, new AjxListener(this, this._previewSelectedSkins));
	this.pbDialog.popup();

};

/**
 * Creates the skin selector dialog view.
 * 
 * @see		_showSkinSelectorDialog
 */
SkinPreviewerZimlet.prototype._createSkinSelectorView =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<DIV align='center' class='skinpreviewer_skinMenuDiv'>";
	html[i++] = this.getMessage("SkinPreviewerZimlet_previewSkins");
	html[i++] = "</DIV>";
	html[i++] = "<DIV>";
	html[i++] = this.getMessage("SkinPreviewerZimlet_selectSkins");
	html[i++] = "<br><br></DIV>";
	html[i++] = "<DIV>";
	html[i++] = "<select class='skinpreviewer_skinMenuSelect' id=\"";
	html[i++] = SkinPreviewerZimlet.ELEMENT_ID_SKIN_MENU;
	html[i++] = "\" multiple='' size=10>";
	var len = this._availableSkins.length;
	for (var j = 0; j < len; j++) {
		var itm = this._availableSkins[j];
		html[i++] = "<option value=" + itm + ">" + itm + "</option>";
	}
	html[i++] = "</select>";
	html[i++] = "</DIV>";
	html[i++] = "<br>";
	
	return html.join("");
};

/**
 * Handles the preview selected skins button.
 * 
 * @see		_showSkinSelectorDialog
 */
SkinPreviewerZimlet.prototype._previewSelectedSkins =
function() {
	var url = AjxUtil.formatUrl({});
	var ch = "";
	if (url.indexOf("?") > 0) {
		ch = "&skin=";
	} else {
		ch = "?skin=";
	}

	var skinNamesArry = this._getSelectedSkins();
	if (skinNamesArry.length == 0) {
		var msgParams = {
				msg		: this.getMessage("SkinPreviewerZimlet_warningNoSkins"),
				level	: ZmStatusView.LEVEL_WARNING
		};
		
		appCtxt.setStatusMsg(msgParams);
		return;
	}

	var len = skinNamesArry.length;
	for (var i = 0; i < len; i++) {
		var newurl = url + ch + skinNamesArry[i];
		setTimeout(AjxCallback.simpleClosure(this._openAWindow, this, newurl), i * 5000);
	}

};

/**
 * Opens window.
 * 
 * @param	{string}	url		the window url
 */
SkinPreviewerZimlet.prototype._openAWindow =
function(url) {
	window.open(url);
};

/**
 * Gets the selected skins.
 * 
 * @return	{array}	an array of skin names or an empty array if none selected
 */
SkinPreviewerZimlet.prototype._getSelectedSkins =
function() {
	var me = document.getElementById(SkinPreviewerZimlet.ELEMENT_ID_SKIN_MENU);
	var selectedSkins = [];
	for (var i = 0; i < me.options.length; i++) {
		if (me.options[i].selected) {
			selectedSkins.push(me.options[i].value);
		}
	}
	return selectedSkins;
};
