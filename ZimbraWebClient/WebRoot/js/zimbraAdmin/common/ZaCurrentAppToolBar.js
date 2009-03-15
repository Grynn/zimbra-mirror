/*
 * ***** BEGIN LICENSE BLOCK *****
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
 * ***** END LICENSE BLOCK *****
 */

/**
* This toolbar sits above the overview and represents the current app. It has a label
* that tells the user what the current app is, and an optional View button/menu for
* switching views within the current app.
* @class
*/
ZaCurrentAppToolBar = function(parent, className, buttons) {

	DwtToolBar.call(this, parent, className, Dwt.ABSOLUTE_STYLE);

	this._currentAppLabel = new ZaToolBarLabel(this, DwtLabel.IMAGE_LEFT | DwtLabel.ALIGN_LEFT, "currentAppLabel");

	this.addFiller();
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
