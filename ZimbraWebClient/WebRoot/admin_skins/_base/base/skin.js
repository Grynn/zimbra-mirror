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
function skin() {}

/*
skin.hints = {
	appChooser		: {	style:"chiclet", 	direction:"TB"	},
	helpButton		: {	style:"link", 		container:"appChooser"	},
	logoutButton	: { style:"link", 		container:"appChooser"	}
}*/

skin.hints = {
	appChooser		: {	style:"tabs", 		direction:"LR"	},
	helpButton		: {	style:"link", 		container:"quota"	},
	logoutButton	: { style:"link", 		container:"quota"	},
	logo			: { url: "@LogoURL@" }
}


/* PUBLIC API FOR SHOWING/HIDING PIECES OF THE SKIN */

skin.showSkin = function (state) {
	skin._showEl("skin_outer", state);
}
skin.hideSkin = function () {
	skin.showSkin(false);
}

skin.showQuota = function (state) {
	skin._showEl("skin_td_quota_spacer", state);
	skin._showEl("skin_td_quota", state);
}
skin.hideQuota = function () {
	this.showQuota(false);
}

skin.showSearchBuilder = function (state) {
	skin._showEl("search_builder_outer", state);
	skin._showEl("skin_td_search_builder", state);
}
skin.hideSearchBuilder = function () {
	this.showSearchBuilder(false);
}

skin.showTopToolbar = function (state) {
	skin._showEl("skin_tr_top_toolbar", state);
}
skin.hideTopToolbar = function () {
	this.showTopToolbar(false);
}



skin.showTreeFooter = function (state) {
	skin._showEl("skin_tr_tree_footer", state);
}
skin.hideTreeFooter = function () {
	this.showTreeFooter(false);
}


skin.setTreeWidth = function(newWidth) {
	skin.setSize("skin_col_tree", newWidth, null);
}

skin.setSize = function(id, width, height) {
	var el = skin._getEl(id);
	if (width != null) el.style.width = width;
	if (height != null) el.style.height = height;
}

skin._getEl = function(id) {
	return document.getElementById(id);
}
skin._showEl = function(id, state) {
	var el = skin._getEl(id);
	var value;
	if (!el) return;
	if (state == false) {
		value = "none";
	} else {
		var tagName = el.tagName;
		if (tagName == "TD" && document.all == null)		value = "table-cell";
		else if (tagName == "TR" && document.all == null) 	value = "table-row";
		else value = "block";
	}
	el.style.display = value;
}
