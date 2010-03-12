/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 Zimbra, Inc.
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
 * Creates a radio button.
 * @constructor
 * @class
 * This class implements a radio button.
 * 
 * @param {Hash}	params	a hash of parameters
 * @param  {DwtComposite}     parent	theparent widget
 * @param  {constant}     style 	the text style. May be one of: {@link DwtCheckbox.TEXT_LEFT} or
 * 									{@link DwtCheckbox.TEXT_RIGHT} arithimatically or'd (|) with one of:
 * 									{@link DwtCheckbox.ALIGN_LEFT}, {@link DwtCheckbox.ALIGN_CENTER}, or
 * 									{@link DwtCheckbox.ALIGN_LEFT}.
 * 									The first determines were in the checkbox the text will appear
 * 									(if set), the second determine how the content of the text will be
 * 									aligned. The default value for this parameter is: 
 * 									{@link DwtCheckbox.TEXT_LEFT} | {@link DwtCheckbox.ALIGN_CENTER}
 * @param  {String}     name		the input control name (required for IE)
 * @param  {String}     value     the input control value.
 * @param  {Boolean}     checked	the input control checked status (required for IE)
 * @param  {String}     className	the CSS class
 * @param  {constant}     posStyle	the positioning style (see {@link DwtControl})
 * @param  {String}     id		an explicit ID to use for the control's HTML element
 * @param  {int}     index 	the index at which to add this control among parent's children
 * 
 * @extends	DwtCheckbox
 */
DwtRadioButton = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtRadioButton.PARAMS);
	params.className = params.className || "DwtRadioButton";
	DwtCheckbox.call(this, params);
}

DwtRadioButton.PARAMS = DwtCheckbox.PARAMS;

DwtRadioButton.prototype = new DwtCheckbox;
DwtRadioButton.prototype.constructor = DwtRadioButton;

DwtRadioButton.prototype.toString = function() {
	return "DwtRadioButton";
};

//
// Data
//

DwtRadioButton.prototype.TEMPLATE = "dwt.Widgets#DwtRadioButton";
