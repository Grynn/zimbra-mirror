/*
 * ***** BEGIN LICENSE BLOCK *****
 * Version: ZPL 1.2
 * 
 * The contents of this file are subject to the Zimbra Public License
 * Version 1.2 ("License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://www.zimbra.com/license
 * 
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See
 * the License for the specific language governing rights and limitations
 * under the License.
 * 
 * The Original Code is: Zimbra Collaboration Suite Web Client
 * 
 * The Initial Developer of the Original Code is Zimbra, Inc.
 * Portions created by Zimbra are Copyright (C) 2005, 2006 Zimbra, Inc.
 * All Rights Reserved.
 * 
 * Contributor(s):
 * 
 * ***** END LICENSE BLOCK *****
 */

/**
* This toolbar sits above the overview and represents the current app. It has a label
* that tells the user what the current app is, and an optional View button/menu for
* switching views within the current app.
* @class
*/
function ZaCurrentAppToolBar(parent, className, buttons) {

	DwtToolBar.call(this, parent, className, Dwt.ABSOLUTE_STYLE);

	this._currentAppLabel = new DwtLabel(this, DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_LEFT, "currentAppLabel");

	this.addFiller();
	/*this._viewButton = new DwtButton(this, null, "DwtToolbarButton");
	this._viewButton.setText(ZmMsg.view);
	this._viewButton.setToolTipContent(ZmMsg.view);
	this._viewButton.setEnabled(true);
	this._viewButton.setVisible(false);
	this._viewButton.noMenuBar = true;
	
	this._viewIcon = new Object();
	this._viewTooltip = new Object();
	this._viewMenu = new Object();*/
}

ZaCurrentAppToolBar.prototype = new DwtToolBar;
ZaCurrentAppToolBar.prototype.constructor = ZaCurrentAppToolBar;

ZaCurrentAppToolBar.prototype.toString = 
function() {
	return "ZaCurrentAppToolBar";
}

ZaCurrentAppToolBar.prototype.setCurrentAppLabel = 
function(title) {
	
	var maxNumberOfLetters = 20 ;
	
	if (title.length > maxNumberOfLetters) {
		title = title.substring(0, (maxNumberOfLetters - 2)) + "...";
	}
	this._currentAppLabel.setText(title);
	//this._currentAppLabel.setImage(ZaZimbraAdmin.APP_ICON[appName]);
}

/*
ZaCurrentAppToolBar.prototype.setCurrentView = 
function(appName) {
	this._currentAppLabel.setText(ZaMsg[ZaZimbraAdmin.MSG_KEY[appName]]);
	//this._currentAppLabel.setImage(ZaZimbraAdmin.APP_ICON[appName]);
}*/
/*
ZaCurrentAppToolBar.prototype.getViewButton = 
function() {
	return this._viewButton;
}

ZaCurrentAppToolBar.prototype.setViewTooltip = 
function(view, tooltip) {
	this._viewTooltip[view] = tooltip;
}

ZaCurrentAppToolBar.prototype.getViewMenu = 
function(view) {
	return this._viewMenu[view];
}

ZaCurrentAppToolBar.prototype.setViewMenu = 
function(view, menu) {
	this._viewMenu[view] = menu;
	this.showViewMenu(view);
}

ZaCurrentAppToolBar.prototype.showViewMenu = 
function(view) {
	var viewMenu = this._viewMenu[view];
	if (viewMenu) {
		this._viewButton.setVisible(true);
		this._viewButton.setToolTipContent(this._viewTooltip[view]);
		this._viewButton.setMenu(viewMenu, false, DwtMenuItem.RADIO_STYLE);
		var mi = viewMenu.getSelectedItem(DwtMenuItem.RADIO_STYLE);
		var icon = mi ? mi.getImage() : null;
		if (icon)
			this._viewButton.setImage(icon);
	} else {
		this._viewButton.setVisible(false);
	}
}
*/
