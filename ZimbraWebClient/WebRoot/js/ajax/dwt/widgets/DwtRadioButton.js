/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007 Zimbra, Inc.
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
DwtRadioButton = function(parent, style, name, checked, className, posStyle, id, index) {
	if (arguments.length == 0) return;
	className = className ? className : "DwtRadioButton";
	DwtCheckbox.call(this, parent, style, name, checked, className, posStyle, id, index);
}

DwtRadioButton.prototype = new DwtCheckbox;
DwtRadioButton.prototype.constructor = DwtRadioButton;

DwtRadioButton.prototype.toString = function() {
	return "DwtRadioButton";
};

//
// Data
//

DwtRadioButton.prototype.TEMPLATE = "dwt.Widgets#DwtRadioButton";
