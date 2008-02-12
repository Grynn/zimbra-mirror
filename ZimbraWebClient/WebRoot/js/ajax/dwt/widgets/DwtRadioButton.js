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

/**
 * @constructor
 * @class
 * This class implements a radio button.
 * 
 * @param params	[hash]			hash of params:
 *        parent	[DwtComposite] 	parent widget
 *        style 	[constant]*		The text style. May be one of: <i>DwtCheckbox.TEXT_LEFT</i> or
 * 									<i>DwtCheckbox.TEXT_RIGHT</i> arithimatically or'd (|) with one of:
 * 									<i>DwtCheckbox.ALIGN_LEFT</i>, <i>DwtCheckbox.ALIGN_CENTER</i>, or
 * 									<i>DwtCheckbox.ALIGN_LEFT</i>.
 * 									The first determines were in the checkbox the text will appear
 * 									(if set), the second determine how the content of the text will be
 * 									aligned. The default value for this parameter is: 
 * 									<code>DwtCheckbox.TEXT_LEFT | DwtCheckbox.ALIGN_CENTER</code>.
 *        name		[string]		The input control name. Required for IE.
 *        checked	[boolean]		The input control checked status. Required for IE.
 *        className	[string]*		CSS class
 *        posStyle	[constant]*		positioning style
 *        id		[string]*		an explicit ID to use for the control's HTML element
 *        index 	[int]*			index at which to add this control among parent's children 
 */
DwtRadioButton = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtRadioButton.PARAMS);
	params.className = params.className || "DwtRadioButton";
	DwtCheckbox.call(this, params);
}

DwtRadioButton.PARAMS = ["parent", "style", "name", "checked", "className", "posStyle", "id", "index"];

DwtRadioButton.prototype = new DwtCheckbox;
DwtRadioButton.prototype.constructor = DwtRadioButton;

DwtRadioButton.prototype.toString = function() {
	return "DwtRadioButton";
};

//
// Data
//

DwtRadioButton.prototype.TEMPLATE = "dwt.Widgets#DwtRadioButton";
