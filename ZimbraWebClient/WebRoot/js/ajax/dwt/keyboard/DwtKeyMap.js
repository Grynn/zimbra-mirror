/*
 * ***** BEGIN LICENSE BLOCK *****
 * 
 * Zimbra Collaboration Suite Web Client
 * Copyright (C) 2006, 2007 Zimbra, Inc.
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
 * Creates and loads a key map.
 * @constructor
 * @class
 * This class provides the basic keyboard mappings for Dwt components. The
 * key bindings are taken from the class AjxKeys, which is populated from a
 * properties file. The identifiers used in the properties file must match
 * those used here.
 * 
 * @author Ross Dargahi
 */
DwtKeyMap = function(subclassInit) {
	if (subclassInit) {	return };

	this._map			= {};
	this._args			= {};
	this._checkedMap	= {};	// cache results of _checkMap()
	this._load(this._map, AjxKeys, DwtKeyMap.MAP_NAME);
};

DwtKeyMap.deserialize =
function(keymap) {
	alert("DwtKeyMap.deserialize: NOT IMPLEMENTED");
};

DwtKeyMap.serialize =
function(keymap) {
	alert("DwtKeyMap.serialize: NOT IMPLEMENTED");
};

// translations for map names used in properties file
DwtKeyMap.MAP_NAME = {};
DwtKeyMap.MAP_NAME["dialog"]			= "DwtDialog";
DwtKeyMap.MAP_NAME["button"]			= "DwtButton";
DwtKeyMap.MAP_NAME["list"]				= "DwtListView";
DwtKeyMap.MAP_NAME["menu"]				= "DwtMenu";
DwtKeyMap.MAP_NAME["toolbar"]			= "DwtToolBar";
DwtKeyMap.MAP_NAME["toolbarHorizontal"]	= "DwtToolBar-horiz";
DwtKeyMap.MAP_NAME["toolbarVertical"]	= "DwtToolBar-vert";
DwtKeyMap.MAP_NAME["tabView"]			= "DwtTabView";

// Key names
DwtKeyMap.CTRL			= "Ctrl+";
DwtKeyMap.META			= "Meta+";
DwtKeyMap.ALT			= "Alt+";
DwtKeyMap.SHIFT			= "Shift+";

DwtKeyMap.ARROW_DOWN	= "ArrowDown";
DwtKeyMap.ARROW_LEFT	= "ArrowLeft";
DwtKeyMap.ARROW_RIGHT	= "ArrowRight";
DwtKeyMap.ARROW_UP		= "ArrowUp";
DwtKeyMap.BACKSPACE		= "Backspace";
DwtKeyMap.COMMA			= "Comma";
DwtKeyMap.SEMICOLON		= "Semicolon";
DwtKeyMap.DELETE		= "Del";
DwtKeyMap.END			= "End";
DwtKeyMap.ENTER			= "Enter";
DwtKeyMap.ESC			= "Esc";
DwtKeyMap.HOME			= "Home";
DwtKeyMap.PAGE_DOWN		= "PgDown";
DwtKeyMap.PAGE_UP		= "PgUp";
DwtKeyMap.SPACE			= "Space";
DwtKeyMap.BACKSLASH		= "Backslash";
DwtKeyMap.TAB			= "Tab";

// Action codes
DwtKeyMap.ACTION				= "ContextMenu";
DwtKeyMap.SELECT_CURRENT		= "SelectCurrent";
DwtKeyMap.ADD_SELECT_NEXT		= "AddNext";
DwtKeyMap.ADD_SELECT_PREV		= "AddPrevious";
DwtKeyMap.CANCEL				= "Cancel";
DwtKeyMap.DBLCLICK				= "DoubleClick";
DwtKeyMap.GOTO_TAB				= "GoToTab";
DwtKeyMap.NEXT					= "Next";
DwtKeyMap.NEXT_TAB				= "NextTab";
DwtKeyMap.PREV					= "Previous";
DwtKeyMap.PREV_TAB				= "PreviousTab";
DwtKeyMap.SELECT_ALL			= "SelectAll";
DwtKeyMap.SELECT				= "Select";
DwtKeyMap.SELECT_FIRST			= "SelectFirst";
DwtKeyMap.SELECT_LAST			= "SelectLast";
DwtKeyMap.SELECT_NEXT			= "SelectNext";
DwtKeyMap.SELECT_PREV			= "SelectPrevious";
DwtKeyMap.SUBMENU				= "SubMenu";
DwtKeyMap.PARENTMENU			= "ParentMenu";

DwtKeyMap.GOTO_TAB_RE = new RegExp(DwtKeyMap.GOTO_TAB + "(\\d+)");

DwtKeyMap.SEP = ","; // Key separator
DwtKeyMap.INHERIT = "INHERIT"; // Inherit keyword.

DwtKeyMap.IS_DOC_KEY = {};
DwtKeyMap.IS_DOC_KEY["description"]	= true;
DwtKeyMap.IS_DOC_KEY["summary"]		= true;
DwtKeyMap.IS_DOC_KEY["sort"]		= true;
DwtKeyMap.IS_DOC_KEY["example"]		= true;

DwtKeyMap.prototype.getMap =
function() {
	return this._map;
};

/**
 * Converts a properties representation of shortcuts into a hash. The
 * properties version is actually a reverse map of what we want, so we
 * have to swap keys and values. Handles platform-specific shortcuts,
 * and inheritance. The properties version is made available via a
 * servlet.
 * 
 * @param map			[hash]		hash to populate with shortcuts
 * @param keys			[hash]		properties version of shortcuts
 * @param mapNames		[hash]		map for getting internal map names
 */
DwtKeyMap.prototype._load =
function(map, keys, mapNames) {
	// preprocess for platform-specific bindings
	var curPlatform = AjxEnv.platform.toLowerCase();
	for (var propName in keys) {
		var parts = propName.split(".");
		var last = parts[parts.length - 1];
		if (last == "win" || last == "mac" || last == "linux") {
			if (last == curPlatform) {
				var baseKey = parts.slice(0, 2).join(".");
				keys[baseKey] = keys[propName];
			}
			keys[propName] = null;
		}
	}

	for (var propName in keys) {
		var propValue = keys[propName];
		if (typeof keys[propName] != "string") { continue; }
		var parts = propName.split(".");
		var last = parts[parts.length - 1];
		if (DwtKeyMap.IS_DOC_KEY[last]) { continue; }
		var mapName = mapNames[parts[0]];
		if ((this._checkedMap[mapName] === false) ||
			(!this._checkedMap[mapName] && !this._checkMap(mapName))) { continue; }
		if (!map[mapName]) {
			map[mapName]= {};
		}
		var action = parts[1];
		if (!this._checkAction(mapName, action)) { continue; }
		var keySequences = propValue.split(/\s*;\s*/);
		for (var i = 0; i < keySequences.length; i++) {
			var ks = keySequences[i];
			if (action == DwtKeyMap.INHERIT) {
				var parents = ks.split(/\s*,\s*/);
				var parents1 = [];
				for (var p = 0; p < parents.length; p++) {
					parents1[p] = mapNames[parents[p]];
				}
				map[mapName][action] = parents1.join(",");
			} else {
				map[mapName][ks] = action;
			}
		}
	}
};

/**
 * Returns true if this map is valid. This class always returns true,
 * but subclasses may override to do more checking.
 *
 * @param mapName	[string]	name of map
 */
DwtKeyMap.prototype._checkMap =
function(mapName) {
	var result = true;
	this._checkedMap[mapName] = result;
	return result;
};

/**
 * Returns true if this action is valid. This class always returns true,
 * but subclasses may override to do more checking.
 *
 * @param mapName	[string]	name of map
 * @param action	[string]	action to check
 */
DwtKeyMap.prototype._checkAction =
function(mapName, action) {
	return true;
};
