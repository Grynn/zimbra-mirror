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
 * This class represents a button without a border
 *
 * @param params		[hash]				hash of params:
 *        parent		[DwtComposite] 		parent widget
 *        style			[constant]*			button style
 *        className		[string]*			CSS class
 *        posStyle		[constant]*			positioning style
 *        actionTiming	[constant]*			if DwtButton.ACTION_MOUSEUP, then the button is triggered
 *											on mouseup events, else if DwtButton.ACTION_MOUSEDOWN,
 * 											then the button is triggered on mousedown events
 *        id			[string]*			ID to use for the control's HTML element
 *        index 		[int]*				index at which to add this control among parent's children
 */
DwtBorderlessButton = function(params) {
	if (arguments.length == 0) { return; }
	params = Dwt.getParams(arguments, DwtBorderlessButton.PARAMS);

	DwtButton.call(this, params);
}

DwtBorderlessButton.PARAMS = ["parent", "style", "className", "posStyle", "actionTiming", "id", "index"];

DwtBorderlessButton.prototype = new DwtButton;
DwtBorderlessButton.prototype.constructor = DwtBorderlessButton;

DwtBorderlessButton.prototype.toString =
function() {
	return "DwtBorderlessButton";
}

//
// Data
//

DwtBorderlessButton.prototype.TEMPLATE = "dwt.Widgets#ZBorderlessButton"

