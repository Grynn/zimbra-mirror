/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2004, 2005, 2006, 2007 Zimbra, Inc.
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

ZaSearchToolBar = function(parent, posStyle) {

	ZaToolBar.call(this, parent, null, posStyle, "SearchToolBar");
	this._app = ZaApp.getInstance();
	this._searchField = new ZaSearchField(this, "SearchTBSearchField", 48, null);
//	var h1 = this._searchField.getSize().y;
		
	//this.setSize(DwtControl.DEFAULT, Math.max(this._searchField.getSize().y, this.computeHeight()));
}

ZaSearchToolBar.prototype = new ZaToolBar;
ZaSearchToolBar.prototype.constructor = ZaSearchToolBar;

ZaSearchToolBar.prototype.toString = 
function() {
	return "ZaSearchToolBar";
}

ZaSearchToolBar.prototype.addSelectionListener =
function(buttonId, listener) {
	// Don't allow listeners on the search by button since we only want listeners registered
	// on its menu items
	if (buttonId != ZaSearchToolBar.SEARCHFOR_BUTTON)
		ZaToolBar.prototype.addSelectionListener.call(this, buttonId, listener);
}


ZaSearchToolBar.prototype.getSearchField =
function() {
	return this._searchField;
}
