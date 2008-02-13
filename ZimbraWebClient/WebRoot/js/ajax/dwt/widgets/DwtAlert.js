/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2005, 2006, 2007 Zimbra, Inc.
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

//
// Constructor
//

/**
 * Constructs a control that alerts the user to important information.
 *
 * @param parent    The parent container for this control.
 * @param className (optional) The CSS class for this control. Default
 *					value is "DwtAlert".
 * @param posStyle  (optional) The position style of this control.
 */
DwtAlert = function(parent, className, posStyle) {
	if (arguments.length == 0) return;
	className = className || "DwtAlert";
	posStyle = posStyle || DwtControl.STATIC_STYLE;
	DwtControl.call(this, {parent:parent, className:className, posStyle:posStyle});
	this._alertStyle = DwtAlert.INFORMATION;
	this._createHtml();
}

DwtAlert.prototype = new DwtControl;
DwtAlert.prototype.constructor = DwtAlert;

//
// Constants
//

DwtAlert.INFORMATION = 0;
DwtAlert.WARNING = 1;
DwtAlert.CRITICAL = 2;

DwtAlert._ICONS = [ AjxImg.getClassForImage("Information_32"), AjxImg.getClassForImage("Warning_32"), AjxImg.getClassForImage("Critical_32") ];
DwtAlert._CLASSES = [ "DwtAlertInfo", "DwtAlertWarn", "DwtAlertCrit" ];

DwtAlert._RE_ICONS = new RegExp(DwtAlert._ICONS.join("|"));
DwtAlert._RE_CLASSES = new RegExp(DwtAlert._CLASSES.join("|"));

//
// Data
//

DwtAlert.prototype.TEMPLATE = "dwt.Widgets#DwtAlert";

//
// Public methods
//

DwtAlert.prototype.setStyle = function(style) {
	this._alertStyle = style || DwtAlert.INFORMATION;
	if (this._iconDiv) {
		Dwt.delClass(this._iconDiv, DwtAlert._RE_ICONS, DwtAlert._ICONS[this._alertStyle]);
	}
	Dwt.delClass(this.getHtmlElement(), DwtAlert._RE_CLASSES, DwtAlert._CLASSES[this._alertStyle]);
};
DwtAlert.prototype.getStyle = function() {
	return this._alertStyle;
};

DwtAlert.prototype.setIconVisible = function(visible) {
	if (this._iconDiv) {
		Dwt.setVisible(this._iconDiv, visible);
	}
};
DwtAlert.prototype.getIconVisible = function() {
	return this._iconDiv ? Dwt.getVisible(this._iconDiv) : false;
};

DwtAlert.prototype.setTitle = function(title) {
	this._alertTitle = title;
	if (this._titleDiv) {
		this._titleDiv.innerHTML = title || "";
	}
};
DwtAlert.prototype.getTitle = function() {
	return this._alertTitle;
};

DwtAlert.prototype.setContent = function(content) {
	this._alertContent = content;
	if (this._contentDiv) {
		this._contentDiv.innerHTML = content || "";
	}
};
DwtAlert.prototype.getContent = function() {
	return this._alertContent;
};

//
// DwtControl methods
//

DwtAlert.prototype.getTabGroupMember = function() {
	// NOTE: This control is not tabbable
	return null;
};

//
// Protected methods
//

DwtAlert.prototype._createHtml = function(templateId) {
	var data = { id: this._htmlElId };
	this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

DwtAlert.prototype._createHtmlFromTemplate = function(templateId, data) {
	DwtControl.prototype._createHtmlFromTemplate.apply(this, arguments);
	this._iconDiv = document.getElementById(data.id+"_icon");
	this._titleDiv = document.getElementById(data.id+"_title");
	this._contentDiv = document.getElementById(data.id+"_content");
};
