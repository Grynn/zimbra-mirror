function skin() {}

skin.hints = {
	app_chooser		: {	style:"chiclet", 	direction:"TB"	},
	help_button		: {	style:"link", 		container:"app_chooser"	},
	logout_button	: { style:"link", 		container:"app_chooser"	}
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
	var container = navigator.userAgent.indexOf("Safari/") != -1
		? "skin_div_search_builder"
		: "skin_tr_search_builder";

	skin._showEl(container, state);
	skin._showEl("skin_container_search_builder", state);	//HACK: necessary for IE?
}
skin.hideSearchBuilder = function () {
	this.showSearchBuilder(false);
}

skin.showTopToolbar = function (state) {
	skin._showEl("skin_tr_top_toolbar", state);
	skin._showEl("skin_tr_top_toolbar_shim", (state == false));
}
skin.hideTopToolbar = function () {
	this.showTopToolbar(false);
}



skin.showBottomToolbar = function (state) {
	skin._showEl("skin_tr_bottom_toolbar", state);
	skin._showEl("skin_tr_bottom_toolbar_shim", (state == false));
}
skin.hideBottomToolbar = function () {
	this.showBottomToolbar(false);
}


skin.showTreeFooter = function (state) {
	skin._showEl("skin_tr_tree_footer_sep", state);
	skin._showEl("skin_tr_tree_footer", state);
}
skin.hideTreeFooter = function () {
	this.showTreeFooter(false);
}

skin.setSize = function(id, width, height) {

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
