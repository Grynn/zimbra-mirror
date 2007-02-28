function skin() {}

/*
skin.hints = {
	app_chooser		: {	style:"chiclet", 	direction:"TB"	},
	help_button		: {	style:"link", 		container:"app_chooser"	},
	logout_button	: { style:"link", 		container:"app_chooser"	}
}*/

skin.hints = {
	app_chooser		: {	style:"tabs", 		direction:"LR"	},
	help_button		: {	style:"link", 		container:"quota"	},
	logout_button	: { style:"link", 		container:"quota"	},
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
