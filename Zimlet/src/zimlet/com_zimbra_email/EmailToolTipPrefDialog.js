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
*/


/**
 * Object that deals with Preferences and Preferences dialog
 * @param zimlet  Email Zimlet
 */
function EmailToolTipPrefDialog(zimlet) {
	this.zimlet = zimlet;
	this.emailZimlet_tooltipArea = this.zimlet.getUserProperty("emailZimlet_tooltipArea");
	if(!this.emailZimlet_tooltipArea) {
		this.emailZimlet_tooltipArea = EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM];
	}
	this.updateEmailTooltipSize();
}

EmailToolTipPrefDialog.SIZE_VERYSMALL = "VERYSMALL";
EmailToolTipPrefDialog.SIZE_SMALL = "SMALL";
EmailToolTipPrefDialog.SIZE_MEDIUM = "MEDIUM";
EmailToolTipPrefDialog.SIZE_LARGE = "LARGE";
EmailToolTipPrefDialog.SIZE_XL = "XL";

EmailToolTipPrefDialog.DIMENSIONS = [];
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_VERYSMALL]  = "220px x 130px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_SMALL]  = "230px x 140px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM]  = "250px x 140px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_LARGE]  = "260px x 200px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_XL]  = "270px x 210px";

/**
 * Updates Email Tooltip's tooltipWidth and tooltipHeight
 */
EmailToolTipPrefDialog.prototype.updateEmailTooltipSize =
function() {
	var size = this.emailZimlet_tooltipArea.replace(/px/ig, "");
	var arry = size.split(" x ");
	EmailTooltipZimlet.tooltipWidth = arry[0];
	EmailTooltipZimlet.tooltipHeight = arry[1];
};

/**
 * Creates and displays the dialog
 */
EmailToolTipPrefDialog.prototype.popup =
function() {
	if (this.pbDialog) {
		this._setPreferences();
		this.pbDialog.popup();
		return;
	}
	this.pView = new DwtComposite(this.zimlet.getShell());
	this.pView.getHtmlElement().innerHTML = this._createPreferenceView();
	this.pbDialog = new DwtDialog({parent:this.zimlet.getShell(),title:this.zimlet.getMessage("emailZimletPreferences"), 
									view:this.pView, standardButtons:[DwtDialog.OK_BUTTON, DwtDialog.CANCEL_BUTTON]});

	this.pbDialog.setButtonListener(DwtDialog.OK_BUTTON, new AjxListener(this, this._okBtnListner));
	this._setPreferences();
	this.pbDialog.popup();
};

/**
 * Hides the dialog
 */
EmailToolTipPrefDialog.prototype.popdown =
function() {
	if (this.pbDialog) {
		this.pbDialog.popup();
	}
};

/**
 * Sets user preferences to dialog DOM objects
 */
EmailToolTipPrefDialog.prototype._setPreferences =
function() {
	document.getElementById("emailZimlet_tooltipArea").value = this.emailZimlet_tooltipArea;
};

/**
 * Creates Preferences view
 */
EmailToolTipPrefDialog.prototype._createPreferenceView =
function() {
	var html = new Array();
	html.push("<div>", this.zimlet.getMessage("selectToolTipSize"), " ",this._getTooltipMenuHtml(), "</div>");
	return html.join("");
};

/**
 * Returns tooltip size menu's html
 */
EmailToolTipPrefDialog.prototype._getTooltipMenuHtml =
function() {
	var html = new Array();
	var i = 0;
	html[i++] = "<select id='emailZimlet_tooltipArea'>";
	for (var el in EmailToolTipPrefDialog.DIMENSIONS) {
		var dimension = EmailToolTipPrefDialog.DIMENSIONS[el];
		var name = this.zimlet.getMessage(el);
		html[i++] = "<option value='" + dimension + "'>" + name + " (" + dimension + ")</option>";
	}
	html[i++] = "</select>";
	return html.join("");
};

/**
 * Listens to OK button and saves user properties
 */
EmailToolTipPrefDialog.prototype._okBtnListner =
function() {
	var emailZimlet_tooltipArea = document.getElementById("emailZimlet_tooltipArea").value;
	if (emailZimlet_tooltipArea != this.emailZimlet_tooltipArea) {
		this.zimlet.setUserProperty("emailZimlet_tooltipArea", emailZimlet_tooltipArea);
		this.emailZimlet_tooltipArea = emailZimlet_tooltipArea;
	}
	this.updateEmailTooltipSize();
	var callback = new AjxCallback(this, this._propertiesSaved);
	this.zimlet.saveUserProperties(callback);
	this.pbDialog.popdown();
};

/**
 *  Displays Preferences saved message
 */
EmailToolTipPrefDialog.prototype._propertiesSaved =
function() {
	appCtxt.getAppController().setStatusMsg(this.zimlet.getMessage("preferencesSaved"), ZmStatusView.LEVEL_INFO);
};
