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
* @constructor
* @class
* This class provides the basic keyboard mappings for Dwt components. It is also
* the base class for application keymaps. 
* 
* @author Ross Dargahi
*/
function DwtKeyMap(subclassInit) {
	if (subclassInit) 
		return;
		
	// Always specify Control, then Alt, then Shift. All Chars must be upper case
	this._map = {};
	
	this._map["DwtDialog"] = {
		"Enter":			DwtKeyMap.ENTER,
		"Esc":				DwtKeyMap.CANCEL
	};

	this._map["DwtButton"] = {
		"Enter":			DwtKeyMap.SELECT_CURRENT,
		"ArrowDown":		DwtKeyMap.SELECT_SUBMENU	
	};
	
	this._map["DwtListView"] = {
		"Space":			DwtKeyMap.SELECT_CURRENT,
			
		"Ctrl+Space":		DwtKeyMap.ADD_SELECT_CURRENT,
		"Ctrl+`":			DwtKeyMap.ADD_SELECT_CURRENT, // Mac FF
			
		"ArrowDown":		DwtKeyMap.SELECT_NEXT,
		"Space":			DwtKeyMap.SELECT_NEXT,
		"Shift+ArrowDown":	DwtKeyMap.ADD_SELECT_NEXT,
		"Ctrl+ArrowDown":	DwtKeyMap.NEXT,
		"ArrowUp":			DwtKeyMap.SELECT_PREV,
		"Shift+ArrowUp":	DwtKeyMap.ADD_SELECT_PREV,
		"Ctrl+ArrowUp":		DwtKeyMap.PREV,

		"Ctrl+A":			DwtKeyMap.SELECT_ALL,
		"Home":				DwtKeyMap.SELECT_FIRST,
		"End":				DwtKeyMap.SELECT_LAST,
		
		"Enter":			DwtKeyMap.DBLCLICK,
		
		"Comma":			DwtKeyMap.ACTION,
		"Shift+Comma":		DwtKeyMap.ACTION,
		"Ctrl+Enter":		DwtKeyMap.ACTION,
		"Ctrl+M":			DwtKeyMap.ACTION  // Mac FF
	};
	
	this._map["DwtMenu"] = {
		"Esc":				DwtKeyMap.CANCEL,
		"Enter":			DwtKeyMap.SELECT_CURRENT,	
		"ArrowDown":		DwtKeyMap.SELECT_NEXT,
		"ArrowUp":			DwtKeyMap.SELECT_PREV,
		"ArrowLeft":		DwtKeyMap.SELECT_PARENTMENU,
		"ArrowRight":		DwtKeyMap.SELECT_SUBMENU
	};
	
	this._map["DwtToolBar-horiz"] = {

		"INHERIT":			"DwtButton",

		"ArrowLeft":		DwtKeyMap.PREV,
		"ArrowRight":		DwtKeyMap.NEXT
	};

	this._map["DwtToolBar-vert"] = {

		"INHERIT":			"DwtButton",

		"ArrowUp":			DwtKeyMap.PREV,
		"ArrowDown":		DwtKeyMap.NEXT
	};
};


DwtKeyMap.deserialize =
function(keymap) {
	alert("DwtKeyMap.deserialize: NOT IMPLEMENTED");
};

DwtKeyMap.serialize =
function(keymap) {
	alert("DwtKeyMap.serialize: NOT IMPLEMENTED");
};

// Key names

DwtKeyMap.CTRL			= "Ctrl+";
DwtKeyMap.ALT			= "Alt+";
DwtKeyMap.SHIFT			= "Shift+";

DwtKeyMap.ARROW_DOWN	= "ArrowDown";
DwtKeyMap.ARROW_LEFT	= "ArrowLeft";
DwtKeyMap.ARROW_RIGHT	= "ArrowRight";
DwtKeyMap.ARROW_UP		= "ArrowUp";
DwtKeyMap.BACKSPACE		= "Backspace";
DwtKeyMap.COMMA			= "Comma";
DwtKeyMap.DELETE		= "Del";
DwtKeyMap.END			= "End";
DwtKeyMap.ENTER			= "Enter";
DwtKeyMap.ESC			= "Esc";
DwtKeyMap.HOME			= "Home";
DwtKeyMap.PAGE_DOWN		= "PgDown";
DwtKeyMap.PAGE_UP		= "PgUp";
DwtKeyMap.SPACE			= "Space";

// Action codes
DwtKeyMap.ACTION				= "DwtAction";
DwtKeyMap.ADD_SELECT_CURRENT	= "DwtAddCurrent";
DwtKeyMap.ADD_SELECT_NEXT		= "DwtAddNext";
DwtKeyMap.ADD_SELECT_PREV		= "DwtAddPrevious";
DwtKeyMap.CANCEL				= "DwtCancel";
DwtKeyMap.DBLCLICK				= "DwtDoubleClick";
DwtKeyMap.DONE					= "DwtDone";
DwtKeyMap.NEXT					= "DwtNext";
DwtKeyMap.PREV					= "DwtPrevious";
DwtKeyMap.SELECT_ALL			= "DwtSelectAll";
DwtKeyMap.SELECT_CURRENT		= "DwtSelectCurrent";
DwtKeyMap.SELECT_FIRST			= "DwtSelectFirst";
DwtKeyMap.SELECT_LAST			= "DwtSelectLast";
DwtKeyMap.SELECT_NEXT			= "DwtSelectNext";
DwtKeyMap.SELECT_PREV			= "DwtSelectPrevious";
DwtKeyMap.SELECT_SUBMENU		= "DwtSubMenu";
DwtKeyMap.SELECT_PARENTMENU		= "DwtParentMenu";

DwtKeyMap.SEP = ","; // Key separator
DwtKeyMap.INHERIT = "INHERIT"; // Inherit keyword.

DwtKeyMap.prototype.getMap =
function() {
	return this._map;
};
