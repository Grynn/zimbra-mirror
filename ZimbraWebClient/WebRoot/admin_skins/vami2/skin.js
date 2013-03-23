/*
 * ***** BEGIN LICENSE BLOCK *****
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2010 VMware, Inc.
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
function ZaSkin(hints) {
    this.hints = this.mergeObjects(ZaSkin.hints, hints);
}


// default hints for all skins
ZaSkin.hints = {
	// info
	name:			"@SkinName@",
	version:		"@SkinVersion@",
	
	// skin regions
	skin:		  	{ containers: "skin_outer" },
	banner:			{ position:"static", url: "@LogoURL@"},		// == "logo"
	userInfo:		{ position:"static"},
	search:		  	{ position:"static" },
	quota:		  	{ position:"static" },
	presence:	  	{ width:"40px", height: "24px" },
	appView:		{ position:"static" },

	searchBuilder: 		 { minHeight:parseInt("@SBMinHeight@"), 
				   maxHeight:parseInt("@SBMaxHeight@"),
                                   containers: ["skin_container_search_builder", "skin_tr_sb_app_sash"],
				   resizeContainers: ["skin_container_search_builder"]
				 },
	
	tree:			{ minWidth:parseInt("@TreeMinWidth@"), maxWidth:parseInt("@TreeMaxWidth@"), 
					  containers: ["skin_td_tree","skin_td_tree_app_sash"],
					  resizeContainers : ["skin_td_tree"]
					},
	
	topToolbar:	 	{ containers: "skin_tr_top_toolbar" },

	treeFooter:	 	{ containers: "skin_tr_tree_footer" },

	// specific components
	helpButton: 	{	style:"link", url: "@HelpAdminURL@", daUrl: "@HelpDelegatedURL@"	},
	logoutButton: 	{ style: "link" },
	appChooser:		{ position:"static", direction: "LR" },

	fullScreen:     { containers : ["!skin_td_tree", "!skin_td_tree_app_sash"] }
};

//
//	set up the ZaSkin prototype with methods common to all skins
//
ZaSkin.prototype = {
	maxAdminName:21,
	
	skin_container_help_max_str_length:17,
	
	skin_container_dw_max_str_length:17,
	
	//
	// Public methods
	//
	show : function(name, state) {
		var containers = this.hints[name] && this.hints[name].containers;
		if (containers) {
			if (typeof containers == "function") {
				containers.apply(this, [state != false]);
				this._reflowApp();
				return;
			}
			if (typeof containers == "string") {
				containers = [ containers ];
			}
			for (var i = 0; i < containers.length; i++) {
				var ocontainer = containers[i];
				var ncontainer = ocontainer.replace(/^!/,"");
				var inverse = ocontainer != ncontainer;
				this._showEl(ncontainer, inverse ? !state : state);
			}
			this._reflowApp();
		}
	},

	hide : function(name) {
	    this.show(name, false);
	},
		
	mergeObjects : function(dest, src1 /*, ..., srcN */) {
		if (dest == null) dest = {};
	
		// merge all source properties into destination object
		for (var i = 1; i < arguments.length; i++) {
			var src = arguments[i];
			for (var pname in src) {
				// recurse through properties
				var prop = dest[pname];
				if (typeof prop == "object" && !(prop instanceof Array)) {
					this.mergeObjects(dest[pname], src[pname]);
					continue;
				}
	
				// insert missing property
				if (!dest[pname]) {
					dest[pname] = src[pname];
				}
			}
		}
	
		return dest;
	},
	
	getTreeWidth : function() {
		return Dwt.getSize(this._getEl(this.hints.tree.containers[0])).x;
	},
	
	setTreeWidth : function(width) {
		this._setContainerSizes("tree", width, null);
	},
		
        getSBHeight : function() {
                return Dwt.getSize(this._getEl(this.hints.searchBuilder.containers[0])).y;
        },

        setSBHeight: function(height) {
                this._setContainerSizes("searchBuilder", null, height);
        },

	showLoginMsg : function (state) {
		this._showEl("skin_container_login_msg", state);
		this._showEl("skin_td_login_msg", state);
		this._showEl("skin_tr_login_msg", state);	
	},
	
	hideLoginMsg : function () {
		this.showLoginMsg(false);
	},
	showSkin : function () {
		this._showEl("skin_outer", true);
	},
	hideSkin : function () {
		this._hideEl("skin_outer");
	},
	showSearchBuilder : function (state){
              this._showEl("search_builder_outer", state);
		this._showEl("skin_td_search_builder", state);
		this._showEl("skin_tr_search_builder", state);
                this._showEl("skin_tr_sb_app_sash", state);
	},
	hideSearchBuilder : function () {
		this.showSearchBuilder(false);
	},	
	//
	// Protected methods
	//
	
	_getEl : function(id) {
		return (typeof id == "string" ? document.getElementById(id) : id);
	},
	
	_showEl : function(id, state) {
		var el = this._getEl(id);
		if (!el) return;
	
		var value;
		if (state == false) {
			value = "none";
		}
		else {
			var tagName = el.tagName;
			if (tagName == "TD" && !document.all) {
				value = "table-cell";
			}  else if (tagName == "TR" && !document.all) {
				value = "table-row";
			} else {
				value = "block";
			}
		}
		el.style.display = value;
	},
	
	_hideEl : function(id) {
		this._showEl(id, false);
	},
	
	_reparentEl : function(id, containerId) {
		var containerEl = this._getEl(containerId);
		var el = containerEl && this._getEl(id);
		if (el) {
			containerEl.appendChild(el);
		}
	},
	
	_setSize : function(id, width, height) {
		var el = this._getEl(id);
		if (!el) return;
		if (width != null) el.style.width = width;
		if (height != null) el.style.height = height;
	},
	
	_setContainerSizes : function(containerName, width, height) {
		var containers = this.hints[containerName].resizeContainers || this.hints[containerName].containers;
		for (var i = 0; i < containers.length; i++) {
			this._setSize(containers[i], width, height);
		}
	},
	
	_reflowApp : function() {
		if (ZaZimbraAdmin.getInstance && ZaZimbraAdmin.getInstance()) {
			ZaZimbraAdmin.getInstance().getAppViewMgr().fitAll();
		}
	}
	
};


//
//	create an instance as "skin" -- some skins may create another one that overrides this
//
window.skin = new ZaSkin();

