/*
 * Copyright (C) 2006, The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


function DvListViewActionMenu(parent, className, dialog) {

	if (arguments.length == 0) return;
	className = className || "ActionMenu";
	DwtMenu.call(this, parent, DwtMenu.POPUP_STYLE, className, null, dialog);

	this._menuItems = new Object();
}

DvListViewActionMenu.prototype = new DwtMenu;
DvListViewActionMenu.prototype.constructor = DvListViewActionMenu;

DvListViewActionMenu.prototype.toString = 
function() {
	return "DvListViewActionMenu";
}

DvListViewActionMenu.prototype.addSelectionListener =
function(menuItemId, listener) {
	this._menuItems[menuItemId].addSelectionListener(listener);
}

DvListViewActionMenu.prototype.removeSelectionListener =
function(menuItemId, listener) {
	this._menuItems[menuItemId].removeSelectionListener(listener);
}

DvListViewActionMenu.prototype.popup =
function(delay, x, y, kbGenerated) {
	if (delay == null)
		delay = 0;
	if (x == null) 
		x = Dwt.DEFAULT;
	if (y == null)
		y = Dwt.DEFAULT;
	this.setLocation(x, y);
	DwtMenu.prototype.popup.call(this, delay, null, null, kbGenerated);
}

DvListViewActionMenu.prototype.createOp =
function(menuItemId, text, imageInfo, disImageInfo, enabled) {
	var mi = this.createMenuItem.call(this, menuItemId, imageInfo, text, disImageInfo, enabled);
	mi.setData(LmOperation.KEY_ID, menuItemId);
	return mi;
}

DvListViewActionMenu.prototype.createMenuItem =
function(menuItemId, imageInfo, text, disImageInfo, enabled, style, radioGroupId) {
	var mi = this._menuItems[menuItemId] = new DwtMenuItem(this, style, radioGroupId);
	if (imageInfo)
		mi.setImage(imageInfo);
	if (text)
		mi.setText(text);
	if (disImageInfo)
		mi.setDisabledImage(disImageInfo);
	mi.setEnabled(enabled !== false);
	return mi;
}

DvListViewActionMenu.prototype.createSeparator =
function() {
	new DwtMenuItem(this, DwtMenuItem.SEPARATOR_STYLE);
}
