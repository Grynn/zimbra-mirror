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
	this.emailZimlet_tooltipSize = this.zimlet.getUserProperty("emailZimlet_tooltipSize");
	if(!this.emailZimlet_tooltipSize) {
		this.emailZimlet_tooltipSize = EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM];
	}
	this.updateEmailTooltipSize();
}

EmailToolTipPrefDialog.SIZE_VERYSMALL = "verysmall";
EmailToolTipPrefDialog.SIZE_SMALL = "small";
EmailToolTipPrefDialog.SIZE_MEDIUM = "medium";
EmailToolTipPrefDialog.SIZE_LARGE = "large";
EmailToolTipPrefDialog.SIZE_XL = "xl";
EmailToolTipPrefDialog.SIZE_2XL = "2xl";


EmailToolTipPrefDialog.DIMENSIONS = [];
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_VERYSMALL] = "270px x 320px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_SMALL]  = "300px x 350px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_MEDIUM]  = "320px x 360px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_LARGE]  = "330px x 370px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_XL]  = "340px x 380px";
EmailToolTipPrefDialog.DIMENSIONS[EmailToolTipPrefDialog.SIZE_2XL]  = "350px x 390px";




/**
 * Updates Email Tooltip's tooltipWidth and tooltipHeight
 */
EmailToolTipPrefDialog.prototype.updateEmailTooltipSize =
function() {
	var size = this.emailZimlet_tooltipSize.replace(/px/ig, "");
	var arry = size.split(" x ");
	Com_Zimbra_Email.tooltipWidth = arry[0];
	Com_Zimbra_Email.tooltipHeight = arry[1];
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
	document.getElementById("emailZimlet_tooltipSize").value = this.emailZimlet_tooltipSize;
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
	debugger;
	html[i++] = "<select id='emailZimlet_tooltipSize'>";
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
	var emailZimlet_tooltipSize = document.getElementById("emailZimlet_tooltipSize").value;
	if (emailZimlet_tooltipSize != this.emailZimlet_tooltipSize) {
		this.zimlet.setUserProperty("emailZimlet_tooltipSize", emailZimlet_tooltipSize);
		this.emailZimlet_tooltipSize = emailZimlet_tooltipSize;
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
