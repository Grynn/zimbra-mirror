/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2007, 2008, 2009, 2010 VMware, Inc.
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
 * Creates a border less button.
 * @constructor
 * @class
 * This class represents a button without a border.
 *
 * @param {hash}	params		a hash of parameters
 * @param {DwtComposite}      params.parent		the parent widget
 * @param {constant}      params.style			the button style (see {@link DwtButton})
 * @param {string}      params.className		the CSS class
 * @param {constant}      params.posStyle		the positioning style (see {@link Dwt})
 * @param {DwtButton.ACTION_MOUSEUP|DwtButton.ACTION_MOUSEDOWN}      params.actionTiming	if {@link DwtButton.ACTION_MOUSEUP}, then the button is triggered
 *											on mouseup events, else if {@link DwtButton.ACTION_MOUSEDOWN},
 * 											then the button is triggered on mousedown events
 * @param {string}      params.id			the ID to use for the control's HTML element
 * @param {number}      params.index 		the index at which to add this control among parent's children
 * 
 * @extends		DwtButton
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

