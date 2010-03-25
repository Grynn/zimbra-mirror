/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
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

//
// Constructor
//

/**
 * Constructs a control that alerts the user to important information.
 * @class
 * This class represents an alert.
 * 
 * @param {DwtComposite}	parent    the parent container for this control
 * @param {string}	[className="DwtAlert"] the CSS class for this control
 * @param {Dwt.STATIC_STYLE|Dwt.ABSOLUTE_STYLE|Dwt.RELATIVE_STYLE|Dwt.FIXED_STYLE}	[posStyle] 	the position style of this control
 * 
 * @extends		DwtControl
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
/**
 * Defines the "information" style.
 */
DwtAlert.INFORMATION = 0;
/**
 * Defines the "warning" style.
 */
DwtAlert.WARNING = 1;
/**
 * Defines the "critical" style.
 */
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

/**
 * Sets the style.
 * 
 * @param	{DwtAlert.INFORMATION|DwtAlert.WARNING|DwtAlert.CRITICAL}	style		the style
 */
DwtAlert.prototype.setStyle = function(style) {
	this._alertStyle = style || DwtAlert.INFORMATION;
	if (this._iconDiv) {
		Dwt.delClass(this._iconDiv, DwtAlert._RE_ICONS, DwtAlert._ICONS[this._alertStyle]);
	}
	Dwt.delClass(this.getHtmlElement(), DwtAlert._RE_CLASSES, DwtAlert._CLASSES[this._alertStyle]);
};

/**
 * Gets the style.
 * 
 * @return	{DwtAlert.INFORMATION|DwtAlert.WARNING|DwtAlert.CRITICAL}		the style
 */
DwtAlert.prototype.getStyle = function() {
	return this._alertStyle;
};

/**
 * Sets the icon visibility.
 * 
 * @param	{boolean}	visible		if <code>true</code>, the icon is visible
 */
DwtAlert.prototype.setIconVisible = function(visible) {
	if (this._iconDiv) {
		Dwt.setVisible(this._iconDiv, visible);
	}
};

/**
 * Gets the icon visibility.
 * 
 * @return	{boolean}	<code>true</code> if the icon is visible
 */
DwtAlert.prototype.getIconVisible = function() {
	return this._iconDiv ? Dwt.getVisible(this._iconDiv) : false;
};

/**
 * Sets the title.
 * 
 * @param	{string}	title	the title
 */
DwtAlert.prototype.setTitle = function(title) {
	this._alertTitle = title;
	if (this._titleDiv) {
		this._titleDiv.innerHTML = title || "";
	}
};

/**
 * Gets the title.
 * 
 * @return	{string}	the title
 */
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

DwtAlert.prototype.setDismissContent = function (dwtElement) {
    if (this._dismissDiv) {
        dwtElement.reparentHtmlElement(this._dismissDiv.id) ;
    }
}

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

/**
 * @private
 */
DwtAlert.prototype._createHtml = function(templateId) {
	var data = { id: this._htmlElId };
	this._createHtmlFromTemplate(templateId || this.TEMPLATE, data);
};

/**
 * @private
 */
DwtAlert.prototype._createHtmlFromTemplate = function(templateId, data) {
	DwtControl.prototype._createHtmlFromTemplate.apply(this, arguments);
	this._iconDiv = document.getElementById(data.id+"_icon");
	this._titleDiv = document.getElementById(data.id+"_title");
	this._contentDiv = document.getElementById(data.id+"_content");
    this._dismissDiv = document.getElementById(data.id+"_dismiss") ;
};
