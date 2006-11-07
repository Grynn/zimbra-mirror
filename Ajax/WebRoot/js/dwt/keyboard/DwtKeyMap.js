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
function DwtKeyMap(subclassInit) {
	if (subclassInit) {	return };

	this._map = {};
	this._args = {};
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

DwtKeyMap.prototype.getMap =
function() {
	return this._map;
};

DwtKeyMap.prototype._load =
function(map, keys, mapNames) {
	var curPlatform = AjxEnv.platform.toLowerCase();
	for (var propName in keys) {
		var propValue = keys[propName];
		if (typeof propValue != "string") { continue; }
		var parts = propName.split(".");
		var mapName = mapNames[parts[0]];
		if (!map[mapName]) {
			map[mapName]= {};
		}
		var action = parts[1];
		var platform = parts[2];
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
				if (!platform || (platform == curPlatform)) {
					map[mapName][ks] = action;
				}
			}
		}
	}
};
